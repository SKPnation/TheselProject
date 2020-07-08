package com.skiplab.theselproject.Questionnaire;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstQuestionnaire extends Fragment {

    Button maleBtn, femaleBtn;


    public FirstQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_questionnaire, container, false);

        maleBtn = view.findViewById(R.id.male_btn);
        femaleBtn = view.findViewById(R.id.female_btn);

        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("gender", maleBtn.getText().toString());

                SecondQuestionnaire secondQuestionnaire = new SecondQuestionnaire();
                secondQuestionnaire.setArguments(b);
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.replace(R.id.content, secondQuestionnaire);
                ft2.commit();
            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("gender", femaleBtn.getText().toString());

                SecondQuestionnaire secondQuestionnaire = new SecondQuestionnaire();
                secondQuestionnaire.setArguments(b);
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.replace(R.id.content, secondQuestionnaire);
                ft2.commit();
            }
        });

        return view;
    }

}
