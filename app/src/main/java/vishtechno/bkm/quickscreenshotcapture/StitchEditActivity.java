package vishtechno.bkm.quickscreenshotcapture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Adapter.ItemAdapter;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.myList;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;
import static vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help.creation_path;

public class StitchEditActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back, done;

    ImageView hv, arrange, add_image;
    ImageView close, save;
    ScrollView sc;
    HorizontalScrollView hsc;
    LinearLayout myLay1, myLay2, arrange_lay;
    int height, width;

    ArrayList<ImageData> myImageList = new ArrayList<>();
    ArrayList<ImageView> allCuts = new ArrayList<>();
    ArrayList<ImageView> allCloses = new ArrayList<>();
    ArrayList<ImageView> allSaves = new ArrayList<>();
    ArrayList<ImageView> allResets = new ArrayList<>();
    ArrayList<View> allCutLay = new ArrayList<>();

    ProgressDialog progress;

    DragListView list;
    ItemAdapter adapter;
    ArrayList<Pair<Long, ImageData>> tempList = new ArrayList<>();
    ArrayList<ImageData> newList = new ArrayList<>();

    int ARRANGE = 125;
    public static boolean stitchAdd = false;
    private FrameLayout adContainerView;
    LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stitch_edit);

        loadInterstitial();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_stitchedit_id));
        adContainerView.addView(adView);
        loadBanner();

        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        width = getResources().getDisplayMetrics().widthPixels;
        height = getResources().getDisplayMetrics().heightPixels;
        mContext = this;
        Help.FS(this);

        if (Help.hasNavBar(mContext)) {
            height = height + Help.getNavHeight(mContext);
        }

        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        title = findViewById(R.id.title);
        lin = findViewById(R.id.lin);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progress.show();
//                for (int i = 0; i < myImageList.size(); i++) {
//                    gone(allCuts.get(i));
//                    gone(allCloses.get(i));
//                    gone(allSaves.get(i));
//                    gone(allResets.get(i));
//                    gone(allCutLay.get(i));
//                }

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        new SaveImage().execute();
//                    }
//                }, 2000);

                if (interstitialAd.isLoaded()) {
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            progress.show();
                            for (int i = 0; i < myList.size(); i++) {
                                gone(allCuts.get(i));
                                gone(allCloses.get(i));
                                gone(allSaves.get(i));
                                gone(allResets.get(i));
                                gone(allCutLay.get(i));
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new SaveImage().execute();
                                }
                            }, 2000);
                        }
                    });
                    interstitialAd.show();
                } else {
                    progress.show();
                    for (int i = 0; i < myImageList.size(); i++) {
                        gone(allCuts.get(i));
                        gone(allCloses.get(i));
                        gone(allSaves.get(i));
                        gone(allResets.get(i));
                        gone(allCutLay.get(i));
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new SaveImage().execute();
                        }
                    }, 2000);
                }

            }
        });

        myImageList = new ArrayList<>();
        myImageList.addAll(myList);
        stitchAdd = false;
        init();
        setClick();
        setLay();
    }

    private void init() {
        sc = findViewById(R.id.sc);
        hsc = findViewById(R.id.hsc);
        myLay1 = findViewById(R.id.myLay1);
        myLay2 = findViewById(R.id.myLay2);

        hv = findViewById(R.id.hv);
        arrange = findViewById(R.id.arrange);
        add_image = findViewById(R.id.add_image);

        arrange_lay = findViewById(R.id.arrange_lay);
        list = findViewById(R.id.list);
        close = findViewById(R.id.close);
        save = findViewById(R.id.save);

        progress = Help.setPD(mContext, "Saving...", false);

        setSC();
    }

    private void setClick() {
        hv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(arrange_lay);
                if (sc.getVisibility() == View.GONE) {
                    setSC();
                    hv.setImageResource(R.drawable.horizontal_click);
                } else {
                    setHSC();
                    hv.setImageResource(R.drawable.vertical_click);
                }
            }
        });

        arrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrangeImageActivity.arrangeImages = new ArrayList<>();
                ArrangeImageActivity.arrangeImages.addAll(myImageList);
                Help.startResult(mContext, ArrangeImageActivity.class, ARRANGE);
