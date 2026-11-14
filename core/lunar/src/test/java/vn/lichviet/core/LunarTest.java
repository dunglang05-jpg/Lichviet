package vn.lichviet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LunarTest {
    private static String l(int d, int m, int y) {
        int[] r = LunarCalendar.solarToLunar(d, m, y);
        return r[0] + "/" + r[1] + "/" + r[2] + (r[3] == 1 ? "N" : "");
    }

    @Test public void tetDates() {
        assertEquals("1/1/2024", l(10, 2, 2024));
        assertEquals("1/1/2025", l(29, 1, 2025));
        assertEquals("1/1/2026", l(17, 2, 2026));
        assertEquals("1/1/2027", l(6, 2, 2027));
    }

    @Test public void leapMonth() {
        assertEquals("1/2/2023N", l(22, 3, 2023));
        assertEquals(2, LunarCalendar.leapMonthOf(2023));
        assertEquals(6, LunarCalendar.leapMonthOf(2025));
        int[] s = LunarCalendar.lunarToSolar(1, 2, 2023, 1);
        assertEquals(22, s[0]); assertEquals(3, s[1]);
    }

    @Test public void roundTrip1900to2100() {
        int jd0 = LunarCalendar.jdFromDate(1, 1, 1900), jd1 = LunarCalendar.jdFromDate(31, 12, 2100);
        for (int jd = jd0; jd <= jd1; jd += 3) {
            int[] dt = LunarCalendar.jdToDate(jd);
            int[] lu = LunarCalendar.solarToLunar(dt[0], dt[1], dt[2]);
            int[] back = LunarCalendar.lunarToSolar(lu[0], lu[1], lu[2], lu[3]);
            assertEquals("jd " + jd, jd, LunarCalendar.jdFromDate(back[0], back[1], back[2]));
        }
    }

    @Test public void dayDetails() {
        DayInfo d = DayInfo.of(28, 1, 2000);
        assertEquals("Ất Dậu", d.dayCanChi);
        assertEquals("Thành", d.truc);
        assertEquals("Lâu", d.tu);
        d = DayInfo.of(28, 8, 2000);
        assertEquals("Khai", d.truc);
        assertEquals("Nhâm Tý", d.hours.get(0).canChi);
        assertEquals("Tý (23-1), Sửu (1-3), Mão (5-7), Ngọ (11-13), Thân (15-17), Dậu (17-19)", d.goodHoursText());
        assertEquals("Lập xuân", DayInfo.of(4, 2, 2026).tietKhiToday);
        assertTrue(DayInfo.isGiaoThua(16, 2, 2026));
    }

    @Test public void moonAndCountdown() {
        MoonPhase p = MoonPhase.of(10, 11, 2026); // mùng 2
        assertTrue(p.age < 2.5);
        assertEquals("Trăng tròn", MoonPhase.upcoming(14, 11, 2026, 2).get(1).name);
        assertEquals(84, Countdown.daysToTet(14, 11, 2026));
        assertEquals(0, Countdown.daysToTet(17, 2, 2026));
    }

    @Test public void finder() {
        for (DayInfo d : GoodDayFinder.find(Activity.CUOI_HOI, 1, 1, 2027, 120, 20)) {
            assertTrue(Activity.CUOI_HOI.trucFits(d.trucIndex));
            assertTrue(!d.tamNuong && !d.nguyetKy && d.score >= 3);
        }
    }

    @Test public void monthGrid() {
        MonthGrid g = new MonthGrid(11, 2026, true);
        assertEquals(26, g.cells[0].solarDay); // T2 26/10
        assertEquals("Tháng 9 – 10 năm Bính Ngọ", g.lunarRange());
    }
}
