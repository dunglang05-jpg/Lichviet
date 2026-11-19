package vn.lichviet.tools

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import vn.lichviet.core.fortune.KinhDich

// Màu lấy theo giao diện đang dùng để đồng bộ với toàn app.
private val Red1: Color @Composable get() = MaterialTheme.colorScheme.primary
private val Red2: Color @Composable get() = androidx.compose.ui.graphics.lerp(MaterialTheme.colorScheme.primary, Color.Black, .3f)
private val Cream: Color @Composable get() = MaterialTheme.colorScheme.onPrimary
private val Gold: Color @Composable get() = MaterialTheme.colorScheme.secondary
private val OnGold: Color @Composable get() = MaterialTheme.colorScheme.onSecondary
private val Wine: Color @Composable get() = MaterialTheme.colorScheme.primary
private val Paper: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHighest
private val Ink: Color @Composable get() = MaterialTheme.colorScheme.onSurface
private val Muted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

/** Chủ đề hỏi quẻ: id file lời giải trong assets/gieoque. */
private val TOPICS = listOf(
    "money" to "Tiền bạc", "love" to "Tình duyên", "finding_love" to "Cầu duyên", "having_more_kids" to "Cầu tự",
    "brotherhood" to "Quan hệ với anh chị em", "parents" to "Quan hệ với bố mẹ", "friends" to "Quan hệ với bạn bè",
    "career" to "Công danh, sự nghiệp", "health" to "Sức khỏe", "children" to "Con cái",
    "buying_real_estate" to "Mua nhà đất", "studying" to "Học hành",
)

private data class LoiGiai(val verdict: Char, val text: String)

private object GieoQueData {
    private val cache = HashMap<String, Map<Int, LoiGiai>>()
    fun load(ctx: Context, id: String): Map<Int, LoiGiai> = cache.getOrPut(id) {
        runCatching {
            ctx.assets.open("gieoque/$id.txt").bufferedReader().readLines().mapNotNull { l ->
                val p = l.split('|', limit = 3)
                if (p.size == 3) p[0].trim().toIntOrNull()?.let { it to LoiGiai(p[1].trim().firstOrNull() ?: 'B', p[2].trim()) } else null
            }.toMap()
        }.getOrDefault(emptyMap())
    }
}

/** Gieo quẻ hỏi việc: chọn chủ đề, gieo 3 đồng xu × 6 lần, xem lời giải theo chủ đề. */
@Composable
fun GieoQueV2(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var topic by rememberSaveable { mutableIntStateOf(-1) }
    var question by rememberSaveable { mutableStateOf("") }
    var seed by rememberSaveable { mutableLongStateOf(0L) }
    var shown by rememberSaveable { mutableIntStateOf(0) } // số hào đã hiện (0..6)
    var menu by remember { mutableStateOf(false) }
    val cast = remember(seed) { if (seed == 0L) null else KinhDich.cast(java.util.Random(seed)) }
    BackHandler { if (seed != 0L) { seed = 0L; shown = 0 } else onBack() }

    LaunchedEffect(seed) {
        if (seed != 0L && shown < 6) while (shown < 6) { delay(450); shown++ }
    }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Red1, Red2)))) {
        CloudBackdrop(Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().height(56.dp)) {
                IconButton(onClick = { if (seed != 0L) { seed = 0L; shown = 0 } else onBack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Cream)
                }
                Text("GIEO QUẺ", color = Cream, fontWeight = FontWeight.Bold, fontSize = 18.sp, letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.Center))
            }
            if (cast == null) {
                Spacer(Modifier.height(48.dp))
                Text("Hãy thành tâm nghĩ về điều muốn hỏi", color = Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(Modifier.height(20.dp))
                Box(Modifier.padding(horizontal = 28.dp)) {
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.5.dp, Cream.copy(alpha = .8f), RoundedCornerShape(14.dp))
                            .clickable { menu = true }.padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(if (topic >= 0) TOPICS[topic].second else "Chọn chủ đề quan tâm", color = Cream, fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ExpandMore, null, tint = Cream)
                    }
                    DropdownMenu(menu, { menu = false }) {
                        TOPICS.forEachIndexed { i, (_, t) -> DropdownMenuItem(text = { Text(t) }, onClick = { topic = i; menu = false }) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                BasicTextField(
                    question, { question = it.take(120) },
                    textStyle = TextStyle(color = Cream, fontSize = 16.sp, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(horizontal = 28.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(Cream.copy(alpha = .1f)).padding(14.dp),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.Center) {
                            if (question.isEmpty()) Text("Việc cụ thể muốn hỏi (không bắt buộc)", color = Cream.copy(alpha = .6f), fontSize = 15.sp)
                            inner()
                        }
                    },
                )
                Spacer(Modifier.height(40.dp))
                val ready = topic >= 0
                Box(
                    Modifier.size(150.dp).shadow(if (ready) 16.dp else 0.dp, CircleShape).clip(CircleShape)
                        .background(if (ready) Brush.verticalGradient(listOf(Gold, Gold)) else Brush.verticalGradient(listOf(Cream.copy(.35f), Cream.copy(.25f))))
                        .clickable(enabled = ready) { shown = 0; seed = System.nanoTime() },
                    contentAlignment = Alignment.Center,
                ) { Text("Gieo quẻ", color = if (ready) OnGold else Cream.copy(.7f), fontWeight = FontWeight.Bold, fontSize = 24.sp) }
                Spacer(Modifier.height(14.dp))
                Text(if (ready) "Tĩnh tâm, mỗi việc chỉ nên hỏi một lần" else "Chọn chủ đề trước khi gieo", color = Cream.copy(alpha = .8f), fontSize = 13.sp)
            } else {
                CastResult(cast, shown, TOPICS[topic.coerceAtLeast(0)], question) { seed = 0L; shown = 0 }
            }
        }
    }
}

