package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.Adapter.ChatroomListAdapter;
import com.skiplab.theselproject.Authentication.LoginActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatRoomsActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomsActivity";

    Context mContext = ChatRoomsActivity.this;

    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    CollectionReference  chatroomRef;

    RecyclerView recyclerView;
    ChatroomListAdapter chatroomAdapter;
    List<ChatRoom> chatroomList;

    ProgressBar mProgressBar;

    TextView hintText, titleInstantSession;
    private ImageView closeBtn, sessionCountIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        chatroomRef = FirebaseFirestore.getInstance().collection("chatrooms");

        mProgressBar = findViewById(R.id.progressBar);

        hintText = findViewById(R.id.hintText);
        closeBtn = findViewById(R.id.closeBtn);
        sessionCountIv = findViewById(R.id.session_count);
        titleInstantSession = findViewById(R.id.title_instant_session);

        chatroomList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //Show latest video first, for the load from last
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            if (user.getIsStaff().equals("false"))
                            {
                                titleInstantSession.setText("Instant Session");

                                loadClientSession();

                            }
                            else if (user.getIsStaff().equals("true"))
                            {
                                titleInstantSession.setText("Instant Sessions");

                                loadCounsultantSessions();

                                sessionCountIv.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(mContext, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
            }
        });

    }

    private void loadCounsultantSessions() {
        chatroomRef.whereEqualTo("counsellor_id",mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if(task.getResult().size() > 0) {

                                int number_sessions = task.getResult().size();

                                TextDrawable textDrawable = TextDrawable.builder()
                                        .buildRound(""+number_sessions, Color.BLACK);
                                sessionCountIv.setImageDrawable(textDrawable);

                                task.getResult().getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        chatroomList.clear();
                                        for (DocumentSnapshot ds: queryDocumentSnapshots.getDocuments() )
                                        {
                                            ChatRoom chatRoom = ds.toObject(ChatRoom.class);

                                            mProgressBar.setVisibility(View.GONE);

                                            chatroomList.add(chatRoom);

                                        }
                                        chatroomAdapter = new ChatroomListAdapter(mContext, chatroomList);
                                        recyclerView.setAdapter(chatroomAdapter);
                                        chatroomAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            else
                            {
                                mProgressBar.setVisibility(View.GONE);
                                hintText.setText("No Instant Sessions!");
                            }
                        }
                    }
                });
    }

    private void loadClientSession() {
        chatroomRef.whereEqualTo("client_id",mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if(task.getResult().size() > 0) {
                                task.getResult().getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        chatroomList.clear();
                                        for (DocumentSnapshot ds: queryDocumentSnapshots.getDocuments() )
                                        {
                                            ChatRoom chatRoom = ds.toObject(ChatRoom.class);

                                            mProgressBar.setVisibility(View.GONE);

                                            chatroomList.add(chatRoom);

                                        }
                                        chatroomAdapter = new ChatroomListAdapter(mContext, chatroomList);
                                        recyclerView.setAdapter(chatroomAdapter);
                                        chatroomAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            else
                            {
                                mProgressBar.setVisibility(View.GONE);
                                hintText.setText("No Instant Session!");
                            }


                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*Intent intent = new Intent(mContext, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        finish();
    }
}
