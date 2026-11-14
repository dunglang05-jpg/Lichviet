package vn.lichviet.core;

/**
 * Thuật toán âm lịch Việt Nam (múi giờ UTC+7) theo phương pháp thiên văn của Hồ Ngọc Đức.
 * Mọi hàm đều thuần Java, không phụ thuộc Android.
 */
public final class LunarCalendar {
    public static final double TZ = 7.0;
    private static final double PI = Math.PI;

    private LunarCalendar() {}

    private static int INT(double d) {
        return (int) Math.floor(d);
    }

    /** Số ngày Julian (JDN) của ngày dương lịch. */
    public static int jdFromDate(int dd, int mm, int yy) {
        int a = (14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        int jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083;
        }
        return jd;
    }

    /** Ngược lại: JDN -> {ngày, tháng, năm} dương lịch. */
    public static int[] jdToDate(int jd) {
        int a, b, c;
        if (jd > 2299160) {
            a = jd + 32044;
            b = (4 * a + 3) / 146097;
            c = a - (b * 146097) / 4;
        } else {
            b = 0;
            c = jd + 32082;
        }
        int d = (4 * c + 3) / 1461;
        int e = c - (1461 * d) / 4;
        int m = (5 * e + 2) / 153;
        int day = e - (153 * m + 2) / 5 + 1;
        int month = m + 3 - 12 * (m / 10);
        int year = b * 100 + d - 4800 + m / 10;
        return new int[] {day, month, year};
    }

