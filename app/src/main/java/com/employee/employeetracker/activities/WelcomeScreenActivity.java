package com.employee.employeetracker.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.SlidePagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {
    //Displays a log message into the logcat
    private static final String TAG = "WelcomeScreenActivity";
    private SlidePagerAdapter slidePagerAdapter;
    private Button btnLogin, btnSignUp;
    private ViewPager viewPager;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removes anything relating to a title
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Splash Screen activity displays in a full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        initViews();
        initListeners();

    }


    private void initListeners() {
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void initViews() {
        CircleIndicator indicator = findViewById(R.id.slideDots);
        btnLogin = findViewById(R.id.btnOpenLogin);
        btnSignUp = findViewById(R.id.btnOpenSignUp);
        viewPager = findViewById(R.id.Viewpager);
        slidePagerAdapter = new SlidePagerAdapter(this);
        Timer timer = new Timer();
        handler = new Handler();

        viewPager.setAdapter(slidePagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setBackgroundColor(Color.BLACK);

        try {
            runnable = new Runnable() {
                @Override
                public void run() {

                    int count = viewPager.getCurrentItem();
                    if (count == slidePagerAdapter.slideDescriptions.length - 1) {
                        count = 0;
                        viewPager.setCurrentItem(count, true);
                    } else {
                        count++;
                        viewPager.setCurrentItem(count, true);

                    }

                }
            };

            try {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(runnable);
                    }
                }, 2000, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Method defined in the OnClicklistener interface
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenLogin:
                //keep track in logcat
                Log.d(TAG, "onClick: " + "Login opened");
                //opens Login activity
                startActivity(new Intent(WelcomeScreenActivity.this, LoginActivity.class));
                break;

            case R.id.btnOpenSignUp:
                Log.d(TAG, "onClick: " + "Sign up opened");
                //opens registration activity
                startActivity(new Intent(WelcomeScreenActivity.this, NameActivity.class));
                break;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
