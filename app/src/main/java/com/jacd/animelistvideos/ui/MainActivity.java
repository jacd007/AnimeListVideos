package com.jacd.animelistvideos.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.services.MyServices;
import com.jacd.animelistvideos.ui.activity.HomeActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String FORMATE_DATE = "dd 'de' MMMM 'del' yyyy, 'a las' hh:mm a";

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private Intent i;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        settings = getSharedPreferences(getString(R.string.shared_key), 0);
        editor = settings.edit();

        starServiceNotiRED();
        //starServiceNotiVIDEOS();

        VideoView videoview = (VideoView) findViewById(R.id.login_video_bg);
        videoview.requestFocus();

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_1);
        videoview.setVideoURI(uri);
        videoview.setOnPreparedListener(mp -> mp.setLooping(false));

        videoview.setOnCompletionListener(mp -> {
            goScreenResume();
        });
        videoview.setOnErrorListener((mp, what, extra) -> {
            Log.e("MainActivity","error al repoducir");
            goScreenResume();
            return false;
        });
        videoview.start();


        //new Handler().postDelayed(this::goScreenResume,3000);

    }

    private void goScreenResume() {
        String date = Utils.getToday(FORMATE_DATE);
        i = new Intent(this, HomeActivity.class);
        i.putExtra("data",""+ date);
        i.putExtra("data_epoch",""+Utils.dateToEpoch(FORMATE_DATE, date));
        startActivity(i);
        //Bungee.fade(MainActivity.this);
        finish();
    }

    private void starServiceNotiRED() {
        Intent serv = new Intent(this, MyServices.class); //serv de tipo Intent
        if (!Utils.isMyServiceRunning(this, MyServices.class)){ //método que determina si el servicio ya está corriendo o no
            this.startService(serv); //ctx de tipo Context
            Log.d("App", "Service started");
            //btnNotifications.setText("Detener Servicio");
        } else {
            //this.stopService(serv);
            //starServiceNotiRED();
            //btnNotifications.setText("Iniciar Servicio");
            Log.d("App", "Service already running");
        }
    }


    /*
    private void starServiceNotiVIDEOS() {
        Intent serv = new Intent(this, ServiceNotification.class); //serv de tipo Intent
        if (!Utils.isMyServiceRunning(this, ServiceNotification.class)){ //método que determina si el servicio ya está corriendo o no
            this.startService(serv); //ctx de tipo Context
            Log.d("App", "Service started 2");
            //btnNotifications.setText("Detener Servicio");
        } else {
            //this.stopService(serv);
            //btnNotifications.setText("Iniciar Servicio");
            Log.d("App", "Service already running");
        }
    }
     */




}