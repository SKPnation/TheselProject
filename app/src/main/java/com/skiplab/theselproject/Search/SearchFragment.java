package com.skiplab.theselproject.Search;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.skiplab.theselproject.Adapter.AdapterUsers;
import com.skiplab.theselproject.ChatActivity;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    DatabaseReference usersRef;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

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
                        SelectPlanFragment spf = new SelectPlanFragment();
                        FragmentManager fm = getFragmentManager();

                        fm.beginTransaction().add(R.id.content, spf).commit();
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

}

