package com.employee.employeetracker.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.ReportViewHolderAdapter;
import com.employee.employeetracker.bottomsheets.MakeAReportBottomSheet;
import com.employee.employeetracker.models.Report;
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
public class ReportFragment extends Fragment {
    private static final String TAG = "ReportFragment";
    private DatabaseReference reportDb;
    private View view;
    private ReportViewHolderAdapter adapter;
    private RecyclerView recyclerView;
    private int childCount = 0;
    private ConstraintLayout mShowEmptyLayout;
    private TextView txtDescription;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mShowEmptyLayout = view.findViewById(R.id.showEmptyLayoutMsg);
        txtDescription = view.findViewById(R.id.txtDescription);

        final FragmentManager fm = getFragmentManager();
        assert fm != null;
        fm.beginTransaction().commit();
        view.findViewById(R.id.to_makeReport).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                MakeAReportBottomSheet makeAReportBottomSheet = new MakeAReportBottomSheet();
                makeAReportBottomSheet.show(fm, "makeReport");

            }
        });

        setUpRecycler();

    }

    private void checkEmptyDb() {

        reportDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        childCount = (int) dataSnapshot.getChildrenCount();

                    } else if (dataSnapshot.getChildrenCount() == 0) {

                        mShowEmptyLayout.setVisibility(View.VISIBLE);
                        txtDescription.setText(getResources().getString(R.string.awww_snap_make_report));
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


    private void setUpRecycler() {
        Log.d(TAG, "setUpRecycler: completed");
        recyclerView = view.findViewById(R.id.recyclerViewForReport);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);


        reportDb = FirebaseDatabase.getInstance().getReference().child("Reports");
        reportDb.keepSynced(true);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        //A unique id that will differentiate each attendance made
        String getUsersId = mUser.getUid();

        //querying the database base of the time posted
        Query query = reportDb.orderByChild("userId").equalTo(getUsersId);

        checkEmptyDb();

        FirebaseRecyclerOptions<Report> options =
                new FirebaseRecyclerOptions.Builder<Report>().setQuery(query, Report.class).build();

        adapter = new ReportViewHolderAdapter(options);

        swipeToDelete();

        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //set adapter to recycler
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void swipeToDelete() {
        new ItemTouchHelper(new SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull ViewHolder viewHolder, int i) {
                delete(viewHolder.getAdapterPosition());
            }

            //delete items
            void delete(int position) {
                adapter.getSnapshots().getSnapshot(position).getRef().removeValue();


            }
        }).attachToRecyclerView(recyclerView);
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
