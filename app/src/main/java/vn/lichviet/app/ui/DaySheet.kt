package vn.lichviet.app.ui

import vn.lichviet.ui.*

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import vn.lichviet.core.DayInfo

private val THANG_TEN = listOf("Giêng", "Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy", "Tám", "Chín", "Mười", "Mười Một", "Chạp")

/** Tranh cho tờ lịch: ngày lễ âm lịch có tranh riêng, ngày thường dùng 2 tranh của tháng luân phiên. */
fun sheetArtPath(d: DayInfo): String {
    val next = dayOf(d.jd + 1)
    val le = { n: Int -> "art/thang/le-$n.jpg" }
    if (!d.leap) {
        if (d.lunarMonth == 12 && next.lunarDay == 1) return le(1)             // Giao thừa
        if (d.lunarMonth == 12 && d.lunarDay >= 24) return le(6)               // Tất niên
        if (d.lunarMonth == 1 && d.lunarDay == 15) return le(2)                // Rằm tháng Giêng
        if (d.lunarMonth == 1 && d.lunarDay in 4..14) return le(5)             // Mùa hội làng
        if (d.lunarMonth == 3 && d.lunarDay == 3) return le(4)                 // Tết Hàn thực
    }
    if (d.tietKhiToday == "Thanh minh") return le(3)
    return "art/thang/t%02d-%d.jpg".format(d.solarMonth, 1 + Math.floorMod(d.jd, 2))
}

private object SheetArt {
    private val cache = LinkedHashMap<String, ImageBitmap>()
    fun get(ctx: Context, path: String): ImageBitmap? = cache[path] ?: runCatching {
        ctx.assets.open(path).use { BitmapFactory.decodeStream(it) }.asImageBitmap()
    }.getOrNull()?.also { if (cache.size > 4) cache.remove(cache.keys.first()); cache[path] = it }
}

/** Tờ lịch ngày treo tường: lật lên cho ngày sau, kéo xuống cho ngày trước. Hai kiểu: tờ giấy có châm ngôn, tờ tranh. */
@Composable
fun DaySheet(jd: Int, todayJd: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var style by remember { mutableStateOf(prefs.getString("sheetStyle", "giay") ?: "giay") }
    // Tờ đang hiển thị và tờ đang lật
    var shown by remember { mutableIntStateOf(jd) }
    var flipping by remember { mutableIntStateOf(jd) }
    var dir by remember { mutableIntStateOf(0) }
    val prog = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    LaunchedEffect(jd) {
        if (jd == shown) return@LaunchedEffect
        dir = if (jd > shown) 1 else -1
        flipping = shown
        shown = jd
        prog.snapTo(0f)
        prog.animateTo(1f, tween(850, easing = CubicBezierEasing(.45f, .05f, .35f, 1f)))
        dir = 0
    }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.widthIn(max = 380.dp).fillMaxWidth().padding(horizontal = 20.dp)) {
            Column {
                // Thanh treo có 2 khoen
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(20.dp)
                        .shadow(3.dp, RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF5A2A22), Color(0xFF3A1A15))), RoundedCornerShape(8.dp)),
                    horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(2) { Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF1C1513)).border(2.dp, Color(0xFFC9A35A), CircleShape)) }
                }
                Box(
                    Modifier.fillMaxWidth().padding(top = 2.dp).pointerInput(shown) {
                        var dx = 0f; var dy = 0f
                        detectDragGestures(onDragStart = { dx = 0f; dy = 0f }, onDragEnd = {
                            val lim = with(density) { 50.dp.toPx() }
                            when {
                                dy < -lim || dx < -lim -> onChange(shown + 1)
                                dy > lim || dx > lim -> onChange(shown - 1)
                            }
                        }) { c, a -> c.consume(); dx += a.x; dy += a.y }
                    },
                ) {
                    val p = prog.value
                    val camera = 14f * density.density
                    if (dir > 0) {
                        // Tờ mới nằm dưới, tờ cũ lật lên phía thanh treo
                        Sheet(shown, todayJd, style)
                        Box(Modifier.graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0f); cameraDistance = camera
                            rotationX = 115f * p; alpha = if (p > .8f) (1f - p) / .2f else 1f
                        }) {
                            Sheet(flipping, todayJd, style)
                            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .45f * p)))
                        }
                    } else if (dir < 0) {
                        Sheet(flipping, todayJd, style)
                        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .35f * (1f - p))))
                        Box(Modifier.graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0f); cameraDistance = camera
                            rotationX = 115f * (1f - p); alpha = if (p < .2f) p / .2f else 1f
                        }) { Sheet(shown, todayJd, style) }
                    } else {
                        Sheet(shown, todayJd, style)
                    }
                }
            }
        }
        Row(
            Modifier.widthIn(max = 380.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onChange(shown - 1) }) { Text("‹", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary) }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                val sd = remember(shown) { dayOf(shown) }
                vn.lichviet.tools.WeatherChip(sd.solarYear, sd.solarMonth, sd.solarDay, shown == todayJd)
            }
            IconButton(onClick = { onChange(shown + 1) }) { Text("›", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("giay" to "Tờ giấy · châm ngôn", "tranh" to "Tờ tranh").forEach { (k, label) ->
                FilterChip(selected = style == k, onClick = {
                    style = k; prefs.edit().putString("sheetStyle", k).apply()
                }, label = { Text(label) })
            }
        }
        if (jd != todayJd) {
            androidx.compose.material3.TextButton(onClick = { onChange(todayJd) }) { Text("← Về hôm nay") }
        }
    }
}

