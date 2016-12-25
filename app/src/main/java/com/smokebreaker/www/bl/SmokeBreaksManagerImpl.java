package com.smokebreaker.www.bl;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.smokebreaker.www.pl.notifications.SmokeBreakNotification;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class SmokeBreaksManagerImpl implements SmokeBreaksManager {

    private final Application application;
    private final FirebaseAnalytics analytics;
    private final Settings settings;

    public static class SmokeBreak {
        int index;
        boolean isLast;
        long timestamp;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isLast() {
            return isLast;
        }

        public void setLast(boolean last) {
            isLast = last;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    private final Statistics statistics;
    private final Config config;
    private final SharedPreferences prefs;

    AlarmManager alarmManager;

    @Inject
    public SmokeBreaksManagerImpl(Application application, Statistics statistics, Config config, Settings settings) {
        prefs = PreferenceManager.getDefaultSharedPreferences(application);
        analytics = FirebaseAnalytics.getInstance(application);
        this.application = application;
        this.statistics = statistics;
        this.config = config;
        this.settings = settings;

        BroadcastReceiver start = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onNextBreak();
            }
        };
        application.registerReceiver(start,new IntentFilter(application.getPackageName()+"smokeBreakStarted"));

        BroadcastReceiver end = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBreakEnd();
            }
        };
        application.registerReceiver(end,new IntentFilter(application.getPackageName()+"smokeBreakEnded"));

        alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
        setAlarm();

        statistics.smokesStream()
                .subscribe(s->{
                    if(isOnBreak())
                        setSmoked(true);
                });
    }

    private void setAlarm(){
        PendingIntent pi1 = PendingIntent.getBroadcast(application,
                579,
                new Intent(application.getPackageName()+"smokeBreakStarted"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent pi2 = PendingIntent.getBroadcast(application,
                135,
                new Intent(application.getPackageName()+"smokeBreakEnded"),
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    getNextBreakTime() - 1000,
                    pi1);
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    getNextBreakTime() - 1000 + config.smokeBreakDuration(),
                    pi2);
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    getNextBreakTime() - 1000,
                    pi1);
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    getNextBreakTime() - 1000 + config.smokeBreakDuration(),
                    pi2);
        }
    }

    private SmokeBreak getNextBreak(boolean alignToPast){
        if(getStart() == null || getEnd() == null || getNumberOfBreaks() < 1)
            return null;

        long time = new Date().getTime();
        long base = roundToStartOfDay(time);

        long start = base + (getStart()*60*1000);
        long end = base + (getEnd()*60*1000);

        if(end < start)
            end += 24*60*60*1000;

        float diff = (end - start);
        long interval = (long) (diff/(getNumberOfBreaks()-1));
        long nextBreakTime;
        boolean isLast;
        int index;

        if(alignToPast)
            time -= interval;

        if(time <= start){
            nextBreakTime = start;
            isLast = false;
            index = 0;
        }
        else if(time >= end)
        {
            nextBreakTime = start + 24L*60*60*1000;
            isLast = false;
            index = 0;
        }
        else{
            long offset = (time - start + interval);
            offset /= interval;
            offset *= interval;

            nextBreakTime = start + offset;
            index = (int) (offset/interval);
            isLast = index == (getNumberOfBreaks() - 1);
        }

        SmokeBreak smokeBreak = new SmokeBreak();
        smokeBreak.setIndex(index);
        smokeBreak.setLast(isLast);
        smokeBreak.setTimestamp(nextBreakTime);

        return smokeBreak;
    }

    @Override
    public long getNextBreakTime() {
        SmokeBreak smokeBreak = getNextBreak(false);
        return smokeBreak != null ? smokeBreak.getTimestamp() : -1;
    }

    @Override
    public long getPreviousBreakTime(){
        SmokeBreak smokeBreak = getNextBreak(true);
        return smokeBreak != null ? smokeBreak.getTimestamp() : -1;
    }

    @Override
    public int getNextIndex(){
        SmokeBreak smokeBreak = getNextBreak(false);
        return smokeBreak != null ? smokeBreak.getIndex() : -1;
    }

    @Override
    public int getPreviousIndex(){
        SmokeBreak smokeBreak = getNextBreak(true);
        return smokeBreak != null ? smokeBreak.getIndex() : -1;
    }

    @Override
    public void onNextBreak(){
        if(!isActive())
            return;
        setSmoked(false);
        analytics.logEvent("smokeBreakStarted",null);
        if(settings.isSmokeBreakNotificationsSwitchEnabled())
            SmokeBreakNotification.notify(application,null);

        setAlarm();
    }

    private void onBreakEnd() {
        if(!isActive())
            return;
        analytics.logEvent("smokeBreakEnded",null);
        SmokeBreakNotification.cancel(application);
    }

    @Override
    public int getNumberOfBreaks() {
        int p = prefs.getInt("breaks.numberOfBreaks",-1);
        if(p == -1)
            p = statistics.cigarettesPerDay() - 1;
        return Math.max(0,p);
    }

    @Override
    public boolean isReady(){
        return !settings.isColdTurkey() && getNextBreakTime() != -1;
    }

    @Override
    public boolean isActive() {
        return isReady();
    }

    @Override
    public boolean isOnBreak() {
        long diff = new Date().getTime() - getPreviousBreakTime();
        boolean isInTime = diff >= 0 && diff <= config.smokeBreakDuration();
        boolean out = !isSmoked() && isInTime;
        return out;
    }

    @Override
    public long getBreakDuration() {
        return config.smokeBreakDuration();
    }

    @Override
    public void setNumberOfBreaks(int q) {
        prefs.edit().putInt("breaks.numberOfBreaks",q).apply();
        setAlarm();
    }

    private long roundToStartOfDay(long time){
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(new Date(time));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public Integer getStart() {
        int p = prefs.getInt("breaks.start",-1);
        if(p == -1)
            p = statistics.firstCigaretteTime();
        return p != -1 ? p : null;
    }

    @Override
    public Integer getEnd() {
        int p = prefs.getInt("breaks.end",-1);
        if(p == -1)
            p = statistics.lastCigaretteTime();
        return p != -1 ? p : null;
    }

    @Override
    public void setStart(Integer minutes) {
        if (minutes == null)
            prefs.edit().remove("breaks.start");
        else
            prefs.edit().putInt("breaks.start",minutes).apply();
        setAlarm();
    }

    @Override
    public void setEnd(Integer minutes) {
        if (minutes == null)
            prefs.edit().remove("breaks.end");
        else
            prefs.edit().putInt("breaks.end",minutes).apply();
        setAlarm();
    }

    @Override
    public void reset() {
        prefs.edit().remove("breaks.start").remove("breaks.end").remove("breaks.numberOfBreaks").apply();
        setAlarm();
    }

    private boolean isSmoked(){
        return prefs.getBoolean("breaks.smoked",false);
    }

    private void setSmoked(boolean b){
        prefs.edit().putBoolean("breaks.smoked",b).apply();
    }
}
