# Tranh tháng phong cách tranh dân gian Đông Hồ (vẽ mới, nét đen đậm, màu phẳng, nền giấy điệp).
# Mỗi phần tử: (d, fill, stroke, width) – d là path SVG trên khung 320 x 180.
import math

INK = "#1E1A16"; SON = "#C0392B"; VANG = "#E8B43A"; CHAM = "#2E4A7A"; LUC = "#3E7D4A"; NAU = "#8A5A2B"
HONG = "#E58FA0"; TRANG = "#FBF4E2"; XAM = "#4E4A46"; NUOC = "#9DB8C9"; DAT = "#C9A46A"; LA = "#6FA35A"; CAM = "#E07A2E"
PAPER = "#F2E3C2"

def f(n): return f"{n:.1f}".rstrip('0').rstrip('.')

def ell(cx, cy, rx, ry):
    return f"M{f(cx-rx)} {f(cy)}A{f(rx)} {f(ry)} 0 1 0 {f(cx+rx)} {f(cy)}A{f(rx)} {f(ry)} 0 1 0 {f(cx-rx)} {f(cy)}Z"

def poly(*pts, close=True):
    s = "M" + " L".join(f"{f(x)} {f(y)}" for x, y in zip(pts[0::2], pts[1::2]))
    return s + ("Z" if close else "")

