@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui.tools

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.lichviet.app.ui.Section
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.Person
import vn.lichviet.data.EventRepository
import vn.lichviet.data.FamilyRepository
import vn.lichviet.data.Member
import vn.lichviet.data.Today

private val RELATIONS = listOf("Ông nội", "Bà nội", "Ông ngoại", "Bà ngoại", "Bố", "Mẹ", "Vợ", "Chồng", "Con", "Anh", "Chị", "Em", "Cụ", "Khác")

private fun Member.subtitle(): String {
    val parts = mutableListOf<String>()
    if (relation.isNotBlank()) parts += relation
    parts += if (male) "Nam" else "Nữ"
    if (hasBirth) parts += Person(name, male, birthD, birthM, birthY).let { "sinh ${birthD}/${birthM}/${birthY} (${it.canChi()})" }
    if (hasGio) parts += "giỗ ${deathLunarD}/${deathLunarM} âm"
    return parts.joinToString(" · ")
}

@Composable
fun FamilyScreen() {
    val ctx = LocalContext.current
    val repo = remember { FamilyRepository.get(ctx) }
    val list by repo.members.collectAsState()
    var editing by remember { mutableStateOf<Member?>(null) }
    Section("Thành viên (${list.size})") {
        if (list.isEmpty()) Para("Thêm ông bà, cha mẹ, anh chị em, con cái để xem tuổi, nhắc ngày giỗ và dựng gia phả.")
        list.sortedBy { it.birthY.takeIf { y -> y > 0 } ?: 9999 }.forEach { m ->
            Column(Modifier.fillMaxWidth().clickable { editing = m }.padding(vertical = 6.dp)) {
                Text(m.name, fontWeight = FontWeight.Bold)
                Note(m.subtitle())
            }
        }
        Button(onClick = { editing = Member(repo.newId(), "") }, modifier = Modifier.fillMaxWidth()) { Text("Thêm thành viên") }
    }
    editing?.let { m -> MemberDialog(m, list, onDismiss = { editing = null }) }
}

