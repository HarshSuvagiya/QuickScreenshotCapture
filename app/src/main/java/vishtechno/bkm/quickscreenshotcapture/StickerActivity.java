package vishtechno.bkm.quickscreenshotcapture;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vishtechno.bkm.quickscreenshotcapture.Adapter.Sticker_Adapter;

public class StickerActivity extends Activity {

    Context mContext;
    TextView title;
    ImageView back;

    RecyclerView art_list;
    Sticker_Adapter art_adapter;
    ArrayList<String> art_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        mContext = this;
        Help.FS(this);

        title = findViewById(R.id.title);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        art_list = findViewById(R.id.art_list);
        art_arr = Help.setList(mContext, "art");
        art_adapter = new Sticker_Adapter(mContext, art_arr, "art");
        GridLayoutManager lm = new GridLayoutManager(this, 4);
        art_list.setLayoutManager(lm);
        art_list.setAdapter(art_adapter);

        title.setTypeface(MyApplication.myRegular);

        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0,0,0,Help.w(30));

        Help.setSize(back, 118, 96, false);
        Help.setMargin(art_list, 20,0,20,0, false);
    }

    public void setImage(int pos) {
        Help.Image_Path = "file:///android_asset/art/" + art_arr.get(pos);
        setResult(RESULT_OK);
        finish();
    }
}
