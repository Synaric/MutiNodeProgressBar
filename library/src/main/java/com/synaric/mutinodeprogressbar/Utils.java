package com.synaric.mutinodeprogressbar;

import android.view.View;
import android.view.ViewGroup;

/**
 *
 * Created by Synaric-雍高超 on 2016/5/26 0026.
 */
public class Utils {

    public static void setChildEnabled(View view, boolean enable){
        view.setEnabled(enable);
        if(view instanceof ViewGroup){
            ViewGroup vg = ((ViewGroup) view);
            for(int i = 0; i < vg.getChildCount(); ++i){
                setChildEnabled(vg.getChildAt(i), enable);
            }
        }
    }
}
