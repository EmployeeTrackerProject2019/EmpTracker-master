package com.employee.employeetracker.utils;

import android.annotation.SuppressLint;
import android.content.Context;

public class MyToast {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public MyToast(Context context) {
        MyToast.context = context;
    }


}
