package vn.lichviet.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
        ThemeBadge(Modifier.padding(top = 12.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { onChange(jd - 1) }) { Text("◀ Trước", maxLines = 1) }
            TextButton(onClick = { onChange(todayJd) }, enabled = jd != todayJd) { Text("Hôm nay", maxLines = 1) }
            TextButton(onClick = { onChange(jd + 1) }) { Text("Sau ▶", maxLines = 1) }
        }
        // Thẻ ngày chính: dương lịch bên trái, âm lịch bên phải
        ShadowCard(elevation = 8.dp) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(DayInfo.THU[d.weekday].uppercase(), style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("${d.solarDay}", fontSize = 76.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary,
                        lineHeight = 80.sp)
                    Text("Tháng ${d.solarMonth}, ${d.solarYear}", style = MaterialTheme.typography.bodyMedium)
                }
                Box(Modifier.width(1.dp).height(110.dp).background(MaterialTheme.colorScheme.outlineVariant))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("ÂM LỊCH", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${d.lunarDay}", fontSize = 52.sp, fontWeight = FontWeight.Bold, lineHeight = 56.sp)
                    Text("Tháng ${d.lunarMonth}${if (d.leap) " nhuận" else ""}", style = MaterialTheme.typography.bodyMedium)
                    Text("Năm ${d.yearCanChi}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 6.dp)) {
                val good = goodColor()
                Pill("${d.verdict} ${d.score}/5", (if (d.score >= 4) good else MaterialTheme.colorScheme.primary).copy(alpha = 0.15f),
                    if (d.score >= 4) good else MaterialTheme.colorScheme.primary, bold = true)
                Pill(if (d.hoangDao) "Hoàng đạo · ${d.than12}" else "Hắc đạo · ${d.than12}",
                    MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill("Ngày ${d.dayCanChi}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill("Trực ${d.truc}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill(a.khongMinh, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        if (d.holidays.isNotEmpty()) {
            ShadowCard(elevation = 6.dp) {
                SectionTitle("Ngày lễ hôm nay")
                d.holidays.forEach {
                    HolidayRow(it.name, if (it.lunar) "Theo âm lịch · ${d.lunarText()}" else "Theo dương lịch", major = it.major)
                }
            }
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
            d.luuY.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        }
        Section("Giờ hoàng đạo") {
            d.hours.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { h ->
                        val bg = if (h.good) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        val fg = if (h.good) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(bg).padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(h.canChi, color = fg, fontWeight = if (h.good) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Text("${h.range}h", color = fg, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }
            }
            Text("Ô đậm là giờ hoàng đạo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
