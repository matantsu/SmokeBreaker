package com.smokebreaker.www;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Smoke;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SmokingActivity extends AppCompatActivity {

    @Inject
    SmokeBreaksManager smokeBreaksManager;

    @Inject
    Statistics statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoking);
        App.getAppComponent(this).inject(this);
        ButterKnife.bind(this);
        smoke(!smokeBreaksManager.isReady() || smokeBreaksManager.isOnBreak());
    }

    private void smoke(boolean force){
        if(force){
            Smoke smoke = statistics.smoke();
            SuperActivityToast.create(this, new Style(), Style.TYPE_BUTTON)
                    .setButtonText("UNDO")
                    .setOnButtonClickListener("smokeToastTag", null, (view, token) -> {
                        statistics.regret(smoke);
                    })
                    .setProgressBarColor(Color.WHITE)
                    .setText("You smoked a cigarette")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_STANDARD)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setAnimations(Style.ANIMATIONS_POP)
                    .setOnDismissListener((view, token) -> finish())
                    .show();
        }
        else
            new MaterialDialog.Builder(this)
                    .title("Not on break")
                    .content("Your next smoke break hasn't started yet. Are you sure you want to smoke ?")
                    .positiveText("SMOKE")
                    .neutralText("SET BREAK TIMES")
                    .negativeText("CANCEL")
                    .onPositive((dialog, which) -> smoke(true))
                    .onNegative((a,b)->finish())
                    .onNeutral((a,b)->{
                        startActivity(new Intent(this,SplashScreenActivity.class).putExtra("page",MainActivity.Page.BREAKS.getValue()));
                        finish();
                    })
                    .canceledOnTouchOutside(false)
                    .show();
    }

    @OnClick(R.id.root)
    public void onRootClick(View view){
        finish();
    }
}
