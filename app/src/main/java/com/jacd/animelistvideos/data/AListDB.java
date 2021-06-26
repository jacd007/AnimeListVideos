package com.jacd.animelistvideos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.models.ImageModel;
import com.jacd.animelistvideos.models.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zippyttech on 21/08/17.
 */

public class AListDB {

    private static SQLiteDatabase db;
    private static AListDBHelper mDbHelper;
    private Context context;
    private static final String TAG = "AListDB";

    public static String table_name = AListSchedule.Videos.COLUMN_NAME_TITLE;
    public static String table_id = AListSchedule.Videos.COLUMN_NAME_ID;
    public static String table_date = AListSchedule.Videos.COLUMN_NAME_DATE;

    public AListDB(Context context) {

        mDbHelper = new AListDBHelper(context);
        this.context = context;
    }

    public static AListDB getInstance(Context context){
        return new AListDB(context);
    }

    //-------------


    /**
     * @param tableName  name of table of dataBase to select registers
     * @param projection that specifies which columns from the table of database
     * @param where      Filter results WHERE "qr" = 'My qr'
     * @param args       arguments to where = {"My Caegory"}
     * @return
     */

    public Cursor selectRows(String tableName, String[] projection, String where, String[] args) {

        db = mDbHelper.getReadableDatabase();
/*
        Cursor c = db.query(
                tableName,                                  // The table to query
                projection,                                 // The columns to return
                where,                                      // The columns for the WHERE clause
                args,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );
*/
        String proj = Utils.StringJoiner(", ", projection);
        String argWhere = "";

        if (args.length > 1)
            argWhere = Utils.StringJoiner(", " + args);
        else if (args.length == 1)
            argWhere = args[0];

        String query = "SELECT " + proj + " FROM " + tableName + " " + where + " " + argWhere;

        Cursor c = db.rawQuery(query, null);

        return c;

    }

    /**
     * where and args separated by comma
     *
     * @param tableName
     * @param projection
     * @param where
     * @param args
     * @return
     */

    public Cursor selectRows(String tableName, String projection, String where, String args) {

        db = mDbHelper.getReadableDatabase();
        String query = "SELECT " + projection + " FROM " + tableName + " " + where + " " + args + ";";
        Cursor c = db.rawQuery(query, null);
        return c;

    }

    /**
     * @param tableName name of table of dataBase
     * @param values    values to new register
     */
    public long insertRow(String tableName, ContentValues values) {

        db = mDbHelper.getWritableDatabase();
        long insert = db.insert(tableName, null, values);
        db.close();
        Log.i("DB insert", tableName + ": " + String.valueOf(insert));
        return insert;

    }

    /**
     * Update Tables: where format => id= "01"
     * @param tableName
     * @param value
     * @param where
     * @return
     */

    public long updateRows(String tableName, ContentValues value, String where){

        db = mDbHelper.getWritableDatabase();
        long update= db.update(tableName, value, where, null);
        db.close();
        Log.i("DB update", tableName + ": " + String.valueOf(update));
        return update;
    }

    private long updateRow(String tableName, ContentValues value, String where){

        db = mDbHelper.getWritableDatabase();
        long update= db.update(tableName, value, where, null);
        db.close();
        Log.i("DB update", tableName + ": " + String.valueOf(update));
        return update;
    }


