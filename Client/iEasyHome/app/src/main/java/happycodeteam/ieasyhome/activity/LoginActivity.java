package happycodeteam.ieasyhome.activity;

/**
 * Created by Sephiroth on 16/7/27.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import happycodeteam.ieasyhome.web.HardwareWeb;
import happycodeteam.ieasyhome.web.IPWeb;
import happycodeteam.ieasyhome.web.LoginWeb;

public class LoginActivity extends AppCompatActivity implements OnClickListener {

    // 登陆按钮
    private Button loginbutton;
    private Button registbutton;
    // 调试文本，注册文本
    // 显示用户名和密码
    private EditText username, password;
    // 创建等待框
    private ProgressDialog dialog;
    private ArrayList<HashMap<String, String>> info;
    private ArrayList<HashMap<String, String>> data0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 获取控件
        username = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        loginbutton = (Button) findViewById(R.id.login_loginbutton);
        registbutton = (Button) findViewById(R.id.login_registerbutton);
        // 设置按钮监听器
        loginbutton.setOnClickListener(this);
        registbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_loginbutton:
                // 检测网络，无法检测wifi
                if (!checkNetwork()) {
                    Toast toast = Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    break;
                }
                // 提示框
                dialog = new ProgressDialog(this);
                dialog.setTitle("提示");
                dialog.setMessage("正在登陆，请稍后...");
                dialog.setCancelable(true);
                dialog.show();
                // 创建子线程，分别进行Get和Post传输
                MyThread1 th = new MyThread1();
                th.start();
                while (!th.getIsDone()) {

                }
                new Thread(new MyThread0()).start();
                break;
            case R.id.login_registerbutton:
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
                break;
        }
        ;
    }

    public class MyThread0 implements Runnable {
        @Override
        public void run() {
            info = LoginWeb.login(username.getText().toString(), password.getText().toString(), "Login");
            if (info.get(0).get("username").equals("")) {
                dialog.setTitle("提示");
                dialog.setMessage("登陆失败");
                dialog.setCancelable(true);
                dialog.show();
            } else {
                ArrayList<HashMap<String, String>> al = getData();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username.getText().toString());
                intent.putExtra("hardwarelist", al);
                startActivity(intent);
            }
        }
    }

    public class MyThread1 extends Thread {
        private boolean isDone = false;

        @Override
        public void run() {
            data0 = HardwareWeb.getHardware(username.getText().toString());
            isDone = true;
        }

        public boolean getIsDone() {
            return this.isDone;
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

    public ArrayList<HashMap<String, String>> getData() {
        return this.data0;
    }
}
