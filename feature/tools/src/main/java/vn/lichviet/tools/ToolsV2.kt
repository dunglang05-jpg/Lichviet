@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package vn.lichviet.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.Almanac
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.BatTrach
import vn.lichviet.core.fortune.HopTuoi
import vn.lichviet.core.fortune.LoBan
import vn.lichviet.core.fortune.Person
import vn.lichviet.core.fortune.SaoHan
import vn.lichviet.data.Today
import vn.lichviet.ui.Section
import vn.lichviet.ui.GiapImage
import vn.lichviet.ui.ShadowCard
import vn.lichviet.ui.KeyValue
import vn.lichviet.ui.goodColor
import vn.lichviet.ui.theme.LocalLich
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

// ------------------------------------------------------------ khung trang tiện ích

/** Đầu trang tiện ích: dải màu chính, nút quay lại, tiêu đề ở giữa; nội dung nằm trên tấm nền bo tròn. */
@Composable
fun ToolScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val spec = LocalLich.current
    val accent = spec?.accent ?: MaterialTheme.colorScheme.primary
    val on = spec?.onAccent ?: MaterialTheme.colorScheme.onPrimary
    val bg = spec?.bg ?: MaterialTheme.colorScheme.background
    Column(Modifier.background(accent)) {
        Box(Modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 64.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = on)
            }
            Text(title, color = on, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 56.dp, vertical = 10.dp))
        }
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(bg)
                .padding(top = 10.dp, bottom = 24.dp),
            content = content,
        )
    }
}

// ------------------------------------------------------------ chọn năm sinh + giới tính

/** Năm sinh dương lịch, giới tính, sinh trước Tết → Person (năm âm lịch đúng). */
@Composable
fun YearGenderPicker(title: String, key: String, defYear: Int, defMale: Boolean): Person {
    var year by rememberSaveable(key + "y") { mutableIntStateOf(defYear) }
    var male by rememberSaveable(key + "g") { mutableStateOf(defMale) }
    var beforeTet by rememberSaveable(key + "t") { mutableStateOf(false) }
    var typing by remember { mutableStateOf(false) }
    val tet = remember(year) { LunarCalendar.lunarToSolar(1, 1, year, 0) }
    Section(title) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            RoundBtn("−") { if (year > 1900) year-- }
            Box(
                Modifier.padding(horizontal = 16.dp).width(132.dp).clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .4f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .clickable { typing = true }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) { Text("$year", fontSize = 34.sp, fontWeight = FontWeight.Bold) }
            RoundBtn("+") { if (year < 2100) year++ }
        }
        val ly = if (beforeTet) year - 1 else year
        val lc = Math.floorMod(ly + 8, 12)
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            GiapImage(lc, 44.dp)
            Spacer(Modifier.width(10.dp))
            Text("${DayInfo.CAN[Math.floorMod(ly + 6, 10)]} ${DayInfo.CHI[lc]} · tuổi ${DayInfo.CON_GIAP[lc]}", fontWeight = FontWeight.SemiBold)
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Segment("Nam", male, Modifier.weight(1f)) { male = true }
            Segment("Nữ", !male, Modifier.weight(1f)) { male = false }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Sinh trước Tết âm lịch", fontWeight = FontWeight.SemiBold)
                Text("Tết năm $year: ${tet[0]}/${tet[1]}/${tet[2]}. Sinh trước Tết thì tính theo năm âm trước.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(beforeTet, { beforeTet = it })
        }
    }
    if (typing) YearDialog(year, { typing = false }) { year = it; typing = false }
    val ly = if (beforeTet) year - 1 else year
    return remember(ly, male) { Person("", male, 1, 7, ly) }
}

@Composable
private fun RoundBtn(t: String, onClick: () -> Unit) {
    Box(
        Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .6f)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(t, fontSize = 26.sp, color = MaterialTheme.colorScheme.onSurface) }
}

