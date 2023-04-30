package vishtechno.bkm.quickscreenshotcapture.helper;

import android.os.FileObserver;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VISHTECHNO_RecursiveFileObserver extends FileObserver {
    private EventListener mListener;
    private int mMask;
    private final Map<String, FileObserver> mObservers;
    private String mPath;

    public interface EventListener {
        void onEvent(int i, File file);
    }

    private class SingleFileObserver extends FileObserver {
        private String filePath;

        public SingleFileObserver(String str, int i) {
            super(str, i);
            filePath = str;
        }

        public void onEvent(int i, String str) {
            File file;
            if (str == null) {
                file = new File(filePath);
            } else {
                file = new File(filePath, str);
            }
            int i2 = i & 4095;
            if (i2 != 256) {
                if (i2 == 1024) {
                    VISHTECHNO_RecursiveFileObserver.this.stopWatching(filePath);
                }
            } else if (VISHTECHNO_RecursiveFileObserver.this.watch(file)) {
                VISHTECHNO_RecursiveFileObserver.this.startWatching(file.getAbsolutePath());
            }
            VISHTECHNO_RecursiveFileObserver.this.notify(i, file);
        }
    }

    public VISHTECHNO_RecursiveFileObserver(String str, EventListener eventListener) {
        this(str, 4095, eventListener);
    }

    public VISHTECHNO_RecursiveFileObserver(String str, int i, EventListener eventListener) {
        super(str, i);
        this.mObservers = new HashMap();
        this.mPath = str;
        this.mMask = i | 256 | 1024;
        this.mListener = eventListener;
    }

    public void startWatching(String str) {
        synchronized (this.mObservers) {
            FileObserver fileObserver = (FileObserver) mObservers.remove(str);
            if (fileObserver != null) {
                fileObserver.stopWatching();
            }
            SingleFileObserver singleFileObserver = new SingleFileObserver(str, mMask);
            singleFileObserver.startWatching();
            mObservers.put(str, singleFileObserver);
        }
    }

    public void startWatching() {
        Stack stack = new Stack();
        stack.push(mPath);
        while (!stack.empty()) {
            String str = (String) stack.pop();
            startWatching(str);
            File[] listFiles = new File(str).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (watch(file)) {
                        stack.push(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public boolean watch(File file) {
        return file.isDirectory() && !file.getName().equals(".") && !file.getName().equals("..");
    }

    public void stopWatching(String str) {
        synchronized (mObservers) {
            FileObserver fileObserver = (FileObserver) mObservers.remove(str);
            if (fileObserver != null) {
                fileObserver.stopWatching();
            }
        }
    }

    public void stopWatching() {
        synchronized (mObservers) {
            for (FileObserver stopWatching : mObservers.values()) {
                stopWatching.stopWatching();
            }
            mObservers.clear();
        }
    }

    public void onEvent(int i, String str) {
        File file;
        if (str == null) {
            file = new File(mPath);
        } else {
            file = new File(mPath, str);
        }
        notify(i, file);
    }

    public void notify(int i, File file) {
        if (mListener != null) {
            mListener.onEvent(i & 4095, file);
        }
    }
}
