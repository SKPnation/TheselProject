package com.skiplab.theselproject.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.skiplab.theselproject.Adapter.SectionsStatePagerAdapter;
import com.skiplab.theselproject.R;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";

    private ImageView backBtn;

    private SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        Toolbar toolbar = findViewById(R.id.optionsToolbar);
        setSupportActionBar(toolbar);

        backBtn = findViewById(R.id.backArrow);
        backBtn.setOnClickListener(v -> finish());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayout1);

        setupAccountSettingsList();
        setupFragments();

    }

    private void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), "Edit Profile"); //fragment 0
        pagerAdapter.addFragment(new AboutThesel(), "About Thesel"); //fragment 1
        pagerAdapter.addFragment(new PrivacyPolicyFragment(),"Privacy Policy"); //fragment 3
        pagerAdapter.addFragment(new TermsAndConditions(), "Terms And Conditions"); //fragment 4
        pagerAdapter.addFragment(new SignOutFragment(), "Sign Out"); //fragment 5

    }

    private void setupAccountSettingsList() {
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add("Edit Profile");
        options.add("About Thesel");
        options.add("Privacy Policy");
        options.add("Terms & Conditions");
        options.add("Sign Out");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
            setViewPager(position);

        });
    }

}
