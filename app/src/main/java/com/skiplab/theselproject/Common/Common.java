package com.skiplab.theselproject.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {

    private static int currentTimeSlot = -1;

    public static boolean isConnectedToTheInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState().equals(NetworkInfo.State.CONNECTED))
                        return true;
                }
            }
        }
        return false;
    }

    private static String convertTimeSlotToString(int slot)
    {
        switch (slot)
        {
            case 0:
                return "5:00 - 5:40";
            case 1:
                return "6:00 - 6:40";

            case 2:
                return "7:00 - 7:40";
            case 3:
                return "8:00 - 8:40";
            case 4:
                return "9:00 - 9:40";
            case 5:
                return "10:00 - 10:40";
        }

        return convertTimeSlotToString(slot);
    }
}
