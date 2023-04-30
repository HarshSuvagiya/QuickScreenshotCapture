package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ayoubfletcher.consentsdk.ConsentSDK;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import vishtechno.bkm.quickscreenshotcapture.services.ScreenRecord_Icon;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.mVideoPath;

public class Video_Player extends Activity {

    Context mContext;
    VideoView bg_video;

    String new_url;

    ImageView share, delete, back, thumb;
    TextView title;

    TextView start_time, end_time, filename;
    ImageView play_pause, edit;
    SeekBar player_seek;
    Handler seekHandler = new Handler();
    ProgressDialog progress;
    LinearLayout play_lay;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        mContext = this;
        Help.FS(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_view_container);
// Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.admob_banner_videoplayer_id));
        adContainerView.addView(adView);
        loadBanner();
        loadInterstitial();


        bg_video = findViewById(R.id.bg_video);

        share = findViewById(R.id.share);
        delete = findViewById(R.id.delete);
        back = findViewById(R.id.back);

        title = findViewById(R.id.title);
        filename = findViewById(R.id.filename);

        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        play_pause = findViewById(R.id.play_pause);
        player_seek = findViewById(R.id.player_seek);
        thumb = findViewById(R.id.thumb);
        edit = findViewById(R.id.edit);

        new_url = Help.mVideoPath;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        progress = Help.setPD(mContext, "Loading...", false);
        ScreenRecord_Icon.mF = 2;
        if (Help.mBoolean) {
            Help.mBoolean = false;
            ScreenRecord_Icon.mHandler.removeCallbacksAndMessages(null);
//            progress.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MediaScannerConnection.scanFile(mContext, new String[]{mVideoPath}, new String[]{"video/*"}, null);
                    setVideo();
//                    progress.dismiss();
                }
            }, 1000);
        } else {
            setVideo();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("video/*");
                File f = new File(new_url);
                Uri uri = Uri.parse("file://" + f.getAbsolutePath());
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share Video"));
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thumb.setVisibility(View.GONE);
                bg_video.setBackgroundDrawable(null);
                if (bg_video.isPlaying()) {
                    bg_video.pause();
                    play_pause.setImageResource(R.drawable.play_click);
                } else {
                    seekHandler.removeCallbacksAndMessages(null);
                    seekUpdation();
                    bg_video.start();
                    play_pause.setImageResource(R.drawable.pause_click);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPause();

                deletePopUp();
//                AlertDialog.Builder alert = new AlertDialog.Builder(Video_Player.this);
//                alert.setTitle("Confirm Delete !");
//                alert.setMessage("Are you sure to delete Video??");
//                alert.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                // do your work here
//                                dialog.dismiss();
//                                File file = new File(new_url);
//                                if (file.delete()) {
//                                    MediaScannerConnection.scanFile(Video_Player.this, new String[]{new_url}, new String[]{"video/*"}, null);
//                                    Toast.makeText(Video_Player.this, "File Deleted", Toast.LENGTH_SHORT).show();
//                                }
//                                onBackPressed();
//                            }
//                        });
//                alert.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                alert.show();
            }
        });

        Glide.with(mContext).load(new_url).into(thumb);
        thumb.setVisibility(View.GONE);

        player_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean user) {
                if (user) {
                    bg_video.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        gone(edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EditVideActivity.mVideoUrl = new_url;
//                Help.nextwithnew(mContext, EditVideActivity.class);
            }
        });

//        filename.setText(Help.getFileName(new_url));
        setLayout();
    }

    private void deletePopUp() {
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
                File file = new File(new_url);
                if (file.delete()) {
                    MediaScannerConnection.scanFile(Video_Player.this, new String[]{new_url}, new String[]{"video/*"}, null);
                    Toast.makeText(Video_Player.this, "Your Video Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                onBackPressed();
            }
        });

        dialog.show();
    }

    void setVideo() {
        try {
            bg_video.setVideoPath(new_url);
            bg_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    seekHandler.removeCallbacksAndMessages(null);
                    play_pause.setImageResource(R.drawable.play_click);
                }
            });
            bg_video.start();
            seekHandler.removeCallbacksAndMessages(null);
            seekUpdation();
        } catch (Exception e) {
            Help.Toast(mContext, "Unable to Play Video");
            finish();
        }
    }

    @Override
    protected void onPause() {
        bg_video.pause();
        thumb.setVisibility(View.VISIBLE);
        play_pause.setImageResource(R.drawable.play_click);
        super.onPause();
    }

    void setLayout() {
        title.setTypeface(MyApplication.myRegular);
        filename.setTypeface(MyApplication.myRegular);
        start_time.setTypeface(MyApplication.myRegular);
        end_time.setTypeface(MyApplication.myRegular);

        LinearLayout play_lay = findViewById(R.id.play_lay);
        Help.setSize(play_lay, 1080, 160, false);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(share, 354, 130, false);
        Help.setSize(delete, 354, 130, false);
        Help.setMargin(share, 0,50,0,30, false);
        Help.setMargin(delete, 50,50,0,30, false);

        Help.setSize(play_pause, 103, 103, false);
    }

    @Override
    public void onBackPressed() {
        bg_video.stopPlayback();
//        ScreenRecord_Icon.mF = 0;
//        super.onBackPressed();

        if(interstitialAd.isLoaded()){
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    ScreenRecord_Icon.mF = 0;
                    finish();
                }
            });
            interstitialAd.show();
        }else {
            ScreenRecord_Icon.mF = 0;
            super.onBackPressed();
        }

    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        player_seek.setMax(bg_video.getDuration());
        String sct = Help.getDuration(bg_video.getCurrentPosition());
        start_time.setText(sct);
        String stt = Help.getDuration(bg_video.getDuration());
        end_time.setText(stt);

        player_seek.setProgress(bg_video.getCurrentPosition());
        seekHandler.postDelayed(run, 100);
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
    InterstitialAd interstitialAd;
    // Load Interstitial
    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_fullscreen_videoplayer_id));
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        interstitialAd.loadAd(ConsentSDK.getAdRequest(this));

    }
}
