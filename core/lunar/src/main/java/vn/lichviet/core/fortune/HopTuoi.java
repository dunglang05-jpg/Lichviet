package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.DayInfo;

/** Xem hai người hợp nhau: mệnh, thiên can, địa chi, cung phi. Thang điểm 10. */
public final class HopTuoi {
    private HopTuoi() {}

    static final String[] HANH = {"Kim", "Thủy", "Mộc", "Hỏa", "Thổ"}; // Kim sinh Thủy sinh Mộc sinh Hỏa sinh Thổ sinh Kim

    static int hanh(String h) {
        for (int i = 0; i < 5; i++) if (HANH[i].equals(h)) return i;
        return 0;
    }

    /** 2 = tương sinh, 1 = bình hòa (cùng hành), -2 = tương khắc. */
    public static int quanHeHanh(String a, String b) {
        int x = hanh(a), y = hanh(b);
        if (x == y) return 1;
        if ((x + 1) % 5 == y || (y + 1) % 5 == x) return 2;
        return -2;
    }

    /** Hành sinh ra [h]. */
    public static String sinhRa(String h) { return HANH[Math.floorMod(hanh(h) - 1, 5)]; }
    /** Hành bị [h] khắc. */
    public static String khac(String h) { return HANH[Math.floorMod(hanh(h) + 2, 5)]; }
    /** Hành khắc [h]. */
    public static String biKhac(String h) { return HANH[Math.floorMod(hanh(h) - 2, 5)]; }

    public static boolean canHop(int a, int b) { return Math.floorMod(a - b, 10) == 5; }
    public static boolean canXung(int a, int b) {
        int d = Math.floorMod(a - b, 10);
        return d == 4 || d == 6;
    }
    public static boolean tamHop(int a, int b) { return a != b && Math.floorMod(a - b, 4) == 0; }
    public static boolean lucHop(int a, int b) { return Math.floorMod(a + b, 12) == 1; }
    public static boolean lucXung(int a, int b) { return Math.floorMod(a - b, 12) == 6; }
    public static boolean lucHai(int a, int b) { return Math.floorMod(a + b, 12) == 7; }
    public static boolean tuHanhXung(int a, int b) { return a != b && Math.floorMod(a - b, 3) == 0 && !lucXung(a, b); }

    /** Kết quả so tuổi: chỉ liệt kê các quan hệ theo quy tắc ngũ hành, can chi, cung phi; không chấm điểm. */
    public static final class Result {
        public final List<String> good = new ArrayList<>();
        public final List<String> bad = new ArrayList<>();
        public final List<String> neutral = new ArrayList<>();
        public String cungPhiSao;    // sao Bát trạch của cặp cung phi
    }

