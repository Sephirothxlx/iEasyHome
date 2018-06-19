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
 * Servlet implementation class AddHardware
 */
@WebServlet("/AddHardware")
public class AddHardware extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddHardware() {
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
		String hardware_name = request.getParameter("hardware_name");
		String hardware_type = request.getParameter("hardware_type");
		String hub_address = request.getParameter("hub_address");
		String log_name = request.getParameter("log_name");
		// 新建服务对象
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();
		// 获取Sql查询语句
		String sql0 = "select * from Hardware where username ='" + username + "' and  hardware_name='" + hardware_name
				+ "'";
		ResultSet rs = db.executeQuery(sql0);
		try {
			if (rs.next()) {

				// 有注册过在客户端显示信息
			} else {
				// 没注册过，添加
				String sql1 = "insert into Hardware(username,hardware_name,hardware_type,hub_address,log_name) values('"
						+ username + "','" + hardware_name + "','" + hub_address + "','" + log_name + "','"
						+ hardware_type + "')";
				int n = db.executeUpdate(sql1);
				if (n == 0) {

				} else {

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获取Sql查询语句
		String s = "select * from Hardware where username ='" + username + "'";
		ResultSet r = db.executeQuery(s);
		JSONArray jsonArray = new JSONArray();
		try {
			while (r.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("username", r.getString("username"));
				jsonObject.put("hardware_id", r.getString("hardware_id"));
				jsonObject.put("hardware_name", r.getString("hardware_name"));
				jsonObject.put("hardware_type", r.getString("hardware_type"));
				jsonObject.put("hub_address", r.getString("hub_address"));
				jsonObject.put("log_name", r.getString("log_name"));
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
