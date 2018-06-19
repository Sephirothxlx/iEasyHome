package happycodeteam.ieasyhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.view.View;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.HardwareWeb;

/**
 * Created by Sephiroth on 16/9/16.
 */
public class WebViewActivity extends Activity {
    private WebView wv;
    private Button b0;
    private ArrayList<HashMap<String, String>> data0;
    private String username;
    private String hub_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        hub_address = bundle.getString("hub_address");

        setContentView(R.layout.activity_webview);

        b0 = (Button) findViewById(R.id.webview_housebutton);
        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyThread0 th = new MyThread0();
                th.start();
                while (!th.getIsDone()) {

                }

                Intent intent = new Intent(WebViewActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("hardwarelist", data0);
                startActivity(intent);
            }
        });

        wv = (WebView) findViewById(R.id.webview_web);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        WebSettings webSettings = wv.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        wv.loadUrl("http://192.168.1.102:3000");
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {

                } else {

                }
            }
        });
        //当网页上的连接被点击的时候进行处理
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // goBack()表示返回webView的上一页面


    }


    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (keyCoder == KeyEvent.KEYCODE_BACK) {
            wv.goBack();
            return true;
        } else {
            finish();

        }
        return super.onKeyDown(keyCoder, event);
    }

    public class MyThread0 extends Thread {
        private boolean isDone = false;

        @Override
        public void run() {
            data0 = HardwareWeb.getHardware(username);
            isDone = true;
        }

        public boolean getIsDone() {
            return this.isDone;
        }
    }
}

