package vishtechno.bkm.quickscreenshotcapture.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vishtechno.bkm.quickscreenshotcapture.Adapter.VISHTECHNO_Creation_Adapter;
import vishtechno.bkm.quickscreenshotcapture.Help;
import vishtechno.bkm.quickscreenshotcapture.Model.VISHTECHNO_SavedPhoto_Model;
import vishtechno.bkm.quickscreenshotcapture.R;

public class ScreenShots_Fragment extends Fragment {

    private Activity mActivity;
    TextView txt;
    private RecyclerView list;

    File file;
    ArrayList<VISHTECHNO_SavedPhoto_Model> creation_models = new ArrayList<>();
    VISHTECHNO_Creation_Adapter creation_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment, container, false);

        mActivity = getActivity();
        txt = view.findViewById(R.id.txt);
        txt.setText("");

        list = view.findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(mActivity, 2));

        Help.setMargin(list, 35, 0, 35, 0, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/" + getResources().getString(R.string.app_name));
        VISHTECHNO_SavedPhoto_Model creation_model;

        creation_models.clear();

        if (mediaStorageDir.exists()) {
            File[] files = mediaStorageDir.listFiles();

            if(files!=null) {
                Arrays.sort(files, new Comparator() {
                    public int compare(Object o1, Object o2) {

                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });

                for (int i = 0; i < files.length; i++) {
                    file = files[i];
                    creation_model = new VISHTECHNO_SavedPhoto_Model();
                    String pathh = file.getAbsolutePath();
                    String name = file.getName();

                    creation_model.setFile_path(pathh);
                    creation_model.setFile_name(name);

                    if (file.isFile()) {
                        creation_models.add(creation_model);
                    }
                }
            }
        }

        creation_adapter = new VISHTECHNO_Creation_Adapter(mActivity, creation_models,false);
        list.setAdapter(creation_adapter);
        creation_adapter.notifyDataSetChanged();
    }
}
