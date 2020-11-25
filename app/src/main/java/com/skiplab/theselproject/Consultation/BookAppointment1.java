package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.TimeDayAlreadyChosen;
import com.skiplab.theselproject.models.Afternoon;
import com.skiplab.theselproject.models.Appointment;
import com.skiplab.theselproject.models.Early;
import com.skiplab.theselproject.models.Evening;
import com.skiplab.theselproject.models.Morning;
import com.skiplab.theselproject.models.Night;
import com.skiplab.theselproject.models.User;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class BookAppointment1 extends AppCompatActivity {

    private static final String TAG = "BookAppointment1";

    private Context mContext = BookAppointment1.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference usersDb;

    private CollectionReference earlyDb, morningDb, afternoonDb, eveningDb, nightDb;
    private CollectionReference appointmentDb;

    private ImageView closeBtn;

    private Button five_am_btn, six_am_btn;
    private Button seven_am_btn, eight_am_btn, nine_am_btn, ten_am_btn, eleven_am_btn;
    private Button twelve_pm_btn, one_pm_btn, two_pm_btn, three_pm_btn, four_pm_btn;
    private Button five_pm_btn, six_pm_btn, seven_pm_btn, eight_pm_btn;
    private Button nine_pm_btn, ten_pm_btn, eleven_pm_btn;

    String five_am = "05:00", six_am = "06:00";
    String seven_am = "07:00", eight_am = "08:00", nine_am = "09:00", ten_am = "10:00", eleven_am = "11:00";
    String twelve_pm = "12:00", one_pm = "13:00", two_pm = "14:00", three_pm = "15:00", four_pm = "16:00";
    String five_pm = "17:00", six_pm = "18:00", seven_pm = "19:00", eight_pm = "20:00";
    String nine_pm = "21:00", ten_pm = "22:00", eleven_pm = "23:00";


    String hisUID, myUID, myName;
    long hisCost, dateMillis;
    int wallet;

    String TRUE = "true", FALSE="false";

    LocalDate todayDate, expiryDate;

    Date sd;

    Calendar selected_date, date1;
    SimpleDateFormat simpleDateFormat, simpleDateFormat1;

    int i = 0;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment1);

        setupFirebaseAuth();

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        hisCost = intent.getLongExtra("hisCost",0L);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        selected_date = Calendar.getInstance();
        selected_date.add(Calendar.DATE,0);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,7);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendar_view)
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

                    dateMillis = date.getTimeInMillis();
                }
            }
        });

        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        usersDb = FirebaseDatabase.getInstance().getReference("users");
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
                        try {
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                        five_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(five_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","05:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","05:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","05:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","05:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","05:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","05:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","05:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","05:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(five_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(five_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","05:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","5:00");
                                                            intent1.putExtra("slot","0");
                                                            intent1.putExtra("endTime","5:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }

                            }
                        });

                        six_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(six_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","06:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","06:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","06:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","06:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","06:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","06:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","06:00");
                                                                                        intent1.putExtra("slot","0");
                                                                                        intent1.putExtra("endTime","06:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(six_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(six_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","06:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","6:00");
                                                            intent1.putExtra("slot","0");
                                                            intent1.putExtra("endTime","6:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
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

                        seven_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(seven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","07:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","07:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","07:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","07:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","07:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","07:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","07:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","07:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(seven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(seven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","07:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","7:00");
                                                            intent1.putExtra("slot","1");
                                                            intent1.putExtra("endTime","7:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        eight_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(eight_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","08:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","08:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","08:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","08:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","08:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","08:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","08:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","08:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eight_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eight_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","08:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","8:00");
                                                            intent1.putExtra("slot","1");
                                                            intent1.putExtra("endTime","8:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        nine_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(nine_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","09:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","09:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","09:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","09:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","09:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","09:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","09:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","09:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(nine_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(nine_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","09:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","9:00");
                                                            intent1.putExtra("slot","1");
                                                            intent1.putExtra("endTime","9:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        ten_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(ten_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","10:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","10:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","10:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","10:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","10:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","10:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","10:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","10:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(ten_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(ten_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","10:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","10:00");
                                                            intent1.putExtra("slot","1");
                                                            intent1.putExtra("endTime","10:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        eleven_am_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(eleven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","11:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","11:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","11:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","11:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","11:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","11:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","11:00");
                                                                                        intent1.putExtra("slot","1");
                                                                                        intent1.putExtra("endTime","11:30");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eleven_am) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eleven_am) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","11:00");
                                                                                        intent1.putExtra("timeType","am");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","11:00");
                                                            intent1.putExtra("slot","1");
                                                            intent1.putExtra("endTime","11:30");
                                                            intent1.putExtra("timeType","am");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
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


                        twelve_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(twelve_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(twelve_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","12:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","12:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(twelve_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","12:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","12:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(twelve_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","12:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","12:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(twelve_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","12:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","12:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(twelve_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(twelve_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","12:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","12:00");
                                                            intent1.putExtra("slot","2");
                                                            intent1.putExtra("endTime","12:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        one_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(one_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(one_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","13:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","13:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(one_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","13:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","13:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(one_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","13:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","13:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(one_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","13:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","13:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(one_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(one_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","13:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","13:00");
                                                            intent1.putExtra("slot","2");
                                                            intent1.putExtra("endTime","13:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        two_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(two_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(two_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","14:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","14:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(two_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","14:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","14:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(two_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","14:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","14:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(two_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","14:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","14:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(two_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(two_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","14:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","14:00");
                                                            intent1.putExtra("slot","2");
                                                            intent1.putExtra("endTime","14:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        three_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(three_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(three_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","15:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","15:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(three_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","15:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","15:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(three_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","15:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","15:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(three_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","15:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","15:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(three_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(two_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","15:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","15:00");
                                                            intent1.putExtra("slot","2");
                                                            intent1.putExtra("endTime","15:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        four_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(four_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(four_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","16:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","16:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(four_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","16:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","16:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(four_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","16:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","16:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(four_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","16:00");
                                                                                        intent1.putExtra("slot","2");
                                                                                        intent1.putExtra("endTime","16:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(four_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(four_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","16:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","16:00");
                                                            intent1.putExtra("slot","2");
                                                            intent1.putExtra("endTime","16:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
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


                        five_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(five_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","17:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","17:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","17:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","17:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","17:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","17:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(five_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","17:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","17:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(five_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(five_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","17:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","17:00");
                                                            intent1.putExtra("slot","3");
                                                            intent1.putExtra("endTime","17:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        six_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(six_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","18:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","18:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","18:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","18:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","18:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","18:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(six_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","18:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","18:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(six_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(six_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","18:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","18:00");
                                                            intent1.putExtra("slot","3");
                                                            intent1.putExtra("endTime","18:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        seven_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(seven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","19:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","19:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","19:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","19:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","19:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","19:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(seven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","19:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","19:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(seven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(seven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","19:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","19:00");
                                                            intent1.putExtra("slot","3");
                                                            intent1.putExtra("endTime","19:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        eight_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(eight_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","20:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","20:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","20:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","20:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","20:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","20:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eight_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","20:00");
                                                                                        intent1.putExtra("slot","3");
                                                                                        intent1.putExtra("endTime","20:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eight_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eight_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","20:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","20:00");
                                                            intent1.putExtra("slot","3");
                                                            intent1.putExtra("endTime","20:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
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


                        nine_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(nine_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","21:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","21:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","21:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","21:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","21:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","21:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(nine_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","21:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","21:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(nine_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(nine_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","21:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","21:00");
                                                            intent1.putExtra("slot","4");
                                                            intent1.putExtra("endTime","21:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        ten_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(ten_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","22:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","22:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","22:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","22:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","22:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","22:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(ten_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","22:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","22:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(ten_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(ten_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","22:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","22:00");
                                                            intent1.putExtra("slot","4");
                                                            intent1.putExtra("endTime","22:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                        eleven_pm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocalDate today = null;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();

                                    todayDate = today;

                                    if (simpleDateFormat1.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                .setMessage("Swipe the calendar to choose a date for your appointment!")
                                                .create();
                                        alertDialog.show();
                                    }
                                    else
                                    {
                                        appointmentDb.whereEqualTo("booked_date",selected_date.getTime().toInstant().toEpochMilli())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.getResult().size() > 0)
                                                        {
                                                            task.getResult().getQuery()
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                for (DocumentSnapshot ds: task.getResult().getDocuments())
                                                                                {
                                                                                    Appointment appointment = ds.toObject(Appointment.class);

                                                                                    if (appointment.getStart_time().equals(eleven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        startActivity(new Intent(mContext,ChatRoomsActivity.class));
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","23:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","23:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","23:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","23:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","23:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","23:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (!appointment.getStart_time().equals(eleven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("wallet",wallet);
                                                                                        intent1.putExtra("myName",myName);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","23:00");
                                                                                        intent1.putExtra("slot","4");
                                                                                        intent1.putExtra("endTime","23:30");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        intent1.putExtra("hisCost",hisCost);
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eleven_pm) && !appointment.getCounsellor_id().equals(hisUID)
                                                                                            && appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        i++;

                                                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                                                .setMessage("You already have an appointment scheduled for the selected time and date.")
                                                                                                .create();
                                                                                        alertDialog.show();

                                                                                        Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                startActivity(new Intent(mContext, ChatRoomsActivity.class));
                                                                                            }
                                                                                        },5000);
                                                                                    }
                                                                                    else if (appointment.getStart_time().equals(eleven_pm) && appointment.getCounsellor_id().equals(hisUID)
                                                                                            && !appointment.getClient_id().equals(myUID))
                                                                                    {
                                                                                        Intent intent1 = new Intent(mContext, TimeDayAlreadyChosen.class);
                                                                                        intent1.putExtra("hisUID",hisUID);
                                                                                        intent1.putExtra("selectedDate", dateMillis);
                                                                                        intent1.putExtra("startTime","23:00");
                                                                                        intent1.putExtra("timeType","pm");
                                                                                        startActivity(intent1);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                        else
                                                        {
                                                            Intent intent1 = new Intent(mContext, BookAppointment2.class);
                                                            intent1.putExtra("hisUID",hisUID);
                                                            intent1.putExtra("wallet",wallet);
                                                            intent1.putExtra("myName",myName);
                                                            intent1.putExtra("selectedDate", dateMillis);
                                                            intent1.putExtra("startTime","23:00");
                                                            intent1.putExtra("slot","4");
                                                            intent1.putExtra("endTime","23:30");
                                                            intent1.putExtra("timeType","pm");
                                                            intent1.putExtra("hisCost",hisCost);
                                                            startActivity(intent1);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        isActivityRunning = true;
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

                usersDb.orderByKey().equalTo(myUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                    myName = ds.getValue(User.class).getUsername();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };


    }
}
