package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditReportActivity extends AppCompatActivity {
    private DatabaseReference reportDbRef;
    private String mTitle, mContent;
    private TextInputLayout txtTitle, txtContent;
    private TextInputEditText mGetTitle, mGetContent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);
        Toolbar toolbar = findViewById(R.id.toolBarEditReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while report is updated");
        progressDialog.setCancelable(false);

        //get data from the view holder
        String getImage = getIntent().getStringExtra("image");//image
        String getAdapterPosition = getIntent().getStringExtra("position");
        String getTitle = getIntent().getStringExtra("title");
        String getContent = getIntent().getStringExtra("description");

        //db reference
        reportDbRef = FirebaseDatabase.getInstance().getReference().child("Reports").child(getAdapterPosition);

        txtTitle = findViewById(R.id.inputReportTitle);
        txtContent = findViewById(R.id.inputReportContent);
        mGetTitle = findViewById(R.id.reportTitle);
        mGetContent = findViewById(R.id.reportContent);
        CircleImageView userImage = findViewById(R.id.imgEditReportPhoto);

        txtTitle.getEditText().setText(getTitle);
        txtContent.getEditText().setText(getContent);
        Glide.with(this).load(getImage).into(userImage);

//Get details from user
        mTitle = mGetTitle.getText().toString();
        mContent = mGetContent.getText().toString();


        mGetTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    mTitle = s.toString();
                }
            }
        });

        mGetContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    mContent = s.toString();
                }
            }
        });


        findViewById(R.id.btnUpdateReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if mTitle and mContent are not empty and update database
                if (!TextUtils.isEmpty(txtTitle.getEditText().getText().toString()) && !TextUtils.isEmpty(txtContent.getEditText().getText().toString())) {
                    //prompt user to confirm
                    new AlertDialog.Builder(EditReportActivity.this)
                            .setMessage("Are you sure you want to update the report?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            progressDialog.show();
                            //call method to perform update
                            updateReportFromUser();
                        }
                    }).create().show();


                }

                if (TextUtils.isEmpty(txtTitle.getEditText().getText().toString())) {
                    txtTitle.setErrorEnabled(true);
                    txtTitle.setError("Title required");

                }
                if (TextUtils.isEmpty(txtContent.getEditText().getText().toString())) {
                    txtContent.setErrorEnabled(true);
                    txtContent.setError("Report Content required");

                }


            }
        });

    }

    private void updateReportFromUser() {

        Map<String, Object> updateReport = new HashMap<>();
        updateReport.put("title", mTitle);
        updateReport.put("description", mContent);

        //now update the database
        reportDbRef.updateChildren(updateReport).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    makeToast(mTitle + " " + mContent);
                    //makeToast("Successfully updated");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                makeToast("Error: " + e.getMessage());
            }
        });
    }

    void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
