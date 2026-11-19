package vn.lichviet.app.ui

import vn.lichviet.ui.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.Activity
import vn.lichviet.core.Almanac
import vn.lichviet.core.DayInfo
import vn.lichviet.core.GoodDayFinder
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.MoonPhase
import vn.lichviet.core.NhiThapBatTu
import vn.lichviet.core.TietKhi
import vn.lichviet.core.TuoiHop
import vn.lichviet.data.AppSettings

// ---------------------------------------------------------------- Dải tuần

@Composable
fun WeekStrip(jd: Int, todayJd: Int, mondayFirst: Boolean, onPick: (Int) -> Unit) {
    val wd = (jd + 1) % 7 // 0 = CN
    val offset = if (mondayFirst) (wd + 6) % 7 else wd
    val start = jd - offset
    val days = remember(start) { (0 until 7).map { dayOf(start + it) } }
    val mid = days[3]
    ShadowCard(elevation = 5.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { onPick(jd - 7) }) { Text("◀", maxLines = 1) }
            Text(
                "THÁNG ${mid.solarMonth}, ${mid.solarYear}", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { onPick(jd + 7) }) { Text("▶", maxLines = 1) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            days.forEach { d ->
                val sel = d.jd == jd
                val isToday = d.jd == todayJd
                val sunday = d.weekday == 0
                val bg = when {
                    isToday -> MaterialTheme.colorScheme.primary
                    sel -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
                val fg = when {
                    isToday -> MaterialTheme.colorScheme.onPrimary
                    sunday -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(bg)
                        .border(if (sel) 2.dp else 0.dp, if (sel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { onPick(d.jd) }.padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(DayInfo.THU_NGAN[d.weekday], style = MaterialTheme.typography.labelSmall, color = fg.copy(alpha = 0.8f), maxLines = 1)
                    Text("${d.solarDay}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = fg, maxLines = 1)
                    Text(
                        if (d.lunarDay == 1) "${d.lunarDay}/${d.lunarMonth}" else "${d.lunarDay}",
                        style = MaterialTheme.typography.labelSmall, maxLines = 1,
                        color = if (d.lunarDay == 1 && !isToday) MaterialTheme.colorScheme.error else fg.copy(alpha = 0.75f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        val mp = remember(d.jd) { MoonPhase.of(d.solarDay, d.solarMonth, d.solarYear) }
                        MoonDisc(mp.fraction, Modifier.size(9.dp), dark = fg.copy(alpha = 0.25f), ring = fg)
                        if (d.holidays.isNotEmpty()) Box(Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error))
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Thiên · Địa · Nhân

@Composable
fun ThienDiaNhanCard(d: DayInfo, settings: AppSettings, onEditProfile: () -> Unit) {
    ShadowCard(elevation = 6.dp) {
        SectionTitle("Thiên · Địa · Nhân")
        Text("Hợp mệnh · Đúng việc · Thuận hướng", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (settings.birthYear == 0) {
            Text("Nhập năm sinh để xem chỉ số ngày tốt theo tuổi của bạn.")
            Button(onClick = onEditProfile) { Text("Nhập năm sinh") }
            return@ShadowCard
        }
        val rep = remember(d.jd, settings.birthYear) { TuoiHop.report(settings.birthYear, d) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onEditProfile)) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text(DayInfo.CHI[(settings.birthYear + 8) % 12], fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(settings.userName.ifBlank { "Bạn" }, fontWeight = FontWeight.SemiBold)
                Text("Tuổi ${TuoiHop.tuoiName(settings.birthYear)} (${settings.birthYear})", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        // Chỉ liệt kê quan hệ hợp/xung giữa tuổi và can chi ngày, tháng, năm (không chấm điểm)
        listOf("Ngày ${d.dayCanChi}" to rep.day, "Tháng ${d.monthCanChi}" to rep.month, "Năm ${d.yearCanChi}" to rep.year).forEach { (t, r) ->
            Text(t, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(r.reasons.joinToString("; ").ifBlank { "Không có quan hệ hợp hay xung đáng kể" }, style = MaterialTheme.typography.bodySmall)
        }
        Text("Theo quy tắc tam hợp, lục hợp, lục xung của địa chi và sinh khắc ngũ hành. Phong tục dân gian, chỉ để tham khảo.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun scoreColor(v: Int): Color = when {
    v >= 65 -> goodColor()
    v >= 45 -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.error
}

@Composable
private fun ScoreLine(label: String, v: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(104.dp), style = MaterialTheme.typography.bodyMedium)
        Text("$v%", fontWeight = FontWeight.Bold, color = scoreColor(v), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ScoreRing(v: Int, size: Dp) {
    val c = scoreColor(v)
    val track = MaterialTheme.colorScheme.outlineVariant
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(Modifier.size(size)) {
            val st = Stroke(10.dp.toPx(), cap = StrokeCap.Round)
            val inset = 6.dp.toPx()
            drawArc(track, 0f, 360f, false, Offset(inset, inset), Size(this.size.width - 2 * inset, this.size.height - 2 * inset), style = st)
            drawArc(c, -90f, 360f * v / 100f, false, Offset(inset, inset), Size(this.size.width - 2 * inset, this.size.height - 2 * inset), style = st)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$v%", fontWeight = FontWeight.Black, fontSize = 22.sp, color = c)
            Text("tổng", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ---------------------------------------------------------------- Chọn ngày theo việc

private val GRID_ACTIVITIES = listOf(
    Activity.CUOI_HOI, Activity.MUA_XE, Activity.MUA_NHA, Activity.KHAI_TRUONG, Activity.DONG_THO, Activity.NHAP_TRACH,
    Activity.KY_KET, Activity.MUA_TAI_SAN, Activity.XUAT_HANH, Activity.NHAP_HOC, Activity.CUNG_LE, Activity.NHAN_VIEC,
)

@Composable
fun ActivityGridCard(fromJd: Int, onOpenDay: (Int) -> Unit) {
    var picked by remember { mutableStateOf<Activity?>(null) }
    ShadowCard(elevation = 5.dp) {
        SectionTitle("Sắp có việc quan trọng?")
        Text("Chọn việc để tìm ngày tốt trong 12 tháng tới", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        GRID_ACTIVITIES.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { a ->
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable { picked = a }.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ActivityIcon(a, 52.dp)
                        Text(a.label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2,
                            modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
    picked?.let { a ->
        val from = remember(fromJd) { dayOf(fromJd) }
        val result = remember(a, fromJd) { GoodDayFinder.find(a, from.solarDay, from.solarMonth, from.solarYear, 365, 12) }
        AlertDialog(
            onDismissRequest = { picked = null },
            icon = { ActivityIcon(a, 44.dp) },
            title = { Text("Ngày tốt: ${a.label}") },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (result.isEmpty()) Text("Không tìm thấy ngày phù hợp trong 12 tháng tới.")
                    result.forEach { d ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .clickable { picked = null; onOpenDay(d.jd) }.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${DayInfo.THU[d.weekday]}, ${d.solarDay}/${d.solarMonth}/${d.solarYear}", fontWeight = FontWeight.Bold)
                                Text("${d.lunarText()} âm · ${d.dayCanChi} · Trực ${d.truc}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(d.starsText(), color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false)
                        }
                    }
                    val why = GoodDayFinder.whyNot(from, a)
                    if (why != null) Text("Ngày đang xem không hợp: $why", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = { TextButton(onClick = { picked = null }) { Text("Đóng") } },
        )
    }
}

@Composable
fun ActivityIcon(a: Activity, size: Dp) {
    val bg = Color(
        when (a) {
            Activity.CUOI_HOI -> 0xFFFCE4EC; Activity.MUA_XE -> 0xFFE8EAF6; Activity.MUA_NHA -> 0xFFE8F5E9
            Activity.KHAI_TRUONG -> 0xFFE3F2FD; Activity.DONG_THO -> 0xFFE0F2F1; Activity.NHAP_TRACH -> 0xFFE1F5FE
            Activity.KY_KET -> 0xFFFFF3E0; Activity.MUA_TAI_SAN -> 0xFFFFF8E1; Activity.XUAT_HANH -> 0xFFEDE7F6
            Activity.NHAP_HOC -> 0xFFE3F2FD; Activity.CUNG_LE -> 0xFFFBE9E7; else -> 0xFFF1F8E9
        },
    )
    Canvas(Modifier.size(size).shadow(3.dp, CircleShape).clip(CircleShape).background(bg)) { drawActivity(a) }
}

private fun DrawScope.drawActivity(a: Activity) {
    val u = size.minDimension / 48f
    fun o(x: Float, y: Float) = Offset(x * u, y * u)
    fun s(w: Float, h: Float) = Size(w * u, h * u)
    fun path(vararg pts: Float): Path = Path().apply {
        moveTo(pts[0] * u, pts[1] * u)
        var i = 2
        while (i < pts.size) { lineTo(pts[i] * u, pts[i + 1] * u); i += 2 }
        close()
    }
    val red = Color(0xFFD32F2F); val blue = Color(0xFF3949AB); val green = Color(0xFF388E3C); val gold = Color(0xFFF9A825)
    val brown = Color(0xFF6D4C41); val teal = Color(0xFF00897B)
    when (a) {
        Activity.CUOI_HOI -> { // hai trái tim
            for ((cx, col) in listOf(19f to red, 29f to Color(0xFFF06292))) {
                val p = Path().apply {
                    moveTo(cx * u, 34f * u)
                    cubicTo((cx - 11f) * u, 26f * u, (cx - 9f) * u, 13f * u, cx * u, 19f * u)
                    cubicTo((cx + 9f) * u, 13f * u, (cx + 11f) * u, 26f * u, cx * u, 34f * u); close()
                }
                drawPath(p, col)
            }
        }
        Activity.MUA_XE -> { // ô tô
            drawPath(path(9f, 28f, 14f, 19f, 33f, 19f, 39f, 28f), blue)
            drawRoundRect(blue, o(6f, 26f), s(36f, 9f), androidx.compose.ui.geometry.CornerRadius(3f * u))
            drawRect(Color(0xFFC5CAE9), o(16f, 21f), s(7f, 6f)); drawRect(Color(0xFFC5CAE9), o(25f, 21f), s(7f, 6f))
            drawCircle(Color(0xFF263238), 4f * u, o(15f, 35f)); drawCircle(Color(0xFF263238), 4f * u, o(33f, 35f))
        }
        Activity.MUA_NHA, Activity.NHAP_TRACH -> { // ngôi nhà
            drawPath(path(24f, 9f, 40f, 23f, 8f, 23f), if (a == Activity.MUA_NHA) green else teal)
            drawRect(if (a == Activity.MUA_NHA) Color(0xFF2E7D32) else Color(0xFF00695C), o(12f, 23f), s(24f, 16f))
            drawRect(Color.White, o(21f, 29f), s(6f, 10f))
            if (a == Activity.NHAP_TRACH) drawCircle(gold, 3.5f * u, o(36f, 12f))
        }
        Activity.KHAI_TRUONG -> { // băng đỏ + kéo
            drawLine(red, o(6f, 22f), o(42f, 22f), 4f * u, StrokeCap.Round)
            drawPath(path(24f, 22f, 18f, 34f, 22f, 34f), red); drawPath(path(24f, 22f, 30f, 34f, 26f, 34f), red)
            drawCircle(blue, 4f * u, o(16f, 13f), style = Stroke(2.2f * u)); drawCircle(blue, 4f * u, o(32f, 13f), style = Stroke(2.2f * u))
            drawLine(blue, o(19f, 16f), o(29f, 28f), 2.2f * u); drawLine(blue, o(29f, 16f), o(19f, 28f), 2.2f * u)
        }
        Activity.DONG_THO -> { // xẻng + đống đất
            drawArc(brown, 180f, 180f, true, o(10f, 30f), s(28f, 16f))
            drawLine(Color(0xFF455A64), o(34f, 8f), o(22f, 28f), 2.6f * u, StrokeCap.Round)
            drawPath(path(18f, 26f, 26f, 30f, 22f, 36f, 15f, 33f), Color(0xFF455A64))
        }
        Activity.KY_KET -> { // giấy + bút
            drawRect(Color.White, o(12f, 9f), s(20f, 28f)); drawRect(Color(0xFFFFB74D), o(12f, 9f), s(20f, 28f), style = Stroke(1.5f * u))
            for (y in listOf(16f, 21f, 26f)) drawLine(Color(0xFFBCAAA4), o(15f, y), o(29f, y), 1.4f * u)
            drawLine(Color(0xFFE65100), o(38f, 18f), o(26f, 36f), 3f * u, StrokeCap.Round)
        }
        Activity.MUA_TAI_SAN, Activity.CAU_TAI -> { // nén vàng
            drawPath(path(10f, 30f, 14f, 22f, 34f, 22f, 38f, 30f), gold)
            drawArc(Color(0xFFFBC02D), 180f, 180f, true, o(17f, 14f), s(14f, 16f))
            drawRoundRect(Color(0xFFF57F17), o(8f, 29f), s(32f, 7f), androidx.compose.ui.geometry.CornerRadius(3f * u))
        }
        Activity.XUAT_HANH -> { // máy bay
            drawPath(path(8f, 26f, 40f, 14f, 30f, 36f, 24f, 28f), Color(0xFF5E35B1))
            drawPath(path(24f, 28f, 22f, 36f, 30f, 36f), Color(0xFF7E57C2))
        }
        Activity.NHAP_HOC -> { // sách
            drawPath(path(24f, 15f, 9f, 11f, 9f, 34f, 24f, 38f), Color(0xFF1E88E5))
            drawPath(path(24f, 15f, 39f, 11f, 39f, 34f, 24f, 38f), Color(0xFF64B5F6))
        }
        Activity.CUNG_LE -> { // bát hương
            for (x in listOf(19f, 24f, 29f)) {
                drawLine(Color(0xFFBF360C), o(x, 10f), o(x, 26f), 1.8f * u)
                drawCircle(Color(0xFFFF7043), 1.6f * u, o(x, 10f))
            }
            drawArc(Color(0xFF8D6E63), 0f, 180f, true, o(12f, 18f), s(24f, 20f))
        }
        Activity.NHAN_VIEC -> { // cặp tài liệu
            drawRoundRect(brown, o(9f, 17f), s(30f, 20f), androidx.compose.ui.geometry.CornerRadius(3f * u))
            drawRect(brown, o(19f, 12f), s(10f, 6f), style = Stroke(2f * u))
            drawLine(gold, o(9f, 25f), o(39f, 25f), 1.6f * u)
        }
        else -> { // chữ thập y tế
            drawRect(red, o(20f, 10f), s(8f, 28f)); drawRect(red, o(10f, 20f), s(28f, 8f))
        }
    }
}

// ---------------------------------------------------------------- Tiết khí

@Composable
fun TietKhiCard(d: DayInfo) {
    val t = remember(d.jd) { TietKhi(d.solarDay, d.solarMonth, d.solarYear) }
    ShadowCard {
        SectionTitle("Tiết khí")
        Text("Ngày ${t.dayInTerm} · Tháng tiết khí ${t.monthCanChi} (tháng ${t.monthNumber})", fontWeight = FontWeight.SemiBold)
        Text(t.name.uppercase(), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
        Text(t.desc, style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            for (i in 1..t.termDays) {
                val on = i <= t.dayInTerm
                Box(
                    Modifier.weight(1f).height(18.dp).clip(RoundedCornerShape(4.dp))
                        .background(if (on) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    if (i == t.dayInTerm || i == 1 || i == t.termDays) Text("$i", fontSize = 9.sp, maxLines = 1,
                        color = if (on) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(t.name, fontWeight = FontWeight.Bold)
                Text("Khởi tiết: " + TietKhi.formatLocal(t.startJdLocal), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(t.nextName, fontWeight = FontWeight.Bold)
                Text("Chuyển tiết: " + TietKhi.formatLocal(t.nextJdLocal), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------- Thông tin thêm

private fun hanhColor(h: String): Long = when (h) {
    "Mộc" -> 0xFF2E7D32; "Hỏa" -> 0xFFD84315; "Thổ" -> 0xFF8D6E00; "Kim" -> 0xFF78909C; else -> 0xFF1565C0
}

@Composable
fun NguHanhTuoiXungCard(d: DayInfo, a: Almanac) {
    ShadowCard {
        SectionTitle("Ngũ hành ngày")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(a.hanhNgay, fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall, color = Color(hanhColor(a.hanhNgay)))
                Text("Nạp âm: ${a.napAmNgay}", style = MaterialTheme.typography.bodyMedium)
                Text(a.quanHeCanChi, style = MaterialTheme.typography.bodySmall,
                    color = if (a.quanHeTot) goodColor() else MaterialTheme.colorScheme.error)
            }
            Box(
                Modifier.size(56.dp).shadow(3.dp, CircleShape).clip(CircleShape).background(Color(hanhColor(a.hanhNgay))),
                contentAlignment = Alignment.Center,
            ) { Text(a.hanhNgay, color = Color.White, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(6.dp))
        SectionTitle("Những tuổi xung với ngày", MaterialTheme.colorScheme.error)
        val tuoi = a.tuoiXung.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        tuoi.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { t ->
                    val chi = t.substringAfterLast(' ')
                    val idx = DayInfo.CHI.indexOf(chi)
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(44.dp).shadow(2.dp, CircleShape).clip(CircleShape)
                                .background(Color(hanhColor(Almanac.HANH_CHI[idx.coerceAtLeast(0)]))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(if (idx >= 0) DayInfo.CON_GIAP[idx] else chi, color = Color.White, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(t, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        if (a.tuoiNgoaiLe.isNotEmpty()) Text("Ngoại lệ (không bị hành ngày khắc): ${a.tuoiNgoaiLe}",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(a.hopXung, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (a.tamSat.isNotEmpty()) Text(a.tamSat, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun KienTruTuCard(d: DayInfo) {
    val i = NhiThapBatTu.index(d.tu)
    ShadowCard {
        SectionTitle("Nhị thập bát tú")
        if (i >= 0) {
            Text(
                "${NhiThapBatTu.fullName(i)}: ${if (d.tuTot) "Tốt (Kiết tú)" else "Xấu (Hung tú)"}",
                fontWeight = FontWeight.Bold, color = if (d.tuTot) goodColor() else MaterialTheme.colorScheme.error,
            )
            Text("Tướng tinh con ${NhiThapBatTu.CON_VAT[i]} · hành ${NhiThapBatTu.HANH[i % 7]}", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(NhiThapBatTu.meaning(i), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Suppress("unused") private val keepLunar = LunarCalendar.TZ
