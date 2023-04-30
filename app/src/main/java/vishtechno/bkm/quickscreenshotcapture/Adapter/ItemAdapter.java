package vishtechno.bkm.quickscreenshotcapture.Adapter;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.Model.ImageData;
import vishtechno.bkm.quickscreenshotcapture.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class ItemAdapter extends DragItemAdapter<Pair<Long, ImageData>, ItemAdapter.ViewHolder> {

    Context mContext;
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    ArrayList<Pair<Long, ImageData>> imageLists;

    public ItemAdapter(Context context, ArrayList<Pair<Long, ImageData>> imageLists, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mContext = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.imageLists = imageLists;
        setItemList(this.imageLists);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        String text = imageLists.get(position).second.getImageUrl();
        Glide.with(mContext).load(text).into(holder.thumb);
        holder.itemView.setTag(mItemList.get(position));

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLists.remove(position);
                notifyDataSetChanged();
            }
        });

        if (imageLists.size() > 2) {
            visible(holder.close);
        } else {
            gone(holder.close);
        }
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        RelativeLayout mainLay;
        ImageView close;
        RoundedImageView thumb;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            thumb = itemView.findViewById(R.id.thumb);
            close = itemView.findViewById(R.id.close);
            mainLay = itemView.findViewById(R.id.mainLay);

            Help.setSize(mainLay, 462, 436, false);
            Help.setSize(close, 76, 66, false);
            Help.setMargin(mainLay, 0, 50, 0, 0, false);
            thumb.setCornerRadius(Help.w(35));
        }

        @Override
        public void onItemClicked(View view) {
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}