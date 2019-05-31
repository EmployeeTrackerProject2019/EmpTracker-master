package com.employee.employeetracker.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.SectionsPagerAdapter;

import jahirfiquitiva.libs.fabsmenu.TitleFAB;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeCheckInFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TitleFAB gotoCheckout, gotoHistory, toViewCheckIn, toAssignDuty;

    public EmployeeCheckInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_check_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initView();
        initListener();

        setUpRecycler();
    }

    private void initView() {
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        final SectionsPagerAdapter sectionPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        sectionPagerAdapter.addFragment(new MondayFragment(), "Monday");
        sectionPagerAdapter.addFragment(new TuesdayFragment(), "Tuesday");
        sectionPagerAdapter.addFragment(new WednesdayFragment(), "Wednesday");
        sectionPagerAdapter.addFragment(new ThursdayFragment(), "Thursday");
        sectionPagerAdapter.addFragment(new FridayFragment(), "Friday");
        sectionPagerAdapter.addFragment(new SaturdayFragment(), "Saturday");
        sectionPagerAdapter.addFragment(new SundayFragment(), "Sunday");


        viewPager.setAdapter(sectionPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        gotoCheckout = view.findViewById(R.id.to_viewCheckOut);
        gotoHistory = view.findViewById(R.id.to_viewHistory);
        toViewCheckIn = view.findViewById(R.id.to_viewCheckIn);
        toAssignDuty = view.findViewById(R.id.to_assignDuty);


    }

    private void initListener() {
        gotoCheckout.setOnClickListener(this);
        gotoHistory.setOnClickListener(this);
        toViewCheckIn.setOnClickListener(this);
        toAssignDuty.setOnClickListener(this);
    }

    private void setUpRecycler() {
    }

    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {

            case R.id.to_viewCheckOut:
                Toast.makeText(getActivity(), "checked out", Toast.LENGTH_SHORT).show();
                fragment = new CheckOutFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();

                break;

            case R.id.to_viewHistory:
                Toast.makeText(getActivity(), "history", Toast.LENGTH_SHORT).show();
                // fragment = new HistoryFragment();
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();


                break;

            case R.id.to_viewCheckIn:
                // startActivity(new Intent(getContext(), Main2Activity.class));
                break;

            case R.id.to_assignDuty:
                Toast.makeText(getContext(), "Assign duty selected", Toast.LENGTH_LONG).show();

                break;


        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
