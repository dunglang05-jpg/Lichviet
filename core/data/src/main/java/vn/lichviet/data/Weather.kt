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
    val gust: Double,
    /** Hướng gió thổi tới, độ (0 = từ phía Bắc). */
    val windDir: Int,
    val uv: Double,
    val code: Int,
    val isDay: Boolean,
    val hours: List<Hour>,
    val days: List<Day>,
    val air: Air? = null,
) {
    /** Chất lượng không khí (Open-Meteo / CAMS). aqi = chỉ số AQI chuẩn Mỹ. */
    data class Air(val aqi: Int, val pm25: Double, val pm10: Double, val o3: Double, val no2: Double, val hours: List<Pair<String, Int>>)
    data class Hour(val time: String, val temp: Double, val code: Int, val rain: Int)
    /** date = yyyy-MM-dd */
    data class Day(val date: String, val max: Double, val min: Double, val code: Int, val rain: Int, val sunrise: String, val sunset: String,
                   val windMax: Double, val gustMax: Double, val windDir: Int, val uvMax: Double)

    fun day(y: Int, m: Int, d: Int): Day? = days.firstOrNull { it.date == "%04d-%02d-%02d".format(y, m, d) }

    /** Trang AccuWeather của địa điểm (mở trong trình duyệt). */
    fun accuweatherUrl(): String =
        "https://www.accuweather.com/vi/search-locations?query=" + URLEncoder.encode(place.ifBlank { "%.3f,%.3f".format(Locale.US, lat, lon) }, "UTF-8")

    companion object {
        private val HUONG = listOf("Bắc", "Đông Bắc", "Đông", "Đông Nam", "Nam", "Tây Nam", "Tây", "Tây Bắc")
        /** Tên hướng gió (gió thổi từ hướng này tới). */
        fun direction(deg: Int) = HUONG[Math.floorMod(Math.round(deg / 45f), 8)]

        /** Cấp gió Beaufort theo tốc độ km/h. */
        fun beaufort(kmh: Double): Int = listOf(1, 6, 12, 20, 29, 39, 50, 62, 75, 89, 103, 118).indexOfFirst { kmh < it }.let { if (it < 0) 12 else it }
        fun beaufortText(b: Int) = when (b) {
            0 -> "lặng gió"; 1, 2 -> "gió nhẹ"; 3, 4 -> "gió vừa"; 5 -> "gió khá mạnh"; 6, 7 -> "gió mạnh"
            8, 9 -> "gió rất mạnh – cẩn thận"; else -> "gió bão – nguy hiểm"
        }

        fun uvText(uv: Double) = when {
            uv < 3 -> "Thấp"; uv < 6 -> "Trung bình"; uv < 8 -> "Cao"; uv < 11 -> "Rất cao"; else -> "Cực cao"
        }

        /** Mức AQI chuẩn Mỹ (US EPA): tên, màu ARGB, lời khuyên. */
        fun aqiLevel(aqi: Int): Triple<String, Long, String> = when {
            aqi <= 50 -> Triple("Tốt", 0xFF2E9E44, "Không khí trong lành, thoải mái hoạt động ngoài trời.")
            aqi <= 100 -> Triple("Trung bình", 0xFFE0A800, "Người rất nhạy cảm nên hạn chế gắng sức ngoài trời lâu.")
            aqi <= 150 -> Triple("Kém", 0xFFF07C1B, "Người già, trẻ nhỏ, người bệnh hô hấp nên giảm hoạt động ngoài trời.")
            aqi <= 200 -> Triple("Xấu", 0xFFD93025, "Mọi người nên hạn chế ra ngoài, đeo khẩu trang khi đi đường.")
            aqi <= 300 -> Triple("Rất xấu", 0xFF8E3FA5, "Tránh hoạt động ngoài trời, đóng cửa sổ.")
            else -> Triple("Nguy hại", 0xFF7E0023, "Ở trong nhà, dùng máy lọc không khí nếu có.")
        }

        /** Biểu tượng emoji theo mã WMO (dùng cho widget). */
        fun emoji(code: Int, day: Boolean = true): String = when (code) {
            0 -> if (day) "☀️" else "🌙"
            1 -> if (day) "🌤️" else "🌙"
            2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            in 51..57, 80 -> "🌦️"
            in 61..67, 81, 82 -> "🌧️"
            in 71..77, 85, 86 -> "❄️"
            in 95..99 -> "⛈️"
            else -> "☁️"
        }

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
    /** Bản đệm gần nhất (đọc đồng bộ, dùng cho widget). */
    fun cached(ctx: Context): Weather? {
        val prefs = ctx.getSharedPreferences("weather", Context.MODE_PRIVATE)
        return prefs.getString("json", null)?.let { runCatching { parse(JSONObject(it), prefs.getString("place", "") ?: "", prefs.getLong("at", 0), prefs.getString("air", null)) }.getOrNull() }
    }

    fun isStale(w: Weather?) = w == null || System.currentTimeMillis() - w.fetchedAt >= TTL

    suspend fun load(ctx: Context, force: Boolean = false): Weather? = withContext(Dispatchers.IO) {
        val prefs = ctx.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val cached = cached(ctx)
        if (_state.value == null && cached != null) _state.value = cached
        if (!force && cached != null && System.currentTimeMillis() - cached.fetchedAt < TTL) return@withContext cached
        val loc = location(ctx) ?: cached?.let { it.lat to it.lon } ?: return@withContext cached
        val url = "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f".format(Locale.US, loc.first, loc.second) +
            "&current=temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,wind_gusts_10m,wind_direction_10m,uv_index,weather_code,is_day" +
            "&hourly=temperature_2m,weather_code,precipitation_probability&forecast_hours=24" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,sunrise,sunset," +
            "wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,uv_index_max" +
            "&timezone=auto&forecast_days=7"
        val body = runCatching { http(url) }.getOrNull() ?: return@withContext cached
        val airUrl = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=%.4f&longitude=%.4f".format(Locale.US, loc.first, loc.second) +
            "&current=us_aqi,pm2_5,pm10,ozone,nitrogen_dioxide&hourly=us_aqi&forecast_hours=24&timezone=auto"
        val airBody = runCatching { http(airUrl) }.getOrNull() ?: prefs.getString("air", null)
        val place = placeName(ctx, loc.first, loc.second) ?: cached?.place ?: ""
        val now = System.currentTimeMillis()
        val w = runCatching { parse(JSONObject(body), place, now, airBody) }.getOrNull() ?: return@withContext cached
        prefs.edit().putString("json", body).putString("air", airBody).putString("place", place).putLong("at", now).apply()
        _state.value = w
        // báo widget vẽ lại với thời tiết mới
        runCatching { ctx.sendBroadcast(android.content.Intent("vn.lichviet.widget.MIDNIGHT").setPackage(ctx.packageName)) }
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

    private fun parseAir(s: String?): Weather.Air? = runCatching {
        val o = JSONObject(s ?: return null)
        val c = o.getJSONObject("current")
        val h = o.optJSONObject("hourly")
        val hours = h?.let { hh ->
            val t = hh.getJSONArray("time"); val a = hh.getJSONArray("us_aqi")
            (0 until t.length()).filter { !a.isNull(it) }.map { t.getString(it).substringAfter('T') to a.getInt(it) }
        } ?: emptyList()
        Weather.Air(c.getInt("us_aqi"), c.optDouble("pm2_5", 0.0), c.optDouble("pm10", 0.0), c.optDouble("ozone", 0.0), c.optDouble("nitrogen_dioxide", 0.0), hours)
    }.getOrNull()

    private fun parse(o: JSONObject, place: String, at: Long, air: String?): Weather {
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
                d.optJSONArray("wind_speed_10m_max")?.optDouble(i, 0.0) ?: 0.0, d.optJSONArray("wind_gusts_10m_max")?.optDouble(i, 0.0) ?: 0.0,
                d.optJSONArray("wind_direction_10m_dominant")?.optInt(i, 0) ?: 0, d.optJSONArray("uv_index_max")?.optDouble(i, 0.0) ?: 0.0,
            )
        }
        return Weather(
            place, o.getDouble("latitude"), o.getDouble("longitude"), at,
            cur.getDouble("temperature_2m"), cur.getDouble("apparent_temperature"), cur.getInt("relative_humidity_2m"),
            cur.getDouble("wind_speed_10m"), cur.optDouble("wind_gusts_10m", 0.0), cur.optInt("wind_direction_10m", 0), cur.optDouble("uv_index", 0.0),
            cur.getInt("weather_code"), cur.optInt("is_day", 1) == 1, hours, days, parseAir(air),
        )
    }
}
