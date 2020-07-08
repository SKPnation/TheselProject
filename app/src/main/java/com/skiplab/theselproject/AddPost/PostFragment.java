package com.skiplab.theselproject.AddPost;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skiplab.theselproject.Adapter.AdapterMood;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    RecyclerView recyclerView;
    String[] moods = {"Happy","Sad","Frustrated","Exhausted","Nostalgic","Angry","Proud","Supportive","Optimistic","Tired","Lonely","Down","Anxious","Overwhelmed","Depressed","Afraid","Confused","Nothing","Annoyed","Worried","Meh","Shocked","Loved","Embarrassed","Amused","Positive","Relaxed","Ecstatic","Calm","Chilled","Thankful","Determined","Jealous","Irritated","Nervous","Disgust"};

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_post, container, false);

        recyclerView = view.findViewById(R.id.recycler_moods);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AdapterMood(getActivity(), moods));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
    }
}
