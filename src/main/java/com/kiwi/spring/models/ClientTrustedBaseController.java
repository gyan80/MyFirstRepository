package com.kiwi.spring.models;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

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
import com.kiwi.spring.entity.UserCopy;
import com.kiwi.spring.util.Mail;
import com.kiwi.spring.util.SingleInstance;

public class ClientTrustedBaseController {

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

	protected boolean validateAccessToken(String accToken) {

		try {
			AccessToken aToken = accessTokenDAO.byAccessToken(accToken);
			if (aToken != null)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected User getUserObjectByAdvisorID(String id) {

		try {
			User usr = userDAO.byAdvisorID(id);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;

	}
	
	protected User getUserObjectByClintID(String id) {

		try {
			User usr = userDAO.byAdvisorID(id);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;

	}
	
	protected String getSharedIdByUserId(String userId) {

		BoxFolders bfs = userDAO.getSharedIdByUserId(userId);
		if (bfs != null)
			return bfs.getShared_id();
		else
			return null;
	}
	
	protected List<UserCopy> getAllDataUsingTimeStamp(String advisorid, String cl_id,
			Long timestamp) {

		return userDAO.fetchAllWealthPlans(advisorid, cl_id,
				timestamp);
	}

	protected List<User> getAllClientsTrustedFamily(String clientid,
			Long timestamp) {

		try {
			List<User> usr = userDAO.byUserAndAdivsorIDs(clientid, timestamp);
			if (!usr.isEmpty()) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected List<User> fetchAllTrusted(String clientid) {

		try {
			List<User> usr = userDAO.fetchAllTrusted(clientid);
			if (!usr.isEmpty()) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject getTrustedJson(User usr) {

		JSONObject js =new JSONObject();
		
		js.put("client_id", usr.getAdvisor_id());
		js.put("user_id", usr.getUser_id());
		js.put("role_id", usr.getRole_id());
		js.put("first_name", usr.getFirst_name());
		js.put("middle_name", usr.getMiddle_name());
		js.put("last_name", usr.getLast_name());
		js.put("email", usr.getEmail());
		js.put("relation_to_self", usr.getRelation_to_self());
		js.put("status_id", usr.getStatus_id());
		
		return js;
	}

	protected User getUserObjectByID(String userid) {

		try {
			User usr = userDAO.byTrustedID(userid);
			if (usr != null) {
				return usr;
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
			return false;
		}

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
		
		User usr = userDAO.byID(user_id);
		String response = null;
		String[] names = name.split(":");
		String user_count = getUserWithSameName(user_id, names[0], names[1]) ;
		  if(usr.getRole_id()==2){
		   user_count = "";
		  }
		  name = names[0]+" "+names[1]+user_count;
		  
		if (usr != null) {
			System.out.println("Start..........................1");
			String advisor_id = getAdvisorId(user_id);
			Long role_id = usr.getRole_id();
			if (role_id == 2) {
				BoxFolders box_user = boxFoldersDAO.checkUser_id(user_id);
				if (box_user == null) {
					String box_user_id = createBoxUser();
					String box_pfolder_id = createFolder(name + ":Private",	"0", box_user_id);
					String box_sfolder_id = createFolder(name + ":Shared", "0", box_user_id);
					saveBoxData(box_pfolder_id, box_sfolder_id, user_id, box_user_id);
					response = box_sfolder_id;
				}
			} else {
				BoxFolders box_user = boxFoldersDAO.checkUser_id(user_id);
				if (box_user == null) {
					System.out.println("Start..........................2");
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
	/**************** Box Folder Structure ******************/

}
