package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.models.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView.Guidelines;

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

public class PhotoRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhotoRegisterActivity";
    private String getFulName;
    private String getEmail;
    private String getFirstName;
    private String getLastName;
    private String getPass;
    private String datePosted = "";
    private String uid;
    private String getImageUri = "";
    private CircleImageView mUserProfileImage;
    private FloatingActionButton mFabSelectPhoto;
    private Button btnFinishProcess;
    private Uri resultUri;
    private StorageReference mStorageReferenceForPhoto;
    private DatabaseReference usersDbRef, historyDbRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_register);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            getFirstName = intent.getStringExtra("passFirstName");
            getLastName = intent.getStringExtra("passLastName");
            getEmail = intent.getStringExtra("passEmail");
            getPass = intent.getStringExtra("passPassword");
            getFulName = getFirstName + " " + getLastName;

        }

        initViews();
        initListeners();
    }

    private void initViews() {
        btnFinishProcess = findViewById(R.id.btnFinishProcess);
        mFabSelectPhoto = findViewById(R.id.fabAddFragmentPhoto);
        mUserProfileImage = findViewById(R.id.imageViewEditFragmentPhoto);

        progressDialog = new ProgressDialog(this);

//vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //creates a node for employee database
        usersDbRef = FirebaseDatabase.getInstance().getReference("Employee");

        //creates a storage like data for check in photos to be stored into
        mStorageReferenceForPhoto = FirebaseStorage.getInstance().getReference().child(
                "userPhotos");
        //create the history database
        String log = "History";
        historyDbRef = FirebaseDatabase.getInstance().getReference(log);


    }

    private void initListeners() {
        btnFinishProcess.setOnClickListener(this);
        mUserProfileImage.setOnClickListener(this);
        mFabSelectPhoto.setOnClickListener(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(PhotoRegisterActivity.this).load(resultUri).into(mUserProfileImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(Guidelines.ON)
                .start(PhotoRegisterActivity.this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinishProcess:

                if (resultUri != null) {

                    insertDetailsIntoDatabase();
                } else {

                    new AlertDialog.Builder(PhotoRegisterActivity.this)
                            .setTitle("Warning")
                            .setMessage("Must add a photo ")
                            .setPositiveButton("OK", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();

                }

                break;
            case R.id.imageViewEditFragmentPhoto:
                openGallery();
                break;
            case R.id.fabAddFragmentPhoto:
                openGallery();
                break;
        }
    }

    private void insertDetailsIntoDatabase() {

        progressDialog.setMessage("loading please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(getEmail, getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    uid = firebaseUser.getUid();

                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                // progressDialog.dismiss();

                                //thumb file
                                final File thumb_imageFile = new File(resultUri.getPath());

                                //  compress image file to bitmap surrounding with try catch
                                byte[] thumbBytes = new byte[0];
                                try {
                                    Bitmap thumb_imageBitmap =
                                            new Compressor(PhotoRegisterActivity.this)
                                                    .setQuality(100)
                                                    .compressToBitmap(thumb_imageFile);

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                    thumbBytes = byteArrayOutputStream.toByteArray();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //                file path for the image
                                final StorageReference fileReference =
                                        mStorageReferenceForPhoto.child(uid + "." + resultUri.getLastPathSegment());

                                fileReference.putFile(resultUri).continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
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
                                        if (task.isSuccessful()) {
                                            Uri downLoadUri = task.getResult();
                                            assert downLoadUri != null;
                                            getImageUri = downLoadUri.toString();


                                            //keep log files
                                            String historyBuilder;
                                            historyBuilder =
                                                    getFulName + " " + "created an account on" + " " + datePosted;

                                            //add to history database
                                            final Map<String, Object> history = new HashMap<>();
                                            history.put("history", historyBuilder);
                                            final String historyID = historyDbRef.push().getKey();
                                            Calendar calendar = Calendar.getInstance();
                                            Date today = calendar.getTime();
//                SimpleDateFormat sfd = new SimpleDateFormat("EEEE dd/MMMM/yyyy", Locale.US);
                                            datePosted = getFormattedDate(today);
                                            //datePosted = sfd.format(new Date(today.toString()));


                                            Log.d(TAG, "onComplete: Email verification has been sent");
                                            Users users = new Users(uid, getFirstName, getLastName,
                                                    getFulName, getEmail, "", "", getImageUri);

                                            usersDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);

                                            //Now create the database for the user
                                            usersDbRef.setValue(users);

                                            //insert data into history database
                                            assert historyID != null;
                                            historyDbRef.child(historyID).setValue(history);
                                            Log.d(TAG, "User has registered: " + historyDbRef);

                                            progressDialog.dismiss();


                                            //for Mashmallow and above
                                            if (Build.VERSION.SDK_INT >= 26) {
                                                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                            } else {
                                                //vibrate on lollipop and below
                                                vibrator.vibrate(200);

                                            }
                                            //Alert dialog prompt to show user email link has be
                                            // sent
                                            new AlertDialog.Builder(PhotoRegisterActivity.this)
                                                    .setMessage("Hello" + " " + getFulName + "\n" + "an" +
                                                            " email" +
                                                            " verification link has been sent to " + getEmail + "\n" + "please verify to continue")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            startActivity(new Intent(PhotoRegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                            finish();

                                                        }
                                                    })
                                                    .create()
                                                    .show();
                                        }

                                    }
                                });


                            } else {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(PhotoRegisterActivity.this,
                                        task.getException().getMessage(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d(TAG, "Error : " + task.getException().getMessage());
                            }

                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(PhotoRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "Error : " + task.getException().getMessage());
                }

            }
        });


    }


}
