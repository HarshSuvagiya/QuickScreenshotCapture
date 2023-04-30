package vishtechno.bkm.quickscreenshotcapture.view.camera;

import android.os.Environment;
import java.io.File;

public final class FroyoAlbumDirFactory extends AlbumStorageDirFactory {
    public File getAlbumStorageDir(String str) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), str);
    }
}
