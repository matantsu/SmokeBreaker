package com.smokebreaker.www.pl.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.smokebreaker.www.App;
import com.smokebreaker.www.BackgroundService;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.models.Tip;

import javax.inject.Inject;

/**
 * Implementation of App Widget functionality.
 */
public class TipWidget extends AppWidgetProvider {


    @Inject
    TipsManager tipsManager;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        App.getAppComponent(context).inject(this);
        Tip tip = tipsManager.randomTip();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tip_widget);

        if(tip != null){
            views.setTextViewText(R.id.tipText,tip.getTip());
            Utils.setUserWidget(context,views,R.id.tipUser,R.id.userImage,tip.getAuthor());
        }
        else{
            views.setViewVisibility(R.id.tipUser, View.GONE);
            views.setTextViewText(R.id.tipText,"No tip yet...");
        }

        views.setOnClickPendingIntent(R.id.root,PendingIntent.getActivity(
                context,
                6852,
                new Intent(context,SplashScreenActivity.class).putExtra("page", MainActivity.Page.TIPS.getValue()),
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

