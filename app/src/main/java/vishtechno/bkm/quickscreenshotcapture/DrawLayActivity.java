package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.divyanshu.draw.widget.DrawView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import cn.hzw.doodle.DoodleColor;
import cn.hzw.doodle.DoodleOnTouchGestureListener;
import cn.hzw.doodle.DoodlePen;
import cn.hzw.doodle.DoodleShape;
import cn.hzw.doodle.DoodleTouchDetector;
import cn.hzw.doodle.DoodleView;
import cn.hzw.doodle.IDoodleListener;
import cn.hzw.doodle.core.IDoodle;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.ColorPicker;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.OpacityBar;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.SVBar;

public class DrawLayActivity extends FragmentActivity implements ColorPickerDialogListener {

    Activity mContext;

    DoodleView draw;
    ImageView reset, color, img, back, done, undo, redo;
    SeekBar seek;

    int pick_color = 0xff80ff00;

    TextView title;
    RelativeLayout mainLay;
    private FrameLayout adContainerView;

    ImageView erase, brush, line, arrow, circle, square, circle_fill, square_fill;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawlay);

        mContext = this;
        Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_drawlay_id));
        adContainerView.addView(adView);
        loadBanner();

        init();
        setClick();
        shapeinit();
        shapesetClick();
        setLayout();
    }

    private void init() {
        seek = findViewById(R.id.seek);

        img = findViewById(R.id.img);
        reset = findViewById(R.id.reset);
        color = findViewById(R.id.color);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);

        title = findViewById(R.id.title);
        mainLay = findViewById(R.id.mainLay);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);

        seek.setMax(Help.w(100));
        seek.setProgress(0);

        if(EditImageActivity.mBitmap!=null) {
            draw = new DoodleView(this, EditImageActivity.mBitmap, new IDoodleListener() {
                @Override
                public void onSaved(IDoodle doodle, Bitmap doodleBitmap, Runnable callback) {
                    EditImageActivity.mBitmap = doodleBitmap;
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onReady(IDoodle doodle) {
                    doodle.setSize(Help.w(15));
                }
            });

            DoodleOnTouchGestureListener touchGestureListener = new DoodleOnTouchGestureListener(draw, null);
            DoodleTouchDetector touchDetector = new DoodleTouchDetector(this, touchGestureListener);
            draw.setDefaultTouchDetector(touchDetector);

            draw.setPen(DoodlePen.BRUSH);
            draw.setShape(DoodleShape.HAND_WRITE);
            draw.setColor(new DoodleColor(pick_color));

            mainLay.addView(draw);
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Help.Toast(mContext,"Something went wrong");
                    onBackPressed();
                }
            });
        }
    }

    private void setClick() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.clear();
            }
        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(0)
                        .setColor(pick_color)
                        .setShowAlphaSlider(true)
                        .show(DrawLayActivity.this);
