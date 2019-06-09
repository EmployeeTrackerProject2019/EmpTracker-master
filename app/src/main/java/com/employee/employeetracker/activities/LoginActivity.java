package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.employee.employeetracker.R;
import com.employee.employeetracker.security.MD5Encryption;
import com.employee.employeetracker.utils.GetDateTime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private TextInputLayout txtEmail, txtPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog startLoading;
    private Button btnLogin;
    private TextView txtForgotPass;
    private String datePosted;
    private DatabaseReference historyDbRef;
    private Vibrator vibrator;
//    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolBarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        startLoading = new ProgressDialog(this);
//        DatabaseReference employeeDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Employee");
        //All user inputs
        txtEmail = findViewById(R.id.txtEmailLayout);
        txtPassword = findViewById(R.id.txtPasswordLayout);
        btnLogin = findViewById(R.id.btnLoginUser);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        txtForgotPass = findViewById(R.id.txtForgotPass);

        //create the history database
        String log = "History";
        historyDbRef = FirebaseDatabase.getInstance().getReference(log);


    }

    //Method to log in and verify if email has been created before creating the database
    private void logUsersIn() {
        try {

            try {
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
                datePosted = GetDateTime.getFormattedDate(today);
                //datePosted = sfd.format(new Date(today.toString()));

            } catch (Exception e) {
                e.printStackTrace();
            }


            final String email = txtEmail.getEditText().getText().toString();
            byte[] password = txtPassword.getEditText().getText().toString().getBytes();
//validate inputs from the user
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(Arrays.toString(password))) {
                //perform Encryption
                BigInteger md5BigInteger = null;

                try {
                    md5BigInteger = new BigInteger(1, MD5Encryption.encryptData(password));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                assert md5BigInteger != null;
                final String encryptedPass = md5BigInteger.toString(16);

                startLoading.setMessage("loading please wait ...");
                startLoading.setCancelable(false);
                startLoading.show();

                mAuth.signInWithEmailAndPassword(email, encryptedPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        if (task.isSuccessful()) {

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

                                Intent openMainPage = new Intent(LoginActivity.this, MainActivity.class);
                                openMainPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(openMainPage);
                                finish();

                            } else {
                                startLoading.dismiss();
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("Your email" + " " + email + " " + "\n" + "is not yet verified" + "\n" + "please  verify to continue")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create()
                                        .show();


                                if (Build.VERSION.SDK_INT >= 26)
                                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                else
                                    vibrator.vibrate(200);


                            }


                        } else {

                            Log.d(TAG, "Log in failed: " + task.getException().getMessage());
                            startLoading.dismiss();
                            String errorMsg = task.getException().getMessage();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setIcon(R.drawable.sorry)
                                    .setTitle("Log in Failed")
                                    .setMessage(errorMsg)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();

//                            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            } else {
                startLoading.dismiss();
                Toast.makeText(this, "All fields required to login", Toast.LENGTH_SHORT).show();
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

    //link to go to sign up activity
    public void gotoSignUP(View view) {
        startActivity(new Intent(LoginActivity.this, NameActivity.class));
    }

    //Method from the on click interface
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginUser:
                //get details from users and log in
                logUsersIn();
                break;
            case R.id.txtForgotPass:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
        }

    }


    //life cycle
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("loginEmail", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", txtEmail.getEditText().getText().toString());
        editor.apply();
    }

    //life cycle
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("loginEmail", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        txtEmail.getEditText().setText(email);


    }
}
