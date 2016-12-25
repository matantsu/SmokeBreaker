package com.smokebreaker.www.bl.models;

import android.content.Context;
import android.support.annotation.StringRes;

import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;

public abstract class Achievement implements Comparable{
    protected final Context context;
    protected final Statistics statistics;
    protected final Settings settings;

    public Achievement(Context context, Settings settings, Statistics statistics) {
        this.context = context;
        this.statistics = statistics;
        this.settings = settings;
    }

    //region g&s

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getRank();

    //endregion

    public abstract boolean isAchieved();

    @Override
    public int compareTo(Object o) {
        if(o instanceof Achievement){
            Achievement other = (Achievement) o;
            if(getRank() > other.getRank())
                return 1;
            else if(getRank() < other.getRank())
                return -1;
            else
                return 0;
        }
        else
            return 0;
    }

    protected String getString(@StringRes int res, Object... args){
        return context.getString(res,args);
    }
}
