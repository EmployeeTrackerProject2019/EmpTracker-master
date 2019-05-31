package com.employee.employeetracker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MyToast {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public MyToast(Context context) {
        MyToast.context = context;
    }

    public static void makeToast(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
