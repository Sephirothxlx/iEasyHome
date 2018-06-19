package happycodeteam.ieasyhome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.IPWeb;

/**
 * Created by Sephiroth on 16/9/18.
 */
public class SettingActivity extends AppCompatActivity {
    private EditText IP;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 获取控件
        IP = (EditText) findViewById(R.id.setting_IP);
        b = (Button) findViewById(R.id.setting_button0);


        // 设置按钮监听器
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                IPWeb.IP=IP.getText().toString()+":8080";
                startActivity(intent);
            }
        });
    }


}
