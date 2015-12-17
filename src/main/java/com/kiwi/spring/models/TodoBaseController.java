package com.kiwi.spring.models;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.TodoDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.Notifications;
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;

public class TodoBaseController {

	@Autowired
	TodoDAO todoDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	protected List<Todo> getAllDataUsingTimeStamp(String advisorid,
			String cl_id, Long timestamp) {

		return todoDAO.fetchAllTodo(advisorid, cl_id, timestamp);
	}

	protected WealthPlans getCreatedByIdUsingId(String milestoneId) {

		WealthPlans wp = todoDAO.getCreatedByIdUsingId(milestoneId);
		if (wp != null)
			return wp;
		return null;
	}
	
	protected PlanElement getWealthPlanElementsUsingTodoAndMilestoneId(String milestoneId, String todoId) {

		PlanElement pe = todoDAO.getWealthPlanElementsUsingTodoAndMilestoneId(milestoneId, todoId);
		if (pe != null)
			return pe;
		return null;
	}

	protected List<Todo> getAllSharedDataUsingTimeStamp(String trstd_id,
			Long timestamp) {

		return todoDAO.fetchAllSharedTodo(trstd_id, timestamp);
	}

	protected ShareWealthPlans getSharedStatus(String mid, String trstdid) {
		return todoDAO.getSharedStatus(mid, trstdid);
	}

	protected Todo getTodoObjectByAdvisorID(String id) {

		Todo usr = todoDAO.byAdvisorID(id);
		if (usr != null) {
			return usr;
		}

		return null;
	}
	
	protected User getUserObjectByUserID(String id) {

		User usr = todoDAO.byID(id);
		if (usr != null) {
			return usr;
		}

		return null;
	}

	protected boolean getUpdate(Todo wp, int flag) {

		if (flag == 1)
			todoDAO.update(wp);
		else
			todoDAO.save(wp);
		return true;
	}

	protected boolean getUpdate(Notifications noti, int flag) {

		if (flag == 1)
			todoDAO.update(noti);
		else
			todoDAO.save(noti);
		return true;
	}

	protected void createNotifications(String todoFrequency, Long isDelete,
			String clientName, String todoTitle, String planTitle, String todoId, Long todoStatus, String userId, String wpId, String wpTitle) {

		Notifications noti = getNotificationObjectById(todoId, userId);
		if (noti != null) {
			/*String title = clientName + ", you have ToDos pending. " + planTitle + " > " + milstoneTitle + " > " + todoTitle;
			noti.setCreated_date(String.valueOf(System.currentTimeMillis()));
			noti.setFrequency(todoFrequency);
			noti.setIs_delete(isDelete);
			noti.setTitle(title);
			noti.setTodo_id(todoId);
			noti.setWealth_plan_id(wpId);
			noti.setTodo_status(todoStatus);
			noti.setUpdated_date(String.valueOf(System.currentTimeMillis()));
			noti.setUser_id(userId);
			getUpdate(noti, 1);*/
		} else {
			String title = "You have a to-do '" + todoTitle + "' pending in your plan '" + wpTitle + "' :: '" + planTitle + "'";
			Notifications notifications = new Notifications();
			notifications.setNotification_id(getBsonId());
			notifications.setTodo_id(todoId);
			notifications.setWealth_plan_id(wpId);
			if (todoFrequency == null) {
				notifications.setFrequency("None");
			}else{
				notifications.setFrequency(todoFrequency);
			}
			notifications.setIs_delete(isDelete);
			notifications.setTitle(title);
			notifications.setTodo_status(todoStatus);
			notifications.setCreated_date(String.valueOf(System.currentTimeMillis()));
			notifications.setUpdated_date(String.valueOf(System
					.currentTimeMillis()));
			notifications.setUser_id(userId);
			getUpdate(notifications, 0);
		}

	}

	protected boolean checkAndInsertTodo(Todo todo, String createdBy) {

		Long result = todoDAO.checkAndInsertTodo(todo, createdBy, getBsonId());
		if (result > 0l) {
			return true;
		}
		return false;
	}

	protected boolean deleteAllElements(String todoId) {

		try {
			int result = todoDAO.deleteAllElements(todoId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected Notifications getNotificationObjectById(String todo_id, String userId) {

		Notifications notification = todoDAO.getNotificationObjectById(todo_id,userId);

		return notification;
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

	protected Milestone checkMilestoneId(String id) {

		Milestone usrs = todoDAO.checkMilestoneId(id);
		if (usrs != null) {
			return usrs;
		}
		return null;
	}

	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = todoDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}

	protected Todo checkTodoID(String wpid) {

		Todo wps = todoDAO.checkTodoID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
}
