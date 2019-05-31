package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.utils.MyToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditReportActivity extends AppCompatActivity {
    private DatabaseReference reportDbRef;
    private String title, content;
    private TextInputLayout txtTitle, txtContent;
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
        String getContent = getIntent().getStringExtra("content");

        //db reference
        reportDbRef = FirebaseDatabase.getInstance().getReference("Reports").child(getAdapterPosition);

        txtTitle = findViewById(R.id.inputReportTitle);
        txtContent = findViewById(R.id.inputReportContent);
        CircleImageView userImage = findViewById(R.id.imgEditReportPhoto);

        txtTitle.getEditText().setText(getTitle);
        txtContent.getEditText().setText(getContent);
        Glide.with(this).load(getImage).into(userImage);

//Get details from user
        title = txtTitle.getEditText().getText().toString();
        content = txtContent.getEditText().getText().toString();

        findViewById(R.id.btnUpdateReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if title and content are not empty and update database
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
                            updateReportFromUser(title, content);
                        }
                    }).create().show();


                } else if (TextUtils.isEmpty(txtTitle.getEditText().getText().toString())) {
                    txtTitle.setError("Title required");
                    txtTitle.setErrorEnabled(true);
                } else if (!TextUtils.isEmpty(txtContent.getEditText().getText().toString())) {
                    txtContent.setError("Report Content required");
                    txtTitle.setErrorEnabled(true);
                }


            }
        });

    }

    private void updateReportFromUser(String title, String content) {

        HashMap<String, Object> updateReport = new HashMap<>();
        updateReport.put("title", title);
        updateReport.put("description", content);

        //now update the database
        reportDbRef.updateChildren(updateReport).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    MyToast.makeToast("Successfully updated");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                MyToast.makeToast("Error: " + e.getMessage());
            }
        });
    }


}
