package com.smokebreaker.www.bl;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.smokebreaker.www.bl.models.Achievement;
import com.smokebreaker.www.bl.models.achievements.CigarettesAvoided100Achievement;
import com.smokebreaker.www.bl.models.achievements.CigarettesAvoided10Achievement;
import com.smokebreaker.www.bl.models.achievements.CigarettesAvoided1Achievement;
import com.smokebreaker.www.bl.models.achievements.CigarettesAvoided30Achievement;
import com.smokebreaker.www.bl.models.achievements.CigarettesAvoided3Achievement;
import com.smokebreaker.www.bl.models.achievements.FirstLogAchievement;
import com.smokebreaker.www.bl.models.achievements.Save100Achievement;
import com.smokebreaker.www.bl.models.achievements.Save10Achievement;
import com.smokebreaker.www.bl.models.achievements.Save1Achievement;
import com.smokebreaker.www.bl.models.achievements.Save4Achievement;
import com.smokebreaker.www.bl.models.achievements.TimeRegained15MinsAchievement;
import com.smokebreaker.www.bl.models.achievements.TimeRegained1DayAchievement;
import com.smokebreaker.www.bl.models.achievements.TimeRegained1HourAchievement;
import com.smokebreaker.www.bl.models.achievements.TimeRegained30MinsAchievement;
import com.smokebreaker.www.pl.notifications.AchievementUnlockedNotification;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class AchievementsManagerImpl implements AchievementsManager {

    private final Statistics statistics;
    private final Application application;
    private final Settings settings;
    SharedPreferences sharedPreferences;
    List<Achievement> achievements = new LinkedList<>();
    private Context context;

    HashMap<Achievement,Boolean> isAchieved = new HashMap<>();

    @Inject
    public AchievementsManagerImpl(Context context, Application application, Statistics statistics, Settings settings) {
        this.context = context;
        this.statistics = statistics;
        this.application = application;
        this.settings = settings;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);

        BroadcastReceiver randomTipReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkAchievements();
            }
        };

        application.registerReceiver(randomTipReceiver,new IntentFilter(application.getPackageName()+"achievementCheck"));
        AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                0,
                AlarmManager.INTERVAL_HALF_HOUR,
                PendingIntent.getBroadcast(application,
                        12,
                        new Intent(application.getPackageName()+"achievementCheck"),
                        PendingIntent.FLAG_CANCEL_CURRENT));

        statistics.smokesStream()
                .subscribe(t->checkAchievements());

        achievements = new LinkedList<>();

        if(!settings.isColdTurkey())
            achievements.add(new FirstLogAchievement(application,settings,statistics));
        achievements.add(new CigarettesAvoided1Achievement(application,settings,statistics));
        achievements.add(new CigarettesAvoided3Achievement(application,settings,statistics));
        achievements.add(new CigarettesAvoided10Achievement(application,settings,statistics));
        achievements.add(new CigarettesAvoided30Achievement(application,settings,statistics));
        achievements.add(new CigarettesAvoided100Achievement(application,settings,statistics));
        achievements.add(new Save1Achievement(application,settings,statistics));
        achievements.add(new Save4Achievement(application,settings,statistics));
        achievements.add(new Save10Achievement(application,settings,statistics));
        achievements.add(new Save100Achievement(application,settings,statistics));
        achievements.add(new TimeRegained15MinsAchievement(application,settings,statistics));
        achievements.add(new TimeRegained30MinsAchievement(application,settings,statistics));
        achievements.add(new TimeRegained1HourAchievement(application,settings,statistics));
        achievements.add(new TimeRegained1DayAchievement(application,settings,statistics));

        Collections.sort(achievements);

        checkAchievements(true);
    }

    @Override
    public List<Achievement> getAchievements(){
        return achievements;
    }

    @Override
    public List<Achievement> getUnachieved(){
        List<Achievement> unachieved = new LinkedList<>();
        for(Achievement ac: getAchievements())
            if(!ac.isAchieved())
                unachieved.add(ac);
        return unachieved;
    }

    @Override
    public Achievement getNext(){
        List<Achievement> unachieved = getUnachieved();
        return unachieved.size() > 0 ? unachieved.get(0) : null;
    }

    @Override
    public void checkAchievements(boolean silent) {
        for(Achievement achievement : achievements){
            Boolean b = isAchieved.get(achievement);
            b = b != null ? b : false;

            if(!b && achievement.isAchieved() && !silent)
                AchievementUnlockedNotification.notify(context,achievement);

            isAchieved.put(achievement,achievement.isAchieved());
        }
    }

    @Override
    public void checkAchievements() {
        checkAchievements(false);
    }
}
