package vn.lichviet.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import vn.lichviet.data.EventRepository
import vn.lichviet.data.SettingsRepository
import vn.lichviet.data.Today

/** Chạy mỗi sáng: đăng thông báo lịch ngày, mùng 1/rằm, ngày lễ và sự kiện cá nhân. */
class DailyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val notes = ReminderContent.build(
            Today.info(),
            SettingsRepository.get(ctx).settings.value,
            EventRepository.get(ctx).events.value,
        )
        notes.forEach { post(ctx, it) }
        ReminderScheduler.onDailyRun?.invoke(ctx)
        return Result.success()
    }

    companion object {
        const val CHANNEL = "lich_hang_ngay"

        fun ensureChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= 26) {
                val nm = ctx.getSystemService(NotificationManager::class.java)
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL, "Lịch hằng ngày & nhắc nhở", NotificationManager.IMPORTANCE_DEFAULT),
                )
            }
        }

        fun post(ctx: Context, note: ReminderContent.Note) {
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) return
            ensureChannel(ctx)
            val launch = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
            val pi = launch?.let {
                android.app.PendingIntent.getActivity(
                    ctx, note.id, it,
                    android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT,
                )
            }
            val n = NotificationCompat.Builder(ctx, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle(note.title)
                .setContentText(note.text.lineSequence().first())
                .setStyle(NotificationCompat.BigTextStyle().bigText(note.text))
                .setAutoCancel(true)
                .apply { if (pi != null) setContentIntent(pi) }
                .build()
            NotificationManagerCompat.from(ctx).notify(note.id, n)
        }
    }
}
