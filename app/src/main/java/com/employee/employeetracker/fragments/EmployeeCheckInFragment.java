package com.employee.employeetracker.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.employee.employeetracker.MapsActivity2;
import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import jahirfiquitiva.libs.fabsmenu.TitleFAB;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeCheckInFragment extends Fragment implements View.OnClickListener {
    private View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    private TitleFAB gotoCheckIn;

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


    }

    private void initView() {
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        final SectionsPagerAdapter sectionPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        sectionPagerAdapter.addFragment(new MondayFragment(), "Monday");
        sectionPagerAdapter.addFragment(new TuesdayFragment(), "Tuesday");
        sectionPagerAdapter.addFragment(new WednesdayFragment(), "Wednesday");
        sectionPagerAdapter.addFragment(new ThursdayFragment(), "Thursday");
        sectionPagerAdapter.addFragment(new FridayFragment(), "Friday");
        sectionPagerAdapter.addFragment(new SaturdayFragment(), "Saturday");
        sectionPagerAdapter.addFragment(new SundayFragment(), "Sunday");

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(sectionPagerAdapter);


        //viewPager.getCurrentItem();

        // viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        //viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        gotoCheckIn = view.findViewById(R.id.to_checkIn);
        //  gotoCheckOut = view.findViewById(R.id.to_checkOut);
        //tstChkIn = view.findViewById(R.id.to_TESTcHK);


    }

    private void initListener() {
        gotoCheckIn.setOnClickListener(this);
        //gotoCheckOut.setOnClickListener(this);
        // tstChkIn.setOnClickListener(this);

    }


    public void onClick(View v) {

        if (v.getId() == R.id.to_checkIn) {
            startActivity(new Intent(getContext(), MapsActivity2.class));


            //            case R.id.to_checkOut:
//                startActivity(new Intent(getContext(), CheckInAndOutActivity.class));
//                break;

//            case R.id.to_TESTcHK:
//                startActivity(new Intent(getContext(), CheckInActivity.class));
//                break;
        }

    }


}
