package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatActivity;
import com.skiplab.theselproject.Consultation.ConsultantsActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.Data;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>{

    Context context;
    List<User> userList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    public AdapterUser(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
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
                if (Common.isConnectedToTheInternet(context)){
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("hisUID", hisUID);
                    context.startActivity(intent);
                }
                else {
                    //..
                }
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
        ImageButton profileBtn, chatBtn;
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
            chatBtn = itemView.findViewById(R.id.chatBtn);
            mCostTv = itemView.findViewById(R.id.costTv);
        }
    }
}