@Composable
private fun MemberDialog(m: Member, all: List<Member>, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val repo = remember { FamilyRepository.get(ctx) }
    val isNew = all.none { it.id == m.id }
    var name by rememberSaveable { mutableStateOf(m.name) }
    var rel by rememberSaveable { mutableStateOf(m.relation) }
    var male by rememberSaveable { mutableStateOf(m.male) }
    var d by rememberSaveable { mutableStateOf(if (m.birthD > 0) "${m.birthD}" else "") }
    var mo by rememberSaveable { mutableStateOf(if (m.birthM > 0) "${m.birthM}" else "") }
    var y by rememberSaveable { mutableStateOf(if (m.birthY > 0) "${m.birthY}" else "") }
    var dead by rememberSaveable { mutableStateOf(m.deceased) }
    var gd by rememberSaveable { mutableStateOf(if (m.deathLunarD > 0) "${m.deathLunarD}" else "") }
    var gm by rememberSaveable { mutableStateOf(if (m.deathLunarM > 0) "${m.deathLunarM}" else "") }
    var parent by rememberSaveable { mutableStateOf(m.parentId) }
    val kb = KeyboardOptions(keyboardType = KeyboardType.Number)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Thêm thành viên" else "Sửa thông tin") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Họ tên") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(RELATIONS) { r -> FilterChip(rel == r, { rel = r }, label = { Text(r) }) }
                }
                GenderChips(male) { male = it }
                Text("Ngày sinh dương lịch (nếu biết)", style = MaterialTheme.typography.labelMedium)
                DateFields(d, mo, y) { a, b, c -> d = a; mo = b; y = c }
                val others = all.filter { it.id != m.id }
                if (others.isNotEmpty()) {
                    Text("Con của (cho gia phả)", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item { FilterChip(parent == 0L, { parent = 0L }, label = { Text("Không") }) }
                        items(others, key = { it.id }) { o -> FilterChip(parent == o.id, { parent = o.id }, label = { Text(o.name) }) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(dead, { dead = it }); Text("  Đã mất")
                }
                if (dead) {
                    Text("Ngày mất âm lịch (để nhắc giỗ)", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(gd, { gd = it.filter(Char::isDigit).take(2) }, Modifier.weight(1f), label = { Text("Ngày") }, singleLine = true, keyboardOptions = kb)
                        OutlinedTextField(gm, { gm = it.filter(Char::isDigit).take(2) }, Modifier.weight(1f), label = { Text("Tháng") }, singleLine = true, keyboardOptions = kb)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = {
                val bd = d.toIntOrNull() ?: 0; val bm = mo.toIntOrNull() ?: 0; val by = y.toIntOrNull() ?: 0
                val okBirth = validDate(bd, bm, by)
                repo.upsert(m.copy(
                    name = name.trim(), relation = rel, male = male,
                    birthD = if (okBirth) bd else 0, birthM = if (okBirth) bm else 0, birthY = if (okBirth) by else 0,
                    deceased = dead,
                    deathLunarD = if (dead) (gd.toIntOrNull() ?: 0).coerceIn(0, 30) else 0,
                    deathLunarM = if (dead) (gm.toIntOrNull() ?: 0).coerceIn(0, 12) else 0,
                    parentId = parent,
                ))
                onDismiss()
            }) { Text("Lưu") }
        },
        dismissButton = {
            Row {
                if (!isNew) TextButton(onClick = { repo.delete(m.id); onDismiss() }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
                TextButton(onClick = onDismiss) { Text("Hủy") }
            }
        },
    )
}

@Composable
fun NhacGioScreen() {
    val ctx = LocalContext.current
    val members by FamilyRepository.get(ctx).members.collectAsState()
    val events by EventRepository.get(ctx).events.collectAsState()
    val t = Today.ymd()
    val today = LunarCalendar.jdFromDate(t[0], t[1], t[2])
    val gio = members.filter { it.hasGio }.mapNotNull { m ->
        events.firstOrNull { it.id == FamilyRepository.GIO_ID_BASE + m.id }?.let { e ->
            m to EventRepository.nextOccurrence(e, t[0], t[1], t[2])
        }
    }.sortedBy { it.second }
    Section("Ngày giỗ sắp tới") {
        if (gio.isEmpty()) Para("Chưa có ngày giỗ. Vào Thành Viên Gia Đình, thêm người đã mất và nhập ngày mất âm lịch; ứng dụng sẽ nhắc trước 3 ngày.")
        gio.forEach { (m, jd) ->
            val s = LunarCalendar.jdToDate(jd)
            val left = jd - today
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Giỗ ${m.relation} ${m.name}".replace("  ", " "), fontWeight = FontWeight.Bold)
                    Note("${m.deathLunarD}/${m.deathLunarM} âm lịch · ${s[0]}/${s[1]}/${s[2]} dương lịch")
                }
                Text(if (left == 0) "Hôm nay" else "còn $left ngày", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
    Note("Thông báo nhắc giỗ dùng chung lịch nhắc buổi sáng của ứng dụng (bật trong Cài đặt).")
}

@Composable
fun GiaPhaScreen() {
    val ctx = LocalContext.current
    val members by FamilyRepository.get(ctx).members.collectAsState()
    if (members.isEmpty()) { Section("Gia phả") { Para("Thêm thành viên và chọn \"Con của\" để dựng cây gia phả.") }; return }
    val ids = members.map { it.id }.toSet()
    val roots = members.filter { it.parentId == 0L || it.parentId !in ids }.sortedBy { it.birthY.takeIf { y -> y > 0 } ?: 9999 }
    Section("Cây gia phả") {
        roots.forEach { r -> TreeNode(r, members, 0) }
    }
    Note("Mỗi bậc thụt vào là một đời. Bấm vào Thành Viên Gia Đình để sửa quan hệ.")
}

@Composable
private fun TreeNode(m: Member, all: List<Member>, depth: Int) {
    Row(Modifier.padding(start = (depth * 18).dp, top = 4.dp, bottom = 4.dp)) {
        Text(if (depth == 0) "◆ " else "└ ", color = MaterialTheme.colorScheme.primary)
        Column {
            Text(m.name + if (m.deceased) " (đã mất)" else "", fontWeight = FontWeight.Bold)
            Note(m.subtitle())
        }
    }
    if (depth < 8) all.filter { it.parentId == m.id }.sortedBy { it.birthY.takeIf { y -> y > 0 } ?: 9999 }.forEach { TreeNode(it, all, depth + 1) }
}
