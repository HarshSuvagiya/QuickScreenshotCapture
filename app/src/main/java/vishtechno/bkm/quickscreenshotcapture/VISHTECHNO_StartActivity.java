package vishtechno.bkm.quickscreenshotcapture;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import pl.droidsonroids.gif.GifImageView;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.judemanutd.autostarter.AutoStartPermissionHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon;
import vishtechno.bkm.quickscreenshotcapture.services.VISHTECHNO_FloatWidgetService;

import static vishtechno.bkm.quickscreenshotcapture.Help.BatteryOptimization;
import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;
import static vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon.isRec;

public class VISHTECHNO_StartActivity extends AppCompatActivity {

    public static Activity mContext;
    ImageView start, creation, logo, stitch_photo, photo_edit,screen_recorder;
    ImageView share, rate, pp, title;
    private static final int REQUEST_CODE = 1101;
    public static boolean isEdit, attached;
    private FrameLayout adContainerView;

    InnerRecevier innerReceiver;
    Help.AppPreferences preferences;
    RelativeLayout showcase_lay;
    GifImageView my_gif;
    ImageView checkbox, got_it;
    boolean check;
    Handler handler = new Handler();
    public static UnifiedNativeAd unifiedNativeAd1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vishtechno_activity_start);

        mContext = this;
        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        Help.FS(mContext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_start_id));
        adContainerView.addView(adView);

        initConsentSDK(this);
        if (!isConsentDone() && isNetworkAvailable() && ConsentSDK.isUserLocationWithinEea(this)) {
//        if(!isConsentDone()&& isNetworkAvailable()){
            consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {

                @Override
                public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                    setPref();
                    ConsentSDK.Builder.dialog.dismiss();
                    goToMain();
                }
            });


        } else {
            goToMain();
        }

//        checkPermissions();
        init();
        setLay();

        deviceCheck();
        BatteryOptimization(mContext);
        innerReceiver = new InnerRecevier();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(innerReceiver, intentFilter);
//        showLog(Help.getStatusBarHeight(mContext)+"");
//        showLog(Help.getNavHeight(mContext)+"");
//        showLog(Help.hasNavBar(mContext)+"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Help.FS2(mContext);
        isEdit = false;
        attached = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        attached = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(innerReceiver);
    }

    private void setLay() {
        Help.setSize(logo, 1080, 760, false);

//        Help.setSize(title, 670, 122, false);
//        Help.setMargin(title, 0, 80, 0, 0, true);

        Help.setSize(start, 466, 376, false);
        Help.setSize(screen_recorder, 466, 376, false);
        Help.setSize(stitch_photo, 966, 192, false);
        Help.setSize(creation, 966, 192, false);

        Help.setMargin(start, 0, 20, 0, 0, true);
        Help.setMargin(screen_recorder, 40, 0, 0, 0, true);
        Help.setMargin(stitch_photo, 0, 40, 0, 0, true);
        Help.setMargin(creation, 0, 40, 0, 0, true);

        Help.setSize(share, 96, 78, false);
        Help.setSize(pp, 96, 78, false);
    }

    private void init() {

        start = findViewById(R.id.start);
        creation = findViewById(R.id.creation);
        share = findViewById(R.id.share);
        rate = findViewById(R.id.rate);
        pp = findViewById(R.id.pp);
        logo = findViewById(R.id.logo);
        stitch_photo = findViewById(R.id.stitch_photo);
        title = findViewById(R.id.title);
        photo_edit = findViewById(R.id.photo_edit);
        screen_recorder = findViewById(R.id.screen_recorder);

        showcase_lay = findViewById(R.id.showcase_lay);
        my_gif = findViewById(R.id.my_gif);
        checkbox = findViewById(R.id.checkbox);
        got_it = findViewById(R.id.got_it);

        preferences = new Help.AppPreferences(this);

        check = false;
//        if (preferences.getMiPopCase()) {
//            gone(showcase_lay);
//        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(mContext, VISHTECHNO_MainActivity.class));

                if (interstitialAd.isLoaded()) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            startActivity(new Intent(mContext, VISHTECHNO_MainActivity.class));
                            loadInterstitial();
                        }
                    });
                    interstitialAd.show();
                } else {
                    startActivity(new Intent(mContext, VISHTECHNO_MainActivity.class));
                }
            }
        });

        creation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkper()) {
                    if (isRec) {
                        Help.Toast(mContext, "Please stop Recording First");
                    } else {
                        changeToggle();
                    }
                } else {
                    Help.Toast(mContext, "Require Permission is Not Given");
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

            }
        });

        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {

                    startActivity(new Intent(mContext, VISHTECHNO_Privacy_policy.class));
                } else {
                    Help.Toast(mContext, "Please connect to Internet");
                }

            }
        });

        stitch_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkper()) {
//                    Help.myList.clear();
//                    Help.nextwithnew(mContext, ImageFolderActivity.class);
//                } else {
//                    Help.Toast(mContext, "Require Permission is Not Given");
//                }

                if (interstitialAd.isLoaded()) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            if (checkper()) {
                                Help.myList.clear();
                                Help.nextwithnew(mContext, ImageFolderActivity.class);
                            } else {
                                Help.Toast(mContext, "Require Permission is Not Given");
                            }
                            loadInterstitial();
                        }
                    });
                    interstitialAd.show();
                } else {
                    if (checkper()) {
                        Help.myList.clear();
                        Help.nextwithnew(mContext, ImageFolderActivity.class);
                    } else {
                        Help.Toast(mContext, "Require Permission is Not Given");
                    }
                }

            }
        });

        photo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isEdit=true;
