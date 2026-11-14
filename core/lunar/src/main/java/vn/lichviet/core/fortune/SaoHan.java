package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.DayInfo;

/** Sao chiếu mệnh (Cửu Diệu), Tam tai, Kim lâu, Hoang ốc theo tuổi mụ. */
public final class SaoHan {
    private SaoHan() {}

    public static final String[] SAO = {"La Hầu", "Thổ Tú", "Thủy Diệu", "Thái Bạch", "Thái Dương", "Vân Hớn", "Kế Đô", "Thái Âm", "Mộc Đức"};
    /** Thứ tự sao bắt đầu từ tuổi 10 (rồi 19, 28...) cho nam và nữ. */
    private static final int[] NAM = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] NU = {6, 5, 8, 7, 1, 0, 4, 3, 2};
    /** 2 = sao tốt, 1 = sao trung, 0 = sao xấu (phân loại phổ biến: hoatieu.vn, mytour.vn). */
    public static final int[] SAO_TOT = {0, 1, 1, 0, 2, 1, 0, 2, 2};
    /** Tính chất sao, tóm lược theo vnexpress.net, hoatieu.vn, ngaydep.com. */
    public static final String[] SAO_Y = {
        "Hung tinh, còn gọi Khẩu thiệt tinh: chủ thị phi, kiện tụng, phiền muộn, bệnh về máu huyết; kỵ nhất với nam giới.",
        "Chủ dễ gặp tiểu nhân, đi xa không may, hay mộng mị; nửa cuối năm thuận hơn.",
        "Nam: phúc lộc, đi xa có lợi. Nữ: bất lợi hơn, dễ khẩu thiệt thị phi, nên tránh sông nước.",
        "Chủ mọi việc không như ý, hao tài; đại kỵ với nữ giới, nam đỡ hơn.",
        "Cát tinh. Nam: làm ăn phát đạt, thăng tiến. Nữ: có chút tai ương.",
        "Nữ: nhiều tai ương, đề phòng huyết quang. Nam: kiện tụng, thị phi, gia đạo bất an.",
        "Cả nam và nữ đều kỵ, nữ nặng hơn; riêng nữ có thai lại may mắn.",
        "Cát tinh. Nam: đi lại hanh thông, được giúp đỡ về tiền bạc. Nữ: có tiền tài nhưng sinh nở bất lợi.",
        "Cát tinh: may mắn trong công việc, hôn nhân hòa hợp; nữ đề phòng huyết quang, nam đề phòng bệnh mắt.",
    };
    /** Ngày cúng âm lịch hằng tháng, số nến, hướng lạy (hoatieu.vn, luatduonggia.vn). */
    public static final String[] CUNG = {
        "Ngày 8 âm lịch hằng tháng, 9 ngọn nến, hướng Bắc",
        "Ngày 19 âm lịch hằng tháng, 5 ngọn nến, hướng Tây",
        "Ngày 21 âm lịch hằng tháng, 7 ngọn nến, hướng Bắc",
        "Ngày 15 âm lịch hằng tháng, 8 ngọn nến, hướng Tây",
        "Ngày 27 âm lịch hằng tháng, 12 ngọn nến, hướng Đông",
        "Ngày 29 âm lịch hằng tháng, 15 ngọn nến",
        "Ngày 18 âm lịch hằng tháng, 20 hoặc 21 ngọn nến tùy sách, hướng Tây",
        "Ngày 26 âm lịch hằng tháng, 7 ngọn nến, hướng Tây",
        "Ngày 25 âm lịch hằng tháng, 20 ngọn nến, hướng Đông",
    };

    /** Tám hạn. Thứ tự vòng của nam và của nữ (dichhocungdung.com). */
    public static final String[] HAN = {"Huỳnh Tuyền", "Tam Kheo", "Ngũ Mộ", "Thiên Tinh", "Toán Tận", "Thiên La", "Địa Võng", "Diêm Vương"};
    private static final int[] HAN_NAM = {0, 1, 2, 3, 4, 5, 6, 7};
    private static final int[] HAN_NU = {4, 3, 2, 1, 0, 7, 6, 5};
    /** Tính chất hạn, tóm lược theo masterphongthuy.vn, hoatieu.vn, dichhocungdung.com. */
    public static final String[] HAN_Y = {
        "Đại hạn: chủ bệnh nặng, hao tài; nên tránh bảo lãnh cho người khác.",
        "Tiểu hạn: chú ý bệnh khớp, chấn thương tay chân.",
        "Tiểu hạn: dễ hao tiền tốn của.",
        "Hạn xấu: dễ thị phi, kiện tụng; chú ý ăn uống.",
        "Đại hạn: dễ mất tiền bất ngờ, đặc biệt bất lợi với nam.",
        "Hạn xấu: chủ sức khỏe, tâm lý bất an; dễ bất hòa vợ chồng.",
        "Hạn xấu: dễ gặp thị phi, bị hiểu lầm, rắc rối pháp lý.",
        "Hạn xấu: bất lợi cho phụ nữ, nhất là khi mang thai, sinh nở.",
    };

    /** Hạn theo tuổi mụ: mỗi chục tuổi khởi một hạn, mỗi năm lẻ đếm tiếp (10 tuổi: nam Huỳnh Tuyền, nữ Toán Tận). */
    public static int han(int tuoiMu, boolean male) {
        int tens = tuoiMu / 10, units = tuoiMu % 10;
        int i = Math.floorMod(tens - 1 + units, 8);
        return male ? HAN_NAM[i] : HAN_NU[i];
    }

    public static int sao(int tuoiMu, boolean male) {
        int i = Math.floorMod(tuoiMu - 10, 9);
        return male ? NAM[i] : NU[i];
    }

    /** Ba năm tam tai (chi năm) của tuổi có chi [chi]. */
    public static int[] tamTai(int chi) {
        switch (chi % 4) {
            case 0: return new int[]{2, 3, 4};   // Thân Tý Thìn -> Dần Mão Thìn
            case 1: return new int[]{11, 0, 1};  // Tỵ Dậu Sửu -> Hợi Tý Sửu
            case 2: return new int[]{8, 9, 10};  // Dần Ngọ Tuất -> Thân Dậu Tuất
            default: return new int[]{5, 6, 7};  // Hợi Mão Mùi -> Tỵ Ngọ Mùi
        }
    }

    public static boolean isTamTai(int chiTuoi, int chiNam) {
        for (int c : tamTai(chiTuoi)) if (c == chiNam) return true;
        return false;
    }

    public static String tamTaiText(int chi) {
        int[] t = tamTai(chi);
        return DayInfo.CHI[t[0]] + ", " + DayInfo.CHI[t[1]] + ", " + DayInfo.CHI[t[2]];
    }

    /** Kim lâu: null nếu không phạm. */
    public static String kimLau(int tuoiMu) {
        switch (tuoiMu % 9) {
            case 1: return "Kim Lâu Thân (hại bản thân)";
            case 3: return "Kim Lâu Thê (hại vợ)";
            case 6: return "Kim Lâu Tử (hại con)";
            case 8: return "Kim Lâu Lục Súc (hại vật nuôi, tài sản)";
            default: return null;
        }
    }

    public static final String[] HOANG_OC = {"Nhất Cát", "Nhì Nghi", "Tam Địa Sát", "Tứ Tấn Tài", "Ngũ Thọ Tử", "Lục Hoang Ốc"};

    public static int hoangOc(int tuoiMu) {
        int tens = tuoiMu / 10, units = tuoiMu % 10;
        if (tens == 0) return 0;
        return Math.floorMod(tens - 1 + units, 6);
    }

    public static boolean hoangOcXau(int i) { return i == 2 || i == 4 || i == 5; }

    /** Các mốc tuổi gặp sao xấu nặng (La Hầu, Kế Đô, Thái Bạch) từ 10 đến 80 tuổi. */
    public static List<int[]> badYears(boolean male) {
        List<int[]> out = new ArrayList<>();
        for (int t = 10; t <= 80; t++) {
            int s = sao(t, male);
            if (s == 0 || s == 6 || s == 3) out.add(new int[]{t, s});
        }
        return out;
    }
}
