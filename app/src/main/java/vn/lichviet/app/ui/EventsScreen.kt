@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui

import vn.lichviet.ui.*

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.Countdown
import vn.lichviet.core.DayInfo
import vn.lichviet.core.Holidays
import vn.lichviet.data.EventRepository
import vn.lichviet.data.UserEvent
import vn.lichviet.widget.WidgetUpdater

/** Ngày lễ theo tháng + sự kiện cá nhân (sinh nhật, ngày giỗ âm lịch...). */
@Composable
fun EventsScreen(todayJd: Int, onOpenDay: (Int) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val repo = remember { EventRepository.get(ctx) }
    val mine by repo.events.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<UserEvent?>(null) }
    val today = remember(todayJd) { dayOf(todayJd) }
    val tetDays = remember(todayJd) { Countdown.daysToTet(today.solarDay, today.solarMonth, today.solarYear) }
    val tetJd = todayJd + tetDays
    val tet = remember(tetJd) { dayOf(tetJd) }

    var year by rememberSaveable { mutableIntStateOf(today.solarYear) }
    var month by rememberSaveable { mutableIntStateOf(today.solarMonth) }
    val yearList = remember(year) { Holidays.listYear(year) }
    val monthItems = remember(year, month, yearList) { yearList.filter { (it[2] as Int) == month } }

    Box(modifier) {
        LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
            item {
                // Thẻ đếm ngược Tết
                ShadowCard(elevation = 8.dp, container = MaterialTheme.colorScheme.primary) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HolidayIcon("Tết Nguyên đán", 56.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("TẾT NGUYÊN ĐÁN ${tet.yearCanChi.uppercase()}", style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(solarText(tet), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(if (tetDays == 0) "Hôm nay" else "$tetDays", fontSize = 34.sp, fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary, lineHeight = 36.sp)
                            if (tetDays > 0) Text("ngày nữa", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = tab == 0, onClick = { tab = 0 }, label = { Text("Ngày lễ theo tháng", maxLines = 1) })
                    FilterChip(selected = tab == 1, onClick = { tab = 1 }, label = { Text("Của tôi (${mine.size})", maxLines = 1) })
                }
            }
            if (tab == 0) {
                item {
                    // Chọn năm + tháng
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { year-- }) { Text("◀") }
                        Text("Năm $year", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        TextButton(onClick = { year++ }) { Text("▶") }
                    }
                    val listState = rememberLazyListState()
                    LaunchedEffect(Unit) { listState.scrollToItem((month - 3).coerceAtLeast(0)) }
                    LazyRow(state = listState, contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items((1..12).toList()) { m ->
                            val count = yearList.count { (it[2] as Int) == m }
                            FilterChip(
                                selected = m == month, onClick = { month = m },
                                label = { Text("Tháng $m · $count", maxLines = 1, softWrap = false) },
                            )
                        }
                    }
                }
                item { SectionTitle("Tháng $month / $year", modifier = Modifier.padding(start = 20.dp, top = 10.dp)) }
                if (monthItems.isEmpty()) item { ShadowCard { Text("Không có ngày lễ.", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                items(monthItems.size) { i ->
                    val row = monthItems[i]
                    val jd = row[0] as Int
                    val item = row[4] as Holidays.Item
                    val d = dayOf(jd)
                    val left = jd - todayJd
                    val trailing = when {
                        left == 0 -> "Hôm nay"
                        left > 0 -> "còn $left ngày"
                        else -> null
                    }
                    ShadowCard(Modifier.clickable { onOpenDay(jd) }, elevation = 2.dp) {
                        HolidayRow(
                            item.name,
                            "${DayInfo.THU_NGAN[d.weekday]} ${d.solarDay}/${d.solarMonth} · ${d.lunarText()} âm" + if (item.major) " · nghỉ lễ" else "",
                            trailing, item.major,
                        )
                    }
                }
            } else {
                val upcoming = repo.upcoming(today.solarDay, today.solarMonth, today.solarYear)
                val past = mine.filter { e -> upcoming.none { it.first.id == e.id } }
                if (mine.isEmpty()) item {
                    ShadowCard { Text("Chưa có sự kiện. Bấm + để thêm sinh nhật, ngày giỗ, kỷ niệm…") }
                }
                itemsIndexed(upcoming, key = { _, p -> p.first.id }) { _, (e, jd) ->
                    EventCard(e, "${solarText(dayOf(jd))} · còn ${jd - todayJd} ngày", onClick = { editing = e }, onDelete = {
                        repo.delete(e.id); WidgetUpdater.updateAll(ctx)
                    })
                }
                itemsIndexed(past, key = { _, e -> e.id }) { _, e ->
                    EventCard(e, "Đã qua", onClick = { editing = e }, onDelete = { repo.delete(e.id); WidgetUpdater.updateAll(ctx) })
                }
            }
        }
        if (tab == 1) {
            FloatingActionButton(
                onClick = { editing = UserEvent(repo.newId(), "", today.lunarDay, today.lunarMonth) },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) { Icon(Icons.Filled.Add, contentDescription = "Thêm sự kiện") }
        }
    }

    editing?.let { e ->
        EventDialog(e, onDismiss = { editing = null }, onSave = {
            repo.upsert(it); editing = null; WidgetUpdater.updateAll(ctx)
        })
    }
}

@Composable
private fun EventCard(e: UserEvent, sub: String, onClick: () -> Unit, onDelete: () -> Unit) {
    ShadowCard(Modifier.clickable(onClick = onClick), elevation = 3.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HolidayIcon(if (e.title.lowercase().contains("giỗ")) "Vu Lan" else if (e.title.lowercase().contains("sinh nhật")) "Thiếu nhi" else e.title, 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${e.day}/${e.month}" + (if (e.year != 0) "/${e.year}" else "") + (if (e.isLunar) " âm lịch" else " dương lịch") + " · $sub",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Xóa") }
        }
    }
}

