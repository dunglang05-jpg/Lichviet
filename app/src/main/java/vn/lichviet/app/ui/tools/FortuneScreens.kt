@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package vn.lichviet.app.ui.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.lichviet.app.ui.KeyValue
import vn.lichviet.app.ui.ScoreRing
import vn.lichviet.app.ui.Section
import vn.lichviet.app.ui.goodColor
import vn.lichviet.core.DayInfo
import vn.lichviet.core.LunarCalendar
import vn.lichviet.core.fortune.BatTrach
import vn.lichviet.core.fortune.HopTuoi
import vn.lichviet.core.fortune.MenhInfo
import vn.lichviet.core.fortune.Numerology
import vn.lichviet.core.fortune.Person
import vn.lichviet.core.fortune.SaoHan
import vn.lichviet.core.fortune.TuViNgay
import vn.lichviet.core.fortune.VanHan
import vn.lichviet.core.fortune.Zodiac
import vn.lichviet.data.Today

private fun currentLunarYear(): Int = Today.ymd().let { LunarCalendar.solarToLunar(it[0], it[1], it[2])[2] }

@Composable
private fun badColor() = MaterialTheme.colorScheme.error

@Composable
fun VanHanSection(p: Person, year: Int) {
    val v = remember(p.day, p.month, p.year, p.male, year) { VanHan(p, year) }
    Section("Năm ${v.yearCanChi} ($year) · tuổi mụ ${v.tuoiMu}") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(v.verdict(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Stars(v.score)
            }
            ScoreRing(v.score * 20, 64.dp)
        }
        KeyValue("Sao chiếu mệnh", SaoHan.SAO[v.sao])
        Para(SaoHan.SAO_Y[v.sao])
        KeyValue("Hạn làm nhà", SaoHan.HOANG_OC[v.hoangOc])
        KeyValue("Kim lâu", v.kimLau ?: "Không phạm")
        KeyValue("Tam tai", if (v.tamTai) "Phạm" else "Không phạm")
        v.tot.forEach { Bullet(it, goodColor()) }
        v.xau.forEach { Bullet(it, badColor()) }
        Text("Lời khuyên", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
        v.loiKhuyen.forEach { Bullet(it) }
    }
}

@Composable
fun VanHanScreen() {
    val p = PersonPicker("Xem vận hạn cho") ?: return
    val y = currentLunarYear()
    VanHanSection(p, y)
    VanHanSection(p, y + 1)
    Note("Thông tin mang tính tham khảo theo phong tục dân gian.")
}

@Composable
fun XemSaoScreen() {
    val p = PersonPicker("Xem sao hạn cho") ?: return
    val y = currentLunarYear()
    Section("Sao chiếu mệnh 5 năm") {
        for (k in 0 until 5) {
            val t = p.tuoiMu(y + k)
            val s = SaoHan.sao(t, p.male)
            val col = when (SaoHan.SAO_TOT[s]) { 2 -> goodColor(); 0 -> badColor(); else -> MaterialTheme.colorScheme.onSurface }
            Row(Modifier.fillMaxWidth()) {
                Text("${y + k} (${t} tuổi)", Modifier.weight(1f))
                Text(SaoHan.SAO[s], color = col, fontWeight = FontWeight.Bold)
            }
        }
    }
    val s = SaoHan.sao(p.tuoiMu(y), p.male)
    Section("Năm nay: sao ${SaoHan.SAO[s]}") {
        Para(SaoHan.SAO_Y[s])
        KeyValue("Cúng giải", SaoHan.CUNG[s])
        KeyValue("Năm tam tai", SaoHan.tamTaiText(p.chi))
    }
    Section("Các tuổi gặp sao xấu nặng") {
        Para(SaoHan.badYears(p.male).joinToString(", ") { "${it[0]} (${SaoHan.SAO[it[1]]})" })
    }
}

