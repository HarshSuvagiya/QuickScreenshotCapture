package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.ListActivity;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageFolderData;
import vishtechno.bkm.quickscreenshotcapture.MyApplication;
import vishtechno.bkm.quickscreenshotcapture.R;

public class IF_Adapter extends
        RecyclerView.Adapter<IF_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<ImageFolderData> list;
    boolean attached;

    public IF_Adapter(Context context, ArrayList<ImageFolderData> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.if_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        final ImageFolderData fileData = list.get(position);

        String folderName = fileData.getfolder();
        holder.foldername.setText(new File(folderName).getName());

        if(attached) {
            Glide.with(mContext).load(fileData.getPath().get(0).getImageUrl()).into(holder.img);
        }

        holder.vid_count.setText(fileData.getPath().size() + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListActivity.imageFolderData = fileData;
                Help.nextwithnew(mContext, ListActivity.class);
            }
        });

        Help.setMargin(holder.mainLay, 0, 0, 0, 0, false);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        attached = true;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        attached = false;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mainLay;
        public RoundedImageView img;
        public TextView foldername, vid_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainLay = itemView.findViewById(R.id.mainLay);
            img = itemView.findViewById(R.id.img);
            foldername = itemView.findViewById(R.id.foldername);
            vid_count = itemView.findViewById(R.id.vid_count);

            Help.setSize(mainLay, 516, 522, false);
            int p = Help.w(23);
            mainLay.setPadding(p, p, p, p);
            Help.setSize(img, 468, 356, false);

            foldername.setTypeface(MyApplication.myRegular);
            vid_count.setTypeface(MyApplication.myRegular);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
