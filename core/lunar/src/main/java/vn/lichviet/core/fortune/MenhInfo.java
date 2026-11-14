package vn.lichviet.core.fortune;

/** Thông tin theo ngũ hành bản mệnh và theo con giáp (tham khảo dân gian). */
public final class MenhInfo {
    private MenhInfo() {}

    static int idx(String h) {
        switch (h) {
            case "Kim": return 0;
            case "Thủy": return 1;
            case "Mộc": return 2;
            case "Hỏa": return 3;
            default: return 4;
        }
    }

    public static final String[] TINH_CACH = {
        "Người mệnh Kim cứng rắn, quyết đoán, trọng nguyên tắc và công bằng; có óc tổ chức nhưng đôi khi cứng nhắc.",
        "Người mệnh Thủy mềm mỏng, linh hoạt, giao tiếp tốt, giàu trực giác; đôi khi dễ dao động.",
        "Người mệnh Mộc nhân hậu, sáng tạo, ham học hỏi, có lý tưởng; đôi khi thiếu kiên nhẫn.",
        "Người mệnh Hỏa nhiệt tình, năng động, có sức lôi cuốn, dám nghĩ dám làm; dễ nóng nảy.",
        "Người mệnh Thổ điềm đạm, thật thà, đáng tin, chu đáo; đôi khi bảo thủ, chậm thay đổi.",
    };
    public static final String[] NGHE = {
        "Tài chính – ngân hàng, kế toán, kỹ thuật cơ khí, kim hoàn, quản lý, luật, công an – quân đội.",
        "Du lịch, vận tải, thương mại, truyền thông, ngoại giao, nghệ thuật, thủy sản, đồ uống.",
        "Giáo dục, y dược, nông – lâm nghiệp, nội thất gỗ, xuất bản, thời trang, thiết kế.",
        "Công nghệ – điện tử, năng lượng, ẩm thực, marketing, giải trí, mỹ phẩm, làm đẹp.",
        "Bất động sản, xây dựng, kiến trúc, nông nghiệp, gốm sứ, bảo hiểm, tư vấn, nhân sự.",
    };
    public static final String[] TAI_CHINH = {
        "Giỏi giữ tiền và lập kế hoạch. Hợp tích lũy dài hạn, tài sản bền vững; nên tránh cứng nhắc bỏ lỡ cơ hội.",
        "Kiếm tiền linh hoạt nhưng chi tiêu cũng nhanh. Nên đặt quỹ dự phòng và tự động tiết kiệm mỗi tháng.",
        "Tiền đến từ sự học hỏi và phát triển bản thân. Hợp đầu tư cho kiến thức, kinh doanh nhỏ phát triển dần.",
        "Nhạy cơ hội, dám chơi lớn. Nên kiểm soát cảm xúc khi đầu tư, chia nhỏ rủi ro, tránh vay nóng.",
        "Thận trọng, tích lũy chắc chắn. Hợp tài sản hữu hình như nhà đất; nên mạnh dạn hơn với cơ hội tốt.",
    };
    public static final String[] TINH_DUYEN = {
        "Chung thủy, nghiêm túc trong tình cảm nhưng ít thể hiện. Hợp người mệnh Thổ, Thủy.",
        "Lãng mạn, tinh tế, biết lắng nghe. Hợp người mệnh Kim, Mộc.",
        "Chân thành, ấm áp, quan tâm đối phương. Hợp người mệnh Thủy, Hỏa.",
        "Nồng nhiệt, chủ động, yêu hết mình. Hợp người mệnh Mộc, Thổ.",
        "Ổn định, chu đáo, coi trọng gia đình. Hợp người mệnh Hỏa, Kim.",
    };
    public static final String[] MAU_HOP = {
        "Trắng, xám, bạc (bản mệnh); vàng, nâu đất (tương sinh)",
        "Đen, xanh nước biển (bản mệnh); trắng, xám, bạc (tương sinh)",
        "Xanh lá (bản mệnh); đen, xanh nước biển (tương sinh)",
        "Đỏ, hồng, tím (bản mệnh); xanh lá (tương sinh)",
        "Vàng, nâu đất (bản mệnh); đỏ, hồng, tím (tương sinh)",
    };
    public static final String[] MAU_KY = {"Đỏ, hồng, tím", "Vàng, nâu đất", "Trắng, xám, bạc", "Đen, xanh nước biển", "Xanh lá"};
    public static final String[] SO_HOP = {"6, 7, 2, 5, 8", "1, 6, 7", "3, 4, 1", "9, 3, 4", "2, 5, 8, 9"};
    public static final String[] CAY_HOP = {
        "Kim tiền, lưỡi hổ, bạch mã hoàng tử, cẩm nhung",
        "Thủy tùng, phát tài, kim ngân, trầu bà xanh",
        "Trầu bà, vạn niên thanh, lưỡi hổ, cau tiểu trâm",
        "Hồng môn, phú quý, trạng nguyên, vạn lộc",
        "Ngọc ngân, sen đá, phát lộc, lưỡi hổ viền vàng",
    };

    public static String tinhCach(String h) { return TINH_CACH[idx(h)]; }
    public static String nghe(String h) { return NGHE[idx(h)]; }
    public static String taiChinh(String h) { return TAI_CHINH[idx(h)]; }
    public static String tinhDuyen(String h) { return TINH_DUYEN[idx(h)]; }
    public static String mauHop(String h) { return MAU_HOP[idx(h)]; }
    public static String mauKy(String h) { return MAU_KY[idx(h)]; }
    public static String soHop(String h) { return SO_HOP[idx(h)]; }
    public static String cayHop(String h) { return CAY_HOP[idx(h)]; }

    public static final String[] CON_GIAP = {
        "Tý: nhanh nhẹn, khéo léo, tiết kiệm, giỏi xoay xở; đôi khi đa nghi.",
        "Sửu: cần cù, nhẫn nại, đáng tin; làm việc bền bỉ nhưng hơi cố chấp.",
        "Dần: dũng cảm, tự tin, có tố chất lãnh đạo; dễ bốc đồng.",
        "Mão: hiền hòa, tinh tế, khéo giao tiếp; đôi khi thiếu quyết đoán.",
        "Thìn: tham vọng, mạnh mẽ, nhiều năng lượng; có lúc kiêu ngạo.",
        "Tỵ: sâu sắc, khôn ngoan, kín đáo; đôi khi đa nghi, khó gần.",
        "Ngọ: phóng khoáng, nhiệt tình, thích tự do; dễ thiếu kiên nhẫn.",
        "Mùi: hiền lành, giàu cảm xúc, yêu nghệ thuật; đôi khi dễ lo âu.",
        "Thân: thông minh, lanh lợi, hài hước; đôi khi thiếu nhất quán.",
        "Dậu: chăm chỉ, chỉn chu, thẳng thắn; hay cầu toàn.",
        "Tuất: trung thành, chính trực, có trách nhiệm; dễ lo lắng.",
        "Hợi: thật thà, rộng lượng, hưởng thụ; đôi khi cả tin.",
    };
}
