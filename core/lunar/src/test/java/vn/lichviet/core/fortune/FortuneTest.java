package vn.lichviet.core.fortune;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
import vn.lichviet.core.DayInfo;

public class FortuneTest {
    @Test public void numerology() {
        assertEquals(6, Numerology.lifePath(15, 8, 1990));
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

    @Test public void person() {
        Person p = new Person("A", true, 15, 8, 1990);
        assertEquals("Canh Ngọ", p.canChi());
        assertEquals("Lộ Bàng Thổ", p.napAm());
        assertEquals("Thổ", p.menh());
        Person q = new Person("B", false, 20, 1, 1991); // trước Tết 1991 -> năm Canh Ngọ
        assertEquals(1990, q.lunarYear);
        VanHan v = new VanHan(p, 2026);
        assertEquals(37, v.tuoiMu);
        assertTrue(v.score >= 1 && v.score <= 5);
        HopTuoi.Result r = HopTuoi.compare(p, new Person("C", false, 1, 6, 1994));
        assertTrue(r.score >= 0 && r.score <= 10);
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
        assertFalse(GiaiMong.search("ran").isEmpty());
        assertTrue(VanKhan.fill(VanKhan.DATA[0][1], "ngày X", "", "").contains("ngày X"));
        assertTrue(SongKhoe.tip("Lập đông").startsWith("Lập đông"));
        TuViNgay t = new TuViNgay(new Person("A", true, 15, 8, 1990), DayInfo.of(15, 11, 2026));
        assertTrue(t.total > 0 && !t.gioTot.isEmpty());
    }
}
