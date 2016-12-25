package com.smokebreaker.www.bl.models.achievements;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;

public class TimeRegained1DayAchievement extends Achievement {

    public TimeRegained1DayAchievement(Context context, Settings settings, Statistics statistics) {
        super(context, settings, statistics);
    }

    @Override
    public String getName() {
        return getString(R.string.achievement_time_regained_1_day_title);
    }

    @Override
    public String getDescription() {
        return getString(R.string.achievement_time_regained_1_day_desc);
    }

    @Override
    public int getRank() {
        return 2;
    }

    @Override
    public boolean isAchieved() {
        return statistics.timeRegained() >= 60*24;
    }
}
