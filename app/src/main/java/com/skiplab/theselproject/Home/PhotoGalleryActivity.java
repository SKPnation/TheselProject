package com.skiplab.theselproject.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.skiplab.theselproject.Adapter.GridImageAdapter;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Gallery;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;

public class PhotoGalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    Context mContext = PhotoGalleryActivity.this;

    //private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 2;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    private ImageView addBtn, closeBtn;

    ProgressDialog progressDialog;
    private ProgressBar mProgressBar;
    private GridView gridView;

    String[] storagePermissions;
    Uri image_uri;

    FirebaseAuth mAuth;
    StorageReference storageRef;
    DatabaseReference usersRef, galleryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        galleryRef = FirebaseDatabase.getInstance().getReference("gallery");
        galleryRef.keepSynced(true);

        closeBtn = findViewById(R.id.closeBtn);
        addBtn = findViewById(R.id.addtoGallery);
        gridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.gridImageProgressbar);
        progressDialog = new ProgressDialog(mContext);

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
                //..
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupGridView();

        setupActivityWidgets();
        //tempGridSetup();

    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
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

    private void setupActivityWidgets() {
        mProgressBar.setVisibility(View.GONE);
    }

}
