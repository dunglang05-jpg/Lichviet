package vn.lichviet.tools

import vn.lichviet.ui.*
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

/** Tab Tiện ích: hồ sơ, lưới công cụ, đổi ngày âm/dương, tìm ngày tốt. Cài đặt nằm ở module :feature:settings. */
@Composable
fun ToolsScreen(todayJd: Int, onOpenDay: (Int) -> Unit, onSettings: () -> Unit = {}, modifier: Modifier = Modifier) {
    var openName by rememberSaveable { mutableStateOf<String?>(null) }
    val open = openName?.let { n -> Tool.entries.firstOrNull { it.name == n } }
    if (open != null) {
        ToolPage(open, todayJd, onOpenDay, onBack = { openName = null }, modifier = modifier)
        return
    }
    Column(modifier) {
      val spec = vn.lichviet.ui.theme.LocalLich.current
      AppHeader("Tiện ích", spec?.let { "${it.name} · ${it.variant}" }, onSettings = onSettings)
      Column(Modifier.verticalScroll(rememberScrollState()).padding(top = 8.dp, bottom = 16.dp)) {
        ProfileCard()
        ToolGrid { openName = it.name }
        ConverterSection(todayJd, onOpenDay)
        FinderSection(todayJd, onOpenDay)
      }
    }
}

@Composable
fun ConverterSection(todayJd: Int, onOpenDay: (Int) -> Unit) {
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
        DateFields(sd, sm, sy, "Ngày dương lịch") { a, b, c -> sd = a; sm = b; sy = c }
        val d = sd.toIntOrNull(); val m = sm.toIntOrNull(); val y = sy.toIntOrNull()
        if (d != null && m != null && y != null && m in 1..12 && y in 1800..2199 && d in 1..vn.lichviet.core.Holidays.daysInMonth(m, y)) {
            val info = DayInfo.of(d, m, y)
            Text(info.lunarFull(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.clickable { onOpenDay(info.jd) })
            Text("${DayInfo.THU[info.weekday]} · Ngày ${info.dayCanChi} · ${info.verdict}", style = MaterialTheme.typography.bodySmall)
        }
    }
    Section("Âm lịch → Dương lịch") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberStepper("Ngày", ld.toIntOrNull() ?: 1, 1..30, Modifier.weight(1f)) { ld = it.toString() }
            NumberStepper("Tháng", lm.toIntOrNull() ?: 1, 1..12, Modifier.weight(1f)) { lm = it.toString() }
            NumberStepper("Năm", ly.toIntOrNull() ?: 2026, 1800..2199, Modifier.weight(1.3f)) { ly = it.toString() }
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

