package vn.lichviet.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Cài đặt ứng dụng. [themeId] 0..9 dành cho 10 giao diện sẽ làm sau. */
data class AppSettings(
    val themeId: Int = 0,
    val autoSeason: Boolean = true,
    /** Năm sinh âm lịch của người dùng (0 = chưa nhập), dùng cho chỉ số hợp tuổi. */
    val birthYear: Int = 0,
    val userName: String = "",
    val mondayFirst: Boolean = true,
    val morningNotify: Boolean = true,
    val notifyHour: Int = 7,
    val remindSocVong: Boolean = true,
    val remindHolidays: Boolean = true,
    /** Bộ tranh lịch tháng: 0 = đồng quê màu nước, 1 = Đông Hồ. */
    val artSet: Int = 0,
)

class SettingsRepository private constructor(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val _settings = MutableStateFlow(read())
    val settings: StateFlow<AppSettings> = _settings

    private fun read() = AppSettings(
        themeId = prefs.getInt("themeId", 0),
        autoSeason = prefs.getBoolean("autoSeason", true),
        birthYear = prefs.getInt("birthYear", 0),
        userName = prefs.getString("userName", "") ?: "",
        mondayFirst = prefs.getBoolean("mondayFirst", true),
        morningNotify = prefs.getBoolean("morningNotify", true),
        notifyHour = prefs.getInt("notifyHour", 7),
        remindSocVong = prefs.getBoolean("remindSocVong", true),
        remindHolidays = prefs.getBoolean("remindHolidays", true),
        artSet = prefs.getInt("artSet", 0),
    )

    fun update(transform: (AppSettings) -> AppSettings) {
        val s = transform(_settings.value)
        prefs.edit()
            .putInt("themeId", s.themeId.coerceIn(0, 9))
            .putBoolean("autoSeason", s.autoSeason)
            .putInt("birthYear", s.birthYear)
            .putString("userName", s.userName)
            .putBoolean("mondayFirst", s.mondayFirst)
            .putBoolean("morningNotify", s.morningNotify)
            .putInt("notifyHour", s.notifyHour.coerceIn(0, 23))
            .putBoolean("remindSocVong", s.remindSocVong)
            .putBoolean("remindHolidays", s.remindHolidays)
            .putInt("artSet", s.artSet)
            .apply()
        _settings.value = s
    }

    companion object {
        @Volatile private var instance: SettingsRepository? = null
        fun get(context: Context): SettingsRepository =
            instance ?: synchronized(this) { instance ?: SettingsRepository(context).also { instance = it } }
    }
}
