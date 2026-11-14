package vn.lichviet.core.fortune;

/** Thước Lỗ Ban: 52,2 cm (thông thủy), 42,9 cm (âm phần), 38,8 cm (dương trạch). */
public final class LoBan {
    private LoBan() {}

    public static final String[] TYPE = {"Thông thủy 52,2 cm (cửa, cửa sổ)", "Dương trạch 42,9 cm (nhà, bếp, giường, tủ)", "Âm phần 38,8 cm (bàn thờ, mộ)"};
    public static final double[] LEN = {52.2, 42.9, 38.8};
    public static final String[][] CUNG = {
        {"Quý Nhân", "Hiểm Họa", "Thiên Tai", "Thiên Tài", "Nhân Lộc", "Cô Độc", "Thiên Tặc", "Tể Tướng"},
        {"Tài", "Bệnh", "Ly", "Nghĩa", "Quan", "Kiếp", "Hại", "Bản"},
        {"Đinh", "Hại", "Vượng", "Khổ", "Nghĩa", "Quan", "Tử", "Hưng", "Thất", "Tài"},
    };
    public static final boolean[][] TOT = {
        {true, false, false, true, true, false, false, true},
        {true, false, false, true, true, false, false, true},
        {true, false, true, false, true, true, false, true, false, true},
    };

    /** Chỉ số cung của kích thước [cm] trên thước [type]. */
    public static int cung(int type, double cm) {
        double len = LEN[type];
        int n = CUNG[type].length;
        double r = cm % len;
        int i = (int) Math.floor(r / (len / n));
        return Math.min(n - 1, Math.max(0, i));
    }

    public static boolean tot(int type, double cm) { return TOT[type][cung(type, cm)]; }

    /** Kích thước tốt gần nhất (bước 0,5 cm) quanh [cm]. */
    public static double nearestGood(int type, double cm) {
        for (double d = 0; d < 30; d += 0.5) {
            if (cm - d > 0 && tot(type, cm - d)) return cm - d;
            if (tot(type, cm + d)) return cm + d;
        }
        return cm;
    }
}
