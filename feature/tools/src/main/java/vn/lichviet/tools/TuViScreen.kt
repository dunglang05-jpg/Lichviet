package vn.lichviet.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.lichviet.core.fortune.TuVi
import vn.lichviet.ui.*
import vn.lichviet.data.Today

private val GIO_KHOANG = listOf("23–1", "1–3", "3–5", "5–7", "7–9", "9–11", "11–13", "13–15", "15–17", "17–19", "19–21", "21–23")

/** Vị trí 12 cung trên lưới 4×4 truyền thống (Tỵ Ngọ Mùi Thân ở trên, Dần Sửu Tý Hợi ở dưới). */
private val HANG_TREN = listOf(5, 6, 7, 8)
private val HANG_DUOI = listOf(2, 1, 0, 11)

@Composable
private fun hoaColor(h: String?): Color = when (h) {
    "Hóa Lộc" -> goodColor()
    "Hóa Quyền" -> MaterialTheme.colorScheme.secondary
    "Hóa Khoa" -> Color(0xFF1E6FD9)
    "Hóa Kỵ" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurface
}

private fun hoaNgan(h: String?) = when (h) { "Hóa Lộc" -> "Lộc"; "Hóa Quyền" -> "Quyền"; "Hóa Khoa" -> "Khoa"; "Hóa Kỵ" -> "Kỵ"; else -> "" }
private fun mvNgan(m: String?) = when (m) { "Miếu" -> "M"; "Vượng" -> "V"; "Đắc" -> "Đ"; "Bình" -> "B"; "Hãm" -> "H"; else -> "" }

