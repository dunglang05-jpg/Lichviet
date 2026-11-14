package vn.lichviet.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.MoonPhase

/** Lịch trăng: hình pha trăng, tuổi trăng, mốc trăng sắp tới và 30 ngày tới. */
@Composable
fun MoonScreen(jd: Int, modifier: Modifier = Modifier) {
    val d = remember(jd) { dayOf(jd) }
    val p = remember(jd) { MoonPhase.of(d.solarDay, d.solarMonth, d.solarYear) }
    val events = remember(jd) { MoonPhase.upcoming(d.solarDay, d.solarMonth, d.solarYear, 8) }
    Column(modifier.verticalScroll(rememberScrollState()).padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ThemeBadge(Modifier.padding(top = 12.dp))
        Text(solarText(d), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Text("Âm lịch ${d.lunarText()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        MoonDisc(
            p.fraction, Modifier.size(200.dp).padding(16.dp),
            dark = MaterialTheme.colorScheme.surfaceContainerHighest, ring = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(p.phaseName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Tuổi trăng %.1f ngày · Độ sáng %d%%".format(p.age, p.illumination), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Section("Mốc trăng sắp tới") {
            events.forEach { e ->
                val ed = DayInfo.of(e.day, e.month, e.year)
                KeyValue(e.name, "${DayInfo.THU_NGAN[ed.weekday]} ${e.day}/${e.month} · ${ed.lunarText()} âm")
            }
            Text("Thượng/hạ huyền là ước lượng, có thể lệch ±1 ngày.", style = MaterialTheme.typography.bodySmall)
        }
        Section("30 ngày tới") {
            for (i in 0 until 30) {
                val dt = LunarCalendar.jdToDate(jd + i)
                val mp = MoonPhase.of(dt[0], dt[1], dt[2])
                KeyValue("${dt[0]}/${dt[1]}", "${mp.phaseName} · ${mp.illumination}%")
            }
        }
    }
}

/** Vẽ đĩa trăng theo pha (0 = trăng mới, 0.5 = trăng tròn). */
@Composable
fun MoonDisc(
    fraction: Double, modifier: Modifier = Modifier, lit: Color = Color(0xFFF3EBC9), dark: Color = Color(0xFF2B3160),
    ring: Color = Color(0x55FFFFFF),
) {
    Canvas(modifier) {
        drawMoon(fraction, lit, dark)
        drawCircle(ring.copy(alpha = 0.5f), size.minDimension / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f))
    }
}

private fun DrawScope.drawMoon(f: Double, lit: Color, dark: Color) {
    val r = size.minDimension / 2
    val c = Offset(size.width / 2, size.height / 2)
    drawCircle(dark, r, c)
    val k = cos(2 * Math.PI * f).toFloat() // 1 mới, -1 tròn
    val rx = abs(k) * r
    val waxing = f < 0.5
    val path = Path().apply {
        val outer = Rect(c.x - r, c.y - r, c.x + r, c.y + r)
        // nửa sáng: bên phải khi trăng tăng, bên trái khi trăng giảm
        arcTo(outer, -90f, if (waxing) 180f else -180f, false)
        val inner = Rect(c.x - rx, c.y - r, c.x + rx, c.y + r)
        // đường phân cách sáng/tối
        val sweep = if (waxing) { if (k > 0) -180f else 180f } else { if (k > 0) 180f else -180f }
        arcTo(inner, 90f, sweep, false)
        close()
    }
    drawPath(path, lit)
}
