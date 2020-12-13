package com.employee.employeetracker.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.SlidePagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomePageFragment extends Fragment implements View.OnClickListener {
    //Displays a log message into the logcat
    private static final String TAG = "WelcomePageFragment";
    private SlidePagerAdapter slidePagerAdapter;
    private Button btnLogin, btnSignUp;
    private ViewPager viewPager;
    private Runnable runnable;
    private Handler handler;
    private View view;
    private NameFragment nameFragment;
    private LoginFragment loginFragment;
    private FragmentManager fragmentManager;

    public WelcomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        initViews();
        initListeners();
    }

    private void initListeners() {
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void initViews() {
        CircleIndicator indicator = view.findViewById(R.id.slideDots);
        btnLogin = view.findViewById(R.id.btnOpenLoginFragment);
        btnSignUp = view.findViewById(R.id.btnOpenSignUpFragment);
        viewPager = view.findViewById(R.id.Viewpager);
        slidePagerAdapter = new SlidePagerAdapter(getContext());
        Timer timer = new Timer();
        handler = new Handler();

        viewPager.setAdapter(slidePagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setBackgroundColor(Color.BLACK);

        fragmentManager = getFragmentManager();
        nameFragment = new NameFragment();
        loginFragment = new LoginFragment();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpenLoginFragment:
                //keep track in logcat
                Log.d(TAG, "onClick: " + "Login opened");
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.welcomeFrameLayout, loginFragment)
                        .addToBackStack("login")
                        .commit();

                break;

            case R.id.btnOpenSignUpFragment:
                Log.d(TAG, "onClick: " + "Sign up opened");
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.welcomeFrameLayout, nameFragment)
                        .addToBackStack("name")
                        .commit();
                break;
        }

    }
}
