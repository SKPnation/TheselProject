package com.skiplab.theselproject.Adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.skiplab.theselproject.models.MyNotifications;

import java.util.List;

public class MyDiffCallBack extends DiffUtil.Callback {
    List<MyNotifications> oldList;
    List<MyNotifications> newList;

    public MyDiffCallBack(List<MyNotifications> oldList, List<MyNotifications> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getCounsellor_id() == newList.get(newItemPosition).getCounsellor_id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
