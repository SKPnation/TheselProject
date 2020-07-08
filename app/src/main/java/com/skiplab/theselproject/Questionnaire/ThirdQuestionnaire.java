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
public class ThirdQuestionnaire extends Fragment {

    Button noBtn, yesBtn;

    public ThirdQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_questionnaire, container, false);

        noBtn = view.findViewById(R.id.no_btn);
        yesBtn = view.findViewById(R.id.yes_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("relationship_status");

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religious", noBtn.getText().toString());

                FourthQuestionnaire fourthQuestionnaire = new FourthQuestionnaire();
                fourthQuestionnaire.setArguments(b);
                FragmentTransaction ft4 = getFragmentManager().beginTransaction();
                ft4.replace(R.id.content, fourthQuestionnaire);
                ft4.commit();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("religious", yesBtn.getText().toString());

                FourthQuestionnaire fourthQuestionnaire = new FourthQuestionnaire();
                fourthQuestionnaire.setArguments(b);
                FragmentTransaction ft4 = getFragmentManager().beginTransaction();
                ft4.replace(R.id.content, fourthQuestionnaire);
                ft4.commit();
            }
        });

        return view;
    }

}