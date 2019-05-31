package com.employee.employeetracker.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.employee.employeetracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EmailPasswordFragment";
    private View view;
    private TextInputLayout txtEmail, txtPass, txtConfirmPass;
    private Button btnContinue;
    private FragmentManager fragmentManager;
    private PhotoFragment photoFragment;
    private String emailPattern, getFirstName, getLastName;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initViews();
        initListeners();
    }

    private void initListeners() {
        btnContinue.setOnClickListener(this);
    }

    private void initViews() {
        fragmentManager = getFragmentManager();
        photoFragment = new PhotoFragment();
        txtEmail = view.findViewById(R.id.emailFragmentLayout);
        txtPass = view.findViewById(R.id.PasswordFragmentLayout);
        btnContinue = view.findViewById(R.id.btnContinue);
        txtConfirmPass = view.findViewById(R.id.ConfirmPasswordFragmentLayout);

        getFirstName = getArguments().getString("firstName");
        getLastName = getArguments().getString("lastName");

        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    }


    @Override
    public void onClick(View v) {
        validateAndProceed();
    }

    private void validateAndProceed() {
        String getEmail = txtEmail.getEditText().getText().toString();
        String getPass = txtPass.getEditText().getText().toString();
        String getConfirmPass = txtConfirmPass.getEditText().getText().toString();

        if (getEmail.isEmpty()) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError("Email required");
        } else {
            txtEmail.setErrorEnabled(false);
        }

        if (getPass.isEmpty()) {
            txtPass.setErrorEnabled(true);
            txtPass.setError("Password required");
        } else {
            txtPass.setErrorEnabled(false);
        }

        if (getConfirmPass.isEmpty()) {
            txtConfirmPass.setErrorEnabled(true);
            txtConfirmPass.setError("Password required");
        } else {
            txtConfirmPass.setErrorEnabled(false);
        }

        if (!getPass.equals(getConfirmPass)) {
            txtConfirmPass.setErrorEnabled(true);
            txtConfirmPass.setError("Passwords do not match");
        }

        if (!getEmail.matches(emailPattern)) {
            txtEmail.setErrorEnabled(true);
            txtEmail.setError("Please check email format");
        }

        if (getPass.length() < 6 || getConfirmPass.length() < 6) {
            txtPass.setErrorEnabled(true);
            txtPass.setError("Password is too short");
        }

        if (!txtEmail.getEditText().getText().toString().isEmpty() && !txtPass.getEditText().getText().toString().isEmpty() && !txtConfirmPass.getEditText().getText().toString().isEmpty() && getEmail.matches(emailPattern)) {
            if (getPass.equals(getConfirmPass)) {

                Bundle bundle = new Bundle();
                bundle.putString("firstName", getFirstName);
                bundle.putString("lastName", getLastName);
                bundle.putString("email", getEmail);
                bundle.putString("pass", getPass);
                bundle.putString("confirmPass", getConfirmPass);

                photoFragment.setArguments(bundle);
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.welcomeFrameLayout, photoFragment)
                        .addToBackStack("emailPass")
                        .commit();

                Log.d(TAG, getFirstName + " " + getLastName + getEmail + getPass + getConfirmPass);

            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("emailPrefs",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", txtEmail.getEditText().getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("emailPrefs",
                    Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            txtEmail.getEditText().setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