@Composable
private fun CastResult(c: KinhDich.Cast, shown: Int, topic: Pair<String, String>, question: String, onAgain: () -> Unit) {
    val ctx = LocalContext.current
    Spacer(Modifier.height(8.dp))
    Text(topic.second.uppercase(), color = Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
    if (question.isNotBlank()) Text("“$question”", color = Cream, fontSize = 14.sp, textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
    Spacer(Modifier.height(12.dp))
    // Ba đồng xu của lần gieo đang hiện
    if (shown < 6) {
        Coins(c.lines[shown])
        Text("Lần gieo ${shown + 1}/6", color = Cream, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    }
    Spacer(Modifier.height(10.dp))
    Hexagram(c.lines, shown, Modifier.size(150.dp, 156.dp))
    if (shown < 6) return
    val data = remember(topic.first) { GieoQueData.load(ctx, topic.first) }
    val main = data[c.main]
    Spacer(Modifier.height(14.dp))
    Column(
        Modifier.padding(horizontal = 18.dp).fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Paper).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Quẻ số ${c.main}", color = Muted, fontSize = 13.sp)
                Text(c.mainName, color = Wine, fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = FontFamily.Serif)
            }
            main?.let { VerdictPill(it.verdict) }
        }
        Text(main?.text ?: "Chưa có lời giải cho quẻ này.", color = Ink, fontSize = 17.sp, lineHeight = 25.sp)
        if (c.changed != 0) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            val ch = data[c.changed]
            Text("Có hào động → quẻ biến: ${c.changedName} (số ${c.changed})", color = Wine, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text("Quẻ biến cho biết chiều hướng về sau của sự việc.", color = Muted, fontSize = 13.sp)
            ch?.let {
                Row(verticalAlignment = Alignment.Top) {
                    VerdictPill(it.verdict, small = true)
                    Spacer(Modifier.width(8.dp))
                    Text(it.text, color = Ink, fontSize = 15.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                }
            }
        }
        if (topic.first == "health" || topic.first == "having_more_kids")
            Text("Chỉ để tham khảo, không thay thế ý kiến của bác sĩ.", color = Muted, fontSize = 12.sp)
    }
    Spacer(Modifier.height(20.dp))
    Box(
        Modifier.clip(RoundedCornerShape(26.dp)).background(Gold).clickable(onClick = onAgain).padding(horizontal = 36.dp, vertical = 14.dp),
    ) { Text("Gieo quẻ khác", color = OnGold, fontWeight = FontWeight.Bold, fontSize = 17.sp) }
    Text("Lời giải tóm lược theo gieoque.net", color = Cream.copy(alpha = .7f), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
}

@Composable
private fun VerdictPill(v: Char, small: Boolean = false) {
    val (t, c) = when (v) { 'T' -> "Tốt" to vn.lichviet.ui.goodColor(); 'X' -> "Xấu" to MaterialTheme.colorScheme.error; else -> "Trung bình" to Color(0xFFB7791F) }
    Text(t, color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (small) 12.sp else 14.sp,
        modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(c).padding(horizontal = if (small) 8.dp else 12.dp, vertical = 4.dp))
}

/** Ba đồng xu lật; mặt ngửa (3) màu vàng, mặt sấp (2) màu đồng. */
@Composable
private fun Coins(sum: Int) {
    val heads = when (sum) { 6 -> 0; 7 -> 1; 8 -> 2; else -> 3 }
    val t = rememberInfiniteTransition(label = "coin")
    val spin by t.animateFloat(0f, 1f, infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Restart), label = "spin")
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        for (i in 0 until 3) {
            val head = i < heads
            Box(
                Modifier.size(54.dp).graphicsLayer { rotationY = spin * 360f + i * 40f }
                    .clip(CircleShape).background(if (head) Color(0xFFF4C46A) else Color(0xFFB87333)).border(3.dp, Color(0xFF7A4A12), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Box(Modifier.size(14.dp).background(Red2)) }
        }
    }
}

/** Vẽ quẻ từ dưới lên; [shown] hào đầu tiên được hiện. Hào động có chấm. */
@Composable
private fun Hexagram(lines: IntArray, shown: Int, modifier: Modifier) {
    val on = Cream; val mark = Gold
    Canvas(modifier) {
        val h = size.height / 6f
        val w = size.width * .82f
        val x0 = (size.width - w) / 2f
        for (i in 0 until 6) {
            val y = size.height - (i + .5f) * h
            val v = lines[i]
            val c = if (i < shown) on else on.copy(alpha = .18f)
            val th = h * .42f
            val yang = v == 7 || v == 9
            if (yang) drawRoundRect(c, Offset(x0, y - th / 2), Size(w, th), CornerRadius(th / 3))
            else {
                drawRoundRect(c, Offset(x0, y - th / 2), Size(w * .43f, th), CornerRadius(th / 3))
                drawRoundRect(c, Offset(x0 + w * .57f, y - th / 2), Size(w * .43f, th), CornerRadius(th / 3))
            }
            if (i < shown && (v == 6 || v == 9)) drawCircle(mark, th * .45f, Offset(x0 + w + th * .9f, y))
        }
    }
}

/** Mây cách điệu mờ trên nền đỏ. */
@Composable
private fun CloudBackdrop(modifier: Modifier) {
    val cc = Cream.copy(alpha = .1f)
    Canvas(modifier) {
        val c = cc
        fun cloud(x: Float, y: Float, s: Float) = drawCloud(c, Offset(x * size.width, y * size.height), s * size.width)
        cloud(.05f, .12f, .26f); cloud(.62f, .08f, .3f); cloud(.75f, .32f, .22f); cloud(.02f, .45f, .2f)
        cloud(.6f, .58f, .26f); cloud(.1f, .76f, .24f); cloud(.7f, .88f, .22f)
    }
}

private fun DrawScope.drawCloud(c: Color, o: Offset, w: Float) {
    val h = w * .1f
    drawRoundRect(c, Offset(o.x, o.y), Size(w * .55f, h), CornerRadius(h / 2))
    drawRoundRect(c, Offset(o.x + w * .62f, o.y), Size(w * .3f, h), CornerRadius(h / 2))
    drawRoundRect(c, Offset(o.x + w * .2f, o.y + h * 1.5f), Size(w * .5f, h), CornerRadius(h / 2))
    drawCircle(c, h * .55f, Offset(o.x + w * .78f, o.y + h * 2f))
    drawCircle(c, h * .45f, Offset(o.x - h * .9f, o.y + h * .5f), style = Stroke(h * .3f))
}

@Suppress("unused") private val keepDp: Dp = 0.dp
