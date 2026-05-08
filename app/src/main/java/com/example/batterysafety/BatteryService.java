package com.example.batterysafety;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncherKt;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.batterysafety.ui.FunctionsFragment;

public class BatteryService extends Service {
    private Handler handler = new Handler();

    private int level;
    private int low;
    private int full;
    private boolean firstTime = true;
    private static final String CHANNEL_ID = "ChannelIdOfBatteryService";
    private static final int NOTIFICATION_ID = 101000;
    public BatteryService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Create and display the notification

        startForeground(NOTIFICATION_ID, getNotification());
        startServiceBattery();


        return START_STICKY;
    }
    private void startServiceBattery() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check the battery level here
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            // Check the status battery is charging
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;

            // SharedPreferences for get status of switch and value of full|low
            SharedPreferences sharedPreferences = getSharedPreferences("Switch", Context.MODE_PRIVATE);
            full = sharedPreferences.getInt("valueFull", 85);
            low = sharedPreferences.getInt("valueLow", 45);
            boolean switchFull = sharedPreferences.getBoolean("isCheckedFull", false);
            boolean switchLow = sharedPreferences.getBoolean("isCheckedLow", false);

                // Check if the switch status of full is checked and the battery level is equal full
            if (switchFull && level == full && firstTime && isCharging){
                firstTime = false;
//                goToAlarm();
                scheduleAlarmStart();
                Toast.makeText(context, "full "+full, Toast.LENGTH_SHORT).show();
            }
            // Check if the switch status of low is checked and the battery level is equal low
            if (switchLow && level == low && firstTime && !isCharging) {
                firstTime = false;
//                goToAlarm();
                scheduleAlarmStart();
                Toast.makeText(context, "low "+low, Toast.LENGTH_SHORT).show();
            }
            if ( level != full && level != low){
                firstTime = true;
            }

        }
    };

    private void scheduleAlarmStart() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToAlarm();
            }
        }, 5 * 1000);
    }

    private void goToAlarm(){
        Intent activityIntent = new Intent(getApplicationContext(), AlarmActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(activityIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); // Replace yourReceiver with your actual receiver instance
        unregisterReceiver(batteryReceiver);
        stopSelf();

    }
    private Notification getNotification(){

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Battery Service", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Battery Service");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("Battery safety")
                .setContentText("Tap to return to the app")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        return builder.build();
    }
}