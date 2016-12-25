package com.smokebreaker.www.bl.models.statistics;

import android.content.Context;

import com.smokebreaker.www.R;
import com.smokebreaker.www.bl.Config;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Statistic;

public class HealthStatistic extends Statistic {
    public HealthStatistic(Context context, Statistics statistics, Settings settings) {
        super(context, statistics, settings);
    }

    @Override
    public String getStatus() {
        float prog = getProgress();
        if(prog <= 0.5)
            return getString(R.string.statistic_status_health_1);
        else if(prog <= 0.95)
            return getString(R.string.statistic_status_health_2);
        else
            return getString(R.string.statistic_status_health_3);
    }

    @Override
    public float factor() {
        return 0.2f;
    }
}
