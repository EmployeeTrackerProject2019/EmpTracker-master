package com.employee.employeetracker.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.MainActivity;
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
    // private View view;
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recyclerViewForReport);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView.setItemAnimator(itemAnimator);

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

        final FragmentManager fm = getFragmentManager();
        assert fm != null;
        fm.beginTransaction().commit();
        view.findViewById(R.id.to_makeReport).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - MainActivity.mLastClickTime < 1000) {
                    return;
                }

                MainActivity.mLastClickTime = SystemClock.elapsedRealtime();
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });


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
        if (adapter != null) {
            adapter.startListening();
        }

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
