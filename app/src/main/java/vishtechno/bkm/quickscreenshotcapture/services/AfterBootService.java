package vishtechno.bkm.quickscreenshotcapture.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_StartActivity;

import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class AfterBootService extends Service {
    public AfterBootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showLog("service start");

    }
}