    public static Result compare(Person a, Person b) {
        Result r = new Result();
        int mh = quanHeHanh(a.menh(), b.menh());
        String m = "Mệnh " + a.menh() + " (" + a.napAm() + ") – " + b.menh() + " (" + b.napAm() + ")";
        if (mh == 2) r.good.add(m + ": tương sinh");
        else if (mh == 1) r.neutral.add(m + ": cùng hành (bình hòa)");
        else r.bad.add(m + ": tương khắc");
        String c = "Thiên can " + DayInfo.CAN[a.can] + " – " + DayInfo.CAN[b.can];
        if (canHop(a.can, b.can)) r.good.add(c + ": tương hợp");
        else if (canXung(a.can, b.can)) r.bad.add(c + ": tương xung");
        else r.neutral.add(c + ": không hợp không xung");
        String z = "Địa chi " + DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi];
        if (tamHop(a.chi, b.chi)) r.good.add(z + ": tam hợp");
        else if (lucHop(a.chi, b.chi)) r.good.add(z + ": lục hợp");
        else if (lucXung(a.chi, b.chi)) r.bad.add(z + ": lục xung");
        else if (lucHai(a.chi, b.chi)) r.bad.add(z + ": lục hại");
        else if (tuHanhXung(a.chi, b.chi)) r.bad.add(z + ": cùng nhóm tứ hành xung");
        else if (a.chi == b.chi) r.neutral.add(z + ": cùng tuổi");
        else r.neutral.add(z + ": không hợp không xung");
        int ca = BatTrach.cung(a.lunarYear, a.male), cb = BatTrach.cung(b.lunarYear, b.male);
        int i = java.util.Arrays.asList(BatTrach.huong(ca)).indexOf(BatTrach.PHUONG[cb]);
        r.cungPhiSao = BatTrach.HUONG_TEN[i];
        String cp = "Cung phi " + BatTrach.CUNG[ca] + " – " + BatTrach.CUNG[cb] + ": " + r.cungPhiSao;
        if (i < 4) r.good.add(cp); else r.bad.add(cp);
        return r;
    }

    /** Các chi hợp với chi [c] (tam hợp + lục hợp). */
    public static String chiHop(int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) if (tamHop(c, i) || lucHop(c, i)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(DayInfo.CHI[i]);
        }
        return sb.toString();
    }

    public static String chiKy(int c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) if (lucXung(c, i) || lucHai(c, i) || tuHanhXung(c, i)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(DayInfo.CHI[i]);
        }
        return sb.toString();
    }

    /** Một tiêu chí trong phép chấm điểm hợp tuổi (0–2 điểm). */
    public static final class Criterion {
        public final String name, detail;
        public final int point;
        Criterion(String name, String detail, int point) { this.name = name; this.detail = detail; this.point = point; }
    }

    /**
     * Chấm điểm 10 theo cách các trang lịch vạn niên Việt Nam dùng (ngaydep.com, xem tuổi vợ chồng):
     * 5 tiêu chí mệnh nạp âm, thiên can, địa chi, cung phi bát trạch, thiên mệnh (hành của cung phi), mỗi tiêu chí 0–2 điểm.
     */
    public static List<Criterion> score(Person a, Person b) {
        List<Criterion> r = new ArrayList<>();
        int mh = quanHeHanh(a.menh(), b.menh());
        String rel = mh == 2 ? (sinhRa(b.menh()).equals(a.menh()) ? a.menh() + " sinh " + b.menh() : b.menh() + " sinh " + a.menh()) + " (tương sinh)"
            : mh == 1 ? "cùng hành " + a.menh() + " (bình hòa)" : "tương khắc";
        r.add(new Criterion("Mệnh (ngũ hành nạp âm)", a.napAm() + " – " + b.napAm() + ": " + rel, mh == 2 ? 2 : mh == 1 ? 1 : 0));

        String c = DayInfo.CAN[a.can] + " – " + DayInfo.CAN[b.can] + ": ";
        if (canHop(a.can, b.can)) r.add(new Criterion("Thiên can", c + "tương hợp", 2));
        else if (canXung(a.can, b.can)) r.add(new Criterion("Thiên can", c + "tương xung", 0));
        else r.add(new Criterion("Thiên can", c + "bình thường", 1));

        String z = DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi] + ": ";
        if (tamHop(a.chi, b.chi)) r.add(new Criterion("Địa chi", z + "tam hợp", 2));
        else if (lucHop(a.chi, b.chi)) r.add(new Criterion("Địa chi", z + "lục hợp", 2));
        else if (lucXung(a.chi, b.chi)) r.add(new Criterion("Địa chi", z + "lục xung", 0));
        else if (lucHai(a.chi, b.chi)) r.add(new Criterion("Địa chi", z + "lục hại", 0));
        else r.add(new Criterion("Địa chi", z + "bình thường", 1));

        int ca = BatTrach.cung(a.lunarYear, a.male), cb = BatTrach.cung(b.lunarYear, b.male);
        int i = java.util.Arrays.asList(BatTrach.huong(ca)).indexOf(BatTrach.PHUONG[cb]);
        r.add(new Criterion("Cung phi (Bát trạch)", BatTrach.CUNG[ca] + " – " + BatTrach.CUNG[cb] + ": " + BatTrach.HUONG_TEN[i]
            + (i < 4 ? " (tốt)" : " (xấu)"), i < 4 ? 2 : 0));

        String ha = BatTrach.HANH_CUNG[ca], hb = BatTrach.HANH_CUNG[cb];
        int th = quanHeHanh(ha, hb);
        String t = ha + " – " + hb + ": " + (th == 2 ? (sinhRa(hb).equals(ha) ? ha + " sinh " + hb : hb + " sinh " + ha) + " (tương sinh)"
            : th == 1 ? "cùng hành (bình hòa)" : "tương khắc");
        r.add(new Criterion("Thiên mệnh", t, th == 2 ? 2 : th == 1 ? 1 : 0));
        return r;
    }

    public static int total(List<Criterion> l) { int s = 0; for (Criterion c : l) s += c.point; return s; }

    /** Từ 8 điểm: rất hợp · 6–7: hợp · 4–5: trung bình · dưới 4: không hợp. */
    public static String rating(int total) {
        return total >= 8 ? "Rất hợp" : total >= 6 ? "Hợp" : total >= 4 ? "Trung bình" : "Không hợp";
    }
}
