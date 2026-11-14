package vn.lichviet.core.fortune;

/** Gợi ý sống khỏe theo 24 tiết khí (thông tin chung, không thay cho tư vấn y tế). */
public final class SongKhoe {
    private SongKhoe() {}

    /** Theo thứ tự DayInfo.TIET_KHI (bắt đầu Xuân phân). */
    public static final String[] TIPS = {
        "Xuân phân: ngày đêm cân bằng. Ngủ đủ giấc, ăn nhiều rau xanh, giữ tinh thần thoải mái.",
        "Thanh minh: trời ấm, ẩm. Giữ nhà thông thoáng, đề phòng dị ứng phấn hoa, uống đủ nước.",
        "Cốc vũ: mưa nhiều, độ ẩm cao. Ăn món dễ tiêu, hạn chế đồ lạnh, giữ khô ráo cơ thể.",
        "Lập hạ: bắt đầu mùa hè. Ngủ trưa ngắn, ăn thanh đạm, bổ sung trái cây tươi.",
        "Tiểu mãn: nóng ẩm tăng. Uống nước ấm, hạn chế đồ chiên rán, chú ý vệ sinh ăn uống.",
        "Mang chủng: nóng và mưa rào. Tránh nắng gắt buổi trưa, tập thể dục sáng sớm hoặc chiều mát.",
        "Hạ chí: ngày dài nhất năm. Bổ sung nước và điện giải, tránh vận động mạnh dưới nắng.",
        "Tiểu thử: nóng bức. Ăn món thanh nhiệt như canh rau, bí đao; ngủ đủ giấc.",
        "Đại thử: nóng nhất năm. Hạn chế ra nắng, mặc đồ thoáng, uống nước thường xuyên.",
        "Lập thu: bắt đầu mùa thu. Giảm dần đồ lạnh, ăn thêm thực phẩm nhuận như lê, mật ong.",
        "Xử thử: hết nóng. Ngủ sớm dậy sớm, giữ ấm bụng khi đêm se lạnh.",
        "Bạch lộ: sương trắng, khô hanh. Uống đủ nước, dưỡng ẩm da, giữ ấm cổ họng.",
        "Thu phân: cân bằng âm dương. Tập thở sâu, đi bộ nhẹ nhàng, giữ tâm trạng ổn định.",
        "Hàn lộ: sương lạnh. Giữ ấm chân, ăn món ấm, hạn chế đồ sống lạnh.",
        "Sương giáng: cuối thu, lạnh dần. Bổ sung đạm và rau củ, giữ ấm khi ra ngoài sớm.",
        "Lập đông: bắt đầu mùa đông. Ngủ sớm, giữ ấm đầu, cổ và chân; vận động vừa phải.",
        "Tiểu tuyết: lạnh khô. Uống nước ấm, dưỡng ẩm da, ăn canh nóng.",
        "Đại tuyết: rét đậm. Giữ ấm cơ thể, khởi động kỹ trước khi tập thể dục.",
        "Đông chí: đêm dài nhất năm. Nghỉ ngơi nhiều, ăn món ấm bổ, giữ tinh thần vui vẻ.",
        "Tiểu hàn: rét. Hạn chế tắm khuya, giữ ấm khi ra ngoài, theo dõi huyết áp với người lớn tuổi.",
        "Đại hàn: lạnh nhất năm. Mặc nhiều lớp, ăn đủ chất, tránh thay đổi nhiệt độ đột ngột.",
        "Lập xuân: bắt đầu mùa xuân. Vận động ngoài trời nhẹ, ăn uống điều độ dịp Tết.",
        "Vũ thủy: mưa xuân, nồm ẩm. Giữ nhà khô thoáng, đề phòng cảm cúm.",
        "Kinh trập: sâu bọ thức dậy, thời tiết thất thường. Tăng sức đề kháng, ngủ đủ, ăn nhiều rau quả.",
    };

    public static String tip(String tietKhi) {
        for (String t : TIPS) if (t.startsWith(tietKhi + ":")) return t;
        return TIPS[0];
    }

    public static final String[] CHUNG = {
        "Ngủ 7–8 tiếng mỗi đêm, đi ngủ trước 23 giờ.",
        "Uống khoảng 1,5–2 lít nước mỗi ngày.",
        "Vận động ít nhất 30 phút mỗi ngày, 5 ngày mỗi tuần.",
        "Ăn nhiều rau xanh, trái cây; hạn chế đường, muối và đồ chiên rán.",
        "Đứng dậy đi lại sau mỗi giờ ngồi làm việc.",
        "Khám sức khỏe định kỳ mỗi năm.",
    };
}
