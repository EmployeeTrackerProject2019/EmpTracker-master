package com.employee.employeetracker;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MyActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        ConstraintLayout mBottomSheetConstraintLayout = findViewById(R.id.moreBottomSheetAdmin);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetConstraintLayout);
        //HIDE state
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ImageButton mMore = findViewById(R.id.imgBtnMoreOptions);
        mMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //expand state
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

    }

}
