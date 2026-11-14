package vn.lichviet.core;

import java.util.ArrayList;
import java.util.List;

/** Toàn bộ thông tin của một ngày: âm lịch, can chi, ngày giờ tốt xấu, lễ, tiết khí... */
public final class DayInfo {
    public static final String[] CAN = {"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"};
    public static final String[] CHI = {"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};
    public static final String[] CON_GIAP = {"Chuột", "Trâu", "Hổ", "Mèo", "Rồng", "Rắn", "Ngựa", "Dê", "Khỉ", "Gà", "Chó", "Heo"};
    public static final String[] THU = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
    public static final String[] THU_NGAN = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};

    public static final String[] TRUC = {"Kiến", "Trừ", "Mãn", "Bình", "Định", "Chấp", "Phá", "Nguy", "Thành", "Thâu", "Khai", "Bế"};
    // 1 = tốt, 0 = trung bình, -1 = xấu, -2 = rất xấu
    private static final int[] TRUC_SCORE = {0, 0, 1, 1, 1, 0, -2, -1, 1, 1, 1, -1};
    private static final String[][] TRUC_NEN = {
        {"Xuất hành", "Khai trương", "Cưới hỏi", "Nhậm chức"},
        {"Dọn dẹp nhà cửa", "Cúng giải hạn", "Chữa bệnh", "Trừ tà"},
        {"Cầu tài", "Nhập kho", "Khai trương", "Cầu phúc"},
        {"Cưới hỏi", "Xuất hành", "Sửa nhà", "Thăm viếng"},
        {"Cưới hỏi", "Ký kết hợp đồng", "Mua bán", "Nhập học"},
        {"Xây dựng nhỏ", "Sửa chữa", "Cầu tự", "Trồng cây"},
        {"Phá dỡ", "Dọn dẹp nhà cũ", "Trị bệnh"},
        {"Cúng lễ", "Cầu phúc", "Tu tâm dưỡng tính"},
        {"Khai trương", "Cưới hỏi", "Nhập học", "Ký kết", "Xuất hành"},
        {"Thu hoạch", "Thu nợ", "Nhập kho", "Mua sắm"},
        {"Khai trương", "Động thổ", "Cưới hỏi", "Xuất hành", "Nhận việc"},
        {"Đắp đê", "Lấp hố", "Giữ gìn, nghỉ ngơi"}
    };
    private static final String[][] TRUC_KY = {
        {"Động thổ", "Đào giếng", "Chôn cất"},
        {"Cưới hỏi", "Xuất hành xa", "Nhậm chức"},
        {"Chôn cất", "Kiện tụng"},
        {"Chôn cất", "Dời giường"},
        {"Kiện tụng", "Đi xa", "Chữa bệnh"},
        {"Xuất hành", "Dọn nhà", "Mở cửa hàng"},
        {"Cưới hỏi", "Khai trương", "Ký kết", "Động thổ"},
        {"Leo cao", "Đi thuyền", "Xuất hành xa", "Khai trương"},
        {"Kiện tụng", "Tranh chấp"},
        {"Khởi công", "Xuất hành", "An táng"},
        {"Chôn cất", "Kiện tụng"},
        {"Khai trương", "Xuất hành", "Cưới hỏi", "Khám bệnh"}
    };

    public static final String[] THAN_12 = {"Thanh Long", "Minh Đường", "Thiên Hình", "Chu Tước", "Kim Quỹ", "Thiên Đức",
        "Bạch Hổ", "Ngọc Đường", "Thiên Lao", "Huyền Vũ", "Tư Mệnh", "Câu Trần"};
    private static final boolean[] THAN_HOANG_DAO = {true, true, false, false, true, true, false, true, false, false, true, false};
    // chi của ngày Thanh Long theo chi tháng (Dần=2 ... ) chỉ số: chiThang 0..11
    private static final int[] THANH_LONG_START = {8, 10, 0, 2, 4, 6, 8, 10, 0, 2, 4, 6};

