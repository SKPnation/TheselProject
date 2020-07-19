package com.skiplab.theselproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skiplab.theselproject.R;
import com.skiplab.theselproject.models.LatenessReports;
import com.skiplab.theselproject.models.User;

import java.util.List;

public class AdapterLateness extends RecyclerView.Adapter<AdapterLateness.ViewHolder>{

    Context context;
    List<LatenessReports> reportsList;

    public AdapterLateness(Context context, List<LatenessReports> reportsList) {
        this.reportsList = reportsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lateness_reports, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String counsellor_id = reportsList.get(position).getCounsellor_id();
        final String counsellor_name = reportsList.get(position).getCounsellor_name();
        final String client_id = reportsList.get(position).getClient_id();
        final String client_name = reportsList.get(position).getClient_name();
        final String client_phone = reportsList.get(position).getClient_phone();
        final String client_email = reportsList.get(position).getClient_email();
        final String cost = reportsList.get(position).getCost();
        final String report_message = reportsList.get(position).getReport_message();
        String timestamp = reportsList.get(position).getTimestamp();

        holder.tvUsername.setText(client_name);
        holder.tvStaffName.setText(counsellor_name);
        holder.tvReport.setText(report_message);
        holder.tvTime.setText(timestamp);

        holder.itemView.setOnClickListener(v -> {

            final String tenThousand = "N10,000";
            final String twentyThousand = "N20,000";
            final String thirtyThousand = "N30,000";
            final String fortyThousand = "N40,000";
            final String fiftyThousand = "N50,000";
            final String sixtyThousand = "N60,000";
            final String seventyThousand = "N70,000";
            final String eightyThousand = "N80,000";
            final String ninetyThousand = "N90,000";
            final String hundredThousand = "N100,000";

            Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("uid").equalTo(counsellor_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        String counsellor_phone = user.getPhone();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Report details");
                        builder.setMessage("Delete report after a successful response.");

                        final View mView =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.layout_report_details, null);

                        TextView costTv = mView.findViewById(R.id.costTv);
                        TextView staff_phone = mView.findViewById(R.id.consultant_phone);
                        TextView staff_id = mView.findViewById(R.id.consultant_id);
                        TextView user_phone = mView.findViewById(R.id.client_phone);
                        TextView user_email = mView.findViewById(R.id.client_email);
                        TextView user_id = mView.findViewById(R.id.client_id);
                        TextView time = mView.findViewById(R.id.timestampTv);

                        if (cost.equals("1000000")){
                            costTv.setText(tenThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("2000000")){
                            costTv.setText(twentyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("3000000")){
                            costTv.setText(thirtyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("4000000")){
                            costTv.setText(fortyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("5000000")){
                            costTv.setText(fiftyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("6000000")){
                            costTv.setText(sixtyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("7000000")){
                            costTv.setText(seventyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("8000000")){
                            costTv.setText(eightyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else if (cost.equals("9000000")){
                            costTv.setText(ninetyThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        } else {
                            costTv.setText(hundredThousand);
                            staff_phone.setText(counsellor_phone);
                            staff_id.setText(counsellor_id);
                            user_phone.setText(client_phone);
                            user_id.setText(client_id);
                            user_email.setText(client_email);
                            time.setText(timestamp);
                        }

                        builder.setView(mView);
                        builder.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //..
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvUsername, tvStaffName;
        public TextView tvReport;
        public TextView tvTime;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            tvUsername = itemView.findViewById( R.id.usernameTv );
            tvReport = itemView.findViewById( R.id.tvReport );
            tvStaffName = itemView.findViewById( R.id.tvStaffName );
            tvTime = itemView.findViewById( R.id.time );

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            });*/
        }

        /*private AdapterUsers.UsersViewHolder.ClickListener mClickListener;
        //interface for click listener
        public interface ClickListener{
            void onItemClick(View view, int position);
        }
        public void setOnClickListener(AdapterUsers.UsersViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }*/
    }
}
