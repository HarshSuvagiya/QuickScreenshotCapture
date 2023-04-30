package vishtechno.bkm.quickscreenshotcapture.services;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.StitchEditActivity;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_MainActivity;
import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_ScreenCaptureActivity;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_ShowDialogActivity;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_StartActivity;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_Constants;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_RecursiveFileObserver;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_SetScreenshotList;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_TakeScreenshotRoot;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_FloatWidgetServiceMethodsInterface;
import vishtechno.bkm.quickscreenshotcapture.receivers.VISHTECHNO_BootUpReceiver;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_TakeScreenshot;
import vishtechno.bkm.quickscreenshotcapture.view.ShakeDetector;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.ColorPicker;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.OpacityBar;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;

import static vishtechno.bkm.quickscreenshotcapture.Help.mSS;
import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.invisible;
import static vishtechno.bkm.quickscreenshotcapture.Help.myList;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;
import static vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help.creation_path;
import static vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon.isRec;

public class VISHTECHNO_FloatWidgetService extends Service implements SensorEventListener, VISHTECHNO_FloatWidgetServiceMethodsInterface,
        VISHTECHNO_RecursiveFileObserver.EventListener, ShakeDetector.Listener {

    private static final int MIN_WIDGET_SIZE_IN_DP = 20;
    private static final int SHAKE_SLOP_TIME_MS = 2000;

    public float SHAKE_THRESHOLD_GRAVITY = 2.5f;
    public GestureDetector gestureDetector;
    private boolean isFromReceiver = false;

    public static View mFloatingWidget;
    private Intent mIntent;

    public VISHTECHNO_RecursiveFileObserver mObserver;
    private long mShakeTimestamp;

    public WindowManager mWindowManager;
    int x;
    int y;
    int selectedColor;
    Notification notification;

    boolean close = false, first = false;
    Vibrator vibrator;
    public static Context mContext;

    Help.AppPreferences preferences;
    public static String currentPath;

    Handler handler = new Handler();
//    private Runnable updateTimerThread = new Runnable() {
//        public void run() {
//            if (preferences.getNotifi()) {
//                updateData();
//            }
//            handler.postDelayed(this, 1000);
//        }
//    };

    protected BroadcastReceiver resizeWidgetReceicer = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            Help.nextwithnew(context, VISHTECHNO_MainActivity.class);
        }
    };

    public long screenshotTakingTime;
    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            preferences.setisService(false);
            if (Help.mSSActivity != null) {
                Help.mSSActivity.finish();
            }
        }
    };
    protected BroadcastReceiver takeScreenshotReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            if (!isRec) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (!preferences.getDis()) {
                            takeScreenshot(mFloatingWidget);
                            return;
                        }
                        if (!((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.FLOATING_BUTTON)).booleanValue()) {
                            takeScreenshot(mFloatingWidget);
                        } else if (mFloatingWidget != null) {
                            gone(mFloatingWidget);
                        }
                    }
                }, 700);
            }
        }
    };

    public LayoutParams widgetParams;
    private int LAYOUT_FLAG;

    public static int mF = 0;

    @Override
    public void hearShake() {
        if (preferences.getisService()) {
            if (preferences.getShake()) {
                if (mF == 0) {
                    mF++;
                    if (!isRec) {
                        takeScreenshot(mFloatingWidget);
                    }
                }
            }
        }
    }

    private class GestureListener extends SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            dimWidget(mFloatingWidget);
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            super.onLongPress(motionEvent);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        try {
            if (this.widgetParams != null) {
                float f = (float) getResources().getDisplayMetrics().heightPixels;
                float f2 = (float) getResources().getDisplayMetrics().widthPixels;
                if (configuration.orientation == 2) {
                    widgetParams.y = (int) Math.floor((double) ((((float) widgetParams.y) * f) / f2));
                } else {
                    widgetParams.y = Math.round((((float) widgetParams.y) * f) / f2);
                }
            }
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService onConfigurationChanged.", e);
        }
        startUpdatingPosition(widgetParams);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            float f = sensorEvent.values[0];
            float f2 = f / 9.80665f;
            float f3 = sensorEvent.values[1] / 9.80665f;
            float f4 = sensorEvent.values[2] / 9.80665f;
            if (((float) Math.sqrt((double) ((f2 * f2) + (f3 * f3) + (f4 * f4)))) > this.SHAKE_THRESHOLD_GRAVITY) {
                @SuppressLint("WrongConstant") KeyguardManager keyguardManager = (KeyguardManager) getSystemService("keyguard");
                if (keyguardManager != null && !keyguardManager.isKeyguardLocked()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (mShakeTimestamp + 2000 <= currentTimeMillis) {
                        mShakeTimestamp = currentTimeMillis;
                        if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SHAKESHOT)).booleanValue()) {
                            if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.FLOATING_BUTTON)).booleanValue()) {
                                gone(mFloatingWidget);
                            } else {
                                takeScreenshot(mFloatingWidget);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService onSensorChanged.", e);
        }
    }

    public void onCreate() {
        showLog("Create");

        mContext = this;
        mFloatingWidget = null;
        VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface = this;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        File file = new File((String) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SCREENSHOT_DIR));
        if (!file.exists()) {
            file.mkdirs();
        }
        VISHTECHNO_PreferenceManager.screenshotDir = file;
        SHAKE_THRESHOLD_GRAVITY = (float) ((((double) ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SHAKE_THRESHOLD)).intValue()) * 0.4d) + 2.5d);
        VISHTECHNO_PreferenceManager.density = getResources().getDisplayMetrics().density;
        registerReceiver(new VISHTECHNO_BootUpReceiver(), new IntentFilter("android.intent.action.BOOT_COMPLETED"));
        registerReceiver(new VISHTECHNO_BootUpReceiver(), new IntentFilter("android.intent.action.MY_PACKAGE_REPLACED"));
        startFileObserver();
        VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface = this;
        if (!isFromReceiver) {
            startProjection(false);
        } else {
            onGetResult(true, null, false);
        }

        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        preferences = new Help.AppPreferences(mContext);
        mF = 0;

        if (preferences.getNotifi()) {
            updateData();
//            handler.postDelayed(updateTimerThread, 0);
            registerReceiver(takeScreenshotReceiver, new IntentFilter("takeScreenshotFilter"));
            registerReceiver(resizeWidgetReceicer, new IntentFilter("resizeWidgetFilter"));
            registerReceiver(stopServiceReceiver, new IntentFilter("stopServiceFilter"));
//        startForeground(1337, vishtechno_notification);
        }

