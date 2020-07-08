package com.skiplab.theselproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.skiplab.theselproject.Adapter.AdapterComments;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.Profile.ProfileFragment;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Comment;
import com.skiplab.theselproject.models.User;
import com.skiplab.theselproject.notifications.APIService;
import com.skiplab.theselproject.notifications.Client;
import com.skiplab.theselproject.notifications.Data;
import com.skiplab.theselproject.notifications.Response;
import com.skiplab.theselproject.notifications.Sender;
import com.skiplab.theselproject.notifications.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailActivity";

    Context mContext = PostDetailActivity.this;

    boolean mProcessLike = false;

    //To detail of user and post
    String hisUid, myUid, myEmail, myMood, myName, myDp,
            postId, pLikes, hisDp, hisName, hisMood, pImage, pCategory;

    ProgressDialog progressDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView uPictureIv, pImageIv, cAvatarIv;
    private TextView uNameTv, pTimeTv, pDescriptionTv, pLikesTv, uMoodTv, pCommentsTv, pCategoryTv;
    private ImageView moreBtn, mHeartWhite, mHeartRed, sendBtn;
    private LinearLayout profileLayout;
    private EditText commentEt;

    private RecyclerView recyclerView;
    private List<Comment> commentList;
    private AdapterComments adapterComments;

    /*ArrayList<String> arrayList = new ArrayList<>();
    ListView ch1;*/

    APIService apiService;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        uPictureIv = findViewById(R.id.uPictureIv);
        pCategoryTv = findViewById(R.id.pCategoryTv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        uMoodTv = findViewById(R.id.uMoodTv);
        pTimeTv = findViewById(R.id.timestampTv);
        pDescriptionTv = findViewById(R.id.pDescTv);
        pLikesTv = findViewById(R.id.pLikesTV);
        pCommentsTv = findViewById(R.id.pCommentsTV);
        moreBtn = findViewById(R.id.moreBtn);
        mHeartRed = (ImageView) findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) findViewById(R.id.image_heart);
        profileLayout = findViewById(R.id.profileLayout);
        mHeartRed.setVisibility(View.GONE);
        mHeartWhite.setVisibility(View.VISIBLE);
        moreBtn.setVisibility(View.GONE);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);

        recyclerView = findViewById(R.id.recyclerView);

        checkUserStatus();

        loadUserInfo();

        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(myUid)){
                    //user has like this post
                    /*To indicate this post is liked by this (signed in)  user
                     * Chang drawable left icon of like button
                     * Change text of like button from "Like" to "Liked"*/
                    mHeartWhite.setImageResource(R.drawable.ic_liked);
                }
                else {
                    mHeartWhite.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });

        mHeartWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i++;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 1){
                            postComment();

                        } else if (i == 2){
                            Toast.makeText(mContext, "Wait for 5 seconds before posting another comment", Toast.LENGTH_SHORT).show();
                        }
                        i=0;
                    }
                }, 500);

            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //..
            }
        });

        swipeRefreshLayout = findViewById( R.id.swipe_layout );
        swipeRefreshLayout.setColorSchemeResources( R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToTheInternet(getBaseContext()))
                {
                    loadPostInfo();
                    loadComments();
                }
                else
                {
                    Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } );

        //Default, when loading for first time
        swipeRefreshLayout.post( new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToTheInternet(getBaseContext()))
                {
                    loadPostInfo();
                    loadComments();
                }
                else
                {
                    Toast.makeText(mContext, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } );
    }

    private void addToHisNotifications(String hisUid, String pId, String notification){
        String timestamp = ""+System.currentTimeMillis();

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId",pId);
        hashMap.put("timestamp",timestamp);
        hashMap.put("pUid",hisUid);
        hashMap.put("notification", notification);
        hashMap.put("sUid",myUid);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notifications");
        reference.child(hisUid).child(timestamp).setValue(hashMap);
    }


    private void loadComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        //Show latest post first, for the load from last
        recyclerView.setLayoutManager(linearLayoutManager);

        commentList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Comment comment = ds.getValue(Comment.class);

                    commentList.add(comment);

                    adapterComments = new AdapterComments(mContext, commentList, myUid, postId);
                    adapterComments.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterComments);
                    swipeRefreshLayout.setRefreshing( false );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    private void likePost() {

        //get total number of likes of this post, who liked it
        //if currently signed user has not liked it before
        mProcessLike = true;
        //get id of the post clicked
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("likes");
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike){
                    if (dataSnapshot.child(postId).hasChild(myUid)){
                        //Already liked. So remove like
                        postsRef.child(postId).child("pLikes").setValue( ""+(Integer.parseInt(pLikes)-1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike=false;

                        mHeartWhite.setImageResource(R.drawable.ic_like);
                    }
                    else {
                        //not liked, like it
                        postsRef.child(postId).child("pLikes").setValue( ""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike=false;

                        addToHisNotifications(""+hisUid,""+postId," liked your post");

                        sendNotification1(hisUid, myName);

                        mHeartWhite.setImageResource(R.drawable.ic_liked);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    private void sendNotification1(String hisUid, String myName) {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(myUid, myName+" liked your post", "Thesel", hisUid, R.mipmap.ic_launcher2);

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

    private void postComment() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Adding comment...");
        progressDialog.show();

        String comment = commentEt.getText().toString().trim();
        //validate
        if (TextUtils.isEmpty(comment))
        {
            Toast.makeText(mContext, "Comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    String image = user.getProfile_photo();

                    String timestamp = String.valueOf(System.currentTimeMillis());

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("cid", timestamp);
                    hashMap.put("comment", comment);
                    hashMap.put("timestamp", timestamp);
                    hashMap.put("uEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    hashMap.put("uid", myUid);
                    hashMap.put("uDp", image);
                    hashMap.put("uName", myName);

                    ref.child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //added
                                    progressDialog.dismiss();
                                    commentEt.setText("");
                                    Toast.makeText(mContext, "Comment Added...", Toast.LENGTH_SHORT).show();

                                    updateCommentCount();

                                    addToHisNotifications(""+hisUid,""+postId," commented on your post");

                                    sendNotification2(hisUid, myName);

                                    onRestart();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //failed not added
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            private void sendNotification2(String hisUid, String myName) {
                CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
                allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                            Token token = new Token(ds1.getString("token"));
                            Data data = new Data(myUid, myName+" commented on your post", "Thesel", hisUid, R.mipmap.ic_launcher2);

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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    boolean mProcessComment = false;
    private void updateCommentCount() {
        //whener user adds comment, increase the comment count like i did for like count
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessComment)
                {
                    String comments = ""+dataSnapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    private void loadUserInfo() {
        //get current user info
        Query ref = FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    myName = ""+ds.child("username").getValue();
                    myDp = ""+ds.child("profile_photo").getValue();

                    //set Data
                    try {
                        //if image is received then set
                        UniversalImageLoader.setImage(myDp, cAvatarIv, null, "");
                    }
                    catch (Exception e)
                    {
                        //..
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            //user in signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        }
        else
        {
            //user not signed should go to the main activity
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
    }

    private void loadPostInfo() {
        //get Post using the id of the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //keep checking the posts until you get to the required post
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get Data
                    String pDescr = ""+ds.child("pDescription").getValue();
                    pLikes = ""+ds.child("pLikes").getValue().toString();
                    pCategory = ""+ds.child("pCategory").getValue().toString();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    pImage = ""+ds.child("pImage").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    hisUid = ""+ds.child("uid").getValue();
                    hisMood = ""+ds.child("uMood").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String commentCount = ""+ds.child("pComments").getValue();

                    //convert timestamp to dd/mm/yyyy hh:mm am/pm
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

                    //set Data
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText(pLikes);
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount);
                    pCategoryTv.setText(pCategory);

                    uNameTv.setText(hisName);
                    uMoodTv.setText(hisMood);

                    if (hisName.equals("Thesel Team")){
                        uNameTv.setTypeface(uNameTv.getTypeface(), Typeface.BOLD);
                        uNameTv.setTextColor(Color.parseColor("#28A0CE"));
                    }

                    //set image of the user who posted
                    //if there is no image, i.e pImage.equals("noImage) then hide ImageView
                    if (pImage.equals("noImage"))
                    {
                        //hide ImageView
                        pImageIv.setVisibility(View.GONE);
                    }
                    else
                    {
                        //show ImageView
                        pImageIv.setVisibility(View.VISIBLE);

                        try {
                            UniversalImageLoader.setImage(pImage, pImageIv,null, "");
                        }
                        catch (Exception e){
                            Log.d(TAG, "Post ImageView Error: "+e.getMessage());
                        }
                    }

                    //set usr image in comment part
                    try {
                        UniversalImageLoader.setImage(hisDp, uPictureIv, null, "");
                    }
                    catch (Exception e){
                        Log.d(TAG, "Comment User image: "+e.getMessage());
                    }

                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }
}