class S:
    def __init__(s): s.els = []
    def fill(s, d, c, w=1.6): s.els.append((d, c, INK, w)); return s
    def flat(s, d, c): s.els.append((d, c, None, 0)); return s
    def line(s, d, c=INK, w=1.6): s.els.append((d, None, c, w)); return s
    def e(s, cx, cy, rx, ry, c, w=1.6): return s.fill(ell(cx, cy, rx, ry), c, w)
    def p(s, c, *pts, w=1.6): return s.fill(poly(*pts), c, w)

    # --- hoạ tiết dùng chung ---
    def frame(s):
        s.line("M4 4H316V176H4Z", SON, 3)
        s.line("M9 9H311V171H9Z", INK, 1.2)
        return s
    def ground(s, y=150, c=DAT):
        s.flat(f"M9 {y}Q80 {y-6} 160 {y}T311 {y}V171H9Z", c)
        s.line(f"M9 {y}Q80 {y-6} 160 {y}T311 {y}", INK, 1.4)
        for x in range(24, 300, 22):
            s.line(f"M{x} {y+8}q3 -6 6 0", INK, 1)
        return s
    def water(s, y=120, c=NUOC):
        s.flat(f"M9 {y}H311V171H9Z", c)
        for row, yy in enumerate(range(y + 10, 170, 12)):
            off = 12 if row % 2 else 0
            for x in range(14 + off, 306, 24):
                s.line(f"M{x} {yy}q6 -6 12 0", CHAM, 1.1)
        return s
    def sun(s, cx, cy, r, c=SON):
        s.e(cx, cy, r, r, c)
        for i in range(12):
            a = i * math.pi / 6
            s.line(f"M{f(cx+(r+4)*math.cos(a))} {f(cy+(r+4)*math.sin(a))}L{f(cx+(r+9)*math.cos(a))} {f(cy+(r+9)*math.sin(a))}", c, 1.6)
        return s
    def cloud(s, x, y, k=1.0):
        d = (f"M{f(x)} {f(y)}q{f(4*k)} {f(-10*k)} {f(14*k)} {f(-6*k)}q{f(6*k)} {f(-9*k)} {f(16*k)} {f(-2*k)}"
             f"q{f(12*k)} {f(-2*k)} {f(10*k)} {f(8*k)}H{f(x)}Z")
        s.fill(d, TRANG, 1.3)
        s.line(f"M{f(x+12*k)} {f(y-3*k)}a{f(4*k)} {f(4*k)} 0 1 1 {f(6*k)} {f(3*k)}", INK, 1)
        return s
    def blossom(s, x, y, r=4, c=HONG):
        for i in range(5):
            a = -math.pi / 2 + i * 2 * math.pi / 5
            s.e(x + r * 0.95 * math.cos(a), y + r * 0.95 * math.sin(a), r * 0.62, r * 0.62, c, 0.9)
        s.e(x, y, r * 0.35, r * 0.35, VANG, 0.8)
        return s
    def branch(s, d, flowers, c=HONG):
        s.line(d, NAU, 3.2)
        for x, y in flowers: s.blossom(x, y, 4.2, c)
        return s
    def grass(s, x, y, h=10, c=LUC):
        s.line(f"M{x} {y}q-2 -{h*0.6} -4 -{h}M{x} {y}q0 -{h*0.7} 1 -{h*1.1}M{x} {y}q2 -{h*0.6} 5 -{h*0.9}", c, 1.4)
        return s
    def person(s, x, y, shirt=CHAM, hat=True, bend=0.0):
        # x,y = chân; bend: cúi người (0..1)
        hx = x + 10 * bend; hy = y - 34 + 10 * bend
        s.p(shirt, x - 7, y - 14, x + 7, y - 14, hx + 6, hy + 10, hx - 6, hy + 10)  # áo
        s.p(INK, x - 6, y - 14, x + 6, y - 14, x + 5, y, x - 5, y, w=1)          # quần
        s.e(hx, hy + 3, 5, 5, "#F1C89A", 1.2)                                     # mặt
        if hat: s.p(VANG, hx - 11, hy + 2, hx + 11, hy + 2, hx, hy - 9)          # nón lá
        return s
    def heron(s, x, y, k=1.0):
        s.fill(f"M{f(x)} {f(y)}q{f(12*k)} {f(-8*k)} {f(24*k)} 0q{f(-12*k)} {f(-3*k)} {f(-24*k)} 0Z", TRANG, 1.1)
        s.fill(f"M{f(x)} {f(y)}q{f(-10*k)} {f(-10*k)} {f(-4*k)} {f(-18*k)}q{f(2*k)} {f(9*k)} {f(8*k)} {f(16*k)}Z", TRANG, 1.1)
        return s
    def fish(s, x, y, k=1.0, c=SON, flip=False):
        sx = -1 if flip else 1
        s.fill(f"M{f(x)} {f(y)}q{f(sx*16*k)} {f(-12*k)} {f(sx*32*k)} 0q{f(-sx*16*k)} {f(12*k)} {f(-sx*32*k)} 0Z", c)
        s.p(c, x + sx * 30 * k, y, x + sx * 42 * k, y - 8 * k, x + sx * 42 * k, y + 8 * k)
        s.e(x + sx * 7 * k, y - 2 * k, 1.6 * k, 1.6 * k, INK, 0.5)
        for i in range(3):
            s.line(f"M{f(x+sx*(14+i*5)*k)} {f(y-5*k)}q{f(sx*3*k)} {f(5*k)} 0 {f(10*k)}", INK, 0.9)
        return s
    def lotus(s, x, y, k=1.0, c=HONG):
        for dx, rot in [(-1, -1), (1, 1), (0, 0)]:
            tip_x = x + rot * 12 * k
            s.fill(f"M{f(x)} {f(y)}Q{f(x+dx*14*k + (8*k if dx==0 else 0))} {f(y-14*k)} {f(tip_x)} {f(y-24*k)}Q{f(x+dx*2*k - (8*k if dx==0 else 0))} {f(y-12*k)} {f(x)} {f(y)}Z", c, 1.2)
        return s
    def lotus_leaf(s, x, y, rx, ry):
        s.fill(f"M{f(x)} {f(y)}L{f(x+rx*0.9)} {f(y-ry*0.45)}A{f(rx)} {f(ry)} 0 1 0 {f(x+rx*0.95)} {f(y+ry*0.3)}Z", LA, 1.3)
        for a in (-0.6, 0, 0.6, 1.4, 2.2, 3.0, 3.8):
            s.line(f"M{f(x)} {f(y)}L{f(x+rx*0.8*math.cos(a))} {f(y+ry*0.8*math.sin(a))}", LUC, 0.8)
        return s
    def buffalo(s, x, y, k=1.0, c=XAM):
        s.e(x, y, 34 * k, 18 * k, c)                                             # thân
        for lx in (-24, -12, 14, 26):
            s.p(c, x + lx * k - 3 * k, y + 10 * k, x + lx * k + 3 * k, y + 10 * k, x + lx * k + 3 * k, y + 32 * k, x + lx * k - 3 * k, y + 32 * k, w=1.3)
        s.e(x + 38 * k, y - 4 * k, 11 * k, 9 * k, c)                             # đầu
        s.line(f"M{f(x+32*k)} {f(y-11*k)}q{f(-10*k)} {f(-14*k)} {f(4*k)} {f(-18*k)}M{f(x+44*k)} {f(y-11*k)}q{f(12*k)} {f(-12*k)} {f(-2*k)} {f(-18*k)}", INK, 2.6)
        s.e(x + 42 * k, y - 6 * k, 1.6 * k, 1.6 * k, TRANG, 0.6)
        s.line(f"M{f(x-34*k)} {f(y-2*k)}q{f(-8*k)} {f(10*k)} {f(-4*k)} {f(20*k)}", INK, 1.6)  # đuôi
        for i in range(3):
            s.line(f"M{f(x-16*k+i*12*k)} {f(y-8*k)}q{f(6*k)} {f(-4*k)} {f(10*k)} 0", "#6F6A64", 1)
        return s
    def chicken(s, x, y, k=1.0, body=CAM, tail=True):
        s.e(x, y, 14 * k, 10 * k, body)
        s.e(x + 12 * k, y - 10 * k, 6 * k, 6 * k, body)
        s.p(SON, x + 9 * k, y - 15 * k, x + 13 * k, y - 21 * k, x + 16 * k, y - 15 * k, w=1)  # mào
        s.p(VANG, x + 17 * k, y - 11 * k, x + 23 * k, y - 9 * k, x + 17 * k, y - 7 * k, w=1)  # mỏ
        s.e(x + 13 * k, y - 11 * k, 1.2 * k, 1.2 * k, INK, 0.4)
        if tail:
            for col, dy in [(CHAM, -14), (LUC, -8), (SON, -2)]:
                s.fill(f"M{f(x-10*k)} {f(y-2*k)}q{f(-14*k)} {f(dy*k)} {f(-22*k)} {f((dy+10)*k)}q{f(10*k)} {f(-4*k)} {f(22*k)} {f(-6*k)}Z", col, 1.1)
        s.line(f"M{f(x-3*k)} {f(y+9*k)}v{f(9*k)}M{f(x+4*k)} {f(y+9*k)}v{f(9*k)}", INK, 1.6)
        s.line(f"M{f(x-4*k)} {f(y-2*k)}q{f(6*k)} {f(6*k)} {f(12*k)} 0", INK, 1)
        return s
    def pig(s, x, y, k=1.0):
        s.e(x, y, 36 * k, 20 * k, INK)
        s.e(x + 34 * k, y - 2 * k, 10 * k, 11 * k, INK)
        s.e(x + 43 * k, y + 2 * k, 4 * k, 3.5 * k, "#3A332D", 1)
        for lx in (-22, -8, 14, 26):
            s.p(INK, x + lx * k - 3 * k, y + 14 * k, x + lx * k + 3 * k, y + 14 * k, x + lx * k + 3 * k, y + 28 * k, x + lx * k - 3 * k, y + 28 * k, w=1)
        # xoáy âm dương trắng trên mình – hoạ tiết dân gian
        for cx, cy, r in [(x - 12 * k, y - 2 * k, 8 * k), (x + 10 * k, y + 4 * k, 6 * k)]:
            s.line(f"M{f(cx-r)} {f(cy)}a{f(r)} {f(r)} 0 1 1 {f(2*r)} 0a{f(r/2)} {f(r/2)} 0 0 1 {f(-r)} 0a{f(r/2)} {f(r/2)} 0 0 0 {f(-r)} 0", TRANG, 1.6)
        s.p(INK, x + 30 * k, y - 12 * k, x + 34 * k, y - 22 * k, x + 38 * k, y - 11 * k, w=1)
        s.e(x + 36 * k, y - 5 * k, 1.5 * k, 1.5 * k, TRANG, 0.5)
        s.line(f"M{f(x-36*k)} {f(y-4*k)}q{f(-8*k)} {f(-6*k)} {f(-4*k)} {f(-12*k)}", INK, 1.6)
        return s
    def duck(s, x, y, k=1.0):
        s.fill(f"M{f(x-12*k)} {f(y)}q{f(4*k)} {f(10*k)} {f(20*k)} {f(4*k)}q{f(4*k)} {f(-6*k)} 0 {f(-12*k)}q{f(-8*k)} {f(4*k)} {f(-20*k)} {f(8*k)}Z", TRANG, 1.2)
        s.e(x + 8 * k, y - 12 * k, 4.5 * k, 4.5 * k, TRANG, 1.1)
        s.p(VANG, x + 12 * k, y - 13 * k, x + 18 * k, y - 11 * k, x + 12 * k, y - 10 * k, w=0.9)
        s.e(x + 9 * k, y - 13 * k, 0.9 * k, 0.9 * k, INK, 0.3)
        return s
    def star_lantern(s, x, y, r, c=SON):
        pts = []
        for i in range(10):
            a = -math.pi / 2 + i * math.pi / 5
            rr = r if i % 2 == 0 else r * 0.45
            pts += [x + rr * math.cos(a), y + rr * math.sin(a)]
        s.p(c, *pts, w=1.3)
        s.e(x, y, r * 0.22, r * 0.22, VANG, 0.8)
        return s
    def banh_chung(s, x, y, w):
        s.p(LUC, x, y, x + w, y, x + w, y + w, x, y + w, w=1.6)
        for t in (0.33, 0.66):
            s.line(f"M{f(x+w*t)} {f(y)}V{f(y+w)}M{f(x)} {f(y+w*t)}H{f(x+w)}", SON, 1.8)
        return s
    def boy(s, x, y, shirt=SON):
        s.p(shirt, x - 6, y - 12, x + 6, y - 12, x + 4, y - 24, x - 4, y - 24)
        s.p(CHAM, x - 5, y - 12, x + 5, y - 12, x + 4, y, x - 4, y, w=1)
        s.e(x, y - 29, 5, 5, "#F1C89A", 1.2)
        s.fill(f"M{f(x-5)} {f(y-31)}q5 -7 10 0q-5 -2 -10 0Z", INK, 1)  # tóc trái đào
        return s
    def svg(s):
        out = [f'<rect width="320" height="180" fill="{PAPER}"/>']
        for d, fill, stroke, w in s.els:
            out.append(f'<path d="{d}" fill="{fill or "none"}" stroke="{stroke or "none"}" stroke-width="{w}" stroke-linecap="round" stroke-linejoin="round"/>')
        return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 180" width="640" height="360">' + "".join(out) + "</svg>"


