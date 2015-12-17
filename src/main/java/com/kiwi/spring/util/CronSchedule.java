package com.kiwi.spring.util;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobDetail;

public class CronSchedule implements Job {

	static int i = 0;
	Properties prop = new Properties();
	InputStream input = null;

	public static void main(String args[]) {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			JobDetail jd = new JobDetail("job1", "group1", CronSchedule.class);
			CronTrigger ct = new CronTrigger("cronTrigger", "group2",
					"0 0/30 * * * ?");

			sched.scheduleJob(jd, ct);
			sched.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Working........" + i + " " + new Date());
		dbConnect(i++);
	}

	public void dbConnect(int i) {

		String refresh_token = null;
		String response = null;
		String accessToken = null;
		String refreshToken = null;
		int id = 0;
		Connection connect = null;
		Statement statement = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();

		try {
			String rootPath = new File("").getAbsolutePath();
			String filepath = rootPath + "/src/main/resources/";
			input = new FileInputStream(filepath + "app.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			String dbUrl = prop.getProperty("database.url");

			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(dbUrl);

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from box_data");

			while (resultSet.next()) {
				refresh_token = resultSet.getString("refresh_token");
				id = resultSet.getInt("id");
				response = getAccessTokenUsingBoxAPI(refresh_token);

				Object obj = parser.parse(response.toString());
				json = (JSONObject) obj;

				accessToken = (String) json.get("access_token");
				refreshToken = (String) json.get("refresh_token");

				String timestamp = String.valueOf(System
						.currentTimeMillis());
				
				String sql = "UPDATE box_data SET access_token='" + accessToken
						+ "', refresh_token='" + refreshToken + "', updated_date='" + timestamp + "' WHERE id = " + id;
				
				stmt = connect.createStatement();
				stmt.executeUpdate(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
			try {
				if (connect != null)
					connect.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	public String getAccessTokenUsingBoxAPI(String refreshToken)
			throws Exception {

		HttpsURLConnection con = null;
		StringBuffer response = null;

		try {

			String rootPath = new File("").getAbsolutePath();
			String filepath = rootPath + "/src/main/resources/";
			input = new FileInputStream(filepath + "app.properties");
			// load a properties file
			prop.load(input);
			// get the property value and print it out
			String boxUrl = prop.getProperty("box.access.token.url");
			String boxClientId = prop.getProperty("box.client.id");
			String boxClientSecret = prop.getProperty("box.client.secret");

			String targetURL = boxUrl;
			String query = "grant_type="
					+ URLEncoder.encode("refresh_token", "UTF-8");
			query += "&";
			query += "refresh_token="
					+ URLEncoder.encode(refreshToken, "UTF-8");
			query += "&";
			query += "client_id=" + URLEncoder.encode(boxClientId, "UTF-8");
			query += "&";
			query += "client_secret="
					+ URLEncoder.encode(boxClientSecret, "UTF-8");

			URL url = new URL(targetURL);
			con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(query);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = con.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			response = new StringBuffer();

			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			System.out.println(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (con != null) {
				con.disconnect();
			}
		}
		return response.toString();
	}
}
