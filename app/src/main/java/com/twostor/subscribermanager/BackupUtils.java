package com.twostor.subscribermanager;

import android.content.*;
import android.net.Uri;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BackupUtils {
    public static String makeJson(DatabaseHelper db){
        JSONObject root=new JSONObject();
        try{
            root.put("format","SubscriberManagerBackup");
            root.put("version",1);
            root.put("created_at",MainActivity.nowDateTime());

            JSONArray subs=new JSONArray();

            for(Subscriber s:db.all("")){
                JSONObject o=new JSONObject();

                o.put("id",s.id);
                o.put("name",s.name);
                o.put("contact",s.contact);
                o.put("type",s.type);
                o.put("username",s.username);
                o.put("password",s.password);
                o.put("start_date",s.startDate);
                o.put("end_date",s.endDate);
                o.put("renewed",s.renewed);
                o.put("notes",s.notes);
                o.put("new_password",s.newPassword);
                o.put("new_notes",s.newNotes);
                o.put("panel_info",s.panelInfo);
                o.put("custom_days",s.customDays);

                JSONArray rr=new JSONArray();

                for(String[] r:db.renewals(s.id)){
                    JSONObject x=new JSONObject();

                    x.put("payment_date",r[0]);
                    x.put("end_date",r[1]);
                    x.put("type",r[2]);
                    x.put("old_end_date",r[3]);
                    x.put("notes",r[4]);
                    x.put("created_at",r[5]);

                    rr.put(x);
                }

                o.put("renewals",rr);
                subs.put(o);
            }

            root.put("subscribers",subs);

            return root.toString(2);

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static int restore(Context ctx,DatabaseHelper db,Uri uri,boolean replace)throws Exception{
        String json=read(ctx,uri);
        JSONObject root=new JSONObject(json);

        if(!"SubscriberManagerBackup".equals(root.optString("format")))
            throw new IllegalArgumentException("ملف النسخ الاحتياطي غير صحيح");

        JSONArray arr=root.getJSONArray("subscribers");

        if(replace){
            for(Subscriber s:db.all(""))
                db.delete(s.id);
        }

        int n=0;

        for(int i=0;i<arr.length();i++){
            JSONObject o=arr.getJSONObject(i);

            Subscriber s=new Subscriber();

            s.name=o.optString("name");

            if(s.name.trim().isEmpty())
                continue;

            s.contact=o.optString("contact");
            s.type=o.optString("type","شهري");
            s.username=o.optString("username");
            s.password=o.optString("password");
            s.startDate=o.optString("start_date");
            s.endDate=o.optString("end_date");
            s.renewed=o.optBoolean("renewed");
            s.notes=o.optString("notes");
            s.newPassword=o.optString("new_password");
            s.newNotes=o.optString("new_notes");
            s.panelInfo=o.optString("panel_info");
            s.customDays=o.optInt("custom_days",30);

            long id=db.insert(s);

            JSONArray rr=o.optJSONArray("renewals");

            if(rr!=null){
                for(int j=0;j<rr.length();j++){
                    JSONObject x=rr.getJSONObject(j);

                    db.addRenewal(
                        id,
                        x.optString("payment_date"),
                        x.optString("end_date"),
                        x.optString("type"),
                        x.optString("old_end_date"),
                        x.optString("notes")
                    );
                }
            }

            n++;
        }

        return n;
    }

    private static String read(Context c,Uri u)throws Exception{
        try(
            InputStream in=c.getContentResolver().openInputStream(u);
            ByteArrayOutputStream out=new ByteArrayOutputStream()
        ){
            byte[] b=new byte[8192];
            int n;

            while((n=in.read(b))>0)
                out.write(b,0,n);

            return out.toString(StandardCharsets.UTF_8.name());
        }
    }

    public static void write(Context c,Uri u,String data)throws Exception{
        try(OutputStream out=c.getContentResolver().openOutputStream(u)){
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }
}
