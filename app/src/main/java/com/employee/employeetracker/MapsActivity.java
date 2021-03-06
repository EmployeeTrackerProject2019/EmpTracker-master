package com.employee.employeetracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.activities.MainActivity;
import com.employee.employeetracker.utils.GetDateTime;
import com.firebase.geofire.GeoFire;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    public static final int MYPERMISSIONREQUEST = 100;
    public static final int PLAYSERVICES_RESOLUTION = 101;
    public static final int UPDATE_INTERVAL = 1000;
    public static final int FAST_INTERVAL = 1000;
    public static final int DISPLACEMENT = 10;
    private static final String TAG = "MapsActivity";
    private static GoogleApiClient googleApiClient;
    private DatabaseReference dbCheckOut;
    private DatabaseReference historyDbRef;
    private GoogleMap mMap;
    private TextView txtWarning;
    private String getName;
    private String getDuty;
    private String getShift;
    private Marker marker;
    private Location mLastLocation;
    private LocationRequest locationRequest;
    private FloatingActionButton bar;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCheckOut);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Location");
        GeoFire geoFire = new GeoFire(reference);

        initViews();
        initListener();
        setUpLocation();
    }

    private void initListener() {
        bar.setOnClickListener(this);
    }

    private void initViews() {
        //firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        String uid = mFirebaseUser.getUid();


        Intent intent = getIntent();
        TextView txtName = findViewById(R.id.checkOutName);
        TextView txtDutyPost = findViewById(R.id.txtShhDutyPost);
        TextView txtShift = findViewById(R.id.txtShhShift);
        TextView txtCheckInTime = findViewById(R.id.txtShhCheckInTime);
        CircleImageView userPhoto = findViewById(R.id.checkInPhoto);
        bar = findViewById(R.id.checkOutFab);
        txtWarning = findViewById(R.id.txtShowWarning);
        loading = new ProgressDialog(this);

        if (intent != null) {
            String adapterPosition = intent.getStringExtra("position");
            getName = intent.getStringExtra("name");
            getDuty = intent.getStringExtra("dutyPost");
            getShift = intent.getStringExtra("shift");
            String getPhoto = intent.getStringExtra("photo");
            String getCheckInTime = intent.getStringExtra("checkInTime");

            txtName.setText(getName);
            txtDutyPost.setText(getDuty);
            txtShift.setText(getShift);
            txtCheckInTime.setText(getCheckInTime);
            Glide.with(this).load(getPhoto).into(userPhoto);

            String dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
            //attendance
            dbCheckOut = FirebaseDatabase.getInstance().getReference("Attendance").child(dayOfTheWeek).child(adapterPosition);
            //create the history database
            String log = "History";
            historyDbRef = FirebaseDatabase.getInstance().getReference(log);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

            bar.setEnabled(false);
            bar.setBackgroundColor(getResources().getColor(R.color.colorRed));
            txtWarning.setText(getString(R.string.cannotCheckOut));
            Log.i(TAG, "cannot check in from this location: ");
            //makeToast("cannot check in from this location ");
        } else {
            bar.setEnabled(true);
            txtWarning.setText(getString(R.string.canCheckOut));
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
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.isIndoorLevelPickerEnabled();
        uiSettings.isRotateGesturesEnabled();
        uiSettings.isIndoorLevelPickerEnabled();
        uiSettings.isTiltGesturesEnabled();
        uiSettings.setIndoorLevelPickerEnabled(true);


        Log.d(TAG, "onMapReady: Successful");


        //create a geo fence of the area or the boundary
        LatLng placeBoundary = new LatLng(5.596091, -0.223362);//gtuc
        mMap.addCircle(new CircleOptions()
                .center(placeBoundary)
                .radius(130) //130 meters from the center of the school
                .strokeColor(Color.GREEN)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );

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
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);

            Log.i(TAG, "displayLocation: " + latitude + " " + longitude);
            if (marker != null) marker.remove();
            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .snippet(getDuty + " on " + getShift)
                    .title(getName));
            //move camera to position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                    16f));

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

    @Override
    public void onClick(View v) {
        checkOutUser();

    }

    private void checkOutUser() {
        if (!bar.isEnabled()) {
            makeToast("You can not check out from this location");
        } else {
            loading.setMessage("Checking out on  " + getShift + " at " + getDuty);
            loading.setCancelable(false);
            loading.show();

            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            String dateCheckOut = GetDateTime.getFormattedDate(today);

//The constructor can equally be used as this
            Map<String, Object> checkOutDetails = new HashMap<>();
            checkOutDetails.put("checkOutTimeStamp", dateCheckOut);
//keep history
            String historyBuilder;
            historyBuilder =
                    getName + " " + "checked out on " + " " + dateCheckOut;

            //add to history database
            final Map<String, Object> history = new HashMap<>();
            history.put("history", historyBuilder);
            final String historyID = historyDbRef.push().getKey();

            dbCheckOut.updateChildren(checkOutDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loading.dismiss();
                        makeToast("Successfully checked out");
                        //insert data into history database
                        assert historyID != null;
                        historyDbRef.child(historyID).setValue(history);
                        Log.d(TAG, "onComplete: " + historyDbRef);


                        startActivity(new Intent(MapsActivity.this,
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

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

}
