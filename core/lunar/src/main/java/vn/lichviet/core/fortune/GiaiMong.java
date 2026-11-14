package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;

/** Từ điển giải mộng dân gian (mang tính tham khảo, giải trí). */
public final class GiaiMong {
    private GiaiMong() {}

    public static final String[][] DATA = {
        {"Rắn", "Mơ thấy rắn thường báo hiệu có người theo đuổi hoặc tài lộc đến; rắn cắn có thể là điềm có tin vui về tình cảm."},
        {"Chó", "Chó thân thiện báo có bạn bè giúp đỡ; chó cắn nhắc bạn đề phòng người thân quen."},
        {"Mèo", "Mèo là điềm cần cẩn trọng với lời nói ngọt ngào, dễ bị lợi dụng."},
        {"Cá", "Cá bơi tung tăng là điềm tài lộc, công việc thuận lợi; bắt được cá to là có lộc."},
        {"Nước", "Nước trong báo may mắn, sức khỏe tốt; nước đục nhắc bạn giữ gìn sức khỏe và tâm trạng."},
        {"Lửa", "Lửa cháy sáng là dấu hiệu công việc hưng thịnh; cháy nhà theo dân gian lại là điềm phát đạt."},
        {"Mưa", "Mưa nhẹ báo điều tốt lành, phiền muộn được gột rửa; mưa bão nhắc chuẩn bị cho thay đổi."},
        {"Tiền", "Nhặt được tiền là điềm có thể hao tài nhỏ; cho người khác tiền là sắp có lộc."},
        {"Vàng", "Mơ thấy vàng báo hiệu cơ hội tốt, nhưng cần tỉnh táo trước những lời hứa hẹn."},
        {"Người chết", "Thường là điềm may, tượng trưng cho kết thúc chuyện cũ và khởi đầu mới."},
        {"Đám cưới", "Dự đám cưới báo có tin vui; chính mình cưới có thể là sắp có thay đổi lớn."},
        {"Đám tang", "Theo dân gian là điềm lành, công việc sắp hanh thông."},
        {"Răng rụng", "Răng rụng nhắc bạn quan tâm sức khỏe người thân lớn tuổi và chính mình."},
        {"Máu", "Thấy máu theo dân gian là điềm tài lộc; nhưng cũng nhắc bạn nghỉ ngơi đầy đủ."},
        {"Bay", "Bay lượn trên cao thể hiện mong muốn tự do, sắp thoát khỏi áp lực."},
        {"Rơi", "Rơi từ trên cao phản ánh lo âu, thiếu an toàn; nên sắp xếp lại công việc."},
        {"Bị đuổi", "Bị rượt đuổi cho thấy bạn đang né tránh một vấn đề cần đối diện."},
        {"Thi cử", "Mơ đi thi cho thấy áp lực, mong muốn được công nhận; nên chuẩn bị kỹ việc sắp tới."},
        {"Em bé", "Em bé tượng trưng cho khởi đầu mới, hy vọng và niềm vui."},
        {"Mang thai", "Mơ mang thai báo sắp có dự định mới hình thành, ý tưởng mới."},
        {"Nhà", "Nhà mới, rộng rãi là điềm thăng tiến; nhà đổ nát nhắc bạn chăm lo gia đình."},
        {"Xe", "Lái xe suôn sẻ báo việc thuận lợi; hỏng xe nhắc kiểm tra kế hoạch."},
        {"Tàu thuyền", "Thuyền xuôi dòng là điềm may, công việc trôi chảy; thuyền lật cần cẩn trọng."},
        {"Máy bay", "Báo có chuyến đi xa hoặc cơ hội mới sắp tới."},
        {"Hoa", "Hoa nở là điềm vui về tình cảm; hoa héo nhắc giữ gìn các mối quan hệ."},
        {"Cây", "Cây xanh tốt báo sức khỏe và sự nghiệp phát triển; cây đổ cần phòng biến cố."},
        {"Trái cây", "Hái quả chín là điềm thu hoạch, gặt hái thành quả."},
        {"Gạo", "Gạo đầy là điềm no đủ, gia đạo ấm êm."},
        {"Ma", "Gặp ma thường do căng thẳng, lo nghĩ; nên thư giãn, ngủ đủ."},
        {"Phật", "Thấy Phật hoặc chùa chiền là điềm lành, được che chở, tâm an."},
        {"Rồng", "Rồng là điềm đại cát, công danh thăng tiến."},
        {"Hổ", "Hổ báo hiệu có người quyền thế giúp đỡ hoặc thử thách lớn cần can đảm."},
        {"Chuột", "Chuột nhắc đề phòng hao hụt nhỏ, tiểu nhân quấy phá."},
        {"Gà", "Gà gáy là điềm có tin vui; gà đẻ trứng là sắp có lộc."},
        {"Trâu bò", "Trâu bò khỏe mạnh là điềm làm ăn chăm chỉ, có của ăn của để."},
        {"Ngựa", "Ngựa phi là điềm thành công nhanh chóng, có tin tốt từ xa."},
        {"Chim", "Chim bay về nhà báo có khách quý hoặc tin vui."},
        {"Ong", "Ong báo hiệu công việc bận rộn nhưng có kết quả ngọt ngào."},
        {"Nhện", "Nhện giăng tơ báo có người lạ đến hoặc tin tức mới."},
        {"Biển", "Biển êm là tâm trạng thư thái; sóng lớn báo thử thách sắp tới."},
        {"Núi", "Leo núi lên đỉnh là điềm đạt mục tiêu; tuột dốc cần kiên nhẫn hơn."},
        {"Cầu", "Đi qua cầu là vượt qua khó khăn, chuyển sang giai đoạn mới."},
        {"Mặt trời", "Mặt trời rực rỡ báo vận may, danh tiếng và sức khỏe tốt."},
        {"Mặt trăng", "Trăng tròn là điềm viên mãn, tình cảm hạnh phúc."},
        {"Ngôi sao", "Thấy sao sáng là điềm hy vọng, ước mơ có thể thành."},
        {"Bão", "Bão tố phản ánh xáo trộn cảm xúc; giữ bình tĩnh sẽ qua."},
        {"Động đất", "Báo hiệu thay đổi lớn trong cuộc sống, nên chuẩn bị tâm lý."},
        {"Lũ lụt", "Lũ lụt cho thấy cảm xúc dồn nén; nên chia sẻ với người tin cậy."},
        {"Người yêu cũ", "Thường phản ánh ký ức chưa khép lại; là lúc nên nhìn về phía trước."},
        {"Cãi nhau", "Cãi vã trong mơ nhắc bạn giải tỏa mâu thuẫn ngoài đời."},
        {"Khóc", "Khóc trong mơ thường là điềm vui, nỗi buồn được giải tỏa."},
        {"Cười", "Cười lớn trong mơ có thể báo một chuyện nhỏ không như ý."},
        {"Trộm", "Bị trộm nhắc giữ gìn tài sản, cẩn trọng chuyện tiền bạc."},
        {"Lạc đường", "Lạc đường phản ánh đang phân vân lựa chọn; nên xác định rõ mục tiêu."},
        {"Bệnh viện", "Nhắc bạn quan tâm sức khỏe, nghỉ ngơi hợp lý."},
        {"Trường học", "Trở lại trường học cho thấy bạn đang học bài học mới trong cuộc sống."},
        {"Quần áo mới", "Mặc đồ mới là điềm có thay đổi tốt, được người khác chú ý."},
        {"Giày dép", "Mất giày dép nhắc cẩn trọng khi đi xa; giày mới là có cơ hội mới."},
        {"Gương", "Soi gương là lúc tự nhìn lại bản thân; gương vỡ cần cẩn thận lời nói."},
        {"Chìa khóa", "Chìa khóa báo bạn sắp tìm ra lời giải cho vấn đề."},
        {"Điện thoại", "Nhận cuộc gọi báo có tin tức từ người lâu ngày không gặp."},
        {"Thầy giáo", "Gặp thầy cô là điềm có người hướng dẫn, giúp đỡ."},
        {"Ông bà tổ tiên", "Ông bà về báo mộng thường là lời nhắc giữ gìn gia đạo, nhớ ngày giỗ."},
        {"Bánh chưng", "Báo một dịp sum họp gia đình sắp tới."},
        {"Tóc", "Cắt tóc là dấu hiệu muốn thay đổi; tóc dài mượt là sức khỏe tốt."},
    };

    public static List<String[]> search(String q) {
        String k = Numerology.plain(q).trim();
        List<String[]> out = new ArrayList<>();
        for (String[] e : DATA) {
            if (k.isEmpty() || Numerology.plain(e[0]).contains(k) || Numerology.plain(e[1]).contains(k)) out.add(e);
        }
        return out;
    }
}
