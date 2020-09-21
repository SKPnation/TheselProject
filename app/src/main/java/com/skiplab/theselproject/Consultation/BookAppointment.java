package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Appointment;
import com.skiplab.theselproject.models.Early;
import com.skiplab.theselproject.models.Morning;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nullable;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import static android.os.Build.VERSION_CODES.O;

public class BookAppointment extends AppCompatActivity {

    private static final String TAG = "BookAppointment";

    private Context mContext = BookAppointment.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private CollectionReference earlyDb, morningDb, afternoonDb, eveningDb;
    private CollectionReference appointmentDb;


    private Button five_am_btn, six_am_btn;
    private Button seven_am_btn, eight_am_btn, nine_am_btn, ten_am_btn, eleven_am_btn;

    String hisUID, myUID;
    long hisCost;

    LocalDate todayDate, expiryDate;

    Calendar selected_date, date1;
    HorizontalCalendarView horizontalCalendarView;
    SimpleDateFormat simpleDateFormat;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        setupFirebaseAuth();

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        hisCost = intent.getLongExtra("hisCost",0L);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE,0);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,6);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this,R.id.calendar_view)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (selected_date.getTimeInMillis() != date.getTimeInMillis())
                {
                    date1 = date;
                    selected_date = date1;
                    Toast.makeText(mContext, simpleDateFormat.format(date1.getTime()), Toast.LENGTH_LONG).show();

                }
            }
        });



        five_am_btn = findViewById(R.id.five_am_btn);
        six_am_btn = findViewById(R.id.six_am_btn);
        seven_am_btn = findViewById(R.id.seven_am_btn);
        eight_am_btn = findViewById(R.id.eight_am_btn);
        nine_am_btn = findViewById(R.id.nine_am_btn);
        ten_am_btn = findViewById(R.id.ten_am_btn);
        eleven_am_btn = findViewById(R.id.eleven_am_btn);

        appointmentDb = FirebaseFirestore.getInstance().collection("appointments");

        earlyDb = FirebaseFirestore.getInstance().collection("early");
        morningDb = FirebaseFirestore.getInstance().collection("morning");
        afternoonDb = FirebaseFirestore.getInstance().collection("afternoon");
        eveningDb = FirebaseFirestore.getInstance().collection("evening");


        earlyDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Early early = documentSnapshot.toObject(Early.class);
                        if (early.getFive_am().equals("false") && early.getSix_am().equals("false")) {
                            five_am_btn.setVisibility(View.GONE);
                            six_am_btn.setVisibility(View.GONE);
                        }
                        else if (early.getFive_am().equals("true") && early.getSix_am().equals("true")) {
                            five_am_btn.setVisibility(View.VISIBLE);
                            six_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (early.getFive_am().equals("true") && early.getSix_am().equals("false")) {
                            five_am_btn.setVisibility(View.VISIBLE);
                            six_am_btn.setVisibility(View.GONE);
                        }
                        else {
                            five_am_btn.setVisibility(View.GONE);
                            six_am_btn.setVisibility(View.VISIBLE);
                        }


                        five_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                LocalDate today = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();
                                }
                                todayDate = today;

                                try {
                                    if (todayDate.toString().equals(simpleDateFormat.format(date1.getTime())))
                                    {
                                        Toast.makeText(mContext, "Choose another day on the calendar", Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "ERROR: "+e );
                                }
                                /*expiryDate = todayDate.plusDays(1);

                                String expiryDateS = expiryDate.toString();
                                String todayDateS = todayDate.toString();

                                String expiryDay = todayDate.plusDays(1).getDayOfWeek().toString();


                                //five_am_btn.setBackgroundColor(Color.parseColor("#29D300"));

                                String five_am = five_am_btn.getText().toString();

                                if (!LocalDate.parse(expiryDateS).isAfter(LocalDate.parse(todayDateS))){
                                    Toast.makeText(mContext, "NO: "+expiryDay+", "+expiryDate, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(mContext, "YES: "+expiryDay+", "+expiryDate, Toast.LENGTH_LONG).show();
                                }*/


                                /*appointmentDb.whereEqualTo("counsellor_id",hisUID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful())
                                                {
                                                    if(task.getResult().size() > 0)
                                                    {
                                                        Toast.makeText(mContext, "EXISTS!!!", Toast.LENGTH_SHORT).show();
                                                        *//*task.getResult().getQuery().addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                for (DocumentSnapshot ds: documentSnapshots.getDocuments())
                                                                {
                                                                    Appointment appointment = ds.toObject(Appointment.class);
                                                                    if (appointment.getBooked_time().equals(five_am))
                                                                    {
                                                                        //..
                                                                    }
                                                                }
                                                            }
                                                        });*//*
                                                    }
                                                    else
                                                    {
                                                        //Toast.makeText(mContext, ""+ expiryDate, Toast.LENGTH_LONG).show();

                                                        *//*Appointment appointment = new Appointment();
                                                        appointment.setClient_id(myUID);
                                                        appointment.setCounsellor_id(hisUID);
                                                        appointment.setBooked_time(five_am);
                                                        appointment.setBooked_date("");
                                                        appointment.setNum_messages(0);
                                                        appointment.setTimestamp(timestamp);
                                                        appointment.setSlot("0");
                                                        appointment.setAppointment_id(timestamp);
                                                        appointment.setAbsent(false);*//*
                                                    }
                                                }
                                            }
                                        });*/

                            }
                        });

                        six_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //..
                            }
                        });
                    }
                });

        morningDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Morning morning = documentSnapshot.toObject(Morning.class);
                        if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("false") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("true") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("false")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("false")
                                && morning.getEleven_am().equals("true")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        else if (morning.getSeven_am().equals("true") && morning.getEight_am().equals("true")
                                && (morning.getNine_am().equals("false") && morning.getTen_am().equals("true")
                                && morning.getEleven_am().equals("false")))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }

                    }
                });

        afternoonDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        //..
                    }
                });

        Toast.makeText(mContext,""+hisCost,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        isActivityRunning = true;

        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                .setMessage("Swipe the calendar to choose a date for your appointment!")
                .create();
        alertDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            myUID = user.getUid();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };


    }
}
