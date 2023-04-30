package vishtechno.bkm.quickscreenshotcapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;

import static android.content.Context.POWER_SERVICE;

public class Help {
    public static Uri mUri;
    public static int width, height;
    public static Bitmap mBitmap, mOriginal, stickBit;
    private static final String TAG = "Jay_Help";
    public static String Image_Path, mWaterMarkPath, mVideoPath,
            mMusicPath, mTempPath, mAudioPath, mSS;
    public static boolean mBoolean, mAddSS;
    public static TextView mTextView;

    public static ArrayList<ImageData> myList = new ArrayList<>();

    public static String PhoneNumber = "";
    public static String Password = "";
    public static String Email = "";
    public static String Name = "";
    public static String Profile_Image = "";
    public static Activity mSRActivity, mSSActivity;

    public static Bitmap bitmapResize(Bitmap bit, int width, int height) {

        int layoutwidth = width;
        int layoutheight = height;
        int imagewidth = bit.getWidth();
        int imageheight = bit.getHeight();
        int newwidth = 0;
        int newheight = 0;
        if (imagewidth >= imageheight) {
            newwidth = layoutwidth;
            newheight = (newwidth * imageheight) / imagewidth;
            if (newheight > layoutheight) {
                newwidth = (layoutheight * newwidth) / newheight;
                newheight = layoutheight;
            }
        } else {
            newheight = layoutheight;
            newwidth = (newheight * imagewidth) / imageheight;
            if (newwidth > layoutwidth) {
                newheight = (newheight * layoutwidth) / newwidth;
                newwidth = layoutwidth;
            }
        }
        Bitmap b56 = Bitmap.createScaledBitmap(bit, newwidth, newheight, true);
        return b56;
    }

    public static boolean isVideoHaveAudioTrack(String path) {
        boolean audioTrack = false;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        if (hasAudioStr != null && hasAudioStr.equals("yes")) {
            audioTrack = true;
        } else {
            audioTrack = false;
        }

        return audioTrack;
    }

