package vishtechno.bkm.quickscreenshotcapture;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {

    public static Typeface myBold, myRegular, myBahnschrift;

    @Override
    public void onCreate() {
        super.onCreate();

        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        myBold = Typeface.createFromAsset(getAssets(), "arialbd.ttf");
        myRegular = Typeface.createFromAsset(getAssets(), "arial.ttf");
        myBahnschrift = Typeface.createFromAsset(getAssets(), "bahnschrift.ttf");
    }
}