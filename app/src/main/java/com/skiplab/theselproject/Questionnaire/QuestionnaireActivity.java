package com.skiplab.theselproject.Questionnaire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.Home.HomeFragment;
import com.skiplab.theselproject.R;

public class QuestionnaireActivity extends AppCompatActivity {

    Context mContext = QuestionnaireActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        FirstQuestionnaire firstQuestionnaire = new FirstQuestionnaire();
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().add(R.id.content, firstQuestionnaire).commit();

        /*QuestionnaireFragment fragment1 = new QuestionnaireFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();*/
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(mContext, DashboardActivity.class));
        finish();

        super.onBackPressed();
    }
}
