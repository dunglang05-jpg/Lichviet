package vn.lichviet.app.ui.tools

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import vn.lichviet.app.ui.KeyValue
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.Sources
import vn.lichviet.core.fortune.Zodiac
import kotlin.math.absoluteValue

/** Tử vi cung hoàng đạo hằng ngày: điểm và lời luận do ứng dụng tạo theo (cung, ngày) – mang tính giải trí. */
object ZodiacDaily {
    class Cat(val name: String, val icon: String, val color: Color, val text: List<List<String>>)
    class Line(val name: String, val icon: String, val color: Color, val score: Int, val text: String)
    class Result(val cats: List<Line>, val so: List<Int>, val mau: String, val hop: Int)

    private val CAT = listOf(
        Cat("Tình cảm", "♥", Color(0xFFE8566C), listOf(
            listOf("Dễ có hiểu lầm nhỏ với người thân, nên nói chậm và lắng nghe nhiều hơn.", "Cảm xúc hơi thất thường, tránh tranh luận chuyện cũ.", "Người ấy có thể bận rộn, đừng vội trách móc."),
            listOf("Một ngày bình yên, những cử chỉ quan tâm nhỏ sẽ được ghi nhận.", "Quan hệ gia đình ổn định, hợp để cùng nhau ăn một bữa cơm.", "Không có biến động lớn, giữ sự chân thành là đủ."),
            listOf("Tình cảm thăng hoa, rất hợp để hẹn hò hoặc nói lời yêu thương.", "Người độc thân dễ gặp người hợp ý qua bạn bè.", "Gia đình hòa thuận, bạn nhận được sự ủng hộ từ người thân."))),
        Cat("Sự nghiệp", "★", Color(0xFF4F8EE8), listOf(
            listOf("Công việc nhiều áp lực, nên kiểm tra kỹ trước khi gửi đi.", "Dễ bị chậm tiến độ, hãy chia nhỏ việc để làm.", "Tránh quyết định vội vàng trong các cuộc họp."),
            listOf("Công việc trôi chảy, hoàn thành đúng những gì đã lên kế hoạch.", "Một ngày phù hợp để sắp xếp lại giấy tờ, việc tồn đọng.", "Đồng nghiệp hợp tác tốt, mọi việc đều đâu vào đấy."),
            listOf("Ý tưởng mới được đánh giá cao, hãy mạnh dạn trình bày.", "Cơ hội thể hiện năng lực, cấp trên chú ý đến bạn.", "Năng suất cao, hợp để bắt đầu việc quan trọng."))),
        Cat("Tài chính", "◆", Color(0xFFE0A526), listOf(
            listOf("Chi tiêu dễ vượt dự tính, nên hạn chế mua sắm theo cảm hứng.", "Không phải lúc cho vay mượn hay đầu tư mạo hiểm.", "Có khoản chi bất ngờ, hãy để dành một ít dự phòng."),
            listOf("Tài chính ổn định, thu chi cân bằng.", "Hợp để ghi lại chi tiêu và lập kế hoạch tiết kiệm.", "Không có biến động lớn về tiền bạc."),
            listOf("Có tin vui về tiền bạc hoặc khoản thu ngoài dự kiến.", "Hợp để thương lượng giá hoặc chốt các thỏa thuận nhỏ.", "Quản lý tiền khéo léo, cuối ngày còn dư dả."))),
        Cat("Sức khỏe", "✚", Color(0xFF3AA76D), listOf(
            listOf("Dễ mệt mỏi, nên ngủ sớm và uống đủ nước.", "Chú ý cổ vai gáy nếu ngồi lâu, hãy đứng dậy vận động.", "Ăn uống điều độ, tránh đồ nhiều dầu mỡ."),
            listOf("Sức khỏe ổn định, giữ thói quen vận động nhẹ.", "Một buổi đi bộ ngắn sẽ giúp tinh thần sảng khoái.", "Cơ thể khỏe, chỉ cần nghỉ ngơi hợp lý."),
            listOf("Tràn đầy năng lượng, hợp để tập thể thao.", "Tinh thần tỉnh táo, làm việc gì cũng nhanh.", "Sức khỏe dồi dào, giấc ngủ sâu và ngon."))),
        Cat("Tâm trạng", "✿", Color(0xFFF08A24), listOf(
            listOf("Hơi trầm, hãy dành thời gian cho sở thích riêng.", "Dễ nóng nảy, hít thở sâu trước khi phản ứng.", "Nhiều suy nghĩ, viết ra giấy sẽ thấy nhẹ lòng."),
            listOf("Tâm trạng nhẹ nhàng, nói chuyện ôn hòa.", "Bình thản, hợp để đọc sách hoặc nghe nhạc.", "Một ngày dễ chịu, không có gì làm bạn bận lòng."),
            listOf("Vui vẻ, lạc quan và lan tỏa năng lượng tích cực.", "Hài hước, được mọi người quý mến.", "Tự tin, thấy mọi việc đều trong tầm tay."))),
    )
    private val MAU = listOf("Đỏ", "Cam", "Vàng", "Xanh lá", "Xanh dương", "Tím", "Hồng", "Trắng", "Nâu", "Xám bạc", "Xanh ngọc", "Be")

    private fun hash(s: String): Long {
        var h = 2166136261L
        for (ch in s) { h = h xor ch.code.toLong(); h = (h * 16777619L) and 0xFFFFFFFFL }
        return h
    }

