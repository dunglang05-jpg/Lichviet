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

    /** Nguyên âm A, E, I, O, U (fptshop.com.vn, viettelstore.vn). */
    static boolean vowel(char c) { return "AEIOU".indexOf(c) >= 0; }

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


    /** Một đỉnh cao trong kim tự tháp thần số học. */
    public static final class Pinnacle {
        public final int number, fromAge;
        Pinnacle(int n, int a) { number = n; fromAge = a; }
    }

    private static int r9(int n) { while (n > 9) n = digitSum(n); return n; }

    /**
     * 4 đỉnh cao (jobsgo.vn): đáy là tháng, ngày, năm sinh rút gọn; đỉnh 1 = tháng + ngày, đỉnh 2 = ngày + năm,
     * đỉnh 3 = đỉnh 1 + đỉnh 2, đỉnh 4 = tháng + năm. Tuổi đỉnh 1 = 36 − số chủ đạo, mỗi đỉnh sau +9 năm.
     */
    public static Pinnacle[] pinnacles(int d, int m, int y) {
        int mm = r9(m), dd = r9(d), yy = r9(digitSum(y));
        int p1 = r9(mm + dd), p2 = r9(dd + yy), p3 = r9(p1 + p2), p4 = r9(mm + yy);
        int lp = lifePath(d, m, y);
        int lpr = lp == 22 ? 4 : r9(lp);
        int a1 = 36 - lpr;
        return new Pinnacle[]{new Pinnacle(p1, a1), new Pinnacle(p2, a1 + 9), new Pinnacle(p3, a1 + 18), new Pinnacle(p4, a1 + 27)};
    }

    /** Từ khóa của các con số 1–9 (hellobacsi.com). */
    public static String keyword(int n) {
        switch (n == 11 ? 2 : n == 22 ? 4 : n == 33 ? 6 : n) {
            case 1: return "Độc lập, lãnh đạo, sáng tạo";
            case 2: return "Hòa giải, nhạy cảm, hợp tác";
            case 3: return "Giao tiếp, nghệ thuật, vui vẻ";
            case 4: return "Kỷ luật, thực tế, bền vững";
            case 5: return "Tự do, phiêu lưu, linh hoạt";
            case 6: return "Trách nhiệm, gia đình, yêu thương";
            case 7: return "Trí tuệ, tâm linh, phân tích";
            case 8: return "Tham vọng, quyền lực, thành công";
            case 9: return "Nhân đạo, bao dung, lý tưởng";
            default: return "";
        }
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
    /**
     * Chi tiết số chủ đạo: {tính cách và điểm mạnh, điểm cần lưu ý, nghề nghiệp phù hợp, lời khuyên}.
     * Tổng hợp, diễn đạt lại từ dienmayxanh.com, careerviet.vn và momo.vn.
     */
    public static String[] profile(int n) {
        switch (n) {
            case 2: return new String[]{
                "Người hòa giải giàu trực giác. Bạn nhạy cảm, khiêm tốn, biết lắng nghe và có lòng trắc ẩn, thường là người kết nối, giữ hòa khí cho tập thể.",
                "Dễ thiếu quyết đoán và dễ tổn thương trước lời nói của người khác.",
                "Tư vấn, giảng dạy, ngoại giao, đàm phán, công tác xã hội, trị liệu tâm lý; cũng hợp viết lách, thiết kế thời trang, diễn xuất, ca hát. Làm tốt khi có người dẫn dắt năng động.",
                "Rèn sự tự tin, dám nói ra mong muốn của mình và biết đặt ranh giới."};
            case 3: return new String[]{
                "Người truyền cảm hứng sáng tạo: tư duy nhanh, linh hoạt, hài hước, lạc quan và giao tiếp lôi cuốn, có năng khiếu nghệ thuật.",
                "Dễ phân tán, thiếu kiên nhẫn, đôi khi hời hợt; nhanh chán với việc tỉ mỉ, lặp đi lặp lại.",
                "Diễn xuất, làm phim, hội họa, nhiếp ảnh, thiết kế, viết lách, báo chí, MC, marketing; ngoài ra có luật, giảng dạy, tư vấn, tâm lý học.",
                "Học cách tập trung và giữ kỷ luật để biến ý tưởng thành kết quả."};
            case 4: return new String[]{
                "Người xây dựng thực tế: ổn định, chăm chỉ, có nguyên tắc, giỏi lập kế hoạch và tổ chức, giải quyết vấn đề một cách cụ thể, logic; là người đáng tin cậy.",
                "Có thể cứng nhắc, bảo thủ, quá sa vào chi tiết mà bỏ qua bức tranh toàn cảnh.",
                "Kỹ sư, kiến trúc sư, kế toán, lập trình viên, quản lý dự án, lập kế hoạch, quản lý tài chính hoặc doanh nghiệp, luật sư, giáo viên, nghề mộc.",
                "Linh hoạt hơn và cởi mở với ý tưởng mới."};
            case 5: return new String[]{
                "Người yêu tự do: thích phiêu lưu và trải nghiệm, đa tài, sáng tạo, thích nghi nhanh và giao tiếp tốt.",
                "Thiếu cam kết, dễ chán, khó gắn bó lâu một chỗ; dễ đổi việc khi thấy bị gò bó.",
                "Du lịch, hướng dẫn viên, tổ chức sự kiện, báo chí, bán hàng, kinh doanh, tiếp thị, diễn xuất, nghệ thuật.",
                "Rèn tính kiên định và học cách giữ cam kết."};
            case 6: return new String[]{
                "Người của gia đình: giàu yêu thương, có trách nhiệm, ấm áp, sáng tạo, yêu cái đẹp và đề cao sự công bằng.",
                "Dễ hy sinh quá mức, gánh quá nhiều việc; đôi khi bao bọc và muốn kiểm soát người thân.",
                "Giáo viên mầm non, bác sĩ, y tá, chăm sóc người cao tuổi, trị liệu tâm lý, thiết kế nội thất hoặc thời trang, kiến trúc, đầu bếp, nghệ thuật.",
                "Biết cân bằng giữa chăm lo cho người khác và cho chính mình."};
            case 7: return new String[]{
                "Người tri thức: ham học hỏi, giỏi phân tích, quan sát tinh tường, hay chiêm nghiệm; kỷ luật, kiên nhẫn và sẵn sàng nghiên cứu sâu, quan tâm đến điều bí ẩn và tâm linh.",
                "Sống nội tâm, khó gần, đôi khi hoài nghi.",
                "Nhà khoa học, nhà nghiên cứu, phân tích dữ liệu, lập trình viên, giảng viên, phóng viên, diễn giả, trị liệu tâm lý, điêu khắc, thủ công mỹ nghệ.",
                "Học cách chia sẻ và tin tưởng người khác."};
            case 8: return new String[]{
                "Người điều hành: tự tin, độc lập, có tham vọng, tư duy chiến lược và năng lực lãnh đạo; có tiềm năng về tài chính, kinh doanh và thành công vật chất.",
                "Khó cân bằng công việc với cuộc sống, có thể độc đoán, quá chú trọng tiền bạc và khó bộc lộ cảm xúc.",
                "Chủ doanh nghiệp, quản lý, tài chính, pháp luật, chính trị; ngoài ra có diễn xuất, nghệ thuật.",
                "Dùng quyền lực một cách nhân đạo và cân bằng công việc với cuộc sống."};
            case 9: return new String[]{
                "Người cho đi: nhân ái, vị tha, có trách nhiệm với xã hội, hoài bão và tầm nhìn lớn.",
                "Dễ lý tưởng hóa, đôi khi lúng túng với chính vấn đề của bản thân; không hợp việc tính toán, quản lý tài chính.",
                "Giáo dục, công tác xã hội, tình nguyện, y tế nhân đạo, tâm lý học, quản lý hành chính, nghệ thuật, thiết kế, nhiếp ảnh.",
                "Học cách buông bỏ."};
            case 10: return new String[]{
                "Người tiên phong dễ thích nghi: tự tin, quyết đoán, lạc quan, dám đối mặt thử thách, có tố chất lãnh đạo, sáng tạo và truyền cảm hứng.",
                "Có thể tự cao và thiếu kiên nhẫn với người chậm hơn mình.",
                "Quản lý, trưởng nhóm, khởi nghiệp, kinh doanh, bán hàng, thể thao, giải trí, đạo diễn, giảng dạy, thiết kế, kiến trúc, kỹ thuật.",
                "Rèn sự kiên nhẫn với người xung quanh."};
            case 11: return new String[]{
                "Con số Master, phiên bản nâng cao của số 2: trực giác rất nhạy, thấu cảm sâu, chân thật, yêu cái đẹp, luôn dành tình cảm cho gia đình, bạn bè; có khả năng truyền cảm hứng, dẫn dắt và phán đoán tốt.",
                "Nhạy cảm quá mức nên dễ căng thẳng thần kinh.",
                "Tư vấn tâm lý, nghệ sĩ, nhạc sĩ, nhà văn, họa sĩ, diễn giả, ngoại giao, quan hệ quốc tế, PR, phiên dịch, chiến lược, kinh doanh.",
                "Chú ý giữ cân bằng cảm xúc để tránh căng thẳng."};
            case 22: return new String[]{
                "Con số Master, được gọi là \"con số vua\": tầm nhìn lớn đi cùng sự kỷ luật, thực tế của số 4; thông minh, sáng tạo, giỏi giải quyết vấn đề, có khả năng biến ước mơ lớn thành hiện thực và thường đạt mục đích dù khó khăn.",
                "Đôi khi thiếu kiên nhẫn và khó hợp tác với người khác.",
                "Kiến trúc, xây dựng, kỹ thuật, thiết kế, quản trị, lãnh đạo doanh nghiệp, chiến lược, chính trị, giảng dạy.",
                "Học cách kiên nhẫn và hợp tác với người khác."};
            default: return new String[]{"", "", "", ""};
        }
    }
}
