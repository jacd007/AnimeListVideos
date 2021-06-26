package com.jacd.animelistvideos.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.adapter.RecyclerFileAdapter;
import com.jacd.animelistvideos.common.Sets;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private Context context;
    private EditText etChannel, etEvent;
    private TextView tvSelectSubs;
    private Switch swSubs;
    private Button btnSaveSubs, btnSubs;
    private RecyclerView rv;

    private List<String> list;
    private RecyclerFileAdapter adapter;
    private RecyclerView recyclerView;

    private String mChannel, mEvent;
    private Pusher pusher;

    private final String cluster = Sets.pusher_cluster;
    private final String apiKey = Sets.pusher_key;
    private ArrayList<String> listChannel;
    private ArrayList<String> listEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context = NotificationActivity.this;
        initComponent();
        try {
            startcomponent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponent(){
        list = new ArrayList<>();
        listChannel = new ArrayList<>();
        listEvents = new ArrayList<>();

        etChannel = findViewById(R.id.et_notification_channel);
        etEvent = findViewById(R.id.et_notification_event);
        recyclerView = findViewById(R.id.rv_subscription);

        tvSelectSubs = findViewById(R.id.tv_notification_select_subs);

        swSubs = findViewById(R.id.sw_notification_isSubs);

        btnSaveSubs = findViewById(R.id.btn_notification_save_to_list);
        btnSubs = findViewById(R.id.btn_notification_subs);

        rv = findViewById(R.id.rv_subscription);

        print(list, true);
    }

    private void print(List<String> list, boolean showIndex) {
        adapter = new RecyclerFileAdapter(context, list, showIndex);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void startcomponent() throws Exception {
        etChannel.setText("noticias");
        listChannel.add("noticias");
        listEvents.add("nuevos");
        swSubs.setOnCheckedChangeListener((compoundButton, isCheck)->{
            swSubs.setText(isCheck?"Sí":"No");
        });

        btnSaveSubs.setOnClickListener(view -> {
            //HERE CODE
        });

        etEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()){
                    mEvent = "Seleccione uno de la lista";
                }else{
                    mEvent = charSequence.toString();
                }
                tvSelectSubs.setText(mEvent);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        PusherOptions options = new PusherOptions();
        options.setCluster(""+ cluster);

        pusher = new Pusher(""+ apiKey, options);

        btnSubs.setOnClickListener(view -> {
            if (tvSelectSubs.getText().toString().length()>2){
                Toast.makeText(context, "Procesado...", Toast.LENGTH_SHORT).show();
                mEvent = tvSelectSubs.getText().toString();
                tvSelectSubs.setText("");
                etEvent.setText("");
                mEvent = mEvent.equals("Seleccione uno de la lista") ? "nuevos" : mEvent;

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


                Channel channel = pusher.subscribe(""+ Sets.pusher_channel_news);

                channel.bind(mEvent, callback);
           /*
            for (String item: listEvents){
                channel.bind(item, callback);
            }*/
            }else{
                Toast.makeText(context, "No se puede suscribir a "+tvSelectSubs.getText().toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private SubscriptionEventListener callback = new SubscriptionEventListener() {
        @Override
        public void onEvent(PusherEvent event) {
            Log.i("onEvent", event.toString());
            try{
                JSONObject object = new JSONObject(event.toString());
                String response = object.getString("data");

                list.add(response);
                print(list, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //HERE CODE

        }
    };

}