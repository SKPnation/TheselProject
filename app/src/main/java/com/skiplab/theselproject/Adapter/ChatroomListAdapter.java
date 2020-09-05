package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Consultation.ChatActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.User;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatroomListAdapter extends RecyclerView.Adapter<ChatroomListAdapter.ViewHolder>{

    Context context;
    List<ChatRoom> chatRoomList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    public ChatroomListAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
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
        String clientUID = chatRoomList.get(position).getCreator_id();
        String clientDp = chatRoomList.get(position).getCreator_dp();
        String clientName = chatRoomList.get(position).getCreator_name();
        String counsellorID = chatRoomList.get(position).getCounsellor_id();
        String timestamp = chatRoomList.get(position).getTimestamp();
        int chatroom_messages = chatRoomList.get(position).getNum_messages();

        //set the number of chat messages
        String chatMessagesString = chatroom_messages + " messages";

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar).toString();

        //set number of chatroom messages
        holder.dateTv.setText(dateTime);
        holder.numMessagesTv.setText(chatMessagesString);

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds :dataSnapshot.getChildren())
                        {
                            User client = ds.getValue(User.class);
                            if (client.getIsStaff().equals("false"))
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

                                                    //set counsellor dp
                                                    try {
                                                        UniversalImageLoader.setImage(counsellor.getProfile_photo(), holder.avaterIv, null, "");
                                                    }
                                                    catch (Exception e){
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.putExtra("hisUID", counsellorID);
                                        intent.putExtra("chatroomID",chatroomID);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                            else if (client.getIsStaff().equals("true"))
                            {
                                //set client name
                                holder.hisNameTv.setText(clientName);

                                //set client dp
                                try {
                                    UniversalImageLoader.setImage(clientDp, holder.avaterIv, null, "");
                                }
                                catch (Exception e){
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView avaterIv, iconTrashIv;
        TextView dateTv, hisNameTv, numMessagesTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            avaterIv = itemView.findViewById(R.id.avatarIv);
            iconTrashIv = itemView.findViewById(R.id.icon_trash);
            dateTv = itemView.findViewById(R.id.consultation_date);
            hisNameTv = itemView.findViewById(R.id.his_name);
            numMessagesTv = itemView.findViewById(R.id.number_chatmessages);
        }
    }
}
