package com.jacd.animelistvideos.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.models.VideoModel;
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

/**
 *      agregar
 *
 *     implementation 'com.github.bumptech.glide:glide:4.10.0'
 *     implementation 'com.google.code.gson:gson:2.8.6'
 *     implementation 'com.pusher:pusher-java-client:2.2.1'
 * **/

public  class PusherTaskNew {

    private static final String TAG = "PusherTask";
    private static String response;
    private static String title_notific, title_notific2;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static final String SHARED_PUSHER="_shared_pusher";

    public PusherTaskNew(Context context, @NonNull String cluster, @NonNull String apiKey,
                         @NonNull String chanelName, @NonNull String eventName) {
        response = "";
        settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
        editor = settings.edit();
        PusherOptions options = new PusherOptions();
        options.setCluster(""+ cluster);

        Pusher pusher = new Pusher(""+ apiKey, options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                String string = "There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e;

                Log.i("Pusher", string);

            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe(""+ chanelName);

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

                    String chan = object.getString("channel");
                    String eve = object.getString("event");

                    title_notific = "Canal de "+chan.toUpperCase()+ " ("+eve+")";
                    title_notific2 = "Canal de "+chan.toUpperCase();


                    String _d = Utils.getToday(FormatDate.full);
                    long dd = (Utils.dateToEpoch(FormatDate.full, _d))/1000;
                    String idstr = (""+dd).substring( 4);
                    int id = Integer.parseInt(idstr);
                    //Log.e(TAG, ""+idstr);

                    //todo: aqui envio/actualizo la informacion al hilo principal
                    if (!response.isEmpty()){
                        try{//revisar
                            Intent refreshDelivery;
                            refreshDelivery = new Intent(Sets.STRING_BROADCAST_INTERNET);
                            refreshDelivery.putExtra("notific", response);

                            VideoModel vm = new Gson().fromJson(response, VideoModel.class);
                            String CONTENT = "Titulo: "+ vm.getTitle()
                                    +"\nCapitulo: "+vm.getCapitule()
                                    +((vm.getSeason().equals("0") || vm.getSeason().equals("1"))?"":"\nTemporada: "+vm.getSeason())
                                    +"\nEstado: "+(vm.getDone() ?"Visto.":"Por ver.");

                            String url = ""+ vm.getContent();

                            NotificationsTask.createPusherNotificacions(context, ""+title_notific,""+CONTENT, ""+url, id);

                            editor.putString( SHARED_PUSHER , response );
                        } catch (Exception e) {
                            e.printStackTrace();
                            editor.putString( "_shared_pusher_all" , response );
                            NotificationsTask.createPusherNotificacions(context, ""+title_notific2, response, ""+_d, id);
                        }

                        editor.commit();
                    }

                    //notifications(context, response);
                }catch (Exception e) {
                    e.printStackTrace();
                    /*
                    * int idVideo = AListDB.getInstance(context).getLastIDVideo() +1;
                        vm.setId(idVideo);
                        List<VideoModel> list = new ArrayList<>();
                        list.add(vm);
                        AListDB.getInstance(context).setVideo(list);
                    * */
                }
            }
        });
    }

}
