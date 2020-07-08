package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.Adapter.AdapterLateness;
import com.skiplab.theselproject.models.LatenessReports;

import java.util.ArrayList;
import java.util.List;

public class LatenessReportActivity extends AppCompatActivity {

    Context context = LatenessReportActivity.this;

    AdapterLateness adapterLateness;
    List<LatenessReports> reportsList;
    RecyclerView recyclerView;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lateness_report);

        db = FirebaseDatabase.getInstance().getReference("latenessReports");

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        //Show latest post first, for the load from last
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        reportsList = new ArrayList<>();

        getReports();
    }

    private void getReports() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    LatenessReports reports = ds.getValue(LatenessReports.class);

                    reportsList.add(reports);
                }
                adapterLateness = new AdapterLateness(context, reportsList);
                recyclerView.setAdapter(adapterLateness);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