@Composable
fun TuViLaSoScreen() {
    val p = PersonPicker("Lập lá số cho") ?: return
    var gio by rememberSaveable(p.name, p.day, p.month, p.year) { mutableIntStateOf(if (p.hour >= 0) TuVi.chiGio(p.hour) else -1) }
    var namXem by rememberSaveable { mutableIntStateOf(Today.ymd()[2]) }

    Section("Giờ sinh") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items((0..11).toList()) { i ->
                FilterChip(gio == i, { gio = i }, label = { Text("${TuVi.CHI[i]} ${GIO_KHOANG[i]}h") })
            }
        }
        if (gio < 0) Note("Chọn giờ sinh để lập lá số. Không rõ giờ thì lá số không chính xác.")
        NumberStepper("Năm xem hạn", namXem, 1900..2100) { namXem = it }
    }
    if (gio < 0) return

    val ls = remember(p.name, p.male, p.day, p.month, p.year, gio) { TuVi.lap(p.name, p.male, p.day, p.month, p.year, gio) }
    val dv = remember(ls) { TuVi.daiVan(ls) }
    val han = remember(ls, namXem) { TuVi.hanNam(ls, namXem) }
    val saoLuu = remember(ls, namXem) { TuVi.saoLuu(ls, namXem) }
    val cachCuc = remember(ls) { TuVi.cachCuc(ls) }
    val dvNay = dv.firstOrNull { han.tuoiMu in it.tuTuoi..it.denTuoi }
    var chon by rememberSaveable(ls.cungMenh, p.year) { mutableIntStateOf(ls.cungMenh) }

    Section("Lá số Tử Vi") {
        LaSoGrid(ls, chon, han.luuNienViTri, dvNay?.viTri ?: -1) { chon = it }
        Note("Bấm vào một cung để xem đủ các sao. Viền đậm: cung Mệnh. ● năm xem (lưu niên), ◆ đại vận hiện tại.")
    }
    CungDetail(ls, chon)

    Section("Đại vận (10 năm)") {
        dv.forEach { d ->
            val nay = d == dvNay
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(if (nay) MaterialTheme.colorScheme.primary.copy(alpha = .12f) else Color.Transparent)
                    .clickable { chon = d.viTri }.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${d.tuTuoi}–${d.denTuoi}", Modifier.width(64.dp), fontWeight = FontWeight.Bold)
                Column(Modifier.weight(1f)) {
                    Text("${d.tenCung} (${d.chi})", fontWeight = if (nay) FontWeight.Bold else FontWeight.Normal)
                    Text(d.chinhTinh.ifBlank { "Vô chính diệu" }, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${ls.amNam + d.tuTuoi - 1}–${ls.amNam + d.denTuoi - 1}", style = MaterialTheme.typography.bodySmall)
            }
        }
        Note("Tuổi tính theo tuổi mụ (sinh ra là 1 tuổi).")
    }

    Section("Hạn năm ${han.nam} (${han.canChiNam}) · ${han.tuoiMu} tuổi") {
        KeyValue("Lưu niên (Thái Tuế)", "${han.luuNienTenCung} (${TuVi.CHI[han.luuNienViTri]})")
        KeyValue("Tiểu vận", "${han.tieuVanTenCung} (${TuVi.CHI[han.tieuVanViTri]})")
        KeyValue("Đại vận", if (han.daiVanTenCung.isBlank()) "Chưa vào đại vận" else "${han.daiVanTenCung} (${han.daiVanTuTuoi}–${han.daiVanDenTuoi} tuổi)")
        SectionTitle("Lưu Tứ Hóa", modifier = Modifier.padding(top = 6.dp))
        han.luuTuHoa.forEach { t ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(t.hoa, Modifier.width(90.dp), color = hoaColor(t.hoa), fontWeight = FontWeight.Bold)
                Text(t.sao + if (t.coTrenLaSo()) " → ${t.tenCung} (${t.chiCung})" else "", style = MaterialTheme.typography.bodyMedium)
            }
        }
        SectionTitle("Sao lưu", modifier = Modifier.padding(top = 6.dp))
        saoLuu.forEach { s ->
            val qh = listOfNotNull(
                quanHe(s.voiLuuNien)?.let { "$it lưu niên" }, quanHe(s.voiTieuVan)?.let { "$it tiểu vận" }, quanHe(s.voiDaiVan)?.let { "$it đại vận" },
            )
            KeyValue(s.ten, "${s.tenCung} (${s.chi})" + if (qh.isNotEmpty()) "\n" + qh.joinToString(", ") else "")
        }
        SectionTitle("Nguyệt hạn (tháng âm lịch)", modifier = Modifier.padding(top = 6.dp))
        Text("Lưu Đẩu Quân ở ${TuVi.CHI[han.luuDauQuan]} là tháng Giêng, đếm thuận.", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        for (r in 0 until 6) Row(Modifier.fillMaxWidth()) {
            for (k in 0..1) {
                val t = r * 2 + k
                val pos = han.thang[t]
                Text("Tháng ${t + 1}: ${ls.cung[pos].tenCung} (${TuVi.CHI[pos]})", Modifier.weight(1f).padding(vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    if (cachCuc.isNotEmpty()) Section("Cách cục") {
        cachCuc.forEach { c ->
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(c.ten) }
                    c.tenHan?.let { append("  $it") }
                }, color = when (c.nhom) {
                    TuVi.DINH_BAN_TIEN -> MaterialTheme.colorScheme.error
                    TuVi.DINH_PHU, TuVi.DINH_QUY -> goodColor()
                    else -> MaterialTheme.colorScheme.primary
                })
                Text(c.dieuKien + (c.tenCung?.let { " · cung $it" } ?: ""), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(c.moTa, style = MaterialTheme.typography.bodyMedium)
                c.chuVan?.let { Text("Toàn thư: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (c.coDiBan) Note("Có sách luận khác.")
            }
        }
    }
    Note("An sao theo lối phổ biến ở Việt Nam (Tử Vi Đẩu Số Tân Biên), theo thư viện Nhật Nguyệt (nhatnguyet.org). " +
        "Tứ Hóa can Canh dùng Thiên Đồng hóa Khoa, Thái Âm hóa Kỵ. Sinh giờ Tý lấy ngày âm của đúng ngày dương đã nhập.")
}

private fun quanHe(s: String) = when (s) { "dong-cung" -> "Cùng cung"; "xung-chieu" -> "Xung chiếu"; "tam-hop" -> "Tam hợp"; else -> null }

@Composable
private fun LaSoGrid(ls: TuVi.LaSo, chon: Int, luuNien: Int, daiVan: Int, onPick: (Int) -> Unit) {
    val line = MaterialTheme.colorScheme.outlineVariant
    Column(Modifier.fillMaxWidth().border(1.dp, line)) {
        Row(Modifier.height(IntrinsicSize.Min)) { HANG_TREN.forEach { CungCell(ls, it, chon, luuNien, daiVan, onPick, Modifier.weight(1f).fillMaxHeight()) } }
        Row(Modifier.height(IntrinsicSize.Min)) {
            Column(Modifier.weight(1f)) {
                CungCell(ls, 4, chon, luuNien, daiVan, onPick, Modifier.fillMaxWidth())
                CungCell(ls, 3, chon, luuNien, daiVan, onPick, Modifier.fillMaxWidth())
            }
            Center(ls, Modifier.weight(2f).fillMaxHeight().border(.5.dp, line))
            Column(Modifier.weight(1f)) {
                CungCell(ls, 9, chon, luuNien, daiVan, onPick, Modifier.fillMaxWidth())
                CungCell(ls, 10, chon, luuNien, daiVan, onPick, Modifier.fillMaxWidth())
            }
        }
        Row(Modifier.height(IntrinsicSize.Min)) { HANG_DUOI.forEach { CungCell(ls, it, chon, luuNien, daiVan, onPick, Modifier.weight(1f).fillMaxHeight()) } }
    }
}

@Composable
private fun Center(ls: TuVi.LaSo, modifier: Modifier) {
    Column(modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("LÁ SỐ TỬ VI", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        if (ls.hoTen.isNotBlank()) Text(ls.hoTen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, textAlign = TextAlign.Center)
        val c = listOf(
            "Dương lịch: ${ls.ngayDuong}/${ls.thangDuong}/${ls.namDuong}",
            "Âm lịch: ${ls.amNgay}/${ls.amThang}${if (ls.amNhuan) " nhuận" else ""}/${ls.amNam}",
            "Năm ${ls.canChiNam} · giờ ${ls.gioSinh}",
            ls.amDuongMenh,
            "Mệnh: ${ls.banMenh}",
            ls.tenCuc,
            "Mệnh ${TuVi.CHI[ls.cungMenh]} · Thân cư ${ls.cung[ls.cungThan].tenCung}",
        )
        c.forEach { Text(it, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp) }
    }
}

@Composable
private fun CungCell(ls: TuVi.LaSo, pos: Int, chon: Int, luuNien: Int, daiVan: Int, onPick: (Int) -> Unit, modifier: Modifier) {
    val c = ls.cung[pos]
    val cs = MaterialTheme.colorScheme
    val menh = pos == ls.cungMenh
    Column(
        modifier
            .background(if (pos == chon) cs.primary.copy(alpha = .10f) else Color.Transparent)
            .border(if (menh) 2.dp else .5.dp, if (menh) cs.primary else cs.outlineVariant)
            .clickable { onPick(pos) }
            .padding(3.dp),
    ) {
        Text(buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = cs.primary)) { append(c.tenCung) }
            if (c.laThan) withStyle(SpanStyle(color = cs.secondary, fontWeight = FontWeight.Bold)) { append(" ·Thân") }
        }, fontSize = 10.sp, lineHeight = 12.sp)
        Text(buildAnnotatedString {
            append(c.canChi)
            if (pos == luuNien) append(" ●")
            if (pos == daiVan) append(" ◆")
        }, fontSize = 9.sp, color = cs.onSurfaceVariant, lineHeight = 11.sp)
        if (c.tuan || c.triet) Text(listOfNotNull("Tuần".takeIf { c.tuan }, "Triệt".takeIf { c.triet }).joinToString(" "),
            fontSize = 9.sp, color = cs.error, lineHeight = 11.sp)
        val chinh = c.chinhTinh()
        if (chinh.isEmpty()) Text("(vô chính diệu)", fontSize = 9.sp, color = cs.onSurfaceVariant, lineHeight = 11.sp)
        chinh.forEach { s ->
            Text(buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(s.ten) }
                append(" ${mvNgan(s.mieuVuong)}")
                if (s.tuHoa != null) withStyle(SpanStyle(color = hoaColor(s.tuHoa), fontWeight = FontWeight.Bold)) { append(" ${hoaNgan(s.tuHoa)}") }
            }, fontSize = 10.sp, lineHeight = 12.sp, color = if (s.mieuVuong == "Hãm") cs.onSurfaceVariant else cs.onSurface)
        }
        val good = goodColor()
        c.sao.filter { it.loai == "phu-tinh" && it.nhom != "tap-dieu" }.forEach { s ->
            Text(s.ten + (s.tuHoa?.let { " " + hoaNgan(it) } ?: ""), fontSize = 9.sp, lineHeight = 11.sp,
                color = if (s.nhom == "sat-tinh") cs.error else good)
        }
        c.sao.firstOrNull { it.loai == "truong-sinh" }?.let { Text(it.ten, fontSize = 9.sp, lineHeight = 11.sp, color = cs.onSurfaceVariant) }
    }
}

