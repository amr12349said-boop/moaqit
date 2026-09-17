package com.mawaqit.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_ADHAN = "com.mawaqit.app.action.ADHAN";
    public static final String ACTION_PRE = "com.mawaqit.app.action.PRE";
    public static final String ACTION_STOP = "com.mawaqit.app.action.STOP";

    @Override
    public void onReceive(Context c, Intent i) {
        if (i == null || i.getAction() == null) return;
        String a = i.getAction();

        if (ACTION_STOP.equals(a)) {
            try {
                c.stopService(new Intent(c, AdhanService.class));
            } catch (Exception e) { }
            return;
        }

        String key = i.getStringExtra("key");
        String name = i.getStringExtra("name");

        if (ACTION_ADHAN.equals(a)) {
            if (!MainActivity.foreground) {
                AdhanService.start(c, key, name);
            }
        } else if (ACTION_PRE.equals(a)) {
            Notify.prayerSoon(c, name == null ? "" : name);
        }
    }
}
