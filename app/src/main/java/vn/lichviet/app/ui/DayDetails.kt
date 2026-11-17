package vn.lichviet.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.Almanac
import vn.lichviet.core.DayInfo
import vn.lichviet.core.NhiThapBatTu
import vn.lichviet.ui.ShadowCard
import vn.lichviet.ui.goodColor

private val Bad = Color(0xFFC62828)

/** Tiêu đề thẻ có biểu tượng tròn. */
@Composable
fun IconTitle(icon: ImageVector, title: String, color: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(color.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun Dot(text: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•", color = color, modifier = Modifier.width(14.dp), fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

/** Thẻ tổng quan: vòng cát–hung và 4 ô Ngày / Tháng / Tiết khí / Hoàng đạo. */
@Composable
fun DayOverviewCard(d: DayInfo) {
    val good = goodColor()
    val tone = when { d.score >= 4 -> good; d.score <= 2 -> Bad; else -> MaterialTheme.colorScheme.primary }
    ShadowCard(elevation = 5.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                val track = MaterialTheme.colorScheme.outlineVariant
                Canvas(Modifier.size(96.dp)) {
                    val st = Stroke(9.dp.toPx(), cap = StrokeCap.Round)
                    val i = 6.dp.toPx()
                    val sz = Size(size.width - 2 * i, size.height - 2 * i)
                    drawArc(track, 135f, 270f, false, Offset(i, i), sz, style = st)
                    drawArc(tone, 135f, 270f * d.score / 5f, false, Offset(i, i), sz, style = st)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${d.score}/5", fontWeight = FontWeight.Black, fontSize = 22.sp, color = tone)
                    Text("Cát – Hung", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Đánh giá chung", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(d.verdict, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = tone,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(tone.copy(alpha = .12f)).padding(horizontal = 12.dp, vertical = 4.dp))
                Text("${d.than12} · trực ${d.truc} · sao ${d.tu}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
            }
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(top = 6.dp)) {
            val cells = listOf(
                "Ngày" to d.dayCanChi, "Tháng" to d.monthCanChi, "Tiết khí" to (d.tietKhiCurrent ?: "—"),
                "Ngày" to if (d.hoangDao) "Hoàng đạo" else "Hắc đạo",
            )
            cells.forEachIndexed { i, (k, v) ->
                if (i > 0) Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(k, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(v, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center,
                        color = if (i == 3) (if (d.hoangDao) good else Bad) else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        vn.lichviet.ui.MoonStatus(d)
    }
}

/** Hai thẻ cạnh nhau: việc nên làm / việc cần tránh. */
@Composable
fun NenTranhCards(d: DayInfo, fits: String?) {
    val good = goodColor()
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 7.dp).height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        HalfCard(Icons.Filled.CheckCircle, "Việc nên làm", good, Modifier.weight(1f)) { d.nenLam.forEach { Dot(it) } }
        HalfCard(Icons.Filled.Cancel, "Việc cần tránh", Bad, Modifier.weight(1f)) { d.kyLam.forEach { Dot(it) } }
    }
    if (fits != null) Text("Ngày hợp cho: $fits", fontWeight = FontWeight.SemiBold, color = good,
        style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 22.dp))
}

@Composable
private fun HalfCard(icon: ImageVector, title: String, color: Color, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxHeight().clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, color.copy(alpha = .35f), RoundedCornerShape(20.dp)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(6.dp))
            Text(title, fontWeight = FontWeight.Bold, color = color, style = MaterialTheme.typography.titleSmall)
        }
        content()
    }
}

/** Lưới 12 giờ, giờ hoàng đạo tô màu. */
@Composable
fun GioHoangDaoCard(d: DayInfo) {
    val good = goodColor()
    ShadowCard {
        IconTitle(Icons.Filled.Schedule, "Giờ hoàng đạo")
        d.hours.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { h ->
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (h.good) good.copy(alpha = .14f) else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (h.good) good.copy(alpha = .6f) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(h.name, fontWeight = FontWeight.Bold, color = if (h.good) good else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(h.range.split("-").joinToString("–") { it.padStart(2, '0') }, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(top = 2.dp)) {
            Legend(good.copy(alpha = .5f), "Giờ hoàng đạo"); Legend(MaterialTheme.colorScheme.outlineVariant, "Giờ hắc đạo")
        }
    }
}

@Composable
private fun Legend(c: Color, t: String) = Row(verticalAlignment = Alignment.CenterVertically) {
    Box(Modifier.size(10.dp).clip(CircleShape).background(c)); Spacer(Modifier.width(6.dp))
    Text(t, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

/** Ngày kỵ (Tam nương, Nguyệt kỵ, Sát chủ…) và lưu ý. */
@Composable
fun NgayKyCard(d: DayInfo, a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.WarningAmber, "Ngày kỵ", if (a.kyNhat.isEmpty()) goodColor() else Bad)
        if (a.kyNhat.isEmpty() && d.luuY.isEmpty()) Dot("Hôm nay không phạm ngày kỵ nào (Tam nương, Nguyệt kỵ…).", goodColor())
        a.kyNhat.forEach { Dot("${it.name}: ${it.desc}", Bad) }
        d.luuY.forEach { Dot(it, Bad) }
    }
}

@Composable
fun BanhToCard(a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.Block, "Bành Tổ bách kỵ nhật")
        Dot(a.banhToCan); Dot(a.banhToChi)
    }
}

@Composable
fun KhongMinhCard(a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.AutoAwesome, "Khổng Minh lục diệu", if (a.khongMinhTot) goodColor() else Bad)
        Dot("Ngày ${a.khongMinh} (${if (a.khongMinhTot) "tốt" else "xấu"}): ${a.khongMinhDesc}", if (a.khongMinhTot) goodColor() else Bad)
    }
}

