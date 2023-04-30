package vishtechno.bkm.quickscreenshotcapture.interfaces;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface VISHTECHNO_FloatWidgetServiceMethodsInterface {
    void hideOverlay();

    void onGetResult(boolean z, @Nullable Intent intent, boolean z2);

    void setColor(int i);

    void showResizePopUp();
}
