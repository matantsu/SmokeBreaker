package com.smokebreaker.www.bl;

import com.afollestad.materialdialogs.BuildConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.smokebreaker.www.R;

import java.util.List;

import javax.inject.Inject;

public class ConfigImpl implements Config, OnSuccessListener<Void> {

    private final FirebaseRemoteConfig config;

    Gson gson = new Gson();

    public class InAppProducts{
        List<String> products;
        List<String> subscriptions;
    }

    @Inject
    public ConfigImpl() {
        config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setConfigSettings(configSettings);
        config.setDefaults(R.xml.remote_config_defaults);
        Task<Void> task = config.fetch(5);
        task.addOnSuccessListener(this);
    }

    @Override
    public long tipsInterval() {
        return config.getLong("tips_interval");
    }

    @Override
    public double timeRegainedFromAvoidingCigarette() {
        return config.getDouble("time_regained_from_avoiding_cigarette");
    }

    @Override
    public long smokeBreakDuration() {
        return config.getLong("smoke_break_duration");
    }

    @Override
    public int numberOfDaysToAverage() {
        return (int) config.getLong("number_of_days_to_average");
    }

    @Override
    public double interstitialFrequency() {
        return config.getDouble("interstitial_frequency");
    }

    @Override
    public List<String> productIds(){
        return gson.fromJson(config.getString("in_app_products"),InAppProducts.class).products;
    }

    @Override
    public List<String> subscriptionIds(){
        return gson.fromJson(config.getString("in_app_products"),InAppProducts.class).subscriptions;
    }

    @Override
    public void onSuccess(Void aVoid) {
        config.activateFetched();
    }
}
