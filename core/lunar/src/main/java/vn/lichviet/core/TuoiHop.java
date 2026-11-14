package vn.lichviet.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Chỉ số hợp tuổi (Nhân mệnh): so can chi năm sinh với can chi của ngày, tháng, năm.
 * Công thức của Lịch Việt, mang tính tham khảo:
 *   chi: lục hợp +25, tam hợp +20, xung −30, hình −15, hại −15, phá −10
 *   can: ngũ hợp +15, sinh +10, khắc −10
 *   nạp âm: được sinh +10, đồng hành +5, bị khắc −15, mình khắc −5
 * Bắt đầu 50 điểm, giới hạn 5..95. Tổng = 50% ngày + 25% tháng + 25% năm.
 */
public final class TuoiHop {
    private TuoiHop() {}

    private static final String[] HANH = {"Mộc", "Hỏa", "Thổ", "Kim", "Thủy"};
    private static final int[] LUC_HOP = {1, 0, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] HAI = {7, 6, 5, 4, 3, 2, 1, 0, 11, 10, 9, 8};
    private static final int[] PHA = {9, 4, 11, 6, 1, 8, 3, 10, 5, 0, 7, 2};

    public static final class Result {
        public final int score;
        public final List<String> reasons;
        Result(int score, List<String> reasons) {
            this.score = score;
            this.reasons = reasons;
        }
    }

    public static final class Report {
        public final Result day, month, year;
        public final int total;
        Report(Result day, Result month, Result year) {
            this.day = day;
            this.month = month;
            this.year = year;
            this.total = Math.round(day.score * 0.5f + month.score * 0.25f + year.score * 0.25f);
        }
    }

    private static int hanhIdx(String h) {
        for (int i = 0; i < 5; i++) if (HANH[i].equals(h)) return i;
        return -1;
    }

    private static String napHanh(int can, int chi) {
        String n = AlmanacData.NAP_AM[Almanac.canChiIndex(can, chi)];
        return n.substring(n.lastIndexOf(' ') + 1);
    }

    private static boolean hinh(int a, int b) {
        if (a == b) return a == 4 || a == 6 || a == 9 || a == 11; // tự hình Thìn, Ngọ, Dậu, Hợi
        int[][] groups = {{0, 3}, {2, 5, 8}, {1, 10, 7}};
        for (int[] g : groups) {
            boolean ia = false, ib = false;
            for (int x : g) { ia |= x == a; ib |= x == b; }
            if (ia && ib) return true;
        }
        return false;
    }

    /** So tuổi (canTuoi, chiTuoi) với một can chi. */
    public static Result compare(int canTuoi, int chiTuoi, int can, int chi) {
        int s = 50;
        List<String> r = new ArrayList<>();
        String ten = DayInfo.CAN[can] + " " + DayInfo.CHI[chi];
        if (LUC_HOP[chiTuoi] == chi) { s += 25; r.add("Lục hợp " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }
        else if (chiTuoi % 4 == chi % 4 && chiTuoi != chi) { s += 20; r.add("Tam hợp " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }
        if ((chiTuoi + 6) % 12 == chi) { s -= 30; r.add("Xung " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }
        if (hinh(chiTuoi, chi)) { s -= 15; r.add("Hình " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }
        if (HAI[chiTuoi] == chi) { s -= 15; r.add("Hại " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }
        if (PHA[chiTuoi] == chi) { s -= 10; r.add("Phá " + DayInfo.CHI[chiTuoi] + " – " + DayInfo.CHI[chi]); }

        if ((canTuoi + 5) % 10 == can) { s += 15; r.add("Thiên can hợp " + DayInfo.CAN[canTuoi] + " – " + DayInfo.CAN[can]); }
        int ht = hanhIdx(Almanac.HANH_CAN[canTuoi]), hc = hanhIdx(Almanac.HANH_CAN[can]);
        if ((hc + 1) % 5 == ht) { s += 10; r.add("Can " + DayInfo.CAN[can] + " sinh can tuổi"); }
        else if ((hc + 2) % 5 == ht) { s -= 10; r.add("Can " + DayInfo.CAN[can] + " khắc can tuổi"); }

        String nt = napHanh(canTuoi, chiTuoi), nd = napHanh(can, chi);
        int a = hanhIdx(nt), b = hanhIdx(nd);
        if ((b + 1) % 5 == a) { s += 10; r.add(nd + " sinh " + nt + " (nạp âm)"); }
        else if (a == b) { s += 5; r.add("Cùng hành " + nt + " (nạp âm)"); }
        else if ((b + 2) % 5 == a) { s -= 15; r.add(nd + " khắc " + nt + " (nạp âm)"); }
        else if ((a + 2) % 5 == b) { s -= 5; r.add(nt + " khắc " + nd + " (nạp âm)"); }

        if (r.isEmpty()) r.add("Không hợp không xung với " + ten);
        return new Result(Math.max(5, Math.min(95, s)), r);
    }

    /** Báo cáo cho một ngày với tuổi theo năm sinh âm lịch. */
    public static Report report(int birthLunarYear, DayInfo d) {
        int ct = (birthLunarYear + 6) % 10, zt = (birthLunarYear + 8) % 12;
        int monthCan = (d.lunarYear * 12 + d.lunarMonth + 3) % 10, monthChi = (d.lunarMonth + 1) % 12;
        int yearCan = (d.lunarYear + 6) % 10, yearChi = (d.lunarYear + 8) % 12;
        return new Report(
            compare(ct, zt, d.dayCan, d.dayChi),
            compare(ct, zt, monthCan, monthChi),
            compare(ct, zt, yearCan, yearChi));
    }

    public static String tuoiName(int birthLunarYear) {
        return DayInfo.CAN[(birthLunarYear + 6) % 10] + " " + DayInfo.CHI[(birthLunarYear + 8) % 12];
    }
}
