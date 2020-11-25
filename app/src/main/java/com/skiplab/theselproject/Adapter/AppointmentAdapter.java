package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.AppointmentChatActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Appointment;
import com.skiplab.theselproject.models.InstantSession;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder>{

    private static final String TAG = "AppointmentAdapter";

    Context context;
    List<Appointment> appointmentList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    CollectionReference mAppointmentReference;

    LocalDate todayDate;

    SimpleDateFormat simpleDateFormat, simpleDateFormat1;

    int i = 0;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mAppointmentReference = FirebaseFirestore.getInstance().collection("appointments");
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat1 = new SimpleDateFormat("HH:mm");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_listitem, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String appointmentID = appointmentList.get(position).getAppointment_id();
        String clientUID = appointmentList.get(position).getClient_id();
        String counsellorID = appointmentList.get(position).getCounsellor_id();
        long appointmentDate = appointmentList.get(position).getBooked_date();
        String startTime = appointmentList.get(position).getStart_time();
        String endTime = appointmentList.get(position).getEnd_time();
        String timeType = appointmentList.get(position).getTimeType();
        long num_messages = appointmentList.get(position).getNum_messages();

        //set the number of chat messages
        String chatMessagesString = num_messages + " message(s)";

        //set number of chatroom messages
        holder.apptDuration.setText(startTime+" - "+endTime+" "+timeType);
        holder.apptDateTv.setText(simpleDateFormat.format(appointmentDate));
        holder.numMessagesTv.setText(chatMessagesString);

        /*Date date0 = new Date();
        Date date1 = new Date();
        date0.getTime();
        date1.setTime(appointmentDate);*/

        usersRef.orderByKey().equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);

                            if (user.getIsStaff().equals("false"))
                            {
                                holder.chatBtn.setText("Join");

                                if (appointmentList.get(position).isAbsent())
                                    holder.absentBtn.setVisibility(View.VISIBLE);

                                usersRef.orderByKey().equalTo(counsellorID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                                                    User counsellor = ds.getValue(User.class);

                                                    //set counsellor name
                                                    holder.hisNameTv.setText(counsellor.getUsername());
                                                    holder.categoryTv.setText(counsellor.getCategory_one());

                                                    //set counsellor dp
                                                    try {
                                                        UniversalImageLoader.setImage(counsellor.getProfile_photo(), holder.avaterIv, null, "");
                                                    } catch (Exception e) {
                                                        Log.d("ERROR: ", "" + e);
                                                    }

                                                    holder.chatBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (Common.isConnectedToTheInternet(context))
                                                            {
                                                                if (!appointmentList.get(position).isOpen())
                                                                {
                                                                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                            .setMessage("Wait for the consultant to begin the session.")
                                                                            .create();
                                                                    alertDialog.show();
                                                                }
                                                                else
                                                                {
                                                                    if (!appointmentList.get(position).isAbsent())
                                                                    {
                                                                        Intent intent = new Intent(context, AppointmentChatActivity.class);
                                                                        intent.putExtra("hisUID", counsellorID);
                                                                        intent.putExtra("appointmentID",appointmentID);
                                                                        intent.putExtra("hisName",counsellor.getUsername());
                                                                        context.startActivity(intent);
                                                                    }
                                                                    else
                                                                    {
                                                                        AlertDialog alertDialog =new AlertDialog.Builder(context)
                                                                                .setMessage("For showing up late, you have been marked as absent and have lost 5% of your Thesel funds.")
                                                                                .create();
                                                                        alertDialog.show();

                                                                        holder.absentBtn.setVisibility(View.VISIBLE);

                                                                        i++;

                                                                        Handler handler = new Handler();
                                                                        handler.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Intent intent = new Intent(context, AppointmentChatActivity.class);
                                                                                intent.putExtra("hisUID", counsellorID);
                                                                                intent.putExtra("appointmentID",appointmentID);
                                                                                intent.putExtra("hisName",counsellor.getUsername());
                                                                                context.startActivity(intent);
                                                                            }
                                                                        },5000);
                                                                    }
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
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                //..
                                            }
                                        });


                            }
                            else if (user.getIsStaff().equals("true"))
                            {
                                holder.chatBtn.setText("Begin");

                                holder.absentBtn.setVisibility(View.VISIBLE);

                                if (appointmentList.get(position).isAbsent())
                                    holder.absentBtn.setBackgroundColor(Color.parseColor("#0098DB"));

                                usersRef.orderByKey().equalTo(clientUID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                                                    User client = ds.getValue(User.class);

                                                    //set counsellor name
                                                    holder.hisNameTv.setText(client.getUsername());

                                                    holder.categoryTv.setText(user.getCategory_one());

                                                    holder.absentBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                            builder.setTitle("Has "+client.getUsername()+" been absent for over 10 mins?");
                                                            builder.setMessage("If YES, "+client.getUsername()+" will lose 5% of his/her Thesel funds");
                                                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    mAppointmentReference.document(appointmentID)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                                                        hashMap.put("absent", true);
                                                                                        mAppointmentReference.document(appointmentID).set(hashMap, SetOptions.merge());
                                                                                    }
                                                                                }
                                                                            });
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
                                                    });

                                                    holder.chatBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            mAppointmentReference.document(appointmentID)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()){
                                                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                                                hashMap.put("open", true);
                                                                                mAppointmentReference.document(appointmentID).set(hashMap, SetOptions.merge());

                                                                                Intent intent = new Intent(context, AppointmentChatActivity.class);
                                                                                intent.putExtra("hisUID", clientUID);
                                                                                intent.putExtra("appointmentID",appointmentID);
                                                                                intent.putExtra("hisName",client.getUsername());
                                                                                context.startActivity(intent);
                                                                            }
                                                                        }
                                                                    });
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

        holder.iconTrashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("END SESSION");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAppointmentReference.document(appointmentID).delete();
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
        });

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView avaterIv, iconTrashIv;
        TextView categoryTv, apptDateTv, apptDuration, hisNameTv, numMessagesTv;
        Button chatBtn, absentBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            avaterIv = itemView.findViewById(R.id.hisImage);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            iconTrashIv = itemView.findViewById(R.id.icon_trash);
            apptDateTv = itemView.findViewById(R.id.booked_date_tv);
            apptDuration = itemView.findViewById(R.id.appt_duration);
            hisNameTv = itemView.findViewById(R.id.hisName);
            numMessagesTv = itemView.findViewById(R.id.number_chatmessages);
            chatBtn = itemView.findViewById(R.id.chat_btn);
            absentBtn = itemView.findViewById(R.id.absent_btn);
        }
    }
}
