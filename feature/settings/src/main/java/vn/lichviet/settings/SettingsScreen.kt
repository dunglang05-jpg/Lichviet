package vn.lichviet.settings

import androidx.compose.foundation.layout.width

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.fillMaxSize

import vn.lichviet.ui.*
import vn.lichviet.ui.theme.MotifBand
import vn.lichviet.ui.theme.ThemeSpec
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.saveable.rememberSaveable
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
import vn.lichviet.ui.theme.LocalLich
import vn.lichviet.ui.theme.Themes
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

/** Màn Cài đặt (module riêng): giao diện, tranh nền tờ lịch, thông tin cá nhân, thông báo, widget. */
@Composable
fun SettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    androidx.activity.compose.BackHandler(onBack = onBack)
    Column(modifier.fillMaxSize()) {
        AppHeader("Cài đặt", "Giao diện · thông báo · widget", onBack = onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(top = 8.dp, bottom = 24.dp)) {
            ThemeSection()
            SettingsSection()
            Text("Lịch Việt · âm lịch theo thuật toán Hồ Ngọc Đức (múi giờ UTC+7)", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp))
        }
    }
}

/** Thẻ xem trước một giao diện: đầu trang màu chính, dải hoa văn hai màu, hai chấm màu đối ứng. */
@Composable
private fun ThemeCard(id: Int, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val (bg, a1, a2) = Themes.SWATCH[id]
    val dark = bg.red * .3f + bg.green * .59f + bg.blue * .11f < .5f
    val preview = ThemeSpec(id, Themes.GROUPS[id], Themes.NAMES[id], "", dark, bg, bg, Color.Unspecified, Color.Unspecified,
        a1, Color.White, a2, Color.White, Color.Unspecified, Color.Unspecified, Themes.PATTERNS[id], a1, .2f)
    Column(
        modifier.clip(RoundedCornerShape(16.dp))
            .border(if (selected) 3.dp else 1.dp, if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.fillMaxWidth().height(26.dp).background(a1))
        Column(Modifier.fillMaxWidth().background(bg).padding(horizontal = 8.dp, vertical = 6.dp)) {
            MotifBand(preview, height = 14.dp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Box(Modifier.size(14.dp).clip(CircleShape).background(a1))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(14.dp).clip(CircleShape).background(a2))
                Spacer(Modifier.width(8.dp))
                Text(Themes.NAMES[id], color = if (dark) Color(0xFFF5EFE6) else Color(0xFF1E1A16),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ThemeSection() {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val s by repo.settings.collectAsState()
    val current = LocalLich.current
    Section("Giao diện") {
        Text("Mỗi giao diện có một cặp màu đối ứng (màu chính và màu tương phản cùng độ đậm) và một bộ hoa văn riêng, dùng xuyên suốt ở đầu trang, tiêu đề, góc thẻ và thanh điều hướng.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Themes.GROUP_ORDER.forEach { group ->
            Text(group.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            Themes.NAMES.indices.filter { Themes.GROUPS[it] == group }.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { id ->
                        ThemeCard(id, selected = current?.id == id && !s.autoSeason, Modifier.weight(1f)) {
                            repo.update { it.copy(themeId = id, autoSeason = false) }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        SwitchRow("Tự đổi theo mùa (theo tiết khí)", s.autoSeason) { v -> repo.update { it.copy(autoSeason = v) } }
        Text("Ngũ Hành đổi theo can ngày (màu đối ứng là hành được sinh ra) · Thời Thần theo canh giờ · Hoàng Đạo theo ngày hoàng/hắc đạo · Con Giáp theo năm.",
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
    Section("Thông tin & thông báo") {
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
