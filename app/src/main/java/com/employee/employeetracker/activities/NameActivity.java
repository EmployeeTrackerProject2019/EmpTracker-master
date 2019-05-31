package com.employee.employeetracker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.employee.employeetracker.R;

public class NameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NameActivity";
    private TextInputLayout txtFirstName, txtLastName;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        initViews();
        initListeners();
    }

    private void initListeners() {
        btnNext.setOnClickListener(this);
    }

    private void initViews() {
        txtFirstName = findViewById(R.id.FirstnameLayout);
        txtLastName = findViewById(R.id.LastnameLayout);
        btnNext = findViewById(R.id.btnNext);

    }

    @Override
    public void onClick(View v) {
        validateAndProceed();

    }

    private void validateAndProceed() {
        String getFirstName = txtFirstName.getEditText().getText().toString();
        String getLastName = txtLastName.getEditText().getText().toString();

        if (getFirstName.isEmpty()) {
            txtFirstName.setErrorEnabled(true);
            txtFirstName.setError("First name required");
        } else {
            txtFirstName.setErrorEnabled(false);
        }

        if (getLastName.isEmpty()) {
            txtLastName.setErrorEnabled(true);
            txtLastName.setError("Last name required");
        } else {
            txtLastName.setErrorEnabled(false);
        }

        if (!txtFirstName.getEditText().getText().toString().isEmpty() && !txtLastName.getEditText().getText().toString().isEmpty()) {
            txtFirstName.setErrorEnabled(false);
            txtLastName.setErrorEnabled(true);

            Intent gotToEmailPassActivity = new Intent(NameActivity.this, EmailPassActivity.class);
            gotToEmailPassActivity.putExtra("firstName", txtFirstName.getEditText().getText().toString());
            gotToEmailPassActivity.putExtra("lastName", txtLastName.getEditText().getText().toString());
            startActivity(gotToEmailPassActivity);


            Log.d(TAG, "First name: " + getFirstName + "\n " + "Last name " + getLastName);


        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getSharedPreferences("namePrefs",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("firstName", txtFirstName.getEditText().getText().toString());
            editor.putString("lastName", txtLastName.getEditText().getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("namePrefs",
                    MODE_PRIVATE);
            String fname = sharedPreferences.getString("firstName", "");
            String lname = sharedPreferences.getString("lastName", "");

            txtFirstName.getEditText().setText(fname);
            txtLastName.getEditText().setText(lname);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
