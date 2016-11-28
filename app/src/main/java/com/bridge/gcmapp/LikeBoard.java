package com.bridge.gcmapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by sec on 2016-11-01.
 */
public class LikeBoard extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likeboard);

        Intent intent = getIntent();
    }
}
