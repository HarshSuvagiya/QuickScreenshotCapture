package vishtechno.bkm.quickscreenshotcapture.helper;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.OutputStream;

public class VISHTECHNO_TakeScreenshotRoot extends AsyncTask<Void, Void, Void> {
    Context context;
    OnRootScreenshotTakenListener onRootScreenshotTakenListener;
    String ssPath;

    public interface OnRootScreenshotTakenListener {
        void getRootedScreenshot(boolean z);
    }

    public VISHTECHNO_TakeScreenshotRoot(Context context2, String str) {
        this.context = context2;
        this.ssPath = str;
    }

    public void setOnRootScreenshotTakenListener(OnRootScreenshotTakenListener onRootScreenshotTakenListener2) {
        this.onRootScreenshotTakenListener = onRootScreenshotTakenListener2;
        execute(new Void[0]);
    }


    public void onPreExecute() {
        super.onPreExecute();
    }


    public Void doInBackground(Void... voidArr) {
        try {
            Process exec = Runtime.getRuntime().exec("su");
            OutputStream outputStream = exec.getOutputStream();
            StringBuilder sb = new StringBuilder();
            sb.append("screencap -p /");
            sb.append(ssPath);
            outputStream.write(sb.toString().getBytes("ASCII"));
            outputStream.flush();
            outputStream.close();
            exec.waitFor();
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_TakeScreenshotRoot doInBackground ", e);
        }
        return null;
    }

    public void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
        try {
            File file = new File(ssPath);
            StringBuilder sb = new StringBuilder();
            sb.append("FILE SIZE IN ROOT : ");
            sb.append(file.length() / 1 << 10);
            sb.append(" Kb");
            VISHTECHNO_PreferenceManager.printIt(sb.toString());
            if (file.length() == 0) {
                file.delete();
                onRootScreenshotTakenListener.getRootedScreenshot(true);
                return;
            }
            onRootScreenshotTakenListener.getRootedScreenshot(false);
        } catch (Exception e) {
            onRootScreenshotTakenListener.getRootedScreenshot(true);
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_TakeScreenshotRoot onPostExecute ", e);
        }
    }
}
