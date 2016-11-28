package com.bridge.gcmapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by sec on 2016-10-25.
 */
public class MyAsyncTask extends AsyncTask<String,Void,String> {
    SecondActivity context;
    ProgressDialog dialog;

    LoadManager load;//접속과 요청을 담당하는 객체 선언

    public MyAsyncTask(Context context) {
        this.context = (SecondActivity) context;

        load = new LoadManager();
    }

    public MyAsyncTask() {

    }

    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        //dialog.setCancelable(false);
        dialog.show();
    }

    protected String doInBackground(String... params) {

        //웹서버에 요청시도
        String data = load.request();


        return data;
    }



    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        dialog.dismiss();
        //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        //최종적으로 웹서버로부터 데이터를 완전히 가져온 시점은 이 메서드이므로,
        //어댑터가 보유한 ArrayList를 갱신시켜주자!
        ArrayList<DataVo> dataList = context.adapter.lst;
        dataList.removeAll(dataList);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'");
        try {
          //  JSONObject o = new JSONObject(result);
            //JSONArray array = new JSONArray(result);
/*            JSONArray array = o.getJSONArray("a");*/

            JSONArray array = new JSONArray(result);

            DataVo dataVo = null;
            JSONObject obj = null;
            for(int i=0;i<array.length();i++){
                obj = array.getJSONObject(i);
                dataVo = new DataVo();

                System.out.println("2obj.getString(post_title):" + obj.getString("post_title"));
                dataVo.setTitle(obj.getString("post_title"));
                dataVo.setContents(obj.getString("post_content"));
                dataVo.setDate(obj.getString("created_at"));
                System.out.println("2obj.getString(post_title):" + obj.getString("post_title"));
                System.out.println("2obj.getString(post_content):" + obj.getString("post_content"));
                System.out.println("2obj.getString(created_at):" + obj.getString("created_at"));

                dataList.add(dataVo);

                context.listView.invalidateViews();
            }
            context.listView.invalidate();

            System.out.println("array.length():"+array.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
