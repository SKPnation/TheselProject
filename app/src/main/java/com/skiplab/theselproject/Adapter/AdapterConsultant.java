package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skiplab.theselproject.DashboardActivity;
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
                ((DashboardActivity)context).selectSessionType();
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
