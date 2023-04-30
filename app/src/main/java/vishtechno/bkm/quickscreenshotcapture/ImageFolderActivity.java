package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Adapter.IF_Adapter;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageFolderData;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.myList;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class ImageFolderActivity extends AppCompatActivity {

    public static Activity mContext;
    TextView title;
    ImageView back;

    RecyclerView list;
    ArrayList<ImageData> myVideo = new ArrayList<>();
    ArrayList<ImageFolderData> folderData = new ArrayList<>();
    IF_Adapter if_adapter;

    LinearLayout bottom_lay, addLay;
    ImageView done;
    TextView count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder);

        mContext = this;
        Help.FS(mContext);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        init();
        setLay();
    }

    void init() {
        list = findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(mContext, 2));
        if_adapter = new IF_Adapter(mContext, folderData);
        list.setAdapter(if_adapter);

        bottom_lay = findViewById(R.id.bottom_lay);
        addLay = findViewById(R.id.addLay);
        done = findViewById(R.id.done);
        count = findViewById(R.id.count);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StitchEditActivity.stitchAdd) {
                    finish();
                } else if (myList.size() >= 2) {
                    Help.nextwithnew(mContext, StitchEditActivity.class);
                    finish();
                } else {
                    Help.Toast(mContext, "2 Image Require");
                }
            }
        });

        if (VISHTECHNO_StartActivity.isEdit) {
            gone(bottom_lay);
            gone(done);
        }
    }

    void setLay() {
        title.setTypeface(MyApplication.myRegular);
        count.setTypeface(MyApplication.myBahnschrift);

        LinearLayout top_bar = findViewById(R.id.top_bar);
        Help.setSize(top_bar, 1080, 182, false);
        top_bar.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);

        Help.setSize(bottom_lay, 1080, 230, true);
    }

    class Get_Video extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            myVideo.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String[] proj = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.WIDTH,
                    MediaStore.Images.Media.HEIGHT};
            // Can include more data for more details and check it.

            final Cursor audioCursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");

            if (audioCursor != null) {
                if (audioCursor.moveToFirst()) {
                    do {
                        try {
                            String name = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                            String width = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.WIDTH));

                            String height = audioCursor
                                    .getString(audioCursor
                                            .getColumnIndex(MediaStore.Images.Media.HEIGHT));

                            String path = audioCursor.getString(audioCursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));

                            int w = Integer.parseInt(width);
                            int h = Integer.parseInt(height);


                            int r;
                            if (w > h) {
                                r = (w / h);
                            } else {
                                r = (h / w);
                            }

                            if (r <= 4) {
                                if (!name.endsWith(".gif")) {
                                    myVideo.add(new ImageData(name, path, w, h));
                                }
                            }
                        } catch (Exception e) {
                            showLog(e.toString());
                        }

                    } while (audioCursor.moveToNext());
                }
                audioCursor.close();
            }

            folderData.clear();
            ArrayList<ImageData> files;
            for (int i = 0; i < myVideo.size(); i++) {
                String folder = new File(myVideo.get(i).getImageUrl()).getParent();

                boolean b = false;
                int pos = 0;
                for (int k = 0; k < folderData.size(); k++) {
                    if (folderData.get(k).getfolder().equals(folder)) {
                        b = true;
                        pos = k;
                    }
                }

                if (b) {
                    ArrayList<ImageData> fs = folderData.get(pos).getPath();
                    fs.add(myVideo.get(i));
                } else {
                    files = new ArrayList<>();
                    files.add(myVideo.get(i));
                    folderData.add(new ImageFolderData(folder, files));
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Get_Video().execute();
        addLay.removeAllViews();
        for (int i = 0; i < myList.size(); i++) {
            addLay(myList.get(i).getImageUrl());
        }
    }

    public void addLay(final String Path) {

        count.setText("(" + myList.size() + ")\nSelected");

        final View myview = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.myview, null, false);
        RelativeLayout mainLay = myview.findViewById(R.id.mainLay);
        RoundedImageView thumb = myview.findViewById(R.id.thumb);
        ImageView close = myview.findViewById(R.id.close);

        addLay.addView(myview);

        Help.setSize(mainLay, 200, 190, false);
        Help.setSize(thumb, 176, 166, false);
        Help.setSize(close, 50, 50, false);

        Glide.with(mContext).load(Path).into(thumb);

        thumb.setCornerRadius(Help.w(15));

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
            }
        });
    }
}
