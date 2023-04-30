package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Adapter.List_Adapter;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageFolderData;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.myList;

public class ListActivity extends AppCompatActivity {

    public static Activity mContext;
    ImageView back;
    TextView title;

    RecyclerView videoList;
    RecyclerView.LayoutManager lm;
    List_Adapter listadapter;

    int int_position;
    public static Activity mActivity;

    HorizontalScrollView horscroll;
    LinearLayout bottom_lay, addLay;
    ImageView done;
    TextView count;

    public static ImageFolderData imageFolderData;
    private ArrayList<ImageData> myImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mActivity = this;
        mContext = this;
        Help.FS(this);

        back = findViewById(R.id.back);
        title = findViewById(R.id.title);

        videoList = findViewById(R.id.videoList);

        int_position = getIntent().getIntExtra("value", 0);

        videoList.setHasFixedSize(true);
        lm = new GridLayoutManager(this, 3);
        videoList.setLayoutManager(lm);

        if (imageFolderData != null)
            myImages = imageFolderData.getPath();

        listadapter = new List_Adapter(this, myImages);
        videoList.setAdapter(listadapter);

        title.setText(new File(imageFolderData.getfolder()).getName());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        init();

        setLayout();

        if (VISHTECHNO_StartActivity.isEdit) {
            gone(bottom_lay);
            gone(done);
        }
    }

    private void init() {
        horscroll = findViewById(R.id.horscroll);
        bottom_lay = findViewById(R.id.bottom_lay);
        addLay = findViewById(R.id.addLay);
        done = findViewById(R.id.done);
        count = findViewById(R.id.count);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StitchEditActivity.stitchAdd) {
                    if (ImageFolderActivity.mContext != null) {
                        ImageFolderActivity.mContext.finish();
                    }
                    finish();
                } else if (myList.size() >= 2) {
                    Help.nextwithnew(mContext, StitchEditActivity.class);
                    if (ImageFolderActivity.mContext != null) {
                        ImageFolderActivity.mContext.finish();
                    }
                    finish();
                } else {
                    Help.Toast(mContext, "2 Image Require");
                }
            }
        });
    }

    public void setLayout() {
        title.setTypeface(MyApplication.myRegular);
        count.setTypeface(MyApplication.myBahnschrift);

        LinearLayout top_bar = findViewById(R.id.top_bar);
        Help.setSize(top_bar, 1080, 182, false);
        top_bar.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(bottom_lay, 1080, 230, true);
    }

    public void addLay(final String Path) {

        count.setText("(" + myList.size() + ")\nSelected");

        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.myview, null, false);
        RelativeLayout mainLay = myview.findViewById(R.id.mainLay);
        RoundedImageView thumb = myview.findViewById(R.id.thumb);
        ImageView close = myview.findViewById(R.id.close);

        addLay.addView(myview);
        horscroll.requestChildFocus(addLay, myview);

        Help.setSize(mainLay, 200, 190, false);
        Help.setSize(thumb, 176, 166, false);
        Help.setSize(close, 50, 50, false);

        thumb.setCornerRadius(Help.w(15));

        Glide.with(mContext).load(Path).into(thumb);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLay.removeView(myview);
                for (int i = 0; i < myList.size(); i++) {
                    if (Path.equals(myList.get(i).getImageUrl())) {
                        myList.remove(i);
                    }
                }
                count.setText("(" + myList.size() + ")\nSelected");
                listadapter.notifyDataSetChanged();
            }
        });

    }

    public void removeView(int pos) {
        addLay.removeViewAt(pos);
        count.setText("(" + myList.size() + ")\nSelected");
    }

    @Override
    protected void onResume() {
        super.onResume();
        addLay.removeAllViews();
        for (int i = 0; i < myList.size(); i++) {
            addLay(myList.get(i).getImageUrl());
        }
        listadapter.notifyDataSetChanged();
    }
}
