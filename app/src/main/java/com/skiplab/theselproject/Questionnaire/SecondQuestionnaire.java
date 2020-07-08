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
public class SecondQuestionnaire extends Fragment {

    Button singleBtn, relationshipBtn, marriedBtn, divorcedBtn, widowedBtn, otherBtn;

    public SecondQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second_questionnaire, container, false);

        singleBtn = view.findViewById(R.id.single_btn);
        relationshipBtn = view.findViewById(R.id.relationship_btn);
        marriedBtn = view.findViewById(R.id.married_btn);
        divorcedBtn = view.findViewById(R.id.divorced_btn);
        widowedBtn = view.findViewById(R.id.widowed_btn);
        otherBtn = view.findViewById(R.id.other_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("gender");

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        singleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", singleBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        relationshipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", relationshipBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        marriedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", marriedBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        divorcedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", divorcedBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        widowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", widowedBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", otherBtn.getText().toString());

                ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                thirdQuestionnaire.setArguments(b);
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.content, thirdQuestionnaire);
                ft3.commit();
            }
        });

        return  view;
    }

}
