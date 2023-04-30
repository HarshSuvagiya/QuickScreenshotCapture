package vishtechno.bkm.quickscreenshotcapture.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_FloatWidgetServiceMethodsInterface;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_MainActivityMethodsInterface;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_ScreenshotListInterface;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_ShowDialogMethodsInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

public class VISHTECHNO_PreferenceManager {
    public static float density = 0.0f;
    public static VISHTECHNO_FloatWidgetServiceMethodsInterface floatWidgetServiceMethodsInterface = null;
    public static boolean isColorFragment = false;
    public static boolean isDonationFragment = false;
    public static boolean isHelpDialogOpened = false;
    public static boolean isRooted = false;
    public static boolean isScreenCaptureStarted = false;
    public static boolean isScreenshotDirChanged = false;
    public static boolean isScreenshotListClass = false;
    public static boolean isScreenshotPopUpOpen = false;
    public static VISHTECHNO_MainActivityMethodsInterface mainActivityMethodsInterface;
    public static File screenshotDir;
    public static ArrayList<String> screenshotList = new ArrayList<>();
    public static VISHTECHNO_ScreenshotListInterface screenshotListInterface;
    public static int selectedScreenshotNumber;
    public static ArrayList<Integer> selectedScreenshots = new ArrayList<>();
    public static VISHTECHNO_ShowDialogMethodsInterface showDialogMethodsInterface;
    public static int totalScreenshots;

    public static void printIt(Object obj) {
    }

    public static void printIt(Object obj, Throwable th) {
    }

    public static void toastIt(Context context, String str) {
        if (context != null && str != null) {
            @SuppressLint("WrongConstant") Toast makeText = Toast.makeText(context, str, 0);
            makeText.setGravity(80, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            makeText.show();
        }
    }

    public static void toastItLong(Context context, String str) {
        if (str != null) {
            @SuppressLint("WrongConstant") Toast makeText = Toast.makeText(context, str, 1);
            makeText.setGravity(80, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            makeText.show();
        }
    }

    public static String getScreenshotName() {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(screenshotDir.getAbsolutePath());
        screenshotDir.mkdirs();
        sb.append(File.separator);
        sb.append(format);
        sb.append(".jpeg");
        return sb.toString();
    }

    public static void setSharedPref(Context context, String str, Object obj) {
        Editor edit = context.getSharedPreferences("squick", 0).edit();
        if (obj instanceof String) {
            StringBuilder sb = new StringBuilder();
            sb.append("SHARED PREF SET ");
            sb.append(str);
            sb.append(" ");
            sb.append(obj);
            sb.append(" STRING");
            printIt(sb.toString());
            edit.putString(str, (String) obj);
        } else if (obj instanceof Boolean) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("SHARED PREF SET ");
            sb2.append(str);
            sb2.append(" ");
            sb2.append(obj);
            sb2.append(" BOOLEAN");
            printIt(sb2.toString());
            edit.putBoolean(str, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Integer) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("SHARED PREF SET ");
            sb3.append(str);
            sb3.append(" ");
            sb3.append(obj);
            sb3.append(" INTEGER");
            printIt(sb3.toString());
            edit.putInt(str, ((Integer) obj).intValue());
        }
        edit.apply();
    }

    public static Object getSharedPref(Context context, String str) {
        return context.getSharedPreferences("squick", 0).getAll().get(str);
    }

    public static boolean isTablet(Context context) {
        boolean z = (context.getResources().getConfiguration().screenLayout & 15) == 4;
        boolean z2 = (context.getResources().getConfiguration().screenLayout & 15) == 3;
        if (z || z2) {
            return true;
        }
        return false;
    }

    public static void refreshGallery(String str, Context context) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(str);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, null, null);
    }

    public static void performVibrate(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.VIBRATE") == 0) {
            @SuppressLint("WrongConstant") Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(30);
            }
        }
    }

//    public static void getRamUsage() {
//        Runtime runtime = Runtime.getRuntime();
//        long maxMemory = runtime.maxMemory() / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
//        long freeMemory = (runtime.totalMemory() - runtime.freeMemory()) / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED;
//        long j = maxMemory - freeMemory;
//        StringBuilder sb = new StringBuilder();
//        sb.append("MAX HEAP MEMORY : ");
//        sb.append(maxMemory);
//        sb.append(" MB | USED MEMORY : ");
//        sb.append(freeMemory);
//        sb.append(" MB | FREE MEMORY : ");
//        sb.append(j);
//        sb.append(" MB");
//        printIt(sb.toString());
//    }
}
