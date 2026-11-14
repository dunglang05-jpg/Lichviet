package vn.lichviet.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

private class ParsedEl(val path: Path, val fill: Color?, val stroke: Color?, val width: Float)

/** Tranh tháng phong cách Đông Hồ (khung 320 x 180, co giãn theo bề ngang). */
@Composable
fun MonthPainting(month: Int, modifier: Modifier = Modifier) {
    val parsed = remember(month) {
        MonthArtData.scene(month).map {
            ParsedEl(
                PathParser().parsePathString(it.d).toPath(),
                if (it.fill == 0L) null else Color(it.fill),
                if (it.stroke == 0L) null else Color(it.stroke),
                it.width,
            )
        }
    }
    Canvas(modifier.fillMaxWidth().aspectRatio(320f / 180f).clip(RoundedCornerShape(12.dp))) {
        val k = size.width / 320f
        drawRect(Color(0xFFF2E3C2))
        withTransform({ scale(k, k, Offset.Zero) }) {
            parsed.forEach { el ->
                el.fill?.let { drawPath(el.path, it) }
                el.stroke?.let { drawPath(el.path, it, style = Stroke(el.width, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
            }
        }
    }
}
