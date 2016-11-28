package com.bridge.gcmapp;

/**
 * Created by ryan on 2016. 5. 30..
 */
public class Grobal {
    private static boolean done = false;

    public static boolean isDone() {
        return done;
    }

    public static void setDone(boolean done) {
        Grobal.done = done;
    }
}
