package com.z5h.seenth;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: markb
 * Date: 12-08-18
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Debugger {
    public static void print(String message, Throwable t) {
        Log.d("z5h.seenth", message, t);
    }

    public static void print(String message) {
        Log.d("z5h.seenth", message);
    }
}
