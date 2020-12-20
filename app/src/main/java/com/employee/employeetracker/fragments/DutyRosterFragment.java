package com.employee.employeetracker.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.EmployeeDutyAdapter;
import com.employee.employeetracker.models.Employee;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DutyRosterFragment extends Fragment {
    private DatabaseReference usersDbRef;
    // private DutyTableAdapter adapter;
    private EmployeeDutyAdapter adapter;
    private ArrayList<Employee> arrayList;
    // private View view;
    private RecyclerView recyclerView;

    public DutyRosterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duty_roster, container, false);
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recyDuty);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //add decorator
        final DividerItemDecoration itemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(itemDecoration);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // this.view = view;


        setUpRecycler();
    }

    private void setUpRecycler() {
        arrayList = new ArrayList<>();
        adapter = new EmployeeDutyAdapter(getContext(), arrayList);

        usersDbRef =
                FirebaseDatabase.getInstance().getReference().child("Employee");
        usersDbRef.keepSynced(true);

        Query query = usersDbRef
                .orderByChild("fullName");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    arrayList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.exists() && ds.hasChildren()) {

                            Employee attendance = ds.getValue(Employee.class);
                            String name = attendance.getFullName();
                            String duty = attendance.getDutyPost();
                            String shift = attendance.getTypeOfShift();

                            arrayList.add(new Employee(name, duty, shift));

                            //arrayList.add(attendance);
                        }

                    }


                    //set adapter to recycler
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
