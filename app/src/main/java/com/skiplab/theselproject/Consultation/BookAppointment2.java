package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.JavaMailAPI;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static android.provider.CalendarContract.ACTION_EVENT_REMINDER;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

public class BookAppointment2 extends AppCompatActivity {

    private static final String TAG = "BookAppointment2";

    private static final int REQUEST_PERMISSION_CODE = 40;

    private Context mContext = BookAppointment2.this;

    private FirebaseAuth.AuthStateListener mAuthListener;

    DatabaseReference usersRef;
    CollectionReference appointmentsRef;

    ImageView hisImageIv;
    TextView hisNameTv, hisCategoryTv, hisCostTv, apptDateTv, apptTimeTv, apptFeeTv;
    Button confirmBtn;

    String hisName;
    String myUID, myEmail, myName;
    String hisUID, startTime, endTime, timeType, slot;
    Long selectedDate;
    Long hisCost;

    int wallet;
    int i = 0;

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
        appointmentsRef = FirebaseFirestore.getInstance().collection("appointments");

        locale = new Locale("en", "NG");
        fmt = NumberFormat.getCurrencyInstance(locale);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        simpleDateFormat1 = new SimpleDateFormat("HH:mm");

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        slot = intent.getStringExtra("slot");
        myName = intent.getStringExtra("myName");
        wallet = intent.getIntExtra("wallet",0);
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

                        ProgressDialog pd = new ProgressDialog(mContext);
                        pd.setMessage("Loading...");
                        pd.show();

                        long result0 = hisCost/100;

                        if (!(wallet >= (result0)))
                        {
                            pd.dismiss();

                            AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                    .setMessage("Insufficient funds!!!")
                                    .create();
                            alertDialog.show();

                            i++;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    startActivity(new Intent(mContext, WalletActivity.class));
                                }
                            },2000);
                        }
                        else
                        {
                            long result1 = wallet - result0;

                            usersRef.child(myUID).child("wallet").setValue(result1);

                            String appointmentId = UUID.randomUUID().toString();

                            HashMap<String, Object> appointmentMap = new HashMap<>();
                            appointmentMap.put("counsellor_id", hisUID);
                            appointmentMap.put("client_id", myUID);
                            appointmentMap.put("appointment_id", appointmentId);
                            appointmentMap.put("booked_date", simpleDateFormat.format(selectedDate));
                            appointmentMap.put("start_time", startEventTime);
                            appointmentMap.put("end_time",endEventTime);
                            appointmentMap.put("slot",slot);
                            appointmentMap.put("absent",false);
                            appointmentMap.put("num_messages",0);
                            appointmentMap.put("timeType",timeType);
                            appointmentMap.put("timestamp", FieldValue.serverTimestamp());

                            appointmentsRef.document(appointmentId).set(appointmentMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            JavaMailAPI javaMailAPI = new JavaMailAPI(
                                                    mContext,
                                                    "skiplab.innovation@gmail.com",
                                                    "ayomideseaz@gmail.com",
                                                    "THESEL CONSULTATION",
                                                    "Hello "+hisName+","+"\n\n"+"You have an appointment from "+startEventTime+" - "+endEventTime+" "+timeType
                                                            +" on "+simpleDateFormat.format(selectedDate)+" with "+myName+
                                                            "\n\n\n"+"Warm Regards,"+"\n"+"Thesel Team.");

                                            javaMailAPI.execute();

                                            pd.dismiss();

                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                                            builder1.setTitle("Add Reminder");
                                            builder1.setMessage("Add a reminder to your calendar.");
                                            builder1.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        Date start = simpleDateFormat1.parse(startEventTime);
                                                        Date end = simpleDateFormat1.parse(endEventTime);

                                                        Intent intent0 = new Intent(Intent.ACTION_INSERT);
                                                        intent0.setData(CalendarContract.Events.CONTENT_URI);

                                                        intent0.putExtra(CalendarContract.Events.TITLE, "Thesel Appointment");
                                                        intent0.putExtra(CalendarContract.Events.DESCRIPTION, "Appointment from "+startEventTime+" - "+endEventTime+" "+timeType
                                                                +" on "+simpleDateFormat.format(selectedDate)+" with "+hisName);
                                                        intent0.putExtra(CalendarContract.Events.ALL_DAY, false);
                                                        intent0.putExtra(EXTRA_EVENT_BEGIN_TIME,start.getTime()+"");
                                                        intent0.putExtra(EXTRA_EVENT_END_TIME, end.getTime()+"");
                                                        intent0.putExtra(Intent.EXTRA_EMAIL, myEmail+", skiplab.innovation@gmail.com");
                                                        intent0.putExtra(ACTION_EVENT_REMINDER,true);

                                                        intent0.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                                                        if(intent0.resolveActivity(getPackageManager()) != null){
                                                            startActivity(intent0);
                                                            finish();
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
                                                    Intent intent1 = new Intent(mContext, ChatRoomsActivity.class);
                                                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent1);
                                                    finish();
                                                }
                                            });

                                            builder1.show();
                                        }
                                    });
                        }

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
