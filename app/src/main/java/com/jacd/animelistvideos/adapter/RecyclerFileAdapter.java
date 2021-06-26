package com.jacd.animelistvideos.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Utils;

import java.util.List;

public class RecyclerFileAdapter extends RecyclerView.Adapter<RecyclerFileAdapter.ViewHolder> {

    private static final String TAG = "FileAdapter";
    private static final int REQ_CODE_EDIT = 1500;
    private final Context context;

    private int SELECT = 0;
    private List<String> mData;
    private boolean showIndex;

    public RecyclerFileAdapter(Context context, List<String> list, boolean showIndex ) {
        this.context = context;
        this.mData = list;
        this.showIndex = showIndex;

        SELECT = 1;
    }

    public RecyclerFileAdapter(Context context, List<String> list, int select, boolean showIndex ) {
        this.context = context;
        this.mData = list;
        this.showIndex = showIndex;

        SELECT = 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_file_manager, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textTitle.setText(mData.get(position));

        if (SELECT == 1){
            holder.textIndex.setText((position+1)+".");
        }else{
            String dd = Utils.getToday(FormatDate.clasic_long);
            holder.textIndex.setText(dd);

        }



        if (showIndex){
            holder.textIndex.setVisibility(View.VISIBLE);
        }else{
            holder.textIndex.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            Log.w(TAG, "click");
        });


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textIndex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.item_tv_text);
            textIndex = itemView.findViewById(R.id.item_tv_index);

        }

    }

    public void refresh(){
        notifyDataSetChanged();
    }

    public void refresh(List<String> list){
        this.mData = list;
        notifyDataSetChanged();
    }

    private void snackMessage(View view, @NonNull String message, View.OnClickListener clickListener){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Undo",clickListener).show();
    }


}



