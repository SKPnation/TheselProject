package com.skiplab.theselproject.AddPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skiplab.theselproject.Activity.ActivityFragment;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";

    Context mContext = CategoryActivity.this;

    private String currentMood, pDescription, image_uri;
    private ImageView sendPost;
    private TextView selectedCategoryTv;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;
    FirebaseUser user;

    ProgressDialog pd;

    //user info
    String name, email, uid, dp;

    int i = 0;
    private double mPhotoUploadProgress = 0;

    ListView ch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent getIntent = getIntent();
        currentMood = getIntent.getStringExtra("mood");
        pDescription = getIntent.getStringExtra("pDesc");
        image_uri = getIntent.getStringExtra("imageUri");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        pd = new ProgressDialog(this);

        userDbRef = FirebaseDatabase.getInstance().getReference("users");

        Query query1 = userDbRef
                .orderByKey()
                .equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() );

        //orderByKey method will look for the key encapsulating the values of the object

        query1.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                    User user = singleSnapshot.getValue(User.class);
                    Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                    name = user.getUsername();
                    email = user.getEmail();
                    dp = user.getProfile_photo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        } );


        selectedCategoryTv = findViewById(R.id.selectedCategoryTv);
        sendPost = findViewById(R.id.sendPost);
        ch1 = findViewById(R.id.list_item);
        ch1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        String[] items = {"Relationship", "Addiction", "Depression", "Parenting", "Career", "Child Abuse", "Low self-esteem",
                "Family", "Anxiety", "Pregnancy", "Business", "Weight Loss", "Fitness", "Helpful Tips", "#COVID19 NIGERIA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.category_row, R.id.categoryTv, items);
        ch1.setAdapter(adapter);
        ch1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                //String selectedItem  = ((TextView)view).getText().toString();
                selectedCategoryTv.setText(selectedItem);
            }
        });

        sendPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selCatTv = selectedCategoryTv.getText().toString();

                if (selCatTv.isEmpty()){
                    Toast.makeText(CategoryActivity.this, "Please choose a category", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(Common.isConnectedToTheInternet(getBaseContext()))
                    {
                        AlertDialog ad = new AlertDialog.Builder(CategoryActivity.this)
                                .setTitle("Do you want to be anonymous?")
                                .setPositiveButton( "YES", new DialogInterface.OnClickListener() {
                                    public void onClick( DialogInterface dialog, int i)
                                    {
                                        Toast.makeText(mContext, "Please wait...", Toast.LENGTH_LONG).show();

                                        uploadDataAsAnonymous(currentMood, pDescription, image_uri, selCatTv);
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Toast.makeText(mContext, "Please wait...", Toast.LENGTH_LONG).show();

                                        uploadData(currentMood, pDescription, image_uri, selCatTv);
                                    }
                                }).show();
                    }
                    else
                    {

                        i++;

                        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                        builder.setMessage("Please check your internet connection");
                        builder.show();

                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(CategoryActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);

                        return;
                    }
                }

            }
        });

        checkUserStatus();
    }

    private void uploadData(final String currentMood, final String pDescription, String image_uri, final String selCatTv) {
        pd.setMessage("Publishing post...");
        pd.show();

        //for post- image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "posts/" + "post_" + timeStamp;

        if (!image_uri.equals("noImage")){
            //post Image
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(image_uri));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] pictureData = baos.toByteArray(); //convert images to bytes

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                storageRef.putBytes(pictureData)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //image has been uploaded to firebase storage, now get its url
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());

                                String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful()){

                                    Query query1 = userDbRef
                                            .orderByKey()
                                            .equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() );

                                    //orderByKey method will look for the key encapsulating the values of the object

                                    query1.addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                                                User user = singleSnapshot.getValue(User.class);
                                                Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                                                //url is received upload post to firebase firestore

                                                Post post = new Post();
                                                post.setUid(uid);
                                                post.setuName(user.getUsername());
                                                post.setuEmail(firebaseAuth.getCurrentUser().getEmail());
                                                post.setuDp(user.getProfile_photo());
                                                post.setpId(timeStamp);
                                                post.setpLikes("0");
                                                post.setpComments("0");
                                                post.setpDescription(pDescription);
                                                post.setpImage(downloadUri);
                                                post.setpCategory(selCatTv);
                                                post.setpTime(timeStamp);
                                                post.setuMood(currentMood);

                                                //Path to store post data
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                                                //put data in this ref
                                                ref.child(timeStamp).setValue(post)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //added to firebase
                                                                pd.dismiss();

                                                                Query query = userDbRef.orderByKey().equalTo(uid);
                                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                            User user = ds.getValue(User.class);
                                                                            Long postCount = user.getPosts();

                                                                            userDbRef.child(uid).child("posts").setValue(postCount+1);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        //..
                                                                    }
                                                                });

                                                                Intent intent = new Intent(CategoryActivity.this, DashboardActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //..
                                        }
                                    } );

                                }
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                                if (progress - 15 > mPhotoUploadProgress)
                                {
                                    pd.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage("photo upload progress: "+ String.format("%.0f", progress)+"%");
                                    builder.show();
                                    mPhotoUploadProgress = progress;
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //post without image

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName", name);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");
            hashMap.put("pDescription", pDescription);
            hashMap.put("pImage", "noImage");
            hashMap.put("pCategory", selCatTv);
            hashMap.put("pTime", timeStamp);
            hashMap.put("uMood", currentMood);

            //Path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added to firebase database
                            pd.dismiss();
                            Toast.makeText(CategoryActivity.this, "Post Published", Toast.LENGTH_SHORT).show();

                            Query query = userDbRef.orderByKey().equalTo(uid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = ds.getValue(User.class);
                                        Long postCount = user.getPosts();

                                        userDbRef.child(uid).child("posts").setValue(postCount+1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });

                            Intent intent = new Intent(CategoryActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadDataAsAnonymous(final String currentMood, final String pDescription, String image_uri, final String selCatTv) {
        pd.setMessage("Publishing post...");
        pd.show();

        //for post- image name, post-id, post-publish-time
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String uniqueAnonymousId = generateID();

        String filePathAndName = "posts/" + "post_" + timeStamp;

        if (!image_uri.equals("noImage"))
        {
            //post Image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(image_uri));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] pictureData = baos.toByteArray(); //convert images to bytes

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                storageRef.putBytes(pictureData)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //image has been uploaded to firebase storage, now get its url
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());

                                String downloadUri = uriTask.getResult().toString();

                                if (uriTask.isSuccessful()){

                                    Query query1 = userDbRef
                                            .orderByKey()
                                            .equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() );

                                    //orderByKey method will look for the key encapsulating the values of the object

                                    query1.addListenerForSingleValueEvent( new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                                                User user = singleSnapshot.getValue(User.class);
                                                Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                                                //url is received upload post to firebase firestore

                                                Post post = new Post();
                                                post.setUid(uid);
                                                post.setuName("Anonymous"+uniqueAnonymousId);
                                                post.setuEmail(firebaseAuth.getCurrentUser().getEmail());
                                                post.setuDp("");
                                                post.setpId(timeStamp);
                                                post.setpLikes("0");
                                                post.setpComments("0");
                                                post.setpDescription(pDescription);
                                                post.setpImage(downloadUri);
                                                post.setpCategory(selCatTv);
                                                post.setpTime(timeStamp);
                                                post.setuMood(currentMood);

                                                //Path to store post data
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                                                //put data in this ref
                                                ref.child(timeStamp).setValue(post)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //added to firebase
                                                                pd.dismiss();

                                                                Query query = userDbRef.orderByKey().equalTo(uid);
                                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                            User user = ds.getValue(User.class);
                                                                            Long postCount = user.getPosts();

                                                                            userDbRef.child(uid).child("posts").setValue(postCount+1);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                        //..
                                                                    }
                                                                });

                                                                Intent intent = new Intent(CategoryActivity.this, DashboardActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //..
                                        }
                                    } );

                                }
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                                if (progress - 15 > mPhotoUploadProgress)
                                {
                                    pd.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setMessage("uploading picture: "+ String.format("%.0f", progress)+"%");
                                    builder.show();
                                    mPhotoUploadProgress = progress;
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //post without image

            HashMap<Object, String> hashMap = new HashMap<>();
            //put post info
            hashMap.put("uid", uid);
            hashMap.put("uName", "Anonymous"+uniqueAnonymousId);
            hashMap.put("uEmail", email);
            hashMap.put("uDp", "");
            hashMap.put("pId", timeStamp);
            hashMap.put("pLikes", "0");
            hashMap.put("pComments", "0");
            hashMap.put("pDescription", pDescription);
            hashMap.put("pImage", "noImage");
            hashMap.put("pCategory", selCatTv);
            hashMap.put("pTime", timeStamp);
            hashMap.put("uMood", currentMood);

            //Path to store post data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added to firebase database
                            pd.dismiss();
                            Toast.makeText(CategoryActivity.this, "Post Published", Toast.LENGTH_SHORT).show();

                            Query query = userDbRef.orderByKey().equalTo(uid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = ds.getValue(User.class);
                                        Long postCount = user.getPosts();

                                        userDbRef.child(uid).child("posts").setValue(postCount+1);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });

                            Intent intent = new Intent(CategoryActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(CategoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String generateID() {
        String keys = "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(4);

        for (int i = 0; i < 4; i++) {
            int index = (int)(keys.length() * Math.random());
            sb.append(keys.charAt(index));
        }

        return sb.toString();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            email = user.getEmail();
            uid = user.getUid();
        } else {
            //user not signed in else go to main activity
            startActivity(new Intent(CategoryActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
}