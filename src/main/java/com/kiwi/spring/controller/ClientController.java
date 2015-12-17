package com.kiwi.spring.controller;

import java.io.File;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.logs.BoxControllerLog;
import com.kiwi.spring.logs.ClientControllerLog;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.models.UserBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;
import com.kiwi.spring.util.S3Save;

@Controller
@RequestMapping(value = "/advisors/clients")
public class ClientController extends UserBaseController {

	@Autowired
	Configuration env;

	User obj = null;
	Object object = null;
	JSONParser parser = new JSONParser();
	JSONObject jsonObject = null;
	Gson gson = new Gson();
	String encryptedString = null;
	String decryptedString = null;
	CustomException ce = CustomException.getInstance();

	// *********************** fetch specific client service done
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/getspecificclient", method = RequestMethod.GET)
	public String fetchSpecificClient(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");
		String host = env.get("host.url");
		String url = env.get("profile.pic.url");
		JSONObject responseDetailsJson = new JSONObject();

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "fetchSpecificClient method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController My Profile...");
		
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
							if (jsonObject.get("user_id") != null) {

								User usrsList = getUserObjectByID((String) jsonObject.get("user_id"));

								if (usrsList != null) {

									if (usrsList.getProfile_picture() != null)
										usrsList.setProfile_picture(host + url
												+ usrsList.getProfile_picture());

									String advisorProfilePic = null;
									String logo = null;
									if (usrsList.getRole_id() == 2) {

										logo = getLogoByAdvisorID(usrsList.getUser_id());
										advisorProfilePic = getAdvisorProfilePicByAdvisorID(usrsList.getUser_id());
									} else if (usrsList.getRole_id() == 3 || usrsList.getRole_id() == 4) {

										logo = getLogoByAdvisorID(usrsList.getAdvisor_id());
										advisorProfilePic = getAdvisorProfilePicByAdvisorID(usrsList.getAdvisor_id());
									} else if (usrsList.getRole_id() == 5) {

										String cl_id = usrsList.getAdvisor_id();
										User usr0 = getUserObjectByID(cl_id);
										User usr1 = getUserObjectByID(usr0.getAdvisor_id());
										
										if (usr1.getAdvisor_id().equals("0")) {
											logo = getLogoByTrustedID(usr1.getUser_id());
											advisorProfilePic = getAdvisorProfilePic(usr1.getUser_id());
										} else {
											User usr2 = getUserObjectByID(usr1.getAdvisor_id());
											logo = getLogoByTrustedID(usr2.getUser_id());
											advisorProfilePic = getAdvisorProfilePic(usr2.getUser_id());
										}
									}

									String advsrProfilePic = null;
									if (advisorProfilePic != null)
										advsrProfilePic = env.get("host.url")
												+ env.get("logo.url")
												+ advisorProfilePic;
									
									String cmsLogo = null;
									if (logo != null)
										cmsLogo = env.get("host.url")
												+ env.get("logo.url")
												+ logo;

									responseDetailsJson
											.put("statusCode", "200");
									responseDetailsJson.put("currentDate",
											new Date().toString());
									responseDetailsJson
											.put("result", "success");
									responseDetailsJson.put("message",
											"Client found");

									responseDetailsJson.put("client", usrsList
											.getUserJson(usrsList.getProfile_picture(), advsrProfilePic, cmsLogo));

									encryptedString = Encrypter
											.encrypt(responseDetailsJson
													.toString(), key);
									return encryptedString;
								} else {
									ce.setFields("404", new Date().toString(),
											"User not found", "error");
								}

							} else {
								ce.setFields("404", new Date().toString(),
										"User id not found", "error");
							}
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}
		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	// *********************** fetch all client service done
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public String fetchAllClients(@RequestParam String EncryptedValue)
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
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "fetchAllClients method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Fetch all clients...");

		if (decryptedString != null) {

			try {
				
				String imgpath = new File("").getAbsolutePath();
				    File newTextFile = new File(imgpath+"/senserytext.txt");

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
				String advsrId = (String) jsonObject.get("advisor_id");
				if (jsonObject.get("access_token") != null) {
					if (validateAccessToken((String) jsonObject.get("access_token"))) {
						if (advsrId != null) {

							Long time_stamp = null;

							if (jsonObject.get("timestamp") != null) {
								time_stamp = (Long) jsonObject.get("timestamp");
							} else {
								time_stamp = 0l;
							}

							List<User> usrsList = null;
							int flag = 0;
							if(!advsrId.equals("0")){
								usrsList = getAllClients((String) jsonObject.get("advisor_id"), time_stamp);
							}else{
								flag = 1;
							}

							if (usrsList != null) {
								for (User p : usrsList) {
									if (p.getProfile_picture() != null)
										p.setProfile_picture(host + url	+ p.getProfile_picture());
										p.setShared_id(getSharedIdByUserId(p.getUser_id()));
									jsonArray.add(p);
								}
							}
							
							if(flag == 1)
								responseDetailsJson.put("error", "You have send Advisro id=0 ");
							
							responseDetailsJson.put("statusCode", "200");
							responseDetailsJson.put("currentDate",
									new Date().toString());
							responseDetailsJson.put("result", "success");
							responseDetailsJson.put("message", "Clients found");

							responseDetailsJson.put("timestamp",
									System.currentTimeMillis());

							responseDetailsJson.put("clients", jsonArray);

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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
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
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editClient(HttpServletRequest request,
			@RequestPart("profile_picture") MultipartFile file)
			throws Exception {

		JSONObject js = null;
		String key = env.get("AES_KEY");
		String EncryptedValue = request.getParameter("EncryptedValue");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "editClient method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController Edit client...");

		if (decryptedString != null) {

			try {
				object = parser.parse(decryptedString);
				js = parsingJsonForEditClient(decryptedString,
						(JSONObject) object);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				if (js.get("access_token") != null) {

					if (validateAccessToken((String) js.get("access_token"))) {

						if (js.get("advisor_id") != null) {
							if (js.get("user_id") != null) {

								boolean res = validateForEditClient(
										(String) js.get("first_name"),
										(String) js.get("last_name"));

								if (!res) {
									ce.setFields(
											"404",
											new Date().toString(),
											"Requiered fields are blank or missing",
											"error");

									encryptedString = Encrypter.encrypt(
											ce.toString(), key);
									return encryptedString;
								}

								User usr = getUserObjectByAdvisorID((String) js
										.get("advisor_id"));

								if (usr != null) {

									User temp = getUserObjectByID((String) js
											.get("user_id"));

									if (temp != null) {

										js.put("id", temp.getId());
										js.put("user_id", temp.getUser_id());
										js.put("email", temp.getEmail());
										js.put("username", temp.getUsername());
										js.put("password", temp.getPassword());
										js.put("status_id", temp.getStatus_id());
										js.put("role_id", temp.getRole_id());
										js.put("device_wipe",
												temp.getDevice_wipe());
										js.put("password_reset", temp.getPassword_reset());
										if (js.get("is_delete") == null)
											js.put("is_delete", temp.getIs_delete());
										if (js.get("add_to_network_again") == null)
											js.put("add_to_network_again", temp.getAdd_to_network_again());

										if (temp.getCreated_date() != null)
											js.put("created_date", temp.getCreated_date());

										obj = gson.fromJson(js.toJSONString(), User.class);

										obj.setUpdated_date(String
												.valueOf(System
														.currentTimeMillis()));

										String file_name = null;
										if (!file.isEmpty()) {
											file_name = file.getOriginalFilename();
											String ext = file_name.substring(file_name.lastIndexOf("."), file_name.length());

											file_name = obj.getUser_id() + "_" + String.valueOf(System
															.currentTimeMillis()) + ext;

											String filepath = request.getSession().getServletContext().getRealPath("resources")
													+File.separator+"temp"+File.separator
													+file_name;

											File newFile = new File(filepath);
											
											try {
												newFile.setWritable(true, false);
												file.transferTo(newFile);
												newFile.createNewFile();
												S3Save s3 = new S3Save();
												s3.Save("senserybucket", "profilepics/" + file_name, newFile);
												newFile.deleteOnExit();
											} catch (Exception e2) {
												e2.printStackTrace();
											}
											obj.setProfile_picture(file_name);
										} else {
											obj.setProfile_picture(null);
										}
										if (getUpdate(obj)) {

											getUpdateWealthPlans(
													(String) js.get("user_id"),
													String.valueOf(System
															.currentTimeMillis()));
											ce.setFields(
													"200",
													new Date().toString(),
													"Client updated successfully. ",
													"success");
										} else {
											ce.setFields(
													"405",
													new Date().toString(),
													"Error occurred. Please try again.",
													"error");
										}
									} else {
										ce.setFields("404",
												new Date().toString(),
												"User not found", "error");
									}
								} else {
									ce.setFields("404", new Date().toString(),
											"Advisor not found", "error");
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"User id not found", "error");
							}
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	// *********************** delete service done

	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public String deleteClient(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "deleteClient method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Delete...");

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

						if (jsonObject.get("user_id") != null) {

							User usrs = getUserObjectByID((String) jsonObject
									.get("user_id"));

							if (usrs != null) {

								usrs.setIs_delete(1l);
								usrs.setUpdated_date(String.valueOf(System
										.currentTimeMillis()));
								deleteFolderStructure((String) jsonObject.get("user_id"));
								if (getUpdate(usrs)) {
									if (deleteTrustedByUserId(usrs.getUser_id())) {
										getWealthPlanByClientId(usrs
												.getUser_id());
										ce.setFields("200",
												new Date().toString(),
												"Client deleted successfully",
												"success");
									} else {
										ce.setFields(
												"405",
												new Date().toString(),
												"Error occurred. Please try again.",
												"error");
									}

								} else {
									ce.setFields("405", new Date().toString(),
											"Error occurred. Please try again.",
											"error");
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/addtonetwork", method = RequestMethod.POST)
	public String addToNetwork(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "addtoNetwork method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Add to network...");

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

						if (jsonObject.get("user_id") != null) {

							User usrs = getUserObjectByID((String) jsonObject
									.get("user_id"));

							if (usrs != null) {
								if (usrs.getStatus_id() == 1) {
									usrs.setStatus_id(2l);
									usrs.setPassword(generatePass(8));
									usrs.setUpdated_date(String.valueOf(System
											.currentTimeMillis()));
									if (network(usrs)) {
										if (usrs.getAdd_to_network_again() == 1) {
											sentMails(
													usrs,
													"addto.network.again.subject",
													"addto.network.again.message",
													"addto.network.again.restmessage",
													"Login Credentials",
													"Username - " + usrs.getUsername(),
													"Password - " + usrs.getPassword(),
													0);
										} else {

											
											String response = generateFolderStructure(usrs.getFirst_name()
															+ ":"
															+ usrs.getLast_name(),
															usrs.getUser_id());
											usrs.setAdd_to_network_again(1l);
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
											
											if(response != null)
												sentMails(
													usrs,
													"addto.network.subject",
													"addto.network.message",
													"addto.network.restmessage",
													"Login Credentials -",
													"Username - " + usrs.getUsername(),
													"Password - " + usrs.getPassword(),
													0);
										}

										ce.setFields(
												"200",
												new Date().toString(),
												"Client added to network. An email is sent to associated email id",
												"success");
									} else {
										ce.setFields(
												"404",
												new Date().toString(),
												"Error occurred. Please try again.",
												"error");
									}
								} else {
									ce.setFields(
											"404",
											new Date().toString(),
											"Client is already added to the network",
											"error");
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"Client not found", "error");
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/removefromnetwork", method = RequestMethod.POST)
	public String removeNetwork(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "removeNetwork method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Remove from network...");

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

						if (jsonObject.get("user_id") != null) {
							User usrs = getUserObjectByID((String) jsonObject
									.get("user_id"));

							if (usrs != null) {
								if (usrs.getStatus_id() == 2
										|| usrs.getStatus_id() == 3) {

									boolean result = removeNetwork(usrs, 1l);
									if (result) {

										ce.setFields(
												"200",
												new Date().toString(),
												"Client "
														+ usrs.getUsername()
														+ " removed from network.",
												"success");
									} else {
										ce.setFields(
												"405",
												new Date().toString(),
												"Error occurred. Please try again.",
												"error");
									}
								} else {
									ce.setFields(
											"404",
											new Date().toString(),
											"Client already removed from network",
											"error");
								}

							} else {
								ce.setFields("404", new Date().toString(),
										"Client not found.", "error");
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}
		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/resendinvite", method = RequestMethod.POST)
	public String resendInvite(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "resendInvite method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Resend invite...");

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

						if (jsonObject.get("user_id") != null) {

							User usrs = getUserObjectByID((String) jsonObject
									.get("user_id"));

							if (usrs != null) {
								if (usrs.getStatus_id() == 3) {
									ce.setFields(
											"404",
											new Date().toString(),
											"Client already joined the network",
											"error");
								} else {
									if (usrs.getStatus_id() == 2) {

										sentMails(usrs,
												"addto.network.subject",
												"addto.network.message",
												"addto.network.restmessage",
												"Login Credentials -",
												"Username - " + usrs.getUsername(),
												"Password - " + usrs.getPassword(),
												0);

										ce.setFields(
												"200",
												new Date().toString(),
												"Email has been resent to notify client",
												"success");

									} else {
										ce.setFields(
												"404",
												new Date().toString(),
												"Client is not added to network",
												"error");
									}
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"Client not found", "error");
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}
		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/update_quickbox", method = RequestMethod.PUT)
	public String updateQuickBoxID(@RequestParam String EncryptedValue)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		ClientControllerLog.logger.log(Level.INFO, dateString + "updateQuickBlox method request: " + decryptedString);
		Log.logger.log(Level.INFO, "ClientController Update Quick Box Id...");

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

						if (jsonObject.get("user_id") != null) {
							if (jsonObject.get("quick_box_id") != null) {

								User usrs = getUserObjectByID((String) jsonObject
										.get("user_id"));

								if (usrs != null) {

									usrs.setQuick_box_id((Long) jsonObject
											.get("quick_box_id"));

									if (getUpdate(usrs)) {

										ce.setFields("200",
												new Date().toString(),
												"Quick box ID updated",
												"success");
									} else {
										ce.setFields(
												"405",
												new Date().toString(),
												"Error occurred. Please try again.",
												"success");
									}

								} else {
									ce.setFields("404", new Date().toString(),
											"Client not found", "error");
								}

							} else {
								ce.setFields("404", new Date().toString(),
										"Quick box id not found", "error");
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
				
				ClientControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

}
