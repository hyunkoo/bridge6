package com.bridge.gcmapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sec on 2016-11-23.
 */
public class Join extends Activity implements View.OnClickListener {
    EditText idInput, passwordInput;
    CheckBox autoLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Boolean loginChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        idInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        autoLogin = (CheckBox) findViewById(R.id.checkBox);

        // if autoLogin checked, get input
        if (pref.getBoolean("autoLogin", false)) {
            idInput.setText(pref.getString("user_id", ""));
            passwordInput.setText(pref.getString("password", ""));
            autoLogin.setChecked(true);
            // goto mainActivity

        } else {
            // if autoLogin unChecked
            String id = idInput.getText().toString();
            String password = passwordInput.getText().toString();
            Boolean validation = loginValidation(id, password);

            if (validation) {
                Toast.makeText(Join.this, "Login Success", Toast.LENGTH_LONG).show();
                // save id, password to Database

                if (loginChecked) {
                    // if autoLogin Checked, save values
                    editor.putString("user_id", id);
                    editor.putString("password", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();
                }
                // goto mainActivity

            } else {
                Toast.makeText(Join.this, "Login Failed", Toast.LENGTH_LONG).show();
                // goto LoginActivity
            }
        }
            // set checkBoxListener
            autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        loginChecked = true;
                    } else {
                        // if unChecked, removeAll
                        loginChecked = false;
                        editor.clear();
                        editor.commit();
                    }
                }
            });

    }
    // 아이디와 비밀번호가 일치하는지 확인
    private boolean loginValidation(String id, String password) {
        if(pref.getString("user_id","").equals(id) && pref.getString("password","").equals(password)) {
            // login success
            return true;
        } else if (pref.getString("user_id","").equals(null)){
            // sign in first
            Toast.makeText(Join.this, "Please Sign in first", Toast.LENGTH_LONG).show();
            return false;
        } else {
            // login failed
            return false;
        }
    }


    @Override
    public void onClick(View v) {

    }
}