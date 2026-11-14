package vn.lichviet.core.fortune;

/** Văn khấn cổ truyền thông dụng. {NGAY} = ngày âm lịch hôm nay, {TEN} = tín chủ, {DIACHI} = địa chỉ. */
public final class VanKhan {
    private VanKhan() {}

    private static final String MO = "Nam mô A Di Đà Phật! (3 lần)\n\n"
        + "Con lạy chín phương Trời, mười phương Chư Phật, Chư Phật mười phương.\n";
    private static final String TIN_CHU = "Tín chủ con là: {TEN}\nNgụ tại: {DIACHI}\n";
    private static final String KET = "\nCẩn cáo!\n\nNam mô A Di Đà Phật! (3 lần)";

    public static final String[][] DATA = {
        {"Mùng Một và ngày Rằm (gia tiên)",
            MO + "Con kính lạy Hoàng thiên Hậu thổ chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại họ ......\n\n"
            + "Hôm nay là {NGAY}.\n" + TIN_CHU
            + "Thành tâm sắm sửa hương hoa, lễ vật, trà quả, thắp nén tâm hương dâng lên trước án.\n"
            + "Kính mời các cụ Tổ tiên nội ngoại về chứng giám lòng thành, thụ hưởng lễ vật.\n"
            + "Cúi xin phù hộ độ trì cho cả gia đình chúng con bình an, mạnh khỏe, công việc hanh thông, mọi sự tốt lành." + KET},
        {"Thần Tài – Thổ Địa (mùng Một, Rằm)",
            MO + "Con kính lạy Đức Thần Tài vị tiền.\nCon kính lạy Ngài Bản gia Thổ Địa Long Mạch Tôn thần cùng các vị Tôn thần cai quản trong xứ này.\n\n"
            + "Hôm nay là {NGAY}.\n" + TIN_CHU
            + "Thành tâm sửa biện hương hoa, lễ vật, kim ngân, trà quả dâng lên trước án.\n"
            + "Cúi xin các Ngài thương xót, giáng lâm chứng giám, phù hộ cho gia chủ buôn bán thuận lợi, tài lộc dồi dào, gia đạo an khang." + KET},
        {"Ngày giỗ (Tiên thường – Chính giỗ)",
            MO + "Con kính lạy Hoàng thiên Hậu thổ chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Hôm nay là {NGAY}, chính ngày giỗ của: ...... (tên, vai vế người mất).\n" + TIN_CHU
            + "Nhân ngày giỗ, con cháu thành tâm sửa biện hương hoa, phẩm vật, cỗ bàn dâng lên trước linh vị.\n"
            + "Kính mời hương linh về chứng giám lòng thành, thụ hưởng lễ vật, phù hộ cho con cháu bình an, mạnh khỏe, gia đạo hưng long.\n"
            + "Chúng con cũng kính mời các vị Tiên linh nội ngoại cùng về hâm hưởng." + KET},
        {"Ông Công, Ông Táo (23 tháng Chạp)",
            MO + "Con kính lạy Ngài Đông Trù Tư mệnh Táo phủ Thần quân.\n\n"
            + "Hôm nay là {NGAY}.\n" + TIN_CHU
            + "Nhân ngày tiễn Táo quân về chầu trời, chúng con thành tâm sắm sửa hương hoa, phẩm vật dâng lên trước án.\n"
            + "Cúi xin Tôn thần gia ân, tâu lên Thượng đế những điều tốt đẹp, bỏ qua lỗi lầm của gia đình trong năm qua; "
            + "phù hộ cho cả nhà sang năm mới bình an, mạnh khỏe, làm ăn phát đạt." + KET},
        {"Giao thừa ngoài trời",
            MO + "Con kính lạy Đương niên Hành khiển, Hành binh chi thần, Phán quan.\nCon kính lạy các Ngài Thần linh, Thổ địa cai quản trong xứ này.\n\n"
            + "Nay phút giao thừa năm cũ sang năm mới, {NGAY}.\n" + TIN_CHU
            + "Thành tâm sắm sửa hương hoa, lễ vật dâng lên trước án, nghênh đón các Ngài quan Đương niên mới.\n"
            + "Cúi xin các Ngài phù hộ cho toàn gia chúng con năm mới an khang thịnh vượng, vạn sự tốt lành." + KET},
        {"Giao thừa trong nhà",
            MO + "Con kính lạy Hoàng thiên Hậu thổ, Táo quân, Thổ địa, Thần tài cùng chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Phút giao thừa, {NGAY}.\n" + TIN_CHU
            + "Năm cũ đã qua, năm mới đến, con cháu thành kính dâng hương, kính mời Tổ tiên về ăn Tết cùng con cháu.\n"
            + "Cúi xin phù hộ cho gia đình năm mới sức khỏe dồi dào, mọi việc hanh thông." + KET},
        {"Mùng Một Tết Nguyên Đán",
            MO + "Con kính lạy Hoàng thiên Hậu thổ chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Hôm nay là {NGAY}, ngày đầu xuân năm mới.\n" + TIN_CHU
            + "Con cháu thành tâm sửa biện hương hoa, phẩm vật, mâm ngũ quả dâng lên trước án.\n"
            + "Kính mời các cụ Tổ tiên về vui xuân cùng con cháu, phù hộ cho cả nhà một năm mới an lành, hạnh phúc." + KET},
        {"Rằm tháng Giêng (Tết Nguyên Tiêu)",
            MO + "Con kính lạy Hoàng thiên Hậu thổ chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Hôm nay là {NGAY}, ngày Rằm tháng Giêng.\n" + TIN_CHU
            + "Thành tâm sắm lễ hương hoa, trà quả dâng lên trước án. Cúi xin chư vị chứng giám, phù hộ cho gia đình cả năm bình an, cát tường." + KET},
        {"Rằm tháng Bảy (Vu Lan – gia tiên)",
            MO + "Con kính lạy Đức Phật Thích Ca, Đức Mục Kiền Liên Bồ Tát.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Hôm nay là {NGAY}, ngày Rằm tháng Bảy, tiết Vu Lan báo hiếu.\n" + TIN_CHU
            + "Con cháu nhớ ơn sinh thành, thành tâm sắm lễ dâng lên. Kính mời hương linh Tổ tiên về thụ hưởng, phù hộ con cháu an lành." + KET},
        {"Cúng cô hồn (Rằm tháng Bảy, ngoài trời)",
            MO + "Con kính lạy Đức Đại Từ Đại Bi Quán Thế Âm Bồ Tát.\n\n"
            + "Hôm nay là {NGAY}.\n" + TIN_CHU
            + "Nhân tiết Trung Nguyên, chúng con sắm lễ cháo, gạo muối, hoa quả, thành tâm cúng dâng các vong linh cô hồn không nơi nương tựa.\n"
            + "Nguyện các vong linh thụ hưởng lễ vật, sớm được siêu thoát; phù hộ cho gia đình bình yên." + KET},
        {"Động thổ",
            MO + "Con kính lạy Hoàng thiên Hậu thổ, Ngài Bản cảnh Thành hoàng, Ngài Thổ địa, Long mạch Tôn thần cai quản khu vực này.\n\n"
            + "Hôm nay là {NGAY}, ngày lành tháng tốt.\n" + TIN_CHU
            + "Chúng con khởi công xây dựng tại đây, thành tâm sắm lễ dâng lên trước án.\n"
            + "Cúi xin các Ngài chấp thuận, phù hộ cho công trình thuận lợi, an toàn, chóng hoàn thành." + KET},
        {"Nhập trạch (về nhà mới)",
            MO + "Con kính lạy Hoàng thiên Hậu thổ, Táo quân, Thổ địa, Long mạch cùng chư vị Tôn thần.\nCon kính lạy các cụ Tổ tiên nội ngoại.\n\n"
            + "Hôm nay là {NGAY}, ngày lành tháng tốt, gia đình chúng con dọn về nhà mới.\n" + TIN_CHU
            + "Thành tâm sắm lễ dâng lên, kính xin các Ngài chứng giám, cho phép gia đình được nhập trạch,"
            + " phù hộ cả nhà an cư lạc nghiệp, mạnh khỏe, làm ăn phát đạt." + KET},
        {"Khai trương cửa hàng",
            MO + "Con kính lạy Thần Tài, Thổ địa, Bản cảnh Thành hoàng cùng chư vị Tôn thần.\n\n"
            + "Hôm nay là {NGAY}, ngày lành tháng tốt, chúng con khai trương cửa hàng.\n" + TIN_CHU
            + "Thành tâm sắm lễ dâng lên. Cúi xin các Ngài phù hộ cho việc buôn bán hanh thông, khách hàng đông đúc, tài lộc dồi dào." + KET},
        {"Cúng đầy tháng cho bé",
            MO + "Con kính lạy Đức Ông, Đức Bà, mười hai Bà Mụ cùng chư vị Tiên Bà.\n\n"
            + "Hôm nay là {NGAY}.\n" + TIN_CHU
            + "Vợ chồng chúng con sinh được cháu ...... (tên bé), nay tròn một tháng tuổi, thành tâm sắm lễ dâng lên.\n"
            + "Cúi xin các Bà Mụ phù hộ cho cháu hay ăn chóng lớn, mạnh khỏe, ngoan ngoãn." + KET},
    };

    public static String fill(String text, String ngay, String ten, String diaChi) {
        return text.replace("{NGAY}", ngay)
            .replace("{TEN}", ten == null || ten.isEmpty() ? "......" : ten)
            .replace("{DIACHI}", diaChi == null || diaChi.isEmpty() ? "......" : diaChi);
    }
}
