# Công cụ tái tạo AlmanacData.java từ bộ dữ liệu ILamTH/du-lieu-ngay-tot-xau (MIT).
# Chạy trong thư mục làm việc có data.pkl/cal.csv... (xem README, mục "Tái tạo dữ liệu").
import pickle,collections,csv,json
data_st=pickle.load(open('stars.pkl','rb'))
cal={r['date']:r for r in csv.DictReader(open('cal.csv'))}
tk={l.split(',')[0]:list(map(int,l.strip().split(',')[1:])) for l in open('tk.txt')}
dates=sorted(data_st)
rows=[]
for d in dates:
    r=cal[d]; can=int(r['can']); chi=int(r['chi']); cc=None
    # canchi index 0..59
    for i in range(60):
        if i%10==can and i%12==chi: cc=i;break
    k=tk[d][2]; lm=int(r['lm']); ld=int(r['ld'])
    rows.append(dict(d=d,can=can,chi=chi,cc=cc,tk=k,lm=lm,ld=ld,season_tk=k//3,season_lm=(lm-1)//3,last=int(r['mlen_last'])))
keys={
 'tk_chi':lambda x:(x['tk'],x['chi']), 'lm_chi':lambda x:(x['lm'],x['chi']),
 'tk_can':lambda x:(x['tk'],x['can']), 'lm_can':lambda x:(x['lm'],x['can']),
 'tk_cc':lambda x:(x['tk'],x['cc']), 'lm_cc':lambda x:(x['lm'],x['cc']),
 'cc':lambda x:x['cc'], 'stk_cc':lambda x:(x['season_tk'],x['cc']), 'slm_cc':lambda x:(x['season_lm'],x['cc']),
 'lm_ld':lambda x:(x['lm'],x['ld']), 'ld':lambda x:x['ld'], 'stk_chi':lambda x:(x['season_tk'],x['chi']),'slm_chi':lambda x:(x['season_lm'],x['chi']),
 'stk_can':lambda x:(x['season_tk'],x['can']),'slm_can':lambda x:(x['season_lm'],x['can']),
}
allstars=sorted({(t,n) for g,b in data_st.values() for t,n in [('g',s) for s in g]+[('b',s) for s in b]})
pres={s:[] for s in allstars}
for x in rows:
    g,b=data_st[x['d']]; S=set([('g',s) for s in g]+[('b',s) for s in b])
    for s in allstars: pres[s].append(s in S)
res={}
for s in allstars:
    best=None
    for kn,kf in keys.items():
        cnt=collections.defaultdict(lambda:[0,0])
        for x,p in zip(rows,pres[s]):
            c=cnt[kf(x)]; c[0]+=1; c[1]+=p
        acc=sum(max(c[1],c[0]-c[1]) for c in cnt.values())/len(rows)
        # tiebreak prefer fewer keys
        if best is None or acc>best[1]+1e-9 or (abs(acc-best[1])<1e-9 and len(cnt)<best[2]): best=(kn,acc,len(cnt))
    res[s]=best
    print(f"{s[0]} {s[1]:<32} {best[0]:<8} {best[1]*100:.2f}%  n={sum(pres[s])}")
pickle.dump((rows,pres,res),open('fit.pkl','wb'))
