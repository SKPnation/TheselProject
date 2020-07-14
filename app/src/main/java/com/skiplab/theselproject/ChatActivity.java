package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skiplab.theselproject.Adapter.AdapterChat;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Chat;
import com.skiplab.theselproject.models.LatenessReports;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private static final int REQUEST_PERMISSION_CODE = 1000;

    private static final long START_TIME_IN_MILLIS = 4200000;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //imagepick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    CircleImageView profileIv;
    TextView nameTv, countDownTv;
    EditText messageEt;
    Button mButtonStartPause, mReportBtn;
    ImageButton sendBtn, attachBtn, recordBtn;
    private ProgressDialog progressDialog;

    String adminUid = "MrRpckxLVqVzscd34r6PAxIJNVC2";

    List<Chat> chatList;
    AdapterChat adapterChat;

    SwipeRefreshLayout swipeRefreshLayout;

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference usersRef, sessionsRef;
    FirebaseDatabase firebaseDatabase;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    public static boolean isActivityRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    String hisUid;
    String myUid;
    String hisImage;

    APIService apiService;
    boolean notify = false;

    //image picked will be saved in this uri
    Uri image_uri = null;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    String pathSave = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        progressDialog = new ProgressDialog(this);

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        recyclerView = findViewById(R.id.chat_recylerView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        countDownTv = findViewById(R.id.text_view_countdown);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mReportBtn = findViewById(R.id.reportBtn);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        attachBtn = findViewById(R.id.attachBtn);
        recordBtn = findViewById(R.id.recordBtn);

        //Layout (LinearLayout) for the RecyclerView
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        final Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        sessionsRef = firebaseDatabase.getReference("sessions");

        //search user to get user's info
        Query query1 = usersRef
                .orderByKey()
                .equalTo( hisUid );

        //orderByKey method will look for the key encapsulating the values of the object

        query1.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                    User user = singleSnapshot.getValue(User.class);
                    Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                    nameTv.setText(user.getUsername());
                    hisImage = user.getProfile_photo();

                    try {
                        UniversalImageLoader.setImage(user.getProfile_photo(), profileIv, null, "");
                    }
                    catch (Exception e){
                        //
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        } );

        mReportBtn.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.lateness_report_dialog, null);

            final EditText reportMessageEt = mView.findViewById(R.id.report_message);
            String report_message = reportMessageEt.getText().toString().trim();

            builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Query query = usersRef.orderByKey().equalTo(myUid);

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                User user = ds.getValue(User.class);
                                if (user.getIsStaff().equals("false"))
                                {
                                    Query queryStaff = usersRef.orderByKey().equalTo(hisUid);
                                    queryStaff.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                User userStaff = ds.getValue(User.class);

                                                String timestamp = String.valueOf(System.currentTimeMillis());

                                                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                                                calendar.setTimeInMillis(Long.parseLong(timestamp));
                                                String sTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                                                String counsellor_id = userStaff.getUid();
                                                String counsellor_name = userStaff.getUsername();
                                                String cost = String.valueOf(userStaff.getCost());
                                                String client_name = user.getUsername();
                                                String client_id = user.getUid();
                                                String client_phone = user.getPhone();
                                                String client_email = user.getEmail();

                                                String message = counsellor_name+" "+report_message;

                                                LatenessReports reports = new LatenessReports();
                                                reports.setCounsellor_id(counsellor_id);
                                                reports.setCounsellor_name(counsellor_name);
                                                reports.setCost(cost);
                                                reports.setClient_id(client_id);
                                                reports.setClient_name(client_name);
                                                reports.setClient_email(client_email);
                                                reports.setClient_phone(client_phone);
                                                reports.setReport_message(message);
                                                reports.setTimestamp(sTime);


                                                FirebaseDatabase.getInstance().getReference("latenessReports")
                                                        .child(timestamp).setValue(reports);

                                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
                                                database.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if (notify)
                                                        {
                                                            sendNotification(adminUid, client_name, message);
                                                            //Toast.makeText(ChatActivity.this, "Report sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //..
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
                }
            });

            builder.setView(mView);
            builder.show();
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning){
                    mButtonStartPause.setVisibility(View.GONE);
                    mReportBtn.setVisibility(View.VISIBLE);
                    //mButtonReset.setVisibility(View.VISIBLE);
                } else {
                    startTimer();
                }
            }
        });

        updateCountDownText();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                isThisUserAStaff(myUid);
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTimerRunning)
                {
                    showImagePickDialog();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Double tap the start button.");

                    builder.show();
                }
            }
        });

        //View
        swipeRefreshLayout = findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToTheInternet(getBaseContext())){
                    readMessages();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
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
                if(Common.isConnectedToTheInternet(getBaseContext())){
                    readMessages();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setMessage("Please check your internet connection");

                    builder.show();
                    return;
                }
            }
        } );

        recordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkPermissionFromDevice())
                    {
                        if (mTimerRunning)
                        {
                            pathSave = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath()+"/"
                                    + UUID.randomUUID().toString()+"_audio_record.3gp";
                            setupMediaRecorder();
                            try {
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setMessage("Recording...");
                            builder.setCancelable(false);
                            builder.setPositiveButton("STOP", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    stopRecording();
                                }
                            });

                            builder.show();
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setMessage("Double tap the start button.");

                            builder.show();
                        }
                    }
                    else
                    {
                        requestPermission();
                    }


                }
            });
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }



    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();

        uploadAudio();
    }

    private void uploadAudio() {
        progressDialog.setMessage("Uploading Audio...");
        progressDialog.show();

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Audio/").child("new_audio.mp3");

        Uri uri = Uri.fromFile(new File(pathSave));

        filePath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //image uploaded
                        progressDialog.dismiss();
                        //get url of uploaded image
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            //add image uri and other info to database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                            final String timeStamp = ""+System.currentTimeMillis();

                            //setup equired data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", myUid);
                            hashMap.put("receiver", hisUid);
                            hashMap.put("message", downloadUri);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("type", "audio");

                            databaseReference.child("chats").push().setValue(hashMap);

                            //send Notification
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = new User();

                                    if (notify){
                                        progressDialog.dismiss();
                                        sendNotification(hisUid, user.getUsername(), " Sent you a recording...");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });
                        }
                        Toast.makeText(ChatActivity.this, "Uploaded Successfully.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, "Failed!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showImagePickDialog() {

        //options(Camera, Gallery) to show in Dialog
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from:");
        //set options to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //Camera clicked
                    //We need to check permissions first
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if (which == 1){
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.show();
    }

    private void pickFromGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
        //intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Desc");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permissions
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){
        //check if camera permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //request runtime storage permissions
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }


    //handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this method is called when user press Allow or Deny from permission request Dialog
        //here will handle permissions cases (allowed and denied)

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //both permission granted
                        pickFromCamera();
                    }
                    else {
                        //camera or gallery or both permissions denied
                        Toast.makeText(ChatActivity.this, "Camera & Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //storage permission granted
                        pickFromGallery();
                    }
                    else {
                        //gallery permissions denied
                        Toast.makeText(ChatActivity.this, "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case REQUEST_PERMISSION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }


    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);

                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }

                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                    swipeRefreshLayout.setRefreshing( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }


    private void isThisUserAStaff(String myUid) {

        Query query = usersRef.orderByKey().equalTo(myUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (user.getIsStaff().equals("false"))
                    {
                        if (mTimerRunning)
                        {
                            String message = messageEt.getText().toString().trim();

                            if (TextUtils.isEmpty(message)){
                                Toast.makeText(ChatActivity.this, "Cannot send an empty message", Toast.LENGTH_SHORT).show();
                            } else {
                                sendMessage(message);

                                Toast.makeText(ChatActivity.this, "Wait for a reply...", Toast.LENGTH_SHORT).show();
                            }
                            //reset edittext after sending message
                            messageEt.setText("");
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setMessage("Double tap the start button.");

                            builder.show();
                        }
                    }
                    else
                    {
                        if (mTimerRunning)
                        {
                            String message = messageEt.getText().toString().trim();

                            if (TextUtils.isEmpty(message)){
                                Toast.makeText(ChatActivity.this, "Cannot send an empty message", Toast.LENGTH_SHORT).show();
                            } else {
                                sendMessage(message);

                                Toast.makeText(ChatActivity.this, "Wait for a reply...", Toast.LENGTH_SHORT).show();
                            }
                            //reset edittext after sending message
                            messageEt.setText("");
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setMessage("Double tap the start button.");

                            builder.show();
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

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");
        //hashMap.put("isSeen", false);
        databaseReference.child("chats").push().setValue(hashMap);

        //String msg = message;



        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (notify)
                {
                    sendNotification(hisUid, user.getUsername(), message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });


    }

    private void sendImageMessage(Uri image_uri) {
        notify = true;

        progressDialog.setMessage("Sending image...");
        progressDialog.show();

        final String timeStamp = ""+System.currentTimeMillis();

        String fileNameAndPath = "ChatImages/"+"post_"+timeStamp;

        //get bitmap from image uri
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray(); //convert images to bytes
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
            reference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded
                            progressDialog.dismiss();
                            //get url of uploaded image
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){
                                //add image uri and other info to database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                //setup equired data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", myUid);
                                hashMap.put("receiver", hisUid);
                                hashMap.put("message", downloadUri);
                                hashMap.put("timestamp", timeStamp);
                                hashMap.put("type", "image");

                                databaseReference.child("chats").push().setValue(hashMap);

                                //send Notification
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
                                database.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = new User();

                                        if (notify){
                                            progressDialog.dismiss();
                                            sendNotification(hisUid, user.getUsername(), " Sent you a picture...");
                                        }
                                        notify = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        //..
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(final String hisUid, final String username, final String message) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds.getString("token"));
                    Data data = new Data(myUid, message, username, hisUid, R.mipmap.ic_launcher2);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    //Toast.makeText(ChatActivity.this, "Sent notification"+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                    //Toast.makeText(ChatActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of the image
                image_uri = data.getData();

                sendImageMessage(image_uri);

            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of the image
                sendImageMessage(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //mButtonStartPause.setText("Start");
                mReportBtn.setVisibility(View.VISIBLE);
                mButtonStartPause.setVisibility(View.GONE);

                Query query = usersRef.orderByKey().equalTo(myUid);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            if (user.getIsStaff().equals("false"))
                            {
                                int i = 0;

                                DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                sessionsRef.child(hisUid).child("client_id").setValue("no client");

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                builder.setMessage("TIME'S UP!!!" );
                                builder.show();

                                i++;

                                Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(ChatActivity.this, DashboardActivity.class));
                                        finish();
                                    }
                                }, 2000);

                            } else {
                                int i = 0;

                                DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                sessionsRef.child(myUid).child("client_id").setValue("no client");

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                builder.setMessage("TIME'S UP!!!" );
                                builder.show();

                                i++;

                                Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });
            }
        }.start();

        mTimerRunning = true;
        //mButtonStartPause.setText("pause");
        //mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        //mButtonStartPause.setText("Start");
        //mButtonReset.setVisibility(View.VISIBLE);
    }


    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 6000) / 10;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countDownTv.setText(timeLeftFormatted);

    }

    private void checkUserStatus(){
        //get current user
        user = firebaseAuth.getCurrentUser();
        if (user != null){
            //user is signed in stay here
            myUid = user.getUid();//get Currently signed in User's id
        }
        else {
            //user not signed in else go to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserStatus();
        isActivityRunning = true;

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setTitle("END SESSION")
                .setMessage("Are you sure?")
                .setPositiveButton("END", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        DatabaseReference reference = usersRef.child(myUid);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user.getIsStaff().equals("true"))
                                {
                                    DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                    sessionsRef.child(myUid).child("client_id").setValue("no client");

                                    int i = 0;

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                                    builder1.setMessage("Your client seat is now empty");
                                    builder1.show();

                                    i++;

                                    Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 2000);

                                }
                                else {
                                    int i = 0;

                                    DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                                    sessionsRef.child(hisUid).child("client_id").setValue("no client");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                    builder.setMessage("ENDING SESSION..." );
                                    builder.show();

                                    i++;

                                    Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(ChatActivity.this, DashboardActivity.class));
                                            finish();
                                        }
                                    }, 2000);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}
