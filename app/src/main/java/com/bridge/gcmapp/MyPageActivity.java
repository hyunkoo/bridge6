package com.bridge.gcmapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sec on 2016-09-02.
 */
public class MyPageActivity extends Activity{


    TextView txt_keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        Intent intent = getIntent();

        TextView AddKeyword = (TextView)findViewById(R.id.addedKeyword);

           AddKeyword.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(getApplicationContext(), KeywordActivity.class);
                   startActivity(intent);
               }
           });

        TextView LikeBoard = (TextView)findViewById(R.id.txt_like);
        LikeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LikeBoard.class);
                startActivity(intent);
            }
        });


    }
}
