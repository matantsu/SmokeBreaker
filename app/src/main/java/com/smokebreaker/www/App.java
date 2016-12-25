package com.smokebreaker.www;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.Config;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.di.AppComponent;
import com.smokebreaker.www.di.AppModule;
import com.smokebreaker.www.di.DaggerAppComponent;

import jonathanfinerty.once.Once;
import ollie.Ollie;

public class App extends MultiDexApplication {

    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance()
                    .setPersistenceEnabled(true);
            Once.initialise(this);
            Ollie.with(this)
                    .setName("smoke_breaker_db")
                    .setVersion(1)
                    .setLogLevel(Ollie.LogLevel.FULL)
                    .init();

            appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
            appComponent.inject(this);
        }
    }

    public static AppComponent getAppComponent(Context context) {
        return ((App)context.getApplicationContext()).appComponent;
    }
}
