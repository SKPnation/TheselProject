package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.models.Profile;
import com.skiplab.theselproject.models.User;

import java.util.HashMap;

import javax.annotation.Nullable;

public class EditConsultantProfile extends AppCompatActivity {

    Context mContext = EditConsultantProfile.this;

    DatabaseReference usersRef;
    CollectionReference profileRef;

    TextView phoneTv, costTv, categoryTv1, categoryTv2, categoryTv3,
            countryEt, num_payments_tv, totalTv, num_consultations_tv,
            dayTimeEt, nightTimeEt, bankEt, accountNumEt, profileTv, statusTv;
    Button save_btn;

    String hisUID;
    String [] categories, costs, numbers, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consultant_profile);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        categories = this.getResources().getStringArray(R.array.categories);
        costs = this.getResources().getStringArray(R.array.costs);
        numbers = this.getResources().getStringArray(R.array.numbers);
        status = this.getResources().getStringArray(R.array.status);

        phoneTv = findViewById(R.id.phoneTv);
        costTv = findViewById(R.id.costTv);
        categoryTv1 = findViewById(R.id.categoryTv1);
        categoryTv2 = findViewById(R.id.categoryTv2);
        categoryTv3 = findViewById(R.id.categoryTv3);
        statusTv = findViewById(R.id.status_tv);
        countryEt = findViewById(R.id.countryEt);
        num_payments_tv = findViewById(R.id.num_payments_tv);
        num_consultations_tv = findViewById(R.id.consultationsTv);
        totalTv = findViewById(R.id.totalTv);
        bankEt = findViewById(R.id.bankEt);
        accountNumEt = findViewById(R.id.accountnumEt);
        profileTv = findViewById(R.id.profileTv);
        dayTimeEt = findViewById(R.id.daytimeEt);
        nightTimeEt = findViewById(R.id.nightimeEt);
        save_btn = findViewById(R.id.saveBtn);

        profileRef = FirebaseFirestore.getInstance().collection("profiles");
        usersRef = FirebaseDatabase.getInstance().getReference("users");


        usersRef.orderByKey().equalTo(hisUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);

                            phoneTv.setText(user.getPhone());
                            costTv.setText(String.valueOf(user.getCost()));
                            countryEt.setText(user.getAddress());
                            categoryTv1.setText(user.getCategory1());
                            categoryTv2.setText(user.getCategory2());
                            categoryTv3.setText(user.getCategory3());
                            statusTv.setText(user.getOnlineStatus());
                            dayTimeEt.setText(user.getDayTime());
                            nightTimeEt.setText(user.getNightTime());
                            bankEt.setText(user.getBank());
                            accountNumEt.setText(user.getAccountNumber());

                            profileRef.whereEqualTo("user_id",hisUID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                if (task.getResult().size()>0)
                                                {
                                                    task.getResult().getQuery()
                                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                    for (DocumentSnapshot ds: documentSnapshots.getDocuments())
                                                                    {
                                                                        Profile profile = ds.toObject(Profile.class);

                                                                        num_consultations_tv.setText(String.valueOf(profile.getNum_of_consultations()));
                                                                        num_payments_tv.setText(String.valueOf(profile.getNum_of_payments()));
                                                                        profileTv.setText(profile.getDescription());

                                                                        long cost = user.getCost()/100;

                                                                        double pay_out = (profile.getNum_of_payments() * cost)*0.6;

                                                                        totalTv.setText(String.valueOf(pay_out));

                                                                        profileTv.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                                                builder.setCancelable(false);
                                                                                final View mView =  LayoutInflater.from(mContext).inflate(R.layout.consultant_profile, null);

                                                                                TextView nameTv = mView.findViewById(R.id.counsellor_name);
                                                                                EditText descEt = mView.findViewById(R.id.counsellor_desc);
                                                                                TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                                                                                TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                                                                                TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                                                                                nameTv.setText(user.getUsername());
                                                                                if (!user.getCategory1().isEmpty()){
                                                                                    catTv1.setVisibility(View.VISIBLE);
                                                                                    catTv1.setText(user.getCategory1());
                                                                                }
                                                                                if (!user.getCategory2().isEmpty()){
                                                                                    catTv2.setVisibility(View.VISIBLE);
                                                                                    catTv2.setText(user.getCategory2());
                                                                                }
                                                                                if (!user.getCategory3().isEmpty()){
                                                                                    catTv3.setVisibility(View.VISIBLE);
                                                                                    catTv3.setText(user.getCategory3());
                                                                                }

                                                                                String desc = profile.getDescription();
                                                                                if (!desc.equals(""))
                                                                                    descEt.setText(desc);

                                                                                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        HashMap<String, Object> profileMap = new HashMap<>();
                                                                                        profileMap.put("description",descEt.getText().toString());

                                                                                        profileRef.document(hisUID).set(profileMap, SetOptions.merge());
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


                                                                    }
                                                                }
                                                            });
                                                }
                                                else
                                                {
                                                    //..
                                                }
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        categoryTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTv1.setText(categories[which]);
                    }
                });

                builder.show();
            }
        });

        categoryTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTv2.setText(categories[which]);
                    }
                });

                builder.show();
            }
        });

        categoryTv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTv3.setText(categories[which]);
                    }
                });

                builder.show();
            }
        });

        costTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(costs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        costTv.setText(costs[which]);
                    }
                });

                builder.show();
            }
        });

        num_payments_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Number");
                builder.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        num_payments_tv.setText(numbers[which]);
                    }
                });

                builder.show();
            }
        });

        statusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Status");
                builder.setItems(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusTv.setText(status[which]);
                    }
                });

                builder.show();
            }
        });


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
            }
        });
    }

    private void saveData() {
        String cost = costTv.getText().toString();
        String category1 = categoryTv1.getText().toString();
        String category2 = categoryTv2.getText().toString();
        String category3 = categoryTv3.getText().toString();
        String online_status = statusTv.getText().toString();
        String country = countryEt.getText().toString();
        String daytime = dayTimeEt.getText().toString();
        String nighttime = nightTimeEt.getText().toString();
        String bank = bankEt.getText().toString();
        String accountNo = accountNumEt.getText().toString();

        String num_of_payments = num_payments_tv.getText().toString();
        String total = totalTv.getText().toString();
        String num_of_consultations = num_consultations_tv.getText().toString();
        String description = profileTv.getText().toString();

        profileRef.whereEqualTo("user_id",hisUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().size() > 0)
                            {
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("cost", Long.parseLong(cost));
                                userMap.put("category1",category1);
                                userMap.put("category2",category2);
                                userMap.put("category3",category3);
                                userMap.put("onlineStatus",online_status);
                                userMap.put("address",country);
                                userMap.put("dayTime",daytime);
                                userMap.put("nightTime",nighttime);
                                userMap.put("bank",bank);
                                userMap.put("accountNumber",accountNo);

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("description",description);
                                profileMap.put("user_id",hisUID);
                                profileMap.put("num_of_consultations",Long.parseLong(num_of_consultations));
                                profileMap.put("total", Double.parseDouble(total));
                                profileMap.put("num_of_payments",Long.parseLong(num_of_payments));

                                usersRef.child(hisUID).updateChildren(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                profileRef.document(hisUID).set(profileMap, SetOptions.merge());

                                                finish();
                                            }
                                        });
                            }
                            else
                            {
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("cost", Long.parseLong(cost));
                                userMap.put("category1",category1);
                                userMap.put("category2",category2);
                                userMap.put("category3",category3);
                                userMap.put("onlineStatus",online_status);
                                userMap.put("address",country);
                                userMap.put("dayTime",daytime);
                                userMap.put("nightTime",nighttime);
                                userMap.put("bank",bank);
                                userMap.put("accountNumber",accountNo);

                                Profile profile = new Profile();
                                profile.setDescription("");
                                profile.setUser_id(hisUID);
                                profile.setNum_of_consultations(0);
                                profile.setTotal(0);
                                profile.setNum_of_payments(0);

                                usersRef.child(hisUID).updateChildren(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                profileRef.document(hisUID).set(profile);

                                                finish();
                                            }
                                        });

                            }
                        }
                    }
                });
    }
}
