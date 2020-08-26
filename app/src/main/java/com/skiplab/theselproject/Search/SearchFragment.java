package com.skiplab.theselproject.Search;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    DatabaseReference usersRef;
    private TextView usersFrgamentTitle;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersFrgamentTitle = view.findViewById(R.id.users_fragment_title);
        usersFrgamentTitle.setText("Do you need a consultant?");

        Query query1 = usersRef
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
                        UsersFragment usersFragment = new UsersFragment();
                        FragmentManager fm = getFragmentManager();

                        fm.beginTransaction().add(R.id.content, usersFragment).commit();
                    }
                    else if (user.getIsStaff().equals("true"))
                    {
                        UsersFragment usersFragment = new UsersFragment();
                        FragmentManager fm = getFragmentManager();

                        fm.beginTransaction().add(R.id.content, usersFragment).commit();
                    }
                    else
                    {
                        UsersFragment usersFragment = new UsersFragment();
                        FragmentManager fm = getFragmentManager();

                        fm.beginTransaction().add(R.id.content, usersFragment).commit();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        } );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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

