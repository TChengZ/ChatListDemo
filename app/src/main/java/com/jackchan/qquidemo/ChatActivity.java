package com.jackchan.qquidemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchan.qquidemo.ui.HeaderScrollView;
import com.jackchan.qquidemo.ui.ScrollItemListView;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends ActionBarActivity {
    private final static String TAG = "ChatActivity";
    private List<Item> mList = null;
    private ScrollItemListView mListView = null;
    private MyAdapter mMyAdapter = null;
    private HeaderScrollView mHeaderScrollView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mList = new ArrayList<Item>();
        for(int i = 0; i < 50; i++){
            mList.add(new Item(i+""));
        }
        mHeaderScrollView = (HeaderScrollView) findViewById(R.id.scrollView);
        mMyAdapter = new MyAdapter();
        mHeaderScrollView.setAdapter(mMyAdapter);
        View header = LayoutInflater.from(this).inflate(R.layout.header, null);
        mHeaderScrollView.setHeaderView(header);
    }

    private class Item{
        public Item(String name){
            this.name = name;
        }

        String name;
    }

    private class ViewHolder{
        TextView tvName;
        Button btnDelete;
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(null == convertView){
                convertView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
                viewHolder.tvName.setTag(ScrollItemListView.NOR_DELETE_TAG);
                viewHolder.btnDelete.setTag(ScrollItemListView.DELETE_TAG);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.tvName.setText(mList.get(i).name);
            final View itemView = convertView;
            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChatActivity.this, mList.get(i).name + " delete", Toast.LENGTH_SHORT).show();
                    mList.remove(i);
                    modifyMargin(i, itemView ,viewGroup);
                    mMyAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    private void modifyMargin(int i, View view,ViewGroup viewGroup){
        View itemView = mMyAdapter.getView(i, view, viewGroup);
        if(null != itemView){
            View leftView = itemView.findViewWithTag(ScrollItemListView.NOR_DELETE_TAG);
            if(null != leftView) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) leftView.getLayoutParams();
                Log.d(TAG, "leftMargin:" + params.leftMargin);
                params.leftMargin = 0;
                leftView.setLayoutParams(params);
            }
        }
    }
}
