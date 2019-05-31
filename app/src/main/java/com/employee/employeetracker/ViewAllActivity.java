package com.employee.employeetracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAllActivity extends AppCompatActivity {
    private static final String TAG = "Employee ";
    private DatabaseReference mAttendanceDbRef;
    String getUsersId;
    FirebaseUser mUser;
    private FirebaseRecyclerAdapter<Employee, ShowAttendanceViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        setUpRecycler();
    }


    private void initViews() {

    }

    @SuppressWarnings("EmptyTryBlock")
    private void setUpRecycler() {
        Log.d(TAG, "setUpRecycler: completed");
        final RecyclerView recyclerView = findViewById(R.id.recyclerAll);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ViewAllActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        //now set the drawable of the item decorator
        try {
//            itemDecoration.setDrawable(
            //      ContextCompat.getDrawable(ViewAllActivity.this, R.drawable.recycler_divider)
            // );

        } catch (Exception e) {
            e.printStackTrace();
        }

        mAttendanceDbRef =
                FirebaseDatabase.getInstance().getReference().child("Employee").child("CheckIn");
        mAttendanceDbRef.keepSynced(true);


        //querying the database base of the time posted
        Query query = mAttendanceDbRef.orderByChild("timeStamp");

        FirebaseRecyclerOptions<Employee> options =
                new FirebaseRecyclerOptions.Builder<Employee>().setQuery(query, Employee.class).build();

        adapter = new FirebaseRecyclerAdapter<Employee, ViewAllActivity.ShowAttendanceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewAllActivity.ShowAttendanceViewHolder holder, int position, @NonNull Employee model) {
                holder.showUserName(model.getUserName());
                holder.showCheckInPhoto(model.getCheckInPhoto());
                holder.showCheckInDate(model.getCheckInTimeStamp());
                holder.showDutyPost(model.getDutyPost());
                holder.showWorkShift(model.getTypeOfShift());
                holder.showLocation(model.getLocation());


            }

            @NonNull
            @Override
            public ViewAllActivity.ShowAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // View view = LayoutInflater.from(viewGroup.getContext())
                return new ViewAllActivity.ShowAttendanceViewHolder(getLayoutInflater().inflate(R.layout.layout_view_check_in_details_bottom_sheet, viewGroup, false));
            }
        };

        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //set adapter to recycler
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //an inner class to hold the views to be inflated
    public class ShowAttendanceViewHolder extends RecyclerView.ViewHolder {
        View view;


        ShowAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }


        //display the user name of the person who checked in
        void showUserName(String name) {
            TextView nameOfAttendee = view.findViewById(R.id.textViewAttendeeName);
            nameOfAttendee.setText(name);
        }


        void showCheckInDate(Long date) {

            TextView txtCheckInDate = view.findViewById(R.id.txtViewAttendanceCheckIn);
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy '@' hh:mm aa", Locale.US);

            try {
                txtCheckInDate.setText(String.format("Checked in on : %s", sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //display the check in photo
        void showCheckInPhoto(String urlOfImage) {
            CircleImageView checkInPhoto = view.findViewById(R.id.imageViewCheckIn);

            Glide.with(ViewAllActivity.this).load(urlOfImage).into(checkInPhoto);
        }


        //display the check out photo
        void showCheckOutPhoto(String urlOfImage) {
            CircleImageView checkOutPhoto = view.findViewById(R.id.imageViewCheckOut);

            Glide.with(ViewAllActivity.this).load(urlOfImage).into(checkOutPhoto);
        }


        //display the duty post
        void showDutyPost(String dutyPost) {
            TextView txtDutyPost = view.findViewById(R.id.textViewDutyPost);
            txtDutyPost.setText(dutyPost);
        }

        //display the work shift
        void showWorkShift(String workShift) {
            TextView txtWorkShift = view.findViewById(R.id.textViewWorkShift);
            txtWorkShift.setText(workShift);
        }

        //display the location of the user
        void showLocation(String location) {
            TextView txtLocation = view.findViewById(R.id.txtViewAttendeeLocation);
            txtLocation.setText(location);
        }


    }

}
