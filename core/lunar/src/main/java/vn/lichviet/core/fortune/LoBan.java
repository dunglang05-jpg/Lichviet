package vn.lichviet.core.fortune;

/** Thước Lỗ Ban: 52,2 cm (thông thủy), 42,9 cm (âm phần), 38,8 cm (dương trạch). */
public final class LoBan {
    private LoBan() {}

    public static final String[] TYPE = {"Thước Lỗ Ban 52,2 cm (thông thủy)", "Thước Lỗ Ban 42,9 cm (dương trạch)", "Thước Lỗ Ban 38,8 cm (âm phần)"};
    public static final double[] LEN = {52.2, 42.9, 38.8};
    public static final String[][] CUNG = {
        {"Quý Nhân", "Hiểm Họa", "Thiên Tai", "Thiên Tài", "Nhân Lộc", "Cô Độc", "Thiên Tặc", "Tể Tướng"},
        {"Tài", "Bệnh", "Ly", "Nghĩa", "Quan", "Kiếp", "Hại", "Bản"},
        {"Đinh", "Hại", "Vượng", "Khổ", "Nghĩa", "Quan", "Tử", "Hưng", "Thất", "Tài"},
    };
    /** Cung nhỏ trong từng cung lớn, chia đều (vansunhuy.net, alux.com.vn, phongthuyphunggia.com). */
    public static final String[][][] SUB = {
        {
            {"Quyền lộc", "Trung tín", "Tác quan", "Phát đạt", "Thông minh"},
            {"Án thành", "Hỗn nhân", "Thất hiếu", "Tai họa", "Trường bệnh"},
            {"Hoàn tử", "Quan tài", "Thân tàn", "Thất tài", "Hệ quả"},
            {"Thi thơ", "Văn học", "Thanh quý", "Tác lộc", "Thiên lộc"},
            {"Trí tôn", "Phú quý", "Tiến bửu", "Thập thiện", "Văn chương"},
            {"Bạc nghịch", "Vô vọng", "Ly tán", "Tửu thục", "Dâm dục"},
            {"Phong bệnh", "Chiêu ôn", "Ôn tài", "Ngục tù", "Quan tài"},
            {"Đại tài", "Thi thơ", "Hoạch tài", "Hiếu tử", "Quý nhân"},
        },
        {
            {"Tài đức", "Bảo khố", "Lục hợp", "Nghênh phúc"},
            {"Thoái tài", "Công sự", "Lao chấp", "Cô quả"},
            {"Trường khổ", "Kiếp tài", "Quan quỷ", "Thất thoát"},
            {"Thêm đinh", "Lợi ích", "Quý tử", "Đại cát"},
            {"Thuận khoa", "Hoạch tài", "Tấn ích", "Phú quý"},
            {"Tử biệt", "Thoái khẩu", "Ly hương", "Thất tài"},
            {"Họa chí", "Tử tuyệt", "Lâm bệnh", "Khẩu thiệt"},
            {"Tài chí", "Đăng khoa", "Tiến bảo", "Hưng vượng"},
        },
        {
            {"Phúc tinh", "Đỗ đạt", "Tài vượng", "Đăng khoa"},
            {"Khẩu thiệt", "Lâm bệnh", "Tử tuyệt", "Tai chí"},
            {"Thiên đức", "Hỷ sự", "Tiến bảo", "Thêm phúc"},
            {"Thất thoát", "Quan quỷ", "Kiếp tài", "Vô tự"},
            {"Đại cát", "Tài vượng", "Ích lợi", "Thiên khố"},
            {"Phú quý", "Tiến bảo", "Tài lộc", "Thuận khoa"},
            {"Ly hương", "Tử biệt", "Thoái đinh", "Thất tài"},
            {"Đăng khoa", "Quý tử", "Thêm đinh", "Hưng vượng"},
            {"Cô quả", "Lao chấp", "Công sự", "Thoái tài"},
            {"Nghênh phúc", "Lục hợp", "Tiến bảo", "Tài đức"},
        },
    };
    /** Ý nghĩa tóm tắt của cung lớn (vansunhuy.net). */
    public static final String[][] Y = {
        {"Mọi việc hanh thông, có quý nhân giúp, tài lộc và con cái thuận lợi.", "Hao tài, công việc trắc trở, cuộc sống khó khăn.",
         "Tai ương, bệnh tật, mất mát, vợ chồng bất hòa.", "Gia cảnh khấm khá, may mắn về tài lộc, con cái hiếu thảo.",
         "Gia chủ phát triển, con cái học giỏi, gia đình yên ổn, phú quý.", "Hao tài tốn của, chia ly, con cái ngỗ nghịch.",
         "Bệnh tật bất ngờ, tai họa, vướng lao lý.", "Hanh thông mọi mặt, công danh và tài lộc đủ đầy."},
        {"Nhiều tài lộc, tiền bạc dồi dào, làm gì cũng thuận ý.", "Hao tiền, dễ dính thị phi, cuộc sống khó khăn.",
         "Thị phi, hao tài, công việc đình trệ.", "Nhiều may mắn, gia đình êm ấm.", "Công danh hanh thông, tiền tài phát đạt.",
         "Dễ gặp tai nạn, mất mát, phải tha hương.", "Nhiều bất trắc, bệnh tật, gia đình bất hòa.", "Cầu được tiền, thi cử đỗ đạt, có quý nhân giúp."},
        {"Nhiều may mắn, tiền bạc đến, con cái ngoan, thi cử đỗ đạt.", "Cẩn thận lời nói, dễ ốm đau.", "Thịnh vượng, tài lộc dồi dào, có việc vui.",
         "Hao tiền tốn của, tranh chấp không thành.", "Đại cát, tiền bạc đến, được quý nhân phù trợ.", "Cầu gì được nấy, nhanh giàu có.",
         "Chia lìa, xa quê, mất tiền.", "Hưng thịnh, làm ăn phát đạt, con cái giỏi giang.", "Mất mát, cô đơn, làm ăn thất bát.",
         "Nhiều phúc lộc, tiền bạc đến, được người kính trọng."},
    };

