package com.skiplab.theselproject.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.skiplab.theselproject.AddPost.CategoryActivity;
import com.skiplab.theselproject.Authentication.StaffRegisterActivity;
import com.skiplab.theselproject.ChatActivity;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.PaymentActivity;
import com.skiplab.theselproject.Profile.RequestsActivity;
import com.skiplab.theselproject.Questionnaire.QuestionnaireActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Search.ConsultantsActivity;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Cards;
import com.skiplab.theselproject.models.ReferenceNumber;
import com.skiplab.theselproject.models.Requests;
import com.skiplab.theselproject.models.Sessions;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;

public class AdapterRequests extends RecyclerView.Adapter<AdapterRequests.RequestHolder>{

    Context context;
    List<Requests> requestsList;
    DatabaseReference usersRef, sessionsRef, requestsRef;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    APIService apiService;

    int i = 0;

    public AdapterRequests(Context context, List<Requests> requestsList) {
        this.context = context;
        this.requestsList = requestsList;
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_requests, parent, false);

        return new RequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {

        //String client_id = "client_id";
        String no_client = "no client";

        final String fiveThousand = "N5,000";
        final String tenThousand = "N10,000";
        final String fifteenThousand = "N15,000";
        final String twentyThousand = "N20,000";
        final String twentyFiveThousand = "N25,000";
        final String thirtyThousand = "N30,000";
        final String thirtyFiveThousand = "N35,000";
        final String fortyThousand = "N40,000";
        final String fortyFiveThousand = "N45,000";
        final String fiftyThousand = "N50,000";
        final String fiftyFiveThousand = "N55,000";
        final String sixtyThousand = "N60,000";
        final String sixtyFiveThousand = "N65,000";
        final String seventyThousand = "N70,000";
        final String seventyFiveThousand = "N75,000";
        final String eightyThousand = "N80,000";
        final String eightyFiveThousand = "N85,000";
        final String ninetyThousand = "N90,000";
        final String ninetyFiveThousand = "N95,000";
        final String hundredThousand = "N100,000";

        holder.requestStatus.setText(requestsList.get(position).getStatus());
        holder.requestTime.setText(requestsList.get(position).getRequest_time());
        holder.requestPlan.setText(requestsList.get(position).getPlan());
        holder.acceptBtn.setText(requestsList.get(position).getStatus());

        usersRef.orderByKey().equalTo(fUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            if (user.getIsStaff().equals("true"))
                            {
                                holder.clientName.setText(requestsList.get(position).getClient_name());
                                holder.acceptBtn.setVisibility(View.VISIBLE);

                                if (requestsList.get(position).getPlan().equals("Free")){
                                    holder.requestPlan.setVisibility(View.VISIBLE);
                                }

                                sessionsRef.orderByKey().equalTo(fUser.getUid())
                                        .addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            Sessions sessions = ds.getValue(Sessions.class);
                                                            if (!sessions.getClient_id().equals(no_client)){

                                                                requestsRef.orderByChild("timestamp").equalTo(requestsList.get(position).getTimestamp())
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                                                    Requests requests = ds.getValue(Requests.class);

                                                                                    if (requests.getStatus().equals("accepted"))
                                                                                    {
                                                                                        holder.emptyBtn.setVisibility(View.VISIBLE);
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //..
                                                    }
                                                }
                                        );
                            }
                            else if (user.getIsStaff().equals("false"))
                            {
                                holder.counsellorName.setText(requestsList.get(position).getCounsellor_name());
                                holder.requestStatus.setVisibility(View.VISIBLE);
                                holder.requestPlan.setVisibility(View.VISIBLE);
                                holder.requestTimer.setVisibility(View.VISIBLE);
                                holder.requestTimer.setText(requestsList.get(position).getTimer());

                                i++;

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        requestsRef.orderByChild("timestamp").equalTo(requestsList.get(position).getTimestamp())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            Requests requests = ds.getValue(Requests.class);
                                                            String key = requests.getTimestamp();

                                                            requestsRef.child(key).child("timer").setValue("Expired")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                Handler handler = new Handler();
                                                                                handler.postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        ((RequestsActivity)context).setExpiry(requestsList.get(position).getTimer(), holder.requestTimer);

                                                                                        /*holder.requestTimer.setText(requestsList.get(position).getTimer());
                                                                                        holder.requestTimer.setVisibility(View.VISIBLE);*/
                                                                                    }
                                                                                },5000);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }, 900000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usersRef.orderByKey().equalTo(fUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    User user = ds.getValue(User.class);

                                    if (user.getIsStaff().equals("true"))
                                    {
                                        String client_id = requestsList.get(position).getClient_id();
                                        String plan = requestsList.get(position).getPlan();

                                        requestsRef.orderByChild("timestamp").equalTo(requestsList.get(position).getTimestamp())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            Requests requests = ds.getValue(Requests.class);
                                                            String key = requests.getTimestamp();

                                                            if (requests.getStatus().equals("pending"))
                                                            {
                                                                requestsRef.child(key).child("status").setValue("accepted");
                                                                sessionsRef.child(fUser.getUid()).child("client_id").setValue(client_id);

                                                                String accepted_message = "Proceed to payment and begin session";

                                                                sendNotification(requests.getClient_id(), requests.getCounsellor_name(), accepted_message);

                                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                                                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_counsellor_layout, null);

                                                                String onlineStatus = user.getOnlineStatus();

                                                                final ImageView currentCPhotoIv = mView.findViewById(R.id.cPictureIv);
                                                                final ImageView onlineIv = mView.findViewById(R.id.onlineIv);
                                                                final ImageView offlineIv = mView.findViewById(R.id.offlineIv);
                                                                final TextView cNameTv = mView.findViewById(R.id.cNameTv);
                                                                final TextView countryTv = mView.findViewById(R.id.cCountryTv);
                                                                final TextView costTv = mView.findViewById(R.id.cCostTv);
                                                                final TextView categoryTv1 = mView.findViewById(R.id.categoryTv1);
                                                                final TextView categoryTv2 = mView.findViewById(R.id.categoryTv2);
                                                                final TextView categoryTv3 = mView.findViewById(R.id.categoryTv3);
                                                                final TextView dayTimeTv = mView.findViewById(R.id.dayTime);
                                                                final TextView nightTimeTv = mView.findViewById(R.id.nightTime);
                                                                final Button profileBtn = mView.findViewById(R.id.profileBtn);
                                                                final Button chatBtn = mView.findViewById(R.id.requestBtn);

                                                                profileBtn.setVisibility(View.GONE);

                                                                cNameTv.setText(user.getUsername());
                                                                dayTimeTv.setText(user.getDayTime());
                                                                categoryTv1.setText(user.getCategory1());
                                                                categoryTv2.setText(user.getCategory2());
                                                                categoryTv3.setText(user.getCategory3());
                                                                countryTv.setText(user.getAddress().toUpperCase());
                                                                nightTimeTv.setText(user.getNightTime());
                                                                final String cost = String.valueOf(user.getCost());

                                                                if (cost.equals(String.valueOf(500000)))
                                                                    costTv.setText(fiveThousand);
                                                                else if (cost.equals(String.valueOf(1000000)))
                                                                    costTv.setText(tenThousand);
                                                                else if (cost.equals(String.valueOf(1500000)))
                                                                    costTv.setText(fifteenThousand);
                                                                else if (cost.equals(String.valueOf(2000000)))
                                                                    costTv.setText(twentyThousand);
                                                                else if (cost.equals(String.valueOf(2500000)))
                                                                    costTv.setText(twentyFiveThousand);
                                                                else if (cost.equals(String.valueOf(3000000)))
                                                                    costTv.setText(thirtyThousand);
                                                                else if (cost.equals(String.valueOf(3500000)))
                                                                    costTv.setText(thirtyFiveThousand);
                                                                else if (cost.equals(String.valueOf(4000000)))
                                                                    costTv.setText(fortyThousand);
                                                                else if (cost.equals(String.valueOf(4500000)))
                                                                    costTv.setText(fortyFiveThousand);
                                                                else if (cost.equals(String.valueOf(5000000)))
                                                                    costTv.setText(fiftyThousand);
                                                                else if (cost.equals(String.valueOf(5500000)))
                                                                    costTv.setText(fiftyFiveThousand);
                                                                else if (cost.equals(String.valueOf(6000000)))
                                                                    costTv.setText(sixtyThousand);
                                                                else if (cost.equals(String.valueOf(6500000)))
                                                                    costTv.setText(sixtyFiveThousand);
                                                                else if (cost.equals(String.valueOf(7000000)))
                                                                    costTv.setText(seventyThousand);
                                                                else if (cost.equals(String.valueOf(7500000)))
                                                                    costTv.setText(seventyFiveThousand);
                                                                else if (cost.equals(String.valueOf(8000000)))
                                                                    costTv.setText(eightyThousand);
                                                                else if (cost.equals(String.valueOf(8500000)))
                                                                    costTv.setText(eightyFiveThousand);
                                                                else if (cost.equals(String.valueOf(9000000)))
                                                                    costTv.setText(ninetyThousand);
                                                                else if (cost.equals(String.valueOf(9500000)))
                                                                    costTv.setText(ninetyFiveThousand);
                                                                else if (cost.equals(String.valueOf(10000000)))
                                                                    costTv.setText(hundredThousand);

                                                                if (onlineStatus.equals("online")){
                                                                    offlineIv.setVisibility(View.GONE);
                                                                    onlineIv.setVisibility(View.VISIBLE);
                                                                }
                                                                else {
                                                                    onlineIv.setVisibility(View.GONE);
                                                                    offlineIv.setVisibility(View.VISIBLE);
                                                                }

                                                                try {
                                                                    UniversalImageLoader.setImage(user.getProfile_photo(), currentCPhotoIv, null, "");
                                                                }catch (Exception e){
                                                                    //..
                                                                }

                                                                chatBtn.setText("Begin Session");


                                                                chatBtn.setOnClickListener(v12 -> {

                                                                    if(Common.isConnectedToTheInternet(context)){
                                                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                                                        progressDialog.setMessage("Please wait...");
                                                                        progressDialog.show();

                                                                        sessionsRef.orderByKey().equalTo(fUser.getUid())
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                            Sessions sessions = ds.getValue(Sessions.class);
                                                                                            if (!sessions.getClient_id().equals(no_client))
                                                                                            {
                                                                                                progressDialog.dismiss();

                                                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                                                                                View mView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_session_rules, null);

                                                                                                builder.setPositiveButton("BEGIN", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        Intent intent = new Intent(context, ChatActivity.class);
                                                                                                        intent.putExtra("hisUid", sessions.getClient_id());
                                                                                                        intent.putExtra("plan", requestsList.get(position).getPlan());
                                                                                                        context.startActivity(intent);
                                                                                                    }
                                                                                                });
                                                                                                builder.setView(mView);
                                                                                                builder.show();
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                progressDialog.dismiss();
                                                                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                                                builder1.setMessage("SORRY, YOU HAVE NO CLIENT AT THE MOMENT.");
                                                                                                builder1.show();
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                        //..
                                                                                    }
                                                                                });
                                                                    }
                                                                    else{
                                                                        Toast.makeText(context, "Please check your internet connection...", Toast.LENGTH_LONG).show();
                                                                    }
                                                                });


                                                                builder.setView(mView);
                                                                builder.show();
                                                            }
                                                            else
                                                            {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                                                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_counsellor_layout, null);

                                                                String onlineStatus = user.getOnlineStatus();

                                                                final ImageView currentCPhotoIv = mView.findViewById(R.id.cPictureIv);
                                                                final ImageView onlineIv = mView.findViewById(R.id.onlineIv);
                                                                final ImageView offlineIv = mView.findViewById(R.id.offlineIv);
                                                                final TextView cNameTv = mView.findViewById(R.id.cNameTv);
                                                                final TextView countryTv = mView.findViewById(R.id.cCountryTv);
                                                                final TextView costTv = mView.findViewById(R.id.cCostTv);
                                                                final TextView categoryTv1 = mView.findViewById(R.id.categoryTv1);
                                                                final TextView categoryTv2 = mView.findViewById(R.id.categoryTv2);
                                                                final TextView categoryTv3 = mView.findViewById(R.id.categoryTv3);
                                                                final TextView dayTimeTv = mView.findViewById(R.id.dayTime);
                                                                final TextView nightTimeTv = mView.findViewById(R.id.nightTime);
                                                                final Button chatBtn = mView.findViewById(R.id.requestBtn);

                                                                cNameTv.setText(user.getUsername());
                                                                dayTimeTv.setText(user.getDayTime());
                                                                categoryTv1.setText(user.getCategory1());
                                                                categoryTv2.setText(user.getCategory2());
                                                                categoryTv3.setText(user.getCategory3());
                                                                countryTv.setText(user.getAddress().toUpperCase());
                                                                nightTimeTv.setText(user.getNightTime());
                                                                final String cost = String.valueOf(user.getCost());

                                                                if (cost.equals(String.valueOf(500000)))
                                                                    costTv.setText(fiveThousand);
                                                                else if (cost.equals(String.valueOf(1000000)))
                                                                    costTv.setText(tenThousand);
                                                                else if (cost.equals(String.valueOf(1500000)))
                                                                    costTv.setText(fifteenThousand);
                                                                else if (cost.equals(String.valueOf(2000000)))
                                                                    costTv.setText(twentyThousand);
                                                                else if (cost.equals(String.valueOf(2500000)))
                                                                    costTv.setText(twentyFiveThousand);
                                                                else if (cost.equals(String.valueOf(3000000)))
                                                                    costTv.setText(thirtyThousand);
                                                                else if (cost.equals(String.valueOf(3500000)))
                                                                    costTv.setText(thirtyFiveThousand);
                                                                else if (cost.equals(String.valueOf(4000000)))
                                                                    costTv.setText(fortyThousand);
                                                                else if (cost.equals(String.valueOf(4500000)))
                                                                    costTv.setText(fortyFiveThousand);
                                                                else if (cost.equals(String.valueOf(5000000)))
                                                                    costTv.setText(fiftyThousand);
                                                                else if (cost.equals(String.valueOf(5500000)))
                                                                    costTv.setText(fiftyFiveThousand);
                                                                else if (cost.equals(String.valueOf(6000000)))
                                                                    costTv.setText(sixtyThousand);
                                                                else if (cost.equals(String.valueOf(6500000)))
                                                                    costTv.setText(sixtyFiveThousand);
                                                                else if (cost.equals(String.valueOf(7000000)))
                                                                    costTv.setText(seventyThousand);
                                                                else if (cost.equals(String.valueOf(7500000)))
                                                                    costTv.setText(seventyFiveThousand);
                                                                else if (cost.equals(String.valueOf(8000000)))
                                                                    costTv.setText(eightyThousand);
                                                                else if (cost.equals(String.valueOf(8500000)))
                                                                    costTv.setText(eightyFiveThousand);
                                                                else if (cost.equals(String.valueOf(9000000)))
                                                                    costTv.setText(ninetyThousand);
                                                                else if (cost.equals(String.valueOf(9500000)))
                                                                    costTv.setText(ninetyFiveThousand);
                                                                else if (cost.equals(String.valueOf(10000000)))
                                                                    costTv.setText(hundredThousand);

                                                                if (onlineStatus.equals("online")){
                                                                    offlineIv.setVisibility(View.GONE);
                                                                    onlineIv.setVisibility(View.VISIBLE);
                                                                }
                                                                else {
                                                                    onlineIv.setVisibility(View.GONE);
                                                                    offlineIv.setVisibility(View.VISIBLE);
                                                                }

                                                                try {
                                                                    UniversalImageLoader.setImage(user.getProfile_photo(), currentCPhotoIv, null, "");
                                                                }catch (Exception e){
                                                                    //..
                                                                }

                                                                chatBtn.setText("Begin Session");

                                                                chatBtn.setOnClickListener(v12 -> {

                                                                    if(Common.isConnectedToTheInternet(context)){
                                                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                                                        progressDialog.setMessage("Please wait...");
                                                                        progressDialog.show();

                                                                        sessionsRef.orderByKey().equalTo(fUser.getUid())
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                            Sessions sessions = ds.getValue(Sessions.class);
                                                                                            if (!sessions.getClient_id().equals(no_client))
                                                                                            {
                                                                                                progressDialog.dismiss();

                                                                                                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                                                                                View mView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_session_rules, null);

                                                                                                builder.setPositiveButton("BEGIN", new DialogInterface.OnClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        Intent intent = new Intent(context, ChatActivity.class);
                                                                                                        intent.putExtra("hisUid", sessions.getClient_id());
                                                                                                        intent.putExtra("plan", requestsList.get(position).getPlan());
                                                                                                        context.startActivity(intent);
                                                                                                    }
                                                                                                });
                                                                                                builder.setView(mView);
                                                                                                builder.show();
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                progressDialog.dismiss();

                                                                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                                                builder1.setMessage("Sorry, you have no client at the moment.");
                                                                                                builder1.show();
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                        //..
                                                                                    }
                                                                                });
                                                                    }
                                                                    else{
                                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                        builder1.setMessage("Please check your internet connection...");
                                                                        builder1.show();
                                                                    }
                                                                });


                                                                builder.setView(mView);
                                                                builder.show();
                                                            }
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

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
            }
        });

        holder.emptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usersRef.orderByKey().equalTo(fUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    User user = ds.getValue(User.class);
                                    if (user.getIsStaff().equals("true"))
                                    {
                                        requestsRef.orderByChild("timestamp").equalTo(requestsList.get(position).getTimestamp())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            Requests requests = ds.getValue(Requests.class);

                                                            if (requests.getStatus().equals("pending"))
                                                            {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                builder.setTitle("EMPTY YOUR CLIENT SEAT");
                                                                builder.setMessage("Are you sure?");
                                                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        int i = 0;

                                                                        sessionsRef.child(fUser.getUid()).child("client_id").setValue(no_client);

                                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                        builder1.setMessage("Your client seat is now empty");
                                                                        builder1.show();

                                                                        i++;

                                                                        Handler handler1 = new Handler();
                                                                        handler1.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                context.startActivity(new Intent(context, DashboardActivity.class));
                                                                                Toast.makeText(context, "Your client seat is now empty", Toast.LENGTH_LONG).show();

                                                                            }
                                                                        }, 2000);

                                                                    }
                                                                });

                                                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                                builder.show();
                                                            }
                                                            else
                                                            {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                builder.setTitle("EMPTY YOUR CLIENT SEAT");
                                                                builder.setMessage("Are you sure?");

                                                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        int i = 0;


                                                                        sessionsRef.child(fUser.getUid()).child("client_id").setValue(no_client);

                                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                        builder1.setMessage("Your client seat is now empty");
                                                                        builder1.show();

                                                                        i++;

                                                                        Handler handler1 = new Handler();
                                                                        handler1.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                context.startActivity(new Intent(context, DashboardActivity.class));
                                                                                Toast.makeText(context, "Your client seat is now empty", Toast.LENGTH_LONG).show();

                                                                            }
                                                                        }, 2000);

                                                                    }
                                                                });

                                                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });

                                                                builder.show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Common.isConnectedToTheInternet(context))
                {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("loading...");

                    usersRef.orderByKey().equalTo(mAuth.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = ds.getValue(User.class);
                                        if (user.getIsStaff().equals("false"))
                                        {
                                            String username = user.getUsername();
                                            String uDp = user.getProfile_photo();

                                            Query query1 = usersRef.orderByKey().equalTo(requestsList.get(position).getCounsellor_id());
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                        User userStaff = ds.getValue(User.class);
                                                        String staffName = userStaff.getUsername();

                                                        if (userStaff.getOnlineStatus().equals("offline")){
                                                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                                            dialog.setMessage(staffName.toUpperCase() + " IS OFFLINE.");

                                                            dialog.show();
                                                        }
                                                        else if (userStaff.getOnlineStatus().equals("deactivated")){
                                                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                                            dialog.setMessage("SORRY, "+staffName.toUpperCase() + " IS DEACTIVATED AT THE MOMENT.");

                                                            dialog.show();
                                                        }
                                                        else
                                                        {
                                                            requestsRef.orderByChild("timestamp").equalTo(requestsList.get(position).getTimestamp())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                Requests requests = ds.getValue(Requests.class);
                                                                                //String key = requests.getTimestamp();

                                                                                if (requests.getStatus().equals("accepted"))
                                                                                {
                                                                                    sessionsRef.orderByChild("counsellor_id").equalTo(requests.getCounsellor_id())
                                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                                                        Sessions sessions = ds.getValue(Sessions.class);

                                                                                                        if (sessions.getClient_id().equals(fUser.getUid()))
                                                                                                        {
                                                                                                            //Toast.makeText(context, ""+sessions.getClient_id(), Toast.LENGTH_SHORT).show();

                                                                                                            if (requests.getTimer().equals("Expired"))
                                                                                                            {
                                                                                                                progressDialog.dismiss();

                                                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                                                builder.setTitle("THIS REQUEST HAS EXPIRED");
                                                                                                                builder.setMessage("Send a new request");
                                                                                                                builder.show();
                                                                                                            }
                                                                                                            else
                                                                                                            {
                                                                                                                if (requests.getPlan().equals("Paid"))
                                                                                                                {
                                                                                                                    progressDialog.dismiss();

                                                                                                                    Long cost = userStaff.getCost();

                                                                                                                    Intent intent = new Intent(context, PaymentActivity.class);
                                                                                                                    intent.putExtra("cost", cost);
                                                                                                                    intent.putExtra("counsellor_id", requests.getCounsellor_id());
                                                                                                                    intent.putExtra("plan", requestsList.get(position).getPlan());
                                                                                                                    context.startActivity(intent);
                                                                                                                }
                                                                                                                else
                                                                                                                {
                                                                                                                    progressDialog.dismiss();

                                                                                                                    HashMap<String,Object> hashMap = new HashMap<>();
                                                                                                                    hashMap.put("uid",fUser.getUid());

                                                                                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trials");
                                                                                                                    reference.child(fUser.getUid()).setValue(hashMap);

                                                                                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                                                                                    intent.putExtra("hisUid", requests.getCounsellor_id());
                                                                                                                    intent.putExtra("plan", requestsList.get(position).getPlan());
                                                                                                                    context.startActivity(intent);
                                                                                                                }

                                                                                                            }
                                                                                                        }
                                                                                                        else{
                                                                                                            progressDialog.dismiss();

                                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                                            builder.setMessage("That session has ended");
                                                                                                            builder.show();
                                                                                                        }
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                    //..
                                                                                                }
                                                                                            });


                                                                                }
                                                                                else
                                                                                {
                                                                                    progressDialog.dismiss();

                                                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                                    builder1.setMessage("The consultant has not accepted this request");
                                                                                    builder1.show();
                                                                                }
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

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    //..
                                                }
                                            });


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please check your internet connection");
                    builder.show();

                    return;
                }
            }
        });

    }

    private void sendNotification(String client_id, String counsellor_name, String accepted_message) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(mAuth.getUid(), counsellor_name+": "+ accepted_message, counsellor_name+" ACCEPTED YOUR REQUEST", client_id, R.mipmap.ic_launcher2);

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

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder{

        TextView clientName, counsellorName, requestStatus, requestTime, requestPlan, requestTimer;
        Button acceptBtn, emptyBtn;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);

            clientName = itemView.findViewById( R.id.request_client_name );
            counsellorName = itemView.findViewById( R.id.request_counsellor_name );
            requestStatus = itemView.findViewById( R.id.request_status );
            requestTime = itemView.findViewById(R.id.request_time);
            requestTimer = itemView.findViewById(R.id.request_timer);
            requestPlan = itemView.findViewById(R.id.request_plan);
            acceptBtn = itemView.findViewById( R.id.acceptBtn );
            emptyBtn = itemView.findViewById(R.id.emptyBtn);
        }
    }
}
