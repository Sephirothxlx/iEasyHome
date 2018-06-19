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
 * Servlet implementation class DeleteHardware
 */
@WebServlet("/DeleteHardware")
public class DeleteHardware extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteHardware() {
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
		String hardware_id = request.getParameter("hardware_id");
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();
		// 获取Sql查询语句
		String sql0 = "delete from Hardware where hardware_id ='" + hardware_id + "'";
		db.executeUpdate(sql0);
		
		String sql1 = "delete from Service where hardware_id ='" + hardware_id + "'";
		db.executeUpdate(sql1);

		// 获取Sql查询语句
		String sql = "select * from Hardware where username ='" + username + "'";
		ResultSet rs = db.executeQuery(sql);
		JSONArray jsonArray = new JSONArray();
		try {
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("username", rs.getString("username"));
				jsonObject.put("hardware_id", rs.getString("hardware_id"));
				jsonObject.put("hub_address", rs.getString("hub_address"));
				jsonObject.put("hardware_name", rs.getString("hardware_name"));
				jsonObject.put("hardware_type", rs.getString("hardware_type"));
				jsonObject.put("log_name", rs.getString("log_name"));
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
