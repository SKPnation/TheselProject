package com.skiplab.theselproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.models.Early;
import com.skiplab.theselproject.models.Profile;

import java.util.HashMap;

import javax.annotation.Nullable;

public class ProfileEdit extends AppCompatActivity {

    Context mContext = ProfileEdit.this;

    private CollectionReference profileDb;

    TextView appointmentsTv, apptDurationTv, descTv, instantSessionCostTv, instantSessionsTv;
    ImageView closeBtn, saveBtn;

    String hisUID;

    String [] costs, durations, numbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        profileDb = FirebaseFirestore.getInstance().collection("profiles");

        costs = this.getResources().getStringArray(R.array.costs);
        numbers = this.getResources().getStringArray(R.array.numbers);

        appointmentsTv = findViewById(R.id.appointments_tv);
        apptDurationTv = findViewById(R.id.appt_duration_tv);
        descTv = findViewById(R.id.description_tv);
        instantSessionCostTv = findViewById(R.id.instant_session_cost_tv);
        instantSessionsTv = findViewById(R.id.instant_sessions_tv);
        saveBtn = findViewById(R.id.saveBtn);
        closeBtn = findViewById(R.id.backArrow);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDb.document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (!documentSnapshot.exists())
                                {
                                    Profile profile = new Profile();
                                    profile.setDescription("");
                                    profile.setAppt_duration("30");
                                    profile.setInstant_cost(0);
                                    profile.setAppointments(0);
                                    profile.setInstants(0);
                                    profileDb.document(hisUID).set(profile)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext,"Saved!",Toast.LENGTH_LONG).show();

                                                    finish();
                                                }
                                            });
                                }
                                else
                                {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("instant_cost", Long.valueOf(instantSessionCostTv.getText().toString()));
                                    hashMap.put("instants", Long.valueOf(instantSessionsTv.getText().toString()));
                                    hashMap.put("appointments", Long.valueOf(appointmentsTv.getText().toString()));
                                    hashMap.put("appt_duration","30");
                                    hashMap.put("description", descTv.getText().toString());
                                    profileDb.document(hisUID).set(hashMap, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext,"Saved!",Toast.LENGTH_LONG).show();

                                                    finish();
                                                }
                                            });
                                }
                            }
                        });
            }
        });

        profileDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists())
                        {
                            Profile profile = documentSnapshot.toObject(Profile.class);

                            appointmentsTv.setText(String.valueOf(profile.getAppointments()));
                            apptDurationTv.setText(profile.getAppt_duration());
                            descTv.setText(profile.getDescription());
                            instantSessionCostTv.setText(String.valueOf(profile.getInstant_cost()));
                            instantSessionsTv.setText(String.valueOf(profile.getInstants()));

                            descTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    builder.setTitle("Description");

                                    final EditText descriptionEt = new EditText(mContext);

                                    descriptionEt.setHint("profile description");
                                    descriptionEt.setText(profile.getDescription());
                                    builder.setView(descriptionEt);

                                    builder.setPositiveButton("SET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String description = descriptionEt.getText().toString();
                                            descTv.setText(description);
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            appointmentsTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("Select Number Of Appointments");
                                    builder.setItems(numbers, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            appointmentsTv.setText(numbers[which]);
                                        }
                                    });

                                    builder.show();
                                }
                            });

                            apptDurationTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //..
                                }
                            });

                            instantSessionsTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("Select Number Of Instants");
                                    builder.setItems(numbers, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            instantSessionsTv.setText(numbers[which]);
                                        }
                                    });

                                    builder.show();
                                }
                            });

                            instantSessionCostTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("Select Cost");
                                    builder.setItems(costs, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            instantSessionCostTv.setText(costs[which]);
                                        }
                                    });

                                    builder.show();
                                }
                            });
                        }
                    }
                });
    }
}