@Composable
private fun Segment(t: String, sel: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val p = MaterialTheme.colorScheme.primary
    Box(
        modifier.clip(RoundedCornerShape(50)).background(if (sel) p else Color.Transparent)
            .border(1.dp, if (sel) p else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
            .clickable(onClick = onClick).padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) { Text(t, fontWeight = FontWeight.Bold, color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface) }
}

@Composable
private fun YearDialog(cur: Int, onDismiss: () -> Unit, onOk: (Int) -> Unit) {
    var t by remember { mutableStateOf(cur.toString()) }
    val v = t.toIntOrNull()?.takeIf { it in 1900..2100 }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Nhập năm sinh") },
        text = { OutlinedTextField(t, { t = it.filter(Char::isDigit).take(4) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) },
        confirmButton = { TextButton(enabled = v != null, onClick = { onOk(v!!) }) { Text("Xong") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } })
}

// ------------------------------------------------------------ Xem tuổi hợp nhau

@Composable
fun HopNhauV2(love: Boolean) {
    val me = mySettings().toPerson()
    val a = YearGenderPicker(if (love) "Bạn" else "Người thứ nhất", if (love) "lv-a" else "hp-a", me?.lunarYear ?: 1990, me?.male ?: true)
    val b = YearGenderPicker(if (love) "Người ấy" else "Người thứ hai", if (love) "lv-b" else "hp-b", (me?.lunarYear ?: 1990) + 2, !(me?.male ?: true))
    val list = remember(a.lunarYear, a.male, b.lunarYear, b.male) { HopTuoi.score(a, b) }
    val total = HopTuoi.total(list)
    val good = goodColor(); val bad = MaterialTheme.colorScheme.error; val mid = Color(0xFF8A6D2B)
    val col = when { total >= 6 -> good; total >= 4 -> mid; else -> bad }
    ShadowCard {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$total/10", fontSize = 56.sp, fontWeight = FontWeight.Black, color = col)
            Text(HopTuoi.rating(total), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = col)
            Text("${a.canChi()} · ${a.napAm()} · cung ${BatTrach.CUNG[BatTrach.cung(a.lunarYear, a.male)]}\nvà ${b.canChi()} · ${b.napAm()} · cung ${BatTrach.CUNG[BatTrach.cung(b.lunarYear, b.male)]}",
                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        }
    }
    Section("Chi tiết 5 tiêu chí") {
        list.forEach { c ->
            val cc = when (c.point) { 2 -> good; 1 -> mid; else -> bad }
            Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(40.dp).border(2.dp, cc, CircleShape), contentAlignment = Alignment.Center) {
                    Text("${c.point}", color = cc, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column(Modifier.padding(start = 14.dp)) {
                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(c.detail, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Note("Mỗi tiêu chí 0–2 điểm. Từ 8 điểm: rất hợp · 6–7: hợp · 4–5: trung bình · dưới 4: không hợp.")
    }
    if (love) {
        val wife = if (!a.male) a else if (!b.male) b else null
        if (wife != null) {
            val y = currentLunarYear()
            Section("Kim lâu của bên nữ") {
                for (k in 0 until 4) KeyValue("Năm ${y + k}", SaoHan.kimLau(wife.tuoiMu(y + k)) ?: "Không phạm")
            }
        }
    }
    Text("Theo quan niệm dân gian (ngũ hành, can chi, Bát trạch) — chỉ mang tính tham khảo.", style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
}

// ------------------------------------------------------------ Thông tin năm

@Composable
fun ThongTinNamV2() {
    var y by rememberSaveable { mutableIntStateOf(Today.ymd()[2]) }
    ShadowCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (y > 1801) y-- }) { Icon(Icons.Filled.ChevronLeft, "Năm trước") }
            Text("Năm $y", fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            IconButton(onClick = { if (y < 2198) y++ }) { Icon(Icons.Filled.ChevronRight, "Năm sau") }
        }
    }
    val leapSolar = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0
    Section("Dương lịch năm $y") {
        Text(if (leapSolar) "366 ngày" else "365 ngày", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(if (leapSolar) "Năm nhuận — tháng 2 có 29 ngày" else "Không nhuận — tháng 2 có 28 ngày", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    val info = remember(y) { yearInfo(y) }
    val can = Math.floorMod(y + 6, 10); val chi = Math.floorMod(y + 8, 12)
    Section("Âm lịch năm ${DayInfo.CAN[can]} ${DayInfo.CHI[chi]}") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GiapImage(chi, 96.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Con ${DayInfo.CON_GIAP[chi]}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text("Mệnh ${Almanac.napAm(can, chi)} (hành ${Almanac.hanhNapAm(can, chi)})", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        KeyValue("Tết Nguyên Đán", info.tet)
        KeyValue("Kết thúc năm âm", info.end)
        KeyValue("Số ngày năm âm", "${info.days} ngày")
        Text(if (info.leap > 0) "Có tháng ${info.leap} nhuận — năm âm có 13 tháng" else "Không có tháng nhuận — năm âm có 12 tháng",
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
    Section("Các tháng âm lịch") {
        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            Text("Tháng", Modifier.weight(1.1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Mùng 1 (dương)", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Số ngày", Modifier.weight(.8f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
        }
        info.months.forEach { m ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Text(m[0], Modifier.weight(1.1f))
                Text(m[1], Modifier.weight(1.2f))
                Text(m[2], Modifier.weight(.8f), textAlign = TextAlign.End,
                    color = if (m[2].contains("Đủ")) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private class YearInfo(val tet: String, val end: String, val days: Int, val leap: Int, val months: List<Array<String>>)

private val TEN_THANG = listOf("Giêng", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "Chạp")

private fun dmyText(jd: Int): String {
    val d = LunarCalendar.jdToDate(jd)
    return "${DayInfo.THU[DayInfo.of(d[0], d[1], d[2]).weekday].replaceFirstChar { it }}, ${d[0]}/${d[1]}/${d[2]}"
}

private fun yearInfo(y: Int): YearInfo {
    val start = LunarCalendar.lunarToSolar(1, 1, y, 0).let { LunarCalendar.jdFromDate(it[0], it[1], it[2]) }
    val next = LunarCalendar.lunarToSolar(1, 1, y + 1, 0).let { LunarCalendar.jdFromDate(it[0], it[1], it[2]) }
    val leap = LunarCalendar.leapMonthOf(y)
    val months = ArrayList<Array<String>>()
    var jd = start
    while (jd < next) {
        val d = LunarCalendar.jdToDate(jd)
        val l = LunarCalendar.solarToLunar(d[0], d[1], d[2])
        val len = LunarCalendar.monthLength(jd)
        val name = "Tháng " + TEN_THANG[l[1] - 1] + if (l[3] == 1) " nhuận" else ""
        months.add(arrayOf(name, "${d[0]}/${d[1]}/${d[2]}", "$len · ${if (len == 30) "Đủ" else "Thiếu"}"))
        jd += len
    }
    return YearInfo(dmyText(start), dmyText(next - 1), next - start, leap, months)
}

// ------------------------------------------------------------ La bàn

@Composable
fun LaBanV2() {
    val ctx = LocalContext.current
    var az by remember { mutableFloatStateOf(0f) }
    var has by remember { mutableStateOf(true) }
    var hold by remember { mutableStateOf(false) }
    DisposableEffect(hold) {
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
                az = (az + diff * 0.25f + 360) % 360
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        if (sensor == null) has = false else if (!hold) sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(l) }
    }
    val me = mySettings().toPerson()
    val red = MaterialTheme.colorScheme.primary
    val dir = BatTrach.dirName(az)
    val back = (az + 180) % 360
    ShadowCard {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (!has) Text("Thiết bị không có cảm biến la bàn.", color = MaterialTheme.colorScheme.error)
            Text("${az.roundToInt() % 360}°", fontSize = 64.sp, fontWeight = FontWeight.Black, color = red)
            Text(dir, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Sơn ${BatTrach.son(az)} · Quẻ ${queOf(az)}", modifier = Modifier.padding(top = 6.dp))
            Text("Tọa (lưng): ${BatTrach.dirName(back)} · sơn ${BatTrach.son(back)} · ${back.roundToInt()}°", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Compass24(az)
            Text(if (hold) "Đã giữ kết quả" else "Đang đo · Tham chiếu Bắc từ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = { hold = !hold }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text(if (hold) "Đo lại" else "Giữ kết quả", fontWeight = FontWeight.Bold)
            }
        }
    }
    val p = YearGenderPicker("Xem hướng hợp tuổi gia chủ", "lb", me?.lunarYear ?: 1990, me?.male ?: true)
    val c = BatTrach.cung(p.lunarYear, p.male)
    val huong = BatTrach.huong(c)
    val i = huong.indexOf(dir)
    Section("Hướng hợp tuổi gia chủ") {
        Text("Gia chủ cung ${BatTrach.CUNG[c]} · ${if (BatTrach.dongTuMenh(c)) "Đông tứ mệnh" else "Tây tứ mệnh"}. Các hướng nên nhìn ra:",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (i >= 0) {
            val ok = i < 4
            val cc = if (ok) goodColor() else MaterialTheme.colorScheme.error
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(cc.copy(alpha = .1f)).padding(14.dp)) {
                Text("$dir · ${BatTrach.HUONG_TEN[i]} · ${if (ok) "Hướng tốt" else "Hướng xấu"}", color = cc, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("${BatTrach.HUONG_Y[i]}. Hướng đang đo thuộc nhóm hướng ${if (ok) "nên chọn" else "nên tránh"}.")
            }
        }
        for (k in 0 until 4) Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Text(huong[k], color = goodColor(), fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Text(BatTrach.HUONG_TEN[k], color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Note("Thông tin Bát trạch mang tính tham khảo. Gần ranh giới giữa hai hướng, hãy đo lại ở nhiều vị trí.")
    }
    Section("Để đo ổn định hơn") {
        Para("Giữ máy nằm ngang, mặt màn hình hướng lên.")
        Para("Tránh nam châm, ốp lưng từ tính, loa và các vật kim loại lớn.")
        Para("Khi cảm biến thiếu chính xác, di chuyển máy theo hình số 8 để hiệu chỉnh.")
        Note("Số đo là hướng Bắc từ, có thể khác hướng Bắc địa lý.")
    }
}

private fun queOf(az: Float): String = listOf("Khảm", "Cấn", "Chấn", "Tốn", "Ly", "Khôn", "Đoài", "Càn")[Math.floorMod(((az + 22.5f) / 45f).toInt(), 8)]

@Composable
private fun Compass24(az: Float) {
    val tm = rememberTextMeasurer()
    val ink = MaterialTheme.colorScheme.onSurface
    val dim = MaterialTheme.colorScheme.onSurfaceVariant
    val red = MaterialTheme.colorScheme.primary
    val face = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f)
    val dirs = listOf("B" to "Khảm", "ĐB" to "Cấn", "Đ" to "Chấn", "ĐN" to "Tốn", "N" to "Ly", "TN" to "Khôn", "T" to "Đoài", "TB" to "Càn")
    Box(Modifier.widthIn(max = 360.dp).fillMaxWidth().aspectRatio(1f).padding(vertical = 12.dp)) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2, size.height / 2)
            drawCircle(face, r, c)
            drawCircle(dim.copy(alpha = .4f), r, c, style = Stroke(3f))
            rotate(-az, c) {
                for (t in 0 until 72) {
                    val a = Math.toRadians((t * 5 - 90).toDouble())
                    val len = if (t % 9 == 0) .1f else .04f
                    drawLine(if (t % 9 == 0) ink else dim.copy(alpha = .5f),
                        Offset(c.x + (r * (.96f - len) * cos(a)).toFloat(), c.y + (r * (.96f - len) * sin(a)).toFloat()),
                        Offset(c.x + (r * .96f * cos(a)).toFloat(), c.y + (r * .96f * sin(a)).toFloat()), if (t % 9 == 0) 4f else 1.5f)
                }
                for (i in 0 until 24) {
                    val deg = i * 15f
                    val m = tm.measure(BatTrach.SON_24[i], TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = if (BatTrach.SON_24[i] in listOf("Tý", "Mão", "Ngọ", "Dậu", "Càn", "Khôn", "Cấn", "Tốn")) red else ink))
                    rotate(deg, c) { drawText(m, topLeft = Offset(c.x - m.size.width / 2, c.y - r * .82f - m.size.height / 2)) }
                }
                dirs.forEachIndexed { k, (s, q) ->
                    val m1 = tm.measure(s, TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black, color = ink))
                    val m2 = tm.measure(q, TextStyle(fontSize = 13.sp, color = dim))
                    rotate(k * 45f, c) {
                        drawText(m1, topLeft = Offset(c.x - m1.size.width / 2, c.y - r * .6f - m1.size.height / 2))
                        drawText(m2, topLeft = Offset(c.x - m2.size.width / 2, c.y - r * .44f - m2.size.height / 2))
                    }
                }
            }
            drawCircle(red.copy(alpha = .12f), r * .25f, c)
            val arrow = Path().apply { moveTo(c.x, c.y - r * .14f); lineTo(c.x + r * .08f, c.y + r * .1f); lineTo(c.x, c.y + r * .04f); lineTo(c.x - r * .08f, c.y + r * .1f); close() }
            drawPath(arrow, red)
            val tri = Path().apply { moveTo(c.x - r * .05f, c.y - r * 1.0f); lineTo(c.x + r * .05f, c.y - r * 1.0f); lineTo(c.x, c.y - r * .9f); close() }
            drawPath(tri, red)
        }
    }
}

// ------------------------------------------------------------ Thước Lỗ Ban

private val VAT = listOf(
    Triple("Cửa đi", 0, "Đo lọt lòng (thông thủy) khung cửa: rộng và cao."),
    Triple("Cửa sổ", 0, "Đo lọt lòng (thông thủy) khung cửa sổ."),
    Triple("Ô thoáng", 0, "Đo khoảng lọt sáng, lọt gió."),
    Triple("Giường", 1, "Đo khối đặc: dài, rộng, cao của giường."),
    Triple("Bậc thang", 1, "Đo khối đặc: chiều cao, rộng bậc."),
    Triple("Bàn thờ", 2, "Đo kích thước bàn thờ, tủ thờ."),
    Triple("Đồ thờ", 2, "Đo bát hương, khám thờ, đồ thờ cúng."),
)

@Composable
fun LoBanV2() {
    var vat by rememberSaveable { mutableIntStateOf(0) }
    var cm by rememberSaveable { mutableFloatStateOf(81f) }
    var typing by remember { mutableStateOf(false) }
    val type = VAT[vat].second
    Section("Vật cần đo") {
        androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VAT.forEachIndexed { i, v -> FilterChip(vat == i, { vat = i }, label = { Text(v.first) }) }
        }
        Text(LoBan.TYPE[type], color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 6.dp))
        Text(VAT[vat].third, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    ShadowCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { cm = (cm - .5f).coerceAtLeast(1f) }) { Icon(Icons.Outlined.RemoveCircleOutline, "Giảm", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) }
            Column(Modifier.weight(1f).clickable { typing = true }, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(fmt(cm) + " cm", fontSize = 48.sp, fontWeight = FontWeight.Black)
                Text("Chạm để gõ số", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = { cm = (cm + .5f).coerceAtMost(1000f) }) { Icon(Icons.Outlined.AddCircleOutline, "Tăng", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) }
        }
        Ruler(type, cm) { cm = it }
        Text("Kéo thước sang trái, phải để đổi số đo. Cung đỏ là cung tốt, cung đen là cung xấu.", color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
    val c = LoBan.cung(type, cm.toDouble())
    val s = LoBan.sub(type, cm.toDouble())
    val ok = LoBan.TOT[type][c]
    val col = if (ok) goodColor() else MaterialTheme.colorScheme.error
    val rg = LoBan.range(type, cm.toDouble())
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).border(2.dp, col, RoundedCornerShape(24.dp)).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(if (ok) "Kích thước TỐT" else "Kích thước XẤU", fontSize = 28.sp, fontWeight = FontWeight.Black, color = col)
        Text("Cung ${LoBan.CUNG[type][c]} · ${LoBan.SUB[type][c][s]}", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(LoBan.Y[type][c], textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
        Text("Cung này kéo dài từ ${fmt(rg[0].toFloat())} đến ${fmt(rg[1].toFloat())} cm.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (!ok) Text("Kích thước tốt gần nhất: ${fmt(LoBan.nearestGood(type, cm.toDouble()).toFloat())} cm", color = goodColor(),
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
    Text("Theo quan niệm phong thủy dân gian — chỉ mang tính tham khảo.", style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
    if (typing) {
        var t by remember { mutableStateOf(fmt(cm)) }
        val v = t.replace(',', '.').toFloatOrNull()?.takeIf { it in 1f..1000f }
        AlertDialog(onDismissRequest = { typing = false }, title = { Text("Nhập kích thước (cm)") },
            text = { OutlinedTextField(t, { t = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' }.take(6) }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)) },
            confirmButton = { TextButton(enabled = v != null, onClick = { cm = v!!; typing = false }) { Text("Xong") } },
            dismissButton = { TextButton(onClick = { typing = false }) { Text("Hủy") } })
    }
}

private fun fmt(v: Float): String = if (v == floor(v)) v.toInt().toString() else "%.1f".format(v).replace('.', ',')

/** Thước Lỗ Ban vẽ như thước cuộn thật: thân vàng, cung tốt màu đỏ, cung xấu màu đen, kim xanh ở giữa. */
@Composable
private fun Ruler(type: Int, cm: Float, onChange: (Float) -> Unit) {
    val tm = rememberTextMeasurer()
    val body = Color(0xFFF5C842); val bodyDark = Color(0xFFE0A91E); val ink = Color(0xFF1B1B1B)
    val red = Color(0xFFC62828); val needle = Color(0xFF1565C0)
    val pxPerCm = 40f
    var drag by remember { mutableFloatStateOf(cm) }
    Canvas(
        Modifier.fillMaxWidth().height(210.dp).padding(vertical = 10.dp).clip(RoundedCornerShape(10.dp)).pointerInput(type) {
            detectHorizontalDragGestures(onDragStart = { drag = cm }) { ch, dx ->
                ch.consume(); drag = (drag - dx / (pxPerCm * density)).coerceIn(1f, 1000f)
                onChange((drag * 10).roundToInt() / 10f)
            }
        },
    ) {
        val k = pxPerCm * density
        val cx = size.width / 2
        val len = LoBan.LEN[type].toFloat(); val n = LoBan.CUNG[type].size; val w = len / n
        val from = cm - cx / k; val to = cm + cx / k
        val x = { v: Float -> cx + (v - cm) * k }
        val H = size.height
        // thân thước vàng, viền dưới đậm như mép kim loại
        drawRect(body, Offset.Zero, Size(size.width, H))
        drawRect(bodyDark, Offset(0f, H - 8f), Size(size.width, 8f))
        // vạch mm / cm
        val mmFrom = floor(from * 10).toInt(); val mmTo = (to * 10).toInt()
        for (mm in mmFrom..mmTo) {
            val xx = x(mm / 10f)
            val h = when { mm % 10 == 0 -> 34f * density / 2.6f; mm % 5 == 0 -> 24f * density / 2.6f; else -> 14f * density / 2.6f }
            drawLine(ink, Offset(xx, 0f), Offset(xx, h), if (mm % 10 == 0) 3f else 1.3f)
            if (mm % 10 == 0) {
                val v = mm / 10
                val m = tm.measure("$v", TextStyle(fontSize = 14.sp, fontWeight = if (v % 5 == 0) FontWeight.ExtraBold else FontWeight.SemiBold, color = ink))
                drawText(m, topLeft = Offset(xx - m.size.width / 2, 36f * density / 2.6f))
            }
        }
        // dải cung lớn và cung nhỏ
        val bandY = H * .42f; val bandH = H * .24f; val subY = bandY + bandH + 3f; val subH = H * .22f
        var g = floor(from / w).toInt()
        while (g * w <= to) {
            val ci = Math.floorMod(g, n); val ok = LoBan.TOT[type][ci]
            val col = if (ok) red else ink
            val x0 = x(g * w); val x1 = x((g + 1) * w)
            drawRect(col, Offset(x0, bandY), Size(x1 - x0 - 2f, bandH))
            val m = tm.measure(LoBan.CUNG[type][ci].uppercase(), TextStyle(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = body))
            val mid = ((x0 + x1) / 2).coerceIn(x0 + m.size.width / 2f + 4f, x1 - m.size.width / 2f - 4f)
            drawText(m, topLeft = Offset(mid - m.size.width / 2, bandY + (bandH - m.size.height) / 2))
            val subs = LoBan.SUB[type][ci]; val sw = (x1 - x0) / subs.size
            subs.forEachIndexed { j, sn ->
                val sx = x0 + j * sw
                drawLine(col, Offset(sx, subY), Offset(sx, subY + subH), 2f)
                val ms = tm.measure(sn, TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = col), maxLines = 2,
                    overflow = TextOverflow.Ellipsis, constraints = androidx.compose.ui.unit.Constraints(maxWidth = (sw - 6f).toInt().coerceAtLeast(1)))
                drawText(ms, topLeft = Offset(sx + (sw - ms.size.width) / 2, subY + (subH - ms.size.height) / 2))
            }
            g++
        }
        // kim đo
        drawLine(needle, Offset(cx, 0f), Offset(cx, H), 5f)
        val tri = Path().apply { moveTo(cx - 16f, 0f); lineTo(cx + 16f, 0f); lineTo(cx, 22f); close() }
        drawPath(tri, needle)
    }
}
