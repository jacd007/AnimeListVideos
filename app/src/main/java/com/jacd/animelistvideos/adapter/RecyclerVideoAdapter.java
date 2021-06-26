package com.jacd.animelistvideos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.VideoModel;
import com.jacd.animelistvideos.ui.activity.HomeActivity;
import com.jacd.animelistvideos.ui.activity.ResumeActivity;
import com.jacd.animelistvideos.ui.dialogs.DialogUtils;

import java.util.List;

public class RecyclerVideoAdapter extends RecyclerView.Adapter<RecyclerVideoAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";
    private static final int REQ_CODE_EDIT = 1500;
    private final long date;
    private final Context context;

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private List<VideoModel> mData;

    public RecyclerVideoAdapter(Context context, List<VideoModel> list ) {
        this.context = context;
        this.mData = list;

        String dd = Utils.getToday(FormatDate.clasic_short);
        date = Utils.dateToEpoch(FormatDate.clasic_short, dd);

        settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
        editor = settings.edit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_today, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int ss = Integer.parseInt(mData.get(position).getSeason());
        String season = ss<1 ?" ":" T."+(ss<9?"0"+ss:ss);

        Glide.with(context)
            .load(mData.get(position).getImage())
            .placeholder(R.drawable.ic_baseline_image_search_24)
            .error(R.drawable.ic_baseline_broken_image_24)
            .into(holder.imageView);

        holder.textTitle.setText(mData.get(position).getTitle() + season);

        String dd = getDate(Long.parseLong(mData.get(position).getDate()));

        holder.textDate.setText(dd);

        holder.textCapitule.setText(getMinCero(mData.get(position).getCapitule()));

        String stateVideo = Utils.cleanString( mData.get(position).getState().toLowerCase());
        //exceptua finalizados ultima posicion
        String STATE = Utils.cleanString(Sets.STATES[Sets.STATES.length - 1].toLowerCase());//fin
        String STATE1 = Utils.cleanString(Sets.STATES[Sets.STATES.length - 2].toLowerCase());//pau
        String STATE2 = Utils.cleanString(Sets.STATES[Sets.STATES.length - 3].toLowerCase());//Olv

        boolean val1 = (stateVideo.equals(STATE));
        boolean val2 = (stateVideo.equals(STATE1));
        boolean val3 = (stateVideo.equals(STATE2));

        boolean val = val1 || val2 || val3;
        holder.textState.setText( ""+mData.get(position).getState());
        holder.textState.setVisibility(val ?View.VISIBLE :View.GONE);

        holder.imageView.setOnClickListener(view -> {
            try {
                editor.putString("AuxVideo", new Gson().toJson(mData.get(position)));
                editor.commit();
                Intent intent = new Intent(context, ResumeActivity.class);
                intent.putExtra("state",1);
                intent.putExtra("new",false);
                intent.putExtra("idVideo", mData.get(position).getId());
                intent.putExtra("tag",mData.get(position).getId());
                ((HomeActivity) context).startActivityForResult(intent, REQ_CODE_EDIT);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al enviar datos para editar", Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
            }

        });

        holder.imageView.setOnLongClickListener(view -> {
            VideoModel item = mData.get(position);
            String title = "\"Borrar Video\"\nAcción no reversible";
            String msg = "¿Desea borrar \""+item.getTitle()+"\"?";
            DialogUtils.getInstance(context).customDialogMessage(title, msg,
                    viewOk -> {
                        AListDB.getInstance(context).deleteItemByID(String.valueOf(item.getId()));
                        HomeActivity.list.remove(item);
                        notifyItemRemoved(position);

                        if (DialogUtils.dialog != null)
                            DialogUtils.dialog.dismiss();
                    }, viewCancel ->{
                        notifyDataSetChanged();

                        if (DialogUtils.dialog != null)
                            DialogUtils.dialog.dismiss();
                    });

            return false;
        });


    }

    private String getMinCero(String value) {
        int s = Integer.parseInt(value);
        return s<9 ?"0"+value :value;
    }

    private void getImageItem(String image, ImageView imageView) {
        if (image.contains("data:image/jpeg;base64,")){
            try {
                Bitmap bitmap = Utils.base64ToBitmap(image.split(",")[0]);

                Glide.with(context)
                        .load(bitmap)
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                Glide.with(context)
                        .load(image)
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .into(imageView);
            }

        }else{
            Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(imageView);
        }

    }

    private String getDate(long date_aux) {
       String dd = Utils.epochToDate(date_aux, FormatDate.clasic_short);
       long dde = Utils.dateToEpoch(FormatDate.clasic_short, dd);

        return dde == date
                ?"Hoy, "+Utils.epochToDate(date_aux, FormatDate.clasic_hour)
                :Utils.epochToDate(date_aux, FormatDate.clasic_short3);
    }

    public VideoModel getItemFromPosition(int pos){
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItem(VideoModel videoModel){
        this.mData.add(videoModel);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textTitle;
        TextView textDate;
        TextView textState;
        TextView textCapitule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_today_iv_image);
            textTitle = itemView.findViewById(R.id.item_today_tv_title);
            textDate = itemView.findViewById(R.id.item_today_tv_date);
            textCapitule = itemView.findViewById(R.id.item_today_tv_capitule);
            textState = itemView.findViewById(R.id.item_history_tv_status);

        }

    }

    public void refresh(){
        notifyDataSetChanged();
    }

    public void refresh(List<VideoModel> list){
        this.mData = list;
        notifyDataSetChanged();
    }

    private void snackMessage(View view, @NonNull String message, View.OnClickListener clickListener){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Undo",clickListener).show();
    }


}















