package edu.ewubd.CSE489232_2020_2_60_054;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.Manifest;
import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyAlarmReceiver extends BroadcastReceiver {

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        mediaPlayer = MediaPlayer.create(context, R.raw.herotheme);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
            }
        }, 28000);
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }
}