//        startProjection(false);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        super.onCreate();
    }

    @SuppressLint("WrongConstant")
    public int onStartCommand(Intent intent, int i, int i2) {
//        Notification vishtechno_notification;

        showLog("StartCommand");
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String string = extras.getString("FROM");
                if (string != null && string.equals("RECEIVER")) {
                    VISHTECHNO_PreferenceManager.printIt("SERVICE STARTED BY BROADCAST RECEIVER");
                    isFromReceiver = true;
                }
            }
        }

        registerReceiver(takeScreenshotReceiver, new IntentFilter("takeScreenshotFilter"));
        registerReceiver(resizeWidgetReceicer, new IntentFilter("resizeWidgetFilter"));
        registerReceiver(stopServiceReceiver, new IntentFilter("stopServiceFilter"));
//        startForeground(1337, vishtechno_notification);
//        if (VERSION.SDK_INT >= 23 && (getApplicationContext().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0 || !Settings.canDrawOverlays(getApplicationContext()))) {
//            VISHTECHNO_PreferenceManager.printIt("STOPPING SERVICE :- NO STORAGE OR OVERLAY PERMISSION IN SERVICE AUTO START");
//            stopSelf();
//            return START_STICKY;
//        } else if (VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SCREENSHOT_DIR) == null) {
//            stopSelf();
//            return START_STICKY;
//        } else {
//            File file = new File((String) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SCREENSHOT_DIR));
//            if (file.exists()) {
//                VISHTECHNO_PreferenceManager.screenshotDir = file;
//                SHAKE_THRESHOLD_GRAVITY = (float) ((((double) ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SHAKE_THRESHOLD)).intValue()) * 0.4d) + 2.5d);
//                VISHTECHNO_PreferenceManager.density = getResources().getDisplayMetrics().density;
//                registerReceiver(new VISHTECHNO_BootUpReceiver(), new IntentFilter("android.intent.action.BOOT_COMPLETED"));
//                registerReceiver(new VISHTECHNO_BootUpReceiver(), new IntentFilter("android.intent.action.MY_PACKAGE_REPLACED"));
//                startFileObserver();
//                SensorManager sensorManager = (SensorManager) getSystemService("sensor");
//                if (sensorManager != null) {
//                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(1), 3);
//                }
//                VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface = this;
//                if (!isFromReceiver) {
//                    startProjection(false);
//                } else {
//                    onGetResult(true, null, false);
//                }
//                return START_STICKY;
//            }
//            stopSelf();
//        }
        return START_STICKY;
    }

    @SuppressLint("WrongConstant")
    public void updateData() {

        PendingIntent activity = PendingIntent.getActivity(this, 0,
                new Intent(this, VISHTECHNO_MainActivity.class).setFlags(268435456), 0);
        @SuppressLint("WrongConstant")
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0,
                new Intent("takeScreenshotFilter"), 134217728);

        @SuppressLint("WrongConstant")
        PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 0,
                new Intent("resizeWidgetFilter"), 134217728);

        @SuppressLint("WrongConstant")
        PendingIntent broadcast3 = PendingIntent.getBroadcast(this, 0,
                new Intent("stopServiceFilter"), 134217728);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.vishtechno_notification);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_ss, broadcast);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_settings, broadcast2);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_power, broadcast3);

        if (VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("channelId", "System", 1);
//            notificationChannel.setLockscreenVisibility(-1);
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notification = new Notification.Builder(this, "channelId")
                    .setSmallIcon(R.drawable.ss_not)
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setOnlyAlertOnce(true)
                    .setPriority(Notification.PRIORITY_MAX)
//                    .setContentIntent(activity)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        } else {
            notification = new Notification.Builder(this).
                    setSmallIcon(R.drawable.ss_not)
                    .setContent(remoteViews)
                    .setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_MAX)
