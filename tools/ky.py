# Công cụ tái tạo AlmanacData.java từ bộ dữ liệu ILamTH/du-lieu-ngay-tot-xau (MIT).
# Chạy trong thư mục làm việc có data.pkl/cal.csv... (xem README, mục "Tái tạo dữ liệu").
import pickle,collections,re,unicodedata,csv,json
N=lambda s: unicodedata.normalize('NFC',s or '')
data=pickle.load(open('data.pkl','rb'))
rows,_,_=pickle.load(open('fit.pkl','rb'))
names=collections.Counter(); per={}; descs=collections.defaultdict(collections.Counter)
for d,v in data.items():
    t=N(v['cac_ngay_ky']); ns=[]
    for m in re.finditer(r'^- ([^:]+?):\s*(.*)$',t,re.M):
        n=m.group(1).strip(); ns.append(n); names[n]+=1; descs[n][m.group(2).strip()]+=1
    per[d]=set(ns)
print(names)
keys={'ld':lambda x:x['ld'],'lm_ld':lambda x:(x['lm'],x['ld']),'lm_chi':lambda x:(x['lm'],x['chi']),'lm_can':lambda x:(x['lm'],x['can']),'lm_cc':lambda x:(x['lm'],x['cc']),'slm_chi':lambda x:(x['season_lm'],x['chi']),'cc':lambda x:x['cc'],'last':lambda x:(x['last'],),'ld_last':lambda x:(x['ld'],x['last'])}
out={}
for n in names:
    best=None
    for kn,kf in keys.items():
        cnt=collections.defaultdict(lambda:[0,0])
        for x in rows:
            c=cnt[kf(x)]; c[0]+=1; c[1]+= n in per[x['d']]
        acc=sum(max(c[1],c[0]-c[1]) for c in cnt.values())/len(rows)
        if best is None or acc>best[1]+1e-9: best=(kn,acc,{str(k):(c[1],c[0]) for k,c in cnt.items() if c[1]*2>c[0]})
    print(n,best[0],"%.3f"%(best[1]*100), sorted(best[2].items())[:40] if len(best[2])<45 else len(best[2]))
    out[n]=dict(rule=best[0],on=list(best[2].keys()),desc=descs[n].most_common(1)[0][0])
json.dump(out,open('ky.json','w'),ensure_ascii=False,indent=0)
