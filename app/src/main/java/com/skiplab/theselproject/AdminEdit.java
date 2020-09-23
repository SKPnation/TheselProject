package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.models.User;

public class AdminEdit extends AppCompatActivity {

    Context mContext = AdminEdit.this;

    String hisUID;

    TextView hisNameTv;
    Button realtimeDbBtn;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        hisNameTv = findViewById(R.id.hisName);
        realtimeDbBtn = findViewById(R.id.realtime_db_btn);

        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);
                            hisNameTv.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });


        realtimeDbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RealtimeDbEdit.class);
                intent.putExtra("hisUID",hisUID);
                startActivity(intent);
            }
        });
    }
}
