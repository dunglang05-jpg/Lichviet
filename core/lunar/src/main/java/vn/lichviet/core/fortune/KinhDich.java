package vn.lichviet.core.fortune;

import java.util.Random;

/** Gieo quẻ Kinh Dịch bằng 3 đồng xu, 6 lần (hào dưới lên trên). */
public final class KinhDich {
    private KinhDich() {}

    /** Bát quái theo thứ tự: Càn, Chấn, Khảm, Cấn, Khôn, Tốn, Ly, Đoài. */
    public static final String[] QUAI = {"Càn", "Chấn", "Khảm", "Cấn", "Khôn", "Tốn", "Ly", "Đoài"};
    public static final String[] TUONG = {"Thiên", "Lôi", "Thủy", "Sơn", "Địa", "Phong", "Hỏa", "Trạch"};
    /** 3 hào (dưới -> trên), 1 = dương. */
    private static final int[][] LINES = {{1, 1, 1}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {0, 0, 0}, {0, 1, 1}, {1, 0, 1}, {1, 1, 0}};
    /** Số quẻ Văn Vương: [quái trên][quái dưới]. */
    private static final int[][] KW = {
        {1, 25, 6, 33, 12, 44, 13, 10},
        {34, 51, 40, 62, 16, 32, 55, 54},
        {5, 3, 29, 39, 8, 48, 63, 60},
        {26, 27, 4, 52, 23, 18, 22, 41},
        {11, 24, 7, 15, 2, 46, 36, 19},
        {9, 42, 59, 53, 20, 57, 37, 61},
        {14, 21, 64, 56, 35, 50, 30, 38},
        {43, 17, 47, 31, 45, 28, 49, 58},
    };
    public static final String[] TEN = {"", "Càn", "Khôn", "Truân", "Mông", "Nhu", "Tụng", "Sư", "Tỷ", "Tiểu Súc", "Lý",
        "Thái", "Bĩ", "Đồng Nhân", "Đại Hữu", "Khiêm", "Dự", "Tùy", "Cổ", "Lâm", "Quan",
        "Phệ Hạp", "Bí", "Bác", "Phục", "Vô Vọng", "Đại Súc", "Di", "Đại Quá", "Khảm", "Ly",
        "Hàm", "Hằng", "Độn", "Đại Tráng", "Tấn", "Minh Di", "Gia Nhân", "Khuê", "Kiển", "Giải",
        "Tổn", "Ích", "Quải", "Cấu", "Tụy", "Thăng", "Khốn", "Tỉnh", "Cách", "Đỉnh",
        "Chấn", "Cấn", "Tiệm", "Quy Muội", "Phong", "Lữ", "Tốn", "Đoài", "Hoán", "Tiết",
        "Trung Phu", "Tiểu Quá", "Ký Tế", "Vị Tế"};
    public static final class Cast {
        public final int[] lines = new int[6];   // 6,7,8,9 (dưới -> trên)
        public int main, changed;                 // số quẻ; changed = 0 nếu không có hào động
        public String mainName, changedName;
    }

    static int trigram(int[] l) {
        for (int i = 0; i < 8; i++) if (LINES[i][0] == l[0] && LINES[i][1] == l[1] && LINES[i][2] == l[2]) return i;
        return 0;
    }

    public static int number(int[] yang) { // 6 hào 0/1 dưới -> trên
        int lower = trigram(new int[]{yang[0], yang[1], yang[2]});
        int upper = trigram(new int[]{yang[3], yang[4], yang[5]});
        return KW[upper][lower];
    }

    public static String fullName(int[] yang) {
        int lower = trigram(new int[]{yang[0], yang[1], yang[2]});
        int upper = trigram(new int[]{yang[3], yang[4], yang[5]});
        int n = KW[upper][lower];
        if (upper == lower) return "Thuần " + QUAI[upper] + " (" + TEN[n] + ")";
        return TUONG[upper] + " " + TUONG[lower] + " " + TEN[n];
    }

    public static Cast cast(Random rnd) {
        Cast c = new Cast();
        int[] a = new int[6], b = new int[6];
        boolean moving = false;
        for (int i = 0; i < 6; i++) {
            int sum = 0;
            for (int k = 0; k < 3; k++) sum += rnd.nextBoolean() ? 3 : 2; // mặt ngửa 3, sấp 2
            c.lines[i] = sum;
            a[i] = (sum == 7 || sum == 9) ? 1 : 0;
            b[i] = sum == 6 ? 1 : sum == 9 ? 0 : a[i];
            if (sum == 6 || sum == 9) moving = true;
        }
        c.main = number(a);
        c.mainName = fullName(a);
        if (moving) { c.changed = number(b); c.changedName = fullName(b); }
        return c;
    }
}
