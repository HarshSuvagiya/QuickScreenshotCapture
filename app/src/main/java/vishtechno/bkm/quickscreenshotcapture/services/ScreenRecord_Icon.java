package vishtechno.bkm.quickscreenshotcapture.services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodecInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.RecordTransData;
import vishtechno.bkm.quickscreenshotcapture.SR_Setting_Activity;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_MainActivity;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_MyCreation;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_StartActivity;
import vishtechno.bkm.quickscreenshotcapture.Video_Player;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.AudioEncodeConfig;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.NamedSpinner;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.ScreenRecorder;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.Utils;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.Utils123;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.VideoEncodeConfig;
import vishtechno.bkm.quickscreenshotcapture.view.ShakeDetector;

import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;
import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class ScreenRecord_Icon extends Service implements
        TextureView.SurfaceTextureListener, ShakeDetector.Listener {
    private static final String TAG = ScreenRecord_Icon.class.getSimpleName();

    TextureView videoView;
    public static RelativeLayout container;
    LayoutInflater inflater;
    WindowManager.LayoutParams params;
    WindowManager wm;
    DisplayManager dm;
    Display display;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    int dispWidth, dispHeight;
    int statusBarHeight;
    float w, h;
    Rect dispRect = new Rect();
    float scale = 1.0f;
    public static boolean attached = false;
    private GestureDetector gestureDetector;
    public static Context mContext;
    boolean close = false, first = false;
    Vibrator vibrator;
    Help.AppPreferences preferences;
    public static int mF = 0;
    Notification notification;
    RemoteViews remoteViews;
    public static Chronometer my_timer;

    public static boolean tryPause = false;
    public static boolean isRec = false;
    public static Intent SR_Projection = null;

    NamedSpinner maudioprofile, mvideocodec, maudiocodec, mvideoprofilelevel;
    MediaCodecInfo[] mavccodecinfos;

    MediaProjectionManager mProjectionManager;
    MediaProjection mMediaProjection;
    VirtualDisplay mvirtualdisplay;
    ScreenRecorder a_mrecorder;

    public ScreenRecord_Icon() {
    }

    BroadcastReceiver SR_START_STOP = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            checkStartStop();
        }
    };

    public static void setProjection(Intent data) {
        SR_Projection = data;
    }

    private void checkStartStop() {
        if (isRec) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    BroadcastReceiver SR_Setting = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            Help.nextwithnew(context, SR_Setting_Activity.class);
        }
    };

    BroadcastReceiver SR_PauseResume = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            checkPauseResume();
        }
    };

    BroadcastReceiver SR_STOP = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            if (isRec) {
                stopRecordingAndOpenFile(context);
            }
            preferences.setisScreenRec(false);
            stopSelf();
        }
    };

    private void checkPauseResume() {
        if (tryPause) {
            resumeRecording();
        } else {
            pauseRecording();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        mF = 0;
        mContext = this;
        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        preferences = new Help.AppPreferences(mContext);

        remoteViews = null;
        registerReceiver(SR_START_STOP, new IntentFilter("startSR"));
        registerReceiver(SR_Setting, new IntentFilter("settingSR"));
        registerReceiver(SR_PauseResume, new IntentFilter("pauseResumeSR"));
        registerReceiver(SR_STOP, new IntentFilter("stopService"));

        if (preferences.getSRNoti()) {
            updateNotification();
        }

        if (SR_Projection == null) {
            Help.nextwithnew(mContext, RecordTransData.class);
        }

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

//        if (preferences.getPowerCD()) {
//            if (Help.isConnected(mContext)) {
//                checkDisconnection();
//            } else {
//                checkConnection();
//            }
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    public static View startstopView;
    public TextView startstop;

    @SuppressLint("WrongConstant")
    private View startstop(LayoutInflater layoutInflater) {
        startstopView = layoutInflater.inflate(R.layout.startstop_layout, null);

        final WindowManager.LayoutParams layoutParams;
        if (Build.VERSION.SDK_INT >= 26) {
            layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 8, -3);
        } else {
            layoutParams = new WindowManager.LayoutParams(-2, -2, 2002, 8, -3);
        }

        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = (int) preferences.getXH();
        layoutParams.y = (int) preferences.getYH();
        if (!preferences.getHOA()) {
            gone(startstopView);
        }
        startstop = startstopView.findViewById(R.id.startstop);

        startstopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStartStop();
            }
        });

        startstopView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (gestureDetector.onTouchEvent(motionEvent)) {
                        startstopView.performClick();
                        return true;
                    }
                    switch (motionEvent.getAction()) {
                        case 0:
                            initialX = layoutParams.x;
                            initialY = layoutParams.y;
                            initialTouchX = motionEvent.getRawX();
                            initialTouchY = motionEvent.getRawY();
                            return true;
                        case 1:
                            return true;
                        case 2:
                            layoutParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                            layoutParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                            preferences.setXH(layoutParams.x);
                            preferences.setYH(layoutParams.y);
                            wm.updateViewLayout(startstopView, layoutParams);
                            return true;
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    showLog(e.toString());
                    return false;
                }
            }
        });

        wm.addView(startstopView, layoutParams);
        return startstopView;
    }

    public View removeFloatingWidgetView;
    public ImageView remove_image_view;

    @SuppressLint("WrongConstant")
    private View addRemoveView(LayoutInflater layoutInflater) {
        this.removeFloatingWidgetView = layoutInflater.inflate(R.layout.vishtechno_remove_floating_widget_layout, null);

        int w = getResources().getDisplayMetrics().widthPixels;

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = 2038;
        } else {
            LAYOUT_FLAG = 2002;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        this.removeFloatingWidgetView.setVisibility(8);
        this.remove_image_view = this.removeFloatingWidgetView.findViewById(R.id.remove_img);

        RelativeLayout.LayoutParams params_close = new RelativeLayout.LayoutParams(w * 120 / 1080, w * 120 / 1080);
        params_close.setMargins(0, 0, 0, w * 160 / 1080);
        remove_image_view.setLayoutParams(params_close);

        this.wm.addView(this.removeFloatingWidgetView, layoutParams);
        return this.remove_image_view;
    }

    public View pauseresumeView;
    public static ImageView pauseresume;

    @SuppressLint("WrongConstant")
    private View addPauseResumeView(LayoutInflater layoutInflater) {
        this.pauseresumeView = layoutInflater.inflate(R.layout.pauseresume_layout, null);

        int w = getResources().getDisplayMetrics().widthPixels;

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = 2038;
        } else {
            LAYOUT_FLAG = 2002;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        this.pauseresumeView.setVisibility(8);
        this.pauseresume = this.pauseresumeView.findViewById(R.id.pauseresume);

        Help.setSize(pauseresume, 95, 95, false);

        if (params.x < w / 2) {
            layoutParams.x = params.x + Help.w(60);
        } else {
            layoutParams.x = params.x - Help.w(60);
        }
        layoutParams.y = params.y - Help.w(120);

        animSmallGone(pauseresumeView);

        pauseresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRec) {
                    checkPauseResume();
                    GoneWithAnim();
                } else {
                    startRecording();
                }
            }
        });

        this.wm.addView(this.pauseresumeView, layoutParams);
        return this.pauseresumeView;
    }

    public View openstopView;
    public static ImageView openstop;

    @SuppressLint("WrongConstant")
    private View addopenstopView(LayoutInflater layoutInflater) {
        this.openstopView = layoutInflater.inflate(R.layout.openstop_layout, null);

        int w = getResources().getDisplayMetrics().widthPixels;

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = 2038;
        } else {
            LAYOUT_FLAG = 2002;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        this.openstopView.setVisibility(8);
        this.openstop = this.openstopView.findViewById(R.id.openstop);

        Help.setSize(openstop, 95, 95, false);

        if (params.x < w / 2) {
            layoutParams.x = params.x + Help.w(130);
        } else {
            layoutParams.x = params.x - Help.w(130);
        }
        layoutParams.y = params.y;

        animSmallGone(openstopView);

        openstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRec) {
                    stopRecording();
                } else {
                    Help.nextwithnew(mContext, VISHTECHNO_MyCreation.class);
                    GoneWithAnim();
                }
            }
        });

        this.wm.addView(this.openstopView, layoutParams);
        return this.openstopView;
    }

    public View settingView;
    public static ImageView setting;

    @SuppressLint("WrongConstant")
    private View addsettingView(LayoutInflater layoutInflater) {
        this.settingView = layoutInflater.inflate(R.layout.setting_layout, null);

        int w = getResources().getDisplayMetrics().widthPixels;

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = 2038;
        } else {
            LAYOUT_FLAG = 2002;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, LAYOUT_FLAG, 262664, -3);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        this.settingView.setVisibility(8);
        this.setting = this.settingView.findViewById(R.id.setting);

        Help.setSize(setting, 95, 95, false);

        if (params.x < w / 2) {
            layoutParams.x = params.x + Help.w(60);
        } else {
            layoutParams.x = params.x - Help.w(60);
        }
        layoutParams.y = params.y + Help.w(120);

        animSmallGone(settingView);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Help.nextwithnew(mContext, SR_Setting_Activity.class);
                GoneWithAnim();
            }
        });

        this.wm.addView(this.settingView, layoutParams);
        return this.settingView;
    }

    public View transblackView;

    @SuppressLint("WrongConstant")
    private View addtransblackView(LayoutInflater layoutInflater) {
        this.transblackView = layoutInflater.inflate(R.layout.transblack_layout, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = 2038;
        } else {
            LAYOUT_FLAG = 2002;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(LAYOUT_FLAG);
        layoutParams.flags |= 262664;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.alpha = 1.0f;
        this.transblackView.setVisibility(8);

        transblackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoneWithAnim();
            }
        });

        this.wm.addView(this.transblackView, layoutParams);
        return this.transblackView;
    }

    Handler myHideButtonHandler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        mContext = this;
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        display = wm.getDefaultDisplay();
        final Resources resources = getResources();
        final int statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeightId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId);
        } else {
            statusBarHeight = 0;
        }
        updateDefaultDisplayInfo();
        dm.registerDisplayListener(displayListener, null);

        inflater = LayoutInflater.from(this);
        container = (RelativeLayout) inflater.inflate(R.layout.screenrecord_icon, null);
        videoView = container.findViewById(R.id.video_view);
        my_timer = container.findViewById(R.id.my_timer);

        maudioprofile = container.findViewById(R.id.aac_profile);
        mvideocodec = container.findViewById(R.id.video_codec);
        maudiocodec = container.findViewById(R.id.audio_codec);
        mvideoprofilelevel = container.findViewById(R.id.avc_profile);
        setOtherAdapter();

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        if (Build.VERSION.SDK_INT >= 26) {
            params = new WindowManager.LayoutParams(-2, -2, 2038, 262664, -3);
        } else {
            params = new WindowManager.LayoutParams(-2, -2, 2002, 262664, -3);
        }
        params.gravity = Gravity.TOP | Gravity.LEFT;

        container.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                dimWidget(v);

                myHideButtonHandler.removeCallbacksAndMessages(null);
                if (pauseresumeView.getVisibility() == View.GONE) {
                    container.animate().scaleX(1f).scaleY(1f).alpha(1f).translationX(0);
                    dimHandler.removeCallbacksAndMessages(null);
                    int i = getResources().getDisplayMetrics().heightPixels;

                    WindowManager.LayoutParams layoutParams1 = (WindowManager.LayoutParams) container.getLayoutParams();
                    if (layoutParams1.y < i / 2) {
                        if (layoutParams1.y < Help.w(130)) {
                            layoutParams1.y = Help.w(130);
                        }
                    } else {
                        if (layoutParams1.y > i - Help.w(320)) {
                            layoutParams1.y = i - Help.w(320);
                        }
                    }
                    wm.updateViewLayout(container, layoutParams1);
                    startUpdatingPosition(params);
                    visible(pauseresumeView);
                    visible(openstopView);
                    visible(settingView);
                    visible(transblackView);
                    animBigVisible(pauseresumeView);
                    animBigVisible(openstopView);
                    animBigVisible(settingView);
                } else {
                    GoneWithAnim();
                }
            }
        });

        setupCoordinates();
        addtransblackView(inflater);
        wm.addView(container, params);
        if (!preferences.getSRDis()) {
            gone(container);
        }
        attached = true;
        videoView.setSurfaceTextureListener(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        dimWidget(container);

        addRemoveView(inflater);
        addPauseResumeView(inflater);
        addopenstopView(inflater);
        addsettingView(inflater);
        startstop(inflater);

        container.setOnTouchListener(containerTouch);

        return START_STICKY;
    }

    @SuppressLint({"ResourceType"})
    private void setOtherAdapter() {
        Utils.findEncodersByTypeAsync("video/avc", new Utils.Callback() {
            public void onResult(MediaCodecInfo[] mediaCodecInfoArr) {
                mavccodecinfos = mediaCodecInfoArr;
                mvideocodec.setAdapter(createCodecsAdapter(mavccodecinfos));
                MediaCodecInfo videoCodecInfo = getVideoCodecInfo(getSelectedVideoCodec());
                resetAvcProfileLevelAdapter(videoCodecInfo.getCapabilitiesForType("video/avc"));
            }
        });

        Utils.findEncodersByTypeAsync("audio/mp4a-latm", new Utils.Callback() {
            public void onResult(MediaCodecInfo[] mediaCodecInfoArr) {
                maudiocodec.setAdapter(createCodecsAdapter(mediaCodecInfoArr));
            }
        });

        String[] aacProfiles = Utils.aacProfiles();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367048, new ArrayList());
        arrayAdapter.setDropDownViewResource(17367049);
        arrayAdapter.addAll(aacProfiles);
        this.maudioprofile.setAdapter(arrayAdapter);
    }

    public MediaCodecInfo getVideoCodecInfo(String str) {
        MediaCodecInfo mediaCodecInfo;
        if (str == null) {
            return null;
        }
        if (this.mavccodecinfos == null) {
            this.mavccodecinfos = Utils.findEncodersByType("video/avc");
        }
        int i = 0;
        while (true) {
            MediaCodecInfo[] mediaCodecInfoArr = this.mavccodecinfos;
            if (i >= mediaCodecInfoArr.length) {
                mediaCodecInfo = null;
                break;
            }
            mediaCodecInfo = mediaCodecInfoArr[i];
            if (mediaCodecInfo.getName().equals(str)) {
                break;
            }
            i++;
        }
        if (mediaCodecInfo == null) {
            return null;
        }
        return mediaCodecInfo;
    }

    @SuppressLint({"ResourceType"})
    public SpinnerAdapter createCodecsAdapter(MediaCodecInfo[] mediaCodecInfoArr) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367048, codecInfoNames(mediaCodecInfoArr));
        arrayAdapter.setDropDownViewResource(17367049);
        Log.e("Screen", String.valueOf(arrayAdapter));
        return arrayAdapter;
    }

    public static String[] codecInfoNames(MediaCodecInfo[] mediaCodecInfoArr) {
        String[] strArr = new String[mediaCodecInfoArr.length];
        for (int i = 0; i < mediaCodecInfoArr.length; i++) {
            strArr[i] = mediaCodecInfoArr[i].getName();
        }
        Log.e("Screen", String.valueOf(strArr));
        return strArr;
    }

    @SuppressLint({"ResourceType"})
    public void resetAvcProfileLevelAdapter(MediaCodecInfo.CodecCapabilities codecCapabilities) {
        MediaCodecInfo.CodecProfileLevel[] codecProfileLevelArr = codecCapabilities.profileLevels;
        if (codecProfileLevelArr == null || codecProfileLevelArr.length == 0) {
            this.mvideoprofilelevel.setEnabled(false);
            return;
        }
        this.mvideoprofilelevel.setEnabled(true);
        String[] strArr = new String[(codecProfileLevelArr.length + 1)];
        strArr[0] = "Default";
        int i = 0;
        while (i < codecProfileLevelArr.length) {
            int i2 = i + 1;
            strArr[i2] = Utils.avcProfileLevelToString(codecProfileLevelArr[i]);
            i = i2;
        }
        SpinnerAdapter adapter = this.mvideoprofilelevel.getAdapter();
        if (adapter == null || !(adapter instanceof ArrayAdapter)) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, 17367048, new ArrayList());
            arrayAdapter.setDropDownViewResource(17367049);
            arrayAdapter.addAll(strArr);
            this.mvideoprofilelevel.setAdapter(arrayAdapter);
            return;
        }
        ArrayAdapter arrayAdapter2 = (ArrayAdapter) adapter;
        arrayAdapter2.setNotifyOnChange(false);
        arrayAdapter2.clear();
        arrayAdapter2.addAll(strArr);
        arrayAdapter2.notifyDataSetChanged();
    }

    void animSmallGone(View view) {
        view.animate().scaleX(0f).scaleY(0f).alpha(0f).rotation(-360).setDuration(300);
        myHideButtonHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gone(view);
                gone(transblackView);
            }
        }, 300);
        dimWidget(container);
    }

    void animBigVisible(View view) {
        view.animate().scaleX(1f).scaleY(1f).alpha(1f).rotation(0).setDuration(300);
    }

    @SuppressLint("WrongConstant")
    void stopRecording() {
        showLog("Stop Screen Recording Click");
        stopRecordingAndOpenFile(mContext);
        container.setBackgroundResource(R.drawable.start_rec_click);
        notiHandler.removeCallbacksAndMessages(null);

        if (remoteViews != null) {
            remoteViews.setImageViewResource(R.id.into_txt, R.drawable.start_rec_text);
            remoteViews.setImageViewResource(R.id.sr_start_stop, R.drawable.nt_record);
            remoteViews.setViewVisibility(R.id.pause_resume, View.GONE);
            remoteViews.setViewVisibility(R.id.div, View.GONE);
            remoteViews.setViewVisibility(R.id.sr_setting, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.div2, View.VISIBLE);
            updateNoti();
        }

        if (preferences.getHOA()) {
            startstopView.setAlpha(1f);
        }

        gone(my_timer);
        my_timer.stop();
        my_timer.setBase(SystemClock.elapsedRealtime());
        container.setBackgroundResource(R.drawable.start_rec_click);

        openstop.setImageResource(R.drawable.open_start_click);
        pauseresume.setImageResource(R.drawable.start_recorder_click);
        GoneWithAnim();
    }

    @SuppressLint("WrongConstant")
    void startRecording() {
        showLog("Start Screen Recording");

        notiHandler.removeCallbacksAndMessages(null);
        if (VISHTECHNO_MyCreation.mContext != null) {
            VISHTECHNO_MyCreation.mContext.finish();
        }

        try {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mMediaProjection = mProjectionManager.getMediaProjection(-1, SR_Projection);
            mMediaProjection.registerCallback(mProjectionCallback, new Handler());
            startCapturing(mMediaProjection);
        } catch (Exception e) {
            showLog(e.toString());
            Help.nextwithnew(mContext, RecordTransData.class);
            return;
        }

        try {
            CheckForNoti();
        } catch (Exception e) {
            showLog(e.toString());
        }
        GoneWithAnim();

        tryPause = false;
        prevOutputPTSUs = 0;
        offsetPTSUs = 0;
        mLastPausedTimeUs = 0;
    }

    public MediaProjection.Callback mProjectionCallback = new MediaProjection.Callback() {
        public void onStop() {
            if (a_mrecorder != null) {
                stopRecorder();
            }
        }
    };

    void GoneWithAnim() {
        animSmallGone(pauseresumeView);
        animSmallGone(openstopView);
        animSmallGone(settingView);
    }

    void FastGoneWithAnim() {
        animFastSmallGone(pauseresumeView);
        animFastSmallGone(openstopView);
        animFastSmallGone(settingView);
    }

    void animFastSmallGone(View view) {
        view.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(0);
        gone(view);
    }

    public static long prevOutputPTSUs = 0;
    public static long offsetPTSUs = 0;
    public static long mLastPausedTimeUs = 0;

    static long timeWhenStopped;

    void pauseRecording() {
        tryPause = true;
        mLastPausedTimeUs = System.nanoTime() / 1000;
        pauseresume.setImageResource(R.drawable.resume_recorder_click);
        timeWhenStopped = my_timer.getBase() - SystemClock.elapsedRealtime();
        my_timer.stop();
    }

    void resumeRecording() {
        if (mLastPausedTimeUs != 0) {
            offsetPTSUs += (System.nanoTime() / 1000 - mLastPausedTimeUs);
            mLastPausedTimeUs = 0;
        }
        tryPause = false;
        pauseresume.setImageResource(R.drawable.pause_recorder_click);
        my_timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        my_timer.start();
    }

    public static long getPTSUs() {
        long result = System.nanoTime() / 1000L - offsetPTSUs;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs) {
            final long offset = prevOutputPTSUs - result;
            offsetPTSUs -= offset;
            result += offset;
        }
        return result;
    }

    View.OnTouchListener containerTouch = new View.OnTouchListener() {
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
                    container.performClick();
                    removeFloatingWidgetView.setVisibility(View.GONE);
                    return true;
                }
                switch (motionEvent.getAction()) {
                    case 0:
                        dimHandler.removeCallbacksAndMessages(null);
                        view.animate().scaleX(1f).scaleY(1f).alpha(1f).translationX(0);
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case 1:
                        dimWidget(view);
                        if (close) {
                            stopSelf();
                            preferences.setisScreenRec(false);
                            if (Help.mSRActivity != null) {
                                Help.mSRActivity.finish();
                            }
                        } else {
                            removeFloatingWidgetView.setVisibility(View.GONE);
                            startUpdatingPosition(params);
                        }
                        return true;
                    case 2:
                        FastGoneWithAnim();
                        params.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        params.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        wm.updateViewLayout(container, params);

                        if (!isRec) {
                            int w = getResources().getDisplayMetrics().widthPixels;
                            int h = getResources().getDisplayMetrics().heightPixels;
                            if (h < w) {
                                w = h;
                                h = w;
                            }
                            removeFloatingWidgetView.setVisibility(View.VISIBLE);
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
                        }
                        return true;
                    default:
                        return false;
                }
            } catch (Exception e) {
                showLog(e.toString());
                return false;
            }
        }
    };

    Handler dimHandler = new Handler();

    public void dimWidget(final View view) {
        dimHandler.removeCallbacksAndMessages(null);
        dimHandler.postDelayed(new Runnable() {
            public void run() {
                if (view != null) {
                    if (params.x > (getResources().getDisplayMetrics().widthPixels / 2)) {
                        view.animate().scaleX(0.78f).scaleY(0.78f).alpha(0.6f).translationX(Help.w(20)).setDuration(300);
                    } else {
                        view.animate().scaleX(0.78f).scaleY(0.78f).alpha(0.6f).translationX(Help.w(-20)).setDuration(300);
                    }
                }
            }
        }, 2000);
    }

    public void startUpdatingPosition(final WindowManager.LayoutParams layoutParams) {
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
                                preferences.setX(layoutParams.x);
                                preferences.setY(layoutParams.y);
                            }
                        } else if (layoutParams.x < (i - (Help.w(110)))) {
                            layoutParams.x += 50;
                            startUpdatingPosition(layoutParams);
                        } else {
                            layoutParams.x = (i - (Help.w(110)));
                            preferences.setX(layoutParams.x);
                            preferences.setY(layoutParams.y);
                        }
                        wm.updateViewLayout(container, layoutParams);

                        WindowManager.LayoutParams layoutParams1 = (WindowManager.LayoutParams) pauseresumeView.getLayoutParams();
                        if (layoutParams.x < i / 2) {
                            layoutParams1.x = layoutParams.x + Help.w(60);
                        } else {
                            layoutParams1.x = layoutParams.x - Help.w(60);
                        }
                        layoutParams1.y = layoutParams.y - Help.w(120);
                        wm.updateViewLayout(pauseresumeView, layoutParams1);

                        WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) settingView.getLayoutParams();
                        if (layoutParams.x < i / 2) {
                            layoutParams2.x = layoutParams.x + Help.w(60);
                        } else {
                            layoutParams2.x = layoutParams.x - Help.w(60);
                        }
                        layoutParams2.y = layoutParams.y + Help.w(120);
                        wm.updateViewLayout(settingView, layoutParams2);

                        WindowManager.LayoutParams layoutParams3 = (WindowManager.LayoutParams) openstopView.getLayoutParams();
                        if (layoutParams.x < i / 2) {
                            layoutParams3.x = layoutParams.x + Help.w(130);
                        } else {
                            layoutParams3.x = layoutParams.x - Help.w(130);
                        }
                        layoutParams3.y = layoutParams.y;
                        wm.updateViewLayout(openstopView, layoutParams3);
                    }
                }
            }, 10);
        } catch (Exception e) {
            showLog(e.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            if (this.params != null) {
                float f = (float) getResources().getDisplayMetrics().heightPixels;
                float f2 = (float) getResources().getDisplayMetrics().widthPixels;
                if (newConfig.orientation == 2) {
                    params.y = (int) Math.floor((double) ((((float) params.y) * f) / f2));
                } else {
                    params.y = Math.round((((float) params.y) * f) / f2);
                }
                params.x = params.x + 150;
            }
        } catch (Exception e) {
            showLog(e.toString());
        }
        startUpdatingPosition(params);
    }

    private boolean updateDefaultDisplayInfo() {
        display.getMetrics(displayMetrics);
        dispWidth = displayMetrics.widthPixels;
        dispHeight = displayMetrics.heightPixels;
        return true;
    }

    private void setupCoordinates() {
        if (dispWidth > dispHeight) {
            w = dispHeight * 110 / 1080;
            h = dispHeight * 110 / 1080;
            dispRect = new Rect(0, statusBarHeight, dispHeight, dispWidth);
        } else {
            w = dispWidth * 110 / 1080;
            h = dispWidth * 110 / 1080;
            dispRect = new Rect(0, statusBarHeight, dispWidth, dispHeight);
        }
        params.x = (int) preferences.getX();
        params.y = (int) preferences.getY();
        params.width = Math.round(w * scale);
        params.height = Math.round(h * scale);
        videoView.setPivotX(0);
        videoView.setPivotY(0);
        videoView.getLayoutParams().width = Math.round(w);
        videoView.getLayoutParams().height = Math.round(h);
        videoView.setScaleX(scale);
        videoView.setScaleY(scale);
    }

    private void relayout() {
        if (!attached)
            return;
        setupCoordinates();
        wm.updateViewLayout(container, params);
    }

    private void dismiss() {
        if (!attached)
            return;
        attached = false;
        dm.unregisterDisplayListener(displayListener);
        CDHandler.removeCallbacksAndMessages(null);
        try {
            wm.removeView(container);
            wm.removeView(removeFloatingWidgetView);
            wm.removeView(startstopView);
            wm.removeView(pauseresumeView);
            wm.removeView(settingView);
            wm.removeView(openstopView);
            wm.removeView(transblackView);
        } catch (Exception e) {
            showLog(e.toString());
        }
    }

    private final DisplayManager.DisplayListener displayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
            Log.d(TAG, "onDisplayAdded");
        }

        @Override
        public void onDisplayChanged(int displayId) {
            Log.d(TAG, "onDisplayChanged");
            if (displayId == display.getDisplayId()) {
                updateDefaultDisplayInfo();
                setupCoordinates();
                relayout();
            }
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            Log.d(TAG, "onDisplayRemoved");
            if (displayId == display.getDisplayId()) {
                dismiss();
            }
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        showLog("SR Remove");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showLog("SR Destroy");
        if (SR_START_STOP != null) {
            unregisterReceiver(SR_START_STOP);
        }
        if (SR_Setting != null) {
            unregisterReceiver(SR_Setting);
        }
        if (SR_PauseResume != null) {
            unregisterReceiver(SR_PauseResume);
        }
        if (SR_STOP != null) {
            unregisterReceiver(SR_STOP);
        }
        dismiss();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        Surface s = new Surface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public static Handler mHandler = new Handler();

    @Override
    public void hearShake() {
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (!myKM.inKeyguardRestrictedInputMode()) {
            if (preferences.getisScreenRec()) {
                if (preferences.getSRShake()) {
                    if (mF == 0) {
                        mF++;
                        showLog("Shaked");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mF = 0;
                            }
                        }, 3000);
                    }
                }
            }
        }
    }

    private class SingleTapConfirm extends SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    @SuppressLint("WrongConstant")
    public void updateNotification() {

        PendingIntent activity = PendingIntent.getActivity(this, 0,
                new Intent(this, VISHTECHNO_MainActivity.class).setFlags(268435456), 0);

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0,
                new Intent("startSR"), 134217728);

        PendingIntent broadcast1 = PendingIntent.getBroadcast(this, 0,
                new Intent("settingSR"), 134217728);

        PendingIntent broadcast2 = PendingIntent.getBroadcast(this, 0,
                new Intent("pauseResumeSR"), 134217728);

        PendingIntent broadcast3 = PendingIntent.getBroadcast(this, 0,
                new Intent("stopService"), 134217728);

        remoteViews = new RemoteViews(getPackageName(), R.layout.sr_notification);
        remoteViews.setOnClickPendingIntent(R.id.sr_start_stop, broadcast);
        remoteViews.setOnClickPendingIntent(R.id.sr_setting, broadcast1);
        remoteViews.setOnClickPendingIntent(R.id.pause_resume, broadcast2);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_power, broadcast3);

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("channelId", "System", 1);
//            notificationChannel.setLockscreenVisibility(-1);
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notification = new Notification.Builder(this, "channelId")
                    .setSmallIcon(R.drawable.sr_not)
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
                    setSmallIcon(R.drawable.sr_not)
                    .setContent(remoteViews)
                    .setAutoCancel(false)
                    .setPriority(Notification.PRIORITY_MAX)
