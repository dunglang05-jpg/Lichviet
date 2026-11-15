package vn.lichviet.core.fortune;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
import vn.lichviet.core.DayInfo;

public class FortuneTest {
    @Test public void numerology() {
        assertEquals(6, Numerology.lifePath(15, 8, 1990));   // 1+5+8+1+9+9+0 = 33 -> 6
        assertEquals(10, Numerology.lifePath(15, 7, 1995)); // ví dụ dienmayxanh: 37 -> 10
        assertEquals(22, Numerology.lifePath(10, 3, 1980)); // ví dụ dienmayxanh: 22/4
        assertEquals(9, Numerology.lifePath(28, 5, 2001));  // ví dụ dienmayxanh
        assertEquals(4, Numerology.personalYear(27, 2, 2018)); // ví dụ mytour
    }

    @Test public void numerologyMaster() {
        assertEquals(11, Numerology.reduce(29));
        assertEquals(33, Numerology.reduce(33));
        assertEquals("NGUYEN VAN DUNG", Numerology.plain("Nguyễn Văn Dũng"));
        assertTrue(Numerology.nameNumber("Nguyễn Văn Dũng", 0) > 0);
    }

    @Test public void zodiac() {
        assertEquals("Sư Tử", Zodiac.NAME[Zodiac.of(15, 8)]);
        assertEquals("Ma Kết", Zodiac.NAME[Zodiac.of(5, 1)]);
        assertEquals("Ma Kết", Zodiac.NAME[Zodiac.of(25, 12)]);
        assertEquals("Song Ngư", Zodiac.NAME[Zodiac.of(1, 3)]);
    }

    @Test public void cungPhi() {
        assertEquals("Khảm", BatTrach.CUNG[BatTrach.cung(1990, true)]);
        assertEquals("Cấn", BatTrach.CUNG[BatTrach.cung(1990, false)]);
        assertEquals("Ly", BatTrach.CUNG[BatTrach.cung(2000, true)]);
        assertEquals("Càn", BatTrach.CUNG[BatTrach.cung(1985, true)]);
        assertEquals("Ly", BatTrach.CUNG[BatTrach.cung(1985, false)]);
    }

    @Test public void saoHan() {
        assertEquals("La Hầu", SaoHan.SAO[SaoHan.sao(10, true)]);
        assertEquals("Kế Đô", SaoHan.SAO[SaoHan.sao(10, false)]);
        assertEquals("Thái Dương", SaoHan.SAO[SaoHan.sao(23, true)]);
        assertEquals("Thủy Diệu", SaoHan.SAO[SaoHan.sao(18, false)]);
        assertTrue(SaoHan.isTamTai(6, 8));  // Ngọ gặp năm Thân
        assertNotNull(SaoHan.kimLau(28));   // 28 % 9 = 1
        assertEquals("Nhất Cát", SaoHan.HOANG_OC[SaoHan.hoangOc(10)]);
        assertEquals("Tam Địa Sát", SaoHan.HOANG_OC[SaoHan.hoangOc(12)]);
        assertEquals("Tứ Tấn Tài", SaoHan.HOANG_OC[SaoHan.hoangOc(40)]);
    }

    @Test public void hanTheoNguon() {
        // thuvienphapluat 2025: 1999 nam 27 Huỳnh Tuyền, nữ Toán Tận; 1949 (77) nam Thiên La, nữ Diêm Vương
        assertEquals("Huỳnh Tuyền", SaoHan.HAN[SaoHan.han(27, true)]);
        assertEquals("Toán Tận", SaoHan.HAN[SaoHan.han(27, false)]);
        assertEquals("Thiên La", SaoHan.HAN[SaoHan.han(77, true)]);
        assertEquals("Diêm Vương", SaoHan.HAN[SaoHan.han(77, false)]);
        // hoatieu: nam 81, 89, 90 Huỳnh Tuyền; nam/nữ 16, 69, 70 Địa Võng; nữ 37 Thiên Tinh
        assertEquals("Huỳnh Tuyền", SaoHan.HAN[SaoHan.han(89, true)]);
        assertEquals("Huỳnh Tuyền", SaoHan.HAN[SaoHan.han(90, true)]);
        assertEquals("Địa Võng", SaoHan.HAN[SaoHan.han(69, false)]);
        assertEquals("Địa Võng", SaoHan.HAN[SaoHan.han(16, true)]);
        assertEquals("Thiên Tinh", SaoHan.HAN[SaoHan.han(37, false)]);
        // sao: 1960 (66) nam Thủy Diệu, nữ Mộc Đức (thuvienphapluat 2025)
        assertEquals("Thủy Diệu", SaoHan.SAO[SaoHan.sao(66, true)]);
        assertEquals("Mộc Đức", SaoHan.SAO[SaoHan.sao(66, false)]);
    }

