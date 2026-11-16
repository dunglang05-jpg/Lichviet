"""Lấy tranh cho lịch tháng (chạy trên GitHub Actions).

1) Tranh làng quê do AI vẽ (Cloudflare Workers AI) -> app/src/main/assets/art/dongque/01..12.jpg
2) Ứng viên tranh Đông Hồ thật từ Wikimedia Commons, CHỈ ảnh có giấy phép tự do
   -> tools/art/candidates/donghoNN.jpg + credits.json + sheet.jpg (để chọn 12 bức)

Biến môi trường: CF_ACCOUNT_ID, CF_API_TOKEN. Tham số: ai | dongho | all
"""
import base64
import io
import json
import os
import re
import sys
import time
import urllib.parse
import urllib.request

from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
UA = "LichVietCalendarApp/1.0 (https://github.com/dunglang05-jpg/Lichviet)"

STYLE = (". Detailed vintage Vietnamese illustration in the style of a retro Vietnamese calendar painting: fine pen lines with "
         "gouache and colored pencil, aged beige paper texture, muted warm earthy colors (terracotta red, ochre yellow, olive green, "
         "faded pink), rich detail in people, trees and buildings, small decorative auspicious clouds and lotus ornaments in the top "
         "corners, scenery spread along the bottom half with soft open sky above. Clearly Vietnamese people, clothes and places "
         "(ao dai, ao ba ba, non la conical hats, Vietnamese flags where fitting). Not Chinese, not Japanese, no text, no letters, no watermark")
# Mỗi tháng một cảnh đúng mùa, đậm chất Việt Nam
PROMPTS = [
    # 1 Tết
    "Tet in a northern Vietnamese village: families in red and yellow ao dai, pink peach blossom branches and kumquat trees, a tray of square banh chung wrapped in green dong leaves, red lanterns, old houses with brown tiled roofs, children receiving lucky money",
    # 2 Xuân, hội làng
    "spring village festival in northern Vietnam: villagers in ao tu than and non la walk to the dinh lang communal house, colorful five-color festival flags, a wrestling ring, a swing game, fresh green bamboo, light spring mist",
    # 3 hoa gạo
    "late spring in the Red River Delta: a big red silk-cotton tree (cay gao) in full bloom at the village edge, a boy riding a water buffalo on the dike, women in non la carrying baskets, young green rice fields and a village pond",
    # 4 Giỗ Tổ, loa kèn
    "April in Hanoi: a girl in white ao dai cycling with a basket of white Easter lilies (hoa loa ken) along a tree-lined street with yellow French colonial houses, street vendors with shoulder poles, old tram-era lamp posts",
    # 5 phượng, ngày lễ
    "early summer in Vietnam: red flame trees (hoa phuong) blooming over a yellow schoolyard, pupils in white shirts and red scarves, families walking with small red flags, a lake promenade, festive atmosphere",
    # 6 sen
    "summer lotus pond in northern Vietnam: pink lotus flowers and big leaves, a woman in non la picking lotus from a round basket boat (thung chai), an old Vietnamese one-pillar style small shrine by the water, willow trees, white egrets",
    # 7 mưa ngâu, bằng lăng
    "July rainy season in Hue, Vietnam: purple crape myrtle (bang lang) trees along the Perfume River, a dragon boat, girls in white ao dai with non la and bicycles, soft summer rain",
    # 8 nước nổi miền Tây
    "flood season in the Mekong Delta: a floating market with wooden boats full of fruits, women in ao ba ba and non la rowing xuong ba la sampans, stilt houses with nipa palm roofs, coconut trees, a monkey bridge, yellow dien dien flowers",
    # 9 Quốc khánh, Trung thu
    "September in Hanoi: the Hanoi Flag Tower with a big red flag with a yellow star, streets decorated with red flags, families in ao dai, and in the evening children carrying red star lanterns (den ong sao) with a lion dance and a full moon",
    # 10 gặt lúa
    "autumn harvest in the northern Vietnamese countryside: golden ripe rice fields, farmers in non la cutting rice and carrying sheaves on shoulder poles, straw drying on the village road, buffalo carts, a bamboo-hedged village",
    # 11 cúc họa mi, Hà Nội thu
    "late autumn in Hanoi: a woman street vendor with a bicycle loaded with white daisies (cuc hoa mi) by Hoan Kiem Lake with the red The Huc bridge and Turtle Tower, yellow leaves, light morning mist",
    # 12 đông, hoa cải
    "winter in the northern Vietnamese highlands: fields of yellow mustard flowers and pink buckwheat flowers, H'mong people in colorful traditional clothes, wooden houses with smoke, terraced fields, misty mountains",
]


def http(url, data=None, headers=None, timeout=120):
    req = urllib.request.Request(url, data=data, headers=headers or {})
    with urllib.request.urlopen(req, timeout=timeout) as r:
        return r.read(), r.headers.get("Content-Type", "")


