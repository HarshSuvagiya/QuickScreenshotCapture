package vishtechno.bkm.quickscreenshotcapture.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

import vishtechno.bkm.quickscreenshotcapture.R;

import java.io.File;
import java.io.FileOutputStream;

public class VISHTECHNO_help {


    public static String creation_path;
    public static int from;

    public static void saveBitmap(Context mContext, Bitmap bitmap) {

        File myDir = new File(Environment.getExternalStorageDirectory()
                .toString()
                + "/"
                + mContext.getResources().getString(R.string.app_name));
        myDir.mkdirs();
        File file = new File(myDir, mContext.getResources().getString(
                R.string.app_name)
                + System.currentTimeMillis() + ".jpg");
        String filepath = file.getPath();
        if (file.exists()) {
            file.delete();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

            creation_path = file.getAbsolutePath();

            MediaScannerConnection.scanFile(mContext, new String[]{creation_path}, new String[]{"image/*"}, null);
            ostream.close();

        } catch (Exception e) {

        }

    }

    static public void deleteDirectory(File path) {
        if (path.exists())
        {
            path.delete();
            boolean b = path.isDirectory();
        }
    }

}