//                    .setContentIntent(activity)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        startForeground(1337, notification);

    }

    public void hideOverlay() {
        if (mFloatingWidget != null) {
            invisible(mFloatingWidget);
        }
    }

    public void setColor(int i) {
        if (mFloatingWidget != null) {
            mFloatingWidget.setAlpha(1.0f);
//            mFloatingWidget.setBackground(getBackgroundShape(i, mFloatingWidget.getWidth()));
            dimWidget(mFloatingWidget);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        showLog("SS Remove");
        super.onTaskRemoved(rootIntent);
    }

    public void onDestroy() {
        super.onDestroy();
        showLog("SS Destroy");
        if (mPlayer2 != null) {
            mPlayer2.release();
        }
        VISHTECHNO_PreferenceManager.printIt("SERVICE DESTROYED");
        if (VISHTECHNO_PreferenceManager.mainActivityMethodsInterface != null) {
            VISHTECHNO_PreferenceManager.mainActivityMethodsInterface.onStarted(false);
        } else {
            VISHTECHNO_PreferenceManager.isScreenCaptureStarted = false;
        }
        handler.removeCallbacksAndMessages(null);
        try {
            if (takeScreenshotReceiver != null) {
                unregisterReceiver(takeScreenshotReceiver);
            }
            if (resizeWidgetReceicer != null) {
                unregisterReceiver(resizeWidgetReceicer);
            }
            if (stopServiceReceiver != null) {
                unregisterReceiver(stopServiceReceiver);
            }
        } catch (IllegalArgumentException e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService IN onDestroy", e);
        }
        if (mFloatingWidget != null) {
            try {
                mWindowManager.removeView(mFloatingWidget);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
        }
        if (removeFloatingWidgetView != null) {
            try {
                mWindowManager.removeView(removeFloatingWidgetView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
        }
        if (addSCView != null) {
            try {
                mWindowManager.removeView(addSCView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
            addSCView = null;
        }
        if (addCloseView != null) {
            try {
                mWindowManager.removeView(addCloseView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
            addCloseView = null;
        }
        if (showPopImageView != null) {
            try {
                mWindowManager.removeView(showPopImageView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
            showPopImageView = null;
        }
        if (addSSView != null) {
            try {
                mWindowManager.removeView(addSSView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
            addSSView = null;
        }
        if (addNextView != null) {
            try {
                mWindowManager.removeView(addNextView);
            } catch (Exception e) {
                showLog(e.getMessage());
            }
            addNextView = null;
        }
        if (mObserver != null) {
            mObserver.stopWatching();
        }
    }

    public void startProjection(boolean z) {
        startActivity(new Intent(getApplicationContext(), VISHTECHNO_ScreenCaptureActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                .putExtra("afterClick", z));
    }

    public void startUpdatingPosition(final LayoutParams layoutParams) {
        try {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (layoutParams != null) {
                        int i = getResources().getDisplayMetrics().widthPixels;
                        if (layoutParams.x < i / 2) {
                            if (layoutParams.x > 0) {
                                layoutParams.x -= 50;
                                startUpdatingPosition(layoutParams);
                            } else {
                                layoutParams.x = 0;
                                VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_X, Integer.valueOf(layoutParams.x));
                                VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_Y, Integer.valueOf(layoutParams.y));
                            }
                        } else if (layoutParams.x < i) {
                            layoutParams.x += 50;
                            startUpdatingPosition(layoutParams);
                        } else {
                            layoutParams.x = i;
                            VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_X, Integer.valueOf(layoutParams.x));
                            VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_Y, Integer.valueOf(layoutParams.y));
                        }
                        try {
                            mWindowManager.updateViewLayout(mFloatingWidget, layoutParams);
                        } catch (Exception e) {
                            showLog(e.toString());
                        }
                    }
                }
            }, 10);
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService startUpdatingPosition()", e);
        }
    }

    ImageView float_img;

    @SuppressLint("WrongConstant")
    private void createWidget() {

        Boolean bool = (Boolean) VISHTECHNO_PreferenceManager.getSharedPref(this, VISHTECHNO_Constants.FLOATING_BUTTON);
        int intValue = ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)).intValue() + 20;

        mFloatingWidget = LayoutInflater.from(this).inflate(R.layout.vishtechno_layout_floating_widget, null);

        if (VERSION.SDK_INT >= 26) {
            widgetParams = new LayoutParams(-2, -2, LayoutParams.TYPE_APPLICATION_OVERLAY, 8, -3);
        } else {
            widgetParams = new LayoutParams(-2, -2, LayoutParams.TYPE_SYSTEM_ERROR, 8, -3);
        }

        widgetParams.gravity = 8388659;
        widgetParams.x = ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_X)).intValue();
        widgetParams.y = ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_Y)).intValue();

        int i = Help.w(110);
        widgetParams.width = i;
        widgetParams.height = i;

        float_img = mFloatingWidget.findViewById(R.id.float_img);
//        float_img.setColorFilter(Color.parseColor(preferences.getSelColor()));

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            if (preferences.getDis()) {
                try {
                    mWindowManager.addView(mFloatingWidget, widgetParams);
                } catch (Exception e) {
                    showLog(e.toString());
                    createWidget();
                    return;
                }
            }
        }

        mFloatingWidget.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                try {
                    mFloatingWidget.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } catch (Exception e) {
                }
