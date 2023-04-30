package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import vishtechno.bkm.quickscreenshotcapture.R;

import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help;
import vishtechno.bkm.quickscreenshotcapture.photoview.PhotoView;

import java.io.File;
import java.lang.reflect.Method;

public class VISHTECHNO_Share_DeleteActivity extends AppCompatActivity {

    Context mContext;
    PhotoView imageview;
    ImageView share, delete, back;
    TextView title;
    private ImageLoader imageLoader;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.vishtechno_activity_share__delete);

        mContext = this;

        loadInterstitial();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_preview_id));
        adContainerView.addView(adView);
        loadBanner();


        init();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setLay();
    }

    private void setLay() {
        title.setTypeface(MyApplication.myRegular);

        Help.setSize(back, 118, 96, false);
        Help.setSize(share, 354, 130, false);
        Help.setSize(delete, 354, 130, false);
        Help.setMargin(share, 0,50,0,30, false);
        Help.setMargin(delete, 50,50,0,30, false);
    }

    private void init() {

        imageview = findViewById(R.id.imageview);
        share = findViewById(R.id.share);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + VISHTECHNO_help.creation_path));
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                startActivity(Intent.createChooser(intent, "Share Image"));
                if(VISHTECHNO_help.creation_path!=null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    File f = new File(VISHTECHNO_help.creation_path);
                    Uri uri = Uri.parse("file://" + f.getAbsolutePath());
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share Image"));
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(VISHTECHNO_help.creation_path!=null) {
                    File mediaStorageDir = new File(VISHTECHNO_help.creation_path);

                    VISHTECHNO_help.deleteDirectory(mediaStorageDir);

                    finish();

                    MediaScannerConnection.scanFile(mContext, new String[]{VISHTECHNO_help.creation_path}, new String[]{"image/*"}, null);

                    Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_LONG).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

//        Glide.with(mContext).load(VISHTECHNO_help.creation_path).into(imageview);
        initImageLoader();
        build(VISHTECHNO_help.creation_path, imageview);

    }

    @Override
    public void onBackPressed() {
         if (VISHTECHNO_help.from == 1) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        finish();
                    }
                });
                interstitialAd.show();
            } else {
                finish();
            }
        }else {
             super.onBackPressed();
         }
    }

    InterstitialAd interstitialAd;

    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_preview_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }

    private void initImageLoader() {
        // TODO Auto-generated method stub
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .resetViewBeforeLoading(true)

                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(displayOptions).build();

        imageLoader.init(config);
    }

    void build(String url, ImageView image) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .build();
        ImageLoader.getInstance().displayImage("file:///" + url, image,
                defaultOptions);
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
}
