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

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.BackgroundService;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;
import com.smokebreaker.www.bl.models.Tip;

public class TipNotification {
    private static final String NOTIFICATION_TAG = "Tip";
    public static void notify(Context context, Tip tip) {
        final Resources res = context.getResources();
        final String title = context.getString(R.string.tip_notification_title,tip.getAuthor().getDisplayName());
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tip.getTip()))
                .setSmallIcon(R.drawable.ic_stat_ic_info_white_48px)
                .setContentTitle(title)
                .setContentText(tip.getTip())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, SplashScreenActivity.class).putExtra("page", MainActivity.Page.TIPS.getValue()),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_stat_content_remove_circle_outline,
                        "Don't show again",
                        PendingIntent.getService(
                                context,
                                162,
                                new Intent(context,BackgroundService.class)
                                        .putExtra("isTipNotificationEnabled",false),
                                PendingIntent.FLAG_ONE_SHOT))
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
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel((NOTIFICATION_TAG).hashCode());
        }
    }
}
