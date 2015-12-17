package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.PlanElementDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.PlanElementCopy;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.User;

public class PlanElementBaseController {

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	PlanElementDAO planElementDAO;

	@Autowired
	Configuration env;

	protected List<PlanElement> getAllDataUsingTimeStamp(String advisor_id,
			Long timestamp) {

		return planElementDAO.fetchAllPlanElements(advisor_id, timestamp);

	}
	
	protected List<PlanElementCopy> getAllSharedDataUsingTimeStamp(String trstdid,
			Long timestamp, Long status) {

		return planElementDAO.fetchAllSharedPlanElements(trstdid, timestamp, status);

	}
	
	protected ShareWealthPlans getSharedFull(String peid, String trstd_id){
		return planElementDAO.getSharedFull(peid, trstd_id);
	}

	protected PlanElement getPlanElementObjectByAdvisorID(String id) {

		PlanElement usr = planElementDAO.byAdvisorID(id);
		if (usr != null) {
			return usr;
		}

		return null;
	}

	protected boolean deleteAllElements(String peId) {

		try {
			int result = planElementDAO.deleteAllElements(peId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean getUpdate(PlanElement wp, int flag) {

		if (flag == 1)
			planElementDAO.update(wp);
		else
			planElementDAO.save(wp);
		return true;
	}

	protected boolean byID(String id) {

		User user = planElementDAO.byID(id);
		if (user != null)
			return true;
		else
			return false;
	}
	
	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = planElementDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}

	protected PlanElement checkPlanElementID(String wpid) {

		PlanElement wps = planElementDAO.checkPlanElementID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
}