@Composable
fun ThanSoScreen() {
    val p = PersonPicker("Xem thần số học cho") ?: return
    val y = Today.ymd()[2]
    val r = remember(p.name, p.day, p.month, p.year, y) { Numerology.of(p.name, p.day, p.month, p.year, y) }
    Section("Con số chủ đạo") {
        Text(r.lifePath.toString(), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(Numerology.title(r.lifePath), fontWeight = FontWeight.Bold)
        Para(Numerology.meaning(r.lifePath))
    }
    Section("Các chỉ số khác") {
        KeyValue("Số ngày sinh", "${r.birthDay} – ${Numerology.title(r.birthDay).substringAfter("– ")}")
        KeyValue("Số thái độ", "${r.attitude} – ${Numerology.title(r.attitude).substringAfter("– ")}")
        if (r.expression > 0) {
            KeyValue("Số sứ mệnh (tên)", r.expression.toString())
            KeyValue("Số linh hồn (nguyên âm)", r.soul.toString())
            KeyValue("Số nhân cách (phụ âm)", r.personality.toString())
        }
        KeyValue("Năm cá nhân $y", r.personalYear.toString())
        Para(Numerology.yearMeaning(r.personalYear))
    }
}

@Composable
fun HoangDao12Screen(todayJd: Int) {
    val s = mySettings()
    val me = s.toPerson()
    val mine = me?.let { Zodiac.of(it.day, it.month) }
    if (mine != null) ZodiacCard(mine, todayJd, true)
    for (i in 0 until 12) if (i != mine) ZodiacCard(i, todayJd, false)
}

@Composable
private fun ZodiacCard(i: Int, jd: Int, mine: Boolean) {
    Section("${Zodiac.SYMBOL[i]}  ${Zodiac.NAME[i]}${if (mine) " · cung của bạn" else ""}") {
        KeyValue("Ngày sinh", Zodiac.RANGE[i])
        KeyValue("Nguyên tố", Zodiac.ELEMENT[i])
        KeyValue("Hành tinh", Zodiac.PLANET[i])
        Para(Zodiac.TRAITS[i])
        KeyValue("Hợp với", Zodiac.compatible(i))
        Text("Hôm nay: " + Zodiac.daily(i, jd), color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun HopNhauScreen(love: Boolean) {
    val a = PersonPicker(if (love) "Bạn" else "Người thứ nhất", true, if (love) "love-a" else "hop-a")
    val b = PersonPicker(if (love) "Người ấy" else "Người thứ hai", false, if (love) "love-b" else "hop-b")
    if (a == null || b == null) { Note("Nhập đủ ngày sinh hai người để xem."); return }
    val r = remember(a.year, a.month, a.day, a.male, b.year, b.month, b.day, b.male) { HopTuoi.compare(a, b) }
    Section("Kết quả: ${r.verdict}") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("${r.score}/10 điểm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${a.canChi()} (${a.napAm()}) và ${b.canChi()} (${b.napAm()})", style = MaterialTheme.typography.bodySmall)
            }
            ScoreRing(r.score * 10, 64.dp)
        }
        r.good.forEach { Bullet(it, goodColor()) }
        r.bad.forEach { Bullet(it, badColor()) }
    }
    if (love) {
        val na = Numerology.lifePath(a.day, a.month, a.year)
        val nb = Numerology.lifePath(b.day, b.month, b.year)
        val za = Zodiac.of(a.day, a.month); val zb = Zodiac.of(b.day, b.month)
        Section("Thần số học & cung hoàng đạo") {
            KeyValue("Số chủ đạo", "$na và $nb")
            KeyValue("Cung", "${Zodiac.NAME[za]} và ${Zodiac.NAME[zb]}")
            val zHop = Zodiac.compatible(za).contains(Zodiac.NAME[zb]) || za == zb
            Para(if (zHop) "Hai cung có nguyên tố hòa hợp, dễ thấu hiểu nhau." else "Hai cung khác tính, cần lắng nghe và tôn trọng khác biệt.")
            Para(MenhInfo.tinhDuyen(a.menh()))
        }
    }
    Note("Kết quả mang tính tham khảo; tình cảm bền lâu nhờ sự thấu hiểu của hai người.")
}

enum class MenhTopic { TINH_DUYEN, NGHE, TAI_CHINH }

@Composable
fun MenhAdviceScreen(topic: MenhTopic) {
    val p = PersonPicker("Xem cho") ?: return
    val h = p.menh()
    when (topic) {
        MenhTopic.TINH_DUYEN -> {
            Section("Tình duyên mệnh $h") {
                Para(MenhInfo.tinhDuyen(h))
                KeyValue("Tuổi hợp (địa chi)", HopTuoi.chiHop(p.chi))
                KeyValue("Tuổi nên tránh", HopTuoi.chiKy(p.chi))
                KeyValue("Mệnh hợp", "$h, ${HopTuoi.sinhRa(h)}, ${HANH_SINH[h]}")
                KeyValue("Mệnh khắc", HopTuoi.biKhac(h) + ", " + HopTuoi.khac(h))
                val kl = SaoHan.kimLau(p.tuoiMu(currentLunarYear()))
                if (!p.male) KeyValue("Kim lâu năm nay", kl ?: "Không phạm – thuận cưới hỏi")
            }
            Section("Tính cách theo con giáp") { Para(MenhInfo.CON_GIAP[p.chi]) }
        }
        MenhTopic.NGHE -> {
            val lp = Numerology.lifePath(p.day, p.month, p.year)
            Section("Ngành nghề hợp mệnh $h") { Para(MenhInfo.nghe(h)) }
            Section("Tính cách") { Para(MenhInfo.tinhCach(h)); Para(MenhInfo.CON_GIAP[p.chi]) }
            Section("Theo con số chủ đạo $lp") { Para(Numerology.meaning(lp)) }
            val c = BatTrach.cung(p.lunarYear, p.male)
            Section("Hướng bàn làm việc") {
                KeyValue("Sinh Khí", BatTrach.huong(c)[0])
                KeyValue("Thiên Y", BatTrach.huong(c)[1])
            }
        }
        MenhTopic.TAI_CHINH -> {
            val v = VanHan(p, currentLunarYear())
            Section("Xu hướng tài chính mệnh $h") { Para(MenhInfo.taiChinh(h)) }
            Section("Năm nay") {
                KeyValue("Vận năm", v.verdict())
                Para(if (v.score >= 4) "Năm thuận để mở rộng nguồn thu, đầu tư vào kỹ năng và công việc chính."
                    else if (v.score <= 2) "Năm nên phòng thủ: giữ quỹ dự phòng, tránh vay mượn và quyết định vội vàng."
                    else "Năm ổn định: tích lũy đều đặn, chi tiêu có kế hoạch.")
                KeyValue("Màu may mắn", MenhInfo.mauHop(h))
                KeyValue("Số hợp", MenhInfo.soHop(h))
                KeyValue("Hướng tài lộc (Sinh Khí)", BatTrach.huong(BatTrach.cung(p.lunarYear, p.male))[0])
            }
            Note("Gợi ý theo phong tục, không phải tư vấn tài chính chuyên nghiệp.")
        }
    }
}

private val HANH_SINH = mapOf("Kim" to "Thủy", "Thủy" to "Mộc", "Mộc" to "Hỏa", "Hỏa" to "Thổ", "Thổ" to "Kim")

@Composable
fun PhongThuyScreen() {
    val p = PersonPicker("Xem phong thủy cho") ?: return
    val c = BatTrach.cung(p.lunarYear, p.male)
    val h = BatTrach.huong(c)
    Section("Cung ${BatTrach.CUNG[c]} – ${if (BatTrach.dongTuMenh(c)) "Đông" else "Tây"} tứ mệnh") {
        KeyValue("Hành cung", BatTrach.HANH_CUNG[c])
        KeyValue("Mệnh nạp âm", p.napAm())
    }
    Section("Hướng tốt") {
        for (i in 0 until 4) {
            Row { Text(BatTrach.HUONG_TEN[i], Modifier.weight(1f), fontWeight = FontWeight.Bold, color = goodColor()); Text(h[i], fontWeight = FontWeight.Bold) }
            Note(BatTrach.HUONG_Y[i])
        }
    }
    Section("Hướng xấu") {
        for (i in 4 until 8) {
            Row { Text(BatTrach.HUONG_TEN[i], Modifier.weight(1f), fontWeight = FontWeight.Bold, color = badColor()); Text(h[i], fontWeight = FontWeight.Bold) }
            Note(BatTrach.HUONG_Y[i])
        }
    }
    val m = p.menh()
    Section("Theo mệnh $m") {
        KeyValue("Màu hợp", MenhInfo.mauHop(m))
        KeyValue("Màu nên tránh", MenhInfo.mauKy(m))
        KeyValue("Con số hợp", MenhInfo.soHop(m))
        KeyValue("Cây hợp mệnh", MenhInfo.cayHop(m))
    }
    Section("Gợi ý bố trí") {
        Bullet("Cửa chính, bàn làm việc nhìn về hướng ${h[0]} (Sinh Khí).")
        Bullet("Đầu giường hướng ${h[2]} (Diên Niên) để gia đạo hòa thuận.")
        Bullet("Bàn thờ, bàn học hướng ${h[3]} (Phục Vị) hoặc ${h[1]} (Thiên Y).")
        Bullet("Bếp nên đặt ở vùng xấu (${h[4]}, ${h[5]}) và miệng bếp nhìn về hướng tốt.")
    }
}

@Composable
fun TuViTronDoiScreen() {
    val p = PersonPicker("Xem tử vi trọn đời cho") ?: return
    val m = p.menh()
    val c = BatTrach.cung(p.lunarYear, p.male)
    Section("Tuổi ${p.canChi()} – ${p.gioi()} mạng") {
        KeyValue("Năm sinh âm", "${p.lunarYear} (${p.conGiap()})")
        KeyValue("Mệnh", p.napAm())
        KeyValue("Cung phi", BatTrach.CUNG[c])
        KeyValue("Con số chủ đạo", Numerology.lifePath(p.day, p.month, p.year).toString())
    }
    Section("Tính cách") { Para(MenhInfo.CON_GIAP[p.chi]); Para(MenhInfo.tinhCach(m)) }
    Section("Sự nghiệp") { Para(MenhInfo.nghe(m)) }
    Section("Tài lộc") { Para(MenhInfo.taiChinh(m)) }
    Section("Tình duyên – gia đạo") {
        Para(MenhInfo.tinhDuyen(m))
        KeyValue("Tuổi hợp", HopTuoi.chiHop(p.chi))
        KeyValue("Tuổi kỵ", HopTuoi.chiKy(p.chi))
    }
    Section("Diễn biến theo tuổi") {
        val list = (20..80 step 1).map { t -> t to SaoHan.sao(t, p.male) }
        val good = list.filter { SaoHan.SAO_TOT[it.second] == 2 }.map { it.first }
        val bad = list.filter { SaoHan.SAO_TOT[it.second] == 0 && (it.second == 0 || it.second == 6) }.map { it.first }
        KeyValue("Tuổi gặp sao tốt", good.joinToString(", "))
        KeyValue("Tuổi cần cẩn trọng", bad.joinToString(", "))
        KeyValue("Năm tam tai", SaoHan.tamTaiText(p.chi))
    }
    Section("Màu sắc – con số") {
        KeyValue("Màu hợp", MenhInfo.mauHop(m))
        KeyValue("Số hợp", MenhInfo.soHop(m))
        KeyValue("Hướng tốt", BatTrach.huong(c).take(4).joinToString(", "))
    }
}

@Composable
fun TongQuanVanMenhScreen() {
    val p = PersonPicker("Xem cho") ?: return
    val y = currentLunarYear()
    val v = VanHan(p, y)
    val lp = Numerology.lifePath(p.day, p.month, p.year)
    val z = Zodiac.of(p.day, p.month)
    Section("Bức tranh tổng quan") {
        KeyValue("Tuổi", "${p.canChi()} · mệnh ${p.napAm()}")
        KeyValue("Cung phi", BatTrach.CUNG[BatTrach.cung(p.lunarYear, p.male)])
        KeyValue("Số chủ đạo", "$lp – ${Numerology.title(lp).substringAfter("– ")}")
        KeyValue("Cung hoàng đạo", Zodiac.NAME[z])
        KeyValue("Vận năm $y", "${v.verdict()} (sao ${SaoHan.SAO[v.sao]})")
    }
    Section("Điểm mạnh") { Para(Numerology.meaning(lp)) }
    Section("Năm nay") { v.loiKhuyen.forEach { Bullet(it) } }
    Section("Định hướng") {
        Bullet("Nghề hợp: " + MenhInfo.nghe(p.menh()))
        Bullet("Tài chính: " + MenhInfo.taiChinh(p.menh()))
    }
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
        KeyValue("Số chủ đạo", Numerology.lifePath(p.day, p.month, p.year).toString())
        KeyValue("Tuổi mụ năm nay", p.tuoiMu(currentLunarYear()).toString())
    }
}

