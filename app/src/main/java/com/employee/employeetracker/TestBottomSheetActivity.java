package com.employee.employeetracker;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.employee.employeetracker.activities.MainActivity;
import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet;
import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet.PhoneNumberBottomSheetListener;

public class TestBottomSheetActivity extends AppCompatActivity implements PhoneNumberBottomSheetListener {
    private TextView SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bottom_sheet);

        SHOW = findViewById(R.id.txtShowMsg);

        findViewById(R.id.btnShowMsg).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - MainActivity.mLastClickTime < 1000) {
                    return;
                }

                MainActivity.mLastClickTime = SystemClock.elapsedRealtime();

                PhoneNumberBottomSheet phoneNumberBottomSheet = new PhoneNumberBottomSheet();
                phoneNumberBottomSheet.show(getSupportFragmentManager(), "phoneNumber");

                EditProfilePhoneNumberBottomSheet editProfileBottomSheet = new EditProfilePhoneNumberBottomSheet();
                editProfileBottomSheet.show(getSupportFragmentManager(), "editProfile");


            }
        });
    }

    @Override
    public void onButtonClicked(String text) {
        SHOW.setText(text);
    }
}
