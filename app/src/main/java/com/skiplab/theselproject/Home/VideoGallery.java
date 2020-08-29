package com.skiplab.theselproject.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.skiplab.theselproject.Adapter.AdapterVideo;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.models.Videos;

import java.util.ArrayList;
import java.util.List;

public class VideoGallery extends AppCompatActivity {

    private static final String TAG = "VideoGallery";

    Context mContext = VideoGallery.this;

    RecyclerView recyclerView;
    List<Videos> videosList;
    AdapterVideo adapterVideo;

    private ImageView closeBtn, addVideoBtn;
    private ProgressBar mProgressBar;

    FirebaseAuth mAuth;
    StorageReference storageRef;
    DatabaseReference usersRef, videosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        videosRef = FirebaseDatabase.getInstance().getReference("videos");

        recyclerView = findViewById(R.id.recycler_view);
        addVideoBtn = findViewById(R.id.addVideo);
        closeBtn = findViewById(R.id.closeBtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //Show latest video first, for the load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //init video list
        videosList = new ArrayList<>();

        /*mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);*/

        Query query = usersRef.orderByKey().equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getIsStaff().equals("admin"))
                    {
                        addVideoBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EditVideoActivity.class));
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadVideos();

    }

    private void loadVideos() {
        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videosList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Videos videos = ds.getValue(Videos.class);

                    videosList.add(videos);

                    adapterVideo = new AdapterVideo(mContext, videosList);
                    adapterVideo.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterVideo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlayer(adapterVideo.exoPlayer);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        stopPlayer(adapterVideo.exoPlayer);
    }

    public static void stopPlayer(SimpleExoPlayer exoPlayer) {

        if (exoPlayer != null) {
            exoPlayer.stop(true);

        }
    }
}
