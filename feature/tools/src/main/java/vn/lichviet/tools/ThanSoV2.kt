package vn.lichviet.tools

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.fortune.Numerology
import vn.lichviet.data.FamilyRepository
import vn.lichviet.data.Today
import kotlin.math.cos
import kotlin.math.sin

private val SkyTop = Color(0xFF5CC6D9)
private val SkyMid = Color(0xFF7DBCE6)
private val SkyBottom = Color(0xFFD9D4F2)
private val Deep = Color(0xFF3F97C7)
private val Ink = Color(0xFF2B5F86)
private val Title = Color(0xFF4A7BE0)

/** Thần số học: trang nhập (tên + ngày sinh) và trang kết quả với các mục mở/đóng. */
@Composable
fun ThanSoV2(onBack: () -> Unit) {
    val s = mySettings()
    var name by rememberSaveable { mutableStateOf(s.userName) }
    var d by rememberSaveable { mutableStateOf(if (s.birthD > 0) s.birthD.toString() else "") }
    var m by rememberSaveable { mutableStateOf(if (s.birthM > 0) s.birthM.toString() else "") }
    var y by rememberSaveable { mutableStateOf(if (s.birthY > 0) s.birthY.toString() else "") }
    var showResult by rememberSaveable { mutableStateOf(false) }
    val dd = d.toIntOrNull() ?: 0; val mm = m.toIntOrNull() ?: 0; val yy = y.toIntOrNull() ?: 0
    val ok = validDate(dd, mm, yy)
    BackHandler { if (showResult) showResult = false else onBack() }
    if (showResult && ok) ThanSoResult(name, dd, mm, yy) { showResult = false }
    else ThanSoInput(name, { name = it }, d, m, y, { a, b, c -> d = a; m = b; y = c }, ok, onBack) { if (ok) showResult = true }
}

@Composable
private fun ThanSoInput(
    name: String, onName: (String) -> Unit, d: String, m: String, y: String,
    onDate: (String, String, String) -> Unit, ok: Boolean, onBack: () -> Unit, onSubmit: () -> Unit,
) {
    val ctx = LocalContext.current
    val s = mySettings()
    val members by FamilyRepository.get(ctx).members.collectAsState()
    var pick by remember { mutableStateOf(false) }
    var people by remember { mutableStateOf(false) }
    val lp = if (ok) Numerology.lifePath(d.toInt(), m.toInt(), y.toInt()) else 0
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(SkyTop, SkyMid, SkyBottom)))) {
        SacredGeometry(Modifier.fillMaxWidth().height(300.dp))
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding()) {
            Row(Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Color.White) }
                Spacer(Modifier.weight(1f))
                Box {
                    IconButton(onClick = { people = true }) { Icon(Icons.Filled.Groups, "Chọn người trong gia đình", tint = Color.White) }
                    DropdownMenu(people, { people = false }) {
                        if (s.birthD > 0) DropdownMenuItem(text = { Text(s.userName.ifBlank { "Tôi" }) }, onClick = {
                            onName(s.userName); onDate(s.birthD.toString(), s.birthM.toString(), s.birthY.toString()); people = false
                        })
                        members.filter { it.hasBirth }.forEach { mb ->
                            DropdownMenuItem(text = { Text(mb.name) }, onClick = {
                                onName(mb.name); onDate(mb.birthD.toString(), mb.birthM.toString(), mb.birthY.toString()); people = false
                            })
                        }
                        DropdownMenuItem(text = { Text("Người khác") }, onClick = { onName(""); onDate("", "", ""); people = false })
                    }
                }
            }
            Spacer(Modifier.height(110.dp))
            Text("THẦN SỐ HỌC", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 38.sp, color = Color.White,
                textAlign = TextAlign.Center, letterSpacing = 2.sp, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Column(Modifier.padding(horizontal = 32.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                WhiteField(Icons.Filled.Person) {
                    BasicTextField(
                        name, onName, singleLine = true, textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF263238)),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (name.isEmpty()) Text("Họ tên khai sinh (không bắt buộc)", color = Color(0xFF9AA5AE), fontSize = 16.sp)
                            inner()
                        },
                    )
                    if (name.isNotEmpty()) IconButton(onClick = { onName("") }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Cancel, "Xóa", tint = Color(0xFFB0B8BF))
                    }
                }
                WhiteField(Icons.Filled.Cake, Modifier.clickable { pick = true }) {
                    Text(if (ok) "%02d/%02d/%s".format(d.toInt(), m.toInt(), y) else "Ngày sinh dương lịch",
                        fontSize = 18.sp, color = if (ok) Color(0xFF263238) else Color(0xFF9AA5AE), modifier = Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(24.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                NumberEmblem(if (ok) Numerology.label(lp) else "?", 280.dp)
            }
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.padding(horizontal = 40.dp).fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp))
                    .background(if (ok) Color.White else Color.White.copy(alpha = .55f)).clickable(enabled = ok, onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) { Text("XEM KẾT QUẢ", color = Deep, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 1.sp) }
            if (!ok) Text("Chọn ngày sinh để xem", color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            Spacer(Modifier.height(40.dp))
        }
    }
    if (pick) BirthDatePickerDialog(d, m, y, { pick = false }, onDate)
}

