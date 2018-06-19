package happycodeteam.ieasyhome.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.HardwareWeb;

import android.view.View.OnClickListener;

public class ServiceActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private ArrayList<HashMap<String, String>> data0;
    private ArrayList<HashMap<String, String>> data1;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");


        ListView listView = (ListView) this.findViewById(R.id.service_listview);
        Button button = (Button) this.findViewById(R.id.service_button);
        //获取到集合数据
        data0 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("servicelist");
        if (data0 != null) {
            //创建SimpleAdapter适配器将数据绑定到item显示控件上
            SimpleAdapter adapter = new SimpleAdapter(this, data0, R.layout.service_item, new String[]{"service_name", "hardware_name"}, new int[]{R.id.service_servicename, R.id.service_hardwarename});
            //实现列表的显示
            listView.setAdapter(adapter);
            //条目点击事件
            listView.setOnItemClickListener(new ItemClickListener());
        }

        button.setOnClickListener(new AddServiceListener());

        MyThread0 th=new MyThread0();
        th.start();
        while(!th.getIsDone()){

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ServiceActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("hardwarelist",data1);
        startActivity(intent);
    }


    //获取点击事件
    public class ItemClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ServiceActivity.this, ConfigureServiceActivity.class);
            ArrayList al0 = getData0();
            ArrayList al1 = getData1();
            intent.putExtra("type", "0");
            intent.putExtra("servicelist", al0);
            intent.putExtra("hardwarelist", al1);
            intent.putExtra("index", (int) id);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public class MyThread0 extends Thread {
        private boolean isDone = false;

        @Override
        public void run() {
            data1 = HardwareWeb.getHardware(username);
            isDone = true;
        }

        public boolean getIsDone() {
            return this.isDone;
        }
    }

    public class AddServiceListener implements OnClickListener {
        public void onClick(View v) {
            ArrayList al0 = getData0();
            if (al0 != null) {
                Intent intent = new Intent(ServiceActivity.this, ConfigureServiceActivity.class);
                intent.putExtra("hardwarelist", al0);
                intent.putExtra("type", "1");
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                dialog=new AlertDialog.Builder(ServiceActivity.this)
                        .setPositiveButton("确定",new positiveListner())
                        .create();
                dialog.setTitle("提示");
                dialog.setMessage("硬件列表为空，请先添加硬件！");
                dialog.show();
            }
        }
    }

    public class positiveListner implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public ArrayList<HashMap<String, String>> getData0() {
        return data0;
    }

    public ArrayList<HashMap<String, String>> getData1() {
        return data1;
    }
}

