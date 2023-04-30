package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vishtechno.bkm.quickscreenshotcapture.EditImageActivity;
import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.ImageFolderActivity;
import vishtechno.bkm.quickscreenshotcapture.ListActivity;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.MyApplication;
import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_StartActivity;

import static vishtechno.bkm.quickscreenshotcapture.Help.myList;

public class List_Adapter extends
        RecyclerView.Adapter<List_Adapter.MyViewHolder> {

    static Context mContext;
    ArrayList<ImageData> folders;
    boolean attached;

    public List_Adapter(Context context, ArrayList<ImageData> myfolders) {
        this.mContext = context;
        this.folders = myfolders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_adapter, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,
                                 final int position) {

        final File f = new File(folders.get(position).getImageUrl());

        holder.foldername.setText(folders.get(position).getTitle());
        if(attached) {
            Glide.with(mContext).load(folders.get(position).getImageUrl())
                    .into(holder.folder_img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (VISHTECHNO_StartActivity.isEdit) {
                    VISHTECHNO_StartActivity.isEdit = false;
                    EditImageActivity.mImageUrl = f.getAbsolutePath();
                    Help.nextwithnew(mContext, EditImageActivity.class);
                    if (ImageFolderActivity.mContext != null) {
                        ImageFolderActivity.mContext.finish();
                    }
                    if (ListActivity.mContext != null) {
                        ListActivity.mContext.finish();
                    }
                } else {
                    boolean b = false;
                    for (int i = 0; i < myList.size(); i++) {
                        if (myList.get(i).getImageUrl().equals(f.getAbsolutePath())) {
                            myList.remove(i);
                            ((ListActivity) mContext).removeView(i);
                            b = true;
                        }
                    }
                    if (!b) {
                        myList.add(folders.get(position));
                        ((ListActivity) mContext).addLay(f.getAbsolutePath());
                    }

                    notifyDataSetChanged();
                }
            }
        });

        boolean b = false;
        for (int i = 0; i < myList.size(); i++) {
            if (myList.get(i).getImageUrl().equals(folders.get(position).getImageUrl())) {
                b = true;
                holder.selected.setText((i + 1) + "");
            }
        }

        if (VISHTECHNO_StartActivity.isEdit) {
            Help.gone(holder.selected);
        } else {
            if (b) {
                Help.visible(holder.selected);
            } else {
                Help.gone(holder.selected);
            }
        }

        if (position == (folders.size()-1)) {
            Help.setMargin(holder.mainlay, 0, 30, 0, 30, false);
        } else {
            Help.setMargin(holder.mainlay, 0, 30, 0, 0, false);
        }
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

        public LinearLayout mainlay;
        public RelativeLayout centerLay;
        public RoundedImageView folder_img;
        public TextView foldername,selected;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainlay = itemView.findViewById(R.id.mainlay);
            folder_img = itemView.findViewById(R.id.folder_img);
            foldername = itemView.findViewById(R.id.foldername);
            centerLay = itemView.findViewById(R.id.centerLay);
            selected = itemView.findViewById(R.id.selected);

            folder_img.setCornerRadius(Help.w(30));
            Help.setSize(centerLay, 304, 296, false);

            foldername.setTypeface(MyApplication.myRegular);
            selected.setTypeface(MyApplication.myRegular);
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

}
