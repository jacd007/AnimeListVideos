package com.jacd.animelistvideos.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.adapter.MyAdapter;
import com.jacd.animelistvideos.adapter.MyAdapterText;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Sets;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.ImageModel;
import com.jacd.animelistvideos.models.SpnModel;
import com.jacd.animelistvideos.models.VideoModel;
import com.jacd.animelistvideos.ui.dialogs.DialogUtils;
import com.yalantis.ucrop.UCrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class ResumeActivity extends AppCompatActivity {

    private static final String TAG = "ResumeActivity";
    private static final String auxB64 = "data:image/jpeg;base64,";
    private Context mContext;
    private Activity activity;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private SharedPreferences settingsImage;
    private SharedPreferences.Editor editorImage;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;

    private FloatingActionButton fabGetImage, fabEdit, fabDelete;
    private EditText etTitleVideo, etCapVideo, etSeasonVideo;
    private ImageView ivImage;
    private Button btnSave;

    private Spinner spnStatus, spnNextDay, spnColor, spnType;
    private TextView tvDate;
    private Switch swDone;

    private MyAdapter myAdapter;

    private static int COUNT;
    private Bitmap bitmap, bitmap_original;
    private Uri mUri, mUri_original, mUri_aux;
    private int cap=1;

    private ArrayList<SpnModel> listSpn;
    private int ID_VID_EDIT;
    private boolean NEW_ITEM = true;
    private static String tagv;

    private VideoModel videoModel = new VideoModel();
    private ImageModel imageModel = new ImageModel();

    private boolean[] not_double_click;
    //private String date;
    private long epoch;
    private boolean dateModific;
    private Calendar c;
    private int day, month, year, hour, minute;
    private String date_aux, hour_aux, dateReference, AM_PM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        not_double_click = new boolean[]{true,true,true,true};
        settings = getSharedPreferences(getString(R.string.shared_key), 0);
        editor = settings.edit();
        dateModific = false;
        getDataBundle();
        initComponent();
        getDate();


        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
    }

    private void getDataBundle() {

        try {
            boolean val = false;
            int POSITION = 0;
            Bundle parametros = this.getIntent().getExtras();
            List<VideoModel> listVideo = new ArrayList<>();
            List<ImageModel> listImage = new ArrayList<>();

            listVideo.addAll(AListDB.getInstance(mContext).getVideo(AListDB.table_id));
            listImage.addAll(AListDB.getInstance(mContext).getImages());
            //Log.e(TAG, "IDmax: >> "+AListDB.getInstance(mContext).getIdMaxVideo());

            if (parametros != null) {
                NEW_ITEM = parametros.getBoolean("new", true);
                tagv = "";

                if (!NEW_ITEM){
                    POSITION = parametros.getInt("position");
                    ID_VID_EDIT = parametros.getInt("idVideo");
                    tagv = parametros.getString("tag");
                    for (VideoModel vm: listVideo){
                        if (vm.getId() == ID_VID_EDIT){
                            videoModel = vm;
                            val=true;
                            break;
                        }
                    }

                    for (ImageModel im: listImage){
                        if (im.getTag().equals(videoModel.getImage())){
                            videoModel.setImage(im.getImage());
                            imageModel = im;
                            break;
                        }
                    }
                 /*  AuxVideo  */
                    String aux = settings.getString("AuxVideo", "");
                    Log.i("",""+val);
                    try {
                        VideoModel vm = new Gson().fromJson(aux, VideoModel.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!val){
                        try {
                            videoModel = new Gson().fromJson(aux, VideoModel.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            } else {
                Log.w(TAG, "parametros nulos");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initComponent() {
        mContext = ResumeActivity.this;
        activity = ResumeActivity.this;

         settingsImage = getSharedPreferences(getString(R.string.shared_key_image), 0);
         editorImage = settingsImage.edit();

        //NEW_ITEM = settings.getBoolean("title_resume", true);
        COUNT = settingsImage.getInt("count",0);
        listSpn = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(NEW_ITEM ?"Nuevo video" : "Editar info del Video");
        //toolBarLayout.setTitle(getTitle());

        tvDate = (TextView) findViewById(R.id.tv_resume_video_date);
        ivImage = (ImageView) findViewById(R.id.resume_iv_image);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
        etTitleVideo = (EditText) findViewById(R.id.et_resume_video_name);
        etCapVideo = (EditText) findViewById(R.id.et_resume_video_capitule);
        etSeasonVideo = (EditText) findViewById(R.id.et_resume_video_season);

        fabGetImage = (FloatingActionButton) findViewById(R.id.fab_image);
        fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        btnSave = (Button) findViewById(R.id.btn_resume_save);

        spnStatus = (Spinner) findViewById(R.id.resume_spn_xx1);
        spnNextDay = (Spinner) findViewById(R.id.resume_spn_xx2);
        spnType = (Spinner) findViewById(R.id.resume_spn_xx3);
        spnColor = (Spinner) findViewById(R.id.resume_spn_xx4);

        swDone = (Switch) findViewById(R.id.sw_resume_video_done);
        swDone.setChecked(false);

        fabEdit.setVisibility(View.VISIBLE);
        fabEdit.setVisibility(View.GONE);
        fabDelete.setVisibility(View.GONE);

        for (int i=0; i<Sets.COLORS.length; i++){
            SpnModel sm = new SpnModel();
            sm.setPosition(i);
            sm.setmColor(Sets.COLORS[i]);
            sm.setTitle(i==0?"No Asignado":"lvl. "+i);
            listSpn.add(sm);
        }

        ArrayAdapter<String> dAdapter;
        View vColors = (View) findViewById(R.id.v_resume_color);
        myAdapter = new MyAdapter(this, 0, listSpn,vColors);
        spnColor.setAdapter(myAdapter);

        MyAdapterText adapter0 = new MyAdapterText(this, Sets.STATES);
        spnStatus.setAdapter(adapter0);

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.TYPES);
        dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        spnType.setAdapter(dAdapter);

        dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.DAYS);
        dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        spnNextDay.setAdapter(dAdapter);


        //dAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, Sets.TYPES);
        //dAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //spnType.setAdapter(dAdapter);

        tvDate.setOnClickListener(view -> {
            showDialogs();
        });


        if (!NEW_ITEM)
            getDataEdit(vColors);
    }

    private void getDataEdit(View vColors) {

        swDone.setChecked(videoModel.getDone());

        if (videoModel.getImage().contains("http")){
            Glide.with(mContext)
                    .load("" + videoModel.getImage())
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(ivImage);

            Glide.with(this)
                    .asBitmap()
                    .load("" + videoModel.getImage())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            bitmap_original = resource;
                            //String auxName = Utils.getToday(FormatDate.full);


                            //mUri = bitmapToUri(resource, auxName );
                            //mUri_original = bitmapToUri(resource, auxName );
                            //mUri_original = Utils.createImageFileFromBitmap(activity, Utils.getToday(FormatDate.full), bitmap);
                            //mUri = mUri_original;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        }else if(videoModel.getImage().contains("data:image/jpeg;base64,")){

            String ima = videoModel.getImage().replace("data:image/jpeg;base64,", "");
            Bitmap resource = Utils.base64ToBitmap(ima);
            bitmap = resource;
            bitmap_original = resource;
            Glide.with(mContext)
                    .load(resource)
                    .placeholder(R.drawable.ic_baseline_image_search_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(ivImage);
        }


            fabEdit.setVisibility(View.VISIBLE);
            fabGetImage.setVisibility(View.VISIBLE);
            fabDelete.setVisibility(View.VISIBLE);

            etTitleVideo.setText(""+videoModel.getTitle());
            etCapVideo.setText(""+videoModel.getCapitule());
            etSeasonVideo.setText(""+videoModel.getSeason());

            try {
                int poscolor = Integer.parseInt(videoModel.getColor());

                String color = listSpn.get(poscolor).getmColor();
                vColors.setBackgroundColor(Color.parseColor(color));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            try {
                int posStat = Integer.parseInt(videoModel.getContent().split("-")[0]);
                int postype = Integer.parseInt(videoModel.getContent().split("-")[1]);
                int posday = Integer.parseInt(videoModel.getContent().split("-")[2]);
                int poscolor = Integer.parseInt(videoModel.getContent().split("-")[3]);

                spnStatus.setSelection(posStat);
                spnNextDay.setSelection(posday);
                spnType.setSelection(postype);
                spnColor.setSelection(poscolor);
            } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void onStart() {
        super.onStart();

        ivImage.setOnLongClickListener(view -> {
            DialogUtils.getInstance(mContext)
                    .customDialogMessage("Crear Video","¿Desea Crear Este Video?",viewok -> {
                        NEW_ITEM=true;
                        editor.remove("addDB");
                        editor.commit();
                        Toast.makeText(mContext, "Ahora es un Nuevo Video...", Toast.LENGTH_SHORT).show();
                        if (DialogUtils.dialog != null)
                            DialogUtils.dialog.dismiss();
                    },viewcancel -> {

                        if (DialogUtils.dialog != null)
                            DialogUtils.dialog.dismiss();
                    });

            return false;
        });

        fabGetImage.setOnClickListener(view -> {
            if (not_double_click[0]){
                not_double_click[0]=false;
                permissionValidate();
                new Handler().postDelayed(()->{
                    not_double_click[0] = true;
                }, 1500);
            }

        });

        fabEdit.setOnClickListener(view -> {
            if (not_double_click[1]){
                not_double_click[1]=false;
                editImage();
                new Handler().postDelayed(()->{
                    not_double_click[1] = true;
                }, 1500);
            }

        });

        fabDelete.setOnClickListener(view -> {
            if (not_double_click[2]){
                not_double_click[2]=false;
                deleteImage();
                new Handler().postDelayed(()->{
                    not_double_click[2] = true;
                }, 1500);
            }

        });

        btnSave.setOnClickListener(view -> {

            if (not_double_click[3]){
                not_double_click[3]=false;

                Toast.makeText(mContext, "Espere....", Toast.LENGTH_SHORT).show();
                 //date = Utils.getToday(Sets.FORMAT2);
                 //epoch = Utils.dateToEpoch(Sets.FORMAT2, date);
                //String strDate = ""+epoch;
                String strDate = "";
                String image ="";
                try {
                    if (bitmap != null){
                        strDate = "";
                        image = auxB64 + Utils.bitmapToBase64(bitmap);
                    }else{
                        image = Sets.baseNoImage;
                        strDate = "x";
                        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_24dp);
                        //if (b!=null)
                        //    image = auxB64 + Utils.bitmapToBase64(b);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String nameIdImage = "id_"+COUNT;
                editorImage.putString(nameIdImage, image);
                editorImage.putInt("count",COUNT++);
                editorImage.commit();

                String capi = "0"+ etCapVideo.getText().toString();
                int cap = Utils.isNumeric(capi) ?Integer.parseInt(capi) :0;

                String temporada = ""+ etSeasonVideo.getText().toString();
                int temp = Utils.isNumeric(temporada) ?Integer.parseInt(temporada) :0;

                String status = Sets.STATES[spnStatus.getSelectedItemPosition()];
                String type = Sets.TYPES[spnType.getSelectedItemPosition()];
                String day = Sets.DAYS[spnNextDay.getSelectedItemPosition()];
                String color = Sets.COLORS[spnColor.getSelectedItemPosition()];

                //id de referencia para video, infoVideo e imagen
                final String VIDEO_TAG = "Video-"+Utils.getToday(FormatDate.full) +strDate;
                String idStrVideo = Utils.getToday(FormatDate.full);
                long dates = Utils.dateToEpoch(FormatDate.full, idStrVideo);
                epoch = dateModific ?epoch :dates;

                String content = ""+spnStatus.getSelectedItemPosition()
                        +"-"+spnType.getSelectedItemPosition()
                        +"-"+spnNextDay.getSelectedItemPosition()
                        +"-"+spnColor.getSelectedItemPosition()
                        +"-"+VIDEO_TAG;

                int idVideo = AListDB.getInstance(mContext).getIdMaxVideo();
                int idImage = AListDB.getInstance(mContext).getIdMaxImage();

                if (NEW_ITEM){
                    idVideo++;
                    idImage++;

                    List<VideoModel> listVideo = new ArrayList<>();
                    VideoModel vm = new VideoModel();
                    vm.setId(idVideo);
                    vm.setTitle(etTitleVideo.getText().toString());
                    vm.setCapitule(String.valueOf(cap));
                    vm.setSeason(String.valueOf(temp));
                    vm.setImage(image);
                    vm.setState(status);
                    vm.setDay(day);
                    vm.setType(type);
                    vm.setColor(color);
                    vm.setDate(String.valueOf((dateModific) ?epoch :epoch-86400000));
                    vm.setDone(swDone.isChecked());
                    vm.setContent(content);

                    List<ImageModel> listImage = new ArrayList<>();
                    ImageModel imModel = new ImageModel();
                    imModel.setId(idImage);
                    imModel.setTitle(etTitleVideo.getText().toString());
                    imModel.setImage(image);
                    imModel.setDate(String.valueOf(dates));
                    imModel.setHistory("[]");
                    imModel.setUri(""+mUri_original);
                    imModel.setTag(VIDEO_TAG);

                    listVideo.add(vm);
                    listImage.add(imModel);
                    AListDB.getInstance(mContext).setImage(listImage);
                    AListDB.getInstance(mContext).setVideo(listVideo);


                    if (etTitleVideo.getText().toString().length()>0){
                        Intent i = getIntent();
                        i.putExtra("state",1);
                        i.putExtra("name", etTitleVideo.getText().toString());
                        i.putExtra("cap", cap);
                        i.putExtra("id_image", nameIdImage);
                        i.putExtra("tag", VIDEO_TAG);
                        setResult(RESULT_OK, i);
                        ResumeActivity.this.finish();
                    }else{
                        etTitleVideo.setError("Campo requerido...");
                        Toast.makeText(mContext, "Escriba un titulo del video", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //MODO EDICION

                    idImage = imageModel.getId();
                    idVideo = videoModel.getId();
                    //VIDEO
                    VideoModel vm = new VideoModel();
                    vm.setId(videoModel.getId());
                    vm.setTitle(etTitleVideo.getText().toString());
                    vm.setCapitule(String.valueOf(cap));
                    vm.setSeason(String.valueOf(temp));
                    vm.setImage(image);
                    vm.setState(status);
                    vm.setDay(day);
                    vm.setType(type);
                    vm.setColor(color);
                    vm.setDate(String.valueOf(epoch));
                    vm.setDone(swDone.isChecked());
                    vm.setContent(content);

                    //IMAGE
                    //List<String> listx = new ArrayList<>();
                    //listx = new Gson().fromJson(imageModel.getHistory());
                    //listx.add(imageModel.getImage());
                    //listx.add(imageModel.getImage());

                    ImageModel imModel = new ImageModel();
                    imModel.setId(idImage);
                    imModel.setTitle(etTitleVideo.getText().toString());
                    imModel.setImage(image);
                    imModel.setDate(String.valueOf(dates));
                    imModel.setHistory("[]");
                    imModel.setUri(imageModel.getUri());
                    imModel.setTag(imageModel.getTag());

                    AListDB.getInstance(mContext).updateVideo(vm, String.valueOf(idVideo));
                    AListDB.getInstance(mContext).updateImage(imModel, String.valueOf(idImage));

                    //Then, delete the (extra) saved bitmap:
                    if (mUri_aux!=null){
                        this.getContentResolver().delete(mUri_aux, null, null);
                    }


                    //PS: Above code for deletion is for Activity. If you want to delete inside a Fragment, use this:
                    //getActivity().getContentResolver().delete(uri, null, null);


                    Intent i = getIntent();
                    i.putExtra("state",2);

                    setResult(RESULT_OK, i);
                    ResumeActivity.this.finish();
                }

                new Handler().postDelayed(()->{
                    not_double_click[3] = true;
                }, 1500);
            }



        });
    }

    private void getDate(){
         c = Calendar.getInstance();
         day = c.get(Calendar.DAY_OF_MONTH);
         month = c.get(Calendar.MONTH);
         year = c.get(Calendar.YEAR);
         hour = c.get(Calendar.HOUR);
         minute = c.get(Calendar.MINUTE);

    }

    private void showDialogs() {
        c.set(year, month, day, hour, minute,0);
        hour_aux = "00:00:00";

        DatePickerDialog myDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int years, int monthOfYear, int dayOfMonth) {
                day = dayOfMonth;
                month = monthOfYear;
                year = years;
                date_aux = year + "-" + ((month+1)<10?"0"+(month+1):(month+1)) + "-" + (day<10?"0"+day:day);
                dateReference = date_aux + " " + hour_aux;
                changeReferenceDate(dateReference);
                getHours();
            }
        }
                ,year, month, day);
        myDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        myDatePickerDialog.show();
    }

    private void getHours() {

        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                hour_aux = (hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute:minute)+":"+"00";

                AM_PM = hour < 12 ? " a.m." : " p.m.";
                dateReference = date_aux + " " + hour_aux;
                changeReferenceDate(dateReference);
            }
        }, hour, minute, false).show();

        AM_PM = hour < 12 ? " a.m." : " p.m.";
        changeReferenceDate(dateReference);
    }

    private void changeReferenceDate(String strDateReference){
        dateModific = true;
        epoch = Utils.dateToEpoch("yyyy-MM-dd HH:mm:ss", strDateReference);
        tvDate.setText(Utils.reformateDate(strDateReference, "yyyy-MM-dd HH:mm:ss","dd MMMM yyyy'.' hh:mm a"));
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        tvDate.setText(Utils.getToday(FormatDate.clasic_long4));
    }

    private void permissionValidate() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                selectImageFromGalery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Si rechaza el permiso, no puede utilizar este servicio\n" +
                        "\nActive los permisos en [Configuración]> [Permiso]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.w(TAG,">> probando metodo imagen uri");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void selectImageFromGalery() {
        int hh = 500;
        TedBottomPicker.with(ResumeActivity.this)
                .setPeekHeight(hh)
                .showTitle(true)
                .setPreviewMaxCount(300)
                .setTitle("Seleccione una imagen")
                .setCompleteButtonText("Hecho")
                .setEmptySelectionText("Elija una imagen")
                .show(uri -> {
                    try {
                        if (uri != null){
                            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                            ivImage.setImageURI(uri);
                            bitmap_original = bitmap;
                            mUri = uri;
                            mUri_original = uri;
                            fabEdit.setVisibility(View.VISIBLE);
                            fabDelete.setVisibility(View.VISIBLE);
                            fabGetImage.setImageResource(R.drawable.ic_image_swap);
                        }else
                            Toast.makeText(mContext, "No se pudo traer la imagen...", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
    }

    public void editImage( ) {
        if (bitmap_original!=null){
            if(!NEW_ITEM && mUri_original==null){
                mUri_aux = getImageUri(mContext, bitmap_original); //todo: ojo este metodo me guarda una imagen en /pictures
                mUri_original = mUri_aux;
            }
            starCrop(mUri_original);
            /*
            if (mUri_original!=null)
                starCrop(mUri_original);
            else{
                String date = Utils.getToday(FormatDate.full);
                String FILENAME = "image_"+Utils.dateToEpoch(FormatDate.full, date)+".png";
                String PATH = "/mnt/sdcard/"+ FILENAME;
                File f = new File(PATH);
                Uri yourUri = Uri.fromFile(f);
                starCrop(yourUri);
            }
            */
        }else {
            Toast.makeText(mContext, "Primero seleccione una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteImage( ) {
        bitmap=null;
        bitmap_original=null;
        mUri = null;
        mUri_original=null;
        ivImage.setImageResource(R.drawable.ic_done_white_24dp);
        fabDelete.setVisibility(View.GONE);
        fabEdit.setVisibility(View.GONE);
        fabGetImage.setImageResource(R.drawable.ic_add_image);
    }


    private void starCrop(@NonNull Uri uri) {
        String destinationFileName = "image"+ Utils.getToday("ssmmHHddMMyy")+".jpeg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        //uCrop.withAspectRatio(1, 1);
        try{
            Bitmap b = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            int ww = b.getWidth();
            int hh = b.getHeight();
            uCrop.withMaxResultSize(ww, hh);
            //uCrop.withAspectRatio(16, 9);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uCrop.withOptions(getCropOption());
        uCrop.start(ResumeActivity.this);

    }

    private UCrop.Options getCropOption() {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(100);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.withMaxResultSize(1024, 1024);

        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        options.setToolbarTitle("Editar imagen");

        return options;
    }


    public Uri bitmapToUri(Bitmap bitmap, String nameImage){
        // save bitmap to cache directory
        try {

            File cachePath = new File(mContext.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath +"/"+ (nameImage.replaceAll(" ","_")).trim() +".png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(mContext.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.jacd.animelistvideos.fileprovider", newFile);

            return contentUri;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            mUri = resultUri;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mUri);
                ivImage.setImageURI(mUri);
            }  catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    //Copy to clipboard
    public void copyClipboard(View view) {
        String text = etTitleVideo.getText().toString().trim();
        if (!text.isEmpty()){
            Utils.copyClipboard(mContext, text);
        }else{
            Toast.makeText(mContext, "No puede copiar un texto vacio.", Toast.LENGTH_SHORT).show();
        }

    }

    //Sustract one to id capitule
    public void capituleSubstract(View view) {
        String cap_str = etCapVideo.getText().toString().trim();
        int cap = cap_str.isEmpty() ? 0 : Integer.parseInt(cap_str) ;

        if (cap>0){
            cap--;
            etCapVideo.setText(String.valueOf(cap));
        }

    }

    //Add one to id capitule
    public void capituleAdded(View view) {
        String cap_str = etCapVideo.getText().toString().trim();
        int cap = cap_str.isEmpty() ? 0 : Integer.parseInt(cap_str) ;

        if (cap<=99999){
            cap++;
            etCapVideo.setText(String.valueOf(cap));
        }
    }

    //Sustract one to id season
    public void seasonSubstract(View view) {
        String cap_str = etSeasonVideo.getText().toString().trim();
        int cap = cap_str.isEmpty() ? 0 : Integer.parseInt(cap_str) ;

        if (cap>0){
            cap--;
            etSeasonVideo.setText(String.valueOf(cap));
        }

    }

    //Add one to id season
    public void seasonAdded(View view) {
        String cap_str = etSeasonVideo.getText().toString().trim();
        int cap = cap_str.isEmpty() ? 0 : Integer.parseInt(cap_str) ;

        if (cap<=99999){
            cap++;
            etSeasonVideo.setText(String.valueOf(cap));
        }
    }
}