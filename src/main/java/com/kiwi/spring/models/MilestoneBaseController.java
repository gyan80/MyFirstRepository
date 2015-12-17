package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.MilestoneDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.ShareWealthPlans;

public class MilestoneBaseController {

	@Autowired
	MilestoneDAO milestoneDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	boolean flag = false;

	protected List<Milestone> getAllDataUsingTimeStamp(String advisorid, String cl_id,
			Long timestamp) {

		return milestoneDAO.fetchAllWealthPlans(advisorid, cl_id,
				timestamp);
	}
	
	protected List<Milestone> getAllSharedDataUsingTimeStamp(String trstdid,
			Long timestamp) {

		return milestoneDAO.fetchAllSharedWealthPlans(trstdid, timestamp);
	}
	
	protected ShareWealthPlans getSharedStatus(String wpeid, String trstdid){
		return milestoneDAO.getSharedStatus(wpeid, trstdid);
	}

	protected Milestone getWealthPlanObjectByAdvisorID(String id) {

		Milestone usr = milestoneDAO.byAdvisorID(id);
		if (usr != null) {
			return usr;
		}

		return null;
	}

	protected boolean getUpdate(Milestone wp, int flag) {

		if (flag == 1)
			milestoneDAO.update(wp);
		else
			milestoneDAO.save(wp);
		return true;
	}
	
	protected boolean checkWPEId(String id) {

		Milestone m = milestoneDAO.checkWPEId(id);
		if (m != null) {
			return true;
		}
		return false;
	}

	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = milestoneDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}
	
	protected boolean deleteAllElements(String mId) {

		try {
			int result = milestoneDAO.deleteAllElements(mId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected Milestone checkWealthPlanID(String wpid) {

		Milestone wps = milestoneDAO.checkWPID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
}
