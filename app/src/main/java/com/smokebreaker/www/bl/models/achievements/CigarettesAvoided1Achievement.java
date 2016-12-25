package com.smokebreaker.www.bl.models.achievements;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Achievement;

public class CigarettesAvoided1Achievement extends Achievement {
    public CigarettesAvoided1Achievement(Context context, Settings settings, Statistics statistics) {
        super(context, settings, statistics);
    }

    @Override
    public String getName() {
        return getString(R.string.achievement_cigarettes_avoided_1_title);
    }

    @Override
    public String getDescription() {
        return getString(R.string.achievement_cigarettes_avoided_1_desc);
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public boolean isAchieved() {
        return statistics.cigarettesAvoided() > 0;
    }
}
