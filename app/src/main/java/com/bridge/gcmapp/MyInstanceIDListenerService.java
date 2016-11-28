package com.bridge.gcmapp;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by sec on 2016-05-23.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
   /* public MyInstanceIDListenerService(){

    }

    @Override
    public void onTokenRefresh(){

    }*/
   private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
