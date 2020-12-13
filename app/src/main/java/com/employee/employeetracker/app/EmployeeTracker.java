package com.employee.employeetracker.app;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class EmployeeTracker extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        if (mAuth.getCurrentUser() == null) {
//            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

}