//                mFloatingWidget.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), mFloatingWidget.getWidth()));
            }
        });

        gestureDetector = new GestureDetector(this, new GestureListener());

        if (bool == null || !bool.booleanValue()) {
            invisible(mFloatingWidget);
        } else {
            visible(mFloatingWidget);
        }

        mFloatingWidget.setTag(Integer.valueOf(mFloatingWidget.getVisibility()));

        mFloatingWidget.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                try {
                    if (((Integer) mFloatingWidget.getTag()).intValue() != mFloatingWidget.getVisibility()) {

                        mFloatingWidget.setTag(Integer.valueOf(mFloatingWidget.getVisibility()));

                        if (mFloatingWidget.getVisibility() == 8) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    if (!isRec) {
                                        takeScreenshot(mFloatingWidget);
                                    }
                                }
                            }, (long) (((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.TAKE_SS_AFTER)).intValue() * 1000));
                        }
                    }
                } catch (Exception e) {
                    Help.Toast(mContext, "Something went Wrong try Again");
                }
            }
        });

        dimWidget(mFloatingWidget);

        mFloatingWidget.setOnTouchListener(new OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int rawX = (int) motionEvent.getRawX();
                int rawY = (int) motionEvent.getRawY();
                try {
                    if (gestureDetector.onTouchEvent(motionEvent)) {
                        dimHandler.removeCallbacksAndMessages(null);
                        view.animate().scaleX(1f).scaleY(1f).alpha(1f).translationX(0);
                        gone(removeFloatingWidgetView);
                        if (!isRec) {
                            gone(view);
                        }
                        dimWidget(view);
                        return true;
                    }
                    switch (motionEvent.getAction()) {
                        case 0:
                            dimHandler.removeCallbacksAndMessages(null);
                            view.animate().scaleX(1f).scaleY(1f).alpha(1f).translationX(0);
//                                view.setAlpha(1.0f);
                            initialX = widgetParams.x;
                            initialY = widgetParams.y;
                            initialTouchX = motionEvent.getRawX();
                            initialTouchY = motionEvent.getRawY();
                            return true;
                        case 1:
                            dimWidget(view);
                            if (close) {
                                stopSelf();
                                preferences.setisService(false);
                                if (Help.mSSActivity != null) {
                                    Help.mSSActivity.finish();
                                }
                            } else {
                                gone(removeFloatingWidgetView);
                                startUpdatingPosition(widgetParams);
                            }
                            return true;
                        case 2:
                            widgetParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                            widgetParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                            mWindowManager.updateViewLayout(mFloatingWidget, widgetParams);

                            int w = getResources().getDisplayMetrics().widthPixels;
                            int h = getResources().getDisplayMetrics().heightPixels;
                            if (h < w) {
                                w = h;
                                h = w;
                            }
                            visible(removeFloatingWidgetView);
                            int x1 = w * 440 / 1080;
                            int x2 = w * 640 / 1080;
                            int y1 = h - (w * 350 / 1080);
                            int y2 = h - (w * 100 / 1080);
                            if (rawX >= x1 && rawX <= x2 && rawY >= y1 && rawY <= y2) {
                                close = true;
                                remove_image_view.setBackgroundResource(R.drawable.new_white_circle);
                                if (first) {
                                    first = false;
                                    if (vibrator.hasVibrator()) {
                                        vibrator.vibrate(100);
                                    }
                                }
                            } else {
                                first = true;
                                close = false;
                                remove_image_view.setBackgroundResource(R.drawable.white_circle_shape);
                            }

                            return true;
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    VISHTECHNO_PreferenceManager.printIt("ERROR ", e);
                    return false;
                }
            }
        });
    }

    public void newSSTake(View view, String screenshotName) {
        new SSSAVE(mContext, mIntent, screenshotName, new SSSAVE.ScreenshotListener() {
            @Override
            public void onForcedStop() {
                visible(view);
            }

            @Override
            public void onOutOfMemory(File file) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (VISHTECHNO_PreferenceManager.isScreenshotListClass) {
//                            VISHTECHNO_MainActivity.changeFragment(false, new ScreenshotsList());
                        }
                    }
                }, 1000);
                VISHTECHNO_PreferenceManager.refreshGallery(file.getAbsolutePath(), getApplicationContext());
                startFinishing(false, null, view);
            }

            @Override
            public void onProjectionNotAvailable() {
                startProjection(true);
            }

            @Override
            public void onScreenshotTaken(String str) {
                StringBuilder sb = new StringBuilder();
                sb.append("TOTAL SCREENSHOT TAKEN TIME : ");
                sb.append((int) (Calendar.getInstance().getTimeInMillis() - screenshotTakingTime));
                showLog(sb.toString());
                startFinishing(true, str, view);
            }

            @Override
            public void onUnsupported(String str) {
                new VISHTECHNO_TakeScreenshotRoot(getApplicationContext(), str).setOnRootScreenshotTakenListener(new VISHTECHNO_TakeScreenshotRoot.OnRootScreenshotTakenListener() {
                    public void getRootedScreenshot(boolean z) {
                        if (!z) {
                            try {
                                startFinishing(true, str, view);
                            } catch (Exception e) {
                                VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService getRootedScreenshot ", e);
                            }
                        } else {
                            VISHTECHNO_PreferenceManager.toastIt(getApplicationContext(), "Device is not allowing Screenshot");
                            startFinishing(false, str, view);
                        }
                    }
                });
            }
        });
    }

    public void takeScreenshot(final View view) {


        screenshotTakingTime = Calendar.getInstance().getTimeInMillis();
        mContext = VISHTECHNO_FloatWidgetService.this;

        if (VISHTECHNO_PreferenceManager.screenshotDir == null) {
            File file = new File((String) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SCREENSHOT_DIR));
            if (!file.exists()) {
                file.mkdirs();
            }
            VISHTECHNO_PreferenceManager.screenshotDir = file;
        }

        currentPath = VISHTECHNO_PreferenceManager.getScreenshotName();
        newSSTake(view, VISHTECHNO_PreferenceManager.getScreenshotName());
