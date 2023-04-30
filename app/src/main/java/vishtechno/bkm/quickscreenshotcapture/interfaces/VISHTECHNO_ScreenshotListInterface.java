package vishtechno.bkm.quickscreenshotcapture.interfaces;


import androidx.annotation.Nullable;

public interface VISHTECHNO_ScreenshotListInterface {
    void onScreenshotListAvailable(boolean z, @Nullable String str);

    void onScreenshotsDeleted();
}
