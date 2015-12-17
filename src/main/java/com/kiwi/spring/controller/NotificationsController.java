package com.kiwi.spring.controller;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.Notifications;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.NotificationsControllerLog;
import com.kiwi.spring.logs.PlanElementControllerLog;
import com.kiwi.spring.models.NotificationsBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/notifications")
public class NotificationsController extends NotificationsBaseController {

	@Autowired
	Configuration env;

	User obj = null;
	Object object = null;
	JSONParser parser = new JSONParser();;
	JSONObject jsonObject = null;
	Gson gson = new Gson();
	String encryptedString = null;
	String decryptedString = null;
	CustomException ce = CustomException.getInstance();
	JSONObject responseDetailsJson = new JSONObject();

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/reminder", method = RequestMethod.POST)
	public String syncNotifications(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		NotificationsControllerLog.logger.log(Level.INFO, dateString + "syncNotifications method request: " + decryptedString);
		Log.logger.log(Level.INFO, "syncNotificationsController...");
		
		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				if (jsonObject.get("access_token") != null) {
					if (validateAccessToken((String) jsonObject.get("access_token"))) {
						if (jsonObject.get("user_id") != null) {

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<Notifications> notifications = getAllNotificationsUsingTimeStamp(
									(String) jsonObject.get("user_id"),
									time_stamp);

							if (!notifications.isEmpty()) {
								for (Notifications notification : notifications) {
									jsonArray.add(notification);
								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Notifications found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());
							responseDetailsJson.put("notifications", jsonArray);

							encryptedString = Encrypter.encrypt(
									responseDetailsJson.toString(), key);

							return encryptedString;
						} else {
							ce.setFields("404", new Date().toString(),
									"User ID not found", "error");
						}
					} else {
						ce.setFields("403", new Date().toString(),
								"User logged out", "error");
					}
				} else {
					ce.setFields("404", new Date().toString(),
							"Access token not found", "error");
				}
			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");

				NotificationsControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
