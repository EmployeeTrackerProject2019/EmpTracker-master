package com.employee.employeetracker;

import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class MapsActivity extends AppCompatActivity {
    DatabaseReference dbCheckIn;
    private TextView name, dutyPost, shift, checkcInTime;
    private String getName, getDUTY, GETsHIFT, getCheckInTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        BottomAppBar bar = findViewById(R.id.bottomAppBar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the navigation click by showing a BottomDrawer etc.
            }
        });
    }


}
