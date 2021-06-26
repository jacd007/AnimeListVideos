package com.jacd.animelistvideos.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.ImageModel;
import com.jacd.animelistvideos.models.VideoModel;
import com.jacd.animelistvideos.ui.activity.HomeActivity;
import com.jacd.animelistvideos.ui.dialogs.DialogUtils;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *      agregar
 *
 *     implementation 'com.github.bumptech.glide:glide:4.10.0'
 *     implementation 'com.google.code.gson:gson:2.8.6'
 *     implementation 'com.pusher:pusher-java-client:2.2.1'
 * **/

public  class PusherTask {

    private static final String TAG = "PusherTask";
    private static final String auxB64 = "data:image/jpeg;base64,";

    private static String response;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static final String SHARED_PUSHER="_shared_pusher";

    private Pusher pusher;
    private Channel channel;
    private Bitmap bitmap;

    public PusherTask(@NonNull Activity activity, Context context, @NonNull String cluster, @NonNull String apiKey,
                      @NonNull String chanelName, @NonNull String eventName) {
        response = "";
        settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
        editor = settings.edit();
        PusherOptions options = new PusherOptions();
        options.setCluster(""+ cluster);

        pusher = new Pusher(""+ apiKey, options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        channel = pusher.subscribe(""+ chanelName);

        channel.bind(""+ eventName, new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.i("Pusher", "Received event with data: " + event.toString());
                response="";
                try {
                    JSONObject object = new JSONObject(event.toString());
                    String data = object.getString("data");
                    Log.i(TAG, data);

                    if (Utils.isJSONArray(data)){
                        JSONArray item = object.getJSONArray("data");
                        response = item.toString();
                    }else if(Utils.isJSONObject(data)){
                        JSONObject item = object.getJSONObject("data");
                        response = item.toString();
                    }else{
                        response = data;
                    }

                    //todo: aqui envio/actualizo la informacion al hilo principal
                    if (!response.isEmpty()){
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                //todo: aqui el hilo principal, tener cuidado con "activity"
                                Toast.makeText(context, "Video recibido...", Toast.LENGTH_SHORT).show();
                                try{
                                    VideoModel vm = new Gson().fromJson(response, VideoModel.class);


                                    int ID = AListDB.getInstance(context).getIdMaxVideo() +1;
                                    vm.setId(ID);

                                    if(vm.getImage().contains("http") || vm.getImage().contains("data:image")){
                                        String url = vm.getImage();

                                        Glide.with(context)
                                                .asBitmap()
                                                .load("" + vm.getImage())
                                                .into(new CustomTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        String b64 = ""+Utils.bitmapToBase64(resource);
                                                        //vm.setImage(b64);
                                                        bitmap = resource;
                                                        vm.setContent(vm.getContent()+"-"+url);

                                                        String msg = "Save WITH image";
                                                        saveDataToDB(context, vm, msg);

                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                    }
                                                });
                                    }else{
                                        String msg = "Save WITHOUT image";
                                        saveDataToDB(context, vm, msg);
                                    }

                                    
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                }

                                System.out.println("Algo de la data del pusher!");
                            }
                        });

                    }

                    //notifications(context, response);
                }catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });
    }

    public void pusherDiscontect(){
        pusher.disconnect();
    }

    private void saveDataToDB(Context context, VideoModel vm, String msg){
        List<VideoModel> listVideo = new ArrayList<>();
        String image ="";
        String VIDEO_TAG = "Video-"+Utils.getToday(FormatDate.full) ;

        try {
            image = msg.contains("WITHOUT") ? (auxB64 + Utils.bitmapToBase64(bitmap)) : Sets.baseNoImage;

            //vm.setImage(image);
        } catch (Exception e) {  e.printStackTrace(); }

        String finalImage = image;
        DialogUtils.getInstance(context)
                .customDialogMessage("Agregar Video", "¿Desea actualizar la lista de videos?",
                        view -> {
                            listVideo.add(vm);

                            List<ImageModel> listImage = new ArrayList<>();
                            ImageModel imModel = new ImageModel();
                            imModel.setId(AListDB.getInstance(context).getIdMaxImage() +1);
                            imModel.setTitle(vm.getTitle());
                            imModel.setImage(finalImage);
                            imModel.setDate(vm.getDate());
                            imModel.setHistory("[]");
                            imModel.setUri("null");
                            imModel.setTag(VIDEO_TAG);

                            listVideo.add(vm);
                            listImage.add(imModel);
                            editor.putBoolean("addDB",true);
                            editor.commit();
                            //AListDB.getInstance(context).setImage(listImage);
                            //AListDB.getInstance(context).setVideo(listVideo);

                            HomeActivity.adapter.setItem(vm);
                            HomeActivity.lastItemRV();
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

                            if (DialogUtils.dialog != null)
                                DialogUtils.dialog.dismiss();
                        }, view -> {


                            if (DialogUtils.dialog != null)
                                DialogUtils.dialog.dismiss();
                        });
    }

    private static void notifications(Context context, String response) {
        String TITLE = context.getResources().getString(R.string.app_name),
                CONTENT = "",
                CONT_INFO = "Pedido",
                TICKERS = "ticker";
        int ID_NOTIFICACTIONS = 0;
        if (!response.isEmpty()){
            try {
                JSONObject object = new JSONObject(response);
                ID_NOTIFICACTIONS = object.has("order")?object.getInt("order"):ID_NOTIFICACTIONS;
                TITLE = object.has("title")?object.getString("title"):TITLE;
                CONTENT = object.has("message")?object.getString("message"):CONTENT;
                CONT_INFO = object.has("date")?object.getString("date"):CONT_INFO;
                TICKERS = object.has("ticker")?object.getString("ticker"):TICKERS;

            }catch (Exception e){e.printStackTrace();}

            NotificationsTask.createNotificacions(context, TITLE, CONTENT, CONT_INFO, ID_NOTIFICACTIONS);
            //NotificationsTask.createNotificacions(context, TITLE, CONTENT, TICKERS, CONT_INFO, 0, ID_NOTIFICACTIONS);
        }
    }

}
