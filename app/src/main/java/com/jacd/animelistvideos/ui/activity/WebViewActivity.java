package com.jacd.animelistvideos.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.Utils;


public class WebViewActivity extends AppCompatActivity {

    private final String TAG = "WebViewFragment";
    private Context mContext;
    private Toolbar mToolbar;
    private WebView webView;
    private ProgressDialog dialog;

    private String url_webView="";
    private String strTitle="Registro de aliados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_web_view);


        getBundle();

    }

    private void getBundle() {
        mContext = WebViewActivity.this;
        Bundle extras = getIntent().getExtras();
        try{
            if (extras!=null){
                url_webView = ""+ extras.getString("url_webview");
                strTitle = ""+ extras.getString("title_webview");
            }else{
                url_webView = ""+ mContext.getResources().getString(R.string.url_jkanime);
                strTitle = "Registro de aliados";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initComponent();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initComponent() {

        mToolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(strTitle.replace("null", "WebView"));

        mToolbar.setNavigationOnClickListener(view -> {
            goBackScreen();
        });



        dialog = ProgressDialog.show(mContext, "",
                "Cargando...", true);
        if (Utils.checkConnectivity(mContext)){
            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){

                public void onPageFinished(WebView view, String url) {
                    dialog.dismiss();
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    view.loadUrl(url);
                    return true;
                }

            });
            webView.loadUrl(""+url_webView);
        }else{
            new Handler().postDelayed(()->{
                if (dialog!=null) dialog.dismiss();
                Toast.makeText(mContext, "Sin conexión a Internet...", Toast.LENGTH_SHORT).show();
            },8000);
        }

    }

    private void goBackScreen() {
        //Intent i = new Intent();
        //setResult(RESULT_CANCELED, i);
        //Bungee.fade(WebViewActivity.this);
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();

        }
    }
}