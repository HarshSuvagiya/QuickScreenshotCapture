package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import vishtechno.bkm.quickscreenshotcapture.Adapter.Quality_Adapter;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_Constants;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_MainActivityMethodsInterface;
import vishtechno.bkm.quickscreenshotcapture.services.VISHTECHNO_FloatWidgetService;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.ColorPicker;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.OpacityBar;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.SVBar;

import java.io.File;
import java.util.ArrayList;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class VISHTECHNO_MainActivity extends AppCompatActivity implements VISHTECHNO_MainActivityMethodsInterface {

    Context mContext;

    ImageView start_btn;
    LinearLayout top_bar, seek_lay, lay_action, lay_nav;
    ImageView back, sss_img, dis_img, shake_img, status_img, nav_img;
    TextView title, txt_lay3, txt_lay5, txt_lay6, txt_lay7, txt_lay8,
            txt_lay9, txt_lay10, txt_lay11, txt_lay12;

    RelativeLayout lay_itype, lay_quality;
    TextView txt_itype, txt_quality, txt_location, txt_action;
    ImageView itype_arrow, quality_arrow, action_arrow;
    SeekBar quality_seek;

    Help.AppPreferences preferences;
    private FrameLayout adContainerView;

    PopupWindow mypopupWindow;
    ArrayList<String> array_list = new ArrayList<>();
    ArrayList<String> array_itype = new ArrayList<>();
    ArrayList<String> array_action = new ArrayList<>();

    ImageView text_off, text_on ,info, text, below_shape, icon1, icon2, icon3, icon4, icon5, icon6, icon7, icon8, icon9;
    TextView txt_permission, txt_system_setting, txts_lay3, txts_lay6,txts_lay8;
    ScrollView sc;
    LinearLayout myTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.vishtechno_activity_main);

        mContext = this;
        preferences = new Help.AppPreferences(mContext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_main_id));
        adContainerView.addView(adView);
        loadBanner();

        init();
        setSharedPreferences();

        File file = new File((String) VISHTECHNO_PreferenceManager.getSharedPref(mContext, VISHTECHNO_Constants.SCREENSHOT_DIR));
        if (file.exists()) {
            VISHTECHNO_PreferenceManager.screenshotDir = file;
        } else {
            VISHTECHNO_PreferenceManager.screenshotDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Screenshots");
            if (!VISHTECHNO_PreferenceManager.screenshotDir.exists()) {
                VISHTECHNO_PreferenceManager.screenshotDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Screenshots");
                if (!VISHTECHNO_PreferenceManager.screenshotDir.exists() && VISHTECHNO_PreferenceManager.screenshotDir.mkdir()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("FOLDER CREATED : ");
                    sb.append(VISHTECHNO_PreferenceManager.screenshotDir);
                    VISHTECHNO_PreferenceManager.printIt(sb.toString());
                }
            }
        }
        VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SCREENSHOT_DIR, VISHTECHNO_PreferenceManager.screenshotDir.getAbsolutePath());
        VISHTECHNO_PreferenceManager.printIt(VISHTECHNO_PreferenceManager.screenshotDir.getPath());

        setLay();
        setArray();
    }

    private void setArray() {
        array_itype.clear();
        array_itype.add("JPEG");
        array_itype.add("PNG");
        array_itype.add("WEBP");

        array_action.clear();
        array_action.add("Open Option");
        array_action.add("Open Edit");
        array_action.add("Open Preview");
        array_action.add("No Action");
    }

    private void setLay() {
        title.setTypeface(MyApplication.myBold);
        txt_permission.setTypeface(MyApplication.myBold);
        txt_system_setting.setTypeface(MyApplication.myBold);

        txt_lay3.setTypeface(MyApplication.myRegular);
        txt_lay5.setTypeface(MyApplication.myRegular);
        txt_lay6.setTypeface(MyApplication.myRegular);
        txt_lay7.setTypeface(MyApplication.myRegular);
        txt_lay8.setTypeface(MyApplication.myRegular);
        txt_lay9.setTypeface(MyApplication.myRegular);
        txt_lay10.setTypeface(MyApplication.myRegular);
        txt_lay11.setTypeface(MyApplication.myRegular);
        txt_lay12.setTypeface(MyApplication.myRegular);

        txt_itype.setTypeface(MyApplication.myRegular);
        txt_quality.setTypeface(MyApplication.myRegular);
        txt_location.setTypeface(MyApplication.myRegular);
        txt_action.setTypeface(MyApplication.myRegular);

        txts_lay3.setTypeface(MyApplication.myRegular);
        txts_lay6.setTypeface(MyApplication.myRegular);
        txts_lay8.setTypeface(MyApplication.myRegular);

        Help.setSize(icon1, 80, 80, false);
        Help.setSize(icon2, 80, 80, false);
        Help.setSize(icon3, 80, 80, false);
        Help.setSize(icon4, 80, 80, false);
        Help.setSize(icon5, 80, 80, false);
        Help.setSize(icon6, 80, 80, false);
        Help.setSize(icon7, 80, 80, false);
        Help.setSize(icon8, 80, 80, false);
        Help.setSize(icon9, 80, 80, false);

        Help.setSize(back, 70, 70, false);
        Help.setSize(info, 66, 66, false);
        Help.setSize(start_btn, 350, 350, false);
        Help.setSize(sss_img, 152, 76, false);
        Help.setSize(dis_img, 152, 76, false);
        Help.setSize(shake_img, 152, 76, false);
        Help.setSize(status_img, 152, 76, false);
        Help.setSize(nav_img, 152, 76, false);

        Help.setSize(itype_arrow, 30, 30, false);
        Help.setMargin(itype_arrow, 20, 0, 0, 0, false);
        Help.setSize(quality_arrow, 30, 30, false);
        Help.setMargin(quality_arrow, 20, 0, 0, 0, false);
        Help.setSize(action_arrow, 30, 30, false);
        Help.setMargin(action_arrow, 20, 0, 0, 0, false);

        Help.setSize(seek_lay, 912, 124, false);

        Help.setSize(top_bar, 1080, 868, false);
        Help.setSize(myTop, 1080, 150, false);
        Help.setSize(text, 956, 82, false);
        Help.setMargin(text, 0, 0, 0, 70, false);

        Help.setSize(text_off, 132, 94, false);
        Help.setSize(text_on, 132, 94, false);

        Help.setSize(below_shape, 1018, 132, false);
        Help.setMargin(below_shape, 0, 0, 0, -2, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Help.w(1000), LinearLayout.LayoutParams.WRAP_CONTENT);
        sc.setLayoutParams(params);
        Help.setMargin(sc, 0, -100, 0, 200, false);
    }

    private void init() {
        start_btn = findViewById(R.id.btn_start);
        top_bar = findViewById(R.id.top_bar);
        back = findViewById(R.id.back);
        info = findViewById(R.id.info);
        title = findViewById(R.id.title);

        text_off = findViewById(R.id.text_off);
        text_on = findViewById(R.id.text_on);
        text = findViewById(R.id.text);
        below_shape = findViewById(R.id.below_shape);
        sc = findViewById(R.id.sc);
        myTop = findViewById(R.id.myTop);

        txt_permission = findViewById(R.id.txt_permission);
        txt_system_setting = findViewById(R.id.txt_system_setting);
        txts_lay3 = findViewById(R.id.txts_lay3);
        txts_lay6 = findViewById(R.id.txts_lay6);
        txts_lay8 = findViewById(R.id.txts_lay8);
        icon1 = findViewById(R.id.icon1);
        icon2 = findViewById(R.id.icon2);
        icon3 = findViewById(R.id.icon3);
        icon4 = findViewById(R.id.icon4);
        icon5 = findViewById(R.id.icon5);
        icon6 = findViewById(R.id.icon6);
        icon7 = findViewById(R.id.icon7);
        icon8 = findViewById(R.id.icon8);
        icon9 = findViewById(R.id.icon9);

        txt_lay3 = findViewById(R.id.txt_lay3);
        txt_lay5 = findViewById(R.id.txt_lay5);
        txt_lay6 = findViewById(R.id.txt_lay6);
        txt_lay7 = findViewById(R.id.txt_lay7);
        txt_lay8 = findViewById(R.id.txt_lay8);
        txt_lay9 = findViewById(R.id.txt_lay9);
        txt_lay10 = findViewById(R.id.txt_lay10);
        txt_lay11 = findViewById(R.id.txt_lay11);
        txt_lay12 = findViewById(R.id.txt_lay12);

        txt_location = findViewById(R.id.txt_location);
        lay_nav = findViewById(R.id.lay_nav);

        lay_action = findViewById(R.id.lay_action);
        txt_action = findViewById(R.id.txt_action);
        action_arrow = findViewById(R.id.action_arrow);

        lay_itype = findViewById(R.id.lay_itype);
        txt_itype = findViewById(R.id.txt_itype);
        itype_arrow = findViewById(R.id.itype_arrow);

        seek_lay = findViewById(R.id.seek_lay);
        lay_quality = findViewById(R.id.lay_quality);
        txt_quality = findViewById(R.id.txt_quality);
        quality_arrow = findViewById(R.id.quality_arrow);
        quality_seek = findViewById(R.id.quality_seek);
        quality_seek.setMax(50);
        quality_seek.setProgress(preferences.getImageQuality() - 50);

        sss_img = findViewById(R.id.sss_img);
        dis_img = findViewById(R.id.dis_img);
        shake_img = findViewById(R.id.shake_img);
        status_img = findViewById(R.id.status_img);
        nav_img = findViewById(R.id.nav_img);

        txt_location.setText(Environment.getExternalStorageDirectory().toString() + "/" + mContext.getResources().getString(R.string.app_name));

        if (!Help.hasNavBar(mContext)) {
            gone(lay_nav);
        }
        setClick();
    }

    private void setClick() {
        start_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!VISHTECHNO_PreferenceManager.isScreenCaptureStarted) {
                    checkOverlayPermission();
                } else {
                    startFloatWidgetService(false);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);
        Log.e("AAA", "" + b1);

        if (b1) {
            start_btn.setImageResource(R.drawable.on_click);
            text_off.setImageResource(R.drawable.txt_off_unpress);
            text_on.setImageResource(R.drawable.txt_on_press);
        } else {
            start_btn.setImageResource(R.drawable.offt_click);
            text_off.setImageResource(R.drawable.txt_off_press);
            text_on.setImageResource(R.drawable.txt_on_unpress);
        }

        check();

        sss_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getSSSound()) {
                    preferences.setSSSound(false);
                } else {
                    preferences.setSSSound(true);
                }
                check();
            }
        });

        dis_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);
                if (preferences.getDis()) {
                    preferences.setDis(false);
                } else {
                    preferences.setDis(true);
                }
                check();
                if (b1) {
                    stopService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
                    if (Build.VERSION.SDK_INT >= 26) {
                        if (preferences.getNotifi()) {
                            startForegroundService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
                        } else {
                            startService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
                        }
                    } else {
                        startService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
                    }
                }
            }
        });

        shake_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getShake()) {
                    preferences.setShake(false);
                } else {
                    preferences.setShake(true);
                }
                check();
            }
        });

        lay_itype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("itype", itype_arrow);
                showPopup(view);
            }
        });

        quality_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    int p = i + 50;
                    preferences.setImageQuality(p);
                    check();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lay_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seek_lay.getVisibility() == View.VISIBLE) {
                    gone(seek_lay);
                    quality_arrow.setImageResource(R.drawable.down_click);
                } else {
                    visible(seek_lay);
                    quality_arrow.setImageResource(R.drawable.up_click);
                }
            }
        });

        lay_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("action", action_arrow);
                showPopup(view);
            }
        });

        status_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setExcludeSB(!preferences.getExcludeSB());
                check();
            }
        });

        nav_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setExcludeNB(!preferences.getExcludeNB());
                check();
            }
        });
    }

    void showPopup(View view) {
        int[] values = new int[2];
        view.getLocationInWindow(values);
        int positionOfIcon = values[1];
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels - (displayMetrics.heightPixels / 5);
        if (positionOfIcon > height) {
            mypopupWindow.showAsDropDown(view, 0, Help.w(-350));
        } else {
            mypopupWindow.showAsDropDown(view, 0, Help.w(-75));
        }
    }

    Techniques technique;
    public YoYo.YoYoString rope;

    void startAnim(int count,boolean b) {
        if (count == 1) {
            technique = Techniques.ZoomOut;
        } else {
            technique = Techniques.ZoomIn;
        }
        long time = 300;

        rope = YoYo.with(technique)
                .duration(time)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (count == 1) {
                            startAnim(count + 1,b);
                            if(b){
                                start_btn.setImageResource(R.drawable.on_click);
                            }else {
                                start_btn.setImageResource(R.drawable.offt_click);
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(start_btn);
    }

    void startAnim2(int count) {
        if (count == 1) {
            technique = Techniques.RotateOut;
        } else {
            technique = Techniques.RotateIn;
        }
        long time = 300;

        rope = YoYo.with(technique)
                .duration(time)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (count == 1) {
                            startAnim2(count + 1);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(start_btn);
    }


    private void check() {
        if (preferences.getSSSound()) {
            sss_img.setImageResource(R.drawable.on);
        } else {
            sss_img.setImageResource(R.drawable.off);
        }
        if (preferences.getDis()) {
            dis_img.setImageResource(R.drawable.on);
        } else {
            dis_img.setImageResource(R.drawable.off);
        }
        if (preferences.getShake()) {
            shake_img.setImageResource(R.drawable.on);
        } else {
            shake_img.setImageResource(R.drawable.off);
        }

        if (preferences.getExcludeSB()) {
            status_img.setImageResource(R.drawable.on);
        } else {
            status_img.setImageResource(R.drawable.off);
        }

        if (preferences.getExcludeNB()) {
            nav_img.setImageResource(R.drawable.on);
        } else {
            nav_img.setImageResource(R.drawable.off);
        }

        txt_itype.setText(preferences.getImageType());
        txt_quality.setText(preferences.getImageQuality() + "%");
        txt_action.setText(preferences.getOpenType());
    }

    @Override
    public void onSelection(boolean z) {

    }

    @Override
    public void onStarted(boolean z) {

    }

    @SuppressLint({"MissingSuperCall"})
    public void onSaveInstanceState(Bundle bundle) {
        if (VISHTECHNO_PreferenceManager.selectedScreenshots.size() > 0) {
            bundle.putIntegerArrayList("savedSelected", VISHTECHNO_PreferenceManager.selectedScreenshots);
        }
    }

    public void setSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("squick", 0);
        try {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.VERSION_NAME, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR ", e);
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.FLOATING_BUTTON)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.FLOATING_BUTTON, Boolean.valueOf(true));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SHAKESHOT)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SHAKESHOT, Boolean.valueOf(false));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SHAKE_THRESHOLD)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SHAKE_THRESHOLD, Integer.valueOf(4));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.NOTIFICATION_DARK)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.NOTIFICATION_DARK, Boolean.valueOf(false));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SHOW_POPUP)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SHOW_POPUP, Boolean.valueOf(true));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.WIDGET_X)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.WIDGET_X, Integer.valueOf(0));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.WIDGET_Y)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.WIDGET_Y, Integer.valueOf(100));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.WIDGET_SIZE)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.WIDGET_SIZE, Integer.valueOf(12));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.TAKE_SS_AFTER)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.TAKE_SS_AFTER, Integer.valueOf(0));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SS_TAKEN_HF)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SS_TAKEN_HF, Boolean.valueOf(true));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SS_DELETE_HF)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SS_DELETE_HF, Boolean.valueOf(true));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.OVERLAY_COLOR)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.OVERLAY_COLOR, Integer.valueOf(-1));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SELECTION_LINE_X)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SELECTION_LINE_X, Integer.valueOf(5));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SELECTION_LINE_Y)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SELECTION_LINE_Y, Integer.valueOf(5));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SHOWED_PINCH_ZOOM)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SHOWED_PINCH_ZOOM, Boolean.valueOf(false));
        }
        if (!sharedPreferences.contains(VISHTECHNO_Constants.SCREENSHOT_DIR)) {
            VISHTECHNO_PreferenceManager.setSharedPref(mContext, VISHTECHNO_Constants.SCREENSHOT_DIR, "");
        }
    }

    public void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(mContext)) {
            startFloatWidgetService(true);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("package:");
        sb.append(getPackageName());
        startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(sb.toString())), 102);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != 102) {
            if (i != 105 || Build.VERSION.SDK_INT < 23) {
                return;
            }
        } else if (Build.VERSION.SDK_INT < 23 || !Settings.canDrawOverlays(mContext)) {
            startFloatWidgetService(false);
            VISHTECHNO_PreferenceManager.toastIt(mContext, "Allow ScreenshotQuick to draw overlay to use Floating Button");
        } else {
            startFloatWidgetService(true);
        }
    }

    public void startFloatWidgetService(boolean z) {

        boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);

        if (!b1) {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
            } else {
                startService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
            }
            text_off.setImageResource(R.drawable.txt_off_unpress);
            text_on.setImageResource(R.drawable.txt_on_press);
