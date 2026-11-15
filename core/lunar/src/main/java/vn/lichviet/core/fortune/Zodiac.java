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

    /** Tính chất (modality): Tiên phong / Kiên định / Linh hoạt — hoanghamobile.com. */
    public static final String[] MODALITY = {"Tiên phong", "Kiên định", "Linh hoạt", "Tiên phong", "Kiên định", "Linh hoạt",
        "Tiên phong", "Kiên định", "Linh hoạt", "Tiên phong", "Kiên định", "Linh hoạt"};
    /** Biểu tượng — shopee.vn/blog (ký hiệu cung hoàng đạo), careerlink.vn. */
    public static final String[] EMBLEM = {"Con cừu đực", "Con bò đực", "Cặp song sinh", "Con cua", "Con sư tử", "Cô gái cầm bông lúa",
        "Cán cân", "Con bọ cạp", "Người bắn cung", "Con dê biển", "Người đổ nước", "Hai con cá"};
    public static final String[] ENGLISH = {"Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"};
    /** Tóm lược tính cách, ưu và nhược điểm — theo hoanghamobile.com (tính cách 12 cung hoàng đạo) và thegioididong.com. */
    public static final String[] TRAIT = {
        "Năng lượng khởi đầu mạnh, thích hành động ngay.",
        "Coi trọng sự ổn định, ít quyết định vội.",
        "Giỏi giao tiếp, tiếp thu nhanh.",
        "Đặt cảm xúc và gia đình lên trước.",
        "Tự tin, thích ở vị trí trung tâm.",
        "Chú ý chi tiết, làm việc có quy trình.",
        "Tìm sự cân bằng, ghét xung đột.",
        "Kín đáo, sâu sắc, quan sát nhiều.",
        "Yêu tự do, sống lạc quan.",
        "Bền bỉ, đi từng bước theo mục tiêu dài hạn.",
        "Nhìn vấn đề theo hướng mới, không thích bị gò bó.",
        "Giàu tưởng tượng, dễ đồng cảm."};
    public static final String[] PROS = {
        "Quyết đoán, dũng cảm, thích thử cái mới", "Kiên trì, đáng tin, có gu thẩm mỹ", "Linh hoạt, tư duy nhanh, thích nghi tốt",
        "Chu đáo, trung thành, trực giác tốt", "Có tố chất lãnh đạo, hào phóng", "Tỉ mỉ, có trách nhiệm, giỏi phân tích",
        "Hòa nhã, công bằng, khéo léo", "Quyết tâm cao, trung thành, giữ bí mật tốt", "Phóng khoáng, ham khám phá, hài hước",
        "Kỷ luật, tham vọng, thực tế", "Sáng tạo, độc lập, cởi mở", "Nhân hậu, lãng mạn, biết lắng nghe"};
    public static final String[] CONS = {
        "Nóng vội, dễ bỏ dở giữa chừng", "Bảo thủ, ngại thay đổi", "Hay đổi ý, khó tập trung lâu dài",
        "Nhạy cảm quá mức, dễ tổn thương", "Hơi tự cao, khó nhận phê bình", "Cầu toàn, khắt khe",
        "Do dự, ngại từ chối", "Đa nghi, hay giữ ấm ức", "Thiếu kiên nhẫn, đôi khi quá thẳng",
        "Quá nghiêm túc, dễ ôm đồm", "Xa cách về cảm xúc, đôi khi cố chấp", "Hay mơ mộng, thiếu dứt khoát"};

    /** Cung đối diện trên vòng hoàng đạo (cách 180°). */
    public static int opposite(int i) { return (i + 6) % 12; }

    /** Hai cung cùng nguyên tố còn lại (tam hợp, cách nhau 120°). */
    public static String sameElement(int i) { return NAME[(i + 4) % 12] + ", " + NAME[(i + 8) % 12]; }

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
