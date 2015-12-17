package com.kiwi.spring.models;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.BoxDAO;
import com.kiwi.spring.dao.BoxFoldersDAO;
import com.kiwi.spring.dao.UserDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Box;
import com.kiwi.spring.entity.BoxFolders;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.util.Mail;
import com.kiwi.spring.util.SingleInstance;

public class UserBaseController {

	@Autowired
	UserDAO userDAO;

	@Autowired
	BoxDAO boxDAO;

	@Autowired
	BoxFoldersDAO boxFoldersDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	boolean flag = false;

	protected User getViewer(HttpServletRequest request) {
		return null;
	}

	protected List<User> getAllClients(String advisorid, Long timestamp) {

		List<User> usr = userDAO.fetchAllClients(advisorid, timestamp);
		if (!usr.isEmpty())
			return usr;
		else
			return null;

	}
	
	protected String getSharedIdByUserId(String userId) {

		BoxFolders bfs = userDAO.getSharedIdByUserId(userId);
		if (bfs != null)
			return bfs.getShared_id();
		else
			return null;
	}

	protected List<User> getAllClientWithoutTimestamp(String advisorid) {

		List<User> usr = userDAO.fetchAllClientsWithoutTimestamp(advisorid);
		if (!usr.isEmpty())
			return usr;
		else
			return null;

	}

