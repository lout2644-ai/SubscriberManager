package com.twostor.subscribermanager;

import android.app.*;
import android.content.*;
import android.os.Build;

public class NotificationReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){long id=i.getLongExtra("id",-1);String name=i.getStringExtra("name");String title=i.getStringExtra("title");NotificationHelper.show(c,title==null?"تنبيه اشتراك":title,"اشتراك " + (name==null?"":name)+" يحتاج إلى مراجعة.");}
}
class NotificationHelper {
    static final String CH="subscriptions";
    static void create(Context c){if(Build.VERSION.SDK_INT>=26){NotificationManager m=c.getSystemService(NotificationManager.class);m.createNotificationChannel(new NotificationChannel(CH,"تنبيهات الاشتراكات",NotificationManager.IMPORTANCE_DEFAULT));}}
    static void show(Context c,String title,String body){create(c);if(Build.VERSION.SDK_INT>=33&&c.checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=0)return;PendingIntent pi=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,CH):new Notification.Builder(c);b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).setContentIntent(pi);c.getSystemService(NotificationManager.class).notify((int)(System.currentTimeMillis()%100000),b.build());}
}
