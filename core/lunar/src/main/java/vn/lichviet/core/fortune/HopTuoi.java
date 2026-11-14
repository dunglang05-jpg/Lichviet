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

    public static final class Result {
        public final int score;               // 0..10
        public final List<String> good = new ArrayList<>();
        public final List<String> bad = new ArrayList<>();
        public String verdict;
        Result(int s) { score = s; }
    }

    public static Result compare(Person a, Person b) {
        int s = 0;
        List<String> g = new ArrayList<>(), x = new ArrayList<>();
        int mh = quanHeHanh(a.menh(), b.menh());
        if (mh == 2) { s += 3; g.add("Mệnh " + a.menh() + " – " + b.menh() + " tương sinh"); }
        else if (mh == 1) { s += 2; g.add("Mệnh cùng hành " + a.menh() + ", bình hòa"); }
        else x.add("Mệnh " + a.menh() + " – " + b.menh() + " tương khắc");
        if (canHop(a.can, b.can)) { s += 2; g.add("Thiên can " + DayInfo.CAN[a.can] + " – " + DayInfo.CAN[b.can] + " hợp"); }
        else if (canXung(a.can, b.can)) x.add("Thiên can " + DayInfo.CAN[a.can] + " – " + DayInfo.CAN[b.can] + " xung");
        else s += 1;
        if (tamHop(a.chi, b.chi)) { s += 3; g.add("Địa chi " + DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi] + " tam hợp"); }
        else if (lucHop(a.chi, b.chi)) { s += 3; g.add("Địa chi " + DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi] + " lục hợp"); }
        else if (lucXung(a.chi, b.chi)) x.add("Địa chi " + DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi] + " lục xung");
        else if (lucHai(a.chi, b.chi)) { s += 1; x.add("Địa chi " + DayInfo.CHI[a.chi] + " – " + DayInfo.CHI[b.chi] + " lục hại"); }
        else s += 2;
        int ca = BatTrach.cung(a.lunarYear, a.male), cb = BatTrach.cung(b.lunarYear, b.male);
        if (BatTrach.dongTuMenh(ca) == BatTrach.dongTuMenh(cb)) {
            s += 2; g.add("Cung phi " + BatTrach.CUNG[ca] + " – " + BatTrach.CUNG[cb] + " cùng nhóm " + (BatTrach.dongTuMenh(ca) ? "Đông" : "Tây") + " tứ mệnh");
        } else x.add("Cung phi " + BatTrach.CUNG[ca] + " – " + BatTrach.CUNG[cb] + " khác nhóm Đông/Tây tứ mệnh");
        Result r = new Result(Math.min(10, s));
        r.good.addAll(g);
        r.bad.addAll(x);
        r.verdict = r.score >= 8 ? "Rất hợp" : r.score >= 6 ? "Khá hợp" : r.score >= 4 ? "Trung bình" : "Ít hợp, cần nhường nhịn";
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
}