//            startService(new Intent(mContext,VISHTECHNO_RecieverService.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim(1,true);
                    startAnim2(1);
                }
            },100);
        } else {
            preferences.setisService(false);
            startAnim(1,false);
            startAnim2(1);
            text_off.setImageResource(R.drawable.txt_off_press);
            text_on.setImageResource(R.drawable.txt_on_unpress);
//            stopService(new Intent(mContext,VISHTECHNO_RecieverService.class));
            stopService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
        }
    }

    public boolean checkServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);
        Log.e("AAA", "" + b1);

        if (b1) {
            start_btn.setImageResource(R.drawable.on_click);
            text_off.setImageResource(R.drawable.txt_off_unpress);
            text_on.setImageResource(R.drawable.txt_on_press);
            preferences.setisService(true);
        } else {
            start_btn.setImageResource(R.drawable.offt_click);
            text_off.setImageResource(R.drawable.txt_off_press);
            text_on.setImageResource(R.drawable.txt_on_unpress);
        }

    }

    public void showSeeting() {
        boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);
        if (b1) {
            Help.Toast(mContext, "First Stop Service");
        } else {
            showResizePopUp();
        }
    }

    int selectedColor, x, y;

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
            final TextView txt_sel_color = dialog.findViewById(R.id.txt_sel_color);
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

            txt1.setTypeface(MyApplication.myRegular);
            txt2.setTypeface(MyApplication.myRegular);
            textView.setTypeface(MyApplication.myRegular);
            textView2.setTypeface(MyApplication.myRegular);
            txt_sel_color.setTypeface(MyApplication.myRegular);

            seekBar.setProgress(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.TAKE_SS_AFTER)).intValue());
            textView.setText(String.valueOf(seekBar.getProgress()));
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    textView.setText(String.valueOf(i));
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.TAKE_SS_AFTER, Integer.valueOf(seekBar.getProgress()));
                }
            });

            int intValue = ((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)).intValue() + 20;
            int i = (int) ((((float) intValue) * VISHTECHNO_PreferenceManager.density) + 0.5f);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i, i);

            imageView.setLayoutParams(layoutParams);
