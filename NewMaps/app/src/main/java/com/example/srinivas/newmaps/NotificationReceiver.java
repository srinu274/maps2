package com.example.srinivas.newmaps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Srinivas on 02-09-2016.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Notif reci","in receive");
        ArrayList<LocationHandler> handlers=(ArrayList<LocationHandler>)
                intent.getSerializableExtra(PlacesSearchService.EXTRA_PLACES);
        if(handlers==null || handlers.size()<=0) return;
        Log.i("Notif reci","handlers received"+handlers.size());
        Intent i=new Intent(context,LocationMapActivity.class);
        i.putExtra(LocationMapActivity.EXTRA_PLACES,handlers);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi=PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =(NotificationCompat.Builder)new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.map_marker_hotel)
                .setContentText("found "+handlers.size()+" cafes nearby")
                .setContentTitle("New Cafes found")
                .setContentIntent(pi)
                .setAutoCancel(true);
        Log.i("Notif reci","after not");
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, notificationBuilder.build());
        Log.i("Notif reci","completed");
    }

}
