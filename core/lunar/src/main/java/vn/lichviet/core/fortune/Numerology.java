package vn.lichviet.core.fortune;

import java.text.Normalizer;

/** Thần số học Pythagoras: con số chủ đạo, ngày sinh, thái độ, năm cá nhân và các chỉ số tên. */
public final class Numerology {
    private Numerology() {}

    public static final class Result {
        public final int lifePath, birthDay, attitude, personalYear;
        public final int soul, personality, expression;   // 0 nếu không có tên
        Result(int lp, int bd, int at, int py, int so, int pe, int ex) {
            lifePath = lp; birthDay = bd; attitude = at; personalYear = py; soul = so; personality = pe; expression = ex;
        }
    }

    static int digitSum(int n) {
        int s = 0;
        n = Math.abs(n);
        while (n > 0) { s += n % 10; n /= 10; }
        return s;
    }

    /** Rút gọn về 1..9, giữ số Master 11, 22, 33 (dùng cho chỉ số tên). */
    public static int reduce(int n) {
        while (n > 9 && n != 11 && n != 22 && n != 33) n = digitSum(n);
        return n;
    }

    /**
     * Số chủ đạo theo cách tính phổ biến ở Việt Nam: cộng tất cả chữ số ngày tháng năm sinh,
     * rút gọn đến khi được 2–11; tổng bằng 22 giữ nguyên là 22/4 (vietnamworks.com, dienmayxanh.com, hellobacsi.com).
     */
    public static int lifePath(int d, int m, int y) {
        int s = digitSum(d) + digitSum(m) + digitSum(y);
        while (s > 11 && s != 22) s = digitSum(s);
        return s;
    }

    /** Năm cá nhân: cộng chữ số ngày, tháng sinh và năm cần xem, rút gọn về 1–9 (mytour.vn). */
    public static int personalYear(int d, int m, int currentYear) {
        int s = digitSum(d) + digitSum(m) + digitSum(currentYear);
        while (s > 9) s = digitSum(s);
        return s;
    }

    /** Bỏ dấu tiếng Việt, chuyển chữ hoa. */
    public static String plain(String s) {
        String n = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.replace('đ', 'd').replace('Đ', 'D').toUpperCase();
    }

    static int letter(char c) { return (c - 'A') % 9 + 1; }

    static boolean vowel(char c) { return "AEIOUY".indexOf(c) >= 0; }

    public static int nameNumber(String name, int mode) { // 0 tất cả, 1 nguyên âm, 2 phụ âm
        int s = 0;
        for (char c : plain(name).toCharArray()) {
            if (c < 'A' || c > 'Z') continue;
            boolean v = vowel(c);
            if (mode == 0 || (mode == 1 && v) || (mode == 2 && !v)) s += letter(c);
        }
        return s == 0 ? 0 : reduce(s);
    }

    public static Result of(String name, int d, int m, int y, int currentYear) {
        return new Result(lifePath(d, m, y), reduce(d), reduce(d + m), personalYear(d, m, currentYear),
            nameNumber(name, 1), nameNumber(name, 2), nameNumber(name, 0));
    }

    public static String label(int n) { return n == 22 ? "22/4" : String.valueOf(n); }

    /** Ý nghĩa số chủ đạo, tóm lược theo momo.vn (2–9) và vietnamworks.com (10, 11, 22/4). */
    public static String meaning(int n) {
        switch (n) {
            case 2: return "Người hòa giải: trực giác, nhạy cảm, khéo ngoại giao, giỏi cân bằng. Cần rèn sự tự tin và biết đặt ranh giới.";
            case 3: return "Người sáng tạo: giỏi truyền đạt, hài hước, lạc quan, có năng khiếu nghệ thuật. Cần học tập trung và kỷ luật.";
            case 4: return "Người xây dựng: thực tế, logic, có tổ chức, đáng tin cậy. Cần linh hoạt, cởi mở hơn với ý tưởng mới.";
            case 5: return "Người tự do: thích phiêu lưu, thông minh, đa tài. Cần học tính kiên định, cam kết.";
            case 6: return "Người nuôi dưỡng: có trách nhiệm, yêu thương, coi trọng gia đình. Dễ gánh quá nhiều trách nhiệm, cần biết cân bằng.";
            case 7: return "Người nghiên cứu: trí tuệ, giỏi phân tích, quan tâm điều bí ẩn và tâm linh. Cần học chia sẻ và tin tưởng người khác.";
            case 8: return "Người quyền lực: có tiềm năng về tài chính, kinh doanh, thành công vật chất. Cần dùng quyền lực một cách nhân đạo.";
            case 9: return "Người phục vụ: nhân ái, có tầm nhìn lớn, giàu lòng từ bi. Cần học cách buông bỏ.";
            case 10: return "Số 10 cho thấy người dễ thích nghi.";
            case 11: return "Người nhạy cảm, chân thật, hướng đến cái đẹp và sự tinh tế, luôn dành tình cảm cho gia đình, bạn bè.";
            case 22: return "Được gọi là \"con số vua\": thường đạt được mục đích dù khó khăn, có tiềm năng rất lớn.";
            default: return "";
        }
    }
}
