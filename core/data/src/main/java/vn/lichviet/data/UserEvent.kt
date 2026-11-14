package vn.lichviet.data

import org.json.JSONObject

/**
 * Sự kiện của người dùng: sinh nhật, ngày giỗ, kỷ niệm...
 * [isLunar] = true thì [day]/[month] là ngày âm lịch, lặp lại hằng năm theo âm lịch.
 * [year] = 0 nghĩa là lặp hằng năm; khác 0 là sự kiện một lần.
 */
data class UserEvent(
    val id: Long,
    val title: String,
    val day: Int,
    val month: Int,
    val year: Int = 0,
    val isLunar: Boolean = true,
    val remindDaysBefore: Int = 1,
    val note: String = "",
) {
    val repeatsYearly: Boolean get() = year == 0

    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("title", title).put("day", day).put("month", month)
        .put("year", year).put("isLunar", isLunar).put("remind", remindDaysBefore).put("note", note)

    companion object {
        fun fromJson(o: JSONObject) = UserEvent(
            id = o.getLong("id"),
            title = o.getString("title"),
            day = o.getInt("day"),
            month = o.getInt("month"),
            year = o.optInt("year", 0),
            isLunar = o.optBoolean("isLunar", true),
            remindDaysBefore = o.optInt("remind", 1),
            note = o.optString("note", ""),
        )
    }
}
