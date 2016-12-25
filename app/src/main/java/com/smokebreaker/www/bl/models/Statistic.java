package com.smokebreaker.www.bl.models;

import android.content.Context;
import android.support.annotation.StringRes;

import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;

public abstract class Statistic {
    protected final Statistics statistics;
    private final Settings settings;
    private Context context;
    public Statistic(Context context, Statistics statistics, Settings settings) {
        this.context = context;
        this.statistics = statistics;
        this.settings = settings;
    }

    public abstract String getStatus();
    public abstract float factor();

    public float getProgress(){
        if(settings.isColdTurkey())
            return 1-(float) Math.tanh((((float)statistics.timeSmokeFree())/(60*1000))*factor());
        else
            return 1-(float) Math.tanh(statistics.cigarettesPerDay()*factor());
    }

    protected String getString(@StringRes int res,Object... args){
        return context.getString(res,args);
    }
}