def paper_dots(s):
    import random
    r = random.Random(5)
    for _ in range(70):
        x, y = r.uniform(12, 308), r.uniform(12, 168)
        s.flat(ell(x, y, 0.8, 0.8), "#E9D3A6")
    return s


def m1():  # Tháng Giêng: gà trống gáy xuân, cành đào
    s = paper_dots(S())
    s.sun(262, 40, 16).cloud(160, 36, 1.0)
    s.branch("M9 20C60 26 90 40 120 70M60 28C70 45 85 50 95 52M100 54C110 40 118 36 130 34",
             [(118, 68), (95, 52), (130, 34), (72, 40), (48, 24), (108, 48)])
    s.ground(150)
    s.chicken(196, 122, 2.0)
    for x in (40, 70, 280, 300): s.grass(x, 150)
    return s.frame()

def m2():  # Mục đồng thổi sáo trên lưng trâu
    s = paper_dots(S())
    s.fill("M9 110Q70 60 140 106T260 96T311 104V171H9Z", "#A9C58A", 1.4)
    s.sun(60, 42, 13, VANG).cloud(200, 36, 1.1)
    s.ground(152, "#B8CF8F")
    s.buffalo(150, 118, 1.15)
    s.boy(150, 102, SON)
    s.line("M150 76L176 66", NAU, 2.2)  # sáo
    for i in range(3): s.line(f"M{180+i*6} {58-i*5}q3 -3 6 0", INK, 1)  # nốt nhạc
    for x in (30, 60, 260, 290): s.grass(x, 152)
    return s.frame()

