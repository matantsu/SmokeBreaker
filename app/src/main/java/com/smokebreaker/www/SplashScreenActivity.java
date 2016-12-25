package com.smokebreaker.www;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import jonathanfinerty.once.Once;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 21354;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(!Once.beenDone("onBoarding")){
            startActivity(new Intent(this,OnBoardingActivity.class));
            finish();
        }
        else if(!Once.beenDone("setup")){
            startActivity(new Intent(this,SetupActivity.class));
            finish();
        }
        else if(FirebaseAuth.getInstance().getCurrentUser() == null)
            login();
        else
            ready();
    }

    private void ready() {
        Intent intent = new Intent(this,MainActivity.class);
        if(getIntent() != null && getIntent().getExtras() != null)
            intent.putExtras(getIntent().getExtras());
        startActivity(intent);
        finish();
    }

    private void login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setTheme(R.style.AppTheme)
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER)
                        .setLogo(R.mipmap.ic_launcher)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                ready();
            } else {
                Toast.makeText(this, R.string.sign_in_fail,Toast.LENGTH_LONG).show();
            }
        }
    }
}
