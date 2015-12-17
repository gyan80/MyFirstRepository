package com.kiwi.spring.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.User;
import com.github.sendgrid.SendGrid;

public class Mail {

	@Autowired
	Configuration env;

	private static Mail instance = null;

	private Mail() {
	}

	public static Mail getInstance() {
		if (instance == null) {
			instance = new Mail();
		}
		return instance;
	}

	public boolean sendMail(User usr, String uname, String pass, String from,
			String url, String subject, String message, String restMessage,
			String uri, int response) {

		boolean res = false;
		try {
			String username = usr.getUsername();
			String email = usr.getEmail();
			String uid = usr.getForgot_token();
			String msgTxt = null;
			if (response == 1) {
				msgTxt = "Dear " + username + message + url + uri + username
						+ "&token=" + uid + restMessage;
			} else {
				msgTxt = "Dear " + username + message + uri + restMessage;
			}

			SendGrid sendgrid = new SendGrid(uname, pass);
			sendgrid.addTo(email);
			sendgrid.setFrom(from);
			sendgrid.setSubject(subject);
			sendgrid.setText(msgTxt);
			sendgrid.send();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	
	public boolean sendMail(User usr, String uname, String pass, String from,
			String url, String subject, String message, String restMessage,String usrMsg, String usrname, String password, String appLink,
			int response) {

		boolean res = false;
		try {
			String username = usr.getUsername();
			String email = usr.getEmail();
			String uid = usr.getForgot_token();
			String msgTxt = null;
			if (response == 1) {
				msgTxt = "Dear " + username + message + url + usrMsg + username
						+ "&token=" + uid + restMessage;
			} else {
				msgTxt = "Dear " + username + "<br/><br/>" + message + "<br/><br/>" + usrMsg + "<br/>"+usrname+"<br/>"+password +"<br>"+ restMessage + "<br/>";
				
			}
			String restLinkPart = "<br/>Note: Please open this email on your iOS device to download the app.";

			String rest = "<br>Please contact your advisor in case of any issues." + "<br><br>" + "Thankyou" + "<br>" + "Team Sensery";
			SendGrid sendgrid = new SendGrid(uname, pass);
			sendgrid.addTo(email);
			sendgrid.setFrom(from);
			sendgrid.setSubject(subject);
			//sendgrid.setText(msgTxt);
			sendgrid.setHtml(msgTxt + appLink + restLinkPart + rest);
			sendgrid.send();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public boolean sentCustomMails(String uname, String pass, String from,
			String to, JSONArray getArray, String subject, String message) {

		boolean result = false;
		try {

			SendGrid sendgrid = new SendGrid(uname, pass);
			sendgrid.addTo(to);
			
			if (getArray.size() > 0) {
				for (int i = 0; i < getArray.size(); i++) {
					JSONObject objects = (JSONObject) getArray.get(i);
					String cc = (String) objects.get("cc");

					sendgrid.addTo(cc);
				}
			}
			
			sendgrid.setFrom(from);
			sendgrid.setSubject(subject);
			sendgrid.setText(message);
			sendgrid.send();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
