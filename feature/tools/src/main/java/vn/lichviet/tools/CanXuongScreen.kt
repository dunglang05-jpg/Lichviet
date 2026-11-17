package vn.lichviet.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.DayInfo
import vn.lichviet.core.fortune.CanXuong
import vn.lichviet.ui.KeyValue
import vn.lichviet.ui.Section

private val GIO_RANGE = listOf("23–1", "1–3", "3–5", "5–7", "7–9", "9–11", "11–13", "13–15", "15–17", "17–19", "19–21", "21–23")

/** Cân xương tính số: năm, tháng, ngày, giờ sinh âm lịch quy ra lượng chỉ. */
@Composable
fun CanXuongScreen() {
    val p = PersonPicker("Xem cân xương cho") ?: return
    var gio by rememberSaveable(p.year, p.month, p.day) { mutableIntStateOf(if (p.hour >= 0) ((p.hour + 1) / 2) % 12 else -1) }
    Section("Giờ sinh") {
        (0 until 12).chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { i ->
                    val sel = gio == i
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .clickable { gio = i }.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(DayInfo.CHI[i], fontWeight = FontWeight.Bold,
                            color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        Text(GIO_RANGE[i], fontSize = 11.sp,
                            color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (gio < 0) Note("Chọn giờ sinh để tính.")
    }
    if (gio < 0) return
    val nam = CanXuong.nam(p.can, p.chi)
    val thang = CanXuong.thang(p.lunarMonth)
    val ngay = CanXuong.ngay(p.lunarDay)
    val g = CanXuong.gio(gio)
    val t = nam + thang + ngay + g
    Section("Kết quả") {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(CanXuong.text(t), fontSize = 34.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Text("Sinh giờ ${DayInfo.CHI[gio]}, ngày ${p.lunarDay}/${p.lunarMonth} âm lịch, năm ${p.canChi()}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
        Text(CanXuong.meaning(t), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
    }
    Section("Cách tính") {
        KeyValue("Năm ${p.canChi()}", CanXuong.text(nam).replace("0 lượng ", ""))
        KeyValue("Tháng ${p.lunarMonth} âm lịch", CanXuong.text(thang).replace("0 lượng ", ""))
        KeyValue("Ngày ${p.lunarDay} âm lịch", CanXuong.text(ngay).replace("0 lượng ", ""))
        KeyValue("Giờ ${DayInfo.CHI[gio]}", CanXuong.text(g).replace("0 lượng ", ""))
        KeyValue("Tổng", CanXuong.text(t))
        Note("10 chỉ = 1 lượng. Đây là phép xem dân gian, chủ yếu xem chữ \"phú\", chỉ để tham khảo.")
    }
}