@Composable
fun GiaiMaNgaySinhScreen() {
    val p = PersonPicker("Ngày sinh cần giải mã", key = "giaima") ?: return
    val d = remember(p.day, p.month, p.year) { DayInfo.of(p.day, p.month, p.year) }
    val lp = Numerology.lifePath(p.day, p.month, p.year)
    val z = Zodiac.of(p.day, p.month)
    Section("Sinh vào ${DayInfo.THU[d.weekday]}") {
        Para("Ngày ${d.dayCanChi}, tháng ${d.monthCanChi}, năm ${d.yearCanChi}, tiết ${d.tietKhiCurrent}. Ngày sinh âm lịch ${d.lunarDay}/${d.lunarMonth}.")
        Para("Ngày sinh là ngày ${if (d.hoangDao) "hoàng đạo" else "hắc đạo"} (${d.than12}), trực ${d.truc}.")
    }
    Section("Con số ngày sinh ${Numerology.reduce(p.day)}") { Para(Numerology.meaning(Numerology.reduce(p.day))) }
    Section("Con số chủ đạo $lp") { Para(Numerology.meaning(lp)) }
    Section("Cung ${Zodiac.NAME[z]}") { Para(Zodiac.TRAITS[z]) }
    Section("Mệnh ${p.napAm()}") { Para(MenhInfo.tinhCach(p.menh())) }
}

