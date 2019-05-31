package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.employee.employeetracker.R;
import com.employee.employeetracker.fragments.PhotoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class EmailPassActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassActivity";
    private FirebaseAuth auth;
    private ProgressDialog loading;
    private TextInputLayout txtEmail, txtPass, txtConfirmPass;
    private Button btnContinue;
    private String emailPattern, getFirstName, getLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_pass);
        Intent intent = getIntent();
        if (intent != null) {

            getFirstName = intent.getStringExtra("firstName");
            getLastName = intent.getStringExtra("lastName");
        }
        initViews();
        initListeners();
    }


    private void initListeners() {
        btnContinue.setOnClickListener(this);
    }

    private void initViews() {
        auth = FirebaseAuth.getInstance();
        PhotoFragment photoFragment = new PhotoFragment();
        txtEmail = findViewById(R.id.emailFragmentLayout);
        txtPass = findViewById(R.id.PasswordFragmentLayout);
        btnContinue = findViewById(R.id.btnContinue);
        txtConfirmPass = findViewById(R.id.ConfirmPasswordFragmentLayout);
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        loading = new ProgressDialog(this);


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


        loading.setMessage("Verifying email...Please wait");

        if (!txtEmail.getEditText().getText().toString().isEmpty() && !txtPass.getEditText().getText().toString().isEmpty() && !txtConfirmPass.getEditText().getText().toString().isEmpty() && getEmail.matches(emailPattern)) {
            if (getPass.equals(getConfirmPass)) {
                loading.show();

                try {

                    auth.fetchSignInMethodsForEmail(txtEmail.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = !task.getResult().getSignInMethods().isEmpty();
                            if (task.isSuccessful()) {
                                if (!check) {

                                    loading.dismiss();
                                    Intent gotoPhotoActivity = new Intent(EmailPassActivity.this,
                                            PhotoRegisterActivity.class);
                                    gotoPhotoActivity.putExtra("passFirstName", getFirstName);
                                    gotoPhotoActivity.putExtra("passLastName", getLastName);
                                    gotoPhotoActivity.putExtra("passEmail", txtEmail.getEditText().getText().toString());
                                    gotoPhotoActivity.putExtra("passPassword", txtPass.getEditText().getText().toString());
                                    startActivity(gotoPhotoActivity);


                                } else {
                                    loading.dismiss();
                                    Toast.makeText(getApplicationContext(), "You already have an account. Please login.", Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(EmailPassActivity.this, LoginActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }


                            } else {

                                Toast.makeText(EmailPassActivity.this,
                                        "Error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getSharedPreferences("emailPrefs",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", txtEmail.getEditText().getText().toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            SharedPreferences sharedPreferences = getSharedPreferences("emailPrefs",
                    MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");

            txtEmail.getEditText().setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