@Composable
fun TrucCard(d: DayInfo) {
    val sc = DayInfo.trucScore(d.trucIndex)
    val tone = when { sc > 0 -> goodColor(); sc < 0 -> Bad; else -> MaterialTheme.colorScheme.primary }
    ShadowCard {
        IconTitle(Icons.Filled.Today, "Trực ${d.truc}", tone)
        Text(when { sc > 0 -> "Trực tốt"; sc == 0 -> "Trực trung bình"; sc == -1 -> "Trực xấu"; else -> "Trực rất xấu" },
            fontWeight = FontWeight.SemiBold, color = tone)
        Dot("Nên: " + DayInfo.trucNen(d.trucIndex).joinToString(", "))
        Dot("Không nên: " + DayInfo.trucKy(d.trucIndex).joinToString(", "))
    }
}

@Composable
fun NhiThapBatTuCard(d: DayInfo) {
    val i = NhiThapBatTu.index(d.tu)
    if (i < 0) return
    val tone = if (d.tuTot) goodColor() else Bad
    ShadowCard {
        IconTitle(Icons.Filled.Stars, "Nhị thập bát tú", tone)
        Dot("Tên sao: sao ${d.tu}")
        Dot("Tên ngày: ${NhiThapBatTu.fullName(i)} – ${if (d.tuTot) "tốt (Kiết tú)" else "xấu (Hung tú)"}, tướng tinh con ${NhiThapBatTu.CON_VAT[i]}", tone)
        Dot(NhiThapBatTu.meaning(i))
    }
}

@Composable
fun NgocHapCard(a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.MenuBook, "Ngọc Hạp Thông Thư")
        Text("SAO TỐT", style = MaterialTheme.typography.labelLarge, color = goodColor(), fontWeight = FontWeight.Bold)
        if (a.goodStars.isEmpty()) Dot("Không có")
        a.goodStars.forEach { Dot("${it.name}: ${it.desc}") }
        Text("SAO XẤU", style = MaterialTheme.typography.labelLarge, color = Bad, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        if (a.badStars.isEmpty()) Dot("Không có")
        a.badStars.forEach { Dot("${it.name}: ${it.desc}") }
    }
}

@Composable
fun HuongXuatHanhCard(a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.Explore, "Hướng xuất hành")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HuongTile("Hỷ thần", a.hyThan, goodColor(), Modifier.weight(1f))
            HuongTile("Tài thần", a.taiThan, Color(0xFFB8860B), Modifier.weight(1f))
            HuongTile("Hạc thần", a.hacThan, Bad, Modifier.weight(1f))
        }
        Text(if ("Trên trời" == a.hacThan) "Hạc thần ở trên trời, không cần tránh hướng nào." else "Tránh xuất hành về hướng ${a.hacThan} (gặp Hạc thần).",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HuongTile(k: String, v: String, c: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(c.copy(alpha = .1f)).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(k, style = MaterialTheme.typography.labelMedium, color = c, fontWeight = FontWeight.SemiBold)
        Text(v, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun LyThuanPhongCard(a: Almanac) {
    ShadowCard {
        IconTitle(Icons.Filled.DirectionsWalk, "Giờ xuất hành theo Lý Thuần Phong")
        a.xuatHanh.take(6).forEachIndexed { i, h ->
            val h2 = a.xuatHanh[i + 6]
            val c = if (h.good) goodColor() else Bad
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(top = 2.dp)) {
                Box(Modifier.padding(top = 5.dp).size(8.dp).clip(CircleShape).background(c))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("${h.range}h & ${h2.range}h · ${h.name}", fontWeight = FontWeight.SemiBold, color = c)
                    Text(h.desc, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
