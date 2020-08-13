package com.skiplab.theselproject.Profile;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.MainActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Requests;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase ref;
    private DatabaseReference allUsersRef;

    private ImageView settingsMenu, profilePhoto;
    private TextView mUsername, mAge, mBio, mPosts, mRequests, mConsultations, requests_client, requests_consult;
    RelativeLayout postsLayout, galleryRelLayout, appointmentRelLayout, groupSessRelLayout, requestsRelLayout, consultationsRelLayout;
    CardView consultationsCv;
    LinearLayout layout3;
    View view_line;

    String uid;

    View view;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            view = inflater.inflate(R.layout.fragment_profile, container, false);


            //init firebase
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            ref = FirebaseDatabase.getInstance();
            allUsersRef = ref.getReference();

            profilePhoto = view.findViewById(R.id.profile_photo);
            settingsMenu = view.findViewById(R.id.settingsMenu);
            mUsername = view.findViewById(R.id.usernameTv);
            mAge = view.findViewById(R.id.ageTv);
            mBio = view.findViewById(R.id.bioTv);
            mRequests = view.findViewById(R.id.num_requestsTv);
            mConsultations = view.findViewById(R.id.num_consultationsTv);
            mPosts = view.findViewById(R.id.num_postsTv);
            consultationsCv = view.findViewById(R.id.cardViewConsultations);
            view_line = view.findViewById(R.id.view_requests);
            requests_client = view.findViewById(R.id.requests_client);
            requests_consult = view.findViewById(R.id.requests_consult);
            postsLayout = view.findViewById(R.id.postsLayout);
            galleryRelLayout = view.findViewById(R.id.galleryRelLayout);
            layout3 = view.findViewById(R.id.layout3);
            requestsRelLayout = view.findViewById(R.id.requestsRelLayout);
            consultationsRelLayout = view.findViewById(R.id.consultationsRelLayout);
            appointmentRelLayout = view.findViewById(R.id.appointmentRelLayout);
            appointmentRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "This feature will be activated in future updates.", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getActivity(), ScheduledAppointments.class));
                }
            });
            requestsRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = user.getUid();
                    Intent intent = new Intent(getActivity(), RequestsActivity.class);
                    intent.putExtra("myUid", uid);
                    startActivity(intent);
                }
            });

            postsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = user.getUid();
                    Intent intent = new Intent(getActivity(), MyPostsActivity.class);
                    intent.putExtra("myUid", uid);
                    startActivity(intent);
                }
            });

            galleryRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), GalleryActivity.class));
                }
            });
            groupSessRelLayout = view.findViewById(R.id.groupSessionslayout);
            groupSessRelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "This feature will be activated in future updates.", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getActivity(), GroupSessionActivity.class));
                }
            });

            if(Common.isConnectedToTheInternet(getContext()))
            {
                checkUserStatus();
                retrieveUserData();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Please check your internet connection");
                builder.show();
            }

            setupToolbar();
        }
        catch (Exception e){
            Log.d(TAG, "onCreateProfile: "+e.getMessage());
        }

        return view;
    }

    private void retrieveUserData() {
        /*We have to get info of currently signed in user. We can get it using user's email or uid
          Im gonna retrieve user detail using email*/
        /*By using orderByChild query we will show the detail from a node whose key named email has a value
        equal to currently signed in email.
        It will search all nodes, where the matches it will get its detail*/

        Query query1 = allUsersRef.child( "users" )
                .orderByKey()
                .equalTo( FirebaseAuth.getInstance().getCurrentUser().getUid() );

        //orderByKey method will look for the key encapsulating the values of the object

        query1.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                    User user = singleSnapshot.getValue(User.class);
                    if (user.getIsStaff().equals("false"))
                    {
                        Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                        mUsername.setText( user.getUsername() );
                        mAge.setText(user.getAge());
                        mBio.setText(user.getBio());
                        mPosts.setText(String.valueOf(user.getPosts()));

                        requests_client.setVisibility(View.VISIBLE);

                        try {
                            UniversalImageLoader.setImage(user.getProfile_photo(), profilePhoto, null, "");
                        }
                        catch (Exception e){
                            //
                        }
                    }
                    else if (user.getIsStaff().equals("true"))
                    {
                        Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                        mUsername.setText( user.getUsername() );
                        mAge.setText(user.getAge());
                        mBio.setText(user.getBio());
                        mPosts.setText(String.valueOf(user.getPosts()));

                        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                        Query query = sessionsRef.orderByKey().equalTo(uid);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    Sessions sessions = ds.getValue(Sessions.class);

                                    mRequests.setText(String.valueOf(sessions.getRequests()));
                                    mConsultations.setText(String.valueOf(sessions.getCompleted()));

                                    consultationsCv.setVisibility(View.VISIBLE);
                                    view_line.setVisibility(View.VISIBLE);
                                    requests_consult.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });

                        layout3.setVisibility(View.VISIBLE);

                        try {
                            UniversalImageLoader.setImage(user.getProfile_photo(), profilePhoto, null, "");
                        }
                        catch (Exception e){
                            //
                        }

                    }
                    else
                        {
                            Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                            mUsername.setText( user.getUsername() );

                            try {
                                UniversalImageLoader.setImage(user.getProfile_photo(), profilePhoto, null, "");
                            }
                            catch (Exception e){
                                //
                            }

                            layout3.setVisibility(View.GONE);
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        } );
    }

    private void setupToolbar(){
        settingsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AccountSettingsActivity.class));
            }
        });
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            //mProfileTv.setText(user.getEmail());
            uid = user.getUid();
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveUserData();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }

}
