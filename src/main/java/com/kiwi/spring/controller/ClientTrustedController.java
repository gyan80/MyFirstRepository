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
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.UserCopy;
import com.kiwi.spring.logs.ClientTrustedControllerLog;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.MilestoneControllerLog;
import com.kiwi.spring.models.ClientTrustedBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/advisors/clients/trusted")
public class ClientTrustedController extends ClientTrustedBaseController {

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

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public String fetchTrustedFamily(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");
		String host = env.get("host.url");
		String url = env.get("profile.pic.url");
		JSONArray jsonArray = new JSONArray();
		JSONObject responseDetailsJson = new JSONObject();

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientTrustedControllerLog.logger.log(Level.INFO, dateString + "fetchTrustedFamily method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Fetch all clients...");

		if (decryptedString != null) {
			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				String cl_id = null;
				if (jsonObject.get("access_token") != null) {
					if (validateAccessToken((String) jsonObject.get("access_token"))) {
						if (jsonObject.get("advisor_id") != null) {
							if (jsonObject.get("client_id") != null) {
								cl_id = (String) jsonObject.get("client_id");
							}

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<UserCopy> usrsList = getAllDataUsingTimeStamp((String) jsonObject.get("advisor_id"),
									cl_id, time_stamp);

							if (usrsList != null) {
								for (UserCopy p : usrsList) {
									JSONObject js = new JSONObject();

									js.put("shared_id", getSharedIdByUserId(p.getUser_id()));
									js.put("client_id", p.getAdvisor_id());
									js.put("user_id", p.getUser_id());
									js.put("dob", p.getDob());
									js.put("title", p.getTitle());
									js.put("first_name", p.getFirst_name());
									js.put("middle_name", p.getMiddle_name());
									js.put("last_name", p.getLast_name());
									js.put("email", p.getEmail());
									js.put("relation_to_self",
											p.getRelation_to_self());
									js.put("status_id", p.getStatus_id());
									js.put("is_delete", p.getIs_delete());
									if (p.getProfile_picture() != null)
										js.put("profile_picture", host + url
												+ p.getProfile_picture());
									else
										js.put("profile_picture",
												p.getProfile_picture());
									jsonArray.add(js);
								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Trusted family found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());

							responseDetailsJson.put("associates", jsonArray);

							encryptedString = Encrypter.encrypt(
									responseDetailsJson.toString(), key);
							return encryptedString;

						} else {
							ce.setFields("404", new Date().toString(),
									"Advisor id not found", "error");
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
				
				ClientTrustedControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public String verifyTrustedFamily(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");
		JSONObject responseDetailsJson = new JSONObject();

		decryptedString = Encrypter.decrypt(EncryptedValue, key);

		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientTrustedControllerLog.logger.log(Level.INFO, dateString + "verifyTrustedFamily method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Fetch all clients...");

		if (decryptedString != null) {
			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				String accessToken = (String) jsonObject.get("access_token");

				if (accessToken != null) {
					if (validateAccessToken(accessToken)) {
						if (jsonObject.get("trusted_associates") != null) {
							JSONArray getArray = (JSONArray) jsonObject.get("trusted_associates");
							
							if (getArray.size() > 0) {
								for (int i = 0; i < getArray.size(); i++) {
									JSONObject objects = (JSONObject) getArray.get(i);

									User usrs = getUserObjectByID((String) objects.get("user_id"));

									if (usrs != null) {
										if (usrs.getStatus_id() == 1) {

											usrs.setStatus_id(2l);
											usrs.setPassword_reset(1l);
											usrs.setUpdated_date(String.valueOf(System
													.currentTimeMillis()));

											String response = generateFolderStructure(
													usrs.getFirst_name()
															+ ":"
															+ usrs.getLast_name(),
													usrs.getUser_id());
											getUpdate(usrs);

											try {
												System.out
														.println("Thread going to sleep for 10 seconds...");
												Thread.sleep(10000);
												System.out
														.println("Thread resumed...");

											} catch (InterruptedException ie) {
												ie.printStackTrace();
											}
											if (response != null)
												sentMails(
														usrs,
														"trusted.family.subject",
														"trusted.family.message",
														"trusted.family.restmessage",
														"Trusted family's Credentials",
														"Username - " + usrs.getUsername(),
														"Password - " + usrs.getPassword(),
														0);

										}
									}
								}
								responseDetailsJson.put("statusCode", "200");
								responseDetailsJson.put("currentDate",
										new Date().toString());
								responseDetailsJson.put("result", "success");
								responseDetailsJson.put("message",
										"Trusted members verified");

								encryptedString = Encrypter.encrypt(
										responseDetailsJson.toString(), key);
								return encryptedString;
							} else {
								ce.setFields("404", new Date().toString(),
										"Trusted Associates blank or missing", "error");
							}
						} else {
							ce.setFields("404", new Date().toString(),
									"Trusted Associates id not found", "error");
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
				
				ClientTrustedControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}
		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

}