@Composable
private fun Sheet(jd: Int, todayJd: Int, style: String) {
    val d = remember(jd) { dayOf(jd) }
    if (style == "tranh") PaintingSheet(d, jd == todayJd) else PaperSheet(d, jd == todayJd)
}

private fun lunarHead(d: DayInfo): String {
    val full = dayOf(d.jd + 30 - d.lunarDay).lunarDay == 30
    return "Tháng ${THANG_TEN[d.lunarMonth - 1]}${if (d.leap) " nhuận" else ""} (${if (full) "đủ" else "thiếu"})"
}

private fun goodHours(d: DayInfo) = d.hours.filter { it.good }.joinToString(", ") { "${it.name} (${it.range})" }

@Composable
private fun PaperSheet(d: DayInfo, today: Boolean) {
    val red = d.weekday == 0 || d.holidays.any { it.major }
    val ink = Color(0xFF22303F)
    val brand = Color(0xFFA6262A)
    val numColor = if (red) brand else ink
    val serif = FontFamily.Serif
    Column(
        Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFBF6EA)).border(1.dp, Color(0xFFD9C9A8), RoundedCornerShape(14.dp)),
    ) {
        Row(Modifier.fillMaxWidth().background(brand).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("THÁNG ${d.solarMonth}", color = Color(0xFFFFF4E0), fontWeight = FontWeight.Bold, fontFamily = serif, modifier = Modifier.weight(1f))
            Text("${d.solarYear}", color = Color(0xFFFFF4E0), fontWeight = FontWeight.Bold, fontFamily = serif)
        }
        Text("${d.solarDay}", fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 92.sp, lineHeight = 96.sp, color = numColor,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        Text(DayInfo.THU[d.weekday].uppercase() + if (today) " · HÔM NAY" else "", fontWeight = FontWeight.Bold, letterSpacing = 3.sp,
            color = numColor, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        d.holidays.firstOrNull()?.let {
            Text(it.name, color = brand, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 12.dp, end = 12.dp))
        }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                .border(1.dp, Color(0xFFE2D3B2), RoundedCornerShape(8.dp)).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("❝ ${TucNgu.forJd(d.jd)} ❞", fontFamily = serif, fontStyle = FontStyle.Italic, fontSize = 15.sp, lineHeight = 22.sp,
                color = Color(0xFF4A2A1A), textAlign = TextAlign.Center)
            Text("— Tục ngữ, ca dao Việt Nam", style = MaterialTheme.typography.labelSmall, color = Color(0xFF8A7A6A),
                modifier = Modifier.padding(top = 4.dp))
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.width(108.dp).border(1.5.dp, brand, RoundedCornerShape(8.dp)).padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(lunarHead(d), fontSize = 11.sp, color = brand, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Text("${d.lunarDay}", fontFamily = serif, fontSize = 36.sp, lineHeight = 40.sp, color = brand)
                Text(if (d.lunarDay <= 10) "Mùng ${d.lunarDay}" else "Ngày ${d.lunarDay}", fontSize = 11.sp, color = brand)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                CC("Năm", d.yearCanChi); CC("Tháng", d.monthCanChi); CC("Ngày", d.dayCanChi); CC("Tiết", d.tietKhiCurrent)
            }
        }
        GiapRow(d, dark = false)
        Text("Giờ hoàng đạo: ${goodHours(d)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2B211A),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        val good = d.hoangDao
        Text("${if (good) "Hoàng đạo" else "Hắc đạo"} · ${d.than12} · Trực ${d.truc}", fontWeight = FontWeight.Bold,
            color = if (good) Color(0xFF2E7D4F) else Color(0xFF8A5A12), textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().background(if (good) Color(0xFFE7F2EA) else Color(0xFFF6E9D6)).padding(vertical = 7.dp))
    }
}

