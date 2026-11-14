@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Random
import vn.lichviet.app.ui.ConverterSection
import vn.lichviet.app.ui.KeyValue
import vn.lichviet.app.ui.Section
import vn.lichviet.app.ui.goodColor
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.BatTrach
import vn.lichviet.core.fortune.Faq
import vn.lichviet.core.fortune.GiaiMong
import vn.lichviet.core.fortune.KinhDich
import vn.lichviet.core.fortune.LoBan
import vn.lichviet.core.fortune.Numerology
import vn.lichviet.core.fortune.SongKhoe
import vn.lichviet.core.fortune.TenHay
import vn.lichviet.core.fortune.VanKhan

@Composable
fun DoiNgayScreen(todayJd: Int, onOpenDay: (Int) -> Unit) = ConverterSection(todayJd, onOpenDay)

@Composable
fun GieoQueScreen() {
    var q by rememberSaveable { mutableStateOf("") }
    var seed by rememberSaveable { mutableStateOf(0L) }
    Section("Thành tâm nghĩ về việc cần hỏi") {
        OutlinedTextField(q, { q = it }, label = { Text("Việc cần hỏi (không bắt buộc)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { seed = System.nanoTime() }, modifier = Modifier.fillMaxWidth()) { Text("Gieo quẻ (3 đồng xu × 6 lần)") }
        Note("Tĩnh tâm, tập trung vào một câu hỏi rồi bấm gieo. Mỗi việc chỉ nên hỏi một lần.")
    }
    if (seed == 0L) return
    val c = remember(seed) { KinhDich.cast(Random(seed)) }
    Section("Quẻ chính: ${c.mainName}") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HexagramDrawing(c.lines)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Quẻ số ${c.main}", fontWeight = FontWeight.Bold)
                val cat = KinhDich.CAT[c.main]
                Text(if (cat == 2) "Cát" else if (cat == 1) "Bình" else "Hung", fontWeight = FontWeight.Bold,
                    color = if (cat == 2) goodColor() else if (cat == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
            }
        }
        if (q.isNotBlank()) Note("Việc hỏi: $q")
        Para(KinhDich.Y[c.main])
    }
    if (c.changed != 0) Section("Quẻ biến: ${c.changedName}") {
        Note("Hào động (đánh dấu ●) cho biết xu hướng sự việc chuyển sang quẻ biến.")
        Para(KinhDich.Y[c.changed])
    }
}

@Composable
private fun HexagramDrawing(lines: IntArray) {
    val ink = MaterialTheme.colorScheme.onSurface
    val mark = MaterialTheme.colorScheme.primary
    Canvas(Modifier.size(90.dp, 96.dp)) {
        val h = size.height / 6f
        for (i in 0 until 6) {
            val v = lines[i]
            val y = size.height - (i + 0.5f) * h
            val yang = v == 7 || v == 9
            val w = size.width * 0.8f
            if (yang) drawLine(ink, Offset(0f, y), Offset(w, y), strokeWidth = h * 0.45f)
            else {
                drawLine(ink, Offset(0f, y), Offset(w * 0.42f, y), strokeWidth = h * 0.45f)
                drawLine(ink, Offset(w * 0.58f, y), Offset(w, y), strokeWidth = h * 0.45f)
            }
            if (v == 6 || v == 9) drawCircle(mark, h * 0.2f, Offset(size.width * 0.92f, y))
        }
    }
}

@Composable
fun GiaiMongScreen() {
    var q by rememberSaveable { mutableStateOf("") }
    Section("Bạn mơ thấy gì?") {
        OutlinedTextField(q, { q = it }, label = { Text("Ví dụ: rắn, nước, răng rụng…") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    }
    val list = remember(q) { GiaiMong.search(q) }
    if (list.isEmpty()) Section("Không tìm thấy") { Para("Thử từ khóa ngắn hơn, ví dụ \"cá\", \"nhà\", \"tiền\".") }
    list.forEach { e -> Section("Mơ thấy ${e[0].lowercase()}") { Para(e[1]) } }
    Note("Giải mộng theo quan niệm dân gian, mang tính tham khảo và giải trí.")
}

private fun ngayAmText(jd: Int): String {
    val t = LunarCalendar.jdToDate(jd)
    val d = DayInfo.of(t[0], t[1], t[2])
    val ngay = if (d.lunarDay <= 10) "mùng ${d.lunarDay}" else "${d.lunarDay}"
    val thang = when (d.lunarMonth) { 1 -> "Giêng"; 12 -> "Chạp"; else -> d.lunarMonth.toString() }
    return "ngày $ngay tháng $thang${if (d.leap) " nhuận" else ""} năm ${d.yearCanChi} (tức ngày ${d.solarDay}/${d.solarMonth}/${d.solarYear} dương lịch)"
}

@Composable
fun VanKhanScreen(todayJd: Int) {
    val s = mySettings()
    var open by rememberSaveable { mutableIntStateOf(-1) }
    var jd by rememberSaveable { mutableIntStateOf(todayJd) }
    Section("Chọn bài văn khấn") {
        Note("Ngày, tên và địa chỉ được điền sẵn theo hồ sơ của bạn. Bấm vào tên bài để xem.")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ngày cúng: ${LunarCalendar.jdToDate(jd).let { "${it[0]}/${it[1]}/${it[2]}" }}", Modifier.weight(1f))
            TextButton(onClick = { jd-- }) { Text("◀") }
            TextButton(onClick = { jd = todayJd }) { Text("Hôm nay") }
            TextButton(onClick = { jd++ }) { Text("▶") }
        }
        VanKhan.DATA.forEachIndexed { i, e ->
            Text(e[0], fontWeight = if (open == i) FontWeight.Bold else FontWeight.Medium,
                color = if (open == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { open = if (open == i) -1 else i }.padding(vertical = 8.dp))
        }
    }
    if (open >= 0) {
        val e = VanKhan.DATA[open]
        Section(e[0]) {
            Text(VanKhan.fill(e[1], ngayAmText(jd), s.userName, s.address), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun SongKhoeScreen(todayJd: Int) {
    val d = remember(todayJd) { LunarCalendar.jdToDate(todayJd).let { DayInfo.of(it[0], it[1], it[2]) } }
    Section("Tiết ${d.tietKhiCurrent}") { Para(SongKhoe.tip(d.tietKhiCurrent)) }
    Section("Thói quen tốt mỗi ngày") { SongKhoe.CHUNG.forEach { Bullet(it) } }
    Section("Cả năm theo tiết khí") { SongKhoe.TIPS.forEach { Note(it) } }
    Note("Thông tin chăm sóc sức khỏe chung, không thay cho tư vấn của bác sĩ.")
}

@Composable
fun FaqScreen() {
    var open by rememberSaveable { mutableIntStateOf(-1) }
    Faq.DATA.forEachIndexed { i, e ->
        Section(e[0]) {
            if (open == i) Para(e[1])
            TextButton(onClick = { open = if (open == i) -1 else i }) { Text(if (open == i) "Thu gọn" else "Xem trả lời") }
        }
    }
}

@Composable
fun ChonTenScreen() {
    var male by rememberSaveable { mutableStateOf(true) }
    var hanh by rememberSaveable { mutableIntStateOf(0) }
    var ho by rememberSaveable { mutableStateOf("") }
    var dem by rememberSaveable { mutableStateOf("") }
    Section("Tiêu chí") {
        GenderChips(male) { male = it }
        Text("Ngũ hành của bé (theo năm sinh dự kiến)", style = MaterialTheme.typography.labelMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(TenHay.HANH.size) { i -> FilterChip(hanh == i, { hanh = i }, label = { Text(TenHay.HANH[i]) }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(ho, { ho = it }, label = { Text("Họ") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(dem, { dem = it }, label = { Text("Tên đệm") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        Note("Năm sinh → mệnh: dùng mục Thông Tin Tổng Quan hoặc nhập năm ở mục Người khác.")
    }
    Section("Gợi ý tên hành ${TenHay.HANH[hanh]}") {
        TenHay.DATA[if (male) 0 else 1][hanh].forEach { e ->
            val (ten, y) = e.split("|").let { it[0] to it[1] }
            val full = listOf(ho.trim(), dem.trim(), ten).filter { it.isNotEmpty() }.joinToString(" ")
            val n = Numerology.nameNumber(full, 0)
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(full, fontWeight = FontWeight.Bold)
                    Note(y)
                }
                Text("Số $n", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun LoBanScreen() {
    var type by rememberSaveable { mutableIntStateOf(0) }
    var cm by rememberSaveable { mutableStateOf("81") }
    Section("Kích thước cần đo") {
        LoBan.TYPE.forEachIndexed { i, t -> FilterChip(type == i, { type = i }, label = { Text(t) }) }
        OutlinedTextField(cm, { cm = it.replace(',', '.').filter { c -> c.isDigit() || c == '.' }.take(7) }, label = { Text("Kích thước (cm)") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
    }
    val v = cm.toDoubleOrNull() ?: return
    val c = LoBan.cung(type, v)
    val ok = LoBan.TOT[type][c]
    Section("Kết quả") {
        Text("Cung ${LoBan.CUNG[type][c]}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            color = if (ok) goodColor() else MaterialTheme.colorScheme.error)
        Para(if (ok) "Kích thước ${"%.1f".format(v)} cm rơi vào cung tốt." else "Cung xấu – nên điều chỉnh kích thước.")
        if (!ok) KeyValue("Kích thước tốt gần nhất", "%.1f cm".format(LoBan.nearestGood(type, v)))
        RulerBar(type, v)
    }
    Section("Các cung của thước") {
        LoBan.CUNG[type].forEachIndexed { i, n ->
            Text("${i + 1}. $n", color = if (LoBan.TOT[type][i]) goodColor() else MaterialTheme.colorScheme.error)
        }
        Note("Chiều dài một vòng thước: ${LoBan.LEN[type]} cm.")
    }
}

@Composable
private fun RulerBar(type: Int, v: Double) {
    val n = LoBan.CUNG[type].size
    val good = goodColor(); val bad = MaterialTheme.colorScheme.error; val ink = MaterialTheme.colorScheme.onSurface
    val frac = ((v % LoBan.LEN[type]) / LoBan.LEN[type]).toFloat()
    Canvas(Modifier.fillMaxWidth().height(28.dp)) {
        val w = size.width / n
        for (i in 0 until n) drawRect(if (LoBan.TOT[type][i]) good.copy(alpha = 0.35f) else bad.copy(alpha = 0.25f), Offset(i * w, 0f), androidx.compose.ui.geometry.Size(w - 2f, size.height))
        val x = frac * size.width
        drawLine(ink, Offset(x, 0f), Offset(x, size.height), strokeWidth = 5f)
    }
}

/** La bàn dùng cảm biến xoay; hiện hướng, 24 sơn và đánh giá theo cung phi của bạn. */
@Composable
fun LaBanScreen() {
    val ctx = LocalContext.current
    var az by remember { mutableFloatStateOf(0f) }
    var has by remember { mutableStateOf(true) }
    DisposableEffect(Unit) {
        val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val rot = FloatArray(9); val ori = FloatArray(3)
        val l = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rot, e.values)
                SensorManager.getOrientation(rot, ori)
                val deg = ((Math.toDegrees(ori[0].toDouble()) + 360) % 360).toFloat()
                var diff = deg - az
                if (diff > 180) diff -= 360
                if (diff < -180) diff += 360
                az = (az + diff * 0.25f + 360) % 360   // làm mượt
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        if (sensor == null) has = false else sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(l) }
    }
    if (!has) { Section("Không có cảm biến") { Para("Thiết bị không có cảm biến la bàn.") }; return }
    val p = mySettings().toPerson()
    val dir = BatTrach.dirName(az)
    Section("Hướng ${dir} · ${az.toInt()}° · sơn ${BatTrach.son(az)}") {
        if (p != null) {
            val c = BatTrach.cung(p.lunarYear, p.male)
            val i = BatTrach.huong(c).indexOf(dir)
            if (i >= 0) Text("Với cung ${BatTrach.CUNG[c]}: ${BatTrach.HUONG_TEN[i]} – ${BatTrach.HUONG_Y[i]}",
                color = if (i < 4) goodColor() else MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
        } else Note("Nhập hồ sơ để biết hướng này tốt hay xấu với bạn.")
        Compass(az, p?.let { BatTrach.huong(BatTrach.cung(it.lunarYear, it.male)) })
        Note("Đặt điện thoại nằm ngang, tránh xa vật kim loại. Nếu sai lệch, xoay máy theo hình số 8 để hiệu chỉnh.")
    }
}

@Composable
private fun Compass(az: Float, huong: Array<String>?) {
    val tm = rememberTextMeasurer()
    val ink = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val good = goodColor(); val bad = MaterialTheme.colorScheme.error
    val red = Color(0xFFD32F2F)
    Box(Modifier.fillMaxWidth().aspectRatio(1f).padding(8.dp)) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2, size.height / 2)
            rotate(-az, c) {
                drawCircle(primary.copy(alpha = 0.08f), r, c)
                drawCircle(ink, r, c, style = Stroke(3f))
                drawCircle(ink.copy(alpha = 0.5f), r * 0.72f, c, style = Stroke(2f))
                // tô 8 hướng tốt/xấu
                if (huong != null) for (i in 0 until 8) {
                    val deg = BatTrach.degrees(huong[i]).toFloat()
                    drawArc(if (i < 4) good.copy(alpha = 0.25f) else bad.copy(alpha = 0.18f), deg - 90 - 22.5f, 45f, true,
                        Offset(c.x - r * 0.72f, c.y - r * 0.72f), androidx.compose.ui.geometry.Size(r * 1.44f, r * 1.44f))
                }
                for (i in 0 until 24) {
                    val a = Math.toRadians((i * 15 - 90).toDouble())
                    val p1 = Offset(c.x + (r * 0.9f * Math.cos(a)).toFloat(), c.y + (r * 0.9f * Math.sin(a)).toFloat())
                    val p2 = Offset(c.x + (r * Math.cos(a)).toFloat(), c.y + (r * Math.sin(a)).toFloat())
                    drawLine(ink, p1, p2, strokeWidth = 2f)
                    val label = BatTrach.SON_24[i]
                    val m = tm.measure(label, TextStyle(fontSize = 10.sp, color = ink))
                    val pt = Offset(c.x + (r * 0.81f * Math.cos(a)).toFloat(), c.y + (r * 0.81f * Math.sin(a)).toFloat())
                    drawText(m, topLeft = Offset(pt.x - m.size.width / 2, pt.y - m.size.height / 2))
                }
                listOf("B" to 0, "Đ" to 90, "N" to 180, "T" to 270).forEach { (t, d) ->
                    val a = Math.toRadians((d - 90).toDouble())
                    val m = tm.measure(t, TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (d == 0) red else ink))
                    val pt = Offset(c.x + (r * 0.55f * Math.cos(a)).toFloat(), c.y + (r * 0.55f * Math.sin(a)).toFloat())
                    drawText(m, topLeft = Offset(pt.x - m.size.width / 2, pt.y - m.size.height / 2))
                }
            }
            // kim chỉ hướng điện thoại (cố định, hướng lên)
            val needle = Path().apply { moveTo(c.x, c.y - r * 0.95f); lineTo(c.x - r * 0.05f, c.y); lineTo(c.x + r * 0.05f, c.y); close() }
            drawPath(needle, red)
            drawCircle(ink, r * 0.04f, c)
        }
    }
}
