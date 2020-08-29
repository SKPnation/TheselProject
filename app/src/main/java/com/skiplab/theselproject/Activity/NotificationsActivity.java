package com.skiplab.theselproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterNotification;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Activity;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    Context mContext = NotificationsActivity.this;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView notificationsRecycler;

    private FirebaseAuth firebaseAuth;

    private ArrayList<Activity> notificationsList;
    AdapterNotification adapterNotification;

    private TextView hintText;
    private ImageView closeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsRecycler = findViewById(R.id.notificationsRecycler);
        hintText = findViewById(R.id.hintText);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(v -> finish());

        swipeRefreshLayout = findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllNotifications();
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {

                getAllNotifications();
            }
        } );
    }

    private void getAllNotifications()
    {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //Show latest post first, for the load from last
        notificationsRecycler.setLayoutManager(linearLayoutManager);

        notificationsList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notifications");
        Query query = ref.child(firebaseAuth.getUid()).orderByChild("pUid").equalTo(firebaseAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Activity model = ds.getValue(Activity.class);

                    if (!model.getsUid().equals(firebaseAuth.getUid())){
                        if (ds.exists()){
                            notificationsList.add(model);
                            hintText.setVisibility(View.GONE);
                        }
                    }


                    adapterNotification = new AdapterNotification(mContext, notificationsList);
                    adapterNotification.notifyDataSetChanged();
                    notificationsRecycler.setAdapter(adapterNotification);
                    swipeRefreshLayout.setRefreshing( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
