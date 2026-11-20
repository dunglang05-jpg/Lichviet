package vn.lichviet.core;

import java.util.ArrayList;
import java.util.List;

/** Ngày lễ, ngày kỷ niệm dương lịch và âm lịch của Việt Nam. */
public final class Holidays {
    private Holidays() {}

    public static final class Item {
        public final String name;
        public final boolean major;   // lễ lớn / nghỉ lễ chính thức
        public final boolean lunar;
        public Item(String name, boolean major, boolean lunar) {
            this.name = name;
            this.major = major;
            this.lunar = lunar;
        }
    }

    // {ngày, tháng, tên, lớn?[, áp dụng từ năm]}
    private static final Object[][] SOLAR = {
        {1, 1, "Tết Dương lịch", true},
        {3, 2, "Ngày thành lập Đảng", false},
        {14, 2, "Lễ Tình nhân (Valentine)", false},
        {27, 2, "Ngày Thầy thuốc Việt Nam", false},
        {8, 3, "Quốc tế Phụ nữ", false},
        {26, 3, "Ngày thành lập Đoàn", false},
        {30, 4, "Ngày Giải phóng miền Nam", true},
        {1, 5, "Quốc tế Lao động", true},
        {7, 5, "Chiến thắng Điện Biên Phủ", false},
        {19, 5, "Sinh nhật Chủ tịch Hồ Chí Minh", false},
        {1, 6, "Quốc tế Thiếu nhi", false},
        {21, 6, "Ngày Báo chí Việt Nam", false},
        {28, 6, "Ngày Gia đình Việt Nam", false},
        {11, 7, "Ngày Dân số Thế giới", false},
        {27, 7, "Ngày Thương binh Liệt sĩ", false},
        {19, 8, "Cách mạng tháng Tám", false},
        {2, 9, "Quốc khánh", true},
        {10, 9, "Ngày Truyền thống Công an", false},
        {1, 10, "Ngày Quốc tế Người cao tuổi", false},
        {10, 10, "Ngày Giải phóng Thủ đô", false},
        {13, 10, "Ngày Doanh nhân Việt Nam", false},
        {20, 10, "Ngày Phụ nữ Việt Nam", false},
        {31, 10, "Halloween", false},
        {9, 11, "Ngày Pháp luật Việt Nam", false},
        {20, 11, "Ngày Nhà giáo Việt Nam", false},
        // Nghị quyết 28/2026/QH16, hiệu lực 1/7/2026: nghỉ lễ hưởng nguyên lương từ năm 2026
        {24, 11, "Ngày Văn hóa Việt Nam", true, 2026},
        {1, 12, "Ngày Thế giới phòng chống AIDS", false},
        {22, 12, "Ngày thành lập Quân đội nhân dân", false},
        {24, 12, "Đêm Giáng sinh", false},
        {25, 12, "Lễ Giáng sinh", false}
    };

    // {ngày âm, tháng âm, tên, lớn?}
    private static final Object[][] LUNAR = {
        {1, 1, "Mùng 1 Tết Nguyên đán", true},
        {2, 1, "Mùng 2 Tết", true},
        {3, 1, "Mùng 3 Tết", true},
        {4, 1, "Mùng 4 Tết", false},
        {5, 1, "Mùng 5 Tết", false},
        {15, 1, "Tết Nguyên tiêu (Rằm tháng Giêng)", false},
        {3, 3, "Tết Hàn thực", false},
        {10, 3, "Giỗ Tổ Hùng Vương", true},
        {15, 4, "Lễ Phật Đản", false},
        {5, 5, "Tết Đoan Ngọ", false},
        {15, 7, "Lễ Vu Lan (Rằm tháng Bảy)", false},
        {15, 8, "Tết Trung Thu", false},
        {9, 9, "Tết Trùng Cửu", false},
        {10, 10, "Tết Hạ nguyên", false},
        {15, 10, "Rằm tháng Mười", false},
        {23, 12, "Ông Táo chầu trời", false}
    };

    static List<Item> of(DayInfo di) {
        List<Item> out = new ArrayList<>();
        for (Object[] h : SOLAR) {
            if ((Integer) h[0] == di.solarDay && (Integer) h[1] == di.solarMonth
                    && (h.length < 5 || di.solarYear >= (Integer) h[4])) {
                out.add(new Item((String) h[2], (Boolean) h[3], false));
            }
        }
        if (!di.leap) {
            for (Object[] h : LUNAR) {
                if ((Integer) h[0] == di.lunarDay && (Integer) h[1] == di.lunarMonth) {
                    out.add(new Item((String) h[2], (Boolean) h[3], true));
                }
            }
            if (di.lunarDay == 1 && di.lunarMonth != 1) {
                out.add(new Item("Mùng 1 âm lịch (ngày Sóc)", false, true));
            }
            if (di.lunarDay == 15 && di.lunarMonth != 1 && di.lunarMonth != 7 && di.lunarMonth != 8 && di.lunarMonth != 10) {
                out.add(new Item("Rằm (ngày Vọng)", false, true));
            }
        }
        if (DayInfo.isGiaoThua(di.solarDay, di.solarMonth, di.solarYear)) {
            out.add(new Item("Giao thừa (30/29 Tết)", true, true));
        }
        return out;
    }

    /** Danh sách các ngày lễ của một năm dương (dương lịch + âm lịch quy đổi), sắp theo ngày. */
    public static List<Object[]> listYear(int year) {
        List<Object[]> out = new ArrayList<>(); // {jd, d, m, y, Item}
        for (int m = 1; m <= 12; m++) {
            int dim = daysInMonth(m, year);
            for (int d = 1; d <= dim; d++) {
                DayInfo di = DayInfo.of(d, m, year);
                for (Item it : di.holidays) {
                    out.add(new Object[] {di.jd, d, m, year, it});
                }
            }
        }
        return out;
    }

    public static int daysInMonth(int m, int y) {
        switch (m) {
            case 2: return ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) ? 29 : 28;
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }
}
