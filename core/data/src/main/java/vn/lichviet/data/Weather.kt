package vn.lichviet.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

/** Thời tiết địa phương lấy từ Open-Meteo (miễn phí, không cần khóa), lưu đệm 30 phút. */
data class Weather(
    val place: String,
    val lat: Double,
    val lon: Double,
    val fetchedAt: Long,
    val temp: Double,
    val feels: Double,
    val humidity: Int,
    val wind: Double,
    val code: Int,
    val isDay: Boolean,
    val hours: List<Hour>,
    val days: List<Day>,
) {
    data class Hour(val time: String, val temp: Double, val code: Int, val rain: Int)
    /** date = yyyy-MM-dd */
    data class Day(val date: String, val max: Double, val min: Double, val code: Int, val rain: Int, val sunrise: String, val sunset: String)

    fun day(y: Int, m: Int, d: Int): Day? = days.firstOrNull { it.date == "%04d-%02d-%02d".format(y, m, d) }

    /** Trang AccuWeather của địa điểm (mở trong trình duyệt). */
    fun accuweatherUrl(): String =
        "https://www.accuweather.com/vi/search-locations?query=" + URLEncoder.encode(place.ifBlank { "%.3f,%.3f".format(Locale.US, lat, lon) }, "UTF-8")

    companion object {
        /** Mô tả theo mã thời tiết WMO. */
        fun describe(code: Int): String = when (code) {
            0 -> "Trời quang"
            1 -> "Ít mây"
            2 -> "Có mây"
            3 -> "Nhiều mây"
            45, 48 -> "Sương mù"
            51, 53, 55 -> "Mưa phùn"
            56, 57 -> "Mưa phùn lạnh"
            61 -> "Mưa nhỏ"
            63 -> "Mưa vừa"
            65 -> "Mưa to"
            66, 67 -> "Mưa lạnh"
            71, 73, 75, 77 -> "Tuyết"
            80 -> "Mưa rào nhẹ"
            81 -> "Mưa rào"
            82 -> "Mưa rào rất to"
            85, 86 -> "Mưa tuyết"
            95 -> "Dông"
            96, 99 -> "Dông kèm mưa đá"
            else -> "—"
        }
    }
}

object WeatherRepository {
    private val _state = MutableStateFlow<Weather?>(null)
    val state: StateFlow<Weather?> = _state
    private const val TTL = 30 * 60 * 1000L

    fun hasPermission(ctx: Context) =
        ctx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Lấy dữ liệu (dùng bản đệm nếu còn mới). Trả về null nếu không có vị trí hoặc lỗi mạng. */
    suspend fun load(ctx: Context, force: Boolean = false): Weather? = withContext(Dispatchers.IO) {
        val prefs = ctx.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val cached = prefs.getString("json", null)?.let { runCatching { parse(JSONObject(it), prefs.getString("place", "") ?: "", prefs.getLong("at", 0)) }.getOrNull() }
        if (_state.value == null && cached != null) _state.value = cached
        if (!force && cached != null && System.currentTimeMillis() - cached.fetchedAt < TTL) return@withContext cached
        val loc = location(ctx) ?: cached?.let { it.lat to it.lon } ?: return@withContext cached
        val url = "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f".format(Locale.US, loc.first, loc.second) +
            "&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day" +
            "&hourly=temperature_2m,weather_code,precipitation_probability&forecast_hours=24" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset" +
            "&timezone=auto&forecast_days=7"
        val body = runCatching { http(url) }.getOrNull() ?: return@withContext cached
        val place = placeName(ctx, loc.first, loc.second) ?: cached?.place ?: ""
        val now = System.currentTimeMillis()
        val w = runCatching { parse(JSONObject(body), place, now) }.getOrNull() ?: return@withContext cached
        prefs.edit().putString("json", body).putString("place", place).putLong("at", now).apply()
        _state.value = w
        w
    }

    @SuppressLint("MissingPermission")
    private fun location(ctx: Context): Pair<Double, Double>? {
        if (!hasPermission(ctx)) return null
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val best = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            .mapNotNull { p -> runCatching { lm.getLastKnownLocation(p) }.getOrNull() }
            .maxByOrNull { it.time } ?: return null
        return best.latitude to best.longitude
    }

    @Suppress("DEPRECATION")
    private fun placeName(ctx: Context, lat: Double, lon: Double): String? = runCatching {
        if (!Geocoder.isPresent()) return null
        val a = Geocoder(ctx, Locale("vi", "VN")).getFromLocation(lat, lon, 1)?.firstOrNull() ?: return null
        listOfNotNull(a.subAdminArea ?: a.locality, a.adminArea).distinct().joinToString(", ").ifBlank { null }
    }.getOrNull()

    private fun http(url: String): String {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 10000; c.readTimeout = 10000
        return try { c.inputStream.bufferedReader().readText() } finally { c.disconnect() }
    }

    private fun parse(o: JSONObject, place: String, at: Long): Weather {
        val cur = o.getJSONObject("current")
        val h = o.getJSONObject("hourly")
        val ht = h.getJSONArray("time"); val htemp = h.getJSONArray("temperature_2m"); val hc = h.getJSONArray("weather_code")
        val hr = h.optJSONArray("precipitation_probability")
        val hours = (0 until ht.length()).map { i ->
            Weather.Hour(ht.getString(i).substringAfter('T'), htemp.getDouble(i), hc.getInt(i), hr?.optInt(i, 0) ?: 0)
        }
        val d = o.getJSONObject("daily")
        val dt = d.getJSONArray("time")
        val days = (0 until dt.length()).map { i ->
            Weather.Day(
                dt.getString(i), d.getJSONArray("temperature_2m_max").getDouble(i), d.getJSONArray("temperature_2m_min").getDouble(i),
                d.getJSONArray("weather_code").getInt(i), d.optJSONArray("precipitation_probability_max")?.optInt(i, 0) ?: 0,
                d.getJSONArray("sunrise").getString(i).substringAfter('T'), d.getJSONArray("sunset").getString(i).substringAfter('T'),
            )
        }
        return Weather(
            place, o.getDouble("latitude"), o.getDouble("longitude"), at,
            cur.getDouble("temperature_2m"), cur.getDouble("apparent_temperature"), cur.getInt("relative_humidity_2m"),
            cur.getDouble("wind_speed_10m"), cur.getInt("weather_code"), cur.optInt("is_day", 1) == 1, hours, days,
        )
    }
}
