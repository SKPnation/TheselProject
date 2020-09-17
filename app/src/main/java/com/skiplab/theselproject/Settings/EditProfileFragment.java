package com.skiplab.theselproject.Settings;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    //imagepick constants
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private ImageView backBtn, saveBtn;
    private ImageView mProfilePhoto;
    private TextView changPhotoTv;
    private EditText uNameEt, mAgeEt, mBioEt, mEmail, mPhone;

    FirebaseAuth firebaseAuth;
    StorageReference storageRef;
    DatabaseReference usersRef;
    AnstronCoreHelper coreHelper;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    String uid;

    Uri image_uri;

    ProgressDialog progressDialog;


    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        coreHelper = new AnstronCoreHelper(getActivity());

        mProfilePhoto = view.findViewById(R.id.profile_photo);
        uNameEt = view.findViewById(R.id.display_name);
        mAgeEt = view.findViewById(R.id.display_age);
        mBioEt = view.findViewById(R.id.display_bio);
        mEmail = view.findViewById(R.id.display_email);
        mPhone = view.findViewById(R.id.display_phone);
        backBtn = view.findViewById(R.id.backArrow);
        saveBtn = view.findViewById(R.id.saveBtn);
        changPhotoTv = view.findViewById(R.id.changeProfilePhoto);

        mEmail.setFocusable(false);
        mEmail.setFocusableInTouchMode(false);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d( TAG, "onClick: attempting to save settings." );

                /*
                    -------Change Name--------
                */
                if (!uNameEt.getText().toString().equals( "" ))
                {
                    progressDialog.setMessage( "Loading..." );
                    progressDialog.show();

                    usersRef.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .child( "username" )
                            .setValue( uNameEt.getText().toString() );

                    //commented all these codes below to keep user anonymous on posts & comments

                    /*//if user edits his/her name also update in his/her posts
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                    Query query1 = ref.orderByChild("uid").equalTo(uid);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String child = ds.getKey();
                                dataSnapshot.getRef().child(child).child("uName").setValue(uNameEt.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });

                    //if user edits his/her name also update in his/her comments on posts
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String child = ds.getKey();
                                if (dataSnapshot.child(child).hasChild("comments")){
                                    String child1 = ""+dataSnapshot.child(child).getKey();
                                    Query child2 = FirebaseDatabase.getInstance().getReference("posts").child(child1).child("comments").orderByChild("uid").equalTo(uid);
                                    child2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                String child = ds.getKey();
                                                dataSnapshot.getRef().child(child).child("uName").setValue(uNameEt.getText().toString());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //..
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });*/


                    progressDialog.dismiss();
                }
                if (!mAgeEt.getText().toString().equals( "" )){
                    progressDialog.setMessage( "Loading..." );
                    progressDialog.show();

                    usersRef.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .child( "age" )
                            .setValue( mAgeEt.getText().toString() );
                    progressDialog.dismiss();
                }
                if (!mBioEt.getText().toString().equals( "" )){
                    progressDialog.setMessage( "Loading..." );
                    progressDialog.show();

                    usersRef.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .child( "bio" )
                            .setValue( mBioEt.getText().toString() );
                    progressDialog.dismiss();
                }
                /*
                    --------Change Phone Number--------
                 */
                if (!mPhone.getText().toString().equals( "" )){
                    usersRef.child( FirebaseAuth.getInstance().getCurrentUser().getUid() )
                            .child( "phone" )
                            .setValue( mPhone.getText().toString() );
                    progressDialog.dismiss();
                }

                getActivity().finish();

            }
        });

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()){
                    requestStoragePermission();
                }
                else {
                    pickFromGallery();
                }
            }
        });

        changPhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()){
                    requestStoragePermission();
                }
                else {
                    pickFromGallery();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        if(Common.isConnectedToTheInternet(getContext()))
        {
            retrieveUserData();
        }
        else
        {
            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

        checkUserStatus();

        return view;
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permissions
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data!=null){
            //image is picked from gallery, get uri of the image
            image_uri = data.getData();

            if (image_uri !=null)
            {
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                final String randomNum = ""+ UUID.randomUUID().toString();

                String fileNameAndPath = "Profiles/"+"image_"+randomNum;

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image_uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] pictureData = baos.toByteArray(); //convert images to bytes
                    StorageReference reference = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
                    reference.putBytes(pictureData)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());

                                    final String downloadUri = uriTask.getResult().toString();

                                    if (uriTask.isSuccessful()){

                                        usersRef.child( uid )
                                                .child( "profile_photo" )
                                                .setValue(downloadUri);

                                        progressDialog.dismiss();
                                        retrieveUserData();

                                                /*.addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            //if user edits his/her photo also update in his/her posts
                                                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                                                            Query query1 = ref.orderByChild("uid").equalTo(uid);
                                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                        String child = ds.getKey();
                                                                        dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    //..
                                                                }
                                                            });

                                                            //if user edits his/her name also update in his/her comments on posts
                                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                        String child = ds.getKey();
                                                                        if (dataSnapshot.child(child).hasChild("comments")){
                                                                            String child1 = ""+dataSnapshot.child(child).getKey();
                                                                            Query child2 = FirebaseDatabase.getInstance().getReference("posts").child(child1).child("comments").orderByChild("uid").equalTo(uid);
                                                                            child2.addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                        String child = ds.getKey();
                                                                                        dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri);
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                    //..
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    //..
                                                                }
                                                            });


                                                            progressDialog.dismiss();
                                                            retrieveUserData();

                                                        } else {
                                                            String message = task.getException().getMessage();
                                                            Toast.makeText(getActivity(), "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    }
                                                });*/
                                    }
                                    else
                                    {
                                        String message = uriTask.getException().getMessage();
                                        Toast.makeText(getActivity(), "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //set to imageView
            mProfilePhoto.setImageURI(image_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void retrieveUserData() {
        /*We have to get info of currently signed in user. We can get it using user's email or uid
          Im gonna retrieve user detail using email*/
        /*By using orderByChild query we will show the detail from a node whose key named email has a value
        equal to currently signed in email.
        It will search all nodes, where the matches it will get its detail*/

        Query query1 = usersRef
                .orderByKey()
                .equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() );

        //orderByKey method will look for the key encapsulating the values of the object

        query1.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                    User user = singleSnapshot.getValue(User.class);
                    Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                    //mProgressBar.setVisibility(View.GONE);

                    uNameEt.setText( user.getUsername() );
                    mAgeEt.setText(user.getAge());
                    mBioEt.setText(user.getBio());
                    mEmail.setText(user.getEmail());
                    mPhone.setText(user.getPhone());

                    try {
                        UniversalImageLoader.setImage(user.getProfile_photo(), mProfilePhoto, null, "");
                    }
                    catch (Exception e){
                        //
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        } );
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //mProfileTv.setText(user.getEmail());
            uid = user.getUid();
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
