package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.provider.CalendarContract.ACTION_EVENT_REMINDER;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

public class BookAppointment2 extends AppCompatActivity {

    private static final String TAG = "BookAppointment2";

    private static final int REQUEST_PERMISSION_CODE = 40;

    private Context mContext = BookAppointment2.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference usersRef;

    ImageView hisImageIv;
    TextView hisNameTv, hisCategoryTv, hisCostTv, apptDateTv, apptTimeTv, apptFeeTv;
    Button confirmBtn;

    String hisName;
    String myUID, myEmail;
    String hisUID, startTime, endTime, timeType;
    Long selectedDate;
    Long hisCost;

    Locale locale;
    NumberFormat fmt;

    SimpleDateFormat simpleDateFormat,simpleDateFormat1;

    public static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment2);

        setupFirebaseAuth();

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        locale = new Locale("en", "NG");
        fmt = NumberFormat.getCurrencyInstance(locale);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat1 = new SimpleDateFormat("HH:mm");

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        timeType = intent.getStringExtra("timeType");
        hisCost = intent.getLongExtra("hisCost",0L);
        selectedDate = intent.getLongExtra("selectedDate",1L);

        String appointmentDuration = startTime+"-"+endTime;

        String[] convertTime = appointmentDuration.split("-");

        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); //we get 5
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); //we get 00

        String[] endTimeConvert = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(endTimeConvert[0].trim()); //we get 5
        int endMinInt = Integer.parseInt(endTimeConvert[1].trim()); //we get 30

        Calendar startEvent = Calendar.getInstance();
        startEvent.set(Calendar.HOUR_OF_DAY,startHourInt);
        startEvent.set(Calendar.MINUTE,startMinInt);

        Calendar endEvent = Calendar.getInstance();
        endEvent.set(Calendar.HOUR_OF_DAY,endHourInt);
        endEvent.set(Calendar.MINUTE,endMinInt);

        String startEventTime = simpleDateFormat1.format(startEvent.getTime());
        String endEventTime = simpleDateFormat1.format(endEvent.getTime());


        hisNameTv = findViewById(R.id.hisName);
        hisCategoryTv = findViewById(R.id.hisCatgeory);
        hisCostTv = findViewById(R.id.appt_fee_tv);
        hisImageIv = findViewById(R.id.hisImage);
        apptDateTv = findViewById(R.id.appt_date_tv);
        apptTimeTv = findViewById(R.id.appt_time_tv);
        apptFeeTv = findViewById(R.id.appt_fee_tv);
        confirmBtn = findViewById(R.id.confirm_button);

        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            hisName = ds.getValue(User.class).getUsername();
                            hisNameTv.setText(ds.getValue(User.class).getUsername());
                            hisCategoryTv.setText(ds.getValue(User.class).getCategory1());

                            try {
                                UniversalImageLoader.setImage(ds.getValue(User.class).getProfile_photo(), hisImageIv, null, "");
                            }
                            catch (Exception e){
                                Log.d("ERROR: ", ""+e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });

        apptDateTv.setText(simpleDateFormat.format(selectedDate));
        apptTimeTv.setText(startEventTime+" - "+endEventTime+" "+timeType);
        hisCostTv.setText(fmt.format(hisCost/100));

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                final View mView =  LayoutInflater.from(mContext).inflate(R.layout.layout_appointment_warning, null);

                Button button = mView.findViewById(R.id.btn_ok);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        builder1.setTitle("Add Reminder");
                        builder1.setMessage("Add a reminder to your calendar.");
                        builder1.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Date start = simpleDateFormat1.parse(startEventTime);
                                    Date end = simpleDateFormat1.parse(endEventTime);

                                    Intent intent = new Intent(Intent.ACTION_INSERT);
                                    intent.setData(CalendarContract.Events.CONTENT_URI);

                                    intent.putExtra(CalendarContract.Events.TITLE, "Thesel Appointment");
                                    intent.putExtra(CalendarContract.Events.DESCRIPTION, "Appointment from "+startEventTime+" - "+endEventTime+" "+timeType
                                            +" on the "+simpleDateFormat.format(selectedDate)+" with "+hisName);
                                    intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                                    intent.putExtra(EXTRA_EVENT_BEGIN_TIME,start.getTime()+"");
                                    intent.putExtra(EXTRA_EVENT_END_TIME, end.getTime()+"");
                                    intent.putExtra(Intent.EXTRA_EMAIL, myEmail+", skiplab.innovation@gmail.com");
                                    intent.putExtra(ACTION_EVENT_REMINDER,true);

                                    if(intent.resolveActivity(getPackageManager()) != null){
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(mContext, "You have no calendar app that supports this action", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder1.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder1.show();
                    }
                });

                builder.setView(mView);
                builder.show();

            }
        });

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR
        },REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_calendar_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int read_calendar_result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CALENDAR);
        return write_calendar_result == PackageManager.PERMISSION_GRANTED &&
                read_calendar_result == PackageManager.PERMISSION_GRANTED;
    }

