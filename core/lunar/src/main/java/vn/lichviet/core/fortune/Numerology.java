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

    private static int r9(int n) { while (n > 9) n = digitSum(n); return n; }

    static int digitSum(int n) {
        int s = 0;
        n = Math.abs(n);
        while (n > 0) { s += n % 10; n /= 10; }
        return s;
    }

    /** Rút gọn về 1..9, giữ số Master 11, 22 (dùng cho chỉ số tên; tracuuthansohoc.com). */
    public static int reduce(int n) {
        while (n > 9 && n != 11 && n != 22) n = digitSum(n);
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

    /** Năng lực tự nhiên: cộng chữ số ngày sinh, rút gọn về 1–9 (tracuuthansohoc.com). */
    public static int naturalAbility(int d) { return r9(d); }

    /** Thái độ: tổng chữ số ngày + tổng chữ số tháng, rút gọn về 1–9 (tracuuthansohoc.com). */
    public static int attitude(int d, int m) { return r9(digitSum(d) + digitSum(m)); }

    /** Trưởng thành = số chủ đạo + số sứ mệnh, rút gọn về 1–9, giữ 11, 22 (tracuuthansohoc.com). */
    public static int maturity(int lifePath, int expression) { return expression == 0 ? 0 : reduce(lifePath + expression); }

    /** Bỏ dấu tiếng Việt, chuyển chữ hoa. */
    public static String plain(String s) {
        String n = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.replace('đ', 'd').replace('Đ', 'D').toUpperCase();
    }

    static int letter(char c) { return (c - 'A') % 9 + 1; }

    /** Nguyên âm A, E, I, O, U (fptshop.com.vn, viettelstore.vn). */
    static boolean vowel(char c) { return "AEIOU".indexOf(c) >= 0; }

    /**
     * Y là nguyên âm, trừ khi đứng giữa hai nguyên âm thì là phụ âm, vd NGUYEN (tracuuthansohoc.com).
     */
    static boolean vowelAt(String w, int i) {
        char c = w.charAt(i);
        if (c != 'Y') return vowel(c);
        boolean before = i > 0 && vowel(w.charAt(i - 1));
        boolean after = i + 1 < w.length() && vowel(w.charAt(i + 1));
        return !(before && after);
    }

    /** Tổng chưa rút gọn của chữ cái trong tên. mode: 0 tất cả, 1 nguyên âm, 2 phụ âm. */
    public static int nameSum(String name, int mode) {
        int s = 0;
        String w = plain(name);
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            if (c < 'A' || c > 'Z') continue;
            boolean v = vowelAt(w, i);
            if (mode == 0 || (mode == 1 && v) || (mode == 2 && !v)) s += letter(c);
        }
        return s;
    }

    public static int nameNumber(String name, int mode) {
        int s = nameSum(name, mode);
        return s == 0 ? 0 : reduce(s);
    }

    /** Các chữ số 1–9 của từng chữ cái trong tên (để đưa vào biểu đồ). */
    public static int[] nameDigits(String name) {
        String w = plain(name);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < w.length(); i++) { char c = w.charAt(i); if (c >= 'A' && c <= 'Z') b.append((char) ('0' + letter(c))); }
        int[] r = new int[b.length()];
        for (int i = 0; i < r.length; i++) r[i] = b.charAt(i) - '0';
        return r;
    }

    public static Result of(String name, int d, int m, int y, int currentYear) {
        return new Result(lifePath(d, m, y), naturalAbility(d), attitude(d, m), personalYear(d, m, currentYear),
            nameNumber(name, 1), nameNumber(name, 2), nameNumber(name, 0));
    }


    /** Một đỉnh cao trong kim tự tháp thần số học. */
    public static final class Pinnacle {
        public final int number, fromAge;
        Pinnacle(int n, int a) { number = n; fromAge = a; }
    }

    /**
     * 4 đỉnh cao (jobsgo.vn, tracuuthansohoc.com): đáy là tháng, ngày, năm sinh rút gọn; đỉnh 1 = tháng + ngày,
     * đỉnh 2 = ngày + năm (rút gọn 1 chữ số); đỉnh 3 = đỉnh 1 + đỉnh 2, đỉnh 4 = tháng + năm (giữ 2–11).
     * Tuổi đỉnh 1 = 36 − số chủ đạo (22/4 tính là 4), mỗi đỉnh sau +9 năm.
     */
    public static Pinnacle[] pinnacles(int d, int m, int y) {
        int mm = r9(m), dd = r9(d), yy = r9(digitSum(y));
        int p1 = r9(mm + dd), p2 = r9(dd + yy), p3 = r11(p1 + p2), p4 = r11(mm + yy);
        int lp = lifePath(d, m, y);
        int a1 = 36 - (lp == 22 ? 4 : lp);
        return new Pinnacle[]{new Pinnacle(p1, a1), new Pinnacle(p2, a1 + 9), new Pinnacle(p3, a1 + 18), new Pinnacle(p4, a1 + 27)};
    }

    private static int r11(int n) { while (n > 11) n = digitSum(n); return n; }

    // ------------------------------------------------------------ nợ nghiệp

    /** Có số nợ nghiệp 13, 14, 16, 19 xuất hiện trong chuỗi rút gọn của tổng [sum] không. */
    public static int karmic(int sum) {
        int n = sum;
        while (true) {
            if (n == 13 || n == 14 || n == 16 || n == 19) return n;
            if (n < 10) return 0;
            n = digitSum(n);
        }
    }

    /** Ý nghĩa nợ nghiệp (tracuuthansohoc.com). */
    public static String karmicMeaning(int k) {
        switch (k) {
            case 13: return "13/4 – liên quan đến sự thiếu kỷ luật, né tránh trách nhiệm. Dễ gặp trở ngại, bực bội; bài học là lên kế hoạch, làm việc đều đặn và kiên trì.";
            case 14: return "14/5 – liên quan đến việc dùng tự do sai cách, buông thả. Dễ thấy gò bó, bất an; bài học là tự chủ, có trách nhiệm và nguyên tắc rõ ràng.";
            case 16: return "16/7 – liên quan đến cái tôi quá lớn, kiêu ngạo. Dễ gặp biến cố bất ngờ, thấy cô lập; bài học là khiêm tốn, chân thành và buông bỏ quá khứ.";
            case 19: return "19/1 – liên quan đến việc lạm dụng quyền lực, đặt lợi ích bản thân lên trước. Dễ cô đơn, phải tự gánh vác; bài học là hợp tác, lắng nghe và biết nhờ giúp đỡ.";
            default: return "";
        }
    }

    /** Tổng chữ số ngày tháng năm sinh chưa rút gọn. */
    public static int birthSum(int d, int m, int y) { return digitSum(d) + digitSum(m) + digitSum(y); }

    // ------------------------------------------------------------ biểu đồ ngày sinh

    /** Số lần xuất hiện của các chữ số 1–9 (chỉ số 0 bỏ qua) trong ngày sinh, cộng thêm các chữ số tên nếu có. */
    public static int[] chart(int d, int m, int y, int[] extra) {
        int[] c = new int[10];
        for (char ch : (String.valueOf(d) + m + y).toCharArray()) if (ch > '0') c[ch - '0']++;
        if (extra != null) for (int x : extra) if (x > 0 && x <= 9) c[x]++;
        return c;
    }

    /** Mũi tên: 3 số trên một hàng/cột/chéo. {số, tên khi đủ, ý nghĩa khi đủ, tên khi trống, ý nghĩa khi trống} (tracuuthansohoc.com). */
    public static final String[][] ARROWS = {
        {"123", "Mũi tên kế hoạch", "Có tổ chức, logic, cân nhắc kỹ trước khi làm.", "Mũi tên thiếu kế hoạch", "Hay làm theo cảm tính, dễ rối khi phải lo nhiều việc một lúc."},
        {"456", "Mũi tên ý chí", "Bền bỉ, đưa ra quyết định đúng khi bị áp lực.", "Mũi tên uất hận", "Dễ thất vọng khi kết quả không như ý, thiếu bền chí hoặc hay dựa vào người khác."},
        {"789", "Mũi tên hành động", "Nhiều năng lượng, linh hoạt, sẵn sàng nhận thử thách.", "Mũi tên trì hoãn", "Thiếu động lực, dễ thụ động hoặc bỏ dở mục tiêu."},
        {"147", "Mũi tên thực tế", "Thực tế, khéo tay, học tốt nhất qua trải nghiệm.", "Mũi tên thiếu thực tế", "Dễ nóng vội, hay đổi hướng, khó theo những việc thực tế kéo dài."},
        {"258", "Mũi tên cân bằng cảm xúc", "Trực giác tốt, cảm xúc cân bằng, khéo léo trong các mối quan hệ.", "Mũi tên nhạy cảm", "Cảm xúc dễ lấn át, khó cân bằng giữa cảm xúc, lý trí và hành động."},
        {"369", "Mũi tên trí tuệ", "Học nhanh, trí nhớ và khả năng phân tích tốt.", "Mũi tên trí nhớ ngắn hạn", "Khả năng tập trung và ghi nhớ yếu hơn, cần nhiều thời gian để tiếp thu."},
        {"357", "Mũi tên trực giác", "Trực giác mạnh, dễ hiểu được người khác.", "Mũi tên hoài nghi", "Dễ nghi ngờ, phân tích quá mức và hay phòng thủ."},
        {"159", "Mũi tên quyết tâm", "Quyết tâm, làm đến nơi đến chốn, có tố chất lãnh đạo.", "Mũi tên do dự", "Dễ bỏ dở giữa chừng, dao động khi không có hướng đi rõ ràng."},
    };

    /** 1 = đủ cả ba số, -1 = trống cả ba, 0 = không thành mũi tên. */
    public static int arrowState(int[] chart, String digits) {
        int have = 0;
        for (char ch : digits.toCharArray()) if (chart[ch - '0'] > 0) have++;
        return have == 3 ? 1 : have == 0 ? -1 : 0;
    }

    // ------------------------------------------------------------ ý nghĩa các chỉ số phụ (tracuuthansohoc.com)

    public static String attitudeMeaning(int n) {
        switch (n) {
            case 1: return "Quyết đoán, tự tin, độc lập, có khả năng lãnh đạo; nên khiêm tốn để không bị xem là kiêu ngạo.";
            case 2: return "Tốt bụng, hợp tác, khéo léo, để ý cảm xúc người khác; trực giác tốt nhưng đôi khi quan tâm quá mức.";
            case 3: return "Vui vẻ, sáng tạo, hài hước, truyền cảm hứng; dễ phân tán và bốc đồng.";
            case 4: return "Điềm tĩnh, thực tế, đáng tin, làm có kế hoạch và giữ lời hứa; thích sự an toàn.";
            case 5: return "Ham tự do, thích thay đổi và phiêu lưu, giao tiếp tốt, không ngại thử thách; thường muốn được chú ý.";
            case 6: return "Ấm áp, chín chắn, có trách nhiệm, chăm lo người thân; hay muốn kiểm soát mọi việc.";
            case 7: return "Hay phân tích, quan sát kỹ, hướng nội, thích yên tĩnh; có thể sợ sai hoặc sợ bị chỉ trích.";
            case 8: return "Mạnh mẽ, có tổ chức, quyết đoán, tham vọng và kiên trì; có thể cố chấp, thích kiểm soát.";
            case 9: return "Ấm áp, bao dung, nhạy cảm, có năng khiếu nghệ thuật, thích giúp đỡ; cần sống ở hiện tại và buông bỏ quá khứ.";
            default: return "";
        }
    }

    public static String naturalMeaning(int n) {
        switch (n) {
            case 1: return "Chủ động, có tố chất lãnh đạo và tinh thần tiên phong.";
            case 2: return "Hợp tác, thấu cảm, tạo sự hài hòa trong các mối quan hệ.";
            case 3: return "Giao tiếp, sáng tạo và truyền cảm hứng cho người khác.";
            case 4: return "Tổ chức, kỷ luật, xây dựng nền tảng vững chắc.";
            case 5: return "Thích nghi, linh hoạt, mở rộng trải nghiệm.";
            case 6: return "Chăm sóc, nâng đỡ người khác và tạo sự ổn định.";
            case 7: return "Phân tích, nghiên cứu, suy nghĩ chiều sâu.";
            case 8: return "Quản lý, tổ chức nguồn lực và tạo ra kết quả thực tế.";
            case 9: return "Nhân ái, bao dung, truyền cảm hứng bằng những giá trị ý nghĩa.";
            default: return "";
        }
    }

    public static String soulMeaning(int n) {
        switch (n) {
            case 1: return "Khao khát làm chủ cuộc đời, tự quyết định và được công nhận là người độc lập.";
            case 2: return "Khao khát tình cảm, sự gắn kết, được thấu hiểu và thuộc về một nơi ấm áp.";
            case 3: return "Cần được thể hiện bản thân, tận hưởng niềm vui, sáng tạo và lan tỏa năng lượng tích cực.";
            case 4: return "Mong muốn ổn định, trật tự và một nền tảng vững chắc từ kế hoạch rõ ràng.";
            case 5: return "Khát vọng tự do, trải nghiệm điều mới, không bị bó buộc.";
            case 6: return "Cần yêu thương, chăm sóc, xây dựng mái ấm và gánh vác trách nhiệm với người thân.";
            case 7: return "Khao khát chiều sâu, sự thật và tri thức, muốn hiểu ý nghĩa đằng sau mọi việc.";
            case 8: return "Mong muốn thành tựu, quyền tự chủ và tạo ra kết quả lớn có giá trị thực tế.";
            case 9: return "Khát khao sống có ý nghĩa, nhân ái và đóng góp vượt ra ngoài lợi ích cá nhân.";
            case 11: return "Số Master: mong sống có ý nghĩa, truyền cảm hứng và nâng đỡ người khác bằng trực giác.";
            case 22: return "Số Master: mong xây dựng những điều lớn lao, hữu ích và lâu dài cho cộng đồng.";
            default: return "";
        }
    }

    public static String personalityMeaning(int n) {
        switch (n) {
            case 1: return "Người khác thấy bạn độc lập, tự chủ, có tham vọng và thích dẫn dắt.";
            case 2: return "Người khác thấy bạn thích làm việc nhóm, hòa hợp và coi trọng các mối quan hệ lâu dài.";
            case 3: return "Toát lên vẻ vui vẻ, lạc quan, hài hước nên dễ được cảm tình.";
            case 4: return "Người khác thấy bạn thực tế, cẩn thận, kỷ luật, làm việc chỉn chu.";
            case 5: return "Người khác thấy bạn yêu tự do, năng động, ham trải nghiệm và sáng tạo.";
            case 6: return "Người khác thấy bạn quan tâm, chăm sóc, sẵn sàng làm chỗ dựa.";
            case 7: return "Người khác thấy bạn thích tìm hiểu, phân tích, có chính kiến, khó bị áp đặt.";
            case 8: return "Người khác thấy bạn tự tin, độc lập, tư duy chiến lược, có năng lực kinh doanh, tài chính.";
            case 9: return "Người khác thấy bạn có cái nhìn bao quát, sẵn lòng cống hiến và giúp đỡ.";
            case 11: return "Hòa hợp, dễ mến, hiểu biết sâu sắc về cuộc sống nên được nhiều người quý.";
            case 22: return "Tinh tế, tổ chức tốt, theo đuổi lý tưởng lớn; kỳ vọng cao đôi khi khiến khó gần.";
            default: return "";
        }
    }

    public static String maturityMeaning(int n) {
        switch (n) {
            case 1: return "Hướng tới sự độc lập, lãnh đạo, tự chịu trách nhiệm với quyết định của mình.";
            case 2: return "Hướng tới sự hợp tác, nhạy cảm, hòa giải và xây dựng các mối quan hệ.";
            case 3: return "Hướng tới việc thể hiện bản thân, sáng tạo, giao tiếp và lạc quan.";
            case 4: return "Hướng tới kỷ luật, trật tự, sự đáng tin và nền tảng vững chắc.";
            case 5: return "Hướng tới tự do, khám phá, thích nghi và đón nhận thay đổi.";
            case 6: return "Hướng tới sự chăm sóc, trách nhiệm, gia đình và nâng đỡ người khác.";
            case 7: return "Hướng tới nội tâm, trí tuệ, nghiên cứu và chiều sâu tinh thần.";
            case 8: return "Hướng tới thành tựu, quản lý và thành công vật chất có đạo đức.";
            case 9: return "Hướng tới lòng trắc ẩn, phụng sự và đóng góp cho cộng đồng.";
            case 11: return "Hướng tới trực giác, truyền cảm hứng, giảng dạy và dẫn dắt tinh thần.";
            case 22: return "Hướng tới việc biến ý tưởng lớn thành thành quả lâu dài, quy mô lớn.";
            default: return "";
        }
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
