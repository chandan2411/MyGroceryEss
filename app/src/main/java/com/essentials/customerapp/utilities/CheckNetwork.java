package com.essentials.customerapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class CheckNetwork {


    private CheckNetwork() {
        /*
        private constructor
         */
    }

    /*
     * Checking the network is available or not
     */
    public static boolean checkNet(Context mContext) {
        ConnectivityManager connMgr;
        if (mContext == null)
            return false;
        connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        /**
         * ConnectivityManager is null problem should not raise so returning false (in flight mode it will happens)
         */
        if (connMgr == null)
            return false;
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        /**
         * NetworkInfo is null problem should not raise so returning false (in worst case situations)
         */
        if (activeNetworkInfo == null) {
            return false;
        }
        try {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } catch (Exception e) {
            Log.e(CheckNetwork.class.getSimpleName(), "Exception in checkNetwork method", e);
        }
        return false;
    }
}
