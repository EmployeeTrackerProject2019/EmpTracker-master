package com.employee.employeetracker.fragments;


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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PhotoFragment";
    private String getFulName;
    private String getEmail;
    private String getFirstName;
    private String getLastName;
    private String getPass;
    private String getConfirmPass;
    private String datePosted = "";
    private String uid;
    private String getImageUri = "";
    private CircleImageView mUserProfileImage;
    private FloatingActionButton mFabSelectPhoto;
    private Button btnFinishProcess;
    private View view;
    private Uri resultUri;
    private DatabaseReference usersDbRef, historyDbRef;
    private StorageReference mStorageReferenceForPhoto;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private Vibrator vibrator;


    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        assert getArguments() != null;
        getFirstName = getArguments().getString("firstName");
        getLastName = getArguments().getString("lastName");
        getEmail = getArguments().getString("passEmail");
        getPass = getArguments().getString("pass");
        getFulName = getFirstName + " " + getLastName;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        mAuth = FirebaseAuth.getInstance();


        initViews();
        initListeners();
    }

    private void initViews() {
        btnFinishProcess = view.findViewById(R.id.btnFinishProcess);
        mFabSelectPhoto = view.findViewById(R.id.fabAddFragmentPhoto);
        mUserProfileImage = view.findViewById(R.id.imageViewEditFragmentPhoto);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

//vibration
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFinishProcess:
                insertDetailsIntoDatabase();
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
        if (resultUri != null) {
            progressDialog.setMessage("loading please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(getEmail, getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                //thumb file
                                final File thumb_imageFile = new File(resultUri.getPath());

                                //  compress image file to bitmap surrounding with try catch
                                byte[] thumbBytes = new byte[0];
                                try {
                                    Bitmap thumb_imageBitmap =
                                            new Compressor(getContext())
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
                                        }

                                    }
                                });


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


                                // progressDialog.dismiss();
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
                                new AlertDialog.Builder(getContext())
                                        .setMessage("Hello" + " " + getFulName + "\n" + "an" +
                                                " email" +
                                                " verification link has been sent to " + getEmail + "\n" + "please verify to continue")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();


//                                                dialog.dismiss();
                                            }
                                        })
                                        .create()
                                        .show();


                            } else {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getContext(),
                                        task.getException().getMessage(), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                Log.d(TAG, "Error : " + task.getException().getMessage());
                            }


                        }
                    });

                }
            });
        } else {
            new AlertDialog.Builder(getContext())
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

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(Guidelines.ON)
                .start(getActivity(), this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(view).load(resultUri).into(mUserProfileImage);
                //insertDetailsIntoDatabase();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
