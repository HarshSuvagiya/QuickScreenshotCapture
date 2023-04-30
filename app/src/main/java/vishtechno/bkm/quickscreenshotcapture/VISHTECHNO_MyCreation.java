package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import androidx.viewpager.widget.ViewPager;
import vishtechno.bkm.quickscreenshotcapture.Adapter.TabsPagerAdapter;
import vishtechno.bkm.quickscreenshotcapture.R;

import vishtechno.bkm.quickscreenshotcapture.Adapter.VISHTECHNO_Creation_Adapter;
import vishtechno.bkm.quickscreenshotcapture.Model.VISHTECHNO_SavedPhoto_Model;
import vishtechno.bkm.quickscreenshotcapture.fragment.ScreenRecording_Fragment;
import vishtechno.bkm.quickscreenshotcapture.fragment.ScreenShots_Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class VISHTECHNO_MyCreation extends AppCompatActivity {

    public static Activity mContext;
    ImageView back;

    private FrameLayout adContainerView;
    TextView title;

    ViewPager viewPager;
    ImageView tab1, tab2;
    TabsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.vishtechno_activity_my_creation);

        mContext = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_mycreation_id));
        adContainerView.addView(adView);
        loadBanner();

        init();
        setLay();

    }

    private void setLay() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);

        Help.setSize(tab1, 458, 128, false);
        Help.setSize(tab2, 458, 128, false);
        Help.setMargin(tab2, -2,0 , 0, 40, false);

        LinearLayout child_lay=findViewById(R.id.child_lay);
        Help.setMargin(child_lay, 0, 20, 0, 0, false);
    }

    private void init() {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewPager = findViewById(R.id.viewPager);

        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);

        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        pagerAdapter.addFragment(new ScreenShots_Fragment());
        pagerAdapter.addFragment(new ScreenRecording_Fragment());
        pagerAdapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                inviAll();
                if (i == 0) {
                    tab1.setImageResource(R.drawable.tab1_press);
                }
                if (i == 1) {
                    tab2.setImageResource(R.drawable.tab2_press);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        onclick(tab1, 0);
        onclick(tab2, 1);
    }

    void onclick(View view, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(pos);
            }
        });
    }

    void inviAll() {
        tab1.setImageResource(R.drawable.tab1_unpress);
        tab2.setImageResource(R.drawable.tab2_unpress);
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
