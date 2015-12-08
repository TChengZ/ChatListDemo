package com.jackchan.qquidemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jackchan.qquidemo.ui.CircleNotifyView;
import com.jackchan.qquidemo.ui.HeaderScrollView;
import com.jackchan.qquidemo.ui.ScrollItemListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatActivity extends ActionBarActivity implements CircleNotifyView.ICircleNotifyCallback{
    private final static String TAG = "ChatActivity";
    private List<Item> mList = null;
    private MyAdapter mMyAdapter = null;
    private HeaderScrollView mHeaderScrollView = null;

    private Set<Integer> mDismissTag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mDismissTag = new HashSet<Integer>();
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

    @Override
    public void onDismissByTag(String tag) {
        mList.remove(new Item(tag));
        mMyAdapter.notifyDataSetChanged();
    }

    private class Item{
        public Item(String name){
            this.name = name;
        }

        String name;

        @Override
        public boolean equals(Object o) {
            if(o instanceof Item && ((Item)o).name != null){
                return ((Item)o).name.equals(this.name);
            }
            return false;
        }
    }

    private class ViewHolder{
        RelativeLayout rlName;
        TextView tvName;
        Button btnDelete;
        CircleNotifyView viewNotify;
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
                viewHolder.rlName = (RelativeLayout) convertView.findViewById(R.id.rl_name);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
                viewHolder.viewNotify = (CircleNotifyView) convertView.findViewById(R.id.notify_view);
                viewHolder.rlName.setTag(ScrollItemListView.NOR_DELETE_TAG);
                viewHolder.btnDelete.setTag(ScrollItemListView.DELETE_TAG);
                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.tvName.setText(mList.get(i).name);
            viewHolder.viewNotify.setCircleNotifyCallback(ChatActivity.this);
            viewHolder.viewNotify.setNumber(Integer.valueOf(mList.get(i).name));
            viewHolder.viewNotify.setTag(mList.get(i).name);
            viewHolder.viewNotify.setVisibility(mDismissTag.contains(i) ? View.GONE : View.VISIBLE);
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
