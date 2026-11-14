@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import vn.lichviet.app.ui.theme.LocalLich
import vn.lichviet.app.ui.theme.Themes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.lichviet.core.Activity
import vn.lichviet.core.DayInfo
import vn.lichviet.core.GoodDayFinder
import vn.lichviet.core.LunarCalendar
import vn.lichviet.data.SettingsRepository
import vn.lichviet.data.Today
import vn.lichviet.reminder.ReminderScheduler
import vn.lichviet.widget.WidgetUpdater

/** Đổi ngày âm/dương, tìm ngày tốt theo việc, cài đặt. */
@Composable
fun ToolsScreen(todayJd: Int, onOpenDay: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 16.dp)) {
        ThemeBadge()
        ThemeSection()
        ConverterSection(todayJd, onOpenDay)
        FinderSection(todayJd, onOpenDay)
        SettingsSection()
    }
}

@Composable
private fun ConverterSection(todayJd: Int, onOpenDay: (Int) -> Unit) {
    val t = remember { dayOf(todayJd) }
    var sd by remember { mutableStateOf(t.solarDay.toString()) }
    var sm by remember { mutableStateOf(t.solarMonth.toString()) }
    var sy by remember { mutableStateOf(t.solarYear.toString()) }
    var ld by remember { mutableStateOf(t.lunarDay.toString()) }
    var lm by remember { mutableStateOf(t.lunarMonth.toString()) }
    var ly by remember { mutableStateOf(t.lunarYear.toString()) }
    var leap by remember { mutableStateOf(false) }
    val num = KeyboardOptions(keyboardType = KeyboardType.Number)

    Section("Dương lịch → Âm lịch") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(sd, { sd = it }, Modifier.weight(1f), label = { Text("Ngày") }, keyboardOptions = num, singleLine = true)
            OutlinedTextField(sm, { sm = it }, Modifier.weight(1f), label = { Text("Tháng") }, keyboardOptions = num, singleLine = true)
            OutlinedTextField(sy, { sy = it }, Modifier.weight(1.4f), label = { Text("Năm") }, keyboardOptions = num, singleLine = true)
        }
        val d = sd.toIntOrNull(); val m = sm.toIntOrNull(); val y = sy.toIntOrNull()
        if (d != null && m != null && y != null && m in 1..12 && y in 1800..2199 && d in 1..vn.lichviet.core.Holidays.daysInMonth(m, y)) {
            val info = DayInfo.of(d, m, y)
            Text(info.lunarFull(), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onOpenDay(info.jd) })
            Text("${DayInfo.THU[info.weekday]} · Ngày ${info.dayCanChi} · ${info.verdict}", style = MaterialTheme.typography.bodySmall)
        } else Text("Ngày không hợp lệ (hỗ trợ năm 1800–2199)", color = MaterialTheme.colorScheme.error)
    }
    Section("Âm lịch → Dương lịch") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(ld, { ld = it }, Modifier.weight(1f), label = { Text("Ngày") }, keyboardOptions = num, singleLine = true)
            OutlinedTextField(lm, { lm = it }, Modifier.weight(1f), label = { Text("Tháng") }, keyboardOptions = num, singleLine = true)
            OutlinedTextField(ly, { ly = it }, Modifier.weight(1.4f), label = { Text("Năm") }, keyboardOptions = num, singleLine = true)
        }
        Row(verticalAlignment = Alignment.CenterVertically) { Switch(leap, { leap = it }); Text("  Tháng nhuận") }
        val d = ld.toIntOrNull(); val m = lm.toIntOrNull(); val y = ly.toIntOrNull()
        val res = if (d != null && m != null && y != null && d in 1..30 && m in 1..12 && y in 1800..2199) {
            LunarCalendar.lunarToSolar(d, m, y, if (leap) 1 else 0)
        } else null
        if (res == null || res[2] == 0) {
            Text(if (leap) "Năm này không có tháng $lm nhuận" else "Ngày không hợp lệ", color = MaterialTheme.colorScheme.error)
        } else {
            val info = DayInfo.of(res[0], res[1], res[2])
            // tháng thiếu: ngày 30 không tồn tại sẽ rơi sang mùng 1 tháng sau
            if (info.lunarDay != d) Text("Tháng này chỉ có 29 ngày", color = MaterialTheme.colorScheme.error)
            else {
                Text(solarText(info), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onOpenDay(info.jd) })
                Text("Năm ${info.yearCanChi} · Ngày ${info.dayCanChi}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun FinderSection(todayJd: Int, onOpenDay: (Int) -> Unit) {
    var activity by remember { mutableStateOf(Activity.CUOI_HOI) }
    var days by remember { mutableStateOf(90) }
    val t = remember { dayOf(todayJd) }
    val result = remember(activity, days) { GoodDayFinder.find(activity, t.solarDay, t.solarMonth, t.solarYear, days, 30) }
    Section("Tìm ngày tốt theo việc") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(Activity.entries) { a -> FilterChip(selected = a == activity, onClick = { activity = a }, label = { Text(a.label, maxLines = 1, softWrap = false) }) }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(listOf(30, 90, 180, 365)) { n ->
                FilterChip(selected = n == days, onClick = { days = n }, label = { Text("$n ngày", maxLines = 1, softWrap = false) })
            }
        }
        if (result.isEmpty()) Text("Không tìm thấy ngày phù hợp trong khoảng này.")
        result.forEach { d ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                    .clickable { onOpenDay(d.jd) }.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("${DayInfo.THU[d.weekday]}, ${d.solarDay}/${d.solarMonth}/${d.solarYear}", fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("${d.lunarText()} âm · Trực ${d.truc} · ${d.almanac().khongMinh}", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(d.starsText(), color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false)
            }
        }
        Text("Tiêu chí: Trực hợp việc; tránh Tam Nương, Nguyệt Kỵ, Dương Công, Thụ Tử, Sát Chủ và sao xấu kỵ việc này; điểm ngày từ 3/5.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ThemeSection() {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val s by repo.settings.collectAsState()
    val current = LocalLich.current
    Section("Giao diện") {
        Themes.GROUP_ORDER.forEach { group ->
            Text(group.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Themes.NAMES.indices.filter { Themes.GROUPS[it] == group }.forEach { id ->
                    val (bg, accent) = Themes.SWATCH[id]
                    val selected = current?.id == id
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                            .border(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                            .clickable { repo.update { it.copy(themeId = id, autoSeason = false) } }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(Modifier.size(36.dp).shadow(2.dp, CircleShape).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
                            Box(Modifier.size(14.dp).clip(CircleShape).background(accent))
                        }
                        Text(Themes.NAMES[id], style = MaterialTheme.typography.labelMedium, maxLines = 2,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                    }
                }
                // giữ hàng 2 ô có bề rộng như hàng 4 ô
                repeat(4 - Themes.GROUPS.count { it == group }) { Spacer(Modifier.weight(1f)) }
            }
        }
        SwitchRow("Tự đổi theo mùa (theo tiết khí)", s.autoSeason) { v -> repo.update { it.copy(autoSeason = v) } }
        Text("Ngũ Hành đổi màu theo can ngày · Thời Thần theo canh giờ · Hoàng Đạo theo ngày hoàng/hắc đạo · Con Giáp theo năm.",
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Section("Tranh nền tờ lịch") {
        val month = remember { Today.ymd()[1] }
        ArtSetPicker(month, s.artSet) { id -> repo.update { it.copy(artSet = id) } }
    }
}

@Composable
private fun SettingsSection() {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val s by repo.settings.collectAsState()
    var editProfile by remember { mutableStateOf(false) }
    Section("Cài đặt") {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Thông tin của bạn")
                Text(
                    if (s.birthYear == 0) "Chưa nhập năm sinh" else "${s.userName.ifBlank { "Bạn" }} · tuổi ${vn.lichviet.core.TuoiHop.tuoiName(s.birthYear)} (${s.birthYear})",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = { editProfile = true }) { Text("Sửa", maxLines = 1) }
        }
        if (editProfile) {
            ProfileDialog(s, onDismiss = { editProfile = false }) { name, year ->
                repo.update { it.copy(userName = name, birthYear = year) }
                editProfile = false
            }
        }
        SwitchRow("Tuần bắt đầu Thứ Hai", s.mondayFirst) { v -> repo.update { it.copy(mondayFirst = v) }; WidgetUpdater.updateAll(ctx) }
        SwitchRow("Thông báo lịch mỗi sáng", s.morningNotify) { v -> repo.update { it.copy(morningNotify = v) } }
        SwitchRow("Nhắc trước mùng 1 và rằm", s.remindSocVong) { v -> repo.update { it.copy(remindSocVong = v) } }
        SwitchRow("Nhắc trước ngày lễ lớn", s.remindHolidays) { v -> repo.update { it.copy(remindHolidays = v) } }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Giờ thông báo", Modifier.weight(1f))
            TextButton(onClick = { setHour(ctx, repo, (s.notifyHour + 23) % 24) }) { Text("−") }
            Text("%02d:00".format(s.notifyHour), fontWeight = FontWeight.Bold)
            TextButton(onClick = { setHour(ctx, repo, (s.notifyHour + 1) % 24) }) { Text("+") }
        }
        Button(onClick = { WidgetUpdater.updateAll(ctx) }) { Text("Làm mới widget") }
    }
}

private fun setHour(ctx: android.content.Context, repo: SettingsRepository, h: Int) {
    repo.update { it.copy(notifyHour = h) }
    ReminderScheduler.schedule(ctx, h)
}

@Composable
private fun SwitchRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f).padding(end = 8.dp))
        Switch(value, onChange)
    }
}
