@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.tools

import vn.lichviet.ui.*
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.lichviet.ui.Section
import vn.lichviet.ui.ShadowCard
import vn.lichviet.ui.goodColor
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.Person
import vn.lichviet.data.AppSettings
import vn.lichviet.data.FamilyRepository
import vn.lichviet.data.SettingsRepository

fun validDate(d: Int, m: Int, y: Int): Boolean {
    if (y < 1900 || y > 2199 || m !in 1..12 || d < 1) return false
    val days = intArrayOf(31, if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    return d <= days[m - 1]
}

fun AppSettings.toPerson(): Person? =
    if (validDate(birthD, birthM, birthY)) Person(userName.ifBlank { "Tôi" }, male, birthD, birthM, birthY, birthHour) else null

@Composable
fun mySettings(): AppSettings {
    val ctx = LocalContext.current
    val s by SettingsRepository.get(ctx).settings.collectAsState()
    return s
}

/** Ô nhập ngày/tháng/năm dương lịch. */
@Composable
fun DateFields(d: String, m: String, y: String, label: String = "Ngày sinh (dương lịch)", onChange: (String, String, String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val has = validDate(d.toIntOrNull() ?: 0, m.toIntOrNull() ?: 0, y.toIntOrNull() ?: 0)
    InputBox(label, if (has) "$d/$m/$y" else "Chọn ngày", has, Modifier.fillMaxWidth()) { open = true }
    if (open) BirthDatePickerDialog(d, m, y, { open = false }) { a, b, c -> onChange(a, b, c) }
}

/** Hộp thoại chọn ngày dương lịch (nhập tay hoặc lịch). */
@Composable
fun BirthDatePickerDialog(d: String, m: String, y: String, onDismiss: () -> Unit, onPick: (String, String, String) -> Unit) {
    val has = validDate(d.toIntOrNull() ?: 0, m.toIntOrNull() ?: 0, y.toIntOrNull() ?: 0)
    val init = if (has) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        clear(); set(y.toInt(), m.toInt() - 1, d.toInt())
    }.timeInMillis else null
    val st = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = init, initialDisplayMode = androidx.compose.material3.DisplayMode.Input,
        yearRange = 1900..2100)
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = {
            st.selectedDateMillis?.let { ms ->
                val c = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply { timeInMillis = ms }
                onPick(c.get(java.util.Calendar.DAY_OF_MONTH).toString(), (c.get(java.util.Calendar.MONTH) + 1).toString(), c.get(java.util.Calendar.YEAR).toString())
            }
            onDismiss()
        }) { Text("Chọn") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    ) { androidx.compose.material3.DatePicker(st, showModeToggle = true, title = null) }
}

/** Ô nhập kiểu thẻ: nhãn nhỏ phía trên, giá trị lớn, viền bo tròn; bấm để mở bộ chọn. */
@Composable
fun InputBox(label: String, value: String, filled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = if (filled) FontWeight.Bold else FontWeight.Normal,
                color = if (filled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

/** Bộ chỉnh số: nhãn, nút −, giá trị, nút +. */
@Composable
fun NumberStepper(label: String, value: Int, range: IntRange, modifier: Modifier = Modifier, onChange: (Int) -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .35f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)).padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("−", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(CircleShape).clickable { onChange(if (value <= range.first) range.last else value - 1) }.padding(horizontal = 10.dp))
            Text("$value", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("+", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(CircleShape).clickable { onChange(if (value >= range.last) range.first else value + 1) }.padding(horizontal = 10.dp))
        }
    }
}

@Composable
fun GenderChips(male: Boolean, onChange: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(male, { onChange(true) }, label = { Text("Nam") })
        FilterChip(!male, { onChange(false) }, label = { Text("Nữ") })
    }
}

