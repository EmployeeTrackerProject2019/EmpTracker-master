package com.employee.employeetracker.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.employee.employeetracker.R;
import com.employee.employeetracker.activities.SplashScreenActivity;
import com.employee.employeetracker.bottomsheets.PhoneNumberBottomSheet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EditProfileFragment";
    private CircleImageView userImage;
    private TextView txtfullName;
    private TextView txtEmail;
    private FirebaseUser mFirebaseUser;
    private Button btnLogout;
    private DatabaseReference userDbRef;
    private String uid;
    private View view;
    private FirebaseAuth mAuth;


    private Button btnSave;
    private final AtomicReference<DatabaseReference> historyDbRef = new AtomicReference<DatabaseReference>();
    //User id , get Phone number from the user , send verification code
    private String getEmail, mVerificationCode;
    private Uri resultUri;
    private StorageReference mStorageReferenceForPhoto;
    private TextInputLayout txtfirstName, txtLastName, txtPhoneNumber, txtAbout;
    private ProgressDialog progressDialog;
    private String getImageUri, log = "History", fullName;
    private StringBuilder historyBuilder;
    private Map<String, Object> history;


    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        initViews();
        initListeners();
        retrieveUserDetailsFromDatabase();


    }

    private void initViews() {

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        getEmail = mFirebaseUser.getEmail();


        txtfullName = view.findViewById(R.id.txtDisplayUserName);
        txtEmail = view.findViewById(R.id.txtDisplayUserEmail);
        userImage = view.findViewById(R.id.imageViewUserPhoto);

        btnLogout = view.findViewById(R.id.btnLogoutUserFromAccount);

        userDbRef = FirebaseDatabase.getInstance().getReference().child("Employee").child(uid);
        //synchronise the database offline
        userDbRef.keepSynced(true);


        historyDbRef.set(FirebaseDatabase.getInstance().getReference().child("History"));

        //creates a storage like data for check in photos to be stored into
        mStorageReferenceForPhoto = FirebaseStorage.getInstance().getReference().child(
                "userPhotos");


        //initialize views
        btnSave = view.findViewById(R.id.btnSaveProfile);
        txtfirstName = view.findViewById(R.id.textInputLayoutFirstName);
        txtLastName = view.findViewById(R.id.textInputLayoutLastName);
        txtPhoneNumber = view.findViewById(R.id.textInputLayoutPhone);
        txtAbout = view.findViewById(R.id.textInputLayoutAbout);
        progressDialog = new ProgressDialog(getActivity());

        history = new HashMap<>();
        historyBuilder = new StringBuilder();


        view.findViewById(R.id.txtEditPhoneNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                PhoneNumberBottomSheet registerPhone = new PhoneNumberBottomSheet();
                registerPhone.getEnterTransition();
                registerPhone.setRetainInstance(true);
                registerPhone.show(ft, "registerPhone");

            }
        });
    }

    private void initListeners() {
        btnLogout.setOnClickListener(this);
        userImage.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }


    private void retrieveUserDetailsFromDatabase() {
        //add a value even listener that will fetch the items in the database

        userDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user details exists

                try {

                    if (dataSnapshot.exists()) {

                        //String showEmail = (String) dataSnapshot.child("email").getValue();
                        String showImage = (String) dataSnapshot.child("image").getValue();
                        String showFullName = (String) dataSnapshot.child("fullName").getValue();

                        String showFirstName = (String) dataSnapshot.child("firstName").getValue();
                        String showLastName = (String) dataSnapshot.child("lastName").getValue();
                        String showAbout = (String) dataSnapshot.child("about").getValue();
                        String showNumber = (String) dataSnapshot.child("phone").getValue();


                        //display the values into the required fields
                        txtfullName.setText(showFullName);
                        txtEmail.setText(getEmail);
                        //display the values into the required fields
                        txtfirstName.getEditText().setText(showFirstName);
                        txtLastName.getEditText().setText(showLastName);
                        txtPhoneNumber.getEditText().setText(String.valueOf(showNumber));
                        txtAbout.getEditText().setText(showAbout);
                        Glide.with(getActivity().getApplicationContext()).load(showImage).into(userImage);

                        if (!dataSnapshot.child("phone").exists() || (dataSnapshot.child("phone").getValue()).equals(" ")) {
                            txtPhoneNumber.setVisibility(View.GONE);
                        } else {
                            txtPhoneNumber.setVisibility(View.VISIBLE);

                        }

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                try {
//                    Toast toast = Toast.makeText(getContext(),
//                            databaseError.getMessage(), Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogoutUserFromAccount:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Log out")
                        .setMessage("You will be logged out from this account")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                try {
                                    if (mFirebaseUser != null) {
                                        mAuth.signOut();

                                        Intent intent = new Intent(getActivity(),
                                                SplashScreenActivity.class);
                                        EditProfileFragment.this.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }


                }).create().show();

                break;


            case R.id.btnSaveProfile:
                saveUserDetailsEdited();
                break;

            case R.id.imageViewUserPhoto:
                openGallery();
                break;
        }


    }


    private void saveUserDetailsEdited() {
        String getFname = txtfirstName.getEditText().getText().toString();
        String getLname = txtLastName.getEditText().getText().toString();
        String getAbout = txtAbout.getEditText().getText().toString();

        if (getFname.isEmpty() || getLname.isEmpty() || getAbout.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
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
        final String historyID = historyDbRef.get().push().getKey();

        userDbRef.updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    assert historyID != null;
                    historyDbRef.get().child(log).child(historyID).setValue(history);
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Profile Successfully changed", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity(), this);


        // Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // getActivity().startActivityForResult(pickPhotoIntent, Constants.GALLERY_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
/*
        getActivity();
        if (requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                Glide.with(view).load(resultUri).into(userImage);
                uploadFile();
                Log.d(TAG, "onActivityResult: " + resultUri.toString());


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: " + error);

            }
        }
*/
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == getActivity().RESULT_OK) {
                assert result != null;
                resultUri = result.getUri();
                //  userImage.setImageURI(uri);

                Glide.with(view).load(resultUri).into(userImage);
                uploadFile();
                //insertDetailsIntoDatabase();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                progressDialog.dismiss();
                assert result != null;
                String error = result.getError().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile() {
        if (resultUri != null) {
            progressDialog.setMessage("please wait ...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //thumb file
            final File thumb_imageFile = new File(resultUri.getPath());

            //  compress image file to bitmap surrounding with try catch
            byte[] thumbBytes = new byte[0];
            try {
                Bitmap thumb_imageBitmap = new Compressor(getContext())
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
                        final String historyID = historyDbRef.get().push().getKey();


                        userDbRef.updateChildren(userPhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //add to history
                                    assert historyID != null;
                                    historyDbRef.get().child(log).child(historyID).setValue(history);
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(getContext(), "Successfully posted",
                                            Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();

                                } else {
                                    progressDialog.dismiss();
                                    Toast toast = Toast.makeText(getContext(),
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
