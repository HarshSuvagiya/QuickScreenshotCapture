package vishtechno.bkm.quickscreenshotcapture.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import vishtechno.bkm.quickscreenshotcapture.R;
import vishtechno.bkm.quickscreenshotcapture.services.VISHTECHNO_FloatWidgetService;

import androidx.core.app.NotificationCompat;

public class VISHTECHNO_RecieverService extends Service {

    Context context;
    Intent intent;
    PendingIntent pendingIntent;
    IntentFilter theFilter;

    Handler handler=new Handler();
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            updateViewData();
            handler.postDelayed(this, 1000);
        }
    };

    private static final String ACTION = Intent.ACTION_DATE_CHANGED;
    public BroadcastReceiver yourReceiver;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(updateTimerThread, 0);
        return START_STICKY;
    }

    public void updateViewData() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context=this;

        intent=new Intent(this, VISHTECHNO_FloatWidgetService.class);
        pendingIntent=PendingIntent.getActivity(this,0,intent,0);

        theFilter = new IntentFilter();
        theFilter.addAction(ACTION);
        createNotificationChannel();
        this.registerReceiver(this.yourReceiver, theFilter);

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("speed");
            String description = ("none");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
