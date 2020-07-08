package com.skiplab.theselproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skiplab.theselproject.AddPost.CategoryActivity;
import com.skiplab.theselproject.AddPost.NewPostActivity;
import com.skiplab.theselproject.Common.Common;
import com.skiplab.theselproject.DashboardActivity;
import com.skiplab.theselproject.R;

public class AdapterMood extends RecyclerView.Adapter<AdapterMood.MyHolder> {

    Context context;
    String[] moods;
    int i = 0;

    public AdapterMood(Context context, String[] moods) {
        this.context = context;
        this.moods = moods;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_row, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.mood.setText(moods[position]);
        holder.mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToTheInternet(context))
                {
                    final String moodPosition = moods[position];
                    Intent intent = new Intent(context, NewPostActivity.class);
                    intent.putExtra("mood", moodPosition);
                    context.startActivity(intent);
                }
                else
                {
                    i++;

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please check your internet connection");
                    builder.show();

                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    }, 1000);

                    return;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moods.length;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView mood;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mood = itemView.findViewById(R.id.moodTv);

        }
    }
}
