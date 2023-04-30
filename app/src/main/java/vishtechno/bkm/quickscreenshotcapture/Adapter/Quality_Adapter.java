package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.R;

public class Quality_Adapter extends BaseAdapter {

    private ArrayList<String> title;
    LayoutInflater inflater = null;
    Context mContext;
    String STR;

    // constructor
    public Quality_Adapter(Context context, ArrayList<String> mytitle, String STR) {
        this.mContext = context;
        this.title = mytitle;
        this.STR = STR;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return title.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // returns the individual images to the widget as it requires them
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.quality_adapter, null);

        LinearLayout mainLay = vi.findViewById(R.id.mainLay);
        TextView name = vi.findViewById(R.id.name);
        Help.setSize(name, 332, 76, false);

        name.setText(title.get(position));

        if (STR.equals(title.get(position))) {
            name.setBackgroundResource(R.drawable.blue_bg);
            name.setTextColor(Color.WHITE);
        } else {
            name.setBackgroundResource(R.drawable.gray_bg);
            name.setTextColor(mContext.getResources().getColor(R.color.title_text));
        }

//        if ((title.size()- 1) == position) {
//            Help.setMargin(name, 0, 15, 0, 15, false);
//        } else {
            Help.setMargin(name, 0, 15, 0, 0, false);
//        }

        return vi;
    }
}
