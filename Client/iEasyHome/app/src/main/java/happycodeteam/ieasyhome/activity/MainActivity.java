package happycodeteam.ieasyhome.activity;

import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.os.Message;
import android.os.Handler;
import android.os.Looper;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.HardwareWeb;
import happycodeteam.ieasyhome.web.ServiceWeb;
import happycodeteam.ieasyhome.web.PullWeb;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MyHandler myHandler;

    private ArrayList<HashMap<String, String>> data0;
    private ArrayList<HashMap<String, String>> info;
    private String username;
    private String hub_address;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        ListView listView = (ListView) this.findViewById(R.id.content_listview);
        data0 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("hardwarelist");
        if (data0 != null) {
            //创建SimpleAdapter适配器将数据绑定到item显示控件上
            SimpleAdapter adapter = new SimpleAdapter(this, data0, R.layout.main_item, new String[]{"hardware_name", "hardware_type"}, new int[]{R.id.main_hardwarename, R.id.main_hardwaretype});
            //实现列表的显示
            listView.setAdapter(adapter);
            //条目点击事件
            listView.setOnItemClickListener(new ItemClickListener());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav);
        navigationView.setNavigationItemSelectedListener(this);

        myHandler = new MyHandler();

        new Thread(new PullMessage()).start();

    }

    public class ItemClickListener implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<HashMap<String, String>> al = getData();
            String hub_address=al.get((int)id).get("hub_address");
            Intent intent=new Intent(MainActivity.this,WebViewActivity.class);
            intent.putExtra("username",username);
            intent.putExtra("hub_address",hub_address);
            startActivity(intent);

        }
    }

    public ArrayList<HashMap<String, String>> getData() {
        return data0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hardware) {
            new Thread(new GetHardware()).start();
        } else if (id == R.id.nav_service) {
            new Thread(new GetService()).start();
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法，接受数据
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            //获取传递的数据
            Bundle data = msg.getData();
            String service_name = data.getString("service_name");
            String hardware_name = data.getString("hardware_name");
            String operation = data.getString("operation");
            hub_address=data.getString("hub_address");
            //处理UI更新等操作
            dialog = new AlertDialog.Builder(MainActivity.this)
                    .setPositiveButton("确定", new positiveListner())
                    .setNegativeButton("取消", new negativeListner())
                    .create();
            dialog.setTitle("服务提示");
            dialog.setMessage("根据您的服务：" + service_name + "，是否要对硬件：" + hardware_name + " 执行操作："+operation+"？");
            dialog.show();
        }
    }

    public class GetService implements Runnable {
        @Override
        public void run() {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String username = bundle.getString("username");
            info = ServiceWeb.getService(username);
            intent = new Intent(MainActivity.this, ServiceActivity.class);
            intent.putExtra("servicelist", info);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public class GetHardware implements Runnable {
        @Override
        public void run() {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String username = bundle.getString("username");
            info = HardwareWeb.getHardware(username);
            intent = new Intent(MainActivity.this, HardwareActivity.class);
            intent.putExtra("hardwarelist", (Serializable) info);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public class positiveListner implements OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Intent intent  = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("hub_address", hub_address);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }

    public class negativeListner implements OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    public class PullMessage implements Runnable {
        @Override
        public void run() {
            PullWeb p = new PullWeb();
            ArrayList<HashMap<String, String>> info = null;
            while (true) {
                try {
                    // sleep 20 seconds.
                    Thread.sleep(20000);
                } catch (Exception e) {

                }

                try {
                    info = p.pull(username);
                } catch (Exception e) {

                }

                if (info != null && !info.get(0).get("service_name").equals("")) {
                    String service_name = info.get(0).get("service_name");
                    String hub_address = info.get(0).get("hub_address");
                    String hardware_name = info.get(0).get("hardware_name");
                    String operation = info.get(0).get("operation");

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("service_name", service_name);
                    data.putString("hub_address", hub_address);
                    data.putString("hardware_name", hardware_name);
                    data.putString("operation", operation);
                    msg.setData(data);

                    myHandler.sendMessage(msg);
                }

            }

        }
    }
}
