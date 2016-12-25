package com.smokebreaker.www;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.smokebreaker.www.bl.AchievementsManager;
import com.smokebreaker.www.bl.ChatManager;
import com.smokebreaker.www.bl.Config;
import com.smokebreaker.www.bl.PaymentManager;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.TipsManager;
import com.smokebreaker.www.bl.UsersManager;
import com.smokebreaker.www.pl.achievements.AchievementsFragment;
import com.smokebreaker.www.pl.breaks.BreaksFragment;
import com.smokebreaker.www.pl.community.CommunityFragment;
import com.smokebreaker.www.pl.home.HomeFragment;
import com.smokebreaker.www.pl.payment.PurchaseActivity;
import com.smokebreaker.www.pl.settings.SettingsFragment;
import com.smokebreaker.www.pl.statistics.StatisticsFragment;
import com.smokebreaker.www.pl.tips.TipsFragment;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import jonathanfinerty.once.Once;

public class MainActivity extends RxAppCompatActivity implements Drawer.OnDrawerItemClickListener, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_INVITE = 3751;
    private Drawer drawer;
    Toolbar toolbar;

    @Inject
    ChatManager chatManager;

    @Inject
    TipsManager tipsManager;

    @Inject
    UsersManager usersManager;

    @Inject
    Config config;

    @Inject
    Settings settings;

    @Inject
    Statistics statistics;

    @Inject
    AchievementsManager achievementsManager;

    @Inject
    PaymentManager paymentManager;

    @Inject
    SmokeBreaksManager smokeBreaksManager;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.smokebreaker.www.R.layout.activity_main);
        App.getAppComponent(this).inject(this);

        FirebaseAnalytics.getInstance(this);

        startService(new Intent(this,BackgroundService.class));

        toolbar = (Toolbar) findViewById(com.smokebreaker.www.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setOnClickListener(this);

        GoogleApiClient gapi = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();
        AppInvite.AppInviteApi.getInvitation(gapi, this, true)
                .setResultCallback(
                        result -> {
                            Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                            if (result.getStatus().isSuccess()) {
                                Intent intent = result.getInvitationIntent();
                                String deepLink = AppInviteReferral.getDeepLink(intent);
                                String invitationId = AppInviteReferral.getInvitationId(intent);

                                if(!invitationId.isEmpty()){
                                    RxFirebaseDatabase.observeSingleValueEvent(FirebaseDatabase.getInstance().getReference("invites").child(invitationId))
                                            .compose(bindToLifecycle())
                                            .subscribe(this::onInvited);
                                }
                            }
                        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(!paymentManager.isPro()){
            MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("0FEC7F4FE32087F03E453E4A3E962D07")
                    .build();
            mAdView.loadAd(adRequest);

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_ad_unit_id));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }
        else {
            mAdView.setVisibility(View.GONE);
        }

        initDrawer(toolbar);

        if(statistics.getDays().size() >= 3 && !Once.beenDone(TimeUnit.DAYS, 2,"rateDialogShown")){
            new MaterialDialog.Builder(this)
                    .title("Rate & Review this app !")
                    .content("Youv'e been using this app for more than 3 days, We would greatly appreciate it if you would leave a review at the Google Play store.")
                    .positiveText("REVIEW")
                    .cancelable(true)
                    .autoDismiss(true)
                    .onPositive((dialog, which) -> navigate(Page.REVIEW))
                    .show();
            Once.markDone("rateDialogShown");
        }
        if(!Once.beenDone("invitedFriendsAndFamily") && !Once.beenDone(TimeUnit.DAYS, 2,"invitedFriendsAndFamilyDialogShown")){
            new MaterialDialog.Builder(this)
                    .title("Invite friends and family !")
                    .content("Help us spread the word and invite your friends and family so they can help support you at hard moments.")
                    .positiveText("INVITE")
                    .cancelable(true)
                    .autoDismiss(true)
                    .onPositive((dialog, which) -> navigate(Page.INVITE))
                    .show();
            Once.markDone("invitedFriendsAndFamilyDialogShown");
        }
        if(statistics.getDays().size() >= 3 && !Once.beenDone(TimeUnit.DAYS, 2,"sendFeedbackDialogShown")){
            new MaterialDialog.Builder(this)
                    .title("Help us improve !")
                    .content("Send us some feedback about the app to help us make it better. everything from how it looks to how it functions.")
                    .positiveText("SEND FEEDBACK")
                    .cancelable(true)
                    .autoDismiss(true)
                    .onPositive((dialog, which) -> navigate(Page.FEEDBACK))
                    .show();
            Once.markDone("sendFeedbackDialogShown");
        }
        if(statistics.getDays().size() >= 3 && !Once.beenDone(TimeUnit.DAYS, 2,"shareProgressDialogShown")){
            new MaterialDialog.Builder(this)
                    .title("Share your progress !")
                    .content("Let other people know how yor are doing.")
                    .positiveText("SHARE PROGRESS")
                    .cancelable(true)
                    .autoDismiss(true)
                    .onPositive((dialog, which) -> navigate(Page.SHARE))
                    .show();
            Once.markDone("shareProgressDialogShown");
        }
    }

    private void onInvited(DataSnapshot snap) {
        try {
            String uid = snap.getValue(String.class);
            settings.setInviter(uid);

            usersManager.get(uid)
                    .compose(bindToLifecycle())
                    .subscribe(user->{
                        TextView textView = new TextView(this);
                        Utils.setUser(textView,user);
                        new MaterialDialog.Builder(this)
                                .title(R.string.invited_dialog_title)
                                .customView(textView,false)
                                .build()
                                .show();
                    });
        }
        catch (Exception ignored){}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("invites");
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    ref.child(id).setValue(usersManager.getCurrentUser().getUid());
                }

                Once.markDone("invitedFriendsAndFamily");
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

    private void initDrawer(Toolbar toolbar) {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(com.smokebreaker.www.R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(new ProfileDrawerItem()
                        .withName(usersManager.getCurrentUser().getDisplayName())
                        .withIcon(usersManager.getCurrentUser().getPhotoUrl() != null ? Uri.parse(usersManager.getCurrentUser().getPhotoUrl()) : null))
                .build();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.smokebreaker.www.R.color.md_red_500).sizeDp(56);
                }
                return super.placeholder(ctx, tag);
            }
        });

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.PURCHASE.getValue())
                                .withName(paymentManager.isPro() ? "PRO version active !" : "Get the PRO version !")
                                .withIcon(GoogleMaterial.Icon.gmd_favorite)
                                .withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.HOME.getValue())
                                .withName("Home")
                                .withIcon(GoogleMaterial.Icon.gmd_home),
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.STATISTICS.getValue())
                                .withName("My Progress")
                                .withIcon(GoogleMaterial.Icon.gmd_timeline));
        if(!settings.isColdTurkey())
        drawerBuilder
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.BREAKS.getValue())
                                .withName("My Smoke Breaks")
                                .withIcon(GoogleMaterial.Icon.gmd_access_time));
        drawerBuilder
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.COMMUNITY.getValue())
                                .withName("Community")
                                .withIcon(GoogleMaterial.Icon.gmd_forum),
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.TIPS.getValue())
                                .withName("Tips")
                                .withIcon(GoogleMaterial.Icon.gmd_info),
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.ACHIEVEMENTS.getValue())
                                .withName("Achievements")
                                .withIcon(GoogleMaterial.Icon.gmd_star),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem()
                                .withIdentifier(Page.SETTINGS.getValue())
                                .withName("Settings")
                                .withIcon(GoogleMaterial.Icon.gmd_settings),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withIdentifier(Page.INVITE.getValue())
                                .withName("Invite Family and Friends !")
                                .withIcon(GoogleMaterial.Icon.gmd_person_add)
                                .withSelectable(false),
                        new SecondaryDrawerItem()
                                .withIdentifier(Page.SHARE.getValue())
                                .withName("Share your progress")
                                .withIcon(GoogleMaterial.Icon.gmd_share)
                                .withSelectable(false),
                        new SecondaryDrawerItem()
                                .withIdentifier(Page.FEEDBACK.getValue())
                                .withName("Send Feedback")
                                .withIcon(GoogleMaterial.Icon.gmd_feedback)
                                .withSelectable(false),
                        new SecondaryDrawerItem()
                                .withIdentifier(Page.REVIEW.getValue())
                                .withName("Review This App")
                                .withIcon(GoogleMaterial.Icon.gmd_rate_review)
                                .withSelectable(false)

                )
                .withOnDrawerItemClickListener(this);
        drawer = drawerBuilder.build();

        if(getIntent() != null && getIntent().getExtras() != null && getIntent().hasExtra("page"))
            navigate(Page.values()[getIntent().getExtras().getInt("page")]);
        else
            navigate(Page.HOME);
    }

    @Override
    @DebugLog
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        Fragment fragment = null;

        Page page = Page.values()[(int) drawerItem.getIdentifier()];

        if(page == Page.HOME)
            fragment = new HomeFragment();
        if(page == Page.COMMUNITY)
            fragment = new CommunityFragment();
        if(page == Page.TIPS)
            fragment = new TipsFragment();
        if(page == Page.ACHIEVEMENTS)
            fragment = new AchievementsFragment();
        if(page == Page.SETTINGS)
            fragment = new SettingsFragment();
        if(page == Page.BREAKS)
            fragment = new BreaksFragment();
        if(page == Page.STATISTICS)
            fragment = new StatisticsFragment();
        if(page == Page.INVITE)
            invite();
        if(page == Page.SHARE)
            share();
        if(page == Page.FEEDBACK)
            feedback();
        if(page == Page.REVIEW)
            review();
        if(page == Page.PURCHASE && !paymentManager.isPro())
            purchase();

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(com.smokebreaker.www.R.id.container,fragment)
                    .commit();

            if(drawerItem instanceof PrimaryDrawerItem)
                toolbar.setTitle(((PrimaryDrawerItem)drawerItem).getName().getText());
            toolbar.setSubtitle(fragment instanceof HomeFragment ? null : "tap to return to home");
        }

        if (!paymentManager.isPro() && Math.random() <= config.interstitialFrequency() && mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        return false;
    }

    private void purchase() {
        startActivity(new Intent(this,PurchaseActivity.class));
    }

    private void invite() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    private void share() {
        String timeRegained;

        if(statistics.timeRegained() < 60)
            timeRegained = getString(R.string.minutes,statistics.timeRegained());
        else if(statistics.timeRegained() < 24*60)
            timeRegained = getString(R.string.hours,(int)(statistics.timeRegained()/60));
        else
            timeRegained = getString(R.string.days,(int)(statistics.timeRegained()/(60*24)));

        String text = getString(R.string.share_progress_text,
                statistics.moneySaved(),
                settings.getCurrency().getSymbol(),
                timeRegained,
                statistics.cigarettesAvoided());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void feedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.support_email)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
    }

    private void review() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        }
    }

    public void navigate(Page page) {
        drawer.setSelection(page.getValue());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"A fatal error has occured",Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onClick(View view) {
        navigate(Page.HOME);
    }

    public enum Page {
        HOME(0),
        COMMUNITY(1),
        TIPS(2),
        ACHIEVEMENTS(3),
        SETTINGS(4),
        INVITE(5),
        SHARE(6),
        FEEDBACK(7),
        REVIEW(8),
        BREAKS(9),
        STATISTICS(10),
        PURCHASE(11);

        private final int value;
        Page(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("0FEC7F4FE32087F03E453E4A3E962D07")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
