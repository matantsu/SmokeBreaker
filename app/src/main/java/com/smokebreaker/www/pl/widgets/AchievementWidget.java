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
import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.models.Achievement;

import javax.inject.Inject;

/**
 * Implementation of App Widget functionality.
 */
public class AchievementWidget extends AppWidgetProvider {

    @Inject
    AchievementsManager achievementsManager;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        App.getAppComponent(context).inject(this);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.achievement_widget);

        Achievement achievement = achievementsManager.getNext();
        if(achievement != null){
            views.setTextViewText(R.id.achievementTitle,achievement.getName());
            views.setTextViewText(R.id.achievementSubtitle,achievement.getDescription());

            views.setViewVisibility(R.id.achievementTrophy1,achievement.getRank() >= 0 ? View.VISIBLE : View.GONE);
            views.setViewVisibility(R.id.achievementTrophy2,achievement.getRank() >= 1 ? View.VISIBLE : View.GONE);
            views.setViewVisibility(R.id.achievementTrophy3,achievement.getRank() >= 2 ? View.VISIBLE : View.GONE);
        }
        else{
            views.setTextViewText(R.id.achievementTitle,"No achievement yet ...");
            views.setTextViewText(R.id.achievementSubtitle,"");
            views.setViewVisibility(R.id.achievementTrophy1,View.GONE);
            views.setViewVisibility(R.id.achievementTrophy2,View.GONE);
            views.setViewVisibility(R.id.achievementTrophy3,View.GONE);
        }

        views.setOnClickPendingIntent(R.id.root, PendingIntent.getActivity(
                context,
                3425,
                new Intent(context,SplashScreenActivity.class).putExtra("page", MainActivity.Page.ACHIEVEMENTS.getValue()),
                PendingIntent.FLAG_IMMUTABLE));

        // Instruct the widget manager to update the widget
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

