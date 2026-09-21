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
import android.os.Environment;
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

import androidx.core.content.FileProvider;
import androidx.webkit.WebViewAssetLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    public static volatile boolean foreground = false;

    private static final String HOME = "https://appassets.androidplatform.net/assets/index.html";
    private static final int REQ_NOTIF = 101;
    private static final int REQ_LOC = 102;
    private static final int REQ_FILE = 103;
    private static final int REQ_INSTALL_SRC = 104;

    private static final String JS_CURRENT_TAB =
            "(function(){try{var t=document.querySelectorAll('.tab');for(var i=0;i<t.length;i++){" +
            "if(t[i].classList.contains('active'))return i>0?'other':'first';}}catch(e){}return 'first';})()";

    private WebView web;
    private WebViewAssetLoader loader;
    private GeolocationPermissions.Callback geoCallback;
    private String geoOrigin;
    private ValueCallback<Uri[]> filePathCallback;
    private volatile boolean updaterRunning = false;
    private volatile boolean updaterCancel = false;
    private String pendingUpdateUrl = null;

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

        @JavascriptInterface
        public int getVersionCode() {
            try { return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode; } catch (Throwable t) { return 0; }
        }

        @JavascriptInterface
        public String getVersionName() {
            try { return getPackageManager().getPackageInfo(getPackageName(), 0).versionName; } catch (Throwable t) { return ""; }
        }

        @JavascriptInterface
        public void installUpdate(String url) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startUpdate(url);
                }
            });
        }

        @JavascriptInterface
        public void cancelUpdate() {
            updaterCancel = true;
            updaterRunning = false;
            pendingUpdateUrl = null;
        }
    }

    private void startUpdate(String url) {
        try {
            if (updaterRunning) { toastUpdater("التحميل جاري بالفعل..."); return; }
            if (url == null || url.isEmpty()) { toastUpdater("رابط التحديث غير صالح"); return; }
            if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
                pendingUpdateUrl = url;
                try {
                    Intent i = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(i, REQ_INSTALL_SRC);
                } catch (Exception e) {
                    pendingUpdateUrl = null;
                    startDownload(url);
                }
            } else {
                startDownload(url);
            }
        } catch (Exception e) {
            toastUpdater("تعذر بدء التحديث");
        }
    }

    private void startDownload(final String url) {
        updaterRunning = true;
        updaterCancel = false;
        eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'downloading',pct:0,msg:''})}catch(e){}");
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                OutputStream os = null;
                HttpURLConnection conn = null;
                File out = null;
                try {
                    final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    if (dir == null) throw new Exception("نو-دير");
                    if (!dir.exists()) dir.mkdirs();
                    final File tmp = new File(dir, "mawaqit-update.apk.part");
                    out = new File(dir, "mawaqit-update.apk");

                    URL u = new URL(url);
                    conn = openConn(u, url);
                    for (int hop = 0; hop < 5; hop++) {
                        int code = conn.getResponseCode();
                        if (code == 200) break;
                        if (code >= 300 && code < 400) {
                            String loc = conn.getHeaderField("Location");
                            if (loc == null) throw new Exception("لا-يوجد-رابط-تحويل");
                            conn.disconnect();
                            u = new URL(new URL(url), loc);
                            conn = openConn(u, url);
                        } else {
                            throw new Exception("رمز-http-" + code);
                        }
                    }

                    long total = conn.getContentLengthLong();
                    in = new BufferedInputStream(conn.getInputStream());
                    os = new FileOutputStream(tmp);
                    byte[] buf = new byte[16384];
                    long done = 0;
                    int n, lastPct = -1;
                    while ((n = in.read(buf)) > 0) {
                        if (updaterCancel) {
                            os.close(); os = null; in.close(); in = null;
                            tmp.delete();
                            updaterRunning = false;
                            eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'idle',pct:0,msg:''})}catch(e){}");
                            return;
                        }
                        os.write(buf, 0, n);
                        done += n;
                        if (total > 0) {
                            int pct = (int) Math.min(99, done * 100 / total);
                            if (pct != lastPct) {
                                lastPct = pct;
                                final int fp = pct;
                                eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'downloading',pct:" + fp + ",msg:''})}catch(e){}");
                            }
                        }
                    }
                    os.flush(); os.close(); os = null;
                    in.close(); in = null;
                    if (tmp.renameTo(out)) tmp.delete();
                    else out = tmp;
                    updaterRunning = false;
                    final File apk = out;
                    eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'ready',pct:100,msg:''})}catch(e){}");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startInstall(apk);
                        }
                    });
                } catch (final Exception e) {
                    updaterRunning = false;
                    eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'error',pct:0,msg:'" + jsq(e.getMessage()) + "'})}catch(x){}");
                } finally {
                    try { if (in != null) in.close(); } catch (Exception e) { }
                    try { if (os != null) os.close(); } catch (Exception e) { }
                    try { if (conn != null) conn.disconnect(); } catch (Exception e) { }
                }
            }
        }).start();
    }

    private HttpURLConnection openConn(URL u, String base) throws Exception {
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setConnectTimeout(15000);
        c.setReadTimeout(30000);
        c.setInstanceFollowRedirects(false);
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 12) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36");
        c.setRequestProperty("Accept", "application/vnd.android.package-archive,*/*");
        c.connect();
        return c;
    }

    private void startInstall(File apk) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", apk);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try { i.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true); } catch (Exception e) { }
            startActivity(i);
        } catch (Exception e) {
            toastUpdater("تعذر فتح شاشة التثبيت — افتح صفحة التحميل يدوياً");
            eval("try{if(window.MawaqitAdhan)window.MawaqitAdhan.updateState({state:'error',pct:0,msg:'install-failed'})}catch(x){}");
        }
    }

    private static String jsq(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ").replace("\r", " ");
    }

    private void toastUpdater(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    android.widget.Toast.makeText(MainActivity.this, msg, android.widget.Toast.LENGTH_LONG).show();
                } catch (Exception e) { }
            }
        });
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
        if (requestCode == REQ_INSTALL_SRC) {
            String pending = pendingUpdateUrl;
            pendingUpdateUrl = null;
            if (Build.VERSION.SDK_INT >= 26 && getPackageManager().canRequestPackageInstalls()) {
                if (pending != null && !pending.isEmpty()) startDownload(pending);
                else toastUpdater("تم منح الإذن — اضغط التحديث مجدداً");
            } else {
                toastUpdater("لم يتم منح إذن التثبيت من مصادر خارجية");
            }
            return;
        }
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
