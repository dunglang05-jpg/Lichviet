@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.tools

import vn.lichviet.ui.*

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
fun DateFields(d: String, m: String, y: String, onChange: (String, String, String) -> Unit) {
    val kb = KeyboardOptions(keyboardType = KeyboardType.Number)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(d, { onChange(it.filter(Char::isDigit).take(2), m, y) }, Modifier.weight(1f), label = { Text("Ngày") }, singleLine = true, keyboardOptions = kb)
        OutlinedTextField(m, { onChange(d, it.filter(Char::isDigit).take(2), y) }, Modifier.weight(1f), label = { Text("Tháng") }, singleLine = true, keyboardOptions = kb)
        OutlinedTextField(y, { onChange(d, m, it.filter(Char::isDigit).take(4)) }, Modifier.weight(1.4f), label = { Text("Năm") }, singleLine = true, keyboardOptions = kb)
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
                Text("Ngày sinh dương lịch", style = MaterialTheme.typography.labelMedium)
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
            Text("Bạn chưa nhập ngày sinh.", color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { edit = true }) { Text("Nhập hồ sơ") }
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
