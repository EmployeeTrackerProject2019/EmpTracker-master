package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.fragments.CheckOutFragment;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CheckOutActivity";
    private String datePosted = "";
    private String checkOut = "CheckOut";
    private String log = "History";
    private String getImageUri;
    private Button btnCheckOut;
    private ImageView imgPostImage;
    private CircleImageView checkOutUserPhoto;
    private StorageReference mcheckOutStorageReference;
    private TextView txtViewSignInTime;
    private Uri uri;
    private ProgressDialog loading;
    private String uid, dutyPost, userName, workShift;
    private DatabaseReference checkOutDbRef;
    private DatabaseReference historyDbRef;
    private long timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = findViewById(R.id.checkOutToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStart", true);
        //                        showWarning
        if (firstStart) {
            showWarningDialog();
        }

        initViews();
        initListeners();
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

        txtViewSignInTime = findViewById(R.id.textViewSignedInTime);
//creates a storage like data for check in photos to be stored into
        mcheckOutStorageReference = FirebaseStorage.getInstance().getReference("AttendancePhotos");

        DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);
        usersDbRef.keepSynced(true);

        DatabaseReference checkInDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(
                "CheckIn");
        checkInDbRef.keepSynced(true);

//creates a node for check out
        checkOutDbRef =
                FirebaseDatabase.getInstance().getReference().child("Employee");
        checkOutDbRef.keepSynced(true);

        historyDbRef = FirebaseDatabase.getInstance().getReference().child(log);
//        historyDbRef.keepSynced(true);


        //RETRIEVE the details of the user
        usersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userName = (String) dataSnapshot.child("fullName").getValue();
                dutyPost = (String) dataSnapshot.child("dutyPost").getValue();
                workShift = (String) dataSnapshot.child("typeOfShift").getValue();
                timeStamp = (long) dataSnapshot.child("timeStamp").getValue();

                SimpleDateFormat sfd = new SimpleDateFormat("'You signed in on ' dd-MM-yyyy '@' " +
                        "HH:mm:ss",
                        Locale.US);

                try {
                    txtViewSignInTime.setText(sfd.format(new Date(timeStamp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                //  Toast.makeText(PostCrimeWithPhotoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        btnCheckOut = findViewById(R.id.btnCheckOut);
        checkOutUserPhoto = findViewById(R.id.circleImageViewCheckOut);
        imgPostImage = findViewById(R.id.imageViewCheckOut);
        loading = new ProgressDialog(this);
    }

    private void initListeners() {
        btnCheckOut.setOnClickListener(this);
        checkOutUserPhoto.setOnClickListener(this);
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
                                dialog.dismiss();
                            }
                        });

                builder.create();
                builder.show();


            }
        }, 1000);


        SharedPreferences prefs = this.getSharedPreferences("loader", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewCheckOut:
                openCamera();
                break;
            case R.id.circleImageViewCheckOut:
                openCamera();
                break;
            case R.id.btnCheckOut:
                checkOutUser();
                break;

        }
    }


    private void openCamera() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(CheckOutActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                uri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(getApplicationContext()).load(uri).into(checkOutUserPhoto);
                //checkOutUser();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                loading.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkOutUser() {
        try {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
            datePosted = getFormattedDate(today);
            //datePosted = sfd.format(new Date(today.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uri != null) {
            loading.setMessage("please wait...It may take a moment");
            loading.setCancelable(false);
            loading.show();

            //thumb file
            final File thumb_imageFile = new File(uri.getPath());

            //  compress image file to bitmap surrounding with try catch
            byte[] thumbBytes = new byte[0];
            try {
                Bitmap thumb_imageBitmap = new Compressor(this)
                        .setMaxHeight(720)
                        .setMaxWidth(720)
                        .setQuality(100)
                        .compressToBitmap(thumb_imageFile);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                thumbBytes = byteArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }

            final String historyId = historyDbRef.push().getKey();

            //file path of the original image
            final StorageReference fileReference =
                    mcheckOutStorageReference.child(System.currentTimeMillis() + uri.getLastPathSegment());


            fileReference.putFile(uri).continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if (task.isSuccessful()) {
                        Uri downLoadUri = task.getResult();
                        assert downLoadUri != null;
                        getImageUri = downLoadUri.toString();

                        Map<String, Object> checkOutDetails = new HashMap<>();
                        checkOutDetails.put("checkOutPhoto", getImageUri);
                        checkOutDetails.put("checkOutTimeStamp", ServerValue.TIMESTAMP);
                        checkOutDetails.put("userName", userName);
                        checkOutDetails.put("userId", uid);
                        checkOutDetails.put("date", datePosted);

                        checkOutDetails.put("dutyPost", dutyPost);
                        checkOutDetails.put("typeOfShift", workShift);

                        String checkOutId = checkOutDbRef.push().getKey();
                        assert checkOutId != null;

//keep log files
                        final String historyBuilder;
                        historyBuilder =
                                userName + " " + "checked out on" + " " + datePosted;
                        //add to history database
                        final Map<String, Object> history = new HashMap<>();
                        history.put("history", historyBuilder);


                        checkOutDbRef.child(checkOut).child(uid).child(checkOutId).setValue(checkOutDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //insert into history db
                                    assert historyId != null;
                                    historyDbRef.child(historyId).setValue(history);

                                    loading.dismiss();
                                    Toast toast = Toast.makeText(CheckOutActivity.this,
                                            "Successfully checked out", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                    startActivity(new Intent(CheckOutActivity.this,
                                            MainActivity.class));
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new CheckOutFragment()).commit();
                                    finish();
                                } else if (!task.isSuccessful()) {
                                    loading.dismiss();
                                    Toast toast = Toast.makeText(CheckOutActivity.this,
                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        });


                    } else {
                        Toast toast = Toast.makeText(CheckOutActivity.this,
                                task.getException().getMessage(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });


        } else {

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

    }

    private String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);

        switch (day % 10) {
            case 1:
                return new SimpleDateFormat("EEEE MMMM d'st', yyyy", Locale.US).format(date);
            case 2:
                return new SimpleDateFormat("EEEE MMMM d'nd', yyyy", Locale.US).format(date);
            case 3:
                return new SimpleDateFormat("EEEE MMMM d'rd', yyyy", Locale.US).format(date);
            default:
                return new SimpleDateFormat("EEEE MMMM d'th', yyyy", Locale.US).format(date);
        }

    }
}
