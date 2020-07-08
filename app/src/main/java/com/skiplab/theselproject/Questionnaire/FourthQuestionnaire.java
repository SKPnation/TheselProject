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
public class FourthQuestionnaire extends Fragment {

    Button christianityBtn, islamBtn, judaismBtn, otherBtn;


    public FourthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourth_questionnaire, container, false);

        christianityBtn = view.findViewById(R.id.christianity_btn);
        islamBtn = view.findViewById(R.id.islam_btn);
        judaismBtn = view.findViewById(R.id.judaism_btn);
        otherBtn = view.findViewById(R.id.other_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("religious");

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        christianityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religion", christianityBtn.getText().toString());

                FifthQuestionnaire fifthQuestionnaire = new FifthQuestionnaire();
                fifthQuestionnaire.setArguments(b);
                FragmentTransaction ft5 = getFragmentManager().beginTransaction();
                ft5.replace(R.id.content, fifthQuestionnaire);
                ft5.commit();
            }
        });

        islamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religion", islamBtn.getText().toString());

                FifthQuestionnaire fifthQuestionnaire = new FifthQuestionnaire();
                fifthQuestionnaire.setArguments(b);
                FragmentTransaction ft5 = getFragmentManager().beginTransaction();
                ft5.replace(R.id.content, fifthQuestionnaire);
                ft5.commit();
            }
        });

        judaismBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religion", judaismBtn.getText().toString());

                FifthQuestionnaire fifthQuestionnaire = new FifthQuestionnaire();
                fifthQuestionnaire.setArguments(b);
                FragmentTransaction ft5 = getFragmentManager().beginTransaction();
                ft5.replace(R.id.content, fifthQuestionnaire);
                ft5.commit();
            }
        });

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religion", otherBtn.getText().toString());

                FifthQuestionnaire fifthQuestionnaire = new FifthQuestionnaire();
                fifthQuestionnaire.setArguments(b);
                FragmentTransaction ft5 = getFragmentManager().beginTransaction();
                ft5.replace(R.id.content, fifthQuestionnaire);
                ft5.commit();
            }
        });

        return view;
    }

}
