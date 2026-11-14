package vn.lichviet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/** Đáp án lấy từ bộ dữ liệu ILamTH/du-lieu-ngay-tot-xau (xemlicham.com). */
public class AlmanacTest {
    private static String names(List<Almanac.Star> stars) {
        List<String> n = new ArrayList<>();
        for (Almanac.Star s : stars) n.add(s.name);
        Collections.sort(n);
        return String.join(";", n);
    }

    private static String sorted(String... s) {
        List<String> n = new ArrayList<>();
        Collections.addAll(n, s);
        Collections.sort(n);
        return String.join(";", n);
    }

    @Test public void ngocHap20261114() {
        DayInfo d = DayInfo.of(14, 11, 2026);
        Almanac a = d.almanac();
        assertEquals("Chấp", d.truc);
        assertEquals(sorted("Thiên Quý", "Thiên Quan", "Tuế Hợp", "Giải Thần", "Hoàng Ân", "Tư Mệnh Hoàng Đạo"), names(a.goodStars));
        assertEquals(sorted("Địa Tặc", "Trùng Tang", "Trùng Phục", "Tội Chỉ", "Tứ Thời Đại Mộ"), names(a.badStars));
        assertEquals("Xích Khẩu", a.khongMinh);
        assertEquals("Trường Lưu Thủy", a.napAmNgay);
        assertEquals("Chính Bắc", a.hacThan);
        assertEquals("Chính Tây", a.taiThan);
        assertTrue(a.kyNhat.stream().anyMatch(k -> k.name.equals("Sát Chủ Dương")));
    }

    @Test public void ngocHap20250615() {
        DayInfo d = DayInfo.of(15, 6, 2025);
        Almanac a = d.almanac();
        assertEquals("Thâu", d.truc);
        assertEquals(sorted("Thiên Thành", "U Vi Tinh", "Tục Thế", "Mẫu Thương", "Ngọc Đường Hoàng Đạo"), names(a.goodStars));
        assertEquals("Đại An", a.khongMinh);
        assertEquals("Đại Khê Thủy", a.napAmNgay);
        assertTrue(a.kyNhat.isEmpty());
    }

    @Test public void kyNhatAndHours() {
        Almanac a = DayInfo.of(28, 8, 2000).almanac(); // 29/7 âm: Dương Công + Nguyệt tận
        assertTrue(a.kyNhat.stream().anyMatch(k -> k.name.startsWith("Dương Công")));
        assertTrue(a.kyNhat.stream().anyMatch(k -> k.name.equals("Nguyệt Tận")));
        assertEquals(12, a.xuatHanh.size());
        // giờ Tý và Ngọ luôn cùng loại
        assertEquals(a.xuatHanh.get(0).name, a.xuatHanh.get(6).name);
    }

    @Test public void canChiRelation() {
        // Giáp Tý: Thủy sinh Mộc → Chi sinh Can, nghĩa nhật
        Almanac a = DayInfo.of(18, 12, 2026).almanac();
        assertTrue(DayInfo.of(18, 12, 2026).dayCanChi + " " + a.quanHeCanChi, a.quanHeCanChi.length() > 0);
        for (int i = 0; i < 60; i++) assertTrue(Almanac.hacThan(i).length() > 0);
    }
}
