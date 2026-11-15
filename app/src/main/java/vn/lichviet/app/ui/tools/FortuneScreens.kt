@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.lichviet.app.ui.KeyValue
import vn.lichviet.app.ui.Section
import vn.lichviet.app.ui.goodColor
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.BatTrach
import vn.lichviet.core.fortune.BatTu
import vn.lichviet.core.fortune.HopTuoi
import vn.lichviet.core.fortune.MenhInfo
import vn.lichviet.core.fortune.Numerology
import vn.lichviet.core.fortune.Person
import vn.lichviet.core.fortune.SaoHan
import vn.lichviet.core.fortune.Sources
import vn.lichviet.core.fortune.TuViNgay
import vn.lichviet.core.fortune.VanHan
import vn.lichviet.core.fortune.Zodiac
import vn.lichviet.data.Today

internal fun currentLunarYear(): Int = Today.ymd().let { LunarCalendar.solarToLunar(it[0], it[1], it[2])[2] }

@Composable
internal fun badColor() = MaterialTheme.colorScheme.error

/** Dòng nguồn ở cuối màn hình. */
@Composable
fun SourceNote(vararg src: String) {
    Section("Nguồn") {
        src.forEach { Note(it) }
        Note(Sources.DAN_GIAN)
    }
}

@Composable
fun VanHanSection(p: Person, year: Int) {
    val v = remember(p.day, p.month, p.year, p.male, year) { VanHan(p, year) }
    Section("Năm ${v.yearCanChi} ($year) · tuổi mụ ${v.tuoiMu}") {
        KeyValue("Sao chiếu mệnh", SaoHan.SAO[v.sao])
        Note(SaoHan.SAO_Y[v.sao])
        KeyValue("Hạn", SaoHan.HAN[v.han])
        Note(SaoHan.HAN_Y[v.han])
        KeyValue("Kim lâu", v.kimLau ?: "Không phạm")
        KeyValue("Hoang ốc", SaoHan.HOANG_OC[v.hoangOc])
        KeyValue("Tam tai", if (v.tamTai) "Phạm" else "Không phạm")
        v.tot.forEach { Bullet(it, goodColor()) }
        v.xau.forEach { Bullet(it, badColor()) }
        v.khac.forEach { Bullet(it) }
        if (SaoHan.SAO_TOT[v.sao] != 2) KeyValue("Lễ dâng sao (theo phong tục)", SaoHan.CUNG[v.sao])
    }
}

@Composable
fun VanHanScreen() {
    val p = PersonPicker("Xem vận hạn cho") ?: return
    val y = currentLunarYear()
    VanHanSection(p, y)
    VanHanSection(p, y + 1)
    SourceNote(Sources.SAO, Sources.HAN, Sources.KIM_LAU_HOANG_OC_TAM_TAI)
}

@Composable
fun XemSaoScreen() {
    val p = PersonPicker("Xem sao hạn cho") ?: return
    val y = currentLunarYear()
    Section("Sao và hạn 6 năm") {
        for (k in 0 until 6) {
            val t = p.tuoiMu(y + k)
            val s = SaoHan.sao(t, p.male)
            val col = when (SaoHan.SAO_TOT[s]) { 2 -> goodColor(); 0 -> badColor(); else -> MaterialTheme.colorScheme.onSurface }
            Row(Modifier.fillMaxWidth()) {
                Text("${y + k} · $t tuổi", Modifier.weight(1f))
                Text(SaoHan.SAO[s], color = col, fontWeight = FontWeight.Bold)
                Text("  ·  " + SaoHan.HAN[SaoHan.han(t, p.male)])
            }
        }
    }
    val t0 = p.tuoiMu(y)
    val s = SaoHan.sao(t0, p.male)
    Section("Năm nay: sao ${SaoHan.SAO[s]}") {
        Para(SaoHan.SAO_Y[s])
        KeyValue("Lễ dâng sao", SaoHan.CUNG[s])
        val h = SaoHan.han(t0, p.male)
        KeyValue("Hạn ${SaoHan.HAN[h]}", SaoHan.HAN_Y[h])
        KeyValue("Năm tam tai của tuổi", SaoHan.tamTaiText(p.chi))
    }
    SourceNote(Sources.SAO, Sources.HAN)
}

