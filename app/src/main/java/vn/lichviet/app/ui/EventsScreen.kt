@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.lichviet.core.Countdown
import vn.lichviet.core.Holidays
import vn.lichviet.data.EventRepository
import vn.lichviet.data.UserEvent
import vn.lichviet.widget.WidgetUpdater

/** Ngày lễ sắp tới + sự kiện cá nhân (sinh nhật, ngày giỗ âm lịch...). */
@Composable
fun EventsScreen(todayJd: Int, onOpenDay: (Int) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val repo = remember { EventRepository.get(ctx) }
    val mine by repo.events.collectAsState()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<UserEvent?>(null) }
    val today = remember(todayJd) { dayOf(todayJd) }
    val tetDays = remember(todayJd) { Countdown.daysToTet(today.solarDay, today.solarMonth, today.solarYear) }

    val holidays = remember(todayJd) {
        (Holidays.listYear(today.solarYear) + Holidays.listYear(today.solarYear + 1))
            .filter { (it[0] as Int) >= todayJd }
            .take(60)
    }

    Box(modifier) {
        Column {
            Text(
                if (tetDays == 0) "Chúc mừng năm mới!" else "Còn $tetDays ngày đến Tết Nguyên đán",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
            )
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = tab == 0, onClick = { tab = 0 }, label = { Text("Ngày lễ") })
                FilterChip(selected = tab == 1, onClick = { tab = 1 }, label = { Text("Của tôi (${mine.size})") })
            }
            if (tab == 0) {
                LazyColumn {
                    items(holidays) { row ->
                        val jd = row[0] as Int
                        val item = row[4] as Holidays.Item
                        val d = dayOf(jd)
                        ListItem(
                            headlineContent = { Text(item.name, fontWeight = if (item.major) FontWeight.Bold else FontWeight.Normal) },
                            supportingContent = { Text("${solarText(d)} · ${d.lunarText()} âm" + if (jd == todayJd) " · Hôm nay" else " · còn ${jd - todayJd} ngày") },
                            modifier = Modifier.clickable { onOpenDay(jd) },
                        )
                    }
                }
            } else {
                val upcoming = repo.upcoming(today.solarDay, today.solarMonth, today.solarYear)
                val past = mine.filter { e -> upcoming.none { it.first.id == e.id } }
                if (mine.isEmpty()) Text("Chưa có sự kiện. Bấm + để thêm sinh nhật, ngày giỗ…", Modifier.padding(16.dp))
                LazyColumn {
                    items(upcoming, key = { it.first.id }) { (e, jd) ->
                        EventRow(e, "${solarText(dayOf(jd))} · còn ${jd - todayJd} ngày", onClick = { editing = e }, onDelete = {
                            repo.delete(e.id); WidgetUpdater.updateAll(ctx)
                        })
                    }
                    items(past, key = { it.id }) { e ->
                        EventRow(e, "Đã qua", onClick = { editing = e }, onDelete = { repo.delete(e.id); WidgetUpdater.updateAll(ctx) })
                    }
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
private fun EventRow(e: UserEvent, sub: String, onClick: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(e.title, fontWeight = FontWeight.SemiBold) },
        supportingContent = {
            Text("${e.day}/${e.month}" + (if (e.year != 0) "/${e.year}" else "") + (if (e.isLunar) " âm lịch" else " dương lịch") + " · $sub")
        },
        trailingContent = { IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Xóa") } },
        modifier = Modifier.clickable(onClick = onClick),
    )
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
                    OutlinedTextField(day, { day = it }, Modifier.weight(1f), label = { Text("Ngày") }, keyboardOptions = num, singleLine = true)
                    OutlinedTextField(month, { month = it }, Modifier.weight(1f), label = { Text("Tháng") }, keyboardOptions = num, singleLine = true)
                    OutlinedTextField(year, { year = it }, Modifier.weight(1.4f), label = { Text("Năm") }, placeholder = { Text("Hằng năm") }, keyboardOptions = num, singleLine = true)
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

