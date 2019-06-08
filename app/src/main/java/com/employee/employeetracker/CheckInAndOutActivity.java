package com.employee.employeetracker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class CheckInAndOutActivity extends AppCompatActivity {
    private static final String TAG = "CheckInAndOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_and_out);






    }

    private void showWarningDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                builder.setTitle("Warning")
                        .setMessage("Make sure you are connected to your organisations\'s WiFi before you check in or check out.\nIf not please go back and connect")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();


            }
        }, 1000);


        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

}
