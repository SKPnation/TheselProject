package com.skiplab.theselproject.Search;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectPlanFragment extends Fragment {

    ImageView freeNext, premiumNext;

    public SelectPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_plan, container, false);

        freeNext = view.findViewById(R.id.free_next);
        premiumNext = view.findViewById(R.id.premium_next);
        freeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("free", "Free Plan");

                UsersFragment uf = new UsersFragment();
                FragmentManager fm = getFragmentManager();

                fm.beginTransaction().add(R.id.content, uf).commit();
            }
        });

        premiumNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("premium", "Premium Plan");

                UsersFragment uf = new UsersFragment();
                FragmentManager fm = getFragmentManager();

                fm.beginTransaction().add(R.id.content, uf).commit();
            }
        });

        return view;
    }

}