    /** Cùng cung và ngày luôn ra cùng kết quả như bản web. */
    fun of(sign: Int, d: Int, m: Int, y: Int): Result {
        val key = "$sign|$d/$m/$y"
        val cats = CAT.map { c ->
            val k = when (c.name) { "Tình cảm" -> "tc"; "Sự nghiệp" -> "sn"; "Tài chính" -> "tch"; "Sức khỏe" -> "sk"; else -> "tt" }
            val h = hash(key + k)
            val score = 1 + (h % 5).toInt()
            val lv = if (score <= 2) 0 else if (score == 3) 1 else 2
            Line(c.name, c.icon, c.color, score, c.text[lv][((h ushr 8) % 3).toInt()])
        }
        val h = hash(key + "x")
        return Result(cats, listOf(1 + (h % 9).toInt(), 10 + ((h ushr 5) % 90).toInt()), MAU[((h ushr 12) % MAU.size).toInt()],
            (sign + 4 + 4 * ((h ushr 16) % 2).toInt()) % 12)
    }
}

@Composable
private fun ZodiacIcon(i: Int, modifier: Modifier) {
    val ctx = LocalContext.current
    val bmp = remember(i) { runCatching { ctx.assets.open("zodiac/%02d.jpg".format(i)).use { BitmapFactory.decodeStream(it) }.asImageBitmap() }.getOrNull() }
    Box(modifier.clip(CircleShape).border(3.dp, Color(0xFFF3E6C8), CircleShape)) {
        if (bmp != null) Image(bmp, Zodiac.NAME[i], contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
    }
}

@Composable
fun HoangDao12Screen(todayJd: Int) {
    val me = mySettings().toPerson()
    val start = me?.let { Zodiac.of(it.day, it.month) } ?: 0
    val pager = rememberPagerState(initialPage = start) { 12 }
    val scope = rememberCoroutineScope()
    var tab by remember { mutableIntStateOf(0) }
    var jd by remember { mutableIntStateOf(todayJd) }
    val cur = pager.currentPage

    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF1D1F4A), Color(0xFF07081A)))).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(pager, contentPadding = PaddingValues(horizontal = 120.dp), modifier = Modifier.fillMaxWidth().height(150.dp)) { page ->
            val off = ((pager.currentPage - page) + pager.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ZodiacIcon(page, Modifier.size(120.dp).graphicsLayer {
                    val sc = 1f - .4f * off; scaleX = sc; scaleY = sc; alpha = 1f - .5f * off
                }.clickable { scope.launch { pager.animateScrollToPage(page) } })
            }
        }
        Text(Zodiac.NAME[cur], color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Serif, modifier = Modifier.padding(top = 4.dp))
        Text(Zodiac.RANGE[cur], color = Color(0xFFC9C6E8))
        if (me != null && cur != start) Text("Cung của bạn: ${Zodiac.NAME[start]}", color = Color(0xFFC9C6E8),
            style = MaterialTheme.typography.labelMedium, modifier = Modifier.clickable { scope.launch { pager.animateScrollToPage(start) } }.padding(4.dp))

        Column(Modifier.padding(top = 12.dp, start = 10.dp, end = 10.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface).padding(bottom = 12.dp)) {
            TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(tab == 0, { tab = 0 }, text = { Text("Hằng ngày") })
                Tab(tab == 1, { tab = 1 }, text = { Text("Trọn đời") })
            }
            Column(Modifier.padding(horizontal = 14.dp)) {
                if (tab == 0) {
                    val dd = remember(jd) { LunarCalendar.jdToDate(jd) }
                    val r = remember(cur, jd) { ZodiacDaily.of(cur, dd[0], dd[1], dd[2]) }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        IconButton(onClick = { jd-- }) { Text("‹", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary) }
                        val wd = DayInfo.of(dd[0], dd[1], dd[2]).weekday
                        Text("${DayInfo.THU[wd]}, ${dd[0]}/${dd[1]}/${dd[2]}", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        IconButton(onClick = { jd++ }) { Text("›", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary) }
                    }
                    r.cats.forEach { c ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(c.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                for (k in 1..5) Text(c.icon, color = if (k <= c.score) c.color else Color(0xFFCFC6BD), fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 3.dp))
                            }
                            Text(c.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                        Lucky("Số may mắn", r.so.joinToString(" · "), Modifier.weight(1f))
                        Lucky("Màu may mắn", r.mau, Modifier.weight(1f))
                        Lucky("Cung hợp", Zodiac.NAME[r.hop], Modifier.weight(1f))
                    }
                    Note("Điểm và lời luận hằng ngày do ứng dụng tạo theo cung và ngày, mang tính giải trí, không dựa trên nguồn chiêm tinh nào.")
                } else {
                    val i = cur
                    KeyValue("Nguyên tố", Zodiac.ELEMENT[i])
                    KeyValue("Tính chất", Zodiac.MODALITY[i])
                    KeyValue("Hành tinh chủ quản", Zodiac.PLANET[i])
                    KeyValue("Biểu tượng", Zodiac.EMBLEM[i])
                    KeyValue("Cung đối diện", Zodiac.NAME[Zodiac.opposite(i)])
                    KeyValue("Cùng nguyên tố", Zodiac.sameElement(i))
                    KeyValue("Hợp với", Zodiac.compatible(i))
                    Text("Tính cách", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Para(Zodiac.TRAIT[i])
                    Bullet("Ưu điểm: ${Zodiac.PROS[i]}", vn.lichviet.app.ui.goodColor())
                    Bullet("Nhược điểm: ${Zodiac.CONS[i]}", MaterialTheme.colorScheme.error)
                    SourceNote(Sources.ZODIAC)
                }
            }
        }
    }
}

@Composable
private fun Lucky(label: String, value: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = .5f)).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}
