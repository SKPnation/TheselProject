package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.AdminEdit;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.BookAppointment1;
import com.skiplab.theselproject.Consultation.ChatRoomsActivity;
import com.skiplab.theselproject.Consultation.WalletActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.JavaMailAPI;
import com.skiplab.theselproject.Utils.JavaMailClientAPI;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatMessage;
import com.skiplab.theselproject.models.InstantSession;
import com.skiplab.theselproject.models.Profile;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>{

    private static final String TAG = "AdapterUser";

    Context context;
    List<User> userList;
    List<Profile> profileList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    CollectionReference mInstantSessionReference, mProfileReference, allTokens;
    DatabaseReference mMessageReference;

    APIService apiService;

    //LocalDate todayDate, expiryDate;

    SimpleDateFormat simpleDateFormat;

    String adminUID = "1zNcpaSxviY7GLLRGVQt8ywPla52";

    String expiryDay;
    long instant_cost, displayed_instant_cost, appt_cost, displayed_appt_cost;

    int i = 0;

    public AdapterUser(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mInstantSessionReference = FirebaseFirestore.getInstance().collection("instantSessions");
        mProfileReference = FirebaseFirestore.getInstance().collection("profiles");
        mMessageReference = FirebaseDatabase.getInstance().getReference("chatroom_messages");
        allTokens = FirebaseFirestore.getInstance().collection("tokens");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String hisUID = userList.get(position).getUid();
        String hisEmail = userList.get(position).getEmail();
        String hisName = userList.get(position).getUsername();
        String hisStatus = userList.get(position).getOnline_status();
        String hisLocation = userList.get(position).getLocation();
        String category1 = userList.get(position).getCategory_one();
        String category2 = userList.get(position).getCategory_two();
        String category3 = userList.get(position).getCategory_three();
        Long hisCost = profileList.get(position).getAppointment_cost();

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso().toUpperCase();
        Locale loc = new Locale("",countryCode);
        String myLocation = loc.getDisplayCountry();

        if (!myLocation.equals(hisLocation))
        {
            holder.buttonsLinearLayout.setWeightSum(2);
            holder.scheduleBtn.setVisibility(View.GONE);
        }
        else if (myLocation.equals(null))
        {
            holder.buttonsLinearLayout.setWeightSum(2);
            holder.scheduleBtn.setVisibility(View.GONE);
        }

        if (userList.get(position).getOnline_status().equals("online"))
        {
            holder.onlineIv.setVisibility(View.VISIBLE);
        }
        else if (userList.get(position).getOnline_status().equals("deactivated"))
        {
            holder.itemView.setVisibility(View.GONE);
        }


        try {
            UniversalImageLoader.setImage(userList.get(position).getProfile_photo(), holder.mAvatarIv, null, "");
        }
        catch (Exception e){
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                usersRef.orderByKey().equalTo(mAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    User user = ds.getValue(User.class);
                                    if (user.getIsStaff().equals("admin"))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("PICK ONE!");
                                        builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context, AdminEdit.class);
                                                intent.putExtra("hisUID",hisUID);
                                                context.startActivity(intent);
                                            }
                                        });
                                        builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                builder1.setTitle("DELETE");
                                                builder1.setMessage("Are you sure?");
                                                builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                                        progressDialog.setMessage("Deleting...");

                                                        Query query = usersRef.orderByChild("uid").equalTo(hisUID);
                                                        query.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                                                {
                                                                    ds.getRef().removeValue()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(context,"Deleted successfully!",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                //..
                                                            }
                                                        });
                                                    }
                                                });

                                                builder1.show();

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
                return false;
            }
        });

        //get Data
        holder.usernameTv.setText(hisName);
        holder.usernameTv.setAllCaps(true);
        holder.countryTv.setText("["+hisLocation+"]");
        holder.categoryTv1.setText(category1);
        holder.categoryTv2.setText(category2);
        holder.categoryTv3.setText(category3);

        holder.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.consultant_profile, null);

                TextView nameTv = mView.findViewById(R.id.counsellor_name);
                EditText descEt = mView.findViewById(R.id.counsellor_desc);
                TextView catTv1 = mView.findViewById(R.id.categoryTv1);
                TextView catTv2 = mView.findViewById(R.id.categoryTv2);
                TextView catTv3 = mView.findViewById(R.id.categoryTv3);

                nameTv.setText(hisName);
                if (!category1.isEmpty()){
                    catTv1.setVisibility(View.VISIBLE);
                    catTv1.setText(category1);
                }
                if (!category2.isEmpty()){
                    catTv2.setVisibility(View.VISIBLE);
                    catTv2.setText(category2);
                }
                if (!category3.isEmpty()){
                    catTv3.setVisibility(View.VISIBLE);
                    catTv3.setText(category3);
                }

                FirebaseFirestore.getInstance().collection("profiles")
                        .document(hisUID)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (documentSnapshot.exists())
                                {
                                    Profile profile = documentSnapshot.toObject(Profile.class);

                                    String desc = profile.getDescription();
                                    if (!desc.equals(""))
                                        descEt.setText(desc);
                                }
                            }
                        });

                descEt.setFocusable(false);
                descEt.setFocusableInTouchMode(false);

                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        progressDialog.dismiss();
                    }
                });

                builder.setView(mView);
                builder.show();
            }
        });

        holder.scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToTheInternet(context))
                {
                    ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("Loading...");
                    pd.show();

                    mProfileReference.document(hisUID)
                            .get()
                            .addOnCompleteListener(task -> {
                                String appt_duration = task.getResult().toObject(Profile.class).getAppt_duration();
                                displayed_appt_cost = hisCost / 100;

                                pd.dismiss();

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_book_appointment, null);

                                TextView length = mView.findViewById(R.id.appt_duration_tv);
                                TextView fee_tv = mView.findViewById(R.id.appt_fee_tv);

                                Locale locale = new Locale("en", "NG");
                                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                                fee_tv.setText(fmt.format(displayed_appt_cost));

                                length.setText(appt_duration+" mins");

                                Button book_btn = mView.findViewById(R.id.book_btn);

                                book_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v1) {
                                        if (Common.isConnectedToTheInternet(context))
                                        {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                            {
                                                ProgressDialog progressDialog = new ProgressDialog(context);
                                                progressDialog.setMessage("Loading...");
                                                progressDialog.show();

                                                usersRef.orderByKey().equalTo(mAuth.getUid())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                                                {
                                                                    User client = ds.getValue(User.class);

                                                                    if (client.getIsStaff().equals("false"))
                                                                    {
                                                                        if (userList.get(position).getOnline_status().equals("offline"))
                                                                        {
                                                                            progressDialog.dismiss();

                                                                            AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                    .setMessage(hisName+" is offline!")
                                                                                    .create();
                                                                            alertDialog.show();
                                                                        }
                                                                        else if (userList.get(position).equals("deactivated"))
                                                                        {
                                                                            progressDialog.dismiss();

                                                                            AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                    .setMessage(hisName+" is unavailable at the moment!")
                                                                                    .create();
                                                                            alertDialog.show();
                                                                        }
                                                                        else
                                                                        {
                                                                            int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                                                            if (!(wallet >= displayed_appt_cost))
                                                                            {
                                                                                progressDialog.dismiss();

                                                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                        .setMessage("Insufficient funds!!!")
                                                                                        .create();
                                                                                alertDialog.show();

                                                                                i++;

                                                                                Handler handler = new Handler();
                                                                                handler.postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        alertDialog.dismiss();
                                                                                        context.startActivity(new Intent(context, WalletActivity.class));

                                                                                    }
                                                                                },2000);
                                                                            }
                                                                            else
                                                                            {
                                                                                LocalDateTime time = null;
                                                                                time = LocalDateTime.now();
                                                                                DateTimeFormatter myFormatTime = DateTimeFormatter.ofPattern("HH:mm");
                                                                                String formattedTime = time.format(myFormatTime);

                                                                                try
                                                                                {
                                                                                    if (LocalTime.parse(formattedTime).isAfter(LocalTime.parse("21:00")))
                                                                                    {
                                                                                        progressDialog.dismiss();

                                                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                                                        builder1.setTitle("Closed!");
                                                                                        builder1.setMessage("The book appointment feature closes at 9PM and re-opens at 12AM.");
                                                                                        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        });

                                                                                        builder1.show();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        progressDialog.dismiss();

                                                                                        Intent intent = new Intent(context, BookAppointment1.class);
                                                                                        intent.putExtra("hisUID",hisUID);
                                                                                        intent.putExtra("hisCost",hisCost);
                                                                                        context.startActivity(intent);
                                                                                    }
                                                                                }
                                                                                catch (Exception e)
                                                                                {
                                                                                    Log.d(TAG, "ERROR: "+e );
                                                                                }

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
                                            else
                                            {
                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                        .setTitle("Upgrade your OS")
                                                        .setMessage("To use this feature, your Android OS must be 8.0 and above!")
                                                        .create();
                                                alertDialog.show();
                                            }
                                        }
                                        else
                                        {
                                            AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                    .setMessage("Please check your internet connection!")
                                                    .create();
                                            alertDialog.show();
                                        }
                                    }
                                });

                                builder.setView(mView);
                                builder.show();

                            });
                }
                else
                {
                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                            .setMessage("Please check your internet connection!")
                            .create();
                    alertDialog.show();
                }

            }
        });

        holder.instantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToTheInternet(context))
                {
                    Date todayDate = new Date();
                    LocalDate today = null;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        todayDate.getTime();
                        long expiryDate = todayDate.toInstant().plusMillis(604800000).toEpochMilli();

                        today = LocalDate.now();
                        expiryDay = today.plusDays(7).getDayOfWeek().toString();

                        ProgressDialog pd1 = new ProgressDialog(context);
                        pd1.setMessage("Loading...");
                        pd1.show();

                        mProfileReference.document(hisUID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task0) {
                                        instant_cost = task0.getResult().toObject(Profile.class).getInstant_cost();
                                        displayed_instant_cost = instant_cost / 100;

                                        pd1.dismiss();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_instant_consultation, null);

                                        TextView fee_tv = mView.findViewById(R.id.instant_fee_tv);

                                        Locale locale = new Locale("en", "NG");
                                        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                                        fee_tv.setText(fmt.format(displayed_instant_cost));

                                        Button start_btn = mView.findViewById(R.id.start_instant_btn);

                                        start_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (Common.isConnectedToTheInternet(context))
                                                {
                                                    ProgressDialog pd2 = new ProgressDialog(context);
                                                    pd2.setMessage("Loading...");
                                                    pd2.show();

                                                    usersRef.orderByKey().equalTo(mAuth.getUid())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot ds: dataSnapshot.getChildren())
                                                                    {
                                                                        User client = ds.getValue(User.class);

                                                                        if (client.getIsStaff().equals("false"))
                                                                        {
                                                                            if (userList.get(position).getOnline_status().equals("offline"))
                                                                            {
                                                                                pd2.dismiss();

                                                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                        .setMessage(hisName+" is offline!")
                                                                                        .create();
                                                                                alertDialog.show();
                                                                            }
                                                                            else if (userList.get(position).equals("deactivated"))
                                                                            {
                                                                                pd2.dismiss();

                                                                                AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                        .setMessage(hisName+" is unavailable at the moment!")
                                                                                        .create();
                                                                                alertDialog.show();
                                                                            }
                                                                            else
                                                                            {
                                                                                int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                                                                if (!(wallet >= displayed_instant_cost))
                                                                                {
                                                                                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                            .setMessage("Insufficient funds!!!")
                                                                                            .create();
                                                                                    alertDialog.show();

                                                                                    i++;

                                                                                    Handler handler = new Handler();
                                                                                    handler.postDelayed(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            alertDialog.dismiss();
                                                                                            context.startActivity(new Intent(context, WalletActivity.class));

                                                                                        }
                                                                                    },2000);
                                                                                }
                                                                                else
                                                                                {
                                                                                    mInstantSessionReference.whereEqualTo("client_id", mAuth.getUid())
                                                                                            .get()
                                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                                                    if (task1.isSuccessful())
                                                                                                    {
                                                                                                        if(task1.getResult().size() > 0)
                                                                                                        {
                                                                                                            pd2.dismiss();

                                                                                                            AlertDialog.Builder alertDialog =new AlertDialog.Builder(context);
                                                                                                            alertDialog.setCancelable(false);
                                                                                                            alertDialog.setMessage("End your current instant session before starting a new one!");
                                                                                                            alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                                                @Override
                                                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                                                    dialog.dismiss();

                                                                                                                    context.startActivity(new Intent(context, ChatRoomsActivity.class));
                                                                                                                }
                                                                                                            });
                                                                                                            alertDialog.show();
                                                                                                        }
                                                                                                        else
                                                                                                        {
                                                                                                            mInstantSessionReference.whereEqualTo("counsellor_id", hisUID)
                                                                                                                    .get()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                                                                            if (task2.isSuccessful())
                                                                                                                            {
                                                                                                                                if (task2.getResult().size() > 4)
                                                                                                                                {
                                                                                                                                    pd2.dismiss();

                                                                                                                                    AlertDialog.Builder alertDialog =new AlertDialog.Builder(context);
                                                                                                                                    alertDialog.setCancelable(false);
                                                                                                                                    alertDialog.setMessage("Sorry, "+hisName+"'s Instant Session slots are full."+"\n\n"+ "Please book an appointment.");
                                                                                                                                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                                            dialog.dismiss();
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                                    alertDialog.show();
                                                                                                                                }
                                                                                                                                else
                                                                                                                                {
                                                                                                                                    pd2.dismiss();

                                                                                                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                                                                                                                    alertDialog.setMessage(fmt.format(displayed_instant_cost)+" will be deducted from your Thesel wallet.");
                                                                                                                                    alertDialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                                            dialog.dismiss();

                                                                                                                                            long result = wallet - displayed_instant_cost;

                                                                                                                                            usersRef.child(mAuth.getUid()).child("wallet").setValue(result);

                                                                                                                                            /*JavaMailAPI javaMailAPI = new JavaMailAPI(
                                                                                                                                                    context,
                                                                                                                                                    "contact@thesel.com.ng",
                                                                                                                                                    hisEmail,
                                                                                                                                                    "THESEL CONSULTATION",
                                                                                                                                                    "Hello "+hisName+","+"\n\n"+client.getUsername().toUpperCase()+" just paid for an Instant Session [a One Week Session] with you on the Thesel platform."+
                                                                                                                                                            "\n\n\n"+"Thesel Team.");

                                                                                                                                            javaMailAPI.execute();

                                                                                                                                            JavaMailClientAPI javaMailClientAPI = new JavaMailClientAPI(
                                                                                                                                                    context,
                                                                                                                                                    mAuth.getCurrentUser().getEmail(),
                                                                                                                                                    "THESEL CONSULTATION",
                                                                                                                                                    "Hello "+client.getUsername().toUpperCase()+","+"\n\n"+" You just paid for an Instant Session [a One Week Session] with "+hisName.toUpperCase()+" on the Thesel platform."+
                                                                                                                                                            "\n\n\n"+"Thesel Team.");

                                                                                                                                            javaMailClientAPI.execute();*/

                                                                                                                                            String timestamp = String.valueOf(System.currentTimeMillis());

                                                                                                                                            String chatroomId = UUID.randomUUID().toString();

                                                                                                                                            InstantSession instantSession = new InstantSession();
                                                                                                                                            instantSession.setCounsellor_id(hisUID);
                                                                                                                                            instantSession.setClient_id(mAuth.getUid());
                                                                                                                                            instantSession.setTimestamp(timestamp);
                                                                                                                                            instantSession.setSession_id(chatroomId);
                                                                                                                                            instantSession.setExpiryDate(expiryDate);
                                                                                                                                            instantSession.setExpiryDay(expiryDay);
                                                                                                                                            instantSession.setNum_messages(1);

                                                                                                                                            //create a unique id for the message
                                                                                                                                            String messageId = mMessageReference.push().getKey();

                                                                                                                                            ChatMessage message = new ChatMessage();
                                                                                                                                            message.setMessage("Hi! I'm "+hisName+". Welcome to Thesel.\n"+"Let me know what's bothering you.");
                                                                                                                                            message.setTimestamp(timestamp);
                                                                                                                                            message.setSender_id(hisUID);
                                                                                                                                            message.setReceiver_id(mAuth.getUid());
                                                                                                                                            message.setType("text");
                                                                                                                                            message.setChatroom_id(chatroomId);


                                                                                                                                            /*mProfileReference.document(hisUID)
                                                                                                                                                    .get()
                                                                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task3) {
                                                                                                                                                            if (task3.getResult().exists())
                                                                                                                                                            {
                                                                                                                                                                long instants = task3.getResult().getLong("instants");
                                                                                                                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                                                                                                                hashMap.put("instants",instants+1);

                                                                                                                                                                mProfileReference.document(hisUID).set(hashMap, SetOptions.merge())
                                                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onSuccess(Void aVoid) {

                                                                                                                                                                        FirebaseFirestore.getInstance().collection("revenue")
                                                                                                                                                                                .document(context.getString(R.string.revenue_doc_path))
                                                                                                                                                                                .get()
                                                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                                                        long revenue = task.getResult().getLong("total_revenue");
                                                                                                                                                                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                                                                                                                                                                        hashMap1.put("total_revenue",revenue+displayed_instant_cost);
                                                                                                                                                                                        FirebaseFirestore.getInstance().collection("revenue").document(context.getString(R.string.revenue_doc_path)).set(hashMap1, SetOptions.merge());
                                                                                                                                                                                    }
                                                                                                                                                                                });

                                                                                                                                                                    }
                                                                                                                                                                });
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    });*/

                                                                                                                                            mInstantSessionReference.document(chatroomId).set(instantSession)
                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onSuccess(Void aVoid) {

                                                                                                                                                            mMessageReference.child(messageId).setValue(message);

                                                                                                                                                            HashMap<String,Object> myNotificationsMap = new HashMap<>();
                                                                                                                                                            myNotificationsMap.put("counsellor_id",hisUID);
                                                                                                                                                            myNotificationsMap.put("client_id",mAuth.getUid());
                                                                                                                                                            myNotificationsMap.put("category",category1);
                                                                                                                                                            myNotificationsMap.put("title","Instant Session");
                                                                                                                                                            myNotificationsMap.put("content",hisName.toUpperCase()+", You have a new Instant Session with "+client.getUsername().toUpperCase());
                                                                                                                                                            myNotificationsMap.put("expiry_date","Expiry date: "+expiryDay.toUpperCase()+", "+simpleDateFormat.format(expiryDate));
                                                                                                                                                            myNotificationsMap.put("appointment_time","");
                                                                                                                                                            myNotificationsMap.put("timestamp",FieldValue.serverTimestamp());
                                                                                                                                                            myNotificationsMap.put("read",false);

                                                                                                                                                            FirebaseFirestore.getInstance().collection("myNotifications")
                                                                                                                                                                    .document(timestamp)
                                                                                                                                                                    .set(myNotificationsMap)
                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                        @Override
                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                            //sendNotification(hisUID, client.getUsername(), "NEW CONSULTATION!!!");

                                                                                                                                                                            //sendAdminNotification(mAuth.getUid(), "Instant Session for "+hisName, "Instant Session");

                                                                                                                                                                            context.startActivity(new Intent(context, ChatRoomsActivity.class));
                                                                                                                                                                        }
                                                                                                                                                                    });
                                                                                                                                                        }
                                                                                                                                                    })
                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                            Toast.makeText(context,"Add Instant Session Failed: "+e,Toast.LENGTH_SHORT).show();
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                                    alertDialog.show();

                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    })
                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                        @Override
                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                            Toast.makeText(context,"Counsellor Query Failed: "+e,Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Toast.makeText(context,"Client Query Failed: "+e,Toast.LENGTH_SHORT).show();
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
                                            }
                                        });

                                        builder.setView(mView);
                                        builder.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Profile Query Failed: "+e,Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        AlertDialog alertDialog =new AlertDialog.Builder(context)
                                .setTitle("Upgrade your OS")
                                .setMessage("To use this feature, your Android OS must be 8.0 and above!")
                                .create();
                        alertDialog.show();
                    }
                }
                else
                {
                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                            .setMessage("Please check your internet connection!")
                            .create();
                    alertDialog.show();
                }
            }
        });

    }

    private void sendAdminNotification(String clientUID, String body, String title) {
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(clientUID, body, title, adminUID, R.mipmap.ic_launcher3);

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

    private void sendNotification(String hisUID, String myName, String body) {
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(mAuth.getUid(), body, myName, hisUID, R.mipmap.ic_launcher3);

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
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        LinearLayout buttonsLinearLayout;
        ImageView mAvatarIv, onlineIv, offlineIv;
        ImageButton profileBtn, scheduleBtn, instantBtn;
        TextView usernameTv, countryTv, categoryTv1, categoryTv2, categoryTv3, mCostTv;
        View mView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            offlineIv = itemView.findViewById(R.id.offlineIv);
            usernameTv = itemView.findViewById(R.id.nameTv);
            countryTv = itemView.findViewById(R.id.countryTv);
            categoryTv1 = itemView.findViewById(R.id.categoryTv1);
            categoryTv2 = itemView.findViewById(R.id.categoryTv2);
            categoryTv3 = itemView.findViewById(R.id.categoryTv3);
            profileBtn = itemView.findViewById(R.id.profileBtn);
            scheduleBtn = itemView.findViewById(R.id.scheduleBtn);
            instantBtn = itemView.findViewById(R.id.instantBtn);
            mCostTv = itemView.findViewById(R.id.costTv);
            buttonsLinearLayout = itemView.findViewById(R.id.buttons_linear_layout);
        }
    }
}