@Composable
private fun WhiteField(icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 58.dp).clip(RoundedCornerShape(14.dp)).background(Color.White)
            .border(1.5.dp, Color(0xFFE3EAF0), RoundedCornerShape(14.dp)).then(modifier).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Deep, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(16.dp))
        content()
    }
}

/** Huy hiệu số: vòng ngoài có chấm trăng, vòng vạch xoay chậm, lõi tròn đậm chứa con số. */
@Composable
fun NumberEmblem(text: String, size: Dp, modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "emblem")
    val rot by t.animateFloat(0f, 360f, infiniteRepeatable(tween(60000, easing = LinearEasing), RepeatMode.Restart), label = "rot")
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val c = center; val r = this.size.minDimension / 2f
            val white = Color.White
            drawCircle(white.copy(alpha = .85f), r * .97f, c, style = Stroke(r * .012f))
            for (k in 0 until 8) {
                val a = Math.toRadians(k * 45.0 - 90)
                val p = Offset(c.x + (r * .97f * cos(a)).toFloat(), c.y + (r * .97f * sin(a)).toFloat())
                drawCircle(white, r * .045f, p)
                drawCircle(Deep, r * .045f, p, style = Stroke(r * .012f))
                drawArc(Deep, (k * 45f) - 90f, 180f, true, Offset(p.x - r * .045f, p.y - r * .045f),
                    androidx.compose.ui.geometry.Size(r * .09f, r * .09f))
            }
            rotate(rot, c) {
                for (k in 0 until 72) {
                    val a = Math.toRadians(k * 5.0)
                    val r1 = r * .64f; val r2 = r * (if (k % 2 == 0) .86f else .80f)
                    drawLine(white, Offset(c.x + (r1 * cos(a)).toFloat(), c.y + (r1 * sin(a)).toFloat()),
                        Offset(c.x + (r2 * cos(a)).toFloat(), c.y + (r2 * sin(a)).toFloat()), strokeWidth = r * .012f)
                }
            }
            drawCircle(white, r * .64f, c, style = Stroke(r * .02f))
            drawCircle(Deep, r * .61f, c)
        }
        Text(text, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = (size.value * (if (text.length > 2) .22f else .32f)).sp)
    }
}

/** Hình học thiêng mờ phía trên: hai tam giác lồng nhau trong vòng tròn, chấm sao. */
@Composable
private fun SacredGeometry(modifier: Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height * .3f)
        val r = size.width * .42f
        val w = Color.White.copy(alpha = .55f)
        val st = Stroke(1.5f)
        drawCircle(w, r, c, style = st)
        drawCircle(w.copy(alpha = .35f), r * 1.12f, c, style = Stroke(1.2f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 10f))))
        drawCircle(w.copy(alpha = .4f), r * .55f, c, style = st)
        fun tri(up: Boolean) = Path().apply {
            for (k in 0 until 3) {
                val a = Math.toRadians(k * 120.0 + if (up) -90.0 else 90.0)
                val x = c.x + (r * cos(a)).toFloat(); val yv = c.y + (r * sin(a)).toFloat()
                if (k == 0) moveTo(x, yv) else lineTo(x, yv)
            }
            close()
        }
        drawPath(tri(true), w, style = st); drawPath(tri(false), w, style = st)
        drawLine(w.copy(alpha = .4f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), 1.2f)
        drawLine(w.copy(alpha = .4f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), 1.2f)
        for (k in 0 until 12) {
            val a = Math.toRadians(k * 30.0 + 15)
            drawCircle(Color.White, 5f, Offset(c.x + (r * 1.12f * cos(a)).toFloat(), c.y + (r * 1.12f * sin(a)).toFloat()))
        }
    }
}

// ------------------------------------------------------------ kết quả

private fun digits(n: Int) = n.toString().map { it - '0' }

/** Diễn giải phép cộng: 2+6+0+5+1+9+9+5 = 37 → 3+7 = 10. */
private fun lifePathSteps(d: Int, m: Int, y: Int): String {
    val ds = digits(d) + digits(m) + digits(y)
    var s = ds.sum()
    val sb = StringBuilder(ds.joinToString("+") + " = $s")
    while (s > 11 && s != 22) { val x = digits(s); s = x.sum(); sb.append(" → " + x.joinToString("+") + " = $s") }
    return sb.toString()
}

