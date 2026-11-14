package vn.lichviet.core.fortune;

import java.util.ArrayList;
import java.util.List;
import vn.lichviet.core.Almanac;
import vn.lichviet.core.DayInfo;

/** Quan hệ giữa tuổi và một ngày: chỉ liệt kê theo quy tắc can chi, nạp âm; không chấm điểm, không ngẫu nhiên. */
public final class TuViNgay {
    public final List<String> tot = new ArrayList<>();
    public final List<String> xau = new ArrayList<>();
    public final String gioTot;

    public TuViNgay(Person p, DayInfo d) {
        String z = "Ngày " + DayInfo.CHI[d.dayChi] + " với tuổi " + DayInfo.CHI[p.chi];
        if (HopTuoi.tamHop(p.chi, d.dayChi)) tot.add(z + ": tam hợp");
        else if (HopTuoi.lucHop(p.chi, d.dayChi)) tot.add(z + ": lục hợp");
        else if (HopTuoi.lucXung(p.chi, d.dayChi)) xau.add(z + ": lục xung");
        else if (HopTuoi.lucHai(p.chi, d.dayChi)) xau.add(z + ": lục hại");
        String hn = Almanac.hanhNapAm(d.dayCan, d.dayChi);
        int mh = HopTuoi.quanHeHanh(p.menh(), hn);
        if (mh == 2) tot.add("Hành ngày (" + hn + ") tương sinh mệnh " + p.menh());
        else if (mh == -2) xau.add("Hành ngày (" + hn + ") tương khắc mệnh " + p.menh());
        if (HopTuoi.canHop(p.can, d.dayCan)) tot.add("Can ngày " + DayInfo.CAN[d.dayCan] + " hợp can tuổi " + DayInfo.CAN[p.can]);
        else if (HopTuoi.canXung(p.can, d.dayCan)) xau.add("Can ngày " + DayInfo.CAN[d.dayCan] + " xung can tuổi " + DayInfo.CAN[p.can]);
        StringBuilder g = new StringBuilder();
        for (DayInfo.HourInfo h : d.hours) if (h.good) {
            if (g.length() > 0) g.append(", ");
            g.append(h.name).append(" (").append(h.range).append(")");
        }
        gioTot = g.toString();
    }
}
