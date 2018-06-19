package entity;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Operation {
	private int operation_id;
	private int hardware_id;
	private Timestamp time;
	private String temperature;
	private String operation;

	public void setOpId(int i) {
		this.operation_id = i;
	}

	public int getOpId() {
		return this.operation_id;
	}

	public void setHdId(int i) {
		this.hardware_id = i;
	}

	public int getHdId() {
		return this.hardware_id;
	}

	public void setTime(Timestamp i) {
		this.time = i;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTem(String i) {
		this.temperature = i;
	}

	public String getTem() {
		return this.temperature;
	}

	public void setOp(String i) {
		this.operation = i;
	}

	public String getOp() {
		return this.operation;
	}

	public static Operation[] readOperations(String username) {
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();

		ArrayList<Operation> ops = new ArrayList();
		int days = 5;
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String time = format.format(calendar.getTime());
			String sql = "select * from Operation where username='" + username + "' AND (to_days(" + time
					+ ")-to_days(now()) >= -" + days + ");";

			ResultSet rs = db.executeQuery(sql);
			while (rs.next()) {
				Operation op = new Operation();
				op.operation_id = rs.getInt("operation_id");
				op.hardware_id = rs.getInt("hardware_id");
//				op.temperature = rs.getString("temperature");
				op.time = rs.getTimestamp("time");
				op.operation = rs.getString("operation");
				ops.add(op);
			}

		} catch (Exception e) {

		}
		Operation[] operations = new Operation[ops.size()];
		return ops.toArray(operations);
	}
}
