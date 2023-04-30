package vishtechno.bkm.quickscreenshotcapture;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import vishtechno.bkm.quickscreenshotcapture.R;

public class VISHTECHNO_Privacy_policy extends AppCompatActivity {

    WebView wv;
    Context context;
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vishtechno_activity_privacy_policy);

        getWindow().setFlags(1024,1024);

        context = VISHTECHNO_Privacy_policy.this;

        pbar = findViewById(R.id.progress_bar);
        wv = findViewById(R.id.wv_privacy_policy);

        WebSettings settings = wv.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else
        {
            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        init();
    }

    public void init()
    {

        wv.loadUrl("https://docs.google.com/document/d/e/2PACX-1vThYsCZvmm9jtqhtCnWtKewUuEByvqOKOGzd_Xqoyzd7mqPB8uUOvcoJ6jOLKbV71aNWqr5nYsbodiK/pub");
        wv.requestFocus();
        pbar.setVisibility(View.VISIBLE);
        wv.setWebViewClient(new WebViewClient()
        {

            public void onPageFinished(WebView view, String url)
            {
                try
                {
                    pbar.setVisibility(View.GONE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        finish();

    }

}
