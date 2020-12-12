package com.employee.employeetracker.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.employee.employeetracker.R;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
public class MainActivity extends AppCompatActivity implements MakeReportListener,
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


    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };
    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
//                    updateGPSStatus("GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    showSettingDialog();
//                    updateGPSStatus("GPS is Disabled in your device");
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

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

        if (!isWithinRange) {

            Log.i(TAG, "cannot check in from this location: ");
            //makeToast("cannot check in from this location ");
        } else {
            // makeToast("Can check in");
            Log.i(TAG, " can check in ");
        }

        Log.i(TAG, "onLocationChanged --- distance in meters: " + distanceInMeters);


        retrieveDetails();
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission

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

    private void showWarningDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                builder.setTitle("Warning")
                        .setMessage("Make sure you are connected to your organisations\'s WiFi before you check in or check out.\nIf not please go back and connect")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();


            }
        }, 3000);


        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }


    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
//                        updateGPSStatus("GPS is Enabled in your device");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.e("Settings", "Result OK");
//                        updateGPSStatus("GPS is Enabled in your device");
                    //startLocationUpdates();
                    break;
                case RESULT_CANCELED:
                    Log.e("Settings", "Result Cancel");
//                        updateGPSStatus("GPS is Disabled in your device");
                    break;
            }
        }
    }


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
//                    updateGPSStatus("Location Permission denied.");
                    Toast.makeText(MainActivity.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register
// broadcast receiver to check the status of GPS
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








