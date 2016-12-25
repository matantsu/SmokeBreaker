package com.smokebreaker.www.di;

import android.app.Application;
import android.content.Context;

import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.AchievementsManagerImpl;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.ChatManagerImpl;
import com.smokebreaker.www.bl.Config;
import com.smokebreaker.www.bl.ConfigImpl;
import com.smokebreaker.www.bl.PaymentManager;
import com.smokebreaker.www.bl.PaymentManagerImpl;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.SettingsImpl;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.SmokeBreaksManagerImpl;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.StatisticsImpl;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.TipsManagerImpl;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.bl.UsersManagerImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return application;
    }

    @Provides
    @Singleton
    public Application provideApplication(){
        return application;
    }

    @Provides
    @Singleton
    public UsersManager provideUsersManager(UsersManagerImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public ChatManager provideChatManager(ChatManagerImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public TipsManager provideTipsManager(TipsManagerImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public Config provideConfig(ConfigImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public AchievementsManager provideAchievementsManager(AchievementsManagerImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public Statistics provideStatistics(StatisticsImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public Settings provideSettings(SettingsImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public SmokeBreaksManager provideSmokeBreaksManager(SmokeBreaksManagerImpl impl){
        return impl;
    }

    @Provides
    @Singleton
    public PaymentManager providePaymentManager(PaymentManagerImpl impl){
        return impl;
    }
}