	protected User getEmailValidation(String email) {

		try {
			User usr = userDAO.byEmail(email);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	protected boolean addToClients(User obj) {

		try {
			long userId1 = userDAO.save(obj);
			if (userId1 != 0) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	protected boolean updateClients(User obj) {

		try {
			obj.setUpdated_date(new Timestamp(new Date().getTime()).toString());
			userDAO.update(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	protected User getUserObjectByID(String userid) {

		try {
			User usr = userDAO.byID(userid);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected User getUserObjectByAdivorsAndUserID(String userid,
			String advisorid) {

		try {
			User usr = userDAO.byUserAndAdivsorID(userid, advisorid);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected User getUserObjectByUserID(String userid) {

		try {
			User usr = userDAO.byUserID(userid);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected boolean deleteTrustedByUserId(String userid) {

		try {
			int usr = userDAO.byTrustedId(userid);
			if (usr > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected boolean getWealthPlanByClientId(String userid) {

		try {
			int usr = userDAO.byTrustedId(userid);
			if (usr > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected User getUserObjectByAdvisorID(String id) {

		try {
			User usr = userDAO.byAdvisorID(id);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	protected boolean deleteClientByID(User usr) {

		return userDAO.delete(usr);

	}

	protected User getUserID(String username) {

		try {
			User usr = userDAO.byUsername(username);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	protected String getLoginJson(User usr, String random, String logo) {

		String logo_url = null;
		if (logo != null)
			logo_url = env.get("host.url") + env.get("logo.url") + logo;

		String str = "{\"advisor_id\":\"" + usr.getAdvisor_id()
				+ "\",\"quick_box_id\":" + usr.getQuick_box_id()
				+ ",\"user_id\":\"" + usr.getUser_id()
				+ "\",\"password_reset\":" + usr.getPassword_reset()
				+ ",\"role_id\":" + usr.getRole_id() 
				+ ",\"first_name\":\"" + usr.getFirst_name() 
				+ "\",\"middle_name\":\"" + usr.getMiddle_name() 
				+ "\",\"last_name\":\"" + usr.getLast_name() 
				+ "\",\"username\":\"" + usr.getUsername()
				+ "\",\"access_token\":\"" + random 
				+ "\",\"logo\":\"" + logo_url 
				+ "\",\"result\":\"success\"}";
		return str;

	}

	protected User forgotPassword(String username) {

		User usr = null;
		try {
			usr = userDAO.byUsername(username);
			if (usr != null) {
				String uid = UUID.randomUUID().toString();
				usr.setForgot_token(uid);
				usr.setPassword_reset(0l);
				userDAO.update(usr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usr;
	}

	protected User getForgotUIDValidate(String username, String token) {
		try {
			User usr = userDAO.byForgotToken(username, token);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getLogoByAdvisorID(String advisorId) {

		try {
			User usr = userDAO.byID(advisorId);
			if (usr != null) {
				return usr.getLogo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String getAdvisorProfilePicByAdvisorID(String advisorId) {

		try {
			User usr = userDAO.byID(advisorId);
			if (usr != null) {
				return usr.getProfile_picture();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getLogoByTrustedID(String trustedId) {

		try {
			User usr = userDAO.byID(trustedId);
			if (usr != null) {
				return usr.getLogo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected String getAdvisorProfilePic(String advsrId) {

		try {
			User usr = userDAO.byID(advsrId);
			if (usr != null) {
				return usr.getProfile_picture();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected boolean getUpdate(User usr) {

		try {
			userDAO.update(usr);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected boolean getUpdateWealthPlans(String usrId, String timestamp) {

		try {
			userDAO.getUpdateWealthPlans(usrId, timestamp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected User loginByCredentials(String username, String password) {
		User usr = userDAO.byLoginCredentials(username, password);
		if (usr != null) {
			return usr;
		}
		return null;
	}

	protected boolean network(User usr) {

		try {
			userDAO.update(usr);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected String generatePass(int len) {

		Random randomInstance = new Random();
		char[] choices = ("abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "!#&")
				.toCharArray();

		StringBuilder salt = new StringBuilder(len);
		for (int i = 0; i < len; ++i)
			salt.append(choices[randomInstance.nextInt(choices.length)]);

		return salt.toString();
	}

	protected boolean removeNetwork(User usr, Long status_id) {
		usr.setPassword_reset(status_id);
		usr.setUpdated_date(String.valueOf(System.currentTimeMillis()));
		usr.setStatus_id(status_id);
		usr.setCreated_date(null);

		try {
			List<AccessToken> at = accessTokenDAO.byID(usr.getUser_id());
			userDAO.delete(at);
			userDAO.update(usr);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected boolean sentMails(User usr, String sub, String messg,
			String rest,String usrMsg, String username, String passwords, int response) {
		
/*		if(usr.getRole_id() == 4 || usr.getRole_id() == 5){
			return Mail.getInstance().sendMail(usr, env.get("sendgrid.username"),
				env.get("sendgrid.password"), env.get("sendgrid.from"),
				env.get("backend.web.url"), env.get(sub), env.get(messg),
				env.get(rest), uri, response);
		}*/
		
		return Mail.getInstance().sendMail(usr, env.get("sendgrid.username"),
					env.get("sendgrid.password"), env.get("sendgrid.from"),
					env.get("backend.url"), env.get(sub), env.get(messg),
					"", usrMsg, username, passwords, env.get("app.link"), response);
	}
	
	protected boolean sentMails(User usr, String sub, String messg,
			String rest, String uri, int response) {
		
/*		if(usr.getRole_id() == 4 || usr.getRole_id() == 5){
			return Mail.getInstance().sendMail(usr, env.get("sendgrid.username"),
				env.get("sendgrid.password"), env.get("sendgrid.from"),
				env.get("backend.web.url"), env.get(sub), env.get(messg),
				env.get(rest), uri, response);
		}*/
		
		return Mail.getInstance().sendMail(usr, env.get("sendgrid.username"),
					env.get("sendgrid.password"), env.get("sendgrid.from"),
					env.get("backend.url"), env.get(sub), env.get(messg),
					env.get(rest), uri, response);
	}

	protected boolean sentCustomMails(String to, JSONArray getArray,
			String subject, String message, String fromEmailId) {
		return Mail.getInstance().sentCustomMails(env.get("sendgrid.username"),
				env.get("sendgrid.password"), fromEmailId, to, getArray,
				subject, message);
	}

	@SuppressWarnings("unchecked")
	protected JSONObject parsingJson(String decryptedString, JSONObject obj)
			throws Exception {
		String[] strArray1 = { "access_token", "user_id", "advisor_id",
				"quick_box_id", "username", "password", "title", "first_name",
				"middle_name", "last_name", "address", "city", "state", "zip",
				"ssn", "dob", "telephone", "home_ext", "home_telephone",
				"work_ext", "work_telephone", "email", "marital_status",
				"currently_employed", "employer_name", "type_of_business",
				"employer_address", "employer_city", "employer_state",
				"employer_zip", "occupation", "age_of_retirement", "notes",
				"is_delete", "relation_to_self", "add_to_network_again" };

		String[] strArray2 = { "spouse", "future_income", "projected_pension",
				"working_during_retirement", "expense", "real_estate" };

		String[] strArray3 = { "family", "income", "expected_windfalls",
				"other", "education_expenses", "miscellaneous_expenses",
				"retirement_savings", "other_savings_investments",
				"other_real_estate", "business_interests", "additional_assets",
				"current_debt", "life_insurance", "medical_insurance",
				"property_insurance" };

		JSONObject obj2 = new JSONObject();

		for (String fieldName : strArray1) {
			obj2.put(fieldName, obj.get(fieldName));

		}

		for (String fieldName : strArray2) {
			if (obj.get(fieldName) == null) {

				obj2.put(fieldName, "null");
			} else if (obj.get(fieldName) instanceof JSONObject) {

				String str = obj.get(fieldName).toString();
				System.out.println(str+" =======================");
				String newStr = str.replace("\\\\", "\\");
				obj2.put(fieldName, newStr.toString());
				System.out.println(newStr+" =======================");
			} else {

				obj2.put(fieldName, "null");
			}
		}

		for (String fieldName : strArray3) {
			if (obj.get(fieldName) == null) {

				obj2.put(fieldName, "null");
			} else if (obj.get(fieldName) instanceof JSONArray) {

				String str = obj.get(fieldName).toString();
				System.out.println(str+" =======================");
				String newStr = str.replace("\\\\", "\\");
				obj2.put(fieldName, newStr.toString());
				System.out.println(newStr+" =======================");
			} else {

				obj2.put(fieldName, "null");
			}
		}

		return obj2;
	}

	@SuppressWarnings("unchecked")
	protected JSONObject parsingJsonForEditClient(String decryptedString,
			JSONObject obj) throws Exception {

		String[] strArray1 = { "access_token", "user_id", "advisor_id",
				"quick_box_id", "username", "password", "title", "first_name",
				"middle_name", "last_name", "address", "city", "state", "zip",
				"ssn", "dob", "telephone", "home_ext", "home_telephone",
				"work_ext", "work_telephone", "email", "marital_status",
				"currently_employed", "employer_name", "type_of_business",
				"employer_address", "employer_city", "employer_state",
				"employer_zip", "occupation", "age_of_retirement", "notes",
				"is_delete", "relation_to_self", "add_to_network_again" };

		String[] strArray2 = { "spouse", "future_income", "projected_pension",
				"working_during_retirement", "expense", "real_estate" };

		String[] strArray3 = { "family", "income", "expected_windfalls",
				"other", "education_expenses", "miscellaneous_expenses",
				"retirement_savings", "other_savings_investments",
				"other_real_estate", "business_interests", "additional_assets",
				"current_debt", "life_insurance", "medical_insurance",
				"property_insurance" };

		JSONObject obj2 = new JSONObject();

		for (String fieldName : strArray1) {
			obj2.put(fieldName, obj.get(fieldName));

		}

		for (String fieldName : strArray2) {
			if (obj.get(fieldName) == null) {

				obj2.put(fieldName, "null");
			} else if (obj.get(fieldName) instanceof JSONObject) {

				obj2.put(fieldName, obj.get(fieldName).toString());
			} else {

				obj2.put(fieldName, "null");
			}
		}

		for (String fieldName : strArray3) {
			if (obj.get(fieldName) == null) {

				obj2.put(fieldName, "null");
			} else if (obj.get(fieldName) instanceof JSONArray) {

				obj2.put(fieldName, obj.get(fieldName).toString());
			} else {

				obj2.put(fieldName, "null");
			}
		}

		return obj2;
	}

	protected boolean validate(final String hex, String firstName,
			String lastName) {
		Pattern pattern = null;
		Matcher matcher = null;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean res = false;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		pattern = Pattern.compile(EMAIL_PATTERN);
		if (hex != null) {
			matcher = pattern.matcher(hex);
			res = matcher.matches();
		}
		if (firstName != null && !firstName.equals(""))
			flag1 = true;
		else
			flag1 = false;
		if (lastName != null && !lastName.equals(""))
			flag2 = true;
		else
			flag2 = false;

		if (hex != null && !hex.equals(""))
			flag3 = true;
		else
			flag3 = false;
		if (res == true && flag1 == true && flag2 == true && flag3 == true)
			return true;
		else
			return false;

	}

	protected boolean validateForEditClient(String firstName, String lastName) {

		boolean flag1 = false;
		boolean flag2 = false;

		if (firstName != null && !firstName.equals(""))
			flag1 = true;
		else
			flag1 = false;
		if (lastName != null && !lastName.equals(""))
			flag2 = true;
		else
			flag2 = false;
		if (flag1 == true && flag2 == true)
			return true;
		else
			return false;

	}

	protected String randomDigits(Random random, int length) {
		char[] digits = new char[length];
		// Make sure the leading digit isn't 0.
		digits[0] = (char) ('1' + random.nextInt(9));
		for (int i = 1; i < length; i++) {
			digits[i] = (char) ('0' + random.nextInt(10));
		}
		String s = new String(digits);
		Long timestamp = System.currentTimeMillis() / 1000;
		s += timestamp.toString();
		return s;
	}

	protected boolean getUpdateAccessToken(AccessToken accToken) {

		try {
			accessTokenDAO.update(accToken);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected boolean validateAccessToken(String accToken) {

		try {
			AccessToken aToken = accessTokenDAO.byAccessToken(accToken);
			if (aToken != null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**************** Box Folder Structure ******************/
	protected String getBoxAccessToken() {

		try {
			Box box = boxDAO.getBoxAccessToken();
			if (box != null)
				return box.getAccess_token();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getAdvisorId(String user_id) {
		User usr = userDAO.byID(user_id);
		if (usr != null) {
			Long role_id = usr.getRole_id();
			if (role_id == 2) {
				return "0";
			} else if (role_id == 3) {
				return usr.getAdvisor_id();
			} else if (role_id == 4) {
				String associate_advior = usr.getAdvisor_id();
				User advisor = userDAO.byID(associate_advior);
				if (advisor.getAdvisor_id().equals("0")) {
					return advisor.getUser_id();
				} else {
					return advisor.getAdvisor_id();
				}

			} else if (role_id == 5) {
				String c_id = usr.getAdvisor_id();
				User client = userDAO.byID(c_id);

				User advisor = userDAO.byID(client.getAdvisor_id());
				if (!advisor.getAdvisor_id().equals("0")) {
					return advisor.getAdvisor_id();
				} else {
					return advisor.getUser_id();
				}
			}
		}
		return null;
	}

	
	protected String generateFolderStructure(String name, String user_id) {
		
		String response = null;
		User usr = userDAO.byID(user_id);
		
		String[] names = name.split(":");
		String user_count = getUserWithSameName(user_id, names[0], names[1]) ;
		  if(usr.getRole_id()==2){
		   user_count = "";
		  }
		  name = names[0]+" "+names[1] + user_count;
		  
		
		if (usr != null) {
			String advisor_id = getAdvisorId(user_id);
			Long role_id = usr.getRole_id();
			if (role_id == 2) {
				BoxFolders box_user = boxFoldersDAO.checkUser_id(user_id);
				if (box_user == null) {
					String box_user_id = createBoxUser();
					String box_pfolder_id = createFolder(name + ":Private", "0", box_user_id);
					String box_sfolder_id = createFolder(name + ":Shared", "0", box_user_id);
					saveBoxData(box_pfolder_id, box_sfolder_id, user_id, box_user_id);
					response = box_sfolder_id;
				}
			} else {
				BoxFolders box_user = boxFoldersDAO.checkUser_id(user_id);
				if (box_user == null) {
					BoxFolders advisor_box_user = boxFoldersDAO.checkUser_id(advisor_id);
					String parent_user_id = usr.getAdvisor_id();
					BoxFolders parent_box_user = boxFoldersDAO.checkUser_id(parent_user_id);
					String box_pfolder_id = createFolder(name + ":Private",	"0", advisor_box_user.getAs_user());
					String box_sfolder_id = createFolder(name + ":Shared", parent_box_user.getShared_id(),advisor_box_user.getAs_user());
					saveBoxData(box_pfolder_id, box_sfolder_id, user_id, advisor_box_user.getAs_user());
					response = box_sfolder_id;
				}
			}
		}
		return response;
	}

	protected String createBoxUser() {
		String box_user_id = null;
		String email = "sensery-" + getBsonId() + "@sensery.com";
		String requestQuery = "{\"login\": \"" + email
				+ "\", \"name\": \"Sensery\"}";
		String accessToken = getBoxAccessToken();
		System.out.print("Accescs Token :" + accessToken);
		String jsonString = SingleInstance.getInstance().manageBoxFolders(
				"https://api.box.com/2.0/users", requestQuery, "POST",
				accessToken, null);
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = new JSONObject();
			Object obj = parser.parse(jsonString);
			json = (JSONObject) obj;
			box_user_id = (String) json.get("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return box_user_id;
	}

	protected String createFolder(String name, String parent, String as_user) {
		String box_folder_id = null;
		String requestQuery = "{\"name\":\"" + name
				+ "\", \"parent\": {\"id\": \"" + parent + "\"}}";
		String accessToken = getBoxAccessToken();
		System.out.print("Accescs Token :" + accessToken);
		String jsonString = SingleInstance.getInstance().manageBoxFolders(
				"https://api.box.com/2.0/folders", requestQuery, "POST",
				accessToken, as_user);
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = new JSONObject();
			Object obj = parser.parse(jsonString);
			json = (JSONObject) obj;
			box_folder_id = (String) json.get("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return box_folder_id;
	}

	protected void saveBoxData(String box_pfolder_id, String box_sfolder_id,
			String user_id, String box_user_id) {
		BoxFolders box_user = new BoxFolders();
		if (box_user_id != null)
			box_user.setAs_user(box_user_id);
		else
			box_user.setAs_user(null);
		box_user.setPrivate_id(box_pfolder_id);
		box_user.setShared_id(box_sfolder_id);
		box_user.setUser_id(user_id);
		box_user.setCreated_date(String.valueOf(System.currentTimeMillis()));
		box_user.setUpdated_date(String.valueOf(System.currentTimeMillis()));
		boxFoldersDAO.save(box_user);
	}

	protected String getBsonId() {
		String process = ManagementFactory.getRuntimeMXBean().getName();
		int pos = process.indexOf('@');
		String pid = process.substring(0, pos);
		double i = Math.random() * 1000;
		long j = Math.round(i);
		String t = String.valueOf(System.currentTimeMillis());

		String s = pid + j + t;
		String hash = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			hash = new BigInteger(1, m.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}
	
	protected String getUserWithSameName(String user_id, String first_name, String last_name) {
		  String i = "";
		  User usr = userDAO.byID(user_id);
		  String advisor_id = usr.getAdvisor_id();
		  List<User> users = userDAO.getUserWithsame(advisor_id, first_name, last_name);
		  if(users!=null){
		   i = ""+users.size();
		   if(i.equals("0")){
		    i="";
		   }
		  }
		  return i;
	}
	
	protected void deleteFolderStructure(String user_id) {
		try{
		  BoxFolders bfs = getFolderInfo(user_id);
		  deleteFolder(bfs.getPrivate_id(), user_id);
		  deleteFolder(bfs.getShared_id(), user_id);
		}catch(Exception c){
			c.printStackTrace();
		}
	
	}
	
	protected BoxFolders getFolderInfo(String user_id) {
		  BoxFolders b_folder = null;
		  b_folder = boxFoldersDAO.checkUser_id(user_id);
		  return b_folder;
	}
	
	protected void deleteFolder(String id, String user_id) {
		  String as_user = getBoxAsUser(user_id);
		  String requestQuery = null;
		  String accessToken = getBoxAccessToken();
		  
		  String jsonString = SingleInstance.getInstance().manageBoxFolders("https://api.box.com/2.0/folders/"+id+"?recursive=true", requestQuery, "DELETE", accessToken, as_user);
		  System.out.println(jsonString);
	}
	
	protected String getBoxAsUser(String user_id) {
		  String advisor_id = getAdvisorId(user_id);
		  BoxFolders advisor_box_user;
		  if(advisor_id.equals("0")){
		   advisor_box_user = boxFoldersDAO.checkUser_id(user_id);
		  }else{
		   advisor_box_user = boxFoldersDAO.checkUser_id(advisor_id);
		  }
		  return advisor_box_user.getAs_user();
	}
	/**************** Box Folder Structure ******************/
}
