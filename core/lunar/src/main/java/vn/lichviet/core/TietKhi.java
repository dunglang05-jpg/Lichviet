package vn.lichviet.core;

/**
 * Tiết khí chính xác tới phút: thời điểm giao tiết (Mặt Trời qua kinh độ bội số 15°),
 * ngày thứ mấy trong tiết, tháng tiết khí (can chi), mô tả ngắn.
 */
public final class TietKhi {
    /** 24 tiết khí bắt đầu từ Xuân phân (0°). */
    public static final String[] NAME = DayInfo.TIET_KHI;
    private static final String[] DESC = {
        "Giữa xuân, ngày đêm dài bằng nhau; khí trời ấm, cây cối đâm chồi.",
        "Trời trong sáng, mùa tảo mộ, thăm viếng mộ phần tổ tiên.",
        "Mưa rào cuối xuân, thuận lợi gieo trồng ngũ cốc.",
        "Bắt đầu mùa hạ, nắng tăng, cây cối sinh trưởng mạnh.",
        "Lúa mùa hạ bắt đầu vào hạt, trời nóng dần.",
        "Lúa trổ bông có râu, thời điểm thu hoạch và gieo cấy vụ mới.",
        "Giữa hạ, ngày dài nhất năm; nên giữ sức, tránh nắng gắt.",
        "Nóng nhẹ, mưa nhiều; chú ý giữ gìn sức khỏe.",
        "Nóng nhất trong năm; nên nghỉ ngơi điều độ, uống đủ nước.",
        "Bắt đầu mùa thu, khí trời dịu dần.",
        "Hết nóng, trời trở nên mát mẻ.",
        "Sương trắng xuất hiện buổi sớm, khí thu rõ rệt.",
        "Giữa thu, ngày đêm dài bằng nhau; thời tiết dễ chịu.",
        "Sương lạnh, trời se se; nên giữ ấm cơ thể.",
        "Sương giáng xuống, cuối thu, cây cối bắt đầu rụng lá.",
        "Bắt đầu mùa đông, dương khí ẩn tàng; hợp dưỡng sức, tích lũy.",
        "Tuyết nhẹ ở phương Bắc, khí trời lạnh dần.",
        "Tuyết lớn, lạnh sâu; nên giữ ấm, hạn chế ra ngoài khi rét đậm.",
        "Giữa đông, đêm dài nhất năm; âm cực dương sinh.",
        "Rét nhẹ đầu tháng Chạp, chuẩn bị đón năm mới.",
        "Rét đậm nhất năm, cuối mùa đông.",
        "Bắt đầu mùa xuân, khởi đầu năm tiết khí mới.",
        "Mưa ẩm, băng tuyết tan, cây cối hồi sinh.",
        "Sấm động, sâu bọ thức dậy sau mùa đông."
    };

    public final int index;           // 0..23 (0 = Xuân phân)
    public final String name, desc, nextName;
    public final double startJdLocal; // thời điểm giao tiết hiện tại (JD theo giờ VN)
    public final double nextJdLocal;  // thời điểm giao tiết kế tiếp
    public final int dayInTerm;       // ngày thứ mấy trong tiết (1 = ngày giao tiết)
    public final int termDays;        // số ngày từ ngày giao tiết hiện tại tới ngày giao tiết kế
    public final String monthCanChi;  // tháng tiết khí, vd "Kỷ Hợi"
    public final int monthNumber;     // tháng tiết khí theo số (1 = tháng Dần)

    /** Kinh độ biểu kiến của Mặt Trời (độ), có hiệu chỉnh quang sai, chương động và ΔT ≈ 69 giây. */
    private static double lonDeg(double jdUt) {
        double jdTt = jdUt + 69.0 / 86400.0;
        double t = (jdTt - 2451545.0) / 36525.0;
        double omega = Math.toRadians(125.04 - 1934.136 * t);
        double lon = LunarCalendar.sunLongitude(jdTt) * 180.0 / Math.PI - 0.00569 - 0.00478 * Math.sin(omega);
        return ((lon % 360.0) + 360.0) % 360.0;
    }

    private static double wrap(double d) {
        d = d % 360.0;
        if (d > 180) d -= 360;
        if (d < -180) d += 360;
        return d;
    }

    /** JD (UT) khi kinh độ Mặt Trời đạt target (độ), gần thời điểm guess. */
    static double crossing(double target, double guess) {
        double t = guess;
        for (int i = 0; i < 8; i++) t -= wrap(lonDeg(t) - target) / 0.98564736;
        return t;
    }

    public TietKhi(int d, int m, int y) {
        int jd = LunarCalendar.jdFromDate(d, m, y);
        double endOfDayUt = jd + 0.5 - LunarCalendar.TZ / 24.0 - 1e-6; // 24:00 giờ VN
        double lon = lonDeg(endOfDayUt);
        int k = (int) Math.floor(lon / 15.0) % 24;
        index = k;
        name = NAME[k];
        desc = DESC[k];
        nextName = NAME[(k + 1) % 24];
        double startUt = crossing(k * 15.0, endOfDayUt - wrap(lon - k * 15.0) / 0.98564736);
        double nextUt = crossing(((k + 1) % 24) * 15.0, startUt + 15.2);
        startJdLocal = startUt + LunarCalendar.TZ / 24.0;
        nextJdLocal = nextUt + LunarCalendar.TZ / 24.0;
        int startDay = LunarCalendar.jdUtToLocalDay(startUt);
        int nextDay = LunarCalendar.jdUtToLocalDay(nextUt);
        dayInTerm = jd - startDay + 1;
        termDays = nextDay - startDay;

        // Tháng tiết khí: 0 = Dần (Lập xuân 315°) ... mỗi tháng gồm 2 tiết
        int monthIdx = (int) Math.floor(((lon + 45.0) % 360.0) / 30.0);
        monthNumber = monthIdx + 1;
        int tietYear = (m <= 2 && monthIdx >= 10) ? y - 1 : y;
        int yearCan = (tietYear + 6) % 10;
        int can = ((yearCan % 5) * 2 + 2 + monthIdx) % 10;
        monthCanChi = DayInfo.CAN[can] + " " + DayInfo.CHI[(monthIdx + 2) % 12];
    }

    /** "16:52 7/11" */
    public static String formatLocal(double jdLocal) {
        int day = (int) Math.floor(jdLocal + 0.5);
        double frac = jdLocal + 0.5 - day;
        int minutes = (int) Math.round(frac * 1440);
        if (minutes == 1440) { minutes = 0; day++; }
        int[] dt = LunarCalendar.jdToDate(day);
        return String.format("%02d:%02d %d/%d", minutes / 60, minutes % 60, dt[0], dt[1]);
    }
}
