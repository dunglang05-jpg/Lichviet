package vn.lichviet.core;

/** Đếm ngược đến các ngày âm lịch (Tết, rằm, ngày giỗ...). */
public final class Countdown {
    private Countdown() {}

    /** JDN của lần tới (>= hôm nay) của ngày âm lịch lặp hằng năm. Ngày 30 không có thì lùi về 29. */
    public static int nextLunarYearly(int lunarDay, int lunarMonth, int fromD, int fromM, int fromY) {
        int fromJd = LunarCalendar.jdFromDate(fromD, fromM, fromY);
        int ly = LunarCalendar.solarToLunar(fromD, fromM, fromY)[2];
        for (int y = ly; y <= ly + 2; y++) {
            int jd = lunarToJd(lunarDay, lunarMonth, y);
            if (jd >= fromJd) return jd;
        }
        return -1;
    }

    /** JDN của ngày âm lịch trong năm âm cho trước; ngày 30 thiếu thì trả ngày cuối tháng. */
    public static int lunarToJd(int lunarDay, int lunarMonth, int lunarYear) {
        int[] s = LunarCalendar.lunarToSolar(1, lunarMonth, lunarYear, 0);
        int start = LunarCalendar.jdFromDate(s[0], s[1], s[2]);
        int len = LunarCalendar.monthLength(start);
        int day = Math.min(lunarDay, len);
        return start + day - 1;
    }

    /** Số ngày từ hôm nay đến Tết Nguyên đán sắp tới (0 nếu hôm nay là mùng 1 Tết). */
    public static int daysToTet(int d, int m, int y) {
        int next = nextLunarYearly(1, 1, d, m, y);
        return next - LunarCalendar.jdFromDate(d, m, y);
    }

    /** JDN lần tới của ngày dương lặp hằng năm (29/2 ở năm không nhuận -> 28/2). */
    public static int nextSolarYearly(int day, int month, int fromD, int fromM, int fromY) {
        int fromJd = LunarCalendar.jdFromDate(fromD, fromM, fromY);
        for (int y = fromY; y <= fromY + 1; y++) {
            int dd = Math.min(day, Holidays.daysInMonth(month, y));
            int jd = LunarCalendar.jdFromDate(dd, month, y);
            if (jd >= fromJd) return jd;
        }
        return -1;
    }
}
