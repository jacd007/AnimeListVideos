package com.jacd.animelistvideos.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class TimeRange {

    public static boolean getRange(String x_target, String x_start, String x_end){
        String formate = "HH:mm:ss";
        //System.out.println(x_start+" <= "+x_target+" <= "+x_end);

        long st = Utils.dateToEpoch(formate, x_start)/1000;
        long tt = Utils.dateToEpoch(formate, x_target)/1000;
        long et = Utils.dateToEpoch(formate, x_end)/1000;

        //System.out.println(st+" <= "+tt+" <= "+et);

        boolean r1 = st < tt;
        boolean r2 = tt < et;

        //System.out.println(r1+" <- X -> "+r2);
        //System.out.println("return: "+(r1 && r2));
        if ((r1 && r2)) System.out.println("(" + x_start + " <= " + x_target + " <= " + x_end + ") -> return: true");
        return r1 && r2;
    }

}
