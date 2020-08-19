package com.skiplab.theselproject.Utils;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Default value
        Map<String,Object> defaultValue = new HashMap<>();
        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE, false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION, "3.0");
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id=com.skiplab.theselproject");

        remoteConfig.setDefaults(defaultValue);
        remoteConfig.fetch(5)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            remoteConfig.activateFetched();
                        }
                    }
                });


    }
}
