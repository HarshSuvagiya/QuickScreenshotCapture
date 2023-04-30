package vishtechno.bkm.quickscreenshotcapture.helper;

import android.os.AsyncTask;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import androidx.annotation.Nullable;

public class VISHTECHNO_SetScreenshotList extends AsyncTask<Void, Void, Void> {
    private boolean isUpdating;
    private String screenshotPath;

    public VISHTECHNO_SetScreenshotList(boolean z, @Nullable String str) {
        this.isUpdating = z;
        this.screenshotPath = str;
    }


    public Void doInBackground(Void... voidArr) {
        try {
            if (!isUpdating) {
                VISHTECHNO_PreferenceManager.screenshotList.clear();
                File[] listFiles = VISHTECHNO_PreferenceManager.screenshotDir.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    Arrays.sort(listFiles, new Comparator() {
                        public int compare(Object obj, Object obj2) {
                            return Long.compare(((File) obj2).lastModified(), ((File) obj).lastModified());
                        }
                    });
                    for (File file : listFiles) {
                        if (file.isFile() && (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))) {
                            VISHTECHNO_PreferenceManager.screenshotList.add(file.getPath());
                        }
                    }
                }
            } else if (this.screenshotPath != null) {
                VISHTECHNO_PreferenceManager.screenshotList.add(0, screenshotPath);
            }
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_SetScreenshotList in doInBackground", e);
        }
        return null;
    }


    public void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
        VISHTECHNO_PreferenceManager.totalScreenshots = VISHTECHNO_PreferenceManager.screenshotList.size();
        if (VISHTECHNO_PreferenceManager.screenshotListInterface != null) {
            VISHTECHNO_PreferenceManager.screenshotListInterface.onScreenshotListAvailable(isUpdating, screenshotPath);
        }
    }
}
