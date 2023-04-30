package vishtechno.bkm.quickscreenshotcapture;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;

import vishtechno.bkm.quickscreenshotcapture.view.BrushView;
import vishtechno.bkm.quickscreenshotcapture.view.TouchImageView;
import vishtechno.bkm.quickscreenshotcapture.view.camera.AlbumStorageDirFactory;
import vishtechno.bkm.quickscreenshotcapture.view.camera.FroyoAlbumDirFactory;
import vishtechno.bkm.quickscreenshotcapture.view.utils.BitmapUtils;
import vishtechno.bkm.quickscreenshotcapture.view.utils.Constant;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class BlurActivity extends AppCompatActivity implements OnClickListener, OnSeekBarChangeListener {
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    public static BrushView brushView;
    static int displayHight;
    public static int displayWidth;
    public static SeekBar offsetBar;
    public static ImageView prView;
    public static SeekBar radiusBar;
    static String tempDrawPath;
    public static File tempDrawPathFile;
    ImageView backBtn;
    TextView blurText;
    LinearLayout blurView;
    SeekBar blurrinessBar;
    ImageView colorBtn;
    boolean erase = true;
    ImageView fitBtn;
    ImageView grayBtn;
    Bitmap hand;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    ImageView offsetBtn;
    ImageView offsetDemo;
    LinearLayout offsetLayout;
    ProgressDialog progressBlurring;
    ImageView resetBtn;
    ImageView saveBtn;
    int startBlurSeekbarPosition;
    TouchImageView tiv;
    ImageView undoBtn;
    ImageView zoomBtn;
    TextView title;
    public static Bitmap tempBit;
    private FrameLayout adContainerView;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append(Constant.DIRECTORY_PATH_TEMP);
        tempDrawPath = sb.toString();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
        setContentView(R.layout.activity_blur);

        Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_blur_id));
        adContainerView.addView(adView);
        loadBanner();

        mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        hand = Bitmap.createScaledBitmap(hand, 120, 120, true);
        blurView = findViewById(R.id.blur_view);
        blurText = findViewById(R.id.blur_text);
        tiv = findViewById(R.id.drawingImageView);
        prView = findViewById(R.id.preview);
        offsetDemo = findViewById(R.id.offsetDemo);
        offsetLayout = findViewById(R.id.offsetLayout);
        title = findViewById(R.id.title);

        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inMutable = true;
        bitmapClear = tempBit;
        bitmapClear = blur(this, bitmapClear, tiv.opacity);
