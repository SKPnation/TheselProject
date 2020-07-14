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
public class EleventhQuestionnaire extends Fragment {

    Button goodBtn, fairBtn, poorBtn;
    int i = 0;

    public EleventhQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eleventh_questionnaire, container, false);

        goodBtn = view.findViewById(R.id.good_btn);
        fairBtn = view.findViewById(R.id.fair_btn);
        poorBtn = view.findViewById(R.id.poor_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("pain");
        }

        goodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("finance", goodBtn.getText().toString());

                goodBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirteenthQuestionnaire thirteenthQuestionnaire = new ThirteenthQuestionnaire();
                        thirteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft13 = getFragmentManager().beginTransaction();
                        ft13.replace(R.id.content, thirteenthQuestionnaire);
                        ft13.commit();
                    }
                }, 700);
            }
        });

        fairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("finance", fairBtn.getText().toString());

                fairBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirteenthQuestionnaire thirteenthQuestionnaire = new ThirteenthQuestionnaire();
                        thirteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft13 = getFragmentManager().beginTransaction();
                        ft13.replace(R.id.content, thirteenthQuestionnaire);
                        ft13.commit();
                    }
                }, 700);
            }
        });

        poorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("finance", poorBtn.getText().toString());

                poorBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirteenthQuestionnaire thirteenthQuestionnaire = new ThirteenthQuestionnaire();
                        thirteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft13 = getFragmentManager().beginTransaction();
                        ft13.replace(R.id.content, thirteenthQuestionnaire);
                        ft13.commit();
                    }
                }, 700);
            }
        });

        return view;
    }

}
