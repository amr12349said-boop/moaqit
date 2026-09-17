package com.mawaqit.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {

    public static volatile boolean foreground = false;

    private static final String HOME = "https://appassets.androidplatform.net/assets/index.html";
    private static final int REQ_NOTIF = 101;
    private static final int REQ_LOC = 102;
    private static final int REQ_FILE = 103;

    private static final String JS_CURRENT_TAB =
            "(function(){try{var t=document.querySelectorAll('.tab');for(var i=0;i<t.length;i++){" +
            "if(t[i].classList.contains('active'))return i>0?'other':'first';}}catch(e){}return 'first';})()";

    private WebView web;
    private WebViewAssetLoader loader;
    private GeolocationPermissions.Callback geoCallback;
    private String geoOrigin;
    private ValueCallback<Uri[]> filePathCallback;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        Notify.createChannels(this);

        loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0A0F1A"));

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setGeolocationEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req) {
                try {
                    return loader.shouldInterceptRequest(req.getUrl());
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                if (u != null && "appassets.androidplatform.net".equals(u.getHost())) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, u));
                } catch (Exception e) { }
                return true;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback cb) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    cb.invoke(origin, true, true);
                } else {
                    geoCallback = cb;
                    geoOrigin = origin;
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_LOC);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;
                try {
                    startActivityForResult(fileChooserParams.createIntent(), REQ_FILE);
                } catch (Exception e) {
                    if (MainActivity.this.filePathCallback != null) {
                        MainActivity.this.filePathCallback.onReceiveValue(null);
                        MainActivity.this.filePathCallback = null;
                    }
                    return false;
                }
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "AndroidAdhan");
        setContentView(web);
        web.loadUrl(HOME);

        askNotifPermission();
    }

    public class Bridge {
        @JavascriptInterface
        public void postMessage(String json) {
            Router.handle(MainActivity.this, web, json);
        }

        @JavascriptInterface
        public boolean hasNotifPermission() {
            try {
                if (Build.VERSION.SDK_INT >= 33 &&
                        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                return nm == null || nm.areNotificationsEnabled();
            } catch (Exception e) {
                return false;
            }
        }

        @JavascriptInterface
        public void requestNotifPermission() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    askNotifPermission();
                }
            });
        }

        @JavascriptInterface
        public void openNotifSettings() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(i);
                    } catch (Exception e) {
                        try {
                            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName())));
                        } catch (Exception e2) { }
                    }
                }
            });
        }
    }

    private void askNotifPermission() {
        try {
            if (Build.VERSION.SDK_INT >= 33 &&
                    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
            }
        } catch (Exception e) { }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == REQ_LOC && geoCallback != null) {
            boolean granted = res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED;
            try {
                geoCallback.invoke(geoOrigin, granted, true);
            } catch (Exception e) { }
            geoCallback = null;
            geoOrigin = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_FILE) {
            if (filePathCallback == null) return;
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getData() != null) {
                    results = new Uri[]{data.getData()};
                } else if (data.getClipData() != null && data.getClipData().getItemCount() > 0) {
                    int count = data.getClipData().getItemCount();
                    results = new Uri[count];
                    for (int i = 0; i < count; i++) {
                        results[i] = data.getClipData().getItemAt(i).getUri();
                    }
                }
            }
            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        foreground = true;
        eval("try{ if(window.MawaqitAdhan) MawaqitAdhan.sync(); }catch(e){}");
    }

    @Override
    protected void onPause() {
        foreground = false;
        super.onPause();
    }

    private void eval(final String js) {
        if (web == null) return;
        web.post(new Runnable() {
            @Override
            public void run() {
                try {
                    web.evaluateJavascript(js, null);
                } catch (Exception e) { }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (web == null) {
            finish();
            return;
        }
        web.evaluateJavascript(JS_CURRENT_TAB, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                boolean onFirstTab = value == null || value.contains("first");
                if (onFirstTab) finish();
                else eval("try{ goTab(1); }catch(e){}");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (web != null) {
            try {
                web.destroy();
            } catch (Exception e) { }
            web = null;
        }
        super.onDestroy();
    }
}
