package vn.lichviet.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import vn.lichviet.data.Today

/** Lên lịch công việc hằng ngày vào giờ người dùng chọn (WorkManager tự khôi phục sau khi khởi động lại máy). */
object ReminderScheduler {
    private const val WORK = "daily_reminder"

    /** Hook cho module khác (vd. widget) muốn chạy kèm lúc worker chạy. */
    var onDailyRun: ((Context) -> Unit)? = null

    fun schedule(ctx: Context, hour: Int) {
        val now = Today.calendar()
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        val req = PeriodicWorkRequestBuilder<DailyWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(next.timeInMillis - now.timeInMillis, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.UPDATE, req)
    }

    fun cancel(ctx: Context) {
        WorkManager.getInstance(ctx).cancelUniqueWork(WORK)
    }
}
