package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.utils.GetDateTime;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CheckInActivity";
    private String checkIn = "CheckIn";
    private String datePosted = "", dayOfTheWeek, fullDay, getImageUri;
    private WifiManager wifiManager;
    private Button btnCheckIn;
    private Spinner spinnerDutyPost, spinnerWorkShift;
    private ImageView imgPostImage;
    private CircleImageView checkInUserPhoto;
    private StorageReference mcheckInStorageReference;
    private StorageTask mStorageTask;
    private Uri uri = null;
    private Bitmap photo;
    private ProgressDialog loading;
    private String uid, username, userPhoto;
    private DatabaseReference mUserDbRef, mAttendance, historyDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Toolbar toolbar = findViewById(R.id.checkInToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initViews();
        initListeners();


        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStart", true);

//                        showWarning
//        if (firstStart) {
//            showWarningDialog();
//        }


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

//creates a storage like data for check in photos to be stored into
        mcheckInStorageReference = FirebaseStorage.getInstance().getReference("AttendancePhotos");

//creates a node for check in
        // mAttendance = FirebaseDatabase.getInstance().getReference("CheckIn");

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                //  Toast.makeText(PostCrimeWithPhotoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        checkInUserPhoto = findViewById(R.id.circleImageViewCheckInPhoto);
        imgPostImage = findViewById(R.id.imageViewCheckIn);
        spinnerDutyPost = findViewById(R.id.spinnerForDutyPost);
        spinnerWorkShift = findViewById(R.id.spinnerForShifs);
        loading = new ProgressDialog(this);
    }

    private void initListeners() {
        btnCheckIn.setOnClickListener(this);
        checkInUserPhoto.setOnClickListener(this);
        imgPostImage.setOnClickListener(this);

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
//                                wifiManager.setWifiEnabled(true);
                                dialog.dismiss();
                                Log.d(TAG, "onClick: dismiss");
                            }
                        });

                builder.create();
                builder.show();


            }
        }, 1000);
        Log.d(TAG, "showWarningDialog: ");

    }

    private void checkWifiSSID() {

        // wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = null;
        if (!wifiManager.isWifiEnabled()) {
            showWarningDialog();
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.isWifiEnabled();
            wifiInfo = wifiManager.getConnectionInfo();
        }
        String ssid = null;
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID(); /*you will get SSID <unknown ssid> if location turned off*/


        }
        Log.d(TAG, "onCreate: SSID " + ssid);

        assert ssid != null;
        switch (ssid) {
            case "\"GTUC-HOTSPOT\"":
                Log.d(TAG, "Connected: " + " yES " + "why?? " + ssid);
                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                break;
            case "\"GTUC-STAFF-WIFI\"":
                Log.d(TAG, "Connected: " + " yES " + "why?? " + ssid);
                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                break;
            case "\"HOTSPOT\"":
                Log.d(TAG, "Connected: " + " yES " + "why?? " + ssid);
                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                break;
            case "\"Hack Me If You Can\"":

                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                Log.d(TAG, "Connected: " + " yES " + "why?? " + "SSID = " + ssid + " button = enabled");
                break;
            case "\"VodafoneMobileWiFi-89FC35\"":

                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                Log.d(TAG, "Connected: " + " yES " + "why?? " + "SSID = " + ssid + " button = enabled");
                break;
            case "\"AndroidWifi\"":

                Toast.makeText(this, "Wifi successfully connected", Toast.LENGTH_SHORT).show();
                btnCheckIn.setEnabled(true);
                Log.d(TAG, "Connected: " + " yES " + "why?? " + "SSID = " + ssid + " button = enabled");
                break;

            default:
                showWarningDialog();
                //  Toast.makeText(this, "you cannot log in", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Connected:  " + "NO--" + " why?? " + " invalid ssid");
                btnCheckIn.setEnabled(false);
                // btnCheckIn.setBackgroundColor(R.drawable.buttonpink);
                btnCheckIn.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewCheckIn:
                openCamera();
                break;
            case R.id.circleImageViewCheckInPhoto:
                openCamera();
                break;

            case R.id.btnCheckIn:
                checkInUser();
                break;
        }

    }
/*
    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, Constants.GALLERY_REQUEST_CODE);

        }

    }
*/

    private void openCamera() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(CheckInActivity.this);
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

                photo = (Bitmap) data.getExtras().get("data");

                Glide.with(CheckInActivity.this).load(photo).into(checkInUserPhoto);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(getApplicationContext()).load(uri).into(checkInUserPhoto);
                //checkOutUser();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                loading.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkInUser() {


        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
        datePosted = GetDateTime.getFormattedDate(today);
        dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());


        loading.setMessage("please wait...It may take a moment");
        loading.setCancelable(false);
        loading.show();

        final String getTypeOfShiftSelected = spinnerWorkShift.getSelectedItem().toString();
        final String getTypeOfDutyPostSelected = spinnerDutyPost.getSelectedItem().toString();


        Map<String, Object> checkInDetails = new HashMap<>();
        checkInDetails.put("userId", uid);
        checkInDetails.put("userName", username);
        checkInDetails.put("date", datePosted);
        checkInDetails.put("dayOfWeek", dayOfTheWeek);
        //checkInDetails.put("checkInPhoto", getImageUri);
        // checkInDetails.put("checkInTimeStamp", ServerValue.TIMESTAMP);
        checkInDetails.put("checkOutTimeStamp", "");

        checkInDetails.put("dutyPost", getTypeOfDutyPostSelected);
        checkInDetails.put("typeOfShift", getTypeOfShiftSelected);

        String uploadAttendance = mAttendance.push().getKey();
        assert uploadAttendance != null;

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
                    Toast toast = Toast.makeText(CheckInActivity.this, "Successfully posted", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    startActivity(new Intent(CheckInActivity.this,
                            MainActivity.class));
                    finish();
                } else if (!task.isSuccessful()) {
                    loading.dismiss();
                    Toast toast = Toast.makeText(CheckInActivity.this,
                            task.getException().getMessage(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        //   ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        //     byte[] b = stream.toByteArray();
        //file path of the original image
        //    final StorageReference fileReference =
        // mcheckInStorageReference.child(String.valueOf(System.currentTimeMillis()));

/*
                fileReference.putBytes(b).continueWithTask(new Continuation<TaskSnapshot,
                        Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            //throw task.getException();
                            Log.d(TAG, "then: " + task.getException().getMessage());

                        }
                        return fileReference.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {

                            task.isSuccessful();
                            Uri downLoadUri = task.getResult();
                            assert downLoadUri != null;
                            getImageUri = downLoadUri.toString();



                            Log.d(TAG, "onComplete: " + getImageUri);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

*/

     /*
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Must add a photo and select your duty along with your shift.")
                    .setPositiveButton("OK", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        */

    }



}
