package com.skiplab.theselproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Firebase
    //private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Context mContext = MainActivity.this;

    Animation skipbig, nothingtocome, btnanim;

    ImageView appNameIv;
    TextView appNameTv, sloganTv;

    RelativeLayout mainActivity_click;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Date todayDate = new Date();
        todayDate.getTime();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "EXPIRED "+todayDate.toInstant().plusMillis(604800000).toEpochMilli());
        }*/

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error: "+e);
        }
        //Now the listener will be actively listening for changes in the authentication state
        setupFirebaseAuth();


        skipbig = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);
        nothingtocome = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);
        btnanim = AnimationUtils.loadAnimation(mContext, R.anim.skipbg);

        appNameTv = findViewById(R.id.app_name);
        appNameIv = findViewById(R.id.appNameIv);
        sloganTv = findViewById(R.id.slogan1Tv);
        mainActivity_click = findViewById(R.id.mainActivity_click);

        appNameIv.setAnimation(skipbig);
        sloganTv.setAnimation(skipbig);

        appNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Main2Activity.class));
            }
        });

        appNameIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Main2Activity.class));
            }
        });

        mainActivity_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Main2Activity.class));
            }
        });

        sloganTv.setOnClickListener(new View.OnClickListener() {
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