package com.skiplab.theselproject.Settings;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TermsAndConditions extends Fragment {

    ImageView back_btn;

    public TermsAndConditions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        back_btn = view.findViewById(R.id.backArrow);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

}
