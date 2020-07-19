package com.skiplab.theselproject.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterPosts;
import com.skiplab.theselproject.Adapter.AdapterRequests;
import com.skiplab.theselproject.Authentication.RegisterActivity;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Activity;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.Requests;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    Context mContext = RequestsActivity.this;

    private ImageView backBtn, call_request_Btn;
    private TextView hintText;
    private LinearLayout linearLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference requestsRef, usersRef;

    RecyclerView recyclerView;
    AdapterRequests adapterRequests;
    List<Requests> requestsList;
    String uid;
    String theselSupportPhone;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        final Intent intent = getIntent();
        uid = intent.getStringExtra("myUid");

        mProgressBar = findViewById(R.id.progressBar);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        requestsRef = database.getReference("requests");

        hintText = findViewById(R.id.hintText);
        backBtn = findViewById(R.id.backArrow);
        call_request_Btn = findViewById(R.id.phone_request_staff_Btn);
        linearLayout = findViewById(R.id.warning);

        getServerSupportPhone();

        call_request_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("Thesel Support")
                        .setPositiveButton("CALL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);

                                callIntent.setData(Uri.parse("tel:"+theselSupportPhone));

                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                                    Toast.makeText(mContext, "Please Grant Permission", Toast.LENGTH_SHORT).show();
                                    requestPermission();
                                }
                                else
                                {
                                    startActivity(callIntent);
                                }
                            }
                        });

                builder.show();

            }

            private void requestPermission(){
                ActivityCompat.requestPermissions(RequestsActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkUserStatus();

        recyclerView = findViewById(R.id.recyclerView);

        requestsList = new ArrayList<>();

        //linear layout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //show newest request first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //set to recyclerView
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout = findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToTheInternet(getBaseContext())){
                    retrieveRequests();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                    builder.setMessage("Please check your internet connection");
                    builder.show();
                }
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToTheInternet(getBaseContext())){
                    retrieveRequests();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                    builder.setMessage("Please check your internet connection");
                    builder.show();
                }
            }
        } );
    }

    private void getServerSupportPhone()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("theselSupport")
                .orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onDataChange: got the server key.");
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                theselSupportPhone = singleSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveRequests() {

        usersRef.orderByKey().equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);

                            if (user.getIsStaff().equals("false"))
                            {
                                linearLayout.setVisibility(View.VISIBLE);
                                Query query = requestsRef.orderByChild("client_id").equalTo(uid);
                                //get all data from this reference
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        requestsList.clear();
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            Requests requests = ds.getValue(Requests.class);

                                            //add to list

                                            if (ds.exists()){
                                                mProgressBar.setVisibility(View.GONE);

                                                requestsList.add(requests);
                                                hintText.setVisibility(View.GONE);
                                            }

                                            adapterRequests = new AdapterRequests(mContext, requestsList);
                                            adapterRequests.notifyDataSetChanged();
                                            recyclerView.setAdapter(adapterRequests);
                                            swipeRefreshLayout.setRefreshing( false );
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        //..
                                    }
                                });
                            }
                            else if (user.getIsStaff().equals("true"))
                            {
                                //Retrieve requests that have uids similar to that of the current user
                                Query query = requestsRef.orderByChild("counsellor_id").equalTo(uid);
                                //get all data from this reference
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        requestsList.clear();
                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                            Requests requests = ds.getValue(Requests.class);

                                            //add to list
                                            if (ds.exists()){
                                                mProgressBar.setVisibility(View.GONE);

                                                requestsList.add(requests);
                                                hintText.setVisibility(View.GONE);
                                            }


                                            adapterRequests = new AdapterRequests(mContext, requestsList);
                                            adapterRequests.notifyDataSetChanged();
                                            recyclerView.setAdapter(adapterRequests);
                                            swipeRefreshLayout.setRefreshing( false );
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
                        //...
                    }
                });
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
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
    }
    
}
