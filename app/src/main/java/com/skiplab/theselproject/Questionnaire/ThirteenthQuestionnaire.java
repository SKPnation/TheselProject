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
public class ThirteenthQuestionnaire extends Fragment {

    Button radio_btn, friend_family_member_btn, poster_billboard_btn, snapchat_btn, TV_ad_btn, social_btn, youtube_btn, podcast_btn, other_btn;

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

            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        }

        radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", radio_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        friend_family_member_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", friend_family_member_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        poster_billboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", poster_billboard_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        snapchat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", snapchat_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        TV_ad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", TV_ad_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        social_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", social_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        youtube_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", youtube_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        podcast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", podcast_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        other_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("referred", other_btn.getText().toString());

                FourteenthQuestionnaire fourteenthQuestionnaire = new FourteenthQuestionnaire();
                fourteenthQuestionnaire.setArguments(b);
                FragmentTransaction ft14 = getFragmentManager().beginTransaction();
                ft14.replace(R.id.content, fourteenthQuestionnaire);
                ft14.commit();
            }
        });

        return view;
    }

}
