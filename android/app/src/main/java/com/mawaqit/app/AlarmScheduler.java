package com.mawaqit.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * يجدول أذان كل صلاة + تنبيه قبلها ١٠ دقائق باستخدام المنبهات الدقيقة.
 */
public class AlarmScheduler {

    public static final String PREF = "mawaqit_alarms";
    public static final String KEY = "times_json";

    public static void schedule(Context c, JSONArray times) {
        try {
            c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
                    .putString(KEY, times.toString()).apply();
        } catch (Exception e) { }
        rescheduleAll(c);
    }

    public static void rescheduleAll(Context c) {
        String js = null;
        try {
            js = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, null);
        } catch (Exception e) { }
        if (js == null) return;

        long now = System.currentTimeMillis();
        try {
            JSONArray arr = new JSONArray(js);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject t = arr.getJSONObject(i);
                String key = t.optString("key", "");
                String name = t.optString("name", key);
                long at = t.optLong("at", 0L);
                if (at <= 0) continue;
                if (at - now > 26L * 3600L * 1000L) continue;

                if (at > now + 3000L) {
                    setExact(c, at, AlarmReceiver.ACTION_ADHAN, key, name, i * 2);
                }
                long pre = at - 10L * 60L * 1000L;
                if (pre > now + 3000L) {
                    setExact(c, pre, AlarmReceiver.ACTION_PRE, key, name, i * 2 + 1);
                }
            }
        } catch (Exception e) { }
    }

    private static PendingIntent pi(Context c, String action, String key, String name, int rc) {
        Intent i = new Intent(c, AlarmReceiver.class)
                .setAction(action)
                .putExtra("key", key)
                .putExtra("name", name);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(c, rc, i, flags);
    }

    private static void setExact(Context c, long at, String action, String key, String name, int rc) {
        try {
            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;
            PendingIntent p = pi(c, action, key, name, rc);
            if (Build.VERSION.SDK_INT >= 31) {
                if (am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, p);
                else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, p);
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, p);
            }
        } catch (Exception e) {
            try {
                AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                if (am != null) am.set(AlarmManager.RTC_WAKEUP, at, pi(c, action, key, name, rc));
            } catch (Exception e2) { }
        }
    }
}
