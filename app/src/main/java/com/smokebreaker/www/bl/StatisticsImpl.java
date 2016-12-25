package com.smokebreaker.www.bl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SmokingActivity;
import com.smokebreaker.www.bl.models.Smoke;
import com.smokebreaker.www.bl.models.Statistic;
import com.smokebreaker.www.bl.models.statistics.HealthStatistic;
import com.smokebreaker.www.pl.notifications.CrossingCigarettesPerDayNotification;
import com.smokebreaker.www.pl.notifications.CrossingInitialNotification;
import com.smokebreaker.www.pl.notifications.SmokeBreakNotification;
import com.txusballesteros.bubbles.BubblesManager;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import ollie.Model;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.query.Delete;
import ollie.util.QueryUtils;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class StatisticsImpl implements Statistics {

    private static final String TAG = Statistics.class.getSimpleName();
    private final Settings settings;
    private final Config config;
    private final Context context;
    private final FirebaseAnalytics analytics;
    SharedPreferences prefs;

    Subject<Integer,Integer> smokesSubject = PublishSubject.create();
    private List<Statistic> statistics;
    private List<Day> days;
    private BubblesManager bubblesManager;

    @Table("days")
    public static class Day extends Model{
        @Column("date")
        public Long date = 0L;

        @Column("smokeCount")
        public Integer smokeCount = 0;

        @Column("firstCigaretteTimestamp")
        public Long firstCigaretteTimestamp = 0L;

        @Column("lastCigaretteTimestamp")
        public Long lastCigaretteTimestamp = 0L;

        public Day() {
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getSmokeCount() {
            return smokeCount;
        }

        public void setSmokeCount(int smokeCount) {
            this.smokeCount = smokeCount;
        }

        public long getFirstCigaretteTimestamp() {
            return firstCigaretteTimestamp;
        }

        public void setFirstCigaretteTimestamp(long firstCigaretteTimestamp) {
            this.firstCigaretteTimestamp = firstCigaretteTimestamp;
        }

        public long getLastCigaretteTimestamp() {
            return lastCigaretteTimestamp;
        }

        public void setLastCigaretteTimestamp(long lastCigaretteTimestamp) {
            this.lastCigaretteTimestamp = lastCigaretteTimestamp;
        }
    }

    @Inject
    public StatisticsImpl(Context context, Settings settings, Config config) {
        this.context = context;
        this.settings = settings;
        this.config = config;
        analytics = FirebaseAnalytics.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        BroadcastReceiver end = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                attemptSmoke();
            }
        };
        context.registerReceiver(end,new IntentFilter(context.getPackageName()+"smoke"));

        settings.getChangeStream()
                .subscribe(this::onSettingsChange);

        onSettingsChange(settings);
    }

    @Override
    public int cigarettesSmoked() {
        return prefs.getInt("statistics.cigarettesSmoked",0);
    }

    @Override
    public Observable<Integer> smokesStream(){
        return smokesSubject;
    }

    @Override
    public Smoke smoke() {
        SmokeBreakNotification.cancel(context);

        days = null;
        Smoke smoke = new Smoke(new Date().getTime());
        smoke.save();
        prefs.edit().putInt("statistics.cigarettesSmoked",cigarettesSmoked()+1).apply();
        smokesSubject.onNext(cigarettesSmoked());
        analytics.logEvent("smoke",null);

        if(isReady() && cigarettesSmokedToday() == initialCigarettesPerDay())
            CrossingInitialNotification.notify(context);

        if(isReady() && cigarettesSmokedToday() == cigarettesPerDay() && cigarettesPerDay() != initialCigarettesPerDay())
            CrossingCigarettesPerDayNotification.notify(context);

        return smoke;
    }

    @Override
    public void smoke(int quantity) {
        for(int i = 0 ; i < quantity ; i ++)
            smoke();
    }

    @Override
    public void attemptSmoke() {
        context.startActivity(new Intent(context, SmokingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void regret(Smoke smoke){
        smoke.delete();
        SmokeBreakNotification.cancel(context);
        days = null;
        prefs.edit().putInt("statistics.cigarettesSmoked",cigarettesSmoked()-1).apply();
        smokesSubject.onNext(cigarettesSmoked());
        analytics.logEvent("regret",null);
    }

    @Override
    public List<Day> getDays(){
        if(days == null){
            days = QueryUtils.rawQuery(Day.class,context.getString(R.string.last_seven_days_query),null);
            if(days == null)
                days = new LinkedList<>();

            Collections.reverse(days);
        }

        /*//fill in days which have 0 smokes
        for(int i = 0 ; i < days.size() - 1 ; i++){
            if(days.get(i).getDate() + (24L*60*60*1000) != days.get(i+1).getDate()){
                Day day = new Day();
                day.setSmokeCount(0);
                day.setFirstCigaretteTimestamp(days.get(i).getFirstCigaretteTimestamp());
                day.setLastCigaretteTimestamp(days.get(i).getLastCigaretteTimestamp());
                day.setDate(days.get(i).getDate() + (24L*60*60*1000));
                days.add(i+1,day);
            }
        }

        if(days.size() == 0 || days.get(days.size()-1).getDate() != new Smoke().getDate()){
            Day day = new Day();
            day.setSmokeCount(0);
            day.setDate(new Smoke().getDate());
            days.add(day);
        }*/
        return days;
    }

    @Override
    public List<Statistic> getStatistics(){
        if(statistics == null){
            statistics = new LinkedList<>();
            statistics.add(new HealthStatistic(context,this,settings));

            Collections.sort(statistics, (statistic, t1) -> (int) (Math.abs(statistic.factor() - t1.factor())/(statistic.factor() - t1.factor())));
        }
        return statistics;
    }

    @Override
    public float moneySaved() {
        return settings.getCigarettePrice()*cigarettesAvoided();
    }

    @Override
    public long timeRegained() {
        return (long) (config.timeRegainedFromAvoidingCigarette()*cigarettesAvoided());
    }

    @Override
    public int cigarettesAvoided() {
        return Math.max(0,projectedSmokes() - cigarettesSmoked());
    }

    @Override
    public int initialCigarettesPerDay() {
        return average(0,config.numberOfDaysToAverage()).getSmokeCount();
    }

    @Override
    public int cigarettesPerDay() {
        List<Day> days = getDays();
        return average(days.size() - config.numberOfDaysToAverage()-1, days.size()-1).getSmokeCount();
    }

    @Override
    public int firstCigaretteTime() {
        List<Day> days = getDays();
        long timestamp = average(days.size() - config.numberOfDaysToAverage()-1, days.size()-1).getFirstCigaretteTimestamp();
        return timestamp != 0 ? getMinutes(timestamp) : -1;
    }

    @Override
    public int lastCigaretteTime() {
        List<Day> days = getDays();
        long timestamp = average(days.size() - config.numberOfDaysToAverage()-1, days.size()-1).getLastCigaretteTimestamp();
        return timestamp != 0 ? getMinutes(timestamp) : -1;
    }

    @Override
    public boolean isReady(){
        return settings.isColdTurkey() || getDays().size() > 1;
    }

    @Override
    public int cigarettesSmokedToday() {
        if(getDays().size() == 0)
            return 0;
        Day day = getDays().get(getDays().size()-1);
        return day.getDate() == new Smoke(new Date().getTime()).getDate() ? day.getSmokeCount() : 0;
    }

    @Override
    public int getQuitDays(){
        return (int) Math.ceil(((double)timeSmokeFree())/(24L*60*60*1000));
    }

    @Override
    public long timeSmokeFree(){
        return new Date().getTime() - settings.getQuitDate();
    }

    @Override
    public void clear() {
        Delete.from(Smoke.class).where("timestamp = ? OR 1 = 1","1").execute();
        prefs.edit().putInt("statistics.cigarettesSmoked",0).apply();
        days = null;
    }

    private int projectedSmokes() {
        return initialCigarettesPerDay()*getQuitDays();
    }

    private int getMinutes(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        return calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE);
    }

    private Day average(int from, int to){
        List<Day> days = getDays();
        float smokeCountSum = 0;
        double firstCigSum = 0;
        double lastCigSum = 0;

        int count = 0;
        long today = new Smoke(new Date().getTime()).getDate();
        for(int i = Math.max(0,from) ; i < Math.min(to,days.size()) ; i ++){
            if(days.get(i).getDate() == today)
                continue;
            smokeCountSum += days.get(i).getSmokeCount();
            firstCigSum += days.get(i).getFirstCigaretteTimestamp();
            lastCigSum += days.get(i).getLastCigaretteTimestamp();
            count++;
        }

        Day day = new Day();
        day.setSmokeCount(count == 0 ? 0 : Math.round(smokeCountSum/count));
        day.setFirstCigaretteTimestamp(count == 0 ? 0 : Math.round(firstCigSum/count));
        day.setLastCigaretteTimestamp(count == 0 ? 0 : Math.round(lastCigSum/count));

        return day;
    }

    private void onSettingsChange(Settings settings){
        if(settings.isColdTurkey()){
            clear();
            smoke(settings.getInitialCigarettesPerDay());
        }
    }
}