    /** Chỉ số cung nhỏ trong cung lớn. */
    public static int sub(int type, double cm) {
        double len = LEN[type]; int n = CUNG[type].length;
        double w = len / n, r = cm % len;
        int big = cung(type, cm), k = SUB[type][big].length;
        int i = (int) Math.floor((r - big * w) / (w / k));
        return Math.min(k - 1, Math.max(0, i));
    }

    /** Khoảng [từ, đến] cm của cung lớn chứa [cm]. */
    public static double[] range(int type, double cm) {
        double len = LEN[type], w = len / CUNG[type].length;
        double base = Math.floor(cm / len) * len + cung(type, cm) * w;
        return new double[]{base, base + w};
    }

    public static final boolean[][] TOT = {
        {true, false, false, true, true, false, false, true},
        {true, false, false, true, true, false, false, true},
        {true, false, true, false, true, true, false, true, false, true},
    };

    /** Chỉ số cung của kích thước [cm] trên thước [type]. */
    public static int cung(int type, double cm) {
        double len = LEN[type];
        int n = CUNG[type].length;
        double r = cm % len;
        int i = (int) Math.floor(r / (len / n));
        return Math.min(n - 1, Math.max(0, i));
    }

    public static boolean tot(int type, double cm) { return TOT[type][cung(type, cm)]; }

    /** Kích thước tốt gần nhất (bước 0,5 cm) quanh [cm]. */
    public static double nearestGood(int type, double cm) {
        for (double d = 0; d < 30; d += 0.5) {
            if (cm - d > 0 && tot(type, cm - d)) return cm - d;
            if (tot(type, cm + d)) return cm + d;
        }
        return cm;
    }
}
