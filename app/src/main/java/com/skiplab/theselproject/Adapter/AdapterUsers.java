package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Profile.RequestsActivity;
import com.skiplab.theselproject.Questionnaire.QuestionnaireActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Requests;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UsersViewHolder>{

    Context context;
    List<User> userList;
    APIService apiService;
    private FirebaseAuth mAuth;
    private DatabaseReference sessionsRef, requestsRef, pinsRef;

    String adminID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    public AdapterUsers(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        pinsRef = FirebaseDatabase.getInstance().getReference("pins");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
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
        String staffName = userList.get(position).getUsername();
        String category1 = userList.get(position).getCategory1();
        String category2 = userList.get(position).getCategory2();
        String category3 = userList.get(position).getCategory3();
        Long cost = userList.get(position).getCost();

        final String fiveThousand = "N5,000";
        final String tenThousand = "N10,000";
        final String fifteenThousand = "N15,000";
        final String twentyThousand = "N20,000";
        final String twentyFiveThousand = "N25,000";
        final String thirtyThousand = "N30,000";
        final String thirtyFiveThousand = "N35,000";
        final String fortyThousand = "N40,000";
        final String fortyFiveThousand = "N45,000";
        final String fiftyThousand = "N50,000";
        final String fiftyFiveThousand = "N55,000";
        final String sixtyThousand = "N60,000";
        final String sixtyFiveThousand = "N65,000";
        final String seventyThousand = "N70,000";
        final String seventyFiveThousand = "N75,000";
        final String eightyThousand = "N80,000";
        final String eightyFiveThousand = "N85,000";
        final String ninetyThousand = "N90,000";
        final String ninetyFiveThousand = "N95,000";
        final String hundredThousand = "N100,000";

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef
                .orderByKey()
                .equalTo( mAuth.getCurrentUser().getUid() );

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getIsStaff().equals("admin")){
                        holder.mEditBtn.setVisibility(View.VISIBLE);

                        holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                sessionsRef.orderByKey().equalTo(hisUID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    Sessions sessions = ds.getValue(Sessions.class);
                                                    Long completed = sessions.getCompleted();

                                                    Long percentage = (cost*completed)/100;
                                                    double totalPay = percentage*0.6;

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
                                                    completedTv.setText(""+completed);
                                                    costTv.setText(""+cost);
                                                    totalTv.setText(""+totalPay);
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

                                                    builder.setView(mView);

                                                    builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
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

        if (userCost.equals(String.valueOf(20000)))
            holder.mCostTv.setText(tenThousand);
        else if (userCost.equals(String.valueOf(1000000)))
            holder.mCostTv.setText(tenThousand);
        else if (userCost.equals(String.valueOf(1500000)))
            holder.mCostTv.setText(fifteenThousand);
        else if (userCost.equals(String.valueOf(2000000)))
            holder.mCostTv.setText(twentyThousand);
        else if (userCost.equals(String.valueOf(2500000)))
            holder.mCostTv.setText(twentyFiveThousand);
        else if (userCost.equals(String.valueOf(3000000)))
            holder.mCostTv.setText(thirtyThousand);
        else if (userCost.equals(String.valueOf(3500000)))
            holder.mCostTv.setText(thirtyFiveThousand);
        else if (userCost.equals(String.valueOf(4000000)))
            holder.mCostTv.setText(fortyThousand);
        else if (userCost.equals(String.valueOf(4500000)))
            holder.mCostTv.setText(fortyFiveThousand);
        else if (userCost.equals(String.valueOf(5000000)))
            holder.mCostTv.setText(fiftyThousand);
        else if (userCost.equals(String.valueOf(5500000)))
            holder.mCostTv.setText(fiftyFiveThousand);
        else if (userCost.equals(String.valueOf(6000000)))
            holder.mCostTv.setText(sixtyThousand);
        else if (userCost.equals(String.valueOf(6500000)))
            holder.mCostTv.setText(sixtyFiveThousand);
        else if (userCost.equals(String.valueOf(7000000)))
            holder.mCostTv.setText(seventyThousand);
        else if (userCost.equals(String.valueOf(7500000)))
            holder.mCostTv.setText(seventyFiveThousand);
        else if (userCost.equals(String.valueOf(8000000)))
            holder.mCostTv.setText(eightyThousand);
        else if (userCost.equals(String.valueOf(8500000)))
            holder.mCostTv.setText(eightyFiveThousand);
        else if (userCost.equals(String.valueOf(9000000)))
            holder.mCostTv.setText(ninetyThousand);
        else if (userCost.equals(String.valueOf(9500000)))
            holder.mCostTv.setText(ninetyFiveThousand);
        else if (userCost.equals(String.valueOf(10000000)))
            holder.mCostTv.setText(hundredThousand);

        try {
            UniversalImageLoader.setImage(userList.get(position).getProfile_photo(), holder.mAvatarIv, null, "");
        }
        catch (Exception e){
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

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

                            if (user.getIsStaff().equals("false")){

                                progressDialog.dismiss();

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
                                final Button requestBtn = mView.findViewById(R.id.requestBtn);
                                final Button profileBtn = mView.findViewById(R.id.profileBtn);

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

                                if (cost.equals(String.valueOf(20000)))
                                    costTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1000000)))
                                    costTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1500000)))
                                    costTv.setText(fifteenThousand);
                                else if (cost.equals(String.valueOf(2000000)))
                                    costTv.setText(twentyThousand);
                                else if (cost.equals(String.valueOf(2500000)))
                                    costTv.setText(twentyFiveThousand);
                                else if (cost.equals(String.valueOf(3000000)))
                                    costTv.setText(thirtyThousand);
                                else if (cost.equals(String.valueOf(3500000)))
                                    costTv.setText(thirtyFiveThousand);
                                else if (cost.equals(String.valueOf(4000000)))
                                    costTv.setText(fortyThousand);
                                else if (cost.equals(String.valueOf(4500000)))
                                    costTv.setText(fortyFiveThousand);
                                else if (cost.equals(String.valueOf(5000000)))
                                    costTv.setText(fiftyThousand);
                                else if (cost.equals(String.valueOf(5500000)))
                                    costTv.setText(fiftyFiveThousand);
                                else if (cost.equals(String.valueOf(6000000)))
                                    costTv.setText(sixtyThousand);
                                else if (cost.equals(String.valueOf(6500000)))
                                    costTv.setText(sixtyFiveThousand);
                                else if (cost.equals(String.valueOf(7000000)))
                                    costTv.setText(seventyThousand);
                                else if (cost.equals(String.valueOf(7500000)))
                                    costTv.setText(seventyFiveThousand);
                                else if (cost.equals(String.valueOf(8000000)))
                                    costTv.setText(eightyThousand);
                                else if (cost.equals(String.valueOf(8500000)))
                                    costTv.setText(eightyFiveThousand);
                                else if (cost.equals(String.valueOf(9000000)))
                                    costTv.setText(ninetyThousand);
                                else if (cost.equals(String.valueOf(9500000)))
                                    costTv.setText(ninetyFiveThousand);
                                else if (cost.equals(String.valueOf(10000000)))
                                    costTv.setText(hundredThousand);

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

                                sessionsRef.orderByKey().equalTo(hisUID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    Sessions sessions = ds.getValue(Sessions.class);

                                                    if (sessions.getClient_id().equals(mAuth.getCurrentUser().getUid()))
                                                        requestBtn.setText("Request");
                                                    else
                                                        requestBtn.setText("Request");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });

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

                                    if(Common.isConnectedToTheInternet(context)){
                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage("Please wait...");
                                        progressDialog.show();

                                        if (isStaff.equals("false"))
                                        {
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
                                                            sessionsRef.orderByKey().equalTo(hisUID)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                            for (DataSnapshot ds1 : dataSnapshot1.getChildren()){
                                                                                Sessions sessions = ds1.getValue(Sessions.class);

                                                                                if (!sessions.getClient_id().equals(no_client))
                                                                                {
                                                                                    String clientId = sessions.getClient_id();

                                                                                    if (clientId.equals(mAuth.getCurrentUser().getUid())){

                                                                                        progressDialog.dismiss();

                                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                        final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_pin_layout, null);

                                                                                        builder.setTitle("INSERT YOUR REQUEST PIN");

                                                                                        EditText requestPin = mView.findViewById(R.id.pinEt);
                                                                                        Button createBtn = mView.findViewById(R.id.createBtn);
                                                                                        Button requestButton = mView.findViewById(R.id.requestBtn);

                                                                                        builder.setView(mView);

                                                                                        createBtn.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                context.startActivity(new Intent(context, QuestionnaireActivity.class));
                                                                                            }
                                                                                        });

                                                                                        requestButton.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                pinsRef.orderByKey().equalTo(mAuth.getCurrentUser().getUid())
                                                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                                                    if (ds.exists()){

                                                                                                                        if (ds.hasChild("requestPin")){
                                                                                                                            String pinStr = ds.child("requestPin").getValue().toString();

                                                                                                                            if (requestPin.getText().toString().isEmpty())
                                                                                                                            {
                                                                                                                                requestPin.setError("Your request PIN is required");
                                                                                                                                requestPin.requestFocus();
                                                                                                                            }
                                                                                                                            else if (requestPin.getText().toString().length() < 4 || requestPin.getText().toString().length() > 4) {
                                                                                                                                requestPin.setError("Your PIN must be a 4 digits number");
                                                                                                                                requestPin.requestFocus();
                                                                                                                            }
                                                                                                                            else if (!pinStr.equals(requestPin.getText().toString()))
                                                                                                                            {
                                                                                                                                requestPin.setError("Wrong PIN");
                                                                                                                                requestPin.requestFocus();
                                                                                                                            }
                                                                                                                            else
                                                                                                                            {
                                                                                                                                String message2 = "A user has requested for you. ";
                                                                                                                                String message3 = "New request for ";
                                                                                                                                sendNotification1(hisUID, "Thesel Team", message2);

                                                                                                                                String request_number = String.valueOf( System.currentTimeMillis() );
                                                                                                                                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                                                                                                calendar.setTimeInMillis(Long.parseLong(request_number));
                                                                                                                                String sTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                                                                                                Requests requests = new Requests();
                                                                                                                                requests.setClient_id(mAuth.getCurrentUser().getUid());
                                                                                                                                requests.setCounsellor_id(hisUID);
                                                                                                                                requests.setClient_name(user.getUsername());
                                                                                                                                requests.setCounsellor_name(userStaff.getUsername());
                                                                                                                                requests.setStatus("pending");
                                                                                                                                requests.setTimestamp(request_number);
                                                                                                                                requests.setRequest_time(sTime);

                                                                                                                                //Submit to Firebase
                                                                                                                                //Use System.CurrentMilli to key
                                                                                                                                requestsRef.child(request_number).setValue(requests);

                                                                                                                                Long requestsCount = sessions.getRequests();
                                                                                                                                sessionsRef.child(hisUID).child("requests").setValue(requestsCount+1)
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                sendNotification2(adminID, user.getUsername(), message3+ userStaff.getUsername());
                                                                                                                                            }
                                                                                                                                        });

                                                                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                                                                builder.setTitle("READ THE MESSAGE");
                                                                                                                                builder.setMessage("If your PENDING request to "+ userStaff.getUsername() + " is not ACCEPTED within 15 minutes, send another request or request for another counsellor." +
                                                                                                                                        " Do not proceed to payment on a request that is over 15 minutes old. Always check the time on your request before payment. Tap NEXT to proceed.");

                                                                                                                                builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                                                        Intent intent = new Intent(context, RequestsActivity.class);
                                                                                                                                        intent.putExtra("myUid", mAuth.getCurrentUser().getUid());
                                                                                                                                        context.startActivity(intent);
                                                                                                                                    }
                                                                                                                                });

                                                                                                                                builder.show();
                                                                                                                            }

                                                                                                                        }
                                                                                                                    }
                                                                                                                    else
                                                                                                                    {
                                                                                                                        AlertDialog builder1 = new AlertDialog.Builder(context)
                                                                                                                                .setMessage("You don't have a request PIN")
                                                                                                                                .show();
                                                                                                                    }
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
                                                                                    else
                                                                                    {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(context, userStaff.getUsername()+" is attending to a client", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }
                                                                                else
                                                                                {
                                                                                    progressDialog.dismiss();

                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                    final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_pin_layout, null);

                                                                                    builder.setTitle("INSERT YOUR REQUEST PIN");

                                                                                    EditText requestPin = mView.findViewById(R.id.pinEt);
                                                                                    Button createBtn = mView.findViewById(R.id.createBtn);
                                                                                    Button requestButton = mView.findViewById(R.id.requestBtn);

                                                                                    builder.setView(mView);

                                                                                    createBtn.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            context.startActivity(new Intent(context, QuestionnaireActivity.class));
                                                                                        }
                                                                                    });

                                                                                    requestButton.setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            pinsRef.orderByKey().equalTo(mAuth.getCurrentUser().getUid())
                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                                                if (ds.exists()){

                                                                                                                    if (ds.hasChild("requestPin")){
                                                                                                                        String pinStr = ds.child("requestPin").getValue().toString();

                                                                                                                        if (requestPin.getText().toString().isEmpty())
                                                                                                                        {
                                                                                                                            requestPin.setError("Your request PIN is required");
                                                                                                                            requestPin.requestFocus();
                                                                                                                        }
                                                                                                                        else if (requestPin.getText().toString().length() < 4 || requestPin.getText().toString().length() > 4) {
                                                                                                                            requestPin.setError("Your PIN must be a 4 digits number");
                                                                                                                            requestPin.requestFocus();
                                                                                                                        }
                                                                                                                        else if (!pinStr.equals(requestPin.getText().toString()))
                                                                                                                        {
                                                                                                                            requestPin.setError("Wrong PIN");
                                                                                                                            requestPin.requestFocus();
                                                                                                                        }
                                                                                                                        else
                                                                                                                        {
                                                                                                                            String message2 = "A user has requested for you. ";
                                                                                                                            String message3 = "New request for ";
                                                                                                                            sendNotification1(hisUID, "Thesel Team", message2);

                                                                                                                            String request_number = String.valueOf( System.currentTimeMillis() );
                                                                                                                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                                                                                            calendar.setTimeInMillis(Long.parseLong(request_number));
                                                                                                                            String sTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                                                                                            Requests requests = new Requests();
                                                                                                                            requests.setClient_id(mAuth.getCurrentUser().getUid());
                                                                                                                            requests.setCounsellor_id(hisUID);
                                                                                                                            requests.setClient_name(user.getUsername());
                                                                                                                            requests.setCounsellor_name(userStaff.getUsername());
                                                                                                                            requests.setStatus("pending");
                                                                                                                            requests.setTimestamp(request_number);
                                                                                                                            requests.setRequest_time(sTime);

                                                                                                                            //Submit to Firebase
                                                                                                                            //Use System.CurrentMilli to key
                                                                                                                            requestsRef.child(request_number).setValue(requests);

                                                                                                                            Long requestsCount = sessions.getRequests();
                                                                                                                            sessionsRef.child(hisUID).child("requests").setValue(requestsCount+1)
                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            sendNotification2(adminID, user.getUsername(), message3+ userStaff.getUsername());
                                                                                                                                        }
                                                                                                                                    });

                                                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                                                            builder.setTitle("READ THE MESSAGE");
                                                                                                                            builder.setMessage("If your PENDING request to "+ userStaff.getUsername() + " is not ACCEPTED within 15 minutes, send another request or request for another counsellor." +
                                                                                                                                    " Do not proceed to payment on a request that is over 15 minutes old. Always check the time on your request before payment. Tap NEXT to proceed.");

                                                                                                                            builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                                                                                                                                @Override
                                                                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                                                                    Intent intent = new Intent(context, RequestsActivity.class);
                                                                                                                                    intent.putExtra("myUid", mAuth.getCurrentUser().getUid());
                                                                                                                                    context.startActivity(intent);
                                                                                                                                }
                                                                                                                            });

                                                                                                                            builder.show();
                                                                                                                        }

                                                                                                                    }
                                                                                                                }
                                                                                                                else
                                                                                                                {
                                                                                                                    AlertDialog builder1 = new AlertDialog.Builder(context)
                                                                                                                            .setMessage("You don't have a request PIN")
                                                                                                                            .show();
                                                                                                                }
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
                                                        }
                                                    }


                                                }

                                                private void sendNotification2(String adminID, String username, String message3) {
                                                    CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
                                                    allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                            for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                                                                Token token = new Token(ds1.getString("token"));
                                                                Data data = new Data(mAuth.getCurrentUser().getUid(), message3, username, adminID, R.mipmap.ic_launcher2);

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

                                                private void sendNotification1(String hisUID1, String thesel_team, String message2) {
                                                    CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
                                                    allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                            for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                                                                Token token = new Token(ds1.getString("token"));
                                                                Data data = new Data(mAuth.getCurrentUser().getUid(), thesel_team+": "+ message2, "NEW REQUEST FROM "+client_name, hisUID1, R.mipmap.ic_launcher2);

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
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    //..
                                                }
                                            });
                                        }
                                    }
                                    else
                                        Toast.makeText(context, "Please check your internet connection...", Toast.LENGTH_SHORT).show();
                                });


                                builder.setView(mView);
                                builder.show();
                            }
                            else if (user.getIsStaff().equals("true"))
                            {
                                progressDialog.dismiss();
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
