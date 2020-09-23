package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.AdminEdit;
import com.skiplab.theselproject.Consultation.ConsultantsActivity;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.EditConsultantProfile;
import com.skiplab.theselproject.Home.SelectCategory;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.Utils.UniversalImageLoader;
import com.skiplab.theselproject.models.User;

import java.util.List;

public class AdapterConsultant extends RecyclerView.Adapter<AdapterConsultant.ViewHolder> {

    Context context;
    List<User> consultantList;
    private FirebaseAuth mAuth;

    private DatabaseReference usersRef;

    public AdapterConsultant(Context context, List<User> consultantList) {
        this.context = context;
        this.consultantList = consultantList;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consultant_col, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String hisImage = consultantList.get(position).getProfile_photo();
        final String hisUID = consultantList.get(position).getUid();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            UniversalImageLoader.setImage(hisImage, holder.mAvatarIv, null, "");
            Log.d("Photo: ", hisImage );
        }catch (Exception e){
            //...
        }

        holder.mAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                {
                    usersRef.orderByKey().equalTo(mAuth.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = ds.getValue(User.class);
                                        if (user.getIsStaff().equals("admin"))
                                        {
                                            Intent intent = new Intent(context, AdminEdit.class);
                                            intent.putExtra("hisUID",hisUID);
                                            context.startActivity(intent);

                                            Log.d("hisEmail: ",consultantList.get(position).getEmail() );
                                        }
                                        else
                                        {
                                        /*AlertDialog alertDialog = new AlertDialog.Builder(context)
                                                .setMessage("The private consultation feature is currently undergoing an upgrade")
                                                .create();
                                        alertDialog.show();*/
                                            context.startActivity(new Intent(context, SelectCategory.class));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //..
                                }
                            });
                }
                else
                {
                    AlertDialog alertDialog =new AlertDialog.Builder(context)
                            .setTitle("Upgrade your OS")
                            .setMessage("To use this feature, your Android OS must be 8.0 and above!")
                            .create();
                    alertDialog.show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return consultantList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarIv = itemView.findViewById(R.id.cPictureIv);
        }
    }
}
