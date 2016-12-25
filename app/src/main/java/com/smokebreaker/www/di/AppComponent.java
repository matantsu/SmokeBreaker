package com.smokebreaker.www.di;

import com.smokebreaker.www.App;
import com.smokebreaker.www.BackgroundService;
import com.smokebreaker.www.MainActivity;
import com.smokebreaker.www.SetupActivity;
import com.smokebreaker.www.SmokingActivity;
import com.smokebreaker.www.pl.achievements.AchievementsFragment;
import com.smokebreaker.www.pl.breaks.BreaksFragment;
import com.smokebreaker.www.pl.community.CommunityFragment;
import com.smokebreaker.www.pl.home.HomeFragment;
import com.smokebreaker.www.pl.payment.PurchaseActivity;
import com.smokebreaker.www.pl.settings.SettingsFragment;
import com.smokebreaker.www.pl.statistics.StatisticsFragment;
import com.smokebreaker.www.pl.tips.TipsFragment;
import com.smokebreaker.www.pl.widgets.AchievementWidget;
import com.smokebreaker.www.pl.widgets.StatisticsWidget;
import com.smokebreaker.www.pl.widgets.TipWidget;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(App app);
    void inject(MainActivity mainActivity);
    void inject(CommunityFragment communityFragment);
    void inject(TipsFragment tipsFragment);
    void inject(HomeFragment homeFragment);
    void inject(AchievementsFragment achievementsFragment);
    void inject(SettingsFragment settingsFragment);
    void inject(BackgroundService backgroundService);
    void inject(StatisticsFragment statisticsFragment);
    void inject(BreaksFragment breaksFragment);
    void inject(PurchaseActivity purchaseActivity);
    void inject(SetupActivity setupActivity);
    void inject(SmokingActivity smokingActivity);
    void inject(TipWidget tipWidget);
    void inject(AchievementWidget achievementWidget);
    void inject(StatisticsWidget statisticsWidget);
}
