package com.employee.employeetracker.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.employee.employeetracker.R;

public class MakeAReportBottomSheet extends BottomSheetDialogFragment {
    private TextInputLayout txtTitle, txtDescription;
    private MakeReportListener makeReportListener;
    private ProgressBar loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_make_report, container, false);

        txtTitle = view.findViewById(R.id.textInputLayoutReportTitle);
        txtDescription = view.findViewById(R.id.textInputLayoutReportDescription);
        loading = view.findViewById(R.id.progressBarLoadingReport);


        view.findViewById(R.id.btnSubmitReport).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                validateInputs();

            }
        });


        return view;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            makeReportListener = (MakeReportListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement interface");
        }


    }

    private void validateInputs() {
        String getReportTitle = txtTitle.getEditText().getText().toString();
        String getReportDescription = txtDescription.getEditText().getText().toString();

        //        check and validate inputs
        if (!TextUtils.isEmpty(getReportTitle) && (!TextUtils.isEmpty(getReportDescription)) && getReportTitle.length() > 10 && getReportDescription.length() > 20) {
            txtTitle.setErrorEnabled(false);
            makeReportListener.onButtonClicked(getReportTitle, getReportDescription);
            loading.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    loading.setVisibility(View.INVISIBLE);
                    dismiss();


                }
            }, 2000);
        } else if (TextUtils.isEmpty(getReportTitle)) {

            loading.setVisibility(View.GONE);
            txtTitle.setError("Report title required");
            txtTitle.setErrorEnabled(true);

        } else if (TextUtils.isEmpty(getReportDescription)) {

            loading.setVisibility(View.GONE);
            txtDescription.setError("You must give a detailed description");
            txtDescription.setErrorEnabled(true);

        } else if (getReportTitle.length() <= 5) {

            loading.setVisibility(View.GONE);
            txtTitle.setError("Report title too short");
            txtTitle.setErrorEnabled(true);

        } else if (getReportDescription.length() <= 10) {

            loading.setVisibility(View.GONE);
            txtTitle.setError("Report description too short");
            txtTitle.setErrorEnabled(true);

        } else if (getReportTitle.matches("[-+]?\\d+(\\.\\d+)?")) {

            loading.setVisibility(View.GONE);
            txtTitle.setError("Invalid inputs");
            txtTitle.setErrorEnabled(true);
        }
    }

    public interface MakeReportListener {
        void onButtonClicked(String title, String description);
    }


}
