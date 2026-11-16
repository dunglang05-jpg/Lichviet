package vn.lichviet.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Hoạ tiết nền của từng giao diện, vẽ trên hệ toạ độ chuẩn 390 x 844 rồi co giãn theo màn hình. */
enum class Pattern { BLOSSOM, LOTUS, LEAVES, SNOW, WAVES, BAGUA, COINS, SUNDIAL, STARS, DRUM }

fun DrawScope.drawPattern(p: Pattern, color: Color, alpha: Float) {
    val k = size.width / 390f
    val col = color.copy(alpha = alpha)
    val w = size.width
    val h = size.height
    fun px(v: Float) = v * k
    when (p) {
        Pattern.BLOSSOM -> {
            val r = Random(1)
            repeat(30) {
                val x = r.nextFloat() * w
                val y = h * 0.12f + r.nextFloat() * h * 0.88f
                val s = px(0.5f + r.nextFloat() * 0.8f)
                if (r.nextFloat() < 0.45f) blossom(x, y, s, col, color.copy(alpha = alpha * 1.4f))
                else rotate(r.nextFloat() * 180f, Offset(x, y)) {
                    drawOval(col, Offset(x - 4 * s, y - 2.4f * s), Size(8 * s, 4.8f * s))
                }
            }
            // cành mai góc dưới
            val branch = Path().apply {
                moveTo(-px(10f), h - px(24f)); cubicTo(px(60f), h - px(64f), px(110f), h - px(84f), px(170f), h - px(144f))
                moveTo(px(70f), h - px(72f)); cubicTo(px(90f), h - px(104f), px(85f), h - px(124f), px(100f), h - px(144f))
            }
            drawPath(branch, Color(0xFF7A4A35).copy(alpha = alpha), style = Stroke(px(3f), cap = StrokeCap.Round))
            listOf(170f to 144f, 100f to 144f, 135f to 109f, 60f to 54f).forEach { (x, dy) ->
                blossom(px(x), h - px(dy), px(1.1f), Color(0xFFF2B630).copy(alpha = alpha * 1.3f), Color(0xFFB3541E).copy(alpha = alpha))
            }
        }
        Pattern.LOTUS -> {
            val r = Random(2)
            repeat(9) {
                val x = r.nextFloat() * w
                val y = h * 0.24f + r.nextFloat() * h * 0.76f
                val rad = px(26f + r.nextFloat() * 34f)
                val start = r.nextFloat() * 360f
                drawArc(col, start, 320f, true, Offset(x - rad, y - rad), Size(rad * 2, rad * 2))
            }
            repeat(7) {
                val x = r.nextFloat() * w
                val y = h * 0.18f + r.nextFloat() * h * 0.82f
                for (i in 1..3) {
                    val rx = px(14f * i); val ry = px(5f * i)
                    drawOval(col, Offset(x - rx, y - ry), Size(rx * 2, ry * 2), style = Stroke(px(1f)))
                }
            }
        }
        Pattern.LEAVES -> {
            val r = Random(3)
            val cols = listOf(0xFFC8642A, 0xFFE09A3A, 0xFFB8501F, 0xFFD98A2B)
            repeat(24) {
                val x = r.nextFloat() * w
                val y = h * 0.3f + r.nextFloat() * h * 0.7f
                val s = px(0.7f + r.nextFloat() * 0.7f)
                val leaf = Path().apply {
                    moveTo(x, y); quadraticBezierTo(x + 10 * s, y - 12 * s, x + 20 * s, y)
                    quadraticBezierTo(x + 10 * s, y + 12 * s, x, y); close()
                }
                rotate(r.nextFloat() * 360f, Offset(x, y)) {
                    drawPath(leaf, Color(cols[r.nextInt(cols.size)]).copy(alpha = alpha))
                }
            }
            listOf(Triple(20f, 360f, 1.4f), Triple(250f, 420f, 1.1f), Triple(140f, 640f, 1.6f), Triple(290f, 780f, 1.2f)).forEach { (x, y, s) ->
                cloud(px(x), px(y), px(s), col)
            }
        }
        Pattern.SNOW -> {
            val r = Random(4)
            repeat(26) { flake(r.nextFloat() * w, h * 0.18f + r.nextFloat() * h * 0.82f, px(0.5f + r.nextFloat() * 0.8f), col) }
            repeat(40) { drawCircle(col, px(1f + r.nextFloat() * 1.4f), Offset(r.nextFloat() * w, r.nextFloat() * h)) }
            val hills = Path().apply {
                moveTo(0f, h - px(44f)); quadraticBezierTo(px(80f), h - px(84f), px(160f), h - px(54f))
                quadraticBezierTo(px(240f), h - px(30f), px(320f), h - px(64f)); lineTo(w, h - px(74f)); lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(hills, col)
        }
        Pattern.WAVES -> {
            val rad = px(18f)
            var row = 0
            var y = 0f
            while (y < h + rad) {
                val off = if (row % 2 == 1) rad else 0f
                var x = -rad + off
                while (x < w + 2 * rad) {
                    drawArc(col, 180f, 180f, false, Offset(x - rad, y - rad), Size(rad * 2, rad * 2), style = Stroke(px(1.2f)))
                    val r2 = rad * 0.6f
                    drawArc(col, 180f, 180f, false, Offset(x - r2, y - r2), Size(r2 * 2, r2 * 2), style = Stroke(px(1f)))
                    x += 2 * rad
                }
                y += rad; row++
            }
        }
        Pattern.BAGUA -> {
            val tri = listOf("111", "110", "101", "100", "011", "010", "001", "000")
            val c = Offset(w / 2, h * 0.71f)
            val rad = px(170f)
            tri.forEachIndexed { i, t ->
                rotate(i * 45f, c) {
                    t.forEachIndexed { j, b ->
                        val y = c.y - rad + px(10f * j)
                        if (b == '1') drawRect(col, Offset(c.x - px(26f), y), Size(px(52f), px(6f)))
                        else {
                            drawRect(col, Offset(c.x - px(26f), y), Size(px(22f), px(6f)))
                            drawRect(col, Offset(c.x + px(4f), y), Size(px(22f), px(6f)))
                        }
                    }
                }
            }
            drawCircle(col, rad - px(34f), c, style = Stroke(px(1f)))
            drawCircle(col, rad + px(44f), c, style = Stroke(px(1f)))
        }
        Pattern.COINS -> {
            val r = Random(7)
            repeat(16) {
                val x = r.nextFloat() * w; val y = r.nextFloat() * h; val s = px(0.7f + r.nextFloat() * 0.6f)
                drawCircle(col, 16 * s, Offset(x, y), style = Stroke(2 * s))
                drawRect(col, Offset(x - 5 * s, y - 5 * s), Size(10 * s, 10 * s), style = Stroke(2 * s))
            }
            repeat(10) { cloud(r.nextFloat() * w, r.nextFloat() * h, px(0.9f), col) }
        }
        Pattern.SUNDIAL -> {
            val c = Offset(w / 2, h * 0.39f)
            for (i in 1..12) drawCircle(col, px(44f * i), c, style = Stroke(px(1f)))
            for (i in 0 until 24) {
                val a = i * PI / 12
                drawLine(col, Offset(c.x + px(150f) * cos(a).toFloat(), c.y + px(150f) * sin(a).toFloat()),
                    Offset(c.x + px(560f) * cos(a).toFloat(), c.y + px(560f) * sin(a).toFloat()), px(if (i % 2 == 0) 1.2f else 0.6f))
            }
        }
        Pattern.STARS -> {
            val r = Random(9)
            repeat(140) {
                drawCircle(Color.White.copy(alpha = alpha * (0.3f + r.nextFloat() * 0.7f)), px(listOf(0.6f, 0.8f, 1f, 1.4f, 2f)[r.nextInt(5)]),
                    Offset(r.nextFloat() * w, r.nextFloat() * h))
            }
            val line = Color(0xFF7C86D8).copy(alpha = alpha)
            val dot = Color(0xFFF5D76E).copy(alpha = alpha * 1.4f)
            repeat(6) {
                var pt = Offset(px(20f) + r.nextFloat() * (w - px(100f)), h * 0.24f + r.nextFloat() * h * 0.7f)
                drawCircle(dot, px(2f), pt)
                repeat(3 + r.nextInt(3)) {
                    val nx = pt + Offset(px(-40f + r.nextFloat() * 90f), px(-40f + r.nextFloat() * 80f))
                    drawLine(line, pt, nx, px(0.8f))
                    drawCircle(dot, px(2f), nx)
                    pt = nx
                }
            }
        }
        Pattern.DRUM -> {
            val c = Offset(w / 2, h * 0.76f)
            val n = 14
            for (i in 0 until n) {
                val a = i * 2 * PI / n
                val b = a + PI / n
                val d = a - PI / n
                val ray = Path().apply {
                    moveTo(c.x, c.y)
                    lineTo(c.x + px(26f) * cos(d).toFloat(), c.y + px(26f) * sin(d).toFloat())
                    lineTo(c.x + px(70f) * cos(a).toFloat(), c.y + px(70f) * sin(a).toFloat())
                    lineTo(c.x + px(26f) * cos(b).toFloat(), c.y + px(26f) * sin(b).toFloat())
                    close()
                }
                drawPath(ray, col)
            }
            listOf(86f, 96f, 150f, 160f, 230f, 240f).forEach { drawCircle(col, px(it), c, style = Stroke(px(1.5f))) }
            for (i in 0 until 48) {
                val a = i * 2 * PI / 48
                drawLine(col, Offset(c.x + px(96f) * cos(a).toFloat(), c.y + px(96f) * sin(a).toFloat()),
                    Offset(c.x + px(150f) * cos(a).toFloat(), c.y + px(150f) * sin(a).toFloat()), px(0.8f))
            }
            for (i in 0 until 36) {
                val a = i * 2 * PI / 36
                val o = Offset(c.x + px(195f) * cos(a).toFloat(), c.y + px(195f) * sin(a).toFloat())
                drawCircle(col, px(9f), o, style = Stroke(px(1.2f)))
                drawCircle(col, px(2f), o)
            }
        }
    }
}

private fun DrawScope.blossom(x: Float, y: Float, s: Float, petal: Color, center: Color) {
    for (i in 0 until 5) {
        val a = i * 2 * PI / 5 - PI / 2
        val cx = x + 7 * s * cos(a).toFloat()
        val cy = y + 7 * s * sin(a).toFloat()
        rotate(Math.toDegrees(a).toFloat(), Offset(cx, cy)) {
            drawOval(petal, Offset(cx - 5.5f * s, cy - 4f * s), Size(11f * s, 8f * s))
        }
    }
    drawCircle(center, 2.6f * s, Offset(x, y))
}

private fun DrawScope.flake(x: Float, y: Float, s: Float, col: Color) {
    translate(x, y) {
        for (a in listOf(0f, 60f, 120f)) {
            rotate(a, Offset.Zero) {
                drawLine(col, Offset(0f, -14 * s), Offset(0f, 14 * s), 1.6f * s, StrokeCap.Round)
                drawLine(col, Offset(-4 * s, -10 * s), Offset(0f, -6 * s), 1.4f * s, StrokeCap.Round)
                drawLine(col, Offset(4 * s, -10 * s), Offset(0f, -6 * s), 1.4f * s, StrokeCap.Round)
                drawLine(col, Offset(-4 * s, 10 * s), Offset(0f, 6 * s), 1.4f * s, StrokeCap.Round)
                drawLine(col, Offset(4 * s, 10 * s), Offset(0f, 6 * s), 1.4f * s, StrokeCap.Round)
            }
        }
    }
}

/** Mây cuộn kiểu hoa văn truyền thống. */
private fun DrawScope.cloud(x: Float, y: Float, s: Float, col: Color) {
    val st = Stroke(1.6f * s, cap = StrokeCap.Round)
    drawArc(col, 180f, 180f, false, Offset(x, y - 12 * s), Size(24 * s, 24 * s), style = st)
    drawArc(col, 200f, 200f, false, Offset(x + 18 * s, y - 16 * s), Size(22 * s, 22 * s), style = st)
    drawArc(col, 90f, 270f, false, Offset(x + 10 * s, y - 6 * s), Size(10 * s, 10 * s), style = st)
    drawLine(col, Offset(x, y), Offset(x + 42 * s, y), 1.6f * s, StrokeCap.Round)
}
