package com.mawaqit.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        try {
            Notify.createChannels(c);
            AlarmScheduler.rescheduleAll(c);
        } catch (Exception e) { }
    }
}
