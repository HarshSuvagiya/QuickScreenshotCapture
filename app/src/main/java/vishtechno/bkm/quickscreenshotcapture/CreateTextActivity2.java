package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import vishtechno.bkm.quickscreenshotcapture.Adapter.Text_Adapter;
import vishtechno.bkm.quickscreenshotcapture.view.AutoFitEditText;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.ColorPicker;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.OpacityBar;
import vishtechno.bkm.quickscreenshotcapture.view.holocolorpicker.SVBar;

import static vishtechno.bkm.quickscreenshotcapture.EditImageActivity.currentEditText;

public class CreateTextActivity2 extends FragmentActivity implements ColorPickerDialogListener {

    Context mContext;
    TextView title;
    ImageView back, done;

    AutoFitEditText edit_text;
    RelativeLayout save;

    int pick_color = 0xffff0000;
    ImageView txt_color, txt_style;
    ArrayList<String> style_arr;
    Text_Adapter text_adapter;
    RelativeLayout center_lay;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text);

        mContext = this;
        Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_text_id));
        adContainerView.addView(adView);
        loadBanner();


        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edit_text.getText().toString();
                if (Help.hasChar(text)) {
                    try {
                        Help.mTextView = new TextView(mContext);
                        Help.mTextView.setText(text);
                        Help.mTextView.setTextColor(edit_text.getCurrentTextColor());
                        Help.mTextView.setTypeface(edit_text.getTypeface());

                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.toString();
                    }
                } else {
                    Help.Toast(mContext, "Invalid");
                }
            }
        });

        center_lay = findViewById(R.id.center_lay);
        edit_text = findViewById(R.id.edit_text);
        edit_text.setFilters(new InputFilter[] {new InputFilter.LengthFilter(150)});

        save = findViewById(R.id.save);

        txt_color = findViewById(R.id.txt_color);
        txt_style = findViewById(R.id.txt_style);

        txt_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                color_dialog();
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(0)
                        .setColor(pick_color)
                        .setShowAlphaSlider(true)
                        .show(CreateTextActivity2.this);
            }
        });

        txt_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                style_dialog();
            }
        });

        style_arr = Help.setList(mContext, "fonts");
        text_adapter = new Text_Adapter(mContext, style_arr);

        String text = getIntent().getStringExtra("text");
        edit_text.setText(text);

        if (!text.equals("")) {
            edit_text.setTextColor(currentEditText.getCurrentTextColor());
            edit_text.setTypeface(currentEditText.getTypeface());
        }

        setLay();
    }

    void setLay() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout bottom_lay = findViewById(R.id.bottom_lay);
        Help.setMargin(bottom_lay, 0,125,0,0, false);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(center_lay, 964, 1128, false);
        Help.setMargin(center_lay, 0,35,0,0, false);

        Help.setSize(txt_color, 194, 188, false);
        Help.setSize(txt_style, 194, 188, false);
        Help.setMargin(txt_style, 120,0,0,0, false);

    }

    void color_dialog() {
        final Dialog dialog = new Dialog(this,R.style.Theme_Transparent);
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
                edit_text.setTextColor(pick_color);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void style_dialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sel_text);

        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        Help.setSize(mainLay, 932, 882, false);

        TextView title = dialog.findViewById(R.id.title);
        title.setTypeface(MyApplication.myBahnschrift);

        ImageView close=dialog.findViewById(R.id.close);
        Help.setSize(close, 30, 30, false);
        Help.setMargin(close, 0,0,50,0, false);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        GridView stylelist;
        stylelist = dialog.findViewById(R.id.stylelist);
        stylelist.setAdapter(text_adapter);

        stylelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                edit_text.setTypeface(Help.getTypeface(mContext, "fonts/" + style_arr.get(position)));
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
    public void onColorSelected(int dialogId, int color) {
        pick_color = color;
        edit_text.setTextColor(pick_color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
