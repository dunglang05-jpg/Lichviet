package vn.lichviet.core.fortune;

import vn.lichviet.core.DayInfo;
import vn.lichviet.core.TuoiHop;

/** Tử vi hằng ngày theo tuổi: kết hợp hợp tuổi ngày, điểm ngày và sao năm. Mang tính tham khảo. */
public final class TuViNgay {
    public final int total;            // 0..100
    public final int congViec, taiLoc, tinhCam, sucKhoe;   // 1..5
    public final String tongQuan, congViecText, taiLocText, tinhCamText, sucKhoeText;
    public final String mauMayMan, soMayMan, gioTot;

    private static final String[][] TEXT = {
        {"Công việc gặp trở ngại, nên làm việc thận trọng, kiểm tra kỹ.", "Công việc bình thường, hoàn thành việc đã có trước khi bắt đầu việc mới.",
            "Công việc thuận lợi, dễ được ghi nhận.", "Công việc hanh thông, hợp bàn bạc, ký kết."},
        {"Hạn chế chi tiêu lớn, tránh cho vay mượn.", "Tài chính ổn định, nên tiết kiệm.",
            "Có khoản thu nhỏ hoặc cơ hội kiếm thêm.", "Tài lộc tốt, hợp giao dịch, mua sắm cần thiết."},
        {"Dễ hiểu lầm, nên mềm mỏng, lắng nghe.", "Tình cảm êm đềm, dành thời gian cho người thân.",
            "Quan hệ ấm áp, hợp hẹn hò, gặp gỡ.", "Tình cảm thăng hoa, có tin vui."},
        {"Dễ mệt mỏi, nên nghỉ ngơi và ăn uống điều độ.", "Sức khỏe bình thường, chú ý giấc ngủ.",
            "Tinh thần tốt, hợp vận động nhẹ.", "Sức khỏe dồi dào, năng lượng tích cực."},
    };

    public TuViNgay(Person p, DayInfo d) {
        TuoiHop.Report r = TuoiHop.report(p.lunarYear, d);
        int base = r.total;                                  // 0..100
        int dayBonus = (d.score - 3) * 6;
        total = Math.max(5, Math.min(98, base + dayBonus));
        int seed = Math.floorMod(d.jd * 31 + p.lunarYear * 7 + p.day, 97);
        congViec = level(total, seed % 3 - 1);
        taiLoc = level(total, (seed / 3) % 3 - 1);
        tinhCam = level(total, (seed / 9) % 3 - 1);
        sucKhoe = level(total, (seed / 27) % 3 - 1);
        congViecText = TEXT[0][bucket(congViec)];
        taiLocText = TEXT[1][bucket(taiLoc)];
        tinhCamText = TEXT[2][bucket(tinhCam)];
        sucKhoeText = TEXT[3][bucket(sucKhoe)];
        tongQuan = total >= 75 ? "Ngày rất thuận lợi cho tuổi " + p.canChi() + "."
            : total >= 55 ? "Ngày khá tốt, mọi việc diễn ra suôn sẻ."
            : total >= 40 ? "Ngày bình thường, nên làm việc theo kế hoạch."
            : "Ngày kém thuận, nên giữ ổn định, tránh quyết định lớn.";
        mauMayMan = MenhInfo.mauHop(p.menh()).split(" \\(")[0];
        String[] so = MenhInfo.soHop(p.menh()).split(", ");
        soMayMan = so[seed % so.length];
        StringBuilder g = new StringBuilder();
        for (DayInfo.HourInfo h : d.hours) if (h.good) {
            if (g.length() > 0) g.append(", ");
            g.append(h.name).append(" (").append(h.range).append(")");
        }
        gioTot = g.toString();
    }

    static int level(int total, int jitter) {
        int l = total >= 75 ? 5 : total >= 55 ? 4 : total >= 40 ? 3 : total >= 25 ? 2 : 1;
        return Math.max(1, Math.min(5, l + jitter));
    }

    static int bucket(int l) { return l <= 2 ? 0 : l == 3 ? 1 : l == 4 ? 2 : 3; }
}
