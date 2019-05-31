package com.employee.employeetracker.activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditReportActivity extends AppCompatActivity {
private DatabaseReference reportDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);
        Toolbar toolbar = findViewById(R.id.toolBarEditReport);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //get data from the view holder
        String getImage = getIntent().getStringExtra("image");//image
        String getAdapterPosition = getIntent().getStringExtra("position");
        String getTitle = getIntent().getStringExtra("title");
        String getContent = getIntent().getStringExtra("content");

        //db reference
        reportDbRef = FirebaseDatabase.getInstance().getReference("Reports").child(getAdapterPosition);

        TextInputLayout txtTitle = findViewById(R.id.inputReportTitle);
        TextInputLayout txtContent = findViewById(R.id.inputReportContent);
        CircleImageView userImage = findViewById(R.id.imgEditReportPhoto);

        txtTitle.getEditText().setText(getTitle);
        txtContent.getEditText().setText(getContent);
        Glide.with(this).load(getImage).into(userImage);


        //check if title and content are not empty and update database


    }
}
