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
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.WealthPlanElementsControllerLog;
import com.kiwi.spring.logs.WealthPlansControllerLog;
import com.kiwi.spring.models.WealthPlanElementsBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/advisors/clients")
public class WealthPlanElementsController extends WealthPlanElementsBaseController {

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
	@RequestMapping(value = "/goal/sync", method = RequestMethod.POST)
	public String addWealthPlanElements(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		WealthPlanElementsControllerLog.logger.log(Level.INFO, dateString + "addWealthPlanElements method request: " + decryptedString);
		Log.logger.log(Level.INFO, "WealthPlanElementsController...");
		
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
					if (validateAccessToken((String) jsonObject.get("access_token"))) {
						if (jsonObject.get("advisor_id") != null) {
							if (jsonObject.get("client_id") != null) {
								cl_id = (String) jsonObject.get("client_id");
							}

							if (jsonObject.get("goals") != null) {
								Long time_stamp = null;

								if (jsonObject.get("timestamp") != null) {
									time_stamp = (Long) jsonObject.get("timestamp");
								} else {
									time_stamp = 0l;
								}

								List<WealthPlanElements> wps = getAllDataUsingTimeStamp(
										(String) jsonObject.get("advisor_id"),
										cl_id, time_stamp);

								if (!wps.isEmpty()) {
									for (WealthPlanElements p : wps) {
										jsonArray.add(p);
									}
								}

								JSONArray getArray = (JSONArray) jsonObject.get("goals");

								if(getArray != null){
								if (getArray.size() > 0) {
									for (int i = 0; i < getArray.size(); i++) {
										JSONObject objects = (JSONObject) getArray.get(i);
										String wealth_plan_element_id = (String) objects
												.get("wealth_plan_element_id");

										if (wealth_plan_element_id != null) {
											WealthPlanElements wpe = checkWealthPlanID(wealth_plan_element_id);
											if (wpe != null) {

												if (objects.get("goal") != null)
													wpe.setGoal((String) objects
															.get("goal"));

												if (objects.get("is_delete") != null)
													wpe.setIs_delete((Long) objects
															.get("is_delete"));

												wpe.setUpdated_date(String.valueOf(System
														.currentTimeMillis()));

												getUpdate(wpe, 1);
												if((Long) objects.get("is_delete") == 1l){
													deleteAllElements((String) objects.get("wealth_plan_element_id"), (String) objects.get("wealth_plan_id"), (String) objects.get("plan_element_id"));
												}
											} else {

												if (objects.get("wealth_plan_id") != null) {
													if (checkWealthPlanId((String) objects.get("wealth_plan_id"))) {
														if (objects.get("plan_element_id") != null) {
															if (checkPlanElementId((String) objects.get("plan_element_id"))) {
																if (objects.get("is_delete") != null) {

																	WealthPlanElements wp = gson
																			.fromJson(objects.toJSONString(),
																					WealthPlanElements.class);

																	if (wp.getCreated_date() == null) {
																		wp.setCreated_date(String
																				.valueOf(System
																						.currentTimeMillis()));
																	}
																	wp.setUpdated_date(String
																			.valueOf(System
																					.currentTimeMillis()));

																	getUpdate(wp, 0);

																	checkAndInsertSharedWPE(
																			(String) objects.get("wealth_plan_id"),
																			(String) objects.get("plan_element_id"));

																}
															}
														}
													}
												}

											}
										}
									}
								}
								}
								responseDetailsJson.put("statusCode", "200");
								responseDetailsJson.put("currentDate",
										new Date().toString());
								responseDetailsJson.put("result", "success");
								responseDetailsJson.put("message",
										"Goals found");

								responseDetailsJson.put("timestamp",
										System.currentTimeMillis());
								responseDetailsJson.put("goals", jsonArray);

								encryptedString = Encrypter.encrypt(
										responseDetailsJson.toString(), key);

								return encryptedString;
							} else {
								ce.setFields("404", new Date().toString(),
										"Goals not found", "error");
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
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");
				WealthPlanElementsControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/shared_goal/sync", method = RequestMethod.POST)
	public String syncSharedWealthPlanElements(
			@RequestParam String EncryptedValue) throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		WealthPlanElementsControllerLog.logger.log(Level.INFO, dateString + "syncSharedaddWealthPlanElements method request: " + decryptedString);
		Log.logger.log(Level.INFO, "syncSharedWealthPlanElementsController...");

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

					if (validateAccessToken((String) jsonObject
							.get("access_token"))) {

						if (jsonObject.get("trusted_associate_id") != null) {

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<WealthPlanElements> wpes = getAllSharedDataUsingTimeStamp(
									(String) jsonObject
											.get("trusted_associate_id"),
									time_stamp);

							if (!wpes.isEmpty()) {
								for (WealthPlanElements p : wpes) {
									jsonArray
											.add(p.getJson(getSharedStatus(
													p.getWealth_plan_id(),
													p.getPlan_element_id(),
													(String) jsonObject
															.get("trusted_associate_id"))));
								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Wealth plan elements found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());
							responseDetailsJson.put("goals", jsonArray);

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
				
				WealthPlanElementsControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
