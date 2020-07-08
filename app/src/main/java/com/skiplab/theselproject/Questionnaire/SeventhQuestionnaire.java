package com.skiplab.theselproject.Questionnaire;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeventhQuestionnaire extends Fragment {

    Button never_btn, over_a_year_btn, over_three_months_btn, over_a_month_btn, over_a_week_btn, this_week_btn;

    public SeventhQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seventh_questionnaire, container, false);

        never_btn = view.findViewById(R.id.never_btn);
        over_a_year_btn = view.findViewById(R.id.over_a_year_btn);
        over_three_months_btn = view.findViewById(R.id.over_three_months_btn);
        over_a_month_btn = view.findViewById(R.id.over_a_month_btn);
        over_a_week_btn = view.findViewById(R.id.over_a_week_btn);
        this_week_btn = view.findViewById(R.id.this_week_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("corona");

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        never_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", never_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        over_a_year_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", over_a_year_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        over_three_months_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", over_three_months_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        over_a_month_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", over_a_month_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        over_a_week_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", over_a_week_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        this_week_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("suicide", this_week_btn.getText().toString());

                EighthQuestionnaire eighthQuestionnaire = new EighthQuestionnaire();
                eighthQuestionnaire.setArguments(b);
                FragmentTransaction ft8 = getFragmentManager().beginTransaction();
                ft8.replace(R.id.content, eighthQuestionnaire);
                ft8.commit();
            }
        });

        return view;
    }

}