@Composable
private fun CC(k: String, v: String) {
    Row { Text(k, color = Color(0xFF8A7A6A), fontSize = 13.sp, modifier = Modifier.width(46.dp)); Text(v, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2B211A)) }
}

@Composable
private fun PaintingSheet(d: DayInfo, today: Boolean) {
    val ctx = LocalContext.current
    val path = remember(d.jd) { sheetArtPath(d) }
    val bmp = remember(path) { SheetArt.get(ctx, path) }
    val pink = Color(0xFFF2978A)
    Column(
        Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1513)).border(2.dp, Color(0xFFE8867A), RoundedCornerShape(14.dp)).padding(4.dp),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp))) {
            if (bmp != null) Image(bmp, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x8C140C0A), Color(0x26140C0A), Color(0x99140C0A)))))
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text("THÁNG ${d.solarMonth} · ${d.solarYear}", color = pink, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                if (today) Text("Hôm nay", fontSize = 11.sp, color = Color(0xFF1C1513),
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFE8867A)).padding(horizontal = 8.dp))
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${d.solarDay}", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 80.sp, lineHeight = 84.sp, color = pink)
                Text(DayInfo.THU[d.weekday].uppercase(), color = pink, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xFF1C1513)).padding(horizontal = 16.dp, vertical = 2.dp))
            }
        }
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.width(108.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF8E2320)).padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(lunarHead(d).uppercase(), fontSize = 10.sp, color = Color(0xFFF7E3C8), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("${d.lunarDay}", fontFamily = FontFamily.Serif, fontSize = 36.sp, lineHeight = 40.sp, color = Color(0xFFF0C24A))
                Text(if (d.lunarDay <= 10) "MÙNG ${d.lunarDay}" else "NGÀY ${d.lunarDay}", fontSize = 10.sp, color = Color(0xFFF7E3C8))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                listOf("Năm" to d.yearCanChi, "Tháng" to d.monthCanChi, "Ngày" to d.dayCanChi, "Tiết" to d.tietKhiCurrent).forEach { (k, v) ->
                    Row { Text(k, color = Color(0xFFB9A898), fontSize = 13.sp, modifier = Modifier.width(46.dp)); Text(v, color = Color(0xFFFFF3E4), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        d.holidays.firstOrNull()?.let { Text(it.name, color = Color(0xFFF0C24A), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp)) }
        GiapRow(d, dark = true)
        Text("Giờ hoàng đạo: ${goodHours(d)}", color = Color(0xFFF3E6D8), fontSize = 12.5.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        val good = d.hoangDao
        Text("${if (good) "Hoàng đạo" else "Hắc đạo"} · ${d.than12} · Trực ${d.truc}", fontWeight = FontWeight.Bold,
            color = if (good) Color(0xFFA6E3C0) else Color(0xFFE8B860), textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp))
                .background(if (good) Color(0xFF21402F) else Color(0xFF3D2A14)).padding(vertical = 7.dp))
    }
}



@Composable
private fun GiapRow(d: DayInfo, dark: Boolean) {
    val yearChi = (d.lunarYear + 8) % 12
    val label = if (dark) Color(0xFFB9A898) else Color(0xFF8A7A6A)
    val value = if (dark) Color(0xFFFFF3E4) else Color(0xFF2B211A)
    val tile = if (dark) Color(0xFF2A201C) else Color(0xFFF3EAD6)
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(Triple("Con giáp ngày", d.dayChi, d.dayCanChi), Triple("Con giáp năm", yearChi, d.yearCanChi)).forEach { (k, chi, cc) ->
            Row(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(tile).padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                GiapImage(chi, 52.dp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(k, fontSize = 11.sp, color = label)
                    Text(GIAP_TEN[chi], fontSize = 14.sp, fontWeight = FontWeight.Bold, color = value)
                    Text(cc, fontSize = 11.sp, color = label)
                }
            }
        }
    }
}
