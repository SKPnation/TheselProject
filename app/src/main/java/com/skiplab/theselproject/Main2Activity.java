package com.skiplab.theselproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skiplab.theselproject.Authentication.AdminRegisterActivity;
import com.skiplab.theselproject.Authentication.LoginActivity;
import com.skiplab.theselproject.Authentication.RegisterActivity;
import com.skiplab.theselproject.Authentication.StaffRegisterActivity;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    //Firebase
    //private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Context mContext = Main2Activity.this;

    Animation skip2big;

    private ImageView appNameIv;
    private TextView loginTv, backTv;
    private ImageButton switchBtn;
    private Button userBtn, counsellorBtn;

    int i = 0;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Now the listener will be actively listening for changes in the authentication state
        setupFirebaseAuth();

        skip2big = AnimationUtils.loadAnimation(mContext, R.anim.skip2bg);

        appNameIv = findViewById(R.id.appNameIv);
        backTv = findViewById(R.id.main_backTv);
        loginTv = findViewById(R.id.main2_loginTv);
        userBtn = findViewById(R.id.user_btn);
        counsellorBtn = findViewById(R.id.counsellor_btn);
        switchBtn = findViewById(R.id.switchBtn);


        appNameIv.setAnimation(skip2big);
        userBtn.setAnimation(skip2big);
        counsellorBtn.setAnimation(skip2big);
        loginTv.setAnimation(skip2big);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();

                /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do not register as a user if you are a counsellor");
                builder.show();

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(mContext, RegisterActivity.class) );
                    }
                }, 3000);*/


            }
        });

        counsellorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setMessage("This application is under maintenance.")
                        .create();
                alertDialog.show();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Thesel Consultants ");

                final EditText staffPassword = new EditText(mContext);
                staffPassword.setSingleLine(true);

                staffPassword.setHint("Counsellor code ");
                builder.setView(staffPassword);

                builder.setPositiveButton( "ENTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String staffPwd = staffPassword.getText().toString();

                        if(TextUtils.isEmpty(staffPwd) || !staffPwd.equals("rexi?007"))
                            Toast.makeText( mContext, "Wrong code", Toast.LENGTH_SHORT).show();
                        else
                            startActivity(new Intent(mContext, StaffRegisterActivity.class) );
                    }
                });
                builder.show();*/
            }
        });

        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity.class));
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

        switchBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Administrative team");

                final EditText staffPassword = new EditText(mContext);
                staffPassword.setSingleLine(true);

                staffPassword.setHint("Admin code ");
                builder.setView(staffPassword);

                builder.setPositiveButton( "ENTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String adminPwd = staffPassword.getText().toString();

                        if(TextUtils.isEmpty(adminPwd) || !adminPwd.equals("thesel321BySLI"))
                        {
                            Toast.makeText( mContext, "Wrong code", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            startActivity(new Intent(mContext, AdminRegisterActivity.class));
                            Toast.makeText( mContext, "Welcome", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();

                return false;
            }
        });
    }

    private void setupFirebaseAuth()
    {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());


                Intent intent = new Intent( mContext, DashboardActivity.class );
                startActivity( intent );
                finish();

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
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