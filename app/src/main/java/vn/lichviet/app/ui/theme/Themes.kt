package vn.lichviet.app.ui.theme

import androidx.compose.ui.graphics.Color
import vn.lichviet.core.Almanac
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar

/** Một giao diện đã được "giải" cho ngày/giờ cụ thể. */
data class ThemeSpec(
    val id: Int,
    val group: String,
    val name: String,
    val variant: String,
    val dark: Boolean,
    val bg: Color,
    val card: Color,
    val ink: Color,
    val dim: Color,
    val accent: Color,
    val onAccent: Color,
    val good: Color,
    val bad: Color,
    val pattern: Pattern,
    val patternColor: Color,
    val patternAlpha: Float,
)

/**
 * 10 giao diện chia 4 nhóm: Mùa (Xuân, Hạ, Thu, Đông), Thiên Can (Ngũ Hành, Âm Dương),
 * Địa Chi (Con Giáp, Thời Thần), Sao (Nhị Thập Bát Tú, Hoàng Đạo).
 * Một số giao diện tự đổi theo ngày/giờ: Ngũ Hành theo can ngày, Thời Thần theo canh giờ,
 * Hoàng Đạo theo ngày hoàng/hắc đạo; "Tự đổi theo mùa" chọn mùa theo tiết khí.
 */
object Themes {
    val NAMES = listOf("Xuân", "Hạ", "Thu", "Đông", "Ngũ Hành", "Âm Dương", "Con Giáp", "Thời Thần", "Nhị Thập Bát Tú", "Hoàng Đạo")
    val GROUPS = listOf("Mùa", "Mùa", "Mùa", "Mùa", "Thiên Can", "Thiên Can", "Địa Chi", "Địa Chi", "Sao", "Sao")
    val GROUP_ORDER = listOf("Mùa", "Thiên Can", "Địa Chi", "Sao")

    /** Màu xem trước (nền, nhấn) cho màn chọn giao diện. */
    val SWATCH = listOf(
        0xFFFFF3EF to 0xFFD2456A, 0xFFEDF6EC to 0xFF2F7D4A, 0xFF2E1D14 to 0xFFF0A04B, 0xFFEEF3F7 to 0xFF2F5F8A,
        0xFF0E1B2C to 0xFF5FB0E6, 0xFFF4F1EA to 0xFF141414, 0xFF5A1A12 to 0xFFF2C46A, 0xFFF7E9D3 to 0xFFC8741C,
        0xFF11143A to 0xFFF5D76E, 0xFFFBF6E9 to 0xFF8A6400,
    ).map { Color(it.first) to Color(it.second) }

    /** Mùa theo tiết khí: 0 Xuân (Lập xuân), 1 Hạ (Lập hạ), 2 Thu (Lập thu), 3 Đông (Lập đông). */
    fun seasonOf(d: DayInfo): Int {
        val lon = LunarCalendar.sunLongitudeDeg(d.jd, 12.0)
        return when {
            lon >= 315 || lon < 45 -> 0
            lon < 135 -> 1
            lon < 225 -> 2
            else -> 3
        }
    }

    private fun c(v: Long) = Color(v)

    private fun light(
        id: Int, variant: String, bg: Long, card: Long, ink: Long, dim: Long, accent: Long,
        pattern: Pattern, patternColor: Long, alpha: Float,
        good: Long = 0xFF2E7D4F, bad: Long = 0xFFB42318, onAccent: Long = 0xFFFFFFFF,
    ) = ThemeSpec(id, GROUPS[id], NAMES[id], variant, false, c(bg), c(card), c(ink), c(dim), c(accent), c(onAccent),
        c(good), c(bad), pattern, c(patternColor), alpha)

    private fun dark(
        id: Int, variant: String, bg: Long, card: Long, ink: Long, dim: Long, accent: Long,
        pattern: Pattern, patternColor: Long, alpha: Float,
        good: Long = 0xFF9BD38A, bad: Long = 0xFFFF8A78, onAccent: Long = 0xFF141414,
    ) = ThemeSpec(id, GROUPS[id], NAMES[id], variant, true, c(bg), c(card), c(ink), c(dim), c(accent), c(onAccent),
        c(good), c(bad), pattern, c(patternColor), alpha)

