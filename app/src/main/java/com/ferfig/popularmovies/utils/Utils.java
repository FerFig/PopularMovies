package com.ferfig.popularmovies.utils;

import android.content.Context;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public final class Utils {

    public static int getDeviceSpanByOrientation(Context context){
        if ((context.getSystemService(Context.WINDOW_SERVICE)) != null) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int orientation = display.getRotation();
            if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
                return 6;
            }
        }
        return 3;
    }

}
