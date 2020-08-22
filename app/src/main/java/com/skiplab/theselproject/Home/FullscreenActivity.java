package com.skiplab.theselproject.Home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.skiplab.theselproject.R;

public class FullscreenActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer exoPlayer;

    ProgressBar mProgressBar;
    ImageView btn_landscape;
    TextView video_tv_name;

    String videoId, videoUrl, videoName;
    boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Intent intent = getIntent();
        videoId = intent.getStringExtra("videoId");
        videoUrl = intent.getStringExtra("videoUrl");
        videoName = intent.getStringExtra("videoName");

        mProgressBar = findViewById(R.id.progress_bar);
        playerView = findViewById(R.id.exoplayer_fullscreen);
        btn_landscape = findViewById(R.id.bt_landscape);
        video_tv_name = findViewById(R.id.tv_fullscreen);

        video_tv_name.setText(videoName);

        btn_landscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen)
                {
                    btn_landscape.setImageDrawable(ContextCompat.getDrawable(FullscreenActivity.this,R.drawable.ic_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
                    video_tv_name.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_landscape.setImageDrawable(ContextCompat.getDrawable(FullscreenActivity.this,R.drawable.ic_fullscreen_exit));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen = true;
                    video_tv_name.setVisibility(View.INVISIBLE);
                }
            }
        });

        initiailizePlayer();
    }

    private void initiailizePlayer() {
        try
        {
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
                    this,trackSelector,loadControl
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
            playerView.setPlayer(exoPlayer);
            //Keep screen on
            playerView.setKeepScreenOn(true);
            //Prepare media
            exoPlayer.prepare(mediaSource);
            //Play video when ready
            exoPlayer.setPlayWhenReady(true);
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
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    else if (playbackState == Player.STATE_READY){
                        //When ready
                        // hide progress bar
                        mProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }
            });

        }catch (Exception e)
        {
            Log.e("ViewHolder","exoplayer error"+e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pausePlayer(exoPlayer);
    }


    @Override
    public void onStop() {
        super.onStop();

        pausePlayer(exoPlayer);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        stopPlayer(exoPlayer);
        finish();
    }


    public static void pausePlayer(SimpleExoPlayer exoPlayer) {

        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public static void stopPlayer(SimpleExoPlayer exoPlayer) {

        if (exoPlayer != null) {
            exoPlayer.stop(true);
        }
    }
}