    /** Thời điểm trăng mới thứ k (JD, UT). */
    public static double newMoon(int k) {
        double T = k / 1236.85;
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = PI / 180;
        double Jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3;
        Jd1 = Jd1 + 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr);
        double M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3;
        double Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3;
        double F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3;
        double C1 = (0.1734 - 0.000393 * T) * Math.sin(M * dr) + 0.0021 * Math.sin(2 * dr * M);
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr);
        C1 = C1 - 0.0004 * Math.sin(dr * 3 * Mpr);
        C1 = C1 + 0.0104 * Math.sin(dr * 2 * F) - 0.0051 * Math.sin(dr * (M + Mpr));
        C1 = C1 - 0.0074 * Math.sin(dr * (M - Mpr)) + 0.0004 * Math.sin(dr * (2 * F + M));
        C1 = C1 - 0.0004 * Math.sin(dr * (2 * F - M)) - 0.0006 * Math.sin(dr * (2 * F + Mpr));
        C1 = C1 + 0.0010 * Math.sin(dr * (2 * F - Mpr)) + 0.0005 * Math.sin(dr * (2 * Mpr + M));
        double deltat;
        if (T < -11) {
            deltat = 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3;
        } else {
            deltat = -0.000278 + 0.000265 * T + 0.000262 * T2;
        }
        return Jd1 + C1 - deltat;
    }

    /** Kinh độ Mặt Trời (radian) tại thời điểm JD. */
    public static double sunLongitude(double jdn) {
        double T = (jdn - 2451545.0) / 36525;
        double T2 = T * T;
        double dr = PI / 180;
        double M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2;
        double L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2;
        double DL = (1.914600 - 0.004817 * T - 0.000014 * T2) * Math.sin(dr * M);
        DL = DL + (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + 0.000290 * Math.sin(dr * 3 * M);
        double L = L0 + DL;
        L = L * dr;
        L = L - PI * 2 * (INT(L / (PI * 2)));
        return L;
    }

    /** Kinh độ Mặt Trời theo độ [0,360) lúc 0h địa phương của ngày JDN (UTC+7). */
    public static double sunLongitudeDeg(int dayNumber, double hourOfDay) {
        double jd = dayNumber - 0.5 - TZ / 24.0 + hourOfDay / 24.0;
        return sunLongitude(jd) * 180.0 / PI;
    }

    private static int getSunLongitude(int dayNumber, double tz) {
        return INT(sunLongitude(dayNumber - 0.5 - tz / 24.0) / PI * 6);
    }

    private static int getNewMoonDay(int k, double tz) {
        return INT(newMoon(k) + 0.5 + tz / 24);
    }

    private static int getLunarMonth11(int yy, double tz) {
        double off = jdFromDate(31, 12, yy) - 2415021.076998695;
        int k = INT(off / 29.530588853);
        int nm = getNewMoonDay(k, tz);
        int sunLong = getSunLongitude(nm, tz);
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, tz);
        }
        return nm;
    }

    private static int getLeapMonthOffset(int a11, double tz) {
        int k = INT((a11 - 2415021.076998695) / 29.530588853 + 0.5);
        int last;
        int i = 1;
        int arc = getSunLongitude(getNewMoonDay(k + i, tz), tz);
        do {
            last = arc;
            i++;
            arc = getSunLongitude(getNewMoonDay(k + i, tz), tz);
        } while (arc != last && i < 14);
        return i - 1;
    }

    /** Dương -> âm. Trả về {ngày, tháng, năm, nhuận(0/1)}. */
    public static int[] solarToLunar(int dd, int mm, int yy) {
        double tz = TZ;
        int dayNumber = jdFromDate(dd, mm, yy);
        int k = INT((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = getNewMoonDay(k + 1, tz);
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, tz);
        }
        int a11 = getLunarMonth11(yy, tz);
        int b11 = a11;
        int lunarYear;
        if (a11 >= monthStart) {
            lunarYear = yy;
            a11 = getLunarMonth11(yy - 1, tz);
        } else {
            lunarYear = yy + 1;
            b11 = getLunarMonth11(yy + 1, tz);
        }
        int lunarDay = dayNumber - monthStart + 1;
        int diff = INT((monthStart - a11) / 29);
        int lunarLeap = 0;
        int lunarMonth = diff + 11;
        if (b11 - a11 > 365) {
            int leapMonthDiff = getLeapMonthOffset(a11, tz);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10;
                if (diff == leapMonthDiff) {
                    lunarLeap = 1;
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12;
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1;
        }
        return new int[] {lunarDay, lunarMonth, lunarYear, lunarLeap};
    }

    /** Âm -> dương. Trả về {ngày, tháng, năm} hoặc {0,0,0} nếu không hợp lệ. */
    public static int[] lunarToSolar(int lunarDay, int lunarMonth, int lunarYear, int lunarLeap) {
        double tz = TZ;
        int a11, b11;
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear - 1, tz);
            b11 = getLunarMonth11(lunarYear, tz);
        } else {
            a11 = getLunarMonth11(lunarYear, tz);
            b11 = getLunarMonth11(lunarYear + 1, tz);
        }
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int off = lunarMonth - 11;
        if (off < 0) {
            off += 12;
        }
        if (b11 - a11 > 365) {
            int leapOff = getLeapMonthOffset(a11, tz);
            int leapMonth = leapOff - 2;
            if (leapMonth < 0) {
                leapMonth += 12;
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                return new int[] {0, 0, 0};
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1;
            }
        }
        int monthStart = getNewMoonDay(k + off, tz);
        return jdToDate(monthStart + lunarDay - 1);
    }

    /** Tháng nhuận của năm âm (0 nếu không nhuận). */
    public static int leapMonthOf(int lunarYear) {
        double tz = TZ;
        int a11 = getLunarMonth11(lunarYear - 1, tz);
        int b11 = getLunarMonth11(lunarYear, tz);
        if (b11 - a11 > 365) {
            int leapOff = getLeapMonthOffset(a11, tz);
            int m = leapOff - 2;
            if (m <= 0) {
                m += 12;
            }
            return m;
        }
        return 0;
    }

    /** Số ngày của tháng âm (29 hoặc 30) khi biết ngày bắt đầu tháng ở dạng JDN. */
    public static int monthLength(int jdStartOfLunarMonth) {
        int k = INT((jdStartOfLunarMonth - 2415021.076998695) / 29.530588853 + 0.5);
        int a = getNewMoonDay(k, TZ);
        int b = getNewMoonDay(k + 1, TZ);
        return b - a;
    }

    /** Tuổi trăng (ngày) tại thời điểm JD (UT), tính từ lần trăng mới gần nhất. */
    public static double moonAge(double jdUt) {
        int k = INT((jdUt - 2415021.076998695) / 29.530588853);
        double nm = newMoon(k);
        if (nm > jdUt) {
            nm = newMoon(k - 1);
        }
        return jdUt - nm;
    }

    /** JD (UT) của lần trăng mới thứ k gần nhất sau hoặc tại thời điểm jdUt, theo chỉ số. */
    public static int newMoonIndexAtOrBefore(double jdUt) {
        int k = INT((jdUt - 2415021.076998695) / 29.530588853);
        if (newMoon(k) > jdUt) {
            k -= 1;
        }
        return k;
    }

    /** Chuyển JD (UT) sang JDN địa phương UTC+7. */
    public static int jdUtToLocalDay(double jdUt) {
        return INT(jdUt + 0.5 + TZ / 24.0);
    }
}
