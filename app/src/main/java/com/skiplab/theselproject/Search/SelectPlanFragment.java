package com.skiplab.theselproject.Search;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Questionnaire.SecondQuestionnaire;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectPlanFragment extends Fragment {

    ImageView freeNext, premiumNext;
    CardView cvFree, cvPaid;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference reference;

    int i = 0;

    public SelectPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_plan, container, false);

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("trials");

        cvFree = view.findViewById(R.id.cardViewFree);
        cvPaid = view.findViewById(R.id.cardViewPaid);
        freeNext = view.findViewById(R.id.free_next);
        premiumNext = view.findViewById(R.id.paid_next);
        freeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = reference;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(fUser.getUid()))
                        {
                            AlertDialog ad = new AlertDialog.Builder(getActivity())
                                    .setMessage("You have already used your one time free session")
                                    .show();
                        }
                        else
                        {
                            cvFree.setCardBackgroundColor(Color.BLACK);

                            i++;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle b = new Bundle();
                                    b.putString("plan", "Free");

                                    UsersFragment uf = new UsersFragment();
                                    uf.setArguments(b);
                                    FragmentManager fm = getFragmentManager();

                                    fm.beginTransaction().add(R.id.content, uf).commit();
                                }
                            }, 200);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        premiumNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cvPaid.setCardBackgroundColor(Color.BLACK);

                i++;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bundle b = new Bundle();
                        b.putString("plan", "Paid");

                        UsersFragment uf = new UsersFragment();
                        uf.setArguments(b);
                        FragmentManager fm = getFragmentManager();

                        fm.beginTransaction().add(R.id.content, uf).commit();
                    }
                }, 200);
            }
        });

        return view;
    }

}
