package com.bridge.gcmapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sec on 2016-11-01.
 */
public class KeywordActivity extends Activity {

    TextView txt_keyword;
    Button btnAddKeyword;
    EditText AddKeyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedkeyword);

        //Intent intent = getIntent();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnAddKeyword = (Button)findViewById(R.id.btn_addkeyword);
        AddKeyword = (EditText)findViewById(R.id.et_keyword);
        txt_keyword = (TextView)findViewById(R.id.txt_keyword);

        keywordList ();

        btnAddKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_keyword.setMovementMethod(new ScrollingMovementMethod());
               String url = "http://52.78.7.192:3001/api/keywords";
               // String url = "http://52.78.7.192:3001/api/users/google";
                //String accessToken = "ya29.CjCVA9MoQ-c0rf7VVHyi2fsaweisbvGY0m1s13FOrNox1PwzPVnf-gsTpfOppiS4y_U";

                String addkey = AddKeyword.getText().toString();

                HttpClient http = new DefaultHttpClient();
                try {

                    ArrayList<NameValuePair> nameValuePairs =
                            new ArrayList<NameValuePair>();
                  nameValuePairs.add(new BasicNameValuePair("keyword", addkey));
                  //  nameValuePairs.add(new BasicNameValuePair("accessToken", accessToken));



                    Log.d("mesage", String.valueOf(nameValuePairs));

                    HttpParams params = http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);

                    HttpPost httpPost = new HttpPost(url);
                    UrlEncodedFormEntity entityRequest =
                            new UrlEncodedFormEntity(nameValuePairs, "utf-8");

                    httpPost.setEntity(entityRequest);

                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntity = responsePost.getEntity();

                    Log.d("message2", String.valueOf(resEntity));



                    //txt_keyword.setText(EntityUtils.toString(resEntity));

                    // 모든 키워드 텍스트에 뿌리기
                    Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
            txt_keyword.setText("");
            keywordList();
        }catch(Exception e){e.printStackTrace();}
    }
        });


    }
    // 모든 키워드 텍스트에 뿌리기
    protected void keywordList () {
        txt_keyword = (TextView)findViewById(R.id.txt_keyword);
        txt_keyword.setMovementMethod(new ScrollingMovementMethod());
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream buf  = null;

        try {
            //[URL 지정과 접속]

            //웹서버 URL 지정
            url= new URL("http://52.78.7.192:3001/api/users/keywords");

            //URL 접속
            urlConnection = (HttpURLConnection) url.openConnection();

            //[웹문서 소스를 버퍼에 저장]
            //데이터를 버퍼에 기록

            //buf = new BufferedInputStream(urlConnection.getInputStream());
            //BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf,"UTF-8"));

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
            Log.d("line:",bufreader.toString());

            String line = null;
            String page = "";

            //버퍼의 웹문서 소스를 줄단위로 읽어(line), Page에 저장함
            while((line = bufreader.readLine())!=null){
                Log.d("line:",line);
                page+=line;
            }

            //읽어들인 JSON포맷의 데이터를 JSON객체로 변환
            JSONArray array = new JSONArray(page);

            for (int i=0; i<array.length(); i++){
                JSONObject job = array.getJSONObject(i);


                //ksNo,korName의 값을 추출함
                String keyword = job.getString("keyword_id");
                System.out.println("ksNo:" + keyword);

                //ksNo,korName의 값을 출력함
                txt_keyword.append(" "+keyword+"  \n");
                txt_keyword.append("\n");

            }

        } catch (Exception e) {
            txt_keyword.setText(e.getMessage());
        }finally{
            //URL 연결 해제
            urlConnection.disconnect();
        }


    }
}