@Composable
private fun CungDetail(ls: TuVi.LaSo, pos: Int) {
    val c = ls.cung[pos]
    Section("Cung ${c.tenCung} · ${c.canChi}" + if (c.laThan) " · Thân cư" else "") {
        if (c.tuan || c.triet) Text(listOfNotNull("Tuần trung không vong".takeIf { c.tuan }, "Triệt lộ không vong".takeIf { c.triet }).joinToString(", "),
            color = MaterialTheme.colorScheme.error)
        val nhom = listOf(
            "Chính tinh" to c.sao.filter { it.chinh() },
            "Cát tinh" to c.sao.filter { it.nhom == "cat-tinh" },
            "Sát tinh" to c.sao.filter { it.nhom == "sat-tinh" },
            "Tạp diệu" to c.sao.filter { it.nhom == "tap-dieu" },
            "Vòng Tràng Sinh" to c.sao.filter { it.loai == "truong-sinh" },
            "Vòng Thái Tuế" to c.sao.filter { it.loai == "thai-tue" },
            "Vòng Bác Sỹ" to c.sao.filter { it.loai == "bac-sy" },
        )
        nhom.forEach { (ten, ds) ->
            if (ds.isNotEmpty()) KeyValue(ten, ds.joinToString(", ") { s ->
                s.ten + listOfNotNull(s.mieuVuong, s.tuHoa).joinToString(", ").let { if (it.isEmpty()) "" else " ($it)" }
            })
        }
        if (c.chinhTinh().isEmpty()) {
            val doi = ls.chinhTinhCua(pos + 6)
            Note("Vô chính diệu: mượn chính tinh cung xung chiếu (${TuVi.CHI[(pos + 6) % 12]}): ${doi.ifBlank { "không có" }}.")
        }
        val tp = listOf(4, 6, 8).map { (pos + it) % 12 }
        KeyValue("Tam phương tứ chính", tp.joinToString("; ") { "${ls.cung[it].tenCung} (${TuVi.CHI[it]}): ${ls.chinhTinhCua(it).ifBlank { "vô chính diệu" }}" })
    }
}
