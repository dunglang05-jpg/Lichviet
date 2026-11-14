package vn.lichviet.core.fortune;

/** Màu theo ngũ hành bản mệnh (quy luật tương sinh – tương khắc; nguồn: Sources.NGU_HANH). */
public final class MenhInfo {
    private MenhInfo() {}

    static final String[] MAU_HANH = {"trắng, xám, ghi, bạc", "đen, xanh nước biển", "xanh lá", "đỏ, hồng, cam, tím", "vàng, nâu đất"};

    static int idx(String h) { return HopTuoi.hanh(h); }

    /** Màu bản mệnh và màu của hành sinh ra mệnh. */
    public static String mauHop(String h) {
        int i = idx(h);
        return "Bản mệnh: " + MAU_HANH[i] + ". Tương sinh (" + HopTuoi.HANH[Math.floorMod(i - 1, 5)] + " sinh " + h + "): "
            + MAU_HANH[Math.floorMod(i - 1, 5)];
    }

    /** Màu của hành khắc mệnh. */
    public static String mauKy(String h) {
        int i = idx(h);
        String khac = HopTuoi.HANH[Math.floorMod(i - 2, 5)];
        return MAU_HANH[Math.floorMod(i - 2, 5)] + " (" + khac + " khắc " + h + ")";
    }
}
