package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.WealthPlanElementsDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.entity.WealthPlans;

public class WealthPlanElementsBaseController {

	@Autowired
	WealthPlanElementsDAO wealthPlanElementsDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	boolean flag = false;

	protected List<WealthPlanElements> getAllDataUsingTimeStamp(String advisorid, String cl_id,
			Long timestamp) {

		return wealthPlanElementsDAO.fetchAllWealthPlans(advisorid, cl_id,
				timestamp);
	}
	
	protected List<WealthPlanElements> getAllSharedDataUsingTimeStamp(
			String trstdid, Long timestamp) {

		return wealthPlanElementsDAO.fetchAllSharedWealthPlans(trstdid, timestamp);
	}
	
	protected ShareWealthPlans getSharedStatus(String wpid, String peid, String trstd_id){
		return wealthPlanElementsDAO.getSharedStatus(wpid, peid, trstd_id);
	}

	protected WealthPlanElements getWealthPlanObjectByAdvisorID(String id) {

		WealthPlanElements usr = wealthPlanElementsDAO.byAdvisorID(id);
		if (usr != null) {
			return usr;
		}
		return null;
	}
	
	protected boolean checkAndInsertSharedWPE(String wp_id, String pe_id) {

		Long result = wealthPlanElementsDAO.checkAndInsertSharedWPE(wp_id, pe_id);
		if (result > 0l) {
			return true;
		}
		return false;
	}
	
	protected boolean checkWealthPlanId(String wp_id) {

		WealthPlans usrs = wealthPlanElementsDAO.checkWealthPlanId(wp_id);
		if (usrs != null) {
			return true;
		}
		return false;
	}
	
	protected boolean deleteAllElements(String wpeId, String wpId, String peId) {

		try {
			int result = wealthPlanElementsDAO.deleteAllElements(wpeId, wpId, peId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean checkPlanElementId(String pe_id) {

		PlanElement usrs = wealthPlanElementsDAO.checkPlanElementId(pe_id);
		if (usrs != null) {
			return true;
		}
		return false;
	}

	protected boolean getUpdate(WealthPlanElements wp, int flag) {

		if (flag == 1)
			wealthPlanElementsDAO.update(wp);
		else
			wealthPlanElementsDAO.save(wp);
		return true;
	}

	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = wealthPlanElementsDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}

	protected WealthPlanElements checkWealthPlanID(String wpid) {

		WealthPlanElements wps = wealthPlanElementsDAO.checkWPID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
}
