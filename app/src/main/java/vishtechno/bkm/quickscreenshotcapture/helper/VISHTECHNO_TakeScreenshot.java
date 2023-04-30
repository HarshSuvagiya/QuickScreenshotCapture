package vishtechno.bkm.quickscreenshotcapture.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjection.Callback;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_ScreenCaptureActivity;

import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class VISHTECHNO_TakeScreenshot extends AsyncTask<Object, Void, Void> {
    private int height;
    private boolean isOutOfMemoryError = false;

    public boolean isStopped = false;
    private boolean isUnsupportedError = false;

    public ScreenshotListener screenshotListener;
    private String screenshotName;
    private int width;
    MediaProjection mediaProjection;

    Context mContext;
    public static Bitmap mBitmap;

    public interface ScreenshotListener {
        void onForcedStop();

        void onOutOfMemory(File file);

        void onProjectionNotAvailable();

        void onScreenshotTaken(String str);

        void onUnsupported(String str);
    }

    @SuppressLint("NewApi")
    public VISHTECHNO_TakeScreenshot(Context context, Intent intent, String str, ScreenshotListener screenshotListener2) {
        mContext=context;
        screenshotName = str;
        screenshotListener = screenshotListener2;
        if (intent == null) {
            screenshotListener.onProjectionNotAvailable();
            return;
        }
        @SuppressLint("WrongConstant") MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService("media_projection");
        if (mediaProjectionManager == null) {
            screenshotListener.onProjectionNotAvailable();
            return;
        }
        mediaProjection = null;
        try {
            mediaProjection = mediaProjectionManager.getMediaProjection(-1, intent);
        } catch (Exception e) {
            showLog(e.toString());
        }
        if (mediaProjection == null) {
            screenshotListener.onProjectionNotAvailable();
            return;
        }
        int i = context.getResources().getDisplayMetrics().densityDpi;
        @SuppressLint("WrongConstant") WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager != null) {
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            width = point.x;
            height = point.y;
        }
        final ImageReader newInstance = ImageReader.newInstance(width, height, 1, 2);
        final VirtualDisplay createVirtualDisplay = mediaProjection.createVirtualDisplay("screencap", width, height, i, 25, newInstance.getSurface(), null, null);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!isStopped) {
                    VISHTECHNO_PreferenceManager.printIt("MEDIA PROJECTION STOPPING FORCEFULLY");
                    mediaProjection.stop();
                    screenshotListener.onForcedStop();
                }
            }
        }, 2000);
        newInstance.setOnImageAvailableListener(new OnImageAvailableListener() {
            public void onImageAvailable(ImageReader imageReader) {
                mediaProjection.stop();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getStatus() != Status.RUNNING) {
                            try{ execute(new Object[]{imageReader}); }catch (Exception e){showLog(e.toString());}
                        }
                    }
                },100);
            }
        }, null);

        mediaProjection.registerCallback(new Callback() {
            public void onStop() {
                super.onStop();
                isStopped = true;
                try {
                    if (createVirtualDisplay != null) {
                        createVirtualDisplay.release();
                    }
                    newInstance.setOnImageAvailableListener(null, null);
                    mediaProjection.unregisterCallback(this);
                } catch (Exception e) {
                    showLog("SS Save Error" + e.toString());
                }
            }
        }, null);
    }

    public void onCancelled() {
        super.onCancelled();
        VISHTECHNO_PreferenceManager.printIt("ON CANCELLED");
    }

    public Void doInBackground(Object... objArr) {
        FileOutputStream fileOutputStream;
        Image image;
        Bitmap bitmap;
        ImageReader imageReader = (ImageReader) objArr[0];
        Bitmap bitmap2 = null;
        try {
            image = imageReader.acquireLatestImage();
            if (image != null) {
                try {
                    fileOutputStream = new FileOutputStream(screenshotName);
                } catch (OutOfMemoryError e) {
                    showLog("SS Save Error" + e.toString());
                    e = e;
                    bitmap = null;
                    fileOutputStream = null;
                    VISHTECHNO_PreferenceManager.printIt("OUT OF MEMORY : ", e);
                    isOutOfMemoryError = true;
                    if (image != null) {
                    }
                    imageReader.close();
                    if (fileOutputStream != null) {
                    }
                } catch (Exception e2) {
                    showLog("SS Save Error" + e2.toString());
                    bitmap = null;
                    fileOutputStream = null;
                    try {
                        if (e2 instanceof UnsupportedOperationException) {
                        }
                        if (image != null) {
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                        }
                    } catch (Throwable th) {
                        th = th;
                        bitmap2 = bitmap;
                        if (image != null) {
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                        }
                        if (bitmap2 != null) {
                        }
                        throw th;
                    }
                } catch (Throwable th2) {

                    fileOutputStream = null;
                    if (image != null) {
                    }
                    imageReader.close();
                    if (fileOutputStream != null) {
                    }
                    if (bitmap2 != null) {
                    }
                    throw th2;
                }
                try {

                    Plane[] planes = image.getPlanes();
//                    ByteBuffer buffer = planes[0].getBuffer();
//                    int pixelStride = planes[0].getPixelStride();

                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;

                    // create bitmap
                    Bitmap createBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Config.ARGB_8888);

//                    Bitmap createBitmap = Bitmap.createBitmap(width + ((planes[0].getRowStride() - (width * pixelStride)) / pixelStride), height, Config.ARGB_8888);
                    try {
                        createBitmap.copyPixelsFromBuffer(buffer);
                        buffer.clear();
                        createBitmap = cropTop(createBitmap);
//                        bitmap = Bitmap.createBitmap(createBitmap, 0, 0, width, height);
                        int w = createBitmap.getWidth();
                        int h = createBitmap.getHeight();
                        if (w >= width) {
                            w = width;
                        }
                        if (h >= height) {
                            h = height;
                        }
                        bitmap = Bitmap.createBitmap(createBitmap, 0, 0, w, h);
                        mBitmap = bitmap;
                    } catch (OutOfMemoryError e3) {
                        showLog("SS Save Error" + e3.toString());
                        Bitmap bitmap3 = createBitmap;
                        bitmap = bitmap3;
                        showLog("SS Save Error OUT OF MEMORY");
                        isOutOfMemoryError = true;
                        if (image != null) {
                            image.close();
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Exception e4) {
                                showLog("SS Save Error" + e4.toString());
                                e4.printStackTrace();
                            }
                        }
                    } catch (Exception e5) {
                        showLog("SS Save Error" + e5.toString());
                        Bitmap bitmap4 = createBitmap;
                        bitmap = bitmap4;
                        if (e5 instanceof UnsupportedOperationException) {
                            isUnsupportedError = true;
                            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService in takeScreenshot UnsupportedOperationException");
                        } else {
                            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService in takeScreenshot in AsyncTask", e5);
                        }
                        if (image != null) {
                            image.close();
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Exception e6) {
                                showLog("SS Save Error" + e6.toString());
                                e6.printStackTrace();
                            }
                        }
                    } catch (Throwable th3) {
                        bitmap2 = createBitmap;
                        if (image != null) {
                            image.close();
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (Exception e7) {
                                showLog("SS Save Error" + e7.toString());
                                e7.printStackTrace();
                            }
                        }
                        if (bitmap2 != null) {
                            bitmap2.recycle();
                        }
                        throw th3;
                    }
                    try {
                        bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
                    } catch (OutOfMemoryError e8) {
                        showLog("SS Save Error" + e8.toString());
                    } catch (Exception e9) {
                        showLog("SS Save Error" + e9.toString());
                        if (e9 instanceof UnsupportedOperationException) {
                        }
                        if (image != null) {
                        }
                        imageReader.close();
                        if (fileOutputStream != null) {
                        }
                    }
                } catch (OutOfMemoryError e10) {
                    showLog("SS Save Error" + e10.toString());
                    bitmap = null;
                    VISHTECHNO_PreferenceManager.printIt("OUT OF MEMORY : ", e10);
                    isOutOfMemoryError = true;
                    if (image != null) {
                    }
                    imageReader.close();
                    if (fileOutputStream != null) {
                    }
                } catch (Exception e11) {
                    showLog("SS Save Error" + e11.toString());
                    bitmap = null;
                    if (e11 instanceof UnsupportedOperationException) {
                    }
                    if (image != null) {
                    }
                    imageReader.close();
                    if (fileOutputStream != null) {
                    }
                } catch (Throwable th4) {
                    if (image != null) {
                    }
                    imageReader.close();
                    if (fileOutputStream != null) {
                    }
                    if (bitmap2 != null) {
                    }
                    throw th4;
                }
            } else {
                bitmap = null;
                fileOutputStream = null;
            }
            if (image != null) {
                image.close();
            }
            imageReader.close();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e12) {
                    showLog("SS Save Error" + e12.toString());
                    e12.printStackTrace();
                }
            }
        } catch (OutOfMemoryError e13) {
            showLog("SS Save Error" + e13.toString());
            bitmap = null;
            image = null;
            fileOutputStream = null;
            VISHTECHNO_PreferenceManager.printIt("OUT OF MEMORY : ", e13);
            isOutOfMemoryError = true;
            if (image != null) {
            }
            imageReader.close();
            if (fileOutputStream != null) {
            }
        } catch (Exception e14) {
            showLog("SS Save Error" + e14.toString());
            bitmap = null;
            image = null;
            fileOutputStream = null;
            if (e14 instanceof UnsupportedOperationException) {
            }
            if (image != null) {
            }
            imageReader.close();
            if (fileOutputStream != null) {
            }
        } catch (Throwable th5) {
            image = null;
            fileOutputStream = null;
            if (image != null) {
            }
            imageReader.close();
            if (fileOutputStream != null) {
            }
            if (bitmap2 != null) {
            }
            try {
                throw th5;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }

    public void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
        if (isUnsupportedError) {
            screenshotListener.onUnsupported(screenshotName);
        } else if (isOutOfMemoryError) {
            File file = new File(screenshotName);
            screenshotListener.onOutOfMemory(file);
            if (file.length() == 0 && file.delete()) {
                VISHTECHNO_PreferenceManager.printIt("FILE DELETED AS OUT OF MEMORY ERROR OCCURRED");
            }
        } else {
            screenshotListener.onScreenshotTaken(screenshotName);
        }
    }


    Bitmap cropTop(Bitmap bit) {
        try {
            int y1 = Help.getStatusBarHeight(mContext);
            int y2 = Help.getNavHeight(mContext);
            Help.AppPreferences preferences = new Help.AppPreferences(mContext);
            int w = bit.getWidth();
            int h = bit.getHeight();
            if (preferences.getExcludeSB() && preferences.getExcludeNB()) {
                Bitmap croppedBitmap;
                if (w > h) {
                    croppedBitmap = Bitmap.createBitmap(bit, 0, y1, w - y2, h - y1);
                } else {
                    croppedBitmap = Bitmap.createBitmap(bit, 0, y1, w, h - (y1 + y2));
                }
                return croppedBitmap;
            } else if (preferences.getExcludeSB()) {
                Bitmap croppedBitmap = Bitmap.createBitmap(bit, 0, y1, w, h - y1);
                return croppedBitmap;
            } else if (preferences.getExcludeNB()) {
                Bitmap croppedBitmap;
                if (w > h) {
                    croppedBitmap = Bitmap.createBitmap(bit, 0, 0, w - y2, h);
                } else {
                    croppedBitmap = Bitmap.createBitmap(bit, 0, 0, w, h - y2);
                }
                return croppedBitmap;
            }
            return bit;
        } catch (Exception e) {
            showLog(e.toString());
            return bit;
        }
    }

    Bitmap cropTop2(Bitmap bit) {
        try {
            int y1 = Help.getStatusBarHeight(mContext);
            Help.AppPreferences preferences = new Help.AppPreferences(mContext);
            if (preferences.getExcludeSB()) {
                Bitmap croppedBitmap = Bitmap.createBitmap(bit, 0, 20, bit.getWidth(), bit.getHeight() - y1);
                return croppedBitmap;
            }
            return bit;
        } catch (Exception e) {
            showLog(e.toString());
            return bit;
        }
    }

}
