package vishtechno.bkm.quickscreenshotcapture.Model;

import java.util.ArrayList;

public class ImageFolderData {
    String folder;
    ArrayList<ImageData> path;

    public ImageFolderData(String folder, ArrayList<ImageData> path) {
        this.folder = folder;
        this.path = path;
    }

    public String getfolder() {
        return folder;
    }

    public void setfolder(String folder) {
        this.folder = folder;
    }

    public ArrayList<ImageData> getPath() {
        return path;
    }

    public void setPath(ArrayList<ImageData> path) {
        this.path = path;
    }
}
