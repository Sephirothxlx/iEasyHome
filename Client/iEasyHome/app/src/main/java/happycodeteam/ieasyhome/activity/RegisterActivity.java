package happycodeteam.ieasyhome.activity;

/**
 * Created by Sephiroth on 16/7/27.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.LoginWeb;

public class RegisterActivity extends AppCompatActivity implements OnClickListener {
    // 登陆按钮
    private Button registerbutton;
    // 调试文本，注册文本
    // 显示用户名和密码
    EditText username, password;
    // 创建等待框
    private ProgressDialog dialog;
    // 返回的数据
    private ArrayList<HashMap<String,String>> info;
    // 返回主线程更新数据

    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 获取控件
        username = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        registerbutton = (Button) findViewById(R.id.register_registerbutton);
        // 设置按钮监听器
        registerbutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // 检测网络，无法检测wifi
        if (!checkNetwork()) {
            Toast toast = Toast.makeText(RegisterActivity.this, "网络未连接", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            dialog = new ProgressDialog(this);
            dialog.setTitle("提示");
            dialog.setMessage("正在注册，请稍后...");
            dialog.setCancelable(true);
            dialog.show();
            new Thread(new MyThread()).start();
        }
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = LoginWeb.login(username.getText().toString(), password.getText().toString(), "Register");
            if (info.get(0).get("username").equals("")) {

            } else {
                Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                mainActivity.putExtra("username", username.getText().toString());
                mainActivity.putExtra("password", password.getText().toString());
                startActivity(mainActivity);
            }
        }
    }

    // 检测网络
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