//            imageView.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), i));
            imageView.setBackground(getBackgroundShape(Color.parseColor(preferences.getSelColor()), i));
            seekBar2.setProgress(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)).intValue());
            textView2.setText(String.valueOf(VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE)));

            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    textView2.setText(String.valueOf(i));
                    LinearLayout.LayoutParams layoutParams2 = layoutParams;
                    int i2 = (int) ((((float) (i + 20)) * VISHTECHNO_PreferenceManager.density) + 0.5f);
                    layoutParams2.height = i2;
                    layoutParams2.width = i2;
                    imageView.setLayoutParams(layoutParams2);
//                    imageView.setBackground(getBackgroundShape(((Integer) VISHTECHNO_PreferenceManager.getSharedPref(getApplicationContext(), VISHTECHNO_Constants.OVERLAY_COLOR)).intValue(), i2));
                    imageView.setBackground(getBackgroundShape(Color.parseColor(preferences.getSelColor()), i2));
                }

                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    VISHTECHNO_PreferenceManager.setSharedPref(getApplicationContext(), VISHTECHNO_Constants.WIDGET_SIZE, Integer.valueOf(seekBar.getProgress()));
                }
            });

            selectedColor = Color.parseColor(preferences.getSelColor());
            iv_change_color_colorbox.setColorFilter(selectedColor);

            try {
                iv_change_color_colorbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color_dialog();
                    }
                });

                btn_done.setOnClickListener(new View.OnClickListener() {
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
                        dialog.dismiss();

                    }
                });

            } catch (Exception e) {
                VISHTECHNO_PreferenceManager.printIt("ERROR IN ChangeColor IN setOnTouchListener", e);
            }

            if (Build.VERSION.SDK_INT >= 26) {
                dialog.getWindow().setType(2038);
            } else {
                dialog.getWindow().setType(2003);
            }
            dialog.show();
        } catch (Exception e) {
            VISHTECHNO_PreferenceManager.printIt("ERROR IN VISHTECHNO_FloatWidgetService showResizePopUp()", e);
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

    void color_dialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.pop_color);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 900, 1310, false);

        TextView title = dialog.findViewById(R.id.title);
        title.setTypeface(MyApplication.myBold);

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

        Help.setSize(picker, 550, 550, false);

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

    AdView adView;

    private void loadBanner() {
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(ConsentSDK.getAdRequest(this));
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void setPopUpWindow(String type, ImageView image) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.quality_popup, null);

        LinearLayout mainlay = view.findViewById(R.id.mainLay);

        mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Help.w(423), RelativeLayout.LayoutParams.WRAP_CONTENT);
        mainlay.setLayoutParams(params);

        mainlay.setPadding(Help.w(30), Help.w(30), Help.w(25), Help.w(45));
        ListView list = view.findViewById(R.id.list);

        Quality_Adapter quality_adapter = null;
        if (type.equals("itype")) {
            array_list = array_itype;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getImageType());
        } else if (type.equals("action")) {
            array_list = array_action;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getOpenType());
        }

        list.setAdapter(quality_adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("itype")) {
                    preferences.setImageType(array_list.get(position));
                } else if (type.equals("action")) {
                    preferences.setOpenType(array_list.get(position));
                }
                check();
                mypopupWindow.dismiss();
            }
        });

        image.setImageResource(R.drawable.up_click);
        mypopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                image.setImageResource(R.drawable.down_click);
            }
        });

    }
}
