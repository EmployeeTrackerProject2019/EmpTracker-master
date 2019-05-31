package com.employee.employeetracker.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckInDetailsBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "CheckIn BottomSheet";
    private Bundle bundle;
    private String getPositionBundle;
    private View view;
    private DatabaseReference attendanceDbRef;
    private TextView txtName, txtDutyPost, txtCheckInTime, txtWorkShift, txtLocation;
    private CircleImageView mUserImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_view_check_in_details_bottom_sheet, container,
                false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        bundle = getArguments();
        assert bundle != null;
        getPositionBundle = bundle.getString("position");

        attendanceDbRef =
                FirebaseDatabase.getInstance().getReference().child("Attendance").child("CheckIn").child(getPositionBundle);
        attendanceDbRef.keepSynced(true);

        initViews();
        retrieveLeaveDetails();
    }

    private void initViews() {

        mUserImage = view.findViewById(R.id.imageViewCheckIn);
        txtName = view.findViewById(R.id.textViewAttendeeName);
        txtCheckInTime = view.findViewById(R.id.txtViewAttendanceCheckIn);
        txtDutyPost = view.findViewById(R.id.textViewDutyPost);
        txtWorkShift = view.findViewById(R.id.textViewWorkShift);

    }


    private void retrieveLeaveDetails() {

        attendanceDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = (String) dataSnapshot.child("userName").getValue();
                    String image = (String) dataSnapshot.child("checkInPhoto").getValue();
                    Long timeStamp = (Long) dataSnapshot.child("timeStamp").getValue();
                    String dutyPost = (String) dataSnapshot.child("dutyPost").getValue();
                    String workShift = (String) dataSnapshot.child("typeOfShift").getValue();


                    txtName.setText(name);
                    txtDutyPost.setText(dutyPost);
                    txtWorkShift.setText(workShift);

                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy '@' hh:mm aa", Locale.US);

                    try {
                        txtCheckInTime.setText(sfd.format(new Date(timeStamp)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Glide.with(view).load(image).into(mUserImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                Toast.makeText(getActivity(), databaseError.getDetails(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            CheckInDetailsListener checkInDetailsListener = (CheckInDetailsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement interface");
        }

    }

    public interface CheckInDetailsListener {
        void onButtonClicked();

    }
}
