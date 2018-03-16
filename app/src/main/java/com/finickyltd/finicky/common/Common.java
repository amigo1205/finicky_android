package com.finickyltd.finicky.common;


/**
 * Created by shevchenko on 2015-11-26.
 */
public class Common {
    private static Common s_instance = null;

    public static Common getInstance() {
        if (s_instance == null) {
            s_instance = new Common();

        }
        synchronized (s_instance) {
            return s_instance;
        }
    }

    public int index;
}