//        new VISHTECHNO_TakeScreenshot(getApplicationContext(), mIntent, VISHTECHNO_PreferenceManager.getScreenshotName(), new VISHTECHNO_TakeScreenshot.ScreenshotListener() {
//
//            public void onScreenshotTaken(String str) {
//
//                StringBuilder sb = new StringBuilder();
//                sb.append("TOTAL SCREENSHOT TAKEN TIME : ");
//                sb.append((int) (Calendar.getInstance().getTimeInMillis() - screenshotTakingTime));
//                showLog(sb.toString());
//                startFinishing(true, str, view);
//
//            }
//
//            public void onProjectionNotAvailable() {
//                startProjection(true);
//            }
//
//            public void onForcedStop() {
//                visible(view);
//            }
//
//            public void onUnsupported(final String str) {
//                new VISHTECHNO_TakeScreenshotRoot(getApplicationContext(), str).setOnRootScreenshotTakenListener(new VISHTECHNO_TakeScreenshotRoot.OnRootScreenshotTakenListener() {
//                    public void getRootedScreenshot(boolean z) {
//                        if (!z) {
//                            try {
//                                startFinishing(true, str, view);
//                            } catch (Exception e) {
//                                VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService getRootedScreenshot ", e);
//                            }
//                        } else {
//                            VISHTECHNO_PreferenceManager.toastIt(getApplicationContext(), "Device is not allowing Screenshot");
//                            startFinishing(false, str, view);
//                        }
//                    }
//                });
//            }
//
//            public void onOutOfMemory(File file) {
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        if (VISHTECHNO_PreferenceManager.isScreenshotListClass) {
////                            VISHTECHNO_MainActivity.changeFragment(false, new ScreenshotsList());
//                        }
//                    }
//                }, 1000);
//                VISHTECHNO_PreferenceManager.refreshGallery(file.getAbsolutePath(), getApplicationContext());
//                startFinishing(false, null, view);
//            }
//        });
    }

    MediaPlayer mPlayer2;

    @SuppressLint("WrongConstant")
    public void startFinishing(boolean z, @Nullable String str, final View view) {
        mContext = VISHTECHNO_FloatWidgetService.this;
        if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SS_TAKEN_HF)).booleanValue()) {
            VISHTECHNO_PreferenceManager.performVibrate(getApplicationContext());
        }
        int i = 0;
        if (!z) {
            i = 3500;
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.FLOATING_BUTTON)).booleanValue()) {
                    if (!Help.mAddSS) {
                        visible(view);
                    }
                }
            }
        }, (long) i);
        if (VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen) {
            VISHTECHNO_PreferenceManager.showDialogMethodsInterface.closeActivity();
        }
        if (z && ((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.SHOW_POPUP)).booleanValue()) {
            if (preferences.getSSSound()) {
                try {
                    mPlayer2 = MediaPlayer.create(this, R.raw.ss_click);
                    if (mPlayer2 != null) {
                        mPlayer2.start();
                    }
                } catch (Exception e) {
                }
            }
            MediaScannerConnection.scanFile(mContext, new String[]{str}, new String[]{"image/*"}, null);
            if (Help.mAddSS) {
                AddLayINSC(str);
                new CopyImage().execute();
            } else {
//                mSS = str;
//                Help.nextwithnewclear(mContext, VISHTECHNO_ShowDialogActivity.class);

                boolean action = preferences.getOpenType().equals("No Action");

                visible(showPopImageView);
                gone(popborder);
                Glide.with(mContext).load(str).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (action) {
                            new CopyImage().execute();
                        }
                        startAnim(1);
                        visible(popborder);
                        return false;
                    }
                }).into(popImage);

                if (!action) {
                    Intent intent = new Intent(mContext, VISHTECHNO_ShowDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ssname", str);
                    startActivity(intent);
                }
//                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
//                try {
//                    pendingIntent.send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }

//                startActivity(new Intent(getApplicationContext(), VISHTECHNO_ShowDialogActivity.class).addFlags(276824064).putExtra("ssname", str));
            }
        }
    }

    Techniques technique;
    public static YoYo.YoYoString rope;

    void startAnim(int count) {
        long time;
        if (count == 1) {
            technique = Techniques.Landing;
            time = 2000;
        } else {
            technique = Techniques.TakingOff;
            time = 5000;
        }

        rope = YoYo.with(technique)
                .duration(time)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (count != 1) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    gone(showPopImageView);
                                }
                            }, 3000);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (count == 1) {
                            startAnim(count + 1);
                        } else {
                            gone(showPopImageView);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(mainReal);
    }

    Handler dimHandler = new Handler();

    public void dimWidget(final View view) {
        dimHandler.postDelayed(new Runnable() {
            public void run() {
                if (view != null) {
                    if (widgetParams.x > (getResources().getDisplayMetrics().widthPixels / 2)) {
                        view.animate().scaleX(0.78f).scaleY(0.78f).alpha(0.6f).translationX(Help.w(20)).setDuration(300);
                    } else {
                        view.animate().scaleX(0.78f).scaleY(0.78f).alpha(0.6f).translationX(Help.w(-20)).setDuration(300);
                    }
                }
            }
        }, 2000);
    }

    ImageView iv_change_color_colorbox;

    public void showResizePopUp() {

        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.vishtechno_dialog_resize_widget);

            int w = getResources().getDisplayMetrics().widthPixels;