    public static final String[] TU = {"Giác", "Cang", "Đê", "Phòng", "Tâm", "Vĩ", "Cơ", "Đẩu", "Ngưu", "Nữ", "Hư", "Nguy", "Thất", "Bích",
        "Khuê", "Lâu", "Vị", "Mão", "Tất", "Chủy", "Sâm", "Tỉnh", "Quỷ", "Liễu", "Tinh", "Trương", "Dực", "Chẩn"};
    private static final boolean[] TU_TOT = {true, false, false, true, false, true, true, true, false, false, false, false, true, true,
        false, true, true, false, true, false, true, true, false, false, false, true, false, true};
    private static final String[] HANH_7 = {"Mộc", "Kim", "Thổ", "Nhật", "Nguyệt", "Hỏa", "Thủy"};

    public static final String[] TIET_KHI = {"Xuân phân", "Thanh minh", "Cốc vũ", "Lập hạ", "Tiểu mãn", "Mang chủng", "Hạ chí", "Tiểu thử",
        "Đại thử", "Lập thu", "Xử thử", "Bạch lộ", "Thu phân", "Hàn lộ", "Sương giáng", "Lập đông", "Tiểu tuyết", "Đại tuyết", "Đông chí",
        "Tiểu hàn", "Đại hàn", "Lập xuân", "Vũ thủy", "Kinh trập"};

    private static final String[] HOUR_PATTERN = {
        "110100101100", // Tý, Ngọ
        "001101001011", // Sửu, Mùi
        "110011010010", // Dần, Thân
        "101100110100", // Mão, Dậu
        "001011001101", // Thìn, Tuất
        "010010110011"  // Tỵ, Hợi
    };
    private static final String[] HOUR_RANGE = {"23-1", "1-3", "3-5", "5-7", "7-9", "9-11", "11-13", "13-15", "15-17", "17-19", "19-21", "21-23"};

    public static final class HourInfo {
        public final String name;       // "Tý"
        public final String canChi;     // "Giáp Tý"
        public final String range;      // "23-1"
        public final boolean good;
        HourInfo(String name, String canChi, String range, boolean good) {
            this.name = name;
            this.canChi = canChi;
            this.range = range;
            this.good = good;
        }
    }

    public final int solarDay, solarMonth, solarYear, weekday, jd;
    public final int lunarDay, lunarMonth, lunarYear;
    public final boolean leap;
    public final String dayCanChi, monthCanChi, yearCanChi;
    public final String yearAnimal;
    public final boolean hoangDao;
    public final String than12;
    public final String truc;
    public final int trucIndex, dayCan, dayChi;
    public final boolean tamNuong, nguyetKy;
    public final String tu;
    public final String tuHanh;
    public final boolean tuTot;
    public final String tietKhiToday;      // null nếu hôm nay không phải ngày tiết khí
    public final String tietKhiCurrent;    // tiết khí đang trong kỳ
    public final int score;                // 1..5
    public final String verdict;
    public final List<String> nenLam = new ArrayList<>();
    public final List<String> kyLam = new ArrayList<>();
    public final List<String> luuY = new ArrayList<>();
    public final List<HourInfo> hours = new ArrayList<>();
    public final String hyThan, taiThan, chiXung;
    private final Almanac almanac;
    public final List<Holidays.Item> holidays;

    public static DayInfo of(int d, int m, int y) {
        return new DayInfo(d, m, y);
    }