//                if (arrange_lay.getVisibility() == View.GONE) {
//                    tempList.clear();
//                    for (long i = 0; i < myImageList.size(); i++) {
//                        tempList.add(new Pair<>(i, myImageList.get((int) i)));
//                    }
//
//                    RecyclerView.LayoutManager lm1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//                    list.setLayoutManager(lm1);
//                    adapter = new ItemAdapter(mContext, tempList, R.layout.images_adapter, R.id.mainLay, true);
//                    list.setAdapter(adapter, false);
//                    list.setCanDragHorizontally(true);
//                    visible(arrange_lay);
//                } else {
//                    gone(arrange_lay);
//                }
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stitchAdd = true;
                myList.clear();
                Help.nextwithnew(mContext, ImageFolderActivity.class);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(arrange_lay);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
    }

    private void setData() {
//        newList.clear();
//
//        for (int i = 0; i < tempList.size(); i++) {
//            newList.add(tempList.get(i).second);
//        }

        myImageList = newList;

        if (sc.getVisibility() == View.GONE) {
            setHSC();
        } else {
            setSC();
        }

        gone(arrange_lay);
    }

    void setSC() {
        allCuts.clear();
        allCloses.clear();
        allSaves.clear();
        allResets.clear();
        allCutLay.clear();
        myLay1.removeAllViews();
        myLay2.removeAllViews();
        gone(hsc);
        visible(sc);
        for (int i = 0; i < myImageList.size(); i++) {
            addImagesSC(myImageList.get(i));
        }
    }

    void setHSC() {
        allCuts.clear();
        allCloses.clear();
        allSaves.clear();
        allResets.clear();
        allCutLay.clear();
        myLay1.removeAllViews();
        myLay2.removeAllViews();
        gone(sc);
        visible(hsc);
        for (int i = 0; i < myImageList.size(); i++) {
            addImagesHSC(myImageList.get(i));
        }
    }

    void setLay() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout top_bar = findViewById(R.id.top_bar);
        Help.setSize(top_bar, 1080, 182, false);
        top_bar.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);
        Help.setSize(hv, 392, 156, true);
        Help.setSize(arrange, 392, 156, true);
        Help.setSize(add_image, 213, 212, true);

