package com.jacd.animelistvideos.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jacd.animelistvideos.common.FormatDate;
import com.jacd.animelistvideos.common.Utils;
import com.jacd.animelistvideos.ui.MainActivity;
import com.jacd.animelistvideos.R;
import com.jacd.animelistvideos.ui.activity.WebViewActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public abstract class NotificationsTask {

    private static final String CHANNEL_ID = "com.jacd.animelistvideos.ANDROID";
    public static final String ANDROID_CHANNEL_ID = "com.jacd.animelistvideos.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "notifications_alist_video_android";
    private static final String GROUP_KEY_WORK_EMAIL = "com.jacd.animelistvideos.VIDEOS_TODAY";


    public abstract class Tasks {
        public static final int SECOND = 1000;
        public static final int TIME_SLEEP_NOTIFICATIONS = 30 * SECOND;
        public static final int ICONS_SMALL = R.drawable.logo;
    }


    public static void createPusherNotificacions(Context context, String title, String content, String contendInfo, int id) {
        NotificationCompat.Builder mBuilder;
        boolean isURL = (""+contendInfo).contains("http");

        int icono = Tasks.ICONS_SMALL;
        Intent i = new Intent(context, WebViewActivity.class);
        i.putExtra("url_webview", contendInfo);
        i.putExtra("title_webview", "Notificaciones Webview");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    .setContentTitle(title)
                    .setContentText(content)
                    //.setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if(isURL)
                mBuilder.setContentIntent(pendingIntent);

        } else {
            mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    //.setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if(isURL)
                mBuilder.setContentIntent(pendingIntent);

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ANDROID_CHANNEL_NAME;
            String description = ANDROID_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager mNotifyMgr = context.getSystemService(NotificationManager.class);
            mNotifyMgr.createNotificationChannel(channel);
            mNotifyMgr.notify(id, mBuilder.build());
        } else {
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(id, mBuilder.build());
        }

    }

    public static void createNotificacions(Context context, String title, String content, String contendInfo, int id) {
        NotificationCompat.Builder mBuilder;

        int icono = Tasks.ICONS_SMALL;
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ANDROID_CHANNEL_NAME;
            String description = ANDROID_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager mNotifyMgr = context.getSystemService(NotificationManager.class);
            mNotifyMgr.createNotificationChannel(channel);
            mNotifyMgr.notify(id, mBuilder.build());
        } else {
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(id, mBuilder.build());
        }

    }

    public static void createNotificacions(Context context, String title, String content, Bitmap urlImage, String contendInfo, int id) {
        NotificationCompat.Builder mBuilder;

        int icono = Tasks.ICONS_SMALL;
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(urlImage).bigLargeIcon(null))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } else {
            mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(icono)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setTicker("tickers")
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(urlImage).bigLargeIcon(null))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ANDROID_CHANNEL_NAME;
            String description = ANDROID_CHANNEL_NAME;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager mNotifyMgr = context.getSystemService(NotificationManager.class);
            mNotifyMgr.createNotificationChannel(channel);
            mNotifyMgr.notify(id, mBuilder.build());
        } else {
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(id, mBuilder.build());
        }

    }

    public static void createNotificacions(Context context, String title, String content, String contendInfo,int icon , int id){
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        int icono =(icon==0)? Tasks.ICONS_SMALL:icon;
        Intent i=new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder =new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content).setSmallIcon(icono)
                //.setContentIntent(pIntent)
                .setVibrate(new long[] {100, 250, 100, 500})
                .setTicker("tickers")
                .setContentInfo(contendInfo)
                .setSound(sonido)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setAutoCancel(true);


        mNotifyMgr.notify(id, mBuilder.build());
    }

    public static void createNotificacions(Context context, String title, String content, String ticker, String contendInfo,int icon , int id){
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        int icono =(icon==0)? Tasks.ICONS_SMALL:icon;
        Intent i=new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder =new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content).setSmallIcon(icono)
                //.setContentIntent(pIntent)
                .setVibrate(new long[] {100, 250, 100, 500})
                .setTicker(ticker)
                .setContentInfo(contendInfo)
                .setSound(sonido)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setAutoCancel(true);


        mNotifyMgr.notify(id, mBuilder.build());
    }

    public abstract static class Methods {

        //metodo para comprobar si un servicio esta activo
        public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }



    }


    /*** ***/
    public abstract static class Notifications {
        @SuppressLint("StaticFieldLeak")
        private static Context context;

        public static void Notifications(Context c) {
            context = c;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public static void throudNotificacion(String title, String content, String ticker, String contendInfo, int id){

            //Intent intent = new Intent(context, MainActivity.class);
            //PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
            Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // Build notification
            // Actions are just fake
            Notification noti = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content).setSmallIcon(Tasks.ICONS_SMALL)
                    //.setContentIntent(pIntent)
                    .setVibrate(new long[] {100, 250, 100, 500})
                    .setTicker(ticker)
                    .setContentInfo(contendInfo)
                    .setSound(sonido)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            // hide the notification after its selected
            noti.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(id, noti);
        }
    }
    public static class SyncNotification {
        /**
         * The unique identifier for this type of notification.
         */
        private static final String NOTIFICATION_TAG = "Sync";

        /**
         * Shows the notification, or updates a previously shown notification of
         * this type, with the given parameters.
         * <p>
         * TODO: Customize this method's arguments to present relevant content in
         * the notification.
         * <p>
         * TODO: Customize the contents of this method to tweak the behavior and
         * presentation of sync notifications. Make
         * sure to follow the
         * <a href="https://developer.android.com/design/patterns/notifications.html">
         * Notification design guidelines</a> when doing so.
         *
         * @see #cancel(Context)
         */
        private static void notify(final Context context,
                                   final String exampleString, final int number) {
            final Resources res = context.getResources();

            // This image is used as the notification's large icon (thumbnail).
            // TODO: Remove this if your notification has no relevant thumbnail.
            final Bitmap picture = BitmapFactory.decodeResource(res, Tasks.ICONS_SMALL);


            final String ticker = exampleString;
            final String title = "Sync: %1$s";
            final String text = "You said %1$s and lorem ipsum dolor\n" +
                    "        sit amet, consectetur adipiscing elit. Etiam non enim magna. Morbi dictum, velit vel semper\n" +
                    "        venenatis, magna odio volutpat velit, at ullamcorper nulla lacus sed turpis. Pellentesque\n" +
                    "        vitae metus elit, nec tincidunt tellus. Integer sed nisl sem, ullamcorper ornare lacus. Duis\n" +
                    "        ac mauris sed massa congue volutpat. Donec sed erat sit amet turpis viverra rhoncus sit amet\n" +
                    "        nec magna. Donec lacinia ligula at libero volutpat volutpat nec nec tortor.";

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(Tasks.ICONS_SMALL)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLargeIcon(picture)
                    .setTicker(ticker)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setNumber(number)

                    .setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text)
                            .setBigContentTitle(title)
                            .setSummaryText("Dummy summary text"))
                    .addAction(
                            Tasks.ICONS_SMALL,
                            "Share",
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                            .setType("text/plain")
                                            .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .addAction(
                            Tasks.ICONS_SMALL,
                            "Reply",
                            null)
                    .setAutoCancel(true);

            notify(context, builder.build());
        }

        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private static void notify(final Context context, final Notification notification) {
            final NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, 0, notification);
            } else {
                nm.notify(NOTIFICATION_TAG.hashCode(), notification);
            }
        }

        /**
         * Cancels any notifications of this type previously shown using
         * {@link #notify(Context, String, int)}.
         */
        @TargetApi(Build.VERSION_CODES.ECLAIR)
        private static void cancel(final Context context) {
            final NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.cancel(NOTIFICATION_TAG, 0);
            } else {
                nm.cancel(NOTIFICATION_TAG.hashCode());
            }
        }
    }
    /*** ***/

    /** Comentario de ayuda **/
    public static final String HELP = "Para usar este sistema de notificaciones, " +
            "agregue ene el Manifest los uses-permission: INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE," +
            "CHANGE_NETWORK_STATE, VIBRATE y RECEIVE_BOOT_COMPLETED. Adicionalmente instancie esto, " +
            "cambiando por el paquete donde se encuentra, \"service\" de nombre: MyServiceNotificacions, \"receiver\"" +
            "de nombre: BootBroadcast. ";

    private final String servicex =
            "<service\n" +
            "            android:name=\".services.MyServiceNotificacions\"\n" +
            "            android:enabled=\"true\"\n" +
            "            android:exported=\"true\"/>";
    private final String receiverx =
            "        <receiver android:name=\".services.BootBroadcast\">\n" +
            "            <intent-filter>\n" +
            "                <action android:name=\"com.zippyttech.alist\" />\n" +
            "                <action android:name=\"android.intent.action.BOOT_COMPLETED\" />\n" +
            "            </intent-filter>\n" +
            "        </receiver>";
}
