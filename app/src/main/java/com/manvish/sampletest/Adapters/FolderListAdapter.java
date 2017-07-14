package com.manvish.sampletest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.manvish.sampletest.R;

import java.io.File;
import java.util.ArrayList;

public class FolderListAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<File> fileArrayList;
    public FolderListAdapter(Context c, ArrayList<File> arrayList) {
        mContext = c;
        fileArrayList = arrayList;
    }

    @Override
    public int getCount() {
        return fileArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        TextView TextView1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final Context context = parent.getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dropdown, null);
            viewHolder = new ViewHolder();
            viewHolder.TextView1 = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String str = fileArrayList.get(position).getName();

        viewHolder.TextView1.setText(str);


        return convertView;
    }
}
