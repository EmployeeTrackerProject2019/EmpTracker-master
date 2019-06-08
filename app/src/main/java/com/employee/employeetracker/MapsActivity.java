package com.employee.employeetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity {
    private DatabaseReference dbCheckIn;
    private CircleImageView userPhoto;
    private TextView txtName, txtDutyPost, txtShift, txtCheckcInTime;
    private String getName, getDuty, getShift, getCheckInTime, getPhoto, adapterPosition;
    private Intent intent;

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

        initViews();
    }

    private void initViews() {
        intent = getIntent();
        txtName = findViewById(R.id.checkOutName);
        txtDutyPost = findViewById(R.id.txtShhDutyPost);
        txtShift = findViewById(R.id.txtShhShift);
        txtCheckcInTime = findViewById(R.id.txtShhCheckInTime);
        userPhoto = findViewById(R.id.checkInPhoto);

        if (intent != null) {
            adapterPosition = intent.getStringExtra("position");
            getName = intent.getStringExtra("name");
            getDuty = intent.getStringExtra("dutyPost");
            getShift = intent.getStringExtra("shift");
            getPhoto = intent.getStringExtra("photo");
            getCheckInTime = intent.getStringExtra("checkInTime");

            txtName.setText(getName);
            txtDutyPost.setText(getDuty);
            txtShift.setText(getShift);
            txtCheckcInTime.setText(getCheckInTime);
            Glide.with(this).load(getPhoto).into(userPhoto);
        }

    }


}
