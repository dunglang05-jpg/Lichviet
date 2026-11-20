package vn.lichviet.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HolidaysTest {
    private static boolean co(int d, int m, int y, String ten) {
        for (Holidays.Item it : DayInfo.of(d, m, y).holidays) if (it.name.equals(ten)) return it.major;
        return false;
    }

    @Test
    public void ngayVanHoaVietNamTu2026() {
        assertTrue(co(24, 11, 2026, "Ngày Văn hóa Việt Nam"));
        assertTrue(co(24, 11, 2027, "Ngày Văn hóa Việt Nam"));
        assertFalse(co(24, 11, 2025, "Ngày Văn hóa Việt Nam"));
    }
}
