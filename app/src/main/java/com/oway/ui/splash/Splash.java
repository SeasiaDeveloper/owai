package com.oway.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.oway.R;
import com.oway.base.BaseActivity;
import com.oway.datasource.pref.PreferenceHandler;
import com.oway.ui.LoginSignUpChoice;
import com.oway.ui.registration.RegisterPayment;
import com.oway.ui.registration.Registration;
import com.oway.utillis.AppConstants;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class Splash extends BaseActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 3000;
    @Inject
    PreferenceHandler preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferencesHelper.readBoolean(Splash.this, AppConstants.TOTURIAL_STATUS, false)) {
                    Registration.start(Splash.this);
                    finish();

                } else {
                    Intent i = new Intent(Splash.this,
                            Tutorial.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    @Override
    protected void setUp() {

    }
}
