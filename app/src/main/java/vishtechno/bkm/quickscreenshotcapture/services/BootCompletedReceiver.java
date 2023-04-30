package vishtechno.bkm.quickscreenshotcapture.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_StartActivity;

import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        showLog("ReBOOt");
        Help.AppPreferences preferences = new Help.AppPreferences(context);
        if (preferences.getisService()) {
            showLog("in1");
            if (Build.VERSION.SDK_INT >= 26) {
                showLog("in2");
                context.startForegroundService(new Intent(context, VISHTECHNO_FloatWidgetService.class));
            } else {
                showLog("in3");
                context.startService(new Intent(context, VISHTECHNO_FloatWidgetService.class));
            }
        }
        if(preferences.getisScreenRec()){
            context.startService(new Intent(context, ScreenRecord_Icon.class));
        }
    }
}