//        Help.setSize(arrange_lay, 1080, 180, false);
//        Help.setSize(close, 188, 180, false);
//        Help.setSize(save, 188, 180, false);
        Help.setSize(lin, 1080, 226, true);

        if (isNetworkAvailable()) {
            Help.setMargin(lin, 0, 0, 0, 200, true);
        } else {
            Help.setMargin(lin, 0, 0, 0, 0, true);
        }
    }

    void addImagesSC(final ImageData imageData) {
        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.mystritchview, null, false);

        final ImageView img = myview.findViewById(R.id.img);
        final ImageView cut = myview.findViewById(R.id.cut);
        final ImageView close = myview.findViewById(R.id.close);
        final ImageView save = myview.findViewById(R.id.save);
        final ImageView reset = myview.findViewById(R.id.reset);

        final RelativeLayout cut_lay = myview.findViewById(R.id.cut_lay);

        final LinearLayout lay_top = myview.findViewById(R.id.lay_top);
        final ImageView top_img = myview.findViewById(R.id.top_img);
        final ImageView tra_top = myview.findViewById(R.id.tra_top);

        final LinearLayout lay_bot = myview.findViewById(R.id.lay_bot);
        final ImageView bot_img = myview.findViewById(R.id.bot_img);
        final ImageView tra_bot = myview.findViewById(R.id.tra_bot);

        Help.setSize(cut, 104, 104, false);
        Help.setSize(close, 104, 104, false);
        Help.setSize(save, 104, 104, false);
        Help.setSize(reset, 104, 104, false);

        int m = 15;
        Help.setMargin(cut, m, m, m, m, false);
        Help.setMargin(close, m, m, m, m, false);
        Help.setMargin(save, m, m, m, m, false);
        Help.setMargin(reset, m, m, m, m, false);

        Help.setSize(top_img, 1080, 62, false);
        Help.setSize(bot_img, 1080, 62, false);

        allCuts.add(cut);
        allCloses.add(close);
        allSaves.add(save);
        allResets.add(reset);
        allCutLay.add(cut_lay);

        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible(close);
                visible(save);
                visible(cut_lay);
                gone(cut);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(close);
                gone(save);
                gone(cut_lay);
                visible(cut);

                float height = cut_lay.getHeight();
                lay_bot.setY(height - Help.w(62));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, 0);
                tra_bot.setLayoutParams(params);

                lay_top.setY(0);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(width, 0);
                tra_top.setLayoutParams(params1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(close);
                gone(save);
                gone(cut_lay);
                visible(cut);
                visible(reset);

                cropBitmapSC(img, myview, cut_lay, lay_top, lay_bot, tra_top, tra_bot);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        float height = cut_lay.getHeight();
                        lay_bot.setY(height - Help.w(62));
                    }
                }, 1000);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(reset);
                LayResizeSC(myview, img, width, height, imageData.getWidth(), imageData.getHeight());
                Glide.with(mContext).load(imageData.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(img);

                float height = cut_lay.getHeight();
                lay_bot.setY(height - Help.w(62));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, 0);
                tra_bot.setLayoutParams(params);

                lay_top.setY(0);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(width, 0);
                tra_top.setLayoutParams(params1);

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-1, -1);
                cut_lay.setLayoutParams(params2);
            }
        });

        top_img.setOnTouchListener(new View.OnTouchListener() {
            float dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sc.requestDisallowInterceptTouchEvent(true);
                        dY = lay_top.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float height = cut_lay.getHeight();
                        int width = cut_lay.getWidth();
                        float f = event.getRawY() + dY;
                        float val = ((height / 2) - Help.w(62));
                        if (f >= 0 && f < val) {
                            lay_top.setY(f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) f);
                            tra_top.setLayoutParams(params);
                        }
                        showLog("Y - " + f);
                        showLog("V - " + val);
                        break;
                    case MotionEvent.ACTION_UP:
                        sc.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        });

        bot_img.setOnTouchListener(new View.OnTouchListener() {
            float dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sc.requestDisallowInterceptTouchEvent(true);
                        dY = lay_bot.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float height = cut_lay.getHeight();
                        int width = cut_lay.getWidth();
                        float f = event.getRawY() + dY;
                        float val = (height / 2);
                        if (f >= val && f < (height - Help.w(62))) {
                            lay_bot.setY(f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (height - f - Help.w(62)));
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            tra_bot.setLayoutParams(params);
                        }
                        showLog("Y - " + f);
                        showLog("V - " + val);
                        break;
                    case MotionEvent.ACTION_UP:
                        sc.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        });

        myLay1.addView(myview);

        Glide.with(mContext).load(imageData.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(img);

        LayResizeSC(myview, img, width, height, imageData.getWidth(), imageData.getHeight());
    }

    void addImagesHSC(final ImageData imageData) {
        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.mystritchviewhsc, null, false);

        final ImageView img = myview.findViewById(R.id.img);
        final ImageView cut = myview.findViewById(R.id.cut);
        final ImageView close = myview.findViewById(R.id.close);
        final ImageView save = myview.findViewById(R.id.save);
        final ImageView reset = myview.findViewById(R.id.reset);

        final RelativeLayout cut_lay = myview.findViewById(R.id.cut_lay);

        final LinearLayout lay_top = myview.findViewById(R.id.lay_top);
        final ImageView top_img = myview.findViewById(R.id.top_img);
        final ImageView tra_top = myview.findViewById(R.id.tra_top);

        final LinearLayout lay_bot = myview.findViewById(R.id.lay_bot);
        final ImageView bot_img = myview.findViewById(R.id.bot_img);
        final ImageView tra_bot = myview.findViewById(R.id.tra_bot);

        Help.setSize(cut, 104, 104, false);
        Help.setSize(close, 104, 104, false);
        Help.setSize(save, 104, 104, false);
        Help.setSize(reset, 104, 104, false);

        int m = 15;
        Help.setMargin(cut, m, m, m, m, false);
        Help.setMargin(close, m, m, m, m, false);
        Help.setMargin(save, m, m, m, m, false);
        Help.setMargin(reset, m, m, m, m, false);

        Help.setSize(top_img, 62, 186, false);
        Help.setSize(bot_img, 62, 186, false);

        allCuts.add(cut);
        allCloses.add(close);
        allSaves.add(save);
        allResets.add(reset);
        allCutLay.add(cut_lay);

        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible(close);
                visible(save);
                visible(cut_lay);
                gone(cut);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(close);
                gone(save);
                gone(cut_lay);
                visible(cut);

                int height = hsc.getHeight();
                float width = cut_lay.getWidth();

                lay_bot.setX(width - Help.w(62));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, height);
                tra_bot.setLayoutParams(params);

                lay_top.setX(0);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(0, height);
                tra_top.setLayoutParams(params1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(close);
                gone(save);
                gone(cut_lay);
                visible(cut);
                visible(reset);

                cropBitmapHSC(img, myview, cut_lay, lay_top, lay_bot, tra_top, tra_bot);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        float width = cut_lay.getWidth();
                        lay_bot.setX(width - Help.w(62));
                    }
                }, 1000);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gone(reset);
                int height1 = hsc.getHeight();
                int width1 = hsc.getWidth();
                int h = imageData.getHeight();
                int w = imageData.getWidth();

                while (h < height1) {
                    h = h * 2;
                    w = w * 2;
                }

                LayResizeHSC(myview, img, width1, height1, w, h);
                Glide.with(mContext).load(imageData.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(img);

                int width = cut_lay.getWidth();
                lay_bot.setX(width - Help.w(62));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, height1);
                tra_bot.setLayoutParams(params);

                lay_top.setX(0);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(0, height1);
                tra_top.setLayoutParams(params1);

                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-1, -1);
                cut_lay.setLayoutParams(params2);
            }
        });

        top_img.setOnTouchListener(new View.OnTouchListener() {
            float dX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hsc.requestDisallowInterceptTouchEvent(true);
                        dX = lay_top.getX() - event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int height = cut_lay.getHeight();
                        int width = cut_lay.getWidth();
                        float f = event.getRawX() + dX;
                        float val = ((width / 2) - Help.w(62));
                        if (f >= 0 && f < val) {
                            lay_top.setX(f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) f, height);
                            tra_top.setLayoutParams(params);
                        }
                        showLog("X - " + f);
                        showLog("V - " + val);
                        break;
                    case MotionEvent.ACTION_UP:
                        hsc.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        });

        bot_img.setOnTouchListener(new View.OnTouchListener() {
            float dX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hsc.requestDisallowInterceptTouchEvent(true);
                        dX = lay_bot.getX() - event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int height = cut_lay.getHeight();
                        int width = cut_lay.getWidth();
                        float f = event.getRawX() + dX;
                        float val = (width / 2);
                        if (f >= val && f < (width - Help.w(62))) {
                            lay_bot.setX(f);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width - f - Help.w(62)), height);
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            tra_bot.setLayoutParams(params);
                        }
                        showLog("X - " + f);
                        showLog("V - " + val);
                        break;
                    case MotionEvent.ACTION_UP:
                        hsc.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        });

        myLay2.addView(myview);

        hsc.post(new Runnable() {
            @Override
            public void run() {
                int height = hsc.getHeight();
                int width = hsc.getWidth();
                int h = imageData.getHeight();
                int w = imageData.getWidth();

                while (h < height) {
                    h = h * 2;
                    w = w * 2;
                }

                LayResizeHSC(myview, img, width, height, w, h);
                Glide.with(mContext).load(imageData.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).into(img);
            }
        });
    }

    void LayResizeSC(View view, View img, int scW, int scH, int width, int height) {

        if (height > scH) {
            scH = height;
        }

        int layoutwidth = scW;
        int layoutheight = scH;
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newwidth, newheight);
        view.setLayoutParams(params);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(newwidth, newheight);
        img.setLayoutParams(params1);
    }

    void LayResizeHSC(View view, View img, int scW, int scH, int width, int height) {

        if (width > scW) {
            scW = width;
        }

        int layoutwidth = scW;
        int layoutheight = scH;
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newwidth, newheight);
        view.setLayoutParams(params);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(newwidth, newheight);
        img.setLayoutParams(params1);
    }

    class SaveImage extends AsyncTask<String, String, String> {

        int b;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            b = sc.getVisibility();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Bitmap bit;
                if (b == 0) {
                    bit = Help.getBitmapFromView(myLay1);
                } else {
                    bit = Help.getBitmapFromView(myLay2);
                }
                if (bit == null)
                    return "error";
                VISHTECHNO_help.saveBitmap(mContext, bit);
                return "null";
            } catch (OutOfMemoryError e) {
                showLog(e.toString());
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                progress.dismiss();
            } catch (Exception e) {
                showLog(e.toString());
            }
            if (s.equals("error")) {
                Help.Toast(mContext, "Save Error");
                return;
            }
            MediaScannerConnection.scanFile(mContext, new String[]{creation_path}, new String[]{"image/*"}, null);
