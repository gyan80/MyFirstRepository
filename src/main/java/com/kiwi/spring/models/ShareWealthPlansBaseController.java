package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.dao.ShareWealthPlansDAO;
import com.kiwi.spring.dao.UserDAO;
import com.kiwi.spring.dao.WealthPlansDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;
import com.kiwi.spring.entity.WealthPlanElements;


public class ShareWealthPlansBaseController {

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	ShareWealthPlansDAO shareWealthPlansDAO;
	
	@Autowired
	WealthPlansDAO wealthPlansDAO;

	protected ShareWealthPlans checkWP_PE_TAID(String shareTo, String wpid,
			String peid) {

		ShareWealthPlans wps = shareWealthPlansDAO.checkWP_PE_TAIDValidation(
				shareTo, wpid, peid);
		if (wps != null)
			return wps;
		else
			return null;
	}

	protected List<ShareWealthPlans> checkWP_PE_TAID(String shareTo, String wpid) {

		List<ShareWealthPlans> wps = shareWealthPlansDAO.checkWP_TAID(shareTo,
				wpid);
		if (!wps.isEmpty())
			return wps;
		else
			return null;
	}

	protected boolean getUpdate(ShareWealthPlans wp, int flag) {

		if (flag == 1)
			shareWealthPlansDAO.update(wp);
		else
			shareWealthPlansDAO.save(wp);
		return true;
	}
	
	protected ShareWealthPlans getShareWealthPlan(String wealthPlanID, String ta_id, String p_id) {

		ShareWealthPlans swp = shareWealthPlansDAO.getShareWealthPlan(wealthPlanID, ta_id, p_id);
		if(swp != null)
			return swp;
		else
			return null;

	}
	
	protected boolean saveSharedWp(ShareWealthPlans swp) {

		shareWealthPlansDAO.saveSharedWp(swp);
		return true;
	}
	
	protected List<WealthPlanElements> getPlanElementsByWP(String wp) {

		return shareWealthPlansDAO.getPlanElements(wp);
	}
	
	protected WealthPlans checkWealthPlanID(String wpid) {

		WealthPlans wps = wealthPlansDAO.checkWPID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}
	
	protected User checkIdValidation(String userId) {

		User usr = shareWealthPlansDAO.checkIdValidation(userId);
		if (usr != null)
			return usr;
		else
			return null;
	}
	
	protected List<ShareWealthPlans> getAllSharedDataUsingTimeStamp(String advisorId, String client_id,
			Long timestamp) {

		return shareWealthPlansDAO.fetchAllSharedWealthPlans(advisorId, client_id,
				timestamp);

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
	
	protected void updateSharedPlanElement(String wpid, String user_id) {
		shareWealthPlansDAO.updateSharedPlanElement(wpid, user_id);
	}
	
	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = shareWealthPlansDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}

	protected boolean removeExisting(List<ShareWealthPlans> swp) {

		try {
			shareWealthPlansDAO.delete(swp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	protected void deleteSharedTodos(String wp_id, String owner, String pe_id) {
		try {
			shareWealthPlansDAO.deleteSharedTodos(wp_id, owner, pe_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
