package com.employee.employeetracker.fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.MainActivity;
import com.employee.employeetracker.adapters.LeaveAdapterViewHolder;
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
    RecyclerView recyclerView;
    private ConstraintLayout mShowEmptyLayout;
    private TextView txtDescription;
    //  private FirebaseRecyclerAdapter<Leave, ShowLeaveViewHolder> adapter;
    //private View view;
    private int childCount = 0;
    Query query;

    public LeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewLeave);
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

        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // this.view = view;
        mShowEmptyLayout = view.findViewById(R.id.showEmptyLayoutMsg);
        txtDescription = view.findViewById(R.id.txtDescription);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        //A unique id that will differentiate each attendance made
        String getUsersId = mUser.getUid();

        //querying the database base of the time posted
        leaveDb = FirebaseDatabase.getInstance().getReference().child("Leaves");
        leaveDb.keepSynced(true);
        query = leaveDb.orderByChild("userId").equalTo(getUsersId);


        view.findViewById(R.id.to_requestLeave).setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - MainActivity.mLastClickTime < 1000) {
                return;
            }

            MainActivity.mLastClickTime = SystemClock.elapsedRealtime();

            RequestALeaveBottomSheet requestALeaveBottomSheet = new RequestALeaveBottomSheet();
            assert getFragmentManager() != null;
            requestALeaveBottomSheet.show(getFragmentManager(), "requestLeave");


        });

        setUpRecycler();
    }

    private void setUpRecycler() {


        checkEmptyDb();

        FirebaseRecyclerOptions<Leave> options =
                new FirebaseRecyclerOptions.Builder<Leave>().setQuery(query, Leave.class).build();

        leaveAdapterViewHolder = new LeaveAdapterViewHolder(options);

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
        if (leaveAdapterViewHolder != null) {
            leaveAdapterViewHolder.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        leaveAdapterViewHolder.stopListening();
    }
}
