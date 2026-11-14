package vn.lichviet.core.fortune;

/** Cung phi Bát trạch theo năm sinh âm lịch và giới tính; hướng tốt/xấu. */
public final class BatTrach {
    private BatTrach() {}

    public static final String[] CUNG = {"", "Khảm", "Khôn", "Chấn", "Tốn", "", "Càn", "Đoài", "Cấn", "Ly"};
    public static final String[] HANH_CUNG = {"", "Thủy", "Thổ", "Mộc", "Mộc", "", "Kim", "Kim", "Thổ", "Hỏa"};
    /** Phương vị của từng cung (chỉ số 1..9). */
    public static final String[] PHUONG = {"", "Bắc", "Tây Nam", "Đông", "Đông Nam", "", "Tây Bắc", "Tây", "Đông Bắc", "Nam"};
    public static final String[] HUONG_TEN = {"Sinh Khí", "Thiên Y", "Diên Niên", "Phục Vị", "Tuyệt Mệnh", "Ngũ Quỷ", "Lục Sát", "Họa Hại"};
    /** Mức cát/hung theo wiki.batdongsan.com.vn. */
    public static final String[] HUONG_Y = {
        "Thượng cát", "Trung cát", "Thượng cát", "Tiểu cát",
        "Đại hung", "Đại hung", "Thứ hung", "Tiểu hung",
    };
    /** Hướng theo thứ tự HUONG_TEN cho từng cung (chỉ số 1..9). */
    private static final String[][] HUONG = {
        null,
        {"Đông Nam", "Đông", "Nam", "Bắc", "Tây Nam", "Đông Bắc", "Tây Bắc", "Tây"},          // Khảm
        {"Đông Bắc", "Tây", "Tây Bắc", "Tây Nam", "Bắc", "Đông Nam", "Nam", "Đông"},          // Khôn
        {"Nam", "Bắc", "Đông Nam", "Đông", "Tây", "Tây Bắc", "Đông Bắc", "Tây Nam"},          // Chấn
        {"Bắc", "Nam", "Đông", "Đông Nam", "Đông Bắc", "Tây Nam", "Tây", "Tây Bắc"},          // Tốn
        null,
        {"Tây", "Đông Bắc", "Tây Nam", "Tây Bắc", "Nam", "Đông", "Bắc", "Đông Nam"},          // Càn
        {"Tây Bắc", "Tây Nam", "Đông Bắc", "Tây", "Đông", "Nam", "Đông Nam", "Bắc"},          // Đoài
        {"Tây Nam", "Tây Bắc", "Tây", "Đông Bắc", "Đông Nam", "Bắc", "Đông", "Nam"},          // Cấn
        {"Đông", "Đông Nam", "Bắc", "Nam", "Tây Bắc", "Tây", "Tây Nam", "Đông Bắc"},          // Ly
    };

    /** Số cung phi 1..9 (không bao giờ là 5). */
    public static int cung(int lunarYear, boolean male) {
        int s = lunarYear;
        while (s > 9) s = Numerology.digitSum(s);
        int k;
        if (male) {
            k = Math.floorMod(11 - s, 9);
            if (k == 0) k = 9;
            if (k == 5) k = 2;
        } else {
            k = Math.floorMod(s + 4, 9);
            if (k == 0) k = 9;
            if (k == 5) k = 8;
        }
        return k;
    }

    public static boolean dongTuMenh(int cung) { return cung == 1 || cung == 3 || cung == 4 || cung == 9; }

    public static String[] huong(int cung) { return HUONG[cung]; }

    /** Độ (0 = Bắc, theo chiều kim đồng hồ) của tên hướng. */
    public static int degrees(String h) {
        switch (h) {
            case "Bắc": return 0;
            case "Đông Bắc": return 45;
            case "Đông": return 90;
            case "Đông Nam": return 135;
            case "Nam": return 180;
            case "Tây Nam": return 225;
            case "Tây": return 270;
            default: return 315;
        }
    }

    /** Tên hướng của một góc la bàn. */
    public static String dirName(float deg) {
        String[] n = {"Bắc", "Đông Bắc", "Đông", "Đông Nam", "Nam", "Tây Nam", "Tây", "Tây Bắc"};
        return n[Math.floorMod(Math.round(deg / 45f), 8)];
    }

    public static final String[] SON_24 = {"Tý", "Quý", "Sửu", "Cấn", "Dần", "Giáp", "Mão", "Ất", "Thìn", "Tốn", "Tỵ", "Bính",
        "Ngọ", "Đinh", "Mùi", "Khôn", "Thân", "Canh", "Dậu", "Tân", "Tuất", "Càn", "Hợi", "Nhâm"};

    /** Sơn (1 trong 24) của một góc; mỗi sơn rộng 15°, Tý ở giữa 352.5°–7.5°. */
    public static String son(float deg) {
        return SON_24[Math.floorMod((int) Math.floor((deg + 7.5f) / 15f), 24)];
    }
}
