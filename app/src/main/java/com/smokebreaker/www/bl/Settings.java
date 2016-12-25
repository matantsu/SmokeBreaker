package com.smokebreaker.www.bl;

import java.util.Currency;

import rx.Observable;

public interface Settings {
    void setCurrency(Currency currency);
    Currency getCurrency();
    int getPackQuantity();
    void setPackQuantity(int packQuantity);
    float getPackPrice();
    void setPackPrice(float packPrice);
    float getCigarettePrice();
    long getInstallTime();
    boolean isAchievementUnlockedNotificationsEnabled();
    boolean isSmokeBreakNotificationsSwitchEnabled();
    boolean isTipNotificationsSwitchEnabled();
    void setSmokeBreakNotificationsSwitchEnabled(boolean b);
    void setTipNotificationsSwitchEnabled(boolean b);
    void setAchievementUnlockedNotificationsEnabled(boolean b);
    void setInviter(String uid);
    String getInviter();
    void setColdTurkey(boolean b);
    boolean isColdTurkey();
    int getInitialCigarettesPerDay();
    void setInitialCigarettesPerDay(int q);
    long getQuitDate();
    void setQuitDate(long timeInMillis);
    Observable<Settings> getChangeStream();
    void setShowFloatingSmokeButton(boolean b);
    boolean showFloatingSmokeButton();
}
