package happycodeteam.ieasyhome.activity;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;
import android.os.Bundle;
import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.ConfigureHardwareWeb;
import happycodeteam.ieasyhome.web.DeleteHardwareWeb;

public class ConfigureHardwareActivity extends AppCompatActivity{

    ArrayList<HashMap<String, String>> al;
    private String type;

    private String username;

    private EditText editText0;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private Button button0;
    private Button button1;

    private String hardware_id;
    private String hardware_name;
    private String hardware_type;
    private String hub_address;
    private String log_name;

    private ArrayList<HashMap> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurehardware);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        type=bundle.getString("type");

        editText0 = (EditText) findViewById(R.id.configurehardware_hardwarename);
        editText1 = (EditText) findViewById(R.id.configurehardware_hardwaretype);
        editText2 = (EditText) findViewById(R.id.configurehardware_hubaddress);
        editText3 = (EditText) findViewById(R.id.configurehardware_logname);
        button0 = (Button) findViewById(R.id.configurehardware_button0);
        button1 = (Button) findViewById(R.id.configurehardware_button1);

        al = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("hardwarelist");
        if (al == null || al.isEmpty()) {
            hardware_id = "";
            hardware_name = "";
            hardware_type = "";
            hub_address="";
            log_name="";
        } else {
            int index = (int) getIntent().getSerializableExtra("index");
            hardware_id=al.get(index).get("hardware_id");
            hardware_name = al.get(index).get("hardware_name");
            hardware_type = al.get(index).get("hardware_type");
            hub_address = al.get(index).get("hub_address");
            log_name = al.get(index).get("log_name");
        }
        editText0.setText(hardware_name);
        editText1.setText(hardware_type);
        editText2.setText(hub_address);
        editText3.setText(log_name);

        button0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hardware_name = editText0.getText().toString();
                hardware_type = editText1.getText().toString();
                hub_address = editText2.getText().toString();
                log_name = editText3.getText().toString();
                new Thread(new MyThread0()).start();
            }
        });

        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new MyThread1()).start();
            }
        });

        if(type.equals("1")){
            button1.setEnabled(false);
        }
    }

    public class MyThread0 implements Runnable {
        @Override
        public void run() {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("username", username);
            hm.put("hardware_id", hardware_id);
            hm.put("hardware_name", hardware_name);
            hm.put("hardware_type", hardware_type);
            hm.put("hub_address", hub_address);
            hm.put("log_name", log_name);
            if (type.equals("0")) {
                info = ConfigureHardwareWeb.configureHardware(hm, "ReviseHardware");
            } else if (type.equals("1")) {
                info = ConfigureHardwareWeb.configureHardware(hm, "AddHardware");
            }
            Intent intent = new Intent(ConfigureHardwareActivity.this, HardwareActivity.class);
            intent.putExtra("hardwarelist", info);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }

    public class MyThread1 implements Runnable {
        @Override
        public void run() {
            info = DeleteHardwareWeb.deleteHardware(hardware_id,username);

            Intent intent = new Intent(ConfigureHardwareActivity.this, HardwareActivity.class);
            intent.putExtra("hardwarelist", info);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }


}
