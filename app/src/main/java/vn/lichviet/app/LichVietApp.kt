package vn.lichviet.app

import vn.lichviet.ui.*

import android.app.Application
import vn.lichviet.data.SettingsRepository
import vn.lichviet.reminder.ReminderScheduler
import vn.lichviet.widget.WidgetUpdater

class LichVietApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Nối các module: mỗi lần nhắc nhở chạy thì làm mới widget.
        ReminderScheduler.onDailyRun = { WidgetUpdater.updateAll(it) }
        ReminderScheduler.schedule(this, SettingsRepository.get(this).settings.value.notifyHour)
        WidgetUpdater.scheduleMidnight(this)
    }
}
