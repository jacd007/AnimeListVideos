package com.jacd.animelistvideos.adapter;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.models.SpnModel;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<SpnModel> {
    private static final String TAG = "MyAdapter";
    private View mvColor;
    private Context mContext;
    private static ArrayList<SpnModel> listState;
    private MyAdapter myAdapter;
    private boolean isFromView = false;
    private int intColor;



    public MyAdapter(Context context, int resource, List<SpnModel> objects, View vColors) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpnModel>) objects;
        this.myAdapter = this;
        this.mvColor = vColors;
    }

    public MyAdapter(Context context, int resource, List<SpnModel> objects, View vColors, int color) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<SpnModel>) objects;
        this.myAdapter = this;
        this.mvColor = vColors;
        this.intColor = color;
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
            convertView = layoutInflator.inflate(R.layout.item_color_spinner, null);
            holder = new ViewHolder();
            holder.llColor = (LinearLayout) convertView.findViewById(R.id.ll_spn_colors);
            holder.mColor = (View) convertView.findViewById(R.id.i_spn_color);
            holder.mTextView = (TextView) convertView.findViewById(R.id.tv_spn_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {

            int colors = Color.parseColor(listState.get(position).getmColor());
            String text = listState.get(position).getTitle();
            mvColor.setBackgroundColor(colors);
            holder.llColor = (LinearLayout) convertView.findViewById(R.id.ll_spn_colors);
            holder.mColor.setBackgroundColor(colors);
            holder.mTextView.setText(text);
        }catch (Exception e){e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.mColor.setBackgroundColor(Color.BLACK);
            }
        }


        if (position<1){
            holder.mColor.setVisibility(View.INVISIBLE);
        }else {
            holder.mColor.setVisibility(View.VISIBLE);
        }

        if ( position < listState.size()){
            holder.mTextView.setText(listState.get(position).getTitle());
            holder.llColor.setBackgroundColor(mContext.getResources().getColor((position%2 == 0)
                    ? R.color.color_spn_impar : R.color.color_spn_par));
        }


        return convertView;
    }

    private class ViewHolder {
        private LinearLayout llColor;
        private TextView mTextView;
        private View mColor;
    }

}
