package com.skiplab.theselproject.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.Adapter.AdapterNotification;
import com.skiplab.theselproject.Adapter.ChatroomListAdapter;
import com.skiplab.theselproject.Adapter.MyNotificationAdapter;
import com.skiplab.theselproject.Interface.INotificationLoadListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.MyNotifications;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ConsultationNotificationActivity extends AppCompatActivity {

    public static final int MAX_NOTIFICATION_PER_LOAD = 10;

    Context mContext = ConsultationNotificationActivity.this;

    FirebaseAuth mAuth;

    CollectionReference notificationsDb;

    //INotificationLoadListener iNotificationLoadListener;

    RecyclerView recycler_notification;

    int total_item = 0, last_visible_item;
    boolean isLoading = false, isMaxData = false;
    //DocumentSnapshot finalDoc;

    MyNotificationAdapter adapter;
    List<MyNotifications> firstList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_notification);

        mAuth = FirebaseAuth.getInstance();
        notificationsDb = FirebaseFirestore.getInstance().collection("myNotifications");

        recycler_notification = findViewById(R.id.recycler_notification);
        adapter = new MyNotificationAdapter(this,firstList);

        recycler_notification.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recycler_notification.setLayoutManager(layoutManager);
        recycler_notification.addItemDecoration(new DividerItemDecoration(this,layoutManager.getOrientation()));

        notificationsDb.whereEqualTo("counsellor_id",mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().size() > 0)
                        {
                            task.getResult().getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    firstList.clear();
                                    for (DocumentSnapshot ds: documentSnapshots.getDocuments() )
                                    {
                                        MyNotifications myNotifications = ds.toObject(MyNotifications.class);

                                        //mProgressBar.setVisibility(View.GONE);

                                        firstList.add(myNotifications);

                                    }
                                    adapter = new MyNotificationAdapter(mContext, firstList);
                                    recycler_notification.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
    }


}

