package com.bridge.gcmapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sec on 2016-05-24.
 */
public class SecondActivity extends Activity {
    private static final String TAG = "SecondActivity";

    static boolean mGoogleApiClient ;
   /* public static ArrayList<Song> al = new ArrayList<Song>();
    public static MyAdapter adapter;
    ListView lv ;*/
    MyAsyncTask task;
    ListView listView;
    MyListAapter adapter;
    public static final ArrayList<DataVo> lst = new ArrayList<DataVo>();

    //------------------------------------GCM start-----------------------------------------------

// 리스트뷰 떄문에 잠시 주석 GCM
    static public void addList(String title, String contents) {
        lst.add(new DataVo(title, contents));

    }


   @Override
    protected void onDestroy() {
        super.onDestroy();
        Grobal.setDone(false);
    }

    //------------------------------------GCM end -------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyListAapter(this);


        listView.setAdapter(adapter);

        conntectCheck();

        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }).start();

    }

    public void conntectCheck() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            task = new MyAsyncTask(this);
            task.execute();

        } else {
            // display error
            Toast.makeText(this, "네트워크 상태를 확인하십시오", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                //TextView title = (TextView) findViewById(R.id.txt_title);
                //TextView content = (TextView) findViewById(R.id.txt_contents);

                DataVo dataVo = (DataVo) adapter.getItem(position);

                intent.putExtra("post_title", dataVo.getTitle());
                intent.putExtra("post_content", dataVo.getContents());
                intent.putExtra("created_at", dataVo.getDate());
                //-----------------------------------------------------
                lst.remove(id);
                adapter.notifyDataSetChanged();
                //--------------------------------------------------------
                // extras.putString("post_content", content.getText().toString());
                System.out.println("전환 전 받은 값 :" + dataVo.getContents());
                System.out.println("전환 전 받은 값 :" + dataVo.getDate());

                startActivity(intent);
                System.out.println("Position :" + position);
                System.out.println("id:" + id);
                //  Toast.makeText(getApplication(),title.getText().toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplication(),content.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });







           //--------------------------------------------------------GCM--------------------------------------------------------------------------------

    /*    if(!Grobal.isDone()){
           // al.add(new Song("[공통]2016 하계 단기해외수업 모집공고", "2016 하계 단기해외수업 모집공고..."));
           // al.add(new Song("[공통]2016 사회봉사 교과목 운영안내", "2016 사회봉사 교과목 운영안내..."));
           // al.add(new Song("[공통]2016-1학기 중 휴학 마감안내","-1학기 중 휴학 마감안내..."));

            adapter = new MyListAapter(getApplicationContext());
            Grobal.setDone(true);
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


        new Thread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }).start();*/


/*
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                intent.putExtra("title", al.get(position).title);
                intent.putExtra("artist", al.get(position).artist);

                al.remove(id);
                adapter.notifyDataSetChanged();


                startActivity(intent);

            }
        });*/

        //마이페이지 뷰
        ImageButton myPageView = (ImageButton) findViewById(R.id.btn_mypage);
        myPageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent);

            }
        });

        Button keywordBtn = (Button)findViewById(R.id.btn_keyword);
        keywordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent = new Intent(getApplicationContext(), KeywordActivity.class);
                    startActivity(intent);

            }
        });
    }

}



/*class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<Song> al;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<Song> al) {
        this.context = context;
        this.layout = layout;
        this.al = al;
        inf = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return al.size();
    }
    @Override
    public Object getItem(int position) {
        return al.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = inf.inflate(layout, null);
        }
        TextView tvName = (TextView)convertView.findViewById(R.id.txt_title);
        TextView tvInfo = (TextView)convertView.findViewById(R.id.txt_contents);



        Song m = al.get(position);
        tvName.setText(m.title);
        tvInfo.setText(m.artist);

        return convertView;
    }
}*/

/*
class Song { // 자바빈
    String title = ""; // title
    String artist = ""; // artist
    public Song(String title,  String artist) {
        super();
        this.title = title;
        this.artist = artist;
    }
    public Song() {}
}
*/
