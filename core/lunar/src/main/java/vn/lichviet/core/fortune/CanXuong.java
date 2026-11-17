package vn.lichviet.core.fortune;

/**
 * Cân xương tính số (cân lượng chỉ): cộng trọng lượng của năm (can chi), tháng, ngày, giờ sinh âm lịch.
 * Đơn vị là chỉ; 10 chỉ = 1 lượng. Bảng theo tracuutuvi.com, đối chiếu hocvienlyso.org, thayungkhiem.vn.
 */
public final class CanXuong {
    private CanXuong() {}

    /** Giờ Tý..Hợi (chỉ). */
    static final int[] GIO = {16, 6, 7, 10, 9, 16, 10, 8, 8, 9, 6, 6};
    /** Tháng 1..12 (chỉ). */
    static final int[] THANG = {6, 7, 18, 9, 5, 16, 9, 15, 18, 18, 9, 5};
    /** Ngày 1..30 (chỉ). */
    static final int[] NGAY = {5, 10, 8, 15, 16, 15, 8, 16, 8, 16, 9, 17, 8, 17, 10, 8, 9, 18, 5, 15, 10, 9, 8, 9, 15, 18, 7, 8, 16, 6};
    /**
     * Năm theo chi (Tý..Hợi), mỗi chi 5 can theo thứ tự xuất hiện: chi dương dùng Giáp, Bính, Mậu, Canh, Nhâm;
     * chi âm dùng Ất, Đinh, Kỷ, Tân, Quý.
     */
    static final int[][] NAM = {
        {12, 16, 15, 7, 5},   // Tý
        {9, 8, 8, 7, 5},      // Sửu
        {12, 6, 8, 9, 9},     // Dần
        {8, 7, 19, 12, 12},   // Mão
        {8, 8, 12, 12, 10},   // Thìn
        {7, 6, 5, 6, 7},      // Tỵ
        {15, 13, 19, 9, 8},   // Ngọ
        {6, 5, 6, 8, 7},      // Mùi
        {5, 5, 14, 8, 7},     // Thân
        {15, 14, 5, 16, 8},   // Dậu
        {5, 6, 14, 9, 10},    // Tuất
        {9, 16, 9, 17, 7},    // Hợi
    };

    /** can 0 = Giáp … 9 = Quý; chi 0 = Tý … 11 = Hợi. */
    public static int nam(int can, int chi) { return NAM[chi][can / 2]; }
    public static int thang(int m) { return THANG[Math.max(1, Math.min(12, m)) - 1]; }
    public static int ngay(int d) { return NGAY[Math.max(1, Math.min(30, d)) - 1]; }
    public static int gio(int chi) { return GIO[chi]; }

    public static int total(int can, int chi, int month, int day, int hourChi) {
        return nam(can, chi) + thang(month) + ngay(day) + gio(hourChi);
    }

    /** "4 lượng 6 chỉ". */
    public static String text(int chi10) {
        int l = chi10 / 10, c = chi10 % 10;
        return l + " lượng" + (c > 0 ? " " + c + " chỉ" : "");
    }

