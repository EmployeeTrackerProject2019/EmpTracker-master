package com.employee.employeetracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.employee.employeetracker.activities.MainActivity;
import com.employee.employeetracker.utils.GetDateTime;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    public static final int MYPERMISSIONREQUEST = 100;
    private static final String TAG = "MapsActivity2";
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleMap mMap;
    private TextView txtLat, txtLong;
    private DatabaseReference reference;
    private GeoFire geoFire;
    private Marker marker;
    public static final int PLAYSERVICES_RESOLUTION = 101;
    private Location mLastLocation;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FAST_INTERVAL = 3000;
    public static final int DISPLACEMENT = 10;
    private Spinner spinnerDutyPost, spinnerWorkShift;
    private Button btnCheckIn;
    private double latitude, longitude;


    private String datePosted = "", dayOfTheWeek;
    private ProgressDialog loading;
    private String uid, username;
    private DatabaseReference mUserDbRef, mAttendance, historyDbRef;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static GoogleApiClient mGoogleApiClient;
    //  private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
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
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        reference = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(reference);

        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission

        //setUpLocation();
        initViews();
        initListeners();

    }

    private void initListeners() {
        btnCheckIn.setOnClickListener(this);

    }

    private void initViews() {

        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();


        //attendance
        mAttendance = FirebaseDatabase.getInstance().getReference("Attendance");


        //create the history database

        String log = "History";
        historyDbRef = FirebaseDatabase.getInstance().getReference(log);
//        historyDbRef.keepSynced(true);

        mUserDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);
        mUserDbRef.keepSynced(true);


        btnCheckIn = findViewById(R.id.btnCheckIn);
        spinnerDutyPost = findViewById(R.id.spinnerForDutyPost);
        spinnerWorkShift = findViewById(R.id.spinnerForShifs);
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity2.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
                            status.startResolutionForResult(MapsActivity2.this, REQUEST_CHECK_SETTINGS);
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

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MapsActivity2.this,
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity2.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MapsActivity2.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(MapsActivity2.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /*
       private void setUpLocation() {
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);


           } else {
               if (checkPlayServices()) {
                   buildGoogleClient();
                   createLocationRequest();
                   displayLocation();
               }
           }

       }


           private void displayLocation() {
               if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                   ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

                   return;
               }
               mLastLocation = LocationServices.FusedLocationApi
                       .getLastLocation(googleApiClient);
               if (mLastLocation != null) {
                   latitude = mLastLocation.getLatitude();
                   longitude = mLastLocation.getLongitude();

                   //update to fire base
                   geoFire.setLocation("You", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                       @Override
                       public void onComplete(String key, DatabaseError error) {
                           //add marker
                           if (marker != null) marker.remove();

                           marker = mMap.addMarker(new MarkerOptions()
                                   .position(new LatLng(latitude, longitude))
                                   .title("You are here"));
       //move camera to position
                           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
                       }
                   });


                   Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

               } else {
                   makeToast("Can not get you location");
               }
           }

           private void createLocationRequest() {

               locationRequest = new LocationRequest();
               locationRequest.setInterval(UPDATE_INTERVAL);
               locationRequest.setFastestInterval(FAST_INTERVAL);
               locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
               locationRequest.setSmallestDisplacement(DISPLACEMENT);

           }

           private void buildGoogleClient() {
               //build the google client
               googleApiClient = new GoogleApiClient.Builder(this)
                       .addApi(LocationServices.API)
                       .addConnectionCallbacks(this)
                       .addOnConnectionFailedListener(this)
                       .build();
               googleApiClient.connect();
           }

           private boolean checkPlayServices() {
               int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
               if (resultCode != ConnectionResult.SUCCESS) {
                   if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                       GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAYSERVICES_RESOLUTION).show();
                   else {
                       makeToast("This device is not supported");
                       finish();
                   }
                   return false;

               }
               return true;
           }
       */

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            //update to fire base
            geoFire.setLocation("You", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //add marker
                    if (marker != null) marker.remove();

                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title("You are here"));
                    //move camera to position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
                }
            });


            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

        } else {
            makeToast("Can not get you location");
        }
    }


    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestLocationPermission();
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        //create a geo fence of the area or the boundary
        LatLng placeBoundary = new LatLng(5.583481, -0.237052);//press by
        mMap.addCircle(new CircleOptions()
                .center(placeBoundary)
                .radius(100)
                .strokeColor(Color.GREEN)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );

