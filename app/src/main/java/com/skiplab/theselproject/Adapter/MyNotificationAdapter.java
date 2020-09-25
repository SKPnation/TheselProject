package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

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
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.MyNotifications;
import com.skiplab.theselproject.models.User;

import java.util.List;

import javax.annotation.Nullable;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyViewHolder> {

    Context context;
    List<MyNotifications> myNotificationsList;

    FirebaseAuth mAuth;

    DatabaseReference usersRef;
    CollectionReference myNotificationsDb;

    public MyNotificationAdapter(Context context, List<MyNotifications> myNotificationsList) {
        this.context = context;
        this.myNotificationsList = myNotificationsList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        myNotificationsDb = FirebaseFirestore.getInstance().collection("myNotifications");

    }

    @NonNull
    @Override
    public MyNotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNotificationAdapter.MyViewHolder holder, int position) {

        holder.title_tv.setText(myNotificationsList.get(position).getTitle());
        holder.body_tv.setText(myNotificationsList.get(position).getContent());

        if (myNotificationsList.get(position).getAppointment_time().isEmpty())
            holder.date_time_tv.setText(myNotificationsList.get(position).getExpiry_date());
        else
            holder.date_time_tv.setText(myNotificationsList.get(position).getAppointment_time());

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            if (ds.getValue(User.class).getIsStaff().equals("true"))
                            {
                                usersRef.orderByKey().equalTo(myNotificationsList.get(position).getClient_id())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds1: dataSnapshot.getChildren())
                                                {
                                                    try {
                                                        UniversalImageLoader.setImage(ds1.getValue(User.class).getProfile_photo(), holder.avatarIv, null, "");
                                                    }
                                                    catch (Exception e){
                                                        //..
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });
                            }
                            else
                            {
                                usersRef.orderByKey().equalTo(myNotificationsList.get(position).getCounsellor_id())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds1: dataSnapshot.getChildren())
                                                {
                                                    try {
                                                        UniversalImageLoader.setImage(ds1.getValue(User.class).getProfile_photo(), holder.avatarIv, null, "");
                                                    }
                                                    catch (Exception e){
                                                        ///..
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatRoomsActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return myNotificationsList.size();
    }

    public void updateList(List<MyNotifications> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallBack(this.myNotificationsList,newList));
        myNotificationsList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView avatarIv;
        TextView title_tv, body_tv, date_time_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.avatarIv);
            title_tv = itemView.findViewById(R.id.title_tv);
            body_tv = itemView.findViewById(R.id.body_tv);
            date_time_tv = itemView.findViewById(R.id.date_time_tv);
        }
    }
}
