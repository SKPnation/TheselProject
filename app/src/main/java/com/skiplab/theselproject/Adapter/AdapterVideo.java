package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Home.FullscreenActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Videos;
import com.skiplab.theselproject.notifications.APIService;

import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.VideoViewHolder>{

    Context context;
    List<Videos> videosList;

    public SimpleExoPlayer exoPlayer;

    boolean mProcessLike=false;
    boolean flag = false;


    String myUid;
    APIService apiService;

    private DatabaseReference likesRef, videosRef, usersRef;

    public AdapterVideo(Context context, List<Videos> videosList) {
        this.context = context;
        this.videosList = videosList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        videosRef = FirebaseDatabase.getInstance().getReference("videos");
        likesRef = FirebaseDatabase.getInstance().getReference("likes");
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_videos, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        final String uName = videosList.get(position).getUsername();
        final String search = videosList.get(position).getSearch();
        final String vId = videosList.get(position).getvId();
        final String videoName = videosList.get(position).getName();
        final String videoLikes = videosList.get(position).getvLikes();
        final String videoComments = videosList.get(position).getvComments();
        final String videoUrl = videosList.get(position).getVideourl();

        setLikes(holder, vId);

        holder.vidNameTv.setText(videoName);
        holder.likesdisplay.setText(videoLikes);

        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("videoId", vId);
                intent.putExtra("videoUrl", videoUrl);
                intent.putExtra("videoName", videoName);
                context.startActivity(intent);
            }
        });

        holder.exo_player_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("videoId", vId);
                intent.putExtra("videoUrl", videoUrl);
                intent.putExtra("videoName", videoName);
                context.startActivity(intent);
            }
        });


        try {
            Uri video = Uri.parse(videoUrl);
            //Initilaize load control
            LoadControl loadControl = new DefaultLoadControl();
            //Intiliaze bandwidth meter
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            //Initialize track selector
            TrackSelector trackSelector = new DefaultTrackSelector(
                    new AdaptiveTrackSelection.Factory(bandwidthMeter)
            );
            //Initialize simple exo player
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    context,trackSelector,loadControl
            );
            //Initialize data source factory
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                    "exoplayer_video"
            );
            //Initialize extractors factory
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //Initialize media source
            MediaSource mediaSource = new ExtractorMediaSource(
                    video,
                    dataSourceFactory,
                    extractorsFactory,
                    null,
                    null);
            //Set player
            holder.playerView.setPlayer(exoPlayer);
            //Keep screen on
            holder.playerView.setKeepScreenOn(true);
            //Prepare media
            exoPlayer.prepare(mediaSource);
            //Play video when ready
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.setVolume(0f);
            exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    //Check condition
                    if (playbackState == Player.STATE_BUFFERING){
                        //When buffering
                        // show progress bar
                        holder.mProgressBar.setVisibility(View.VISIBLE);
                    }
                    else if (playbackState == Player.STATE_READY){
                        //When ready
                        // hide progress bar
                        holder.mProgressBar.setVisibility(View.GONE);
                        holder.btn_play.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }
            });

        }catch (Exception e){
            Log.e("ViewHolder","exoplayer error"+e.toString());
        }

        holder.mHeartWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int vLikes = Integer.parseInt(videosList.get(position).getvLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String videoIde = videosList.get(position).getvId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(videoIde).hasChild(myUid)){
                                //..
                            }
                            else {
                                //not liked, like it
                                videosRef.child(videoIde).child("vLikes").setValue( ""+(vLikes+1));
                                likesRef.child(videoIde).child(myUid).setValue("Liked");
                                mProcessLike=false;

                                try {
                                    Uri video = Uri.parse(videoUrl);
                                    //Initilaize load control
                                    LoadControl loadControl = new DefaultLoadControl();
                                    //Intiliaze bandwidth meter
                                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                                    //Initialize track selector
                                    TrackSelector trackSelector = new DefaultTrackSelector(
                                            new AdaptiveTrackSelection.Factory(bandwidthMeter)
                                    );
                                    //Initialize simple exo player
                                    exoPlayer = ExoPlayerFactory.newSimpleInstance(
                                            context,trackSelector,loadControl
                                    );
                                    //Initialize data source factory
                                    DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                                            "exoplayer_video"
                                    );
                                    //Initialize extractors factory
                                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                    //Initialize media source
                                    MediaSource mediaSource = new ExtractorMediaSource(
                                            video,
                                            dataSourceFactory,
                                            extractorsFactory,
                                            null,
                                            null);
                                    //Set player
                                    holder.playerView.setPlayer(exoPlayer);
                                    //Keep screen on
                                    holder.playerView.setKeepScreenOn(true);
                                    //Prepare media
                                    exoPlayer.prepare(mediaSource);
                                    //Play video when ready
                                    exoPlayer.setPlayWhenReady(true);
                                    exoPlayer.setVolume(0f);
                                    exoPlayer.addListener(new Player.EventListener() {
                                        @Override
                                        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                                        }

                                        @Override
                                        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                                        }

                                        @Override
                                        public void onLoadingChanged(boolean isLoading) {

                                        }

                                        @Override
                                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                            //Check condition
                                            if (playbackState == Player.STATE_BUFFERING){
                                                //When buffering
                                                // show progress bar
                                                holder.mProgressBar.setVisibility(View.VISIBLE);
                                            }
                                            else if (playbackState == Player.STATE_READY){
                                                //When ready
                                                // hide progress bar
                                                holder.mProgressBar.setVisibility(View.GONE);
                                                holder.btn_play.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onRepeatModeChanged(int repeatMode) {

                                        }

                                        @Override
                                        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                                        }
                                    });

                                }catch (Exception e){
                                    Log.e("ViewHolder","exoplayer error"+e.toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });
            }
        });

    }

    private void setLikes(VideoViewHolder holder, String videoKey)
    {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(videoKey).hasChild(myUid)){
                    //user has like this post
                    /*To indicate this post is liked by this (signed in)  user
                     * Chang drawable left icon of like button
                     * Change text of like button from "Like" to "Liked"*/
                    holder.mHeartWhite.setImageResource(R.drawable.ic_liked);
                }
                else {
                    holder.mHeartWhite.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        PlayerView playerView;
        ImageView btn_fullscreen, btn_play, mHeartWhite, mHeartRed ;
        TextView vidNameTv, likesdisplay;
        ProgressBar mProgressBar;
        //int likescount;
        //DatabaseReference likesref;
        RelativeLayout exo_player_layout;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            vidNameTv = itemView.findViewById(R.id.tv_video_name);
            likesdisplay = itemView.findViewById(R.id.likes_textview);
            playerView = itemView.findViewById(R.id.exoplayer_item);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
            mHeartRed = (ImageView) itemView.findViewById(R.id.image_heart_red);
            mHeartWhite = itemView.findViewById(R.id.image_heart);
            btn_play = itemView.findViewById(R.id.play_btn);
            exo_player_layout = itemView.findViewById(R.id.exo_player_layout);
            //btn_fullscreen = itemView.findViewById(R.id.bt_fullscreen);
        }
    }
}
