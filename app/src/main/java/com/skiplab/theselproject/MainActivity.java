package com.skiplab.theselproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Firebase
    //private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Context mContext = MainActivity.this;

    Animation skipbig, nothingtocome, btnanim;

    ImageView appNameIv;
    TextView sloganTv, nextTv;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Now the listener will be actively listening for changes in the authentication state
        setupFirebaseAuth();

        skipbig = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);
        nothingtocome = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);
        btnanim = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);

        appNameIv = findViewById(R.id.appNameIv);
        sloganTv = findViewById(R.id.slogan1Tv);
        nextTv = findViewById(R.id.main1_nextTv);

        appNameIv.setAnimation(skipbig);
        sloganTv.setAnimation(skipbig);
        nextTv.setAnimation(skipbig);

        nextTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Main2Activity.class));
            }
        });
    }

    private void setupFirebaseAuth()
    {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null)
            {
                Intent intent = new Intent( mContext, DashboardActivity.class );
                startActivity( intent );
                finish();

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //Everything you need to use the authStateListener Object
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
}
