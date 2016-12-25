package com.smokebreaker.www.pl.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.smokebreaker.www.App;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;

import javax.inject.Inject;

/**
 * Implementation of App Widget functionality.
 */
public class StatisticsWidget extends AppWidgetProvider {

    @Inject
    Settings settings;

    @Inject
    Statistics statistics;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        App.getAppComponent(context).inject(this);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.statistics_widget);

        if(settings.isColdTurkey()){
            if(statistics.timeSmokeFree() < 60*1000)
                views.setTextViewText(R.id.cigarettesSmokedToday,"Just stopped smoking for good !");
            else if(statistics.timeSmokeFree() < 60*60*1000)
                views.setTextViewText(R.id.cigarettesSmokedToday,context.getString(R.string.quit_minutes,statistics.timeSmokeFree()/(60*1000)));
            else if(statistics.timeSmokeFree() < 24*60*60*1000)
                views.setTextViewText(R.id.cigarettesSmokedToday,context.getString(R.string.quit_hours,statistics.timeSmokeFree()/(60*60*1000)));
            else
                views.setTextViewText(R.id.cigarettesSmokedToday,context.getString(R.string.quit_days,statistics.timeSmokeFree()/(24*60*60*1000)));
        }
        else
            views.setTextViewText(R.id.cigarettesSmokedToday,context.getString(R.string.cigarettes_smoked_today,statistics.cigarettesSmokedToday()));

        views.setViewVisibility(R.id.progressLayout,statistics.isReady() ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.progressEmptyLayout,!statistics.isReady() ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.progressChartLayout,!settings.isColdTurkey() && statistics.getDays().size() > 0 ? View.VISIBLE : View.GONE);
        if(statistics.isReady())
            setStatistics(views,context);

        views.setOnClickPendingIntent(R.id.root, PendingIntent.getActivity(
                context,
                563,
                new Intent(context,SplashScreenActivity.class).putExtra("page", MainActivity.Page.STATISTICS.getValue()),
                PendingIntent.FLAG_IMMUTABLE));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setStatistics(RemoteViews views, Context context) {
        views.setTextViewText(R.id.moneySaved,context.getString(R.string.money,statistics.moneySaved(),settings.getCurrency().getSymbol()));

        if(statistics.timeRegained() < 60)
            views.setTextViewText(R.id.timeRegained,context.getString(R.string.minutes,statistics.timeRegained()));
        else if(statistics.timeRegained() < 24*60)
            views.setTextViewText(R.id.timeRegained,context.getString(R.string.hours,(int)(statistics.timeRegained()/60)));
        else
            views.setTextViewText(R.id.timeRegained,context.getString(R.string.days,(int)(statistics.timeRegained()/(60*24))));

        views.setTextViewText(R.id.cigarettesAvoided,context.getString(R.string.cigarettes,statistics.cigarettesAvoided()));
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

