package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.AttachedDocumentDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;

public class AttachedDocumentBaseController {

	@Autowired
	AttachedDocumentDAO attachedDocumentDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;
	
	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = attachedDocumentDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}
	
	protected List<WealthPlans> getAllDataUsingTimeStamp(String advisorid, String cl_id,
			Long timestamp) {

		List<WealthPlans> usr = attachedDocumentDAO.getAllDataUsingTimeStamp(advisorid, cl_id,
				timestamp);

		return usr;
	}
	
	protected User getUserObjectbyId(String userId) {

		User usr = attachedDocumentDAO.getUserObjectbyId(userId);
		if (usr != null)
			return usr;
		else
			return null;
	}
	
	protected WealthPlans checkWealthPlanID(String wpid) {

		WealthPlans wps = attachedDocumentDAO.checkWealthPlanID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
	
	protected boolean getUpdate(WealthPlans wp, int flag) {

		if (flag == 1)
			attachedDocumentDAO.update(wp);
		else
			attachedDocumentDAO.save(wp);
		return true;
	}
	
	protected boolean checkId(String id) {

		User usrs = attachedDocumentDAO.checkId(id);
		if (usrs != null) {
			return true;
		}
		return false;
	}
}
