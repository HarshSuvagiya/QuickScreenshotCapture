package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vishtechno.bkm.quickscreenshotcapture.view.cropper.CropImageView;

public class Image_Crop extends Activity {

    Activity mContext;

    CropImageView img;
    ImageView done, back, rotate;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_crop);

        mContext = this;
        Help.FS(mContext);

        img = findViewById(R.id.img);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        rotate = findViewById(R.id.rotate);

        title = findViewById(R.id.title);

        img.setImageBitmap(EditImageActivity.mBitmap);

        done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bitmap b = img.getCroppedImage();
                if (b != null) {
                    EditImageActivity.mBitmap = bitmapResize(b);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(mContext, "Unable to Crop", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });

        rotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setRotatedDegrees(img.getRotatedDegrees() + 90);
            }
        });

//        img.setFixedAspectRatio(true);
//        img.setAspectRatio(9, 16);

        setLayout();
    }

    void setLayout() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0,0,0,Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);
        Help.setSize(rotate, 294, 142, false);
        Help.setMargin(rotate, 0,50,0,0, false);
    }

    public Bitmap bitmapResize(Bitmap bit) {

        int layoutwidth = 720;
        int layoutheight = 1280;
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
        Bitmap b56 = Bitmap.createScaledBitmap(bit, newwidth, newheight, true);
        return b56;
    }
}