//                Help.nextwithnew(mContext, ImageFolderActivity.class);

                if (interstitialAd.isLoaded()) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            isEdit = true;
                            Help.nextwithnew(mContext, ImageFolderActivity.class);
                            loadInterstitial();
                        }
                    });
                    interstitialAd.show();
                } else {
                    isEdit = true;
                    Help.nextwithnew(mContext, ImageFolderActivity.class);
                }

            }
        });


        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check) {
                    check = false;
                    checkbox.setImageResource(R.drawable.checkbox_unpress);
                } else {
                    check = true;
                    checkbox.setImageResource(R.drawable.checkbox_press);
                }
            }
        });

        got_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setMiPopCase(check);
                gone(showcase_lay);
                showOnLock(mContext);
            }
        });

        screen_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkper()) {
//                    Help.nextwithnew(mContext, SR_Setting_Activity.class);

                    if (interstitialAd.isLoaded()) {
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                Help.nextwithnew(mContext, SR_Setting_Activity.class);
                                loadInterstitial();
                            }
                        });
                        interstitialAd.show();
                    } else {
                        Help.nextwithnew(mContext, SR_Setting_Activity.class);
                    }

                } else {
                    Help.Toast(mContext, "Require Permission is Not Given");
                }
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Help.AppPreferences preferences = new Help.AppPreferences(mContext);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(mContext)) {
                setService(preferences);
            }
        } else {
            setService(preferences);
        }
    }

    private void changeToggle() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(mContext, VISHTECHNO_MyCreation.class));
                    loadInterstitial();
                }
            });
            interstitialAd.show();
        } else {
            startActivity(new Intent(mContext, VISHTECHNO_MyCreation.class));
        }
    }

    private void setService(Help.AppPreferences preferences) {
        if (preferences.getisService()) {
            boolean b1 = checkServiceRunning(VISHTECHNO_FloatWidgetService.class);
            if (!b1) {
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
        } else {
            stopService(new Intent(mContext, VISHTECHNO_FloatWidgetService.class));
        }

        if (preferences.getisScreenRec()) {
            boolean b1 = checkServiceRunning(ScreenRecord_Icon.class);
            if (!b1) {
                startService(new Intent(mContext, ScreenRecord_Icon.class));
            }
        } else {
            stopService(new Intent(mContext, ScreenRecord_Icon.class));
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

    private void checkPermissions() {

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};

        if (Build.VERSION.SDK_INT > 22) {

            requestPermissions(permissions, REQUEST_CODE);

        } else {

            onSuccess();

        }

    }

    boolean checkper() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {

                        finish();

                        return;
                    }
                }
                onSuccess();
            } else {
                finish();
            }
        }

    }

    public void onSuccess() {

        // Code for onSuccess
        checkOverlayPermission();
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String miui = (String) get.invoke(c, "ro.miui.ui.version.name");
            if (miui != null && (miui.contains("11") || miui.contains("12") || miui.contains("13") || miui.contains("14"))) {
                if (!preferences.getMiPopCase()) {
                    visible(showcase_lay);
                }
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                intent.setData(uri);
//                startActivity(intent);
            }
        } catch (Exception e) {
            showLog(e.toString());
        }
    }

    public void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {

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

            VISHTECHNO_PreferenceManager.toastIt(mContext, "Allow ScreenshotQuick to draw overlay to use Floating Button");
        } else {

        }
    }

    InterstitialAd interstitialAd;

    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_start_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }

    private ConsentSDK consentSDK;

    private void initConsentSDK(Context context) {
        // Initialize ConsentSDK
        consentSDK = new ConsentSDK.Builder(this)
//                .addTestDeviceId("77259D4779E9E87A669924752B4E3B2B")
                .addCustomLogTag("CUSTOM_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy(getString(R.string.privacy_link)) // Add your privacy policy url
                .addPublisherId(getString(R.string.admob_publisher_id)) // Add your admob publisher id
                .build();
    }

    private void goToMain() {

        checkPermissions();

        loadInterstitial();

        initNativeAdvanceAds();

        loadBanner();
    }

    void setPref() {

        SharedPreferences.Editor editor = getSharedPreferences("consentpreff", MODE_PRIVATE).edit();
        editor.putBoolean("isDone", true);
        editor.apply();
    }

    boolean isConsentDone() {


        SharedPreferences prefs = getSharedPreferences("consentpreff", MODE_PRIVATE);
        return prefs.getBoolean("isDone", false);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    public UnifiedNativeAdView nativeAdView;

    private void initNativeAdvanceAds() {


// MobileAds.initialize(this,
// getString(R.string.admob_app_id));

        flNativeAds = findViewById(R.id.flNativeAds);
        flNativeAds.setVisibility(View.GONE);
        nativeAdView = (UnifiedNativeAdView) findViewById(R.id.ad_view);

// The MediaView will display a video asset if one is present in the ad, and the
// first image asset otherwise.
        nativeAdView.setMediaView((com.google.android.gms.ads.formats.MediaView) nativeAdView.findViewById(R.id.ad_media));

// Register the view used for each individual asset.
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_icon));
// nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
// nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        loadNativeAds();
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {


        VideoController vc = nativeAd.getVideoController();


        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {

                super.onVideoEnd();
            }
        });

// Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

// These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
// check before trying to display them.
        com.google.android.gms.ads.formats.NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

// if (nativeAd.getPrice() == null) {
// adView.getPriceView().setVisibility(View.INVISIBLE);
// } else {
// adView.getPriceView().setVisibility(View.VISIBLE);
// ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
// }

// if (nativeAd.getStore() == null) {
// adView.getStoreView().setVisibility(View.INVISIBLE);
// } else {
// adView.getStoreView().setVisibility(View.VISIBLE);
// ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
// }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

// Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    private FrameLayout flNativeAds;

    private void loadNativeAds() {
        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_start_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
//						mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            logo.setVisibility(View.INVISIBLE);
                            flNativeAds.setVisibility(View.VISIBLE);
                            unifiedNativeAd1 = unifiedNativeAd;
                            populateNativeAdView(unifiedNativeAd, nativeAdView);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                        }
                    }
                }).withNativeAdOptions(adOptions).build();

        // Load the Native ads.
        adLoader.loadAd(ConsentSDK.getAdRequest(this));
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(getApplicationContext(), Exit.class));

        if (interstitialAd.isLoaded()) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(getApplicationContext(), Exit.class));
                    loadInterstitial();
                }
            });
            interstitialAd.show();
        } else {
            startActivity(new Intent(getApplicationContext(), Exit.class));
        }
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

    public class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        showLog("Home Press");
//                        mHomeClick = true;
//                        if (attached) {
//                            mContext.finish();
//                        }
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        showLog("Recent App Press");
                    }
                }
            }
        }
    }

    public void deviceCheck() {

        String deviceManufacturer = Build.MANUFACTURER;

        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences("auto", 0);
        final String skipMessage = settings.getString("skipMessage", "NOT checked");

        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(eulaLayout);

        TextView allow = (TextView) eulaLayout.findViewById(R.id.tvallow);
        TextView tvtext = (TextView) eulaLayout.findViewById(R.id.tvptext);

        tvtext.setText("Your device requires additional AUTO START permission to work efficiently.");

        final CheckBox dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String checkBoxResult = skipMessage;

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences("auto", 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName(
//                        PACKAGE_NAME, PACKAGE_ACITIVITY));
//                startActivity(intent);

                if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(mContext)) {
//                    Help.Toast(mContext, "Turn on autostart for this app from the list");
                    AutoStartPermissionHelper.getInstance().getAutoStartPermission(mContext);
                } else {
                    Help.Toast(mContext, "No Auto Start Option Available");
                }
                dialog.dismiss();
            }
        });

        if (!skipMessage.equals("checked")) {
            dialog.show();
        }
    }

    @SuppressLint("WrongConstant")
    public void showOnLock(Context context) {
        try {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(268468224);
            intent.addFlags(1073741824);
            intent.addFlags(8388608);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
