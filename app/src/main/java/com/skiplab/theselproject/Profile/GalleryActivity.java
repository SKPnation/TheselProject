package com.skiplab.theselproject.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.skiplab.theselproject.Adapter.GridImageAdapter;
import com.skiplab.theselproject.AddPost.NewPostActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Gallery;
import com.skiplab.theselproject.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    //private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 2;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private Context mContext = GalleryActivity.this;

    private ImageView backBtn, addBtn;
    private ProgressBar mProgressBar;
    private GridView gridView;

    ProgressDialog progressDialog;

    String[] storagePermissions;
    Uri image_uri;

    FirebaseAuth mAuth;
    StorageReference storageRef;
    DatabaseReference usersRef, galleryRef;
    AnstronCoreHelper coreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setupToolbar();

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        galleryRef = FirebaseDatabase.getInstance().getReference("gallery");
        coreHelper = new AnstronCoreHelper(mContext);

        backBtn = findViewById(R.id.backArrow);
        addBtn = findViewById(R.id.addtoGallery);
        gridView = (GridView) findViewById(R.id.gridView);
        progressDialog = new ProgressDialog(this);

        Query query = usersRef.orderByKey().equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getIsStaff().equals("admin"))
                    {
                        addBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupGridView();

        setupActivityWidgets();
        //tempGridSetup();
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Gallery> gallery = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("gallery");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    gallery.add(ds.getValue(Gallery.class));
                }

                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < gallery.size(); i++){
                    imgUrls.add(gallery.get(i).getImage_path());
                }
                GridImageAdapter adapter = new GridImageAdapter(mContext,R.layout.layout_grid_imageview,
                        "", imgUrls);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
    }

    /*private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("");

        setupImageGrid(imgURLs);
    }

    private void setupImageGrid(ArrayList<String> imgURLs){
        GridView gridView = findViewById(R.id.gridView);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
        gridView.setAdapter(adapter);
    }*/

    private void setupActivityWidgets(){
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressbar);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Responsible for setting up the gallery toolbar
     */
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.galleryToolbar);
        setSupportActionBar(toolbar);
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data!=null){
            //image is picked from gallery, get uri of the image
            image_uri = data.getData();

            if (image_uri !=null)
            {
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                String imageUid = UUID.randomUUID().toString();
                final String timestamp = String.valueOf(System.currentTimeMillis());


                File file = new File(SiliCompressor.with(mContext)
                        .compress(FileUtils.getPath(mContext, image_uri), new File(mContext.getCacheDir(),"temp")));
                Uri uri = Uri.fromFile(file);
                storageRef.child("Gallery/").child(coreHelper.getFileSizeFromUri(uri)).putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());

                                final String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful())
                                {
                                    Gallery gallery = new Gallery();
                                    gallery.setPhoto_id(imageUid);
                                    gallery.setDate_created(timestamp);
                                    gallery.setImage_path(downloadUri);

                                    galleryRef.child(imageUid)
                                            .setValue(gallery)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(mContext, "added to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(mContext, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });

                                    progressDialog.dismiss();
                                    setupGridView();
                                }
                                else
                                {
                                    String message = uriTask.getException().getMessage();
                                    Toast.makeText(mContext, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
