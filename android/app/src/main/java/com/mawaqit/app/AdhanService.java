package com.mawaqit.app;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

/**
 * خدمة قدامية تشغّل صوت الأذان كاملاً حتى لو الشاشة مقفولة أو التطبيق في الخلفية.
 */
public class AdhanService extends Service {

    private static final int NOTIF_ID = 4242;
    private MediaPlayer player;

    public static void start(Context c, String key, String name) {
        try {
            Intent i = new Intent(c, AdhanService.class)
                    .putExtra("key", key == null ? "" : key)
                    .putExtra("name", name == null ? "الصلاة" : name);
            if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i);
            else c.startService(i);
        } catch (Exception e) { }
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String name = (intent != null && intent.getStringExtra("name") != null)
                ? intent.getStringExtra("name") : "الصلاة";
        Notification n = Notify.adhanPlaying(this, name);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                startForeground(NOTIF_ID, n);
            }
        } catch (Exception e) { }
        play();
        return START_NOT_STICKY;
    }

    private void play() {
        stopPlay();
        try {
            player = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("azan_nasser.mp3");
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            player.setAudioAttributes(attrs);
            player.setLooping(true);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        mp.start();
                    } catch (Exception e) { }
                }
            });
            player.prepareAsync();
        } catch (Exception e) { }
    }

    private void stopPlay() {
        if (player != null) {
            try {
                player.stop();
            } catch (Exception e) { }
            try {
                player.release();
            } catch (Exception e) { }
            player = null;
        }
    }

    @Override
    public void onDestroy() {
        stopPlay();
        try {
            stopForeground(true);
        } catch (Exception e) { }
        super.onDestroy();
    }
}
