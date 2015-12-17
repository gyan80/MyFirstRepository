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
import com.kiwi.spring.entity.Box;
import com.kiwi.spring.entity.BoxFolders;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.logs.BoxControllerLog;
import com.kiwi.spring.logs.ClientTrustedControllerLog;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.models.BoxBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;
import com.kiwi.spring.util.SingleInstance;

@Controller
@RequestMapping(value = "/vault")
public class BoxController extends BoxBaseController {

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

	// *********************** fetch specific client service done
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/access_token", method = RequestMethod.GET)
	public String getBoxAccessToken(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");
		JSONObject responseDetailsJson = new JSONObject();

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		BoxControllerLog.logger.log(Level.INFO, dateString + "getBoxAccessToken method request: " + decryptedString);
		Log.logger.log(Level.INFO, "Get Box Access Token...");
		
		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);
				BoxControllerLog.logger.log(Level.INFO, "Get Box Access Token...");

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				if (jsonObject.get("access_token") != null) {

					if (validateAccessToken((String) jsonObject.get("access_token"))) {

						Box box = getBoxAccessToken();
						if (box != null) {
							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message",
									"Access token found");

							responseDetailsJson.put("timestamp",
									box.getUpdated_date());
							responseDetailsJson.put("box_access_token",
									box.getAccess_token());

							encryptedString = Encrypter.encrypt(
									responseDetailsJson.toString(), key);

							return encryptedString;
						} else {
							ce.setFields("404", new Date().toString(),
									"Access token not found", "error");
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
				
				BoxControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/default_structure", method = RequestMethod.GET)
	public String getDefaultStructure(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");
		JSONArray jsonArray = new JSONArray();
		
		decryptedString = Encrypter.decrypt(EncryptedValue, key);

		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		BoxControllerLog.logger.log(Level.INFO, dateString + "getDefaultStructure method request: " + decryptedString);
		Log.logger.log(Level.INFO, "BoxController Get Default Structure...");

		if (decryptedString != null) {

			try {
				String imgpath = new File("").getAbsolutePath();
			    File newTextFile = new File(imgpath+"/getDefaultStructure.txt");

	            FileWriter fw = new FileWriter(newTextFile, true);
	            fw.write(new Date().toString() + " :: " + decryptedString + "\n");
	            fw.close();
	            
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

							User usrs = getUserObjectByID((String) jsonObject.get("user_id"));

							if (usrs != null) {
								
								List<BoxFolders> bfs = getBoxNonEditableFoldersList((String) jsonObject.get("user_id"));
								
								if (!bfs.isEmpty()) {
									for (BoxFolders boxFolders : bfs) {
										jsonArray.add(boxFolders.getJson());
									}
								}

								BoxFolders bf = getBoxFoldersData((String) jsonObject.get("user_id"));
								if (bf != null) {
									Box box = getBoxAccessToken();
									String privateFolderUrl = "https://api.box.com/2.0/folders/" + bf.getPrivate_id();
									String sharedFolderUrl = "https://api.box.com/2.0/folders/" + bf.getShared_id();
									String method = "GET";

									String privateFolderJson = SingleInstance.getInstance()
																					.manageBoxFolders(
																							privateFolderUrl, null,
																							method,
																							box.getAccess_token(),
																							bf.getAs_user());

									String sharedFolderJson = SingleInstance.getInstance()
																					.manageBoxFolders(
																							sharedFolderUrl, null,
																							method,
																							box.getAccess_token(),
																							bf.getAs_user());

									String response = "{\"message\": \"Default structure found!\""
											+ ",\"statusCode\": \"200\""
											+ ",\"result\": \"success\""
											+ ",\"currentDate\":\"" + new Date().toString()
											+ "\",\"default_structure\": "
											+ "{ \"private_folder\":" + privateFolderJson
											+ ",\"shared_folder\":" + sharedFolderJson
											+ ",\"as_user\": \"" + bf.getAs_user() 
											+ "\",\"non_editable_folder\": " + jsonArray
											+ "}}";

									encryptedString = Encrypter.encrypt(
											response, key);

									return encryptedString;
								} else {
									ce.setFields("404", new Date().toString(),
											"Folders not found", "error");
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"User not found", "error");
							}
						} else {
							ce.setFields("404", new Date().toString(),
									"User id not found", "error");
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
				
				BoxControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}
		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}
