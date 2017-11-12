package com.rogiers.jeffrey.codestar.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by jeffrey on 11/11/17.
 */

public class BusProvider {
    private static Bus sBus;

    public static Bus getBus(){
        if(sBus == null){
            sBus = new Bus(ThreadEnforcer.ANY);
        }

        return sBus;
    }
}
