package com.bridge.gcmapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.Map;

// MainActivity2 자체 DB 로그인 엑테비티
public class MainActivity2 extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

//----------로그인 관련------------
    EditText user_id, password;
    CheckBox autoLogin;
    Button submit;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    boolean m_session = false;
    String m_cookies = "";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private Button mRegistrationButton;
    private ProgressBar mRegistrationProgressBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
   /* public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mInformationTextView.setVisibility(View.GONE);
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mInformationTextView.setVisibility(View.VISIBLE);
                    mInformationTextView.setText(getString(R.string.registering_message_generating));
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    mRegistrationButton.setText(getString(R.string.registering_message_complete));
                    mRegistrationButton.setEnabled(false);
                    String token = intent.getStringExtra("token");
                    mInformationTextView.setText(token);
                }

            }
        };
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //------------로그인 관련----------------//

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

                            saveCookie(connection);

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

                          /*  String cookieString  = CookieManager.getInstance().getCookie(urlString);
                            if(cookieString == null){
                                connection.setRequestProperty("Cookie", cookieString);
                            }
                            System.out.println("cookieString" + cookieString);*/


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

 // 시작하기 버튼
        TextView btnNewActivity = (TextView) findViewById(R.id.btnNewActivity);
        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }
        });





       /* registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        // 토큰을 가져오는 Button을 정의
        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            *//**
             * 버튼을 클릭하면 토큰을 가져오는 getInstanceIdToken() 메소드를 실행한다.
             *
             * @param view
             *//*
            @Override
            public void onClick(View view) {
                getInstanceIdToken();
            }
        });*/






    }//onCreate End

    public void saveCookie( HttpURLConnection conn)
    {

        Map<String, List<String>> imap = conn.getHeaderFields( ) ;
        if( imap.containsKey( "Set-Cookie" ) )
        {
            List<String> lString = imap.get( "Set-Cookie" ) ;
            for( int i = 0 ; i < lString.size() ; i++ ) {
                m_cookies += lString.get( i ) ;
            }
            Log.e("zdg",m_cookies);
            m_session = true ;
            Log.e("zdggg", String.valueOf(m_session));
        } else {
            m_session = false ;
        }
    }

    // 자동로그인 관련
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
                Log.d("!!!@@!!!!!!@@@", String.valueOf(stringBuilder));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();

    }



    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


