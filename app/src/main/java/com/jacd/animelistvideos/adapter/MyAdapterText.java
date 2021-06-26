package com.jacd.animelistvideos.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacd.animelistvideos.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapterText extends ArrayAdapter<String> {
    private static final String TAG = "MyAdapterText";
    private Context mContext;
    private static List<String> listState;

    public MyAdapterText(Context context, ArrayList<String> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.listState = objects;
    }

    public MyAdapterText(Context context, List<String> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.listState = objects;
    }

    public MyAdapterText(Context context, String[] objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.listState = Arrays.asList(objects);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.item_text_spinner, null);
            holder = new ViewHolder();
            holder.llColor = (LinearLayout) convertView.findViewById(R.id.ll_spn_colors);
            holder.mTextView = (TextView) convertView.findViewById(R.id.tv_spn_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            String text = listState.get(position);
            holder.llColor = (LinearLayout) convertView.findViewById(R.id.ll_spn_colors);
            holder.mTextView.setText(text);
        }catch (Exception e){ e.printStackTrace(); }

        try {
            if (position < listState.size()){
                holder.mTextView.setText(listState.get(position));
                holder.llColor.setBackgroundColor(mContext.getResources().getColor((position%2 == 0)
                        ? R.color.color_spn_impar : R.color.color_spn_par));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        private LinearLayout llColor;
        private TextView mTextView;
    }

}
