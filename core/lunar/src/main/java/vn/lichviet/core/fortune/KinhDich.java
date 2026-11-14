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
    /** 2 = cát, 1 = bình, 0 = hung. */
    public static final int[] CAT = {0, 2, 2, 0, 1, 1, 0, 1, 2, 1, 1, 2, 0, 2, 2, 2, 2, 2, 1, 2, 1,
        1, 1, 0, 2, 1, 2, 1, 0, 0, 1, 2, 2, 0, 1, 2, 0, 2, 0, 0, 2,
        1, 2, 1, 0, 2, 2, 0, 1, 2, 2, 1, 1, 2, 0, 2, 0, 1, 2, 1, 1,
        2, 1, 1, 0};
    public static final String[] Y = {"",
        "Trời mạnh mẽ không ngừng. Thời vận hanh thông, nên tự cường, giữ chính đạo, tránh kiêu ngạo.",
        "Đất dày chở muôn vật. Nên mềm mỏng, theo người đi trước, kiên nhẫn sẽ có kết quả tốt.",
        "Khó khăn lúc khởi đầu như mầm cây đội đất. Chưa nên vội, cần tìm người giúp đỡ.",
        "Còn mờ mịt, cần học hỏi và nghe lời người có kinh nghiệm. Đừng hấp tấp.",
        "Chờ đợi đúng thời. Giữ lòng tin, chuẩn bị kỹ, thời cơ sẽ đến.",
        "Có tranh chấp, kiện tụng. Nên nhường nhịn, hòa giải, không nên theo đến cùng.",
        "Như việc cầm quân: cần kỷ luật, người lãnh đạo đứng đắn, việc lớn mới thành.",
        "Gần gũi, đoàn kết. Hợp tác với người tốt, nên quyết sớm, chậm sẽ lỡ.",
        "Tích lũy nhỏ, mây kéo chưa mưa. Việc tiến chậm, nên bồi đắp dần.",
        "Đi trên đuôi hổ mà không bị cắn: giữ lễ độ, thận trọng thì vượt qua nguy hiểm.",
        "Trời đất giao hòa, thông suốt. Thời kỳ thuận lợi, làm ăn phát đạt.",
        "Bế tắc, trên dưới không thông. Nên giữ mình, chờ thời, tránh việc lớn.",
        "Đồng lòng với mọi người. Hợp tác rộng rãi, công khai minh bạch sẽ thành công.",
        "Có nhiều, thịnh vượng như mặt trời trên cao. Tài lộc tốt, nên chia sẻ, khiêm tốn.",
        "Khiêm tốn thì được lòng người, việc gì cũng thuận. Quẻ rất tốt.",
        "Vui vẻ, hứng khởi, có sự chuẩn bị. Thuận cho việc khởi sự, nhưng đừng ham vui quá.",
        "Theo thời, thuận người. Biết theo điều phải thì tốt.",
        "Đổ nát cần sửa sang. Nên chấn chỉnh việc cũ, sửa sai thì hưng thịnh lại.",
        "Tiến tới, lớn mạnh dần. Thời thuận, nhưng nhớ thịnh rồi sẽ có lúc suy.",
        "Quan sát, chiêm nghiệm. Nên xem xét kỹ trước khi hành động, làm gương cho người.",
        "Cắn vỡ trở ngại. Có vướng mắc pháp lý, cần cương quyết và công minh.",
        "Trang sức, vẻ ngoài. Việc nhỏ thì tốt, việc lớn cần thực chất.",
        "Bóc lột, suy sụp dần. Không nên tiến hành việc gì, giữ yên chờ thời.",
        "Trở lại, phục hồi. Điều tốt bắt đầu quay về, nên làm lại từ đầu.",
        "Không càn bậy. Làm đúng đạo thì tốt, mưu tính bất chính sẽ gặp tai.",
        "Tích lũy lớn. Nên trau dồi, nuôi dưỡng tài năng, hợp làm việc lớn.",
        "Nuôi dưỡng. Chú ý ăn uống, lời nói, nuôi thân và nuôi đức.",
        "Quá mức, gánh nặng. Việc vượt sức, cần biến thông kịp thời.",
        "Hiểm trở chồng chất. Giữ lòng thành, bình tĩnh thì vượt qua.",
        "Sáng sủa, nương tựa. Văn minh, danh tiếng tốt, nên dựa vào người chính trực.",
        "Cảm ứng, giao cảm. Tình cảm, hôn nhân thuận lợi.",
        "Bền lâu. Giữ vững đường đã chọn, kiên trì sẽ tốt.",
        "Lui ẩn. Thời chưa thuận, nên rút lui đúng lúc để bảo toàn.",
        "Mạnh lớn. Có sức mạnh nhưng phải dùng đúng đạo, tránh liều lĩnh.",
        "Tiến lên, như mặt trời mọc. Thăng tiến, được trọng dụng.",
        "Ánh sáng bị che. Gặp khó, nên giữ kín tài năng, nhẫn nại.",
        "Người trong nhà. Lo việc gia đình, nội bộ hòa thuận thì tốt.",
        "Chia lìa, trái ý. Việc nhỏ còn được, việc lớn khó thành.",
        "Khó khăn, què chân. Nên dừng lại, tìm người giúp, đi đường vòng.",
        "Cởi bỏ, giải thoát. Khó khăn qua đi, nên nhanh chóng giải quyết.",
        "Bớt đi. Bớt cái dư để bù cái thiếu; chịu thiệt trước sẽ được sau.",
        "Thêm lên, lợi ích. Thuận làm việc lớn, giúp người sẽ được lợi.",
        "Quyết đoán. Cần dứt khoát loại bỏ điều xấu, nhưng tránh dùng sức mạnh thô bạo.",
        "Gặp gỡ bất ngờ. Cẩn thận người mới, đừng vội tin.",
        "Tụ họp. Hợp tác, hội họp, tế lễ đều tốt.",
        "Đi lên. Tiến từng bước vững chắc, gặp người giúp đỡ.",
        "Khốn cùng. Lời nói khó được tin, nên giữ chí, chờ thời.",
        "Cái giếng nuôi người. Giữ cái gốc, sửa sang nền tảng.",
        "Đổi mới, cải cách. Thời cơ thay cũ đổi mới đã đến.",
        "Cái vạc, xây dựng cái mới. Thành tựu, được trọng dụng.",
        "Sấm động. Có biến động bất ngờ; bình tĩnh thì không hại.",
        "Dừng lại đúng chỗ. Biết dừng thì yên, không nên tiến.",
        "Tiến dần dần. Việc gì cũng nên theo trình tự, hôn nhân tốt.",
        "Em gái về nhà chồng. Việc không đúng thứ tự, tiến hành dễ hỏng.",
        "Thịnh lớn, như mặt trời giữa trưa. Nên tận dụng lúc thịnh, phòng khi suy.",
        "Người lữ khách. Việc nhỏ được, nên khiêm tốn, cẩn trọng nơi xa.",
        "Thuận theo, len lỏi như gió. Mềm mỏng, kiên trì thì thành.",
        "Vui vẻ, hòa duyệt. Giao tiếp tốt, hợp tác thuận lợi.",
        "Phân tán, lìa tan. Nên hàn gắn, đoàn kết lại.",
        "Tiết chế, chừng mực. Chi tiêu, hành xử có giới hạn thì tốt.",
        "Lòng thành tín. Thành thật thì cảm hóa được người, việc thuận.",
        "Nhỏ vượt quá. Việc nhỏ được, việc lớn không nên.",
        "Đã xong, đã thành. Giữ thành quả, đề phòng về sau sinh loạn.",
        "Chưa xong. Còn dang dở, cần cẩn thận đến cùng thì thành.",
    };

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
