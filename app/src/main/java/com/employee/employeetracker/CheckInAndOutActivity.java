package com.employee.employeetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

public class CheckInAndOutActivity extends AppCompatActivity {
    private static final String TAG = "CheckInAndOutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_and_out);


        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStart", true);

//                        showWarning
        if (firstStart) {
            showWarningDialog();
        }
//showWarningDialog();
        TitleFAB a = findViewById(R.id.to_checkIn);
        TitleFAB b = findViewById(R.id.to_checkOut);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckInAndOutActivity.this, MyActivity.class));
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckInAndOutActivity.this, "Check out clicked", Toast.LENGTH_SHORT).show();
            }
        });

        final FABsMenu menu = findViewById(R.id.fabs_menu);
        menu.setMenuUpdateListener(new FABsMenuListener() {
            // You don't need to override all methods. Just the ones you want.

            @Override
            public void onMenuClicked(FABsMenu fabsMenu) {
                super.onMenuClicked(fabsMenu); // Default implementation opens the menu on click


            }

            @Override
            public void onMenuCollapsed(FABsMenu fabsMenu) {
                super.onMenuCollapsed(fabsMenu);

            }

            @Override
            public void onMenuExpanded(FABsMenu fabsMenu) {
                super.onMenuExpanded(fabsMenu);
                fabsMenu.getId();
            }
        });

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