    private DayInfo(int d, int m, int y) {
        solarDay = d;
        solarMonth = m;
        solarYear = y;
        jd = LunarCalendar.jdFromDate(d, m, y);
        weekday = (jd + 1) % 7;
        int[] l = LunarCalendar.solarToLunar(d, m, y);
        lunarDay = l[0];
        lunarMonth = l[1];
        lunarYear = l[2];
        leap = l[3] == 1;

        dayCan = (jd + 9) % 10;
        dayChi = (jd + 1) % 12;
        dayCanChi = CAN[dayCan] + " " + CHI[dayChi];
        monthCanChi = CAN[(lunarYear * 12 + lunarMonth + 3) % 10] + " " + CHI[(lunarMonth + 1) % 12] + (leap ? " (nhuận)" : "");
        yearCanChi = CAN[(lunarYear + 6) % 10] + " " + CHI[(lunarYear + 8) % 12];
        yearAnimal = CON_GIAP[(lunarYear + 8) % 12];

        // Chi tháng theo tháng âm lịch (tháng Giêng = Dần), cách tính phổ biến của lịch Việt
        // 12 thần (hoàng/hắc đạo) theo chi tháng âm lịch (tháng Giêng = Dần)
        int monthChi = (lunarMonth + 1) % 12;
        // Trực (Kiến Trừ) theo tháng tiết khí: tháng mới bắt đầu từ ngày giao tiết
        double lonEnd = LunarCalendar.sunLongitudeDeg(jd, 23.99);
        int tietMonth = (int) Math.floor(((lonEnd + 45.0) % 360.0) / 30.0); // 0 = tháng Dần (Lập xuân)
        int tietMonthChi = (tietMonth + 2) % 12;

        int trucIdx = ((dayChi - tietMonthChi) % 12 + 12) % 12;
        trucIndex = trucIdx;
        truc = TRUC[trucIdx];

        int thanIdx = ((dayChi - THANH_LONG_START[monthChi]) % 12 + 12) % 12;
        // THANH_LONG_START đã được chọn cho chi tháng (Tý=0..)
        than12 = THAN_12[thanIdx];
        hoangDao = THAN_HOANG_DAO[thanIdx];

        int tuIdx = ((jd - 2451572) % 28 + 28 + 15) % 28; // mốc: 28/1/2000 (JDN 2451572) = Lâu (15)
        tu = TU[tuIdx];
        tuTot = TU_TOT[tuIdx];
        tuHanh = HANH_7[((tuIdx % 7) + 7) % 7];

        // Tiết khí
        double lon0 = LunarCalendar.sunLongitudeDeg(jd, 0.0);
        double lon1 = LunarCalendar.sunLongitudeDeg(jd + 1, 0.0);
        int s0 = (int) Math.floor(lon0 / 15.0);
        int s1 = (int) Math.floor(lon1 / 15.0);
        tietKhiCurrent = TIET_KHI[s1 % 24];
        tietKhiToday = (s0 != s1) ? TIET_KHI[s1 % 24] : null;

        nenLam.addAll(java.util.Arrays.asList(TRUC_NEN[trucIdx]));
        kyLam.addAll(java.util.Arrays.asList(TRUC_KY[trucIdx]));

        // Hướng xuất hành
        String[] hy = {"Đông Bắc", "Tây Bắc", "Tây Nam", "Chính Nam", "Đông Nam"};
        hyThan = hy[dayCan % 5];
        String[] tai = {"Đông Nam", "Đông Nam", "Chính Đông", "Chính Đông", "Chính Nam", "Chính Nam", "Tây Nam", "Tây Nam", "Chính Tây", "Chính Tây"};
        taiThan = tai[dayCan];
        chiXung = "Xung tuổi " + CHI[(dayChi + 6) % 12];

        // Giờ hoàng đạo
        String pattern = HOUR_PATTERN[dayChi % 6];
        for (int h = 0; h < 12; h++) {
            int can = (dayCan % 5 * 2 + h) % 10;
            hours.add(new HourInfo(CHI[h], CAN[can] + " " + CHI[h], HOUR_RANGE[h], pattern.charAt(h) == '1'));
        }

        // Lưu ý ngày kỵ
        int tn = 0;
        if (lunarDay == 3 || lunarDay == 7 || lunarDay == 13 || lunarDay == 18 || lunarDay == 22 || lunarDay == 27) {
            luuY.add("Ngày Tam Nương: kỵ khởi công, cưới hỏi, xuất hành");
            tn = 1;
        }
        int nk = 0;
        if (lunarDay == 5 || lunarDay == 14 || lunarDay == 23) {
            luuY.add("Ngày Nguyệt Kỵ: nên hạn chế việc lớn");
            nk = 1;
        }
        if (lunarDay == 1 || lunarDay == 15) {
            luuY.add(lunarDay == 1 ? "Mùng 1: ngày cúng sóc, ăn chay, lễ chùa" : "Rằm: ngày cúng vọng, ăn chay, lễ chùa");
        }
        if (tietKhiToday != null) {
            luuY.add("Tiết khí: " + tietKhiToday);
        }

        // Điểm ngày
        int[] nx = LunarCalendar.jdToDate(jd + 1);
        boolean lastDay = LunarCalendar.solarToLunar(nx[0], nx[1], nx[2])[0] == 1;
        almanac = new Almanac(this, lastDay, (lunarYear * 12 + lunarMonth + 3) % 10, (lunarMonth + 1) % 12,
            (lunarYear + 6) % 10, (lunarYear + 8) % 12);
        int balance = almanac.goodStars.size() - almanac.badStars.size();
        int dc = 0;
        for (Almanac.KyNhat kn : almanac.kyNhat) if (kn.name.startsWith("Dương Công")) dc = 1;
        if (dc == 1) luuY.add("Dương Công Kỵ Nhật: trăm việc nên tránh");
        int sc = 3 + (hoangDao ? 1 : -1) + TRUC_SCORE[trucIdx] + (tuTot ? 1 : -1) - tn - nk - dc
            + (balance >= 3 ? 1 : balance <= -3 ? -1 : 0);
        tamNuong = tn == 1;
        nguyetKy = nk == 1;
        if (sc < 1) sc = 1;
        if (sc > 5) sc = 5;
        score = sc;
        if (score >= 5) verdict = "Ngày rất tốt";
        else if (score == 4) verdict = "Ngày tốt";
        else if (score == 3) verdict = "Ngày bình thường";
        else if (score == 2) verdict = "Ngày xấu";
        else verdict = "Ngày rất xấu";

        holidays = Holidays.of(this);
    }