@Composable
fun ThanSoScreen() {
    val p = PersonPicker("Xem thần số học cho") ?: return
    val y = Today.ymd()[2]
    val r = remember(p.name, p.day, p.month, p.year, y) { Numerology.of(p.name, p.day, p.month, p.year, y) }
    Section("Con số chủ đạo") {
        Text(Numerology.label(r.lifePath), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Note("Cộng tất cả chữ số của ${p.day}/${p.month}/${p.year} rồi rút gọn đến khi được 2–11 (tổng 22 giữ là 22/4).")
        Para(Numerology.meaning(r.lifePath))
    }
    Section("Năm cá nhân") {
        for (k in 0..2) {
            val py = Numerology.personalYear(p.day, p.month, y + k)
            KeyValue("Năm ${y + k}", "$py – ${Numerology.keyword(py)}")
        }
        Note("Cộng chữ số ngày, tháng sinh và năm cần xem, rút gọn về 1–9.")
    }
    Section("4 đỉnh cao cuộc đời") {
        val pk = remember(p.day, p.month, p.year) { Numerology.pinnacles(p.day, p.month, p.year) }
        pk.forEachIndexed { i, x ->
            KeyValue("Đỉnh ${i + 1} · tuổi ${x.fromAge}", "${x.number} – ${Numerology.keyword(x.number)}")
        }
        Note("Đáy kim tự tháp là tháng, ngày, năm sinh đã rút gọn. Đỉnh 1 = tháng + ngày, đỉnh 2 = ngày + năm, đỉnh 3 = đỉnh 1 + đỉnh 2, đỉnh 4 = tháng + năm. Tuổi đỉnh 1 = 36 − số chủ đạo, mỗi đỉnh sau cộng 9 năm.")
    }
    if (r.expression > 0) Section("Chỉ số theo tên (bảng chữ cái Pythagoras A=1 … I=9)") {
        KeyValue("Số sứ mệnh (cả họ tên)", "${r.expression} – ${Numerology.keyword(r.expression)}")
        KeyValue("Số linh hồn (nguyên âm)", "${r.soul} – ${Numerology.keyword(r.soul)}")
        KeyValue("Số nhân cách (phụ âm)", "${r.personality} – ${Numerology.keyword(r.personality)}")
        Note("Số linh hồn: cộng các nguyên âm A, E, I, O, U trong họ tên khai sinh (bỏ dấu) — thể hiện mong muốn, động lực bên trong. Số nhân cách: cộng các phụ âm. Số sứ mệnh: cộng toàn bộ chữ cái.")
    }
    SourceNote(Sources.THAN_SO)
}

@Composable
fun HoangDao12Screen(@Suppress("UNUSED_PARAMETER") todayJd: Int) {
    val me = mySettings().toPerson()
    val mine = me?.let { Zodiac.of(it.day, it.month) }
    if (mine != null) ZodiacCard(mine, true)
    for (i in 0 until 12) if (i != mine) ZodiacCard(i, false)
    SourceNote(Sources.ZODIAC)
}

@Composable
private fun ZodiacCard(i: Int, mine: Boolean) {
    var open by remember { mutableStateOf(mine) }
    Section("${Zodiac.SYMBOL[i]}  ${Zodiac.NAME[i]} (${Zodiac.ENGLISH[i]})${if (mine) " · cung của bạn" else ""}",
        modifier = Modifier.clickable { open = !open }) {
        KeyValue("Ngày sinh", Zodiac.RANGE[i])
        KeyValue("Nguyên tố", Zodiac.ELEMENT[i])
        if (!open) { Note("Chạm để xem chi tiết"); return@Section }
        KeyValue("Tính chất", Zodiac.MODALITY[i])
        KeyValue("Hành tinh chủ quản", Zodiac.PLANET[i])
        KeyValue("Biểu tượng", Zodiac.EMBLEM[i])
        KeyValue("Cung đối diện", Zodiac.NAME[Zodiac.opposite(i)])
        KeyValue("Cùng nguyên tố", Zodiac.sameElement(i))
        KeyValue("Hợp với", Zodiac.compatible(i))
        Para(Zodiac.TRAIT[i])
        Bullet("Ưu điểm: ${Zodiac.PROS[i]}", goodColor())
        Bullet("Nhược điểm: ${Zodiac.CONS[i]}", badColor())
    }
}

@Composable
fun HopNhauScreen(love: Boolean) {
    val a = PersonPicker(if (love) "Bạn" else "Người thứ nhất", true, if (love) "love-a" else "hop-a")
    val b = PersonPicker(if (love) "Người ấy" else "Người thứ hai", false, if (love) "love-b" else "hop-b")
    if (a == null || b == null) { Note("Nhập đủ ngày sinh hai người để xem."); return }
    val r = remember(a.year, a.month, a.day, a.male, b.year, b.month, b.day, b.male) { HopTuoi.compare(a, b) }
    Section("${a.canChi()} và ${b.canChi()}") {
        r.good.forEach { Bullet(it, goodColor()) }
        r.bad.forEach { Bullet(it, badColor()) }
        r.neutral.forEach { Bullet(it) }
        Note("Mỗi mục là một quy tắc riêng; ứng dụng không cộng thành điểm vì không có thang điểm chuẩn.")
    }
    if (love) {
        val wife = if (!a.male) a else if (!b.male) b else null
        if (wife != null) {
            val kl = SaoHan.kimLau(wife.tuoiMu(currentLunarYear()))
            Section("Kim lâu của bên nữ năm nay") { Para(kl ?: "Không phạm Kim lâu") }
        }
    }
    SourceNote(Sources.NGU_HANH, Sources.BAT_TRACH, Sources.KIM_LAU_HOANG_OC_TAM_TAI)
}

@Composable
fun TinhDuyenScreen() {
    val p = PersonPicker("Xem cho") ?: return
    val h = p.menh()
    Section("Tuổi ${p.canChi()} · mệnh ${p.napAm()}") {
        KeyValue("Tam hợp, lục hợp", HopTuoi.chiHop(p.chi))
        KeyValue("Lục xung, lục hại, tứ hành xung", HopTuoi.chiKy(p.chi))
        KeyValue("Mệnh tương sinh", "${HopTuoi.sinhRa(h)} sinh $h; $h sinh ${sinhCua(h)}")
        KeyValue("Mệnh tương khắc", "${HopTuoi.biKhac(h)} khắc $h; $h khắc ${HopTuoi.khac(h)}")
    }
    val y = currentLunarYear()
    Section("Kim lâu các năm tới") {
        for (k in 0 until 6) {
            val t = p.tuoiMu(y + k)
            KeyValue("${y + k} (tuổi mụ $t)", SaoHan.kimLau(t) ?: "Không phạm")
        }
        Note("Dân gian thường xét Kim lâu theo tuổi mụ của người nữ khi cưới.")
    }
    SourceNote(Sources.NGU_HANH, Sources.KIM_LAU_HOANG_OC_TAM_TAI)
}

private fun sinhCua(h: String) = mapOf("Kim" to "Thủy", "Thủy" to "Mộc", "Mộc" to "Hỏa", "Hỏa" to "Thổ", "Thổ" to "Kim")[h] ?: ""

@Composable
fun PhongThuyScreen() {
    val p = PersonPicker("Xem phong thủy cho") ?: return
    val c = BatTrach.cung(p.lunarYear, p.male)
    val h = BatTrach.huong(c)
    Section("Cung ${BatTrach.CUNG[c]} – ${if (BatTrach.dongTuMenh(c)) "Đông" else "Tây"} tứ mệnh") {
        KeyValue("Hành cung", BatTrach.HANH_CUNG[c])
        KeyValue("Mệnh nạp âm", p.napAm())
    }
    Section("Bốn hướng tốt") {
        for (i in 0 until 4) Row {
            Text(BatTrach.HUONG_TEN[i], Modifier.weight(1f), fontWeight = FontWeight.Bold, color = goodColor())
            Text("${h[i]} · ${BatTrach.HUONG_Y[i]}")
        }
    }
    Section("Bốn hướng xấu") {
        for (i in 4 until 8) Row {
            Text(BatTrach.HUONG_TEN[i], Modifier.weight(1f), fontWeight = FontWeight.Bold, color = badColor())
            Text("${h[i]} · ${BatTrach.HUONG_Y[i]}")
        }
    }
    Section("Màu theo mệnh ${p.menh()}") {
        KeyValue("Màu hợp", MenhInfo.mauHop(p.menh()))
        KeyValue("Màu kỵ", MenhInfo.mauKy(p.menh()))
    }
    SourceNote(Sources.BAT_TRACH, Sources.NGU_HANH)
}

@Composable
fun TuViTronDoiScreen() {
    val p = PersonPicker("Xem cho") ?: return
    val c = BatTrach.cung(p.lunarYear, p.male)
    Section("Tuổi ${p.canChi()} – ${p.gioi()}") {
        KeyValue("Năm sinh âm", "${p.lunarYear} (${p.conGiap()})")
        KeyValue("Mệnh", p.napAm())
        KeyValue("Cung phi", "${BatTrach.CUNG[c]} (${if (BatTrach.dongTuMenh(c)) "Đông" else "Tây"} tứ mệnh)")
        KeyValue("Số chủ đạo", Numerology.label(Numerology.lifePath(p.day, p.month, p.year)))
        KeyValue("Tuổi hợp", HopTuoi.chiHop(p.chi))
        KeyValue("Tuổi kỵ", HopTuoi.chiKy(p.chi))
        KeyValue("Hướng tốt", BatTrach.huong(c).take(4).joinToString(", "))
        KeyValue("Màu hợp", MenhInfo.mauHop(p.menh()))
    }
    Section("Sao chiếu mệnh theo tuổi (10–80)") {
        val tot = (10..80).filter { SaoHan.SAO_TOT[SaoHan.sao(it, p.male)] == 2 }
        val xau = (10..80).filter { SaoHan.SAO_TOT[SaoHan.sao(it, p.male)] == 0 }
        KeyValue("Gặp sao tốt", tot.joinToString(", "))
        KeyValue("Gặp sao xấu", xau.joinToString(", "))
    }
    Section("Hạn, Kim lâu, Tam tai") {
        val dai = (10..80).filter { SaoHan.han(it, p.male).let { h -> h == 0 || h == 4 } }
        KeyValue("Đại hạn (Huỳnh Tuyền, Toán Tận)", dai.joinToString(", "))
        KeyValue("Kim lâu", (16..60).filter { SaoHan.kimLau(it) != null }.joinToString(", "))
        KeyValue("Năm tam tai", SaoHan.tamTaiText(p.chi))
    }
    SourceNote(Sources.SAO, Sources.HAN, Sources.KIM_LAU_HOANG_OC_TAM_TAI, Sources.BAT_TRACH, Sources.THAN_SO)
}

@Composable
fun TongQuanVanMenhScreen() {
    val p = PersonPicker("Xem cho") ?: return
    val y = currentLunarYear()
    val v = VanHan(p, y)
    val z = Zodiac.of(p.day, p.month)
    Section("Tổng quan") {
        KeyValue("Tuổi", "${p.canChi()} · mệnh ${p.napAm()}")
        KeyValue("Cung phi", BatTrach.CUNG[BatTrach.cung(p.lunarYear, p.male)])
        KeyValue("Số chủ đạo", Numerology.label(Numerology.lifePath(p.day, p.month, p.year)))
        KeyValue("Cung hoàng đạo", Zodiac.NAME[z])
    }
    VanHanSection(p, y)
    BatTuSummary(p)
    SourceNote(Sources.SAO, Sources.HAN, Sources.BAT_TRACH, Sources.THAN_SO, Sources.BAT_TU)
}

@Composable
fun ThongTinScreen() {
    val p = PersonPicker("Thông tin của") ?: return
    val d = remember(p.day, p.month, p.year) { DayInfo.of(p.day, p.month, p.year) }
    Section("Ngày sinh") {
        KeyValue("Dương lịch", "${DayInfo.THU[d.weekday]}, ${p.day}/${p.month}/${p.year}")
        KeyValue("Âm lịch", "${d.lunarDay}/${d.lunarMonth}${if (d.leap) " (nhuận)" else ""}/${d.lunarYear}")
        KeyValue("Ngày", d.dayCanChi)
        KeyValue("Tháng", d.monthCanChi)
        KeyValue("Năm", d.yearCanChi)
        KeyValue("Tiết khí", d.tietKhiCurrent)
    }
    Section("Bản mệnh") {
        KeyValue("Mệnh", p.napAm())
        KeyValue("Con giáp", p.conGiap())
        KeyValue("Cung phi", BatTrach.CUNG[BatTrach.cung(p.lunarYear, p.male)])
        KeyValue("Cung hoàng đạo", Zodiac.NAME[Zodiac.of(p.day, p.month)])
        KeyValue("Số chủ đạo", Numerology.label(Numerology.lifePath(p.day, p.month, p.year)))
        KeyValue("Tuổi mụ năm nay", p.tuoiMu(currentLunarYear()).toString())
    }
    SourceNote(Sources.BAT_TRACH, Sources.THAN_SO)
}

@Composable
fun GiaiMaNgaySinhScreen() {
    val p = PersonPicker("Ngày sinh cần giải mã", key = "giaima") ?: return
    val d = remember(p.day, p.month, p.year) { DayInfo.of(p.day, p.month, p.year) }
    val lp = Numerology.lifePath(p.day, p.month, p.year)
    Section("Sinh vào ${DayInfo.THU[d.weekday]}") {
        KeyValue("Âm lịch", "${d.lunarDay}/${d.lunarMonth}/${d.lunarYear}")
        KeyValue("Can chi", "Ngày ${d.dayCanChi}, tháng ${d.monthCanChi}, năm ${d.yearCanChi}")
        KeyValue("Tiết khí", d.tietKhiCurrent)
        KeyValue("Ngày", "${if (d.hoangDao) "Hoàng đạo" else "Hắc đạo"} (${d.than12}), trực ${d.truc}")
        KeyValue("Nạp âm năm", p.napAm())
        KeyValue("Cung hoàng đạo", Zodiac.NAME[Zodiac.of(p.day, p.month)])
    }
    Section("Số chủ đạo ${Numerology.label(lp)}") { Para(Numerology.meaning(lp)) }
    SourceNote(Sources.THAN_SO)
}

@Composable
fun TuViNgayScreen(todayJd: Int) {
    val p = PersonPicker("Ngày hợp tuổi cho") ?: return
    for (k in 0..1) {
        val jd = todayJd + k
        val d = remember(jd) { LunarCalendar.jdToDate(jd).let { DayInfo.of(it[0], it[1], it[2]) } }
        val t = remember(jd, p.day, p.month, p.year, p.male) { TuViNgay(p, d) }
        Section((if (k == 0) "Hôm nay" else "Ngày mai") + " – ${d.solarDay}/${d.solarMonth} (${d.dayCanChi})") {
            KeyValue("Ngày", "${if (d.hoangDao) "Hoàng đạo" else "Hắc đạo"} (${d.than12}) · trực ${d.truc}")
            t.tot.forEach { Bullet(it, goodColor()) }
            t.xau.forEach { Bullet(it, badColor()) }
            if (t.tot.isEmpty() && t.xau.isEmpty()) Note("Không có quan hệ hợp hay xung đáng kể với tuổi.")
            KeyValue("Giờ hoàng đạo", t.gioTot)
        }
    }
    SourceNote(Sources.NGU_HANH, "Quan hệ địa chi: tam hợp, lục hợp, lục xung, lục hại theo can chi truyền thống.")
}

@Composable
fun NoiBatScreen(todayJd: Int, onOpenDay: (Int) -> Unit) {
    val d = remember(todayJd) { LunarCalendar.jdToDate(todayJd).let { DayInfo.of(it[0], it[1], it[2]) } }
    val a = remember(todayJd) { d.almanac() }
    Section("${DayInfo.THU[d.weekday]}, ${d.solarDay}/${d.solarMonth}/${d.solarYear}") {
        KeyValue("Âm lịch", "${d.lunarDay}/${d.lunarMonth} năm ${d.yearCanChi}")
        KeyValue("Ngày", "${d.dayCanChi} – ${if (d.hoangDao) "Hoàng đạo" else "Hắc đạo"} (${d.than12})")
        KeyValue("Giờ hoàng đạo", d.hours.filter { it.good }.joinToString(", ") { "${it.name} (${it.range})" })
        KeyValue("Xuất hành", "Hỷ thần ${d.hyThan} · Tài thần ${d.taiThan}")
        KeyValue("Tuổi xung", d.chiXung)
        KeyValue("Khổng Minh lục diệu", a.khongMinh)
    }
    if (d.nenLam.isNotEmpty()) Section("Nên làm") { d.nenLam.forEach { Bullet(it, goodColor()) } }
    if (d.kyLam.isNotEmpty()) Section("Không nên") { d.kyLam.forEach { Bullet(it, badColor()) } }
    val p = mySettings().toPerson()
    if (p != null) {
        val t = remember(todayJd, p.year, p.month, p.day) { TuViNgay(p, d) }
        Section("Với tuổi ${p.canChi()}") {
            t.tot.forEach { Bullet(it, goodColor()) }
            t.xau.forEach { Bullet(it, badColor()) }
            if (t.tot.isEmpty() && t.xau.isEmpty()) Note("Không có quan hệ hợp hay xung đáng kể.")
        }
    }
    TextButton(onClick = { onOpenDay(todayJd) }) { Text("Xem chi tiết ngày hôm nay") }
}

@Composable
fun BatTuScreen() {
    val p = PersonPicker("Lá số của", key = "battu") ?: return
    BatTuSummary(p)
    if (p.hour < 0) Note("Chưa có giờ sinh nên chỉ lập 3 trụ. Nhập giờ sinh ở hồ sơ hoặc mục Người khác để có đủ 4 trụ.")
    SourceNote(Sources.BAT_TU)
}

@Composable
fun BatTuSummary(p: Person) {
    val b = remember(p.day, p.month, p.year, p.hour, p.male) { runCatching { BatTu(p) }.getOrNull() } ?: return
    Section("Tứ trụ – Nhật chủ ${b.nhatChu} (${b.hanhNhatChu})") {
        b.pillars.forEachIndexed { i, pl ->
            Column(Modifier.padding(vertical = 4.dp)) {
                Text("Trụ ${BatTu.TRU[i]}: ${pl.can} ${pl.chi}", fontWeight = FontWeight.Bold)
                Note("Nạp âm ${pl.napAm} · can ${pl.hanhCan}, chi ${pl.hanhChi}")
                Note("Thập thần: ${pl.thapThanCan} · Tàng can: ${pl.tangCan.joinToString(", ")} (${pl.thapThanChi.joinToString(", ")})")
                Note("Trường sinh: ${pl.truongSinh}")
            }
        }
    }
    Section("Số chữ theo ngũ hành (8 chữ can chi)") {
        BatTu.HANH.forEachIndexed { i, h -> KeyValue(h, b.hanhCount[i].toString()) }
    }
    if (b.daiVan.isNotEmpty()) Section("Đại vận") { b.daiVan.forEach { Note(it) } }
}
