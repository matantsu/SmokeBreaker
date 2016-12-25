package com.smokebreaker.www.bl.models.achievements;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;

public class CigarettesAvoided30Achievement extends Achievement {
    public CigarettesAvoided30Achievement(Context context, Settings settings, Statistics statistics) {
        super(context, settings, statistics);
    }

    @Override
    public String getName() {
        return getString(R.string.achievement_cigarettes_avoided_30_title);
    }

    @Override
    public String getDescription() {
        return getString(R.string.achievement_cigarettes_avoided_30_desc);
    }

    @Override
    public int getRank() {
        return 1;
    }

    @Override
    public boolean isAchieved() {
        return statistics.cigarettesAvoided() >= 30;
    }
}
