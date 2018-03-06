package com.ferfig.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public final class Utils {
    public static final String APP_TAG = "PopMoviesByFF";
    public static final String MOVIE_DETAILS_OBJECT = "MovieDetails";

    public static int getDeviceSpanByOrientation(Context context){
        if ((context.getSystemService(Context.WINDOW_SERVICE)) != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                if (display !=null) {
                    int orientation = display.getRotation();
                    if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
                        return 6;
                    }
                }
            }
        }
        return 3;
    }

    public static boolean isInternetConectionAvailable(Context context) {
        if (context.getSystemService(Context.CONNECTIVITY_SERVICE)!=null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork;
                activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }
        return true;
    }
}
