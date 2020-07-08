package com.skiplab.theselproject.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Post;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    Context mContext = MyPostsActivity.this;

    private ImageView backBtn;
    private SwipeRefreshLayout swipeRefreshLayout;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference postsRef;

    RecyclerView recyclerView;
    AdapterPosts adapterPosts;
    List<Post> postList;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        final Intent intent = getIntent();
        uid = intent.getStringExtra("myUid");

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        postsRef = database.getReference("posts");

        TextView hintText = findViewById(R.id.hintText);
        backBtn = findViewById(R.id.backArrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkUserStatus();

        recyclerView = findViewById(R.id.recycler_posts);

        postList = new ArrayList<>();

        //linear layout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        //show newest post first, for this load from last
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
                if(Common.isConnectedToTheInternet(getBaseContext()))
                {
                    //init posts list
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
                    //Retrieve posts that have uids similar to that of the current user
                    Query query = postRef.orderByChild("uid").equalTo(uid);
                    //get all data from this reference
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            postList.clear();
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                Post post = ds.getValue(Post.class);

                                if (ds.exists()){
                                    postList.add(post);
                                    hintText.setVisibility(View.GONE);
                                }

                                adapterPosts = new AdapterPosts(mContext, postList);
                                adapterPosts.notifyDataSetChanged();
                                recyclerView.setAdapter(adapterPosts);
                                swipeRefreshLayout.setRefreshing( false );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please check your internet connection");
                    builder.show();
                    return;
                }
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToTheInternet(getBaseContext()))
                {
                    //init posts list
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
                    //Retrieve posts that have uids similar to that of the current user
                    Query query = postRef.orderByChild("uid").equalTo(uid);
                    //get all data from this reference
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            postList.clear();
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                Post post = ds.getValue(Post.class);

                                if (ds.exists()){
                                    postList.add(post);
                                    hintText.setVisibility(View.GONE);
                                }

                                adapterPosts = new AdapterPosts(mContext, postList);
                                adapterPosts.notifyDataSetChanged();
                                recyclerView.setAdapter(adapterPosts);
                                swipeRefreshLayout.setRefreshing( false );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //...
                        }
                    });
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please check your internet connection");
                    builder.show();
                    return;
                }
            }
        } );
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
