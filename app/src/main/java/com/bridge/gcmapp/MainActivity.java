package com.bridge.gcmapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
// 구글로그인 엑티비티
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    //------로그인 관련
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 3001;
    private static final int RC_GET_AUTH_CODE = 3001;
    private static final String REDIRECT_URI = "";
    //private static final String REDIRECT_URI = "http://52.78.7.192:3001/api/users/google";
    public  GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private Button mRefreshButton;
    private Button mSignoutButton;
    // ------로그인 관련
    private TextView mAuthCodeTextView;
    //String email, name, id, idToken, accessToken;

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
    public void registBroadcastReceiver(){
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatusTextView = (TextView) findViewById(R.id.status);
        mRefreshButton = (Button) findViewById(R.id.button_optional_action);
        //mRefreshButton.setText(R.string.refresh_token);
        mSignoutButton = (Button)findViewById(R.id.sign_out_button);

        TextView btnNewActivity = (TextView) findViewById(R.id.btnNewActivity);
        btnNewActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }
        });


        registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        // 토큰을 가져오는 Button을 정의
        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 버튼을 클릭하면 토큰을 가져오는 getInstanceIdToken() 메소드를 실행한다.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                getInstanceIdToken();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        // start-------------------------구글 로그인 -------------------------
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        mSignoutButton.setOnClickListener(this);
        mRefreshButton.setOnClickListener(this);

        validateServerClientID();
       // AUTHCODE 받기
      String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId, false)
                .build();

        // 로그인 만 가능
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();*/

        // IDTOKEN 받기
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String user_id = "g_116455895327394624727";
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        String text = prefs.getString("user_id", user_id);


    }

    protected void onStop(){
        super.onStop();
        String text = "g_116455895327394624727";
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
         SharedPreferences.Editor Test = editor.putString("user_id", text);
        editor.commit();
        System.out.println("TEST!!!!!!!!!" + Test);
        System.out.println("TEST!!!!!!!!!222" + editor.commit());

    }

    private void getAuthCode() {
        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
        // token.  Otherwise, only get an access token if a refresh token has been previously
        // retrieved.  Getting a new access token for an existing grant does not require
        // user consent.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
    }

    private void refreshIdToken() {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // Users cached credentials are valid, GoogleSignInResult containing ID token
            // is available immediately. This likely means the current ID token is already
            // fresh and can be sent to your server.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently and get a valid
            // ID token. Cross-device single sign on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }



   /* @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        // Very important to start intent from Activity, otherwise requestCode top bit gets set
        // and messes it up
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // --- 토큰 받기
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }




   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       if (requestCode == RC_GET_AUTH_CODE) {
           if (data != null) {
               GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
               Log.d(TAG, "onActivityResult:GET_AUTH_CODE:success:" + result.getStatus().isSuccess());
               Log.d(TAG, "onActivityResult:GET_AUTH_CODE:success:" + result);
               if (result.isSuccess()) {
                   // ID토큰
                 /*  String idToken = result.getSignInAccount().getIdToken();
                   Log.d("RequsteIdToken :", idToken);
*/

                   // AUTH코드
/*                   GoogleSignInAccount acct = result.getSignInAccount();
                   Log.d("acct:", String.valueOf(acct));
                   String authCode = acct.getServerAuthCode();
                   Log.d("authCode:", authCode);*/

                   GoogleSignInAccount acct = result.getSignInAccount();
                   String personName = acct.getDisplayName();
                   String personEmail = acct.getEmail();
                   String personId = acct.getId();


                   //String accessToken = "ya29.CjCfA2C328wjhuZgiTa-8AdgcvciIf-k09krbv99hRhl3-pm5WV1FSlPJkNGSOPWqJc";
                   String accessToken = "ya29.CjChA56cgJGFgEMJpPmF5ffAugWQ2brSnKmhUs51RgGSkSOtFox-g9oMnZEzjqgmZiY";
                   DefaultHttpClient httpClient = new DefaultHttpClient();
                   //HttpPost httpPost = new HttpPost("http://52.78.7.192:3001/api/users/google");
                   String url = "http://52.78.7.192:3001/api/users/google";
                   try {

                       ArrayList<NameValuePair> nameValuePairs =
                               new ArrayList<NameValuePair>();
                       nameValuePairs.add(new BasicNameValuePair("accessToken", accessToken));


                       Log.d("mesage", String.valueOf(nameValuePairs));

                       HttpParams params = httpClient.getParams();
                       HttpConnectionParams.setConnectionTimeout(params, 5000);
                       HttpConnectionParams.setSoTimeout(params, 5000);

                       HttpPost httpPost = new HttpPost(url);
                       UrlEncodedFormEntity entityRequest =
                               new UrlEncodedFormEntity(nameValuePairs, "utf-8");

                       httpPost.setEntity(entityRequest);

                       HttpResponse responsePost = httpClient.execute(httpPost);
                       String responseString = EntityUtils.toString(responsePost.getEntity(), HTTP.UTF_8);
                       Log.d("responseString",responseString );
                       HttpEntity resEntity = responsePost.getEntity();
                       Log.d("Profile :", personName + personEmail + personId);
                       updateUI(true);

                        onStop();


                   }catch(Exception e) {
                       e.printStackTrace();
                   }


                   /*Cookie cookie = null;
                   List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                   if(cookies.isEmpty()) {
                       Log.d("TEST", "cookie is none");
                   } else {
                       for(int i = 0; i < cookies.size(); i++) {
                           cookie = cookies.get(i);
                           Log.d("TEST", cookies.size() + " \n " + cookies.get(i).toString() + "\n");
                       }
                   }*/
                     //handleSignInResult(result);

               }
           }
       }
   }


