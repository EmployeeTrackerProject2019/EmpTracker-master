package com.employee.employeetracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

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
    private String uid, username, userPhoto;
    private DatabaseReference mUserDbRef, mAttendance, historyDbRef;
    private static GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

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


        setUpLocation();
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

        mUserDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("fullName").getValue();
                userPhoto = (String) dataSnapshot.child("image").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                //  Toast.makeText(PostCrimeWithPhotoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loading = new ProgressDialog(this);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        spinnerDutyPost = findViewById(R.id.spinnerForDutyPost);
        spinnerWorkShift = findViewById(R.id.spinnerForShifs);
    }

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

    private synchronized void buildGoogleClient() {
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


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                            16f));
                }
            });


            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

        }
    }

    private void createLocationRequest() {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MYPERMISSIONREQUEST);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
        displayLocation();
        startLocationUpdates();

    }


    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        double lat = mLastLocation.getLatitude();
        double lng = mLastLocation.getLongitude();

        float[] results = new float[1];
        Location.distanceBetween(lat, lng, 5.596091, -0.223362, results);
        float distanceInMeters = results[0];
        boolean isWithinRange = distanceInMeters < 145;

        if (!isWithinRange) {

            btnCheckIn.setEnabled(false);
            btnCheckIn.setBackgroundColor(getResources().getColor(R.color.colorRed));
            btnCheckIn.setText(getString(R.string.cannotCheckIn));
            Log.i(TAG, "cannot check in from this location: ");
            //makeToast("cannot check in from this location ");
        } else {
            btnCheckIn.setEnabled(true);
            // makeToast("Can check in");
            Log.i(TAG, " can check in ");
        }

        Log.i(TAG, "onLocationChanged --- distance in meters: " + distanceInMeters);

        displayLocation();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setBuildingsEnabled(true);

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            uiSettings.setAllGesturesEnabled(true);


            Log.d(TAG, "onMapReady: Successful");


        //create a geo fence of the area or the boundary
        LatLng placeBoundary = new LatLng(5.596091, -0.223362);//gtuc
        mMap.addCircle(new CircleOptions()
                .center(placeBoundary)
                .radius(150) //150 meters from the center of the school
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
                // btnCheckIn.setEnabled(true);

                //sendNotis(String.format("%s You entered and can check in", key));
            }

            @Override
            public void onKeyExited(String key) {
                //cannot check in here .. disable check in button
                // btnCheckIn.setEnabled(false);
                //  makeToast("You have left school and can not check in");
                //   sendNotis(String.format("%s You have left school and can not check in", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                // sendNotis(String.format("You are within", key));
                //  Log.i("onMove : ", String.format("%s moved within the school boundary [%f / %f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("Error", error.getDetails());
            }
        });



    }

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCheckIn) {

            if (spinnerWorkShift.getSelectedItemPosition() != 0 && spinnerDutyPost.getSelectedItemPosition() != 0) {
                checkInUser();
            } else if (spinnerWorkShift.getSelectedItemPosition() == 0) {
                makeToast("please select your shift");
            } else if (spinnerDutyPost.getSelectedItemPosition() == 0) {
                makeToast("please select duty post");
            } else {
                makeToast("Must select your shift and duty post ");
            }



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
            dayOfTheWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis());


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
            checkInDetails.put("checkInPhoto", userPhoto);
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
                        Log.d(TAG, "displayLocation: " + latitude + " " + longitude);

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
