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
public class NameFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NameFragment";
    private View view;
    private TextInputLayout txtFirstName, txtLastName;
    private Button btnNext;
    private FragmentManager fragmentManager;
    private EmailPasswordFragment emailPasswordFragment;

    public NameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initViews();
        initListeners();
    }

    private void initListeners() {
        btnNext.setOnClickListener(this);
    }

    private void initViews() {
        fragmentManager = getFragmentManager();
        emailPasswordFragment = new EmailPasswordFragment();
        txtFirstName = view.findViewById(R.id.FirstnameLayout);
        txtLastName = view.findViewById(R.id.LastnameLayout);
        btnNext = view.findViewById(R.id.btnNext);

    }

    @Override
    public void onClick(View v) {
        validateAndProceed();
    }

    private void validateAndProceed() {
        String getFirstName = txtFirstName.getEditText().getText().toString();
        String getLastName = txtLastName.getEditText().getText().toString();

        if (getFirstName.isEmpty()) {
            txtFirstName.setErrorEnabled(true);
            txtFirstName.setError("First name required");
        } else {
            txtFirstName.setErrorEnabled(false);
        }

        if (getLastName.isEmpty()) {
            txtLastName.setErrorEnabled(true);
            txtLastName.setError("Last name required");
        } else {
            txtLastName.setErrorEnabled(false);
        }

        if (!txtFirstName.getEditText().getText().toString().isEmpty()
                && !txtLastName.getEditText().getText().toString().isEmpty()) {
            txtFirstName.setErrorEnabled(false);
            txtLastName.setErrorEnabled(true);

            Bundle bundle = new Bundle();
            bundle.putString("firstName", getFirstName);
            bundle.putString("lastName", getLastName);

            emailPasswordFragment.setArguments(bundle);
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.welcomeFrameLayout, emailPasswordFragment)
                    .addToBackStack("nameFragment")
                    .commit();

            Log.d(TAG, "First name: " + getFirstName + "\n " + "Last name " + getLastName);


        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getActivity().getSharedPreferences("namePrefs",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("firstName", txtFirstName.getEditText().getText().toString());
            editor.putString("lastName", txtLastName.getEditText().getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("namePrefs",
                    Context.MODE_PRIVATE);
            String fname = sharedPreferences.getString("firstName", "");
            String lname = sharedPreferences.getString("lastName", "");

            txtFirstName.getEditText().setText(fname);
            txtLastName.getEditText().setText(lname);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
