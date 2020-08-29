package com.skiplab.theselproject.AddPost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.skiplab.theselproject.Adapter.AdapterMood;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;

public class SelectMood extends AppCompatActivity {

    Context mContext = SelectMood.this;

    RecyclerView recyclerView;
    String[] moods = {"Happy","Sad","Frustrated","Exhausted","Nostalgic","Angry","Proud","Supportive","Optimistic","Tired","Lonely",
            "Down","Anxious","Overwhelmed","Depressed","Afraid","Confused","Nothing","Annoyed","Worried","Meh","Shocked","Loved","Embarrassed",
            "Amused","Positive","Relaxed","Ecstatic","Calm","Chilled","Thankful","Determined","Jealous","Irritated","Nervous","Disgust"};

    private ImageView closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mood);

        recyclerView = findViewById(R.id.recycler_moods);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new AdapterMood(mContext, moods));

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mContext, DashboardActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity( intent );
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent( mContext, DashboardActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity( intent );
        finish();
    }
}
