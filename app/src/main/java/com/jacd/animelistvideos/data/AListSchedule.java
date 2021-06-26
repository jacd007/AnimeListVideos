package com.jacd.animelistvideos.data;

import android.provider.BaseColumns;

/**
 * Created by zippyttech on 21/08/17.
 */

public class AListSchedule {


    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String LONG_TYPE = "SIGNED BIGINT";
    private static final String COMMA_SEP = ",";

    /**
     * Create tables of data base
     */
    public static final String SQL_CREATE_VIDEO =
            "CREATE TABLE " +  Videos.TABLE_NAME + " (" +
                    Videos._ID + " INTEGER PRIMARY KEY," +
                    Videos.COLUMN_NAME_ID     + INTEGER_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_TITLE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_CAPITULE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_SEASON + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_IMAGE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_STATE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_DAY + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_TYPE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_COLORS + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_DATE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_DONE + TEXT_TYPE  +  COMMA_SEP +
                    Videos.COLUMN_NAME_CONTENT  + TEXT_TYPE     +
                    ")";

    public static final String SQL_CREATE_IMAGES=
            "CREATE TABLE " +  Images.TABLE_NAME + " (" +
                    Images._ID + " INTEGER PRIMARY KEY," +
                    Images.COLUMN_NAME_ID + INTEGER_TYPE  +  COMMA_SEP +
                    Images.COLUMN_NAME_TITLE + TEXT_TYPE     + COMMA_SEP +
                    Images.COLUMN_NAME_IMAGE + TEXT_TYPE     + COMMA_SEP +
                    Images.COLUMN_NAME_DATE + TEXT_TYPE     + COMMA_SEP +
                    Images.COLUMN_NAME_HISTORY + TEXT_TYPE     + COMMA_SEP +
                    Images.COLUMN_NAME_URI + TEXT_TYPE     + COMMA_SEP +
                    Images.COLUMN_NAME_TAG + TEXT_TYPE     +
                    ")";




    public static abstract class Videos implements BaseColumns {

        public static final String TABLE_NAME = "Video";

        public static final String COLUMN_NAME_ID= "vId";
        public static final String COLUMN_NAME_TITLE= "vTitle";
        public static final String COLUMN_NAME_CAPITULE= "vCapitule";
        public static final String COLUMN_NAME_SEASON= "vSeason";
        public static final String COLUMN_NAME_IMAGE= "vImage";
        public static final String COLUMN_NAME_STATE= "vState";
        public static final String COLUMN_NAME_DAY= "vDay";
        public static final String COLUMN_NAME_TYPE= "vType";
        public static final String COLUMN_NAME_COLORS= "vColor";
        public static final String COLUMN_NAME_DATE= "vDate";
        public static final String COLUMN_NAME_DONE= "vDone";
        public static final String COLUMN_NAME_CONTENT= "vContent";

    }


    public static abstract class Images implements BaseColumns {

        public static final String TABLE_NAME = "Images";

        public static final String COLUMN_NAME_ID = "vId";
        public static final String COLUMN_NAME_TITLE = "vTitle";
        public static final String COLUMN_NAME_IMAGE = "vImage";
        public static final String COLUMN_NAME_DATE = "vDate";
        public static final String COLUMN_NAME_HISTORY = "vHistory";
        public static final String COLUMN_NAME_URI = "vUri";
        public static final String COLUMN_NAME_TAG = "vTag";

    }





}
