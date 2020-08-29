package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.util.HashMap;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UsersViewHolder>{

    Context context;
    List<User> userList;
    private FirebaseAuth mAuth;

    public AdapterUsers(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);

        UsersViewHolder viewHolder = new UsersViewHolder(view);
        //handle item clicks


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        String hisUID = userList.get(position).getUid();
        String email = userList.get(position).getEmail();
        String staffName = userList.get(position).getUsername();
        String category1 = userList.get(position).getCategory1();
        String category2 = userList.get(position).getCategory2();
        String category3 = userList.get(position).getCategory3();
        Long cost = userList.get(position).getCost();

        final String threeThousand = "N3,000";

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef
                .orderByKey()
                .equalTo( mAuth.getCurrentUser().getUid() );

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getIsStaff().equals("admin"))
                    {
                        holder.mEditBtn.setVisibility(View.VISIBLE);

                        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                sessionsRef.orderByKey().equalTo(hisUID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    //Sessions sessions = ds.getValue(Sessions.class);
                                                    //Long completed = sessions.getCompleted();

                                                    //Long percentage = (cost*completed)/100;
                                                    //double totalPay = percentage*0.6;

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                    final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.edit_staff_info, null);
                                                    builder.setTitle(staffName+"'s Info");

                                                    TextView phoneTv = mView.findViewById(R.id.phoneTv);
                                                    TextView costTv = mView.findViewById(R.id.costEt);
                                                    TextView categoryTv1 = mView.findViewById(R.id.categoryTv1);
                                                    TextView categoryTv2 = mView.findViewById(R.id.categoryTv2);
                                                    TextView categoryTv3 = mView.findViewById(R.id.categoryTv3);
                                                    TextView countryTv = mView.findViewById(R.id.countryTv);
                                                    TextView completedTv = mView.findViewById(R.id.completedTv);
                                                    TextView totalTv = mView.findViewById(R.id.totalTv);
                                                    EditText bankEt = mView.findViewById(R.id.bankEt);
                                                    EditText accountEt = mView.findViewById(R.id.accountnumEt);
                                                    EditText dayTimeEt = mView.findViewById(R.id.daytimeEt);
                                                    EditText nightTimeEt = mView.findViewById(R.id.nightimeEt);
                                                    TextView profileTv = mView.findViewById(R.id.profileTv);

                                                    profileTv.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                            final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_profile, null);

                                                            TextView nameTv = mView.findViewById(R.id.counsellor_name);
                                                            EditText descEt = mView.findViewById(R.id.counsellor_desc);
                                                            TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                                                            TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                                                            TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                                                            nameTv.setText(staffName +", "+email);
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
                                                                                    descEt.setText(desc);
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                            builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    FirebaseDatabase.getInstance().getReference("profiles")
                                                                            .child(hisUID).child("description").setValue(descEt.getText().toString());
                                                                }
                                                            });

                                                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });

                                                            builder.setView(mView);
                                                            builder.show();
                                                        }
                                                    });

                                                    phoneTv.setText(userList.get(position).getPhone());
                                                    //completedTv.setText(""+completed);
                                                    costTv.setText(""+cost);
                                                    //totalTv.setText(""+totalPay);
                                                    categoryTv1.setText(category1);
                                                    categoryTv2.setText(category2);
                                                    categoryTv3.setText(category3);
                                                    countryTv.setText(userList.get(position).getAddress());
                                                    bankEt.setText(userList.get(position).getBank());
                                                    accountEt.setText(userList.get(position).getAccountNumber());
                                                    dayTimeEt.setText(userList.get(position).getDayTime());
                                                    nightTimeEt.setText(userList.get(position).getNightTime());

                                                    costTv.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final String[] list = context.getResources().getStringArray(R.array.costs);

                                                            AlertDialog ad = new AlertDialog.Builder(context)
                                                                    .setCancelable(false)
                                                                    .setTitle(userList.get(position).getUsername() +"'s Cost")
                                                                    .setSingleChoiceItems(list, position,null)
                                                                    .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                        public void onClick( DialogInterface dialog, int i)
                                                                        {
                                                                            ListView lw = ((AlertDialog)dialog).getListView();
                                                                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                            costTv.setText(checkedItem.toString());
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                        }
                                                    });

                                                    categoryTv1.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final String[] list = context.getResources().getStringArray(R.array.categories);

                                                            AlertDialog ad = new AlertDialog.Builder(context)
                                                                    .setCancelable(false)
                                                                    .setTitle(userList.get(position).getUsername() +"'s 1st category")
                                                                    .setSingleChoiceItems(list, position,null)
                                                                    .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                        public void onClick( DialogInterface dialog, int i)
                                                                        {
                                                                            ListView lw = ((AlertDialog)dialog).getListView();
                                                                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                            categoryTv1.setText(checkedItem.toString());
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                        }
                                                    });

                                                    categoryTv2.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final String[] list = context.getResources().getStringArray(R.array.categories);

                                                            AlertDialog ad = new AlertDialog.Builder(context)
                                                                    .setCancelable(false)
                                                                    .setTitle(userList.get(position).getUsername() +"'s 2nd category")
                                                                    .setSingleChoiceItems(list, position,null)
                                                                    .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                        public void onClick( DialogInterface dialog, int i)
                                                                        {
                                                                            ListView lw = ((AlertDialog)dialog).getListView();
                                                                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                            categoryTv2.setText(checkedItem.toString());
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                        }
                                                    });

                                                    categoryTv3.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            final String[] list = context.getResources().getStringArray(R.array.categories);

                                                            AlertDialog ad = new AlertDialog.Builder(context)
                                                                    .setCancelable(false)
                                                                    .setTitle(userList.get(position).getUsername() +"'s 3rd category")
                                                                    .setSingleChoiceItems(list, position,null)
                                                                    .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                        public void onClick( DialogInterface dialog, int i)
                                                                        {
                                                                            ListView lw = ((AlertDialog)dialog).getListView();
                                                                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                            categoryTv3.setText(checkedItem.toString());
                                                                            dialog.dismiss();
                                                                        }
                                                                    })
                                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                        }
                                                    });

                                                    completedTv.setOnLongClickListener(new View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View v) {
                                                            AlertDialog ad = new AlertDialog.Builder(context)
                                                                    .setCancelable(false)
                                                                    .setTitle("RESET COMPLETED SESSIONS")
                                                                    .setPositiveButton( "RESET", new DialogInterface.OnClickListener() {
                                                                        public void onClick( DialogInterface dialog, int i)
                                                                        {
                                                                            completedTv.setText(""+0);
                                                                        }
                                                                    })
                                                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }).show();
                                                            return false;
                                                        }
                                                    });

                                                    builder.setView(mView);

                                                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                                            sessionsRef.child(hisUID).child("completed").setValue(Long.parseLong(completedTv.getText().toString()));

                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("phone", phoneTv.getText().toString());
                                                            hashMap.put("category1", categoryTv1.getText().toString());
                                                            hashMap.put("category2", categoryTv2.getText().toString());
                                                            hashMap.put("category3", categoryTv3.getText().toString());
                                                            hashMap.put("cost", Long.parseLong(costTv.getText().toString()));
                                                            hashMap.put("address", countryTv.getText().toString());
                                                            hashMap.put("bank", bankEt.getText().toString());
                                                            hashMap.put("accountNumber", accountEt.getText().toString());
                                                            hashMap.put("dayTime", dayTimeEt.getText().toString());
                                                            hashMap.put("nightTime", nightTimeEt.getText().toString());

                                                            usersRef.child(hisUID).updateChildren(hashMap);
                                                        }
                                                    });

                                                    builder.show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });

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

        //get Data
        holder.mNameTv.setText(userList.get(position).getUsername());
        holder.mCategoryTv1.setText(userList.get(position).getCategory1());
        holder.mCategoryTv2.setText(userList.get(position).getCategory2());
        holder.mCategoryTv3.setText(userList.get(position).getCategory3());

        String userCost = String.valueOf(userList.get(position).getCost());

        if (userCost.equals(String.valueOf(300000)))
            holder.mCostTv.setText(threeThousand);

        try {
            UniversalImageLoader.setImage(userList.get(position).getProfile_photo(), holder.mAvatarIv, null, "");
        }
        catch (Exception e){
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String client_id = "client_id";
                String no_client = "no client";

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                Query query = usersRef
                        .orderByKey()
                        .equalTo( mAuth.getCurrentUser().getUid() );

                query.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren() ){

                            User user = ds.getValue(User.class);

                            String client_name = user.getUsername();
                            String isStaff = user.getIsStaff();

                            if (user.getIsStaff().equals("false"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_counsellor_layout, null);

                                String onlineStatus = userList.get(position).getOnlineStatus();

                                final ImageView currentCPhotoIv = mView.findViewById(R.id.cPictureIv);
                                final ImageView onlineIv = mView.findViewById(R.id.onlineIv);
                                final ImageView offlineIv = mView.findViewById(R.id.offlineIv);
                                final TextView cNameTv = mView.findViewById(R.id.cNameTv);
                                final TextView countryTv = mView.findViewById(R.id.cCountryTv);
                                final TextView costTv = mView.findViewById(R.id.cCostTv);
                                final TextView categoryTv1 = mView.findViewById(R.id.categoryTv1);
                                final TextView categoryTv2 = mView.findViewById(R.id.categoryTv2);
                                final TextView categoryTv3 = mView.findViewById(R.id.categoryTv3);
                                final TextView dayTimeTv = mView.findViewById(R.id.dayTime);
                                final TextView nightTimeTv = mView.findViewById(R.id.nightTime);
                                final ImageButton requestBtn = mView.findViewById(R.id.requestBtn);
                                final ImageButton profileBtn = mView.findViewById(R.id.profileBtn);

                                if (!category1.isEmpty())
                                    categoryTv1.setVisibility(View.VISIBLE);
                                if (!category2.isEmpty())
                                    categoryTv2.setVisibility(View.VISIBLE);
                                if (!category3.isEmpty())
                                    categoryTv3.setVisibility(View.VISIBLE);


                                cNameTv.setText(staffName);
                                dayTimeTv.setText(userList.get(position).getDayTime());
                                categoryTv1.setText(category1);
                                categoryTv2.setText(category2);
                                categoryTv3.setText(category3);
                                countryTv.setText(userList.get(position).getAddress().toUpperCase());
                                nightTimeTv.setText(userList.get(position).getNightTime());
                                final String cost = String.valueOf(userList.get(position).getCost());

                                if (cost.equals(String.valueOf(300000)))
                                    costTv.setText(threeThousand);

                                if (onlineStatus.equals("online")){
                                    offlineIv.setVisibility(View.GONE);
                                    onlineIv.setVisibility(View.VISIBLE);
                                }
                                else {
                                    onlineIv.setVisibility(View.GONE);
                                    offlineIv.setVisibility(View.VISIBLE);
                                }

                                try {
                                    UniversalImageLoader.setImage(userList.get(position).getProfile_photo(), currentCPhotoIv, null, "");
                                }catch (Exception e){
                                    //..
                                }

                                profileBtn.setOnClickListener(new View.OnClickListener() {
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

                                        nameTv.setText(staffName);
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

                                requestBtn.setOnClickListener(v12 -> {

                                    Toast.makeText(context, "fkrnkfdknvdfnvdknv", Toast.LENGTH_SHORT).show();

                                    /*if(Common.isConnectedToTheInternet(context)){
                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage("Please wait...");
                                        progressDialog.show();

                                        Query query1 = usersRef.orderByKey().equalTo(hisUID);
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                for (DataSnapshot ds1 : dataSnapshot1.getChildren()){
                                                    User userStaff = ds1.getValue(User.class);
                                                    String staffName = userStaff.getUsername();

                                                    if (userStaff.getOnlineStatus().equals("offline")){
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                                        dialog.setMessage(staffName.toUpperCase() + " IS OFFLINE.");

                                                        dialog.show();
                                                    }
                                                    else if (userStaff.getOnlineStatus().equals("deactivated")){
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                                        dialog.setMessage("SORRY, "+staffName.toUpperCase() + " IS DEACTIVATED AT THE MOMENT.");

                                                        dialog.show();
                                                    }
                                                    else
                                                    {
                                                        ((ConsultantsActivity)context).sendRequest(hisUID, userList.get(position).getUsername(), user.getUsername());
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
                                        Toast.makeText(context, "Please check your internet connection...", Toast.LENGTH_SHORT).show();*/
                                });


                                builder.setView(mView);
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                } );
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UsersViewHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mCategoryTv1, mCategoryTv2, mCategoryTv3, mCostTv;
        Button mEditBtn;
        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mCategoryTv1 = itemView.findViewById(R.id.categoryTv1);
            mCategoryTv2 = itemView.findViewById(R.id.categoryTv2);
            mCategoryTv3 = itemView.findViewById(R.id.categoryTv3);
            mCostTv = itemView.findViewById(R.id.costTv);
            mEditBtn = itemView.findViewById(R.id.editBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
        private UsersViewHolder.ClickListener mClickListener;
        //interface for click listener
        public interface ClickListener{
            void onItemClick(View view, int position);
        }
        public void setOnClickListener(UsersViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }
    }
}

