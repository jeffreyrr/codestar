package com.rogiers.jeffrey.codestar.util;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by jeffrey on 11/11/17.
 */

public class BusUtil {

    private static final Handler mainThread = new Handler(Looper.getMainLooper());

    public static void postOnMain(final Bus bus, final Object event) {

        mainThread.post(new Runnable() {
            @Override
            public void run() {
                bus.post(event);
            }
        });
    }
}