//                color_dialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.save();
                ;
            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (draw.getShape().equals(DoodleShape.ARROW)) {
                    draw.setSize((progress + Help.w(15)) * 3);
                } else {
                    draw.setSize(progress + Help.w(15));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.redo(1);
            }
        });
    }

    private void shapeinit() {
        erase = findViewById(R.id.erase);
        brush = findViewById(R.id.brush);
        line = findViewById(R.id.line);
        arrow = findViewById(R.id.arrow);
        circle = findViewById(R.id.circle);
        square = findViewById(R.id.square);
        circle_fill = findViewById(R.id.circle_fill);
        square_fill = findViewById(R.id.square_fill);
    }

    private void shapesetClick() {
        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                draw.setPen(DoodlePen.ERASER);
                draw.setShape(DoodleShape.HAND_WRITE);
                erase.setImageResource(R.drawable.erase_press);
            }
        });

        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                brush.setImageResource(R.drawable.brush_press);
                draw.setShape(DoodleShape.HAND_WRITE);
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                line.setImageResource(R.drawable.line_press);
                draw.setShape(DoodleShape.LINE);
            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                draw.setSize((seek.getProgress() + Help.w(15)) * 3);
                arrow.setImageResource(R.drawable.arrow_press);
                draw.setShape(DoodleShape.ARROW);
            }
        });

        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                circle.setImageResource(R.drawable.round_press);
                draw.setShape(DoodleShape.HOLLOW_CIRCLE);
            }
        });

        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                square.setImageResource(R.drawable.square_press);
                draw.setShape(DoodleShape.HOLLOW_RECT);
            }
        });

        circle_fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                circle_fill.setImageResource(R.drawable.round_fill_press);
                draw.setShape(DoodleShape.FILL_CIRCLE);
            }
        });

        square_fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unpressAll();
                square_fill.setImageResource(R.drawable.square_fill_press);
                draw.setShape(DoodleShape.FILL_RECT);
            }
        });
    }

    void setLayout() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout bottom_lay = findViewById(R.id.bottom_lay);
        Help.setSize(bottom_lay, 1080, 160, true);

        LinearLayout seek_lay = findViewById(R.id.seek_lay);
        Help.setSize(seek_lay, 1080, 182, false);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(color, 110, 110, false);
        Help.setMargin(color, 0, 0, 50, 0, false);

        Help.setSize(reset, 272, 110, false);
        Help.setSize(redo, 272, 110, false);
        Help.setSize(undo, 272, 110, false);
        Help.setMargin(reset, 30, 30, 30, 30, false);

        Help.setSize(erase, 110, 110, true);
        Help.setSize(brush, 110, 110, true);
        Help.setSize(line, 110, 110, true);
        Help.setSize(arrow, 110, 110, true);
        Help.setSize(circle, 110, 110, true);
        Help.setSize(square, 110, 110, true);
        Help.setSize(circle_fill, 110, 110, true);
        Help.setSize(square_fill, 110, 110, true);
        Help.setMargin(brush, 20, 0, 0, 0, true);
        Help.setMargin(line, 20, 0, 0, 0, true);
        Help.setMargin(arrow, 20, 0, 0, 0, true);
        Help.setMargin(circle, 20, 0, 0, 0, true);
        Help.setMargin(square, 20, 0, 0, 0, true);
        Help.setMargin(circle_fill, 20, 0, 0, 0, true);
        Help.setMargin(square_fill, 20, 0, 0, 0, true);

        if (isNetworkAvailable()) {
            Help.setMargin(bottom_lay, 0, 0, 0, 200, true);
        } else {
            Help.setMargin(bottom_lay, 0, 0, 0, 0, true);
        }
    }

    void unpressAll() {
        draw.setPen(DoodlePen.BRUSH);
        draw.setSize(seek.getProgress() + (Help.w(15)));

        erase.setImageResource(R.drawable.erase_unpress);
        brush.setImageResource(R.drawable.brush_unpress);
        line.setImageResource(R.drawable.line_unpress);
        arrow.setImageResource(R.drawable.arrow_unpress);
        circle.setImageResource(R.drawable.round_unpress);
        square.setImageResource(R.drawable.square_unpress);
        circle_fill.setImageResource(R.drawable.round_fill_unpress);
        square_fill.setImageResource(R.drawable.square_fill_unpress);
    }

    void color_dialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.pop_color);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 865, 1130, false);

        final ImageView cancel, submit;

        cancel = dialog.findViewById(R.id.cancel);
        submit = dialog.findViewById(R.id.ok);

        Help.setSize(cancel, 290, 105, false);
        Help.setSize(submit, 290, 105, false);
        Help.setMargin(submit, 50, 0, 0, 0, false);

        final ColorPicker picker = dialog.findViewById(R.id.picker);
        Help.setSize(picker, 450, 450, false);

        SVBar svBar = dialog.findViewById(R.id.svbar);
        OpacityBar opacityBar = dialog.findViewById(R.id.opacitybar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        picker.setOldCenterColor(pick_color);
        picker.setColor(pick_color);
        svBar.setColor(pick_color);
        opacityBar.setColor(pick_color);

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
                pick_color = picker.getColor();
//                draw.setColor(pick_color);
                draw.setColor(new DoodleColor(pick_color));
                dialog.dismiss();
            }
        });

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        PopUp();
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

    @Override
    public void onColorSelected(int dialogId, int color) {
        pick_color = color;
        draw.setColor(new DoodleColor(pick_color));
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