//        add geo query here
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(placeBoundary.latitude, placeBoundary.longitude), 3.0f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
//can check in here .. enable the check in button
                btnCheckIn.setEnabled(true);

                sendNotis(String.format("%s You entered and can check in", key));
            }

            @Override
            public void onKeyExited(String key) {
                //cannot check in here .. disable check in button
                btnCheckIn.setEnabled(false);
                makeToast("You have left school and can not check in");
                sendNotis(String.format("%s You have left school and can not check in", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //sendNotis(String.format("You are within", key));
                Log.i("onMove : ", String.format("%s moved within the school boundary [%f / %f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("Error", error.getDetails());
            }
        });

        initGoogleAPIClient();

    }

    private void sendNotis(String content) {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("MyLocation")
                .setContentText(content);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MapsActivity2.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentPendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        manager.notify(new Random().nextInt(), notification);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        //requestLocationUpdates();
        displayLocation();
        //  startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

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
                    Toast.makeText(MapsActivity2.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }


    /*
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == MYPERMISSIONREQUEST) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }


        }
    */
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCheckIn) {


            checkInUser();

            //  makeToast("You can not check in from this location");


        }
    }

    private void checkInUser() {

        if (!btnCheckIn.isEnabled()) {
            makeToast("You can not check i n from this location");
        } else {

            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
            datePosted = GetDateTime.getFormattedDate(today);
            dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());


            final String getTypeOfShiftSelected = spinnerWorkShift.getSelectedItem().toString();
            final String getTypeOfDutyPostSelected = spinnerDutyPost.getSelectedItem().toString();

            loading.setMessage("Checking in on " + getTypeOfShiftSelected + " at " + getTypeOfDutyPostSelected);
            loading.setCancelable(false);
            loading.show();

//The constructor can equally be used as this
            Map<String, Object> checkInDetails = new HashMap<>();
            checkInDetails.put("userId", uid);
            checkInDetails.put("userName", username);
            checkInDetails.put("date", datePosted);
            checkInDetails.put("dayOfWeek", dayOfTheWeek);
            checkInDetails.put("checkOutTimeStamp", "");
            checkInDetails.put("latitude", latitude);
            checkInDetails.put("longitude", longitude);
            checkInDetails.put("dutyPost", getTypeOfDutyPostSelected);
            checkInDetails.put("typeOfShift", getTypeOfShiftSelected);


//Add to user profile and update profile
            final Map<String, Object> addDetailsToProfile = new HashMap<>();
            addDetailsToProfile.put("dutyPost", getTypeOfDutyPostSelected);
            addDetailsToProfile.put("typeOfShift", getTypeOfShiftSelected);
            addDetailsToProfile.put("timeStamp", ServerValue.TIMESTAMP);
//keep history
            String historyBuilder;
            historyBuilder =
                    username + " " + "checked in on" + " " + datePosted;

            //add to history database
            final Map<String, Object> history = new HashMap<>();
            history.put("history", historyBuilder);
            final String historyID = historyDbRef.push().getKey();

//Post details to check in database
            final String attendanceKey = mAttendance.push().getKey();
            //  mAttendance.child(dayOfTheWeek).child(uid).setValue(checkInDetails)
            assert attendanceKey != null;
            mAttendance.child(dayOfTheWeek).child(attendanceKey).setValue(checkInDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

//set values to database and update the employee account with the details
                        mUserDbRef.updateChildren(addDetailsToProfile);


                        //insert data into history database
                        assert historyID != null;
                        historyDbRef.child(historyID).setValue(history);
                        Log.d(TAG, "onComplete: " + historyDbRef);

                        loading.dismiss();
                        makeToast("Successfully posted");
                        startActivity(new Intent(MapsActivity2.this,
                                MainActivity.class));
                        finish();
                    } else if (!task.isSuccessful()) {
                        loading.dismiss();
                        makeToast(task.getException().getMessage());
                    }
                }
            });
        }

    }
}