//            int h = getResources().getDisplayMetrics().heightPixels;

            final TextView textView = dialog.findViewById(R.id.txt_ss_after);
            final TextView txt1 = dialog.findViewById(R.id.txt1);
            final TextView txt2 = dialog.findViewById(R.id.txt2);
            SeekBar seekBar = dialog.findViewById(R.id.seek_ss_after);
            final ImageView imageView = dialog.findViewById(R.id.iv_ss_overlay_size);
            final TextView textView2 = dialog.findViewById(R.id.txt_ss_size);
            SeekBar seekBar2 = dialog.findViewById(R.id.seek_resize_widget);
            iv_change_color_colorbox = dialog.findViewById(R.id.iv_change_color_colorbox);
            ImageView btn_done = dialog.findViewById(R.id.btn_done);
            LinearLayout popup = dialog.findViewById(R.id.popup);
            LinearLayout header = dialog.findViewById(R.id.header);

            Help.setSize(popup, 964, 1464, false);
            Help.setSize(header, 964, 235, false);
            Help.setSize(imageView, 158, 158, false);
            Help.setSize(iv_change_color_colorbox, 400, 400, false);
            Help.setMargin(iv_change_color_colorbox, 0, 50, 0, 0, false);
            Help.setSize(btn_done, 416, 140, false);
            Help.setMargin(btn_done, 0, 50, 0, 0, false);

            txt1.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
            txt2.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));
            textView2.setTypeface(Typeface.createFromAsset(getAssets(), "arial.ttf"));

            seekBar.setProgress(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.TAKE_SS_AFTER)).intValue());
            textView.setText(String.valueOf(seekBar.getProgress()));
            seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    textView.setText(String.valueOf(i));
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.TAKE_SS_AFTER, Integer.valueOf(seekBar.getProgress()));
                }
            });

            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widgetParams.width, widgetParams.height);

            imageView.setLayoutParams(layoutParams);
//            imageView.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), mFloatingWidget.getWidth()));
            imageView.setBackground(getBackgroundShape(Color.parseColor(preferences.getSelColor()), mFloatingWidget.getWidth()));

            seekBar2.setProgress(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)).intValue());
            textView2.setText(String.valueOf(VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)));

            seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    textView2.setText(String.valueOf(i));
                    LayoutParams access$600 = widgetParams;
                    LayoutParams access$6002 = widgetParams;
                    LinearLayout.LayoutParams layoutParams2 = layoutParams;
                    int i2 = (int) ((((float) (i + 20)) * VISHTECHNO_PreferenceManager.density) + 0.5f);
                    layoutParams2.height = i2;
                    layoutParams2.width = i2;
                    access$6002.width = i2;
                    access$600.height = i2;
                    try {
                        mWindowManager.updateViewLayout(mFloatingWidget, widgetParams);
                    } catch (Exception e) {

                    }
                    imageView.setLayoutParams(layoutParams2);
//                    imageView.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), mFloatingWidget.getWidth()));
                    imageView.setBackground(getBackgroundShape(Color.parseColor(preferences.getSelColor()), mFloatingWidget.getWidth()));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    visible(mFloatingWidget);
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (!((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.FLOATING_BUTTON)).booleanValue()) {
                        invisible(mFloatingWidget);
                    }
                    VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE, Integer.valueOf(seekBar.getProgress()));
                }
            });

            selectedColor = Color.parseColor(preferences.getSelColor());
            iv_change_color_colorbox.setColorFilter(selectedColor);

            try {
                iv_change_color_colorbox.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color_dialog();
                    }
                });

                btn_done.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (VISHTECHNO_PreferenceManager.isScreenCaptureStarted && VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface != null) {
                            VISHTECHNO_PreferenceManager.floatWidgetServiceMethodsInterface.setColor(selectedColor);
                        }
                        VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.SELECTION_LINE_X, Integer.valueOf(x));
                        VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.SELECTION_LINE_Y, Integer.valueOf(y));
//                        VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR, Integer.valueOf(selectedColor));

                        String saveColor = String.format("#%08X", (0xFFFFFFFF & selectedColor));
                        preferences.setSelColor(saveColor);

                        VISHTECHNO_PreferenceManager.toastIt(getApplicationContext(), "Overlay color saved");
                        imageView.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), mFloatingWidget.getWidth()));
                        float_img.setColorFilter(Color.parseColor(preferences.getSelColor()));
