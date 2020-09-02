package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterUser;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

public class ConsultantsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    Context mContext = ConsultantsActivity.this;

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<User> userList = new ArrayList<>();

    //firebase auth
    FirebaseAuth firebaseAuth;

    FirebaseDatabase db;
    DatabaseReference usersRef, walletRef;

    private ProgressBar mProgressBar;
    ImageView closeBtn;

    String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultants);

        Intent intent = getIntent();
        selectedItem = intent.getStringExtra("selectedItem");

        mProgressBar = findViewById(R.id.progressBar);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation( recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation( controller );

        loadConsultants();
    }

    private void loadConsultants()
    {
        Query queryUsers = usersRef.orderByChild("category1").equalTo(selectedItem);
        queryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    mProgressBar.setVisibility(View.GONE);

                    userList.add(user);
                }
                adapterUser = new AdapterUser(mContext, userList);
                recyclerView.setAdapter(adapterUser);
                //swipeRefreshLayout.setRefreshing( false );
                //Animation
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });

    }
}
