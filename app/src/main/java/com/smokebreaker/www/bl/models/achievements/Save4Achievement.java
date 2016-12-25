package com.smokebreaker.www.bl.models.achievements;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;

public class Save4Achievement extends Achievement{

    public Save4Achievement(Context context, Settings settings, Statistics statistics) {
        super(context, settings, statistics);
    }

    @Override
    public String getName() {
        return getString(R.string.achievement_save_4_title);
    }

    @Override
    public String getDescription() {
        return getString(R.string.achievement_save_4_desc,settings.getCurrency().getSymbol());
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public boolean isAchieved() {
        return statistics.moneySaved() >= 4;
    }
}
