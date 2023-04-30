package vishtechno.bkm.quickscreenshotcapture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Adapter.Sticker_Adapter;
import vishtechno.bkm.quickscreenshotcapture.Model.RotationColor;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help;
import vishtechno.bkm.quickscreenshotcapture.view.AutoFitEditText;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.ColorPicker;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.OpacityBar;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.SVBar;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;


public class EditImageActivity extends FragmentActivity implements ColorPickerDialogListener {

    public static String mImageUrl;
    public static Bitmap mBitmap;

    Activity mContext;
    ImageView back, done;
    TextView title;

    ImageView img;
    RelativeLayout main, mainLay, sticker_lay;
    ImageView crop, draw, btn_blur, sticker, text, image;
    int CROP = 1111, SEL_ART = 444, SEL_IMAGE = 555, EDIT_TEXT = 666,
            Gallery_Image = 121;
    public static boolean blur, isFirstText;
    ImageView currentImageView;
    public static AutoFitEditText currentEditText;

    ArrayList<RotationColor> views = new ArrayList<>();
    String thing;

    ProgressDialog progress;
    RecyclerView sticker_list;
    ArrayList<String> art_arr;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        loadInterstitial();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_editimage_id));
        adContainerView.addView(adView);
        loadBanner();

        Help.width = getResources().getDisplayMetrics().widthPixels;
        Help.height = getResources().getDisplayMetrics().heightPixels;
        mContext = this;
        Help.FS(mContext);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mainLay.performClick();