@Composable
fun TuViNgayScreen(todayJd: Int) {
    val p = PersonPicker("Tử vi hôm nay cho") ?: return
    for (k in 0..1) {
        val jd = todayJd + k
        val d = remember(jd) { LunarCalendar.jdToDate(jd).let { DayInfo.of(it[0], it[1], it[2]) } }
        val t = remember(jd, p.day, p.month, p.year, p.male) { TuViNgay(p, d) }
        Section((if (k == 0) "Hôm nay" else "Ngày mai") + " – ${d.solarDay}/${d.solarMonth} (${d.dayCanChi})") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Para(t.tongQuan) }
                ScoreRing(t.total, 56.dp)
            }
            AspectRow("Công việc", t.congViec, t.congViecText)
            AspectRow("Tài lộc", t.taiLoc, t.taiLocText)
            AspectRow("Tình cảm", t.tinhCam, t.tinhCamText)
            AspectRow("Sức khỏe", t.sucKhoe, t.sucKhoeText)
            KeyValue("Màu may mắn", t.mauMayMan)
            KeyValue("Con số may mắn", t.soMayMan)
            KeyValue("Giờ tốt", t.gioTot)
        }
    }
}

@Composable
private fun AspectRow(label: String, v: Int, text: String) {
    Column(Modifier.padding(vertical = 2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Stars(v)
        }
        Note(text)
    }
}

