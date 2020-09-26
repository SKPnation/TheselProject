package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookAppointment2 extends AppCompatActivity {

    private static final String TAG = "BookAppointment2";

    private Context mContext = BookAppointment2.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference usersRef;

    ImageView hisImageIv;
    TextView hisNameTv, hisCategoryTv, hisCostTv, apptDateTv, apptTimeTv, apptFeeTv;

    String myUID;
    String hisUID, startTime, endTime, selectedDate;
    Long hisCost;

    Locale locale;
    NumberFormat fmt;

    SimpleDateFormat simpleDateFormat;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment2);

        setupFirebaseAuth();

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        locale = new Locale("en", "NG");
        fmt = NumberFormat.getCurrencyInstance(locale);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        selectedDate = intent.getStringExtra("selectedDate");
        hisCost = intent.getLongExtra("hisCost",0L);

        hisNameTv = findViewById(R.id.hisName);
        hisCategoryTv = findViewById(R.id.hisCatgeory);
        hisCostTv = findViewById(R.id.appt_fee_tv);
        hisImageIv = findViewById(R.id.hisImage);
        apptDateTv = findViewById(R.id.appt_date_tv);
        apptTimeTv = findViewById(R.id.appt_time_tv);
        apptFeeTv = findViewById(R.id.appt_fee_tv);

        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            hisNameTv.setText(ds.getValue(User.class).getUsername());
                            hisCategoryTv.setText(ds.getValue(User.class).getCategory1());

                            try {
                                UniversalImageLoader.setImage(ds.getValue(User.class).getProfile_photo(), hisImageIv, null, "");
                            }
                            catch (Exception e){
                                Log.d("ERROR: ", ""+e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        apptDateTv.setText(selectedDate);
        apptTimeTv.setText(startTime+" - "+endTime);
        hisCostTv.setText(fmt.format(hisCost/100));

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            myUID = user.getUid();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }

    /*Appointment appointment0 = new Appointment();
                                                                        appointment0.setClient_id(myUID);
                                                                        appointment0.setCounsellor_id(hisUID);
                                                                        appointment0.setBooked_date(simpleDateFormat.format(selected_date.getTime()));
                                                                        appointment0.setNum_messages(0);
                                                                        appointment0.setStartTime("05:00")
                                                                        appointment0.setEndTime("05:40");
                                                                        appointment0.setTimestamp(timestamp);
                                                                        appointment0.setSlot("0");
                                                                        appointment0.setAppointment_id(String chatroomId = UUID.randomUUID().toString(););
                                                                        appointment0.setAbsent(false);*/

    /*
    LocalDate today = null;
                                LocalDateTime time = null;
                                LocalDateTime nowtime;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();
                                    time = LocalDateTime.now();

                                    todayDate = today;
                                    nowtime = time;


                                    if (simpleDateFormat.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        calendar = Calendar.getInstance();
                                        calendar.setTime(selected_date.getTime());
                                        calendar.add(Calendar.HOUR_OF_DAY,6);

                                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                                        if (selected_date.getTime().after(calendar.getTime()))
                                            Toast.makeText(mContext,"NO!!!",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(mContext,formatter.format(selected_date.getTime())+": "+formatter.format(calendar.getTime()),Toast.LENGTH_SHORT).show();


                                    }
                                    }
    */
}
