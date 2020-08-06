package com.skiplab.theselproject.AddPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

public class NewPostActivity extends AppCompatActivity {

    private static final String TAG = "NewPostActivity";

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //imagepick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    private EditText pDescEt;
    private ImageView imageIv, fowardArrow;
    private FloatingActionButton ImageFab;

    String currentMood;

    private DatabaseReference usersRef;

    //user info
    String name, email, uid, dp;
    String adminID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    int i=0;

    //image picked will be saved in this uri
    Uri image_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        fowardArrow = findViewById(R.id.fowardArrow);
        pDescEt = findViewById(R.id.pDescEt);
        imageIv = findViewById(R.id.pImageIv);
        ImageFab = findViewById(R.id.fab);

        final Intent intent = getIntent();
        currentMood = intent.getStringExtra("mood");

        usersRef.orderByKey().equalTo(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            if (user.getUid().equals(adminID)){
                                ImageFab.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        fowardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pDescription = pDescEt.getText().toString();

                if (TextUtils.isEmpty(pDescription)){
                    Toast.makeText(NewPostActivity.this, "Type what's on your mind...", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if (image_uri == null){
                        if(Common.isConnectedToTheInternet(getBaseContext()))
                        {
                            Intent postIntent = new Intent(NewPostActivity.this, CategoryActivity.class);
                            postIntent.putExtra("pDesc", pDescription);
                            postIntent.putExtra("mood",currentMood);
                            postIntent.putExtra("imageUri", "noImage");
                            startActivity(postIntent);

                            Log.d(TAG, "postInfo: "+ pDescription+ ", " + currentMood +", "+ image_uri);
                        }
                        else
                        {
                            i++;

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NewPostActivity.this);
                            builder.setMessage("Please check your internet connection");
                            builder.show();

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(NewPostActivity.this, DashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);

                            return;
                        }
                    }
                    else {
                        if(Common.isConnectedToTheInternet(getBaseContext()))
                        {
                            Intent postIntent = new Intent(NewPostActivity.this, CategoryActivity.class);
                            postIntent.putExtra("pDesc", pDescription);
                            postIntent.putExtra("mood",currentMood);
                            postIntent.putExtra("imageUri", String.valueOf(image_uri));
                            startActivity(postIntent);
                        }
                        else
                        {
                            i++;

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(NewPostActivity.this);
                            builder.setMessage("Please check your internet connection");
                            builder.show();

                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(NewPostActivity.this, DashboardActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1000);

                            return;
                        }
                    }
                }
            }
        });

        //checkUserStatus();
    }

    private void showImagePickDialog() {

        //options(Camera, Gallery) to show in Dialog
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from:");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //Camera clicked
                    //We need to check permissions first
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if (which == 1){
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.show();
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desc");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permissions
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){
        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //request runtime storage permissions
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }



    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this method is called when user press Allow or Deny from permission request Dialog
        //here will handle permissions cases (allowed and denied)

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //both permission granted
                        pickFromCamera();
                    }
                    else {
                        //camera or gallery or both permissions denied
                        Toast.makeText(NewPostActivity.this, "Camera & Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //storage permission granted
                        pickFromGallery();
                    }
                    else {
                        //gallery permissions denied
                        Toast.makeText(NewPostActivity.this, "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of the image
                image_uri = data.getData();

                //set to imageView
                imageIv.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of the image

                imageIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
