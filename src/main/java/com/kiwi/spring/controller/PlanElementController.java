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
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.PlanElementCopy;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.PlanElementControllerLog;
import com.kiwi.spring.logs.ShareWealthPlansControllerLog;
import com.kiwi.spring.models.PlanElementBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/advisors/clients")
public class PlanElementController extends PlanElementBaseController {

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
	@RequestMapping(value = "/plan/sync", method = RequestMethod.POST)
	public String syncPlanElement(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		PlanElementControllerLog.logger.log(Level.INFO, dateString + "planElementSync method request: " + decryptedString);
		Log.logger.log(Level.INFO, "PlanElementController...");
		
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
						if (jsonObject.get("advisor_id") != null) {
							if (byID((String) jsonObject.get("advisor_id"))) {
								if (jsonObject.get("plan_elements") != null) {
									Long time_stamp = null;

									if (jsonObject.get("timestamp") != null) {
										time_stamp = (Long) jsonObject
												.get("timestamp");
									} else {
										time_stamp = 0l;
									}

									List<PlanElement> pes = getAllDataUsingTimeStamp((String) jsonObject.get("advisor_id"),
											time_stamp);

									if (!pes.isEmpty()) {
										for (PlanElement p : pes) {
											jsonArray.add(p);
										}
									}

									JSONArray getArray = (JSONArray) jsonObject
											.get("plan_elements");
									if (getArray.size() > 0) {
										for (int i = 0; i < getArray.size(); i++) {
											JSONObject objects = (JSONObject) getArray
													.get(i);
											String plan_element_id = (String) objects
													.get("plan_element_id");
											if (plan_element_id != null) {
												PlanElement pe = checkPlanElementID(plan_element_id);
												if (pe != null) {

													if (objects.get("title") != null)
														pe.setTitle((String) objects
																.get("title"));
													if (objects.get("is_delete") != null)
														pe.setIs_delete((Long) objects.get("is_delete"));

													pe.setUpdated_date(String.valueOf(System
															.currentTimeMillis()));

													getUpdate(pe, 1);
													if((Long) objects.get("is_delete") == 1l){
														deleteAllElements((String) objects.get("plan_element_id"));
													}
												} else {
													PlanElement wp = gson
															.fromJson(
																	objects.toJSONString(),
																	PlanElement.class);

													wp.setAdvisor_id((String) jsonObject
															.get("advisor_id"));
													if (wp.getCreated_date() == null) {
														wp.setCreated_date(String.valueOf(System
																.currentTimeMillis()));
													}
													wp.setUpdated_date(String.valueOf(System
															.currentTimeMillis()));

													getUpdate(wp, 0);
												}
											}
										}
									}

									responseDetailsJson
											.put("statusCode", "200");
									responseDetailsJson.put("currentDate",
											new Date().toString());
									responseDetailsJson
											.put("result", "success");
									responseDetailsJson.put("message",
											"Plan elements found");

									responseDetailsJson.put("timestamp",
											System.currentTimeMillis());
									responseDetailsJson.put("plan_elements",
											jsonArray);

									encryptedString = Encrypter
											.encrypt(responseDetailsJson
													.toString(), key);

									return encryptedString;

								} else {
									ce.setFields("404", new Date().toString(),
											"Plan_elements not found", "error");
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"Advisor not found", "error");
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

				PlanElementControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/shared_plan/sync", method = RequestMethod.POST)
	public String syncSharedPlanElements(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		PlanElementControllerLog.logger.log(Level.INFO, dateString + "syncSharedPlanElement method request: " + decryptedString);
		Log.logger.log(Level.INFO, "syncSharedPlanElementsController...");

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

							List<PlanElementCopy> pecs = getAllSharedDataUsingTimeStamp(
									(String) jsonObject
											.get("trusted_associate_id"),
									time_stamp, 0l);

							if (!pecs.isEmpty()) {
								for (PlanElementCopy pec : pecs) {
									jsonArray.add(pec);
								}
							}
							List<PlanElementCopy> pecs1 = getAllSharedDataUsingTimeStamp(
									(String) jsonObject
											.get("trusted_associate_id"),
									time_stamp, 1l);

							if (!pecs1.isEmpty()) {
								for (PlanElementCopy pec : pecs1) {
									jsonArray.add(pec);
								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Plan elements found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());
							responseDetailsJson.put("plan_elements", jsonArray);

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

				PlanElementControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
