package com.twostor.subscribermanager;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB="subscribers.db";
    private static final int VER=2;
    public DatabaseHelper(Context c){super(c,DB,null,VER);}
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE subscribers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,contact TEXT,type TEXT,username TEXT,password TEXT,start_date TEXT,end_date TEXT,renewed INTEGER DEFAULT 0,notes TEXT,new_password TEXT,new_notes TEXT,panel_info TEXT,custom_days INTEGER DEFAULT 30)");
        db.execSQL("CREATE TABLE renewals(id INTEGER PRIMARY KEY AUTOINCREMENT,subscriber_id INTEGER,payment_date TEXT,end_date TEXT,type TEXT,old_end_date TEXT,notes TEXT,created_at TEXT)");
        db.execSQL("CREATE INDEX idx_sub_end ON subscribers(end_date)");
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV){
        if(oldV<2) db.execSQL("CREATE TABLE IF NOT EXISTS renewals(id INTEGER PRIMARY KEY AUTOINCREMENT,subscriber_id INTEGER,payment_date TEXT,end_date TEXT,type TEXT,old_end_date TEXT,notes TEXT,created_at TEXT)");
    }
    public long insert(Subscriber s){
        ContentValues v=cv(s); return getWritableDatabase().insert("subscribers",null,v);
    }
    public int update(Subscriber s){return getWritableDatabase().update("subscribers",cv(s),"id=?",new String[]{String.valueOf(s.id)});}
    public int delete(long id){getWritableDatabase().delete("renewals","subscriber_id=?",new String[]{String.valueOf(id)});return getWritableDatabase().delete("subscribers","id=?",new String[]{String.valueOf(id)});}
    private ContentValues cv(Subscriber s){
        ContentValues v=new ContentValues(); v.put("name",s.name);v.put("contact",s.contact);v.put("type",s.type);v.put("username",s.username);v.put("password",s.password);v.put("start_date",s.startDate);v.put("end_date",s.endDate);v.put("renewed",s.renewed?1:0);v.put("notes",s.notes);v.put("new_password",s.newPassword);v.put("new_notes",s.newNotes);v.put("panel_info",s.panelInfo);v.put("custom_days",s.customDays);return v;
    }
    public Subscriber get(long id){Cursor c=getReadableDatabase().query("subscribers",null,"id=?",new String[]{String.valueOf(id)},null,null,null);try{if(c.moveToFirst())return from(c);return null;}finally{c.close();}}
    public List<Subscriber> all(String q){List<Subscriber> out=new ArrayList<>();String sel=null;String[] args=null;if(q!=null&&!q.trim().isEmpty()){sel="name LIKE ? OR contact LIKE ? OR username LIKE ? OR type LIKE ?";String x="%"+q.trim()+"%";args=new String[]{x,x,x,x};}Cursor c=getReadableDatabase().query("subscribers",null,sel,args,null,null,"CASE WHEN end_date='' THEN 1 ELSE 0 END, end_date ASC, name COLLATE NOCASE ASC");try{while(c.moveToNext())out.add(from(c));}finally{c.close();}return out;}
    private Subscriber from(Cursor c){Subscriber s=new Subscriber();s.id=c.getLong(c.getColumnIndexOrThrow("id"));s.name=c.getString(c.getColumnIndexOrThrow("name"));s.contact=c.getString(c.getColumnIndexOrThrow("contact"));s.type=c.getString(c.getColumnIndexOrThrow("type"));s.username=c.getString(c.getColumnIndexOrThrow("username"));s.password=c.getString(c.getColumnIndexOrThrow("password"));s.startDate=c.getString(c.getColumnIndexOrThrow("start_date"));s.endDate=c.getString(c.getColumnIndexOrThrow("end_date"));s.renewed=c.getInt(c.getColumnIndexOrThrow("renewed"))==1;s.notes=c.getString(c.getColumnIndexOrThrow("notes"));s.newPassword=c.getString(c.getColumnIndexOrThrow("new_password"));s.newNotes=c.getString(c.getColumnIndexOrThrow("new_notes"));s.panelInfo=c.getString(c.getColumnIndexOrThrow("panel_info"));s.customDays=c.getInt(c.getColumnIndexOrThrow("custom_days"));return s;}
    public long addRenewal(long sid,String payment,String end,String type,String oldEnd,String notes){ContentValues v=new ContentValues();v.put("subscriber_id",sid);v.put("payment_date",payment);v.put("end_date",end);v.put("type",type);v.put("old_end_date",oldEnd);v.put("notes",notes);v.put("created_at",MainActivity.nowDateTime());return getWritableDatabase().insert("renewals",null,v);}
    public List<String[]> renewals(long sid){List<String[]> r=new ArrayList<>();Cursor c=getReadableDatabase().query("renewals",new String[]{"payment_date","end_date","type","old_end_date","notes","created_at"},"subscriber_id=?",new String[]{String.valueOf(sid)},null,null,"id DESC");try{while(c.moveToNext())r.add(new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5)});}finally{c.close();}return r;}
    public int count(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM subscribers",null);try{c.moveToFirst();return c.getInt(0);}finally{c.close();}}
    public int countExpired(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM subscribers WHERE end_date < date('now','localtime')",null);try{c.moveToFirst();return c.getInt(0);}finally{c.close();}}
}
