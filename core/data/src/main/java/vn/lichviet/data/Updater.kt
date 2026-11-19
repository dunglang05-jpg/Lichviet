package vn.lichviet.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Tự cập nhật qua mạng (OTA) từ bản phát hành "latest" trên GitHub. */
object Updater {
    private const val BASE = "https://github.com/dunglang05-jpg/Lichviet/releases/download/latest/"
    const val DAY = 24 * 60 * 60 * 1000L

    data class Release(val code: Int, val name: String, val notes: String, val apk: String)

    @Suppress("DEPRECATION")
    fun current(ctx: Context): Pair<Int, String> {
        val pi = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        val code = if (Build.VERSION.SDK_INT >= 28) pi.longVersionCode.toInt() else pi.versionCode
        return code to (pi.versionName ?: "?")
    }

    /** Lần kiểm tra tự động gần nhất đã quá 1 ngày chưa. */
    fun due(ctx: Context): Boolean {
        val p = ctx.getSharedPreferences("update", Context.MODE_PRIVATE)
        return System.currentTimeMillis() - p.getLong("checkedAt", 0) > DAY
    }

    /** Trả về bản mới nếu có (versionCode lớn hơn bản đang cài), null nếu đã mới nhất. Ném lỗi nếu mất mạng. */
    suspend fun check(ctx: Context): Release? = withContext(Dispatchers.IO) {
        val c = URL(BASE + "version.json?t=" + System.currentTimeMillis()).openConnection() as HttpURLConnection
        c.connectTimeout = 10000; c.readTimeout = 10000
        val o = try { JSONObject(c.inputStream.bufferedReader().readText()) } finally { c.disconnect() }
        ctx.getSharedPreferences("update", Context.MODE_PRIVATE).edit().putLong("checkedAt", System.currentTimeMillis()).apply()
        val r = Release(o.getInt("versionCode"), o.optString("versionName"), o.optString("notes"), BASE + o.optString("apk", "LichViet.apk"))
        if (r.code > current(ctx).first) r else null
    }

    /** Tải APK vào bộ nhớ đệm, báo tiến độ 0..1. */
    suspend fun download(ctx: Context, r: Release, progress: (Float) -> Unit): File = withContext(Dispatchers.IO) {
        val dir = File(ctx.cacheDir, "update").apply { mkdirs() }
        val f = File(dir, "LichViet-${r.code}.apk")
        val tmp = File(dir, "dl.part")
        val c = URL(r.apk).openConnection() as HttpURLConnection
        c.connectTimeout = 15000; c.readTimeout = 30000
        try {
            val total = c.contentLengthLong
            c.inputStream.use { inp ->
                tmp.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024); var done = 0L
                    while (true) {
                        val n = inp.read(buf); if (n < 0) break
                        out.write(buf, 0, n); done += n
                        if (total > 0) progress(done.toFloat() / total)
                    }
                }
            }
        } finally { c.disconnect() }
        dir.listFiles()?.filter { it.name.endsWith(".apk") }?.forEach { it.delete() }
        tmp.renameTo(f)
        f
    }

    fun canInstall(ctx: Context) = Build.VERSION.SDK_INT < 26 || ctx.packageManager.canRequestPackageInstalls()

    /** Mở màn hình cho phép "cài ứng dụng không rõ nguồn" cho Lịch Việt. */
    fun openInstallPermission(ctx: Context) {
        if (Build.VERSION.SDK_INT >= 26) runCatching {
            ctx.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun install(ctx: Context, apk: File) {
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", apk)
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
