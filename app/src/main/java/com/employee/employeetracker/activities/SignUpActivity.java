package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Users;
import com.employee.employeetracker.utils.GetDateTime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    private TextInputLayout txtFirstName, txtLastName, txtEmail, txtPassword, txtConfirmPassword;
    private Button btnContinue;
    private DatabaseReference employeeDatabaseReference, historyDbRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private final String urlShortNer = "https://bit.ly/2tizS8L";
    private String firstName;
    private String lastName;
    private String email;
    private String datePosted = "";
    private Vibrator vibrator;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.signUpToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initViews();
        initListeners();
    }

    //initialise listeners to the interface
    private void initListeners() {
        btnContinue.setOnClickListener(this);
    }

    //initialise views on the activity
    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
//        FirebaseApp.initializeApp(this);
        //text inputs views
        txtFirstName = findViewById(R.id.FirstnameLayout);
        txtLastName = findViewById(R.id.LastnameLayout);
        txtEmail = findViewById(R.id.emailLayout);
        txtPassword = findViewById(R.id.PasswordLayout);
        txtConfirmPassword = findViewById(R.id.ConfirmPasswordLayout);
        //buttons
        btnContinue = findViewById(R.id.btnSignUp);
        //progress dialogs
        progressDialog = new ProgressDialog(SignUpActivity.this);

//vibration
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        //creates a node for employee database
        employeeDatabaseReference = FirebaseDatabase.getInstance().getReference("Employee");

        String reference = "Employee";

        //create the history database

        String log = "History";
        historyDbRef = FirebaseDatabase.getInstance().getReference(log);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignUp) {
            registerUsers();
        }
    }


    private void registerUsers() {
        try {
            //get values from users
            firstName = txtFirstName.getEditText().getText().toString().trim();
            lastName = txtLastName.getEditText().getText().toString().trim();
            email = txtEmail.getEditText().getText().toString().trim();
            String password = txtPassword.getEditText().getText().toString().trim();
            String confirmPass = txtConfirmPassword.getEditText().getText().toString().trim();
            final String fullName = firstName + " " + lastName;

            try {
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
                datePosted = GetDateTime.getFormattedDate(today);
                //datePosted = sfd.format(new Date(today.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }


            //keep log files
            String historyBuilder;
            historyBuilder =
                    fullName + " " + "created an account on" + " " + datePosted;

            //add to history database
            final Map<String, Object> history = new HashMap<>();
            history.put("history", historyBuilder);
            final String historyID = historyDbRef.push().getKey();


            if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPass)) {
                if (password.equals(confirmPass)) {
                    progressDialog.setMessage("loading please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


//this code is taken from the firebase documentation
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // progressDialog.dismiss();

                                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                uid = firebaseUser.getUid();
                                //Send email verification and create database
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            Log.d(TAG, "onComplete: Email verification has been sent");
                                            Users users = new Users(uid, firstName, lastName,
                                                    fullName, email, "", "", urlShortNer);

                                            employeeDatabaseReference =
                                                    FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);

                                            //Now create the database for the user
                                            employeeDatabaseReference.setValue(users);

                                            //insert data into history database
                                            assert historyID != null;
                                            historyDbRef.child(historyID).setValue(history);
                                            Log.d(TAG, "User has registered: " + historyDbRef);

                                            progressDialog.dismiss();
                                            //for Mashmallow and above
                                            if (Build.VERSION.SDK_INT >= 26) {
                                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                            } else {
                                                //vibrate on lollipop and below
                                                vibrator.vibrate(200);

                                            }
                                            //Alert dialog prompt to show user email link has be
                                            // sent
                                            new AlertDialog.Builder(SignUpActivity.this)
                                                    .setMessage("Hello" + " " + firstName + " " + lastName + " " + "\n" + "an email verification link has been sent to " + email + "\n" + "please verify to continue")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            Intent intent =
                                                                    new Intent(SignUpActivity.this, LoginActivity.class);
                                                            intent.putExtra("userId", uid);
                                                            startActivity(intent);
                                                            finish();
//                                                dialog.dismiss();
                                                        }
                                                    })
                                                    .create()
                                                    .show();


                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            Log.d(TAG,
                                                    "Error occurred: " + task.getException().getMessage());
                                        }

                                    }
                                });


                            } else {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d(TAG, "Error : " + task.getException().getMessage());
                            }

                        }
                    });
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "All fields are required to fill", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*
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
    */
    @Override
    protected void onPause() {
        super.onPause();

        try {
            SharedPreferences preferences = getSharedPreferences("RegistrationDetails",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("firstName", txtFirstName.getEditText().getText().toString());
            editor.putString("lastName", txtLastName.getEditText().getText().toString());
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
            SharedPreferences sharedPreferences = getSharedPreferences("RegistrationDetails",
                    MODE_PRIVATE);
            String fname = sharedPreferences.getString("firstName", "");
            String lname = sharedPreferences.getString("lastName", "");
            String email = sharedPreferences.getString("email", "");

            txtFirstName.getEditText().setText(fname);
            txtLastName.getEditText().setText(lname);
            txtEmail.getEditText().setText(email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
