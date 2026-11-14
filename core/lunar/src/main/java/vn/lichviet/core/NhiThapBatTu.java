package vn.lichviet.core;

/** 28 sao (Nhị thập bát tú): tên đầy đủ, ngũ hành/thất diệu, con vật tượng trưng, tốt xấu, ý nghĩa ngắn. */
public final class NhiThapBatTu {
    private NhiThapBatTu() {}

    public static final String[] HANH = {"Mộc", "Kim", "Thổ", "Nhật", "Nguyệt", "Hỏa", "Thủy"};
    public static final String[] CON_VAT = {"Giao (thuồng luồng)", "Long (rồng)", "Lạc (lạc đà)", "Thố (thỏ)", "Hồ (cáo)",
        "Hổ (cọp)", "Báo (beo)", "Giải (cua)", "Ngưu (trâu)", "Bức (dơi)", "Thử (chuột)", "Yến (chim én)", "Trư (heo)",
        "Du (rái cá)", "Lang (chó sói)", "Cẩu (chó)", "Trĩ (chim trĩ)", "Kê (gà)", "Ô (quạ)", "Hầu (khỉ)", "Viên (vượn)",
        "Hãn (chó rừng)", "Dương (dê)", "Chương (hoẵng)", "Mã (ngựa)", "Lộc (hươu)", "Xà (rắn)", "Dẫn (giun)"};
    private static final String[] Y_NGHIA = {
        "Tốt cho tạo tác, cưới hỏi, thi cử; kỵ chôn cất, sửa mộ.",
        "Kỵ cưới hỏi, xây dựng, chôn cất; nên làm việc nhỏ.",
        "Kỵ khởi công, xây dựng, cưới hỏi, xuất hành đường thủy.",
        "Tốt cho cưới hỏi, xây dựng, chôn cất, xuất hành.",
        "Kỵ khởi công, cưới hỏi, kiện tụng.",
        "Tốt cho khởi công, cưới hỏi, chôn cất, khai trương.",
        "Tốt cho xây dựng, khai trương, chôn cất, đào kênh.",
        "Tốt cho xây đắp, sửa chữa, cưới hỏi, kinh doanh; kỵ đi thuyền.",
        "Kỵ khởi công, cưới hỏi, xây dựng.",
        "Kỵ cưới hỏi, chôn cất, kiện tụng; nên làm việc nhỏ.",
        "Kỵ khởi công, cưới hỏi, khai trương.",
        "Kỵ xây dựng, đi thuyền, chôn cất.",
        "Tốt cho cưới hỏi, xây dựng, khai trương, chôn cất.",
        "Tốt cho cưới hỏi, xây dựng, chôn cất, khai trương, xuất hành.",
        "Kỵ khai trương, khởi công, đào ao giếng.",
        "Tốt cho cưới hỏi, xây dựng, khai trương, may mặc.",
        "Tốt cho khởi công, cưới hỏi, chôn cất, kinh doanh.",
        "Kỵ cưới hỏi, khởi công, xây cất.",
        "Tốt cho khởi công, cưới hỏi, đào kênh, chôn cất.",
        "Kỵ chôn cất, khởi công, kiện tụng.",
        "Tốt cho khởi công, xây dựng, kinh doanh, xuất hành.",
        "Tốt cho khởi công, xây dựng, thi cử; kỵ chôn cất.",
        "Kỵ cưới hỏi, chôn cất, xây cất.",
        "Kỵ cưới hỏi, chôn cất, khai trương.",
        "Kỵ cưới hỏi, xây dựng, khai trương.",
        "Tốt cho cưới hỏi, khai trương, xây dựng, chôn cất.",
        "Kỵ cưới hỏi, xây cất, chôn cất.",
        "Tốt cho khởi công, cưới hỏi, chôn cất, khai trương, xuất hành."
    };

    public static int index(String tu) {
        for (int i = 0; i < 28; i++) if (DayInfo.TU[i].equals(tu)) return i;
        return -1;
    }

    /** "Đê Thổ Lạc" */
    public static String fullName(int i) {
        String vat = CON_VAT[i];
        return DayInfo.TU[i] + " " + HANH[i % 7] + " " + vat.substring(0, vat.indexOf(' '));
    }

    public static String meaning(int i) {
        return Y_NGHIA[i];
    }
}
