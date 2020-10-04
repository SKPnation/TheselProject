package com.skiplab.theselproject;

import androidx.annotation.NonNull;
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
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.text.SimpleDateFormat;

public class TimeDayAlreadyChosen extends AppCompatActivity {

    private static final String TAG = "TimeDayAlreadyChosen";

    Context mContext = TimeDayAlreadyChosen.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference usersRef;

    ImageView profileIv;
    TextView hisNameTv;
    TextView hisMessageTv;

    String hisUID, startTime, timeType;
    Long selectedDate;

    SimpleDateFormat simpleDateFormat;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_day_already_chosen);

        setupFirebaseAuth();

        Intent intent = getIntent();
        selectedDate = intent.getLongExtra("selectedDate",0L);
        hisUID = intent.getStringExtra("hisUID");
        startTime = intent.getStringExtra("startTime");
        timeType = intent.getStringExtra("timeType");

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        profileIv = findViewById(R.id.profileIv);
        hisNameTv = findViewById(R.id.hisName);
        hisMessageTv = findViewById(R.id.messageTv);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            User user = ds.getValue(User.class);

                            hisNameTv.setText(user.getUsername());

                            try {
                                UniversalImageLoader.setImage(user.getProfile_photo(),profileIv, null, "");
                                Log.d("Photo: ", user.getProfile_photo() );
                            }catch (Exception e){
                                //...
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

        String message = "Sorry, I have an appointment scheduled for "+startTime+" "+timeType+" on "+simpleDateFormat.format(selectedDate)+"\n\n"
                +"Please choose a different time or date." +"\n\n"+"Thank you.";

        hisMessageTv.setText(message);
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

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }
}