//                    .setContentIntent(activity)
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        startForeground(1338, notification);
    }

    public void updateNoti() {
        startForeground(1338, notification);
    }

    public static Handler notiHandler = new Handler();
    public static Handler CDHandler = new Handler();

    void CheckForNoti() {
        notiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRec) {
                    if (remoteViews != null) {
                        remoteViews.setImageViewResource(R.id.into_txt, R.drawable.record_stop_text);
                        remoteViews.setImageViewResource(R.id.sr_start_stop, R.drawable.nt_stop);
                        remoteViews.setViewVisibility(R.id.pause_resume, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.div, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.sr_setting, View.GONE);
                        remoteViews.setViewVisibility(R.id.div2, View.GONE);
                        if (tryPause) {
                            remoteViews.setImageViewResource(R.id.pause_resume, R.drawable.nt_resume);
                        } else {
                            remoteViews.setImageViewResource(R.id.pause_resume, R.drawable.nt_pause);
                        }
                        updateNoti();
                    }
                } else {
                    if (remoteViews != null) {
                        remoteViews.setImageViewResource(R.id.into_txt, R.drawable.start_rec_text);
                        remoteViews.setImageViewResource(R.id.sr_start_stop, R.drawable.nt_record);
                        remoteViews.setViewVisibility(R.id.pause_resume, View.GONE);
                        remoteViews.setViewVisibility(R.id.div, View.GONE);
                        remoteViews.setViewVisibility(R.id.sr_setting, View.VISIBLE);
                        remoteViews.setViewVisibility(R.id.div2, View.VISIBLE);
                        updateNoti();
                    }
                }
                notiHandler.postDelayed(this, 100);
            }
        }, 100);
    }

    public static void checkConnection() {
        CDHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Help.isConnected(mContext)) {
                    checkDisconnection();
                    showLog("Connected");
//                    Help.Toast(mContext, "Connected");
                } else {
                    CDHandler.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    public static void checkDisconnection() {
        CDHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Help.isConnected(mContext)) {
                    CDHandler.postDelayed(this, 100);
                } else {
                    checkConnection();
                    showLog("Disconnected");
//                    Help.Toast(mContext, "Disconnected");
                }
            }
        }, 100);
    }

    public void startCapturing(MediaProjection mediaProjection) {
        VideoEncodeConfig createVideoConfig = createVideoConfig();
        AudioEncodeConfig createAudioConfig = createAudioConfig();
        if (createVideoConfig == null) {
            Help.Toast(getApplicationContext(), "ScreenRecorder create failure");
            return;
        }
        File savingDir = getSavingDir();
        if (savingDir.exists() || savingDir.mkdirs()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
            File file = new File(savingDir, "Screenrecorder-" + simpleDateFormat.format(new Date()) + "-" + createVideoConfig.width + "x" + createVideoConfig.height + ".mp4");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Create recorder with :");
            sb2.append(createVideoConfig);
            sb2.append(" \n ");
            sb2.append(createAudioConfig);
            sb2.append("\n ");
            sb2.append(file);
            Log.d("@@", sb2.toString());
            a_mrecorder = newRecorder(mediaProjection, createVideoConfig, createAudioConfig, file);
            startRecorder();
        } else {
            cancelRecorder();
        }
    }

    public ScreenRecorder newRecorder(MediaProjection mediaProjection, VideoEncodeConfig videoEncodeConfig, AudioEncodeConfig audioEncodeConfig, final File file) {
        ScreenRecorder screenRecorder = new ScreenRecorder(videoEncodeConfig, audioEncodeConfig, getOrCreateVirtualDisplay(mediaProjection, videoEncodeConfig), file.getAbsolutePath());
        screenRecorder.setCallback(new ScreenRecorder.Callback() {
            long startTime = 0;

            public void onStop(Throwable th) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        stopRecorder();
                    }
                });
                if (th != null) {
                    Help.Toast(getApplicationContext(), "Recorder error ! See logcat for more details");
                    th.printStackTrace();
                    file.delete();
                    return;
                }
                sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE").addCategory("android.intent.category.DEFAULT").setData(Uri.fromFile(file)));
            }

            public void onStart() {
            }

            public void onRecording(long j) {
                if (this.startTime <= 0) {
                    this.startTime = j;
                }
            }
        });
        return screenRecorder;
    }

    @TargetApi(21)
    public VirtualDisplay getOrCreateVirtualDisplay(MediaProjection mediaProjection, VideoEncodeConfig videoEncodeConfig) {
        if (this.mvirtualdisplay == null) {
            this.mvirtualdisplay = mediaProjection.createVirtualDisplay("ScreenRecorder-display0", videoEncodeConfig.width, videoEncodeConfig.height, 1, 1, (Surface) null, (VirtualDisplay.Callback) null, (Handler) null);
        } else {
            Point point = new Point();
            this.mvirtualdisplay.getDisplay().getSize(point);
            if (!(point.x == videoEncodeConfig.width && point.y == videoEncodeConfig.height)) {
                this.mvirtualdisplay.resize(videoEncodeConfig.width, videoEncodeConfig.height, 1);
            }
        }
        return this.mvirtualdisplay;
    }

    public void startRecorder() {
        ScreenRecorder screenRecorder = this.a_mrecorder;
        if (screenRecorder != null) {
            screenRecorder.start();
            a_mrecorder.setCallback(new ScreenRecorder.Callback() {
                @Override
                public void onStop(Throwable error) {
                    isRec = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openstop.setImageResource(R.drawable.open_start_click);
                            pauseresume.setImageResource(R.drawable.start_recorder_click);
                            container.setBackgroundResource(R.drawable.start_rec_click);
                            gone(my_timer);
                            my_timer.stop();
                            my_timer.setBase(SystemClock.elapsedRealtime());
                        }
                    });
                }

                @Override
                public void onStart() {
                    isRec = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openstop.setImageResource(R.drawable.stop_rec_click);
                            pauseresume.setImageResource(R.drawable.pause_recorder_click);
                            container.setBackgroundResource(R.drawable.timer_bg);
                            visible(my_timer);
                            my_timer.setBase(SystemClock.elapsedRealtime());
                            my_timer.start();

                            if (preferences.getHOA()) {
                                startstopView.setAlpha(0);
                            }
                        }
                    });
                }

                @Override
                public void onRecording(long presentationTimeUs) {
                }
            });
        }
    }

    @SuppressLint({"WrongConstant"})
    public void cancelRecorder() {
        if (this.a_mrecorder != null) {
            Toast.makeText(this, "Permission denied! Screen recorder is cancel", 0).show();
            stopRecorder();
        }
    }

    public void stopRecorder() {
        isRec = false;
        ScreenRecorder screenRecorder = this.a_mrecorder;
        if (screenRecorder != null) {
            screenRecorder.quit();
        }
        if (mvirtualdisplay != null) {
            mvirtualdisplay.setSurface((Surface) null);
            this.mvirtualdisplay.release();
            this.mvirtualdisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(this.mProjectionCallback);
            this.mMediaProjection.stop();
            this.mMediaProjection = null;
        }
    }

    public File getSavingDir() {
        return new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/Screen Recorder");
    }

    public VideoEncodeConfig createVideoConfig() {
        String selectedVideoCodec = getSelectedVideoCodec();
        if (selectedVideoCodec == null) {
            return null;
        }
//        String[] split = get_STRING("resolution", "1280x720").split("x");
        int DISPLAY_WIDTH = getResources().getDisplayMetrics().widthPixels;
        int DISPLAY_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        if (preferences.getQuality().equals("360p")) {
            DISPLAY_WIDTH = 360;
            DISPLAY_HEIGHT = 640;
        } else if (preferences.getQuality().equals("480p")) {
            DISPLAY_WIDTH = 480;
            DISPLAY_HEIGHT = 854;
        } else if (preferences.getQuality().equals("1080p")) {
            DISPLAY_WIDTH = 1080;
            DISPLAY_HEIGHT = 1920;
        } else {
            DISPLAY_WIDTH = 720;
            DISPLAY_HEIGHT = 1280;
        }
        int[] selectedWithHeight = {DISPLAY_WIDTH, DISPLAY_HEIGHT};
        int i = selectedWithHeight[0];
        int i2 = selectedWithHeight[1];
        int selectedFramerate = getSelectedFramerate();
        return new VideoEncodeConfig(i, i2, getSelectedVideoBitrate(), selectedFramerate, getSelectedIFrameInterval(), selectedVideoCodec, "video/avc", getSelectedProfileLevel());
    }

    public AudioEncodeConfig createAudioConfig() {
        if (!preferences.getisAudio()) {
            return null;
        }
        String selectedAudioCodec = getSelectedAudioCodec();
        int selectedAudioBitrate = getSelectedAudioBitrate();
        int selectedAudioSampleRate = getSelectedAudioSampleRate();
        int selectedAudioChannelCount = getSelectedAudioChannelCount();
        int selectedAudioProfile = getSelectedAudioProfile();
        return new AudioEncodeConfig(selectedAudioCodec, "audio/mp4a-latm", selectedAudioBitrate, selectedAudioSampleRate, selectedAudioChannelCount, selectedAudioProfile);
    }

    public int getSelectedFramerate() {
        return preferences.getVideoFPS();
    }

    public int getSelectedVideoBitrate() {
        return preferences.getVideoBitrate() * 1000;
    }

    public int getSelectedIFrameInterval() {
        return preferences.getVideoFI();
    }

    public int getSelectedAudioBitrate() {
        return preferences.getAudioBitrate() * 1000;
    }

    public int getSelectedAudioSampleRate() {
        return preferences.getAudioSampleRate();
    }

    public int getSelectedAudioChannelCount() {
        return preferences.getAudioChannel();
    }

    public int getSelectedAudioProfile() {
        NamedSpinner namedSpinner = this.maudioprofile;
        if (namedSpinner != null) {
            MediaCodecInfo.CodecProfileLevel profileLevel = Utils.toProfileLevel((String) namedSpinner.getSelectedItem());
            if (profileLevel == null) {
                return 1;
            }
            return profileLevel.profile;
        }
        throw new IllegalStateException();
    }

    public String getSelectedVideoCodec() {
        NamedSpinner namedSpinner = this.mvideocodec;
        if (namedSpinner == null) {
            return null;
        }
        return (String) namedSpinner.getSelectedItem();
    }

    public String getSelectedAudioCodec() {
        Log.e("Screen", String.valueOf(this.maudiocodec));
        NamedSpinner namedSpinner = this.maudiocodec;
        if (namedSpinner == null) {
            return null;
        }
        return (String) namedSpinner.getSelectedItem();
    }

    public MediaCodecInfo.CodecProfileLevel getSelectedProfileLevel() {
        Log.e("Screen", String.valueOf(this.mvideoprofilelevel));
        NamedSpinner namedSpinner = this.mvideoprofilelevel;
        if (namedSpinner != null) {
            return Utils.toProfileLevel((String) namedSpinner.getSelectedItem());
        }
        return null;
    }

    @SuppressLint({"WrongConstant"})
    public void stopRecordingAndOpenFile(Context context) {
        try {
            isRec = false;
            openstop.setImageResource(R.drawable.open_start_click);
            pauseresume.setImageResource(R.drawable.start_recorder_click);
            File file = new File(this.a_mrecorder.getSavedPath());
            stopRecorder();
            Help.mBoolean = true;
            Help.mVideoPath = file.getAbsolutePath();
            Help.nextwithnew(context, Video_Player.class);
        } catch (Exception e) {
            Log.e("Exception", String.valueOf(e));
        }
    }
}