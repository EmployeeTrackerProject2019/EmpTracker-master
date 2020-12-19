package com.employee.employeetracker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, PhoneNumberBottomSheet.PhoneNumberBottomSheetListener {
    private static final String TAG = "EditProfileActivity";
    private Button btnSave;
    private DatabaseReference userDbRef, historyDbRef;
    //User id , get Phone number from the user , send verification code
    private String uid;
    private String mVerificationCode;
    private Uri uri;
    private FloatingActionButton fabAddPhoto;
    private CircleImageView circleImageViewUserPhoto;
    private StorageReference mStorageReferenceForPhoto;
    private TextInputLayout txtfirstName, txtLastName, txtEmail, txtPhoneNumber, txtAbout;
    private ProgressDialog progressDialog;
    private final String log = "History";
    private String getImageUri;
    private String fullName;
    private StringBuilder historyBuilder;
    private Map<String, Object> history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();
        initListeners();
        retrieveUserDetailsFromDatabase();


        findViewById(R.id.txtEditPhoneNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneNumberBottomSheet registerPhone = new PhoneNumberBottomSheet();
                registerPhone.getEnterTransition();
                registerPhone.setRetainInstance(true);
                registerPhone.show(getSupportFragmentManager(), "registerPhone");
            }
        });
    }

    private void initListeners() {
        circleImageViewUserPhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        fabAddPhoto.setOnClickListener(this);
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

        userDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);
        //synchronise the database offline
        userDbRef.keepSynced(true);

        historyDbRef = FirebaseDatabase.getInstance().getReference().child("History");

        //creates a storage like data for check in photos to be stored into
        mStorageReferenceForPhoto = FirebaseStorage.getInstance().getReference().child(
                "userPhotos");


        //initialize views
        btnSave = findViewById(R.id.btnSaveProfile);
        txtEmail = findViewById(R.id.textInputLayoutEmail);
        txtfirstName = findViewById(R.id.textInputLayoutFirstName);
        txtLastName = findViewById(R.id.textInputLayoutLastName);
        txtPhoneNumber = findViewById(R.id.textInputLayoutPhone);
        txtAbout = findViewById(R.id.textInputLayoutAbout);
        circleImageViewUserPhoto = findViewById(R.id.imageViewEditPhoto);
        fabAddPhoto = findViewById(R.id.fabAddPhoto);
        progressDialog = new ProgressDialog(this);

        history = new HashMap<>();
        historyBuilder = new StringBuilder();

    }


    private void retrieveUserDetailsFromDatabase() {
        //add a value even listener that will fetch the items in the database
        final String urlShortNer = "https://bit.ly/2tizS8L";
        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user details exists
                if (dataSnapshot.exists()) {
                    String showFirstName = (String) dataSnapshot.child("firstName").getValue();
                    String showLastName = (String) dataSnapshot.child("lastName").getValue();
                    String showEmail = (String) dataSnapshot.child("email").getValue();
                    String showImage = (String) dataSnapshot.child("image").getValue();
                    String showAbout = (String) dataSnapshot.child("about").getValue();
                    String showNumber = (String) dataSnapshot.child("phone").getValue();

                    fullName = showFirstName + " " + showLastName;

                    //display the values into the required fields
                    txtfirstName.getEditText().setText(showFirstName);
                    txtLastName.getEditText().setText(showLastName);
                    txtEmail.getEditText().setText(showEmail);
                    txtPhoneNumber.getEditText().setText(String.valueOf(showNumber));
                    txtAbout.getEditText().setText(showAbout);
                    if (dataSnapshot.child("image").exists()) {
                        Glide.with(EditProfileActivity.this).load(showImage).into(circleImageViewUserPhoto);

                    } else {

                        Glide.with(EditProfileActivity.this).load(urlShortNer).into(circleImageViewUserPhoto);
                    }
                } else if (!dataSnapshot.exists()) {

                    txtPhoneNumber.getEditText().setText(" ");
                    Glide.with(EditProfileActivity.this).load(urlShortNer).into(circleImageViewUserPhoto);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(EditProfileActivity.this,
                        databaseError.getMessage(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewEditPhoto:

            case R.id.fabAddPhoto:
                openGallery();
                break;

            case R.id.btnSaveProfile:
                saveUserDetailsEdited();
                break;


        }

    }

    private void saveUserDetailsEdited() {
        String getFname = txtfirstName.getEditText().getText().toString();
        String getLname = txtLastName.getEditText().getText().toString();
        String getAbout = txtAbout.getEditText().getText().toString();

        if (getFname.isEmpty() || getLname.isEmpty() || getAbout.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String fullName = getFname + " " + getLname;
        HashMap<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", getFname);
        userDetails.put("lastName", getLname);
        userDetails.put("about", getAbout);
        userDetails.put("fullName", fullName);

        progressDialog.setMessage("please wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //keep log files

        historyBuilder.append(fullName).append(" ").append("edited profile");

        //add to history database

        history.put("history", historyBuilder);
        final String historyID = historyDbRef.push().getKey();

        userDbRef.updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    assert historyID != null;
                    historyDbRef.child(log).child(historyID).setValue(history);
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Profile Successfully changed", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void openGallery() {
        CropImage.activity()
                .setAspectRatio(16, 16)
                .start(EditProfileActivity.this);
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

                Glide.with(EditProfileActivity.this).load(uri).into(circleImageViewUserPhoto);
                uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void uploadFile() {
        if (uri != null) {
            progressDialog.setMessage("please wait ...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //thumb file
            final File thumb_imageFile = new File(uri.getPath());

            //  compress image file to bitmap surrounding with try catch
            byte[] thumbBytes = new byte[0];
            try {
                Bitmap thumb_imageBitmap = new Compressor(this)
                        .setQuality(100)
                        .compressToBitmap(thumb_imageFile);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                thumbBytes = byteArrayOutputStream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //                file path for the image
            final StorageReference fileReference = mStorageReferenceForPhoto.child(uid + "." + uri.getLastPathSegment());


            fileReference.putFile(uri).continueWithTask(new Continuation<TaskSnapshot, Task<Uri>>() {
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

                        Map<String, Object> userPhoto = new HashMap<>();
                        userPhoto.put("image", getImageUri);
//                        userPhoto.put("timeStamp", ServerValue.TIMESTAMP);

                        //keep log files
                        //historyBuilder = new StringBuilder();
                        historyBuilder.append(fullName).append(" ").append("changed profile " +
                                "picture");

                        //add to history database
                        //history = new HashMap<>();
                        history.put("history", historyBuilder);
                        final String historyID = historyDbRef.push().getKey();


                        userDbRef.updateChildren(userPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //add to history
                                    assert historyID != null;
                                    historyDbRef.child(log).child(historyID).setValue(history);
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(EditProfileActivity.this, "Successfully posted",
                                            Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(EditProfileActivity.this,
                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        });
                    }

                }
            });


        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Must add a photo ")
                    .setPositiveButton("OK", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

    }

    @Override
    public void onButtonClicked(String text) {
        String getPhoneNumberFromUser = text;
    }


}
