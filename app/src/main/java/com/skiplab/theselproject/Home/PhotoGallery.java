package com.skiplab.theselproject.Home;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Gallery;
import com.skiplab.theselproject.models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGallery extends Fragment {

    private static final String TAG = "GalleryActivity";

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
    AnstronCoreHelper coreHelper;


    public PhotoGallery() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        galleryRef = FirebaseDatabase.getInstance().getReference("gallery");
        coreHelper = new AnstronCoreHelper(getActivity());

        closeBtn = view.findViewById(R.id.closeBtn);
        addBtn = view.findViewById(R.id.addtoGallery);
        gridView = (GridView) view.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.gridImageProgressbar);
        progressDialog = new ProgressDialog(getActivity());


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
                getActivity().finish();
            }
        });

        setupGridView();

        setupActivityWidgets();
        //tempGridSetup();


        return view;
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
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

                String imageUid = UUID.randomUUID().toString();
                final String timestamp = String.valueOf(System.currentTimeMillis());


                File file = new File(SiliCompressor.with(getActivity())
                        .compress(FileUtils.getPath(getActivity(), image_uri), new File(getActivity().getCacheDir(),"temp")));
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
                                                        Toast.makeText(getActivity(), "added to firebase database successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "Error : " + message, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
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
