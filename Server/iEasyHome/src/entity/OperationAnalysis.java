package entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class OperationAnalysis implements Runnable {
	private String username;

	public OperationAnalysis() {

	}

	public OperationAnalysis(String username) {
		this.username = username;

	}

	@Override
	public void run() {
		while (true) {
			try {
				analyzeOperations();
				// getOperation();
			} catch (Exception e) {

			}
			try {
				// sleep one day.
				Thread.sleep(86400000);

			} catch (Exception e) {

			}

		}
	}

	public void getOperation() {
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();
		// 获取Sql查询语句
		String sql = "select * from Hardware where username ='" + username + "'";
		ResultSet rs = db.executeQuery(sql);

		try {
			while (rs.next()) {
				String hardware_id = rs.getString("hardware_id");
				String hardware_name = rs.getString("hardware_name");

				// 格式是 FD_Edison.local
				String hub_address = rs.getString("hub_address");

				// 格式是 edison_hub.log
				String log_name = rs.getString("log_name");

				// cmd是命令行命令，需要用到hub_address 将log拷贝并存入./下面
				// cmd不要用IP，用hub_address和log_name
				String cmd = "scp root@" + hub_address + ":/home/root/intel-iot-services-orchestration-layer/"
						+ log_name + " ./";

				Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
				Process p = run.exec(cmd);// 启动另一个进程来执行命令

				readLog(hardware_id, hardware_name, log_name);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.closeDB();
	}

	public void readLog(String hardware_id, String hardware_name, String log_name) {
		try {
			String encoding = "GBK";

			String filePath = "/Users/Sephiroth/" + log_name;

			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);

				String line = null;

				// time的格式是mysql的timestamp 类似2016-09-13 22:34:57 需要日期和时间
				// 如果不好做，就开的时候统一一下也可
				String time = "";

				// operation只有两个值 on和off
				String operation = "";

				while ((line = bufferedReader.readLine()) != null) {
					time = "";
					operation = "";
					// 使用传入的参数hardware_name
					// 当开头是[hardware_name]:的时候。就读取这一行
					// 并记录time和operation
					// 暂时不用处理温度
					// 然后存入数据库
					if (!line.contains("LED") && !line.contains("TEMPERATURE"))
						continue;
					String[] tokens = line.split(" ");
					if (line.contains("LED")) {
						time = tokens[2];
						operation = tokens[1];
						Calendar c = Calendar.getInstance();
						time = c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1) + "-" + c.get(c.DATE) + " " + time;
						DataBase db = new DataBase();
						db.initDB();
						db.connectDB();
						if (operation.equals("true")) {
							operation = "on";
						} else if (operation.equals("false")) {
							operation = "off";
						}
						// 获取Sql查询语句
						String sql = "insert into Operation(username,hardware_id,time,operation) values('" + username
								+ "','" + hardware_id + "','" + time + "','" + operation + "')";
						db.executeUpdate(sql);
						db.closeDB();
					} else if (line.contains("TEMPERATURE")) {
						// operation = tokens[1];
					}
					// 开头不对，直接跳过！
				}

				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

	}

	public void analyzeOperations() {
		DataBase db = new DataBase();
		db.initDB();
		db.connectDB();

		// get operation record within 5 days
		Operation[] operations = Operation.readOperations(username);
		ArrayList<ArrayList<Operation>> table = new ArrayList();
		// classify operations according to id_device and operation_type
		table.add(new ArrayList<Operation>());
		if (operations.length > 0)
			table.get(0).add(operations[0]);
		for (int i = 1; i < operations.length; i++) {
			Timestamp time = operations[i].getTime();
			int hardware_id = operations[i].getHdId();
			String operation = operations[i].getOp();
			int index = table.size();
			for (int j = 0; j < table.size(); j++) {
				if (table.get(j).size() > 0) {
					if (table.get(j).get(0).getHdId() == hardware_id && operation.equals(table.get(j).get(0).getOp())) {
						index = j;
						break;
					}
				} else {
					index = j;
					break;
				}
			}
			if (index >= table.size()) {
				table.add(new ArrayList<Operation>());

			}
			table.get(index).add(operations[i]);
		}

		for (int i = 0; i < table.size(); i++) {
			// for each device & operation
			ArrayList<Operation> ops = table.get(i);
			int n = ops.size();
			// Timestamp sum = new Timestamp(0);

			// TODO HERE!
			Set[] sets = classify(ops);
			int len = sets.length;
			int[] center_index = new int[len];
			for (int j = 0; j < len; j++) {
				// -1 for unclustered
				center_index[j] = -1;
			}

			int threshold_minute = 30;
			int threshold_num = 4;
			int index = 0; // record the index of cluster which is processing
			ArrayList<Integer> clusters = new ArrayList();
			for (int j = 0; j < len; j++) { // add to a new cluster
				if (center_index[j] != -1)
					continue;
				// else == -1 the first unclustered set
				center_index[j] = index;
				clusters.add(j);
				index++;
				for (int k = j + 1; k < len; k++) { // scan following sets, add
													// to this cluster
					if (center_index[k] == -1) { // set k belongs to no cluster
						if (sets[j].start.diff(sets[k].start) <= 30) {
							center_index[k] = center_index[j];
							sets[j].num += sets[k].num;
						}
					}
				}
			}
			// after clustering, judge whether num > threshold
			for (int j = 0; j < clusters.size(); j++) {
				if (sets[clusters.get(j)].num > threshold_num) {
					// make a new rule
					Set s = sets[clusters.get(j)];

					String service_name = "default";
					String temperature = "26";
					String time = s.start.hour + ":" + s.start.minute;
					int hardware_id = ops.get(0).getHdId();
					String hardware_name = "";
					String hardware_type = "";

					ResultSet r = db.executeQuery("select *from Hardware where hardware_id='" + hardware_id + "'");

					try {
						if (r.next()) {
							hardware_name = r.getString("hardware_name");
							hardware_type = r.getString("hardware_type");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					String operation = ops.get(0).getOp();

					String insert = "INSERT INTO `Service` (`username`,`service_name`, `temperature`, `time`, `hardware_id`,`hardware_name`,`hardware_type`, `operation`,`validation`) VALUES"
							+ " ('" + username + "','" + service_name + "', '" + temperature + "', '" + time + "', '"
							+ hardware_id + "', '" + hardware_name + "', '" + hardware_type + "', '" + operation + "','"
							+ "1')";
					db.executeUpdate(insert);
				}

			}
		}
	}

	public Set[] classify(ArrayList<Operation> list) {
		// TODO
		ArrayList<Set> sets = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			int index = sets.size();
			int hour = list.get(i).getTime().getHours();
			int minute = list.get(i).getTime().getMinutes();
			minute -= minute % 10;
			for (int j = 0; j < sets.size(); j++) {
				Timer t = sets.get(j).start;
				if (t.equal(hour, minute)) {
					// add to this set
					index = j;
					break;
				}
			}
			if (index == sets.size()) {
				Set s = new Set(new Timer(hour, minute));
				s.index = index;
				s.num = 1;
				sets.add(s);
			} else {
				Set s = sets.get(index);
				s.num = s.num + 1;
			}
		}
		Set[] ret = new Set[sets.size()];
		ret = sets.toArray(ret);
		// sort sets by num in decending order
		int len = ret.length;
		for (int i = 0; i < len; i++) {
			int max = i;
			for (int j = i + 1; j < len; j++) {
				if (ret[j].num > ret[max].num) {
					max = j;
				}
			}
			// exchange max with i
			if (max != i) {
				Set tmp = ret[i];
				ret[i] = ret[max];
				ret[max] = tmp;
			}
		}

		return ret;
	}

	class Timer {
		public int hour;
		public int minute;

		public Timer(int hour, int minute) {
			this.hour = hour;
			this.minute = minute;
		}

		public boolean equal(Timer time) {
			return (this.hour == time.hour && this.minute == time.minute);
		}

		public boolean equal(int hour, int minute) {
			return (this.hour == hour && this.minute == minute);
		}

		public int diff(Timer t) {
			// return absolute value
			int sum_t = t.hour * 60 + t.minute;
			int sum = hour * 60 + minute;
			return (sum > sum_t) ? sum - sum_t : sum_t - sum;
		}
	}

	class Set {
		public Timer start;
		public int index;
		public int num;

		public Set(Timer t) {
			start = t;
		}
	}
}