    /**
     * @param themeId 0..9
     * @param autoSeason true: bỏ qua themeId, dùng giao diện mùa hiện tại
     * @param hour giờ hiện tại 0..23 (giờ VN), dùng cho Thời Thần
     */
    fun resolve(themeId: Int, autoSeason: Boolean, today: DayInfo, hour: Int): ThemeSpec {
        val id = if (autoSeason) seasonOf(today) else themeId.coerceIn(0, 9)
        return when (id) {
            0 -> light(0, "Hoa đào", 0xFFFFF3EF, 0xFFFFFFFF, 0xFF3B1E26, 0xFF7A5560, 0xFFD2456A, Pattern.BLOSSOM, 0xFFE9849E, 0.22f)
            1 -> light(1, "Sen hồng", 0xFFEDF6EC, 0xFFFFFFFF, 0xFF16321F, 0xFF4E6B55, 0xFF2F7D4A, Pattern.LOTUS, 0xFF5E9E6E, 0.25f)
            2 -> dark(2, "Trăng thu", 0xFF2E1D14, 0xFF3D2A1E, 0xFFFBEFDD, 0xFFCDB496, 0xFFF0A04B, Pattern.LEAVES, 0xFFE09A3A, 0.30f)
            3 -> light(3, "Sương tuyết", 0xFFEEF3F7, 0xFFFFFFFF, 0xFF1C2A39, 0xFF556577, 0xFF2F5F8A, Pattern.SNOW, 0xFF9FB6CB, 0.55f, bad = 0xFFB3261E)
            4 -> nguHanh(today)
            5 -> light(5, "Thái cực", 0xFFF4F1EA, 0xFFFFFFFF, 0xFF141414, 0xFF5B574F, 0xFF141414, Pattern.BAGUA, 0xFF141414, 0.10f,
                good = 0xFF2E6B3A, bad = 0xFFB3261E, onAccent = 0xFFF4F1EA)
            6 -> dark(6, "Năm ${today.yearCanChi} · con ${today.yearAnimal}", 0xFF5A1A12, 0xFF6E2419, 0xFFFFF3E6, 0xFFE8BFA8, 0xFFF2C46A,
                Pattern.COINS, 0xFFF2C46A, 0.16f, onAccent = 0xFF3A120C)
            7 -> thoiThan(hour)
            8 -> dark(8, "Sao ${today.tu}", 0xFF11143A, 0xFF1B1F4D, 0xFFEEF0FF, 0xFFA4A9D6, 0xFFF5D76E, Pattern.STARS, 0xFFFFFFFF, 0.55f,
                onAccent = 0xFF11143A)
            else -> if (today.hoangDao) {
                light(9, "${today.than12} · ngày hoàng đạo", 0xFFFBF6E9, 0xFFFFFDF6, 0xFF2B2414, 0xFF6B5E45, 0xFF8A6400, Pattern.DRUM, 0xFFA67C00, 0.13f)
            } else {
                dark(9, "${today.than12} · ngày hắc đạo", 0xFF1E1A14, 0xFF2B251C, 0xFFF3E9D2, 0xFFC9B999, 0xFFD4A52A, Pattern.DRUM, 0xFFD4A52A, 0.12f)
            }
        }
    }

    private fun nguHanh(d: DayInfo): ThemeSpec {
        val hanh = Almanac.HANH_CAN[d.dayCan]
        val v = "Ngày ${d.dayCanChi} · hành $hanh"
        return when (hanh) {
            "Mộc" -> light(4, v, 0xFFEAF4EA, 0xFFFFFFFF, 0xFF173A1E, 0xFF4A6650, 0xFF2E7D32, Pattern.WAVES, 0xFF2E7D32, 0.10f)
            "Hỏa" -> light(4, v, 0xFFFCEDE8, 0xFFFFFFFF, 0xFF3E1610, 0xFF7A4A40, 0xFFC62828, Pattern.WAVES, 0xFFC62828, 0.09f)
            "Thổ" -> light(4, v, 0xFFF6EEDC, 0xFFFFFDF7, 0xFF3A2A10, 0xFF6E5A38, 0xFF8A5E0E, Pattern.WAVES, 0xFF9A6B12, 0.10f)
            "Kim" -> light(4, v, 0xFFF5F3EE, 0xFFFFFFFF, 0xFF2A2A28, 0xFF5E5A52, 0xFF7A6838, Pattern.WAVES, 0xFF8C7A4B, 0.10f)
            else -> dark(4, v, 0xFF0E1B2C, 0xFF16273D, 0xFFE8F1FA, 0xFF93A7BC, 0xFF5FB0E6, Pattern.WAVES, 0xFF3E8EDE, 0.12f)
        }
    }

    private fun thoiThan(hour: Int): ThemeSpec {
        val chi = DayInfo.CHI[((hour + 1) / 2) % 12]
        return when (hour) {
            in 5..10 -> light(7, "Giờ $chi · buổi sáng", 0xFFFDF3E1, 0xFFFFFAF0, 0xFF3A2410, 0xFF7A5B3E, 0xFFB86E12, Pattern.SUNDIAL, 0xFFD98A1E, 0.13f)
            in 11..16 -> light(7, "Giờ $chi · buổi chiều", 0xFFF7E9D3, 0xFFFFF8EC, 0xFF3A2410, 0xFF7A5B3E, 0xFFB0620F, Pattern.SUNDIAL, 0xFFC8741C, 0.13f)
            in 17..22 -> dark(7, "Giờ $chi · buổi tối", 0xFF3A2440, 0xFF4A3152, 0xFFF8EAF2, 0xFFD7BFD0, 0xFFF0A86B, Pattern.SUNDIAL, 0xFFF0A86B, 0.12f)
            else -> dark(7, "Giờ $chi · ban đêm", 0xFF101A33, 0xFF1A2647, 0xFFE6ECFA, 0xFFA9B6D6, 0xFF8FB3FF, Pattern.SUNDIAL, 0xFF8FB3FF, 0.12f)
        }
    }
}
