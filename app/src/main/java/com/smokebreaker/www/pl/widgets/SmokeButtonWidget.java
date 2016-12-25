package com.smokebreaker.www.pl.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.BackgroundService;
import com.smokebreaker.www.R;

/**
 * Implementation of App Widget functionality.
 */
public class SmokeButtonWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.smoke_button_widget);
        views.setImageViewBitmap(R.id.icon, new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_smoking_rooms)
                .color(Color.WHITE).toBitmap());
        views.setOnClickPendingIntent(R.id.root, PendingIntent.getService(
                context,
                6154,
                new Intent(context,BackgroundService.class)
                        .putExtra("action","smoke"),
                PendingIntent.FLAG_IMMUTABLE));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

