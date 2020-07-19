package com.skiplab.theselproject.Questionnaire;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FifthQuestionnaire extends Fragment {

    Button noBtn, yesBtn;
    int i = 0;

    public FifthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fifth_questionnaire, container, false);

        noBtn = view.findViewById(R.id.no_btn);
        yesBtn = view.findViewById(R.id.yes_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("religion");
        }

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("therapy", noBtn.getText().toString());

                noBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SixthQuestionnaire sixthQuestionnaire = new SixthQuestionnaire();
                        sixthQuestionnaire.setArguments(b);
                        FragmentTransaction ft6 = getFragmentManager().beginTransaction();
                        ft6.replace(R.id.content, sixthQuestionnaire);
                        ft6.commit();
                    }
                }, 200);
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("therapy", yesBtn.getText().toString());

                yesBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SixthQuestionnaire sixthQuestionnaire = new SixthQuestionnaire();
                        sixthQuestionnaire.setArguments(b);
                        FragmentTransaction ft6 = getFragmentManager().beginTransaction();
                        ft6.replace(R.id.content, sixthQuestionnaire);
                        ft6.commit();
                    }
                }, 200);
            }
        });

        return view;
    }

}
