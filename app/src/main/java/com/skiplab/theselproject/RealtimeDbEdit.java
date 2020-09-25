package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.models.User;

import java.util.HashMap;

public class RealtimeDbEdit extends AppCompatActivity {

    Context mContext = RealtimeDbEdit.this;

    TextView hisNameTv, emailTv, phoneTv, countryTv, categoryOne, categoryTwo, categoryThree, statusTv;
    TextView costTv, dayTv, nightTv, bankTv, accountNum;
    ImageView saveBtn, backBtn;

    DatabaseReference usersRef;

    String hisUID;
    String [] categories, costs, numbers, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_db_edit);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        categories = this.getResources().getStringArray(R.array.categories);
        costs = this.getResources().getStringArray(R.array.costs);
        numbers = this.getResources().getStringArray(R.array.numbers);
        status = this.getResources().getStringArray(R.array.status);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        hisNameTv = findViewById(R.id.hisName);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        countryTv = findViewById(R.id.countryTv);
        categoryOne = findViewById(R.id.categoryTv1);
        categoryTwo = findViewById(R.id.categoryTv2);
        categoryThree = findViewById(R.id.categoryTv3);
        costTv = findViewById(R.id.costTv);
        dayTv = findViewById(R.id.dayTv);
        nightTv = findViewById(R.id.nightTv);
        bankTv = findViewById(R.id.bankTv);
        accountNum = findViewById(R.id.acc_num_Tv);
        statusTv = findViewById(R.id.status_tv);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backArrow);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            hisNameTv.setText(user.getUsername());
                            emailTv.setText(user.getEmail());
                            phoneTv.setText(user.getPhone());
                            statusTv.setText(user.getOnlineStatus());
                            countryTv.setText(user.getAddress());
                            categoryOne.setText(user.getCategory1());
                            categoryTwo.setText(user.getCategory2());
                            categoryThree.setText(user.getCategory3());
                            costTv.setText(String.valueOf(user.getCost()));
                            dayTv.setText(user.getDayTime());
                            nightTv.setText(user.getNightTime());
                            bankTv.setText(user.getBank());
                            accountNum.setText(user.getAccountNumber());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
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

        categoryOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryOne.setText(categories[which]);
                    }
                });

                builder.show();
            }
        });

        categoryTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTwo.setText(categories[which]);
                    }
                });

                builder.show();
            }
        });

        categoryThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Category");
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryThree.setText(categories[which]);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost = costTv.getText().toString();
                String category1 = categoryOne.getText().toString();
                String category2 = categoryTwo.getText().toString();
                String category3 = categoryThree.getText().toString();
                String online_status = statusTv.getText().toString();
                String country = countryTv.getText().toString();
                String daytime = dayTv.getText().toString();
                String nighttime = nightTv.getText().toString();
                String bank = bankTv.getText().toString();
                String accountNo = accountNum.getText().toString();

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

                usersRef.child(hisUID).updateChildren(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext,"Saved!",Toast.LENGTH_LONG).show();

                                finish();
                            }
                        });
            }
        });

    }
}
