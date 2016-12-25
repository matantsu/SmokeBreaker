package com.smokebreaker.www;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.Config;
import com.smokebreaker.www.bl.PaymentManager;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.pl.notifications.SmokeBreakNotification;

import javax.inject.Inject;

public class BackgroundService extends Service {

    @Inject
    ChatManager chatManager;

    @Inject
    Settings settings;

    @Inject
    TipsManager tipsManager;

    @Inject
    UsersManager usersManager;

    @Inject
    Config config;

    @Inject
    AchievementsManager achievementsManager;

    @Inject
    SmokeBreaksManager smokeBreaksManager;

    @Inject
    PaymentManager paymentManager;

    @Inject
    Statistics statistics;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getAppComponent(this).inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && intent.getExtras() != null){
            if(intent.hasExtra("action") ){
                if("smoke".equals(intent.getExtras().getString("action")))
                    statistics.attemptSmoke();
                else if("dismiss".equals(intent.getExtras().getString("action")))
                    SmokeBreakNotification.cancel(this);
            }
            if(intent.hasExtra("isAchievementUnlockedNotificationEnabled")){
                boolean b = intent.getExtras().getBoolean("isAchievementUnlockedNotificationEnabled");
                settings.setAchievementUnlockedNotificationsEnabled(b);
            }
            if(intent.hasExtra("isTipNotificationEnabled")){
                boolean b = intent.getExtras().getBoolean("isTipNotificationEnabled");
                settings.setTipNotificationsSwitchEnabled(b);
            }
        }
        return START_STICKY;
    }
}
