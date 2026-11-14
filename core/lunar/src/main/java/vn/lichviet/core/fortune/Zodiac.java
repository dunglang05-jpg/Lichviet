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
    public static final String[] TRAITS = {
        "Nhiệt huyết, dũng cảm, thẳng thắn, thích thử thách và hành động nhanh. Đôi khi nóng vội và thiếu kiên nhẫn.",
        "Kiên định, thực tế, chung thủy, yêu cái đẹp và sự ổn định. Có lúc bảo thủ, chậm thay đổi.",
        "Thông minh, nhanh nhạy, giỏi giao tiếp, tò mò với mọi thứ. Dễ thay đổi ý định và thiếu tập trung.",
        "Giàu tình cảm, chu đáo, gắn bó với gia đình, trực giác tốt. Nhạy cảm và hay lo nghĩ.",
        "Tự tin, hào phóng, có sức hút, thích được công nhận. Đôi khi kiêu hãnh và thích làm trung tâm.",
        "Tỉ mỉ, chăm chỉ, khéo léo, có óc phân tích. Hay cầu toàn và tự đòi hỏi cao.",
        "Hòa nhã, công bằng, khéo ngoại giao, yêu nghệ thuật. Hay do dự khi phải lựa chọn.",
        "Sâu sắc, quyết liệt, trung thành, có ý chí mạnh. Dễ ghen và giữ trong lòng.",
        "Lạc quan, phóng khoáng, thích tự do và học hỏi. Đôi khi bộc trực và thiếu cẩn trọng.",
        "Kỷ luật, tham vọng, có trách nhiệm, bền bỉ. Có lúc khô khan và quá nghiêm khắc.",
        "Độc lập, sáng tạo, nhân văn, suy nghĩ khác biệt. Đôi khi xa cách và bướng bỉnh.",
        "Giàu lòng trắc ẩn, mơ mộng, nghệ sĩ, nhạy cảm. Dễ bị ảnh hưởng bởi cảm xúc người khác.",
    };
    /** Cung hợp nhất (cùng nguyên tố và nguyên tố hỗ trợ). */
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

    /** Lời nhắn hằng ngày cho cung (thay đổi theo ngày, mang tính giải trí). */
    public static String daily(int sign, int jd) {
        String[] msg = {
            "Ngày thuận lợi để bắt đầu việc mới, hãy chủ động.",
            "Nên kiên nhẫn, kết quả tốt đến từ sự bền bỉ.",
            "Giao tiếp suôn sẻ, dễ nhận được sự giúp đỡ.",
            "Chú ý chi tiêu, tránh mua sắm theo cảm hứng.",
            "Năng lượng dồi dào, hợp vận động và gặp gỡ bạn bè.",
            "Dành thời gian cho gia đình sẽ mang lại niềm vui.",
            "Hợp học hỏi, đọc sách, lên kế hoạch dài hạn.",
            "Công việc có tiến triển, cấp trên ghi nhận nỗ lực.",
            "Nên nghỉ ngơi đủ, tránh tranh cãi không cần thiết.",
            "Có tin vui về tài chính hoặc cơ hội hợp tác.",
            "Tình cảm ấm áp, nên bày tỏ suy nghĩ chân thành.",
            "Trực giác tốt, hãy tin vào cảm nhận của mình.",
        };
        return msg[Math.floorMod(jd * 7 + sign * 5, msg.length)];
    }
}
