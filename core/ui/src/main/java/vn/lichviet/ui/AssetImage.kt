package vn.lichviet.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Ảnh trong assets (con giáp, ảnh sự kiện), có cache nhỏ. */
object AssetImages {
    private val cache = LruCache<String, ImageBitmap>(24)
    private val missing = HashSet<String>()
    fun get(ctx: Context, path: String): ImageBitmap? {
        cache.get(path)?.let { return it }
        if (path in missing) return null
        val b = runCatching { ctx.assets.open(path).use { BitmapFactory.decodeStream(it) }?.asImageBitmap() }.getOrNull()
        if (b == null) missing.add(path) else cache.put(path, b)
        return b
    }
}

val GIAP_TEN = listOf("Chuột", "Trâu", "Hổ", "Mèo", "Rồng", "Rắn", "Ngựa", "Dê", "Khỉ", "Gà", "Chó", "Lợn")

/** Ảnh con giáp theo chi (0 = Tý ... 11 = Hợi). */
@Composable
fun GiapImage(chi: Int, size: Dp, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val c = Math.floorMod(chi, 12)
    val bmp = remember(c) { AssetImages.get(ctx, "giap/%02d.jpg".format(c)) } ?: return
    Image(bmp, "Con ${GIAP_TEN[c]}", contentScale = ContentScale.Crop,
        modifier = modifier.size(size).clip(RoundedCornerShape(size * 0.18f)))
}

/** Ảnh minh họa của ngày lễ (nếu có), theo nhóm của HolidayArt. */
@Composable
fun holidayPhoto(name: String): ImageBitmap? {
    val ctx = LocalContext.current
    val key = HolidayArt.of(name).name.lowercase()
    return remember(key) { AssetImages.get(ctx, "sukien/$key.jpg") }
}
