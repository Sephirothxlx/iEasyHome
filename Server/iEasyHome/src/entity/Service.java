package entity;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Service {
	private int service_id;
	private int hardware_id;
	private String service_name;
	private String hardware_name;
	private String hardware_type;
	private String temperature;
	private String time;
	private String operation;
	private int validation;

	public int getSeId(){
		return this.service_id;
	}
	
	public int getHdId(){
		return this.hardware_id;
	}
	
	public String getSeName(){
		return this.service_name;
	}
	
	public String getHdName(){
		return this.hardware_name;
	}
	
	public String getTem(){
		return this.temperature;
	}
	public String getTime(){
		return this.time;
	}
	public String getOp(){
		return this.operation;
	}
	
	public int getVd(){
		return this.validation;
	}
	
	public static Service[] readService(String username) {
		ArrayList<Service> services = new ArrayList();
		try {
			String sql = "select * from Service where username='" + username
					+ "' AND validation = '1';";
			
			DataBase db = new DataBase();
			db.initDB();
			db.connectDB();
			
			ResultSet rs = db.executeQuery(sql);
			
			while (rs.next()) {
				Service service = new Service();
				service.service_id=rs.getInt("service_id");
				service.hardware_id=rs.getInt("hardware_id");
				service.service_name=rs.getString("service_name");
				service.hardware_name=rs.getString("hardware_name");
				service.hardware_type=rs.getString("hardware_type");
				service.temperature=rs.getString("temperature");
				service.time=rs.getString("time");
				service.operation=rs.getString("operation");
				service.validation=rs.getInt("validation");
				services.add(service);
			}
			rs.close();
		} catch (Exception e) {
			
		}
		Service[] ret = new Service[services.size()];
		return (Service[])services.toArray(ret);
	}

}

