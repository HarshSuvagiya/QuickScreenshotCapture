package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;
import vishtechno.bkm.quickscreenshotcapture.Adapter.Quality_Adapter;
import vishtechno.bkm.quickscreenshotcapture.screenrecorder.Utils;
import vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Range;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import static vishtechno.bkm.quickscreenshotcapture.Help.invisible;
import static vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon.isRec;
import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class SR_Setting_Activity extends AppCompatActivity {

    public static Activity mContext;
    ImageView back, sra_img, srd_img;
    TextView title, txt_sr_audio, txt_ratio, ratio, txt_sr_dis;
    TextView txt_audio_bitrate, audio_bitrate, txt_audio_sample_rate, audio_sample_rate,
            txt_audio_channel, audio_channel, txt_video_bitrate, video_bitrate,
            txt_video_fps, video_fps, txt_video_fi, video_fi;

    ImageView arrow3, arrow4, arrow5, arrow6, arrow7, arrow8, arrow9;

    Help.AppPreferences preferences;

    MediaCodecInfo[] maaccodecinfos;
    ArrayList<String> array_list = new ArrayList<>();
    ArrayList<String> array_kbps = new ArrayList<>();
    ArrayList<String> array_hz = new ArrayList<>();
    ArrayList<String> array_channel = new ArrayList<>();
    ArrayList<String> array_vkbps = new ArrayList<>();
    ArrayList<String> array_fps = new ArrayList<>();
    ArrayList<String> array_fi = new ArrayList<>();
    ArrayList<String> quality_list = new ArrayList<>();

    RelativeLayout sr_img;
    PopupWindow mypopupWindow;
    TextView txt_permission, txt_video_setting, txt_audio_setting;
    ImageView text_off, text_on,img_off, img_on;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sr_setting);

        mContext = this;
        Help.FS(mContext);

        preferences = new Help.AppPreferences(mContext);
        init();
        setClick();
        setLay();
        initArrays();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_screenrecord_id));
        adContainerView.addView(adView);
        loadBanner();
    }

    private void initArrays() {
        Utils.findEncodersByTypeAsync("audio/mp4a-latm", new Utils.Callback() {
            public void onResult(MediaCodecInfo[] mediaCodecInfoArr) {
//                FloatingPlayTrans.logCodecInfos(mediaCodecInfoArr, "audio/mp4a-latm");
                maaccodecinfos = mediaCodecInfoArr;
                MediaCodecInfo audioCodecInfo = getAudioCodecInfo(maaccodecinfos[0].getName());
                MediaCodecInfo.CodecCapabilities capabilitiesForType = audioCodecInfo.getCapabilitiesForType("audio/mp4a-latm");

                int[] supportedSampleRates = capabilitiesForType.getAudioCapabilities().getSupportedSampleRates();
                array_hz.clear();
                for (int i = 0; i < supportedSampleRates.length; i++) {
                    array_hz.add("" + supportedSampleRates[i]);
                }

                Range<Integer> bitrateRange = capabilitiesForType.getAudioCapabilities().getBitrateRange();
                int lower = Math.max(bitrateRange.getLower() / 1000, 80);
                int upper = bitrateRange.getUpper() / 1000;
                List<Integer> rates = new ArrayList<>();
                for (int rate = lower; rate < upper; rate += lower) {
                    rates.add(rate);
                }
                rates.add(upper);
                array_kbps.clear();
                for (int i = 0; i < rates.size(); i++) {
                    array_kbps.add("" + rates.get(i));
                }

                array_channel.clear();
                String[] channel = getResources().getStringArray(R.array.audio_channels);
                for (int i = 0; i < channel.length; i++) {
                    array_channel.add("" + channel[i]);
                }

                array_vkbps.clear();
                String[] vkbps = getResources().getStringArray(R.array.video_bitrates);
                for (int i = 0; i < vkbps.length; i++) {
                    array_vkbps.add("" + vkbps[i]);
                }

                array_fps.clear();
                String[] fps = getResources().getStringArray(R.array.video_framerates);
                for (int i = 0; i < fps.length; i++) {
                    array_fps.add("" + fps[i]);
                }

                array_fi.clear();
                String[] fi = getResources().getStringArray(R.array.iframeintervals);
                for (int i = 0; i < fi.length; i++) {
                    array_fi.add("" + fi[i]);
                }
            }
        });

        quality_list.clear();
        quality_list.add("360p");
        quality_list.add("480p");
        quality_list.add("720p");
        quality_list.add("1080p");
    }

    private void init() {
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);

        txt_sr_audio = findViewById(R.id.txt_sr_audio);
        txt_ratio = findViewById(R.id.txt_ratio);
        ratio = findViewById(R.id.ratio);
        txt_sr_dis = findViewById(R.id.txt_sr_dis);

        img_off = findViewById(R.id.img_off);
        img_on = findViewById(R.id.img_on);
        sr_img = findViewById(R.id.sr_img);
        sra_img = findViewById(R.id.sra_img);
        srd_img = findViewById(R.id.srd_img);

        txt_audio_bitrate = findViewById(R.id.txt_audio_bitrate);
        txt_audio_sample_rate = findViewById(R.id.txt_audio_sample_rate);
        txt_audio_channel = findViewById(R.id.txt_audio_channel);
        audio_bitrate = findViewById(R.id.audio_bitrate);
        audio_sample_rate = findViewById(R.id.audio_sample_rate);
        audio_channel = findViewById(R.id.audio_channel);

        txt_video_bitrate = findViewById(R.id.txt_video_bitrate);
        txt_video_fps = findViewById(R.id.txt_video_fps);
        txt_video_fi = findViewById(R.id.txt_video_fi);
        video_bitrate = findViewById(R.id.video_bitrate);
        video_fps = findViewById(R.id.video_fps);
        video_fi = findViewById(R.id.video_fi);

        arrow3 = findViewById(R.id.arrow3);
        arrow4 = findViewById(R.id.arrow4);
        arrow5 = findViewById(R.id.arrow5);
        arrow6 = findViewById(R.id.arrow6);
        arrow7 = findViewById(R.id.arrow7);
        arrow8 = findViewById(R.id.arrow8);
        arrow9 = findViewById(R.id.arrow9);

        txt_permission = findViewById(R.id.txt_permission);
        txt_video_setting = findViewById(R.id.txt_video_setting);
        txt_audio_setting = findViewById(R.id.txt_audio_setting);

        text_off = findViewById(R.id.text_off);
        text_on = findViewById(R.id.text_on);
    }

    private void setClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sr_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(mContext)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("package:");
                        sb.append(getPackageName());
                        startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(sb.toString())), 102);
                        return;
                    }
                }
                if (isRec) {
                    Help.Toast(mContext, "Please stop Recording First");
                } else {
                    changeToggle();
                }

            }
        });

        sra_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setisAudio(!preferences.getisAudio());
                check();
            }
        });

        ratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                QualityDialog();
                setPopUpWindow("quality", arrow3);
                showPopup(view);
            }
        });

        srd_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setSRDis(!preferences.getSRDis());
                check();
            }
        });

        audio_bitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("kbps", arrow7);
                showPopup(view);
            }
        });

        audio_sample_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("hz", arrow8);
                showPopup(view);
            }
        });

        audio_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("channel", arrow9);
                showPopup(view);
            }
        });

        video_bitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("vkbps", arrow4);
                showPopup(view);
            }
        });

        video_fps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("fps", arrow5);
                showPopup(view);
            }
        });

        video_fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopUpWindow("fi", arrow6);
                showPopup(view);
            }
        });
    }

    void showPopup(View view) {
        int[] values = new int[2];
        view.getLocationInWindow(values);
        int positionOfIcon = values[1];
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels * 2) / 3;
        if (positionOfIcon > height) {
            mypopupWindow.showAsDropDown(view, 0, Help.w(-350));
        } else {
            mypopupWindow.showAsDropDown(view, 0, Help.w(-75));
        }
    }

    private void setLay() {
        title.setTypeface(MyApplication.myBold);
        txt_permission.setTypeface(MyApplication.myBold);
        txt_video_setting.setTypeface(MyApplication.myBold);
        txt_audio_setting.setTypeface(MyApplication.myBold);

        txt_sr_audio.setTypeface(MyApplication.myRegular);
        txt_sr_dis.setTypeface(MyApplication.myRegular);
        txt_ratio.setTypeface(MyApplication.myRegular);
        txt_video_fps.setTypeface(MyApplication.myRegular);
        txt_video_fi.setTypeface(MyApplication.myRegular);
        txt_audio_bitrate.setTypeface(MyApplication.myRegular);
        txt_audio_sample_rate.setTypeface(MyApplication.myRegular);
        txt_audio_channel.setTypeface(MyApplication.myRegular);
        setTextView(R.id.txts_sr_audio);
        setTextView(R.id.txts_sr_dis);
        setTextView(R.id.txts_ratio);
        setTextView(R.id.txts_video_fps);

        ratio.setTypeface(MyApplication.myRegular);
        video_fps.setTypeface(MyApplication.myRegular);
        video_fi.setTypeface(MyApplication.myRegular);
        video_bitrate.setTypeface(MyApplication.myRegular);
        audio_bitrate.setTypeface(MyApplication.myRegular);
        audio_sample_rate.setTypeface(MyApplication.myRegular);
        audio_channel.setTypeface(MyApplication.myRegular);

        Help.setSize(text_off, 132, 94, false);
        Help.setSize(text_on, 132, 94, false);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 150, false);

        LinearLayout top_bar = findViewById(R.id.top_bar);
        Help.setSize(top_bar, 1080, 720, false);

        ImageView below_shape = findViewById(R.id.below_shape);
        Help.setSize(below_shape, 1018, 132, false);
        Help.setMargin(below_shape, 0, 0, 0, -2, false);

        ScrollView sc = findViewById(R.id.sc);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Help.w(1000), LinearLayout.LayoutParams.MATCH_PARENT);
        sc.setLayoutParams(params);
        Help.setMargin(sc, 0, -100, 0, 200, false);

        Help.setSize(back, 70, 70, false);

        Help.setSize(img_off, 134, 132, false);
        Help.setSize(img_on, 120, 48, false);
        Help.setSize(sr_img, 240, 368, false);
        Help.setSize(sra_img, 152, 76, false);
        Help.setSize(srd_img, 152, 76, false);

        setIcon(R.id.icon1);
        setIcon(R.id.icon2);
        setIcon(R.id.icon3);
        setIcon(R.id.icon4);
        setIcon(R.id.icon5);
        setIcon(R.id.icon6);
        setIcon(R.id.icon7);
        setIcon(R.id.icon8);
        setIcon(R.id.icon9);

        setArrow(arrow3);
        setArrow(arrow4);
        setArrow(arrow5);
        setArrow(arrow6);
        setArrow(arrow7);
        setArrow(arrow8);
        setArrow(arrow9);
    }

    void setIcon(int id) {
        ImageView img = findViewById(id);
        Help.setSize(img, 80, 80, false);
    }

    void setTextView(int id) {
        TextView txt = findViewById(id);
        txt.setTypeface(MyApplication.myRegular);
    }

    void setArrow(ImageView img) {
        Help.setSize(img, 30, 30, false);
        Help.setMargin(img, 20, 0, 40, 0, false);
    }

    void changeToggle() {
        if (preferences.getisScreenRec()) {
            preferences.setisScreenRec(false);
            stopService(new Intent(mContext, ScreenRecord_Icon.class));
            check();
            startAnim(3, img_on, false);
            startAnim(4, img_off, false);
        } else {
            preferences.setisScreenRec(true);
            startService(new Intent(mContext, ScreenRecord_Icon.class));
            check();
            startAnim(1, img_on, true);
            startAnim(2, img_off, true);
        }
    }

    @SuppressLint("SetTextI18n")
    private void check() {
        if (preferences.getisScreenRec()) {
            sr_img.setBackgroundResource(R.drawable.sr_on_bg);
            text_off.setImageResource(R.drawable.txt_off_unpress);
            text_on.setImageResource(R.drawable.txt_on_press);
            invisible(img_off);
            visible(img_on);
        } else {
            sr_img.setBackgroundResource(R.drawable.sr_off_bg);
            text_off.setImageResource(R.drawable.txt_off_press);
            text_on.setImageResource(R.drawable.txt_on_unpress);
            invisible(img_on);
            visible(img_off);
        }

        if (preferences.getisAudio()) {
            sra_img.setImageResource(R.drawable.on);
        } else {
            sra_img.setImageResource(R.drawable.off);
        }

        boolean b1 = Help.checkServiceRunning(mContext, ScreenRecord_Icon.class);
        if (preferences.getSRDis()) {
            srd_img.setImageResource(R.drawable.on);
            if (b1) {
                visible(ScreenRecord_Icon.container);
            }
        } else {
            srd_img.setImageResource(R.drawable.off);
            if (b1) {
                gone(ScreenRecord_Icon.container);
            }
        }

        ratio.setText(preferences.getQuality());
        audio_bitrate.setText("" + preferences.getAudioBitrate());
        audio_sample_rate.setText("" + preferences.getAudioSampleRate());
        audio_channel.setText("" + preferences.getAudioChannel());
        video_bitrate.setText("" + preferences.getVideoBitrate());
        video_fps.setText("" + preferences.getVideoFPS());
        video_fi.setText("" + preferences.getVideoFI());
    }

    Techniques technique;
    public YoYo.YoYoString rope;

    void startAnim(int count, View view, boolean b) {
        visible(view);
        if (count == 1) {
            technique = Techniques.SlideInDown;
        } else if (count == 2) {
            technique = Techniques.SlideOutDown;
        } else if (count == 3) {
            technique = Techniques.SlideOutUp;
        } else {
            technique = Techniques.SlideInUp;
        }
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
                        if (b) {
                            invisible(img_off);
                            visible(img_on);
                        } else {
                            invisible(img_on);
                            visible(img_off);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(view);
    }

    void QualityDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.quality_popup);

        RelativeLayout main = dialog.findViewById(R.id.main);
        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        ListView list = dialog.findViewById(R.id.list);

        final ArrayList<String> quality_list = new ArrayList<>();
        quality_list.add("360p");
        quality_list.add("480p");
        quality_list.add("720p");
        quality_list.add("1080p");

        Quality_Adapter quality_adapter = new Quality_Adapter(mContext, quality_list, preferences.getQuality());
        list.setAdapter(quality_adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                preferences.setQuality(quality_list.get(position));
                check();
                dialog.dismiss();
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void DialogType(String type) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Transparent);
        dialog.setContentView(R.layout.quality_popup);

        RelativeLayout main = dialog.findViewById(R.id.main);
        LinearLayout mainLay = dialog.findViewById(R.id.mainLay);
        ListView list = dialog.findViewById(R.id.list);

        Quality_Adapter quality_adapter = null;
        if (type.equals("kbps")) {
            array_list = array_kbps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioBitrate());
        } else if (type.equals("hz")) {
            array_list = array_hz;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioSampleRate());
        } else if (type.equals("channel")) {
            array_list = array_channel;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioChannel());
        } else if (type.equals("vkbps")) {
            array_list = array_vkbps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoBitrate());
        } else if (type.equals("fps")) {
            array_list = array_fps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoFPS());
        } else if (type.equals("fi")) {
            array_list = array_fi;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoFI());
        }

        list.setAdapter(quality_adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("kbps")) {
                    preferences.setAudioBitrate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("hz")) {
                    preferences.setAudioSampleRate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("channel")) {
                    preferences.setAudioChannel(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("vkbps")) {
                    preferences.setVideoBitrate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("fps")) {
                    preferences.setVideoFPS(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("fi")) {
                    preferences.setVideoFI(Integer.parseInt(array_list.get(position)));
                }
                check();
                dialog.dismiss();
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public MediaCodecInfo getAudioCodecInfo(String str) {
        MediaCodecInfo mediaCodecInfo;
        if (str == null) {
            return null;
        }
        if (maaccodecinfos == null) {
            maaccodecinfos = Utils.findEncodersByType("audio/mp4a-latm");
        }
        int i = 0;
        while (true) {
            MediaCodecInfo[] mediaCodecInfoArr = maaccodecinfos;
            if (i >= mediaCodecInfoArr.length) {
                mediaCodecInfo = null;
                break;
            }
            mediaCodecInfo = mediaCodecInfoArr[i];
            if (mediaCodecInfo.getName().equals(str)) {
                break;
            }
            i++;
        }
        if (mediaCodecInfo == null) {
            return null;
        }
        return mediaCodecInfo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        check();
    }

    private void setPopUpWindow(String type, ImageView image) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.quality_popup, null);

        LinearLayout mainlay = view.findViewById(R.id.mainLay);

        mypopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Help.w(423), RelativeLayout.LayoutParams.WRAP_CONTENT);
        mainlay.setLayoutParams(params);

        mainlay.setPadding(Help.w(30), Help.w(30), Help.w(25), Help.w(45));
        ListView list = view.findViewById(R.id.list);

        Quality_Adapter quality_adapter = null;
        if (type.equals("quality")) {
            array_list = quality_list;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getQuality());
        } else if (type.equals("kbps")) {
            array_list = array_kbps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioBitrate());
        } else if (type.equals("hz")) {
            array_list = array_hz;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioSampleRate());
        } else if (type.equals("channel")) {
            array_list = array_channel;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getAudioChannel());
        } else if (type.equals("vkbps")) {
            array_list = array_vkbps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoBitrate());
        } else if (type.equals("fps")) {
            array_list = array_fps;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoFPS());
        } else if (type.equals("fi")) {
            array_list = array_fi;
            quality_adapter = new Quality_Adapter(mContext, array_list, "" + preferences.getVideoFI());
        }

        list.setAdapter(quality_adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("quality")) {
                    preferences.setQuality(array_list.get(position));
                } else if (type.equals("kbps")) {
                    preferences.setAudioBitrate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("hz")) {
                    preferences.setAudioSampleRate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("channel")) {
                    preferences.setAudioChannel(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("vkbps")) {
                    preferences.setVideoBitrate(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("fps")) {
                    preferences.setVideoFPS(Integer.parseInt(array_list.get(position)));
                } else if (type.equals("fi")) {
                    preferences.setVideoFI(Integer.parseInt(array_list.get(position)));
                }
                check();
                mypopupWindow.dismiss();
            }
        });

        image.setImageResource(R.drawable.up_click);
        mypopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                image.setImageResource(R.drawable.down_click);
            }
        });

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