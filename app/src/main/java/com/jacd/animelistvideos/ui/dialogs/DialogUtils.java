package com.jacd.animelistvideos.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.ui.activity.HomeActivity;
import com.jacd.animelistvideos.ui.activity.WebViewActivity;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

import java.util.List;

public class DialogUtils {

    private Context context;
    public static Dialog dialog;
    private String[] webs;

    private DialogUtils(Context context){
        this.context = context;
    }

    public static DialogUtils getInstance(Context context){
        return new DialogUtils(context);
    }

    /* Interface */
    public interface onClickDataInt{
        View.OnClickListener show(int i);
    }
    public interface OnClickData{
        void show(Dialog dialog);
    }

    public void dialogProgress(@NonNull String title, OnClickData onClickData) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        onClickData.show(dialog);
        dialog.show();
    }

    public void customDialogMessage(@NonNull String title, String message, View.OnClickListener clickOK, View.OnClickListener clickCancel) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_general_message);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tv_message = dialog.findViewById(R.id.tv_dialog_message);
        TextView tv_title = dialog.findViewById(R.id.tv_dialog_title);
        if (message!=null){
            tv_message.setText(message);
        }else{
            tv_message.setVisibility(View.GONE);
        }

        tv_title.setText(title);

        Button btn_ok = dialog.findViewById(R.id.btn_dialog_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_dialog_cancel);

        btn_ok.setOnClickListener(clickOK);
        btn_cancel.setOnClickListener(clickCancel);

        dialog.show();
    }

    public void customDialogWebList(@NonNull String title, List<String> list) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_menu_webview);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton btnExit = dialog.findViewById(R.id.dialog_exit);
        CarouselView carouselView = dialog.findViewById(R.id.carouselView_dialog);
        TextView tv_title = dialog.findViewById(R.id.dialog_title);

        carouselView.setSize(list.size());
        carouselView.setResource(R.layout.item_carousel_var);
        carouselView.setAutoPlay(true);
        carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        carouselView.setCarouselOffset(OffsetType.CENTER);
        carouselView.setCarouselViewListener((view, position) -> {
            ImageView imageView = view.findViewById(R.id.item_dialog_image);
            ImageView imageViewBG = view.findViewById(R.id.item_dialog_image_bg);
            TextView textView = view.findViewById(R.id.item_dialog_title);

            textView.setText(list.get(position));
            if (list.get(position).contains("http") || list.get(position).contains("#")){
                Glide.with(carouselView)
                        .load(list.get(position).contains("#")? Color.parseColor(list.get(position)):list.get(position))
                        .fitCenter()
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_done_white_24dp)
                        .into(imageView);

                Glide.with(context)
                        .load(list.get(position).contains("#")? Color.parseColor(list.get(position)):list.get(position))
                        .fitCenter()
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_baseline_ondemand_video_24)
                        .into(imageViewBG);
            }else if (list.get(position).contains("data:image")){
                Glide.with(carouselView)
                        .load(list.get(position))
                        .fitCenter()
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_done_white_24dp)
                        .into(imageView);

                Glide.with(context)
                        .load(list.get(position))
                        .fitCenter()
                        .placeholder(R.drawable.ic_baseline_image_search_24)
                        .error(R.drawable.ic_baseline_ondemand_video_24)
                        .into(imageViewBG);
            }else{
                int[] im = new int[]{
                        R.drawable.ic_banner_jkanime,
                        R.drawable.ic_banner_mangatigre,
                        R.drawable.ic_banner_animeflv};
                imageView.setImageResource(im[position]);
                imageViewBG.setImageResource(im[position]);
            }

            webs = new String[]{
                    ""+ context.getResources().getString(R.string.url_jkanime),
                    ""+ context.getResources().getString(R.string.url_mangas),
                    ""+ context.getResources().getString(R.string.url_animeflv)};

            imageView.setOnClickListener(view1 -> {
                //url_webview
                Intent i = new Intent(context, WebViewActivity.class);
                i.putExtra("url_webview", webs[position]);
                i.putExtra("title_webview", list.get(position));
                ((HomeActivity) context).startActivity(i);

                //Uri uriUrl = Uri.parse("www.google.com");
                //uriUrl = Uri.parse(webs[position]);
                //Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                //((HomeActivity) context).startActivity(launchBrowser);
            });
        });

        // After you finish setting up, show the CarouselView
        carouselView.show();
        tv_title.setText(title);

        btnExit.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void customDialogList(@NonNull String title, List<String> list, View.OnClickListener clickOK) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_list_item);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
       CarouselView carouselView = dialog.findViewById(R.id.carouselView);
        TextView tv_title = dialog.findViewById(R.id.tv_dialog_title);

        carouselView.setSize(list.size());
        carouselView.setResource(R.layout.item_news);
        carouselView.setAutoPlay(false);
        carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        carouselView.setCarouselOffset(OffsetType.CENTER);
        carouselView.setCarouselViewListener((view, position) -> {
            ImageView imageView = view.findViewById(R.id.item_news_image);
            TextView textView = view.findViewById(R.id.item_news_title);

            textView.setText(list.get(position));
            Glide.with(carouselView)
                    .load(list.get(position).contains("#")? Color.parseColor(list.get(position)):list.get(position))
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_done_white_24dp)
                    .into(imageView);

            imageView.setOnClickListener(view1 -> {
                Toast.makeText(context, ""+list.get(position), Toast.LENGTH_SHORT).show();
            });
        });

        // After you finish setting up, show the CarouselView
        carouselView.show();

        tv_title.setText(title);

        Button btn_ok = dialog.findViewById(R.id.btn_dialog_ok);

        btn_ok.setOnClickListener(clickOK);

        dialog.show();
    }


}
