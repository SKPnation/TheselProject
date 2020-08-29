package com.skiplab.theselproject.Settings;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterPosts;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Post;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView post_count, closeBtn;
    private TextView hintText;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference usersRef, postsRef;

    RecyclerView recyclerView;
    AdapterPosts adapterPosts;
    List<Post> postList;
    String myUid;


    public MyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        postsRef = database.getReference("posts");
        postsRef.keepSynced(true);

        checkUserStatus();

        hintText = view.findViewById(R.id.hintText);
        post_count = view.findViewById(R.id.post_count);
        closeBtn = view.findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_posts);

        //linear layout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //show newest post first, for this load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //set to recyclerView
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();

        swipeRefreshLayout = view.findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //loadPosts();
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {
                //loadPosts();
            }
        } );

        return view;
    }

/*
    private void loadPosts(){

        usersRef.orderByKey().equalTo(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            TextDrawable textDrawable = TextDrawable.builder()
                                    .buildRound(""+user.getPosts(), Color.BLACK);
                            post_count.setImageDrawable(textDrawable);

                            //init posts list
                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts");
                            //Retrieve posts that have uids similar to that of the current user
                            Query query = postRef.orderByChild("uid").equalTo(myUid);
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

                                        adapterPosts = new AdapterPosts(getActivity(), postList);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
*/

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //mProfileTv.setText(user.getEmail());
            myUid = user.getUid();
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }
}
