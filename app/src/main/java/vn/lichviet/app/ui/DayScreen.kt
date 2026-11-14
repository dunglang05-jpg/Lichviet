package vn.lichviet.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.DayInfo
import vn.lichviet.core.GoodDayFinder
import vn.lichviet.data.SettingsRepository

/** Lịch ngày + xem ngày tốt xấu đầy đủ (lịch vạn niên). */
@Composable
fun DayScreen(jd: Int, todayJd: Int, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val repo = remember { SettingsRepository.get(ctx) }
    val settings by repo.settings.collectAsState()
    var editProfile by remember { mutableStateOf(false) }
    val d = remember(jd) { dayOf(jd) }
    val fits = remember(jd) { GoodDayFinder.activitiesFor(d) }
    val a = remember(jd) { d.almanac() }

    Column(modifier.verticalScroll(rememberScrollState()).padding(top = 12.dp, bottom = 16.dp)) {
        ThemeBadge()
        WeekStrip(jd, todayJd, settings.mondayFirst, onChange)
        if (jd != todayJd) {
            TextButton(onClick = { onChange(todayJd) }, modifier = Modifier.padding(start = 12.dp)) { Text("← Về hôm nay", maxLines = 1) }
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
            MoonStatus(d, Modifier.padding(top = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState()).padding(top = 6.dp)) {
                val good = goodColor()
                Pill("${d.verdict} ${d.score}/5", (if (d.score >= 4) good else MaterialTheme.colorScheme.primary).copy(alpha = 0.15f),
                    if (d.score >= 4) good else MaterialTheme.colorScheme.primary, bold = true)
                Pill(if (d.hoangDao) "Hoàng đạo · ${d.than12}" else "Hắc đạo · ${d.than12}",
                    MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill("Ngày ${d.dayCanChi}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill("Tháng ${d.monthCanChi}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                Pill(a.khongMinh, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        if (d.holidays.isNotEmpty()) {
            ShadowCard(elevation = 6.dp) {
                SectionTitle("Ngày lễ")
                d.holidays.forEach {
                    HolidayRow(it.name, if (it.lunar) "Theo âm lịch · ${d.lunarText()}" else "Theo dương lịch", major = it.major)
                }
            }
        }

        // Nhân: hợp tuổi
        ThienDiaNhanCard(d, settings) { editProfile = true }

        // Thiên: việc nên / không nên
        Section("Việc nên làm · không nên làm") {
            Text("NÊN LÀM", style = MaterialTheme.typography.labelLarge, color = goodColor(), fontWeight = FontWeight.Bold)
            d.nenLam.forEach { Text("• $it") }
            Text("KHÔNG NÊN", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            d.kyLam.forEach { Text("• $it") }
            if (fits.isNotEmpty()) {
                Text("Ngày hợp cho: " + fits.joinToString(", ") { it.label }, fontWeight = FontWeight.SemiBold,
                    color = goodColor(), modifier = Modifier.padding(top = 4.dp))
            }
            d.luuY.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        }

        ActivityGridCard(jd, onChange)
        TietKhiCard(d)

        // Thông tin thêm
        Section("Sao tốt · sao xấu (Ngọc Hạp Thông Thư)") {
            Text("SAO TỐT", style = MaterialTheme.typography.labelLarge, color = goodColor(), fontWeight = FontWeight.Bold)
            if (a.goodStars.isEmpty()) Text("Không có")
            a.goodStars.forEach { Text("• ${it.name}: ${it.desc}", style = MaterialTheme.typography.bodySmall) }
            Text("SAO XẤU", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            if (a.badStars.isEmpty()) Text("Không có")
            a.badStars.forEach { Text("• ${it.name}: ${it.desc}", style = MaterialTheme.typography.bodySmall) }
            if (a.kyNhat.isNotEmpty()) {
                Text("NGÀY KỴ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                a.kyNhat.forEach { Text("⚠ ${it.name}: ${it.desc}", style = MaterialTheme.typography.bodySmall) }
            }
        }
        NguHanhTuoiXungCard(d, a)
        KienTruTuCard(d)

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
        Section("Giờ xuất hành Lý Thuần Phong") {
            Text(a.huongText(), style = MaterialTheme.typography.bodyMedium)
            a.xuatHanh.take(6).forEachIndexed { i, h ->
                val h2 = a.xuatHanh[i + 6]
                Column(Modifier.padding(top = 4.dp)) {
                    Text("${h.chi} (${h.range}h) & ${h2.chi} (${h2.range}h) · ${h.name}", fontWeight = FontWeight.SemiBold,
                        color = if (h.good) goodColor() else MaterialTheme.colorScheme.error)
                    Text(h.desc, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Section("Khổng Minh lục diệu") {
            Text("Ngày ${a.khongMinh} (${if (a.khongMinhTot) "cát" else "hung"})", fontWeight = FontWeight.Bold,
                color = if (a.khongMinhTot) goodColor() else MaterialTheme.colorScheme.error)
            Text(a.khongMinhDesc)
        }
        Section("Bành Tổ bách kỵ") {
            Text(a.banhToCan, style = MaterialTheme.typography.bodySmall)
            Text(a.banhToChi, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (editProfile) {
        ProfileDialog(settings, onDismiss = { editProfile = false }) { name, year ->
            repo.update { it.copy(userName = name, birthYear = year) }
            editProfile = false
        }
    }
}
