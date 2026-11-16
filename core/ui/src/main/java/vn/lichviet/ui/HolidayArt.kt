package vn.lichviet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

/** Hình biểu trưng của ngày lễ (vẽ vector, không cần ảnh). */
enum class HolidayArt(val bg: Long) {
    TET(0xFFC62828), PHAO(0xFF5E35B1), TRANG(0xFF283593), DEN_LONG(0xFFE65100), SEN(0xFFAD1457),
    TRONG(0xFF8D6E00), CO(0xFFB71C1C), SACH(0xFF1565C0), HOA(0xFFD81B60), TIM(0xFFE53935),
    THONG(0xFF2E7D32), CA_CHEP(0xFFEF6C00), BANH(0xFF558B2F), BONG(0xFF00838F), SAO(0xFF5D4037);

    companion object {
        fun of(name: String): HolidayArt {
            val n = name.lowercase()
            fun has(vararg k: String) = k.any { n.contains(it) }
            return when {
                has("giao thừa", "nguyên đán") || (has("tết") && has("mùng")) -> TET
                has("dương lịch") -> PHAO
                has("trung thu") -> DEN_LONG
                has("vu lan", "phật đản") -> SEN
                has("hùng vương") -> TRONG
                has("ông táo") -> CA_CHEP
                has("giáng sinh") -> THONG
                has("valentine", "tình nhân") -> TIM
                has("phụ nữ", "gia đình") -> HOA
                has("nhà giáo", "pháp luật", "báo chí", "thầy thuốc") -> SACH
                has("thiếu nhi", "người cao tuổi", "dân số") -> BONG
                has("hàn thực", "đoan ngọ", "trùng cửu") -> BANH
                has("rằm", "sóc", "vọng", "nguyên tiêu", "hạ nguyên", "mùng 1") -> TRANG
                has("quốc khánh", "giải phóng", "lao động", "đảng", "quân đội", "chiến thắng", "cách mạng",
                    "thương binh", "đoàn", "công an", "hồ chí minh", "thủ đô", "doanh nhân") -> CO
                else -> SAO
            }
        }
    }
}

@Composable
fun HolidayIcon(name: String, size: Dp = 44.dp, modifier: Modifier = Modifier) {
    val art = HolidayArt.of(name)
    val shape = RoundedCornerShape(size * 0.28f)
    Canvas(
        modifier.size(size).shadow(3.dp, shape).clip(shape).background(Color(art.bg)),
    ) { drawArt(art) }
}

private val WHITE = Color(0xFFFFF8EC)
private val GOLD = Color(0xFFFFD54F)

