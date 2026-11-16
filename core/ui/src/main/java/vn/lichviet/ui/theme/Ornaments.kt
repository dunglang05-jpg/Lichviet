package vn.lichviet.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Hoạ tiết nhỏ (một "đơn vị hoa văn") của từng giao diện, vẽ trong ô vuông cạnh 2r quanh (cx, cy).
 * c1 là màu nhấn chính, c2 là màu đối ứng: mỗi hoạ tiết luôn dùng cả hai để hoa văn và màu đi cùng nhau.
 */
fun DrawScope.drawMotif(p: Pattern, cx: Float, cy: Float, r: Float, c1: Color, c2: Color) {
    val c = Offset(cx, cy)
    when (p) {
        Pattern.BLOSSOM -> { // hoa đào 5 cánh, nhụy đối ứng
            for (i in 0 until 5) {
                val a = (i * 72 - 90) * PI / 180
                drawCircle(c1, r * .42f, Offset(cx + (r * .5f * cos(a)).toFloat(), cy + (r * .5f * sin(a)).toFloat()))
            }
            drawCircle(c2, r * .28f, c)
        }
        Pattern.LOTUS -> { // búp sen: cánh giữa màu chính, hai cánh bên màu đối ứng
            val petal = { ang: Float, col: Color ->
                rotate(ang, Offset(cx, cy + r * .7f)) {
                    val path = Path().apply {
                        moveTo(cx, cy + r * .7f); quadraticBezierTo(cx + r * .55f, cy, cx, cy - r * .9f)
                        quadraticBezierTo(cx - r * .55f, cy, cx, cy + r * .7f); close()
                    }
                    drawPath(path, col)
                }
            }
            petal(-38f, c2); petal(38f, c2); petal(0f, c1)
        }
        Pattern.LEAVES -> { // lá thu + gân
            val path = Path().apply {
                moveTo(cx - r * .9f, cy + r * .5f); quadraticBezierTo(cx - r * .2f, cy - r * 1.0f, cx + r * .9f, cy - r * .5f)
                quadraticBezierTo(cx + r * .2f, cy + r * 1.0f, cx - r * .9f, cy + r * .5f); close()
            }
            drawPath(path, c1)
            drawLine(c2, Offset(cx - r * .8f, cy + r * .45f), Offset(cx + r * .8f, cy - r * .45f), r * .14f, StrokeCap.Round)
        }
        Pattern.SNOW -> { // bông tuyết 6 nhánh
            for (i in 0 until 6) {
                val a = i * 60 * PI / 180
                val e = Offset(cx + (r * cos(a)).toFloat(), cy + (r * sin(a)).toFloat())
                drawLine(c1, c, e, r * .16f, StrokeCap.Round)
            }
            drawCircle(c2, r * .26f, c)
        }
        Pattern.WAVES -> { // sóng nước (hoa văn sóng cuộn)
            for (k in 0..2) {
                val rr = r * (1f - k * .3f)
                drawArc(if (k % 2 == 0) c1 else c2, 180f, 180f, false, Offset(cx - rr, cy - rr * .2f), Size(rr * 2, rr * 2),
                    style = Stroke(r * .16f, cap = StrokeCap.Round))
            }
        }
        Pattern.BAGUA -> { // quẻ Càn – Khôn xen kẽ
            for (k in 0..2) {
                val y = cy - r * .6f + k * r * .6f
                if (k == 1) {
                    drawLine(c2, Offset(cx - r, y), Offset(cx - r * .18f, y), r * .26f)
                    drawLine(c2, Offset(cx + r * .18f, y), Offset(cx + r, y), r * .26f)
                } else drawLine(c1, Offset(cx - r, y), Offset(cx + r, y), r * .26f)
            }
        }
        Pattern.COINS -> { // đồng tiền cổ lỗ vuông
            drawCircle(c1, r * .95f, c)
            drawCircle(c2, r * .72f, c, style = Stroke(r * .12f))
            drawRect(c2, Offset(cx - r * .26f, cy - r * .26f), Size(r * .52f, r * .52f))
        }
        Pattern.SUNDIAL -> { // mặt trời 8 tia
            for (i in 0 until 8) {
                val a = i * 45 * PI / 180
                drawLine(if (i % 2 == 0) c1 else c2, Offset(cx + (r * .55f * cos(a)).toFloat(), cy + (r * .55f * sin(a)).toFloat()),
                    Offset(cx + (r * cos(a)).toFloat(), cy + (r * sin(a)).toFloat()), r * .16f, StrokeCap.Round)
            }
            drawCircle(c1, r * .38f, c)
        }
        Pattern.STARS -> { // ngôi sao 5 cánh + chấm sao nhỏ
            val path = Path()
            for (i in 0 until 10) {
                val rr = if (i % 2 == 0) r else r * .42f
                val a = (i * 36 - 90) * PI / 180
                val x = cx + (rr * cos(a)).toFloat(); val y = cy + (rr * sin(a)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close(); drawPath(path, c1)
            drawCircle(c2, r * .14f, Offset(cx + r * .9f, cy - r * .8f))
        }
        Pattern.DRUM -> { // mặt trống đồng: sao giữa, vành tia
            drawCircle(c1, r, c, style = Stroke(r * .12f))
            drawCircle(c2, r * .66f, c, style = Stroke(r * .1f))
            for (i in 0 until 12) {
                val a = i * 30 * PI / 180
                drawLine(c1, Offset(cx + (r * .12f * cos(a)).toFloat(), cy + (r * .12f * sin(a)).toFloat()),
                    Offset(cx + (r * .5f * cos(a)).toFloat(), cy + (r * .5f * sin(a)).toFloat()), r * .1f)
            }
        }
    }
}

/** Dải hoa văn: hoạ tiết của giao diện lặp lại, hai màu đối ứng xen kẽ, kẹp giữa hai chỉ mảnh. */
@Composable
fun MotifBand(spec: ThemeSpec, modifier: Modifier = Modifier, height: Dp = 14.dp, alpha: Float = 1f) {
    Canvas(modifier.fillMaxWidth().height(height)) {
        val h = size.height
        val r = h * .36f
        val step = h * 1.6f
        val n = (size.width / step).toInt().coerceAtLeast(1)
        val start = (size.width - (n - 1) * step) / 2
        val line = spec.accent.copy(alpha = .55f * alpha)
        drawLine(line, Offset(0f, h * .06f), Offset(size.width, h * .06f), 1.2f)
        drawLine(line, Offset(0f, h * .94f), Offset(size.width, h * .94f), 1.2f)
        for (i in 0 until n) {
            val a = spec.accent.copy(alpha = alpha); val b = spec.accent2.copy(alpha = alpha)
            drawMotif(spec.pattern, start + i * step, h / 2, r, if (i % 2 == 0) a else b, if (i % 2 == 0) b else a)
        }
    }
}

/** Một hoạ tiết đơn, dùng làm điểm nhấn ở tiêu đề / góc thẻ. */
@Composable
fun Motif(spec: ThemeSpec, modifier: Modifier, alpha: Float = 1f, swap: Boolean = false) {
    Canvas(modifier) {
        val a = spec.accent.copy(alpha = alpha); val b = spec.accent2.copy(alpha = alpha)
        drawMotif(spec.pattern, size.width / 2, size.height / 2, size.minDimension * .45f, if (swap) b else a, if (swap) a else b)
    }
}

/**
 * Hoa văn ẩn ở nền: mặt trống đồng Đông Sơn (góc trên phải, khuất một nửa) và ruộng bậc thang (dưới đáy).
 * Vẽ rất mờ để chỉ thấy khi nhìn kỹ, không làm rối nội dung.
 */
fun DrawScope.drawHeritage(color: Color, alpha: Float = .06f) {
    val c = color.copy(alpha = alpha)
    val w = size.width; val h = size.height
    // trống đồng
    val r = w * .62f
    val o = Offset(w * 1.02f, h * .1f)
    drawCircle(c, r, o, style = Stroke(2.5f))
    for (k in 1..6) drawCircle(c, r * (1f - k * .13f), o, style = Stroke(if (k % 2 == 0) 4f else 1.5f))
    // ngôi sao 14 cánh ở tâm trống
    val star = Path()
    for (i in 0 until 28) {
        val rr = if (i % 2 == 0) r * .2f else r * .07f
        val a = i * PI / 14
        val x = o.x + (rr * cos(a)).toFloat(); val y = o.y + (rr * sin(a)).toFloat()
        if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
    }
    star.close(); drawPath(star, c)
    // vành chim Lạc bay (cách điệu bằng các chữ V)
    for (i in 0 until 36) {
        val a = i * 2 * PI / 36
        val rr = r * .55f
        val px = o.x + (rr * cos(a)).toFloat(); val py = o.y + (rr * sin(a)).toFloat()
        rotate((a * 180 / PI).toFloat() + 90f, Offset(px, py)) {
            drawLine(c, Offset(px - 8f, py - 5f), Offset(px, py), 2f)
            drawLine(c, Offset(px, py), Offset(px + 8f, py - 5f), 2f)
        }
    }
    // ruộng bậc thang
    for (k in 0 until 7) {
        val y0 = h * (.8f + k * .035f)
        val p = Path().apply {
            moveTo(0f, y0)
            cubicTo(w * .25f, y0 - h * .03f, w * .5f, y0 + h * .025f, w * .75f, y0 - h * .015f)
            quadraticBezierTo(w * .9f, y0 - h * .03f, w, y0 - h * .01f)
        }
        drawPath(p, c, style = Stroke(2f))
        // bông lúa nhỏ dọc bờ
        for (j in 0 until 9) {
            val x = w * (.05f + j * .11f) + k * 7f
            val yy = y0 - 6f + (j % 3) * 3f
            drawLine(c, Offset(x, yy), Offset(x + 3f, yy - 10f), 1.5f)
        }
    }
}
