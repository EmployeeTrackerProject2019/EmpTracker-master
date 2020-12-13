package com.employee.employeetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminAttendanceFragment extends Fragment {

    public AdminAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout mBottomSheetConstraintLayout = view.findViewById(R.id.moreBottomSheetAdmin);
        BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetConstraintLayout);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
