package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.DataBase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SetService
 */
@WebServlet("/ReviseService")
public class ReviseService extends HttpServlet {

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReviseService() {
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
		username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
		String service_id = request.getParameter("service_id");
		String service_name = request.getParameter("service_name");
		String hardware_id=request.getParameter("hardware_id");
		String hardware_name=request.getParameter("hardware_name");
		String hardware_type=request.getParameter("hardware_type");
		String time = request.getParameter("time");
		String temperature = request.getParameter("temperature");
		String operation = request.getParameter("operation");
		String validation=request.getParameter("validation");
		// 新建服务对象
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();
		// 获取Sql查询语句
		String sql0 = "select * from Service where service_id='" + service_id+ "'";
		ResultSet rs = db.executeQuery(sql0);
		
		try {
			if (rs.next()) {
				String sql1 = "update Service set service_name='"+service_name+"' , hardware_id='"+hardware_id+
						"' , hardware_name='"+hardware_name+"' , hardware_type='"+hardware_type+"' , temperature='"+
						temperature+"' , time='"+time+"' , operation='"+operation+"',validation='"+validation+"' where service_id='"+service_id+"'";
		
				int n = db.executeUpdate(sql1);
				if (n == 0) {

				} else {

				}
			} else {
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获取Sql查询语句
		String s = "select * from Service where username ='" + username + "'";
		ResultSet r = db.executeQuery(s);
		JSONArray jsonArray = new JSONArray();
		try {
			while (r.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("service_id", r.getString("service_id"));  
				jsonObject.put("username", r.getString("username"));  
		        jsonObject.put("service_name", r.getString("service_name")); 
		        jsonObject.put("hardware_id", r.getString("hardware_id")); 
		        jsonObject.put("hardware_name", r.getString("hardware_name")); 
		        jsonObject.put("hardware_type",r.getString("hardware_type"));  
		        jsonObject.put("temperature", r.getString("temperature"));  
		        jsonObject.put("time", r.getString("time"));  
		        jsonObject.put("operation", r.getString("operation")); 
		        jsonObject.put("validation", r.getString("validation"));  
				jsonArray.add(jsonObject);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		out.write(jsonArray.toString());
		db.closeDB();
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
