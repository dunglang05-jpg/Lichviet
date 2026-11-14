package vn.lichviet.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import vn.lichviet.core.Countdown
import vn.lichviet.core.LunarCalendar

/** Lưu sự kiện người dùng (JSON trong SharedPreferences). Có thể thay bằng Room sau này mà không đổi API. */
class EventRepository private constructor(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("events", Context.MODE_PRIVATE)
    private val _events = MutableStateFlow(load())
    val events: StateFlow<List<UserEvent>> = _events

    private fun load(): List<UserEvent> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { UserEvent.fromJson(arr.getJSONObject(it)) }
        }.getOrDefault(emptyList())
    }

    private fun save(list: List<UserEvent>) {
        val arr = JSONArray()
        list.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(KEY, arr.toString()).apply()
        _events.value = list
    }

    fun upsert(event: UserEvent) {
        val list = _events.value.filterNot { it.id == event.id } + event
        save(list)
    }

    fun delete(id: Long) = save(_events.value.filterNot { it.id == id })

    fun newId(): Long = System.currentTimeMillis()

    /** Sự kiện kèm ngày dương (JDN) lần tới, sắp xếp gần nhất trước. */
    fun upcoming(fromD: Int, fromM: Int, fromY: Int): List<Pair<UserEvent, Int>> {
        val fromJd = LunarCalendar.jdFromDate(fromD, fromM, fromY)
        return _events.value.mapNotNull { e ->
            val jd = nextOccurrence(e, fromD, fromM, fromY)
            if (jd >= fromJd) e to jd else null
        }.sortedBy { it.second }
    }

    companion object {
        private const val KEY = "list"

        @Volatile private var instance: EventRepository? = null
        fun get(context: Context): EventRepository =
            instance ?: synchronized(this) { instance ?: EventRepository(context).also { instance = it } }

        /** JDN lần tới của sự kiện tính từ ngày cho trước; -1 nếu sự kiện một lần đã qua. */
        fun nextOccurrence(e: UserEvent, fromD: Int, fromM: Int, fromY: Int): Int = when {
            e.isLunar && e.repeatsYearly -> Countdown.nextLunarYearly(e.day, e.month, fromD, fromM, fromY)
            e.isLunar -> Countdown.lunarToJd(e.day, e.month, e.year)
            e.repeatsYearly -> Countdown.nextSolarYearly(e.day, e.month, fromD, fromM, fromY)
            else -> LunarCalendar.jdFromDate(e.day, e.month, e.year)
        }
    }
}
