package vn.lichviet.core;

import java.util.ArrayList;
import java.util.List;

/** Pha trăng, tuổi trăng, độ sáng và các mốc trăng sắp tới (giờ Việt Nam). */
public final class MoonPhase {
    public static final double SYNODIC = 29.530588853;
    public static final String[] PHASE_NAMES = {
        "Trăng mới (sóc)", "Trăng lưỡi liềm đầu tháng", "Trăng thượng huyền", "Trăng khuyết đầu tháng",
        "Trăng tròn (vọng)", "Trăng khuyết cuối tháng", "Trăng hạ huyền", "Trăng lưỡi liềm cuối tháng"
    };

    public final double age;          // ngày, 0..29.53
    public final double fraction;     // 0..1 theo chu kỳ
    public final int illumination;    // % phần sáng
    public final int phaseIndex;      // 0..7
    public final String phaseName;
    public final boolean waxing;

    private MoonPhase(double age) {
        this.age = age;
        this.fraction = age / SYNODIC;
        this.illumination = (int) Math.round((1 - Math.cos(2 * Math.PI * fraction)) / 2 * 100);
        this.phaseIndex = (int) Math.floor(fraction * 8 + 0.5) % 8;
        this.phaseName = PHASE_NAMES[phaseIndex];
        this.waxing = fraction < 0.5;
    }

    /** Pha trăng lúc 20h tối (giờ VN) của ngày dương lịch. */
    public static MoonPhase of(int d, int m, int y) {
        return at(d, m, y, 20.0);
    }

    public static MoonPhase at(int d, int m, int y, double hourLocal) {
        int jdn = LunarCalendar.jdFromDate(d, m, y);
        double jdUt = jdn - 0.5 + (hourLocal - LunarCalendar.TZ) / 24.0;
        return new MoonPhase(LunarCalendar.moonAge(jdUt));
    }

    /** Một mốc trăng: loại (0 mới, 1 thượng huyền, 2 tròn, 3 hạ huyền) và ngày dương. */
    public static final class Event {
        public final int type;
        public final int day, month, year;
        public final String name;
        Event(int type, int jdn) {
            this.type = type;
            int[] dt = LunarCalendar.jdToDate(jdn);
            day = dt[0]; month = dt[1]; year = dt[2];
            name = new String[] {"Trăng mới", "Thượng huyền", "Trăng tròn", "Hạ huyền"}[type];
        }
    }

    /** {@code count} mốc trăng kể từ ngày đã cho (gồm cả hôm nay). Mốc phần tư là ước lượng ±1 ngày. */
    public static List<Event> upcoming(int d, int m, int y, int count) {
        int startJdn = LunarCalendar.jdFromDate(d, m, y);
        double jdUt = startJdn - 0.5 - LunarCalendar.TZ / 24.0;
        int k = LunarCalendar.newMoonIndexAtOrBefore(jdUt);
        List<Event> out = new ArrayList<>();
        while (out.size() < count) {
            double nm = LunarCalendar.newMoon(k);
            double next = LunarCalendar.newMoon(k + 1);
            double len = next - nm;
            for (int t = 0; t < 4 && out.size() < count; t++) {
                double when = nm + len * t / 4.0;
                int day = LunarCalendar.jdUtToLocalDay(when);
                if (day >= startJdn) out.add(new Event(t, day));
            }
            k++;
        }
        return out;
    }
}
