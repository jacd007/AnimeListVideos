package com.jacd.animelistvideos.ui.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.adapter.RecyclerFileAdapter;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.data.AListDB;
import com.jacd.animelistvideos.models.VideoModel;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {

    private Context context;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    private EditText etFile;
    private Button btSave;
    private Button btRead;
    private Button btnGetDB;
    private Switch aSwitch;
    private static final String FILE_NAME = "data_alist_videos_not_image.txt";

    private  List<String> list;
    private RecyclerFileAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        context = FileManagerActivity.this;
        initComponent();
    }

    private void initComponent(){
        list = new ArrayList<>();
        settings = context.getSharedPreferences(context.getResources().getString(R.string.shared_key), 0);
        editor = settings.edit();

        etFile = findViewById(R.id.etFile);
        btSave = findViewById(R.id.btSave);

        recyclerView = findViewById(R.id.rv_file_manager);
        aSwitch = findViewById(R.id.sw_enable_edittext);
        btnGetDB = findViewById(R.id.btn_getdb);
        btnGetDB.setOnClickListener(this::getTextFromDB);

        print(list, aSwitch.isChecked());
        aSwitch.setOnCheckedChangeListener((compoundButton, b)->{
            //etFile.setEnabled(b);
            print(list, b);
        });


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etFile.getText().toString().isEmpty()){
                    Toast.makeText(context, "No puedes guardar un texto vacio.", Toast.LENGTH_SHORT).show();
                }else{
                    saveFile();
                }

            }
        });
        btRead = findViewById(R.id.btRead);
        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile();
            }
        });
    }

    private void saveFile(){
        String textoASalvar = etFile.getText().toString();
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fileOutputStream.write(textoASalvar.getBytes());

            Toast.makeText(this, "Documento guardado...", Toast.LENGTH_SHORT).show();
            etFile.setText("");
            list = new ArrayList<>();
            print(list, aSwitch.isChecked());
            editor.putString("archivo", getFilesDir() + "/" + FILE_NAME);
            editor.commit();
            getExportFile(getFilesDir() + "/" + FILE_NAME);
            Log.d("TAG1", "Fichero Salvado en: " + getFilesDir() + "/" + FILE_NAME);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileOutputStream != null){
                try{
                    fileOutputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void getExportFile(String stringDir) {
        Log.e("ss","");
    }

    private void readFile(){
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = openFileInput(FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while((lineaTexto = bufferedReader.readLine())!=null){
                stringBuilder.append(lineaTexto).append("\n");
            }

            etFile.setText(stringBuilder);
            Utils.copyClipboard(context, etFile.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileInputStream !=null){
                try {
                    fileInputStream.close();
                }catch (Exception e){

                }
            }
        }

        String text = etFile.getText().toString();
        try {
            list = new ArrayList<>();
            List<VideoModel> modelList = new ArrayList<>();
            JSONArray array = new JSONArray(text);
            for (int i = 0; i < array.length(); i++) {
                String item = array.getString(i);
                VideoModel vm = new Gson().fromJson(item, VideoModel.class);
                list.add(vm.getTitle());
                modelList.add(vm);
            }

            print(list, aSwitch.isChecked());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG1","fallo la carga....");
        }
    }

    public void getTextFromDB(View view) {
        try {
            List<VideoModel> videoModelList = new ArrayList<>();
            List<VideoModel> data = new ArrayList<>();
            list = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            String text = "";

            videoModelList.addAll(AListDB.getInstance(context).getVideo(AListDB.table_name));

            for (VideoModel vm: videoModelList){
                data.add(vm);
                list.add(vm.getTitle());
            }

            for (VideoModel vm: videoModelList){
                vm.setImage("N/A");
                data.add(vm);
            }

            /*
            for (int i=0; i<videoModelList.size(); i++){
                stringBuilder.append(text).append(new Gson().toJson(videoModelList.get(i))).append("\n");
            }*/
            text = new Gson().toJson(data);
            etFile.setText(text);

            print(list, aSwitch.isChecked());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void print(List<String> list, boolean showIndex) {
        adapter = new RecyclerFileAdapter(context, list, 2, showIndex);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }


}