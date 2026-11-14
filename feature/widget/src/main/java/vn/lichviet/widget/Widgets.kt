package vn.lichviet.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.RemoteViews
import vn.lichviet.core.DayInfo
import vn.lichviet.core.MonthGrid
import vn.lichviet.data.EventRepository
import vn.lichviet.data.SettingsRepository
import vn.lichviet.data.Today

/** Cơ sở chung: mỗi widget chỉ cần tự dựng RemoteViews của mình. */
abstract class BaseWidget : AppWidgetProvider() {
    abstract fun build(ctx: Context, today: DayInfo): RemoteViews

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        val today = Today.info()
        ids.forEach { mgr.updateAppWidget(it, build(ctx, today).also { v -> WidgetUpdater.attachClick(ctx, v) }) }
        WidgetUpdater.scheduleMidnight(ctx)
    }
}

class DayWidget : BaseWidget() {
    override fun build(ctx: Context, today: DayInfo) = RemoteViews(ctx.packageName, R.layout.w_day).apply {
        setTextViewText(R.id.weekday, DayInfo.THU[today.weekday].uppercase())
        setTextViewText(R.id.solarDay, today.solarDay.toString())
        setTextViewText(R.id.monthYear, "Tháng ${today.solarMonth}, ${today.solarYear}")
        setTextViewText(R.id.lunar, "${today.lunarText()} âm lịch")
        setTextViewText(R.id.canchi, "Ngày ${today.dayCanChi}")
    }
}

class DetailWidget : BaseWidget() {
    override fun build(ctx: Context, today: DayInfo) = RemoteViews(ctx.packageName, R.layout.w_detail).apply {
        setTextViewText(R.id.weekday, DayInfo.THU[today.weekday].uppercase())
        setTextViewText(R.id.solarDay, today.solarDay.toString())
        setTextViewText(R.id.lunar, today.lunarText())
        setTextViewText(R.id.title, "${today.dayCanChi} · ${if (today.hoangDao) "Hoàng đạo" else "Hắc đạo"}")
        setTextViewText(R.id.verdict, "${today.verdict} ${today.starsText()} · ${today.almanac().khongMinh}")
        setTextViewText(R.id.hours, "Giờ tốt: " + today.hours.filter { it.good }.joinToString(", ") { it.name })
        setTextViewText(R.id.nen, "Nên: " + today.nenLam.take(3).joinToString(", "))
        val hol = today.holidays.firstOrNull()?.name
        val ev = EventRepository.get(ctx).upcoming(today.solarDay, today.solarMonth, today.solarYear).firstOrNull()
        val line = hol ?: ev?.let { (e, jd) ->
            val left = jd - today.jd
            if (left == 0) "Hôm nay: ${e.title}" else "Còn $left ngày: ${e.title}"
        } ?: "Tiết ${today.tietKhiCurrent}"
        setTextViewText(R.id.event, line)
    }
}

class MonthWidget : BaseWidget() {
    override fun build(ctx: Context, today: DayInfo): RemoteViews {
        val monday = SettingsRepository.get(ctx).settings.value.mondayFirst
        val grid = MonthGrid(today.solarMonth, today.solarYear, monday)
        val v = RemoteViews(ctx.packageName, R.layout.w_month)
        v.setTextViewText(R.id.title, "Tháng ${today.solarMonth} · ${today.solarYear}")
        v.setTextViewText(R.id.subtitle, grid.lunarRange())
        val heads = if (monday) listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN") else listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
        heads.forEachIndexed { i, h -> v.setTextViewText(HEADS[i], h) }
        for (i in 0 until 42) {
            val d = grid.cells[i]
            val lunar = if (d.lunarDay == 1) "${d.lunarDay}/${d.lunarMonth}" else d.lunarDay.toString()
            val text = "${d.solarDay}\n$lunar"
            val s = SpannableString(text)
            val split = d.solarDay.toString().length
            val isToday = d.jd == today.jd
            val base = when {
                isToday -> Color.WHITE
                !grid.inMonth[i] -> Color.parseColor("#B0B0B0")
                d.weekday == 0 || d.holidays.any { it.major } -> Color.parseColor("#B3261E")
                else -> Color.parseColor("#1F1F1F")
            }
            s.setSpan(ForegroundColorSpan(base), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s.setSpan(StyleSpan(Typeface.BOLD), 0, split, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s.setSpan(RelativeSizeSpan(0.62f), split, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            v.setTextViewText(CELLS[i], s)
            v.setInt(CELLS[i], "setBackgroundResource", if (isToday) R.drawable.w_today else 0)
        }
        return v
    }

    companion object {
        private val HEADS = intArrayOf(R.id.h0, R.id.h1, R.id.h2, R.id.h3, R.id.h4, R.id.h5, R.id.h6)
        private val CELLS = intArrayOf(
            R.id.c0, R.id.c1, R.id.c2, R.id.c3, R.id.c4, R.id.c5, R.id.c6,
            R.id.c7, R.id.c8, R.id.c9, R.id.c10, R.id.c11, R.id.c12, R.id.c13,
            R.id.c14, R.id.c15, R.id.c16, R.id.c17, R.id.c18, R.id.c19, R.id.c20,
            R.id.c21, R.id.c22, R.id.c23, R.id.c24, R.id.c25, R.id.c26, R.id.c27,
            R.id.c28, R.id.c29, R.id.c30, R.id.c31, R.id.c32, R.id.c33, R.id.c34,
            R.id.c35, R.id.c36, R.id.c37, R.id.c38, R.id.c39, R.id.c40, R.id.c41,
        )
    }
}

/** Cập nhật lại mọi widget khi đổi ngày/giờ/múi giờ hoặc tới nửa đêm. */
class WidgetRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) = WidgetUpdater.updateAll(ctx)
}

object WidgetUpdater {
    private const val ACTION_MIDNIGHT = "vn.lichviet.widget.MIDNIGHT"
    private val providers = listOf(DayWidget::class.java, DetailWidget::class.java, MonthWidget::class.java)

    /** Gọi từ app khi dữ liệu thay đổi (thêm sự kiện, đổi cài đặt...). */
    fun updateAll(ctx: Context) {
        val mgr = AppWidgetManager.getInstance(ctx)
        providers.forEach { cls ->
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, cls))
            if (ids.isNotEmpty()) {
                val i = Intent(ctx, cls).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                ctx.sendBroadcast(i)
            }
        }
        scheduleMidnight(ctx)
    }

    fun attachClick(ctx: Context, v: RemoteViews) {
        val launch = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName) ?: return
        val pi = PendingIntent.getActivity(ctx, 0, launch, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        v.setOnClickPendingIntent(R.id.root, pi)
    }

    fun scheduleMidnight(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        val pi = PendingIntent.getBroadcast(
            ctx, 77,
            Intent(ctx, WidgetRefreshReceiver::class.java).setAction(ACTION_MIDNIGHT),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        am.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + Today.millisToNextMidnight(),
            pi,
        )
    }

}
