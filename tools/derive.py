# Công cụ tái tạo AlmanacData.java từ bộ dữ liệu ILamTH/du-lieu-ngay-tot-xau (MIT).
# Chạy trong thư mục làm việc có data.pkl/cal.csv... (xem README, mục "Tái tạo dữ liệu").
import pickle,collections,json,re,unicodedata
N=lambda s: unicodedata.normalize('NFC',s or '')
rows,pres,res=pickle.load(open('fit.pkl','rb'))
data=pickle.load(open('data.pkl','rb'))
CC=[(i%10,i%12) for i in range(60)]
def keyf(kn,x):
    return {'lm_chi':(x['lm'],x['chi']),'lm_can':(x['lm'],x['can']),'lm_cc':(x['lm'],x['cc']),'slm_chi':(x['season_lm'],x['chi']),
            'slm_can':(x['season_lm'],x['can']),'slm_cc':(x['season_lm'],x['cc']),'stk_chi':(x['season_lm'],x['chi'])}[kn]
# descriptions
desc={}
for d,v in data.items():
    txt=N(v['ngoc_hap_thong_thu']); cur=None
    for line in txt.split('\n'):
        line=line.strip()
        if line.startswith('Sao tốt'): cur='g';continue
        if line.startswith('Sao xấu'): cur='b';continue
        m=re.match(r'-\s*([^:]+?)\s*:\s*(.*)',line)
        if m and cur: desc.setdefault((cur,m.group(1).strip()),collections.Counter())[m.group(2).strip()]+=1
out=[]; tot_ok=0; tot=0
for s,(kn,acc,_) in res.items():
    if kn=='stk_chi': kn='slm_chi'
    cnt=collections.defaultdict(lambda:[0,0])
    for x,p in zip(rows,pres[s]):
        c=cnt[keyf(kn,x)]; c[0]+=1; c[1]+=p
    on={k for k,c in cnt.items() if c[1]*2>c[0]}
    masks=[]
    for lm in range(1,13):
        m=0
        for i,(can,chi) in enumerate(CC):
            x=dict(lm=lm,season_lm=(lm-1)//3,can=can,chi=chi,cc=i)
            if keyf(kn,x) in on: m|=1<<i
        masks.append(m)
    # accuracy of expanded rule
    ok=sum(((masks[x['lm']-1]>>x['cc'])&1)==p for x,p in zip(rows,pres[s]))
    tot_ok+=ok; tot+=len(rows)
    out.append(dict(good=s[0]=='g',name=s[1],rule=kn,masks=[str(m) for m in masks],acc=ok/len(rows),desc=desc[s].most_common(1)[0][0]))
print("overall star accuracy %.3f%%"%(100*tot_ok/tot))
json.dump(out,open('stars.json','w'),ensure_ascii=False,indent=0)
# sanity print one
for o in out:
    if o['name'] in ('Thiên Đức','Nguyệt Đức','Thiên Phúc','Dương Thác','Quan Nhật'):
        cells=[ [f"{CC[i][0]},{CC[i][1]}" for i in range(60) if int(o['masks'][lm])>>i&1] for lm in range(12)]
        print(o['name'],o['rule'],[len(c) for c in cells], o['desc'][:60])
