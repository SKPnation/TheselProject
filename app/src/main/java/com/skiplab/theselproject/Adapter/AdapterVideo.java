package com.skiplab.theselproject.Adapter;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Videos;
import com.skiplab.theselproject.notifications.APIService;

import java.util.List;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.VideoViewHolder>{

    Context context;
    List<Videos> videosList;

    SimpleExoPlayer exoPlayer;

    boolean mProcessLike=false;

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

        holder.vidNameTv.setText(videoName);
        holder.likesdisplay.setText(videoLikes);

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(context);
            Uri video = Uri.parse(videoUrl);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video,dataSourceFactory,extractorsFactory,null,null);
            holder.playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);

        }catch (Exception e){
            Log.e("ViewHolder","exoplayer error"+e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        PlayerView playerView;
        //ImageButton likebutton,commentbtn,downloadbtn;
        TextView vidNameTv, likesdisplay;
        //int likescount;
        //DatabaseReference likesref;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            vidNameTv = itemView.findViewById(R.id.tv_video_name);
            likesdisplay = itemView.findViewById(R.id.likes_textview);
            playerView = itemView.findViewById(R.id.exoplayer_item);
        }
    }
}
