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
import com.kiwi.spring.util.Encrypter;
import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.UserControllerLog;
import com.kiwi.spring.logs.WealthPlansControllerLog;
import com.kiwi.spring.models.WealthPlansBaseController;
import com.kiwi.spring.util.CustomException;


/**
 * This controller is used for handling wealth plans related data
 * 
 * @author Ajay 
 *
 */
@Controller
@RequestMapping(value = "/advisors/clients")
public class WealthPlansController extends WealthPlansBaseController {

	@Autowired
	Configuration env;

	JSONParser parser = new JSONParser();;
	JSONObject jsonObject = null;
	Gson gson = new Gson();
	String encryptedString = null;
	String decryptedString = null;
	CustomException ce = CustomException.getInstance();
	JSONObject responseDetailsJson = new JSONObject();

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/wp/sync", method = RequestMethod.POST)
	public String addWealthPlan(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");
		
		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		WealthPlansControllerLog.logger.log(Level.INFO, dateString + "addWealthPlan method request: " + decryptedString);
		Log.logger.log(Level.INFO, "WealthPlansController...");
		
		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {

				String cl_id = null;
				if (jsonObject.get("access_token") != null) {

					if (validateAccessToken((String) jsonObject
							.get("access_token"))) {

						if (jsonObject.get("advisor_id") != null) {

							if (jsonObject.get("client_id") != null) {
								cl_id = (String) jsonObject.get("client_id");
							}

							if (jsonObject.get("wealth_plans") != null) {
								Long time_stamp = null;

								if (jsonObject.get("timestamp") != null) {
									time_stamp = (Long) jsonObject
											.get("timestamp");
								} else {
									time_stamp = 0l;
								}

								List<WealthPlans> wps = getAllDataUsingTimeStamp(
										(String) jsonObject.get("advisor_id"),
										cl_id, time_stamp);

								if (!wps.isEmpty()) {
									for (WealthPlans wp : wps) {
										String pic = null;
										User usr = getUserObjectbyId(wp
												.getClient_id());
										if (usr != null)
											if (usr.getProfile_picture() != null)
												pic = env.get("host.url")
														+ env.get("profile.pic.url")
														+ usr.getProfile_picture();

										JSONArray jsnArray = getAttachedDocumentsArray(wp.getWealth_plan_id(), time_stamp);
										jsonArray.add(wp.getJsonWithProfilePicture(pic, jsnArray));
									}
								}

								JSONArray getArray = (JSONArray) jsonObject
										.get("wealth_plans");

								if (getArray != null) {
									if (getArray.size() > 0) {
										for (int i = 0; i < getArray.size(); i++) {
											JSONObject objects = (JSONObject) getArray.get(i);
											saveOrUpdateWealthplans(objects, jsonObject);
										}
									}
								}

								responseDetailsJson.put("statusCode", "200");
								responseDetailsJson.put("currentDate",
										new Date().toString());
								responseDetailsJson.put("result", "success");
								responseDetailsJson.put("message",
										"Wealth Plans found");

								responseDetailsJson.put("timestamp",
										System.currentTimeMillis());
								responseDetailsJson.put("wealth_plans",
										jsonArray);

								encryptedString = Encrypter.encrypt(
										responseDetailsJson.toString(), key);

								return encryptedString;
							} else {
								ce.setFields("404", new Date().toString(),
										"Wealth_plans not found", "error");
							}
						} else {
							ce.setFields("404", new Date().toString(),
									"Advisor ID not found", "error");
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
				ce.setFields("401", new Date().toString(),
						"Error occurred. Please try again.", "error");
				WealthPlansControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/shared_wp/sync", method = RequestMethod.POST)
	public String syncSharedWealthPlans(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		WealthPlansControllerLog.logger.log(Level.INFO, dateString + "syncSharedWealthPlans method request: " + decryptedString);
		Log.logger.log(Level.INFO, "syncSharedWealthPlansController...");

		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {

				if (jsonObject.get("access_token") != null) {

					if (validateAccessToken((String) jsonObject
							.get("access_token"))) {

						if (jsonObject.get("trusted_associate_id") != null) {

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<WealthPlans> wps = getAllSharedDataUsingTimeStamp(
									(String) jsonObject.get("trusted_associate_id"),
									time_stamp);

							if (!wps.isEmpty()) {
								for (WealthPlans wp : wps) {
									String pic = null;
									User usr = getUserObjectbyId(wp
											.getClient_id());
									if (usr != null)
										if (usr.getProfile_picture() != null)
											pic = env.get("host.url")
													+ env.get("profile.pic.url")
													+ usr.getProfile_picture();
									
									JSONArray jsnArray = getAttachedDocumentsArray(wp.getWealth_plan_id(), time_stamp);
									jsonArray.add(wp.getJsonWithProfilePicture(pic, jsnArray, getSharedStatus(wp.getWealth_plan_id(), (String) jsonObject.get("trusted_associate_id"))));

								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Wealth Plans found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());
							responseDetailsJson.put("wealth_plans", jsonArray);

							encryptedString = Encrypter.encrypt(
									responseDetailsJson.toString(), key);

							return encryptedString;
						} else {
							ce.setFields("404", new Date().toString(),
									"Trusted Associate ID not found", "error");
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
				WealthPlansControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
