package vn.lichviet.tools

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import vn.lichviet.data.Weather
import vn.lichviet.data.WeatherRepository
import kotlin.math.roundToInt

fun weatherIcon(code: Int, day: Boolean = true): ImageVector = when (code) {
    0, 1 -> if (day) Icons.Filled.WbSunny else Icons.Filled.NightsStay
    2 -> Icons.Filled.WbCloudy
    3 -> Icons.Filled.Cloud
    45, 48 -> Icons.Filled.Dehaze
    in 51..67, in 80..82 -> Icons.Filled.WaterDrop
    in 71..77, 85, 86 -> Icons.Filled.AcUnit
    in 95..99 -> Icons.Filled.Thunderstorm
    else -> Icons.Filled.Cloud
}

fun openAccuWeather(ctx: Context, w: Weather?) {
    val url = w?.accuweatherUrl() ?: "https://www.accuweather.com/vi"
    runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

private fun t(v: Double) = "${v.roundToInt()}°"

/** Trạng thái chung: xin quyền vị trí, tải dữ liệu. */
@Composable
private fun rememberWeather(): Triple<Weather?, Boolean, () -> Unit> {
    val ctx = LocalContext.current
    val w by WeatherRepository.state.collectAsState()
    var granted by remember { mutableStateOf(WeatherRepository.hasPermission(ctx)) }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) scope.launch { WeatherRepository.load(ctx, force = true) }
    }
    LaunchedEffect(granted) { if (granted) WeatherRepository.load(ctx) }
    return Triple(w, granted) { launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
}

/** Dòng thời tiết nhỏ trên tờ lịch; bấm vào mở AccuWeather. */
@Composable
fun WeatherChip(y: Int, m: Int, d: Int, isToday: Boolean, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val (w, granted, ask) = rememberWeather()
    val cs = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier.clip(shape).background(cs.surfaceContainerHighest, shape)
            .clickable { if (!granted) ask() else openAccuWeather(ctx, w) }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (!granted) {
            Icon(Icons.Filled.LocationOn, null, tint = cs.primary, modifier = Modifier.size(16.dp))
            Text("Bật vị trí để xem thời tiết", fontSize = 13.sp, color = cs.primary, modifier = Modifier.weight(1f, fill = false))
            return@Row
        }
        if (w == null) { Text("Đang tải thời tiết", fontSize = 13.sp, color = cs.onSurfaceVariant); return@Row }
        val day = w.day(y, m, d)
        val place = w.place.substringBefore(',').takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""
        val (icon, line) = when {
            isToday -> weatherIcon(w.code, w.isDay) to buildString {
                append("${t(w.temp)} ${Weather.describe(w.code)}")
                day?.let { append(" · ${t(it.min)}–${t(it.max)}") }
                w.air?.let { append(" · AQI ${it.aqi}") }
                append(place)
            }
            day != null -> weatherIcon(day.code) to "${t(day.min)}–${t(day.max)} ${Weather.describe(day.code)}$place"
            else -> Icons.Filled.Cloud to "Chưa có dự báo cho ngày này"
        }
        Icon(icon, null, tint = cs.secondary, modifier = Modifier.size(18.dp))
        Text(line, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f, fill = false))
    }
}

private val THU = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")