def crop_16_9(img, width=1280):
    w, h = img.size
    th = int(w * 9 / 16)
    if th <= h:
        top = (h - th) // 2
        img = img.crop((0, top, w, top + th))
    return img.convert("RGB").resize((width, int(width * 9 / 16)), Image.LANCZOS)


def run_ai(only=None):
    acc = os.environ["CF_ACCOUNT_ID"].strip()
    tok = os.environ["CF_API_TOKEN"].strip()
    out = os.path.join(ROOT, "app/src/main/assets/art/dongque")
    os.makedirs(out, exist_ok=True)
    models = ["@cf/black-forest-labs/flux-1-schnell", "@cf/bytedance/stable-diffusion-xl-lightning",
              "@cf/stabilityai/stable-diffusion-xl-base-1.0"]
    for i, p in enumerate(PROMPTS, start=1):
        if only and i not in only:
            continue
        path = os.path.join(out, f"{i:02d}.jpg")
        for model in models:
            body = {"prompt": p + STYLE}
            if "flux" in model:
                body["steps"] = 8
            else:
                body.update({"width": 1344, "height": 768, "negative_prompt": "text, watermark, cartoon, anime, blurry"})
            try:
                data, ctype = http(
                    f"https://api.cloudflare.com/client/v4/accounts/{acc}/ai/run/{model}",
                    json.dumps(body).encode(),
                    {"Authorization": f"Bearer {tok}", "Content-Type": "application/json"},
                )
                if "json" in ctype:
                    raw = base64.b64decode(json.loads(data)["result"]["image"])
                else:
                    raw = data
                img = crop_16_9(Image.open(io.BytesIO(raw)))
                img.save(path, "JPEG", quality=84, optimize=True, progressive=True)
                print(f"AI {i:02d} ok ({model}) {os.path.getsize(path)//1024} KB")
                break
            except Exception as e:  # thử model khác
                print(f"AI {i:02d} {model} lỗi: {e}")
                time.sleep(2)
        else:
            print(f"AI {i:02d} THẤT BẠI")


GIAP = ["rat", "water buffalo", "tiger", "cat", "dragon", "snake", "horse", "goat", "monkey", "rooster", "dog", "pig"]
GIAP_STYLE = (", a single cute cartoon {a} sitting, Vietnamese Dong Ho folk painting style, bold clean outlines, flat warm colors "
              "(vermilion red, ochre, indigo, moss green), centered on a plain cream background, full body, sticker style, no text")