// Exchange auth code for access token








                    // TODO(developer): send token to server and validate
                  /*  HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://52.78.7.192:3001/api/users");

                    try {
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("accessToken ", authCode));
                        // httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // ---------------------------------------------------------------------------------
                        Log.d("mesage", String.valueOf(nameValuePairs));

                        HttpParams params = httpClient.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 5000);
                        HttpConnectionParams.setSoTimeout(params, 5000);

                        UrlEncodedFormEntity entityRequest =
                                new UrlEncodedFormEntity(nameValuePairs, "utf-8");
                        httpPost.setEntity(entityRequest);
                        Log.d("entityRequest", String.valueOf(entityRequest));
                        //------------------------------------------------------------------------------------
                        HttpResponse response = httpClient.execute(httpPost);
                        Log.d("response", String.valueOf(response));
                        HttpEntity resEntitiy = response.getEntity();
                        int statusCode = response.getStatusLine().getStatusCode();
                        final String responseBody = EntityUtils.toString(response.getEntity());
                        Log.d("restponseBody: ", responseBody);
                        Log.d("statusCode: ", String.valueOf(statusCode));
                        Log.d("resEntitiy: ", String.valueOf(resEntitiy));

                    } catch (ClientProtocolException e) {
                        Log.e(TAG, "Error sending auth code to backend.", e);
                    } catch (IOException e) {
                        Log.e(TAG, "Error sending auth code to backend.", e);
                    }


                   // handleSignInResult(result);

                   }
               }
       }*/



    // -------id토큰  핸들사인인리설트
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            String idToken = result.getSignInAccount().getIdToken();
            Log.d("handle idToken : ",idToken);
            updateUI(true);
        } else {
            updateUI(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                //signIn();
               // getIdToken();
                 getAuthCode();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
           /* case R.id.button_optional_action:
                refreshIdToken();
                break;*/
        }
    }
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
  //------로그인 핸들사인인리설트
   /* private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
            String personName = acct.getDisplayName();
            String personServerAuthCode = acct.getServerAuthCode();
            String personIdToken = acct.getIdToken();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            System.out.println("personName" + personName);
            System.out.println("personServerAuthCode" + personServerAuthCode);
            System.out.println("personIdToken" + personIdToken);
            System.out.println("personEmail" + personEmail);
            System.out.println("personId" + personId);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }*/

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
           // findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            mSignoutButton.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            //findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            mSignoutButton.setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.GONE);
        }
    }


    /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:sucess:"+ result.getStatus().isSuccess());

            if(result.isSuccess()){
                GoogleSignInAccount acct = result.getSignInAccount();
                String authCode = acct.getServerAuthCode();

                Log.d(TAG,"authCode :"+ authCode);

                HttpPost httpPost = new HttpPost("http://52.78.7.192:3001/api/users/google");
                HttpClient httpClient = new DefaultHttpClient();
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("authCode", authCode));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response =httpClient.execute(httpPost);
                    int statusCode = response.getStatusLine().getStatusCode();
                    final String responseBody = EntityUtils.toString(response.getEntity());
                } catch (ClientProtocolException e) {
                    Log.e(TAG, "Error sending auth code to backend.", e);
                } catch (IOException e) {
                    Log.e(TAG, "Error sending auth code to backend.", e);
                }
            }

            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
           GoogleSignInAccount acct = result.getSignInAccount();
            email = acct.getEmail() == null ? "" : acct.getEmail();
            name = acct.getDisplayName() == null ? "" : acct.getDisplayName();
            id = acct.getId() == null ? "" : acct.getId();
            idToken = acct.getIdToken() == null ? "" : acct.getIdToken();                       // Android support this via OAuth2.0 but have failed to retrieve
            accessToken = acct.getServerAuthCode() == null ? "" : acct.getServerAuthCode();     // Android support this via OAuth2.0 but have failed to retrieve
            Log.e(TAG, "[1] Name : " + name + " | [2] Email : " + email + " | [3] id = " + id + " | [4] ID Token = " + idToken + " | [5] AccessToken = " + accessToken);
        } else {
            // Signed out, show unauthenticated UI.C:\Users\sec\.android
            //updateUI(false);
            email = "";
        }
    }*/



    //  end -----------------------구글 로그인---------------------------

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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}


