package vn.lichviet.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import vn.lichviet.app.ui.theme.LocalLich
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar

/** Tiện ích dùng chung cho các màn hình. */
fun dayOf(jd: Int): DayInfo = LunarCalendar.jdToDate(jd).let { DayInfo.of(it[0], it[1], it[2]) }

fun solarText(d: DayInfo) = "${DayInfo.THU[d.weekday]}, ${d.solarDay}/${d.solarMonth}/${d.solarYear}"

val CardShape = RoundedCornerShape(20.dp)

/** Khung thẻ nổi có đổ bóng. */
@Composable
fun ShadowCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    container: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 7.dp),
        shape = CardShape,
        colors = CardDefaults.elevatedCardColors(containerColor = container),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

/** Khung có tiêu đề kèm vạch màu nhấn. */
@Composable
fun Section(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    ShadowCard(modifier) {
        SectionTitle(title)
        content()
    }
}

@Composable
fun SectionTitle(title: String, color: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 2.dp)) {
        Box(Modifier.width(4.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun KeyValue(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Text(key, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End, modifier = Modifier.weight(1f),
        )
    }
}

/** Nhãn nhỏ bo tròn, luôn một dòng. */
@Composable
fun Pill(text: String, bg: Color, fg: Color, bold: Boolean = false) {
    Text(
        text, color = fg, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

/** Dòng nhóm/tên giao diện đang dùng, vd "MÙA · ĐÔNG — Sương tuyết". */
@Composable
fun ThemeBadge(modifier: Modifier = Modifier) {
    val spec = LocalLich.current ?: return
    Text(
        "${spec.group.uppercase()} · ${spec.name.uppercase()}  —  ${spec.variant}",
        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(horizontal = 20.dp),
    )
}

/** Màu "tốt" của giao diện. */
@Composable
fun goodColor(): Color = LocalLich.current?.good ?: Color(0xFF2E7D4F)

@Composable
fun HolidayRow(name: String, sub: String, trailing: String? = null, major: Boolean = false, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        HolidayIcon(name, 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = if (major) FontWeight.Bold else FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            Pill(trailing, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, bold = true)
        }
    }
}

@Suppress("unused") private val keepSize = Modifier.size(0.dp)

/** Tình trạng trăng gọn của một ngày: hình trăng nhỏ + tên pha + độ sáng. */
@Composable
fun MoonStatus(d: DayInfo, modifier: Modifier = Modifier) {
    val mp = androidx.compose.runtime.remember(d.jd) { vn.lichviet.core.MoonPhase.of(d.solarDay, d.solarMonth, d.solarYear) }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        MoonDisc(mp.fraction, Modifier.size(22.dp), dark = MaterialTheme.colorScheme.outlineVariant, ring = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(
            "${mp.phaseName} · sáng ${mp.illumination}% · tuổi trăng ${"%.1f".format(mp.age)} ngày",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
