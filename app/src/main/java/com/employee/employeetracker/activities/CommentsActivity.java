package com.employee.employeetracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.CommentsAdapter;
import com.employee.employeetracker.models.Report;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {
    private static final String TAG = "CommentsActivity";
    private TextInputLayout edtComment;
    private CommentsAdapter adapter;
    private DatabaseReference reportsDbRef, commentsDbRef;
    private String uid, getName, getImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Toolbar toolbar = findViewById(R.id.commentToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btnReplyBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        initViews();
        setUpRecycler();
    }

    private void initViews() {

        //Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();

        //get data from the view holder
        getImage = getIntent().getStringExtra("image");//image
        String getAdapterPosition = getIntent().getStringExtra("position");//adapter position of the item
        getName = getIntent().getStringExtra("name");//name of user
        String getContent = getIntent().getStringExtra("description");//content of the report

        CircleImageView mUserImage = findViewById(R.id.imageViewComment);
        TextView txtName = findViewById(R.id.txtShowCommentName);
        TextView txtContent = findViewById(R.id.txtShowContent);
        edtComment = findViewById(R.id.edtUserComment);

        txtName.setText(getName);
        txtContent.setText(getContent);
        Glide.with(this).load(getImage).into(mUserImage);

//        reportsDbRef = FirebaseDatabase.getInstance().getReference().child("Reports").child(getAdapterPosition);
//        reportsDbRef.keepSynced(true);

        commentsDbRef = FirebaseDatabase.getInstance().getReference().child("Reports").child(getAdapterPosition);
        //   commentsDbRef.keepSynced(true);


    }


    private void setUpRecycler() {
        Log.d(TAG, "setUpRecycler: completed");
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
            itemDecoration.setDrawable(
                    ContextCompat.getDrawable(CommentsActivity.this, R.drawable.recycler_divider)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference postComments = commentsDbRef.child("Comments");
        postComments.keepSynced(true);
        // commentsDbRef.keepSynced(true);

        //querying the database base of the time posted
        Query query = postComments.orderByChild("timeStamp");

        FirebaseRecyclerOptions<Report> options = new FirebaseRecyclerOptions.Builder<Report>().
                setQuery(query, Report.class).build();

        adapter = new CommentsAdapter(options);


        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //attach adapter to recycler view
        recyclerView.setAdapter(adapter);
        //notify data change
        adapter.notifyDataSetChanged();

    }


    private void addComment() {

        String postComment = edtComment.getEditText().getText().toString();

        if (!postComment.isEmpty()) {
            HashMap<String, Object> comments = new HashMap<>();
            comments.put("comments", postComment);
            comments.put("userId", uid);
            comments.put("timeStamp", ServerValue.TIMESTAMP);
            comments.put("fullName", getName);
            comments.put("image", getImage);

            String commentID = commentsDbRef.push().getKey();
            assert commentID != null;

            commentsDbRef.child("Comments").child(commentID).setValue(comments).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        makeToast("Successfully sent");
                        edtComment.getEditText().setText("");
                        Log.d(TAG, "onComplete: " + "comments posted");
                    }

                    // setUpRecycler();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    makeToast("Error: " + e.getMessage());
                    //edtComment.getEditText().setText("");
                }
            });
        } else {
            edtComment.setError("Comment cannot be empty");
            //  makeToast("Comment cannot be empty");
        }

    }


    void makeToast(String s) {
        Toast toast = Toast.makeText(CommentsActivity.this, s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
