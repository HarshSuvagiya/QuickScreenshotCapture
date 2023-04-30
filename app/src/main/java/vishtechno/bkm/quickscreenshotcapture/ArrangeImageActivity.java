package vishtechno.bkm.quickscreenshotcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;
import vishtechno.bkm.quickscreenshotcapture.Adapter.ItemAdapter;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.showLog;

public class ArrangeImageActivity extends AppCompatActivity {

    Activity mContext;
    TextView title;
    ImageView back, done;

    DragListView list;
    ArrayList<Pair<Long, ImageData>> tempList = new ArrayList<>();
    public static ArrayList<ImageData> arrangeImages;

    Help.AppPreferences preferences;
    RelativeLayout showcase_lay;
    GifImageView my_gif;
    ImageView checkbox, got_it;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_image);

        mContext = this;
        Help.FS(this);
        preferences = new Help.AppPreferences(this);

        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        title = findViewById(R.id.title);
        list = findViewById(R.id.list);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrangeImages.clear();
                for (int i = 0; i < tempList.size(); i++) {
                    arrangeImages.add(tempList.get(i).second);
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        init();
        setClick();
        setLay();
        setListView();
    }

    private void init() {
        showcase_lay = findViewById(R.id.showcase_lay);
        my_gif = findViewById(R.id.my_gif);
        checkbox = findViewById(R.id.checkbox);
        got_it = findViewById(R.id.got_it);

        check = false;
        if (preferences.getArrangeCase()) {
            gone(showcase_lay);
        }
    }

    private void setClick() {
        showcase_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check) {
                    check = false;
                    checkbox.setImageResource(R.drawable.checkbox_unpress);
                } else {
                    check = true;
                    checkbox.setImageResource(R.drawable.checkbox_press);
                }
            }
        });

        got_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.setArrangeCase(check);
                gone(showcase_lay);
            }
        });
    }

    private void setLay() {
        LinearLayout header = findViewById(R.id.header);
        Help.setSize(header, 1080, 182, false);
        header.setPadding(0, 0, 0, Help.w(30));

        title.setTypeface(MyApplication.myRegular);

        Help.setSize(back, 118, 96, false);
        Help.setSize(done, 118, 96, false);
        int p = Help.w(20);
        list.setPadding(p, 0, p, p);

        Help.setSize(my_gif, 600, 600, false);
        Help.setSize(checkbox, 485, 65, false);
        Help.setSize(got_it, 280, 100, false);
        Help.setMargin(got_it, 0, 75, 0, 75, false);
    }

    private void setListView() {
        try {
            tempList.clear();
            for (long i = 0; i < arrangeImages.size(); i++) {
                tempList.add(new Pair<>(i, arrangeImages.get((int) i)));
            }

            RecyclerView.LayoutManager lm1 = new GridLayoutManager(mContext, 2);
            list.setLayoutManager(lm1);
            ItemAdapter adapter = new ItemAdapter(mContext, tempList, R.layout.images_adapter, R.id.mainLay, true);
            list.setAdapter(adapter, false);
            list.setCanDragHorizontally(true);
        } catch (Exception e) {
            showLog(e.toString());
            Help.Toast(mContext, "Error set Arrange List Please try again");
        }
    }
}