def m3():  # Cấy lúa
    s = paper_dots(S())
    s.cloud(240, 34, 1.1).heron(60, 36, 1.2).heron(110, 50, 0.9)
    s.water(96, "#B9CFD6")
    for row, y in enumerate(range(112, 170, 14)):
        for x in range(20 + (row % 2) * 10, 300, 20): s.grass(x, y, 9, LUC)
    s.person(120, 118, CHAM, True, 0.8)
    s.person(200, 122, SON, True, 0.8)
    return s.frame()

def m4():  # Cá chép vờn trăng trong ao
    s = paper_dots(S())
    s.e(250, 44, 22, 22, VANG)
    s.water(70, "#A9C3D2")
    s.lotus_leaf(60, 92, 26, 12).lotus_leaf(270, 140, 30, 13)
    s.fish(110, 120, 1.5, SON).fish(220, 104, 1.3, CAM, True)
    s.lotus(66, 88, 0.9)
    return s.frame()

def m5():  # Đầm sen mùa hạ
    s = paper_dots(S())
    s.sun(270, 36, 13).cloud(40, 36, 1.0)
    s.water(98, "#A9C3D2")
    for x, y, rx in [(50, 120, 32), (150, 132, 36), (250, 118, 30)]: s.lotus_leaf(x, y, rx, 13)
    for x, y in [(90, 112), (200, 104), (290, 124)]:
        s.line(f"M{x} {y}V{y+44}", LUC, 2.4)
        s.lotus(x, y, 1.3)
    s.line("M150 60l30 -6M162 52l6 14M150 60l-8 -6M150 60l-8 6", INK, 1.6)  # chuồn chuồn
    s.e(165, 57, 5, 2, CHAM, 1)
    return s.frame()