@Composable
fun NoiBatScreen(todayJd: Int, onOpenDay: (Int) -> Unit) {
    val d = remember(todayJd) { LunarCalendar.jdToDate(todayJd).let { DayInfo.of(it[0], it[1], it[2]) } }
    val a = remember(todayJd) { d.almanac() }
    Section("${DayInfo.THU[d.weekday]}, ${d.solarDay}/${d.solarMonth}/${d.solarYear}") {
        KeyValue("Âm lịch", "${d.lunarDay}/${d.lunarMonth} năm ${d.yearCanChi}")
        KeyValue("Ngày", "${d.dayCanChi} – ${if (d.hoangDao) "Hoàng đạo" else "Hắc đạo"} (${d.than12})")
        KeyValue("Đánh giá", d.verdict)
        KeyValue("Giờ tốt", d.hours.filter { it.good }.joinToString(", ") { "${it.name} (${it.range})" })
        KeyValue("Xuất hành", "Hỷ thần ${d.hyThan} · Tài thần ${d.taiThan}")
        KeyValue("Tuổi xung", d.chiXung)
        KeyValue("Khổng Minh", a.khongMinh)
    }
    if (d.nenLam.isNotEmpty()) Section("Nên làm") { d.nenLam.forEach { Bullet(it, goodColor()) } }
    if (d.kyLam.isNotEmpty()) Section("Không nên") { d.kyLam.forEach { Bullet(it, badColor()) } }
    val p = mySettings().toPerson()
    if (p != null) {
        val t = remember(todayJd, p.year, p.month, p.day) { TuViNgay(p, d) }
        Section("Với bạn (${p.canChi()})") {
            Para(t.tongQuan)
            KeyValue("Màu may mắn", t.mauMayMan)
        }
    }
    androidx.compose.material3.TextButton(onClick = { onOpenDay(todayJd) }) { Text("Xem chi tiết ngày hôm nay") }
}
