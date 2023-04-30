package vishtechno.bkm.quickscreenshotcapture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_Constants;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_PreferenceManager;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_TakeScreenshot;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help;
import vishtechno.bkm.quickscreenshotcapture.interfaces.VISHTECHNO_ShowDialogMethodsInterface;

import vishtechno.bkm.quickscreenshotcapture.services.VISHTECHNO_FloatWidgetService;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static vishtechno.bkm.quickscreenshotcapture.Help.Toast;
import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.mSS;
import static vishtechno.bkm.quickscreenshotcapture.Help.myList;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;
import static vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help.creation_path;

public class VISHTECHNO_ShowDialogActivity extends AppCompatActivity implements VISHTECHNO_ShowDialogMethodsInterface {

    public static Activity mActivity;
    ImageView img;

    String ssPath;
    Bitmap ssBitmap;
    ImageView crop_img, add;
    Context mContext;
    ImageView back, done;
    ImageView iv_dialog_screenshot_share, iv_dialog_screenshot_delete, iv_dialog_screenshot_close;
    RelativeLayout bottom_bar;
    TextView title;
    int EDIT = 141;
    private FrameLayout adContainerView;
    LinearLayout lin_screenshot_popup_ss;
    //    ProgressDialog progress;
    Help.AppPreferences preferences;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(Integer.MIN_VALUE);
        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        setContentView(R.layout.vishtechno_dialog_screenshot_taken);

        mActivity = this;

        preferences = new Help.AppPreferences(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_showdialog_id));
        adContainerView.addView(adView);
        loadBanner();

        gone(VISHTECHNO_FloatWidgetService.showPopImageView);
        if (VISHTECHNO_FloatWidgetService.rope != null) {
            VISHTECHNO_FloatWidgetService.rope.stop(true);
        }

        Help.FS(this);
        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        mContext = this;
        init();
        setLay();

//        progress=Help.setPD(mContext,"Saving...",false);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        VISHTECHNO_FloatWidgetService.mF = 0;
        img = findViewById(R.id.img);

        String action = preferences.getOpenType();
        if (action.equals("Open Edit")) {
            EditImageActivity.mImageUrl = ssPath;
            Help.nextwithnew(mContext, EditImageActivity.class);
        } else if (action.equals("Open Preview")) {
            creation_path = ssPath;
            Help.nextwithnew(mContext, VISHTECHNO_Share_DeleteActivity.class);
        }

        new CopyImage().execute();

        Glide.with(mContext).load(ssPath).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(img);

        findViewById(R.id.iv_dialog_screenshot_close).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
//                        ((BitmapDrawable) VISHTECHNO_ShowDialogActivity.this.iv_dialog_screenshot).getBitmap().recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                VISHTECHNO_FloatWidgetService.mF = 0;
                VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
                finish();
            }
        });

        findViewById(R.id.iv_dialog_screenshot_delete).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ssPath == null) {
                    Help.Toast(mContext, "Error Deleting Image");
                    MediaScannerConnection.scanFile(mContext, new String[]{ssPath}, new String[]{"image/*"}, null);
                    VISHTECHNO_FloatWidgetService.mF = 0;
                    VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
                    finish();
                }
                if (creation_path == null) {
                    Help.Toast(mContext, "Error Deleting Image");
                    MediaScannerConnection.scanFile(mContext, new String[]{creation_path}, new String[]{"image/*"}, null);
                    VISHTECHNO_FloatWidgetService.mF = 0;
                    VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
                    finish();
                    return;
                }
                File file1 = new File(creation_path);
                file1.delete();
                MediaScannerConnection.scanFile(mContext, new String[]{creation_path}, new String[]{"image/*"}, null);
                File file = new File(ssPath);
                if (file.delete()) {
                    MediaScannerConnection.scanFile(mContext, new String[]{ssPath}, new String[]{"image/*"}, null);
                    VISHTECHNO_PreferenceManager.isScreenshotDirChanged = false;
                    VISHTECHNO_PreferenceManager.toastIt(mContext, "Screenshot deleted");
                    if (((Boolean) VISHTECHNO_PreferenceManager.getSharedPref(mContext, VISHTECHNO_Constants.SS_DELETE_HF)).booleanValue()) {
                        VISHTECHNO_PreferenceManager.performVibrate(mContext);
                    }
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (VISHTECHNO_PreferenceManager.isScreenshotListClass) {
//                                    VISHTECHNO_MainActivity.changeFragment(false, new ScreenshotsList());
                            }
                        }
                    }, 1000);
                    VISHTECHNO_PreferenceManager.refreshGallery(file.getAbsolutePath(), mContext);
                }
                MediaScannerConnection.scanFile(mContext, new String[]{ssPath}, new String[]{"image/*"}, null);
                VISHTECHNO_FloatWidgetService.mF = 0;
                VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
                finish();
            }
        });

        findViewById(R.id.iv_dialog_screenshot_share).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    File f = new File(ssPath);
                    Uri uri = Uri.parse("file://" + f.getAbsolutePath());
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share Image"));
//                        Intent intent = new Intent("android.intent.action.SEND");
//                        intent.setType("image/*");
//                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile
//                                (mContext, "vishtechno.bkm.quickscreenshotcapture.provider", new File(ssPath)));
//                        startActivity(Intent.createChooser(intent, "Share screenshot using.."));


                } catch (Exception e) {
                    VISHTECHNO_PreferenceManager.printIt("ERROR IN share", e);
                }
            }
        });

        VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = true;

        crop_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                EditImageActivity.mImageUrl = ssPath;
                Intent intent = new Intent(mContext, EditImageActivity.class);
                startActivityForResult(intent, EDIT);

