package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vishtechno.bkm.quickscreenshotcapture.view.cropper.CropImageView;

public class Image_Crop2 extends Activity {

    Context mContext;
    CropImageView img;
    ImageView done, back, rotate;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_crop);

        mContext = this;
        Help.FS(this);

        img = findViewById(R.id.img);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        rotate = findViewById(R.id.rotate);

        title = findViewById(R.id.title);

        img.setImageUriAsync(Help.mUri);

        done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    Help.mBitmap = bitmapResize(img.getCroppedImage());
                    if (Help.mBitmap != null) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Help.Toast(mContext, "Something went wrong");
                    }
                } catch (Exception e) {
                    e.toString();
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

        setLayout();
    }

    void setLayout() {
        title.setTypeface(MyApplication.myRegular);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);
        Help.setSize(rotate, 294, 142, false);
        Help.setMargin(rotate, 0,50,0,0, false);
    }

    public Bitmap bitmapResize(Bitmap bit) {

        int layoutwidth = Help.width;
        int layoutheight = Help.height;
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
