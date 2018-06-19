package analysis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import entity.DataBase;
import entity.OperationAnalysis;

public class Analysis {
	public static void main(String []args){
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();

		// 获取Sql查询语句
		String sql = "select * from Users";
		ResultSet rs = db.executeQuery(sql);
		try {
			while(rs.next()) {
				String username=rs.getString("username");
				OperationAnalysis oa = new OperationAnalysis(username);
				oa.run();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
