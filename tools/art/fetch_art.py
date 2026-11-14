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

STYLE = (", soft vintage watercolor painting on aged textured rice paper, muted earthy palette with sepia and soft green, "
         "delicate fine ink outlines, misty atmosphere, gentle warm light, traditional Vietnamese landscape illustration, "
         "calm and poetic, wide panoramic composition, empty sky area in the upper part, no text, no letters, no characters, no watermark, no frame")
PROMPTS = [
    "a northern Vietnamese village on Lunar New Year morning, pink peach blossom trees, old tiled-roof houses, a woman with a shoulder pole of flowers, a small stone bridge over a river",
    "misty spring in the northern Vietnamese countryside, bamboo groves, an ancient communal house gate, villagers in conical hats, light drizzle over green fields",
    "a boy riding a water buffalo and playing a bamboo flute on a rice field dike, limestone mountains in the distance, Vietnamese countryside",
    "farmers in conical hats transplanting rice in flooded paddies, white storks flying, distant mountains and a pagoda, Vietnam",
    "a vast lotus pond in summer with pink lotus flowers, a small wooden boat, an old pagoda by the water, northern Vietnam",
    "an old village banyan tree, a water well and communal house courtyard at summer noon, children flying a kite, Vietnam",
    "a fisherman lifting a square fishing net on a calm river at dawn, karst mountains reflected in water, Ninh Binh, Vietnam",
    "a duck herder guiding ducks across flooded fields in the rainy season, coconut palms, Mekong delta riverside houses",
    "a Vietnamese village at night during the Mid-Autumn festival, children with star lanterns walking along a river, big full moon",
    "golden rice harvest fields in the northern Vietnam countryside, farmers carrying rice sheaves on shoulder poles, haystacks, mountains",
    "late autumn evening in a Vietnamese village, smoke rising from kitchen roofs, straw drying along a path, a river and a temple gate",
    "a quiet misty winter morning in a northern Vietnamese village, an old market under a banyan tree, people in warm scarves, pale sun",
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


def run_ai():
    acc = os.environ["CF_ACCOUNT_ID"].strip()
    tok = os.environ["CF_API_TOKEN"].strip()
    out = os.path.join(ROOT, "app/src/main/assets/art/dongque")
    os.makedirs(out, exist_ok=True)
    models = ["@cf/black-forest-labs/flux-1-schnell", "@cf/bytedance/stable-diffusion-xl-lightning",
              "@cf/stabilityai/stable-diffusion-xl-base-1.0"]
    for i, p in enumerate(PROMPTS, start=1):
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
    if mode in ("ai", "all"):
        run_ai()
    if mode in ("dongho", "all"):
        run_dongho()
