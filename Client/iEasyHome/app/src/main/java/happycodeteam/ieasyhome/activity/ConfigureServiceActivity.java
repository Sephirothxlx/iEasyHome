package happycodeteam.ieasyhome.activity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.widget.NumberPicker.*;
import android.widget.AdapterView.OnItemSelectedListener;

import happycodeteam.ieasyhome.R;
import happycodeteam.ieasyhome.web.ConfigureServiceWeb;
import happycodeteam.ieasyhome.web.DeleteServiceWeb;

public class ConfigureServiceActivity extends AppCompatActivity implements Formatter {

    ArrayList<HashMap<String, String>> al0;
    ArrayList<HashMap<String, String>> al1;

    private String username;
    private String type;

    private EditText editText0;
    private EditText editText1;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker temperaturePicker;
    private Spinner spinner0;
    private int x = 0;
    private Spinner spinner1;
    private Spinner spinner2;
    private ArrayList<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private Button button0;
    private Button button1;
    private SwitchCompat sc0;

    private String service_id;
    private String service_name;
    private String hardware_id;
    private String hardware_name;
    private String hardware_type;
    private String time;
    private String temperature;
    private String operation;
    private String validation;

    private int hour;
    private int minute;
    private String temperature_type;
    private int temperature_number;

    private ArrayList<HashMap> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configureservice);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        type = bundle.getString("type");

        editText0 = (EditText) findViewById(R.id.configureservice_servicename);
        editText1 = (EditText) findViewById(R.id.configureservice_hardwaretype);
        hourPicker = (NumberPicker) findViewById(R.id.configureservice_hourpicker);
        minutePicker = (NumberPicker) findViewById(R.id.configureservice_minpicker);
        temperaturePicker = (NumberPicker) findViewById(R.id.configureservice_temppicker);
        spinner0 = (Spinner) findViewById(R.id.configureservice_spinner0);
        spinner1 = (Spinner) findViewById(R.id.configureservice_spinner1);
        spinner2 = (Spinner) findViewById(R.id.configureservice_spinner2);
        button0 = (Button) findViewById(R.id.configureservice_button0);
        button1 = (Button) findViewById(R.id.configureservice_button1);
        sc0 = (SwitchCompat) findViewById(R.id.configureservice_switchcompat);

        data_list = new ArrayList<String>();
        data_list.add(">");
        data_list.add("<");

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner1.setAdapter(arr_adapter);

        //数据
        data_list = new ArrayList<String>();
        data_list.add("on");
        data_list.add("off");

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner2.setAdapter(arr_adapter);

        al0 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("servicelist");
        if (al0 == null || al0.isEmpty()) {
            service_name = "";
            hardware_name = "";
            hardware_type = "";
            time = "00:00";
            temperature = ">26";
            operation = "on";
            validation = "0";
        } else {
            int index = (int) getIntent().getSerializableExtra("index");
            service_id = al0.get(index).get("service_id");
            service_name = al0.get(index).get("service_name");
            hardware_name = al0.get(index).get("hardware_name");
            hardware_type = al0.get(index).get("hardware_type");
            time = al0.get(index).get("time");
            temperature = al0.get(index).get("temperature");
            operation = al0.get(index).get("operation");
            validation = al0.get(0).get("validation");

        }

        al1 = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("hardwarelist");
        if (al1 != null) {
            data_list = new ArrayList<String>();
            for (int i = 0; i < al1.size(); i++) {
                String temp = al1.get(i).get("hardware_name");
                data_list.add(temp);
                if (hardware_name.equals(temp))
                    x = i;
            }
            //适配器
            arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner0.setAdapter(arr_adapter);
            spinner0.setOnItemSelectedListener(new SpinnerListener());
        }


        String[] s = time.split(":");
        hour = Integer.parseInt(s[0]);
        minute = Integer.parseInt(s[1]);
        temperature_type = temperature.substring(0, 1);
        temperature_number = Integer.parseInt(temperature.substring(1, temperature.length()));

        button0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                service_name = editText0.getText().toString();
                hardware_name = spinner0.getSelectedItem().toString();
                hardware_type = editText1.getText().toString();
                time = hourPicker.getValue() + ":" + minutePicker.getValue();
                temperature = spinner1.getSelectedItem().toString() + temperaturePicker.getValue();
                operation = spinner2.getSelectedItem().toString();
                if (sc0.isChecked()) {
                    validation = "1";
                } else {
                    validation = "0";
                }
                for (int i = 0; i < al1.size(); i++) {
                    String temp = al1.get(i).get("hardware_name");
                    if (hardware_name.equals(temp))
                        hardware_id = al1.get(i).get("hardware_id");
                }

                new Thread(new MyThread0()).start();
            }
        });

        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new MyThread1()).start();
            }
        });

        if (type.equals("1")) {
            button1.setEnabled(false);
        }
        init();
    }

    private void init() {
        editText0.setText(service_name);
        editText1.setEnabled(false);

        hourPicker.setFormatter(this);
        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        hourPicker.setValue(hour);

        minutePicker.setFormatter(this);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);
        minutePicker.setValue(minute);

        spinner0.setSelection(x);
        editText1.setText(al1.get(x).get("hardware_type"));

        if (temperature_type.equals(">"))
            spinner1.setSelection(0);
        else
            spinner1.setSelection(1);

        temperaturePicker.setFormatter(this);
        temperaturePicker.setMaxValue(30);
        temperaturePicker.setMinValue(0);
        temperaturePicker.setValue(temperature_number);

        if (operation.equals("on"))
            spinner2.setSelection(0);
        else
            spinner2.setSelection(1);

        if (validation.equals("0")) {
            sc0.setChecked(false);
        } else if (validation.equals("1")) {
            sc0.setChecked(true);
        }
    }

    private class SpinnerListener implements OnItemSelectedListener {//下拉列表的的事件响应

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            editText1.setText(al1.get(arg2).get("hardware_type"));
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }


    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public class MyThread0 implements Runnable {
        @Override
        public void run() {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("username", username);
            hm.put("service_id", service_id);
            hm.put("service_name", service_name);
            hm.put("hardware_id", hardware_id);
            hm.put("hardware_name", hardware_name);
            hm.put("hardware_type", hardware_type);
            hm.put("temperature", temperature);
            hm.put("time", time);
            hm.put("operation", operation);
            hm.put("validation", validation);
            if (type.equals("0")) {
                info = ConfigureServiceWeb.configureService(hm, "ReviseService");
            } else if (type.equals("1")) {
                info = ConfigureServiceWeb.configureService(hm, "AddService");
            }
            Intent intent = new Intent(ConfigureServiceActivity.this, ServiceActivity.class);
            intent.putExtra("servicelist", (Serializable) info);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }

    public class MyThread1 implements Runnable {
        @Override
        public void run() {
            info = DeleteServiceWeb.deleteService(service_id, username);

            Intent intent = new Intent(ConfigureServiceActivity.this, ServiceActivity.class);
            intent.putExtra("servicelist", (Serializable) info);
            intent.putExtra("username", username);
            startActivity(intent);

        }
    }


}
