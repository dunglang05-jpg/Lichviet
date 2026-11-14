package vn.lichviet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NewFeaturesTest {
    @Test public void tietKhi20261114() {
        TietKhi t = new TietKhi(14, 11, 2026);
        assertEquals("Lập đông", t.name);
        assertEquals("Tiểu tuyết", t.nextName);
        assertEquals(8, t.dayInTerm);
        assertEquals("Kỷ Hợi", t.monthCanChi);
        assertEquals(10, t.monthNumber);
        String s = TietKhi.formatLocal(t.startJdLocal);
        assertTrue(s, s.endsWith("7/11") && (s.startsWith("16:5") || s.startsWith("16:4")));
        String n = TietKhi.formatLocal(t.nextJdLocal);
        assertTrue(n, n.endsWith("22/11") && n.startsWith("14:"));
    }

    @Test public void tietKhiBoundaries() {
        assertEquals("Lập xuân", new TietKhi(4, 2, 2026).name);
        assertEquals(1, new TietKhi(4, 2, 2026).dayInTerm);
        assertEquals("Canh Dần", new TietKhi(4, 2, 2026).monthCanChi);
        assertEquals("Kỷ Sửu", new TietKhi(20, 1, 2026).monthCanChi);
    }

    @Test public void tuoiHop() {
        // Tuổi Ất Hợi (1995) với ngày Nhâm Thìn: không xung; với Tỵ: xung
        TuoiHop.Result r = TuoiHop.compare(1, 11, 3, 5); // Đinh Tỵ
        assertTrue(r.reasons.toString(), r.reasons.stream().anyMatch(x -> x.startsWith("Xung")));
        TuoiHop.Result h = TuoiHop.compare(1, 11, 2, 2); // Bính Dần: lục hợp Hợi–Dần
        assertTrue(h.score > r.score);
        TuoiHop.Report rep = TuoiHop.report(1995, DayInfo.of(14, 11, 2026));
        assertTrue(rep.total >= 5 && rep.total <= 95);
        assertEquals("Ất Hợi", TuoiHop.tuoiName(1995));
    }

    @Test public void tu28() {
        assertEquals("Đê Thổ Lạc", NhiThapBatTu.fullName(NhiThapBatTu.index("Đê")));
        assertEquals("Chẩn Thủy Dẫn", NhiThapBatTu.fullName(27));
    }
}
