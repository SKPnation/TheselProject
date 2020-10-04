package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Consultation.AppointmentChatActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Appointment;
import com.skiplab.theselproject.models.User;

import java.time.LocalDate;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder>{

    private static final String TAG = "AppointmentAdapter";

    Context context;
    List<Appointment> appointmentList;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;
    CollectionReference mAppointmentReference;

    LocalDate todayDate;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        mAppointmentReference = FirebaseFirestore.getInstance().collection("appointments");
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
        String appointmentDate = appointmentList.get(position).getBooked_date();
        String startTime = appointmentList.get(position).getStart_time();
        String endTime = appointmentList.get(position).getEnd_time();
        String timeType = appointmentList.get(position).getTimeType();
        //Boolean absent_bool = appointmentList.get(position).isAbsent();
        long num_messages = appointmentList.get(position).getNum_messages();

        //set the number of chat messages
        String chatMessagesString = num_messages + " message(s)";

        //set number of chatroom messages
        holder.apptDuration.setText(startTime+" - "+endTime+" "+timeType);
        holder.apptDateTv.setText(appointmentDate);
        holder.numMessagesTv.setText(chatMessagesString);

        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();

            todayDate = today;
            String todayDateS = todayDate.toString();

            usersRef.orderByKey().equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds:dataSnapshot.getChildren()){
                                User user = ds.getValue(User.class);

                                if (user.getIsStaff().equals("false"))
                                {
                                    usersRef.orderByKey().equalTo(counsellorID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds :dataSnapshot.getChildren()) {
                                                        User counsellor = ds.getValue(User.class);

                                                        //set counsellor name
                                                        holder.hisNameTv.setText(counsellor.getUsername());
                                                        holder.categoryTv.setText(counsellor.getCategory1());

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
                                                                    Intent intent = new Intent(context, AppointmentChatActivity.class);
                                                                    intent.putExtra("hisUID", counsellorID);
                                                                    intent.putExtra("appointmentID",appointmentID);
                                                                    intent.putExtra("hisName",counsellor.getUsername());
                                                                    context.startActivity(intent);
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

                                                        //Toast.makeText(context,LocalDate.parse(todayDateS)+"",Toast.LENGTH_SHORT).show();

                                                        if (LocalDate.parse(todayDateS).isAfter(LocalDate.parse(appointmentDate)))
                                                        {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                            builder.setCancelable(false);
                                                            builder.setMessage("Your appointment scheduled for "+startTime+" - "+endTime+" "+timeType+" on "+appointmentDate
                                                                    + " with "+counsellor.getUsername()+" has expired.");
                                                            builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();

                                                                    mAppointmentReference.document(appointmentID).delete();

                                                                    //((ChatRoomsActivity)context).finish();
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
                                else if (user.getIsStaff().equals("true"))
                                {
                                    /**
                                     * FOR IF CURRENT USER IS A COUNSELLOR
                                     * */
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });
        }

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
                                /*.addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //context.startActivity(new Intent(context, DashboardActivity.class));
                                        Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                                    }
                                });*/
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
