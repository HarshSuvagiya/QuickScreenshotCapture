package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Exit extends Activity {
    ImageView yes;
    LinearLayout lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.exit_dialog);

        initNativeAdvanceAds();

        yes = findViewById(R.id.ivok);
        lay = findViewById(R.id.lay);


        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 480 / 1080,
                getResources().getDisplayMetrics().heightPixels * 104 / 1920);
        params1.gravity= Gravity.CENTER;
        yes.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 480 / 1080,
                getResources().getDisplayMetrics().heightPixels * 200 / 1920);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lay.setLayoutParams(params2);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();

            }
        });
    }

    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private UnifiedNativeAdView nativeAdView;



    private void initNativeAdvanceAds(){



// MobileAds.initialize(this,
// getString(R.string.admob_app_id));

        flNativeAds=findViewById(R.id.flNativeAds);
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
        if(VISHTECHNO_StartActivity.unifiedNativeAd1==null)
            loadNativeAds();
        else {
            flNativeAds.setVisibility(View.VISIBLE);
            populateNativeAdView(VISHTECHNO_StartActivity.unifiedNativeAd1,nativeAdView);
        }
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

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_exit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
//						mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            flNativeAds.setVisibility(View.VISIBLE);
                            populateNativeAdView(unifiedNativeAd,nativeAdView);
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("Exit", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                        }
                    }
                }).withNativeAdOptions(adOptions).build();

        // Load the Native ads.
        adLoader.loadAd( ConsentSDK.getAdRequest(this));
    }

}
