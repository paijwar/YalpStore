package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_MOBILE_DUN;
import static android.net.ConnectivityManager.TYPE_MOBILE_HIPRI;
import static android.net.ConnectivityManager.TYPE_MOBILE_MMS;
import static android.net.ConnectivityManager.TYPE_MOBILE_SUPL;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.ConnectivityManager.TYPE_WIMAX;

public class NetworkState {

    static public boolean isVpn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return isVpnLollipop(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return isVpnHoneycomb();
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static private boolean isVpnLollipop(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        for (NetworkInfo networkInfo: cm.getAllNetworkInfo()) {
            if (networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_VPN) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static private boolean isVpnHoneycomb() {
        try {
            for (NetworkInterface ni: Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isUp() && (ni.getName().startsWith("tun") || ni.getName().startsWith("ppp")))  {
                    Log.i(NetworkState.class.getSimpleName(), "VPN seems to be on: " + ni.getName());
                    return true;
                }
            }
        } catch (SocketException e) {
            // Could not get network interfaces
        }
        return false;
    }

    static public boolean isMetered(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return isActiveNetworkMetered(connectivityManager);
        } else {
            return connectivityManager.isActiveNetworkMetered();
        }
    }

    static private boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            // err on side of caution
            return true;
        }
        final int type = info.getType();
        switch (type) {
            case TYPE_MOBILE:
            case TYPE_MOBILE_DUN:
            case TYPE_MOBILE_HIPRI:
            case TYPE_MOBILE_MMS:
            case TYPE_MOBILE_SUPL:
            case TYPE_WIMAX:
                return true;
            case TYPE_WIFI:
                return false;
            default:
                // err on side of caution
                return true;
        }
    }
}
