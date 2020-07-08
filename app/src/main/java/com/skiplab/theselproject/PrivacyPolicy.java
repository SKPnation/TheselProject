package com.skiplab.theselproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PrivacyPolicy extends AppCompatActivity {

    Button termsOfServiceBtn;
    TextView googlePlayServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        googlePlayServices = findViewById(R.id.google_play_serviceTv);
        termsOfServiceBtn = findViewById(R.id.terms_of_service_btn);
        termsOfServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PrivacyPolicy.this, TermsOfService.class));
            }
        });

        googlePlayServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/"));
                startActivity(browserIntent);
            }
        });
    }
}
