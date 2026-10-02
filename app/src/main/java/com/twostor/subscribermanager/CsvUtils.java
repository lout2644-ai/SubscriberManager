package com.twostor.subscribermanager;

import android.content.*;
import android.net.Uri;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvUtils {
    public static String exportCsv(DatabaseHelper db){StringBuilder b=new StringBuilder("الرقم,الاسم الحقيقي,وسيلة التواصل,نوع الاشتراك,اسم المستخدم,كلمة السر,تاريخ الاشتراك,تاريخ النهاية,هل تم التجديد,ملاحظات,كلمة السر الجديدة,الملاحظات الجديدة,معلومات اللوحة\n");for(Subscriber s:db.all("")){String[] a={String.valueOf(s.id),s.name,s.contact,s.type,s.username,s.password,s.startDate,s.endDate,s.renewed?"نعم":"لا",s.notes,s.newPassword,s.newNotes,s.panelInfo};for(int i=0;i<a.length;i++){if(i>0)b.append(',');b.append('"').append(a[i].replace("\"","\"\"")).append('"');}b.append('\n');}return "\uFEFF"+b;}
    public static int importCsv(Context c,DatabaseHelper db,Uri uri)throws Exception{BufferedReader r=new BufferedReader(new InputStreamReader(c.getContentResolver().openInputStream(uri),StandardCharsets.UTF_8));String line=r.readLine();if(line!=null&&line.startsWith("\uFEFF"))line=line.substring(1);int n=0;while((line=r.readLine())!=null){List<String> f=parse(line);if(f.size()<13)continue;Subscriber s=new Subscriber();s.name=f.get(1).trim();if(s.name.isEmpty())continue;s.contact=f.get(2);s.type=f.get(3);s.username=f.get(4);s.password=f.get(5);s.startDate=f.get(6);s.endDate=f.get(7);s.renewed="نعم".equals(f.get(8));s.notes=f.get(9);s.newPassword=f.get(10);s.newNotes=f.get(11);s.panelInfo=f.get(12);db.insert(s);n++;}r.close();return n;}
    private static List<String> parse(String s){List<String> a=new ArrayList<>();StringBuilder x=new StringBuilder();boolean q=false;for(int i=0;i<s.length();i++){char c=s.charAt(i);if(c=='"'){if(q&&i+1<s.length()&&s.charAt(i+1)=='"'){x.append('"');i++;}else q=!q;}else if(c==','&&!q){a.add(x.toString());x.setLength(0);}else x.append(c);}a.add(x.toString());return a;}
}