/** Hộp thoại hồ sơ đầy đủ: tên, giới tính, ngày sinh dương lịch, địa chỉ (dùng cho văn khấn). */
@Composable
fun FullProfileDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val s = repo.settings.value
    var name by remember { mutableStateOf(s.userName) }
    var male by remember { mutableStateOf(s.male) }
    var d by remember { mutableStateOf(if (s.birthD > 0) s.birthD.toString() else "") }
    var m by remember { mutableStateOf(if (s.birthM > 0) s.birthM.toString() else "") }
    var y by remember { mutableStateOf(if (s.birthY > 0) s.birthY.toString() else "") }
    var addr by remember { mutableStateOf(s.address) }
    var hour by remember { mutableStateOf(if (s.birthHour >= 0) s.birthHour.toString() else "") }
    val ok = validDate(d.toIntOrNull() ?: 0, m.toIntOrNull() ?: 0, y.toIntOrNull() ?: 0)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hồ sơ của bạn") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Họ và tên") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                GenderChips(male) { male = it }
                DateFields(d, m, y) { a, b, c -> d = a; m = b; y = c }
                OutlinedTextField(hour, { hour = it.filter(Char::isDigit).take(2) }, label = { Text("Giờ sinh 0–23 (không bắt buộc)") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(addr, { addr = it }, label = { Text("Địa chỉ (cho văn khấn)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = ok, onClick = {
                val dd = d.toInt(); val mm = m.toInt(); val yy = y.toInt()
                val lunarYear = LunarCalendar.solarToLunar(dd, mm, yy)[2]
                repo.update { it.copy(userName = name.trim(), male = male, birthD = dd, birthM = mm, birthY = yy, birthYear = lunarYear, address = addr.trim(),
                    birthHour = hour.toIntOrNull()?.takeIf { it in 0..23 } ?: -1) }
                onDismiss()
            }) { Text("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

/** Thẻ hồ sơ ở đầu mục Tiện ích. */
@Composable
fun ProfileCard() {
    val s = mySettings()
    val p = s.toPerson()
    var edit by rememberSaveable { mutableStateOf(false) }
    ShadowCard(elevation = 6.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (s.userName.isBlank()) "Chưa có hồ sơ" else s.userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    if (p == null) "Nhập ngày sinh để xem tử vi, vận hạn, thần số học…"
                    else "${p.gioi()} · ${p.day}/${p.month}/${p.year} · Tuổi ${p.canChi()} · Mệnh ${p.napAm()}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = { edit = true }) { Text(if (p == null) "Nhập" else "Sửa") }
        }
    }
    if (edit) FullProfileDialog { edit = false }
}

/**
 * Chọn người để xem: "Tôi", một thành viên gia đình có ngày sinh, hoặc nhập người khác.
 * Trả về [Person] hoặc null nếu chưa đủ thông tin.
 */
@Composable
fun PersonPicker(title: String, preferMe: Boolean = true, key: String = title): Person? {
    val ctx = LocalContext.current
    val s = mySettings()
    val members by FamilyRepository.get(ctx).members.collectAsState()
    val withBirth = members.filter { it.hasBirth }
    var sel by rememberSaveable(key) { mutableStateOf(if (preferMe) "me" else "other") }
    var name by rememberSaveable(key + "n") { mutableStateOf("") }
    var male by rememberSaveable(key + "g") { mutableStateOf(!preferMe) }
    var d by rememberSaveable(key + "d") { mutableStateOf("") }
    var m by rememberSaveable(key + "m") { mutableStateOf("") }
    var y by rememberSaveable(key + "y") { mutableStateOf("") }
    var edit by rememberSaveable(key + "e") { mutableStateOf(false) }
    var hh by rememberSaveable(key + "h") { mutableStateOf("") }
    val me = s.toPerson()
    val result: Person? = when {
        sel == "me" -> me
        sel == "other" -> {
            val dd = d.toIntOrNull() ?: 0; val mm = m.toIntOrNull() ?: 0; val yy = y.toIntOrNull() ?: 0
            if (validDate(dd, mm, yy)) Person(name.ifBlank { "Người khác" }, male, dd, mm, yy, hh.toIntOrNull()?.takeIf { it in 0..23 } ?: -1) else null
        }
        else -> withBirth.firstOrNull { it.id.toString() == sel }?.let { Person(it.name, it.male, it.birthD, it.birthM, it.birthY) }
    }
    Section(title) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(sel == "me", { sel = "me" }, label = { Text(if (s.userName.isBlank()) "Tôi" else s.userName) }) }
            items(withBirth, key = { it.id }) { mb ->
                FilterChip(sel == mb.id.toString(), { sel = mb.id.toString() }, label = { Text(mb.name) })
            }
            item { FilterChip(sel == "other", { sel = "other" }, label = { Text("Người khác") }) }
        }
        if (sel == "me" && me == null) {
            val ctx2 = LocalContext.current
            Text("Nhập ngày sinh của bạn (lưu vào hồ sơ):", color = MaterialTheme.colorScheme.onSurfaceVariant)
            GenderChips(s.male) { v -> SettingsRepository.get(ctx2).update { it.copy(male = v) } }
            DateFields("", "", "") { a, b, c ->
                val dd = a.toInt(); val mm = b.toInt(); val yy = c.toInt()
                SettingsRepository.get(ctx2).update { it.copy(birthD = dd, birthM = mm, birthY = yy, birthYear = LunarCalendar.solarToLunar(dd, mm, yy)[2]) }
            }
        }
        if (sel == "other") {
            OutlinedTextField(name, { name = it }, label = { Text("Họ tên (không bắt buộc)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            GenderChips(male) { male = it }
            DateFields(d, m, y) { a, b, c -> d = a; m = b; y = c }
            OutlinedTextField(hh, { hh = it.filter(Char::isDigit).take(2) }, label = { Text("Giờ sinh 0–23 (không bắt buộc)") },
                singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        }
        result?.let {
            Text("${it.name} · ${it.gioi()} · sinh ${it.day}/${it.month}/${it.year} (âm ${it.lunarDay}/${it.lunarMonth}/${it.lunarYear}) · ${it.canChi()} · ${it.napAm()}",
                style = MaterialTheme.typography.bodySmall, color = goodColor(), modifier = Modifier.padding(top = 4.dp))
        }
    }
    if (edit) FullProfileDialog { edit = false }
    return result
}

/** Dòng chấm tròn liệt kê. */
@Composable
fun Bullet(text: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row {
        Text("•  ", color = color)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
fun Para(text: String) = Text(text, style = MaterialTheme.typography.bodyMedium)

@Composable
fun Note(text: String) = Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
fun Stars(n: Int, max: Int = 5) {
    Text("★".repeat(n.coerceIn(0, max)) + "☆".repeat((max - n).coerceIn(0, max)), color = Color(0xFFF9A825), fontWeight = FontWeight.Bold)
}
