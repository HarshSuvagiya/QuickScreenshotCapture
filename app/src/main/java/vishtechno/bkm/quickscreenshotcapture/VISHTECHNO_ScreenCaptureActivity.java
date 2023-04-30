package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;

import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class VISHTECHNO_ScreenCaptureActivity extends Activity {
    private boolean afterClick;
    Activity mContext;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Help.mSSActivity = this;
        Help.FS(this);
        mContext = this;

        afterClick = getIntent().getBooleanExtra("afterClick", false);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            try {
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 100);
            } catch (Exception e) {
                showLog(e.toString());
                Help.Toast(this, "Something went wrong");
                finishAndRemoveTask();
            }
        } else if (VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface != null) {
//            showLog("Capture Data null");
            VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface.onGetResult(false, null, afterClick);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
//        showLog("Capture Result");
        if (i != 100 || intent == null) {
//            showLog("Capture null");
            if (VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface != null) {
//                showLog("Capture in null");
                VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface.onGetResult(false, intent, afterClick);
            }
        } else if (VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface != null) {
//            showLog("Capture not");
            VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface.onGetResult(true, intent, afterClick);
        }

        finishAndRemoveTask();
    }
}
