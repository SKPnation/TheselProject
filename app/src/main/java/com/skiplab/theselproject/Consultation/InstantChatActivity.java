package com.skiplab.theselproject.Consultation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
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
import com.google.firebase.database.ValueEventListener;
//import com.skiplab.theselproject.Adapter.AdapterChat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skiplab.theselproject.Adapter.AdapterChat;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatMessage;
import com.skiplab.theselproject.models.InstantSession;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class InstantChatActivity extends AppCompatActivity {

    Context mContext = InstantChatActivity.this;

    private static final String TAG = "InstantChatActivity";

    private static final int REQUEST_RECORD_AUDIO = 0;

    private static final long START_TIME_IN_MILLIS = 900000;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //imagepick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    private MediaRecorder mediaRecorder;
    private String recordFile = "";

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    CircleImageView profileIv;
    TextView nameTv;
    EditText messageEt;
    Button mButtonRate;
    ImageButton sendBtn, attachBtn, recordBtn;

    private ProgressDialog progressDialog;

    List<ChatMessage> chatList;
    AdapterChat adapterChat;

    SwipeRefreshLayout swipeRefreshLayout;

    //firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, mMessageReference;
    CollectionReference mChatroomReference, mProfileReference;

    private CountDownTimer mCountDownTimer;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    String hisUID;
    String myUid, myName;
    String hisImage;
    String chatroomID;

    APIService apiService;
    boolean notify = false;

    //image picked will be saved in this uri
    Uri image_uri = null;

    String pathSave = "";

    SpannableString ss_thanks;
    ForegroundColorSpan fcsBlack;

    int i = 0;
    int num_messages;

    private Boolean isRecording = false;


    public static boolean isActivityRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_chat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        setupFirebaseAuth();

        mediaRecorder = new MediaRecorder();

        final Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        chatroomID = intent.getStringExtra("chatroomID");
        myName = intent.getStringExtra("myName");


        mChatroomReference = FirebaseFirestore.getInstance().collection("instantSessions");
        mMessageReference = FirebaseDatabase.getInstance().getReference("chatroom_messages");
        mProfileReference = FirebaseFirestore.getInstance().collection("profiles");

        //init views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        recyclerView = findViewById(R.id.chat_recylerView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        mButtonRate = findViewById(R.id.rate_btn);
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

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        recordBtn = findViewById(R.id.recordBtn);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"Oops! Error. Try again later",Toast.LENGTH_SHORT).show();
                if (checkPermissionFromDevice())
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    final View mView =  LayoutInflater.from(mContext).inflate(R.layout.recording_dialog, null);

                    ImageView record_btn = mView.findViewById(R.id.record_btn);
                    TextView record_file_name = mView.findViewById(R.id.record_filename);
                    Chronometer timer = mView.findViewById(R.id.record_timer);

                    record_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isRecording)
                            {
                                //Stop Recording
                                stopRecording(timer, record_file_name);
                                record_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_black, null));
                                isRecording = false;
                            }
                            else
                            {
                                startRecording(timer, record_file_name);
                                record_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_blue, null));
                                isRecording = true;
                            }
                        }
                    });

                    alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (recordFile.isEmpty())
                            {
                                Toast.makeText(mContext,"Press the mic button to start recording",Toast.LENGTH_SHORT).show();
                                //stopRecording(timer, record_file_name);
                            }
                            else
                            {
                                if (isRecording)
                                {
                                    AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                            .setMessage("Press the mic button to stop recording")
                                            .create();
                                    alertDialog.show();
                                }
                                else
                                    uploadAudio();
                            }
                        }
                    });


                    alertDialog.setView(mView).show();


                }
                else
                    requestPermission();

            }
        });



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = messageEt.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    Toast.makeText(InstantChatActivity.this, "Cannot send an empty message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }
                //reset edittext after sending message
                messageEt.setText("");

            }
        });

        getRecepientDetails();
        readMessages();
    }

    private void startRecording(Chronometer timer, TextView filenameTv) {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath  = mContext.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss" , Locale.CANADA);
        Date now  = new Date();
        recordFile= "Recording..." + UUID.randomUUID().toString().substring(0,4)+"_"+formatter.format(now) + ".mp3";
        filenameTv.setText("Recording, File Name : " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        }catch (IOException e ){
            e.printStackTrace();
        }
        mediaRecorder.start();
    }


    private void stopRecording(Chronometer timer, TextView filenameTv) {
        timer.stop();
        filenameTv.setText("Recording Stopped, File Saved : " + recordFile);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }


    private void getRecepientDetails() {
        usersRef.orderByKey().equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot: dataSnapshot.getChildren() ){
                            User user = singleSnapshot.getValue(User.class);
                            Log.d( TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());

                            if (user.getIsStaff().equals("true"))
                            {
                                nameTv.setText(user.getUsername());
                                hisImage = user.getProfile_photo();

                                try {
                                    UniversalImageLoader.setImage(hisImage, profileIv, null, "");
                                }
                                catch (Exception e){
                                    Log.d(TAG, "ERROR: "+e);
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(InstantChatActivity.this);
                                builder.setTitle("PLEASE READ THIS!");
                                builder.setMessage("1. You have 15 MINUTES to say what's on your mind!\n\n"
                                        +"2. The moment you start the timer, #1500 will be deducted from your THESEL WALLET!\n\n"
                                        +"3. Check back to see the consultant's reply to your messages during his/her counselling hours!\n\n"
                                        +"4. Click the '"+ss_thanks+"' button if you are satisfied with the consultation!\n\n"
                                        +"5. This ONGOING SESSION will end if you do not reply the counsellor's message within 24 HOURS!\n\n"
                                        +"6. EXCHANGE OF PHONE NUMBERS AND EMAIL ADDRESSES ARE NOT ALLOWED!!!");
                                builder.setPositiveButton("BEGIN", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                //builder.show();

                            }
                            else if (user.getIsStaff().equals("false"))
                            {
                                nameTv.setText(user.getUsername());
                                hisImage = user.getProfile_photo();

                                mButtonRate.setVisibility(View.GONE);

                                try {
                                    UniversalImageLoader.setImage(hisImage, profileIv, null, "");
                                }
                                catch (Exception e){
                                    Log.d(TAG, "ERROR: "+e);
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(InstantChatActivity.this);
                                builder.setTitle("PLEASE READ THIS!");
                                builder.setMessage("EXCHANGE OF PHONE NUMBERS AND EMAIL ADDRESSES ARE NOT ALLOWED!!!");
                                builder.setPositiveButton("BEGIN", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //...
                    }
                });
    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender_id", myUid);
        hashMap.put("receiver_id", hisUID);
        hashMap.put("chatroom_id", chatroomID);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");

        mMessageReference.push().setValue(hashMap);

        mChatroomReference.document(chatroomID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            InstantSession instantSession = task.getResult().toObject(InstantSession.class);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("num_messages", instantSession.getNum_messages()+1);
                            mChatroomReference.document(chatroomID).set(hashMap, SetOptions.merge());
                        }
                    }
                });

        if (notify)
        {
            //sendNotification(hisUID, myName, message);
        }

    }

    private void readMessages() {
        chatList = new ArrayList<>();
        mMessageReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ChatMessage chat = ds.getValue(ChatMessage.class);

                            if (chat.getReceiver_id().equals(myUid) && chat.getSender_id().equals(hisUID) ||
                                    chat.getReceiver_id().equals(hisUID) && chat.getSender_id().equals(myUid)){
                                chatList.add(chat);
                            }

                            //adapter
                            adapterChat = new AdapterChat(InstantChatActivity.this, chatList, hisImage);
                            adapterChat.notifyDataSetChanged();
                            recyclerView.setAdapter(adapterChat);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }
                });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_RECORD_AUDIO);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void stopRecording() {
        //mediaRecorder.stop();
        mediaRecorder.release();

        uploadAudio();
    }

    private void uploadAudio() {
        progressDialog.setMessage("Uploading Audio...");
        progressDialog.show();

        String timeStamp = ""+System.currentTimeMillis();



        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("Audio").child("audio.mp3"+timeStamp);

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


                            //setup equired data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender_id", myUid);
                            hashMap.put("receiver_id", hisUID);
                            hashMap.put("chatroom_id",chatroomID);
                            hashMap.put("message", downloadUri);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("type", "audio");

                            databaseReference.child("chatroom_messages").push().setValue(hashMap);

                            mChatroomReference.document(chatroomID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                InstantSession instantSession = task.getResult().toObject(InstantSession.class);
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("num_messages", instantSession.getNum_messages()+1);
                                                mChatroomReference.document(chatroomID).set(hashMap, SetOptions.merge());
                                            }
                                        }
                                    });


                            //send Notification
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = new User();

                                    if (notify){
                                        progressDialog.dismiss();
                                        //sendNotification(hisUID, user.getUsername(), "Sent you a recording...");
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(mContext, "Failed!!!", Toast.LENGTH_LONG).show();
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

                                //setup required data
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender_id", myUid);
                                hashMap.put("receiver_id", hisUID);
                                hashMap.put("chatroom_id", chatroomID);
                                hashMap.put("message", downloadUri);
                                hashMap.put("timestamp", timeStamp);
                                hashMap.put("type", "image");

                                databaseReference.child("chatroom_messages").push().setValue(hashMap);

                                mChatroomReference.document(chatroomID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    InstantSession instantSession = task.getResult().toObject(InstantSession.class);
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("num_messages", instantSession.getNum_messages()+1);
                                                    mChatroomReference.document(chatroomID).set(hashMap, SetOptions.merge());
                                                }
                                            }
                                        });

                                //send Notification
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
                                database.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = new User();

                                        if (notify){
                                            progressDialog.dismiss();
                                            //sendNotification(hisUID, user.getUsername(), "Sent you a picture...");
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
                        //pickFromCamera();
                    }
                    else {
                        //camera or gallery or both permissions denied
                        Toast.makeText(InstantChatActivity.this, "Camera & Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //storage permission granted
                        //pickFromGallery();
                    }
                    else {
                        //gallery permissions denied
                        Toast.makeText(InstantChatActivity.this, "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case REQUEST_RECORD_AUDIO:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
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
            else if (requestCode == REQUEST_RECORD_AUDIO){
                //.
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*private void sendNotification(String hisUID, String myName, String message) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener((queryDocumentSnapshots, e) -> {
            for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                Token token = new Token(ds1.getString("token"));
                Data data = new Data(myUid, message, myName, hisUID, R.mipmap.ic_launcher3);

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
        });
    }*/

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            myUid = user.getUid();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }*/
        isActivityRunning = false;
    }
}