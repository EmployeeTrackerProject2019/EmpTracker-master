package com.employee.employeetracker.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.employee.employeetracker.R;
import com.employee.employeetracker.app.BaseActivity;
import com.employee.employeetracker.bottomsheets.CheckInDetailsBottomSheet.CheckInDetailsListener;
import com.employee.employeetracker.bottomsheets.CheckOutDetailsBottomSheet.CheckOutDetailsListener;
import com.employee.employeetracker.bottomsheets.MakeAReportBottomSheet.MakeReportListener;
import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet.PhoneNumberBottomSheetListener;
import com.employee.employeetracker.bottomsheets.RequestALeaveBottomSheet.RequestLeaveListener;
import com.employee.employeetracker.fragments.DutyRosterFragment;
import com.employee.employeetracker.fragments.EditProfileFragment;
import com.employee.employeetracker.fragments.EmployeeCheckInFragment;
import com.employee.employeetracker.fragments.LeaveFragment;
import com.employee.employeetracker.fragments.ReportFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("ALL")
public class MainActivity extends BaseActivity implements MakeReportListener,
        RequestLeaveListener, CheckInDetailsListener, CheckOutDetailsListener,
        PhoneNumberBottomSheetListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static GoogleApiClient mGoogleApiClient;
    public static int INTERVAL = 3000;
    public static long mLastClickTime = 0;

    // fragments here
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final Fragment dutyRosterFragment = new DutyRosterFragment();
    private final Fragment attendanceFragment = new EmployeeCheckInFragment();
    private final Fragment reportFragment = new ReportFragment();
    private final Fragment leaveFragment = new LeaveFragment();
    private final Fragment profileFragment = new EditProfileFragment();
    //database
    private DatabaseReference reportDbRef;
    private DatabaseReference leaveDbRef;
    private DatabaseReference usersDbRef;
    //Strings
    private String getPhoneNumber, getReportTitle, getReportDescription;
    private String userId, userName, getImageUri, fullName;
    private String notApproved = "Not yet Approved";
    private String getLeaveMsg, getStartDate, getEndDate;
    //auths
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fire base
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        assert mFirebaseUser != null;
        userId = mFirebaseUser.getUid();

        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(userId);
        usersDbRef.keepSynced(true);
        //Reports database
        reportDbRef = FirebaseDatabase.getInstance().getReference("Reports");
        reportDbRef.keepSynced(true);
        //Leave database
        leaveDbRef = FirebaseDatabase.getInstance().getReference("Leaves");
        leaveDbRef.keepSynced(true);

        float[] results = new float[1];
        Location.distanceBetween(5.594759, -0.223371, 5.596091, -0.223362, results);
        float distanceInMeters = results[0];
        boolean isWithinRange = distanceInMeters < 145;




        retrieveDetails();
        //  initGoogleAPIClient();//Init Google API Client
        // checkPermissions();//Check Permission


        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.action_dutyPost:
                        fragment = dutyRosterFragment;
                        break;
                    case R.id.action_attendance:
                        fragment = attendanceFragment;
                        break;
                    case R.id.action_leaves:
                        fragment = leaveFragment;
                        break;

                    case R.id.action_reports:
                        fragment = reportFragment;
                        break;
                    case R.id.action_profile:
                        fragment = profileFragment;
                        break;

                }
                assert fragment != null;

                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right,
                                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.fragmentContainer, fragment)
                        //.addToBackStack(null)
                        .commit();
                return true;


            }


        });

        mBottomNavigationView.setSelectedItemId(R.id.action_attendance);

    }



    private void retrieveDetails() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                //update users time stamp per log in
                Map<String, Object> lastSeen = new HashMap<>();
                lastSeen.put("lastSeen", ServerValue.TIMESTAMP);
                usersDbRef.updateChildren(lastSeen);


                usersDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userName = (String) dataSnapshot.child("firstName").getValue();
                        fullName = (String) dataSnapshot.child("fullName").getValue();
                        getImageUri = (String) dataSnapshot.child("image").getValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }



    @Override
    public void onButtonClicked(String title, String description) {

        getReportTitle = title;
        getReportDescription = description;
        sendReportToDatabase();

    }

    private void sendReportToDatabase() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> report = new HashMap<>();
                report.put("userId", userId);
                report.put("fullName", fullName);
                report.put("image", getImageUri);
                report.put("timeStamp", ServerValue.TIMESTAMP);
                report.put("title", getReportTitle);
                report.put("description", getReportDescription);


                String reportID = reportDbRef.push().getKey();
                assert reportID != null;

                reportDbRef.child(reportID).setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(MainActivity.this, "Report successful ",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();

                    }
                });

            }
        });


    }

    @Override
    public void onButtonClicked(String leaveMessage, String startDate, String endDate) {
        getLeaveMsg = leaveMessage;
        getStartDate = startDate;
        getEndDate = endDate;

        sendLeaveReport();

    }

    private void sendLeaveReport() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                Map<String, Object> leave = new HashMap<>();
                leave.put("userId", userId);
                leave.put("fullName", fullName);
                leave.put("image", getImageUri);
                leave.put("timeStamp", ServerValue.TIMESTAMP);
                leave.put("leaveMsg", getLeaveMsg);
                leave.put("startDate", getStartDate);
                leave.put("endDate", getEndDate);
                leave.put("leaveResponse", notApproved);


                String leaveID = leaveDbRef.push().getKey();
                assert leaveID != null;

                leaveDbRef.child(leaveID).setValue(leave).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(MainActivity.this, "Leave successfully requested ",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();

                    }
                });
            }
        });


    }

    @Override
    public void onButtonClicked() {

    }

    @Override
    public void onButtonClicked(String text) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (mAuth.getCurrentUser() == null) {
                returnToWelcomePage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //method to return to welcome screen if user id is null
    private void returnToWelcomePage() {
        startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
//        if (gpsLocationReceiver != null)
//          unregisterReceiver(gpsLocationReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}








