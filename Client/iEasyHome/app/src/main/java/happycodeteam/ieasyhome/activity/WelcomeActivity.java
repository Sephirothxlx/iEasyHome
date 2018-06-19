package happycodeteam.ieasyhome.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.app.Activity;

import happycodeteam.ieasyhome.R;

/**
 * Created by Sephiroth on 16/9/16.
 */
public class WelcomeActivity extends Activity {

    private final long SPLASH_LENGTH = 2000;
    Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.postDelayed(new Runnable() {  //使用handler的postDelayed实现延时跳转

            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);//2秒后跳转至应用LoginActivity

    }
}
