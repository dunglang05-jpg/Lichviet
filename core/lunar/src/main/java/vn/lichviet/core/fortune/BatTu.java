package vn.lichviet.core.fortune;

import com.nlf.calendar.EightChar;
import com.nlf.calendar.Solar;
import com.nlf.calendar.eightchar.DaYun;
import com.nlf.calendar.eightchar.Yun;
import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.Almanac;

/**
 * Lá số Bát tự (Tứ trụ) dựa trên thư viện lunar-java (6tail, MIT), chuyển thuật ngữ sang tiếng Việt.
 * Giờ sinh nhập theo giờ Việt Nam (UTC+7) được đổi sang UTC+8 trước khi tính.
 */
public final class BatTu {
    public static final String[] TRU = {"Năm", "Tháng", "Ngày", "Giờ"};

    public static final class Pillar {
        public String can, chi, napAm, hanhCan, hanhChi, thapThanCan, truongSinh;
        public final List<String> tangCan = new ArrayList<>();
        public final List<String> thapThanChi = new ArrayList<>();
    }

    public final Pillar[] pillars;            // 3 hoặc 4 trụ (không có giờ thì 3)
    public final int[] hanhCount = new int[5];   // Kim, Thủy, Mộc, Hỏa, Thổ
    public final String nhatChu, hanhNhatChu;
    public final List<String> daiVan = new ArrayList<>();

    public static final String[] HANH = {"Kim", "Thủy", "Mộc", "Hỏa", "Thổ"};

    public BatTu(Person p) {
        int h = p.hour < 0 ? 12 : p.hour;
        Solar s = Solar.fromYmdHms(p.year, p.month, p.day, h, 30, 0).nextHour(1);
        EightChar ec = s.getLunar().getEightChar();
        int n = p.hour < 0 ? 3 : 4;
        pillars = new Pillar[n];
        pillars[0] = pillar(ec.getYearGan(), ec.getYearZhi(), ec.getYearHideGan(), ec.getYearShiShenGan(), ec.getYearShiShenZhi(), ec.getYearDiShi());
        pillars[1] = pillar(ec.getMonthGan(), ec.getMonthZhi(), ec.getMonthHideGan(), ec.getMonthShiShenGan(), ec.getMonthShiShenZhi(), ec.getMonthDiShi());
        pillars[2] = pillar(ec.getDayGan(), ec.getDayZhi(), ec.getDayHideGan(), "日主", ec.getDayShiShenZhi(), ec.getDayDiShi());
        if (n == 4) pillars[3] = pillar(ec.getTimeGan(), ec.getTimeZhi(), ec.getTimeHideGan(), ec.getTimeShiShenGan(), ec.getTimeShiShenZhi(), ec.getTimeDiShi());

        // Đếm hành của 8 chữ (4 can, 4 chi) – chỉ đếm, không trọng số
        for (Pillar pl : pillars) {
            hanhCount[idx(pl.hanhCan)]++;
            hanhCount[idx(pl.hanhChi)]++;
        }
        nhatChu = pillars[2].can;
        hanhNhatChu = pillars[2].hanhCan;

        Yun yun = ec.getYun(p.male ? 1 : 0);
        for (DaYun d : yun.getDaYun(9)) {
            if (d.getIndex() == 0) continue;
            String gz = d.getGanZhi();
            daiVan.add(d.getStartAge() + "–" + d.getEndAge() + " tuổi: " + canVi(gz.substring(0, 1)) + " " + chiVi(gz.substring(1, 2)) + " (" + d.getStartYear() + ")");
        }
    }

    private Pillar pillar(String gan, String zhi, List<String> hide, String ssGan, List<String> ssZhi, String diShi) {
        Pillar p = new Pillar();
        p.can = canVi(gan);
        p.chi = chiVi(zhi);
        p.hanhCan = hanhOfCan(p.can);
        p.hanhChi = Almanac.HANH_CHI[indexOf(CHI_VI, p.chi)];
        p.napAm = Almanac.napAm(indexOf(CAN_VI, p.can), indexOf(CHI_VI, p.chi));
        p.thapThanCan = thapThan(ssGan);
        p.truongSinh = truongSinh(diShi);
        for (String h : hide) p.tangCan.add(canVi(h));
        for (String t : ssZhi) p.thapThanChi.add(thapThan(t));
        return p;
    }

    static int idx(String h) {
        for (int i = 0; i < 5; i++) if (HANH[i].equals(h)) return i;
        return 4;
    }

    static final String[] CAN_VI = {"Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"};
    static final String[] CHI_VI = {"Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"};
    private static final String CAN_ZH = "甲乙丙丁戊己庚辛壬癸";
    private static final String CHI_ZH = "子丑寅卯辰巳午未申酉戌亥";

    static int indexOf(String[] a, String v) {
        for (int i = 0; i < a.length; i++) if (a[i].equals(v)) return i;
        return 0;
    }

    public static String canVi(String zh) {
        int i = CAN_ZH.indexOf(zh);
        return i < 0 ? zh : CAN_VI[i];
    }

    public static String chiVi(String zh) {
        int i = CHI_ZH.indexOf(zh);
        return i < 0 ? zh : CHI_VI[i];
    }

    public static String hanhOfCan(String canVi) { return Almanac.HANH_CAN[indexOf(CAN_VI, canVi)]; }

    public static String thapThan(String zh) {
        switch (zh) {
            case "比肩": return "Tỷ Kiên";
            case "劫财": return "Kiếp Tài";
            case "食神": return "Thực Thần";
            case "伤官": return "Thương Quan";
            case "偏财": return "Thiên Tài";
            case "正财": return "Chính Tài";
            case "七杀": return "Thất Sát";
            case "正官": return "Chính Quan";
            case "偏印": return "Thiên Ấn";
            case "正印": return "Chính Ấn";
            case "日主": return "Nhật Chủ";
            default: return zh;
        }
    }

    public static String truongSinh(String zh) {
        switch (zh) {
            case "长生": return "Trường Sinh";
            case "沐浴": return "Mộc Dục";
            case "冠带": return "Quan Đới";
            case "临官": return "Lâm Quan";
            case "帝旺": return "Đế Vượng";
            case "衰": return "Suy";
            case "病": return "Bệnh";
            case "死": return "Tử";
            case "墓": return "Mộ";
            case "绝": return "Tuyệt";
            case "胎": return "Thai";
            case "养": return "Dưỡng";
            default: return zh;
        }
    }

}
