package com.skiplab.theselproject.Search;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectPlanFragment extends Fragment {

    ImageView freeNext, premiumNext;
    CardView cvFree, cvPaid;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    DatabaseReference reference;

    ProgressDialog pd;

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

        pd = new ProgressDialog(getActivity());

        cvFree = view.findViewById(R.id.cardViewFree);
        cvPaid = view.findViewById(R.id.cardViewPaid);
        freeNext = view.findViewById(R.id.free_next);
        premiumNext = view.findViewById(R.id.paid_next);

        freeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please wait...");
                pd.show();

                Query query = reference;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(fUser.getUid()))
                        {
                            pd.dismiss();

                            AlertDialog ad = new AlertDialog.Builder(getActivity())
                                    .setMessage("You have already used your one time free session")
                                    .show();
                        }
                        else
                        {
                            i++;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();

                                    Intent intent = new Intent(getActivity(), ConsultantsActivity.class);
                                    intent.putExtra("plan","Free");
                                    getActivity().startActivity(intent);
                                }
                            }, 200);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });


            }
        });

        premiumNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Please wait...");
                pd.show();

                i++;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();

                        Intent intent = new Intent(getActivity(), ConsultantsActivity.class);
                        intent.putExtra("plan","Paid");
                        getActivity().startActivity(intent);
                    }
                }, 200);
            }
        });

        return view;
    }

}
