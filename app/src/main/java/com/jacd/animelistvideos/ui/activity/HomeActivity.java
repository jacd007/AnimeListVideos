package com.jacd.animelistvideos.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.adapter.RecyclerVideoAdapter;
import com.jacd.animelistvideos.common.Contecting;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.InterfaceSwipe;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.VideoModel;
import com.jacd.animelistvideos.services.NotificationsTask;
import com.jacd.animelistvideos.services.PusherTask;
import com.jacd.animelistvideos.ui.dialogs.DialogUtils;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Context context;
    private static final int REQ_CODE_NEW = 1200;
    private static final int REQ_CODE_EDIT = 1500;

    private LinearLayout llSearch;
    public static TextView tvOnline, tvCount;
    private ImageButton btnWebView, ibtnSearchShow, ibtnSearchHide, ibtnSearchSearch, ibtnAddVideo, ibtnAllVideo;
    private EditText etSearchText;
    private CarouselView carouselView;
    private Spinner spnSimulateDay, spnSimulateStatus;

    private static RecyclerView recyclerView;
    public static RecyclerVideoAdapter adapter;
    public static List<VideoModel> list;
    public static List<VideoModel> listNews;

    private boolean valNotif = true;
    private static String url_webView;
    //private BackGroundNotif1 backGroundNotif1;

    private PusherTask pusherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initComponent();
    }

    private void initComponent() {

        context = HomeActivity.this;
        list = new ArrayList<>();
        listNews = new ArrayList<>();
        valNotif = true;

        tvOnline = (TextView) findViewById(R.id.tv_home_state_internet);
        tvOnline.setOnLongClickListener(view -> {
            Intent intent = new Intent(context, FileManagerActivity.class);
            startActivity(intent);
            return false;
        });
        tvCount =  (TextView) findViewById(R.id.tv_home_count);
        ibtnSearchShow = (ImageButton) findViewById(R.id.ibtn_search_show);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);

        ibtnSearchShow.setVisibility(View.VISIBLE);
        llSearch.setVisibility(View.GONE);

        spnSimulateDay = (Spinner) findViewById(R.id.spn_simulate_day);
        spnSimulateStatus = (Spinner) findViewById(R.id.spn_simulate_status);

        btnWebView = (ImageButton) findViewById(R.id.ibtn_webview);
        ibtnAddVideo = (ImageButton) findViewById(R.id.ibtn_search_add_video);
        ibtnSearchHide = (ImageButton) findViewById(R.id.ibtn_search_cancel);
        ibtnSearchSearch = (ImageButton) findViewById(R.id.ibtn_search_show_hide);
        ibtnAllVideo = (ImageButton) findViewById(R.id.ibtn_list_all);

        etSearchText = (EditText) findViewById(R.id.et_search_search);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        carouselView = findViewById(R.id.carouselView_2);

        //MyAdapterText myAdapterText = new MyAdapterText(this, Sets.DAYS_SPINNER );
        //spnSimulateDay.setAdapter(myAdapterText);

        ArrayAdapter<String> dAdapter;

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.DAYS_SPINNER);
        dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        spnSimulateDay.setAdapter(dAdapter);

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.STATES_SPINNER);
        dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        spnSimulateStatus.setAdapter(dAdapter);

        /*
        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.DAYS);
        dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        spnSimulateDay.setAdapter(dAdapter);

        MyAdapterText myAdapterText1 = new MyAdapterText(this, Sets.STATES_SPINNER);
        spnSimulateStatus.setAdapter(myAdapterText1);
        */

        boolean valSize = (AListDB.getInstance(context).getVideoSizeDB()>0);

        spnSimulateDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                valNotif = i==0;
                if (valSize){
                    if (i != 0){
                        print(getSimulateDayList(i));
                    } else{
                        print(list);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spnSimulateStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (valSize){
                    List<VideoModel> listVideo = new ArrayList<>();
                    listVideo.addAll(AListDB.getInstance(context).getVideo(AListDB.table_name));
                    if(i == 0) {
                        print(list);
                    }else if(i == 1){
                        print(listVideo);
                    } else{
                        List<VideoModel> aux = new ArrayList<>();
                        for (VideoModel vm: listVideo){
                            String stateVideo = vm.getState().toLowerCase();
                            stateVideo = Utils.cleanString(stateVideo);

                            String STATE = Sets.STATES[i-2].toLowerCase();
                            STATE = Utils.cleanString(STATE);

                            if (stateVideo.equals(STATE)){
                                aux.add(vm);
                            }
                        }
                        print(aux);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        adapter = new RecyclerVideoAdapter(context, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        String base = "https://www.zelda.com/breath-of-the-wild/assets/icons/BOTW-Share_icon.jpg";
        long date = Utils.dateToEpoch(FormatDate.clasic_long,  Utils.getToday(FormatDate.clasic_long));
        VideoModel vm = new VideoModel();
        vm.setId(0);
        vm.setTitle("Animes del día");
        vm.setImage(base);
        vm.setDate(String.valueOf(date));
        vm.setDay(Sets.DAYS[0]);
        vm.setContent("0-0-0-0-0");
        listNews.add(vm);

        getList();
        getListNews();

        try {
            initCarousel(listNews);
        } catch (Exception e) {
            listNews = new ArrayList<>();
            listNews.add(vm);
            initCarousel(listNews);

            e.printStackTrace();
        }

        print(list);

        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String value = s.toString().toLowerCase();

                if (!value.isEmpty()){
                    List<VideoModel> list_aux = new ArrayList<>();

                    for (VideoModel vm: list){
                        if (vm.getTitle().toLowerCase().contains(value)){
                            list_aux.add(vm);
                        }
                    }
                    print(list_aux);
                    tvCount.setText(list_aux.size()+"/"+list.size());
                }else{
                    print(list);
                    tvCount.setText(""+list.size());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        //TODO: validar el "borrar" y "visto" del swipe
        //TODO: consultas a internet
        //TODO: servicio de notificaciones
    }

    private void getList() {
        try {
            List<VideoModel> aux = new ArrayList<>();
            List<VideoModel> listVideo = new ArrayList<>();
            list = new ArrayList<>();

            aux.addAll(AListDB.getInstance(context).getVideo(AListDB.table_name));

            for (int i=0; i<aux.size(); i++){
                VideoModel vm = aux.get(i);

                String stateVideo = vm.getState().toLowerCase();
                stateVideo = Utils.cleanString(stateVideo);

                //exceptua finalizados ultima posicion
                String STATE = Sets.STATES[Sets.STATES.length - 1].toLowerCase();
                STATE = Utils.cleanString(STATE);

                if (!stateVideo.equals(STATE)){
                    listVideo.add(vm);
                }

            }

            list.addAll(listVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getListNews() {
        try{
            List<VideoModel> listVideo = new ArrayList<>();
            listNews = new ArrayList<>();

            listVideo.addAll(AListDB.getInstance(context).getVideo(AListDB.table_date));

            String DAY = Utils.getToday(FormatDate.day);//Utils.getToday("F");
            DAY = Utils.cleanString(DAY);
            //DAY = Sets.DAYS[Integer.parseInt(DAY)+1];
            Log.e(TAG, ">>> "+DAY);


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

            Collections.sort(listNews, ((vm1, vm2) -> {
                long d1 = Long.parseLong(vm1.getDate());
                long d2 = Long.parseLong(vm2.getDate());

                return Long.compare(d1, d2);
            }));
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


    @SuppressLint("SetTextI18n")
    private void initCarousel(List<VideoModel> list) {
        String dateToday = Utils.getToday(FormatDate.clasic_short);
        String TODAY = Utils.getToday(FormatDate.day);

        carouselView.setSize(list.size());
        carouselView.setResource(R.layout.item_news);
        carouselView.setAutoPlay(true);
        //carouselView.setIndicatorSelectedColor(R.color.colorSelect);
        //carouselView.setIndicatorUnselectedColor(R.color.colorUnselect);
        carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        carouselView.setCarouselOffset(OffsetType.CENTER);
        carouselView.setCarouselViewListener((view, position) -> {
            ImageView imageView = view.findViewById(R.id.item_news_image);
            ImageView imageViewBG = view.findViewById(R.id.item_news_image_bg);
            TextView textView = view.findViewById(R.id.item_news_title);
            TextView textState = view.findViewById(R.id.item_news_tv_status);

            VideoModel vm = list.get(position);
            try{
                long _dateVideo = Long.parseLong(vm.getDate());
                String dateVideo = Utils.epochToDate(_dateVideo, FormatDate.clasic_short);

                String banner = vm.getState()+"\nCap: "+vm.getCapitule();
                banner = dateToday.equals(dateVideo) ? "Cap. "+(Integer.parseInt(vm.getCapitule())-1)+"\nVISTO..." : banner ;
                textState.setText(banner);
            } catch (Exception e) {  e.printStackTrace();   textState.setText("Nada que\n ver..."); }

            textView.setText(vm.getTitle());

            Glide.with(context)
                    .load(vm.getImage())
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_done_white_24dp)
                    .into(imageView);

            Glide.with(context)
                    .load(vm.getImage())
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_done_white_24dp)
                    .into(imageViewBG);

            imageView.setOnLongClickListener(view1 -> {
                Intent intent = new Intent(context, ResumeActivity.class);
                intent.putExtra("state",1);
                intent.putExtra("new",false);
                intent.putExtra("idVideo", vm.getId());
                intent.putExtra("tag",vm.getId());
                startActivityForResult(intent, REQ_CODE_EDIT);
                return false;
            });
            imageView.setOnClickListener(view1 -> {
                VideoModel video = list.get(position);
                String text = "Videos del Dia";
                text = video.getDone() ? "Video visto..." : "Video por ver";

                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            });
        });

        // After you finish setting up, show the CarouselView
        carouselView.show();
    }

    public void goToUrl (String url_webView) {
        Uri uriUrl = Uri.parse("www.google.com");
        uriUrl = Uri.parse(url_webView);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private void goWebView(String url_register, String strTitle) {
        Intent i = new Intent(HomeActivity.this, WebViewActivity.class);
        i.putExtra("url_webview", url_register);
        i.putExtra("title_webview", strTitle);
        HomeActivity.this.startActivity(i);
        //Bungee.fade(HomeActivity.this);
        //finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        pusherTask= new PusherTask(this, context, ""+Sets.pusher_cluster,""+Sets.pusher_key,
                "DB","agregar");

        //backGroundNotif1 = new BackGroundNotif1(context);
        //backGroundNotif1.execute();
        tvCount.setOnLongClickListener(view -> {
            Sets.saveDataShared(context, "dia_actual","13/06/2021");
            return false;
        });
        tvCount.setOnClickListener(view -> {
            try {
                if (listNews!=null && listNews.size()>0 && valNotif) {
                    generateNotifications();

                    //SharedPreferences settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
                    //SharedPreferences.Editor editor = settings.edit();
                    //editor.putString("dia_actual", "12-06-2021");
                    //editor.commit();
                    //Toast.makeText(context, "Salvado...", Toast.LENGTH_SHORT).show();

                    //Log.e(TAG, "REVISAR => generateNotifications");
                }else
                    Toast.makeText(context, "No Hay Videos...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error cargando Videos...", Toast.LENGTH_SHORT).show();
            }

        });

        if (context !=null)
            tvCount.setText(""+AListDB.getInstance(context).getVideoSizeDB());

        String[] states = new String[]{"Sin Red", "Contectando...", "AList Videos"};
        tvOnline.setText(states[0]);

        if ( Contecting.isNetDisponible(context)){
           if (Contecting.isOnlineNet(context)){
               tvOnline.setText(states[2]);
           }else {
               tvOnline.setText(states[1]);
           }
        }else{
            tvOnline.setText(states[0]);
        }

        ibtnAllVideo.setOnClickListener(view -> {
            //Toast.makeText(context, "Recargando lista....", Toast.LENGTH_SHORT).show();
            //list = new ArrayList<>();
            //getList();
            //print(list);
            //tvCount.setText(""+list.size());
            recreate();
        });

        btnWebView.setOnClickListener(view -> {
            List<String> wv = new ArrayList<>();
            wv.add("Url jkanime");
            wv.add("Url mangas");
            wv.add("Url animeflv");

            DialogUtils.getInstance(context).customDialogWebList("WebViews", wv);
            //url_webView = ""+ getResources().getString(R.string.url_jkanime);
            //url_webView_2 = ""+ getResources().getString(R.string.url_jkanime);
            //url_webView_1 = ""+ getResources().getString(R.string.url_animeflv);
            //url_webView_3 = ""+ getResources().getString(R.string.url_mangas);
            //url_webView = ""+ getResources().getString(R.string.url_mangas_black_clover);

            //goToUrl(url_webView);
        });

        btnWebView.setOnLongClickListener(view -> {
            Intent intent = new Intent(context, NotificationActivity.class);
            startActivity(intent);

            return false;
        });

        ibtnAddVideo.setOnClickListener(view -> {
            Intent intent = new Intent(this, ResumeActivity.class);
            intent.putExtra("state",0);
            intent.putExtra("new",true);
            startActivityForResult(intent, REQ_CODE_NEW);
        });
        ibtnSearchShow.setOnClickListener(view -> {
            Utils.animateLayout(true, llSearch);
            new Handler().postDelayed(()->{
                view.setVisibility(View.GONE);
                etSearchText.requestFocus(); //Asegurar que editText tiene focus
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearchText, InputMethodManager.SHOW_IMPLICIT);
            }, 470);
        });
        ibtnSearchHide.setOnClickListener(view -> {
            Utils.animateLayout(false, llSearch);
            etSearchText.setText("");
            print(list);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
            new Handler().postDelayed(()->{
                ibtnSearchShow.setVisibility(View.VISIBLE);
            }, 300);
        });

        ibtnSearchSearch.setOnClickListener(view -> {
            Toast.makeText(this, "Aqui Buscar", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pusherTask!=null)
            pusherTask.pusherDiscontect();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (context !=null)
            tvCount.setText(""+AListDB.getInstance(context).getVideoSizeDB());

        intentFilter3 = new IntentFilter(Sets.STRING_BROADCAST_INTERNET);
        registerReceiver(receiver3, intentFilter3);
    }

    private IntentFilter intentFilter3;
    private BroadcastReceiver receiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Bundle extras = intent.getExtras();
                if (extras.containsKey("internet")){
                    String idOper = extras.getString("internet");
                    tvOnline.setText(""+idOper);
                }

                if (context != null){
                    String notific  = extras.containsKey("notific") ?extras.getString("notific"):"";
                    if (!notific.isEmpty())
                        System.out.println("<><> "+notific);
                }


            }catch(Exception e){e.printStackTrace();}
        }
    };

    private void print(List<VideoModel> LIST){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getSimpleCallback(new InterfaceSwipe() {
            @Override
            public void SWIPE_RIGHT(int position) {// Borrar //actualizar capitulo

                try{
                    Toast.makeText(context, "Actualizado...", Toast.LENGTH_SHORT).show();
                    VideoModel item = adapter.getItemFromPosition(position);//LIST.get(position);

                    String date = Utils.getToday(Sets.FORMAT2);
                    long epoch = Utils.dateToEpoch(Sets.FORMAT2, date);


                    int cap = Integer.parseInt(item.getCapitule()) - 1;
                    item.setCapitule(String.valueOf(cap));
                    item.setDone(false);
                    //item.setDate( String.valueOf(epoch));
                    int idInfoVideo = item.getId();
                    item.setCapitule(""+cap);

                    adapter.notifyItemChanged(position, item);
                    adapter.notifyDataSetChanged();
                    //TODO: BD
                    AListDB.getInstance(context).updateVideo(item, String.valueOf(idInfoVideo));
                } catch (Exception e) {  e.printStackTrace();   }

            }

            @Override
            public void SWIPE_LEFT(int position) {// visto

                try {
                    Toast.makeText(context, "Actualizado (Visto)...", Toast.LENGTH_SHORT).show();
                    VideoModel item = adapter.getItemFromPosition(position);//LIST.get(position);

                    String date = Utils.getToday(Sets.FORMAT2);
                    long epoch = Utils.dateToEpoch(Sets.FORMAT2, date);

                    int cap = Integer.parseInt(item.getCapitule()) + 1;
                    item.setCapitule(String.valueOf(cap));
                    //item.setDone(true);
                    item.setDate(String.valueOf(epoch));
                    int idInfoVideo = item.getId();
                    item.setCapitule(""+cap);

                    adapter.notifyItemChanged(position, item);
                    adapter.notifyDataSetChanged();
                    //TODO: BD
                    AListDB.getInstance(context).updateVideo(item, String.valueOf(idInfoVideo));
                } catch (Exception e) {  e.printStackTrace();  }

            }

                   }));
        recyclerView.setOnFlingListener(null);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter = new RecyclerVideoAdapter(context, LIST);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    /** HERE forResult **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bundle extras = data.getExtras();
            VideoModel vm;

            if (requestCode == REQ_CODE_EDIT && resultCode == RESULT_OK) {
                if (extras != null){
                    //int state = extras.getInt("state", -1);
                    //getList();
                    //getListNews();
                    //print(list);
                    new Handler().postDelayed(this::recreate,2500);

                }else{ Log.e(TAG, "data null"); }
            }
            if (requestCode == REQ_CODE_NEW && resultCode == RESULT_OK) {
                if (extras != null){
                    //String dd = Utils.getToday(FormatDate.clasic_long);
                    //long date = Utils.dateToEpoch(FormatDate.clasic_long, dd);
                    //SharedPreferences settingsImage = getSharedPreferences(getString(R.string.shared_key_image), 0);
                    //SharedPreferences.Editor editorImage = settingsImage.edit();

                    int state = extras.getInt("state", -1);

                        //getList();
                        //getListNews();
                        //print(list);
                    new Handler().postDelayed(this::recreate,2000);

                    /*
                    String name = extras.getString("name", "N/A");
                    int cap = extras.getInt("cap", 0);
                    String tag = extras.getString("tag","");
                    String idImage = extras.getString("id_image", "N/A");
                    String image = settingsImage.getString(idImage, "");


                    vm = new VideoModel();
                    vm.setId(state);
                    vm.setName(name);
                    vm.setCapitule(""+cap);
                    vm.setId_capitule(cap);
                    vm.setImage(image);
                    vm.setDate(date);
                    vm.setTag(tag);

                    list.add(vm);
                    Collections.sort(list, (o1, o2)->{
                        long d1 = o1.getDate();
                        long d2 = o2.getDate();
                        return Long.compare(d1, d2);
                    });
                    adapter.refresh(list);
                    */
                }else{ Log.e(TAG, "data null"); }
            }




        } catch (Exception e) {  e.printStackTrace(); }
    }


    /** Here swipe with image **/
    private ItemTouchHelper.SimpleCallback getSimpleCallback( InterfaceSwipe interfaceSwipe){
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();


                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        interfaceSwipe.SWIPE_LEFT(position);

                        break;
                    case ItemTouchHelper.RIGHT:
                        interfaceSwipe.SWIPE_RIGHT(position);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.color_done))
                        .addSwipeLeftActionIcon(R.drawable.ic_done_white_24dp)

                        .addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.color_substract))
                        .addSwipeRightActionIcon(R.drawable.ic_substract_1_24)
                        //.addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.color_add_cap))
                        //.addSwipeRightActionIcon(R.drawable.ic_exposure_plus_1_24)
                        //.addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.color_delete))
                        //.addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)

                        .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(backGroundNotif1 != null && backGroundNotif1.getStatus() != AsyncTask.Status.FINISHED){
        //    backGroundNotif1.cancel(true);
        //}
    }

    private void generateNotifications(){
        try{
        List<VideoModel> listVideo = new ArrayList<>();
        listNews = new ArrayList<>();

            String dateToday = Utils.getToday(FormatDate.clasic_short);
            String dateVideo = dateToday;
            long _dateVideo = 0;

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
                    _dateVideo = Long.parseLong(vm.getDate());
                    dateVideo = Utils.epochToDate(_dateVideo, FormatDate.clasic_short);


                if ( (!dateToday.equals(dateVideo)) ){
                    String CONTENT = "\n-Tipo: "+vm.getType() +"\n-Estado: "+vm.getState() +"\n-Color: "+vm.getColor();
                    String contentInfo = "" + vm.getCapitule();//"info adicional"

                    if (!vm.getImage().isEmpty()){
                        Bitmap bm = Utils.base64ToBitmap(vm.getImage().replace("data:image/jpeg;base64,",""));
                        if (bm!=null){
                            bm = Utils.getResizedBitmap(bm, 600, 400);
                            NotificationsTask.createNotificacions(context, "Videos del día "+DAY,"*Cap. "+vm.getCapitule()+" de " + vm.getTitle()+", "+CONTENT, bm,""+contentInfo,vm.getId());
                        }else{
                            Log.i(TAG, "No image");
                            NotificationsTask.createNotificacions(context, "Videos de: " + DAY, "-Cap. "+vm.getCapitule()+" de " + vm.getTitle()+", "+CONTENT, "info adicional", vm.getId());
                        }
                    }
                    else {
                        Log.i(TAG, "No image");
                        NotificationsTask.createNotificacions(context, "Videos de: " + DAY, "-> Cap. "+vm.getCapitule()+" de " + vm.getTitle()+", "+CONTENT, "info adicional", vm.getId());
                    }
                }else{
                    Toast.makeText(context, "No Hay videos para hoy...", Toast.LENGTH_SHORT).show();
                }

            }
        }else{
            Toast.makeText(context, "No hay videos para hoy", Toast.LENGTH_LONG).show();
        }
    } catch (Exception e) {  e.printStackTrace(); }

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

    private List<VideoModel> getSimulateDayList(int index) {
        List<VideoModel> listResp = new ArrayList<>();
        List<VideoModel> listVideo = new ArrayList<>();

        try{
            listVideo.addAll(AListDB.getInstance(context).getVideo(AListDB.table_date));

            String DAY = Sets.DAYS_SPINNER[index];//Utils.getToday("F");
            DAY = Utils.cleanString(DAY.toLowerCase());
            Log.e(TAG, "xxx "+DAY);

            for (VideoModel vm: listVideo){
                String id = vm.getImage();


                int ind = Integer.parseInt(vm.getContent().split("-")[2]) ;
                String dayVideo = (""+vm.getDay()).equals("null") ? Sets.DAYS_SPINNER[ind] : (""+vm.getDay());
                dayVideo = Utils.cleanString(dayVideo.toLowerCase());

                String stateVideo = vm.getState().toLowerCase();
                stateVideo = Utils.cleanString(stateVideo);

                String STATE = Sets.STATES[Sets.STATES.length - 1].toLowerCase();
                STATE = Utils.cleanString(STATE);

                //limitado por: Dia y por Estado != finalizado
                if ((DAY.equals(dayVideo)) && (!stateVideo.equals(STATE))){
                    listResp.add(vm);
                }

            }

        } catch (Exception e) {  e.printStackTrace();  }

        return listResp;
    }


    public static void lastItemRV(){
        try {
            recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }




}