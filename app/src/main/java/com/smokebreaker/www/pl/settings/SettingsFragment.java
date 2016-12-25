package com.smokebreaker.www.pl.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.preference.BuildConfig;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.App;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Statistic;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jonathanfinerty.once.Once;

public class SettingsFragment extends RxFragment {
    @Inject
    Settings settings;

    @Inject
    Statistics statistics;

    @Inject
    SmokeBreaksManager smokeBreaksManager;

    @BindView(R.id.debugStatus)
    TextView debugStatus;

    @BindView(R.id.packQuantity)
    EditText packQuantity;

    @BindView(R.id.packPrice)
    EditText packPrice;

    @BindView(R.id.currency)
    EditText currency;

    @BindView(R.id.smokeBreakNotificationsLayout)
    View smokeBreakNotificationsLayout;

    @BindView(R.id.smokeBreakNotificationsSwitch)
    Switch smokeBreakNotificationsSwitch;

    @BindView(R.id.tipNotificationsSwitch)
    Switch tipNotificationsSwitch;

    @BindView(R.id.achievementUnlockedNotificationsSwitch)
    Switch achievementUnlockedNotificationsSwitch;

    private BubblesManager bubblesManager;
    private BubbleLayout bubbleView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);

        bubblesManager = new BubblesManager.Builder(getContext())
                .build();
        bubblesManager.initialize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        Utils.show(debugStatus, BuildConfig.DEBUG);

        packQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    int q = Integer.parseInt(editable.toString());
                    if(q > 0){
                        settings.setPackQuantity(q);
                        packQuantity.setEms(editable.length());
                        packQuantity.setError(null);
                    }
                    else
                        packQuantity.setError(getString(R.string.non_positive_number_error));
                }
                catch (NumberFormatException e){
                    packQuantity.setError(getString(R.string.required_field));
                }

            }
        });
        packPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    float q = Float.parseFloat(editable.toString());
                    if(q > 0){
                        settings.setPackPrice(q);
                        packPrice.setEms(editable.length());
                        packPrice.setError(null);
                    }
                    else
                        packPrice.setError(getString(R.string.non_positive_number_error));
                }
                catch (NumberFormatException e){
                    packPrice.setError(getString(R.string.required_field));
                }
            }
        });
        currency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currency.setEms(editable.length());
            }
        });
        currency.setOnFocusChangeListener((view1, b) -> {
            if(b)
                onCurrencyClick(currency);
        });
        currency.setOnClickListener(view1 -> onCurrencyClick(currency));

        packQuantity.setText(""+settings.getPackQuantity());
        packPrice.setText(""+settings.getPackPrice());
        currency.setText(settings.getCurrency().getSymbol());

        Utils.show(smokeBreakNotificationsLayout,!settings.isColdTurkey());
        smokeBreakNotificationsSwitch.setChecked(settings.isSmokeBreakNotificationsSwitchEnabled());
        tipNotificationsSwitch.setChecked(settings.isTipNotificationsSwitchEnabled());
        achievementUnlockedNotificationsSwitch.setChecked(settings.isAchievementUnlockedNotificationsEnabled());

        smokeBreakNotificationsSwitch.setOnCheckedChangeListener((compoundButton, b) -> settings.setSmokeBreakNotificationsSwitchEnabled(b));
        tipNotificationsSwitch.setOnCheckedChangeListener((compoundButton, b) -> settings.setTipNotificationsSwitchEnabled(b));
        achievementUnlockedNotificationsSwitch.setOnCheckedChangeListener((compoundButton, b) -> settings.setAchievementUnlockedNotificationsEnabled(b));
    }

    public void onCurrencyClick(View v){
        List<Currency> currencies = new ArrayList<>();
        currencies.addAll(Currency.getAvailableCurrencies());

        List<String> titles = new ArrayList<>();
        for(Currency c : currencies)
            titles.add(String.format("%s (%s)",c.getDisplayName(),c.getSymbol()));
        Collections.sort(titles,String::compareToIgnoreCase);

        new MaterialDialog.Builder(getActivity())
                .items(titles)
                .itemsCallbackSingleChoice(currencies.indexOf(settings.getCurrency()),(dialog, itemView, position, text) -> {
                    settings.setCurrency(currencies.get(position));
                    currency.setText(settings.getCurrency().getSymbol());
                    return true;
                })
                /*.negativeText(R.string.cancel)
                .positiveText(R.string.ok)*/
                .cancelable(true)
                .autoDismiss(true)
                .show();
    }

    @OnClick(R.id.startOver)
    public void onStartOverClick(View view){
        new MaterialDialog.Builder(getContext()).title("Are you sure ?")
                .content("this action is not reversible !")
                .onPositive((a,b)->{
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();
                    Once.clearAll();
                    statistics.clear();
                    ProcessPhoenix.triggerRebirth(getContext(),new Intent(getContext(),SplashScreenActivity.class));
                })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .show();
    }
}
