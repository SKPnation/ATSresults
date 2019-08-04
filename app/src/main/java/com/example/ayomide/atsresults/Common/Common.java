package com.example.ayomide.atsresults.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ayomide.atsresults.Model.Parent;

public class Common {
    public static Parent currentParent;

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final String PARENTS_TABLE = "Parents";

    public static final int IMAGE_REQUEST = 71;
    public static final int PDF_REQUEST = 86;

    public static final String SHARE = "Share";

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
}
