package com.bridge.gcmapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.bridge.gcmapp.R.id.txt_contents;
import static com.bridge.gcmapp.R.id.txt_date;
import static com.bridge.gcmapp.R.id.txt_title;

/**
 * Created by sec on 2016-10-25.
 */
public class MyListAapter extends BaseAdapter {

    ArrayList<DataVo> lst = new ArrayList<DataVo>();
    //ArrayList<DataVo> lst;
    Context context;
    LayoutInflater infalter;

    public MyListAapter(Context context) {
        this.context = context;
        infalter = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    public MyListAapter() {

    }

    public int getCount() {
        return lst.size();
    }

    public Object getItem(int position) {
        //return null;
        return lst.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; //뷰 홀더 객체 선언

        if (convertView == null) {  //처음 보여지는(만들어지는) 리스트 아이템이라면
            convertView = infalter.inflate(R.layout.row, parent, false);
            infalter = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);  //레이아웃 뷰 연결

            viewHolder = new ViewHolder();  //뷰 홀더 객체 선언
            viewHolder.txt_title = (TextView) convertView.findViewById(txt_title);
            viewHolder.txt_contents = (TextView) convertView.findViewById(txt_contents);  //제목 및 내용 레이아웃 연결
            viewHolder.txt_date = (TextView)convertView.findViewById(txt_date);

            convertView.setTag(viewHolder);  //view에 현재 연결된 내용 저장
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();  //이미 만들어진 리스트 아이템이라면 캐시되어 있는 뷰를 그대로 가져온다
        }

        //Log.d("!!!!!!!", String.valueOf(txt_title));

        DataVo dataVo = lst.get(position);
        viewHolder.txt_title.setText(dataVo.getTitle());  //각 제목 내용에 출력
        viewHolder.txt_contents.setText(dataVo.getContents());
        viewHolder.txt_date.setText(dataVo.getDate());

        return convertView;
    }
    /**
     * 뷰 홀더
     */
    public class ViewHolder
    {
        public TextView txt_title;
        public TextView txt_contents;
        public TextView txt_date;
    }
}


   /*
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        CustomViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.row, parent, false);
            holder = new CustomViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.content = (TextView) convertView.findViewById(R.id.txt_contents);
            convertView.setTag(holder);
        }else {
            holder = (CustomViewHolder) convertView.getTag();
        }
        DataVo dataVo =lst.get(position);
        holder.title.setText(dataVo.getTitle());
        holder.content.setText(dataVo.getContents());
        RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder() {
        vh.
        }
        return  convertView;
        }


    class CustomViewHolder{
         TextView title;
        TextView content;
    }
}*/
