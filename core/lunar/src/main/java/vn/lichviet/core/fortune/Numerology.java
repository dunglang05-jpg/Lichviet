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

    /** Rút gọn về 1..9, giữ số Master 11, 22, 33. */
    public static int reduce(int n) {
        while (n > 9 && n != 11 && n != 22 && n != 33) n = digitSum(n);
        return n;
    }

    public static int lifePath(int d, int m, int y) {
        return reduce(reduce(d) + reduce(m) + reduce(digitSum(y)));
    }

    public static int personalYear(int d, int m, int currentYear) {
        return reduce(reduce(d) + reduce(m) + reduce(digitSum(currentYear)));
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

    public static String title(int n) {
        switch (n) {
            case 1: return "Số 1 – Người tiên phong";
            case 2: return "Số 2 – Người hòa giải";
            case 3: return "Số 3 – Người truyền cảm hứng";
            case 4: return "Số 4 – Người xây nền móng";
            case 5: return "Số 5 – Người tự do";
            case 6: return "Số 6 – Người chăm sóc";
            case 7: return "Số 7 – Người tìm chân lý";
            case 8: return "Số 8 – Người làm chủ";
            case 9: return "Số 9 – Người nhân ái";
            case 11: return "Số 11 – Người khai sáng";
            case 22: return "Số 22 – Kiến trúc sư bậc thầy";
            case 33: return "Số 33 – Người thầy chữa lành";
            default: return "—";
        }
    }

    public static String meaning(int n) {
        switch (n) {
            case 1: return "Độc lập, quyết đoán, thích dẫn đầu và tự mở đường. Cần học cách lắng nghe và hợp tác để không trở nên cô độc hay nóng vội.";
            case 2: return "Tinh tế, giàu cảm xúc, giỏi kết nối và làm việc nhóm. Điểm cần rèn là sự tự tin và dám nói lên mong muốn của mình.";
            case 3: return "Vui vẻ, sáng tạo, giỏi diễn đạt bằng lời nói và nghệ thuật. Nên tập trung vào ít mục tiêu để không phân tán năng lượng.";
            case 4: return "Chăm chỉ, kỷ luật, thực tế và đáng tin cậy. Thành công đến từ sự bền bỉ; nên linh hoạt hơn khi hoàn cảnh thay đổi.";
            case 5: return "Ham khám phá, thích đổi mới, thích nghi nhanh. Cần cân bằng giữa tự do và trách nhiệm, tránh bốc đồng trong chi tiêu.";
            case 6: return "Giàu tình thương, có trách nhiệm với gia đình và cộng đồng. Đừng ôm đồm việc của người khác mà quên chăm sóc bản thân.";
            case 7: return "Sâu sắc, thích nghiên cứu, trực giác tốt. Cần mở lòng chia sẻ để không bị khép kín và hoài nghi quá mức.";
            case 8: return "Có tố chất quản lý, kinh doanh, nhạy bén với tiền bạc và quyền lực. Nên giữ chữ tín và cân bằng giữa công việc với gia đình.";
            case 9: return "Bao dung, lý tưởng, muốn giúp đỡ mọi người. Hợp các công việc phục vụ cộng đồng; cần học cách buông bỏ chuyện cũ.";
            case 11: return "Trực giác mạnh, giàu cảm hứng, có khả năng truyền động lực cho người khác. Dễ căng thẳng nên cần giữ tinh thần ổn định.";
            case 22: return "Có tầm nhìn lớn và khả năng biến ý tưởng thành hiện thực. Áp lực cao; thành công khi kiên nhẫn từng bước.";
            case 33: return "Tình yêu thương và tinh thần phụng sự lớn, hợp vai trò người thầy, người chữa lành. Cần giữ ranh giới để không kiệt sức.";
            default: return "";
        }
    }

    public static String yearMeaning(int n) {
        switch (n) {
            case 1: return "Năm khởi đầu: gieo hạt cho dự định mới, chủ động nắm cơ hội.";
            case 2: return "Năm kiên nhẫn: vun đắp quan hệ, hợp tác, chờ kết quả chín muồi.";
            case 3: return "Năm sáng tạo: giao tiếp, học hỏi, thể hiện bản thân.";
            case 4: return "Năm xây dựng: làm việc chăm chỉ, sắp xếp tài chính và sức khỏe.";
            case 5: return "Năm thay đổi: đi lại, đổi mới, đón nhận điều bất ngờ.";
            case 6: return "Năm gia đình: trách nhiệm, chăm sóc người thân, chuyện nhà cửa.";
            case 7: return "Năm chiêm nghiệm: học sâu, nghỉ ngơi, nhìn lại bản thân.";
            case 8: return "Năm gặt hái: sự nghiệp, tài chính, khẳng định vị trí.";
            case 9: return "Năm khép lại: hoàn tất việc cũ, buông bỏ để chuẩn bị chu kỳ mới.";
            default: return yearMeaning(reduce(digitSum(n)));
        }
    }
}
