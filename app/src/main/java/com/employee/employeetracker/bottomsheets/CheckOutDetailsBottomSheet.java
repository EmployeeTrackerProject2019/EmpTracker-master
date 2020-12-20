package com.employee.employeetracker.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckOutDetailsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "CheckOut BottomSheet";
    private View view;
    private DatabaseReference attendanceDbRef;
    private TextView txtName, txtDutyPost, txtCheckOutTime, txtWorkShift, txtLocation;
    private CircleImageView mUserImage;
    private FragmentActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_view_check_out_details_bottom_sheet, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        Bundle bundle = getArguments();
        assert bundle != null;
        String getPositionBundle = bundle.getString("position");

        attendanceDbRef =
                FirebaseDatabase.getInstance().getReference().child("Employee").child("CheckOut").child(getPositionBundle);
        attendanceDbRef.keepSynced(true);

        initViews();
        retrieveLeaveDetails();
    }

    private void initViews() {

        mUserImage = view.findViewById(R.id.imageViewCheckOut);
        txtName = view.findViewById(R.id.textViewName);
        txtCheckOutTime = view.findViewById(R.id.txtViewChkOut);
        txtDutyPost = view.findViewById(R.id.textViewDutyPost);
        txtWorkShift = view.findViewById(R.id.textViewWorkShift);

    }

    private void retrieveLeaveDetails() {

        attendanceDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = (String) dataSnapshot.child("userName").getValue();
                    String image = (String) dataSnapshot.child("checkOutPhoto").getValue();
                    Long timeStamp = (Long) dataSnapshot.child("checkOutTimeStamp").getValue();
                    String dutyPost = (String) dataSnapshot.child("dutyPost").getValue();
                    String workShift = (String) dataSnapshot.child("typeOfShift").getValue();


                    txtName.setText(name);
                    txtWorkShift.setText(workShift);
                    txtDutyPost.setText(dutyPost);


                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy '@' hh:mm aa", Locale.US);

                    try {
                        txtCheckOutTime.setText(sfd.format(new Date(timeStamp)));
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
    public void onClick(View v) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            CheckOutDetailsListener checkOutDetailsListener = (CheckOutDetailsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement interface");
        }

    }

    public interface CheckOutDetailsListener {
        void onButtonClicked();

    }
}
