package com.skiplab.theselproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.skiplab.theselproject.Home.HomeFragment;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.Utils.UpdateHelper;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Token;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import co.paystack.android.PaystackSdk;

public class DashboardActivity extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener{

    private static final String TAG = "DashboardActivity";

    Context mContext = DashboardActivity.this;

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference usersRef;

    APIService apiService;

    public static boolean isActivityRunning;

    String mUID;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        setupFirebaseAuth();
        initFCM();

        //bottom app bar


        initImageLoader();
        PaystackSdk.initialize(this.getApplicationContext());

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();

        //Home fragment transaction (default, on star)
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("The installed version of Thesel is no longer supported, please update your app.")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp));
                        startActivity(browserIntent);
                    }
                }).create();
        alertDialog.show();
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void initFCM() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d( TAG, "initFCM: token: " + token );
        SendRegistrationToServer( token );
    }

    private void SendRegistrationToServer(String tokenRefresh) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + tokenRefresh);

        CollectionReference collection = FirebaseFirestore.getInstance().collection("tokens");
        Token token = new Token(tokenRefresh);
        collection.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token);

        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserRef.child("messaging_token").setValue(tokenRefresh)
                .addOnCompleteListener(task -> {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("onlineStatus").setValue("online");
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState()
    {
        Log.d( TAG, "checkAuthenticationState: check authentication state." );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            Intent intent = new Intent( DashboardActivity.this, MainActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity( intent );
            finish();
        } else {
            mUID = user.getUid();

            Log.d( TAG, "checkAuthenticationState: user is authenticated." );
            //save uid of curently signed in user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        }
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

    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
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
}

