package com.employee.employeetracker.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.ForgotPasswordActivity;
import com.employee.employeetracker.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private TextInputLayout txtEmail, txtPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog startLoading;
    private Button btnLogin;
    private TextView txtForgotPass;
    private String datePosted;
    private DatabaseReference historyDbRef;
    private Vibrator vibrator;
    private FirebaseUser firebaseUser;
    private View view;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initViews();
        initListeners();
    }

    private void initListeners() {
        btnLogin.setOnClickListener(this);
        txtForgotPass.setOnClickListener(this);
    }

    private void initViews() {
        // get the user id from registration

        mAuth = FirebaseAuth.getInstance();
        startLoading = new ProgressDialog(getContext());

        //All user inputs
        txtEmail = view.findViewById(R.id.txtEmailLayout);
        txtPassword = view.findViewById(R.id.txtPasswordLayout);
        btnLogin = view.findViewById(R.id.btnLoginUser);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        txtForgotPass = view.findViewById(R.id.txtForgotPass);

        //create the history database
        String log = "History";
        historyDbRef = FirebaseDatabase.getInstance().getReference(log);


    }

    //Method to log in and verify if email has been created before creating the database
    private void logUsersIn() {

        try {

            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
            datePosted = getFormattedDate(today);
            //datePosted = sfd.format(new Date(today.toString()));

            final String email = txtEmail.getEditText().getText().toString();
            String password = txtPassword.getEditText().getText().toString();

            if (email.isEmpty()) {
                txtEmail.setErrorEnabled(true);
                txtEmail.setError("Email required");
            } else {
                txtEmail.setErrorEnabled(false);
            }

            if (password.isEmpty()) {
                txtPassword.setErrorEnabled(true);
                txtPassword.setError("Password required");
            } else {
                txtPassword.setErrorEnabled(false);
            }

//validate inputs from the user
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                startLoading.setMessage("loading please wait ...");
                startLoading.setCancelable(false);
                startLoading.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()) {
//check if the reference match in the employee database and log user in

                            //keep log files
                            String historyBuilder;
                            historyBuilder =
                                    email + " " + "logged in on" + " " + datePosted;

                            //add to history database
                            final Map<String, Object> history = new HashMap<>();
                            history.put("history", historyBuilder);
                            final String historyID = historyDbRef.push().getKey();

                            Log.d(TAG, task.toString());
                            startLoading.dismiss();
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                //insert data into history database
                                assert historyID != null;
                                historyDbRef.child(historyID).setValue(history);
                                Log.d(TAG, "User has logged in: " + historyDbRef);

                                startLoading.dismiss();


                                Intent openMainPage = new Intent(getContext(),
                                        MainActivity.class);
                                openMainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(openMainPage);
                                getActivity().finish();

                            } else {
                                startLoading.dismiss();
                                if (Build.VERSION.SDK_INT >= 26) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    vibrator.vibrate(200);
                                }
                                new AlertDialog.Builder(getContext())
                                        .setMessage("Your email" + " " + email + " " + "\n" + "is not yet verified" + "\n" + "please  verify to continue")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create()
                                        .show();
                            }


                        } else {

                            Log.d(TAG, "Log in failed: " + task.getException().getMessage());
                            startLoading.dismiss();
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);

        switch (day % 10) {
            case 1:
                return new SimpleDateFormat("EEEE MMMM d'st', yyyy", Locale.US).format(date);
            case 2:
                return new SimpleDateFormat("EEEE MMMM d'nd', yyyy", Locale.US).format(date);
            case 3:
                return new SimpleDateFormat("EEEE MMMM d'rd', yyyy", Locale.US).format(date);
            default:
                return new SimpleDateFormat("EEEE MMMM d'th', yyyy", Locale.US).format(date);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginUser:
                //get details from users and log in
                logUsersIn();
                break;
            case R.id.txtForgotPass:
                startActivity(new Intent(getContext(), ForgotPasswordActivity.class));

                break;
            case R.id.txtGoToSignUp:
                NameFragment nameFragment = new NameFragment();
                FragmentManager fm = getFragmentManager();
                assert fm != null;
                fm.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.welcomeFrameLayout, nameFragment)
                        .addToBackStack("login")
                        .commit();

                break;
        }

    }
}
