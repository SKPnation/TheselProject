package com.skiplab.theselproject.Search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.Adapter.AdapterUsers;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Profile.RequestsActivity;
import com.skiplab.theselproject.Questionnaire.QuestionnaireActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Requests;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ConsultantsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<User> userList = new ArrayList<>();

    //firebase auth
    FirebaseAuth firebaseAuth;
    FirebaseUser fUser;

    FirebaseDatabase db;
    DatabaseReference usersRef, sessionsRef, requestsRef, pinsRef;

    private ProgressBar mProgressBar;

    private ImageView backBtn;

    Context mContext = ConsultantsActivity.this;
    APIService apiService;

    String requestPlan;
    String adminID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultants);

        final Intent intent = getIntent();
        requestPlan = intent.getStringExtra("plan");

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users");
        sessionsRef = db.getReference("sessions");
        requestsRef = db.getReference("requests");
        pinsRef = db.getReference("pins");

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        backBtn = findViewById(R.id.backArrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        swipeRefreshLayout = findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToTheInternet(mContext))
                {
                    getAllUsers();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please check your internet connection");
                    builder.show();

                    return;
                }
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToTheInternet(mContext))
                {
                    getAllUsers();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Please check your internet connection");
                    builder.show();

                    return;
                }
            }
        } );
    }

    private void getAllUsers()
    {
        Query queryUsers = usersRef.orderByChild("isStaff").equalTo("true");
        queryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    mProgressBar.setVisibility(View.GONE);

                    userList.add(user);
                }
                adapterUsers = new AdapterUsers(mContext, userList);
                recyclerView.setAdapter(adapterUsers);
                swipeRefreshLayout.setRefreshing( false );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }


    public void sendRequest(String hisUID, String cltName, String username)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final View mView =  LayoutInflater.from(mContext).inflate(R.layout.request_pin_layout, null);

        builder.setTitle("INSERT YOUR REQUEST PIN");

        EditText requestPin = mView.findViewById(R.id.pinEt);
        Button createBtn = mView.findViewById(R.id.createBtn);
        Button requestButton = mView.findViewById(R.id.requestBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mContext, QuestionnaireActivity.class));
                finish();

            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pinsRef.orderByKey().equalTo(fUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    if (ds.exists()){
                                        if (ds.hasChild("requestPin"))
                                        {
                                            String pinStr = ds.child("requestPin").getValue().toString();

                                            if (requestPin.getText().toString().isEmpty())
                                            {
                                                requestPin.setError("Your request PIN is required");
                                                requestPin.requestFocus();
                                            }
                                            else if (requestPin.getText().toString().length() < 4 || requestPin.getText().toString().length() > 4) {
                                                requestPin.setError("Your PIN must be a 4 digits number");
                                                requestPin.requestFocus();
                                            }
                                            else if (!pinStr.equals(requestPin.getText().toString()))
                                            {
                                                requestPin.setError("Wrong PIN");
                                                requestPin.requestFocus();
                                            }
                                            else
                                            {
                                                sessionsRef.orderByKey().equalTo(hisUID)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                                                {
                                                                    Sessions sessions = ds.getValue(Sessions.class);

                                                                    String message2 = "A user has requested for you. ";
                                                                    String message3 = "New request for ";
                                                                    sendNotification1(hisUID, "Thesel Team", message2, username);

                                                                    String request_number = String.valueOf( System.currentTimeMillis() );
                                                                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                                    calendar.setTimeInMillis(Long.parseLong(request_number));
                                                                    String sTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                                    Requests requests = new Requests();
                                                                    requests.setClient_id(fUser.getUid());
                                                                    requests.setCounsellor_id(hisUID);
                                                                    requests.setClient_name(username);
                                                                    requests.setCounsellor_name(cltName);
                                                                    requests.setStatus("pending");
                                                                    requests.setPlan(requestPlan);
                                                                    requests.setTimestamp(request_number);
                                                                    requests.setRequest_time(sTime);
                                                                    requests.setTimer("");

                                                                    //Submit to Firebase
                                                                    //Use System.CurrentMilli to key
                                                                    requestsRef.child(request_number).setValue(requests);

                                                                    Long requestsCount = sessions.getRequests();

                                                                    sessionsRef.child(hisUID).child("requests").setValue(requestsCount+1)
                                                                            .addOnCompleteListener(task -> sendNotification2(adminID, username, message3+ cltName))
                                                                            .addOnFailureListener(e -> Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show());

                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                                    builder.setTitle("READ THE MESSAGE");
                                                                    builder.setMessage("If your PENDING request to "+ cltName + " is not ACCEPTED within 15 minutes, send another request or request for another consultant." +
                                                                            " This request will expire in 15 minutes.");

                                                                    builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Intent intent = new Intent(mContext, RequestsActivity.class);
                                                                            intent.putExtra("myUid", fUser.getUid());
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    });

                                                                    builder.show();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                //..
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        });

        builder.setView(mView);
        builder.show();
    }

    private void sendNotification1(String hisUID, String thesel_team, String message2, String username) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds.getString("token"));
                    Data data = new Data(fUser.getUid(), thesel_team+": "+ message2, "NEW REQUEST FROM "+username, hisUID, R.mipmap.ic_launcher2);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //..
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    //..
                                }
                            });


                }
            }
        });
    }

    private void sendNotification2(String adminID, String username, String message3) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(fUser.getUid(), message3, username, adminID, R.mipmap.ic_launcher2);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //..
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    //Toast.makeText(context, "FAILED REQUEST!!!", Toast.LENGTH_LONG).show();
                                }
                            });


                }
            }
        });
    }
}
