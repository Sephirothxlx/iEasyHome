package happycodeteam.ieasyhome.activity;

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

import android.view.View.OnClickListener;


/**
 * Created by Sephiroth on 16/8/21.
 */
public class HardwareActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> data;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        ListView listView = (ListView) this.findViewById(R.id.hardware_listview);
        Button button = (Button) this.findViewById(R.id.hardware_button);
        //获取到集合数据
        data = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("hardwarelist");
        if (data != null) {
            //创建SimpleAdapter适配器将数据绑定到item显示控件上
            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.hardware_item, new String[]{"hardware_name", "hardware_type"}, new int[]{R.id.hardware_hardwarename, R.id.hardware_hardwaretype});
            //实现列表的显示
            listView.setAdapter(adapter);
            //条目点击事件
            listView.setOnItemClickListener(new ItemClickListener());
        }
        button.setOnClickListener(new AddHardwareListener());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HardwareActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("hardwarelist",data);
        startActivity(intent);
    }

    public class ItemClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<HashMap<String, String>> al = getData();
            Intent intent = new Intent(HardwareActivity.this, ConfigureHardwareActivity.class);
            intent.putExtra("type", "0");
            intent.putExtra("hardwarelist", al);
            intent.putExtra("index", (int) id);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }

    public class AddHardwareListener implements OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(HardwareActivity.this, ConfigureHardwareActivity.class);
            intent.putExtra("type", "1");
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    public ArrayList<HashMap<String, String>> getData() {
        return data;
    }
}
