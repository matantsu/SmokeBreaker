package com.smokebreaker.www;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.smokebreaker.www.R;
import com.smokebreaker.www.SplashScreenActivity;

import jonathanfinerty.once.Once;

public class OnBoardingActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Learns from the best !",
                "Smoke Breaker will help you quit smoking by learning your smoking habit and helping you every step of the way.",
                R.drawable.chemistry,
                getResources().getColor(R.color.onBoardingSlideColor1)));
        addSlide(AppIntroFragment.newInstance("Get and share tips !",
                "Learn from the experience of other quitters.",
                R.drawable.question,
                getResources().getColor(R.color.onBoardingSlideColor2)));
        addSlide(AppIntroFragment.newInstance("Talk about it",
                "Share your quitting process with a community of other quitters and get love and support",
                R.drawable.chat,
                getResources().getColor(R.color.onBoardingSlideColor3)));
        addSlide(AppIntroFragment.newInstance("Track your progress",
                "See exactly how much money you are saving, how much life you regained and how many cigarettes you smoked each day",
                R.drawable.presentation,
                getResources().getColor(R.color.onBoardingSlideColor4)));
        addSlide(AppIntroFragment.newInstance("Optimal smoke breaks",
                "Get custom tailored smoke break notifications which help you decrease gradually, based on learned data.",
                R.drawable.bell,
                getResources().getColor(R.color.onBoardingSlideColor5)));
        addSlide(AppIntroFragment.newInstance("Goal !!",
                "Gain achievements that keep you motivated and help you feel the progress.",
                R.drawable.goal,
                getResources().getColor(R.color.onBoardingSlideColor6)));

        setVibrate(true);
        setVibrateIntensity(30);

        setDoneText("Get Started !");

        setFlowAnimation();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Once.markDone("onBoarding");
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        onDonePressed(currentFragment);
    }
}
