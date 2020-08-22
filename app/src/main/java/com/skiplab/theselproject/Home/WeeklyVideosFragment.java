package com.skiplab.theselproject.Home;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.skiplab.theselproject.Adapter.AdapterConsultant;
import com.skiplab.theselproject.Adapter.AdapterVideo;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.models.Videos;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklyVideosFragment extends Fragment {

    private static final String TAG = "WeeklyVideosFragment";

    RecyclerView recyclerView;
    List<Videos> videosList;
    AdapterVideo adapterVideo;

    private ImageView backBtn, searchVideoBtn, addVideoBtn;
    private ProgressBar mProgressBar;

    FirebaseAuth mAuth;
    StorageReference storageRef;
    DatabaseReference usersRef, videosRef;

    public WeeklyVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_videos, container, false);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        videosRef = FirebaseDatabase.getInstance().getReference("videos");

        recyclerView = view.findViewById(R.id.recycler_view);
        backBtn = view.findViewById(R.id.backArrow);
        addVideoBtn = view.findViewById(R.id.addVideo);
        searchVideoBtn = view.findViewById(R.id.searchVideo);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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

        searchVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //..
            }
        });

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditVideoActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DashboardActivity.class));
                getActivity().finish();
            }
        });

        loadVideos();

        return view;
    }

    private void loadVideos() {

        videosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                videosList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Videos videos = ds.getValue(Videos.class);

                    videosList.add(videos);

                    adapterVideo = new AdapterVideo(getActivity(), videosList);
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
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                    getActivity().finish();

                    pausePlayer(adapterVideo.exoPlayer);

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        pausePlayer(adapterVideo.exoPlayer);
    }

    public static void pausePlayer(SimpleExoPlayer exoPlayer) {

        if (exoPlayer != null) {
            exoPlayer.stop(true);

        }
    }

}