def m6():  # Kéo vó trên sông
    s = paper_dots(S())
    s.fill("M9 90Q60 60 120 86T240 72T311 86V100H9Z", "#A9C58A", 1.3)
    s.cloud(220, 34, 1.0).sun(60, 40, 12, VANG)
    s.water(98)
    s.fill("M80 126H200Q190 142 140 144Q96 142 80 126Z", NAU)   # thuyền
    s.person(124, 126, CHAM, True, 0.2)
    s.line("M140 104L250 60M250 60L232 128M250 60L286 128", NAU, 2.2)  # cần vó
    s.line("M232 128Q259 142 286 128M240 132L278 132M246 120L272 120", INK, 1)
    s.fish(244, 150, 0.7, CAM)
    return s.frame()

def m7():  # Đàn vịt trên đồng
    s = paper_dots(S())
    s.sun(262, 40, 13).cloud(60, 40, 1.1)
    s.water(118, "#B9CFD6")
    s.line("M9 118H311", INK, 1.4)
    for i, x in enumerate(range(60, 280, 40)): s.duck(x, 140 + (i % 2) * 8, 1.3)
    s.boy(30, 140, CHAM)
    s.line("M38 116L52 96", NAU, 2)
    for x in range(14, 310, 30): s.grass(x, 118, 12, LUC)
    return s.frame()

def m8():  # Trung thu rước đèn
    s = paper_dots(S())
    s.flat("M9 9H311V171H9Z", "#25324F")
    s.e(250, 50, 26, 26, "#F6E3A8")
    s.cloud(60, 48, 1.1)
    s.ground(150, "#3A4A6A")
    for x, c in [(90, SON), (170, VANG), (240, CAM)]:
        s.boy(x, 150, c)
        s.line(f"M{x+4} {136}L{x+16} {96}", NAU, 1.8)
        s.star_lantern(x + 16, 90, 12, SON if c != SON else VANG)
    s.fish(20, 110, 0.9, CAM)
    return s.frame()