def run_giap():
    """12 con giáp kiểu Việt Nam (Mèo thay Thỏ, Trâu nước) -> app/src/main/assets/giap/00..11.png (nền kem)."""
    acc = os.environ["CF_ACCOUNT_ID"].strip()
    tok = os.environ["CF_API_TOKEN"].strip()
    out = os.path.join(ROOT, "app/src/main/assets/giap")
    os.makedirs(out, exist_ok=True)
    for i, a in enumerate(GIAP):
        path = os.path.join(out, f"{i:02d}.jpg")
        for model in ["@cf/black-forest-labs/flux-1-schnell", "@cf/bytedance/stable-diffusion-xl-lightning"]:
            body = {"prompt": GIAP_STYLE.format(a=a)}
            if "flux" in model:
                body["steps"] = 8
            try:
                data, ctype = http(f"https://api.cloudflare.com/client/v4/accounts/{acc}/ai/run/{model}", json.dumps(body).encode(),
                                   {"Authorization": f"Bearer {tok}", "Content-Type": "application/json"})
                raw = base64.b64decode(json.loads(data)["result"]["image"]) if "json" in ctype else data
                img = Image.open(io.BytesIO(raw)).convert("RGB")
                s = min(img.size); img = img.crop(((img.width - s) // 2, (img.height - s) // 2, (img.width + s) // 2, (img.height + s) // 2)).resize((384, 384))
                img.save(path, "JPEG", quality=85, optimize=True)
                print(f"GIAP {i:02d} {a} ok ({model})")
                break
            except Exception as e:
                print(f"GIAP {i:02d} {model} lỗi: {e}")
                time.sleep(2)


FREE = re.compile(r"^(public domain|pd|cc0|cc by(-sa)? ?\d(\.\d)?)", re.I)


def strip_html(s):
    return re.sub(r"<[^>]+>", "", s or "").strip()


def commons(params):
    params.update({"format": "json", "formatversion": "2"})
    url = "https://commons.wikimedia.org/w/api.php?" + urllib.parse.urlencode(params)
    data, _ = http(url, headers={"User-Agent": UA}, timeout=60)
    return json.loads(data)


def run_dongho():
    out = os.path.join(ROOT, "tools/art/candidates")
    os.makedirs(out, exist_ok=True)
    files, seen_cats = {}, set()
    # 1) tìm thẳng file theo từ khoá
    for q in ["Đông Hồ painting", "Dong Ho painting", "tranh Đông Hồ", "Dong Ho woodblock print", "Đông Hồ folk painting",
              "tranh dân gian Đông Hồ", "Dong Ho folk"]:
        try:
            j = commons({"action": "query", "list": "search", "srsearch": q, "srnamespace": "6", "srlimit": "100"})
            for m in j.get("query", {}).get("search", []):
                if re.search(r"\.(jpe?g|png)$", m["title"], re.I):
                    files.setdefault(m["title"], "search:" + q)
        except Exception as e:
            print("search lỗi", q, e)
    # 2) danh mục liên quan (tìm theo tên) và file trong đó
    cats = []
    for q in ["Dong Ho painting", "Đông Hồ"]:
        try:
            j = commons({"action": "query", "list": "search", "srsearch": q, "srnamespace": "14", "srlimit": "20"})
            cats += [m["title"] for m in j.get("query", {}).get("search", [])]
        except Exception as e:
            print("catsearch lỗi", e)
    print("categories found:", cats)
    while cats and len(seen_cats) < 25:
        c = cats.pop(0)
        if c in seen_cats:
            continue
        seen_cats.add(c)
        try:
            j = commons({"action": "query", "list": "categorymembers", "cmtitle": c, "cmlimit": "500", "cmtype": "file|subcat"})
        except Exception as e:
            print("cat lỗi", c, e)
            continue
        for m in j.get("query", {}).get("categorymembers", []):
            if m["ns"] == 14:
                cats.append(m["title"])
            elif m["ns"] == 6 and re.search(r"\.(jpe?g|png)$", m["title"], re.I):
                files.setdefault(m["title"], c)
    print("categories", len(seen_cats), "files", len(files))
    titles = list(files)
    credits, n = [], 0
    for k in range(0, len(titles), 40):
        j = commons({"action": "query", "titles": "|".join(titles[k:k + 40]), "prop": "imageinfo",
                     "iiprop": "url|extmetadata|size", "iiurlwidth": "1280"})
        for page in j.get("query", {}).get("pages", []):
            ii = (page.get("imageinfo") or [{}])[0]
            md = ii.get("extmetadata", {})
            lic = strip_html(md.get("LicenseShortName", {}).get("value", ""))
            if not FREE.match(lic) or ii.get("width", 0) < 500:
                continue
            n += 1
            name = f"dongho{n:02d}.jpg"
            try:
                raw, _ = http(ii.get("thumburl") or ii["url"], headers={"User-Agent": UA})
                img = Image.open(io.BytesIO(raw)).convert("RGB")
                img.thumbnail((1280, 1280))
                img.save(os.path.join(out, name), "JPEG", quality=85)
            except Exception as e:
                print("tải lỗi", page["title"], e)
                n -= 1
                continue
            credits.append({
                "file": name, "title": page["title"], "page": ii.get("descriptionurl", ""),
                "artist": strip_html(md.get("Artist", {}).get("value", "")),
                "license": lic, "license_url": md.get("LicenseUrl", {}).get("value", ""),
                "description": strip_html(md.get("ImageDescription", {}).get("value", ""))[:200],
                "category": files[page["title"]],
            })
            time.sleep(0.5)
            if n >= 60:
                break
        if n >= 60:
            break
    json.dump(credits, open(os.path.join(out, "credits.json"), "w"), ensure_ascii=False, indent=1)
    # bảng xem nhanh có đánh số
    if credits:
        tw, th, cols = 320, 220, 6
        rows = (len(credits) + cols - 1) // cols
        sheet = Image.new("RGB", (cols * tw, rows * th), "white")
        d = ImageDraw.Draw(sheet)
        for i, c in enumerate(credits):
            im = Image.open(os.path.join(out, c["file"]))
            im.thumbnail((tw - 10, th - 30))
            x, y = (i % cols) * tw, (i // cols) * th
            sheet.paste(im, (x + 5, y + 5))
            d.text((x + 6, y + th - 22), f"{i+1:02d} {c['license'][:16]}", fill="black")
        sheet.save(os.path.join(out, "sheet.jpg"), "JPEG", quality=80)
    print("Đông Hồ (giấy phép tự do):", len(credits))


if __name__ == "__main__":
    mode = sys.argv[1] if len(sys.argv) > 1 else "all"
    if mode == "giap":
        run_giap()
    if mode.startswith("ai:"):  # vd ai:1,2,9 -> chỉ vẽ lại các tháng này
        run_ai({int(x) for x in mode[3:].split(",") if x.strip()})
    if mode in ("ai", "all"):
        run_ai()
    if mode in ("dongho", "all"):
        run_dongho()
