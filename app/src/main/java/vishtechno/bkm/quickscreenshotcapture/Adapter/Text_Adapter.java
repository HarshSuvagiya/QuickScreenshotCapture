package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.R;

public class Text_Adapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> list;
    LayoutInflater inflater = null;
    Typeface custom_font;

    // constructor
    public Text_Adapter(Context context, ArrayList<String>  list) {
        mContext = context;
        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // returns the individual images to the widget as it requires them
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null)
            vi = inflater.inflate(R.layout.text_adapter, null);

        LinearLayout mainlay = vi.findViewById(R.id.mainlay);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Help.w(1024), -2);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int ml = Help.w(10);
        if (position == (list.size() - 1)) {
            params.setMargins(0, ml, 0, ml);
        } else {
            params.setMargins(0, ml, 0, 0);
        }
        mainlay.setLayoutParams(params);

        TextView t = vi.findViewById(R.id.t);

        custom_font = Help.getTypeface(mContext,"fonts/" + list.get(position));
        t.setTypeface(custom_font);
        return vi;
    }

}