//                new ImageSave().execute();

                if(interstitialAd.isLoaded()){
                    interstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            mainLay.performClick();
                            new ImageSave().execute();
                        }
                    });
                    interstitialAd.show();
                }else {
                    mainLay.performClick();
                    new ImageSave().execute();
                }

            }
        });

        init();
        setClick();
        setLay();
    }

    void init() {
        img = findViewById(R.id.img);
        main = findViewById(R.id.main);
        mainLay = findViewById(R.id.mainLay);
        sticker_lay = findViewById(R.id.sticker_lay);

        crop = findViewById(R.id.crop);
        draw = findViewById(R.id.draw);
        btn_blur = findViewById(R.id.btn_blur);
        sticker = findViewById(R.id.sticker);
        text = findViewById(R.id.text);
        image = findViewById(R.id.image);

        sticker_list = findViewById(R.id.sticker_list);
        art_arr = Help.setList(mContext, "art");
        Sticker_Adapter sticker_adapter = new Sticker_Adapter(mContext, art_arr, "art");
        LinearLayoutManager lm = new LinearLayoutManager(mContext,RecyclerView.HORIZONTAL,false);
        sticker_list.setLayoutManager(lm);
        sticker_list.setAdapter(sticker_adapter);

//        mBitmap = BitmapFactory.decodeFile(EditImageActivity.mImageUrl);
//        Glide.with(mContext).load(mImageUrl).into(img);

        progress = Help.setPD(mContext, "Loading", false);
        progress.show();

        main.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Glide.with(mContext)
                            .asBitmap()
                            .load(mImageUrl)
                            .into(new SimpleTarget<Bitmap>(mainLay.getWidth(), mainLay.getHeight()) {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    mBitmap = resource;
                                    img.setImageBitmap(mBitmap);
                                    Resize(EditImageActivity.mBitmap, main);
                                    progress.dismiss();
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    super.onLoadFailed(errorDrawable);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mImageUrl}, new String[]{"image/*"}, null);
                                            Help.Toast(mContext, "Fail to Load");
                                            onBackPressed();
                                        }
                                    });
                                }
                            });
                }catch (Exception e){
                    Help.Toast(mContext,"Something went Wrong Try Again");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }
            }
        });
        isFirstText = true;
        views.clear();
    }

    void setClick() {
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                crop.setImageResource(R.drawable.ed_crop_press);
                gone(sticker_list);
                Intent intent = new Intent(mContext, Image_Crop.class);
                startActivityForResult(intent, CROP);
            }
        });

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                draw.setImageResource(R.drawable.ed_draw_press);
                Intent intent = new Intent(mContext, DrawLayActivity.class);
                startActivityForResult(intent, CROP);
            }
        });

        btn_blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                btn_blur.setImageResource(R.drawable.ed_blur_press);
                BlurActivity.tempBit = mBitmap;
                Intent intent = new Intent(mContext, BlurActivity.class);
                startActivityForResult(intent, CROP);
            }
        });

        sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                sticker.setImageResource(R.drawable.ed_sticker_press);
                mainLay.performClick();
                Help.mTextView = null;
                thing = "art";
                Intent intent = new Intent(mContext, StickerActivity.class);
                startActivityForResult(intent, SEL_ART);
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                text.setImageResource(R.drawable.ed_text_press);
                mainLay.performClick();
                Help.mBitmap = null;
                Help.mTextView = null;
                Intent intent = new Intent(mContext, CreateTextActivity2.class);
                intent.putExtra("text", "");
                startActivityForResult(intent, SEL_IMAGE);
                thing = "text";
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnpressedAll();
                image.setImageResource(R.drawable.ed_image_press);
                mainLay.performClick();
                Help.mTextView = null;
                thing = "image";
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Image);
            }
        });

        mainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < sticker_lay.getChildCount(); i++) {
                    RelativeLayout stickerview = (RelativeLayout) sticker_lay.getChildAt(i);
                    RelativeLayout mainLay = (RelativeLayout) stickerview.getChildAt(0);
                    ImageView border = (ImageView) mainLay.getChildAt(2);
                    border.setBackgroundColor(Color.TRANSPARENT);
                    ImageView close = (ImageView) mainLay.getChildAt(3);
                    gone(close);
                    ImageView flip = (ImageView) mainLay.getChildAt(4);
                    gone(flip);
                    ImageView resize = (ImageView) mainLay.getChildAt(5);
                    gone(resize);
                    ImageView rotate = (ImageView) mainLay.getChildAt(6);
                    gone(rotate);
                    AutoFitEditText edit_text = (AutoFitEditText) mainLay.getChildAt(1);
                    edit_text.setEnabled(false);
                }
            }
        });
    }

    void setLay() {
        title.setTypeface(MyApplication.myRegular);

        RelativeLayout bottom_lay = findViewById(R.id.bottom_lay);
        Help.setSize(bottom_lay, 1080, 180, true);

        ImageView bottom_lay_img = findViewById(R.id.bottom_lay_img);
        Help.setSize(bottom_lay_img, 1080, 180, true);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(crop, 216, 180, true);
        Help.setSize(draw, 216, 180, true);
        Help.setSize(btn_blur, 216, 180, true);
        Help.setSize(sticker, 216, 180, true);
        Help.setSize(text, 216, 180, true);
        Help.setSize(image, 216, 180, true);

        if(isNetworkAvailable()){
            Help.setMargin(bottom_lay, 0, 20, 0, 200, true);
        }else{
            Help.setMargin(bottom_lay, 0, 20, 0, 0, true);
        }
    }

    void UnpressedAll(){
        crop.setImageResource(R.drawable.ed_crop_unpress);
        draw.setImageResource(R.drawable.ed_draw_unpress);
        btn_blur.setImageResource(R.drawable.ed_blur_unpress);
        sticker.setImageResource(R.drawable.ed_sticker_unpress);
        text.setImageResource(R.drawable.ed_text_unpress);
        image.setImageResource(R.drawable.ed_image_unpress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP && resultCode == RESULT_OK) {
            Glide.with(mContext).load(mBitmap).into(img);
            Resize(EditImageActivity.mBitmap, main);
        } else if (requestCode == SEL_ART) {
            if (resultCode == RESULT_OK) {
                addLay(Help.Image_Path, null, Help.mTextView);
            }
        } else if (requestCode == SEL_IMAGE) {
            if (resultCode == RESULT_OK) {
                addLay("", Help.mBitmap, Help.mTextView);
            }
        } else if (requestCode == Gallery_Image && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Help.mUri = data.getData();
            blur = false;
            Intent intent = new Intent(mContext, Image_Crop2.class);
            startActivityForResult(intent, SEL_IMAGE);
        } else if (requestCode == EDIT_TEXT) {
            if (resultCode == RESULT_OK) {
                currentEditText.setText(Help.mTextView.getText());
                currentEditText.setTypeface(Help.mTextView.getTypeface());
                currentEditText.setTextColor(Help.mTextView.getCurrentTextColor());
            }
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        currentImageView.setColorFilter(color);
        int pos = getCurrent(currentImageView);
        views.get(pos).setColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    class ImageSave extends AsyncTask<String, String, String> {
        ImageSave() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mBitmap = getBitmapFromView(mainLay);
        }

        protected void onPostExecute(String result) {
//            Toast.makeText(mContext, "Save Edit Image", Toast.LENGTH_SHORT).show();
            Help.nextwithnew(mContext, VISHTECHNO_Share_DeleteActivity.class);
            setResult(RESULT_OK);
            finish();
            super.onPostExecute(result);
        }

        protected String doInBackground(String... params) {
            VISHTECHNO_help.saveBitmap(mContext, mBitmap);
            return null;
        }
    }

    Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        view.draw(canvas);
        return returnedBitmap;
    }

    public void Resize(Bitmap bit, View v) {

        int layoutwidth = v.getWidth();
        int layoutheight = v.getHeight();
        int imagewidth = bit.getWidth();
        int imageheight = bit.getHeight();
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

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(newwidth, newheight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLay.setLayoutParams(layoutParams);
    }

    void addLay(String src, Bitmap mBit, TextView txt) {
        mainLay.performClick();
        final View stickerview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.stickerview, null, false);
        final RelativeLayout mainLay = stickerview.findViewById(R.id.mainLay);
        final ImageView sticker = stickerview.findViewById(R.id.sticker);
        final ImageView border = stickerview.findViewById(R.id.border);
        final ImageView close = stickerview.findViewById(R.id.close);
        final ImageView flip = stickerview.findViewById(R.id.flip);
        final ImageView resize = stickerview.findViewById(R.id.resize);
        final ImageView rotate = stickerview.findViewById(R.id.rotate);
        final AutoFitEditText edit_text = stickerview.findViewById(R.id.edit_text);
        edit_text.setEnabled(false);

        sticker_lay.addView(stickerview);
        Help.setSize(mainLay, Help.w(720), Help.w(720), false);
        Help.setCenter(stickerview);
//        Help.setMargin(sticker, 100, 100, 100, 100, false);
        Help.setMargin(sticker, 50, 50, 50, 50, false);
        int p = Help.w(20);
        edit_text.setPadding(p, p, p, p);
        Help.setMargin(border, 42, 42, 42, 42, false);

        Help.setSize(close, 84, 84, false);
        Help.setSize(flip, 84, 84, false);
        Help.setSize(resize, 84, 84, false);
        Help.setSize(rotate, 84, 84, false);

        if (txt != null) {
            edit_text.setText(txt.getText());
            edit_text.setTypeface(txt.getTypeface());
            edit_text.setTextColor(txt.getCurrentTextColor());
            edit_text.setShadowLayer(0, 0, 0, Help.getColor(mContext, R.color.white));
            Help.setMargin(sticker, 42, 42, 42, 42, false);
            if (isFirstText) {
                isFirstText = false;
                Help.Toast(mContext, "Double Tap to Edit Text");
            }
        }

        currentImageView = sticker;
        currentEditText = edit_text;

        if (mBit != null) {
            sticker.setImageBitmap(mBit);
        } else {
            Glide.with(mContext).load(src).into(sticker);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mainLay.performClick();
                int pos = getCurrent(sticker);
                views.remove(pos);
                sticker_lay.removeView(stickerview);
            }
        });

        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImageView = sticker;
                int pos = getCurrent(currentImageView);
                RotationColor rotationColor = views.get(pos);
                if (rotationColor.getThing().equals("art")) {
                    color_dialog(pos, rotationColor.getColor(), currentImageView);
                } else {
                    if (sticker.getScaleX() == 1) {
                        sticker.setScaleX(-1);
                    } else {
                        sticker.setScaleX(1);
                    }
                    if (edit_text.getScaleX() == 1) {
                        edit_text.setScaleX(-1);
                    } else {
                        edit_text.setScaleX(1);
                    }
                }
            }
        });

        resize.setOnTouchListener(new View.OnTouchListener() {

            int baseh;
            int basew;
            int basex;
            int basey;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int j = (int) event.getRawX();
                int i = (int) event.getRawY();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mainLay.getLayoutParams();
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mainLay.invalidate();
                        basex = j;
                        basey = i;
                        basew = mainLay.getWidth();
                        baseh = mainLay.getHeight();
                        int[] loaction = new int[2];
                        mainLay.getLocationOnScreen(loaction);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        float f2 = (float) Math.toDegrees(Math.atan2(i - basey, j - basex));
                        float f1 = f2;
                        if (f2 < 0.0F) {
                            f1 = f2 + 360.0F;
                        }
                        j -= basex;
                        int k = i - basey;
                        i = (int) (Math.sqrt(j * j + k * k)
                                * Math.cos(Math.toRadians(f1 - mainLay.getRotation())));
                        j = (int) (Math.sqrt(i * i + k * k)
                                * Math.sin(Math.toRadians(f1 - mainLay.getRotation())));
                        k = i * 2 + basew;
                        int m = j * 2 + baseh;
                        if (k > Help.w(250)) {
                            layoutParams.width = k;
                        }
                        if (m > Help.w(250)) {
                            layoutParams.height = m;
                        }
                        mainLay.setLayoutParams(layoutParams);
                        mainLay.performLongClick();
                        break;
                }
                return true;
            }
        });

        rotate.setOnTouchListener(new View.OnTouchListener() {

            float cX = 0.0f;
            float cY = 0.0f;
            double vAngle = 0.0d;
            double tAngle = 0.0d;
            double dAngle = 0.0d;
            double angle = 0.0d;

            @Override
            public boolean onTouch(View view1, MotionEvent event) {

                switch (event.getAction()) {
                    case 0:
                        Rect rect = new Rect();
                        ((View) mainLay.getParent()).getGlobalVisibleRect(rect);
                        cX = rect.exactCenterX();
                        cY = rect.exactCenterY();
                        vAngle = (double) ((View) mainLay.getParent()).getRotation();
                        tAngle = (Math.atan2((double) (cY - event.getRawY()), (double) (cX - event.getRawX())) * 180.0d) / 3.141592653589793d;
                        dAngle = vAngle - tAngle;
                        break;
                    case 2:
                        angle = (Math.atan2((double) (cY - event.getRawY()), (double) (cX - event.getRawX())) * 180.0d) / 3.141592653589793d;
                        float f = (float) (angle + dAngle);
                        ((View) mainLay.getParent()).setRotation(f);
                        ((View) mainLay.getParent()).invalidate();
                        ((View) mainLay.getParent()).requestLayout();
                        if (Math.abs(90.0f - Math.abs(f)) <= 5.0f) {
                            f = f > 0.0f ? 90.0f : -90.0f;
                        }
                        if (Math.abs(0.0f - Math.abs(f)) <= 5.0f) {
                            f = f > 0.0f ? 0.0f : -0.0f;
                        }
                        if (Math.abs(180.0f - Math.abs(f)) <= 5.0f) {
                            f = f > 0.0f ? 180.0f : -180.0f;
                        }
                        ((View) mainLay.getParent()).setRotation(f);
                        break;
                }
                return true;
            }
        });

        border.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    String text = edit_text.getText().toString();
                    if (!text.equals("")) {
                        Intent intent = new Intent(mContext, CreateTextActivity2.class);
                        intent.putExtra("text", text);
                        startActivityForResult(intent, EDIT_TEXT);
                    }
                    return super.onDoubleTap(e);
                }
            });

            float dX, dY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                gestureDetector.onTouchEvent(event);

                mainLay.performClick();

                border.setBackgroundResource(R.drawable.sticker_border);
                visible(close);
                visible(flip);
                visible(resize);
                visible(rotate);
                currentImageView = sticker;
                currentEditText = edit_text;

                int pos = getCurrent(currentImageView);
                RotationColor v = views.get(pos);
                UnpressedAll();
                if(v.getThing().equals("art")){
                    EditImageActivity.this.sticker.setImageResource(R.drawable.ed_sticker_press);
                }else if(v.getThing().equals("text")){
                    EditImageActivity.this.text.setImageResource(R.drawable.ed_text_press);
                }else if(v.getThing().equals("image")){
                    EditImageActivity.this.image.setImageResource(R.drawable.ed_image_press);
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = stickerview.getX() - event.getRawX();
                        dY = stickerview.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stickerview.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        if (thing.equals("art")) {
            flip.setImageResource(R.drawable.sticker_color);
            views.add(new RotationColor(stickerview, 45, 45, 180, Color.parseColor("#838383"), thing));
        } else {
            views.add(new RotationColor(stickerview, 45, 45, 180, 2, thing));
        }

    }

    int getCurrent(View view) {
        int pos = -1;
        for (int i = 0; i < sticker_lay.getChildCount(); i++) {
            RelativeLayout stickerview = (RelativeLayout) sticker_lay.getChildAt(i);
            RelativeLayout mainLay = (RelativeLayout) stickerview.getChildAt(0);
            ImageView img = (ImageView) mainLay.getChildAt(0);
            if (view == img) {
                pos = i;
            }
        }
        return pos;
    }

    void color_dialog(int pos, int pick_color, ImageView view) {

        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(0)
                .setColor(pick_color)
                .setShowAlphaSlider(true)
                .show(this);

//        final Dialog dialog = new Dialog(this,R.style.Theme_Transparent);
//        dialog.setContentView(R.layout.pop_color);
//
//        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
//        Help.setSize(mainLay, 865, 1130, false);
//
//        final ImageView cancel, submit;
//
//        cancel = dialog.findViewById(R.id.cancel);
//        submit = dialog.findViewById(R.id.ok);
//
//        Help.setSize(cancel, 290, 105, false);
//        Help.setSize(submit, 290, 105, false);
//        Help.setMargin(submit, 50, 0, 0, 0, false);
//
//        final ColorPicker picker = dialog.findViewById(R.id.picker);
//        Help.setSize(picker, 450, 450, false);
//
//        SVBar svBar = dialog.findViewById(R.id.svbar);
//        OpacityBar opacityBar = dialog.findViewById(R.id.opacitybar);
//
//        picker.addSVBar(svBar);
//        picker.addOpacityBar(opacityBar);
//
//        picker.setOldCenterColor(pick_color);
//        picker.setColor(pick_color);
//        svBar.setColor(pick_color);
//        opacityBar.setColor(pick_color);
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                dialog.dismiss();
//            }
//        });
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                view.setColorFilter(picker.getColor());
//                views.get(pos).setColor(picker.getColor());
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
    }

    public void setImage(int pos) {
        Help.Image_Path = "file:///android_asset/art/" + art_arr.get(pos);
        addLay(Help.Image_Path, null, Help.mTextView);
    }
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_editimage_id));
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
}
