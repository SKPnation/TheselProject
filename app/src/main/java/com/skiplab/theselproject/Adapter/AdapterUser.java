package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatActivity;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.Consultation.WalletActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatMessage;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.models.Wallet;
import com.skiplab.theselproject.notifications.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>{

    Context context;
    List<User> userList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    CollectionReference mChatroomReference;
    DatabaseReference mMessageReference;

    int i = 0;

    public AdapterUser(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mChatroomReference = FirebaseFirestore.getInstance().collection("chatrooms");
        mMessageReference = FirebaseDatabase.getInstance().getReference("chatroom_messages");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String hisUID = userList.get(position).getUid();
        String email = userList.get(position).getEmail();
        String username = userList.get(position).getUsername();
        String country = userList.get(position).getAddress();
        String category1 = userList.get(position).getCategory1();
        String category2 = userList.get(position).getCategory2();
        String category3 = userList.get(position).getCategory3();
        Long cost = userList.get(position).getCost();
        String userCost = String.valueOf(userList.get(position).getCost());


        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            if (user.getOnlineStatus().equals("online"))
                                holder.onlineIv.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //get Data
        holder.usernameTv.setText(username);
        holder.usernameTv.setAllCaps(true);
        holder.countryTv.setText("["+country+"]");
        holder.categoryTv1.setText(category1);
        holder.categoryTv2.setText(category2);
        holder.categoryTv3.setText(category3);

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToTheInternet(context))
                {
                    usersRef.orderByKey().equalTo(hisUID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren())
                                    {
                                        User counsellor = ds.getValue(User.class);
                                        if (counsellor.getOnlineStatus().equals("offline"))
                                        {
                                            //...

                                        }
                                        else if (counsellor.getOnlineStatus().equals("deactivated"))
                                        {
                                            //..
                                        }
                                        else
                                        {
                                            usersRef.orderByKey().equalTo(mAuth.getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot ds: dataSnapshot.getChildren())
                                                            {
                                                                User client = ds.getValue(User.class);
                                                                if (client.getIsStaff().equals("false"))
                                                                {
                                                                    int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                                                    if (!(wallet >= 3000))
                                                                    {
                                                                        AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                .setMessage("Your wallet is empty!!!")
                                                                                .create();
                                                                        alertDialog.show();

                                                                        i++;

                                                                        Handler handler = new Handler();
                                                                        handler.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                alertDialog.dismiss();
                                                                                context.startActivity(new Intent(context, WalletActivity.class));

                                                                            }
                                                                        },2000);
                                                                    }
                                                                    else
                                                                    {
                                                                        mChatroomReference.whereEqualTo("creator_id", mAuth.getUid())
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            if(task.getResult().size() > 0) {
                                                                                                Toast.makeText(context,"exist",Toast.LENGTH_SHORT).show();

                                                                                                /*for (DocumentSnapshot document : task.getResult()) {
                                                                                                    Log.d(FTAG, "Room already exists, start the chat");

                                                                                                }*/
                                                                                            } else {
                                                                                                mChatroomReference.whereEqualTo("counsellor_id",hisUID)
                                                                                                        .get()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                if (task.isSuccessful()){
                                                                                                                    if (task.getResult().size() > 0)
                                                                                                                    {
                                                                                                                        Toast.makeText(context,counsellor.getUsername()+" is currently in a session with a client.",Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                    else
                                                                                                                    {
                                                                                                                        String timestamp = String.valueOf(System.currentTimeMillis());

                                                                                                                        String chatroomId = UUID.randomUUID().toString();

                                                                                                                        ChatRoom chatroom = new ChatRoom();
                                                                                                                        chatroom.setChatroom_id(chatroomId);
                                                                                                                        chatroom.setCreator_id(mAuth.getUid());
                                                                                                                        chatroom.setCreator_name(client.getUsername());
                                                                                                                        chatroom.setCreator_dp(client.getProfile_photo());
                                                                                                                        chatroom.setCounsellor_id(hisUID);
                                                                                                                        chatroom.setTimestamp(timestamp);
                                                                                                                        chatroom.setNum_messages(1);

                                                                                                                        //create a unique id for the message
                                                                                                                        String messageId = mMessageReference.push().getKey();

                                                                                                                        ChatMessage message = new ChatMessage();
                                                                                                                        message.setMessage("Welcome to the new chatroom! \n"+"Get everything off your mind");
                                                                                                                        message.setTimestamp(timestamp);
                                                                                                                        message.setSender_id(hisUID);
                                                                                                                        message.setReceiver_id(mAuth.getUid());
                                                                                                                        message.setType("text");
                                                                                                                        message.setChatroom_id(chatroomId);

                                                                                                                        mChatroomReference.document(chatroomId).set(chatroom).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                mMessageReference.child(messageId).setValue(message);

                                                                                                                            }
                                                                                                                        });

                                                                                                                        Toast.makeText(context, "New session with " + counsellor.getUsername(), Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        Toast.makeText(context,"Failed Query: ERROR: "+e,Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });

                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(context,"Failed Query: ERROR: "+e,Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });


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
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });
                }
                else {
                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                            .setMessage("Please check your internet connection")
                            .create();
                    alertDialog.show();
                }
            }
        });

        holder.timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.counselling_hours_layout, null);

                TextView daytimeTv= mView.findViewById(R.id.dayTime);
                TextView nighttimeTv = mView.findViewById(R.id.nightTime);

                usersRef.orderByKey().equalTo(hisUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    User user = ds.getValue(User.class);
                                    daytimeTv.setText(user.getDayTime());
                                    nighttimeTv.setText(user.getNightTime());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                builder.setView(mView);
                builder.show();
            }
        });

        holder.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_profile, null);

                TextView nameTv = mView.findViewById(R.id.counsellor_name);
                EditText descEt = mView.findViewById(R.id.counsellor_desc);
                TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                nameTv.setText(username);
                if (!category1.isEmpty()){
                    catTv1.setVisibility(View.VISIBLE);
                    catTv1.setText(category1);
                }
                if (!category2.isEmpty()){
                    catTv2.setVisibility(View.VISIBLE);
                    catTv2.setText(category2);
                }
                if (!category3.isEmpty()){
                    catTv3.setVisibility(View.VISIBLE);
                    catTv3.setText(category3);
                }

                FirebaseDatabase.getInstance().getReference("profiles").orderByKey().equalTo(hisUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    if (ds.exists()){
                                        String desc = ds.child("description").getValue().toString();
                                        if (!desc.equals(""))
                                            descEt.setText(desc);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                descEt.setFocusable(false);
                descEt.setFocusableInTouchMode(false);

                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        progressDialog.dismiss();
                    }
                });

                builder.setView(mView);
                builder.show();
            }
        });

        try {
            UniversalImageLoader.setImage(userList.get(position).getProfile_photo(), holder.mAvatarIv, null, "");
        }
        catch (Exception e){
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv, onlineIv, offlineIv;
        ImageButton profileBtn, timeBtn, chatBtn;
        TextView usernameTv, countryTv, categoryTv1, categoryTv2, categoryTv3, mCostTv;
        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            offlineIv = itemView.findViewById(R.id.offlineIv);
            usernameTv = itemView.findViewById(R.id.nameTv);
            countryTv = itemView.findViewById(R.id.countryTv);
            categoryTv1 = itemView.findViewById(R.id.categoryTv1);
            categoryTv2 = itemView.findViewById(R.id.categoryTv2);
            categoryTv3 = itemView.findViewById(R.id.categoryTv3);
            profileBtn = itemView.findViewById(R.id.profileBtn);
            timeBtn = itemView.findViewById(R.id.timeBtn);
            chatBtn = itemView.findViewById(R.id.chatBtn);
            mCostTv = itemView.findViewById(R.id.costTv);
        }
    }
}
