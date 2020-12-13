package com.employee.employeetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet.PhoneNumberBottomSheetListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
