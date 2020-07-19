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
public class EighthQuestionnaire extends Fragment {

    Button noBtn, yesBtn;
    int i = 0;

    public EighthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eighth_questionnaire, container, false);

        noBtn = view.findViewById(R.id.no_btn);
        yesBtn = view.findViewById(R.id.yes_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("suicide");
        }

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("phobias", noBtn.getText().toString());

                noBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NinethQuestionnaire ninethQuestionnaire = new NinethQuestionnaire();
                        ninethQuestionnaire.setArguments(b);
                        FragmentTransaction ft9 = getFragmentManager().beginTransaction();
                        ft9.replace(R.id.content, ninethQuestionnaire);
                        ft9.commit();
                    }
                }, 200);
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("phobias", yesBtn.getText().toString());

                yesBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NinethQuestionnaire ninethQuestionnaire = new NinethQuestionnaire();
                        ninethQuestionnaire.setArguments(b);
                        FragmentTransaction ft9 = getFragmentManager().beginTransaction();
                        ft9.replace(R.id.content, ninethQuestionnaire);
                        ft9.commit();
                    }
                }, 200);
            }
        });

        return view;
    }

}
