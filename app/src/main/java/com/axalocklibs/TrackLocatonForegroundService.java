package com.axalocklibs;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.axalocklibs.singletone.AxaSingleToneClass;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.pixplicity.easyprefs.library.Prefs;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;


/**
 * Booking tracking service : Service run while ride is running on : track user location and send to track
 * location to server
 */
public class TrackLocatonForegroundService extends Service implements LocationListener {

    private static final String channelId = "Foreground";
    private static final String channelName = "Koloni";


    private static final String TAG = TrackLocatonForegroundService.class.getName();
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    public double track_lat = 0.0;
    public double track_lng = 0.0;
    String trackLatitude = "";
    String trackLongitude = "";
    private Context context;
    long myDealy = 10000;
    int uploadId;
    private String trackLocationResponse = "";
    AxaSingleToneClass axaSingleToneClass;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            //  String action = intent.getAction();
            Random random = new Random();

            uploadId = random.nextInt(10000);
            generateNotification(uploadId, "", "", "", "", "", "", "", false, "");
            uploadServiceTask();

            axaSingleToneClass = new AxaSingleToneClass();
            axaSingleToneClass.scanLeDevice(true);


            if (intent.hasExtra("service")) {
                boolean isNeedToService = intent.getBooleanExtra("service", false);
                if (!isNeedToService) {
                    stopForegroundService();
                }
            }
        }

        return START_STICKY;
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            uploadServiceTask();
        }
    };

    /**
     * Satrt and stop foregrounds service based on booking on or off
     */
    private void uploadServiceTask() {
//        Log.e("mLeScanCallback_start" , "Scan Start Again");

        // call every time
    }


    private void generateNotification(int uploadId, String totalTime, String totalAmount, String totalDistance, String bookingId,
                                      String bikeId, String startTime, String endTime, boolean isBookingOn,
                                      String objectUniqueID) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.black))
                .setOngoing(true)
                .setAutoCancel(false)
                .setPriority(0)
                .setNotificationSilent()
                .setSound(null)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name))
                .setContentIntent(resultPendingIntent);
        Notification notification = builder.build();


        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_LOW);
            mChannel.setVibrationPattern(new long[]{0});
            mChannel.enableVibration(true);
            mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
        startForeground(uploadId, notification);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    /**
     * Stop forground service if ride completed
     */

    private void stopForegroundService() {

        Log.e("BookingTrackingService", "Stop foreground service.");
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
            stopForeground(true);
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    NotificationManager notificationManager;

    private void removeNotification() {

        if (notificationManager != null) {
            notificationManager.cancel(555);
        }

        try {
            stopForegroundService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
