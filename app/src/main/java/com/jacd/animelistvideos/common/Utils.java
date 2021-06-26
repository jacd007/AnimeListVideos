package com.jacd.animelistvideos.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
;

import com.jacd.animelistvideos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {


    private static String CHANNEL_ID = "12";

    public static String StringJoiner(String delimiter, String... args) throws NullPointerException {
        String response = "";
        int i = 0;
        for (String value :
                args) {

            if (args.length > 1)
                response = i <= args.length - 2 ? response + value + delimiter : response + value;

            i++;
        }
        return response;
    }

    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

    public static boolean checkConnectivity(Context context) {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {
            Toast.makeText(context, "Revise su conexión a Internet...", Toast.LENGTH_SHORT).show();

            enabled = false;
        }
        return enabled;
    }

    public static boolean isHourInIntervals(String target, String _start, String end_) {
        String aux1 = "23:59:59";
        String aux2 = "00:00:00";

        long e_aux = dateToEpoch("HH:mm:ss", aux1);
        long e_aux2 = dateToEpoch("HH:mm:ss", aux2);

        long e_targed = dateToEpoch("HH:mm:ss", target);
        long e_start = dateToEpoch("HH:mm:ss", _start);
        long e_end = dateToEpoch("HH:mm:ss", end_);

        boolean r1 = (e_targed >= e_start) && (e_targed <= e_aux);
        boolean r2 = (e_targed >= e_aux2) && (e_targed <= e_end);

        //System.out.println(target+"-> ["+_start+" , "+aux1+"]" +" = "+ r1);
        //System.out.println(target+"-> ("+aux1+" , "+end_+"]" +" = "+ r2);

        return r1 || r2;
    }



    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static TextView subRayado(TextView textView){
        SpannableString mitextoU = new SpannableString(textView.getText());
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        textView.setText(mitextoU);
        return textView;
    }

    public interface LoadDialogInterface {
        void onLoadDialog(boolean error);

    }

    public static void copyClipboard(Context context, String text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text",  text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copiado al portapapeles.", Toast.LENGTH_SHORT).show();
    }

    public static void createFileTxt(String nameFolder, String nameFile, String DATA){
        FileOutputStream fileOutputStream = null;
        try {
            nameFile = nameFile.replace("null","");
            nameFolder = nameFolder.replace("null","");

            String NAME_FOLDER = (""+nameFolder).isEmpty() ? "AListVideosData" : nameFolder;
            String NAME_FILE = (""+nameFile).isEmpty() ? "ArchivoAlisVideo.txt" : nameFile;


            File nuevaCarpeta = new File(Environment.getExternalStorageDirectory(), ""+NAME_FOLDER);
            if (!nuevaCarpeta.exists()) {
                nuevaCarpeta.mkdir();
            }
            try {
                File file = new File(nuevaCarpeta, ""+NAME_FILE);
                file.createNewFile();
            } catch (Exception ex) {
                Log.e("Error", "ex: " + ex);
            }
        } catch (Exception e) {
            Log.e("Error", "e: " + e);
        }
    }

    public static File createImageFile(Activity activity, String imageFileName  ) {
        File mFileTemp = null;
        String root=activity.getDir("my_sub_dir",Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }

    public static Uri createImageFileFromBitmap(Activity activity, String imageFileName, Bitmap currentImage){
        Uri uri = null;
        File file = createImageFile(activity, imageFileName);
        if (file != null) {
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(file);
                currentImage.compress(Bitmap.CompressFormat.PNG, 70, fout);
                fout.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uri=Uri.fromFile(file);
        }
        return uri;
    }

    public static void animateLayout(boolean show_or_hide, LinearLayout layoutAnimate){
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (show_or_hide){
            //desde la  derecha a izquierda
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        }
        else{    //desde la  izquierda a la derecha
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        }
        //duración en milisegundos
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        layoutAnimate.setLayoutAnimation(controller);
        layoutAnimate.startAnimation(animation);
        layoutAnimate.setVisibility(show_or_hide? View.VISIBLE: View.GONE);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    /**
     * Verify string is numeric
     *
     * @param strNum
     * @return
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String setHTMLMessageNotification(String title, String origin, String destination, String time, String coordinates, String env, String type, String user, String email, String price, String distance, String phone) {

        String line = "<br>";
        String head = " <h1 style=\"background-color:Orange;\">" + title + "</h1> " + line;
        String usr = "<strong>Usuario: </strong>" + user + line;
        String ema = "<strong>Correo Electrónico: </strong>" + email + line;
        String ori = "<strong>Desde: </strong>" + origin + line;
        String des = "<strong>Hasta: </strong>" + destination + line;
        String tim = "<strong>Fecha y Hora: </strong>" + time + line;
        String prc = "<strong>Precio: </strong>" + price + line;
        String dst = "<strong>Distancia: </strong>" + distance + line;
        String telf = "<strong>Teléfono: </strong>" + phone + line;
        String en = "<strong>Entorno: </strong>" + env + line;
        String imMap = "<br><br><img width=\"600\" src=\"https://maps.googleapis.com/maps/api/staticmap?center=" + origin.replaceAll(" ", "+") + "&zoom=13&scale=1&size=600x300&maptype=roadmap&key=AIzaSyAIiXVbt3z9zRyjpAW2-b7eB9JIgWP7PGI&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff965c%7Clabel:O%7C" + origin.replaceAll(" ", "+") + "&markers=size:mid%7Ccolor:0x3f66ff%7Clabel:D%7C" + destination.replaceAll(" ", "+") + "\" alt=\"Google Map VE\">";


        String coord = "https://www.google.com/maps/dir/?api=1&origin=" + origin.replaceAll(" ", "+") + "&destination=" + destination.replaceAll(" ", "+") + "&travelmode=car";
        String coord2 = "<a href=\"" + coord + "\">Ver en Google Maps</a>";
        return head + usr + ema + telf + ori + des + tim + prc + dst + coord2 + imMap;
    }



    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validate Email
     *
     * @param emailStr
     * @return
     */

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }


    /**
     * Get Date in format dd-MM-yyyy or format required
     *
     * @param indate
     * @param outputDFormat
     * @return
     */
    public static String getDateDay(String indate, String outputDFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date1 = sdf.parse(indate);
            outputDFormat = outputDFormat == null || outputDFormat.isEmpty() ? "dd-MM-yyyy" : outputDFormat;
            SimpleDateFormat time = new SimpleDateFormat(outputDFormat);
            String time_s = time.format(date1);
            return time_s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    public static Date StringToDate(String format, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static double formatTwoDecimal(double value){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%(,.2f", value);
        // System.out.println("Amount is - " + sb);

        String parsed_val = sb.toString().replace(".", "&");
        parsed_val = parsed_val.replaceAll(",", ".");
        parsed_val = parsed_val.replaceAll("&", ",");
       return Double.parseDouble(parsed_val);
    }

    public static String formatDecimal(double value) {
        //  DecimalFormat df = new DecimalFormat("#,##");
        //  return df.format(value);


        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%(,.2f", value);
        // System.out.println("Amount is - " + sb);

        String parsed_val = sb.toString().replace(".", "&");
        parsed_val = parsed_val.replaceAll(",", ".");
        parsed_val = parsed_val.replaceAll("&", ",");

        return parsed_val;
    }

    public static String epochToDate(long epoch, String formatOut){
        SimpleDateFormat sdf;
        if (formatOut == null)
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        else
            sdf = new SimpleDateFormat(formatOut + "");

        Date dt = null;
        try {

            sdf = new SimpleDateFormat(formatOut);
            return sdf.format(new Date(epoch));


        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }


    }

    public static long dateToEpoch(String format, String date) {
        SimpleDateFormat sdf;
        if (format == null)
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        else
            sdf = new SimpleDateFormat(format + "");


        Date dt = null;
        try {


            dt = sdf.parse(date);
            long epoch = dt.getTime();
            int num = (int) (epoch / 1000);
            return epoch;


        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }


    }

    /**
     * Verify if is valid email
     *
     * @param email
     * @return
     */

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    /**
     * Convert a text file to String
     *
     * @param is
     * @return
     * @throws IOException
     */
//InputStream is = getResources().getAssets().open("SQLScript.sql");
//String sql= convertStreamToString(is);
    public static String convertStreamToString(InputStream is)
            throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        String text = writer.toString();
        return text;
    }

    public static String getToday(String format) {
        String f = format != null ? format : "yyyy/MM/dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(f);
        Date date = new Date();
        return dateFormat.format(date);

    }

    public static long getTodayEpoch(String format) {
        String f = format != null ? format : "yyyy/MM/dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(f);
        Date date = new Date();
        String ddddd = dateFormat.format(date);
        return dateToEpoch(format, ddddd);
    }

    public static String reformateDate(String strDate, String originFormat, String newFormat) {
        Date date = null;
        String resp = null;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseador = new SimpleDateFormat(originFormat);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formateador = new SimpleDateFormat(newFormat);

        try {
            date = parseador.parse(strDate);
            resp = formateador.format(date);
            return resp;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static  double StringFormatedToDouble(String num){

        try {
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            num= num.replace(".","@");
            num= num.replace(",",".");
            num=  num.replace("@",",");
            Number number = format.parse(num);
            return number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Encode image Bitmap to Base64
     *
     * @param bm
     * @return
     */
    public static String encodeImage(Bitmap bm) {
        Log.w("encodeImage", "image bounds: " + bm.getWidth() + ", " + bm.getHeight());

        if (bm.getHeight() <= 400 && bm.getWidth() <= 400) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            return (Base64.encodeToString(b, Base64.DEFAULT)).replaceAll("\\n", "");
        }
        int mHeight = 400;
        int mWidth = 400;

        if (bm.getHeight() > bm.getWidth()) {
            float div = (float) bm.getWidth() / ((float) bm.getHeight());
            float auxW = div * 480;
            mHeight = 480;
            mWidth = Math.round(auxW);
            Log.w("encodeImage", "new high: " + mHeight + " width: " + mWidth);
        } else {
            float div = ((float) bm.getHeight()) / (float) bm.getWidth();
            float auxH = div * 480;
            mWidth = 480;
            mHeight = 360;
            mHeight = Math.round(auxH);
            Log.w("encodeImage", "new high: " + mHeight + " width: " + mWidth);
        }

        bm = Bitmap.createScaledBitmap(bm, mWidth, mHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return (Base64.encodeToString(b, Base64.DEFAULT)).replaceAll("\\n", "");
    }


    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        //   dialog.setContentView(R.layout.dialog);

        // TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        //  text.setText(msg);


        dialog.show();

    }

    public static void setToast(Context context, String message, int type) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.color.error_toast);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();
    }


    public static String dateToString(Date d, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format != null ? format + "" : "yyyy-MM-dd HH:mm:ss");
        String stringDate = dateFormat.format(d);
        return stringDate;
    }

    public static boolean isNumberOrDecimal(String string) {
        try {
            float amount = Float.parseFloat(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * return 1 - obj 2 - array  -1 - error
     *
     * @param response
     * @return
     */
    public static boolean isJSONArray(String response) {
        try{
         JSONArray array = new JSONArray(response);
         return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean isJSONObject(String response) {
        try{
            JSONObject array = new JSONObject(response);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{11}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{4}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{4}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{4}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else if ( Patterns.PHONE.matcher(phoneNo).matches()) return true;
        else return false;

    }

/*
    private void ordenarList(ArrayList<Objects> list) {
        //TODO: ordenamiendo de la lista
        Collections.sort(list, new Comparator<Objects>() {
            @Override
            public int compare(Objects o1, Objects o2) {
                int rsp,infinite;
                infinite=1000;
                int va1=0;
                int va2=0;
                //va1 = o1.get();
                //va2 = o2.getStat();

                rsp = Integer.compare(va1, va2);
                return rsp;
            }
        });

    }
*/

    /**
     * Math round with decimals
     *
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public enum typeValue {
        INTEGER, BOOLEAN, STRING, FLOAT, DOUBLE, LONG
    }

    public static void animate(boolean show_or_hide, LinearLayout layoutAnimate){
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (show_or_hide){
            //desde la esquina inferior derecha a la superior izquierda
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        }
        else{    //desde la esquina superior izquierda a la esquina inferior derecha
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        }
        //duración en milisegundos
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        layoutAnimate.setLayoutAnimation(controller);
        layoutAnimate.startAnimation(animation);
        layoutAnimate.setVisibility(show_or_hide?View.VISIBLE:View.GONE);
    }

    public static void animate(boolean show_or_hide, LinearLayout layoutAnimate, float[] fromX, float[] fromY, int duration){
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (show_or_hide)
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromX[0], Animation.RELATIVE_TO_SELF, fromX[1], Animation.RELATIVE_TO_SELF, fromY[0], Animation.RELATIVE_TO_SELF, fromY[1]);
        else
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromX[2], Animation.RELATIVE_TO_SELF, fromX[3], Animation.RELATIVE_TO_SELF, fromY[2], Animation.RELATIVE_TO_SELF, fromY[3]);

        //duración en milisegundos
        animation.setDuration(duration);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        layoutAnimate.setLayoutAnimation(controller);
        layoutAnimate.startAnimation(animation);
        layoutAnimate.setVisibility(show_or_hide?View.VISIBLE:View.GONE);
    }


    public static Object getShared(Context context, int stringKey, typeValue type) {

        if (typeValue.STRING == type)
            return context.getSharedPreferences(context.getString(R.string.shared_key), 0).getString(context.getString(stringKey), "");
        else if (typeValue.BOOLEAN == type)
            return context.getSharedPreferences(context.getString(R.string.shared_key), 0).getBoolean(context.getString(stringKey), false);
        else if (typeValue.INTEGER == type)
            return context.getSharedPreferences(context.getString(R.string.shared_key), 0).getInt(context.getString(stringKey), 0);

        else
            return null;

    }

    public static int checkNullInt(int value){
        try{
            return value;
        }catch (NumberFormatException nex){
            return 0;
        }
    }


}


