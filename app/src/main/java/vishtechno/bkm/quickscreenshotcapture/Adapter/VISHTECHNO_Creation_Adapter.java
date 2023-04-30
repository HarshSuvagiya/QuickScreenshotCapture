package vishtechno.bkm.quickscreenshotcapture.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.Model.VISHTECHNO_SavedPhoto_Model;
import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.VISHTECHNO_Share_DeleteActivity;
import vishtechno.bkm.quickscreenshotcapture.Video_Player;
import vishtechno.bkm.quickscreenshotcapture.helper.VISHTECHNO_help;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static vishtechno.bkm.quickscreenshotcapture.Help.gone;
import static vishtechno.bkm.quickscreenshotcapture.Help.visible;

public class VISHTECHNO_Creation_Adapter extends RecyclerView.Adapter<VISHTECHNO_Creation_Adapter.ViewHolder> {
    Context mContext;
    ArrayList<VISHTECHNO_SavedPhoto_Model> creation_models;
    boolean aBoolean;

    public VISHTECHNO_Creation_Adapter(Context mContext, ArrayList<VISHTECHNO_SavedPhoto_Model> creation_models, boolean b) {
        this.mContext = mContext;
        this.creation_models = creation_models;
        this.aBoolean = b;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vishtechno_creations_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final VISHTECHNO_SavedPhoto_Model model = creation_models.get(position);

        final String path = model.getFile_path();

        Glide.with(mContext).load(path).into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aBoolean) {
                    Help.mVideoPath = path;
                    Help.nextwithnew(mContext, Video_Player.class);
                } else {
                    VISHTECHNO_help.creation_path = path;
                    VISHTECHNO_help.from = 1;
                    Intent intent = new Intent(mContext, VISHTECHNO_Share_DeleteActivity.class);
                    mContext.startActivity(intent);
                }


            }
        });

        if (aBoolean) {
            visible(viewHolder.play_icon);
        } else {
            gone(viewHolder.play_icon);
        }

        if (position == (creation_models.size() - 1)) {
            Help.setMargin(viewHolder.mainLay, 0, 30, 0, 30, false);
        } else {
            Help.setMargin(viewHolder.mainLay, 0, 30, 0, 0, false);
        }
    }

    @Override
    public int getItemCount() {
        return creation_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView play_icon;
        LinearLayout mainLay;
        RoundedImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.img);
            play_icon = itemView.findViewById(R.id.play_icon);
            mainLay = itemView.findViewById(R.id.mainLay);

            Help.setSize(mainLay, 464, 512, false);
            Help.setSize(play_icon, 125, 215, false);
            imageView.setCornerRadius(Help.w(25));
        }
    }
}