    @Test public void person() {
        Person p = new Person("A", true, 15, 8, 1990);
        assertEquals("Canh Ngọ", p.canChi());
        assertEquals("Lộ Bàng Thổ", p.napAm());
        assertEquals("Thổ", p.menh());
        Person q = new Person("B", false, 20, 1, 1991); // trước Tết 1991 -> năm Canh Ngọ
        assertEquals(1990, q.lunarYear);
        VanHan v = new VanHan(p, 2026);
        assertEquals(37, v.tuoiMu);
        assertEquals("La Hầu", SaoHan.SAO[v.sao]);
        assertEquals("Tam Kheo", SaoHan.HAN[v.han]);
        // Chấn (nam 1970) + Ly (nữ 1985) = Sinh khí (wiki.batdongsan)
        HopTuoi.Result r = HopTuoi.compare(new Person("A", true, 1, 6, 1970), new Person("B", false, 1, 6, 1985));
        assertEquals("Sinh Khí", r.cungPhiSao);
    }

    @Test public void kinhDich() {
        assertEquals(1, KinhDich.number(new int[]{1, 1, 1, 1, 1, 1}));
        assertEquals(2, KinhDich.number(new int[]{0, 0, 0, 0, 0, 0}));
        assertEquals(11, KinhDich.number(new int[]{1, 1, 1, 0, 0, 0}));      // Địa Thiên Thái
        assertEquals(63, KinhDich.number(new int[]{1, 0, 1, 0, 1, 0}));      // Thủy Hỏa Ký Tế
        assertEquals("Thủy Lôi Truân", KinhDich.fullName(new int[]{1, 0, 0, 0, 1, 0}));
        boolean[] seen = new boolean[65];
        for (int u = 0; u < 64; u++) {
            int[] a = new int[6];
            for (int i = 0; i < 6; i++) a[i] = (u >> i) & 1;
            seen[KinhDich.number(a)] = true;
        }
        for (int i = 1; i <= 64; i++) assertTrue("thiếu quẻ " + i, seen[i]);
        KinhDich.Cast c = KinhDich.cast(new Random(1));
        assertTrue(c.main >= 1 && c.main <= 64);
    }

    @Test public void loBan() {
        assertTrue(LoBan.tot(0, 81));        // 81 % 52.2 = 28.8 -> cung 4 Nhân Lộc
        assertTrue(LoBan.tot(1, LoBan.nearestGood(1, 100)));
    }

    @Test public void misc() {
        assertTrue(VanKhan.fill(VanKhan.DATA[0][1], "ngày X", "", "").contains("ngày X"));
        TuViNgay t = new TuViNgay(new Person("A", true, 15, 8, 1990), DayInfo.of(15, 11, 2026));
        assertTrue(!t.gioTot.isEmpty());
    }

    @Test public void numerologyExtras() {
        // fptshop.com.vn: Trần An Nhiên -> linh hồn 7; viettelstore.vn: Nguyễn Văn An -> linh hồn 1
        assertEquals(7, Numerology.nameNumber("Trần An Nhiên", 1));
        assertEquals(1, Numerology.nameNumber("Nguyễn Văn An", 1));
        // jobsgo.vn: đáy 4–3–2 (tháng–ngày–năm) -> đỉnh 7, 5, 3, 6
        Numerology.Pinnacle[] p = Numerology.pinnacles(3, 4, 2000);
        assertEquals(7, p[0].number); assertEquals(5, p[1].number);
        assertEquals(3, p[2].number); assertEquals(6, p[3].number);
        assertEquals(36 - 9, p[0].fromAge); // 3+4+2 = 9
        assertEquals(p[0].fromAge + 9, p[1].fromAge);
    }

    @Test public void zodiacExtras() {
        assertEquals("Thiên Bình", Zodiac.NAME[Zodiac.opposite(0)]);
        assertEquals("Sư Tử, Nhân Mã", Zodiac.sameElement(0));
        assertEquals(12, Zodiac.TRAIT.length); assertEquals(12, Zodiac.PROS.length); assertEquals(12, Zodiac.CONS.length);
    }
}
