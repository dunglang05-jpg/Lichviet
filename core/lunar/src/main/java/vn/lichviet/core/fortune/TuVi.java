package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vn.lichviet.core.LunarCalendar;

/**
 * Lá số Tử Vi Đẩu Số: cục, cung Mệnh, Thân, an sao, miếu hãm, Tứ Hóa, Tuần Triệt, đại vận,
 * hạn năm, sao lưu, cách cục. Chuyển từ thư viện mã nguồn mở Nhật Nguyệt
 * (github.com/Nhat-Nguyet/nhatnguyet-lunar-ziwei-bazi-numerology, giấy phép MIT), cách an
 * mặc định theo lối phổ biến ở Việt Nam (Tử Vi Đẩu Số Tân Biên). Kiểm bằng bộ ca kiểm của
 * thư viện ấy (CC BY 4.0).
 *
 * Quy ước: vị trí 0 là Tý … 11 là Hợi; can 0 là Giáp; giờ là chỉ số chi giờ sinh.
 */
public final class TuVi {
    private TuVi() {}

    public static final String[] CAN = {"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"};
    public static final String[] CHI = {"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};

    static int mod(int a, int b) { int m = a % b; return m < 0 ? m + b : m; }

    // ------------------------------------------------------------------ bảng tra

    /** Tên 12 cung, offset nghịch từ Mệnh. */
    public static final String[] TEN_CUNG = {"Mệnh", "Huynh Đệ", "Phu Thê", "Tử Tức", "Tài Bạch", "Tật Ách",
        "Thiên Di", "Nô Bộc", "Quan Lộc", "Điền Trạch", "Phúc Đức", "Phụ Mẫu"};

    static final int[] LOC_TON = {2, 3, 5, 6, 5, 6, 8, 9, 11, 0};
    static final int[] THIEN_KHOI = {1, 0, 11, 11, 1, 0, 6, 6, 3, 3};
    static final int[][] TRIET = {{8, 9}, {6, 7}, {4, 5}, {2, 3}, {0, 1}, {8, 9}, {6, 7}, {4, 5}, {2, 3}, {0, 1}};
    static final String[] VONG_TRUONG_SINH = {"Tràng Sinh", "Mộc Dục", "Quan Đới", "Lâm Quan", "Đế Vượng", "Suy",
        "Bệnh", "Tử", "Mộ", "Tuyệt", "Thai", "Dưỡng"};
    static final String[] VONG_THAI_TUE = {"Thái Tuế", "Thiếu Dương", "Tang Môn", "Thiếu Âm", "Quan Phù", "Tử Phù",
        "Tuế Phá", "Long Đức", "Bạch Hổ", "Phúc Đức", "Điếu Khách", "Trực Phù"};
    static final String[] VONG_BAC_SY = {"Bác Sỹ", "Lực Sĩ", "Thanh Long", "Tiểu Hao", "Tướng Quân", "Tấu Thư",
        "Phi Liêm", "Hỷ Thần", "Bệnh Phù", "Đại Hao", "Phục Binh", "Quan Phủ"};

    /** Tứ Hóa theo can: Lộc, Quyền, Khoa, Kỵ. */
    public static final String[][] TU_HOA = {
        {"Liêm Trinh", "Phá Quân", "Vũ Khúc", "Thái Dương"},
        {"Thiên Cơ", "Thiên Lương", "Tử Vi", "Thái Âm"},
        {"Thiên Đồng", "Thiên Cơ", "Văn Xương", "Liêm Trinh"},
        {"Thái Âm", "Thiên Đồng", "Thiên Cơ", "Cự Môn"},
        {"Tham Lang", "Thái Âm", "Hữu Bật", "Thiên Cơ"},
        {"Vũ Khúc", "Tham Lang", "Thiên Lương", "Văn Khúc"},
        {"Thái Dương", "Vũ Khúc", "Thiên Đồng", "Thái Âm"},
        {"Cự Môn", "Thái Dương", "Văn Khúc", "Văn Xương"},
        {"Thiên Lương", "Tử Vi", "Tả Phù", "Vũ Khúc"},
        {"Phá Quân", "Cự Môn", "Thái Âm", "Tham Lang"},
    };
    public static final String[] HOA = {"Hóa Lộc", "Hóa Quyền", "Hóa Khoa", "Hóa Kỵ"};

    public static final String[] CHINH_TINH = {"Tử Vi", "Thiên Cơ", "Thái Dương", "Vũ Khúc", "Thiên Đồng", "Liêm Trinh",
        "Thiên Phủ", "Thái Âm", "Tham Lang", "Cự Môn", "Thiên Tướng", "Thiên Lương", "Thất Sát", "Phá Quân"};
    static final String[] PHU_TINH_GOC = {"Lộc Tồn", "Kình Dương", "Đà La", "Thiên Khôi", "Thiên Việt",
        "Tả Phù", "Hữu Bật", "Văn Xương", "Văn Khúc", "Địa Không", "Địa Kiếp", "Thiên Mã", "Đào Hoa"};
    static final String[] PHU_TINH_MOI = {"Tam Thai", "Bát Tọa", "Ân Quang", "Thiên Quý", "Thiên Quan", "Thiên Phúc",
        "Thiên Tài", "Thiên Thọ", "Thiên Khốc", "Thiên Hư", "Đẩu Quân"};
    static final String[] PHU_TINH_THEM = {"Hỏa Tinh", "Linh Tinh", "Long Trì", "Phượng Các", "Thiên Đức", "Nguyệt Đức",
        "Thiên Hình", "Thiên Diêu", "Thiên Y", "Hồng Loan", "Thiên Hỷ", "Quốc Ấn", "Đường Phù", "Thiên Giải", "Địa Giải",
        "Giải Thần", "Thai Phụ", "Phong Cáo", "Thiên Thương", "Thiên Sứ", "Thiên La", "Địa Võng", "Cô Thần", "Quả Tú",
        "Kiếp Sát", "Phá Toái", "Hoa Cái", "Lưu Hà", "Thiên Trù", "Lưu Niên Văn Tinh", "Thiên Không"};
    static final Set<String> SAT_TINH = new HashSet<>(Arrays.asList("Kình Dương", "Đà La", "Địa Không", "Địa Kiếp", "Hỏa Tinh", "Linh Tinh"));
    static final Set<String> CAT_TINH = new HashSet<>(Arrays.asList("Tả Phù", "Hữu Bật", "Văn Xương", "Văn Khúc",
        "Thiên Khôi", "Thiên Việt", "Lộc Tồn", "Thiên Mã"));

    static final Map<String, String> DIGNITY = new LinkedHashMap<>();
    static final Map<String, String> DIG_LABEL = new LinkedHashMap<>();
    static {
        String[][] d = {
            {"Tử Vi", "B Đ M B V M M Đ M B V B"}, {"Liêm Trinh", "V Đ V H M H V Đ V H M H"},
            {"Thiên Đồng", "V H M Đ H Đ H H M H H Đ"}, {"Vũ Khúc", "V M V Đ M H V M V Đ M H"},
            {"Thái Dương", "H Đ V V V M M Đ H H H H"}, {"Thiên Cơ", "Đ Đ H M M V Đ Đ V M M H"},
            {"Thiên Phủ", "M B M B V Đ M Đ M B V Đ"}, {"Thái Âm", "V Đ H H H H H Đ V M M M"},
            {"Tham Lang", "H M Đ H V H H M Đ H V H"}, {"Cự Môn", "V H V M H H V H Đ M H Đ"},
            {"Thiên Tướng", "V Đ M H V Đ V Đ M H V Đ"}, {"Thiên Lương", "V Đ V V M H M Đ V H M H"},
            {"Thất Sát", "M Đ M H H V M Đ M H H V"}, {"Phá Quân", "M V H H Đ H M V H H Đ H"},
        };
        for (String[] r : d) DIGNITY.put(r[0], r[1].replace(" ", ""));
        DIG_LABEL.put("M", "Miếu"); DIG_LABEL.put("V", "Vượng"); DIG_LABEL.put("Đ", "Đắc"); DIG_LABEL.put("B", "Bình"); DIG_LABEL.put("H", "Hãm");
    }

    /** Ký hiệu miếu hãm (M V Đ B H) của chính tinh tại vị trí; null nếu không phải chính tinh. */
    static String dig(String ten, int pos) {
        String s = DIGNITY.get(ten);
        return s == null ? null : String.valueOf(s.charAt(mod(pos, 12)));
    }

    // nạp âm 30 cặp
    static final Object[][] NAP_AM = {
        {0, 0, "Kim", "Hải Trung Kim"}, {2, 2, "Hỏa", "Lư Trung Hỏa"}, {4, 4, "Mộc", "Đại Lâm Mộc"}, {6, 6, "Thổ", "Lộ Bàng Thổ"},
        {8, 8, "Kim", "Kiếm Phong Kim"}, {0, 10, "Hỏa", "Sơn Đầu Hỏa"}, {2, 0, "Thủy", "Giản Hạ Thủy"}, {4, 2, "Thổ", "Thành Đầu Thổ"},
        {6, 4, "Kim", "Bạch Lạp Kim"}, {8, 6, "Mộc", "Dương Liễu Mộc"}, {0, 8, "Thủy", "Tuyền Trung Thủy"}, {2, 10, "Thổ", "Ốc Thượng Thổ"},
        {4, 0, "Hỏa", "Tích Lịch Hỏa"}, {6, 2, "Mộc", "Tùng Bách Mộc"}, {8, 4, "Thủy", "Trường Lưu Thủy"}, {0, 6, "Kim", "Sa Trung Kim"},
        {2, 8, "Hỏa", "Sơn Hạ Hỏa"}, {4, 10, "Mộc", "Bình Địa Mộc"}, {6, 0, "Thổ", "Bích Thượng Thổ"}, {8, 2, "Kim", "Kim Bạc Kim"},
        {0, 4, "Hỏa", "Phú Đăng Hỏa"}, {2, 6, "Thủy", "Thiên Hà Thủy"}, {4, 8, "Thổ", "Đại Trạch Thổ"}, {6, 10, "Kim", "Thoa Xuyến Kim"},
        {8, 0, "Mộc", "Tang Đố Mộc"}, {0, 2, "Thủy", "Đại Khê Thủy"}, {2, 4, "Thổ", "Sa Trung Thổ"}, {4, 6, "Hỏa", "Thiên Thượng Hỏa"},
        {6, 8, "Mộc", "Thạch Lựu Mộc"}, {8, 10, "Thủy", "Đại Hải Thủy"},
    };

    /** [hành, tên nạp âm] của can chi. */
    public static String[] napAm(int can, int chi) {
        can = mod(can, 10); chi = mod(chi, 12);
        for (Object[] r : NAP_AM) {
            int c = (Integer) r[0], h = (Integer) r[1];
            if ((c == can && h == chi) || (mod(c + 1, 10) == can && mod(h + 1, 12) == chi)) return new String[]{(String) r[2], (String) r[3]};
        }
        return new String[]{"", ""};
    }

    static int cucSo(String hanh) {
        switch (hanh) { case "Thủy": return 2; case "Mộc": return 3; case "Kim": return 4; case "Thổ": return 5; case "Hỏa": return 6; default: return 0; }
    }
    static String tenCuc(int so) {
        switch (so) { case 2: return "Thủy Nhị Cục"; case 3: return "Mộc Tam Cục"; case 4: return "Kim Tứ Cục"; case 5: return "Thổ Ngũ Cục"; default: return "Hỏa Lục Cục"; }
    }
    static int trangSinh(int cuc) {
        switch (cuc) { case 6: return 2; case 4: return 5; case 3: return 11; default: return 8; }
    }

    // ------------------------------------------------------------------ kiểu kết quả

    public static final class Sao {
        public final String ten, loai, nhom, mieuVuong, tuHoa;
        Sao(String ten, String loai, String nhom, String mieuVuong, String tuHoa) {
            this.ten = ten; this.loai = loai; this.nhom = nhom; this.mieuVuong = mieuVuong; this.tuHoa = tuHoa;
        }
        public boolean chinh() { return "chinh-tinh".equals(loai); }
    }

    public static final class Cung {
        public final int viTri;
        public final String chi, canChi, tenCung;
        public final boolean laThan, tuan, triet;
        public final List<Sao> sao = new ArrayList<>();
        Cung(int viTri, String canChi, String tenCung, boolean laThan, boolean tuan, boolean triet) {
            this.viTri = viTri; this.chi = CHI[viTri]; this.canChi = canChi; this.tenCung = tenCung;
            this.laThan = laThan; this.tuan = tuan; this.triet = triet;
        }
        public List<Sao> chinhTinh() { List<Sao> o = new ArrayList<>(); for (Sao s : sao) if (s.chinh()) o.add(s); return o; }
        public boolean co(String ten) { for (Sao s : sao) if (s.ten.equals(ten)) return true; return false; }
    }

    public static final class LaSo {
        public String hoTen, gioiTinh, gioSinh, canChiNam, amDuongMenh, tenCuc, banMenh;
        public int ngayDuong, thangDuong, namDuong, amNgay, amThang, amNam, cuc, cungMenh, cungThan, gio;
        public boolean amNhuan;
        public final Cung[] cung = new Cung[12];
        public boolean nam() { return "Nam".equals(gioiTinh); }
        public int viTriSao(String ten) { for (Cung c : cung) if (c.co(ten)) return c.viTri; return -1; }
        public String chinhTinhCua(int pos) {
            StringBuilder b = new StringBuilder();
            for (Sao s : cung[mod(pos, 12)].sao) if (s.chinh()) { if (b.length() > 0) b.append(", "); b.append(s.ten); }
            return b.toString();
        }
        Set<String> saoTai(int pos) { Set<String> o = new HashSet<>(); for (Sao s : cung[mod(pos, 12)].sao) o.add(s.ten); return o; }
        List<String> chinhTai(int pos) { List<String> o = new ArrayList<>(); for (Sao s : cung[mod(pos, 12)].sao) if (s.chinh()) o.add(s.ten); return o; }
        Set<String> tamPhuongTuChinh(int pos) { Set<String> o = new HashSet<>(); for (int d : new int[]{0, 4, 6, 8}) o.addAll(saoTai(pos + d)); return o; }
    }

    // ------------------------------------------------------------------ lập lá số

    /** Chỉ số chi giờ theo giờ đồng hồ (23h–0h59 là Tý). */
    public static int chiGio(int hour) { return mod((hour + 1) / 2, 12); }

    /**
     * Lập lá số. [gio] là chỉ số chi giờ sinh (0 Tý … 11 Hợi); ngày âm lấy theo đúng ngày dương đã nhập
     * (giờ Việt Nam UTC+7) kể cả khi sinh từ 23h.
     */
    public static LaSo lap(String hoTen, boolean nam, int dd, int mm, int yyyy, int gio) {
        if (gio < 0 || gio > 11) throw new IllegalArgumentException("giờ sinh phải trong 0–11");
        int[] lu = LunarCalendar.solarToLunar(dd, mm, yyyy);
        LaSo ls = new LaSo();
        ls.hoTen = hoTen == null ? "" : hoTen; ls.gioiTinh = nam ? "Nam" : "Nữ";
        ls.ngayDuong = dd; ls.thangDuong = mm; ls.namDuong = yyyy;
        ls.amNgay = lu[0]; ls.amThang = lu[1]; ls.amNam = lu[2]; ls.amNhuan = lu.length > 3 && lu[3] == 1;
        ls.gio = gio; ls.gioSinh = CHI[gio];
        int thang = ls.amThang, ngay = ls.amNgay;
        int can = mod(ls.amNam - 4, 10), chi = mod(ls.amNam - 4, 12);
        int amDuongCan = can % 2 != 0 ? -1 : 1;
        int adnn = (nam ? 1 : -1) * amDuongCan; // +1 Dương Nam, Âm Nữ
        ls.amDuongMenh = (amDuongCan < 0 ? "Âm" : "Dương") + " " + ls.gioiTinh;
        ls.canChiNam = CAN[can] + " " + CHI[chi];

        int cungThang = mod(2 + thang - 1, 12);
        int menh = mod(cungThang - gio, 12), than = mod(cungThang + gio, 12);
        ls.cungMenh = menh; ls.cungThan = than;
        int canDan = mod(2 * can + 2, 10);
        int canMenh = mod(canDan + mod(menh - 2, 12), 10);
        int cuc = cucSo(napAm(canMenh, menh)[0]);
        ls.cuc = cuc; ls.tenCuc = tenCuc(cuc);
        ls.banMenh = napAm(can, chi)[1];

        Map<String, Integer> p = new LinkedHashMap<>();
        // Tử Vi
        int cungDan = 2, c = cuc;
        while (c < ngay) { c += cuc; cungDan++; }
        int lech = c - ngay;
        if (lech % 2 != 0) lech = -lech;
        int tuVi = mod(cungDan + lech, 12);
        int[] buocTv = {0, -1, -3, -4, -5, -8};
        String[] chomTv = {"Tử Vi", "Thiên Cơ", "Thái Dương", "Vũ Khúc", "Thiên Đồng", "Liêm Trinh"};
        for (int i = 0; i < 6; i++) p.put(chomTv[i], mod(tuVi + buocTv[i], 12));
        int phu = mod(4 - tuVi, 12);
        int[] buocTp = {0, 1, 2, 3, 4, 5, 6, 10};
        String[] chomTp = {"Thiên Phủ", "Thái Âm", "Tham Lang", "Cự Môn", "Thiên Tướng", "Thiên Lương", "Thất Sát", "Phá Quân"};
        for (int i = 0; i < 8; i++) p.put(chomTp[i], mod(phu + buocTp[i], 12));

        int loc = LOC_TON[can];
        p.put("Lộc Tồn", loc); p.put("Kình Dương", mod(loc + 1, 12)); p.put("Đà La", mod(loc - 1, 12));
        int khoi = THIEN_KHOI[can];
        p.put("Thiên Khôi", khoi); p.put("Thiên Việt", mod(8 - khoi, 12));
        int taPhu = mod(4 + thang - 1, 12), huuBat = mod(2 - taPhu, 12);
        p.put("Tả Phù", taPhu); p.put("Hữu Bật", huuBat);
        int vanKhuc = mod(4 + gio, 12), vanXuong = mod(2 - vanKhuc, 12);
        p.put("Văn Khúc", vanKhuc); p.put("Văn Xương", vanXuong);
        int kiep = mod(11 + gio, 12);
        p.put("Địa Kiếp", kiep); p.put("Địa Không", mod(10 - kiep, 12));
        p.put("Thiên Mã", thienMa(chi)); p.put("Đào Hoa", daoHoa(chi));

        p.put("Tam Thai", mod(taPhu + ngay - 1, 12));
        p.put("Bát Tọa", mod(huuBat - (ngay - 1), 12));
        p.put("Ân Quang", mod(vanXuong + ngay - 1 - 1, 12));
        p.put("Thiên Quý", mod(vanKhuc - (ngay - 1) + 1, 12));
        p.put("Thiên Quan", new int[]{7, 4, 5, 2, 3, 9, 11, 9, 10, 6}[can]);
        p.put("Thiên Phúc", new int[]{9, 8, 0, 11, 3, 2, 6, 5, 6, 5}[can]);
        p.put("Thiên Tài", mod(menh + chi, 12));
        p.put("Thiên Thọ", mod(than + chi, 12));
        p.put("Thiên Khốc", mod(6 - chi, 12));
        p.put("Thiên Hư", mod(6 + chi, 12));
        p.put("Đẩu Quân", dauQuan(chi, thang, gio));

        // ba mươi mốt sao thêm
        int[] hl = khoiHoaLinh(chi);
        int s = adnn < 0 ? -1 : 1;
        p.put("Hỏa Tinh", mod(hl[0] + s * gio, 12)); p.put("Linh Tinh", mod(hl[1] - s * gio, 12));
        p.put("Long Trì", mod(4 + chi, 12)); p.put("Phượng Các", mod(10 - chi, 12));
        p.put("Thiên Đức", mod(9 + chi, 12)); p.put("Nguyệt Đức", mod(5 + chi, 12));
        p.put("Thiên Hình", mod(9 + thang - 1, 12)); p.put("Thiên Diêu", mod(1 + thang - 1, 12)); p.put("Thiên Y", mod(1 + thang - 1, 12));
        int hongLoan = mod(3 - chi, 12);
        p.put("Hồng Loan", hongLoan); p.put("Thiên Hỷ", mod(hongLoan + 6, 12));
        p.put("Quốc Ấn", mod(loc + 8, 12)); p.put("Đường Phù", mod(loc - 7, 12));
        p.put("Thiên Giải", mod(8 + thang - 1, 12)); p.put("Địa Giải", mod(7 + thang - 1, 12)); p.put("Giải Thần", mod(10 - chi, 12));
        p.put("Thai Phụ", mod(6 + gio, 12)); p.put("Phong Cáo", mod(2 + gio, 12));
        p.put("Thiên Thương", mod(menh + 5, 12)); p.put("Thiên Sứ", mod(menh + 7, 12));
        p.put("Thiên La", 4); p.put("Địa Võng", 10);
        p.put("Cô Thần", mod(2 + 3 * (mod(chi + 1, 12) / 3), 12)); p.put("Quả Tú", mod(10 + 3 * (mod(chi + 1, 12) / 3), 12));
        p.put("Kiếp Sát", new int[]{5, 2, 11, 8}[mod(chi, 4)]);
        p.put("Phá Toái", new int[]{5, 1, 9}[mod(chi, 3)]);
        p.put("Hoa Cái", new int[]{4, 1, 10, 7}[mod(chi, 4)]);
        p.put("Lưu Hà", new int[]{9, 10, 7, 4, 5, 6, 8, 3, 11, 2}[can]);
        p.put("Thiên Trù", new int[]{5, 6, 0, 5, 6, 8, 2, 6, 9, 10}[can]);
        p.put("Lưu Niên Văn Tinh", new int[]{5, 6, 8, 9, 8, 9, 11, 0, 2, 3}[can]);
        p.put("Thiên Không", mod(chi + 1, 12));

        Map<String, String> hoaOf = new LinkedHashMap<>();
        for (int i = 0; i < 4; i++) hoaOf.put(TU_HOA[can][i], HOA[i]);

        int[] triet = TRIET[can];
        int tuanStart = mod(chi - can, 12);
        int t1 = mod(tuanStart + 10, 12), t2 = mod(tuanStart + 11, 12);
        for (int i = 0; i < 12; i++) {
            String cc = CAN[mod(canDan + mod(i - 2, 12), 10)] + " " + CHI[i];
            ls.cung[i] = new Cung(i, cc, TEN_CUNG[mod(menh - i, 12)], i == than, i == t1 || i == t2, i == triet[0] || i == triet[1]);
        }
        for (String n : CHINH_TINH) add(ls, p.get(n), n, "chinh-tinh", hoaOf);
        for (String n : PHU_TINH_GOC) add(ls, p.get(n), n, "phu-tinh", hoaOf);
        for (String n : PHU_TINH_MOI) add(ls, p.get(n), n, "phu-tinh", hoaOf);
        for (String n : PHU_TINH_THEM) add(ls, p.get(n), n, "phu-tinh", hoaOf);
        int ts = trangSinh(cuc);
        for (int i = 0; i < 12; i++) add(ls, mod(ts + adnn * i, 12), VONG_TRUONG_SINH[i], "truong-sinh", hoaOf);
        for (int i = 0; i < 12; i++) add(ls, mod(chi + i, 12), VONG_THAI_TUE[i], "thai-tue", hoaOf);
        for (int i = 0; i < 12; i++) add(ls, mod(loc + adnn * i, 12), VONG_BAC_SY[i], "bac-sy", hoaOf);
        return ls;
    }

    private static void add(LaSo ls, int pos, String ten, String loai, Map<String, String> hoaOf) {
        String nhom = null;
        if ("phu-tinh".equals(loai)) nhom = SAT_TINH.contains(ten) ? "sat-tinh" : CAT_TINH.contains(ten) ? "cat-tinh" : "tap-dieu";
        String d = "chinh-tinh".equals(loai) ? dig(ten, pos) : null;
        ls.cung[pos].sao.add(new Sao(ten, loai, nhom, d == null ? null : DIG_LABEL.get(d), hoaOf.get(ten)));
    }

    static int thienMa(int chi) {
        switch (mod(chi + 1, 4)) { case 1: return 2; case 2: return 11; case 3: return 8; default: return 5; }
    }
    static int daoHoa(int chi) {
        switch (mod(chi, 4)) {
            case 0: return 9;
            case 2: return 3;
            default: return (chi == 5 || chi == 9 || chi == 1) ? 6 : 0;
        }
    }
    static int dauQuan(int chi, int thang, int gio) { return mod(chi - (thang - 1) + gio, 12); }
    static int[] khoiHoaLinh(int chi) {
        switch (mod(chi, 4)) { case 2: return new int[]{1, 3}; case 0: return new int[]{2, 10}; case 1: return new int[]{3, 10}; default: return new int[]{9, 10}; }
    }

    // ------------------------------------------------------------------ đại vận

    public static final class DaiVan {
        public final int thu, viTri, tuTuoi, denTuoi;
        public final String chi, tenCung, chinhTinh;
        DaiVan(int thu, int viTri, String tenCung, int tu, String chinhTinh) {
            this.thu = thu; this.viTri = viTri; this.chi = CHI[viTri]; this.tenCung = tenCung; this.tuTuoi = tu; this.denTuoi = tu + 9; this.chinhTinh = chinhTinh;
        }
    }

    public static List<DaiVan> daiVan(LaSo ls) {
        int chieu = ("Dương Nam".equals(ls.amDuongMenh) || "Âm Nữ".equals(ls.amDuongMenh)) ? 1 : -1;
        List<DaiVan> o = new ArrayList<>();
        for (int k = 0; k < 12; k++) {
            int pos = mod(ls.cungMenh + chieu * k, 12);
            o.add(new DaiVan(k + 1, pos, ls.cung[pos].tenCung, ls.cuc + k * 10, ls.chinhTinhCua(pos)));
        }
        return o;
    }

    // ------------------------------------------------------------------ hạn năm

    public static final class LuuTuHoa {
        public final String hoa, sao, chiCung, tenCung;
        public final int viTri;
        LuuTuHoa(String hoa, String sao, int viTri, String chiCung, String tenCung) {
            this.hoa = hoa; this.sao = sao; this.viTri = viTri; this.chiCung = chiCung; this.tenCung = tenCung;
        }
        public boolean coTrenLaSo() { return viTri >= 0; }
    }

    public static final class HanNam {
        public int nam, tuoiMu, luuNienViTri, tieuVanViTri, daiVanTuTuoi, daiVanDenTuoi, luuDauQuan;
        public String canChiNam, luuNienTenCung, tieuVanTenCung, daiVanTenCung;
        public final List<LuuTuHoa> luuTuHoa = new ArrayList<>();
        /** Cung của 12 tháng âm (chỉ số 0 = tháng Giêng). */
        public final int[] thang = new int[12];
    }

    static int khoiTieuVan(int chiNam) {
        switch (mod(chiNam, 4)) { case 0: return 10; case 2: return 4; case 1: return 7; default: return 1; }
    }

    public static HanNam hanNam(LaSo ls, int namXem) {
        HanNam h = new HanNam();
        h.nam = namXem;
        h.tuoiMu = Math.max(1, namXem - ls.amNam + 1);
        int can = mod(namXem - 4, 10), chi = mod(namXem - 4, 12);
        int chiSinh = mod(ls.amNam - 4, 12);
        int tv = mod(khoiTieuVan(chiSinh) + (ls.nam() ? 1 : -1) * (h.tuoiMu - 1), 12);
        h.daiVanTenCung = "";
        for (DaiVan d : daiVan(ls)) if (h.tuoiMu >= d.tuTuoi && h.tuoiMu <= d.denTuoi) {
            h.daiVanTuTuoi = d.tuTuoi; h.daiVanDenTuoi = d.denTuoi; h.daiVanTenCung = d.tenCung; break;
        }
        for (int i = 0; i < 4; i++) {
            String sao = TU_HOA[can][i];
            int pos = ls.viTriSao(sao);
            h.luuTuHoa.add(new LuuTuHoa(HOA[i], sao, pos, pos >= 0 ? CHI[pos] : "", pos >= 0 ? ls.cung[pos].tenCung : ""));
        }
        h.canChiNam = CAN[can] + " " + CHI[chi];
        h.luuNienViTri = chi; h.luuNienTenCung = ls.cung[chi].tenCung;
        h.tieuVanViTri = tv; h.tieuVanTenCung = ls.cung[tv].tenCung;
        h.luuDauQuan = dauQuan(chi, ls.amThang, ls.gio);
        for (int t = 1; t <= 12; t++) h.thang[t - 1] = mod(h.luuDauQuan + t - 1, 12);
        return h;
    }

    // ------------------------------------------------------------------ sao lưu

    public static final class SaoLuu {
        public final String ma, ten, chi, tenCung, voiLuuNien, voiTieuVan, voiDaiVan;
        public final boolean theoCan;
        public final int viTri;
        SaoLuu(String ma, String ten, boolean theoCan, int viTri, String tenCung, String a, String b, String c) {
            this.ma = ma; this.ten = ten; this.theoCan = theoCan; this.viTri = viTri; this.chi = CHI[viTri]; this.tenCung = tenCung;
            voiLuuNien = a; voiTieuVan = b; voiDaiVan = c;
        }
    }

    static String quanHe(int a, int b) {
        switch (mod(a - b, 12)) { case 0: return "dong-cung"; case 6: return "xung-chieu"; case 4: case 8: return "tam-hop"; default: return ""; }
    }

    public static List<SaoLuu> saoLuu(LaSo ls, int namXem) {
        int can = mod(namXem - 4, 10), chi = mod(namXem - 4, 12);
        int loc = LOC_TON[can];
        HanNam h = hanNam(ls, namXem);
        int dv = -1;
        for (DaiVan d : daiVan(ls)) if (h.tuoiMu >= d.tuTuoi && h.tuoiMu <= d.denTuoi) { dv = d.viTri; break; }
        Object[][] t = {
            {"luu-thai-tue", "Lưu Thái Tuế", false, chi},
            {"luu-loc-ton", "Lưu Lộc Tồn", true, loc},
            {"luu-kinh-duong", "Lưu Kình Dương", true, mod(loc + 1, 12)},
            {"luu-da-la", "Lưu Đà La", true, mod(loc - 1, 12)},
            {"luu-thien-ma", "Lưu Thiên Mã", false, thienMa(chi)},
        };
        List<SaoLuu> o = new ArrayList<>();
        for (Object[] r : t) {
            int p = (Integer) r[3];
            o.add(new SaoLuu((String) r[0], (String) r[1], (Boolean) r[2], p, ls.cung[p].tenCung,
                quanHe(p, h.luuNienViTri), quanHe(p, h.tieuVanViTri), dv >= 0 ? quanHe(p, dv) : ""));
        }
        return o;
    }

    // ------------------------------------------------------------------ cách cục

    public static final class CachCuc {
        public final String ten, dieuKien, moTa;
        public String tenHan, tenCung, chiCung, nhom, chuVan;
        public int viTri = -1;
        public boolean coDiBan;
        CachCuc(String ten, String tenHan, String dieuKien, String moTa) { this.ten = ten; this.tenHan = tenHan; this.dieuKien = dieuKien; this.moTa = moTa; }
    }

    private static final String[][] BO = {
        {"Sát Phá Lang", "殺破狼", "Bộ ba sao biến động. Ba sao luôn cách nhau đúng bốn cung nên khi một sao toạ Mệnh thì hai sao kia luôn nằm trong tam phương."},
        {"Cơ Nguyệt Đồng Lương", "機月同梁", "Bộ sao ôn hoà, sách thường gắn với nếp làm việc có quy củ và ổn định."},
        {"Tử Phủ Vũ Tướng", "紫府武相", "Bộ sao chủ về tổ chức và quản trị theo cách gọi của sách. Lá số xếp cả Liêm Trinh, Thái Dương và Cự Môn vào nhóm này để mỗi sao toạ Mệnh thuộc đúng một bộ; Tân Biên xếp Liêm Trinh vào bộ Sát Phá Liêm Tham và tách Cự Môn, Thái Dương thành bộ Cự Nhật."},
    };
    private static final String[][] BO_SAO = {
        {"Thất Sát", "Phá Quân", "Tham Lang"},
        {"Thiên Cơ", "Thái Âm", "Thiên Đồng", "Thiên Lương"},
        {"Tử Vi", "Thiên Phủ", "Vũ Khúc", "Thiên Tướng", "Liêm Trinh", "Thái Dương", "Cự Môn"},
    };

    private static boolean coDu(Set<String> tap, String... can) { for (String c : can) if (!tap.contains(c)) return false; return true; }

    public static List<CachCuc> cachCuc(LaSo ls) {
        List<CachCuc> out = new ArrayList<>();
        int menh = ls.cungMenh;
        String tenMenh = ls.cung[menh].tenCung, chiMenh = CHI[menh];
        Set<String> taiMenh = ls.saoTai(menh), hoiMenh = ls.tamPhuongTuChinh(menh);
        java.util.function.Consumer<CachCuc> them = cc -> { cc.viTri = menh; cc.tenCung = tenMenh; cc.chiCung = chiMenh; out.add(cc); };

        int nguon = menh; boolean giaiTiep = false;
        if (ls.chinhTai(menh).isEmpty()) { nguon = menh + 6; giaiTiep = true; }
        Set<String> taiNguon = new HashSet<>(ls.chinhTai(nguon));
        tim:
        for (int b = 0; b < 3; b++) for (String sName : BO_SAO[b]) {
            if (!taiNguon.contains(sName)) continue;
            String dk = giaiTiep ? "Cung Mệnh vô chính diệu, lấy theo " + sName + " ở cung xung chiếu" : "Sao " + sName + " toạ thủ tại cung Mệnh";
            them.accept(new CachCuc(BO[b][0], BO[b][1], dk, BO[b][2]));
            break tim;
        }
        if (coDu(taiMenh, "Tử Vi", "Thiên Phủ")) them.accept(new CachCuc("Tử Phủ đồng cung", "紫府同宮",
            "Tử Vi và Thiên Phủ cùng toạ thủ tại cung Mệnh (chỉ xảy ra ở Dần hoặc Thân)", "Hai sao đứng đầu hai chùm chính tinh cùng nằm một cung."));

        int pn = ls.viTriSao("Thái Dương"), pg = ls.viTriSao("Thái Âm");
        boolean sn = pn >= 0 && !"H".equals(dig("Thái Dương", pn)), sg = pg >= 0 && !"H".equals(dig("Thái Âm", pg));
        if (sn && sg) {
            int[] pos = {menh, menh + 4}; String[] ten = {"Mệnh", "Quan Lộc"};
            for (int k = 0; k < 2; k++) {
                int pp = mod(pos[k], 12); String dk;
                if (pp == 1 && pn == 5 && pg == 9) dk = "Cung " + ten[k] + " an tại Sửu, Thái Dương ở Tỵ và Thái Âm ở Dậu hợp chiếu, cả hai đều sáng";
                else if (pp == 7 && pn == 3 && pg == 11) dk = "Cung " + ten[k] + " an tại Mùi, Thái Dương ở Mão và Thái Âm ở Hợi hợp chiếu, cả hai đều sáng";
                else continue;
                CachCuc cc = new CachCuc("Nhật Nguyệt tịnh minh", "日月並明", dk, "Hai sao mặt trời và mặt trăng cùng ở chỗ sáng và cùng chiếu về một cung.");
                cc.coDiBan = true; cc.viTri = pp; cc.tenCung = ls.cung[pp].tenCung; cc.chiCung = CHI[pp];
                out.add(cc);
                break;
            }
        }
        if (ls.chinhTai(menh).isEmpty()) them.accept(new CachCuc("Mệnh vô chính diệu", "命無正曜",
            "Cung Mệnh không có chính tinh nào toạ thủ", "Luận theo sao ở cung xung chiếu, lối gọi trong sách là tá tinh an cung."));
        Set<String> truoc = ls.saoTai(menh - 1), sau = ls.saoTai(menh + 1);
        if ((truoc.contains("Văn Xương") && sau.contains("Văn Khúc")) || (truoc.contains("Văn Khúc") && sau.contains("Văn Xương")))
            them.accept(new CachCuc("Xương Khúc giáp Mệnh", "昌曲夾命", "Văn Xương và Văn Khúc ở hai cung kề hai bên cung Mệnh",
                "Cặp sao văn chương kẹp cung Mệnh, sách gắn với học vấn và thi cử."));
        if (coDu(hoiMenh, "Văn Xương", "Văn Khúc")) {
            CachCuc cc = new CachCuc("Xương Khúc đồng hội", "昌曲同會", "Văn Xương và Văn Khúc cùng hội về tam phương tứ chính của cung Mệnh",
                "Cặp sao văn chương, sách gắn với học vấn và thi cử.");
            cc.coDiBan = true; them.accept(cc);
        }
        if (coDu(hoiMenh, "Tả Phù", "Hữu Bật")) them.accept(new CachCuc("Tả Hữu đồng hội", "左右同會",
            "Tả Phù và Hữu Bật cùng hội về tam phương tứ chính của cung Mệnh", "Cặp sao phụ trợ, sách gắn với việc được người khác giúp đỡ."));
        if (coDu(hoiMenh, "Địa Không", "Địa Kiếp")) them.accept(new CachCuc("Không Kiếp đồng hội", "空劫同會",
            "Địa Không và Địa Kiếp cùng hội về tam phương tứ chính của cung Mệnh",
            "Cặp sao hao tán. Sách xem đây là cách cục cần lưu ý chứ không phải điềm dữ tất định."));
        out.addAll(toanThu(ls));
        return out;
    }

    // --- Tử Vi Đẩu Số toàn thư: định phú, định quý, định bần tiện

    public static final String DINH_PHU = "dinh-phu", DINH_QUY = "dinh-quy", DINH_BAN_TIEN = "dinh-ban-tien";
    private static String moTaNhom(String n) {
        switch (n) {
            case DINH_PHU: return "Tử Vi Đẩu Số toàn thư xếp cách này vào bảng định phú cục, nhóm cách sách gắn với của cải.";
            case DINH_QUY: return "Tử Vi Đẩu Số toàn thư xếp cách này vào bảng định quý cục, nhóm cách sách gắn với địa vị và danh tiếng.";
            default: return "Tử Vi Đẩu Số toàn thư xếp cách này vào bảng định bần tiện cục, nhóm cách sách gắn với thiếu thốn, vất vả. Đó là quy tắc của một sách cổ, không phải điều tất định.";
        }
    }
    private static final String[] CAT_TOAN_THU = {"Tả Phù", "Hữu Bật", "Văn Xương", "Văn Khúc", "Thiên Khôi", "Thiên Việt", "Lộc Tồn"};
    private static final Map<String, String> SAT_DIA = new LinkedHashMap<>();
    static {
        SAT_DIA.put("Kình Dương", "HM.HM.HM.HM.");
        SAT_DIA.put("Đà La", ".MH.MH.MH.MH");
        SAT_DIA.put("Hỏa Tinh", "HĐMLHĐMLHĐML");
        SAT_DIA.put("Linh Tinh", "HĐMLHĐMLHĐML");
    }
    private static char satDia(String ten, int pos) { return SAT_DIA.get(ten).charAt(mod(pos, 12)); }

    private static boolean coHoa(LaSo ls, int pos, String hoa) { for (Sao s : ls.cung[mod(pos, 12)].sao) if (hoa.equals(s.tuHoa)) return true; return false; }
    private static boolean khongVong(LaSo ls, int pos) { Cung c = ls.cung[mod(pos, 12)]; return c.tuan || c.triet; }
    private static boolean coCat(LaSo ls, int pos) {
        Set<String> t = ls.saoTai(pos);
        for (String s : CAT_TOAN_THU) if (t.contains(s)) return true;
        return coHoa(ls, pos, "Hóa Lộc") || coHoa(ls, pos, "Hóa Quyền") || coHoa(ls, pos, "Hóa Khoa");
    }
    private static boolean coLoc(LaSo ls, int pos) { return ls.saoTai(pos).contains("Lộc Tồn") || coHoa(ls, pos, "Hóa Lộc"); }
    private static boolean giap(LaSo ls, int pos, String a, String b) {
        Set<String> t = ls.saoTai(pos - 1), s = ls.saoTai(pos + 1);
        return (t.contains(a) && s.contains(b)) || (t.contains(b) && s.contains(a));
    }
    private static boolean sangMV(String ten, int pos) { String d = dig(ten, pos); return "M".equals(d) || "V".equals(d); }
    private static boolean tamPhuongCo(LaSo ls, int pos, String ten) { return ls.tamPhuongTuChinh(pos).contains(ten); }
    private static boolean tamPhuongCoHoa(LaSo ls, int pos, String hoa) { for (int d : new int[]{0, 4, 6, 8}) if (coHoa(ls, pos + d, hoa)) return true; return false; }

    private interface Them { void f(String nhom, String ten, String han, String chuVan, int pos, boolean diBan, String dk); }

    private static List<CachCuc> toanThu(LaSo ls) {
        List<CachCuc> out = new ArrayList<>();
        int menh = ls.cungMenh, pM = mod(menh, 12);
        Them them = (nhom, ten, han, chuVan, pos, diBan, dk) -> {
            CachCuc c = new CachCuc(ten, han, dk, moTaNhom(nhom));
            c.nhom = nhom; if (!chuVan.isEmpty()) c.chuVan = chuVan; if (diBan) c.coDiBan = true;
            int p = mod(pos, 12); c.viTri = p; c.tenCung = ls.cung[p].tenCung; c.chiCung = CHI[p];
            out.add(c);
        };
        Set<String> tai = ls.saoTai(menh);
        int pThan = mod(ls.cungThan, 12);
        int[] tmPos = pThan != pM ? new int[]{pM, pThan} : new int[]{pM};
        String[] tmTen = {"Mệnh", "Thân"};
        int pTB = mod(menh - 4, 12), pQL = mod(menh - 8, 12), pDT = mod(menh - 9, 12);

        int[] mtb = {pM, pTB}; String[] mtbTen = {"Mệnh", "Tài Bạch"};
        for (int k = 0; k < 2; k++) {
            int p = mtb[k];
            if (ls.saoTai(p).contains("Vũ Khúc") && giap(ls, p, "Thái Dương", "Thái Âm"))
                them.f(DINH_PHU, "Nhật Nguyệt giáp Tài", "日月夾財", "武守命日月来夹是也，财帛宫亦然。", p, false,
                    "Vũ Khúc toạ thủ cung " + mtbTen[k] + ", Thái Dương và Thái Âm ở hai cung kề hai bên");
        }
        if (tai.contains("Thiên Mã")) {
            int tr = menh - 1, sa = menh + 1;
            if ((ls.saoTai(tr).contains("Vũ Khúc") && coLoc(ls, sa)) || (ls.saoTai(sa).contains("Vũ Khúc") && coLoc(ls, tr)))
                them.f(DINH_PHU, "Tài Lộc giáp Mã", "財祿夾馬", "马守命武禄来夹是也，逢生旺尤妙。", menh, true,
                    "Thiên Mã toạ thủ cung Mệnh, Vũ Khúc và sao Lộc (Lộc Tồn hay sao mang Hóa Lộc) ở hai cung kề hai bên");
        }
        if (coDu(ls.saoTai(pDT), "Thái Dương", "Thái Âm"))
            them.f(DINH_PHU, "Nhật Nguyệt chiếu bích", "日月照璧", "日月临田宅宫是也，喜居墓库。", pDT, false,
                "Thái Dương và Thái Âm cùng ở cung Điền Trạch (chỉ xảy ra ở Sửu hoặc Mùi)");
        if (pM == 6) {
            List<String> ct = ls.chinhTai(menh);
            if (ct.size() == 1 && ct.get(0).equals("Thái Dương"))
                them.f(DINH_PHU, "Kim xán quang huy", "金燦光輝", "太阳单守，命在午宫是也。", menh, false,
                    "Cung Mệnh an tại Ngọ, Thái Dương là chính tinh duy nhất toạ thủ");
        }

        if (giap(ls, menh, "Thái Dương", "Thái Âm") && !khongVong(ls, menh) && coCat(ls, menh))
            them.f(DINH_QUY, "Nhật Nguyệt giáp Mệnh", "日月夾命", "不坐空亡遇逢本宫有吉星是也。", menh, true,
                "Thái Dương và Thái Âm ở hai cung kề hai bên cung Mệnh; cung Mệnh không có Tuần, Triệt và có cát tinh (lục cát tinh, Lộc Tồn hay sao mang Hóa Lộc, Quyền, Khoa)");
        if (ls.viTriSao("Thái Dương") == 3) {
            int[] mq = {pM, pQL}; String[] mqTen = {"Mệnh", "Quan Lộc"};
            for (int k = 0; k < 2; k++) if (mq[k] == 3)
                them.f(DINH_QUY, "Nhật xuất Phù Tang", "日出扶桑", "日在卯守命是也，守官禄宫亦然。", 3, false,
                    "Thái Dương ở Mão, toạ thủ cung " + mqTen[k]);
        }
        if (pM == 11 && tai.contains("Thái Âm"))
            them.f(DINH_QUY, "Nguyệt lạc Hợi cung", "月落亥宮", "月在亥守命是也，又名月朗天门。", menh, false, "Thái Âm toạ thủ cung Mệnh an tại Hợi");
        if (pDT == 0 && ls.saoTai(pDT).contains("Thái Âm"))
            them.f(DINH_QUY, "Nguyệt sinh thương hải", "月生滄海", "月在子宫守田宅是也。", pDT, false, "Thái Âm ở Tý, toạ thủ cung Điền Trạch");
        if (tai.contains("Tử Vi")) {
            boolean ta = false, huu = false;
            for (int d : new int[]{4, 6, 8}) { Set<String> t = ls.saoTai(menh + d); ta |= t.contains("Tả Phù"); huu |= t.contains("Hữu Bật"); }
            if ((ta && huu) || giap(ls, menh, "Tả Phù", "Hữu Bật"))
                them.f(DINH_QUY, "Phụ Bật củng chủ", "輔弼拱主", "紫微守命二星来拱是也，夹之亦然。", menh, true,
                    "Tử Vi toạ thủ cung Mệnh; Tả Phù và Hữu Bật cùng chiếu từ tam phương tứ chính, hoặc ở hai cung kề hai bên");
        }
        if (coDu(tai, "Tử Vi", "Tả Phù", "Hữu Bật"))
            them.f(DINH_QUY, "Quân thần khánh hội", "君臣慶會", "紫微左右同守命是也，更会相武阴妙上。", menh, false, "Tử Vi, Tả Phù và Hữu Bật cùng toạ thủ cung Mệnh");
        Set<String> doi = ls.saoTai(menh + 6);
        if ((tai.contains("Thiên Khôi") && doi.contains("Thiên Việt")) || (tai.contains("Thiên Việt") && doi.contains("Thiên Khôi")))
            them.f(DINH_QUY, "Toạ quý hướng quý", "坐貴向貴", "谓魁钺在命迭相坐拱是也。", menh, true,
                "Một trong hai sao Thiên Khôi, Thiên Việt toạ thủ cung Mệnh, sao kia ở cung xung chiếu");
        if (tai.contains("Thất Sát") && (pM == 0 || pM == 2 || pM == 6 || pM == 8))
            them.f(DINH_QUY, "Thất Sát triều Đẩu", "七殺朝斗", "七杀寅申子午一生爵禄荣昌 为七杀朝斗格。", menh, false,
                "Thất Sát toạ thủ cung Mệnh an tại Dần, Thân, Tý hoặc Ngọ");
        if (pM == 1 && coDu(ls.saoTai(7), "Thái Dương", "Thái Âm"))
            them.f(DINH_QUY, "Nhật Nguyệt đồng lâm", "日月同臨", "日月同未命安丑侯伯之材", menh, true,
                "Cung Mệnh an tại Sửu, Thái Dương và Thái Âm cùng ở Mùi chiếu sang");
        for (int k = 0; k < tmPos.length; k++) if (coDu(ls.saoTai(tmPos[k]), "Thiên Hình", "Liêm Trinh")) {
            them.f(DINH_QUY, "Hình Tù giáp Ấn", "刑囚夾印", "天刑廉贞同临身命主武勇之人。", tmPos[k], true,
                "Thiên Hình và Liêm Trinh cùng toạ thủ cung " + tmTen[k]);
            break;
        }
        if (tamPhuongCoHoa(ls, menh, "Hóa Khoa") && tamPhuongCoHoa(ls, menh, "Hóa Quyền") && tamPhuongCoHoa(ls, menh, "Hóa Lộc"))
            them.f(DINH_QUY, "Khoa Quyền Lộc củng", "科權祿拱", "又得三方四正科权禄拱", menh, true,
                "Hóa Khoa, Hóa Quyền và Hóa Lộc đều ở cung Mệnh hay tam phương tứ chính của nó");
        if (tai.contains("Tham Lang") && tai.contains("Hỏa Tinh") && sangMV("Tham Lang", pM)) {
            char d = satDia("Hỏa Tinh", pM);
            if (d == 'M' || d == 'V')
                them.f(DINH_QUY, "Tham Hỏa tương phùng", "貪火相逢", "谓二星守命同居庙旺是也。", menh, true,
                    "Tham Lang và Hỏa Tinh cùng toạ thủ cung Mệnh, cả hai miếu hay vượng: Tham Lang theo bảng miếu hãm của lá số, Hỏa Tinh theo bảng mười hai cung của Toàn thư (miếu ở Dần, Ngọ, Tuất)");
        }
        if (pM == 3 && tai.contains("Vũ Khúc"))
            them.f(DINH_QUY, "Vũ Khúc thủ viên", "武曲守垣", "武守命卯宫是也，余不是。", menh, false, "Vũ Khúc toạ thủ cung Mệnh an tại Mão");
        if (pM == 2 || pM == 8) {
            int phu = ls.viTriSao("Thiên Phủ"), tuong = ls.viTriSao("Thiên Tướng");
            if ((phu == pTB && tuong == pQL) || (phu == pQL && tuong == pTB))
                them.f(DINH_QUY, "Phủ Tướng triều viên", "府相朝垣", "命寅申，府相在财帛宫，禄官朝者，上格别宫次之。", menh, true,
                    "Cung Mệnh an tại Dần hoặc Thân; Thiên Phủ và Thiên Tướng chia nhau ở cung Tài Bạch và cung Quan Lộc");
        }
        if (coHoa(ls, menh, "Hóa Quyền") && coHoa(ls, menh, "Hóa Lộc")) {
            boolean sang = true;
            for (Sao s : ls.cung[pM].sao) if (("Hóa Quyền".equals(s.tuHoa) || "Hóa Lộc".equals(s.tuHoa)) && !sangMV(s.ten, pM)) sang = false;
            if (sang) them.f(DINH_QUY, "Quyền Lộc sinh phùng", "權祿生逢", "二星守命庙旺是也，陷不是。", menh, false,
                "Hai sao mang Hóa Quyền và Hóa Lộc cùng toạ thủ cung Mệnh, cả hai ở mức miếu hay vượng");
        }
        if (tai.contains("Kình Dương") && (pM == 1 || pM == 4 || pM == 7 || pM == 10) && coCat(ls, menh))
            them.f(DINH_QUY, "Dương Nhận nhập miếu", "羊刃入廟", "辰戌丑未守命遇吉是也。", menh, true,
                "Kình Dương toạ thủ cung Mệnh an tại Thìn, Tuất, Sửu hoặc Mùi, cung Mệnh có cát tinh (lục cát tinh, Lộc Tồn hay sao mang Hóa Lộc, Quyền, Khoa)");
        if (pM == 3 && coDu(tai, "Cự Môn", "Thiên Cơ") && !ls.canChiNam.startsWith("Giáp"))
            them.f(DINH_QUY, "Cự Cơ cư Mão", "巨機居卯", "巨机居卯乙辛己丙至公卿 不贵即富，甲人平常何也，因甲禄到寅卯宫有擎羊破格耳。", menh, false,
                "Cự Môn và Thiên Cơ cùng toạ thủ cung Mệnh an tại Mão; người sinh năm can Giáp không tính vì sách ghi Kình Dương ở Mão phá cách");

        if (tai.contains("Liêm Trinh") && khongVong(ls, menh))
            them.f(DINH_BAN_TIEN, "Sinh bất phùng thời", "生不逢時", "命坐空亡逢廉贞是也。", menh, true, "Liêm Trinh toạ thủ cung Mệnh, cung Mệnh có Tuần hoặc Triệt");
        if (coLoc(ls, menh) && khongVong(ls, menh) && tamPhuongCo(ls, menh, "Địa Không") && tamPhuongCo(ls, menh, "Địa Kiếp"))
            them.f(DINH_BAN_TIEN, "Lộc phùng lưỡng sát", "祿逢兩殺", "禄坐空亡又逢空劫杀星是也。", menh, true,
                "Sao Lộc (Lộc Tồn hay sao mang Hóa Lộc) ở cung Mệnh có Tuần hoặc Triệt, Địa Không và Địa Kiếp cùng ở tam phương tứ chính");
        if (tai.contains("Thiên Mã") && khongVong(ls, menh))
            them.f(DINH_BAN_TIEN, "Mã lạc không vong", "馬落空亡", "马既落亡虽禄冲会无用主奔波。", menh, true, "Thiên Mã toạ thủ cung Mệnh, cung Mệnh có Tuần hoặc Triệt");
        if (tai.contains("Phá Quân") && "H".equals(dig("Phá Quân", pM)))
            them.f(DINH_BAN_TIEN, "Nhất sinh cô bần", "一生孤貧", "谓破守命星陷地是也。", menh, true, "Phá Quân toạ thủ cung Mệnh ở vị trí hãm theo bảng miếu hãm của lá số");
        String[] tuSat = {"Kình Dương", "Đà La", "Hỏa Tinh", "Linh Tinh"};
        for (int k = 0; k < tmPos.length; k++) {
            Set<String> tg = ls.saoTai(tmPos[k]);
            List<String> vi = new ArrayList<>();
            for (String t : tuSat) if (tg.contains(t) && satDia(t, tmPos[k]) == 'H') vi.add(t);
            if (vi.isEmpty()) continue;
            String ds = vi.size() == 1 ? vi.get(0) : String.join(", ", vi.subList(0, vi.size() - 1)) + " và " + vi.get(vi.size() - 1);
            them.f(DINH_BAN_TIEN, "Quân tử tại dã", "君子在野", "谓四杀守身命而言临陷地是也。", tmPos[k], true,
                ds + " " + (vi.size() > 1 ? "cùng toạ thủ" : "toạ thủ") + " cung " + tmTen[k] + ", ở bậc hãm theo bảng mười hai cung của Toàn thư (tứ sát là Kình Dương, Đà La, Hỏa Tinh, Linh Tinh)");
        }
        if (tai.contains("Lộc Tồn") && coHoa(ls, menh, "Hóa Lộc") && tamPhuongCo(ls, menh, "Địa Không") && tamPhuongCo(ls, menh, "Địa Kiếp"))
            them.f(DINH_BAN_TIEN, "Lưỡng trùng Hoa Cái", "兩重華蓋", "谓禄存化禄坐命遇空劫是也。", menh, true,
                "Lộc Tồn và sao mang Hóa Lộc cùng toạ thủ cung Mệnh, Địa Không và Địa Kiếp cùng ở tam phương tứ chính");
        return out;
    }
}
