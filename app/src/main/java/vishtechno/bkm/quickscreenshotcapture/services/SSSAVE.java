package vishtechno.bkm.quickscreenshotcapture.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_TakeScreenshot;

import static android.content.ContentValues.TAG;
import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class SSSAVE {

    Context mContext;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    String mPath;

    ScreenshotListener mScreenshotListener;
    boolean mB;

    public interface ScreenshotListener {
        void onForcedStop();

        void onOutOfMemory(File file);

        void onProjectionNotAvailable();

        void onScreenshotTaken(String str);

        void onUnsupported(String str);
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (!mB) {
                mB = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLog("Image Available Start");
                        stopProjection();
                        Image image = null;
                        FileOutputStream fos = null;
                        File file = new File(mPath);
                        try {
                            try {
                                image = reader.acquireLatestImage();
                                if (image != null) {
                                    Image.Plane[] planes = image.getPlanes();
                                    ByteBuffer buffer = planes[0].getBuffer();
                                    int pixelStride = planes[0].getPixelStride();
                                    int rowStride = planes[0].getRowStride();
                                    int rowPadding = rowStride - pixelStride * mWidth;

                                    // create bitmap
                                    Bitmap createBitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                                    createBitmap.copyPixelsFromBuffer(buffer);
                                    buffer.clear();
                                    createBitmap = cropTop(createBitmap);
//                        bitmap = Bitmap.createBitmap(createBitmap, 0, 0, width, height);
                                    int w = createBitmap.getWidth();
                                    int h = createBitmap.getHeight();
                                    if (w >= mWidth) {
                                        w = mWidth;
                                    }
                                    if (h >= mHeight) {
                                        h = mHeight;
                                    }
                                    createBitmap = Bitmap.createBitmap(createBitmap, 0, 0, w, h);
                                    VISHTECHNO_TakeScreenshot.mBitmap = createBitmap;
                                    // write bitmap to a file
                                    fos = new FileOutputStream(mPath);
                                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    IMAGES_PRODUCED++;
                                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mScreenshotListener.onScreenshotTaken(file.getAbsolutePath());
                                        }
                                    });
                                }
                            } catch (OutOfMemoryError e) {
                                mScreenshotListener.onOutOfMemory(file);
                            } catch (UnsupportedOperationException e) {
                                mScreenshotListener.onUnsupported(file.getAbsolutePath());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }

//                if (bitmap != null) {
//                    bitmap.recycle();
//                }
//
//                if (image != null) {
//                    image.close();
//                }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mScreenshotListener.onForcedStop();
                                }
                            });
                        }
                    }
                }, 200);
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            showLog("Stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    public SSSAVE(Context context, Intent data, String path, ScreenshotListener screenshotListener) {
        mB = false;
        showLog("Capture Start");
        mContext = context;
        mScreenshotListener = screenshotListener;
        mPath = path;

        if (data == null) {
            screenshotListener.onProjectionNotAvailable();
            return;
        }

        mProjectionManager = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (mProjectionManager == null) {
            screenshotListener.onProjectionNotAvailable();
            return;
        }

        try {
            sMediaProjection = mProjectionManager.getMediaProjection(-1, data);
        } catch (Exception e) {
            showLog(e.toString());
            screenshotListener.onProjectionNotAvailable();
        }

        if (sMediaProjection != null) {
            // display metrics
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            mDensity = metrics.densityDpi;

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    mHandler = new Handler();
                    Looper.loop();
                }
            }.start();

            try {
                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register media projection stop callback
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            } catch (Exception e) {
                showLog(e.toString());
                mScreenshotListener.onProjectionNotAvailable();
            }

        } else {
            mScreenshotListener.onProjectionNotAvailable();
        }
    }

    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        defaultDisplay.getRealSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (sMediaProjection != null) {
                        sMediaProjection.stop();
                    }
                    mHandler.removeCallbacksAndMessages(null);
                }
            });
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
}
