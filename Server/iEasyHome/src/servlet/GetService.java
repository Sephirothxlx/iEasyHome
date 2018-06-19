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
 * Servlet implementation class Service
 */
@WebServlet("/GetService")
public class GetService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String username = request.getParameter("username");
		username = new String(username.getBytes("ISO-8859-1"), "UTF-8");
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();
		
		// 获取Sql查询语句
		String sql = "select * from Service where username ='" + username + "'";
		ResultSet rs = db.executeQuery(sql);
		JSONArray jsonArray = new JSONArray(); 
		try {
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();  
				jsonObject.put("service_id", rs.getString("service_id"));  
				jsonObject.put("username", rs.getString("username"));  
		        jsonObject.put("service_name", rs.getString("service_name")); 
		        jsonObject.put("hardware_id", rs.getString("hardware_id")); 
		        jsonObject.put("hardware_name", rs.getString("hardware_name")); 
		        jsonObject.put("hardware_type",rs.getString("hardware_type"));  
		        jsonObject.put("temperature", rs.getString("temperature"));  
		        jsonObject.put("time", rs.getString("time"));  
		        jsonObject.put("operation", rs.getString("operation"));    
		        jsonObject.put("validation", rs.getString("validation"));  
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
