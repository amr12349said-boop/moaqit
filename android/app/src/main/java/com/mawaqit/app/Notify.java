package com.mawaqit.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.os.Build;

public class Notify {

    public static final String CH_AZAN = "azan_high_v2";
    public static final String CH_ALERTS = "prayer_alerts";

    public static void createChannels(Context c) {
        if (Build.VERSION.SDK_INT < 26) return;
        try {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel a = new NotificationChannel(
                    CH_AZAN, c.getString(R.string.ch_azan), NotificationManager.IMPORTANCE_HIGH);
            a.setDescription("تشغيل الأذان عند دخول وقت الصلاة");
            a.setSound(null, null);
            a.enableVibration(false);
            try { a.setVibrationPattern(new long[]{0L}); } catch (Exception e) { }
            a.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(a);

            NotificationChannel n = new NotificationChannel(
                    CH_ALERTS, c.getString(R.string.ch_alerts), NotificationManager.IMPORTANCE_HIGH);
            n.setDescription("تنبيه قبل الصلاة وبوقت الأذان");
            n.enableVibration(false);
            try { n.setVibrationPattern(new long[]{0L}); } catch (Exception e) { }
            try {
                n.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            } catch (Exception e) { }
            nm.createNotificationChannel(n);
        } catch (Exception e) { }
    }

    private static PendingIntent openApp(Context c) {
        Intent i = new Intent(c, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getActivity(c, 90, i, flags);
    }

    private static PendingIntent stopPi(Context c) {
        Intent i = new Intent(c, AlarmReceiver.class).setAction(AlarmReceiver.ACTION_STOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c, 91, i, flags);
    }

    @SuppressWarnings("deprecation")
    public static Notification adhanPlaying(Context c, String name) {
        Notification.Builder b = (Build.VERSION.SDK_INT >= 26)
                ? new Notification.Builder(c, CH_AZAN)
                : new Notification.Builder(c);

        b.setContentTitle("حان الآن وقت صلاة " + name)
                .setContentText(c.getString(R.string.adhan_playing))
                .setSmallIcon(R.drawable.ic_stat_adhan)
                .setContentIntent(openApp(c))
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_ALARM)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        try {
            b.setFullScreenIntent(openApp(c), true);
        } catch (Exception e) { }

        if (Build.VERSION.SDK_INT >= 23) {
            b.addAction(new Notification.Action.Builder(
                    (Icon) null, c.getString(R.string.stop), stopPi(c)).build());
        }
        return b.build();
    }

    @SuppressWarnings("deprecation")
    public static void prayerSoon(Context c, String name) {
        try {
            Notification.Builder b = (Build.VERSION.SDK_INT >= 26)
                    ? new Notification.Builder(c, CH_ALERTS)
                    : new Notification.Builder(c);
            b.setContentTitle("أذان " + name + " بعد ١٠ دقائق")
                    .setContentText("استعد للصلاة")
                    .setSmallIcon(R.drawable.ic_stat_adhan)
                    .setContentIntent(openApp(c))
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_REMINDER);
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.notify((int) (System.currentTimeMillis() % 100000), b.build());
        } catch (Exception e) { }
    }
}
