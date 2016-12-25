package com.smokebreaker.www.pl.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.smokebreaker.www.BackgroundService;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;

import java.util.Date;

public class SmokeBreakNotification {
    private static final String NOTIFICATION_TAG = "SmokeBreak";
    public static void notify(final Context context, Uri sound) {
        if(sound == null)
            sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final Resources res = context.getResources();
        final String title = context.getString(R.string.smoke_break_notification_title);
        final String text = res.getString(R.string.smoke_break_notification_text);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_ic_smoking_rooms_white_48dp)
                .setSound(sound)
                .setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .setWhen(new Date().getTime())
                .setUsesChronometer(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, SplashScreenActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title))
                .addAction(
                        R.drawable.ic_stat_content_remove_circle_outline,
                        context.getString(R.string.dismiss),
                        PendingIntent.getService(
                                context,
                                3,
                                new Intent(context,BackgroundService.class)
                                        .putExtra("action","dismiss"),
                                PendingIntent.FLAG_ONE_SHOT))
                .addAction(
                        R.drawable.ic_stat_ic_smoking_rooms_white_48dp,
                        context.getString(R.string.smoke),
                        PendingIntent.getService(
                                context,
                                2,
                                new Intent(context,BackgroundService.class)
                                        .putExtra("action","smoke"),
                                PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.sound = sound;
        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

        notify(context, notification);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
