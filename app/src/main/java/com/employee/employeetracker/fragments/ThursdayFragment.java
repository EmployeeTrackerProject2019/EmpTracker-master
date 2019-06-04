package com.employee.employeetracker.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.ShowAttendanceRecyclerAdapter;
import com.employee.employeetracker.models.Employee;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThursdayFragment extends Fragment {
    private static final String TAG = "ThursdayFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference checkedInDb;
    private ShowAttendanceRecyclerAdapter adapter;
    private View view;
    private String uid;
    private FragmentActivity mActivity;

    public ThursdayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thursday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        checkedInDb =
                FirebaseDatabase.getInstance().getReference().child("Attendance").child("Thursday");
        checkedInDb.keepSynced(true);


        setUpRecycler();
    }

    private void setUpRecycler() {
        if (mActivity == null) {
            return;
        }


        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewThursday);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(300);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);

        final DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);

        String dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

        //querying the database base of the time posted
        Query query = checkedInDb.orderByChild("userId").equalTo(uid);

        FirebaseRecyclerOptions<Employee> options =
                new FirebaseRecyclerOptions.Builder<Employee>().setQuery(query,
                        Employee.class)
                        .setLifecycleOwner(this)
                        .build();
        adapter = new ShowAttendanceRecyclerAdapter(options);
/*
 adapter = new FirebaseRecyclerAdapter<Employee, ShowAttendanceAdapter>(options) {
@NonNull
@Override
public ShowAttendanceAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
return new ShowAttendanceAdapter(getLayoutInflater()
.inflate(R.layout.layout_view_employee_check_in, viewGroup, false));
}

@Override
protected void onBindViewHolder(@NonNull ShowAttendanceAdapter holder, int position,
@NonNull Employee model) {

holder.showCheckInEmployeeName(model.getUserName());
holder.showCheckInDate(model.getCheckInTimeStamp());

//get the post position using the positions in each view holder
final String getPostPosition = getRef(position).getKey();


}

};
 */
        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


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

}
