package com.skiplab.theselproject.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.PostDetailActivity;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Search.ConsultantsActivity;
import com.skiplab.theselproject.Utils.Heart;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.Comment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder>{

    Context context;
    List<Comment> commentList;
    String myUid, postId;

    ProgressDialog pd;
    ClipboardManager clipboardManager;

    private DatabaseReference likesRef, postsRef;
    private FirebaseAuth mAuth;

    public AdapterComments(Context context, List<Comment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
        pd = new ProgressDialog(context);
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        likesRef = FirebaseDatabase.getInstance().getReference().child("likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comments, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = commentList.get(position).getUid();
        String name = commentList.get(position).getuName();
        String image = commentList.get(position).getuDp();
        String cid = commentList.get(position).getCid();
        String comment = commentList.get(position).getComment();
        String timestamp = commentList.get(position).getTimestamp();
        String cLikes = commentList.get(position).getcLikes(); //Contains total number of likes for a comment


        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy  hh:mm aa", calendar).toString();

        //set data
        holder.nameTv.setText(name);
        holder.commentTv.setText(comment);
        holder.timeTv.setText(dateTime);
        holder.cLikesTv.setText(cLikes); //e.g 100 likes

        try
        {
            UniversalImageLoader.setImage(image, holder.avatarIv, null, "");
        }
        catch (Exception e) {}

        //set likes for each post
        setLikes(holder, cid);

        if (myUid.equals(uid)){
            holder.trashIv.setVisibility(View.VISIBLE);

            holder.trashIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //my comment
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setMessage("Delete comment");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(cid);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (myUid.equals(uid)){
                    ClipData clipData = ClipData.newPlainText("text",commentList.get(position).getComment());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        holder.replyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PostDetailActivity)context).replyComment(uid, name, mAuth.getUid());
            }
        });
    }

    private void setLikes(MyHolder holder, String commentKey)
    {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(commentKey).hasChild(myUid)){
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

    private void deleteComment(String cid)
    {
        pd.setMessage("Deleting comment...");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        ref.child("comments").child(cid).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        //now update comments count
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                String comments = ""+dataSnapshot.child("pComments").getValue();
                                int newCommentVal = Integer.parseInt(comments) - 1;
                                ref.child("pComments").setValue(""+newCommentVal);
                                ((PostDetailActivity)context).reloadComments();

                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //..
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "CommentViewHolder";

        //views
        private CardView cardView;
        private ImageView avatarIv, mHeartWhite, mHeartRed, trashIv;
        private TextView nameTv, timeTv, commentTv, cLikesTv,replyTv;
        private GestureDetector mGestureDetector;

        private Heart mHeart;

        boolean mProcessLike=false;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            cardView = itemView.findViewById(R.id.cardView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            cLikesTv = itemView.findViewById(R.id.cLikesTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            replyTv = itemView.findViewById(R.id.replyTv);
            trashIv = itemView.findViewById(R.id.icon_trash);
            mHeartRed = (ImageView) itemView.findViewById(R.id.image_heart_red);
            mHeartWhite = (ImageView) itemView.findViewById(R.id.image_heart);
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

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: double tap detected" );

                //get total number of likes of this post, who liked it
                //if currently signed user has not liked it before
                final int cLikes = Integer.parseInt(commentList.get(getAdapterPosition()).getcLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String commentId = commentList.get(getAdapterPosition()).getCid();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(commentId).hasChild(myUid)){
                                //Already liked. So remove like
                                postsRef.child(postId).child("comments").child(commentId).child("cLikes").setValue( ""+(cLikes-1));
                                likesRef.child(commentId).child(myUid).removeValue();
                                mProcessLike=false;
                            }
                            else {
                                //not liked, like it
                                postsRef.child(postId).child("comments").child(commentId).child("cLikes").setValue( ""+(cLikes+1));
                                likesRef.child(commentId).child(myUid).setValue("Liked");
                                mProcessLike=false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mHeart.toggleLike();

                return true;
            }
        }
    }
}
