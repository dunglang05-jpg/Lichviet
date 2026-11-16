@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui

import vn.lichviet.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.DayInfo
import vn.lichviet.core.Holidays
import vn.lichviet.core.MonthGrid
import vn.lichviet.core.MoonPhase
import vn.lichviet.data.SettingsRepository

/** Lịch tháng có âm lịch, chấm ngày tốt (xanh) và ngày lễ (cam). */
@Composable
fun MonthScreen(selectedJd: Int, todayJd: Int, onPick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val sel = remember(selectedJd) { dayOf(selectedJd) }
    var month by rememberSaveable { mutableIntStateOf(sel.solarMonth) }
    var year by rememberSaveable { mutableIntStateOf(sel.solarYear) }
    val ctx = LocalContext.current
    val settings by SettingsRepository.get(ctx).settings.collectAsState()
    val grid = remember(month, year, settings.mondayFirst) { MonthGrid(month, year, settings.mondayFirst) }
    val heads = if (settings.mondayFirst) listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN") else listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")

    fun shift(delta: Int) {
        var m = month + delta
        var y = year
        if (m < 1) { m = 12; y-- }
        if (m > 12) { m = 1; y++ }
        month = m; year = y
    }

    val monthHolidays = remember(month, year) { Holidays.listYear(year).filter { (it[2] as Int) == month } }
    Column(modifier.verticalScroll(rememberScrollState()).padding(vertical = 12.dp)) {
        ThemeBadge(Modifier.padding(bottom = 4.dp))
        // Tờ lịch tháng: tranh dân gian + tiêu đề tháng, như lịch treo tường
        ShadowCard(elevation = 8.dp) {
            MonthPainting(month, settings.artSet) {
                // tên tháng đè lên tranh như tờ lịch treo tường
                Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                    Text("Tháng $month", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("$year · ${grid.lunarRange()}", color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyMedium)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { shift(-1) }) { Text("◀ Trước") }
                Text("Tranh: " + MonthArt.caption(ctx, MonthArt.set(settings.artSet), month), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f))
                TextButton(onClick = { shift(1) }) { Text("Sau ▶") }
            }
        }
        ShadowCard(elevation = 6.dp) {
        Row(Modifier.fillMaxWidth()) {
            heads.forEach { Text(it, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
        }
        for (r in 0 until 6) {
            Row(Modifier.fillMaxWidth()) {
                for (c in 0 until 7) {
                    val i = r * 7 + c
                    val d = grid.cells[i]
                    val inMonth = grid.inMonth[i]
                    val isToday = d.jd == todayJd
                    val isSel = d.jd == selectedJd
                    val bg = when {
                        isToday -> MaterialTheme.colorScheme.primary
                        isSel -> MaterialTheme.colorScheme.primaryContainer
                        else -> Color.Transparent
                    }
                    val fg = when {
                        isToday -> MaterialTheme.colorScheme.onPrimary
                        !inMonth -> MaterialTheme.colorScheme.outline
                        d.weekday == 0 || d.holidays.any { it.major } -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    Column(
                        Modifier.weight(1f).height(60.dp).padding(2.dp).clip(RoundedCornerShape(10.dp)).background(bg)
                            .clickable { onPick(d.jd) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("${d.solarDay}", color = fg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(if (d.lunarDay == 1) "${d.lunarDay}/${d.lunarMonth}" else "${d.lunarDay}", color = fg, fontSize = 10.sp)
                        val dot = when {
                            !inMonth -> Color.Transparent
                            d.holidays.isNotEmpty() -> MaterialTheme.colorScheme.primary
                            d.score >= 4 -> goodColor()
                            else -> Color.Transparent
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            val mp = remember(d.jd) { MoonPhase.of(d.solarDay, d.solarMonth, d.solarYear) }
                            MoonDisc(mp.fraction, Modifier.size(9.dp), dark = fg.copy(alpha = 0.22f), ring = fg)
                            Text("●", color = dot, fontSize = 7.sp)
                        }
                    }
                }
            }
        }
        Text("● ngày tốt   ● ngày lễ   · chạm một ngày để xem chi tiết", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        }
        ShadowCard {
            SectionTitle("Ngày lễ trong tháng $month")
            if (monthHolidays.isEmpty()) Text("Không có ngày lễ.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            monthHolidays.forEach { row ->
                val jd = row[0] as Int
                val item = row[4] as Holidays.Item
                val d = dayOf(jd)
                val left = jd - todayJd
                HolidayRow(
                    item.name, "${DayInfo.THU_NGAN[d.weekday]} ${d.solarDay}/${d.solarMonth} · ${d.lunarText()} âm",
                    if (left == 0) "Hôm nay" else if (left > 0) "còn $left ngày" else null, item.major,
                    Modifier.clickable { onPick(jd) },
                )
            }
        }
    }
}