//            Help.Toast(mContext, "Image Saved");
            Help.nextwithnew(mContext, VISHTECHNO_Share_DeleteActivity.class);
            finish();
        }
    }

    void cropBitmapSC(ImageView img, View mainLay, View cut_lay, LinearLayout top_lay, LinearLayout bot_lay, ImageView tra_top, ImageView tra_bot) {
        Bitmap myBit = Help.getBitmapFromView(img);

        int y = ((int) top_lay.getY());

        Bitmap croppedBitmap = Bitmap.createBitmap(myBit, 0, y, myBit.getWidth(), ((((int) bot_lay.getY()) + Help.w(62)) - y));
        img.setImageBitmap(croppedBitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, 0);
        tra_bot.setLayoutParams(params);

        top_lay.setY(0);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(width, 0);
        tra_top.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(width, croppedBitmap.getHeight());
        cut_lay.setLayoutParams(params2);

        LayResizeSC(mainLay, img, width, height, croppedBitmap.getWidth(), croppedBitmap.getHeight());
    }

    void cropBitmapHSC(ImageView img, View mainLay, View cut_lay, LinearLayout top_lay, LinearLayout bot_lay, ImageView tra_top, ImageView tra_bot) {
        Bitmap myBit = Help.getBitmapFromView(img);

        int x = ((int) top_lay.getX());

        Bitmap croppedBitmap = Bitmap.createBitmap(myBit, x, 0, ((((int) bot_lay.getX()) + Help.w(62)) - x), myBit.getHeight());
        img.setImageBitmap(croppedBitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, cut_lay.getHeight());
        tra_bot.setLayoutParams(params);

        top_lay.setX(0);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(0, cut_lay.getHeight());
        tra_top.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(croppedBitmap.getWidth(), height);
        cut_lay.setLayoutParams(params2);

        int height = hsc.getHeight();
        int width = hsc.getWidth();
        LayResizeHSC(mainLay, img, width, height, croppedBitmap.getWidth(), croppedBitmap.getHeight());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stitchAdd) {
            if (sc.getVisibility() == View.VISIBLE) {
                for (int i = 0; i < myList.size(); i++) {
                    addImagesSC(myList.get(i));
                }
            } else {
                for (int i = 0; i < myList.size(); i++) {
                    addImagesHSC(myList.get(i));
                }
            }
            myImageList.addAll(myList);
            stitchAdd = false;
        }
    }

    InterstitialAd interstitialAd;

    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_stitchedit_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ARRANGE) {
            newList = new ArrayList<>();
            newList.addAll(ArrangeImageActivity.arrangeImages);
            setData();
        }
    }
}