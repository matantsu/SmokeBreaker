package com.smokebreaker.www.pl.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;

public class CrossingCigarettesPerDayNotification {
    private static final String NOTIFICATION_TAG = "CrossingCigarettesPerDay";
    public static void notify(Context context) {
        final Resources res = context.getResources();
        final String title = context.getString(R.string.crossing_initial_notification_title);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.crossing_cigarretes_per_day_notification_text)))
                .setSmallIcon(R.drawable.ic_stat_alert_warning)
                .setContentTitle(title)
                .setContentText(context.getString(R.string.crossing_cigarretes_per_day_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, SplashScreenActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify((NOTIFICATION_TAG).hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context, String uid) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG + uid, 0);
        } else {
            nm.cancel((NOTIFICATION_TAG + uid).hashCode());
        }
    }
}
