package com.example.batterysafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;

import com.example.batterysafety.databinding.ActivityAlarmBinding;

import java.io.IOException;

public class AlarmActivity extends AppCompatActivity {

    ActivityAlarmBinding binding;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private static final String CHANNEL_ALARM_ID = "ChannelIdOfAlarmBatteryService";
    private static final int NOTIFICATION_ALARM_ID = 5401000;
    private Handler handler = new Handler();
    private static final int ALARM_DURATION = 15 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startAlarm();
        scheduleAlarmStop();


        binding.stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                finishAndRemoveTask();
            }
        });


    }

    private void scheduleAlarmStop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlarm();
                finishAndRemoveTask();
            }
        }, ALARM_DURATION);
    }


    private void startAlarm() {
        if (mediaPlayer == null) {
            playSound();
            vibrate();
        } else if (!mediaPlayer.isPlaying() || !vibrator.hasVibrator()) {
            playSound();
            vibrate();
        }
    }

    private void stopAlarm() {
        stopSound();
        stopVibrate();
    }

    private void playSound() {
        if (mediaPlayer == null) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            try {
                mediaPlayer.setDataSource(getApplicationContext(), notification);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void stopSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void vibrate() {
        if (vibrator == null) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 500}, 0));
        }
    }

    private void stopVibrate() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    public void createNotificationAlarm() {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ALARM_ID, "Alarm Service", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Alarm Service");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Intent intent = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ALARM_ID);
        builder.setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("Alarm Service")
                .setContentText("Tap to stop alarm")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true);;


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ALARM_ID, builder.build());

    }


}