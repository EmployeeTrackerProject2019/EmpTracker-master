package com.employee.employeetracker.bottomsheets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.PhoneNumberAuthenticationActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class PhoneNumberBottomSheet extends BottomSheetDialogFragment {
    private PhoneNumberBottomSheetListener phoneNumberBottomSheetListener;
    private String getPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_add_phone_number, container, false);

        final CountryCodePicker ccp = view.findViewById(R.id.ccp);
        final TextInputLayout txtNumber = view.findViewById(R.id.textInputLayoutPhone);
        final ProgressBar loading = view.findViewById(R.id.progressBarVerify);


        ccp.registerCarrierNumberEditText(txtNumber.getEditText());
        ccp.setNumberAutoFormattingEnabled(true);

        view.findViewById(R.id.btnRegisterPhoneNumber).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String getPhoneNumber = txtNumber.getEditText().getText().toString();

                if (!TextUtils.isEmpty(getPhoneNumber) && getPhoneNumber.length() == 10) {
                    txtNumber.setEnabled(false);

                    getPhone = ccp.getFormattedFullNumber();

                    //calls method in the interface
                    phoneNumberBottomSheetListener.onButtonClicked(getPhone);

                    loading.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loading.setVisibility(View.INVISIBLE);
                            dismiss();
                            Intent intent = new Intent(getActivity(),
                                    PhoneNumberAuthenticationActivity.class);
                            intent.putExtra("number", getPhone);
                            startActivity(intent);

                        }
                    }, 2000);


                } else if (TextUtils.isEmpty(getPhoneNumber)) {
                    loading.setVisibility(View.GONE);
                    txtNumber.setError("Phone number required");
                    txtNumber.setEnabled(true);

                } else if (getPhoneNumber.length() < 10) {
                    loading.setVisibility(View.GONE);
                    txtNumber.setError("Number is not correct");
                    txtNumber.setEnabled(true);
                }


            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            phoneNumberBottomSheetListener = (PhoneNumberBottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement interface");
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface PhoneNumberBottomSheetListener {
        void onButtonClicked(String text);

    }


}
