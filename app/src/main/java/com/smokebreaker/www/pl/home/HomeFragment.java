package com.smokebreaker.www.pl.home;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.animation.easing.CircEase;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.smokebreaker.www.App;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.R;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.StatisticsImpl;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.models.Achievement;
import com.smokebreaker.www.bl.models.Smoke;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends RxFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @BindView(R.id.fab)
    FloatingActionButton fab;


    @BindView(R.id.progressLayout)
    View progressLayout;

    @BindView(R.id.progressEmptyLayout)
    View progressEmptyLayout;

    @BindView(R.id.progressChartLayout)
    View progressChartLayout;

    @BindView(R.id.lineChart)
    LineChartView lineChart;

    @BindView(R.id.cigarettesSmokedToday)
    TextView cigarettesSmokedToday;

    @BindView(R.id.moneySaved)
    TextView moneySaved;

    @BindView(R.id.timeRegained)
    TextView timeRegained;

    @BindView(R.id.cigarettesAvoided)
    TextView cigarettesAvoided;


    @BindView(R.id.breaks)
    View breaks;

    @BindView(R.id.breaksCard)
    View breaksCard;

    @BindView(R.id.breaksOnBreakLayout)
    View breaksOnBreakLayout;

    @BindView(R.id.breakLayout)
    View breakLayout;

    @BindView(R.id.breakEmptyLayout)
    View breakEmptyLayout;

    @BindView(R.id.breakTime)
    RelativeTimeTextView breakTime;

    @BindView(R.id.breakProgress)
    ProgressBar breakProgress;

    @BindView(R.id.breakIndex)
    TextView breakIndex;

    @BindView(R.id.breakIndexOnBreak)
    TextView breakIndexOnBreak;


    @BindView(R.id.tipLayout)
    View tipLayout;

    @BindView(R.id.tipUser)
    TextView tipUser;

    @BindView(R.id.tipText)
    TextView tipText;

    @BindView(R.id.communityLayout)
    View communityLayout;

    @BindView(R.id.communityUser)
    TextView communityUser;

    @BindView(R.id.communityText)
    TextView communityText;


    @BindView(R.id.achievementLayout)
    View achievementLayout;

    @BindView(R.id.achievementTitle)
    TextView achievementTitle;

    @BindView(R.id.achievementSubtitle)
    TextView achievementSubtitle;

    @BindView(R.id.achievementTrophy1)
    View achievementTrophy1;

    @BindView(R.id.achievementTrophy2)
    View achievementTrophy2;

    @BindView(R.id.achievementTrophy3)
    View achievementTrophy3;

    @Inject
    TipsManager tipsManager;

    @Inject
    ChatManager chatManager;

    @Inject
    AchievementsManager achievementsManager;

    @Inject
    Statistics statistics;

    @Inject
    Settings settings;

    @Inject
    SmokeBreaksManager smokeBreaksManager;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        IconicsDrawable icon = new IconicsDrawable(view.getContext())
                .icon(GoogleMaterial.Icon.gmd_smoking_rooms)
                .color(Color.WHITE);
        fab.setImageDrawable(icon);

        Utils.show(fab,!settings.isColdTurkey());

        if(!settings.isColdTurkey() && PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("smokeButtonShowcase",true))
            new MaterialTapTargetPrompt.Builder(getActivity())
                    .setTarget(fab)
                    .setPrimaryText("Use the smoke button to log your smokes")
                    .setSecondaryText("Tap on this button every time you smoke to help us learn about your smoking habit, show you statistics, suggest tailored smoke breaks and more ...")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("smokeButtonShowcase",false).apply();
                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    })
                    .setBackgroundColourFromRes(R.color.colorPrimary)
                    .show();

        setProgress();
        setBreak();
        setTip();
        setCommunity();
        setAchievement();
    }

    private void setBreak() {
        if(settings.isColdTurkey())
            Utils.show(breaks,false);
        else
            Observable.interval(0,1, TimeUnit.SECONDS)
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(r->{
                        breaksCard.setBackgroundColor(getResources().getColor(smokeBreaksManager.isReady() && smokeBreaksManager.isOnBreak() ? R.color.colorPrimary : R.color.md_white_1000));
                        Utils.show(breaksOnBreakLayout,smokeBreaksManager.isReady() && smokeBreaksManager.isOnBreak());
                        Utils.show(breakLayout,smokeBreaksManager.isReady() && !smokeBreaksManager.isOnBreak());
                        Utils.show(breakEmptyLayout,!smokeBreaksManager.isReady());

                        if(smokeBreaksManager.isReady()){
                            breakTime.setReferenceTime(smokeBreaksManager.getNextBreakTime());
                            double progress = 1-((double)(smokeBreaksManager.getNextBreakTime() - new Date().getTime()))/(smokeBreaksManager.getNextBreakTime() - smokeBreaksManager.getPreviousBreakTime());
                            breakProgress.setProgress((int) (progress*100));
                            breakIndex.setText(getString(R.string.break_index_indicator,smokeBreaksManager.getNextIndex()+1,smokeBreaksManager.getNumberOfBreaks()));
                            breakIndexOnBreak.setText(getString(R.string.break_index_indicator,smokeBreaksManager.getPreviousIndex()+1,smokeBreaksManager.getNumberOfBreaks()));
                        }
            });
    }

    private void setProgress() {
        Observable.merge(Observable.just(null),statistics.smokesStream())
                .compose(bindToLifecycle())
                .subscribe(d->{
                    if(settings.isColdTurkey()){
                        if(statistics.timeSmokeFree() < 60*1000)
                            cigarettesSmokedToday.setText("Just stopped smoking for good !");
                        else if(statistics.timeSmokeFree() < 60*60*1000)
                            cigarettesSmokedToday.setText(getString(R.string.quit_minutes,statistics.timeSmokeFree()/(60*1000)));
                        else if(statistics.timeSmokeFree() < 24*60*60*1000)
                            cigarettesSmokedToday.setText(getString(R.string.quit_hours,statistics.timeSmokeFree()/(60*60*1000)));
                        else
                            cigarettesSmokedToday.setText(getString(R.string.quit_days,statistics.timeSmokeFree()/(24*60*60*1000)));
                    }
                    else
                        cigarettesSmokedToday.setText(getString(R.string.cigarettes_smoked_today,statistics.cigarettesSmokedToday()));
                    Utils.show(progressLayout,statistics.isReady());
                    Utils.show(progressEmptyLayout,!statistics.isReady());
                    Utils.show(progressChartLayout,!settings.isColdTurkey() && statistics.getDays().size() > 0);
                    if(!settings.isColdTurkey() && statistics.getDays().size() > 0)
                        setGraph();
                    if(statistics.isReady())
                        setStatistics();
                });
    }

    boolean isAdded = false;
    private void setGraph() {
        List<StatisticsImpl.Day> days = statistics.getDays();
        days = days.subList(Math.max(0,days.size()-7),days.size());
        StatisticsImpl.Day day = days.get(0);
        final String[] labels = new String[7];
        final float[] vals = new float[7];
        final float[] cigsPerDay = new float[7];
        final float[] init = new float[7];

        int min = Integer.MAX_VALUE;
        int max = 0;
        for(int i = 0 ; i < 7 ; i++){
            if(i < days.size())
                day = days.get(i);
            labels[i] = DateFormat.format("dd MMM",day.getDate() + ((i-days.indexOf(day))*24L*60*60*1000)).toString();
            vals[i] = i < days.size() ? days.get(i).getSmokeCount() : 0;
            cigsPerDay[i] = statistics.cigarettesPerDay();
            init[i] = statistics.initialCigarettesPerDay();

            min = (int) Math.min(min,vals[i]);
            max = (int) Math.ceil(Math.max(max,vals[i]));
        }

        if(lineChart.getData().size() > 0){
            lineChart.updateValues(0,vals);
            lineChart.updateValues(1,init);
            lineChart.updateValues(2,cigsPerDay);
        }
        else{
            isAdded = true;
            LineSet dataset = new LineSet(labels, vals);
            dataset.setColor(getContext().getResources().getColor(R.color.colorPrimary))
                    .setSmooth(true)
                    .setDotsColor(getContext().getResources().getColor(R.color.colorPrimary))
                    .setThickness(4);
            lineChart.addData(dataset);

            LineSet dataset3 = new LineSet(labels, init);
            dataset3.setColor(getContext().getResources().getColor(R.color.md_red_400))
                    .setSmooth(true)
                    .setDotsColor(getContext().getResources().getColor(R.color.md_red_400))
                    .setDotsRadius(0)
                    .setThickness(4);
            lineChart.addData(dataset3);

            LineSet dataset2 = new LineSet(labels, cigsPerDay);
            dataset2.setColor(getContext().getResources().getColor(R.color.colorAccent))
                    .setSmooth(true)
                    .setDotsColor(getContext().getResources().getColor(R.color.colorAccent))
                    .setDotsRadius(0)
                    .setThickness(4);
            lineChart.addData(dataset2);

            Paint gridPaint = new Paint();
            gridPaint.setColor(Color.parseColor("#e7e7e7"));
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setAntiAlias(true);
            gridPaint.setStrokeWidth(Tools.fromDpToPx(.7f));
            lineChart.setBorderSpacing(Tools.fromDpToPx(5))
                    .setGrid(ChartView.GridType.HORIZONTAL,gridPaint)
                    .setAxisBorderValues(min, Math.max(max+3,10))
                    .setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
                    .setLabelsColor(getContext().getResources().getColor(R.color.colorPrimary))
                    .setXAxis(false)
                    .setYAxis(false);
        }
        Animation anim = new Animation().setEasing(new CircEase());
        lineChart.show(anim);
    }

    private void setStatistics() {
        moneySaved.setText(getString(R.string.money,statistics.moneySaved(),settings.getCurrency().getSymbol()));

        if(statistics.timeRegained() < 60)
            timeRegained.setText(getString(R.string.minutes,statistics.timeRegained()));
        else if(statistics.timeRegained() < 24*60)
            timeRegained.setText(getString(R.string.hours,(int)(statistics.timeRegained()/60)));
        else
            timeRegained.setText(getString(R.string.days,(int)(statistics.timeRegained()/(60*24))));

        cigarettesAvoided.setText(getString(R.string.cigarettes,statistics.cigarettesAvoided()));
    }

    private void setAchievement() {
        Achievement achievement = achievementsManager.getNext();
        if(achievement != null){
            achievementTitle.setText(achievement.getName());
            achievementSubtitle.setText(achievement.getDescription());

            Utils.show(achievementTrophy1,achievement.getRank() >= 0);
            Utils.show(achievementTrophy2,achievement.getRank() >= 1);
            Utils.show(achievementTrophy3,achievement.getRank() >= 2);
        }
        else
            achievementLayout.setVisibility(View.GONE);
    }

    private void setCommunity() {
        communityLayout.setVisibility(View.GONE);
        chatManager.lastMessageAsync()
                .compose(bindToLifecycle())
                .subscribe(chatMessage->{
                    if(chatMessage != null){
                        Utils.setUser(communityUser,chatMessage.getFrom());
                        communityText.setText(chatMessage.getMessage());
                        communityLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setTip() {
        tipLayout.setVisibility(View.GONE);
        tipsManager.randomTipAsync()
                .compose(bindToLifecycle())
                .subscribe(tip->{
                    if(tip != null){
                        Utils.setUser(tipUser,tip.getAuthor());
                        tipText.setText(tip.getTip());
                        tipLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    @OnClick(R.id.statisticsCard)
    public void onStatisticsCardClick(View v){
        ((MainActivity)getActivity()).navigate(MainActivity.Page.STATISTICS);
    }

    @OnClick(R.id.breaksCard)
    public void onBreaksCardClick(View v){
        ((MainActivity)getActivity()).navigate(MainActivity.Page.BREAKS);
    }

    @OnClick(R.id.tipLayout)
    public void onTipLayoutClick(View v){
        ((MainActivity)getActivity()).navigate(MainActivity.Page.TIPS);
    }

    @OnClick(R.id.communityLayout)
    public void onCommunityLayoutClick(View v){
        ((MainActivity)getActivity()).navigate(MainActivity.Page.COMMUNITY);
    }

    @OnClick(R.id.achievementLayout)
    public void onAchievementLayoutClick(View v){
        ((MainActivity)getActivity()).navigate(MainActivity.Page.ACHIEVEMENTS);
    }

    @OnClick(R.id.fab)
    public void onFabClick(View v){
        smoke(false);
    }

    private void smoke(boolean force){
        statistics.attemptSmoke();/*
        if(force || !smokeBreaksManager.isReady() || smokeBreaksManager.isOnBreak()){
            Smoke smoke = statistics.smoke();
            setProgress();
            setBreak();
            SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_BUTTON)
                    .setButtonText("UNDO")
                    .setOnButtonClickListener("smokeToastTag", null, (view, token) -> {
                        statistics.regret(smoke);
                        setProgress();
                        setBreak();
                    })
                    .setProgressBarColor(Color.WHITE)
                    .setText("You smoked a cigarette")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_STANDARD)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setAnimations(Style.ANIMATIONS_POP).show();
        }
        else
            new MaterialDialog.Builder(getContext())
                    .title("Not on break")
                    .content("Your next smoke break hasn't started yet. Are you sure you want to smoke ?")
                    .positiveText("SMOKE")
                    .neutralText("SET BREAK TIMES")
                    .negativeText("CANCEL")
                    .onPositive((dialog, which) -> smoke(true))
                    .onNeutral((dialog, which) -> onBreaksCardClick(null))
                    .show();*/
    }
}
