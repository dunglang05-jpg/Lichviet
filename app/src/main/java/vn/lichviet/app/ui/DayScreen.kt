package vn.lichviet.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.DayInfo
import vn.lichviet.core.GoodDayFinder

/** Lịch ngày + xem ngày tốt xấu đầy đủ. */
@Composable
fun DayScreen(jd: Int, todayJd: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val d = remember(jd) { dayOf(jd) }
    val fits = remember(jd) { GoodDayFinder.activitiesFor(d) }
    val a = remember(jd) { d.almanac() }
    Column(modifier.verticalScroll(rememberScrollState()).padding(bottom = 16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onChange(jd - 1) }) { Text("◀ Trước") }
            TextButton(onClick = { onChange(todayJd) }, enabled = jd != todayJd) { Text("Hôm nay") }
            TextButton(onClick = { onChange(jd + 1) }) { Text("Sau ▶") }
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(DayInfo.THU[d.weekday], style = MaterialTheme.typography.titleMedium)
            Text("${d.solarDay}", fontSize = 88.sp, fontWeight = FontWeight.Bold)
            Text("Tháng ${d.solarMonth} năm ${d.solarYear}", style = MaterialTheme.typography.titleMedium)
            Text(d.lunarFull(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
            Text("${d.verdict}  ${d.starsText()}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
        }

        if (d.holidays.isNotEmpty()) {
            Section("Ngày lễ") { d.holidays.forEach { Text("• ${it.name}" + if (it.major) " (lễ lớn)" else "") } }
        }
        Section("Can chi & tiết khí") {
            KeyValue("Năm", "${d.yearCanChi} (${d.yearAnimal})")
            KeyValue("Tháng", d.monthCanChi)
            KeyValue("Ngày", d.dayCanChi)
            KeyValue("Tiết khí", d.tietKhiToday?.let { "Hôm nay: $it" } ?: d.tietKhiCurrent)
        }
        Section("Ngày tốt xấu") {
            KeyValue("Thần trực", "${d.than12} · ${if (d.hoangDao) "Hoàng đạo" else "Hắc đạo"}")
            KeyValue("Trực", d.truc)
            KeyValue("Nhị thập bát tú", "Sao ${d.tu} (${d.tuHanh}) · ${if (d.tuTot) "tốt" else "xấu"}")
            KeyValue("Tuổi xung", d.chiXung)
            d.luuY.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error) }
        }
        Section("Giờ hoàng đạo") {
            d.hours.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    row.forEach { h ->
                        Text(
                            "${h.canChi} ${h.range}h",
                            fontWeight = if (h.good) FontWeight.Bold else FontWeight.Normal,
                            color = if (h.good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        Section("Việc nên làm / nên tránh") {
            Text("Nên: " + d.nenLam.joinToString(", "))
            Text("Kỵ: " + d.kyLam.joinToString(", "))
            Text(
                "Thuận tiện cho: " + (if (fits.isEmpty()) "không có việc lớn nào" else fits.joinToString(", ") { it.label }),
                fontWeight = FontWeight.SemiBold,
            )
        }
        Section("Ngũ hành") {
            KeyValue("Nạp âm ngày", a.napAmNgay)
            KeyValue("Nạp âm tháng / năm", "${a.napAmThang} / ${a.napAmNam}")
            Text(a.quanHeCanChi, color = if (a.quanHeTot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            Text("Tuổi xung: ${a.tuoiXung}" + if (a.tuoiNgoaiLe.isNotEmpty()) " (ngoại lệ: ${a.tuoiNgoaiLe})" else "")
            Text(a.hopXung, style = MaterialTheme.typography.bodySmall)
            if (a.tamSat.isNotEmpty()) Text(a.tamSat, style = MaterialTheme.typography.bodySmall)
        }
        Section("Sao tốt (Ngọc Hạp Thông Thư)") {
            if (a.goodStars.isEmpty()) Text("Không có")
            a.goodStars.forEach { Text("• ${it.name}: ${it.desc}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
        }
        Section("Sao xấu") {
            if (a.badStars.isEmpty()) Text("Không có")
            a.badStars.forEach { Text("• ${it.name}: ${it.desc}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
        }
        Section("Ngày kỵ") {
            if (a.kyNhat.isEmpty()) Text("Không phạm Tam Nương, Nguyệt Kỵ, Dương Công, Sát Chủ, Thụ Tử, Nguyệt Tận")
            a.kyNhat.forEach { Text("⚠ ${it.name}: ${it.desc}", color = MaterialTheme.colorScheme.error) }
        }
        Section("Khổng Minh lục diệu") {
            Text("Ngày ${a.khongMinh} (${if (a.khongMinhTot) "cát" else "hung"})", fontWeight = FontWeight.Bold)
            Text(a.khongMinhDesc)
        }
        Section("Bành Tổ bách kỵ") {
            Text(a.banhToCan, style = MaterialTheme.typography.bodySmall)
            Text(a.banhToChi, style = MaterialTheme.typography.bodySmall)
        }
        Section("Hướng & giờ xuất hành (Lý Thuần Phong)") {
            Text(a.huongText())
            a.xuatHanh.take(6).forEachIndexed { i, h ->
                val h2 = a.xuatHanh[i + 6]
                Text(
                    "${h.chi} ${h.range}h & ${h2.chi} ${h2.range}h: ${h.name} – ${h.desc}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (h.good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
