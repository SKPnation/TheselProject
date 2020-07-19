package com.skiplab.theselproject.Questionnaire;


import android.content.Intent;
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

import com.skiplab.theselproject.ChatActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondQuestionnaire extends Fragment {

    Button singleBtn, relationshipBtn, marriedBtn, divorcedBtn, widowedBtn, otherBtn;
    int i = 0;

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
        }

        singleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", singleBtn.getText().toString());

                singleBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        relationshipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", relationshipBtn.getText().toString());

                relationshipBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        marriedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", marriedBtn.getText().toString());

                marriedBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        divorcedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", divorcedBtn.getText().toString());

                divorcedBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        widowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", widowedBtn.getText().toString());

                widowedBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("relationship_status", otherBtn.getText().toString());

                otherBtn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThirdQuestionnaire thirdQuestionnaire = new ThirdQuestionnaire();
                        thirdQuestionnaire.setArguments(b);
                        FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                        ft3.replace(R.id.content, thirdQuestionnaire);
                        ft3.commit();
                    }
                }, 200);
            }
        });

        return  view;
    }

}
