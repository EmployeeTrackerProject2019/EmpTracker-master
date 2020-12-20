package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.employee.employeetracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneNumberAuthenticationActivity extends AppCompatActivity {
    private static final String TAG = "Phone Auth";
    private String getIntentPhoneNumber;
    private String mVerificationCode;
    private TextInputLayout txtVerifyCode;
    private DatabaseReference userDbRef;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //this method automatically handles the code sent
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneNumberAuthenticationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_authentication);
        Toolbar toolbar = findViewById(R.id.VerificationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btnVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtVerifyCode = findViewById(R.id.InputLayoutCode);
                //create a method to verify the verification code
                String code = txtVerifyCode.getEditText().getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    txtVerifyCode.setError("Invalid Verification code");
                    txtVerifyCode.requestFocus();
                    return;
                }

                verifyCode(code);
            }
        });

        initViews();

    }

    private void initViews() {
        //receive the number from the user
        getIntentPhoneNumber = getIntent().getStringExtra("number");

        TextView showNumberOnToolBar = findViewById(R.id.txtShowVerificationNumber);
        showNumberOnToolBar.setText(String.format("Verify %s", getIntentPhoneNumber));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        String uid = mFirebaseUser.getUid();

        userDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);
        //synchronise the database offline
        userDbRef.keepSynced(true);

        //method sends the verification code to the number
        sendVerificationCode(getIntentPhoneNumber);
    }

    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode, code);
        //method to sign in user
        signInWithPhoneNumber();

    }


    private void signInWithPhoneNumber() {


                    HashMap<String, Object> userPhone = new HashMap<>();
                    userPhone.put("phone", getIntentPhoneNumber);

                    userDbRef.updateChildren(userPhone).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                new AlertDialog.Builder(PhoneNumberAuthenticationActivity.this)
                                        .setMessage("Phone number has been verified")
                                        .setPositiveButton("OK", new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                                startActivity(new Intent(PhoneNumberAuthenticationActivity.this
                                                        , EditProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                finish();
                                            }
                                        }).create();
                            } else {
                                new AlertDialog.Builder(PhoneNumberAuthenticationActivity.this)
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton("OK", new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        }).create();
                            }

                        }
                    });



            }


    private void showDialog() {
        new AlertDialog.Builder(PhoneNumberAuthenticationActivity.this)
                .setMessage("A verification code has been sent to ")
                .setPositiveButton("OK", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).create();

    }

    private ProgressDialog loading;


}
