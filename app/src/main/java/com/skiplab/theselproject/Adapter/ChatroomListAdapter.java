package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatActivity;
import com.skiplab.theselproject.Consultation.WalletActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.User;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatroomListAdapter extends RecyclerView.Adapter<ChatroomListAdapter.ViewHolder>{

    private static final String TAG = "ChatroomListAdapter";

    Context context;
    List<ChatRoom> chatRoomList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    CollectionReference mChatroomReference;

    LocalDate todayDate;

    public ChatroomListAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mChatroomReference = FirebaseFirestore.getInstance().collection("chatrooms");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chatroom_listitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String chatroomID = chatRoomList.get(position).getChatroom_id();
        String clientUID = chatRoomList.get(position).getClient_id();
        String counsellorID = chatRoomList.get(position).getCounsellor_id();
        String timestamp = chatRoomList.get(position).getTimestamp();
        long chatroom_messages = chatRoomList.get(position).getNum_messages();
        String expiryDAY = chatRoomList.get(position).getExpiryDay();
        String expiryDATE = chatRoomList.get(position).getExpiryDate();

        //set the number of chat messages
        String chatMessagesString = chatroom_messages + " message(s)";

        //set number of chatroom messages
        holder.expiryDayTv.setText(expiryDAY+", ");
        holder.expiryDateTv.setText(expiryDATE);
        holder.numMessagesTv.setText(chatMessagesString);

        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        todayDate = today;
        String todayDateS = todayDate.toString();

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds :dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);

                            if (user.getIsStaff().equals("false"))
                            {
                                usersRef.orderByKey().equalTo(counsellorID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds :dataSnapshot.getChildren())
                                                {
                                                    User counsellor = ds.getValue(User.class);

                                                    //set counsellor name
                                                    holder.hisNameTv.setText(counsellor.getUsername());
                                                    holder.categoryTv.setText(counsellor.getCategory1());

                                                    //set counsellor dp
                                                    try {
                                                        UniversalImageLoader.setImage(counsellor.getProfile_photo(), holder.avaterIv, null, "");
                                                    }
                                                    catch (Exception e){
                                                        Log.d("ERROR: ", ""+e);
                                                    }

                                                    holder.chatBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (Common.isConnectedToTheInternet(context))
                                                            {
                                                                Intent intent = new Intent(context, ChatActivity.class);
                                                                intent.putExtra("hisUID", counsellorID);
                                                                intent.putExtra("chatroomID",chatroomID);
                                                                intent.putExtra("myName",counsellor.getUsername());
                                                                context.startActivity(intent);
                                                            }
                                                            else
                                                            {
                                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                        .setMessage("Please check your internet connection!")
                                                                        .create();
                                                                alertDialog.show();
                                                            }
                                                        }
                                                    });

                                                    if (LocalDate.parse(todayDateS).isAfter(LocalDate.parse(expiryDATE)))
                                                    {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Your Instant Session with "+counsellor.getUsername()+" has EXPIRED!");
                                                        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        builder.show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(context, "NOT EXPIRED", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });

                            }
                            else if (user.getIsStaff().equals("true"))
                            {
                                usersRef.orderByKey().equalTo(clientUID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds :dataSnapshot.getChildren())
                                                {
                                                    User client = ds.getValue(User.class);

                                                    //set counsellor name
                                                    holder.hisNameTv.setText(client.getUsername());
                                                    holder.categoryTv.setText(user.getCategory1());

                                                    //set counsellor dp
                                                    try {
                                                        UniversalImageLoader.setImage(client.getProfile_photo(), holder.avaterIv, null, "");
                                                    }
                                                    catch (Exception e){
                                                        Log.d("ERROR: ", ""+e);
                                                    }

                                                    holder.chatBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (Common.isConnectedToTheInternet(context))
                                                            {
                                                                Intent intent = new Intent(context, ChatActivity.class);
                                                                intent.putExtra("hisUID", clientUID);
                                                                intent.putExtra("chatroomID",chatroomID);
                                                                intent.putExtra("myName",client.getUsername());
                                                                context.startActivity(intent);
                                                            }
                                                            else
                                                            {
                                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                        .setMessage("Please check your internet connection!")
                                                                        .create();
                                                                alertDialog.show();
                                                            }
                                                        }
                                                    });

                                                    if (LocalDate.parse(todayDateS).isAfter(LocalDate.parse(expiryDATE)))
                                                    {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Your Instant Session with "+client.getUsername()+" has EXPIRED!");
                                                        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                        builder.show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(context, "NOT EXPIRED", Toast.LENGTH_SHORT).show();
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

        holder.iconTrashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("END SESSION");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChatroomReference.document(chatroomID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        context.startActivity(new Intent(context, DashboardActivity.class));
                                    }
                                });
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avaterIv, iconTrashIv;
        TextView categoryTv, expiryDateTv, expiryDayTv, hisNameTv, numMessagesTv;
        Button chatBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            avaterIv = itemView.findViewById(R.id.hisImage);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            iconTrashIv = itemView.findViewById(R.id.icon_trash);
            expiryDateTv = itemView.findViewById(R.id.expiry_date);
            expiryDayTv = itemView.findViewById(R.id.expiry_day);
            hisNameTv = itemView.findViewById(R.id.hisName);
            numMessagesTv = itemView.findViewById(R.id.number_chatmessages);
            chatBtn = itemView.findViewById(R.id.chat_btn);
        }
    }
}