@Composable
private fun ThanSoResult(name: String, d: Int, m: Int, y: Int, onBack: () -> Unit) {
    val year = Today.ymd()[2]
    val r = remember(name, d, m, y, year) { Numerology.of(name, d, m, y, year) }
    val pk = remember(d, m, y) { Numerology.pinnacles(d, m, y) }
    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(SkyTop, SkyMid, Color(0xFF8FA8E3))))) {
            Column(Modifier.fillMaxWidth().statusBarsPadding().padding(bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.fillMaxWidth().height(56.dp)) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Color.White)
                    }
                    Text("CON SỐ CHỦ ĐẠO", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Center))
                }
                NumberEmblem(Numerology.label(r.lifePath), 240.dp)
                Text(listOfNotNull(name.takeIf { it.isNotBlank() }, "%02d/%02d/%d".format(d, m, y)).joinToString(" · "),
                    color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
                Text(Numerology.keyword(r.lifePath).ifEmpty { " " }, color = Color.White, fontSize = 14.sp)
            }
        }
        Fold("Ý NGHĨA CON SỐ CHỦ ĐẠO", true) {
            Body("Con số chủ đạo là chỉ số quan trọng nhất trong thần số học Pythagoras, tính từ ngày tháng năm sinh dương lịch. " +
                "Con số này được dùng để nói về tính cách, điểm mạnh, điểm yếu và con đường phát triển của mỗi người.")
            Body("Cách tính: cộng tất cả chữ số của ngày sinh rồi rút gọn đến khi được 2–11; tổng bằng 22 giữ là 22/4.")
            Calc(lifePathSteps(d, m, y))
        }
        Fold("ĐẶC ĐIỂM SỐ CHỦ ĐẠO LÀ ${Numerology.label(r.lifePath)}", true) {
            Numerology.keyword(r.lifePath).takeIf { it.isNotEmpty() }?.let { Body("Từ khóa: $it.") }
            Body(Numerology.meaning(r.lifePath))
        }
        Fold("NĂM CÁ NHÂN", false) {
            for (k in 0..2) {
                val py = Numerology.personalYear(d, m, year + k)
                NumRow("Năm ${year + k}", py.toString(), Numerology.keyword(py))
            }
            Calc("Cộng chữ số ngày, tháng sinh và năm cần xem, rút gọn về 1–9.")
        }
        Fold("4 ĐỈNH CAO CUỘC ĐỜI", false) {
            pk.forEachIndexed { i, x -> NumRow("Đỉnh ${i + 1} · từ ${x.fromAge} tuổi", x.number.toString(), Numerology.keyword(x.number)) }
            Calc("Đỉnh 1 = tháng + ngày, đỉnh 2 = ngày + năm, đỉnh 3 = đỉnh 1 + đỉnh 2, đỉnh 4 = tháng + năm (đều rút gọn). " +
                "Tuổi đỉnh 1 = 36 − số chủ đạo, mỗi đỉnh sau cộng 9 năm.")
        }
        Fold("CHỈ SỐ NGÀY SINH VÀ THÁI ĐỘ", false) {
            NumRow("Số ngày sinh (ngày $d)", r.birthDay.toString(), Numerology.keyword(r.birthDay))
            NumRow("Số thái độ (ngày + tháng)", r.attitude.toString(), Numerology.keyword(r.attitude))
        }
        if (r.expression > 0) Fold("CHỈ SỐ THEO TÊN", false) {
            NumRow("Số sứ mệnh (cả họ tên)", r.expression.toString(), Numerology.keyword(r.expression))
            NumRow("Số linh hồn (nguyên âm)", r.soul.toString(), Numerology.keyword(r.soul))
            NumRow("Số nhân cách (phụ âm)", r.personality.toString(), Numerology.keyword(r.personality))
            Calc("Họ tên khai sinh bỏ dấu, A=1 … I=9, J=1 … R=9, S=1 … Z=8. Nguyên âm: A, E, I, O, U.")
        } else Fold("CHỈ SỐ THEO TÊN", false) {
            Body("Nhập họ tên khai sinh ở trang trước để xem số sứ mệnh, linh hồn và nhân cách.")
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Fold(title: String, initiallyOpen: Boolean, content: @Composable () -> Unit) {
    var open by rememberSaveable(title) { mutableStateOf(initiallyOpen) }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().background(Color(0xFFF6F7F9)).clickable { open = !open }.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, color = Title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Icon(if (open) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, if (open) "Thu gọn" else "Mở", tint = Color(0xFF9AA0A6))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE6E8EB)))
        AnimatedVisibility(open) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { content() }
        }
    }
}

@Composable
private fun Body(t: String) = Text(t, fontSize = 17.sp, lineHeight = 26.sp, color = Color(0xFF2E3338), textAlign = TextAlign.Justify)

@Composable
private fun Calc(t: String) = Text(t, fontSize = 14.sp, lineHeight = 20.sp, color = Ink,
    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color(0xFFEAF4FA)).padding(12.dp))

@Composable
private fun NumRow(label: String, n: String, kw: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(Deep), contentAlignment = Alignment.Center) {
            Text(n, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = 13.sp, color = Color(0xFF6B7680))
            Text(kw.ifEmpty { "—" }, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E3338))
        }
    }
}