/*
    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, StringBuilder description) {
        try {
            Date start = simpleDateFormat1.parse(startEventTime);
            Date end = simpleDateFormat1.parse(endEventTime);

            ContentValues event = new ContentValues();

            //put
            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(mContext));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION, String.valueOf(description));
            event.put(CalendarContract.Events.CUSTOM_APP_URI, "https://play.google.com/store/apps/details?id=com.skiplab.theselproject");

            //time
            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,true);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

            Uri calendars = Uri.parse("content://com.android.calendar/calendars");

            mContext.getContentResolver().insert(calendars,event);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
*/


    private String getCalendar(Context context) {
        //get default calendar ID of calendar of gmail
        String gmailIdCalendar = "";
        String projection[]={"_id","calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        //Select all calendar
        Cursor managedCursor = contentResolver.query(calendars,projection,null,null,null);
        if (managedCursor.moveToFirst())
        {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com"))
                {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break; //Exit as soon as id is gotten
                }

            }while (managedCursor.moveToNext());
            managedCursor.close();
        }

        return null;
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
            myEmail = user.getEmail();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };
    }

    /*Appointment appointment0 = new Appointment();
                                                                        appointment0.setClient_id(myUID);
                                                                        appointment0.setCounsellor_id(hisUID);
                                                                        appointment0.setBooked_date(simpleDateFormat.format(selected_date.getTime()));
                                                                        appointment0.setNum_messages(0);
                                                                        appointment0.setStartTime("05:00")
                                                                        appointment0.setEndTime("05:40");
                                                                        appointment0.setTimestamp(timestamp);
                                                                        appointment0.setSlot("0");
                                                                        appointment0.setAppointment_id(String chatroomId = UUID.randomUUID().toString(););
                                                                        appointment0.setAbsent(false);*/

    /*
    LocalDate today = null;
                                LocalDateTime time = null;
                                LocalDateTime nowtime;

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    today = LocalDate.now();
                                    time = LocalDateTime.now();

                                    todayDate = today;
                                    nowtime = time;


                                    if (simpleDateFormat.format(selected_date.getTime()).equals(todayDate.toString()))
                                    {
                                        calendar = Calendar.getInstance();
                                        calendar.setTime(selected_date.getTime());
                                        calendar.add(Calendar.HOUR_OF_DAY,6);

                                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                                        if (selected_date.getTime().after(calendar.getTime()))
                                            Toast.makeText(mContext,"NO!!!",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(mContext,formatter.format(selected_date.getTime())+": "+formatter.format(calendar.getTime()),Toast.LENGTH_SHORT).show();


                                    }
                                    }
    */


     /*if (checkPermissionFromDevice())
                {
                    addToDeviceCalendar(startEventTime, endEventTime,"Thesel Appointment",
                            new StringBuilder("Appointment from ")
                                    .append(appointmentDuration)
                                    .append(" on the ")
                                    .append(simpleDateFormat.format(selectedDate))
                                    .append(" with ")
                                    .append(hisName));
                }
                else
                    requestPermission();*/
}