def m9():  # Gặt lúa
    s = paper_dots(S())
    s.sun(60, 40, 14).cloud(230, 36, 1.1)
    s.flat("M9 100H311V171H9Z", "#E7C35A")
    s.line("M9 100H311", INK, 1.4)
    for row, y in enumerate(range(110, 170, 12)):
        for x in range(16 + (row % 2) * 9, 306, 18):
            s.line(f"M{x} {y}q-2 -8 2 -12", "#B08A1E", 1.3)
    s.person(130, 140, CHAM, True, 0.6)
    s.line("M146 116q10 -8 16 2", INK, 2)  # liềm
    for x in (210, 240, 270):
        s.p(VANG, x - 8, 150, x + 8, 150, x + 4, 120, x - 4, 120, w=1.4)
        s.line(f"M{x-6} 134H{x+6}", SON, 2)
    return s.frame()

def m10():  # Gà mẹ, gà con
    s = paper_dots(S())
    s.cloud(60, 40, 1.0).sun(262, 40, 12, VANG)
    s.ground(144)
    s.line("M20 144V104M50 144V104M20 114H90M20 130H90M70 144V104M90 144V104", NAU, 2.4)  # hàng rào
    s.chicken(190, 118, 1.8, VANG, tail=False)
    for x, y in [(240, 138), (262, 142), (284, 136), (150, 140)]:
        s.e(x, y, 7, 6, "#F3D36B", 1.2).e(x + 5, y - 6, 4, 4, "#F3D36B", 1).e(x + 6, y - 7, 0.8, 0.8, INK, 0.3)
    return s.frame()

def m11():  # Lợn ăn lá ráy
    s = paper_dots(S())
    s.cloud(240, 36, 1.0)
    s.ground(152)
    s.pig(130, 118, 1.25)
    for x, y in [(240, 150), (264, 150)]:
        s.fill(f"M{x} {y}Q{x-14} {y-30} {x} {y-46}Q{x+14} {y-30} {x} {y}Z", LA, 1.4)  # lá ráy
        s.line(f"M{x} {y}V{y-44}", LUC, 1)
    s.pig(60, 152 - 10, 0.45)
    return s.frame()

def m12():  # Tết: bánh chưng, chậu đào, dây pháo
    s = paper_dots(S())
    s.line("M60 14V70", SON, 1.4)
    for i in range(6):
        s.p(SON, 54, 22 + i * 8, 66, 22 + i * 8, 66, 28 + i * 8, 54, 28 + i * 8, w=1)
    s.ground(150, "#D8B57A")
    s.banh_chung(96, 112, 36).banh_chung(138, 118, 30).banh_chung(116, 82, 30)
    s.branch("M246 122C244 96 236 80 222 62M244 100C256 88 268 80 284 76M238 84C246 70 250 56 248 40M230 72C220 66 210 64 200 66",
             [(222, 62), (284, 76), (248, 40), (200, 66), (266, 84), (238, 52), (212, 70), (258, 60)])
    s.fill("M224 150h44l-6 -28h-32z", CHAM)
    s.line("M230 132h32", VANG, 2)
    return s.frame()

SCENES = [m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12]
TITLES = ["Gà gáy đón xuân", "Mục đồng thổi sáo", "Cấy lúa đồng chiêm", "Cá chép vờn trăng", "Đầm sen mùa hạ",
          "Kéo vó trên sông", "Chăn vịt đồng chiều", "Rước đèn Trung thu", "Gặt lúa mùa vàng", "Gà mẹ gà con",
          "Lợn ăn lá ráy", "Bánh chưng ngày Tết"]

if __name__ == "__main__":
    import cairosvg, sys
    from PIL import Image
    import io
    tiles = []
    for i, fn in enumerate(SCENES):
        png = cairosvg.svg2png(bytestring=fn().svg().encode())
        tiles.append(Image.open(io.BytesIO(png)))
    W, H = tiles[0].size
    sheet = Image.new("RGB", (W * 3, H * 4), "white")
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % 3) * W, (i // 3) * H))
    sheet.save("/home/claude/gen/scenes_sheet.png")
    print("ok", W, H)
