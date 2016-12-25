package com.smokebreaker.www.bl.models.achievements;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;

public class Save10Achievement extends Achievement{

    public Save10Achievement(Context context, Settings settings, Statistics statistics) {
        super(context, settings, statistics);
    }

    @Override
    public String getName() {
        return getString(R.string.achievement_save_10_title);
    }

    @Override
    public String getDescription() {
        return getString(R.string.achievement_save_10_desc,settings.getCurrency().getSymbol());
    }

    @Override
    public int getRank() {
        return 1;
    }

    @Override
    public boolean isAchieved() {
        return statistics.moneySaved() >= 10;
    }
}
