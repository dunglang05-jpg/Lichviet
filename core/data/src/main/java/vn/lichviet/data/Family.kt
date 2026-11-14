package vn.lichviet.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

/** Thành viên gia đình / gia phả. Ngày sinh dương lịch (0 = không rõ); ngày mất âm lịch để nhắc giỗ. */
data class Member(
    val id: Long,
    val name: String,
    val relation: String = "",
    val male: Boolean = true,
    val birthD: Int = 0,
    val birthM: Int = 0,
    val birthY: Int = 0,
    val deceased: Boolean = false,
    val deathLunarD: Int = 0,
    val deathLunarM: Int = 0,
    val deathYear: Int = 0,
    val parentId: Long = 0,
    val spouseOf: Long = 0,
    val note: String = "",
) {
    val hasBirth: Boolean get() = birthD > 0 && birthM > 0 && birthY > 0
    val hasGio: Boolean get() = deceased && deathLunarD > 0 && deathLunarM > 0

    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("name", name).put("relation", relation).put("male", male)
        .put("bd", birthD).put("bm", birthM).put("by", birthY)
        .put("dead", deceased).put("dd", deathLunarD).put("dm", deathLunarM).put("dy", deathYear)
        .put("parent", parentId).put("spouse", spouseOf).put("note", note)

    companion object {
        fun fromJson(o: JSONObject) = Member(
            o.getLong("id"), o.optString("name"), o.optString("relation"), o.optBoolean("male", true),
            o.optInt("bd"), o.optInt("bm"), o.optInt("by"),
            o.optBoolean("dead"), o.optInt("dd"), o.optInt("dm"), o.optInt("dy"),
            o.optLong("parent"), o.optLong("spouse"), o.optString("note"),
        )
    }
}

class FamilyRepository private constructor(private val context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("family", Context.MODE_PRIVATE)
    private val _members = MutableStateFlow(load())
    val members: StateFlow<List<Member>> = _members

    private fun load(): List<Member> = runCatching {
        val a = JSONArray(prefs.getString("list", "[]"))
        (0 until a.length()).map { Member.fromJson(a.getJSONObject(it)) }
    }.getOrDefault(emptyList())

    private fun save(list: List<Member>) {
        val a = JSONArray()
        list.forEach { a.put(it.toJson()) }
        prefs.edit().putString("list", a.toString()).apply()
        _members.value = list
    }

    /** Lưu thành viên; nếu đã mất và có ngày giỗ thì tạo/cập nhật sự kiện "Giỗ ..." âm lịch hằng năm để được nhắc. */
    fun upsert(m: Member) {
        save(_members.value.filterNot { it.id == m.id } + m)
        val events = EventRepository.get(context)
        val eventId = GIO_ID_BASE + m.id
        if (m.hasGio) {
            events.upsert(UserEvent(eventId, "Giỗ ${m.relation.ifBlank { "" }} ${m.name}".replace("  ", " ").trim(),
                m.deathLunarD, m.deathLunarM, 0, isLunar = true, remindDaysBefore = 3, note = "Ngày giỗ"))
        } else events.delete(eventId)
    }

    fun delete(id: Long) {
        save(_members.value.filterNot { it.id == id }.map { if (it.parentId == id) it.copy(parentId = 0) else it })
        EventRepository.get(context).delete(GIO_ID_BASE + id)
    }

    fun newId(): Long = System.currentTimeMillis()

    companion object {
        /** Sự kiện giỗ có id riêng để không trùng sự kiện người dùng tạo. */
        const val GIO_ID_BASE = 7_000_000_000_000L
        @Volatile private var instance: FamilyRepository? = null
        fun get(context: Context): FamilyRepository =
            instance ?: synchronized(this) { instance ?: FamilyRepository(context.applicationContext).also { instance = it } }
    }
}