    public void deleteAll(){
        db = mDbHelper.getWritableDatabase();
        String deleteQuery="DELETE FROM "+ AListSchedule.Videos.TABLE_NAME;
        db.execSQL(deleteQuery);
        deleteQuery="DELETE FROM "+ AListSchedule.Images.TABLE_NAME;
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteItemByID(String id){
        db = mDbHelper.getWritableDatabase();
        String deleteQuery="DELETE FROM "+ AListSchedule.Videos.TABLE_NAME + " WHERE "+AListSchedule.Videos.COLUMN_NAME_ID +" = '"+id+"'";
        db.execSQL(deleteQuery);
        deleteQuery="DELETE FROM "+ AListSchedule.Images.TABLE_NAME + " WHERE "+AListSchedule.Images.COLUMN_NAME_ID +" = '"+id+"'";
        db.execSQL(deleteQuery);
        db.close();
    }

    public void eraseAllImagesById(String id){

        db = mDbHelper.getWritableDatabase();
        String deleteQuery="DELETE FROM "+ AListSchedule.Images.TABLE_NAME+
                            " WHERE "+ AListSchedule.Images.COLUMN_NAME_ID +"="+id;
        db.execSQL(deleteQuery);
    }

    public boolean isExistitem(String id){
        db = mDbHelper.getWritableDatabase();
        VideoModel item=null;
        String table_item= AListSchedule.Videos.TABLE_NAME;
        String column_id= AListSchedule.Videos.COLUMN_NAME_ID;


        Cursor c = db.rawQuery("SELECT * FROM " +table_item +" WHERE "+column_id+"=\""+id+"\";", null);
        c.moveToFirst();
        boolean exist=false;

        if (c.moveToFirst()) {
            exist=true;
            do {
               exist=true;

            } while (c.moveToNext());
        }

       return exist;
    }


    /* VIDEO MODEL */

    public void setVideo(List<VideoModel> listVideos) {
        try {
            String table_name = AListSchedule.Videos.TABLE_NAME;

            String column_id = AListSchedule.Videos.COLUMN_NAME_ID;
            String column_title = AListSchedule.Videos.COLUMN_NAME_TITLE;
            String column_cap = AListSchedule.Videos.COLUMN_NAME_CAPITULE;
            String column_season = AListSchedule.Videos.COLUMN_NAME_SEASON;
            String column_image = AListSchedule.Videos.COLUMN_NAME_IMAGE;
            String column_state = AListSchedule.Videos.COLUMN_NAME_STATE;
            String column_day = AListSchedule.Videos.COLUMN_NAME_DAY;
            String column_type = AListSchedule.Videos.COLUMN_NAME_TYPE;
            String column_color = AListSchedule.Videos.COLUMN_NAME_COLORS;
            String column_date = AListSchedule.Videos.COLUMN_NAME_DATE;
            String column_done = AListSchedule.Videos.COLUMN_NAME_DONE;
            String column_content = AListSchedule.Videos.COLUMN_NAME_CONTENT;


            for (VideoModel model
                    :
                    listVideos) {
                ContentValues register = new ContentValues();
                register.put(column_id, model.getId());
                register.put(column_title, model.getTitle());
                register.put(column_cap, model.getCapitule());
                register.put(column_season, model.getSeason());
                register.put(column_image, model.getImage());
                register.put(column_state, model.getState());
                register.put(column_day, model.getDay());
                register.put(column_type, model.getType());
                register.put(column_color, model.getColor());
                register.put(column_date, model.getDate());
                register.put(column_done, String.valueOf(model.getDone()));
                register.put(column_content, model.getContent());

                insertRow(table_name, register);
            }
        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }
    /*
    public void setVideo(VideoModel model) {
        try {
            String table_name = AListSchedule.Videos.TABLE_NAME;

            String column_id = AListSchedule.Videos.COLUMN_NAME_ID;
            String column_title = AListSchedule.Videos.COLUMN_NAME_TITLE;
            String column_cap = AListSchedule.Videos.COLUMN_NAME_CAPITULE;
            String column_season = AListSchedule.Videos.COLUMN_NAME_SEASON;
            String column_image = AListSchedule.Videos.COLUMN_NAME_IMAGE;
            String column_state = AListSchedule.Videos.COLUMN_NAME_STATE;
            String column_day = AListSchedule.Videos.COLUMN_NAME_DAY;
            String column_type = AListSchedule.Videos.COLUMN_NAME_TYPE;
            String column_color = AListSchedule.Videos.COLUMN_NAME_COLORS;
            String column_date = AListSchedule.Videos.COLUMN_NAME_DATE;
            String column_done = AListSchedule.Videos.COLUMN_NAME_DONE;
            String column_content = AListSchedule.Videos.COLUMN_NAME_CONTENT;


                ContentValues register = new ContentValues();
                register.put(column_id, model.getId());
                register.put(column_title, model.getTitle());
                register.put(column_cap, model.getCapitule());
                register.put(column_season, model.getSeason());
                register.put(column_image, model.getImage());
                register.put(column_state, model.getState());
                register.put(column_day, model.getDay());
                register.put(column_type, model.getType());
                register.put(column_color, model.getColor());
                register.put(column_date, model.getDate());
                register.put(column_done, String.valueOf(model.getDone()));
                register.put(column_content, model.getContent());

                insertRow(table_name, register);

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }
    */

    public List<VideoModel> getVideo(@NonNull String AListSchedule_COLUNM) {
        try {
            db = mDbHelper.getWritableDatabase();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        List<VideoModel> Product = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT * FROM "
                        + AListSchedule.Videos.TABLE_NAME
                        +" ORDER BY  LOWER("+ AListSchedule_COLUNM +")", null);
        c.moveToFirst();
        int count = c.getCount();

        if (c.moveToFirst()) {
            VideoModel item;
            do {
                item = new VideoModel();

                item.setId(c.getInt(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_ID)));
                item.setTitle(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TITLE)));
                item.setCapitule(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_CAPITULE)));
                item.setSeason(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_SEASON)));
                item.setImage(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_IMAGE)));
                item.setState(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_STATE)));
                item.setDate(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DAY)));
                item.setType(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_TYPE)));
                item.setColor(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_COLORS)));
                item.setDate(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DATE)));
                item.setDone(Boolean.parseBoolean(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_DONE))));
                item.setContent(c.getString(c.getColumnIndex(AListSchedule.Videos.COLUMN_NAME_CONTENT)));

                Product.add(item);

            } while (c.moveToNext());
        }

        return Product;

    }

    public void updateVideo(VideoModel model, String id) {
        try {
            String table_name = AListSchedule.Videos.TABLE_NAME;

            String column_id = AListSchedule.Videos.COLUMN_NAME_ID;
            String column_title = AListSchedule.Videos.COLUMN_NAME_TITLE;
            String column_cap = AListSchedule.Videos.COLUMN_NAME_CAPITULE;
            String column_season = AListSchedule.Videos.COLUMN_NAME_SEASON;
            String column_image = AListSchedule.Videos.COLUMN_NAME_IMAGE;
            String column_state = AListSchedule.Videos.COLUMN_NAME_STATE;
            String column_day = AListSchedule.Videos.COLUMN_NAME_DAY;
            String column_type = AListSchedule.Videos.COLUMN_NAME_TYPE;
            String column_color = AListSchedule.Videos.COLUMN_NAME_COLORS;
            String column_date = AListSchedule.Videos.COLUMN_NAME_DATE;
            String column_done = AListSchedule.Videos.COLUMN_NAME_DONE;
            String column_content = AListSchedule.Videos.COLUMN_NAME_CONTENT;


            ContentValues register = new ContentValues();
            register.put(column_id, model.getId());
            register.put(column_title, model.getTitle());
            register.put(column_cap, model.getCapitule());
            register.put(column_season, model.getSeason());
            register.put(column_image, model.getImage());
            register.put(column_state, model.getState());
            register.put(column_day, model.getDay());
            register.put(column_type, model.getType());
            register.put(column_color, model.getColor());
            register.put(column_date, model.getDate());
            register.put(column_done, String.valueOf(model.getDone()));
            register.put(column_content, model.getContent());

                updateRow(table_name, register, column_id+" = "+id );

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }

    public int getVideoSizeDB(){
        return getVideo(AListSchedule.Videos.COLUMN_NAME_ID).size();
    }

    public int getIdMaxImage(){
        List<ImageModel> list = getImages();
        int id = 0;
        for (ImageModel im: list){
            id = Math.max(id, im.getId());
        }
        return id;
    }

    public int getIdMaxVideo(){
        List<VideoModel> list = getVideo(AListSchedule.Videos.COLUMN_NAME_ID);
        int id = 0;
        for (VideoModel vm: list){
            id = Math.max(id, vm.getId());
        }
        return id;
    }

    public int getLastIDVideo(){
        List<VideoModel> list = getVideo(table_id);
        int id = 0;
        for (VideoModel vm: list){
            id = Math.max(id, vm.getId());
        }

        return id;
    }


    /* Image MODEL */

    public void setImage(List<ImageModel> list) {
        try {
            String table_name = AListSchedule.Images.TABLE_NAME;

            String column_id = AListSchedule.Images.COLUMN_NAME_ID;
            String column_title = AListSchedule.Images.COLUMN_NAME_TITLE;
            String column_image = AListSchedule.Images.COLUMN_NAME_IMAGE;
            String column_date = AListSchedule.Images.COLUMN_NAME_DATE;
            String column_history = AListSchedule.Images.COLUMN_NAME_HISTORY;
            String column_uri = AListSchedule.Images.COLUMN_NAME_URI;
            String column_tag = AListSchedule.Images.COLUMN_NAME_TAG;

            for (ImageModel vm
                    :
                    list) {
                ContentValues register = new ContentValues();
                register.put(column_id, vm.getId());
                register.put(column_title, vm.getTitle());
                register.put(column_image, vm.getImage());
                register.put(column_date, vm.getDate());
                register.put(column_history, vm.getHistory());
                register.put(column_uri, vm.getUri());
                register.put(column_tag, vm.getTag());

                insertRow(table_name, register);
            }
        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }
    /*
    public void setImage(ImageModel model) {
        try {
            String table_name = AListSchedule.Images.TABLE_NAME;

            String column_id = AListSchedule.Images.COLUMN_NAME_ID;
            String column_title = AListSchedule.Images.COLUMN_NAME_TITLE;
            String column_image = AListSchedule.Images.COLUMN_NAME_IMAGE;
            String column_date = AListSchedule.Images.COLUMN_NAME_DATE;
            String column_history = AListSchedule.Images.COLUMN_NAME_HISTORY;
            String column_uri = AListSchedule.Images.COLUMN_NAME_URI;
            String column_tag = AListSchedule.Images.COLUMN_NAME_TAG;


                ContentValues register = new ContentValues();
                register.put(column_id, model.getId());
                register.put(column_title, model.getTitle());
                register.put(column_image, model.getImage());
                register.put(column_date, model.getDate());
                register.put(column_history, model.getHistory());
                register.put(column_uri, model.getUri());
                register.put(column_tag, model.getTag());

                insertRow(table_name, register);

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }
    */

    public List<ImageModel> getImages() {
        try {
            db = mDbHelper.getWritableDatabase();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        List<ImageModel> Product = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT * FROM "
                        + AListSchedule.Images.TABLE_NAME
                        +" ORDER BY  LOWER("+ AListSchedule.Images.COLUMN_NAME_TITLE +")", null);
        c.moveToFirst();
        int count = c.getCount();

        if (c.moveToFirst()) {
            ImageModel item;
            do {
                item = new ImageModel();

                item.setId(c.getInt(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_ID)));
                item.setTitle(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_TITLE)));
                item.setImage(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_IMAGE)));
                item.setDate(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_DATE)));
                item.setHistory(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_HISTORY)));
                item.setUri(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_URI)));
                item.setTag(c.getString(c.getColumnIndex(AListSchedule.Images.COLUMN_NAME_TAG)));

                Product.add(item);

            } while (c.moveToNext());
        }

        return Product;

    }

    public void updateImage(ImageModel model, String id) {
        try {
            String table_name = AListSchedule.Images.TABLE_NAME;

            String column_id = AListSchedule.Images.COLUMN_NAME_ID;
            String column_title = AListSchedule.Images.COLUMN_NAME_TITLE;
            String column_image = AListSchedule.Images.COLUMN_NAME_IMAGE;
            String column_date = AListSchedule.Images.COLUMN_NAME_DATE;
            String column_history = AListSchedule.Images.COLUMN_NAME_HISTORY;
            String column_uri = AListSchedule.Images.COLUMN_NAME_URI;
            String column_tag = AListSchedule.Images.COLUMN_NAME_TAG;


            ContentValues register = new ContentValues();
            register.put(column_id, model.getId());
            register.put(column_title, model.getTitle());
            register.put(column_image, model.getImage());
            register.put(column_date, model.getDate());
            register.put(column_history, model.getHistory());
            register.put(column_uri, model.getUri());
            register.put(column_tag, model.getTag());

                updateRow(table_name, register, column_id+" = "+id);

        } catch (Exception e) {
            Log.e(TAG, "ocurrio un error insertando registros en db");
            e.printStackTrace();
        }
    }

    public int getImageSizeDB(){
        return getImages().size();
    }


}
