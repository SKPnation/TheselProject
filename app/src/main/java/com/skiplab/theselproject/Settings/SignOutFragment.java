package com.skiplab.theselproject.Settings;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.Authentication.LoginActivity;
import com.skiplab.theselproject.R;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignOutFragment extends Fragment {

    private static final String TAG = "SignOutFragment";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressBar;
    private TextView tvSigningOut;
    private Button btnConfirmSignOut;

    public SignOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sign_out, container, false);

        btnConfirmSignOut = view.findViewById(R.id.btnConfirmSignout);
        tvSigningOut = view.findViewById(R.id.tvSigningOut);
        mProgressBar = view.findViewById(R.id.signOutProgressBar);

        mProgressBar.setVisibility(GONE);
        tvSigningOut.setVisibility(GONE);

        setupFirebaseAuth();

        btnConfirmSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to sign out");
                mProgressBar.setVisibility(View.VISIBLE);
                tvSigningOut.setVisibility(View.VISIBLE);

                try {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("onlineStatus").setValue("offline");
                    Intent intent = new Intent( getActivity(), LoginActivity.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity( intent );
                    mAuth.signOut();

                }
                catch (Exception e){
                    Log.e(TAG, "signOut: Error "+e.getMessage());
                }
            }
        });

        return view;
    }

    private void setupFirebaseAuth() {
        // Initialize Firebase Auth
        Log.d(TAG, "setupFirebaseAuth: Setting up firebse auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null){
                    //user is signed in
                    Log.d( TAG, "onAuthStateChanged: signed_in: " + currentUser.getUid());
                }
                else {
                    //user is signed out
                    Log.d( TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }
}
