package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.Almanac;
import vn.lichviet.core.DayInfo;

/** Vận hạn trong một năm âm lịch: liệt kê sao, hạn, tam tai, kim lâu, hoang ốc và quan hệ tuổi – năm. Không chấm điểm. */
public final class VanHan {
    public final int year, tuoiMu, sao, han, hoangOc;
    public final String yearCanChi, napAmNam, kimLau;
    public final boolean tamTai;
    public final List<String> tot = new ArrayList<>();
    public final List<String> xau = new ArrayList<>();
    public final List<String> khac = new ArrayList<>();

    public VanHan(Person p, int lunarYear) {
        year = lunarYear;
        tuoiMu = p.tuoiMu(lunarYear);
        int can = Math.floorMod(lunarYear + 6, 10), chi = Math.floorMod(lunarYear + 8, 12);
        yearCanChi = DayInfo.CAN[can] + " " + DayInfo.CHI[chi];
        napAmNam = Almanac.napAm(can, chi);
        sao = SaoHan.sao(tuoiMu, p.male);
        String sn = "Sao " + SaoHan.SAO[sao];
        if (SaoHan.SAO_TOT[sao] == 2) tot.add(sn + " (sao tốt)");
        else if (SaoHan.SAO_TOT[sao] == 1) khac.add(sn + " (sao trung)");
        else xau.add(sn + " (sao xấu)");
        han = SaoHan.han(tuoiMu, p.male);
        xau.add("Hạn " + SaoHan.HAN[han]);
        tamTai = SaoHan.isTamTai(p.chi, chi);
        if (tamTai) xau.add("Phạm Tam tai (năm " + DayInfo.CHI[chi] + ")");
        kimLau = SaoHan.kimLau(tuoiMu);
        if (kimLau != null) xau.add("Phạm " + kimLau);
        hoangOc = SaoHan.hoangOc(tuoiMu);
        if (SaoHan.hoangOcXau(hoangOc)) xau.add("Hoang ốc: " + SaoHan.HOANG_OC[hoangOc]);
        else tot.add("Hoang ốc: " + SaoHan.HOANG_OC[hoangOc]);
        if (p.chi == chi) khac.add("Năm tuổi (trùng chi " + DayInfo.CHI[chi] + ")");
        else if (HopTuoi.lucXung(p.chi, chi)) xau.add("Tuổi " + DayInfo.CHI[p.chi] + " lục xung năm " + DayInfo.CHI[chi]);
        else if (HopTuoi.tamHop(p.chi, chi)) tot.add("Tuổi " + DayInfo.CHI[p.chi] + " tam hợp năm " + DayInfo.CHI[chi]);
        else if (HopTuoi.lucHop(p.chi, chi)) tot.add("Tuổi " + DayInfo.CHI[p.chi] + " lục hợp năm " + DayInfo.CHI[chi]);
        int mh = HopTuoi.quanHeHanh(p.menh(), Almanac.hanhNapAm(can, chi));
        if (mh == 2) tot.add("Mệnh " + p.menh() + " tương sinh với hành năm (" + napAmNam + ")");
        else if (mh == -2) xau.add("Mệnh " + p.menh() + " tương khắc với hành năm (" + napAmNam + ")");
    }
}