    /** Ý nghĩa tóm lược theo tổng (2 lượng 1 chỉ … 7 lượng 2 chỉ), diễn đạt lại từ tracuutuvi.com. */
    public static String meaning(int t) {
        switch (t) {
            case 21: return "Mức thấp nhất: đời nhiều vất vả, cô đơn, hay gặp phiền muộn, lo âu.";
            case 22: return "Làm lụng không ngơi mà vẫn thiếu thốn; hôn nhân dễ căng thẳng.";
            case 23: return "Gặp trở ngại suốt đời, ít được gia đình nâng đỡ.";
            case 24: return "Phải xa quê mới dễ sống, ít chỗ dựa từ gia đình.";
            case 25: return "Phải tự lực; đường lập nghiệp nhiều khó khăn.";
            case 26: return "Nhiều trắc trở, làm ăn hay thất bại; nam giới thường phải đi xa lập nghiệp.";
            case 27: return "Ít của cải, thường lo chuyện tiền bạc, ít người giúp đỡ.";
            case 28: return "Khó ổn định chỗ ở, làm ăn vất vả.";
            case 29: return "Tuổi trẻ vất vả, trung niên yên ổn, về già đủ đầy.";
            case 30: return "Siêng năng, tiết kiệm; dù vất vả nhưng tuổi già an nhàn.";
            case 31: return "Kiếm tiền không dễ; trung niên ổn định, có tích lũy.";
            case 32: return "Tuổi trẻ kém may; trung niên làm ăn phát đạt, sự nghiệp đi lên.";
            case 33: return "Thời trẻ vất vả, về sau tài chính ổn định.";
            case 34: return "Tính nóng vội, hay xa nhà; tu tâm, làm việc thiện được cho là giúp ích.";
            case 35: return "Không được hưởng gia sản, nhưng chí lớn; cần rèn luyện bản thân.";
            case 36: return "Đời sống thoải mái, cơ hội đến dễ dàng.";
            case 37: return "Chăm chỉ, ngay thẳng, có tài; tự lập nên sự nghiệp.";
            case 38: return "Thanh nhã; sớm thành đạt, có địa vị và danh tiếng.";
            case 39: return "Nhiều khó khăn; trung niên tiến bộ dần và ổn định.";
            case 40: return "Có phúc; thời trẻ vất vả, về sau sung túc.";
            case 41: return "Thông minh sắc sảo, đời đầy đủ; trung niên gặp phúc.";
            case 42: return "Ít được gia đình hỗ trợ; tự gây dựng, đỉnh cao ở trung niên.";
            case 43: return "Phong thái nho nhã, điềm tĩnh; cuộc sống êm ả.";
            case 44: return "Thời trẻ khó khăn về tiền bạc, về sau sung túc bên gia đình.";
            case 45: return "Thuận lợi về sự nghiệp, là chỗ dựa cho người khác; đường hôn nhân hoặc con cái có trắc trở.";
            case 46: return "Hạnh phúc, đủ đầy; được cho là phát khi đổi chỗ ở hoặc đổi tên.";
            case 47: return "Thời trẻ lận đận, về sau thoải mái, phát đạt.";
            case 48: return "Nhiều trắc trở, kết quả thường không như mong muốn; phải tự lập.";
            case 49: return "Phúc lớn; được kính trọng, đời sống sung túc.";
            case 50: return "Có quý nhân và may mắn; coi trọng tiền bạc; trung niên thoải mái.";
            case 51: return "Thịnh vượng; người thân giỏi giang, hỗ trợ; làm ăn thành công.";
            case 52: return "Thông minh, nhiều tài; dễ gặp may mà không tốn nhiều công sức.";
            case 53: return "Cả đời phú quý, vinh hiển.";
            case 54: return "Thông minh, giỏi giao tiếp; cuộc sống êm đềm, dễ thành công.";
            case 55: return "Thời trẻ nhiều lo toan, về già thịnh vượng.";
            case 56: return "Giàu có và hạnh phúc.";
            case 57: return "Có năng lực, được nhiều người nể phục.";
            case 58: return "Có chí lớn và danh tiếng; gia đình thành đạt nhờ con cái.";
            case 59: return "Có thể theo đường công danh; tính thẳng, nóng; tự lập.";
            case 60: return "Bền bỉ; thành công thuận lợi, giàu có.";
            case 61: return "Cuộc sống hạnh phúc, sự nghiệp tốt.";
            case 62: return "Phúc lộc dồi dào về mọi mặt.";
            case 63: return "Biết nắm bắt thời cơ; có địa vị cao.";
            case 64: return "Giàu có, hạnh phúc trọn đời.";
            case 65: return "Phúc lớn, nhiều quý nhân; theo đường công danh; gia đình vẻ vang.";
            case 66: return "Phúc vượt trội, địa vị cao, gia đình êm ấm.";
            case 67: return "Giàu sang, hạnh phúc suốt đời.";
            case 68: return "Kiếm được nhiều nhưng khó giữ tiền, của dễ hao.";
            case 69: return "Được thừa hưởng của cải; không giữ gìn thì dễ mất nhanh.";
            case 70: return "Thành công không phải vất vả; phú quý, vinh hiển.";
            case 71: return "May mắn hiếm có; giàu sang, vinh hiển.";
            case 72: return "Nhiều may mắn, sức khỏe tốt, sự nghiệp phát triển.";
            default: return t < 21 ? "Thấp hơn mức thấp nhất trong bảng (2 lượng 1 chỉ)." : "Cao hơn mức cao nhất trong bảng (7 lượng 2 chỉ).";
        }
    }
}
