package com.jacd.animelistvideos.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.Contecting;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.TimeRange;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.ImageModel;
import com.jacd.animelistvideos.models.VideoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Thread.sleep;

public class MyServices extends Service {

    private static final String TAG = "MyServices";
    public static Context context;
    public MyServices() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Servicio iniciado x...");
        context = this;
        try{
            PusherTaskNew pusherTask = new PusherTaskNew(this,
                    ""+Sets.pusher_cluster,""+Sets.pusher_key,
                    ""+Sets.pusher_channel_news,""+Sets.pusher_envent_new);
            Hilo2 hil = new Hilo2(this);
            hil.execute();
        } catch (Exception e) { e.printStackTrace(); }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Context getContext() {
        return context;
    }
}

class Hilo2 extends AsyncTask<String, String, String> {
    private static final String TAG ="Hilo2 Service";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int TIME_SLEEP;
    private final int TIME_MILISEC = 1000;

    //private static int LIST_LENGTH = 0;
    private final int COUNT_MAX = 600;
    private int count = 0;
    List<VideoModel> listNews = new ArrayList<>();
    private String[] states = new String[]{"Sin Red", "Contectando...", "AList Videos"};

    private String current_date;
    private String current_hour;

    Hilo2(Context context){
        this.context = context;
        if (context!=null){
            settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
            editor = settings.edit();
            listNews = new ArrayList<>();
            //LIST_LENGTH = settings.getInt("LIST_LENGTH",0);
            int SECOND = TIME_MILISEC* 60;
            int MINUTE =  TIME_MILISEC*60* 59 ;
            int HOURS = TIME_MILISEC*3600* 7;
            TIME_SLEEP = SECOND + MINUTE + HOURS;
        }

    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            do {
                String ddd = Utils.getToday(FormatDate.clasic_long); // ->  yyyy/MM/dd - HH:mm:ss
                current_date = ddd.split(" - ")[0];//observo el dia actual
                current_hour = ddd.split(" - ")[1];//observo la hora actual

                Intent refreshDelivery;
                sleep(1000);
                count++;

                String value;
                if ( Contecting.isNetDisponible(context)){
                    if (Contecting.isOnlineNet(context)){
                        value = states[2];
                        refreshDelivery = new Intent(Sets.STRING_BROADCAST_INTERNET);
                        refreshDelivery.putExtra("internet", value);
                        refreshDelivery.putExtra("refesh",true);
                        context.sendBroadcast(refreshDelivery);
                    }else {
                        value = states[1];
                        refreshDelivery = new Intent(Sets.STRING_BROADCAST_INTERNET);
                        refreshDelivery.putExtra("internet", value);
                        refreshDelivery.putExtra("refesh",true);
                        context.sendBroadcast(refreshDelivery);
                    }
                }else{
                    value = states[0];
                     refreshDelivery = new Intent(Sets.STRING_BROADCAST_INTERNET);
                    refreshDelivery.putExtra("internet", value);
                    refreshDelivery.putExtra("refesh",true);
                    context.sendBroadcast(refreshDelivery);
                }

               if (count == 1500){
                   count = 0;
               }

                if (TimeRange.getRange(current_hour, "08:00:00", "09:00:00")){
                    if (count >1450) getListNews();
                    System.out.println("______________VIENDO(rango_1)______________");
                }else  if (TimeRange.getRange(current_hour, "12:00:00", "13:00:00")){
                    if (count >1450) getListNews();
                    System.out.println("______________VIENDO(rango_2)______________");
                }/*
                else  if (TimeRange.getRange(current_hour, "02:00:00", "03:00:00")){ //test de notificaciones
                    if (count>=40 && count<=50)  NotificationsTask.createNotificacions(context, "text", "count: "+count, "info adicional", 0);
                    System.out.println("______________VIENDO(rango_2)______________");
                }*/
                else if (TimeRange.getRange(current_hour, "17:00:00", "18:00:00")){

                    String date = settings.getString("dia_actual", current_date); //busco la ultima fecha que se hizo la notificacion

                    if (!current_date.equals(date)){
                        editor.putString("dia_actual", current_date); //salvo la nueva fecha de la notificacion
                        editor.commit();
                    }

                    if (count >1450) getListNews();
                    System.out.println("______________VIENDO(rango_3)______________");
                }else if(Utils.isHourInIntervals(current_hour, "23:50:00", "03:00:00")){
                   if( count < 10 || count>=1490) System.out.println("Ningun rango...");
                }


                //Log.w(TAG,"doInBackground Hilo2: "+value);
            }while (true);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private void getListNews() {
        try{
            count=0;
            List<VideoModel> listVideo = new ArrayList<>();
            listNews = new ArrayList<>();

            listVideo.addAll(AListDB.getInstance(context).getVideo(AListDB.table_date));

            String DAY = Utils.getToday(FormatDate.day);
            DAY = Utils.cleanString(DAY);

            for (VideoModel vm: listVideo){
                int ind = Integer.parseInt(vm.getContent().split("-")[2]);
                String dayVideo = (""+vm.getDay()).equals("null") ? Sets.DAYS[ind] : (""+vm.getDay());
                dayVideo = Utils.cleanString(dayVideo.toLowerCase());

                Log.w(TAG, DAY+" == "+dayVideo);

                String stateVideo = vm.getState().toLowerCase();
                stateVideo = Utils.cleanString(stateVideo);

                String STATE = Sets.STATES[Sets.STATES.length - 1].toLowerCase();
                STATE = Utils.cleanString(STATE);

                //limitado por: Dia y por Estado != finalizado
                if ((DAY.equals(dayVideo)) && (!stateVideo.equals(STATE))){
                    listNews.add(vm);
                }

            }

            if (listNews.size()>0){
                for (VideoModel vm: listNews) {
                    String CONTENT = ""+vm.getTitle() +"\n-Capitulo: "+vm.getCapitule() +"\n-Tipo: "+vm.getType() +"\n-Estado: "+vm.getState() +"\n-Color: "+vm.getColor();
                    String contentInfo = "" + vm.getCapitule();//"info adicional"

                    if (!vm.getImage().isEmpty()){
                        Bitmap bm = Utils.base64ToBitmap(vm.getImage().replace("auxBase64",""));
                        if (bm!=null){
                            bm = Utils.getResizedBitmap(bm, 600, 400);
                            NotificationsTask.createNotificacions(context, "Animes de: "+DAY,"*Cap. "+vm.getCapitule()+" de " + vm.getTitle()+", "+CONTENT, bm,""+contentInfo,vm.getId());
                        }else{
                            Log.i(TAG, "No image");
                            NotificationsTask.createNotificacions(context, "Animes de: " + DAY, "-Cap. "+vm.getCapitule()+" de " + vm.getTitle(), "info adicional", vm.getId());
                        }
                    }
                    else {
                        Log.i(TAG, "No image");
                        NotificationsTask.createNotificacions(context, "Animes de: " + DAY, "-> Cap. "+vm.getCapitule()+" de " + vm.getTitle(), "info adicional", vm.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listNews.size()<1){
            String base = "https://www.zelda.com/breath-of-the-wild/assets/icons/BOTW-Share_icon.jpg";
            long date = Utils.dateToEpoch(FormatDate.clasic_long,  Utils.getToday(FormatDate.clasic_long));
            VideoModel vm = new VideoModel();
            vm.setId(0);
            vm.setTitle("Animes del día");
            vm.setImage(base);
            vm.setDate(String.valueOf(date));
            listNews.add(vm);
        }
    }

//---------------------------------------------------

    private void sendBroadCast(Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("es.androcode.android.BootBroadcast");
        broadcastIntent.putExtra("parameter", "Nueva notificacion.");
        context.sendBroadcast(broadcastIntent);
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        sendBroadCast(context);
        //new Hilo2(context).execute();
    }

}




