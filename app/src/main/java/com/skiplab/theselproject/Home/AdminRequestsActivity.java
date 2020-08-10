package com.skiplab.theselproject.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterRequests;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Profile.RequestsActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Requests;

import java.util.ArrayList;
import java.util.List;

public class AdminRequestsActivity extends AppCompatActivity {

    private static final String TAG = "RequestsActivity";

    Context mContext = AdminRequestsActivity.this;

    private ImageView backBtn;
    private TextView hintText;

    SwipeRefreshLayout swipeRefreshLayout;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference requestsRef;

    RecyclerView recyclerView;
    AdapterRequests adapterRequests;
    List<Requests> requestsList;

    private ProgressBar mProgressBar;

    private FirebaseAuth.AuthStateListener mAuthListener;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests);

        setupFirebaseAuth();


        mProgressBar = findViewById(R.id.progressBar);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("requests");

        hintText = findViewById(R.id.hintText);
        backBtn = findViewById(R.id.backArrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminRequestsActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminRequestsActivity.this);
                    builder.setMessage("Please check your internet connection");
                    builder.show();
                }
            }
        } );
    }

    private void retrieveRequests() {
        requestsRef.addValueEventListener(new ValueEventListener() {
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

            }
        });
    }

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }
}
