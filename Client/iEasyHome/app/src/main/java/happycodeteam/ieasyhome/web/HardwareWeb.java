package happycodeteam.ieasyhome.web;

/**
 * Created by Sephiroth on 16/7/27.
 */


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.*;

public class HardwareWeb {

    private static String IP = IPWeb.IP;

    // 通过Get方式获取HTTP服务器数据
    public static  ArrayList<HashMap<String,String>> getHardware(String username) {

        ArrayList<HashMap<String,String>> list=new ArrayList();
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            // 用户名 密码
            // URL 地址
            String path = "http://" + IP + "/iEasyHome/GetHardware";
            path = path + "?username=" + username ;

            conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(3000); // 设置超时时间
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            conn.setRequestMethod("GET"); // 设置获取信息方式
            conn.setRequestProperty("Charset", "UTF-8"); // 设置接收数据编码格式
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                String responseData=parseInfo(is);
                //转换成json数据处理
                JSONArray jsonArray=new JSONArray(responseData);
                for(int i=0;i<jsonArray.length();i++){       //一个循环代表一个对象
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap hm=new HashMap();
                    hm.put("username", jsonObject.getString("username"));
                    hm.put("hardware_id", jsonObject.getString("hardware_id"));
                    hm.put("hardware_name", jsonObject.getString("hardware_name"));
                    hm.put("hardware_type", jsonObject.getString("hardware_type"));
                    hm.put("hub_address", jsonObject.getString("hub_address"));
                    hm.put("log_name", jsonObject.getString("log_name"));
                    list.add(hm);
                }
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 意外退出时进行连接关闭保护
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    // 将输入流转化为 String 型
    private static String parseInfo(InputStream inStream) throws Exception {
        byte[] data = read(inStream);
        // 转化为字符串
        return new String(data, "UTF-8");
    }

    // 将输入流转化为byte型
    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
