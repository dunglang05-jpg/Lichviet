import vn.lichviet.core.*;
public class Dump { public static void main(String[] a){
 int y0=Integer.parseInt(a[0]), y1=Integer.parseInt(a[1]);
 StringBuilder sb=new StringBuilder("date,ld,lm,leap,can,chi,tk,mlen_last\n");
 for(int jd=LunarCalendar.jdFromDate(1,1,y0); jd<=LunarCalendar.jdFromDate(31,12,y1); jd++){
   int[] d=LunarCalendar.jdToDate(jd); DayInfo x=DayInfo.of(d[0],d[1],d[2]);
   double lon=LunarCalendar.sunLongitudeDeg(jd,23.99); int k=(int)Math.floor(((lon+45.0)%360.0)/30.0); // 0 = Dần month
   int[] n=LunarCalendar.jdToDate(jd+1); int[] ln=LunarCalendar.solarToLunar(n[0],n[1],n[2]);
   sb.append(String.format("%04d-%02d-%02d,%d,%d,%d,%d,%d,%d,%d\n",d[2],d[1],d[0],x.lunarDay,x.lunarMonth,x.leap?1:0,x.dayCan,x.dayChi,k,ln[0]==1?1:0));
 }
 System.out.print(sb);
}}
