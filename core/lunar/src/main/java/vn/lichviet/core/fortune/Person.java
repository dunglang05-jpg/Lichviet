package vn.lichviet.core.fortune;

import vn.lichviet.core.Almanac;
import vn.lichviet.core.DayInfo;
import vn.lichviet.core.LunarCalendar;

/** Một người để xem tử vi: tên, giới tính và ngày sinh dương lịch. */
public final class Person {
    public final String name;
    public final boolean male;
    public final int day, month, year;          // dương lịch
    public final int lunarDay, lunarMonth, lunarYear;
    public final int can, chi;                  // can chi năm sinh âm lịch
    /** Giờ sinh 0..23 (giờ Việt Nam), -1 nếu không rõ. */
    public final int hour;

    public Person(String name, boolean male, int day, int month, int year) {
        this(name, male, day, month, year, -1);
    }

    public Person(String name, boolean male, int day, int month, int year, int hour) {
        this.hour = hour;
        this.name = name == null ? "" : name.trim();
        this.male = male;
        this.day = day;
        this.month = month;
        this.year = year;
        int[] l = LunarCalendar.solarToLunar(day, month, year);
        lunarDay = l[0];
        lunarMonth = l[1];
        lunarYear = l[2];
        can = Math.floorMod(lunarYear + 6, 10);
        chi = Math.floorMod(lunarYear + 8, 12);
    }

    /** Tuổi mụ (tuổi âm) trong năm âm lịch [y]. */
    public int tuoiMu(int y) { return y - lunarYear + 1; }

    public String canChi() { return DayInfo.CAN[can] + " " + DayInfo.CHI[chi]; }
    public String conGiap() { return DayInfo.CON_GIAP[chi]; }
    public String napAm() { return Almanac.napAm(can, chi); }
    public String menh() { return Almanac.hanhNapAm(can, chi); }
    public String gioi() { return male ? "Nam" : "Nữ"; }
}
