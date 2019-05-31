package com.employee.employeetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
