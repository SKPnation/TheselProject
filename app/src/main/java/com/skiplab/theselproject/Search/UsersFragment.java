package com.skiplab.theselproject.Search;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.Interface.ItemClickListener;
import com.skiplab.theselproject.Profile.RequestsActivity;
import com.skiplab.theselproject.Questionnaire.QuestionnaireActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.ViewHolders.UsersViewHolder;
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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    UsersFragment frag;

    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference usersRef, sessionsRef, pinsRef, requestsRef;
    FirebaseRecyclerAdapter<User, UsersViewHolder> adapter, searchAdapter;

    //firebase auth
    FirebaseAuth mAuth;

    private TextView usersFrgamentTitle;
    private ProgressBar mProgressBar;

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

    Bundle b;
    String adminID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    APIService apiService;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        b = this.getArguments();

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        pinsRef = FirebaseDatabase.getInstance().getReference("pins");
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");


        usersFrgamentTitle = view.findViewById(R.id.users_fragment_title);
        mProgressBar = view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToTheInternet(getContext()))
                {
                    getAllUsers();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                if(Common.isConnectedToTheInternet(getContext()))
                {
                    getAllUsers();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please check your internet connection");
                    builder.show();

                    return;
                }
            }
        } );

        return view;
    }

    private void getAllUsers()
    {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = usersRef.orderByKey().equalTo(fUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    User userClient = ds.getValue(User.class);
                    String isStaff = userClient.getIsStaff();
                    if (isStaff.equals("false"))
                    {
                        usersFrgamentTitle.setText("Thesel Consultants");
                        adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                                User.class,
                                R.layout.row_users,
                                UsersViewHolder.class,
                                usersRef.orderByChild("isStaff").equalTo("true")
                        ) {
                            @Override
                            protected void populateViewHolder(UsersViewHolder viewHolder, User user, int position) {

                                mProgressBar.setVisibility(View.GONE);

                                String hisUID = adapter.getItem(position).getUid();

                                viewHolder.mNameTv.setText(user.getUsername());
                                viewHolder.mCategoryTv1.setText(user.getCategory1());
                                viewHolder.mCategoryTv2.setText(user.getCategory2());
                                viewHolder.mCategoryTv3.setText(user.getCategory3());

                                try {
                                    UniversalImageLoader.setImage(user.getProfile_photo(), viewHolder.mAvatarIv, null, "");
                                }
                                catch (Exception e){
                                }

                                String cost = Long.valueOf(user.getCost()).toString();

                                if (cost.equals(String.valueOf(20000)))
                                    viewHolder.mCostTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1000000)))
                                    viewHolder.mCostTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1500000)))
                                    viewHolder.mCostTv.setText(fifteenThousand);
                                else if (cost.equals(String.valueOf(2000000)))
                                    viewHolder.mCostTv.setText(twentyThousand);
                                else if (cost.equals(String.valueOf(2500000)))
                                    viewHolder.mCostTv.setText(twentyFiveThousand);
                                else if (cost.equals(String.valueOf(3000000)))
                                    viewHolder.mCostTv.setText(thirtyThousand);
                                else if (cost.equals(String.valueOf(3500000)))
                                    viewHolder.mCostTv.setText(thirtyFiveThousand);
                                else if (cost.equals(String.valueOf(4000000)))
                                    viewHolder.mCostTv.setText(fortyThousand);
                                else if (cost.equals(String.valueOf(4500000)))
                                    viewHolder.mCostTv.setText(fortyFiveThousand);
                                else if (cost.equals(String.valueOf(5000000)))
                                    viewHolder.mCostTv.setText(fiftyThousand);
                                else if (cost.equals(String.valueOf(5500000)))
                                    viewHolder.mCostTv.setText(fiftyFiveThousand);
                                else if (cost.equals(String.valueOf(6000000)))
                                    viewHolder.mCostTv.setText(sixtyThousand);
                                else if (cost.equals(String.valueOf(6500000)))
                                    viewHolder.mCostTv.setText(sixtyFiveThousand);
                                else if (cost.equals(String.valueOf(7000000)))
                                    viewHolder.mCostTv.setText(seventyThousand);
                                else if (cost.equals(String.valueOf(7500000)))
                                    viewHolder.mCostTv.setText(seventyFiveThousand);
                                else if (cost.equals(String.valueOf(8000000)))
                                    viewHolder.mCostTv.setText(eightyThousand);
                                else if (cost.equals(String.valueOf(8500000)))
                                    viewHolder.mCostTv.setText(eightyFiveThousand);
                                else if (cost.equals(String.valueOf(9000000)))
                                    viewHolder.mCostTv.setText(ninetyThousand);
                                else if (cost.equals(String.valueOf(9500000)))
                                    viewHolder.mCostTv.setText(ninetyFiveThousand);
                                else if (cost.equals(String.valueOf(10000000)))
                                    viewHolder.mCostTv.setText(hundredThousand);

                                viewHolder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                                        final View mView =  LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.request_counsellor_layout, null);

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
                                        final Button requestBtn = mView.findViewById(R.id.requestBtn);
                                        final Button profileBtn = mView.findViewById(R.id.profileBtn);

                                        if (!user.getCategory1().isEmpty())
                                            categoryTv1.setVisibility(View.VISIBLE);
                                        if (!user.getCategory2().isEmpty())
                                            categoryTv2.setVisibility(View.VISIBLE);
                                        if (!user.getCategory3().isEmpty())
                                            categoryTv3.setVisibility(View.VISIBLE);

                                        cNameTv.setText(user.getUsername());
                                        dayTimeTv.setText(user.getDayTime());
                                        categoryTv1.setText(user.getCategory1());
                                        categoryTv2.setText(user.getCategory2());
                                        categoryTv3.setText(user.getCategory3());
                                        countryTv.setText(user.getAddress().toUpperCase());
                                        nightTimeTv.setText(user.getNightTime());
                                        final String cost = String.valueOf(user.getCost());

                                        if (cost.equals(String.valueOf(20000)))
                                            costTv.setText(tenThousand);
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

                                        profileBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setCancelable(false);
                                                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_profile, null);

                                                TextView nameTv = mView.findViewById(R.id.counsellor_name);
                                                EditText descEt = mView.findViewById(R.id.counsellor_desc);
                                                TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                                                TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                                                TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                                                nameTv.setText(user.getUsername());
                                                if (!user.getCategory1().isEmpty()){
                                                    catTv1.setVisibility(View.VISIBLE);
                                                    catTv1.setText(user.getCategory1());
                                                }
                                                if (!user.getCategory2().isEmpty()){
                                                    catTv2.setVisibility(View.VISIBLE);
                                                    catTv2.setText(user.getCategory2());
                                                }
                                                if (!user.getCategory3().isEmpty()){
                                                    catTv3.setVisibility(View.VISIBLE);
                                                    catTv3.setText(user.getCategory3());
                                                }

                                                FirebaseDatabase.getInstance().getReference("profiles").orderByKey().equalTo(hisUID)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                                                    if (ds.exists()){

                                                                        ProgressDialog pd = new ProgressDialog(getActivity());
                                                                        pd.setMessage("Please wait...");
                                                                        pd.show();

                                                                        String desc = ds.child("description").getValue().toString();

                                                                        if (!desc.equals("")){
                                                                            pd.dismiss();
                                                                            descEt.setText(desc);
                                                                        }

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                descEt.setFocusable(false);
                                                descEt.setFocusableInTouchMode(false);

                                                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                builder.setView(mView);
                                                builder.show();
                                            }
                                        });

                                        requestBtn.setText("Request");
                                        requestBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(Common.isConnectedToTheInternet(getActivity())){
                                                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                                    progressDialog.setMessage("Please wait...");
                                                    progressDialog.show();

                                                    if (user.getOnlineStatus().equals("offline")){
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                        dialog.setMessage(user.getUsername().toUpperCase() + " IS OFFLINE.");

                                                        dialog.show();
                                                    }
                                                    else if (user.getOnlineStatus().equals("deactivated")){
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                        dialog.setMessage("SORRY, "+user.getUsername().toUpperCase() + " IS DEACTIVATED AT THE MOMENT.");

                                                        dialog.show();
                                                    }
                                                    else
                                                    {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                        final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.request_pin_layout, null);

                                                        builder.setTitle("INSERT YOUR REQUEST PIN");

                                                        EditText requestPin = mView.findViewById(R.id.pinEt);
                                                        Button createBtn = mView.findViewById(R.id.createBtn);
                                                        Button requestButton = mView.findViewById(R.id.requestBtn);

                                                        createBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                getActivity().startActivity(new Intent(getActivity(), QuestionnaireActivity.class));
                                                                getActivity().finish();
                                                            }
                                                        });

                                                        requestButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                pinsRef.orderByKey().equalTo(mAuth.getCurrentUser().getUid())
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
                                                                                                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                                                                                                {
                                                                                                                    Sessions sessions = ds.getValue(Sessions.class);

                                                                                                                    String message2 = "A user has requested for you. ";
                                                                                                                    String message3 = "New request for ";
                                                                                                                    sendNotification1(hisUID, "Thesel Team", message2, userClient.getUsername());

                                                                                                                    String request_number = String.valueOf( System.currentTimeMillis() );
                                                                                                                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                                                                                    calendar.setTimeInMillis(Long.parseLong(request_number));
                                                                                                                    String sTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                                                                                    if(b != null)
                                                                                                                    {
                                                                                                                        String s =b.getString("plan");

                                                                                                                        Requests requests = new Requests();
                                                                                                                        requests.setClient_id(mAuth.getCurrentUser().getUid());
                                                                                                                        requests.setCounsellor_id(hisUID);
                                                                                                                        requests.setClient_name(userClient.getUsername());
                                                                                                                        requests.setCounsellor_name(user.getUsername());
                                                                                                                        requests.setStatus("pending");
                                                                                                                        requests.setPlan(s);
                                                                                                                        requests.setTimestamp(request_number);
                                                                                                                        requests.setRequest_time(sTime);
                                                                                                                        requests.setTimer("");

                                                                                                                        //Submit to Firebase
                                                                                                                        //Use System.CurrentMilli to key
                                                                                                                        requestsRef.child(request_number).setValue(requests);

                                                                                                                        Long requestsCount = sessions.getRequests();

                                                                                                                        sessionsRef.child(hisUID).child("requests").setValue(requestsCount+1)
                                                                                                                                .addOnCompleteListener(task -> sendNotification2(adminID, userClient.getUsername(), message3+ user.getUsername()))
                                                                                                                                .addOnFailureListener(e -> Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show());

                                                                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                                                        builder.setTitle("READ THE MESSAGE");
                                                                                                                        builder.setMessage("If your PENDING request to "+ user.getUsername() + " is not ACCEPTED within 15 minutes, send another request or request for another consultant." +
                                                                                                                                " This request will expire after 15 minutes.");

                                                                                                                        builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                                                                                                                            @Override
                                                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                                                Intent intent = new Intent(getActivity(), RequestsActivity.class);
                                                                                                                                intent.putExtra("myUid", mAuth.getCurrentUser().getUid());
                                                                                                                                getActivity().startActivity(intent);
                                                                                                                            }
                                                                                                                        });

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
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                //..
                                                                            }
                                                                        });

                                                            }
                                                        });

                                                        builder.setView(mView);
                                                        builder.show();
                                                    }

                                                }
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }
                                });
                            }
                        };

                        //Set Adapter
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing( false );
                    }
                    else if (isStaff.equals("true"))
                    {
                        usersFrgamentTitle.setText("Users");
                        adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                                User.class,
                                R.layout.row_users,
                                UsersViewHolder.class,
                                usersRef.orderByChild("isStaff").equalTo("false")
                        ) {
                            @Override
                            protected void populateViewHolder(UsersViewHolder viewHolder, User user, int position) {

                                mProgressBar.setVisibility(View.GONE);

                                viewHolder.mNameTv.setText(user.getUsername());
                                try {
                                    UniversalImageLoader.setImage(user.getProfile_photo(), viewHolder.mAvatarIv, null, "");
                                }
                                catch (Exception e){
                                }

                                viewHolder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        //..
                                    }
                                });
                            }
                        };
                        //Set Adapter
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing( false );

                    }
                    else if (isStaff.equals("admin"))
                    {
                        usersFrgamentTitle.setText("Thesel Consultants");
                        adapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                                User.class,
                                R.layout.row_users,
                                UsersViewHolder.class,
                                usersRef.orderByChild("isStaff").equalTo("true")
                        ) {
                            @Override
                            protected void populateViewHolder(UsersViewHolder viewHolder, User user, int position) {

                                mProgressBar.setVisibility(View.GONE);
                                viewHolder.mEditBtn.setVisibility(View.VISIBLE);

                                String hisUID = adapter.getItem(position).getUid();

                                viewHolder.mNameTv.setText(user.getUsername());
                                viewHolder.mCategoryTv1.setText(user.getCategory1());
                                viewHolder.mCategoryTv2.setText(user.getCategory2());
                                viewHolder.mCategoryTv3.setText(user.getCategory3());

                                try {
                                    UniversalImageLoader.setImage(user.getProfile_photo(), viewHolder.mAvatarIv, null, "");
                                }
                                catch (Exception e){
                                }

                                String cost = Long.valueOf(user.getCost()).toString();

                                if (cost.equals(String.valueOf(20000)))
                                    viewHolder.mCostTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1000000)))
                                    viewHolder.mCostTv.setText(tenThousand);
                                else if (cost.equals(String.valueOf(1500000)))
                                    viewHolder.mCostTv.setText(fifteenThousand);
                                else if (cost.equals(String.valueOf(2000000)))
                                    viewHolder.mCostTv.setText(twentyThousand);
                                else if (cost.equals(String.valueOf(2500000)))
                                    viewHolder.mCostTv.setText(twentyFiveThousand);
                                else if (cost.equals(String.valueOf(3000000)))
                                    viewHolder.mCostTv.setText(thirtyThousand);
                                else if (cost.equals(String.valueOf(3500000)))
                                    viewHolder.mCostTv.setText(thirtyFiveThousand);
                                else if (cost.equals(String.valueOf(4000000)))
                                    viewHolder.mCostTv.setText(fortyThousand);
                                else if (cost.equals(String.valueOf(4500000)))
                                    viewHolder.mCostTv.setText(fortyFiveThousand);
                                else if (cost.equals(String.valueOf(5000000)))
                                    viewHolder.mCostTv.setText(fiftyThousand);
                                else if (cost.equals(String.valueOf(5500000)))
                                    viewHolder.mCostTv.setText(fiftyFiveThousand);
                                else if (cost.equals(String.valueOf(6000000)))
                                    viewHolder.mCostTv.setText(sixtyThousand);
                                else if (cost.equals(String.valueOf(6500000)))
                                    viewHolder.mCostTv.setText(sixtyFiveThousand);
                                else if (cost.equals(String.valueOf(7000000)))
                                    viewHolder.mCostTv.setText(seventyThousand);
                                else if (cost.equals(String.valueOf(7500000)))
                                    viewHolder.mCostTv.setText(seventyFiveThousand);
                                else if (cost.equals(String.valueOf(8000000)))
                                    viewHolder.mCostTv.setText(eightyThousand);
                                else if (cost.equals(String.valueOf(8500000)))
                                    viewHolder.mCostTv.setText(eightyFiveThousand);
                                else if (cost.equals(String.valueOf(9000000)))
                                    viewHolder.mCostTv.setText(ninetyThousand);
                                else if (cost.equals(String.valueOf(9500000)))
                                    viewHolder.mCostTv.setText(ninetyFiveThousand);
                                else if (cost.equals(String.valueOf(10000000)))
                                    viewHolder.mCostTv.setText(hundredThousand);


                                viewHolder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        //..
                                    }
                                });

                                viewHolder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        sessionsRef.orderByKey().equalTo(hisUID)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                            Sessions sessions = ds.getValue(Sessions.class);
                                                            Long completed = sessions.getCompleted();

                                                            Long percentage = (user.getCost()*completed)/100;
                                                            double totalPay = percentage*0.6;

                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                            final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.edit_staff_info, null);
                                                            builder.setTitle(user.getUsername()+"'s Info");

                                                            TextView phoneTv = mView.findViewById(R.id.phoneTv);
                                                            TextView costTv = mView.findViewById(R.id.costEt);
                                                            TextView categoryTv1 = mView.findViewById(R.id.categoryTv1);
                                                            TextView categoryTv2 = mView.findViewById(R.id.categoryTv2);
                                                            TextView categoryTv3 = mView.findViewById(R.id.categoryTv3);
                                                            TextView countryTv = mView.findViewById(R.id.countryTv);
                                                            TextView completedTv = mView.findViewById(R.id.completedTv);
                                                            TextView totalTv = mView.findViewById(R.id.totalTv);
                                                            EditText bankEt = mView.findViewById(R.id.bankEt);
                                                            EditText accountEt = mView.findViewById(R.id.accountnumEt);
                                                            EditText dayTimeEt = mView.findViewById(R.id.daytimeEt);
                                                            EditText nightTimeEt = mView.findViewById(R.id.nightimeEt);
                                                            TextView profileTv = mView.findViewById(R.id.profileTv);

                                                            profileTv.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                    final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_profile, null);

                                                                    TextView nameTv = mView.findViewById(R.id.counsellor_name);
                                                                    EditText descEt = mView.findViewById(R.id.counsellor_desc);
                                                                    TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                                                                    TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                                                                    TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                                                                    nameTv.setText(user.getUsername());
                                                                    if (!user.getCategory1().isEmpty()){
                                                                        catTv1.setVisibility(View.VISIBLE);
                                                                        catTv1.setText(user.getCategory1());
                                                                    }
                                                                    if (!user.getCategory2().isEmpty()){
                                                                        catTv2.setVisibility(View.VISIBLE);
                                                                        catTv2.setText(user.getCategory2());
                                                                    }
                                                                    if (!user.getCategory3().isEmpty()){
                                                                        catTv3.setVisibility(View.VISIBLE);
                                                                        catTv3.setText(user.getCategory3());
                                                                    }

                                                                    FirebaseDatabase.getInstance().getReference("profiles").orderByKey().equalTo(hisUID)
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                                                                                        if (ds.exists()){
                                                                                            String desc = ds.child("description").getValue().toString();
                                                                                            descEt.setText(desc);
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                    builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            FirebaseDatabase.getInstance().getReference("profiles")
                                                                                    .child(hisUID).child("description").setValue(descEt.getText().toString());
                                                                        }
                                                                    });

                                                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });

                                                                    builder.setView(mView);
                                                                    builder.show();

                                                                }
                                                            });

                                                            phoneTv.setText(user.getPhone());
                                                            completedTv.setText(""+completed);
                                                            costTv.setText(""+cost);
                                                            totalTv.setText(""+totalPay);
                                                            categoryTv1.setText(user.getCategory1());
                                                            categoryTv2.setText(user.getCategory2());
                                                            categoryTv3.setText(user.getCategory3());
                                                            countryTv.setText(user.getAddress());
                                                            bankEt.setText(user.getBank());
                                                            accountEt.setText(user.getAccountNumber());
                                                            dayTimeEt.setText(user.getDayTime());
                                                            nightTimeEt.setText(user.getNightTime());

                                                            costTv.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final String[] list = getActivity().getResources().getStringArray(R.array.costs);

                                                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(false)
                                                                            .setTitle(user.getUsername() +"'s Cost")
                                                                            .setSingleChoiceItems(list, position,null)
                                                                            .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                                public void onClick( DialogInterface dialog, int i)
                                                                                {
                                                                                    ListView lw = ((AlertDialog)dialog).getListView();
                                                                                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                                    costTv.setText(checkedItem.toString());
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();

                                                                }
                                                            });

                                                            categoryTv1.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final String[] list = getActivity().getResources().getStringArray(R.array.categories);

                                                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(false)
                                                                            .setTitle(user.getUsername() +"'s 1st category")
                                                                            .setSingleChoiceItems(list, position,null)
                                                                            .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                                public void onClick( DialogInterface dialog, int i)
                                                                                {
                                                                                    ListView lw = ((AlertDialog)dialog).getListView();
                                                                                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                                    categoryTv1.setText(checkedItem.toString());
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();
                                                                }
                                                            });

                                                            categoryTv2.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final String[] list = getActivity().getResources().getStringArray(R.array.categories);

                                                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(false)
                                                                            .setTitle(user.getUsername() +"'s 2nd category")
                                                                            .setSingleChoiceItems(list, position,null)
                                                                            .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                                public void onClick( DialogInterface dialog, int i)
                                                                                {
                                                                                    ListView lw = ((AlertDialog)dialog).getListView();
                                                                                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                                    categoryTv2.setText(checkedItem.toString());
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();
                                                                }
                                                            });

                                                            categoryTv3.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final String[] list = getActivity().getResources().getStringArray(R.array.categories);

                                                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(false)
                                                                            .setTitle(user.getUsername() +"'s 3rd category")
                                                                            .setSingleChoiceItems(list, position,null)
                                                                            .setPositiveButton( "SELECT", new DialogInterface.OnClickListener() {
                                                                                public void onClick( DialogInterface dialog, int i)
                                                                                {
                                                                                    ListView lw = ((AlertDialog)dialog).getListView();
                                                                                    Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                                                                    categoryTv3.setText(checkedItem.toString());
                                                                                    dialog.dismiss();
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();
                                                                }
                                                            });

                                                            completedTv.setOnLongClickListener(new View.OnLongClickListener() {
                                                                @Override
                                                                public boolean onLongClick(View v) {
                                                                    AlertDialog ad = new AlertDialog.Builder(getActivity())
                                                                            .setCancelable(false)
                                                                            .setTitle("RESET COMPLETED SESSIONS")
                                                                            .setPositiveButton( "RESET", new DialogInterface.OnClickListener() {
                                                                                public void onClick( DialogInterface dialog, int i)
                                                                                {
                                                                                    completedTv.setText(""+0);
                                                                                }
                                                                            })
                                                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }).show();
                                                                    return false;
                                                                }
                                                            });

                                                            builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    sessionsRef.child(hisUID).child("completed").setValue(Long.parseLong(completedTv.getText().toString()));

                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("phone", phoneTv.getText().toString());
                                                                    hashMap.put("category1", categoryTv1.getText().toString());
                                                                    hashMap.put("category2", categoryTv2.getText().toString());
                                                                    hashMap.put("category3", categoryTv3.getText().toString());
                                                                    hashMap.put("cost", Long.parseLong(costTv.getText().toString()));
                                                                    hashMap.put("address", countryTv.getText().toString());
                                                                    hashMap.put("bank", bankEt.getText().toString());
                                                                    hashMap.put("accountNumber", accountEt.getText().toString());
                                                                    hashMap.put("dayTime", dayTimeEt.getText().toString());
                                                                    hashMap.put("nightTime", nightTimeEt.getText().toString());

                                                                    usersRef.child(hisUID).updateChildren(hashMap);
                                                                }
                                                            });


                                                            builder.setView(mView);
                                                            builder.show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //..
                                                    }
                                                });
                                    }
                                });
                            }
                        };
                        //Set Adapter
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing( false );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    private void sendNotification1(String hisUID, String thesel_team, String message2, String client_name) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds.getString("token"));
                    Data data = new Data(mAuth.getCurrentUser().getUid(), thesel_team+": "+ message2, "NEW REQUEST FROM "+client_name, hisUID, R.mipmap.ic_launcher2);

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
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds.getString("token"));
                    Data data = new Data(mAuth.getCurrentUser().getUid(), message3, username, adminID, R.mipmap.ic_launcher2);

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



}
