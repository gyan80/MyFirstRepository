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
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.entity.WealthPlans;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.ShareWealthPlansControllerLog;
import com.kiwi.spring.logs.TodoControllerLog;
import com.kiwi.spring.models.ShareWealthPlansBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/advisors/clients/share")
public class ShareWealthPlansController extends ShareWealthPlansBaseController {

	@Autowired
	Configuration env;

	JSONParser parser = new JSONParser();;
	JSONObject jsonObject = null;
	Gson gson = new Gson();
	String encryptedString = null;
	String decryptedString = null;
	CustomException ce = CustomException.getInstance();
	JSONObject responseDetailsJson = new JSONObject();

	@ResponseBody
	@RequestMapping(value = "/wp", method = RequestMethod.POST)
	public String shareWealthPlan(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ShareWealthPlansControllerLog.logger.log(Level.INFO, dateString + "shareWealthPlan method request: " + decryptedString);
		Log.logger.log(Level.INFO, "SharePlansController...");
		
		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {

				String accesToken = (String) jsonObject.get("access_token");
				String advisorId = (String) jsonObject.get("advisor_id");
				String clientId = (String) jsonObject.get("client_id");
				String sharedBy = (String) jsonObject.get("shared_by");
				String wealthPlanID = (String) jsonObject.get("wealth_plan_id");
				Long sharedStatus = (Long) jsonObject.get("shared_status");

				if (accesToken != null) {
					if (validateAccessToken(accesToken)) {
						if (advisorId != null) {
							User usr = checkIdValidation(advisorId);
							if (usr != null) {
								if (clientId != null) {
									User usr1 = checkIdValidation(clientId);
									if (usr1 != null) {
										if (sharedBy != null) {
											if (jsonObject.get("shared_to") != null) {
												if (wealthPlanID != null) {
													WealthPlans wps = checkWealthPlanID(wealthPlanID);
													if (wps != null) {
														if (sharedStatus != null) {

															String plan_element = null;

															if (jsonObject.get("plan_element_id") != null
																	&& !jsonObject.get("plan_element_id").equals("")) {
																plan_element = (String) jsonObject.get("plan_element_id");
															}

															JSONArray getArray = (JSONArray) jsonObject
																	.get("shared_to");

															if (getArray.size() > 0) {

																for (int i = 0; i < getArray.size(); i++) {
																	JSONObject objects = (JSONObject) getArray.get(i);
																	String trusted_associate_id = (String) objects
																			.get("trusted_associate_id");

																	if (plan_element != null) {
																		ShareWealthPlans swp = getShareWealthPlan(
																				wealthPlanID,
																				trusted_associate_id,
																				plan_element);
																		ShareWealthPlans wp = new ShareWealthPlans();
																		if (swp != null)
																			wp.setId(swp.getId());
																		wp.setPlan_element_id(plan_element);
																		wp.setAdvisor_id(advisorId);
																		wp.setClient_id(clientId);
																		wp.setShared_by(sharedBy);
																		wp.setShared_to(trusted_associate_id);
																		wp.setWealth_plan_id(wealthPlanID);
																		wp.setCreated_date(String
																				.valueOf(System
																						.currentTimeMillis()));
																		wp.setUpdated_date(String
																				.valueOf(System
																						.currentTimeMillis()));
																		wp.setShared_status(sharedStatus);
																		wp.setFull_plan(0l);
																		saveSharedWp(wp);
																		updateSharedPlanElement(
																				wealthPlanID,
																				trusted_associate_id);
																		
																		if(sharedStatus==0l){
																			deleteSharedTodos(wealthPlanID, trusted_associate_id, plan_element);
																		}
																		
																	} else {

																		List<WealthPlanElements> wpes = getPlanElementsByWP(wealthPlanID);
																		if (!wpes.isEmpty()) {
																			for (WealthPlanElements wpe : wpes) {
																				plan_element = wpe.getPlan_element_id();
																				ShareWealthPlans swp = getShareWealthPlan(
																						wealthPlanID,
																						trusted_associate_id,
																						plan_element);
																				ShareWealthPlans wp = new ShareWealthPlans();
																				if (swp != null)
																					wp.setId(swp
																							.getId());
																				wp.setPlan_element_id(plan_element);
																				wp.setAdvisor_id(advisorId);
																				wp.setClient_id(clientId);
																				wp.setShared_by(sharedBy);
																				wp.setShared_to(trusted_associate_id);
																				wp.setWealth_plan_id(wealthPlanID);
																				wp.setCreated_date(String
																						.valueOf(System
																								.currentTimeMillis()));
																				wp.setUpdated_date(String
																						.valueOf(System
																								.currentTimeMillis()));
																				wp.setShared_status(sharedStatus);
																				wp.setFull_plan(1l);
																				saveSharedWp(wp);
																				updateSharedPlanElement(
																						wealthPlanID,
																						trusted_associate_id);
																				if(sharedStatus==0l){
																					deleteSharedTodos(wealthPlanID, trusted_associate_id, plan_element);
																				}
																				plan_element = null;
																			}
																		}
																		
																	}
																}
																ce.setFields(
																		"200",
																		new Date()
																				.toString(),
																		"Plans Shared successfully",
																		"success");
															}

														} else {
															ce.setFields(
																	"404",
																	new Date()
																			.toString(),
																	"Shared Status not found",
																	"error");
														}
													} else {
														ce.setFields(
																"404",
																new Date()
																		.toString(),
																"Wealth Plans not found",
																"error");
													}
												} else {
													ce.setFields(
															"404",
															new Date()
																	.toString(),
															"Wealth Plans ID not found",
															"error");
												}
											} else {
												ce.setFields(
														"404",
														new Date().toString(),
														"Shared To ID not found",
														"error");
											}
										} else {
											ce.setFields("404",
													new Date().toString(),
													"Shared By ID not found",
													"error");
										}
									} else {
										ce.setFields("404",
												new Date().toString(),
												"Client not found", "error");
									}
								} else {
									ce.setFields("404", new Date().toString(),
											"Client ID not found", "error");
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
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");

				ShareWealthPlansControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);

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
	@RequestMapping(value = "/check_trusted_planelements", method = RequestMethod.POST)
	public String fetchSharedWealthPlans(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ShareWealthPlansControllerLog.logger.log(Level.INFO, dateString + "fetchSharedwealthplan method request: " + decryptedString);
		Log.logger.log(Level.INFO, "Fetch SharePlansController...");

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
				String accesToken = (String) jsonObject.get("access_token");
				String advisor_id = (String) jsonObject.get("advisor_id");
				String client_id = (String) jsonObject.get("client_id");

				if (accesToken != null) {

					if (validateAccessToken(accesToken)) {
						if (advisor_id != null) {
							if (client_id != null) {
								cl_id = client_id;
							}

							User usr = getUserObjectByID(advisor_id);
							if (usr != null) {
								Long time_stamp = null;

								if (jsonObject.get("timestamp") != null) {
									time_stamp = (Long) jsonObject
											.get("timestamp");
								} else {
									time_stamp = 0l;
								}

								List<ShareWealthPlans> swps = getAllSharedDataUsingTimeStamp(
										advisor_id, cl_id, time_stamp);

								if (!swps.isEmpty()) {
									for (ShareWealthPlans swp : swps) {
										jsonArray.add(swp);
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
								responseDetailsJson.put("shared_plans",
										jsonArray);

								encryptedString = Encrypter.encrypt(
										responseDetailsJson.toString(), key);

								return encryptedString;

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
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");

				ShareWealthPlansControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
