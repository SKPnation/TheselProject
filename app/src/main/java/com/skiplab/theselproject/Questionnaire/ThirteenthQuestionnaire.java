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
public class ThirteenthQuestionnaire extends Fragment {

    Button radio_btn, friend_family_member_btn, poster_billboard_btn, snapchat_btn, TV_ad_btn, social_btn, youtube_btn, podcast_btn, other_btn;
    int i = 0;

    public ThirteenthQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thirteenth_questionnaire, container, false);

        radio_btn = view.findViewById(R.id.radio_btn);
        friend_family_member_btn = view.findViewById(R.id.friend_family_member_btn);
        poster_billboard_btn = view.findViewById(R.id.poster_billboard_btn);
        snapchat_btn = view.findViewById(R.id.snapchat_btn);
        TV_ad_btn = view.findViewById(R.id.TV_ad_btn);
        social_btn = view.findViewById(R.id.social_btn);
        youtube_btn = view.findViewById(R.id.youtube_btn);
        podcast_btn = view.findViewById(R.id.podcast_btn);
        other_btn = view.findViewById(R.id.other_btn);

        Bundle b = this.getArguments();
        if(b != null){
            String s =b.getString("finance");
        }

        radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", radio_btn.getText().toString());

                radio_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        friend_family_member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", friend_family_member_btn.getText().toString());

                friend_family_member_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        poster_billboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", poster_billboard_btn.getText().toString());

                poster_billboard_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        snapchat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", snapchat_btn.getText().toString());

                snapchat_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        TV_ad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", TV_ad_btn.getText().toString());

                TV_ad_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        social_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", social_btn.getText().toString());

                social_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        youtube_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", youtube_btn.getText().toString());

                youtube_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        podcast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", podcast_btn.getText().toString());

                podcast_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        other_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", other_btn.getText().toString());

                other_btn.setBackgroundColor(Color.parseColor("#C3BD2E"));

                i++;

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                        fourteenthQuestionnaire.setArguments(b);
                        FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                        ft14.replace(R.id.content, fourteenthQuestionnaire);
                        ft14.commit();
                    }
                }, 700);
            }
        });

        return view;
    }

}
