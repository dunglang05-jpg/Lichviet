package vn.lichviet.ui.theme

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
    /** Màu đối ứng (tương phản sắc độ, cùng độ đậm) – dùng cho hoa văn xen kẽ, chỉ báo, nhãn phụ. */
    val accent2: Color,
    val onAccent2: Color,
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

    /** Màu xem trước (nền, nhấn chính, nhấn đối ứng) cho màn chọn giao diện. */
    val SWATCH = listOf(
        Triple(0xFFFFF4F1, 0xFFB8174F, 0xFF1F6E4E), Triple(0xFFEEF6EF, 0xFF23703F, 0xFFC2185B),
        Triple(0xFF2A1A12, 0xFFF0A04B, 0xFF8CC4DE), Triple(0xFFEEF3F7, 0xFF2B5A86, 0xFFB3261E),
        Triple(0xFF0E1B2C, 0xFF5FB0E6, 0xFF7CCB8A), Triple(0xFFF4F1EA, 0xFF141414, 0xFFB3261E),
        Triple(0xFF5A1A12, 0xFFF2C46A, 0xFF7FD3A6), Triple(0xFFF7E9D3, 0xFFA35A0D, 0xFF6D3F8F),
        Triple(0xFF11143A, 0xFFF5D76E, 0xFF7FD8E8), Triple(0xFFFBF6E9, 0xFF7A5800, 0xFFA6262A),
    ).map { Triple(Color(it.first), Color(it.second), Color(it.third)) }

    /** Hoa văn của từng giao diện (cho màn chọn giao diện). */
    val PATTERNS = listOf(Pattern.BLOSSOM, Pattern.LOTUS, Pattern.LEAVES, Pattern.SNOW, Pattern.WAVES,
        Pattern.BAGUA, Pattern.COINS, Pattern.SUNDIAL, Pattern.STARS, Pattern.DRUM)

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
        id: Int, variant: String, bg: Long, card: Long, ink: Long, dim: Long, accent: Long, accent2: Long,
        pattern: Pattern, patternColor: Long, alpha: Float,
        good: Long = 0xFF2E7D4F, bad: Long = 0xFFB42318, onAccent: Long = 0xFFFFFFFF, onAccent2: Long = 0xFFFFFFFF,
    ) = ThemeSpec(id, GROUPS[id], NAMES[id], variant, false, c(bg), c(card), c(ink), c(dim), c(accent), c(onAccent),
        c(accent2), c(onAccent2), c(good), c(bad), pattern, c(patternColor), alpha)

    private fun dark(
        id: Int, variant: String, bg: Long, card: Long, ink: Long, dim: Long, accent: Long, accent2: Long,
        pattern: Pattern, patternColor: Long, alpha: Float, onAccent: Long, onAccent2: Long,
        good: Long = 0xFF9BD38A, bad: Long = 0xFFFF8A78,
    ) = ThemeSpec(id, GROUPS[id], NAMES[id], variant, true, c(bg), c(card), c(ink), c(dim), c(accent), c(onAccent),
        c(accent2), c(onAccent2), c(good), c(bad), pattern, c(patternColor), alpha)

    /**
     * @param themeId 0..9
     * @param autoSeason true: bỏ qua themeId, dùng giao diện mùa hiện tại
     * @param hour giờ hiện tại 0..23 (giờ VN), dùng cho Thời Thần
     */
    fun resolve(themeId: Int, autoSeason: Boolean, today: DayInfo, hour: Int): ThemeSpec {
        val id = if (autoSeason) seasonOf(today) else themeId.coerceIn(0, 9)
        return when (id) {
            // Mùa: mỗi mùa một cặp màu đối ứng (hoa – lá, nắng – trời, ...)
            0 -> light(0, "Hoa đào · lộc biếc", 0xFFFFF4F1, 0xFFFFFFFF, 0xFF3B1E26, 0xFF6E4A55, 0xFFB8174F, 0xFF1F6E4E,
                Pattern.BLOSSOM, 0xFFE9849E, 0.22f)
            1 -> light(1, "Sen hồng · lá biếc", 0xFFEEF6EF, 0xFFFFFFFF, 0xFF12301D, 0xFF46624F, 0xFF23703F, 0xFFC2185B,
                Pattern.LOTUS, 0xFF5E9E6E, 0.25f)
            2 -> dark(2, "Lá vàng · trời thu", 0xFF2A1A12, 0xFF3A281C, 0xFFFBEFDD, 0xFFD3BC9F, 0xFFF0A04B, 0xFF8CC4DE,
                Pattern.LEAVES, 0xFFE09A3A, 0.30f, onAccent = 0xFF2A1A12, onAccent2 = 0xFF10202A)
            3 -> light(3, "Sương lam · hoa đỏ", 0xFFEEF3F7, 0xFFFFFFFF, 0xFF1C2A39, 0xFF4F6072, 0xFF2B5A86, 0xFFB3261E,
                Pattern.SNOW, 0xFF9FB6CB, 0.55f, bad = 0xFFB3261E)
            4 -> nguHanh(today)
            5 -> light(5, "Thái cực · ấn son", 0xFFF4F1EA, 0xFFFFFFFF, 0xFF141414, 0xFF58544C, 0xFF141414, 0xFFB3261E,
                Pattern.BAGUA, 0xFF141414, 0.10f, good = 0xFF2E6B3A, bad = 0xFFB3261E, onAccent = 0xFFF4F1EA)
            6 -> dark(6, "Năm ${today.yearCanChi} · con ${today.yearAnimal}", 0xFF5A1A12, 0xFF6E2419, 0xFFFFF3E6, 0xFFEBC6B0,
                0xFFF2C46A, 0xFF7FD3A6, Pattern.COINS, 0xFFF2C46A, 0.16f, onAccent = 0xFF3A120C, onAccent2 = 0xFF12301F,
                good = 0xFFA8E39A, bad = 0xFFFFB4A6)
            7 -> thoiThan(hour)
            8 -> dark(8, "Sao ${today.tu}", 0xFF11143A, 0xFF1B1F4D, 0xFFEEF0FF, 0xFFA9AEDB, 0xFFF5D76E, 0xFF7FD8E8,
                Pattern.STARS, 0xFFFFFFFF, 0.55f, onAccent = 0xFF11143A, onAccent2 = 0xFF0B2A30)
            else -> if (today.hoangDao) {
                light(9, "${today.than12} · ngày hoàng đạo", 0xFFFBF6E9, 0xFFFFFDF6, 0xFF2B2414, 0xFF665940, 0xFF7A5800, 0xFFA6262A,
                    Pattern.DRUM, 0xFFA67C00, 0.13f)
            } else {
                dark(9, "${today.than12} · ngày hắc đạo", 0xFF1E1A14, 0xFF2B251C, 0xFFF3E9D2, 0xFFCBBB9C, 0xFFD4A52A, 0xFFE58B6E,
                    Pattern.DRUM, 0xFFD4A52A, 0.12f, onAccent = 0xFF1E1A14, onAccent2 = 0xFF2A140C)
            }
        }
    }

    /** Ngũ Hành: màu chính là hành của can ngày, màu đối ứng là hành nó sinh ra (tương sinh). */
    private fun nguHanh(d: DayInfo): ThemeSpec {
        val hanh = Almanac.HANH_CAN[d.dayCan]
        val v = "Ngày ${d.dayCanChi} · hành $hanh"
        return when (hanh) {
            "Mộc" -> light(4, "$v sinh Hỏa", 0xFFEAF4EA, 0xFFFFFFFF, 0xFF173A1E, 0xFF46624C, 0xFF2E7D32, 0xFFC62828, Pattern.WAVES, 0xFF2E7D32, 0.10f)
            "Hỏa" -> light(4, "$v sinh Thổ", 0xFFFCEDE8, 0xFFFFFFFF, 0xFF3E1610, 0xFF74463C, 0xFFC62828, 0xFF8A5E0E, Pattern.WAVES, 0xFFC62828, 0.09f)
            "Thổ" -> light(4, "$v sinh Kim", 0xFFF6EEDC, 0xFFFFFDF7, 0xFF3A2A10, 0xFF6A5634, 0xFF8A5E0E, 0xFF5B6470, Pattern.WAVES, 0xFF9A6B12, 0.10f)
            "Kim" -> light(4, "$v sinh Thủy", 0xFFF3F3F0, 0xFFFFFFFF, 0xFF2A2A28, 0xFF5A5750, 0xFF6B5B2E, 0xFF1F5F9E, Pattern.WAVES, 0xFF8C7A4B, 0.10f)
            else -> dark(4, "$v sinh Mộc", 0xFF0E1B2C, 0xFF16273D, 0xFFE8F1FA, 0xFFA3B5C8, 0xFF5FB0E6, 0xFF7CCB8A, Pattern.WAVES, 0xFF3E8EDE, 0.12f,
                onAccent = 0xFF0E1B2C, onAccent2 = 0xFF0E1B2C)
        }
    }

    private fun thoiThan(hour: Int): ThemeSpec {
        val chi = DayInfo.CHI[((hour + 1) / 2) % 12]
        return when (hour) {
            in 5..10 -> light(7, "Giờ $chi · buổi sáng", 0xFFFDF3E1, 0xFFFFFAF0, 0xFF3A2410, 0xFF74563A, 0xFFA85F0C, 0xFF2F6F8F, Pattern.SUNDIAL, 0xFFD98A1E, 0.13f)
            in 11..16 -> light(7, "Giờ $chi · buổi chiều", 0xFFF7E9D3, 0xFFFFF8EC, 0xFF3A2410, 0xFF74563A, 0xFFA35A0D, 0xFF6D3F8F, Pattern.SUNDIAL, 0xFFC8741C, 0.13f)
            in 17..22 -> dark(7, "Giờ $chi · buổi tối", 0xFF3A2440, 0xFF4A3152, 0xFFF8EAF2, 0xFFDCC6D6, 0xFFF0A86B, 0xFF8FD0C8, Pattern.SUNDIAL, 0xFFF0A86B, 0.12f,
                onAccent = 0xFF2A1830, onAccent2 = 0xFF16302C)
            else -> dark(7, "Giờ $chi · ban đêm", 0xFF101A33, 0xFF1A2647, 0xFFE6ECFA, 0xFFAEBBDA, 0xFF8FB3FF, 0xFFF2C46A, Pattern.SUNDIAL, 0xFF8FB3FF, 0.12f,
                onAccent = 0xFF101A33, onAccent2 = 0xFF2A2208)
        }
    }
}
