package vn.lichviet.core.fortune;

/** Gợi ý tên hay theo giới tính và ngũ hành (tên và ý nghĩa). */
public final class TenHay {
    private TenHay() {}

    public static final String[] HANH = {"Kim", "Thủy", "Mộc", "Hỏa", "Thổ"};

    /** [giới tính 0 nam, 1 nữ][hành] -> danh sách "Tên|ý nghĩa". */
    public static final String[][][] DATA = {
        { // Nam
            {"Bảo|Báu vật quý giá", "Cương|Mạnh mẽ, cứng cỏi", "Nguyên|Khởi đầu, nguồn gốc", "Ngân|Bạc quý, giàu có", "Hữu|Có được, đầy đủ", "Thắng|Chiến thắng, thành công", "Trung|Trung thành, ngay thẳng", "Phong|Phong thái, khí chất"},
            {"Hải|Biển cả bao la", "Giang|Dòng sông lớn", "Khánh|Vui mừng, may mắn", "Thủy|Nước, mềm mà mạnh", "Hưng|Hưng thịnh", "Quân|Người quân tử", "Tuyền|Dòng suối trong", "Hà|Sông ngân hà"},
            {"Lâm|Rừng cây rậm rạp", "Tùng|Cây tùng vững chãi", "Khôi|Khôi ngô, tuấn tú", "Đông|Phương đông, ngày mới", "Phúc|Phúc đức, hạnh phúc", "Kiệt|Tài giỏi xuất chúng", "Bách|Cây bách bền bỉ", "Quốc|Đất nước, lớn lao"},
            {"Minh|Sáng suốt, thông minh", "Huy|Rực rỡ, vẻ vang", "Nhật|Mặt trời", "Quang|Ánh sáng", "Dương|Ánh dương ấm áp", "Hiển|Hiển đạt, nổi bật", "Đức|Đức độ", "Thịnh|Thịnh vượng"},
            {"An|Bình an", "Sơn|Núi cao vững vàng", "Kiên|Kiên định", "Thành|Thành công, thành thật", "Vĩnh|Lâu dài, vĩnh cửu", "Trường|Trường tồn", "Bình|Bình yên", "Long|Rồng, cao quý"},
        },
        { // Nữ
            {"Ngân|Bạc quý, trong trẻo", "Kim|Quý giá như vàng", "Tâm|Tấm lòng", "Ái|Yêu thương", "Mỹ|Xinh đẹp", "Vân|Mây trắng nhẹ nhàng", "Phương|Hương thơm, phương hướng", "Nhi|Bé nhỏ, đáng yêu"},
            {"Thủy|Dịu dàng như nước", "Giang|Dòng sông hiền hòa", "Hà|Dòng sông", "Uyên|Uyên bác, sâu sắc", "Băng|Trong sáng như băng", "Khánh|Niềm vui", "Loan|Chim loan quý", "Tuyết|Trắng trong như tuyết"},
            {"Lan|Hoa lan thanh cao", "Mai|Hoa mai", "Trúc|Cây trúc thanh tao", "Thảo|Cỏ thơm, hiếu thảo", "Hương|Hương thơm", "Quỳnh|Hoa quỳnh", "Xuân|Mùa xuân tươi mới", "Liên|Hoa sen"},
            {"Ánh|Ánh sáng", "Linh|Linh hoạt, linh thiêng", "Dung|Dung mạo đẹp", "Hồng|Màu hồng tươi thắm", "Nhung|Mềm mại", "Đan|Màu đỏ son", "Huyền|Huyền diệu", "Thu|Mùa thu dịu dàng"},
            {"An|Bình an", "Ngọc|Ngọc quý", "Diệp|Lá ngọc cành vàng", "Châu|Ngọc trai", "Bích|Ngọc bích", "Anh|Tinh anh", "Thảo|Hiếu thảo", "Yên|Yên bình"},
        },
    };
}