//        bitmapBlur = blur(this, bitmapClear, tiv.opacity);
        bitmapBlur = tempBit;
        resetBtn = findViewById(R.id.resetBtn);
        undoBtn = findViewById(R.id.undoBtn);
        fitBtn = findViewById(R.id.fitBtn);
        saveBtn = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);
        colorBtn = findViewById(R.id.colorBtn);
        grayBtn = findViewById(R.id.grayBtn);
        zoomBtn = findViewById(R.id.zoomBtn);
        offsetBtn = findViewById(R.id.offsetBtn);
        backBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(this);
        fitBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(this);
        grayBtn.setOnClickListener(this);
        zoomBtn.setOnClickListener(this);
        offsetBtn.setOnClickListener(this);
        offsetBar = findViewById(R.id.offsetBar);
        radiusBar = findViewById(R.id.widthSeekBar);
        blurrinessBar = findViewById(R.id.blurrinessSeekBar);
        brushView = findViewById(R.id.magnifyingView);
        brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
        radiusBar.setMax(300);
        radiusBar.setProgress((int) tiv.radius);
        blurrinessBar.setMax(24);
        blurrinessBar.setProgress(tiv.opacity);
        offsetBar.setMax(100);
        offsetBar.setProgress(0);
        radiusBar.setOnSeekBarChangeListener(this);
        blurrinessBar.setOnSeekBarChangeListener(this);
        offsetBar.setOnSeekBarChangeListener(this);
        clearTempBitmap();
        tiv.initDrawing();
        progressBlurring = new ProgressDialog(this);

        setLay();
    }

    LinearLayout bottomBar;
    RelativeLayout bottom_lay;

    private void setLay() {
        title.setTypeface(MyApplication.myRegular);

        bottomBar = findViewById(R.id.bottomBar);
        Help.setSize(bottomBar, 1080, 152, false);

        ImageView seekbar_width_icon = findViewById(R.id.seekbar_width_icon);
        Help.setSize(seekbar_width_icon, 62, 62, false);
        Help.setMargin(seekbar_width_icon, 30,0,0,0, false);

        ImageView blur_seekbar_icon = findViewById(R.id.blur_seekbar_icon );
        Help.setSize(blur_seekbar_icon, 80, 80, false);
        Help.setMargin(blur_seekbar_icon, 30,0,0,0, false);

        ImageView offset_seekbar_icon = findViewById(R.id.offset_seekbar_icon );
        Help.setSize(offset_seekbar_icon, 80, 80, false);
        Help.setMargin(offset_seekbar_icon, 30,0,0,0, false);

        bottom_lay = findViewById(R.id.bottom_lay);
        Help.setSize(bottom_lay, 1080, 180, true);

        ImageView bottom_lay_img = findViewById(R.id.bottom_lay_img);
        Help.setSize(bottom_lay_img, 1080, 180, true);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(backBtn, 118, 96, false);
        Help.setSize(saveBtn, 118, 96, false);

        Help.setSize(resetBtn, 100, 100, false);
        Help.setSize(undoBtn, 100, 100, false);
        Help.setMargin(undoBtn, 30,0,50,50, false);

        Help.setSize(colorBtn, 216, 180, true);
        Help.setSize(grayBtn, 216, 180, true);
        Help.setSize(zoomBtn, 216, 180, true);
        Help.setSize(offsetBtn, 216, 180, true);
        Help.setSize(fitBtn, 216, 180, true);

//        if(isNetworkAvailable()){
//            Help.setMargin(bottom_lay, 0, 0, 0, 200, false);
//        }else{
//            Help.setMargin(bottom_lay, 0, 0, 0, 0, false);
//        }
    }

    public void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        File file = tempDrawPathFile;
        if (file != null && file.isDirectory()) {
            for (String file2 : tempDrawPathFile.list()) {
                new File(tempDrawPathFile, file2).delete();
            }
        }
    }

    public static Bitmap blur(Context context, Bitmap bitmap, int i) {
        Bitmap bitmap2 = null;
        try {
            Bitmap copy = bitmap.copy(Config.ARGB_8888, true);
            bitmap2 = Bitmap.createBitmap(copy);
            if (VERSION.SDK_INT < 17) {
                return BitmapUtils.blurify(copy, i);
            }
            RenderScript create = RenderScript.create(context);
            ScriptIntrinsicBlur create2 = ScriptIntrinsicBlur.create(create, Element.U8_4(create));
            Allocation createFromBitmap = Allocation.createFromBitmap(create, copy);
            Allocation createFromBitmap2 = Allocation.createFromBitmap(create, bitmap2);
            create2.setRadius((float) i);
            create2.setInput(createFromBitmap);
            create2.forEach(createFromBitmap2);
            createFromBitmap2.copyTo(bitmap2);
            return bitmap2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onClick(View view) {
        Log.wtf("Click : ", "Inside onclick");
        switch (view.getId()) {
            case R.id.backBtn /*2131230802*/:
                onBackPressed();
                break;
            case R.id.colorBtn /*2131230839*/:
                erase = true;
                tiv.mode = 0;
                offsetLayout.setVisibility(View.GONE);
                UnpressedAll();
                colorBtn.setImageResource(R.drawable.bblur_press);
                tiv.splashBitmap = bitmapClear;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = true;
                AnimDown(bottom_lay,bottomBar);
                break;
            case R.id.fitBtn /*2131230864*/:
                offsetLayout.setVisibility(View.GONE);
                tiv.saveScale = 1.0f;
                tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                UnpressedAll();
                fitBtn.setImageResource(R.drawable.bfit_press);
                break;
            case R.id.grayBtn /*2131230875*/:
                erase = false;
                tiv.mode = 0;
                offsetLayout.setVisibility(View.GONE);
                UnpressedAll();
                grayBtn.setImageResource(R.drawable.bclear_press);
                tiv.splashBitmap = bitmapBlur;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = false;
                AnimDown(bottom_lay,bottomBar);
                break;
            case R.id.offsetBtn /*2131230939*/:
                offsetLayout.setVisibility(View.VISIBLE);
                UnpressedAll();
                offsetBtn.setImageResource(R.drawable.boffset_press);
                AnimDown(bottom_lay,bottomBar);
                break;
            case R.id.resetBtn /*2131230967*/:
                offsetLayout.setVisibility(View.GONE);
                resetImage();
                break;
            case R.id.saveBtn /*2131230973*/:
                EditImageActivity.mBitmap = tiv.drawingBitmap;
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.undoBtn /*2131231042*/:
                offsetLayout.setVisibility(View.GONE);
                StringBuilder sb = new StringBuilder();
                sb.append(tempDrawPath);
                sb.append("/canvasLog");
                sb.append(tiv.currentImageIndex - 1);
                String str = ".jpg";
                sb.append(str);
                String sb2 = sb.toString();
                Log.wtf("Current Image ", sb2);
                if (new File(sb2).exists()) {
                    tiv.drawingBitmap = null;
                    Options options = new Options();
                    options.inPreferredConfig = Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(sb2, options);
                    TouchImageView touchImageView4 = tiv;
                    touchImageView4.setImageBitmap(touchImageView4.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(tempDrawPath);
                    sb3.append("canvasLog");
                    sb3.append(tiv.currentImageIndex);
                    sb3.append(str);
                    File file = new File(sb3.toString());
                    if (file.exists()) {
                        file.delete();
                    }
                    tiv.currentImageIndex--;
                    return;
                }
                break;
            case R.id.zoomBtn /*2131231054*/:
                tiv.mode = 1;
                offsetLayout.setVisibility(View.GONE);
                UnpressedAll();
                zoomBtn.setImageResource(R.drawable.bzoom_press);
                break;
        }
    }

    void UnpressedAll(){
        grayBtn.setImageResource(R.drawable.bclear_unpress);
        zoomBtn.setImageResource(R.drawable.bzoom_unpress);
        colorBtn.setImageResource(R.drawable.bblur_unpress);
        offsetBtn.setImageResource(R.drawable.boffset_unpress);
        fitBtn.setImageResource(R.drawable.bfit_unpress);
    }

    public void resetImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Warning!");
        builder.setMessage((CharSequence) "Your current progress will be lost. Are you sure?");
        builder.setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
               clearTempBitmap();
               tiv.initDrawing();
               tiv.saveScale = 1.0f;
               tiv.fitScreen();
               tiv.updatePreviewPaint();
               tiv.updatePaintBrush();
            }
        });
        builder.setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if(bottom_lay.getVisibility()==View.GONE){
            AnimDown(bottomBar,bottom_lay);
        }else {
            PopUp();
        }
    }

    private void PopUp() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.delete_popup);

        RelativeLayout main = dialog.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 932, 732, false);

        mainLay.setBackgroundResource(R.drawable.alert_popup);

        final ImageView no, yes;

        no = dialog.findViewById(R.id.no);
        yes = dialog.findViewById(R.id.yes);

        Help.setSize(no, 340, 126, false);
        Help.setSize(yes, 340, 126, false);
        Help.setMargin(yes, 50, 0, 0, 0, false);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.blurrinessSeekBar) {
            brushView.isBrushSize = false;
            brushView.setShapeRadiusRatio(tiv.radius);
            brushView.brushSize.setPaintOpacity(blurrinessBar.getProgress());
            brushView.invalidate();
            tiv.opacity = i + 1;
            blurText.setText(String.valueOf(blurrinessBar.getProgress()));
            tiv.updatePaintBrush();
        } else if (id == R.id.offsetBar) {
            Bitmap copy = Bitmap.createBitmap(300, 300, Config.ARGB_8888).copy(Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(getResources().getColor(R.color.colorPrimary1));
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(hand, 95.0f, 150.0f, null);
            offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            BrushView brushView3 = brushView;
            brushView3.isBrushSize = true;
            brushView3.brushSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
            Log.wtf("radious :", String.valueOf(radiusBar.getProgress()));
            brushView.invalidate();
            tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
            tiv.updatePaintBrush();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.blurrinessSeekBar) {
            blurView.setVisibility(View.VISIBLE);
            startBlurSeekbarPosition = blurrinessBar.getProgress();
            blurText.setText(String.valueOf(startBlurSeekbarPosition));
        } else if (id == R.id.offsetBar) {
            offsetDemo.setVisibility(View.VISIBLE);
            Bitmap copy = Bitmap.createBitmap(300, 300, Config.ARGB_8888).copy(Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(hand, 95.0f, 150.0f, null);
            offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.widthSeekBar) {
            brushView.setVisibility(View.VISIBLE);
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        blurView.setVisibility(View.INVISIBLE);
        if (seekBar.getId() == R.id.blurrinessSeekBar) {
        } else if (seekBar.getId() == R.id.offsetBar) {
            offsetDemo.setVisibility(View.INVISIBLE);
        } else if (seekBar.getId() == R.id.widthSeekBar) {
            brushView.setVisibility(View.INVISIBLE);
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

    Techniques technique;
    public YoYo.YoYoString rope;

    void AnimDown(View v1,View v2){
        technique = Techniques.SlideOutDown;
        long time = 300;
        visible(v1);
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
                        gone(v1);
                        visible(v2);
                        AnimUp(v2);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(v1);
    }

    void AnimUp(View v1){
        technique = Techniques.SlideInUp;
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

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(v1);
    }
}
