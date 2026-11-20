package vn.lichviet.core.fortune;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import vn.lichviet.core.DayInfo;
import vn.lichviet.core.LunarCalendar;
import vn.lichviet.core.fortune.TuVi.*;

/** Đối chiếu với bộ ca kiểm của thư viện Nhật Nguyệt (CC BY 4.0), xem src/test/resources/ca-kiem/README.md. */
public class CaKiemNhatNguyetTest {

    private static List<String> lines(String name) throws Exception {
        List<String> out = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(CaKiemNhatNguyetTest.class.getResourceAsStream("/ca-kiem/" + name)), StandardCharsets.UTF_8))) {
            for (String l; (l = r.readLine()) != null; ) out.add(l);
        }
        return out;
    }

    private static String n(String s) { return s == null ? "" : s; }
    private static String b(boolean v) { return v ? "1" : "0"; }

    @Test
    public void duongSangAm() throws Exception {
        List<String> sai = new ArrayList<>();
        for (String l : lines("duong-sang-am.tsv.gz")) {
            String[] p = l.split("\t", -1);
            DayInfo d = DayInfo.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
            String got = d.lunarDay + "/" + d.lunarMonth + "/" + d.lunarYear + "/" + (d.leap ? 1 : 0) + "|" + d.dayCanChi + "|"
                + d.monthCanChi.replace(" (nhuận)", "") + "|" + d.yearCanChi + "|" + d.tu;
            String exp = p[3] + "/" + p[4] + "/" + p[5] + "/" + p[6] + "|" + p[7] + "|" + p[8] + "|" + p[9] + "|" + p[10];
            if (!got.equals(exp) && sai.size() < 5) sai.add(p[0] + "/" + p[1] + "/" + p[2] + ": " + got + " ≠ " + exp);
        }
        assertEquals(new ArrayList<String>(), sai);
    }

    @Test
    public void amSangDuong() throws Exception {
        List<String> sai = new ArrayList<>();
        for (String l : lines("am-sang-duong.tsv.gz")) {
            String[] p = l.split("\t");
            int[] r = LunarCalendar.lunarToSolar(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]), Integer.parseInt(p[3]));
            String got = r[0] + "/" + r[1] + "/" + r[2], exp = p[4] + "/" + p[5] + "/" + p[6];
            if (!got.equals(exp) && sai.size() < 5) sai.add(l + ": " + got);
        }
        assertEquals(new ArrayList<String>(), sai);
    }

    @Test
    public void tuVi() throws Exception {
        List<String> sai = new ArrayList<>();
        Map<String, String> exp = new LinkedHashMap<>();
        int ca = 0;
        for (String l : lines("tu-vi.txt.gz")) {
            if (!l.equals("---")) { int i = l.indexOf('='); exp.put(l.substring(0, i), l.substring(i + 1)); continue; }
            ca++;
            String[] v = exp.get("vao").split(",");
            int nx = Integer.parseInt(v[5]);
            LaSo ls = TuVi.lap("", v[4].equals("Nam"), Integer.parseInt(v[0]), Integer.parseInt(v[1]), Integer.parseInt(v[2]), Integer.parseInt(v[3]));
            Map<String, String> got = new LinkedHashMap<>();
            got.put("am", ls.amNgay + "/" + ls.amThang + "/" + ls.amNam + "/" + b(ls.amNhuan));
            got.put("chung", ls.canChiNam + "|" + ls.amDuongMenh + "|" + ls.cuc + "|" + ls.tenCuc + "|" + ls.banMenh + "|" + ls.cungMenh + "|" + ls.cungThan + "|" + ls.gioSinh);
            for (Cung c : ls.cung) {
                StringBuilder sb = new StringBuilder(c.canChi + "|" + c.tenCung + "|" + b(c.laThan) + b(c.tuan) + b(c.triet));
                for (Sao s : c.sao) sb.append("|").append(s.ten).append(":").append(s.loai).append(":").append(n(s.nhom)).append(":").append(n(s.mieuVuong)).append(":").append(n(s.tuHoa));
                got.put("cung" + c.viTri, sb.toString());
            }
            StringBuilder dv = new StringBuilder();
            for (DaiVan d : TuVi.daiVan(ls)) dv.append(d.thu + ":" + d.viTri + ":" + d.tenCung + ":" + d.tuTuoi + ":" + d.denTuoi + ":" + d.chinhTinh + ";");
            got.put("daiVan", dv.toString());
            HanNam h = TuVi.hanNam(ls, nx);
            StringBuilder hs = new StringBuilder(h.canChiNam + "|" + h.tuoiMu + "|" + h.luuNienViTri + ":" + h.luuNienTenCung + "|" + h.tieuVanViTri + ":" + h.tieuVanTenCung
                + "|" + h.daiVanTuTuoi + "-" + h.daiVanDenTuoi + ":" + h.daiVanTenCung + "|" + h.luuDauQuan);
            for (LuuTuHoa t : h.luuTuHoa) hs.append("|" + t.hoa + ":" + t.sao + ":" + t.viTri + ":" + t.tenCung + ":" + b(t.coTrenLaSo()));
            for (int t = 0; t < 12; t++) hs.append("|" + h.thang[t]);
            got.put("hanNam", hs.toString());
            StringBuilder sl = new StringBuilder();
            for (SaoLuu s : TuVi.saoLuu(ls, nx)) sl.append(s.ma + ":" + s.ten + ":" + b(s.theoCan) + ":" + s.viTri + ":" + s.tenCung + ":" + s.voiLuuNien + ":" + s.voiTieuVan + ":" + s.voiDaiVan + ";");
            got.put("saoLuu", sl.toString());
            StringBuilder cc = new StringBuilder();
            for (CachCuc c : TuVi.cachCuc(ls)) cc.append(c.ten + ":" + n(c.tenHan) + ":" + c.dieuKien + ":" + c.moTa + ":" + c.viTri + ":" + n(c.tenCung) + ":" + n(c.chiCung) + ":" + n(c.nhom) + ":" + n(c.chuVan) + ":" + b(c.coDiBan) + ";");
            got.put("cachCuc", cc.toString());
            for (Map.Entry<String, String> e : got.entrySet())
                if (!e.getValue().equals(exp.get(e.getKey())) && sai.size() < 5) sai.add(exp.get("vao") + " [" + e.getKey() + "] " + e.getValue() + " ≠ " + exp.get(e.getKey()));
            exp.clear();
        }
        assertEquals(777, ca);
        assertEquals(new ArrayList<String>(), sai);
    }
}
