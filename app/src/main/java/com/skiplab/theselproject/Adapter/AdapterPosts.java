package com.skiplab.theselproject.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.Home.HomeFragment;
import com.skiplab.theselproject.PostDetailActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.Heart;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Post;
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

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.PostViewHolder>{

    private DatabaseReference likesRef, postsRef, usersRef;

    boolean mProcessLike=false;

    String myUid;
    APIService apiService;

    Context context;
    List<Post> postList;

    public AdapterPosts(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
    }



    /*public void addAll(List<Post> newPosts)
    {
        int initSize = postList.size();
        postList.addAll(newPosts);
        notifyItemRangeChanged(initSize,newPosts.size());
    }

    public String getLastItemId()
    {
        return postList.get(postList.size()-1).getpId();
    }*/

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_posts, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {

        final String uid = postList.get(position).getUid();
        final String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        String uMood = postList.get(position).getuMood();
        String pDescription = postList.get(position).getpDescription();
        final String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes(); //Contains total number of likes for a post
        String pComments = postList.get(position).getpComments(); //Contains total number of comments for a post

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy   hh:mm aa", calendar).toString();

        //Set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.uMoodTv.setText(uMood);
        holder.pDescriptionTv.setText(pDescription);
        holder.pLikesTv.setText(pLikes); //e.g 100 likes
        holder.pCommentsTv.setText(pComments);
        holder.pCategoryTv.setText(postList.get(position).getpCategory());

        postsRef.orderByKey().equalTo(pId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (!ds.hasChild("uName")){
                        holder.uNameTv.setText("");
                    }
                    else
                    {
                        if (uName.equals("Thesel Team")){
                            holder.uNameTv.setTypeface(holder.uNameTv.getTypeface(), Typeface.BOLD);
                            holder.uNameTv.setTextColor(Color.parseColor("#28A0CE"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set likes for each post
        setLikes(holder, pId);

        //set user dp
        try{
            Glide
                    .with(context)
                    .load(uDp)
                    .placeholder(R.drawable.default_image)
                    .into(holder.uPictureIv);

            //UniversalImageLoader.setImage(uDp, holder.uPictureIv, null, "");
        }
        catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (pImage.equals("noImage")){
            //hide imageView
            holder.pImageIv.setVisibility(View.GONE);
        }
        else {
            //show Image
            holder.pImageIv.setVisibility(View.VISIBLE);

            try{
                Glide
                        .with(context)
                        .load(pImage)
                        .placeholder(R.drawable.default_image)
                        .into(holder.pImageIv);

                //UniversalImageLoader.setImage(pImage, holder.pImageIv, null, "");
            }
            catch (Exception e){
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        holder.mHeartWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String postIde = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postIde).hasChild(myUid)){
                                //Already liked. So remove like
                                postsRef.child(postIde).child("pLikes").setValue( ""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike=false;
                            }
                            else {
                                //not liked, like it
                                postsRef.child(postIde).child("pLikes").setValue( ""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike=false;

                                addToHisNotifications(""+uid,""+pId," liked your post");

                                usersRef.orderByKey().equalTo(myUid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    User user = ds.getValue(User.class);

                                                    sendNotification(uid, user.getUsername());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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


        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        });
    }

    private void sendNotification(String hisUid, String myName)
    {
        CollectionReference allTokens = FirebaseFirestore.getInstance().collection("tokens");
        allTokens.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot ds1 : queryDocumentSnapshots.getDocuments()){
                    Token token = new Token(ds1.getString("token"));
                    Data data = new Data(myUid, "A user liked your post", "Thesel", hisUid, R.mipmap.ic_launcher3);

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

    private void setLikes(final PostViewHolder holder, final String postKey)
    {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    //user has like this post
                    /*To indicate this post is liked by this (signed in)  user
                     * Chang drawable left icon of like button
                     * Change text of like button from "Like" to "Liked"*/
                    holder.mHeartWhite.setImageResource(R.drawable.ic_liked);
                }
                else {
                    holder.mHeartWhite.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
    }


    private void showMoreOptions(ImageView moreBtn, String uid, String myUid, final String pId, final String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if (uid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }
        else if (myUid.equals("1zNcpaSxviY7GLLRGVQt8ywPla52")){
            popupMenu.getMenu().add(Menu.NONE,0,0, "Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE, 2, 0,"View Comments");

        if (!uid.equals(myUid)){
            //add items in menu
            //popupMenu.getMenu().add(Menu.NONE,3,0, "Report");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    //delete is clicked
                    beginDelete(pId, pImage);
                }
                else if (id==1)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    final EditText editPost = new EditText(context);

                    Query query = postsRef.orderByChild("pId").equalTo(pId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                String pDescription = ""+ds.child("pDescription").getValue();
                                editPost.setText(pDescription);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });

                    builder.setView(editPost);

                    builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postsRef.child( pId )
                                    .child( "pDescription" )
                                    .setValue( editPost.getText().toString() );
                        }
                    });

                    builder.show();
                }
                else if (id==2)
                {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage")){
            //post without image
            deleteWithoutImage(pId);
        }
        else {
            //post with image
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        /*Steps:
          1. Delete images using url
          2. Delete from database using post id*/

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database;

                        Query query = FirebaseDatabase.getInstance().getReference("posts").orderByChild("pId").equalTo(pId);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); //

                                    DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("users");
                                    Query query = userDbRef.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                User user = ds.getValue(User.class);
                                                Long postCount = user.getPosts();

                                                userDbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("posts").setValue(postCount-1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //..
                                        }
                                    });
                                }
                                //deleted
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteWithoutImage(String pId) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");

        Query query = FirebaseDatabase.getInstance().getReference("posts").orderByChild("pId").equalTo(pId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); //

                    DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("users");
                    Query query = userDbRef.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                User user = ds.getValue(User.class);
                                Long postCount = user.getPosts();

                                userDbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("posts").setValue(postCount-1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //..
                        }
                    });
                }
                //deleted
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //..
            }
        });
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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "PostViewHolder";

        private CardView cardView;
        private ImageView uPictureIv, pImageIv;
        private TextView uNameTv, pTimeTv, pDescriptionTv, pLikesTv, pCommentsTv, uMoodTv, pCategoryTv;
        private ImageView moreBtn, mHeartWhite, mHeartRed, commentBtn;
        private GestureDetector mGestureDetector;

        private Heart mHeart;

        boolean mProcessLike=false;

        LinearLayout profileLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            uMoodTv = itemView.findViewById(R.id.uMoodTv);
            pTimeTv = itemView.findViewById(R.id.timestampTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTV);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTV);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            mHeartRed = (ImageView) itemView.findViewById(R.id.image_heart_red);
            mHeartWhite = (ImageView) itemView.findViewById(R.id.image_heart);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            pCategoryTv = itemView.findViewById(R.id.pCategoryTv);
            profileLayout = itemView.findViewById(R.id.profileLayout);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeart = new Heart(mHeartWhite, mHeartRed);
            mGestureDetector = new GestureDetector(context, new GestureListener());

            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red detected" );
                    return mGestureDetector.onTouchEvent(event);
                }
            });
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white detected" );
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }

        public class GestureListener extends GestureDetector.SimpleOnGestureListener {

            //PostViewHolder holder = new PostViewHolder();
            //String postKey;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: double tap detected" );

                //get total number of likes of this post, who liked it
                //if currently signed user has not liked it before
                final int pLikes = Integer.parseInt(postList.get(getAdapterPosition()).getpLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String postIde = postList.get(getAdapterPosition()).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postIde).hasChild(myUid)){
                                //Already liked. So remove like
                                postsRef.child(postIde).child("pLikes").setValue( ""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike=false;
                            }
                            else {
                                String timestamp = ""+System.currentTimeMillis();
                                //not liked, like it
                                postsRef.child(postIde).child("pLikes").setValue( ""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                ((DashboardActivity)context).sendNotification(postIde,
                                        postList.get(getAdapterPosition()).getUid());
                                mProcessLike=false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //..
                    }

                });
                mHeart.toggleLike();

                return true;
            }
        }
    }

}