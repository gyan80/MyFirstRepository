package com.kiwi.spring.controller;

import java.io.File;
import java.io.FileWriter;
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
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.TodoControllerLog;
import com.kiwi.spring.logs.UserControllerLog;
import com.kiwi.spring.models.TodoBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;

@Controller
@RequestMapping(value = "/advisors/clients")
public class TodoController extends TodoBaseController {

	@Autowired
	Configuration env;

	JSONParser parser = new JSONParser();
	JSONObject jsonObject = null;
	Gson gson = new Gson();
	String encryptedString = null;
	String decryptedString = null;
	CustomException ce = CustomException.getInstance();
	JSONObject responseDetailsJson = new JSONObject();

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/todos/sync", method = RequestMethod.POST)
	public String todoSync(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		TodoControllerLog.logger.log(Level.INFO, dateString + "todoSync method request: " + decryptedString);
		Log.logger.log(Level.INFO, "TodoController...");

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

					if (validateAccessToken((String) jsonObject
							.get("access_token"))) {
						if (jsonObject.get("advisor_id") != null) {
							if (jsonObject.get("client_id") != null) {
								cl_id = (String) jsonObject.get("client_id");
							}

							if (jsonObject.get("todos") != null) {
								Long time_stamp = null;

								if (jsonObject.get("timestamp") != null) {
									time_stamp = (Long) jsonObject
											.get("timestamp");
								} else {
									time_stamp = 0l;
								}
								List<Todo> todos = getAllDataUsingTimeStamp((String) jsonObject.get("advisor_id"),
										cl_id, time_stamp);

								if (!todos.isEmpty()) {
									for (Todo td : todos) {
										jsonArray.add(td);
									}
								}
								JSONArray getArray = (JSONArray) jsonObject.get("todos");

								if (getArray.size() > 0) {
									for (int i = 0; i < getArray.size(); i++) {
										JSONObject objects = (JSONObject) getArray.get(i);
										String todoobj = (String) objects.get("todo_id");
										if (todoobj != null) {
											Todo tds = checkTodoID(todoobj);
											if (tds != null) {
												if (objects.get("title") != null)
													tds.setTitle((String) objects
															.get("title"));
												if (objects.get("owner") != null)
													tds.setOwner((String) objects
															.get("owner"));
												if (objects.get("is_delete") != null)
													tds.setIs_delete((Long) objects
															.get("is_delete"));
												if (objects
														.get("notification_entry") != null)
													tds.setNotification_entry((String) objects
															.get("notification_entry"));
												if (objects.get("status") != null)
													tds.setStatus((Long) objects.get("status"));
												if (objects.get("priority") != null)
													tds.setPriority((Long) objects.get("priority"));
												if (objects.get("due_date") != null)
													tds.setDue_date((String) objects
															.get("due_date"));

												tds.setUpdated_date(String.valueOf(System
														.currentTimeMillis()));
												getUpdate(tds, 1);

												Notifications noti = getNotificationObjectById(
														tds.getTodo_id(), (String) objects.get("owner"));

												if (noti != null) {
													noti.setTodo_status((Long) objects.get("status"));
													noti.setIs_delete((Long) objects.get("is_delete"));
													noti.setUser_id((String) objects.get("owner"));
													if ((String) objects.get("notification_entry") != null)
														noti.setFrequency((String) objects.get("notification_entry"));
													noti.setUpdated_date(String.valueOf(System
															.currentTimeMillis()));

													getUpdate(noti, 1);
												}

												if ((Long) objects.get("is_delete") == 1l) {
													deleteAllElements((String) objects.get("todo_id"));
												}
											} else {
												if (objects.get("is_delete") != null) {
													Todo todo = gson.fromJson(objects.toJSONString(),
																	Todo.class);

													if (todo.getNotification_entry() == null) {
														todo.setNotification_entry("None");
													}
													if (todo.getCreated_date() == null) {
														todo.setCreated_date(String.valueOf(System
																.currentTimeMillis()));
													}
													todo.setUpdated_date(String.valueOf(System
															.currentTimeMillis()));
													getUpdate(todo, 0);

													WealthPlans wp = getCreatedByIdUsingId(todo.getMilestone_id());
													PlanElement pe = getWealthPlanElementsUsingTodoAndMilestoneId(
															todo.getMilestone_id(), todo.getTodo_id());
													User usr = getUserObjectByUserID(todo.getOwner());
													if (wp != null) {
														createNotifications(
																todo.getNotification_entry(),
																todo.getIs_delete(),
																usr.getFirst_name(),
																todo.getTitle(),
																pe.getTitle(),
																todo.getTodo_id(),
																todo.getStatus(),
																todo.getOwner(),
																wp.getWealth_plan_id(),
																wp.getTitle());
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
										"Todos found");

								responseDetailsJson.put("timestamp",
										System.currentTimeMillis());
								responseDetailsJson.put("todos", jsonArray);

								encryptedString = Encrypter.encrypt(
										responseDetailsJson.toString(), key);

								return encryptedString;
							} else {
								ce.setFields("404", new Date().toString(),
										"Todos not found", "error");
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
				
				TodoControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/shared_todos/sync", method = RequestMethod.POST)
	public String syncSharedTodos(@RequestParam String EncryptedValue)
			throws Exception {

		JSONArray jsonArray = new JSONArray();
		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		TodoControllerLog.logger.log(Level.INFO, dateString + "syncSharedtodos method request: " + decryptedString);
		Log.logger.log(Level.INFO, "syncSharedTodosController...");

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
						if (jsonObject.get("trusted_associate_id") != null) {

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<Todo> todos = getAllSharedDataUsingTimeStamp(
									(String) jsonObject.get("trusted_associate_id"), time_stamp);

							if (!todos.isEmpty()) {
								for (Todo p : todos) {
									jsonArray.add(p.getJson(getSharedStatus(p.getMilestone_id(),
													(String) jsonObject.get("trusted_associate_id"))));
								}
							}

							JSONArray getArray = (JSONArray) jsonObject.get("todos");

							if (getArray.size() > 0) {
								for (int i = 0; i < getArray.size(); i++) {
									JSONObject objects = (JSONObject) getArray.get(i);
									String todoobj = (String) objects.get("todo_id");
									if (todoobj != null) {
										Todo tds = checkTodoID(todoobj);
										if (tds != null) {
											if (objects.get("status") != null)
												tds.setStatus((Long) objects.get("status"));

											tds.setUpdated_date(String.valueOf(System
													.currentTimeMillis()));

											getUpdate(tds, 1);

											Notifications noti = getNotificationObjectById(
													tds.getTodo_id(), (String) objects.get("owner"));
											if (noti != null) {
												noti.setTodo_status((Long) objects.get("status"));
												noti.setUpdated_date(String.valueOf(System
														.currentTimeMillis()));
												getUpdate(noti, 1);
											}
										}
									}
								}
							}

							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message", "Todos found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());
							responseDetailsJson.put("todos", jsonArray);

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
				
				TodoControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
