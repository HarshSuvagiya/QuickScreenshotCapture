package vishtechno.bkm.quickscreenshotcapture.view.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AlertDialog.Builder;
import com.bumptech.glide.load.Key;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import java.io.File;
import java.io.UnsupportedEncodingException;

import vishtechno.bkm.quickscreenshotcapture.R;

public class Constant {
    public static final String DIRECTORY_PATH_TEMP = "/.Blur Photo Editor-Temp";
    public static UnifiedNativeAd adAdmobNative;
    private static File dir;
    private static File filepath;

    public static native String baseAdsUrl();

    static {
        System.loadLibrary("native-lib");
    }

    public static String getFilePath(Context context) {
        filepath = Environment.getExternalStorageDirectory();
        StringBuilder sb = new StringBuilder();
        sb.append(filepath.getAbsolutePath());
        String str = "/";
        sb.append(str);
        sb.append(context.getString(R.string.app_name));
        sb.append(str);
        dir = new File(sb.toString());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isSamsungApps(Context context) {
        for (ApplicationInfo applicationInfo : context.getPackageManager().getInstalledApplications(128)) {
            if (applicationInfo.packageName.equalsIgnoreCase("com.sec.android.app.samsungapps")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAvailable(Intent intent, Context context) {
        return context.getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }

    public static void privacyPolicy(Context context) {
        Builder builder = new Builder(context);
        builder.setTitle((CharSequence) "Privacy Policy");
        builder.setCancelable(false);
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return false;
            }

            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                webView.loadUrl(webResourceRequest.getUrl().toString());
                return false;
            }
        });
        builder.setView((View) webView);
        builder.setPositiveButton((CharSequence) "Agree", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public static String getSecureBaseUrl(String str) {
        try {
            return new String(Base64.decode(str, 0), Key.STRING_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }
}
