package com.skiplab.theselproject.Home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.Adapter.AdapterMood;
import com.skiplab.theselproject.AddPost.SelectMood;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.Consultation.ConsultantsActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;

public class SelectCategory extends AppCompatActivity {

    Context mContext = SelectCategory.this;

    private ListView listView;

    private String[] items = new String[]{"Relationship", "Addiction", "Depression", "Parenting", "Career", "Low self-esteem",
            "Family", "Anxiety", "Pregnancy", "Business", "Weight Loss", "Fitness"};

    private ImageView closeBtn;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        listView = findViewById(R.id.navList);
        fab = findViewById(R.id.fab);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mContext, R.layout.list_category, R.id.listCategory, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (Common.isConnectedToTheInternet(mContext))
            {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, ConsultantsActivity.class);
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
            else
            {
                AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                        .setMessage("Please check your internet connection!")
                        .create();
                alertDialog.show();
            }
        });

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChatRoomsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
