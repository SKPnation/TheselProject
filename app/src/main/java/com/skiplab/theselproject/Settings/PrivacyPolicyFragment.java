package com.skiplab.theselproject.Settings;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyPolicyFragment extends Fragment {

    TextView googlePlayServices;
    ImageView back_btn;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        back_btn = view.findViewById(R.id.backArrow);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        googlePlayServices = view.findViewById(R.id.google_play_serviceTv);

        googlePlayServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

}
