package com.employee.employeetracker.fragments;


import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.bottomsheets.CheckOutDetailsBottomSheet;
import com.employee.employeetracker.interfaces.RecyclerItemTouchHelperCheckOut;
import com.employee.employeetracker.interfaces.RecyclerItemTouchHelperCheckOut.RecyclerItemTouchHelperListenerCheckOut;
import com.employee.employeetracker.models.Attendance;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckOutFragment extends Fragment implements RecyclerItemTouchHelperListenerCheckOut {
    private static final String TAG = "CheckOutFragment";
    private DatabaseReference checkOutDbRef;
    private View view;
    private ProgressBar loading;
    private ConstraintLayout mShowEmptyLayout;
    private int childCount = 0;
    private FirebaseRecyclerAdapter<Attendance, ShowAttendanceViewHolder> adapter;
    private FragmentActivity mActivity;
    private TextView txtDescription;

    public CheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_out, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        mShowEmptyLayout = view.findViewById(R.id.showEmptyLayoutMsg);
        txtDescription = view.findViewById(R.id.txtDescription);


        setUpRecycler();


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void setUpRecycler() {
        if (mActivity == null) {
            return;
        }
        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        String getUsersId = mFirebaseUser.getUid();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewForCheckOut);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());


        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(500);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setLayoutManager(layoutManager);
        loading = view.findViewById(R.id.progressBarLoadingCheckOutDetails);

        final DividerItemDecoration itemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        checkOutDbRef =
                FirebaseDatabase.getInstance().getReference().child("Attendance").child("CheckOut").child(getUsersId);
        checkOutDbRef.keepSynced(true);

        //querying the database base of the USER who  checked out
        Query query = checkOutDbRef.orderByChild("userId").equalTo(getUsersId);

        checkEmptyDb();

        FirebaseRecyclerOptions<Attendance> options =
                new FirebaseRecyclerOptions.Builder<Attendance>().setQuery(query, Attendance.class).build();


        adapter = new FirebaseRecyclerAdapter<Attendance, ShowAttendanceViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShowAttendanceViewHolder holder, int position, @NonNull Attendance model) {

                holder.showCheckOutDate(model.getCheckOutTimeStamp());

                //get the post position using the positions in each view holder
                final String getPostPosition = getRef(position).getKey();

                holder.txtViewMore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //pass the values to the bottom sheet
                        final Bundle bundle = new Bundle();
                        bundle.putString("position", getPostPosition);
                        loading.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                loading.setVisibility(View.GONE);
                                CheckOutDetailsBottomSheet checkOutDetailsBottomSheet =
                                        new CheckOutDetailsBottomSheet();
                                checkOutDetailsBottomSheet.setArguments(bundle);
                                assert getFragmentManager() != null;
                                checkOutDetailsBottomSheet.show(getFragmentManager().beginTransaction(), "checkout");

                            }
                        }, 500);
                    }
                });

            }

            @NonNull
            @Override
            public ShowAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ShowAttendanceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_show_check_out, viewGroup, false));

            }
        };

        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        //set adapter to recycler
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperCheckOut(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 =
                new SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull ViewHolder viewHolder, int i) {

                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);

        Log.d(TAG, "setUpRecycler: completed");


    }

    private void checkEmptyDb() {

        checkOutDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {
                        childCount = (int) dataSnapshot.getChildrenCount();

                    } else if (dataSnapshot.getChildrenCount() == 0) {

                        mShowEmptyLayout.setVisibility(View.VISIBLE);
                        txtDescription.setText(getResources().getString(R.string.awww_snap_checkout));
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

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ShowAttendanceViewHolder) {

            deleteItem(viewHolder.getAdapterPosition());

        }
    }

    //Method to delete value in recycler
    private void deleteItem(int adapterPosition) {
        adapter.getSnapshots().getSnapshot(adapterPosition).getRef().removeValue();

        childCount -= 1;
        if (childCount == 0) {
            mShowEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            mShowEmptyLayout.setVisibility(View.GONE);
            txtDescription.setText(getResources().getString(R.string.awww_snap_checkout));

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
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
        RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;
        private View view;
        private TextView txtViewMore;


        ShowAttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtViewMore = view.findViewById(R.id.txtViewCheckOutDetails);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }

        void showCheckOutDate(Long date) {

            TextView txtCheckOutDate = view.findViewById(R.id.txtShowCheckOutTime);
            SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd-MMMM-yyyy '@' hh:mm aa",
                    Locale.US);

            try {
                txtCheckOutDate.setText(String.format("You checked out on \n %s",
                        sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
