package vn.lichviet.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thông tin xem ngày chi tiết (lịch vạn niên): sao tốt/xấu theo Ngọc Hạp Thông Thư, ngày kỵ,
 * Khổng Minh lục diệu, giờ xuất hành Lý Thuần Phong, ngũ hành nạp âm, tuổi xung, Bành Tổ bách kỵ,
 * hướng Hạc thần. Lấy qua {@link DayInfo#almanac()}.
 */
public final class Almanac {
    public static final String[] HANH_CAN = {"Mộc", "Mộc", "Hỏa", "Hỏa", "Thổ", "Thổ", "Kim", "Kim", "Thủy", "Thủy"};
    public static final String[] HANH_CHI = {"Thủy", "Thổ", "Mộc", "Mộc", "Thổ", "Hỏa", "Hỏa", "Thổ", "Kim", "Kim", "Thổ", "Thủy"};
    private static final String[] HANH = {"Mộc", "Hỏa", "Thổ", "Kim", "Thủy"}; // vòng tương sinh

    public static final String[] KHONG_MINH = {"Đại An", "Lưu Liên", "Tốc Hỷ", "Xích Khẩu", "Tiểu Cát", "Không Vong"};
    private static final boolean[] KHONG_MINH_TOT = {true, false, true, false, true, false};
    private static final String[] KHONG_MINH_DESC = {
        "Ngày bình yên, mọi việc thuận lợi, cầu tài, xuất hành đều tốt.",
        "Việc dễ trì hoãn, dây dưa; nên kiên nhẫn, tránh việc gấp.",
        "Có tin vui, may mắn đến nhanh; tốt cho gặp gỡ, cầu tài.",
        "Dễ cãi vã, thị phi, hiểu lầm; nên giữ lời ăn tiếng nói.",
        "Ngày tốt nhỏ, có quý nhân giúp; việc nhà, buôn bán thuận lợi.",
        "Mưu sự khó thành, dễ hao tài; nên tránh việc lớn."
    };

    public static final String[] XUAT_HANH = {"Đại An", "Lưu Niên", "Tốc Hỷ", "Xích Khẩu", "Tiểu Cát", "Tuyệt Lộ"};
    private static final boolean[] XUAT_HANH_TOT = {true, false, true, false, true, false};
    private static final String[] XUAT_HANH_DESC = {
        "Đi đường bình yên, cầu tài theo hướng Tây Nam có lợi.",
        "Việc khó thành, tin tức chậm; nên hoãn kiện tụng, việc gấp.",
        "Tin vui sắp tới, gặp gỡ có lợi; cầu tài nên đi hướng Nam.",
        "Dễ xảy ra tranh cãi; nên tránh hội họp, thận trọng lời nói.",
        "Giờ rất tốt, ra đi gặp may, buôn bán có lời, việc nhà êm ấm.",
        "Cầu tài không lợi, đi xa dễ gặp trắc trở; nên cẩn trọng."
    };

    private static final String[] TAI_THAN = {"Đông Nam", "Đông Nam", "Chính Đông", "Chính Đông", "Chính Nam",
        "Chính Nam", "Tây Nam", "Tây Nam", "Chính Tây", "Chính Tây"};
    private static final String[] HY_THAN = {"Đông Bắc", "Tây Bắc", "Tây Nam", "Chính Nam", "Đông Nam"};

    // Dương Công kỵ nhật: {tháng âm, ngày âm}
    private static final int[][] DUONG_CONG = {{1, 13}, {2, 11}, {3, 9}, {4, 7}, {5, 5}, {6, 3}, {7, 1}, {7, 29},
        {8, 27}, {9, 25}, {10, 23}, {11, 21}, {12, 22}};

    // Chi ngày phạm theo tháng âm 1..12 (-1 = không có)
    private static final int[] SAT_CHU_DUONG = {0, 1, 1, 10, 4, 4, 1, 4, 1, 4, 7, 4};
    private static final int[] SAT_CHU_AM = {5, 0, 7, 3, 8, 10, 1, 11, 6, 9, 2, -1};
    private static final int[] THU_TU = {10, 4, 11, 5, 0, 6, 1, 7, 2, 8, 3, 9};

    public static final class Star {
        public final String name, desc;
        public final boolean good;
        Star(String name, String desc, boolean good) {
            this.name = name;
            this.desc = desc;
            this.good = good;
        }
    }

    public static final class KyNhat {
        public final String name, desc;
        KyNhat(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    public static final class XuatHanhHour {
        public final String chi, range, name, desc;
        public final boolean good;
        XuatHanhHour(String chi, String range, int idx) {
            this.chi = chi;
            this.range = range;
            this.name = XUAT_HANH[idx];
            this.desc = XUAT_HANH_DESC[idx];
            this.good = XUAT_HANH_TOT[idx];
        }
    }

    // ---- Kết quả ----
    public final List<Star> goodStars, badStars;
    public final List<KyNhat> kyNhat;
    public final String khongMinh, khongMinhDesc;
    public final boolean khongMinhTot;
    public final List<XuatHanhHour> xuatHanh;
    public final String napAmNgay, napAmThang, napAmNam;
    public final String hanhNgay;          // hành nạp âm của ngày
    public final String quanHeCanChi;      // vd "Chi sinh Can (Thủy sinh Mộc) – Nghĩa nhật, cát"
    public final boolean quanHeTot;
    public final String tuoiXung, tuoiNgoaiLe, hopXung, tamSat;
    public final String banhToCan, banhToChi;
    public final String hyThan, taiThan, hacThan;

    static int canChiIndex(int can, int chi) {
        for (int i = 0; i < 60; i++) if (i % 10 == can && i % 12 == chi) return i;
        throw new IllegalArgumentException();
    }

    Almanac(DayInfo d, boolean lastDayOfMonth, int monthCan, int monthChi, int yearCan, int yearChi) {
        int cc = canChiIndex(d.dayCan, d.dayChi);
        int lm = d.lunarMonth, ld = d.lunarDay;

        List<Star> g = new ArrayList<>(), b = new ArrayList<>();
        for (int i = 0; i < AlmanacData.STAR_NAME.length; i++) {
            if (((AlmanacData.STAR_MASK[i][lm - 1] >>> cc) & 1L) != 0) {
                Star s = new Star(AlmanacData.STAR_NAME[i], AlmanacData.STAR_DESC[i], AlmanacData.STAR_GOOD[i]);
                (s.good ? g : b).add(s);
            }
        }
        goodStars = Collections.unmodifiableList(g);
        badStars = Collections.unmodifiableList(b);

        List<KyNhat> k = new ArrayList<>();
        if (ld == 3 || ld == 7 || ld == 13 || ld == 18 || ld == 22 || ld == 27)
            k.add(new KyNhat("Tam Nương", "Kỵ khai trương, xuất hành, cưới hỏi, động thổ, sửa nhà."));
        if (ld == 5 || ld == 14 || ld == 23)
            k.add(new KyNhat("Nguyệt Kỵ", "“Mùng năm, mười bốn, hai ba”: nên tránh đi xa, buôn bán, việc lớn."));
        for (int[] p : DUONG_CONG) {
            if (p[0] == lm && p[1] == ld) k.add(new KyNhat("Dương Công Kỵ Nhật", "Trăm việc nên tránh, nhất là khởi công, cưới hỏi."));
        }
        if (SAT_CHU_DUONG[lm - 1] == d.dayChi)
            k.add(new KyNhat("Sát Chủ Dương", "Kỵ xây dựng, cưới hỏi, mua bán nhà, nhận việc, đầu tư."));
        if (SAT_CHU_AM[lm - 1] == d.dayChi)
            k.add(new KyNhat("Sát Chủ Âm", "Kỵ việc mai táng, tu sửa mồ mả."));
        if (THU_TU[lm - 1] == d.dayChi)
            k.add(new KyNhat("Thụ Tử", "Ngày rất kỵ theo dân gian; nên hạn chế việc quan trọng."));
        if (lastDayOfMonth) k.add(new KyNhat("Nguyệt Tận", "Ngày cuối tháng âm: nên hạn chế khởi sự việc mới."));
        kyNhat = Collections.unmodifiableList(k);

        int km = (lm + ld + 5) % 6;
        khongMinh = KHONG_MINH[km];
        khongMinhDesc = KHONG_MINH_DESC[km];
        khongMinhTot = KHONG_MINH_TOT[km];

        List<XuatHanhHour> xh = new ArrayList<>();
        String[] ranges = {"23-1", "1-3", "3-5", "5-7", "7-9", "9-11", "11-13", "13-15", "15-17", "17-19", "19-21", "21-23"};
        for (int h = 0; h < 12; h++) xh.add(new XuatHanhHour(DayInfo.CHI[h], ranges[h], (lm + ld + h + 5) % 6));
        xuatHanh = Collections.unmodifiableList(xh);

        napAmNgay = AlmanacData.NAP_AM[cc];
        napAmThang = AlmanacData.NAP_AM[canChiIndex(monthCan, monthChi)];
        napAmNam = AlmanacData.NAP_AM[canChiIndex(yearCan, yearChi)];
        hanhNgay = napAmNgay.substring(napAmNgay.lastIndexOf(' ') + 1);

        String hc = HANH_CAN[d.dayCan], hz = HANH_CHI[d.dayChi];
        int a = idx(hc), z = idx(hz);
        if (a == z) { quanHeCanChi = "Can Chi đồng hành (" + hc + ") – Chuyên nhật, cát"; quanHeTot = true; }
        else if ((a + 1) % 5 == z) { quanHeCanChi = "Can sinh Chi (" + hc + " sinh " + hz + ") – Bảo nhật, cát"; quanHeTot = true; }
        else if ((z + 1) % 5 == a) { quanHeCanChi = "Chi sinh Can (" + hz + " sinh " + hc + ") – Nghĩa nhật, cát"; quanHeTot = true; }
        else if ((a + 2) % 5 == z) { quanHeCanChi = "Can khắc Chi (" + hc + " khắc " + hz + ") – Chế nhật, cát trung bình"; quanHeTot = true; }
        else { quanHeCanChi = "Chi khắc Can (" + hz + " khắc " + hc + ") – Phạt nhật, hung"; quanHeTot = false; }

        tuoiXung = AlmanacData.TUOI_KY[cc];
        tuoiNgoaiLe = AlmanacData.TUOI_NGOAI_LE[cc];
        hopXung = AlmanacData.HOP_XUNG[d.dayChi];
        tamSat = AlmanacData.TAM_SAT[d.dayChi];
        banhToCan = AlmanacData.BANH_TO_CAN[d.dayCan];
        banhToChi = AlmanacData.BANH_TO_CHI[d.dayChi];

        hyThan = HY_THAN[d.dayCan % 5];
        taiThan = TAI_THAN[d.dayCan];
        hacThan = hacThan(cc);
    }

    private static int idx(String hanh) {
        for (int i = 0; i < 5; i++) if (HANH[i].equals(hanh)) return i;
        return -1;
    }

    /** Hạc thần đi 8 hướng (5–6 ngày mỗi hướng) và 16 ngày ở trên trời trong chu kỳ 60 ngày. */
    static String hacThan(int cc) {
        if (cc >= 2 && cc <= 6) return "Chính Nam";
        if (cc >= 7 && cc <= 12) return "Tây Nam";
        if (cc >= 13 && cc <= 17) return "Chính Tây";
        if (cc >= 18 && cc <= 23) return "Tây Bắc";
        if (cc >= 24 && cc <= 28) return "Chính Bắc";
        if (cc >= 29 && cc <= 44) return "Trên trời";
        if (cc >= 45 && cc <= 50) return "Đông Bắc";
        if (cc >= 51 && cc <= 55) return "Chính Đông";
        return "Đông Nam"; // 56..59, 0, 1
    }

    /** Tóm tắt hướng xuất hành. */
    public String huongText() {
        String hac = "Trên trời".equals(hacThan) ? "Hạc thần ở trên trời, không cần tránh hướng nào"
            : "Tránh hướng " + hacThan + " (gặp Hạc thần)";
        return "Hỷ thần: " + hyThan + " · Tài thần: " + taiThan + "\n" + hac;
    }
}