/** Tiện ích Thời tiết: hiện tại, 24 giờ, 7 ngày; nút mở AccuWeather. */
@Composable
fun WeatherScreen() {
    val ctx = LocalContext.current
    val (w, granted, ask) = rememberWeather()
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!granted) {
            Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Cần quyền vị trí (gần đúng) để lấy thời tiết nơi bạn ở.")
                Button(onClick = ask) { Text("Cho phép vị trí") }
            } }
        } else if (w == null) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            Text("Nếu chờ lâu: kiểm tra mạng và bật định vị trên máy.", fontSize = 13.sp, color = cs.onSurfaceVariant)
        } else {
            Card(colors = CardDefaults.cardColors(containerColor = cs.primary, contentColor = cs.onPrimary)) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(16.dp))
                        Text(" " + w.place.ifBlank { "Vị trí hiện tại" }, fontSize = 14.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(t(w.temp), fontSize = 56.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(12.dp))
                        Icon(weatherIcon(w.code, w.isDay), null, modifier = Modifier.size(48.dp))
                    }
                    Text(Weather.describe(w.code), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Cảm giác ${t(w.feels)} · Độ ẩm ${w.humidity}% · UV ${"%.0f".format(w.uv)} (${Weather.uvText(w.uv)})", fontSize = 13.sp)
                    w.days.firstOrNull()?.let { Text("Mặt trời mọc ${it.sunrise} · lặn ${it.sunset}", fontSize = 13.sp) }
                }
            }
            WindCard(w)
            w.air?.let { AirCard(it) }
            Text("24 giờ tới", fontWeight = FontWeight.Bold)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                w.hours.forEach { h ->
                    Column(
                        Modifier.clip(RoundedCornerShape(12.dp)).background(cs.surfaceContainerHighest).padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(h.time.take(5), fontSize = 12.sp, color = cs.onSurfaceVariant)
                        Icon(weatherIcon(h.code, h.time.take(2).toIntOrNull() in 6..17), null, tint = cs.secondary, modifier = Modifier.size(22.dp))
                        Text(t(h.temp), fontWeight = FontWeight.SemiBold)
                        if (h.rain > 0) Text("${h.rain}%", fontSize = 11.sp, color = Color(0xFF1E88E5))
                    }
                }
            }
            Text("7 ngày", fontWeight = FontWeight.Bold)
            Card { Column(Modifier.padding(vertical = 4.dp)) {
                w.days.forEachIndexed { i, dd ->
                    val parts = dd.date.split("-").map { it.toInt() }
                    val wd = java.util.Calendar.getInstance().apply { set(parts[0], parts[1] - 1, parts[2]) }.get(java.util.Calendar.DAY_OF_WEEK) - 1
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (i == 0) "Hôm nay" else "${THU[wd]} ${parts[2]}/${parts[1]}", Modifier.width(80.dp), fontSize = 14.sp)
                        Icon(weatherIcon(dd.code), null, tint = cs.secondary, modifier = Modifier.size(20.dp))
                        Text(" " + Weather.describe(dd.code), Modifier.weight(1f), fontSize = 13.sp)
                        if (dd.rain > 0) Text("${dd.rain}%  ", fontSize = 12.sp, color = Color(0xFF1E88E5))
                        Text("${t(dd.min)} / ${t(dd.max)}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            } }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { openAccuWeather(ctx, w) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.OpenInNew, null, modifier = Modifier.size(18.dp)); Text("  Xem trên AccuWeather")
                }
                OutlinedButton(enabled = !loading, onClick = {
                    loading = true; scope.launch { WeatherRepository.load(ctx, force = true); loading = false }
                }) { Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp)) }
            }
            Text("Số liệu: Open-Meteo, không khí theo Copernicus CAMS · cập nhật mỗi 30 phút", fontSize = 11.sp, color = cs.onSurfaceVariant)
        }
    }
}

@Composable
private fun WindCard(w: Weather) {
    val cs = MaterialTheme.colorScheme
    val b = Weather.beaufort(maxOf(w.wind, 0.0))
    Card { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(56.dp).clip(androidx.compose.foundation.shape.CircleShape).background(cs.primary.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
            // Mũi tên chỉ hướng gió thổi tới (ngược với hướng gió đến)
            Icon(Icons.Filled.Navigation, null, tint = cs.primary,
                modifier = Modifier.size(30.dp).graphicsLayer { rotationZ = (w.windDir + 180f) % 360f })
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Gió ${Weather.direction(w.windDir)} ${w.wind.roundToInt()} km/h", fontWeight = FontWeight.Bold)
            Text("Giật ${w.gust.roundToInt()} km/h · cấp $b (${Weather.beaufortText(b)})", fontSize = 13.sp, color = cs.onSurfaceVariant)
            w.days.firstOrNull()?.let {
                Text("Hôm nay mạnh nhất ${it.windMax.roundToInt()} km/h, giật ${it.gustMax.roundToInt()} km/h", fontSize = 13.sp, color = cs.onSurfaceVariant)
            }
        }
    } }
}

@Composable
private fun AirCard(a: Weather.Air) {
    val cs = MaterialTheme.colorScheme
    val (name, argb, advice) = Weather.aqiLevel(a.aqi)
    val c = Color(argb)
    Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(c), contentAlignment = Alignment.Center) {
                Text("${a.aqi}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Không khí: $name", fontWeight = FontWeight.Bold, color = c)
                Text("Chỉ số AQI (chuẩn Mỹ)", fontSize = 12.sp, color = cs.onSurfaceVariant)
            }
        }
        Text(advice, fontSize = 13.sp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("PM2.5" to a.pm25, "PM10" to a.pm10, "O₃" to a.o3, "NO₂" to a.no2).forEach { (k, v) ->
                Column(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(cs.surfaceContainerHighest).padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(k, fontSize = 12.sp, color = cs.onSurfaceVariant)
                    Text("${v.roundToInt()}", fontWeight = FontWeight.SemiBold)
                    Text("µg/m³", fontSize = 10.sp, color = cs.onSurfaceVariant)
                }
            }
        }
        if (a.hours.size > 1) {
            Text("24 giờ tới", fontSize = 12.sp, color = cs.onSurfaceVariant)
            Row(Modifier.fillMaxWidth().height(36.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
                val mx = maxOf(a.hours.maxOf { it.second }, 50)
                a.hours.forEach { (_, v) ->
                    Box(Modifier.weight(1f).fillMaxHeight(v.toFloat() / mx).clip(RoundedCornerShape(2.dp)).background(Color(Weather.aqiLevel(v).second)))
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Text(a.hours.first().first.take(5), fontSize = 11.sp, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(a.hours.last().first.take(5), fontSize = 11.sp, color = cs.onSurfaceVariant)
            }
        }
    } }
}
