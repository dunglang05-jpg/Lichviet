package vn.lichviet.core;

/** Lưới 6x7 ô của một tháng dương lịch, dùng cho lịch tháng và widget. */
public final class MonthGrid {
    public final int month, year;
    public final DayInfo[] cells = new DayInfo[42];
    public final boolean[] inMonth = new boolean[42];

    public MonthGrid(int month, int year, boolean mondayFirst) {
        this.month = month;
        this.year = year;
        int first = LunarCalendar.jdFromDate(1, month, year);
        int wd = (first + 1) % 7; // 0 = CN
        int offset = mondayFirst ? (wd + 6) % 7 : wd;
        int start = first - offset;
        for (int i = 0; i < 42; i++) {
            int[] dt = LunarCalendar.jdToDate(start + i);
            cells[i] = DayInfo.of(dt[0], dt[1], dt[2]);
            inMonth[i] = dt[1] == month;
        }
    }

    /** Mô tả tháng âm tương ứng, ví dụ "Tháng 9 – 10 năm Bính Ngọ". */
    public String lunarRange() {
        DayInfo a = DayInfo.of(1, month, year);
        DayInfo b = DayInfo.of(Holidays.daysInMonth(month, year), month, year);
        String ma = a.lunarMonth + (a.leap ? " nhuận" : "");
        String mb = b.lunarMonth + (b.leap ? " nhuận" : "");
        String years = a.lunarYear == b.lunarYear ? "năm " + b.yearCanChi : a.yearCanChi + " – " + b.yearCanChi;
        return (ma.equals(mb) ? "Tháng " + ma : "Tháng " + ma + " – " + mb) + " " + years;
    }
}
