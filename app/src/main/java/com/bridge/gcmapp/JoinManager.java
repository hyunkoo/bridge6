package com.bridge.gcmapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sec on 2016-11-23.
 */
public class JoinManager extends Activity {

    TextView txt_join;
    EditText user_id, password;
    CheckBox autoLogin;
    Button submit;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joins);

        submit = (Button) findViewById(R.id.submit);
        user_id = (EditText)findViewById(R.id.user_id);
        password=(EditText)findViewById(R.id.password);
        autoLogin = (CheckBox)findViewById(R.id.autoLogin);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();


        if(pref.getBoolean("Auto_Login_enabled", false)){
            user_id.setText(pref.getString("user_id",""));
            password.setText(pref.getString("password",""));
            autoLogin.setChecked(true);
        }

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String id = user_id.getText().toString();
                    String pw = password.getText().toString();

                    editor.putString("user_id",id);
                    editor.putString("password",pw);
                    editor.putBoolean("Auto_Login_enabled",true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Thread thread = new Thread() {
                    @Override
                    public void run() {
//                      HttpClient HttpClient = new DefaultHttpClient();
                        String id = user_id.getText().toString();
                        String pw = password.getText().toString();

                        String urlString = "http://52.78.7.192:3001/api/users/login";


                        try {
                            URL url = new URL(urlString);

                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
                            nameValuePairs.add(new BasicNameValuePair("user_id", id));
                            nameValuePairs.add(new BasicNameValuePair("password", pw));

                            OutputStream outputStream = connection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            bufferedWriter.write(getURLQuery(nameValuePairs));
                            bufferedWriter.flush();
                            bufferedWriter.close();
                            outputStream.close();

                            connection.connect();


                            StringBuilder responseStringBuilder = new StringBuilder();
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                for (; ; ) {
                                    String stringLine = bufferedReader.readLine();
                                    if (stringLine == null) break;
                                    responseStringBuilder.append(stringLine + '\n');
                                }
                                bufferedReader.close();
                            }

                            connection.disconnect();

                            Log.d("Message", responseStringBuilder.toString());


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        });
    }

    private String getURLQuery(List<BasicNameValuePair> params) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (BasicNameValuePair pair : params) {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();

    }
}