@Composable
private fun EventDialog(initial: UserEvent, onDismiss: () -> Unit, onSave: (UserEvent) -> Unit) {
    var title by remember { mutableStateOf(initial.title) }
    var day by remember { mutableStateOf(initial.day.toString()) }
    var month by remember { mutableStateOf(initial.month.toString()) }
    var year by remember { mutableStateOf(if (initial.year == 0) "" else initial.year.toString()) }
    var lunar by remember { mutableStateOf(initial.isLunar) }
    var remind by remember { mutableStateOf(initial.remindDaysBefore.toString()) }
    var note by remember { mutableStateOf(initial.note) }

    val d = day.toIntOrNull()
    val m = month.toIntOrNull()
    val y = if (year.isBlank()) 0 else year.toIntOrNull()
    val valid = title.isNotBlank() && d != null && d in 1..(if (lunar) 30 else 31) && m != null && m in 1..12 && y != null

    val num = KeyboardOptions(keyboardType = KeyboardType.Number)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.title.isEmpty()) "Thêm sự kiện" else "Sửa sự kiện") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Tên (vd. Giỗ ông nội)") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(day, { day = it }, Modifier.weight(1f), label = { Text("Ngày", maxLines = 1) }, keyboardOptions = num, singleLine = true)
                    OutlinedTextField(month, { month = it }, Modifier.weight(1f), label = { Text("Tháng", maxLines = 1) }, keyboardOptions = num, singleLine = true)
                    OutlinedTextField(year, { year = it }, Modifier.weight(1.4f), label = { Text("Năm", maxLines = 1) }, placeholder = { Text("Hằng năm", maxLines = 1) }, keyboardOptions = num, singleLine = true)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(lunar, { lunar = it })
                    Text(if (lunar) "  Theo âm lịch" else "  Theo dương lịch")
                }
                OutlinedTextField(remind, { remind = it }, label = { Text("Nhắc trước (ngày)") }, keyboardOptions = num, singleLine = true)
                OutlinedTextField(note, { note = it }, label = { Text("Ghi chú") })
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = {
                onSave(
                    initial.copy(
                        title = title.trim(), day = d!!, month = m!!, year = y!!, isLunar = lunar,
                        remindDaysBefore = (remind.toIntOrNull() ?: 1).coerceIn(0, 30), note = note.trim(),
                    ),
                )
            }) { Text("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}

@Suppress("unused") private val keepClip = Modifier.clip(RoundedCornerShape(0.dp))