//                Bitmap bitmap = iv_dialog_screenshot.getCroppedImage();
//                VISHTECHNO_help.saveBitmap(mContext, bitmap);
//                VISHTECHNO_help.from = 0;
//
//                Intent intent = new Intent(mContext, VISHTECHNO_Share_DeleteActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//
//                Toast.makeText(mContext, "Screenshot Saved Successfully", Toast.LENGTH_SHORT).show();

            }
        });

        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    onBackPressed();
                    Help.mAddSS = true;
                    VISHTECHNO_FloatWidgetService.addLay.removeAllViews();
                    myList.clear();
                    VISHTECHNO_FloatWidgetService.AddLayINSC(ssPath);
                }catch (Exception e){
                    showLog(e.toString());
                    Help.Toast(mContext,"Something went wrong");
                }
            }
        });

        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Help.nextwithnew(mContext, VISHTECHNO_Share_DeleteActivity.class);
                setResult(RESULT_OK);
                finish();
            }
        });
        Help.mAddSS = false;
    }

    private void setLay() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(add, 213, 212, false);

        Help.setSize(bottom_bar, 1080, 226, true);
        Help.setMargin(bottom_bar, 0, 20, 0, 0, true);
        Help.setSize(crop_img, 196, 156, true);
        Help.setSize(iv_dialog_screenshot_share, 196, 156, true);
        Help.setSize(iv_dialog_screenshot_delete, 196, 156, true);
        Help.setSize(iv_dialog_screenshot_close, 196, 156, true);

        if (isNetworkAvailable()) {
            Help.setMargin(lin_screenshot_popup_ss, 0, 0, 0, 200, true);
        } else {
            Help.setMargin(lin_screenshot_popup_ss, 0, 0, 0, 0, true);
        }
    }

    private void init() {

        add = findViewById(R.id.add);
        done = findViewById(R.id.done);
        crop_img = findViewById(R.id.crop_img);
        lin_screenshot_popup_ss = findViewById(R.id.lin_screenshot_popup_ss);

        VISHTECHNO_PreferenceManager.showDialogMethodsInterface = this;
        ssPath = getIntent().getStringExtra("ssname");
//        ssPath = mSS;
        iv_dialog_screenshot_share = findViewById(R.id.iv_dialog_screenshot_share);
        iv_dialog_screenshot_delete = findViewById(R.id.iv_dialog_screenshot_delete);
        iv_dialog_screenshot_close = findViewById(R.id.iv_dialog_screenshot_close);
        back = findViewById(R.id.back);
        bottom_bar = findViewById(R.id.bottom_bar);
        title = findViewById(R.id.title);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void closeActivity() {
        if (!isFinishing()) {
            VISHTECHNO_FloatWidgetService.mF = 0;
            VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT && resultCode == RESULT_OK) {
            VISHTECHNO_FloatWidgetService.mF = 0;
            VISHTECHNO_PreferenceManager.isScreenshotPopUpOpen = false;
            finish();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class CopyImage extends AsyncTask<String, String, String> {

        File savefile;

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
            String action = preferences.getOpenType();
            if (!action.equals("Open Option")) {
                finish();
            }
            if (s.equals("error")) {
                Help.Toast(mContext, "Out of Memory Error");
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                File myDir = new File(Environment.getExternalStorageDirectory()
                        .toString()
                        + "/"
                        + mContext.getResources().getString(R.string.app_name));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (progress != null && progress.isShowing()) {
//            progress.dismiss();
//        }
    }

    @Override
    public void onBackPressed() {
        if((preferences.getisRatePopup() % 2) == 0){

            Toast(mContext,"Rate");
            showRateDialog();
        }else {
           super.onBackPressed();
        }
        preferences.setisRatePopup(preferences.getisRatePopup()+1);


    }
    private void showRateDialog(){

        Dialog dialog = new Dialog(mContext, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.dialog_rateus);

        LinearLayout lmain = dialog.findViewById(R.id.lratemain);
        ImageView star = dialog.findViewById(R.id.star);

        ImageView ivlater = dialog.findViewById(R.id.ivlater);
        ImageView ivrate = dialog.findViewById(R.id.ivrate);

        Help.setSize(lmain, 932, 664, true);
        Help.setSize(star, 474, 134, true);
        Help.setSize(ivlater, 340, 126, true);
        Help.setSize(ivrate, 340, 126, true);

        Help.setMargin(lmain,0,30,0,0,true);

        ivlater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

               finish();


            }
        });

        ivrate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

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

        dialog.show();

    }
}
