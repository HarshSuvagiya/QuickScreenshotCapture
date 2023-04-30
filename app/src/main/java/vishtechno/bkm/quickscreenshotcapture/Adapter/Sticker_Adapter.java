package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import vishtechno.bkm.quickscreenshotcapture.StickerActivity;
import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.R;

public class Sticker_Adapter extends
        RecyclerView.Adapter<Sticker_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<String> paths;
    String folder;

    public Sticker_Adapter(Context context, ArrayList<String> myfolders, String folder) {
        this.mContext = context;
        this.paths = myfolders;
        this.folder = folder;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.shape_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        Glide.with(mContext).load(Uri.parse("file:///android_asset/" + folder + "/" + paths.get(position)))
                .into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ((StickerActivity) mContext).setImage(position);
            }
        });

        if((paths.size()-1)==position){
            Help.setMargin(holder.mainLay,0,30,0,30,false);
        }else {
            Help.setMargin(holder.mainLay,0,30,0,0,false);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mainLay;
        public ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            mainLay = itemView.findViewById(R.id.mainLay);
            img = itemView.findViewById(R.id.img);

            int p = Help.w(15);
            img.setPadding(p, p, p, p);

            Help.setCenterHorizontal(img);
            Help.setSize(mainLay, 233, 233, false);
        }
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

}