private fun star(cx: Float, cy: Float, r: Float): Path = Path().apply {
    for (i in 0 until 10) {
        val a = -PI / 2 + i * PI / 5
        val rr = if (i % 2 == 0) r else r * 0.42f
        val x = cx + rr * cos(a).toFloat()
        val y = cy + rr * sin(a).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private fun DrawScope.drawArt(art: HolidayArt) {
    val u = size.minDimension / 48f
    fun o(x: Float, y: Float) = Offset(x * u, y * u)
    fun s(w: Float, h: Float) = Size(w * u, h * u)
    when (art) {
        HolidayArt.TET -> { // hoa mai 5 cánh + cành
            drawLine(Color(0xFF5D2E1F), o(6f, 42f), o(26f, 22f), 2.4f * u, StrokeCap.Round)
            for (i in 0 until 5) {
                val a = -PI / 2 + i * 2 * PI / 5
                drawCircle(GOLD, 6f * u, o(26f + 7f * cos(a).toFloat(), 20f + 7f * sin(a).toFloat()))
            }
            drawCircle(Color(0xFFE65100), 3.4f * u, o(26f, 20f))
            drawCircle(GOLD, 3f * u, o(12f, 34f))
        }
        HolidayArt.PHAO -> { // pháo hoa
            listOf(Triple(18f, 18f, 10f), Triple(33f, 30f, 8f)).forEach { (x, y, r) ->
                for (i in 0 until 12) {
                    val a = i * PI / 6
                    drawLine(if (i % 2 == 0) GOLD else WHITE, o(x + 3 * cos(a).toFloat(), y + 3 * sin(a).toFloat()),
                        o(x + r * cos(a).toFloat(), y + r * sin(a).toFloat()), 1.8f * u, StrokeCap.Round)
                }
            }
        }
        HolidayArt.TRANG -> { // trăng khuyết + sao
            drawCircle(GOLD, 14f * u, o(24f, 24f))
            drawCircle(Color(art.bg), 12f * u, o(30f, 19f))
            drawPath(star(36f * u, 34f * u, 4f * u), WHITE)
        }
        HolidayArt.DEN_LONG -> { // đèn lồng
            drawLine(WHITE, o(24f, 4f), o(24f, 10f), 1.6f * u)
            drawRect(GOLD, o(18f, 9f), s(12f, 3f))
            drawOval(Color(0xFFFF5252), o(11f, 11f), s(26f, 24f))
            for (x in listOf(17f, 24f, 31f)) drawLine(Color(0xFFB71C1C), o(x, 12f), o(x, 34f), 1.2f * u)
            drawRect(GOLD, o(18f, 34f), s(12f, 3f))
            drawLine(GOLD, o(24f, 37f), o(24f, 44f), 1.8f * u, StrokeCap.Round)
        }
        HolidayArt.SEN -> { // hoa sen
            val petal = { cx: Float, rot: Float ->
                rotate(rot, o(24f, 34f)) {
                    val p = Path().apply {
                        moveTo(24f * u, 34f * u); quadraticBezierTo((24f + 9f) * u, 22f * u, 24f * u, 10f * u)
                        quadraticBezierTo((24f - 9f) * u, 22f * u, 24f * u, 34f * u); close()
                    }
                    drawPath(p, if (cx == 0f) WHITE else Color(0xFFF8BBD0))
                }
            }
            petal(1f, -42f); petal(1f, 42f); petal(0f, 0f)
            drawLine(Color(0xFF81C784), o(10f, 38f), o(38f, 38f), 2.4f * u, StrokeCap.Round)
        }
        HolidayArt.TRONG -> { // mặt trống đồng
            drawCircle(GOLD, 18f * u, o(24f, 24f), style = Stroke(1.8f * u))
            drawCircle(GOLD, 13f * u, o(24f, 24f), style = Stroke(1f * u))
            drawPath(star(24f * u, 24f * u, 9f * u), GOLD)
        }
        HolidayArt.CO -> { // cờ đỏ sao vàng
            drawLine(WHITE, o(11f, 8f), o(11f, 42f), 2.2f * u, StrokeCap.Round)
            drawRect(Color(0xFFE53935), o(12f, 9f), s(26f, 18f))
            drawPath(star(25f * u, 18f * u, 6.5f * u), GOLD)
        }
        HolidayArt.SACH -> { // sách mở + bút
            val l = Path().apply { moveTo(24f * u, 16f * u); lineTo(8f * u, 12f * u); lineTo(8f * u, 34f * u); lineTo(24f * u, 38f * u); close() }
            val r = Path().apply { moveTo(24f * u, 16f * u); lineTo(40f * u, 12f * u); lineTo(40f * u, 34f * u); lineTo(24f * u, 38f * u); close() }
            drawPath(l, WHITE); drawPath(r, Color(0xFFE3F2FD))
            for (y in listOf(20f, 25f, 30f)) {
                drawLine(Color(0xFF90CAF9), o(11f, y - 2f), o(21f, y), 1f * u)
                drawLine(Color(0xFF90CAF9), o(27f, y), o(37f, y - 2f), 1f * u)
            }
            drawLine(GOLD, o(30f, 6f), o(40f, 16f), 2.4f * u, StrokeCap.Round)
        }
        HolidayArt.HOA -> { // bông hoa 6 cánh
            drawLine(Color(0xFFA5D6A7), o(24f, 26f), o(24f, 44f), 2.2f * u)
            for (i in 0 until 6) {
                val a = i * PI / 3
                drawCircle(WHITE, 5.5f * u, o(24f + 7f * cos(a).toFloat(), 20f + 7f * sin(a).toFloat()))
            }
            drawCircle(GOLD, 4f * u, o(24f, 20f))
        }
        HolidayArt.TIM -> {
            val p = Path().apply {
                moveTo(24f * u, 40f * u)
                cubicTo(6f * u, 28f * u, 8f * u, 10f * u, 24f * u, 17f * u)
                cubicTo(40f * u, 10f * u, 42f * u, 28f * u, 24f * u, 40f * u); close()
            }
            drawPath(p, WHITE)
        }
        HolidayArt.THONG -> { // cây thông
            for ((top, half, y) in listOf(Triple(6f, 9f, 18f), Triple(12f, 12f, 28f), Triple(20f, 15f, 38f))) {
                val p = Path().apply { moveTo(24f * u, top * u); lineTo((24f - half) * u, y * u); lineTo((24f + half) * u, y * u); close() }
                drawPath(p, Color(0xFFC8E6C9))
            }
            drawRect(Color(0xFF8D6E63), o(21f, 38f), s(6f, 6f))
            drawPath(star(24f * u, 6f * u, 4f * u), GOLD)
        }
        HolidayArt.CA_CHEP -> { // cá chép
            drawOval(GOLD, o(8f, 16f), s(26f, 16f))
            val tail = Path().apply { moveTo(32f * u, 24f * u); lineTo(42f * u, 15f * u); lineTo(42f * u, 33f * u); close() }
            drawPath(tail, Color(0xFFFFE082))
            drawCircle(Color(0xFF3E2723), 1.8f * u, o(14f, 22f))
            drawArc(Color(0xFFE65100), -60f, 120f, false, o(16f, 17f), s(12f, 14f), style = Stroke(1.2f * u))
        }
        HolidayArt.BANH -> { // bánh chưng buộc lạt
            drawRect(Color(0xFF9CCC65), o(10f, 10f), s(28f, 28f))
            drawRect(Color(0xFF33691E), o(10f, 10f), s(28f, 28f), style = Stroke(1.6f * u))
            drawLine(WHITE, o(19f, 10f), o(19f, 38f), 1.8f * u)
            drawLine(WHITE, o(29f, 10f), o(29f, 38f), 1.8f * u)
            drawLine(WHITE, o(10f, 19f), o(38f, 19f), 1.8f * u)
            drawLine(WHITE, o(10f, 29f), o(38f, 29f), 1.8f * u)
        }
        HolidayArt.BONG -> { // bóng bay
            drawLine(WHITE, o(18f, 28f), o(22f, 44f), 1.2f * u)
            drawLine(WHITE, o(31f, 24f), o(24f, 44f), 1.2f * u)
            drawOval(GOLD, o(10f, 8f), s(16f, 20f))
            drawOval(Color(0xFFFF8A80), o(23f, 4f), s(16f, 20f))
        }
        HolidayArt.SAO -> drawPath(star(24f * u, 25f * u, 15f * u), GOLD)
    }
}
