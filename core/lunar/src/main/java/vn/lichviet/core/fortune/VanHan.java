package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.Almanac;
import vn.lichviet.core.DayInfo;

/** Vận hạn trong một năm âm lịch: sao, tam tai, kim lâu, hoang ốc, thái tuế, quan hệ mệnh – năm. */
public final class VanHan {
    public final int year, tuoiMu, sao, hoangOc, score;     // score 1..5
    public final String yearCanChi, napAmNam, kimLau;
    public final boolean tamTai;
    public final List<String> tot = new ArrayList<>();
    public final List<String> xau = new ArrayList<>();
    public final List<String> loiKhuyen = new ArrayList<>();

    public VanHan(Person p, int lunarYear) {
        year = lunarYear;
        tuoiMu = p.tuoiMu(lunarYear);
        int can = Math.floorMod(lunarYear + 6, 10), chi = Math.floorMod(lunarYear + 8, 12);
        yearCanChi = DayInfo.CAN[can] + " " + DayInfo.CHI[chi];
        napAmNam = Almanac.napAm(can, chi);
        int pts = 0;
        sao = SaoHan.sao(tuoiMu, p.male);
        String sName = "Sao " + SaoHan.SAO[sao];
        if (SaoHan.SAO_TOT[sao] == 2) { tot.add(sName + " chiếu mệnh (tốt)"); pts += 2; }
        else if (SaoHan.SAO_TOT[sao] == 1) tot.add(sName + " chiếu mệnh (trung bình)");
        else { xau.add(sName + " chiếu mệnh (xấu)"); pts -= 2; }
        tamTai = SaoHan.isTamTai(p.chi, chi);
        if (tamTai) { xau.add("Phạm Tam tai (năm " + DayInfo.CHI[chi] + ")"); pts -= 1; }
        kimLau = SaoHan.kimLau(tuoiMu);
        if (kimLau != null) xau.add("Phạm " + kimLau + " – kỵ cưới hỏi");
        hoangOc = SaoHan.hoangOc(tuoiMu);
        if (SaoHan.hoangOcXau(hoangOc)) xau.add("Phạm " + SaoHan.HOANG_OC[hoangOc] + " – kỵ làm nhà");
        else tot.add(SaoHan.HOANG_OC[hoangOc] + " – thuận làm nhà");
        if (p.chi == chi) { xau.add("Năm tuổi (phạm Thái Tuế)"); pts -= 1; }
        else if (HopTuoi.lucXung(p.chi, chi)) { xau.add("Tuổi xung với năm " + DayInfo.CHI[chi]); pts -= 1; }
        else if (HopTuoi.tamHop(p.chi, chi) || HopTuoi.lucHop(p.chi, chi)) { tot.add("Tuổi hợp với năm " + DayInfo.CHI[chi]); pts += 1; }
        int mh = HopTuoi.quanHeHanh(p.menh(), Almanac.hanhNapAm(can, chi));
        if (mh == 2) { tot.add("Mệnh " + p.menh() + " tương sinh với năm (" + napAmNam + ")"); pts += 1; }
        else if (mh == -2) { xau.add("Mệnh " + p.menh() + " tương khắc với năm (" + napAmNam + ")"); pts -= 1; }
        score = Math.max(1, Math.min(5, 3 + pts));
        if (SaoHan.SAO_TOT[sao] == 0) loiKhuyen.add("Có thể cúng giải sao " + SaoHan.SAO[sao] + ": " + SaoHan.CUNG[sao] + ".");
        if (tamTai) loiKhuyen.add("Năm tam tai nên tránh khởi sự lớn, đi đường cẩn thận.");
        if (kimLau != null) loiKhuyen.add("Nếu cưới hỏi nên chọn năm khác hoặc mượn tuổi.");
        if (SaoHan.hoangOcXau(hoangOc)) loiKhuyen.add("Nếu làm nhà nên mượn tuổi người hợp.");
        if (score >= 4) loiKhuyen.add("Năm thuận lợi, hợp mở rộng công việc và đầu tư cho bản thân.");
        else if (score <= 2) loiKhuyen.add("Năm nhiều trở ngại, nên giữ sức khỏe, chi tiêu chừng mực, làm việc chắc chắn.");
        else loiKhuyen.add("Năm bình ổn, làm việc đều đặn sẽ có kết quả.");
    }

    public String verdict() {
        switch (score) {
            case 5: return "Rất tốt";
            case 4: return "Tốt";
            case 3: return "Bình thường";
            case 2: return "Kém";
            default: return "Xấu";
        }
    }
}
