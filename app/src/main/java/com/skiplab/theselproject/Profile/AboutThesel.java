package com.skiplab.theselproject.Profile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutThesel extends Fragment {


    public AboutThesel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_thesel, container, false);
    }

}
