package com.jacd.animelistvideos.common;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.ui.dialogs.DialogUtils;

public class WebView {

    private Context context;
    private AppCompatActivity appCompatActivity;
    private String url_register;

    private WebView(Context context){
        this.context = context;
    }

    public WebView(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public static WebView getInstanceContext(Context context){
        return new WebView(context);
    }

    public static WebView getInstance(AppCompatActivity activity){
        return new WebView(activity);
    }


    public void getView(){
        url_register = ""+ context.getResources().getString(R.string.url_jkanime);

        Uri uriUrl = Uri.parse("www.google.com");
        uriUrl = Uri.parse(url_register);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        appCompatActivity.startActivity(launchBrowser);
    }


}
