package vn.lichviet.data

import java.util.Calendar
import java.util.TimeZone
import vn.lichviet.core.DayInfo

/** Ngày hiện tại theo giờ Việt Nam (âm lịch luôn tính theo UTC+7). */
object Today {
    private val tz: TimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    fun calendar(): Calendar = Calendar.getInstance(tz)

    /** {ngày, tháng, năm} */
    fun ymd(): IntArray {
        val c = calendar()
        return intArrayOf(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))
    }

    fun info(): DayInfo = ymd().let { DayInfo.of(it[0], it[1], it[2]) }

    /** Mili-giây tới 00:00:05 ngày mai (giờ VN). */
    fun millisToNextMidnight(): Long {
        val now = calendar()
        val next = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 5); set(Calendar.MILLISECOND, 0)
        }
        return next.timeInMillis - now.timeInMillis
    }
}
