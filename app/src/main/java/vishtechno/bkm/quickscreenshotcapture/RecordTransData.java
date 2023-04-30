package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon;


public class RecordTransData extends Activity {

    Context mContext;
    int REQUEST_CODE_SCREEN_CAPTURE = 111;
    Help.AppPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        Help.FS(this);
        preferences = new Help.AppPreferences(this);

        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final Intent permissionIntent = manager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, REQUEST_CODE_SCREEN_CAPTURE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_SCREEN_CAPTURE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                ScreenRecord_Icon.setProjection(data);
            } else {
                stopService(new Intent(mContext, ScreenRecord_Icon.class));
                preferences.setisScreenRec(false);
            }
            finish();
        }
    }
}
