package com.employee.employeetracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet.PhoneNumberBottomSheetListener;

public class EditProfilePhoneNumberBottomSheet extends BottomSheetDialogFragment implements PhoneNumberBottomSheetListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.layout_view_employee_profile_from_attendance, container, false);
    }

    @Override
    public void onButtonClicked(String text) {

    }
}
