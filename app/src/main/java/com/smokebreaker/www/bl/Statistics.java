package com.smokebreaker.www.bl;

import com.smokebreaker.www.bl.models.Smoke;
import com.smokebreaker.www.bl.models.Statistic;

import java.util.List;

import rx.Observable;

public interface Statistics {
    int cigarettesSmoked();
    Observable<Integer> smokesStream();
    Smoke smoke();
    void smoke(int quantity);
    void attemptSmoke();
    void regret(Smoke smoke);
    List<StatisticsImpl.Day> getDays();
    List<Statistic> getStatistics();
    float moneySaved();
    long timeRegained(); // in minutes
    int cigarettesAvoided();
    int cigarettesPerDay();
    int initialCigarettesPerDay();
    int firstCigaretteTime();
    int lastCigaretteTime();
    boolean isReady();
    int cigarettesSmokedToday();
    int getQuitDays();
    long timeSmokeFree();
    void clear();
}
