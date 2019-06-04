package com.employee.employeetracker.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.employee.employeetracker.activities.CheckInActivity;
import com.employee.employeetracker.activities.CheckOutActivity;
import com.employee.employeetracker.bottomsheets.CheckInDetailsBottomSheet;
import com.employee.employeetracker.interfaces.RecyclerItemTouchHelperCheckIn;
import com.employee.employeetracker.interfaces.RecyclerItemTouchHelperCheckIn.RecyclerItemTouchHelperListenerCheckIn;
import com.employee.employeetracker.models.Employee;
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

import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class CheckInFragment extends Fragment implements RecyclerItemTouchHelperListenerCheckIn {
    private static final String TAG = "Employee ";
    private String getUsersId;
    private DatabaseReference mAttendanceDbRef;
    private TextView txtCount;
    private View view;
    private FirebaseRecyclerAdapter<Employee, ShowAttendanceViewHolder> adapter;
    private ProgressBar loading;
    private ConstraintLayout mShowEmptyLayout;
    private int childCount = 0;
    private RecyclerView recyclerView;
    private FragmentActivity mActivity;
    private TextView txtDescription;

    public CheckInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.check_in_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initViews();

        setUpRecycler();


    }


    private void initViews() {
        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        getUsersId = mFirebaseUser.getUid();


        //views for the floating action menu
        FABsMenu menu = view.findViewById(R.id.fabs_menu);
        TitleFAB checkInTitleFAB = view.findViewById(R.id.to_checkIn); //check in
        TitleFAB checkOutTitleFAB = view.findViewById(R.id.to_checkOut); //check out
        TitleFAB viewCheckOutTitleFAB = view.findViewById(R.id.to_viewCheckOut); //view attendance
        menu = view.findViewById(R.id.fabs_menu);
        loading = view.findViewById(R.id.progressBarLoadingCheckInDetails);
        mShowEmptyLayout = view.findViewById(R.id.showEmptyLayoutMsg);
        txtDescription = view.findViewById(R.id.txtDescription);
        txtCount = view.findViewById(R.id.txtCount);

        mAttendanceDbRef =
                FirebaseDatabase.getInstance().getReference().child("CheckIn").child(getUsersId);
        mAttendanceDbRef.keepSynced(true);


        checkInTitleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CheckInActivity.class));
            }
        });

        checkOutTitleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CheckOutActivity.class));
            }
        });

        viewCheckOutTitleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CheckOutFragment();
                FragmentManager fm = getFragmentManager();
                assert fm != null;
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                ft.replace(R.id.fragmentContainer, fragment).commit();

                //startActivity(new Intent(getActivity(), ViewAllActivity.class));
            }
        });


        menu.setMenuUpdateListener(new FABsMenuListener() {
            // You don't need to override all methods. Just the ones you want.

            @Override
            public void onMenuClicked(FABsMenu fabsMenu) {
                super.onMenuClicked(fabsMenu); // Default implementation opens the menu on click
            }

            @Override
            public void onMenuCollapsed(FABsMenu fabsMenu) {
                super.onMenuCollapsed(fabsMenu);

            }

            @Override
            public void onMenuExpanded(FABsMenu fabsMenu) {
                super.onMenuExpanded(fabsMenu);


            }
        });
    }

    private void checkEmptyDb() {

        mAttendanceDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren() && dataSnapshot.exists()) {
                        childCount = (int) dataSnapshot.getChildrenCount();
                        mShowEmptyLayout.setVisibility(View.INVISIBLE);
                        txtCount.setText(String.format(getString(R.string.numCheckIn), childCount));
                    } else if (dataSnapshot.getChildrenCount() == 0) {

                        txtCount.setText(getString(R.string.nothingPosted));
                        txtDescription.setText(getResources().getString(R.string.awww_snap_checkin));
                        mShowEmptyLayout.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
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
        if (mActivity == null) {
            return;
        }


        recyclerView = view.findViewById(R.id.recyclerViewForCheckIn);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setLayoutManager(layoutManager);


        //querying the database base of the time posted
        Query query = mAttendanceDbRef.orderByChild("userId").equalTo(getUsersId);

        checkEmptyDb();


        FirebaseRecyclerOptions<Employee> options =
                new FirebaseRecyclerOptions.Builder<Employee>().setQuery(query, Employee.class).build();

        adapter = new FirebaseRecyclerAdapter<Employee, ShowAttendanceViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ShowAttendanceViewHolder holder, int position, @NonNull Employee model) {

                // holder.showCheckInDate(model.getCheckInTimeStamp());
                // holder.showCheckInPhoto(model.getCheckInPhoto());
                //  holder.showDutyPost(model.getDutyPost());
                // holder.showUserName(model.getUserName());
                // holder.showWorkShift(model.getTypeOfShift());

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
                                CheckInDetailsBottomSheet checkInDetailsBottomSheet = new CheckInDetailsBottomSheet();
                                checkInDetailsBottomSheet.setArguments(bundle);
                                assert getFragmentManager() != null;
                                checkInDetailsBottomSheet.show(getFragmentManager(), "checkin");

                            }
                        }, 500);
                    }
                });


            }

            @NonNull
            @Override
            public ShowAttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ShowAttendanceViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_show_check_in, viewGroup
                        , false));
            }


        };

        //set adapter to recycler
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        swipeLeftToDelete();


        Log.d(TAG, "setUpRecycler: completed");
    }

    private void swipeLeftToDelete() {
        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelperCheckIn(0, ItemTouchHelper.LEFT, this);
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

    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CheckInFragment.ShowAttendanceViewHolder) {

            deleteItem(viewHolder.getAdapterPosition());

        }
    }

    //Method to delete value in recycler
    private void deleteItem(int adapterPosition) {
        adapter.getSnapshots().getSnapshot(adapterPosition).getRef().removeValue();
        childCount -= 1;
        txtCount.setText("Number of check  ins " + childCount);
        if (childCount == 0) {
            mShowEmptyLayout.setVisibility(View.VISIBLE);
            txtDescription.setText(getResources().getString(R.string.awww_snap_checkin));
        } else {
            mShowEmptyLayout.setVisibility(View.GONE);
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
        try {

            adapter.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            txtViewMore = view.findViewById(R.id.txtViewCheckInDetails);
            viewBackground = view.findViewById(R.id.viewBackground);
            viewForeground = view.findViewById(R.id.viewForeground);
        }


        void showCheckInDate(Long date) {

            TextView txtCheckInDate = view.findViewById(R.id.txtShowCheckInTime);
            SimpleDateFormat sfd = new SimpleDateFormat("EEEE ' ' dd-MMMM-yyyy '@'" +
                    " hh:mm aa", Locale.US);

            try {
                txtCheckInDate.setText(String.format("You checked in on \n %s",
                        sfd.format(new Date(date))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


}
