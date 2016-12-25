package com.smokebreaker.www.bl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.smokebreaker.www.pl.notifications.AchievementUnlockedNotification;
import com.smokebreaker.www.pl.notifications.SmokeBreakNotification;
import com.smokebreaker.www.pl.notifications.TipNotification;

import java.util.Currency;
import java.util.Date;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class SettingsImpl implements Settings {

    private final Context context;
    SharedPreferences prefs;
    private FirebaseAnalytics analytics;

    Subject<Settings,Settings> settingsChangeSubject = PublishSubject.create();

    @Inject
    public SettingsImpl(Context context) {
        this.context = context;
        analytics = FirebaseAnalytics.getInstance(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(getInstallTime() == -1)
            prefs.edit().putLong("settings.installTime",new Date().getTime()).apply();
        if(getQuitDate() == -1)
            setQuitDate(new Date().getTime());
    }

    @Override
    public void setCurrency(Currency currency) {
        prefs.edit().putString("settings.currency",currency.getCurrencyCode()).apply();
        settingsChangeSubject.onNext(this);
    }

    @Override
    public Currency getCurrency() {
        String code = prefs.getString("settings.currency",null);
        if(code != null)
            return Currency.getInstance(code);
        else
            return Currency.getInstance(context.getResources().getConfiguration().locale);
    }

    @Override
    public int getPackQuantity() {
        return prefs.getInt("settings.packQuantity",20);
    }

    @Override
    public void setPackQuantity(int packQuantity) {
        prefs.edit().putInt("settings.packQuantity",packQuantity).apply();
        settingsChangeSubject.onNext(this);
    }

    @Override
    public float getPackPrice() {
        return prefs.getFloat("settings.packPrice",5);
    }

    @Override
    public void setPackPrice(float packPrice) {
        prefs.edit().putFloat("settings.packPrice",packPrice).apply();
        settingsChangeSubject.onNext(this);
    }

    @Override
    public float getCigarettePrice() {
        return getPackPrice()/getPackQuantity();
    }

    @Override
    public long getInstallTime() {
        return prefs.getLong("settings.installTime",-1);
    }

    @Override
    public boolean isAchievementUnlockedNotificationsEnabled() {
        return prefs.getBoolean("settings.isAchievementUnlockedNotificationsEnabled",true);
    }

    @Override
    public boolean isSmokeBreakNotificationsSwitchEnabled() {
        return prefs.getBoolean("settings.isSmokeBreakNotificationsSwitchEnabled",true);
    }

    @Override
    public boolean isTipNotificationsSwitchEnabled() {
        return prefs.getBoolean("settings.isTipNotificationsSwitchEnabled",true);
    }

    @Override
    public void setSmokeBreakNotificationsSwitchEnabled(boolean b) {
        prefs.edit().putBoolean("settings.isSmokeBreakNotificationsSwitchEnabled",b).apply();
        analytics.setUserProperty("smokeBreakNotification",b+"");
        settingsChangeSubject.onNext(this);

        if(!b)
            SmokeBreakNotification.cancel(context);
    }

    @Override
    public void setTipNotificationsSwitchEnabled(boolean b) {
        prefs.edit().putBoolean("settings.isTipNotificationsSwitchEnabled",b).apply();
        analytics.setUserProperty("tipNotification",b+"");
        settingsChangeSubject.onNext(this);

        if(!b)
            TipNotification.cancel(context);
    }

    @Override
    public void setAchievementUnlockedNotificationsEnabled(boolean b) {
        prefs.edit().putBoolean("settings.isAchievementUnlockedNotificationsEnabled",b).apply();
        analytics.setUserProperty("achievementNotification",b+"");
        settingsChangeSubject.onNext(this);

        if(!b)
            AchievementUnlockedNotification.cancel(context);
    }

    @Override
    public void setInviter(String uid) {
        prefs.edit().putString("settings.inviter",uid).apply();
        settingsChangeSubject.onNext(this);
    }

    @Override
    public String getInviter() {
        return prefs.getString("settings.inviter",null);
    }

    @Override
    public void setColdTurkey(boolean b) {
        prefs.edit().putBoolean("settings.isColdTurkey",b).apply();
        analytics.setUserProperty("isColdTurkey",b+"");
        settingsChangeSubject.onNext(this);
    }

    @Override
    public boolean isColdTurkey() {
        return prefs.getBoolean("settings.isColdTurkey",true);
    }

    @Override
    public int getInitialCigarettesPerDay() {
        return prefs.getInt("settings.initialCigarettesPerDay",13);
    }

    @Override
    public void setInitialCigarettesPerDay(int q) {
        prefs.edit().putInt("settings.initialCigarettesPerDay",q).apply();
        analytics.setUserProperty("initialCigarettesPerDay",q+"");
        settingsChangeSubject.onNext(this);
    }

    @Override
    public long getQuitDate() {
        return prefs.getLong("settings.quitDate",-1);
    }

    @Override
    public void setQuitDate(long timeInMillis) {
        prefs.edit().putLong("settings.quitDate",timeInMillis).apply();
        settingsChangeSubject.onNext(this);
    }

    @Override
    public Observable<Settings> getChangeStream(){
        return settingsChangeSubject;
    }

    @Override
    public void setShowFloatingSmokeButton(boolean b) {
        prefs.edit().putBoolean("settings.showFloatingSmokeButton",b).apply();
        analytics.setUserProperty("initialCigarettesPerDay",b+"");
        settingsChangeSubject.onNext(this);
    }

    @Override
    public boolean showFloatingSmokeButton() {
        return prefs.getBoolean("settings.showFloatingSmokeButton",true);
    }
}
