package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.DataBase;
import entity.Service;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class ServiceCheck
 */
@WebServlet("/ServiceCheck")
public class ServiceCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceCheck() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username");
		int signal = 1;
		try {
			Service[] services = Service.readService(username);
			for (int j = 0; j < services.length; j++) {
				boolean exe0 = true;
				boolean exe1 = false;

				int service_id = services[j].getSeId();
				int hardware_id = services[j].getHdId();
				String service_name = services[j].getSeName();
				String hardware_name = services[j].getHdName();
				String operation = services[j].getOp();

				if (services[j].getTem() != null) {
					// 读取传感器的环境温度
				} else
					exe0 = true;
				if (services[j].getTime() != null) {
					String time = services[j].getTime();
					String[] temp = time.split(":");
					int s_hour = Integer.parseInt(temp[0]);
					int s_minute = Integer.parseInt(temp[1]);
					Calendar rightNow = Calendar.getInstance();
					int c_hour = rightNow.get(Calendar.HOUR_OF_DAY);
					int c_minute = rightNow.get(Calendar.MINUTE);
					if (s_hour == c_hour && c_minute - s_minute >= 0 && c_minute - s_minute <= 30)
						exe1 = true;
				} else
					exe1 = true;
				if (exe0 && exe1) {
					String sql0 = "UPDATE Service SET `validation`=0 WHERE `Service_id`='" + service_id + "'";
					String sql1 = "select * from Hardware where hardware_id ='" + hardware_id + "'";
					DataBase db = new DataBase();
					db.initDB();
					db.connectDB();
					db.executeUpdate(sql0);
					ResultSet rs = db.executeQuery(sql1);
					String hub_address="";
					if(rs.next()){
						hub_address=rs.getString("hub_address");
					}
					JSONArray jsonArray = new JSONArray();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("service_name", service_name);
					jsonObject.put("hardware_id", hardware_id);
					jsonObject.put("hardware_name", hardware_name);
					jsonObject.put("hub_address", hub_address);
					jsonObject.put("operation", operation);
					jsonArray.add(jsonObject);
					PrintWriter out = response.getWriter();
					out.write(jsonArray.toString());
					signal = 0;
					db.closeDB();
					break;
				} else {
					signal = 1;
				}
			}
		} catch (Exception e) {

		}
		if (signal == 1) {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("service_name", "");
			jsonObject.put("hardware_id", "");
			jsonObject.put("hub_address", "");
			jsonObject.put("hardware_name", "");
			jsonObject.put("operation", "");
			jsonArray.add(jsonObject);
			PrintWriter out = response.getWriter();
			out.write(jsonArray.toString());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
