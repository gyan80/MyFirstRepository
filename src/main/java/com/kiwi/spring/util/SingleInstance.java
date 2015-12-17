package com.kiwi.spring.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import java.net.URL;
import java.util.Random;

public class SingleInstance {

	private static Random randomInstance = null;
	private static SingleInstance instance = null;
	
	private SingleInstance() {
	}

	public static Random getRandomInstance() {
		if (randomInstance == null) {
			randomInstance = new Random();
		}
		return randomInstance;
	}
	
	public static SingleInstance getInstance() {
		if (instance == null) {
			instance = new SingleInstance();
		}
		return instance;
	}
	
/*	public static void main(String[] args) {
		String requestQuery = "{\"name\":\"new folders\", \"parent\": {\"id\": \"220715991\"}}";
		//String requestQuery = "{\"login\": \"kiwi123@gmail.com\", \"name\": \"Sensery\"}";
		SingleInstance.getInstance().manageBoxFolders("https://api.box.com/2.0/folders", requestQuery, "POST", "klYm71v1DDDMe28W2Bk4VmLbH40tOQvB", null);
	}*/
	
	public String manageBoxFolders(String targetURL, String requestQuery,
			String method, String accessToken, String userId) {

		URL url;
		StringBuffer response = null;
		HttpsURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setRequestProperty("Authorization", "Bearer "
					+ accessToken);
			if(userId != null)
				connection.setRequestProperty("As-User", userId);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			if(requestQuery != null){
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(requestQuery);
			wr.flush();
			wr.close();
			}
			
			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			System.out.println("Response Code : " + connection.getResponseCode());
			System.out.println("Response Text : " + connection.getResponseMessage());
			
			System.out.println(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
		return response.toString();
	}
}
