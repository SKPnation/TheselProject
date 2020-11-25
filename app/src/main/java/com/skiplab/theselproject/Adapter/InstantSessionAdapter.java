package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.skiplab.theselproject.Consultation.InstantChatActivity;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.InstantSession;
import com.skiplab.theselproject.models.User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class InstantSessionAdapter extends RecyclerView.Adapter<InstantSessionAdapter.ViewHolder>{

    private static final String TAG = "InstantSessionAdapter";

    Context context;
    List<InstantSession> instantSessionList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    CollectionReference mChatroomReference;

    LocalDate todayDate;

    SimpleDateFormat simpleDateFormat;

    public InstantSessionAdapter(Context context, List<InstantSession> instantSessionList) {
        this.context = context;
        this.instantSessionList = instantSessionList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mChatroomReference = FirebaseFirestore.getInstance().collection("instantSessions");
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instant_session_listitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String instantSessionID = instantSessionList.get(position).getSession_id();
        String clientUID = instantSessionList.get(position).getClient_id();
        String counsellorID = instantSessionList.get(position).getCounsellor_id();
        String timestamp = instantSessionList.get(position).getTimestamp();
        long num_messages = instantSessionList.get(position).getNum_messages();
        String expiryDAY = instantSessionList.get(position).getExpiryDay();
        long expiryDATE = instantSessionList.get(position).getExpiryDate();

        //set the number of chat messages
        String chatMessagesString = num_messages + " message(s)";

        //set number of chatroom messages
        holder.expiryDayTv.setText(expiryDAY+", "+simpleDateFormat.format(expiryDATE));
        //holder.expiryDateTv.setText(expiryDATE);
        holder.numMessagesTv.setText(chatMessagesString);

        Date date0 = new Date();
        Date date1 = new Date();
        date0.getTime();
        date1.setTime(expiryDATE);

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
                                                    holder.categoryTv.setText(counsellor.getCategory_one());

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
                                                                Intent intent = new Intent(context, InstantChatActivity.class);
                                                                intent.putExtra("hisUID", counsellorID);
                                                                intent.putExtra("chatroomID",instantSessionID);
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

                                                    if (date0.after(date1))
                                                    {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Your Instant Session with "+counsellor.getUsername()+" has EXPIRED!");
                                                        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();

                                                                mChatroomReference.document(instantSessionID).delete();

                                                                ((ChatRoomsActivity)context).finish();
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
                                                    holder.categoryTv.setText(user.getCategory_one());

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
                                                                Intent intent = new Intent(context, InstantChatActivity.class);
                                                                intent.putExtra("hisUID", clientUID);
                                                                intent.putExtra("chatroomID",instantSessionID);
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

                                                    if (date0.after(date1))
                                                    {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Your Instant Session with "+client.getUsername()+" has EXPIRED!");
                                                        builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();

                                                                mChatroomReference.document(instantSessionID).delete();

                                                                ((ChatRoomsActivity)context).finish();
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
                        mChatroomReference.document(instantSessionID).delete()
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
        return instantSessionList.size();
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