//                        mFloatingWidget.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(),
//                                mFloatingWidget.getWidth()));

                        dialog.dismiss();

                    }
                });

            } catch (Exception e) {
                VISHTECHNO_PreferenceManager.printIt("ERROR IN ChangeColor IN setOnTouchListener", e);
            }

            if (VERSION.SDK_INT >= 26) {
                dialog.getWindow().setType(2038);
            } else {
                dialog.getWindow().setType(2003);
            }
            dialog.show();
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService showResizePopUp()", e);
        }
    }

    void color_dialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.pop_color);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 972, 1068, false);

        TextView title = dialog.findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "arialbd.ttf"));

        final ImageView cancel, submit, close;

        cancel = dialog.findViewById(R.id.cancel);
        submit = dialog.findViewById(R.id.ok);
        close = dialog.findViewById(R.id.close);

        Help.setSize(close, 80, 80, false);
        Help.setSize(cancel, 386, 128, false);
        Help.setSize(submit, 386, 128, false);
        Help.setMargin(submit, 50, 0, 0, 0, false);

        final ColorPicker picker = dialog.findViewById(R.id.picker);
        SVBar svBar = dialog.findViewById(R.id.svbar);
        OpacityBar opacityBar = dialog.findViewById(R.id.opacitybar);

        Help.setSize(picker, 500, 500, false);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        picker.setOldCenterColor(selectedColor);
        picker.setColor(selectedColor);
        svBar.setColor(selectedColor);
        opacityBar.setColor(selectedColor);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectedColor = picker.getColor();
                iv_change_color_colorbox.setColorFilter(selectedColor);
                dialog.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(2038);
        } else {
            dialog.getWindow().setType(2003);
        }
        dialog.show();
    }

    public void onGetResult(boolean z, @Nullable Intent intent, boolean z2) {

        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        Help.mBoolean = false;

        if (z) {

            if (intent != null) {
                mIntent = intent;
            }

            if (mFloatingWidget == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                createWidget();
                addRemoveView(layoutInflater);
                addSCView(layoutInflater);
                addCloseView(layoutInflater);
                addSSView(layoutInflater);
                addNextView(layoutInflater);
                addPopImageView(layoutInflater);
            } else if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.FLOATING_BUTTON)).booleanValue() && !z2) {
                visible(mFloatingWidget);
            }
            if (z2) {
                if (!isRec) {
                    takeScreenshot(mFloatingWidget);
                }
            }
            if (VISHTECHNO_PreferenceManager.mainActivityMethodsInterface != null) {
                VISHTECHNO_PreferenceManager.mainActivityMethodsInterface.onStarted(true);
                return;
            }
            return;
        }

        stopSelf();
    }

    public View removeFloatingWidgetView;
    public ImageView remove_image_view;

    @SuppressLint("WrongConstant")
    private void addRemoveView(LayoutInflater layoutInflater) {
        this.removeFloatingWidgetView = layoutInflater.inflate(R.layout.vishtechno_remove_floating_widget_layout, null);

        int w = getResources().getDisplayMetrics().widthPixels;

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        gone(removeFloatingWidgetView);
        this.remove_image_view = this.removeFloatingWidgetView.findViewById(R.id.remove_img);

        RelativeLayout.LayoutParams params_close = new RelativeLayout.LayoutParams(w * 120 / 1080, w * 120 / 1080);
        params_close.setMargins(0, 0, 0, w * 160 / 1080);
        remove_image_view.setLayoutParams(params_close);

        try {
            this.mWindowManager.addView(this.removeFloatingWidgetView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addRemoveView(layoutInflater);
        }
    }

    public static View addSCView, addCloseView, addSSView, addNextView, showPopImageView;
    public static LinearLayout addLay;
    public static ImageView img_close, add, next, popImage, popborder;
    RelativeLayout mainReal;

    @SuppressLint("WrongConstant")
    private void addPopImageView(LayoutInflater layoutInflater) {
        showPopImageView = layoutInflater.inflate(R.layout.popimage, null);

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.CENTER;
        gone(showPopImageView);
        mainReal = showPopImageView.findViewById(R.id.mainReal);
        popImage = showPopImageView.findViewById(R.id.popImage);
        popborder = showPopImageView.findViewById(R.id.popborder);

        Help.setSize(popImage, 720, 1280, true);
        Help.setSize(popborder, 720, 1280, true);

        try {
            this.mWindowManager.addView(showPopImageView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addPopImageView(layoutInflater);
        }
    }

    @SuppressLint("WrongConstant")
    private void addSCView(LayoutInflater layoutInflater) {
        addSCView = layoutInflater.inflate(R.layout.add_sc, null);

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        gone(addSCView);
        addLay = addSCView.findViewById(R.id.addLay);

        try {
            this.mWindowManager.addView(addSCView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addSCView(layoutInflater);
        }
    }

    @SuppressLint("WrongConstant")
    private void addCloseView(LayoutInflater layoutInflater) {
        addCloseView = layoutInflater.inflate(R.layout.add_close, null);

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = w * 300 / 1080;
        layoutParams.y = h - (w * 280 / 1080);
        gone(addCloseView);
        img_close = addCloseView.findViewById(R.id.close);

        RelativeLayout.LayoutParams params_close = new RelativeLayout.LayoutParams(w * 119 / 1080, w * 119 / 1080);
        img_close.setLayoutParams(params_close);

        img_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                visible(mFloatingWidget);
                gone(addSCView);
                gone(addCloseView);
                gone(addSSView);
                gone(addNextView);
                Help.mAddSS = false;
                myList.clear();
            }
        });

        try {
            this.mWindowManager.addView(addCloseView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addCloseView(layoutInflater);
        }
    }

    @SuppressLint("WrongConstant")
    private void addSSView(LayoutInflater layoutInflater) {
        addSSView = layoutInflater.inflate(R.layout.add_ss, null);

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        layoutParams.y = h - (w * 319 / 1080);
        gone(addSSView);
        add = addSSView.findViewById(R.id.add);

        RelativeLayout.LayoutParams params_close = new RelativeLayout.LayoutParams(w * 198 / 1080, w * 198 / 1080);
        add.setLayoutParams(params_close);

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(addSCView);
                gone(addCloseView);
                gone(addSSView);
                gone(addNextView);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRec) {
                            takeScreenshot(mFloatingWidget);
                        }
                    }
                }, 200);
            }
        });

        try {
            this.mWindowManager.addView(addSSView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addSSView(layoutInflater);
        }
    }

    @SuppressLint("WrongConstant")
    private void addNextView(LayoutInflater layoutInflater) {
        addNextView = layoutInflater.inflate(R.layout.add_next, null);

        if (Build.VERSION.SDK_INT >= 26) {
            this.LAYOUT_FLAG = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            this.LAYOUT_FLAG = LayoutParams.TYPE_SYSTEM_ERROR;
        }

        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, this.LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.x = w - (w * 420 / 1080);
        layoutParams.y = h - (w * 280 / 1080);
        gone(addNextView);
        next = addNextView.findViewById(R.id.next);

        RelativeLayout.LayoutParams params_close = new RelativeLayout.LayoutParams(w * 119 / 1080, w * 119 / 1080);
        next.setLayoutParams(params_close);

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                visible(mFloatingWidget);
                gone(addSCView);
                gone(addCloseView);
                gone(addSSView);
                gone(addNextView);
                Help.mAddSS = false;
                Help.nextwithnew(mContext, StitchEditActivity.class);
            }
        });

        try {
            this.mWindowManager.addView(addNextView, layoutParams);
        } catch (Exception e) {
            showLog(e.toString());
            addNextView(layoutInflater);
        }
    }

    public void startFileObserver() {

        if (VISHTECHNO_PreferenceManager.screenshotDir.exists()) {

            mObserver = new VISHTECHNO_RecursiveFileObserver(VISHTECHNO_PreferenceManager.screenshotDir.getAbsolutePath(), this);
            mObserver.startWatching();

        }

    }

    public void onEvent(int i, File file) {

        if (i == 256) {
            try {
                if (file.getName().contains(".png") || file.getName().contains(".jpeg")) {
                    new VISHTECHNO_SetScreenshotList(true, file.getAbsolutePath()).execute();
                    VISHTECHNO_PreferenceManager.printIt(file.getName());
                }
            } catch (Exception e) {
                VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService onEvent", e);
                return;
            }
        }
        if (i == 256 || i == 512) {
            VISHTECHNO_PreferenceManager.refreshGallery(file.getAbsolutePath(), getApplicationContext());
        }
    }

    @SuppressLint("WrongConstant")
    public GradientDrawable getBackgroundShape(int i, int i2) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.OVAL);
        gradientDrawable.setColors(new int[]{i, manipulateColor(i, 0.5f), i});
        gradientDrawable.setGradientType(1);
        gradientDrawable.setGradientRadius((float) i2);
        return gradientDrawable;
    }

    private int manipulateColor(int i, float f) {
        return Color.argb(Color.alpha(i), Math.min(Math.round(((float) Color.red(i)) * f), 255), Math.min(Math.round(((float) Color.green(i)) * f), 255), Math.min(Math.round(((float) Color.blue(i)) * f), 255));
    }

    public static void AddLayINSC(String path) {
        visible(addSCView);
        visible(addCloseView);
        visible(addSSView);
        visible(addNextView);
        invisible(mFloatingWidget);

        if (mContext == null) {
            return;
        }

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;
        if (Help.hasNavBar(mContext)) {
            height = height + Help.getNavHeight(mContext);
        }

        if (path == null) {
            if (currentPath != null) {
                path = currentPath;
            } else {
                Help.Toast(mContext, "Unable to get Capture image");
                return;
            }
        }

        File file = new File(path);
        myList.add(new ImageData(file.getName(), path, width, height));

        final View myview = LayoutInflater.from(mContext)
                .inflate(R.layout.myview, null, false);

        RelativeLayout mainLay = myview.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 275, 445, false);
        Help.setMargin(mainLay, 0, 15, 15, 0, false);

        RoundedImageView thumb = myview.findViewById(R.id.thumb);
        ImageView ssb = myview.findViewById(R.id.ssb);
        ImageView close = myview.findViewById(R.id.close);

        Help.setSize(thumb, 275, 445, false);
        Help.setSize(close, 45, 45, false);
        Help.setMargin(close, 0, 15, 15, 0, false);

        thumb.setCornerRadius(Help.w(10));
        addLay.addView(myview);

        Glide.with(mContext).load(path).into(thumb);

        String finalPath = path;
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLay.removeView(myview);
                for (int i = 0; i < myList.size(); i++) {
                    if (finalPath.equals(myList.get(i).getImageUrl())) {
                        myList.remove(i);
                    }
                }
                if (myList.size() == 0) {
                    visible(mFloatingWidget);
                    gone(addSCView);
                    gone(addCloseView);
                    gone(addSSView);
                    gone(addNextView);
                    Help.mAddSS = false;
                    myList.clear();
                }
            }
        });
    }

    class CopyImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            try {
//                progress.dismiss();
//            } catch (Exception e) {
//                showLog(e.toString());
//            }
            if (s.equals("error")) {
                Help.Toast(mContext, "Out of Memory Error");
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                File myDir = new File(Environment.getExternalStorageDirectory()
                        .toString() + "/" + mContext.getResources().getString(R.string.app_name));
                myDir.mkdirs();

                int quality = preferences.getImageQuality();
                Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                String type = preferences.getImageType();
                String extension = "jpg";
                if (type.equals("PNG")) {
                    format = Bitmap.CompressFormat.PNG;
                    extension = "png";
                } else if (type.equals("WEBP")) {
                    format = Bitmap.CompressFormat.WEBP;
                    extension = "webp";
                }

                File file = new File(myDir, mContext.getResources().getString(
                        R.string.app_name)
                        + System.currentTimeMillis() + "." + extension);
                String filepath = file.getPath();
                if (file.exists()) {
                    file.delete();
                }
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);

                    VISHTECHNO_TakeScreenshot.mBitmap.compress(format, quality, ostream);

                    creation_path = file.getAbsolutePath();

                    MediaScannerConnection.scanFile(mContext, new String[]{creation_path}, new String[]{"image/*"}, null);
                    ostream.close();

                } catch (Exception e) {

                }
                return "null";
            } catch (OutOfMemoryError e) {
                showLog(e.toString());
                return "error";
            }
        }
    }
}
