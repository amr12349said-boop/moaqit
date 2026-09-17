# قواعد الحفاظ على جسر JavaScript (مهم لو تم تفعيل التصغير لاحقاً)
-keepclassmembers class com.mawaqit.app.MainActivity$Bridge {
    public *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
