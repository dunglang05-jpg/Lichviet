package vn.lichviet.ui

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray

/** Một bộ tranh lịch tháng: 12 ảnh trong assets/art/<folder>/01..12.jpg. */
data class ArtSet(val id: Int, val name: String, val folder: String, val fit: Boolean, val titles: List<String>, val credit: String)

object MonthArt {
    val SETS = listOf(
        ArtSet(0, "Bốn mùa quê hương", "dongque", false, listOf(
            "Sáng mùng Một Tết", "Vật làng hội xuân", "Cấy lúa mưa xuân", "Giỗ Tổ Hùng Vương",
            "Gặt chiêm", "Tết Đoan Ngọ", "Tát nước mưa ngâu", "Lễ Vu Lan",
            "Đêm Trung thu", "Tết cơm mới", "Chớm đông, cúc họa mi", "Chợ Tết, ông đồ",
        ), "Tranh vẽ bằng AI (ChatGPT)"),
        ArtSet(1, "Tranh Đông Hồ", "dongho", true, emptyList(), "Wikimedia Commons"),
    )

    fun set(id: Int) = SETS.getOrElse(id) { SETS[0] }

    private val cache = HashMap<String, ImageBitmap>()
    private var dongHoCredits: List<Pair<String, String>>? = null

    fun bitmap(ctx: Context, set: ArtSet, month: Int): ImageBitmap? {
        val path = "art/${set.folder}/%02d.jpg".format(month)
        return cache[path] ?: runCatching {
            ctx.assets.open(path).use { BitmapFactory.decodeStream(it) }.asImageBitmap()
        }.getOrNull()?.also { if (cache.size > 6) cache.clear(); cache[path] = it }
    }

    /** "Tên tranh · giấy phép" cho chú thích dưới tranh. */
    fun caption(ctx: Context, set: ArtSet, month: Int): String {
        if (set.titles.isNotEmpty()) return "${set.titles[month - 1]} · ${set.credit}"
        val list = dongHoCredits ?: runCatching {
            val a = JSONArray(ctx.assets.open("art/${set.folder}/credits.json").bufferedReader().readText())
            (0 until a.length()).map { a.getJSONObject(it).let { o -> o.getString("name") to o.getString("license") } }
        }.getOrDefault(emptyList()).also { dongHoCredits = it }
        val (name, lic) = list.getOrNull(month - 1) ?: return set.credit
        return "$name · $lic · ${set.credit}"
    }
}

/** Tranh tờ lịch tháng: ảnh màu nước tràn khung, hoặc tranh Đông Hồ đặt trên nền giấy dó. */
@Composable
fun MonthPainting(month: Int, setId: Int, modifier: Modifier = Modifier, overlay: @Composable BoxScope.() -> Unit = {}) {
    val ctx = LocalContext.current
    val set = MonthArt.set(setId)
    val bmp = remember(set.id, month) { MonthArt.bitmap(ctx, set, month) }
    val paper = Color(0xFFF1E4C6)
    Box(modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(14.dp)).background(paper)) {
        if (bmp != null) {
            Image(
                bmp, null,
                Modifier.fillMaxSize().then(if (set.fit) Modifier.padding(10.dp) else Modifier),
                contentScale = if (set.fit) ContentScale.Fit else ContentScale.Crop,
            )
        }
        // làm mờ nhẹ đáy tranh để chữ đè lên dễ đọc
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.55f to Color.Transparent, 1f to Color(0x99000000))))
        overlay()
    }
}

/** Ô chọn bộ tranh: xem trước tranh của tháng hiện tại. */
@Composable
fun ArtSetPicker(month: Int, selected: Int, onPick: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MonthArt.SETS.forEach { s ->
            val sel = s.id == selected
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(14.dp))
                    .border(if (sel) 3.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .clickable { onPick(s.id) }.padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MonthPainting(month, s.id)
                Text(s.name, style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
