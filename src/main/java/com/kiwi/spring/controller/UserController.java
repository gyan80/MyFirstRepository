package com.kiwi.spring.controller;

import java.io.File;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.logs.Log;
import com.kiwi.spring.logs.UserControllerLog;
import com.kiwi.spring.logs.WealthPlansControllerLog;
import com.kiwi.spring.models.UserBaseController;
import com.kiwi.spring.util.CustomException;
import com.kiwi.spring.util.Encrypter;
import com.kiwi.spring.util.S3Save;
import com.kiwi.spring.util.SingleInstance;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/users")
public class UserController extends UserBaseController {

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

	@ResponseBody
	@RequestMapping(value = "/addclient", method = RequestMethod.POST)
	public String addClient(HttpServletRequest request,
			@RequestPart("profile_picture") MultipartFile file)
			throws Exception {

		JSONObject js = null;
		String key = env.get("AES_KEY");
		String EncryptedValue = request.getParameter("EncryptedValue");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "addClient method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController Add client...");

		if (decryptedString != null) {

			try {
				object = parser.parse(decryptedString);

				js = parsingJson(decryptedString, (JSONObject) object);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
				
			}

			try {
				String email = (String) js.get("email");
				String first_name = (String) js.get("first_name");
				String last_name = (String) js.get("last_name");

				if (js.get("access_token") != null) {
					if (validateAccessToken((String) js.get("access_token"))) {
						obj = gson.fromJson(js.toJSONString(), User.class);
						if (obj.getAdvisor_id() != null) {
							if (obj.getUser_id() != null) {

								boolean res = validate(email, first_name,
										last_name);

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

								User usres = getUserObjectByUserID(obj
										.getAdvisor_id());

								if (usres != null) {
									User usr = getEmailValidation(email);

									if (usr == null) {
										obj.setUsername(email);
										obj.setPassword_reset(1l);
										obj.setRole_id(4l);
										obj.setStatus_id(1l);
										obj.setIs_delete(0l);
										obj.setAdd_to_network_again(0l);
										obj.setDevice_wipe(0l);
										obj.setUpdated_date(String.valueOf(System.currentTimeMillis()));
										
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
										}else{
											obj.setProfile_picture(null);
										}
										
										if (addToClients(obj)) {
											
												ce.setFields(
														"200",
														new Date().toString(),
														"Client added successfully. ",
														"success");

										} else {
											ce.setFields("405",
													new Date().toString(),
													"User ID already exist",
													"error");

										}
									} else {
										ce.setFields(
												"404",
												new Date().toString(),
												"Entered Email ID already exists.",
												"error");
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
				
				UserControllerLog.logger.log(Level.WARNING, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
			
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam String EncryptedValue, HttpSession session)
			throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "login method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController Login...");

		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				String usrname = (String) jsonObject.get("username");
				String password = (String) jsonObject.get("password");

				if ((usrname != null) && (password != null)) {

					User usr = loginByCredentials(usrname, password);
					String logo = null;
					if (usr != null) {
						if (usr.getDevice_wipe() == 0l) {
							if (usr.getStatus_id() != 1) {
								if (usr.getRole_id() == 2) {
									
									logo = getLogoByAdvisorID(usr.getUser_id());
								} else if (usr.getRole_id() == 3 || usr.getRole_id() == 4) {
									
									logo = getLogoByAdvisorID(usr.getAdvisor_id());
								} else if (usr.getRole_id() == 5) {

									User usr1 = getUserObjectByID(usr.getAdvisor_id());
									User usr11 = getUserObjectByID(usr1.getAdvisor_id());
									if(usr11.getAdvisor_id().equals("0")){
										logo = getLogoByTrustedID(usr11.getUser_id());
									}else{
										User usr2 = getUserObjectByID(usr11.getAdvisor_id());
											logo = getLogoByTrustedID(usr2.getUser_id());
									}
								}

								String random = randomDigits(
										SingleInstance.getRandomInstance(), 14);
								AccessToken accessToken = new AccessToken();
								accessToken.setCreated_date(new Timestamp(
										new Date().getTime()).toString());
								accessToken.setAccess_token(random);
								accessToken.setUser_id(usr.getUser_id());

								if (getUpdateAccessToken(accessToken)) {

									if (usr.getCreated_date() == null) {
										usr.setCreated_date(new Timestamp(
												new Date().getTime())
												.toString());
										getUpdate(usr);
									}

									encryptedString = Encrypter.encrypt(
											getLoginJson(usr, random, logo),
											key);
									return encryptedString;
								} else {
									ce.setFields("405", new Date().toString(),
											"Error occurred. Please try again.",
											"error");
								}

							} else {
								ce.setFields(
										"404",
										new Date().toString(),
										"Your account has been removed or disabled temporarily. Please contact your advisor.",
										"error");
							}
						} else {
							ce.setFields(
									"400",
									new Date().toString(),
									"Your device has been removed or disabled temporarily. Please contact your advisor.",
									"error");

						}
					} else {
						ce.setFields("404", new Date().toString(),
								"Invalid Username or password", "error");

					}
				} else {
					ce.setFields("404", new Date().toString(),
							"Requiered fields are blank or missing", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");

				UserControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");

		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/change_pwd", method = RequestMethod.POST)
	public String changePassword(@RequestParam String EncryptedValue,
			HttpSession session) throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "changePassword method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController Change Password...");

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

						if ((jsonObject.get("user_id") != null)
								&& (jsonObject.get("password") != null)) {

							obj = gson.fromJson(decryptedString, User.class);

							User usr = getUserObjectByID((String) jsonObject
									.get("user_id"));

							if (usr != null) {
								if (!obj.getPassword().equals("")) {
									usr.setPassword(obj.getPassword());
									usr.setPassword_reset(0l);
									usr.setStatus_id(3l);
									usr.setUpdated_date(String.valueOf(System
											.currentTimeMillis()));
									if (getUpdate(usr)) {

										ce.setFields(
												"200",
												new Date().toString(),
												"Password has been changed successfully",
												"success");

									} else {

										ce.setFields(
												"405",
												new Date().toString(),
												"Error occurred. Please try again.",
												"error");
									}
								} else {
									ce.setFields("404", new Date().toString(),
											"Password cannot be left blank",
											"error");
								}
							} else {
								ce.setFields("404", new Date().toString(),
										"User not found", "error");
							}

						} else {
							ce.setFields("404", new Date().toString(),
									"Requiered fields are blank or missing",
									"error");
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

				UserControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@ResponseBody
	@RequestMapping(value = "/forgot_pwd", method = RequestMethod.POST)
	public String forgot(HttpServletRequest request,
			@RequestParam String EncryptedValue) throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "ForgotPassword method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController Forgot password...");

		if (decryptedString != null) {

			try {
				jsonObject = (JSONObject) parser.parse(decryptedString);

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("402", new Date().toString(),
						"Error occurred. Please try again.", "error");
			}

			try {
				if (jsonObject.get("username") != null) {

					User usr = forgotPassword((String) jsonObject.get("username"));
					if (usr != null) {

						if (usr.getStatus_id() == 3) {
							if (sentMails(usr, "email.forgot.subject",
									"email.forgot.message",
									"email.forgot.restmessage",
									"/users/forgotpwdpage?username=", 1)) {

								ce.setFields(
										"200",
										new Date().toString(),
										"An email has been send to your email-id.",
										"success");
							} else {
								ce.setFields("405", new Date().toString(),
										"Error occurred. Please try again.",
										"error");
							}
						} else {
							ce.setFields("404", new Date().toString(),
									"Since you have not logged in yet, so you can not use forgot password service", "error");
						}
					} else {
						ce.setFields("404", new Date().toString(),
								"No matching username found.", "error");
					}
				} else {
					ce.setFields("404", new Date().toString(),
							"Username not found", "error");
				}

			} catch (Exception e) {
				e.printStackTrace();
				ce.setFields("404", new Date().toString(),
						"Error occurred. Please try again.", "error");
				
				UserControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");
		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}

	@RequestMapping(value = "/forgotpwdpage", method = RequestMethod.GET)
	public String forgotPage(@RequestParam("username") String username,
			@RequestParam("token") String token, Model model) throws Exception {

		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "forgotPage method request: username=" + username + " token=" + token);

		User usr = getForgotUIDValidate(username, token);

		if (usr != null) {

			model.addAttribute("userid", usr.getUser_id());
			model.addAttribute("token", token);
			return "changepassword";
		} else {
			model.addAttribute("msg", "Link has been expired!");
			return "common";
		}
	}

	@RequestMapping(value = "/resetForgotPassword", method = RequestMethod.POST)
	public String resetForgotPassword(
			HttpServletRequest request,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "confirmpassword", required = true) String confirmPassword,
			@RequestParam("userid") String userid,
			@RequestParam("token") String token, Model model) throws Exception {

		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "resetforgotpassword method request: password=" + password + " confirmpassword=" + confirmPassword + " user_id=" + userid + " token=" + token);
		
		User usrs = getUserObjectByID(userid);
		User usr = getForgotUIDValidate(usrs.getUsername(), token);

		if (usr != null) {
			if (password != null && !password.equals("")) {
				if (password.equals(confirmPassword)) {
					usr.setPassword(password);
					usr.setForgot_token(null);
					if (getUpdate(usr)) {
						model.addAttribute("msg", "Password has been changed.");
						return "common";
					} else {
						model.addAttribute("msg",
								"Error occurred. Please try again.");
						model.addAttribute("userid", userid);
					}
				} else {
					model.addAttribute("msg",
							"Password does not match with confirm password");
					model.addAttribute("userid", userid);
				}
			} else {
				model.addAttribute("msg", "Password can not be left blank");
				model.addAttribute("userid", userid);
			}

		} else {
			model.addAttribute("msg", "Link has been expired!");
			return "common";
		}
		return "changepassword";
	}

	@ResponseBody
	@RequestMapping(value = "/send_email", method = RequestMethod.POST)
	public String sendingMail(@RequestParam String EncryptedValue,
			HttpSession session) throws Exception {

		String key = env.get("AES_KEY");

		decryptedString = Encrypter.decrypt(EncryptedValue, key);
		
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		UserControllerLog.logger.log(Level.INFO, dateString + "sendingMail method request: " + decryptedString);
		Log.logger.log(Level.INFO, "UserController SendMail API...");

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
				String to = (String) jsonObject.get("to");
				JSONArray getArray = (JSONArray) jsonObject.get("cc");
				String subject = (String) jsonObject.get("subject");
				String message = (String) jsonObject.get("message");
				String fromEmailId = (String) jsonObject.get("from");

				if (accessToken != null) {
					if (validateAccessToken(accessToken)) {
						if (fromEmailId != null) {
							if (to != null) {
								if (subject != null) {
									if (message != null) {

										sentCustomMails(to, getArray, subject,
												message, fromEmailId);

										ce.setFields("200",
												new Date().toString(),
												"An email has been sent",
												"success");
									} else {
										ce.setFields("404",
												new Date().toString(),
												"Message not found", "error");
									}
								} else {
									ce.setFields("404", new Date().toString(),
											"Subject not found", "error");
								}
							} else {
								ce.setFields("403", new Date().toString(),
										"To not found", "error");
							}
						} else {
							ce.setFields("403", new Date().toString(),
									"From not found", "error");
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
				
				UserControllerLog.logger.log(Level.INFO, dateString + " [Error] : " + e);
			}

		} else {
			ce.setFields("401", new Date().toString(),
					"Error occurred. Please try again.", "error");

		}

		encryptedString = Encrypter.encrypt(ce.toString(), key);
		return encryptedString;
	}
}