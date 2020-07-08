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
public class SixthQuestionnaire extends Fragment {

    Button noBtn, yesBtn, maybeBtn;

    public SixthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sixth_questionnaire, container, false);

        noBtn = view.findViewById(R.id.no_btn);
        yesBtn = view.findViewById(R.id.yes_btn);
        maybeBtn = view.findViewById(R.id.maybe_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("therapy");

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("corona", noBtn.getText().toString());

                SeventhQuestionnaire seventhQuestionnaire = new SeventhQuestionnaire();
                seventhQuestionnaire.setArguments(b);
                FragmentTransaction ft7 = getFragmentManager().beginTransaction();
                ft7.replace(R.id.content, seventhQuestionnaire);
                ft7.commit();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("corona", yesBtn.getText().toString());

                SeventhQuestionnaire seventhQuestionnaire = new SeventhQuestionnaire();
                seventhQuestionnaire.setArguments(b);
                FragmentTransaction ft7 = getFragmentManager().beginTransaction();
                ft7.replace(R.id.content, seventhQuestionnaire);
                ft7.commit();
            }
        });

        maybeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("corona", maybeBtn.getText().toString());

                SeventhQuestionnaire seventhQuestionnaire = new SeventhQuestionnaire();
                seventhQuestionnaire.setArguments(b);
                FragmentTransaction ft7 = getFragmentManager().beginTransaction();
                ft7.replace(R.id.content, seventhQuestionnaire);
                ft7.commit();
            }
        });

        return view;
    }

}