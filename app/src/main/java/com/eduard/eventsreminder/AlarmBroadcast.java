package com.eduard.eventsreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class AlarmBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String eventNameNotf = bundle.getString("event");
        String date = bundle.getString("date") + " " + bundle.getString("time");

        //TODO: when click on notification goes to notification message activity
        Intent intent1 = new Intent(context,NotificationMessage.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("message", eventNameNotf);

        //TODO: build the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,intent1,PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notify_001");

        //TODO: Set all the properties for notification
        RemoteViews conentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);        //acces the personalized notification layout
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context,0,intent,0);
        conentView.setOnClickPendingIntent(R.id.snoozeBTN,pendingSwitchIntent);
        conentView.setTextViewText(R.id.Message, eventNameNotf);                                                //set the notification text as the name of the event
        conentView.setTextViewText(R.id.date,date);
        builder.setSmallIcon(R.drawable.alarm);                                                     //set the alarm icon (I did not found in android resources, vector asset, any icon to satisfy me, and I customized alarm.xml
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setOnlyAlertOnce(false);
        builder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        builder.setContent(conentView);
        builder.setContentIntent(pendingIntent);

        String channelId = "channel_id";
        NotificationChannel channel = new NotificationChannel(channelId,"channel_name",NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channelId);

        Notification notification = builder.build();
        notificationManager.notify(1,notification);

    }
}
