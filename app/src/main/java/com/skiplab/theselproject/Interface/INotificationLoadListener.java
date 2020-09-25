package com.skiplab.theselproject.Interface;

import com.google.firebase.firestore.DocumentSnapshot;
import com.skiplab.theselproject.models.MyNotifications;

import java.util.List;

public interface INotificationLoadListener {
    void onNotificationLoadSuccess(List<MyNotifications> myNotificationsList, DocumentSnapshot lastDocument);
    void onNotificationLoadFailed(String message);
}