    public static boolean checkInternet(Context mContext) {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static void setLaySize(View view, int width, int height) {

        int layoutwidth = view.getWidth();
        int layoutheight = view.getHeight();
        int imagewidth = width;
        int imageheight = height;
        int newwidth = 0;
        int newheight = 0;
        if (imagewidth >= imageheight) {
            newwidth = layoutwidth;
            newheight = (newwidth * imageheight) / imagewidth;
            if (newheight > layoutheight) {
                newwidth = (layoutheight * newwidth) / newheight;
                newheight = layoutheight;
            }
        } else {
            newheight = layoutheight;
            newwidth = (newheight * imagewidth) / imageheight;
            if (newwidth > layoutwidth) {
                newheight = (newheight * layoutwidth) / newwidth;
                newwidth = layoutwidth;
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(newwidth, newheight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(params);
    }

    public static void showLog(String str) {
        Log.e("999999", str);
    }

    public static float digit_2(float f) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float twoDigitsF = Float.valueOf(decimalFormat.format(f));
        return twoDigitsF;
    }

    public static float digit(float f) {
        DecimalFormat decimalFormat = new DecimalFormat("#");
        float twoDigitsF = Float.valueOf(decimalFormat.format(f));
        return twoDigitsF;
    }

    public static Bitmap getBitmap(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static float rotationToStartPoint(MotionEvent event, Matrix matrix) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        double arc = Math.atan2(event.getY(0) - y, event.getX(0) - x);
        return (float) Math.toDegrees(arc);
    }

    public static PointF midPointToStartPoint(MotionEvent event, Matrix matrix) {
        PointF mid = new PointF();
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
        return mid;
    }

    public static Bitmap getBitmapFromView(View view) {
        int mWidth = view.getWidth();
        int mHeight = view.getHeight();
        if (mWidth > 0 && mHeight > 0) {
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
//        Drawable bgDrawable =view.getBackground();
//        if (bgDrawable!=null)
//            bgDrawable.draw(canvas);
//        else
//            canvas.drawColor(Color.WHITE);
            view.draw(canvas);
            return returnedBitmap;
        }
        return null;
    }

    public static int w(int v) {
        return (width * v / 1080);
    }

    public static int h(int v) {
        return (height * v / 1920);
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor, Typeface typeface) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public static String getMimeType(String url) {
        String extension = url.substring(url.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }

    public static void showVideoResolution(String path) {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            int w = player.getVideoWidth();
            int h = player.getVideoHeight();
            player.reset();
            showLog(w + "x" + h);
        } catch (Exception e) {
            showLog("Error in file");
        }
    }

    public static Bitmap drawMultilineTextToBitmap(Context gContext, String gText, int color, Typeface typeface) {

        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        // so we need to convert it to mutable one
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        // text size in pixels
        if (gText.length() < 25) {
            paint.setTextSize((int) (60 * scale));
        } else if (gText.length() < 75) {
            paint.setTextSize((int) (40 * scale));
        } else {
            paint.setTextSize((int) (20 * scale));
        }
        paint.setTypeface(typeface);
        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = (bitmap.getHeight() - textHeight) / 2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public static void Toast(Context cn, String str) {
        Toast.makeText(cn, str, Toast.LENGTH_SHORT).show();
    }

    public static void ToastL(Context cn, String str) {
        Toast.makeText(cn, str, Toast.LENGTH_LONG).show();
    }

    public static Bitmap getMask(Context mContext, Bitmap bit, Bitmap maskBitmap) {

        int w = bit.getWidth();
        int h = bit.getHeight();

        bit = Bitmap.createScaledBitmap(bit, w, h, true);
        maskBitmap = Bitmap.createScaledBitmap(maskBitmap, w, h, true);
        Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(),
                maskBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(bit, 0, 0, null);
        mCanvas.drawBitmap(maskBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);

        return result;
    }

    public static String getImagePath(Context mContext, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static GradientDrawable createGradient(int color1, int color2, GradientDrawable.Orientation ort) {
        GradientDrawable gd = new GradientDrawable(ort, new int[]{color1, color2});
        gd.setCornerRadius(0f);
        return gd;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        Log.i(TAG, e.getMessage());
                        return null;
                    }
                }

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap enhanceImage(Bitmap mBitmap, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]{contrast, 0, 0, 0,
                brightness, 0, contrast, 0, 0, brightness, 0, 0, contrast, 0,
                brightness, 0, 0, 0, 1, 0});
        Bitmap mEnhancedBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
                mBitmap.getHeight(), mBitmap.getConfig());
        Canvas canvas = new Canvas(mEnhancedBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        return mEnhancedBitmap;
    }

    public static Bitmap updateSat(Bitmap src, float settingSat) {

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult = Bitmap
                .createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        return bitmapResult;
    }

    public static Bitmap LRBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1f, 1f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public static Bitmap UDBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.preScale(1f, -1f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public static Typeface getTypeface(Context mContext, String str) {
        return Typeface.createFromAsset(mContext.getAssets(), str);
    }

    public static void FS(Activity mActivity) {
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void FS2(Activity mActivity) {
        mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void HideKeyBoard(Activity mActivity) {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void next(Context mContext, Class cls) {
        mContext.startActivity(new Intent(mContext, cls));
    }

    public static void nextwithnew(Context mContext, Class cls) {
        Intent intent = new Intent(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public static void nextwithnewclear(Context mContext, Class cls) {
        Intent intent = new Intent(mContext, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public static void startResult(Activity mContext, Class cls, int code) {
        Intent intent = new Intent(mContext, cls);
        mContext.startActivityForResult(intent, code);
    }

    public static void PenddngStart(Context mContext, Class cls) {
        Intent intent = new Intent(mContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(Activity mActivity, Class cls, File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
            if (mActivity != null) {
                Intent it = new Intent(mActivity, cls);
                it.putExtra("path", zipFile.getAbsolutePath().replace(".zip", ""));
                mActivity.startActivity(it);
                mActivity.finish();
            }
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasNavBar(Context mContext) {
        Resources resources = mContext.getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static int getNavBarHeight(Context context) {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = context.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(context)) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }

        return size;
    }

    public static String[] arrayMerge(String[] arr1, String[] arr2) {
        String[] arr = new String[arr1.length + arr2.length];

        int l = arr1.length;
        for (int i = 0; i < arr.length; i++) {
            if (i < l) {
                arr[i] = arr1[i];
            } else {
                arr[i] = arr2[i - l];
            }
        }
        return arr;
    }

    public static int videoBitRate(String path) {
        int rate = 0;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String BitRate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        try {
            rate = Integer.parseInt(BitRate);
        } catch (Exception e) {
            showLog(e.toString());
        }

        return rate;
    }

    private static boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static int getRealHeight(Context context) {
        final DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Method mGetRawH = null;

        int realHeight;
        // For JellyBeans and onward
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
            realHeight = metrics.heightPixels;
        } else {
            try {
                // Below Jellybeans you can use reflection method
                mGetRawH = Display.class.getMethod("getRawHeight");
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                display.getMetrics(metrics);
                realHeight = metrics.heightPixels;
            }
        }

        return realHeight;
    }

    public static int getScreenWidth(Context context) {
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (wm != null) {
                wm.getDefaultDisplay().getSize(point);
                return point.x;
            }
        }
        return 320;
    }

    public static String getString(Context mContext, int val) {
        return mContext.getResources().getString(val);
    }

    public static String getVideoDate(String path) {
        String date = "";
        File file = new File(path);
        if (file.exists()) {
            date = getDate(file.lastModified());
        }
        return date;
    }

    public static String getVideoDurationS(String path) {
        String duration = "";
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            duration = getDuration(player.getDuration());
            player.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    public static long getVideoDurationL(String path) {
        long duration = 0;
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            duration = player.getDuration();
            player.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }

    public static String getTime(long l) {
        String dateString = DateFormat.format("hh:mm aa", new Date(l)).toString();
        return dateString;
    }

    public static String getDuration(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static String getDuration2(long val) {
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(val),
                TimeUnit.MILLISECONDS.toSeconds(val)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(val)));
    }

    public static String getDate(long l) {
        String dateString = DateFormat.format("dd-MMM-yyyy", new Date(l)).toString();
        return dateString;
    }

    public static String getDateFull(long l) {
        String dateString = DateFormat.format("dd-MMMM-yyyy", new Date(l)).toString();
        return dateString;
    }

    public static String getDateTime(long l) {
        String dateString = DateFormat.format("dd-MMM-yyyy hh-mm-ss", new Date(l)).toString();
        return dateString;
    }

    public static String getDayTime(long l) {
        String dateString = DateFormat.format("DD,MMM dd - hh-mm a", new Date(l)).toString();
        return dateString;
    }

    public static int getDay(long l) {
        String dateString = DateFormat.format("dd", new Date(l)).toString();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);
//        return Integer.parseInt(dateString);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(long l) {
        String dateString = DateFormat.format("MM", new Date(l)).toString();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);
//        return Integer.parseInt(dateString);
        return calendar.get(Calendar.MONTH);
    }

    public static String getMonth2(long l) {
        String dateString = DateFormat.format("MMM", new Date(l)).toString();
        return dateString;
    }

    public static int getYear(long l) {
        String dateString = DateFormat.format("yyyy", new Date(l)).toString();
        return Integer.parseInt(dateString);
    }

    public static int getHour(long l) {
        String dateString = DateFormat.format("hh", new Date(l)).toString();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);
//        return Integer.parseInt(dateString);
        return calendar.get(Calendar.HOUR);
    }

    public static int getMinutes(long l) {
        String dateString = DateFormat.format("mm", new Date(l)).toString();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(l);
//        return Integer.parseInt(dateString);
        return calendar.get(Calendar.MINUTE);
    }

    public static boolean hasChar(String string) {
        boolean b = false;
        if (string.matches(".*[0-9].*") || string.matches(".*[A-Z].*") || string.matches(".*[a-z].*")) {
            b = true;
        }
        return b;
    }

    public static void setSize(View view, int width, int height, boolean b) {
        view.getLayoutParams().width = w(width);
        if (b) {
            view.getLayoutParams().height = h(height);
        } else {
            view.getLayoutParams().height = w(height);
        }
    }

    public static void seekThumb(SeekBar sb, int color) {
        sb.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static void gone(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void visible(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void invisible(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setCenter(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
    }

    public static void setCenterHorizontal(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
    }

    public static void setCenterVertical(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
    }

    public static void setMargin(View view, int l, int t, int r, int b, boolean bo) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (bo) {
            marginLayoutParams.setMargins(w(l), h(t), w(r), h(b));
        } else {
            marginLayoutParams.setMargins(w(l), w(t), w(r), w(b));
        }
    }

    public static void set_share_rate(final Context mContext, View share, View rate) {
        if (share != null) {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String shareBody = "https://play.google.com/store/apps/details?id="
                            + mContext.getPackageName();
                    Intent sharingIntent = new Intent(
                            Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,
                            shareBody);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            });
        }

        if (rate != null) {
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mContext.startActivity(new Intent("android.intent.action.VIEW", Uri
                                .parse("market://details?id=" + mContext.getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        mContext.startActivity(new Intent(
                                "android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/apps/details?id="
                                        + mContext.getPackageName())));
                    }
                }
            });
        }
    }

    public static String getDay(int mYear, int mMonth, int mDay) {
        String day = "";
        Calendar mycal = Calendar.getInstance();
        mycal.set(mYear, mMonth, mDay);
        int dayOfWeek = mycal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                day = "Sun";
                break;
            case Calendar.MONDAY:
                day = "Mon";
                break;
            case Calendar.TUESDAY:
                day = "Tue";
                break;
            case Calendar.WEDNESDAY:
                day = "Wed";
                break;
            case Calendar.THURSDAY:
                day = "Thu";
                break;
            case Calendar.FRIDAY:
                day = "Fri";
                break;
            case Calendar.SATURDAY:
                day = "Sat";
                break;
        }
        return day;
    }

    public static String getMonth(int mYear, int mMonth, int mDay) {
        String day = "";
        Calendar mycal = Calendar.getInstance();
        mycal.set(mYear, mMonth, mDay);
        int dayOfWeek = mycal.get(Calendar.MONTH);
        switch (dayOfWeek) {
            case Calendar.JANUARY:
                day = "Jan";
                break;
            case Calendar.FEBRUARY:
                day = "Feb";
                break;
            case Calendar.MARCH:
                day = "Mar";
                break;
            case Calendar.APRIL:
                day = "Apr";
                break;
            case Calendar.MAY:
                day = "May";
                break;
            case Calendar.JUNE:
                day = "Jun";
                break;
            case Calendar.JULY:
                day = "Jul";
                break;
            case Calendar.AUGUST:
                day = "Aug";
                break;
            case Calendar.SEPTEMBER:
                day = "Sep";
                break;
            case Calendar.OCTOBER:
                day = "Oct";
                break;
            case Calendar.NOVEMBER:
                day = "Nov";
                break;
            case Calendar.DECEMBER:
                day = "Dec";
                break;
        }
        return day;
    }

    public static void hideKeyBoard(Context mContext, View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static String capitaliseName(String name) {
        String collect[] = name.split(" ");
        String returnName = "";
        for (int i = 0; i < collect.length; i++) {
            collect[i] = collect[i].trim().toLowerCase();
            if (collect[i].isEmpty() == false) {
                returnName = returnName + collect[i].substring(0, 1).toUpperCase() + collect[i].substring(1) + " ";
            }
        }
        return returnName.trim();
    }

    public static String capitaliseOnlyFirstLetter(String data) {
        return data.substring(0, 1).toUpperCase() + data.substring(1);
    }

    public static String getThemePath(Context mContext) {
        return Environment.getExternalStorageDirectory() + "/Android/data/"
                + mContext.getPackageName() + "/files/.Theme/";
    }

    public static String getFolderPath(Context mContext) {
        return Environment.getExternalStorageDirectory() + "/" + mContext.getResources().getString(R.string.app_name) + "/";
    }

    public static String getFileSize(long l) {
        double length = (double) l;
        Double.isNaN(length);
        double d = length / 1024.0d;
        double d2 = d / 1024.0d;
        String concat;
        if (d2 > 1.0d) {
            concat = new DecimalFormat("0.00").format(d2).concat("MB");
        } else {
            concat = new DecimalFormat("0").format(Math.round(d)).concat("KB");
        }
        return concat;
    }

    public static String getFileName(String path) {
        String name = new File(path).getName();
        String[] sp = name.split("\\.");
        return sp[0];
    }

    public static String getAddress(Context mContext, double lat, double lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        String add = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);

            Log.e("IGA", "Address" + add);
            return add;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
            return add;
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            showLog(e.toString());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

//    public static LatLng getLocationFromAddress(Context context, String str) {
//        LatLng latLng = null;
//        Geocoder geocoder = new Geocoder(context);
//        List<Address> addressList = null;
//        try {
//            addressList = geocoder.getFromLocationName(str, 1);
//            Address address = addressList.get(0);
//            latLng = new LatLng(address.getLatitude(), address.getLongitude());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return latLng;
//    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static ProgressDialog setPD(Context mContext, String msg, boolean canCancel) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(canCancel);
        return progressDialog;
    }

    public static void setLay(View view, int width, int height, boolean b) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = w(width);
        if (b) {
            params.height = h(height);
        } else {
            params.width = w(height);
        }
    }

    public static BitmapDrawable RepeatedBitmap(Context mContext, int res, Bitmap bit) {
        Bitmap bmp;
        if (res == -1) {
            bmp = bit;
        } else {
            bmp = BitmapFactory.decodeResource(mContext.getResources(), res);
        }
        Bitmap.createScaledBitmap(bmp, width * 100 / 720, width * 100 / 720, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return bitmapDrawable;
    }

    public static ArrayList<String> setList(Context mContext, String folder) {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        try {
            String[] arr = mContext.getAssets().list(folder);
            for (String str : arr) {
                list.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<String> ConvertList(String[] arr) {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        try {
            for (String str : arr) {
                list.add(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getBatteryPercentage(Context mContext) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        return level;
    }

    public static int getColor(Context mContext, int color) {
        return mContext.getResources().getColor(color);
    }

    public static void showKeyboard(Activity activity, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(editText, 0);
            inputMethodManager.toggleSoftInput(2, 0);
        }
    }

    public static class AppPreferences {
        private static String PREFS_NAME;
        private final SharedPreferences preferences;
        private final SharedPreferences.Editor editor;

        String SSSound = "SSSound";
        String Notifi = "Notifi";
        String Dis = "Dis";
        String Shake = "Shake";
        String SelColor = "SelColor";
        String isService = "isService";
        String isScreenRec = "isScreenRec";
        String isAudio = "isAudio";
        String Quality = "Quality";
        String X = "X0";
        String Y = "Y0";
        String XH = "XH0";
        String YH = "YH0";
        String SRShake = "SRShake1";
        String SRNoti = "SRNoti";
        String SRDis = "SRDis";
        String PowerCD = "PowerCD1";
        String HOA = "HOA";
        String AudioBitrate = "AudioBitrate0";
        String AudioSampleRate = "AudioSampleRate0";
        String AudioChannel = "AudioChannel0";
        String VideoBitrate = "VideoBitrate0";
        String VideoFPS = "VideoFPS0";
        String VideoFI = "VideoFI0";
        String ArrangeCase = "ArrangeCase";
        String MiPopCase = "MiPopCase";

        String ImageType = "ImageType";
        String ImageQuality = "ImageQuality";
        String OpenType = "OpenType";
        String ExcludeSB = "ExcludeSB";
        String ExcludeNB = "ExcludeNB";
        String isSRatePopup = "isSRatePopup";

        public AppPreferences(Context context) {
            PREFS_NAME = context.getResources().getString(R.string.app_name);
            this.preferences = context.getSharedPreferences(PREFS_NAME, 0);
            editor = preferences.edit();
        }

        public void setChangeListner(SharedPreferences.OnSharedPreferenceChangeListener listner) {
            preferences.registerOnSharedPreferenceChangeListener(listner);
        }

        public void setNotifi(boolean b) {
            editor.putBoolean(Notifi, b);
            editor.commit();
        }

        public boolean getNotifi() {
            return preferences.getBoolean(Notifi, true);
        }

        public void setSRNoti(boolean b) {
            editor.putBoolean(SRNoti, b);
            editor.commit();
        }

        public boolean getSRNoti() {
            return preferences.getBoolean(SRNoti, true);
        }

        public void setisRatePopup(int i) {
            editor.putInt(isSRatePopup, i);
            editor.commit();
        }

        public int getisRatePopup() {
            return preferences.getInt(isSRatePopup, 1);
        }

        public void setisService(boolean b) {
            editor.putBoolean(isService, b);
            editor.commit();
        }

        public boolean getisService() {
            return preferences.getBoolean(isService, false);
        }

        public void setExcludeSB(boolean b) {
            editor.putBoolean(ExcludeSB, b);
            editor.commit();
        }

        public boolean getExcludeSB() {
            return preferences.getBoolean(ExcludeSB, false);
        }

        public void setExcludeNB(boolean b) {
            editor.putBoolean(ExcludeNB, b);
            editor.commit();
        }

        public boolean getExcludeNB() {
            return preferences.getBoolean(ExcludeNB, false);
        }

        public void setisScreenRec(boolean b) {
            editor.putBoolean(isScreenRec, b);
            editor.commit();
        }

        public boolean getisScreenRec() {
            return preferences.getBoolean(isScreenRec, false);
        }

        public void setPowerCD(boolean b) {
            editor.putBoolean(PowerCD, b);
            editor.commit();
        }

        public boolean getPowerCD() {
            return preferences.getBoolean(PowerCD, false);
        }

        public void setHOA(boolean b) {
            editor.putBoolean(HOA, b);
            editor.commit();
        }

        public boolean getHOA() {
            return preferences.getBoolean(HOA, false);
        }

        public void setisAudio(boolean b) {
            editor.putBoolean(isAudio, b);
            editor.commit();
        }

        public boolean getisAudio() {
            return preferences.getBoolean(isAudio, true);
        }

        public void setSSSound(boolean b) {
            editor.putBoolean(SSSound, b);
            editor.commit();
        }

        public boolean getSSSound() {
            return preferences.getBoolean(SSSound, true);
        }

        public void setDis(boolean b) {
            editor.putBoolean(Dis, b);
            editor.commit();
        }

        public boolean getDis() {
            return preferences.getBoolean(Dis, true);
        }

        public void setSRDis(boolean b) {
            editor.putBoolean(SRDis, b);
            editor.commit();
        }

        public boolean getSRDis() {
            return preferences.getBoolean(SRDis, true);
        }

        public void setShake(boolean b) {
            editor.putBoolean(Shake, b);
            editor.commit();
        }

        public boolean getShake() {
            return preferences.getBoolean(Shake, false);
        }

        public void setSRShake(boolean b) {
            editor.putBoolean(SRShake, b);
            editor.commit();
        }

        public boolean getSRShake() {
            return preferences.getBoolean(SRShake, false);
        }

        public void setSelColor(String s) {
            editor.putString(SelColor, s);
            editor.commit();
        }

        public String getSelColor() {
            return preferences.getString(SelColor, "#ff09BEE7");
        }

        public void setImageType(String s) {
            editor.putString(ImageType, s);
            editor.commit();
        }

        public String getImageType() {
            return preferences.getString(ImageType, "JPEG");
        }

        public void setOpenType(String s) {
            editor.putString(OpenType, s);
            editor.commit();
        }

        public String getOpenType() {
            return preferences.getString(OpenType, "Open Option");
        }

        public void setImageQuality(int i) {
            editor.putInt(ImageQuality, i);
            editor.commit();
        }

        public int getImageQuality() {
            return preferences.getInt(ImageQuality, 100);
        }

        public void setQuality(String s) {
            editor.putString(Quality, s);
            editor.commit();
        }

        public String getQuality() {
            return preferences.getString(Quality, "720p");
        }

        public void setX(float f) {
            editor.putFloat(X, f);
            editor.commit();
        }

        public float getX() {
            return preferences.getFloat(X, 0);
        }

        public void setY(float f) {
            editor.putFloat(Y, f);
            editor.commit();
        }

        public float getY() {
            return preferences.getFloat(Y, 200);
        }

        public void setXH(float f) {
            editor.putFloat(XH, f);
            editor.commit();
        }

        public float getXH() {
            return preferences.getFloat(XH, 0);
        }

        public void setYH(float f) {
            editor.putFloat(YH, f);
            editor.commit();
        }

        public float getYH() {
            return preferences.getFloat(YH, 300);
        }

        public void setAudioBitrate(int i) {
            editor.putInt(AudioBitrate, i);
            editor.commit();
        }

        public int getAudioBitrate() {
            return preferences.getInt(AudioBitrate, 320);
        }

        public void setAudioSampleRate(int i) {
            editor.putInt(AudioSampleRate, i);
            editor.commit();
        }

        public int getAudioSampleRate() {
            return preferences.getInt(AudioSampleRate, 44100);
        }

        public void setAudioChannel(int i) {
            editor.putInt(AudioChannel, i);
            editor.commit();
        }

        public int getAudioChannel() {
            return preferences.getInt(AudioChannel, 1);
        }

        public void setVideoBitrate(int i) {
            editor.putInt(VideoBitrate, i);
            editor.commit();
        }

        public int getVideoBitrate() {
            return preferences.getInt(VideoBitrate, 25000);
        }

        public void setVideoFPS(int i) {
            editor.putInt(VideoFPS, i);
            editor.commit();
        }

        public int getVideoFPS() {
            return preferences.getInt(VideoFPS, 30);
        }

        public void setVideoFI(int i) {
            editor.putInt(VideoFI, i);
            editor.commit();
        }

        public int getVideoFI() {
            return preferences.getInt(VideoFI, 1);
        }

        public void setArrangeCase(boolean b) {
            editor.putBoolean(ArrangeCase, b);
            editor.commit();
        }

        public boolean getArrangeCase() {
            return preferences.getBoolean(ArrangeCase, false);
        }

        public void setMiPopCase(boolean b) {
            editor.putBoolean(MiPopCase, b);
            editor.commit();
        }

        public boolean getMiPopCase() {
            return preferences.getBoolean(MiPopCase, false);
        }


    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    public static String clear_last(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String convertProntoHexStringToIntString(String s) {
        String[] codes = s.split(" ");
        StringBuilder sb = new StringBuilder();
        sb.append(new StringBuilder(String.valueOf(getFrequency(codes[1]))).append(",").toString());
        for (int i = 4; i < codes.length; i++) {
            sb.append(new StringBuilder(String.valueOf(Integer.parseInt(codes[i], 16))).append(",").toString());
        }
        return sb.toString();
    }

    public static boolean checkServiceRunning(Context mContext, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getFrequency(String s) {
        return Integer.valueOf((int) (1000000.0d / (((double) Integer.parseInt(s, 16)) * 0.241246d))).toString();
    }

    public static String getJSONStringFromFile(Context mContext, String name, String f) {
        try {
            InputStream is = mContext.getAssets().open(new StringBuilder(String.valueOf(f)).append(name.replace(" ", "_")).toString());
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    r.close();
                    is.close();
                    return jsonString.toString();
                }
                jsonString.append(line);
            }
        } catch (Exception e) {
            showLog("Error reading file : Error reading file, check JSON format\n" + name);
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObjectFromFile(Context mContext, String path, String f) {
        try {
            return new JSONObject(getJSONStringFromFile(mContext, path, f));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void openUserPage(Context mContext, String user) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://instagram.com/_u/" + user));
        intent.setPackage("com.instagram.android");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "Please Install Instagram", Toast.LENGTH_SHORT).show();
        }
    }

    public static String setF2(float myFloat) {
        return String.format("%.02f", myFloat);
    }

    public static String conuter_Value(int i) {
        String data;
        long l = i;
        if (l > 1000000) {
            float f = l / 1000000;
            data = String.valueOf((int) f) + "M";
        } else if (l > 10000) {
            float f = l / 1000;
            data = String.valueOf((int) f) + "K";
        } else {
            data = "" + i;
        }
        return data;
    }

    public static String convertStringToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public static String getAgoTime(long l) {
        String dateString = DateUtils.getRelativeTimeSpanString(l).toString();
        return dateString;
    }

    public static void BatteryOptimization(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    showLog(e.toString());
                }
            }
        }
    }

    public static String getURLForResource(Context mContext, int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resourceId).toString();
    }

    static public Bitmap scaleCenterCrop(Bitmap source, int newHeight,
                                         int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top
                + scaledHeight);//from ww w  .j a va 2s. co m

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
                source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

}