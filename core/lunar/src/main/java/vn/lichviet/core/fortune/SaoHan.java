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
    public static final int[] SAO_TOT = {0, 0, 1, 0, 2, 0, 0, 2, 2}; // 0 xấu, 1 trung, 2 tốt
    public static final String[] SAO_Y = {
        "Sao xấu, chủ khẩu thiệt, kiện tụng, tai nạn; nam kỵ hơn nữ. Nên giữ lời ăn tiếng nói, cẩn trọng đi lại.",
        "Sao xấu, chủ tiểu nhân, đi xa không thuận, chăn nuôi hao. Nên thận trọng khi ký kết, đầu tư.",
        "Sao trung bình, tốt cho người làm ăn buôn bán; kỵ sông nước, dễ có chuyện thị phi.",
        "Sao xấu, hao tài tốn của, dễ đau ốm. Nên tiết kiệm, tránh vay mượn và làm ăn lớn.",
        "Sao tốt, đặc biệt với nam: công danh, tài lộc thuận lợi, gia đạo yên vui.",
        "Sao xấu vừa, dễ ốm đau, khẩu thiệt, sinh nở khó. Nên giữ sức khỏe, tránh tranh cãi.",
        "Sao xấu, nữ kỵ hơn nam: dễ có chuyện buồn, ốm đau, đi xa nhiều trắc trở.",
        "Sao tốt, đặc biệt với nữ: tài lộc, hôn nhân, sinh nở thuận lợi; kỵ tháng 11 âm.",
        "Sao tốt, chủ hôn nhân, gia đạo hòa thuận, công việc tiến triển.",
    };
    /** Ngày cúng giải hạn hằng tháng (âm lịch) và hướng cúng. */
    public static final String[] CUNG = {
        "Mùng 8 âm hằng tháng, hướng Bắc, 9 ngọn nến",
        "Ngày 19 âm hằng tháng, hướng Tây, 5 ngọn nến",
        "Ngày 21 âm hằng tháng, hướng Bắc, 7 ngọn nến",
        "Ngày 15 âm hằng tháng, hướng Tây, 8 ngọn nến",
        "Ngày 27 âm hằng tháng, hướng Đông, 12 ngọn nến",
        "Ngày 29 âm hằng tháng, hướng Nam, 15 ngọn nến",
        "Ngày 18 âm hằng tháng, hướng Tây, 21 ngọn nến",
        "Ngày 26 âm hằng tháng, hướng Tây, 7 ngọn nến",
        "Ngày 25 âm hằng tháng, hướng Đông, 20 ngọn nến",
    };

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
