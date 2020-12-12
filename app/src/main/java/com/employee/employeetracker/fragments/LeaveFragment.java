package com.employee.employeetracker.fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.MainActivity;
import com.employee.employeetracker.adapters.LeaveAdapterViewHolder;
import com.employee.employeetracker.bottomsheets.MakeAReportBottomSheet;
import com.employee.employeetracker.bottomsheets.RequestALeaveBottomSheet;
import com.employee.employeetracker.models.Leave;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveFragment extends Fragment {
    private static final String TAG = "LeaveFragment";
    private DatabaseReference leaveDb;
    private LeaveAdapterViewHolder leaveAdapterViewHolder;
    //  private FirebaseRecyclerAdapter<Leave, ShowLeaveViewHolder> adapter;
    private View view;
    private int childCount = 0;
    private ConstraintLayout mShowEmptyLayout;
    private TextView txtDescription;


    public LeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leave, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mShowEmptyLayout = view.findViewById(R.id.showEmptyLayoutMsg);
        txtDescription = view.findViewById(R.id.txtDescription);

        view.findViewById(R.id.to_requestLeave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - MainActivity.mLastClickTime < 1000) {
                    return;
                }

                MainActivity.mLastClickTime = SystemClock.elapsedRealtime();

                RequestALeaveBottomSheet requestALeaveBottomSheet = new RequestALeaveBottomSheet();
                assert getFragmentManager() != null;
                requestALeaveBottomSheet.show(getFragmentManager(), "requestLeave");


            }
        });

        setUpRecycler();
    }

    private void setUpRecycler() {
        Log.d(TAG, "setUpRecycler: completed");
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLeave);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        leaveDb = FirebaseDatabase.getInstance().getReference().child("Leaves");
        leaveDb.keepSynced(true);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        //A unique id that will differentiate each attendance made
        String getUsersId = mUser.getUid();

        //querying the database base of the time posted
        Query query = leaveDb.orderByChild("userId").equalTo(getUsersId);

        checkEmptyDb();

        FirebaseRecyclerOptions<Leave> options =
                new FirebaseRecyclerOptions.Builder<Leave>().setQuery(query, Leave.class).build();

        leaveAdapterViewHolder = new LeaveAdapterViewHolder(options);

        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //set adapter to recycler
        recyclerView.setAdapter(leaveAdapterViewHolder);
        leaveAdapterViewHolder.notifyDataSetChanged();


    }


    private void checkEmptyDb() {

        leaveDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        childCount = (int) dataSnapshot.getChildrenCount();

                    } else if (dataSnapshot.getChildrenCount() == 0) {

                        mShowEmptyLayout.setVisibility(View.VISIBLE);
                        txtDescription.setText(getResources().getString(R.string.awww_snap_send_leave));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());

            }

        });

    }


    @Override
    public void onStart() {
        super.onStart();
        leaveAdapterViewHolder.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        leaveAdapterViewHolder.stopListening();
    }
}
