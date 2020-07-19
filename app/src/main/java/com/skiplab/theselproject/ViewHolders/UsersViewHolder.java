package com.skiplab.theselproject.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skiplab.theselproject.Interface.ItemClickListener;
import com.skiplab.theselproject.R;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView mAvatarIv;
    public TextView mNameTv, mCategoryTv1, mCategoryTv2, mCategoryTv3, mCostTv;
    public Button mEditBtn;

    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        mAvatarIv = itemView.findViewById(R.id.avatarIv);
        mNameTv = itemView.findViewById(R.id.nameTv);
        mCategoryTv1 = itemView.findViewById(R.id.categoryTv1);
        mCategoryTv2 = itemView.findViewById(R.id.categoryTv2);
        mCategoryTv3 = itemView.findViewById(R.id.categoryTv3);
        mCostTv = itemView.findViewById(R.id.costTv);
        mEditBtn = itemView.findViewById(R.id.editBtn);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }
}
