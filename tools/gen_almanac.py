# Công cụ tái tạo AlmanacData.java từ bộ dữ liệu ILamTH/du-lieu-ngay-tot-xau (MIT).
# Chạy trong thư mục làm việc có data.pkl/cal.csv... (xem README, mục "Tái tạo dữ liệu").
import json,re,unicodedata,pickle
N=lambda s: unicodedata.normalize('NFC',s or '')
stars=json.load(open('stars.json')); misc=json.load(open('misc.json'))
bt_can,bt_chi,hy,tai,hac,nap,rel,kyt=pickle.load(open('misc.pkl','rb'))
def js(s): return '"'+s.replace('\\','\\\\').replace('"','\\"')+'"'
def clean(s):
    s=N(s).strip().rstrip('.').strip()
    return s[0].upper()+s[1:] if s else s
L=[]
L.append('package vn.lichviet.core;\n')
L.append('/**\n * Bảng dữ liệu xem ngày (TỰ SINH bởi tools/gen_almanac.py, đừng sửa tay).\n * Quy tắc sao Ngọc Hạp Thông Thư được rút ra và đối chiếu với bộ dữ liệu\n * ILamTH/du-lieu-ngay-tot-xau (MIT) cho 1900–2200; mô tả ngắn lấy từ bộ dữ liệu đó.\n */')
L.append('final class AlmanacData {\n    private AlmanacData() {}\n')
# stars
stars=sorted(stars,key=lambda s:(not s['good'],s['name']))
L.append('    static final String[] STAR_NAME = {'+', '.join(js(s['name']) for s in stars)+'};')
L.append('    static final boolean[] STAR_GOOD = {'+', '.join('true' if s['good'] else 'false' for s in stars)+'};')
L.append('    static final String[] STAR_DESC = {\n        '+',\n        '.join(js(clean(s['desc'])) for s in stars)+'\n    };')
L.append('    /** STAR_MASK[sao][tháng âm 0..11]: bit i bật khi ngày có can chi thứ i (0 = Giáp Tý). */')
L.append('    static final long[][] STAR_MASK = {\n'+',\n'.join('        {'+', '.join(f'{int(m)}L' for m in s['masks'])+'}' for s in stars)+'\n    };')
# nap am, tuoi ky, exceptions
L.append('    static final String[] NAP_AM = {'+', '.join(js(n[0]) for n in misc['nap'])+'};')
L.append('    static final String[] TUOI_KY = {'+', '.join(js(n[0].replace(' và ',', ')) for n in misc['kyt'])+'};')
exc=[]
for t,c in misc['exc']:
    m=re.search(r'tuổi: (.+?)\s*(?:vì|thuộc)',N(t).replace('Ngọvà','Ngọ và'))
    exc.append(m.group(1).replace(' và ',', ') if m else '')
L.append('    /** Tuổi được ngoại lệ (không bị hành ngày khắc). */')
L.append('    static final String[] TUOI_NGOAI_LE = {'+', '.join(js(e) for e in exc)+'};')
L.append('    static final String[] BANH_TO_CAN = {\n        '+',\n        '.join(js(N(bt_can[i].most_common(1)[0][0])) for i in range(10))+'\n    };')
L.append('    static final String[] BANH_TO_CHI = {\n        '+',\n        '.join(js(N(bt_chi[i].most_common(1)[0][0])) for i in range(12))+'\n    };')
L.append('    static final String[] HOP_XUNG = {\n        '+',\n        '.join(js(N(h[0]).replace('Ngày ','',1)) for h in misc['hop'])+'\n    };')
L.append('    static final String[] TAM_SAT = {'+', '.join(js(N(s[0]) if s[0] else '') for s in misc['sat'])+'};')
L.append('}')
open('/home/claude/lichviet/core/lunar/src/main/java/vn/lichviet/core/AlmanacData.java','w').write('\n'.join(L)+'\n')
print(len(stars),'stars written')
