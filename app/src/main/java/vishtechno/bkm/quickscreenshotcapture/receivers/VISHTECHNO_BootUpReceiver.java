package vishtechno.bkm.quickscreenshotcapture.receivers;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;

import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;
import vishtechno.bkm.quickscreenshotcapture.services.VISHTECHNO_FloatWidgetService;

import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class VISHTECHNO_BootUpReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        showLog("Receiver Call");
        VISHTECHNO_PreferenceManager.printIt("ON RECEIVE VISHTECHNO_BootUpReceiver");
        if (!isServiceRunning(context, "VISHTECHNO_FloatWidgetService")) {
            Intent putExtra = new Intent(context, VISHTECHNO_FloatWidgetService.class).putExtra("FROM", "RECEIVER");
            if (VERSION.SDK_INT >= 26) {
                context.startForegroundService(new Intent(context, VISHTECHNO_RecieverService.class));
                context.startForegroundService(putExtra);
            } else {
                context.startService(new Intent(context, VISHTECHNO_RecieverService.class));
                context.startService(putExtra);
            }
        }
    }

    private boolean isServiceRunning(Context context, String str) {
        @SuppressLint("WrongConstant") ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        if (activityManager != null) {
            for (RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                String className = runningServiceInfo.service.getClassName();
                StringBuilder sb = new StringBuilder();
                sb.append(context.getPackageName());
                sb.append(".services.");
                sb.append(str);
                if (className.equals(sb.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
