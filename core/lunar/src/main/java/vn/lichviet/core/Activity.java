package vn.lichviet.core;

/**
 * Các việc thường xem ngày. Mỗi việc gồm: các Trực hợp, từ khóa để tránh sao xấu kỵ việc đó
 * (theo mô tả sao Ngọc Hạp), và có tránh ngày Sát Chủ hay không. Thêm việc mới chỉ cần thêm một dòng.
 */
public enum Activity {
    CUOI_HOI("Cưới hỏi", new String[] {"giá thú", "cưới"}, true, 0, 3, 4, 8, 10),
    KHAI_TRUONG("Khai trương", new String[] {"khai trương", "mở hàng", "kinh doanh"}, true, 0, 2, 8, 10),
    XUAT_HANH("Xuất hành, đi xa", new String[] {"xuất hành"}, false, 0, 3, 8, 10),
    DONG_THO("Động thổ, xây dựng", new String[] {"động thổ", "xây", "làm nhà"}, true, 5, 8, 10),
    NHAP_TRACH("Dọn nhà, nhập trạch", new String[] {"nhập trạch"}, true, 4, 8, 10),
    MUA_XE("Mua xe", new String[] {"cầu tài", "xuất hành"}, false, 2, 4, 8, 9),
    MUA_NHA("Mua nhà, đất", new String[] {"nhập trạch", "kinh doanh"}, true, 4, 8, 9),
    MUA_TAI_SAN("Mua tài sản giá trị", new String[] {"cầu tài", "kinh doanh"}, false, 2, 4, 9),
    KY_KET("Ký kết hợp đồng", new String[] {"kinh doanh", "kiện"}, true, 4, 8),
    CAU_TAI("Mua bán, cầu tài", new String[] {"cầu tài", "kinh doanh"}, true, 2, 4, 9),
    NHAP_HOC("Nhập học, thi cử", new String[] {}, false, 4, 8),
    NHAN_VIEC("Nhận việc, nhậm chức", new String[] {}, true, 0, 10),
    CUNG_LE("Cúng lễ, cầu phúc", new String[] {"tế tự", "cúng"}, false, 1, 2, 7),
    CHUA_BENH("Chữa bệnh", new String[] {}, false, 1, 6);

    public final String label;
    private final String[] avoid;
    final boolean avoidSatChu;
    private final int[] goodTruc;

    Activity(String label, String[] avoid, boolean avoidSatChu, int... goodTruc) {
        this.label = label;
        this.avoid = avoid;
        this.avoidSatChu = avoidSatChu;
        this.goodTruc = goodTruc;
    }

    public boolean trucFits(int trucIndex) {
        for (int t : goodTruc) if (t == trucIndex) return true;
        return false;
    }

    /** Sao xấu có kỵ riêng việc này không. */
    public boolean badStarHits(Almanac.Star s) {
        if (s.good) return false;
        String d = s.desc.toLowerCase();
        for (String k : avoid) if (d.contains(k)) return true;
        return false;
    }
}
