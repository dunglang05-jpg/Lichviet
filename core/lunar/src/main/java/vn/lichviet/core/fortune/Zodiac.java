package vn.lichviet.core.fortune;

/** 12 cung hoàng đạo phương Tây. */
public final class Zodiac {
    private Zodiac() {}

    public static final String[] NAME = {"Bạch Dương", "Kim Ngưu", "Song Tử", "Cự Giải", "Sư Tử", "Xử Nữ",
        "Thiên Bình", "Bọ Cạp", "Nhân Mã", "Ma Kết", "Bảo Bình", "Song Ngư"};
    public static final String[] RANGE = {"21/3 – 19/4", "20/4 – 20/5", "21/5 – 20/6", "21/6 – 22/7", "23/7 – 22/8",
        "23/8 – 22/9", "23/9 – 22/10", "23/10 – 21/11", "22/11 – 21/12", "22/12 – 19/1", "20/1 – 18/2", "19/2 – 20/3"};
    public static final String[] ELEMENT = {"Lửa", "Đất", "Khí", "Nước", "Lửa", "Đất", "Khí", "Nước", "Lửa", "Đất", "Khí", "Nước"};
    public static final String[] PLANET = {"Sao Hỏa", "Sao Kim", "Sao Thủy", "Mặt Trăng", "Mặt Trời", "Sao Thủy",
        "Sao Kim", "Sao Diêm Vương", "Sao Mộc", "Sao Thổ", "Sao Thiên Vương", "Sao Hải Vương"};
    public static final String[] SYMBOL = {"♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓"};
    /** Cung cùng nguyên tố hoặc nguyên tố bổ trợ (Lửa–Khí, Đất–Nước), theo cách ghép nguyên tố trong chiêm tinh phương Tây. */
    public static String compatible(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 12; j++) {
            if (j == i) continue;
            int d = Math.floorMod(j - i, 12);
            if (d == 4 || d == 8 || d == 2 || d == 10) { // cùng nguyên tố hoặc Lửa–Khí, Đất–Nước
                if (sb.length() > 0) sb.append(", ");
                sb.append(NAME[j]);
            }
        }
        return sb.toString();
    }

    private static final int[] START_DAY = {21, 20, 21, 21, 23, 23, 23, 23, 22, 22, 20, 19};
    private static final int[] START_MONTH = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2};

    public static int of(int d, int m) {
        int best = 9; // Ma Kết mặc định (cuối 12 – đầu 1)
        int bestKey = -1;
        int key = m * 100 + d;
        for (int i = 0; i < 12; i++) {
            int k = START_MONTH[i] * 100 + START_DAY[i];
            if (k <= key && k > bestKey) { bestKey = k; best = i; }
        }
        return best;
    }

}
