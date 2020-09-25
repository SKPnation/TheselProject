package com.skiplab.theselproject.Consultation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.Afternoon;
import com.skiplab.theselproject.models.Early;
import com.skiplab.theselproject.models.Evening;
import com.skiplab.theselproject.models.Morning;
import com.skiplab.theselproject.models.Night;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Nullable;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointment1 extends AppCompatActivity {

    private static final String TAG = "BookAppointment";

    private Context mContext = BookAppointment1.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private CollectionReference earlyDb, morningDb, afternoonDb, eveningDb, nightDb;
    private CollectionReference appointmentDb;


    private Button five_am_btn, six_am_btn;
    private Button seven_am_btn, eight_am_btn, nine_am_btn, ten_am_btn, eleven_am_btn;
    private Button twelve_pm_btn, one_pm_btn, two_pm_btn, three_pm_btn, four_pm_btn;
    private Button five_pm_btn, six_pm_btn, seven_pm_btn, eight_pm_btn;
    private Button nine_pm_btn, ten_pm_btn, eleven_pm_btn;


    String hisUID, myUID;
    long hisCost;

    String TRUE = "true", FALSE="false";

    LocalDate todayDate, expiryDate;

    Calendar selected_date, date1;
    HorizontalCalendarView horizontalCalendarView;
    SimpleDateFormat simpleDateFormat;

    String five_am = "", one_pm ="13:00 - 13:40";

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment1);

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
        endDate.add(Calendar.DATE,7);

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

        twelve_pm_btn = findViewById(R.id.twelve_pm_btn);
        one_pm_btn = findViewById(R.id.one_pm_btn);
        two_pm_btn = findViewById(R.id.two_pm_btn);
        three_pm_btn = findViewById(R.id.three_pm_btn);
        four_pm_btn = findViewById(R.id.four_pm_btn);

        five_pm_btn = findViewById(R.id.five_pm_btn);
        six_pm_btn = findViewById(R.id.six_pm_btn);
        seven_pm_btn = findViewById(R.id.seven_pm_btn);
        eight_pm_btn = findViewById(R.id.eight_pm_btn);

        nine_pm_btn = findViewById(R.id.nine_pm_btn);
        ten_pm_btn = findViewById(R.id.ten_pm_btn);
        eleven_pm_btn = findViewById(R.id.eleven_pm_btn);

        appointmentDb = FirebaseFirestore.getInstance().collection("appointments");

        earlyDb = FirebaseFirestore.getInstance().collection("early");
        morningDb = FirebaseFirestore.getInstance().collection("morning");
        afternoonDb = FirebaseFirestore.getInstance().collection("afternoon");
        eveningDb = FirebaseFirestore.getInstance().collection("evening");
        nightDb = FirebaseFirestore.getInstance().collection("night");


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

                                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                String countryCode = tm.getSimCountryIso().toUpperCase();
                                Locale loc = new Locale("",countryCode);
                                String country = loc.getDisplayCountry();

                                LocalDateTime time = null;
                                LocalDate today = null;
                                String formattedTime = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();
                                    time = LocalDateTime.now();
                                    DateTimeFormatter myFormatTime = DateTimeFormatter.ofPattern("HH:mm");
                                    formattedTime = time.format(myFormatTime);

                                    todayDate = today;

                                    try {
                                        if (LocalTime.parse(formattedTime).isBefore(LocalTime.parse("15:00")))
                                            Toast.makeText(mContext, country+": "+formattedTime, Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(mContext, country+": NO!!!", Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(TAG, "ERROR: "+e );
                                    }

                                    /*try {
                                        if (todayDate.toString().equals(simpleDateFormat.format(date1.getTime())))
                                        {
                                            Toast.makeText(mContext, formattedTime, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        Log.d(TAG, "ERROR: "+e );
                                    }*/
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

                        /**
                         * W.R.T; 7:00AM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 4
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 5
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 6
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 7
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 8
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 9
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 10
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 11
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 3RD STAGE
                         */

                        //STEP 12
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 13
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 14
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 15
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 4TH STAGE
                         */

                        //STEP 16: ALL TRUE
                        else if (morning.getSeven_am().equals(TRUE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.VISIBLE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 8:00AM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 4
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 5
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 6
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 7
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 3RD STAGE
                         */

                        //STEP 8
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(TRUE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.VISIBLE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 9:00AM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 4
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(TRUE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.VISIBLE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 10:00AM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(TRUE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.VISIBLE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * W.R.T; 11:00AM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(TRUE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 2: ALL FALSE
                        else if (morning.getSeven_am().equals(FALSE) && morning.getEight_am().equals(FALSE) && morning.getNine_am().equals(FALSE)
                                && morning.getTen_am().equals(FALSE) && morning.getEleven_am().equals(FALSE))
                        {
                            seven_am_btn.setVisibility(View.GONE);
                            eight_am_btn.setVisibility(View.GONE);
                            nine_am_btn.setVisibility(View.GONE);
                            ten_am_btn.setVisibility(View.GONE);
                            eleven_am_btn.setVisibility(View.GONE);
                        }
                    }
                });



        afternoonDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Afternoon afternoon = documentSnapshot.toObject(Afternoon.class);

                        /**
                         * W.R.T; 12:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 4
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 5
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 6
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 7
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 8
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 9
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 10
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 11
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 3RD STAGE
                         */

                        //STEP 12
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 13
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 14
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 15
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 4TH STAGE
                         */

                        //STEP 16: ALL TRUE
                        else if (afternoon.getTwelve_pm().equals(TRUE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.VISIBLE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }



                        /**
                         * W.R.T; 1:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 4
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * 2ND STAGE
                         */

                        //STEP 5
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 6
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 7
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * 3RD STAGE
                         */

                        //STEP 8
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(TRUE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.VISIBLE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 2:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 4
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(TRUE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.VISIBLE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * W.R.T; 3:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(TRUE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.VISIBLE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * W.R.T; 4:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(TRUE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 2: ALL FALSE
                        else if (afternoon.getTwelve_pm().equals(FALSE) && afternoon.getOne_pm().equals(FALSE) && afternoon.getTwo_pm().equals(FALSE)
                                && afternoon.getThree_pm().equals(FALSE) && afternoon.getFour_pm().equals(FALSE))
                        {
                            twelve_pm_btn.setVisibility(View.GONE);
                            one_pm_btn.setVisibility(View.GONE);
                            two_pm_btn.setVisibility(View.GONE);
                            three_pm_btn.setVisibility(View.GONE);
                            four_pm_btn.setVisibility(View.GONE);
                        }
                    }
                });



        eveningDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Evening evening = documentSnapshot.toObject(Evening.class);

                        /**
                         * W.R.T; 5:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 4
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 6
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 7
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 8: ALL TRUE
                        else if (evening.getFive_pm().equals(TRUE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.VISIBLE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 6:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 4
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(TRUE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.VISIBLE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 7:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(TRUE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.VISIBLE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * W.R.T; 8:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(TRUE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 2: ALL FALSE
                        else if (evening.getFive_pm().equals(FALSE) && evening.getSix_pm().equals(FALSE) && evening.getSeven_pm().equals(FALSE)
                                && evening.getEight_pm().equals(FALSE))
                        {
                            five_pm_btn.setVisibility(View.GONE);
                            six_pm_btn.setVisibility(View.GONE);
                            seven_pm_btn.setVisibility(View.GONE);
                            eight_pm_btn.setVisibility(View.GONE);
                        }
                    }
                });


        nightDb.document(hisUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Night night = documentSnapshot.toObject(Night.class);

                        /**
                         * W.R.T; 9:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        if (night.getNine_pm().equals(TRUE) && night.getTen_pm().equals(FALSE) && night.getEleven_pm().equals(FALSE))
                        {
                            nine_pm_btn.setVisibility(View.VISIBLE);
                            ten_pm_btn.setVisibility(View.GONE);
                            eleven_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (night.getNine_pm().equals(TRUE) && night.getTen_pm().equals(TRUE) && night.getEleven_pm().equals(FALSE))
                        {
                            nine_pm_btn.setVisibility(View.VISIBLE);
                            ten_pm_btn.setVisibility(View.VISIBLE);
                            eleven_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 3
                        else if (night.getNine_pm().equals(TRUE) && night.getTen_pm().equals(FALSE) && night.getEleven_pm().equals(TRUE))
                        {
                            nine_pm_btn.setVisibility(View.VISIBLE);
                            ten_pm_btn.setVisibility(View.GONE);
                            eleven_pm_btn.setVisibility(View.VISIBLE);
                        }

                        /**
                         * 2ND STAGE
                         */

                        //STEP 4: ALL TRUE
                        else if (night.getNine_pm().equals(TRUE) && night.getTen_pm().equals(TRUE) && night.getEleven_pm().equals(TRUE))
                        {
                            nine_pm_btn.setVisibility(View.VISIBLE);
                            ten_pm_btn.setVisibility(View.VISIBLE);
                            eleven_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 10:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (night.getNine_pm().equals(FALSE) && night.getTen_pm().equals(TRUE) && night.getEleven_pm().equals(FALSE))
                        {
                            nine_pm_btn.setVisibility(View.GONE);
                            ten_pm_btn.setVisibility(View.VISIBLE);
                            eleven_pm_btn.setVisibility(View.GONE);
                        }
                        //STEP 2
                        else if (night.getNine_pm().equals(FALSE) && night.getTen_pm().equals(TRUE) && night.getEleven_pm().equals(TRUE))
                        {
                            nine_pm_btn.setVisibility(View.GONE);
                            ten_pm_btn.setVisibility(View.VISIBLE);
                            eleven_pm_btn.setVisibility(View.VISIBLE);
                        }


                        /**
                         * W.R.T; 11:00PM
                         *
                         * 1ST STAGE
                         */

                        //STEP 1
                        else if (night.getNine_pm().equals(FALSE) && night.getTen_pm().equals(FALSE) && night.getEleven_pm().equals(TRUE))
                        {
                            nine_pm_btn.setVisibility(View.GONE);
                            ten_pm_btn.setVisibility(View.GONE);
                            eleven_pm_btn.setVisibility(View.VISIBLE);
                        }
                        //STEP 2: ALL FALSE
                        else if (night.getNine_pm().equals(FALSE) && night.getTen_pm().equals(FALSE) && night.getEleven_pm().equals(FALSE))
                        {
                            nine_pm_btn.setVisibility(View.GONE);
                            ten_pm_btn.setVisibility(View.GONE);
                            eleven_pm_btn.setVisibility(View.GONE);
                        }

                    }
                });

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