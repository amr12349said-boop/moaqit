package com.mawaqit.app;

import android.content.Context;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * يستقبل رسائل جسر الأذان من الصفحة (ready / times / prayerStart) ويقرر
 * هل يشغّل الأذان أصلياً (Native) أم الصفحة كفاية.
 */
public class Router {

    public static void handle(Context ctx, WebView web, String json) {
        try {
            JSONObject o = new JSONObject(json);
            String type = o.optString("type", "");

            if ("ready".equals(type) || "times".equals(type) || "prayerStart".equals(type)) {
                JSONArray times = o.optJSONArray("times");
                if (times != null) AlarmScheduler.schedule(ctx, times);
            }

            if ("prayerStart".equals(type)) {
                boolean playedInApp = o.optBoolean("playedInApp", false);
                boolean enabled = o.optBoolean("enabled", true);
                if (!enabled) return;
                if (MainActivity.foreground && playedInApp) return;

                if (!MainActivity.foreground && web != null) {
                    web.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                web.evaluateJavascript("try{ MawaqitAdhan.stop(); }catch(e){}", null);
                            } catch (Exception e) { }
                        }
                    });
                }
                AdhanService.start(ctx, o.optString("prayer", "prayer"), o.optString("name", "الصلاة"));
            }
        } catch (Exception e) { }
    }
}
