package com.skiplab.theselproject.Consultation;

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
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.ChatMessage;
import com.skiplab.theselproject.models.ChatRoom;
import com.skiplab.theselproject.models.Profile;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    Context mContext = ChatActivity.this;

    private static final String TAG = "ChatActivity";

    private static final int REQUEST_PERMISSION_CODE = 1000;

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

    //views from xml
    Toolbar toolbar;
    RecyclerView recyclerView;
    CircleImageView profileIv;
    TextView nameTv, countDownTv;
    EditText messageEt;
    Button mButtonStart, mButtonThanks;
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

    private boolean mTimerRunning;
    public static boolean isActivityRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    String hisUID;
    String myUid, myName;
    String hisImage;
    String chatroomID;

    APIService apiService;
    boolean notify = false;

    //image picked will be saved in this uri
    Uri image_uri = null;

    MediaRecorder mediaRecorder;
    String pathSave = "";

    SpannableString ss_thanks;
    ForegroundColorSpan fcsBlack;
    String thanks = "THANKS";

    int i = 0;
    int num_messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        setupFirebaseAuth();

        final Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");
        chatroomID = intent.getStringExtra("chatroomID");
        myName = intent.getStringExtra("myName");

        ss_thanks = new SpannableString(thanks);
        fcsBlack = new ForegroundColorSpan(Color.BLACK);
        ss_thanks.setSpan(fcsBlack, 0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        mChatroomReference = FirebaseFirestore.getInstance().collection("chatrooms");
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
        countDownTv = findViewById(R.id.text_view_countdown);
        mButtonStart = findViewById(R.id.button_start_pause);
        mButtonThanks = findViewById(R.id.button_thanks);
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

        /*mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToTheInternet(mContext))
                {
                    if (mTimerRunning){
                        mButtonStart.setVisibility(View.GONE);
                    } else {
                        usersRef.orderByKey().equalTo(myUid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds: dataSnapshot.getChildren())
                                        {
                                            User user = ds.getValue(User.class);

                                            if (user.getIsStaff().equals("false"))
                                            {
                                                int wallet = Integer.parseInt(ds.child("wallet").getValue().toString());
                                                int result = wallet - 1500;

                                                usersRef.child(myUid).child("wallet").setValue(result);

                                                mProfileReference.document(hisUID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Profile profile = task.getResult().toObject(Profile.class);
                                                                    HashMap<String,Object> hashMap = new HashMap<>();
                                                                    hashMap.put("num_of_payments", profile.getNum_of_payments()+1);

                                                                    mProfileReference.document(hisUID).set(hashMap, SetOptions.merge());
                                                                }
                                                            }
                                                        });

                                                startTimer();

                                                i++;

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                .setMessage("You have 5 minutes left!")
                                                                .create();
                                                        alertDialog.show();
                                                    }
                                                }, 600000);

                                                Toast.makeText(mContext,"Deducted successfully!",Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                            {
                                                startTimer();

                                                i++;

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                                                                .setMessage("You have 5 minutes left!")
                                                                .create();
                                                        alertDialog.show();
                                                    }
                                                }, 600000);
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
                else
                {
                    AlertDialog alertDialog =new AlertDialog.Builder(mContext)
                            .setMessage("Please check your internet connection")
                            .create();
                    alertDialog.show();
                }

            }
        });*/

        mButtonThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.orderByKey().equalTo(myUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    User user = ds.getValue(User.class);
                                    if (user.getIsStaff().equals("false"))
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                        builder.setMessage("Are you satisfied with the consultation?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String message = "Thanks, I'm satisfied with the consultation.";

                                                sendMessage(message);
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
                                    else
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                        builder.setMessage("This button can only be used by the client!");

                                        builder.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        updateCountDownText();

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
                    requestPermission();

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
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
        });


        getRecepientDetails();
        readMessages();
    }



    private void getRecepientDetails()
    {
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                builder.setCancelable(false);
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
                                builder.show();

                            }
                            else if (user.getIsStaff().equals("false"))
                            {
                                nameTv.setText(user.getUsername());
                                hisImage = user.getProfile_photo();

                                try {
                                    UniversalImageLoader.setImage(hisImage, profileIv, null, "");
                                }
                                catch (Exception e){
                                    Log.d(TAG, "ERROR: "+e);
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                builder.setCancelable(false);
                                builder.setTitle("PLEASE READ THIS!");
                                builder.setMessage("1. Try not to give very short replies. Provide enough possible solutions to the client's problems!\n\n"
                                        +"2. You can be speaking with the same client for days until he/she is satisfied with your counselling." +
                                        " During the process, other users will not be allowed to consult you!\n\n"
                                        +"3. Delete the session when the client is satisfied with the consultation.\n\n"
                                        +"4. Your CHAT HISTORY with that client will still be available on the database for future " +
                                        "consultations.\n\n"
                                        +"5. Delete the session if you don't get a reply within 24HOURS!\n\n"
                                        +"6. EXCHANGE OF PHONE NUMBERS AND EMAIL ADDRESSES ARE NOT ALLOWED!!!");
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
                            ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("num_messages",chatRoom.getNum_messages()+1);
                            mChatroomReference.document(chatroomID).set(hashMap, SetOptions.merge());
                        }
                    }
                });

        if (notify)
        {
            sendNotification(hisUID, myName, message);
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
                            adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
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

        String timeStamp = ""+System.currentTimeMillis();

        String fileNameAndPath = "Audio/"+"audio.mp3"+timeStamp;

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child(fileNameAndPath);

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
                                                ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("num_messages",chatRoom.getNum_messages()+1);
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
                                        sendNotification(hisUID, user.getUsername(), "Sent you a recording...");
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
                                                    ChatRoom chatRoom = task.getResult().toObject(ChatRoom.class);
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("num_messages",chatRoom.getNum_messages()+1);
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
                                            sendNotification(hisUID, user.getUsername(), "Sent you a picture...");
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
                mButtonStart.setVisibility(View.GONE);

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage("TIME'S UP!!!");
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

        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 6000) / 10;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countDownTv.setText(timeLeftFormatted);

    }


    private void sendNotification(String hisUID, String myName, String message) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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
            myUid = user.getUid();

            if (user != null)
            {
                Log.d( TAG, "onAuthStateChanged: signed_in: " + user.getUid());

            } else {
                Log.d( TAG, "onAuthStateChanged: signed_out");
            }
        };


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
                .setTitle("DON'T DO THAT!")
                .setMessage("Wait till the time is up")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
