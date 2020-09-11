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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.Consultation.WalletActivity;
import com.skiplab.theselproject.EditConsultantProfile;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatMessage;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.Profile;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>{

    Context context;
    List<User> userList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    CollectionReference mChatroomReference, mProfileReference;
    DatabaseReference mMessageReference;

    APIService apiService;

    String adminUID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    int i = 0;

    public AdapterUser(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mChatroomReference = FirebaseFirestore.getInstance().collection("chatrooms");
        mProfileReference = FirebaseFirestore.getInstance().collection("profiles");
        mMessageReference = FirebaseDatabase.getInstance().getReference("chatroom_messages");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
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
        String username = userList.get(position).getUsername();
        String country = userList.get(position).getAddress();
        String category1 = userList.get(position).getCategory1();
        String category2 = userList.get(position).getCategory2();
        String category3 = userList.get(position).getCategory3();
        Long cost = userList.get(position).getCost();
        String userCost = String.valueOf(userList.get(position).getCost());


        if (userList.get(position).getOnlineStatus().equals("online"))
        {
            holder.onlineIv.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                usersRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    User user = ds.getValue(User.class);
                                    if (user.getIsStaff().equals("admin"))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("PICK ONE!");
                                        builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, EditConsultantProfile.class);
                                                intent.putExtra("hisUID",hisUID);
                                                context.startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                                progressDialog.setMessage("Deleting...");

                                                Query query = usersRef.orderByChild("uid").equalTo(hisUID);
                                                query.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren())
                                                        {
                                                            ds.getRef().removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(context,"Deleted successfully!",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });

                                        builder.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
                return false;
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
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                    usersRef.orderByKey().equalTo(hisUID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren())
                                    {
                                        User counsellor = ds.getValue(User.class);
                                        if (counsellor.getOnlineStatus().equals("offline"))
                                        {
                                            progressDialog.dismiss();

                                            AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                    .setMessage(counsellor.getUsername()+" is offline!")
                                                    .create();
                                            alertDialog.show();
                                        }
                                        else if (counsellor.getOnlineStatus().equals("deactivated"))
                                        {
                                            progressDialog.dismiss();

                                            AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                    .setMessage(counsellor.getUsername()+" is unavailable at the moment!")
                                                    .create();
                                            alertDialog.show();
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
                                                                    if (!(wallet >= 1500))
                                                                    {
                                                                        progressDialog.dismiss();

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
                                                                                            if(task.getResult().size() > 0)
                                                                                            {
                                                                                                progressDialog.dismiss();

                                                                                                AlertDialog.Builder alertDialog =new AlertDialog.Builder(context);
                                                                                                alertDialog.setCancelable(false);
                                                                                                alertDialog.setMessage("End your ongoing session before starting a new session!");
                                                                                                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        dialog.dismiss();

                                                                                                        context.startActivity(new Intent(context, ChatRoomsActivity.class));
                                                                                                    }
                                                                                                });
                                                                                                alertDialog.show();

                                                                                            } else {

                                                                                                mChatroomReference.whereEqualTo("counsellor_id",hisUID)
                                                                                                        .get()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                if (task.isSuccessful()){
                                                                                                                    if (task.getResult().size() > 0)
                                                                                                                    {
                                                                                                                        progressDialog.dismiss();

                                                                                                                        i++;

                                                                                                                        Handler handler = new Handler();
                                                                                                                        handler.postDelayed(new Runnable() {
                                                                                                                            @Override
                                                                                                                            public void run() {
                                                                                                                                Toast.makeText(context,counsellor.getUsername()+" is in an ongoing session.",Toast.LENGTH_SHORT).show();
                                                                                                                            }
                                                                                                                        },2000);
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
                                                                                                                        message.setMessage("Hi! I'm "+counsellor.getUsername()+". Welcome to Thesel.\n"+"Let me know what's bothering you.");
                                                                                                                        message.setTimestamp(timestamp);
                                                                                                                        message.setSender_id(hisUID);
                                                                                                                        message.setReceiver_id(mAuth.getUid());
                                                                                                                        message.setType("text");
                                                                                                                        message.setChatroom_id(chatroomId);

                                                                                                                        mProfileReference.document(hisUID).get()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                        if (task.getResult().exists())
                                                                                                                                        {
                                                                                                                                            long num_of_consultations = task.getResult().getLong("num_of_consultations");
                                                                                                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                                                                                                            hashMap.put("num_of_consultations",num_of_consultations+1);

                                                                                                                                            mProfileReference.document(hisUID).set(hashMap, SetOptions.merge());
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });

                                                                                                                        mChatroomReference.document(chatroomId).set(chatroom).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {

                                                                                                                                sendNotification(hisUID, client.getUsername(), "NEW CONSULTATION!!!");

                                                                                                                                sendAdminNotification(client.getUid(), client.getUsername(), "NEW CONSULTATION!!!", counsellor.getUsername());

                                                                                                                                progressDialog.dismiss();

                                                                                                                                mMessageReference.child(messageId).setValue(message);

                                                                                                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                                                                                                                alertDialog.setCancelable(false);
                                                                                                                                alertDialog.setMessage("New session with " + counsellor.getUsername()+"!");
                                                                                                                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                                                        dialog.dismiss();

                                                                                                                                        context.startActivity(new Intent(context, ChatRoomsActivity.class));
                                                                                                                                    }
                                                                                                                                });
                                                                                                                                alertDialog.show();




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
                            .setMessage("Please check your internet connection!")
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
                                //..
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

                FirebaseFirestore.getInstance().collection("profiles")
                        .document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot.exists())
                                {
                                    Profile profile = documentSnapshot.toObject(Profile.class);

                                    String desc = profile.getDescription();
                                    if (!desc.equals(""))
                                        descEt.setText(desc);
                                }
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

    private void sendAdminNotification(String clientUID, String client_name, String message, String consultant_name) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(clientUID, message+" from "+client_name, consultant_name, adminUID, R.mipmap.ic_launcher3);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //..
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    //Toast.makeText(context, "FAILED REQUEST!!!", Toast.LENGTH_LONG).show();
                                }
                            });


                }
            }
        });
    }

    private void sendNotification(String hisUID, String myName, String message) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(mAuth.getUid(), message, myName, hisUID, R.mipmap.ic_launcher3);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //..
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    //Toast.makeText(context, "FAILED REQUEST!!!", Toast.LENGTH_LONG).show();
                                }
                            });


                }
            }
        });
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