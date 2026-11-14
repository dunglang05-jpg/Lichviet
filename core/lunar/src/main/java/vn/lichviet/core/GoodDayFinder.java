package vn.lichviet.core;

import java.util.ArrayList;
import java.util.List;

/** Tìm ngày thuận tiện cho một việc trong khoảng thời gian. */
public final class GoodDayFinder {
    private GoodDayFinder() {}

    /**
     * Ngày có đạt cho việc này không: Trực hợp; không phạm Tam Nương, Nguyệt Kỵ, Dương Công, Thụ Tử
     * (và Sát Chủ với việc lớn); không có sao xấu kỵ riêng việc đó; điểm ngày >= 3.
     */
    public static boolean suits(DayInfo d, Activity a) {
        if (!a.trucFits(d.trucIndex)) return false;
        if (d.tamNuong || d.nguyetKy) return false;
        if (d.score < 3) return false;
        Almanac al = d.almanac();
        for (Almanac.KyNhat k : al.kyNhat) {
            if (k.name.startsWith("Dương Công") || k.name.equals("Thụ Tử")) return false;
            if (a.avoidSatChu && k.name.startsWith("Sát Chủ")) return false;
        }
        for (Almanac.Star s : al.badStars) if (a.badStarHits(s)) return false;
        return true;
    }

    /** Lý do một ngày không hợp với việc (để hiển thị), hoặc null nếu hợp. */
    public static String whyNot(DayInfo d, Activity a) {
        if (!a.trucFits(d.trucIndex)) return "Trực " + d.truc + " không hợp " + a.label.toLowerCase();
        if (d.tamNuong) return "Phạm ngày Tam Nương";
        if (d.nguyetKy) return "Phạm ngày Nguyệt Kỵ";
        for (Almanac.KyNhat k : d.almanac().kyNhat) {
            if (k.name.startsWith("Dương Công") || k.name.equals("Thụ Tử")) return "Phạm ngày " + k.name;
            if (a.avoidSatChu && k.name.startsWith("Sát Chủ")) return "Phạm ngày " + k.name;
        }
        for (Almanac.Star s : d.almanac().badStars) if (a.badStarHits(s)) return "Gặp sao " + s.name + ": " + s.desc;
        if (d.score < 3) return "Điểm ngày thấp (" + d.score + "/5)";
        return null;
    }

    /** Tìm tối đa {@code limit} ngày phù hợp trong {@code days} ngày kể từ ngày bắt đầu, xếp theo thời gian. */
    public static List<DayInfo> find(Activity a, int d, int m, int y, int days, int limit) {
        List<DayInfo> out = new ArrayList<>();
        int jd = LunarCalendar.jdFromDate(d, m, y);
        for (int i = 0; i < days && out.size() < limit; i++) {
            int[] dt = LunarCalendar.jdToDate(jd + i);
            DayInfo di = DayInfo.of(dt[0], dt[1], dt[2]);
            if (suits(di, a)) out.add(di);
        }
        return out;
    }

    /** Các việc hợp với ngày này. */
    public static List<Activity> activitiesFor(DayInfo d) {
        List<Activity> out = new ArrayList<>();
        for (Activity a : Activity.values()) if (suits(d, a)) out.add(a);
        return out;
    }
}
