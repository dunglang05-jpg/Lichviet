package vn.lichviet.reminder

import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.data.AppSettings
import vn.lichviet.data.UserEvent
import vn.lichviet.data.EventRepository

/** Tạo nội dung thông báo cho một ngày (tách riêng, không phụ thuộc Android, dễ kiểm thử/mở rộng). */
object ReminderContent {
    data class Note(val id: Int, val title: String, val text: String)

    fun build(today: DayInfo, settings: AppSettings, events: List<UserEvent>): List<Note> {
        val out = mutableListOf<Note>()
        if (settings.morningNotify) {
            val hol = today.holidays.joinToString(" · ") { it.name }
            out += Note(
                1,
                "${DayInfo.THU[today.weekday]} ${today.solarDay}/${today.solarMonth} · Âm lịch ${today.lunarText()}",
                buildString {
                    append("${today.dayCanChi} · ${today.verdict} (${today.score}/5)")
                    append("\nGiờ tốt: ${today.goodHoursText()}")
                    val al = today.almanac()
                    append("\nLục diệu: ${al.khongMinh} · Sao tốt ${al.goodStars.size}, sao xấu ${al.badStars.size}")
                    if (al.kyNhat.isNotEmpty()) append("\nKỵ: " + al.kyNhat.joinToString(", ") { it.name })
                    if (hol.isNotEmpty()) append("\n$hol")
                },
            )
        }
        val tomorrowJd = today.jd + 1
        val t = LunarCalendar.jdToDate(tomorrowJd)
        val tomorrow = DayInfo.of(t[0], t[1], t[2])
        if (settings.remindSocVong && (tomorrow.lunarDay == 1 || tomorrow.lunarDay == 15)) {
            out += Note(
                2,
                if (tomorrow.lunarDay == 1) "Ngày mai mùng 1 âm lịch" else "Ngày mai là ngày Rằm",
                "${DayInfo.THU[tomorrow.weekday]} ${tomorrow.solarDay}/${tomorrow.solarMonth} · ${tomorrow.lunarFull()}",
            )
        }
        if (settings.remindHolidays) {
            tomorrow.holidays.filter { it.major }.forEach {
                out += Note(3, "Ngày mai: ${it.name}", "${tomorrow.solarDay}/${tomorrow.solarMonth} · âm lịch ${tomorrow.lunarText()}")
            }
        }
        events.forEach { e ->
            val jd = EventRepository.nextOccurrence(e, today.solarDay, today.solarMonth, today.solarYear)
            if (jd < 0) return@forEach
            val left = jd - today.jd
            if (left == 0 || left == e.remindDaysBefore) {
                val d = LunarCalendar.jdToDate(jd)
                out += Note(
                    (1000 + (e.id % 100000)).toInt(),
                    if (left == 0) "Hôm nay: ${e.title}" else "Còn $left ngày: ${e.title}",
                    "${d[0]}/${d[1]}/${d[2]}" + (if (e.isLunar) " (ngày ${e.day}/${e.month} âm lịch)" else "") +
                        (if (e.note.isNotBlank()) "\n${e.note}" else ""),
                )
            }
        }
        return out
    }
}