    /** Có phải ngày cuối năm âm (30 hoặc 29 Tết, Giao thừa)? */
    public static boolean isGiaoThua(int d, int m, int y) {
        int[] l = LunarCalendar.solarToLunar(d, m, y);
        if (l[1] != 12 || l[3] == 1) return false;
        int jd = LunarCalendar.jdFromDate(d, m, y) + 1;
        int[] nd = LunarCalendar.jdToDate(jd);
        int[] ln = LunarCalendar.solarToLunar(nd[0], nd[1], nd[2]);
        return ln[0] == 1 && ln[1] == 1;
    }

    /** Thông tin lịch vạn niên chi tiết: sao Ngọc Hạp, ngày kỵ, lục diệu, xuất hành, nạp âm, Bành Tổ... */
    public Almanac almanac() {
        return almanac;
    }

    public String lunarText() {
        return lunarDay + "/" + lunarMonth + (leap ? " nhuận" : "");
    }

    public String lunarFull() {
        return "Ngày " + lunarDay + " tháng " + lunarMonth + (leap ? " nhuận" : "") + " năm " + yearCanChi;
    }

    public String goodHoursText() {
        StringBuilder sb = new StringBuilder();
        for (HourInfo h : hours) {
            if (h.good) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(h.name).append(" (").append(h.range).append(")");
            }
        }
        return sb.toString();
    }

    public String starsText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < score; i++) sb.append('★');
        for (int i = score; i < 5; i++) sb.append('☆');
        return sb.toString();
    }
}
