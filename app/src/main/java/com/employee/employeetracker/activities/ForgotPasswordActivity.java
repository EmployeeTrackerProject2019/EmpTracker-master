package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.employee.employeetracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private FirebaseAuth mAuth;
    private TextInputLayout forgotPassLayout;
    private ProgressDialog startLoading;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.toolbarForgotPassToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        startLoading = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        forgotPassLayout = findViewById(R.id.userEmailForgot);


        findViewById(R.id.btnResetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUserEmail = forgotPassLayout.getEditText().getText().toString();
                if (!TextUtils.isEmpty(getUserEmail)) {
                    startLoading.setMessage("loading please wait ...");
                    startLoading.setCancelable(false);
                    startLoading.show();
                    sendResetPasswordLink(getUserEmail);
                } else {
                    startLoading.dismiss();
                    forgotPassLayout.setErrorEnabled(true);
                    forgotPassLayout.setError("please enter your email");
//                    Toast.makeText(ForgotPasswordActivity.this, "please enter your email", Toast.LENGTH_SHORT).show();
                }

            }

            private void sendResetPasswordLink(final String getUserEmail) {
                mAuth.sendPasswordResetEmail(getUserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Password reset successfully sent ");
                            startLoading.dismiss();
                            if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(200);
                            }
                            new AlertDialog.Builder(ForgotPasswordActivity.this)
                                    .setMessage("A password reset link has been sent to " + getUserEmail + "\n" + "please verify and change your password")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            finish();
//                                                dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            Log.d(TAG, "Errot : Password reset successfully sent "
                                    + task.getException());
                            startLoading.dismiss();
                            Toast toast = Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }


                    }
                });
            }
        });
    }
}
