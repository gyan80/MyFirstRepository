package com.kiwi.spring.models;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.WealthPlansDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.AttachedDocuments;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;

public class WealthPlansBaseController {

	@Autowired
	WealthPlansDAO wealthPlansDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	boolean flag = false;

	protected List<WealthPlans> getAllDataUsingTimeStamp(String advisorid,
			String cl_id, Long timestamp) {

		List<WealthPlans> usr = wealthPlansDAO.fetchAllWealthPlans(advisorid,
				cl_id, timestamp);
		return usr;

	}

	protected List<AttachedDocuments> getAttachedDocumentUsingTimeStamp(
			String wpId, Long time_stamp) {

		return wealthPlansDAO.getAttachedDocumentUsingTimeStamp(wpId,
				time_stamp);
	}

	@SuppressWarnings("unchecked")
	protected JSONArray getAttachedDocumentsArray(String wpId, Long time_stamp) {

		JSONArray jsonArray = new JSONArray();
		List<AttachedDocuments> ads = getAttachedDocumentUsingTimeStamp(wpId,
				time_stamp);

		if (!ads.isEmpty()) {
			for (AttachedDocuments ad : ads) {
				jsonArray.add(ad.getJson());
			}
		}

		return jsonArray;
	}
	
	protected int getSharedStatus(String wpid, String trstdid){
		return wealthPlansDAO.getSharedStatus(wpid, trstdid);
	}

	protected User getUserObjectbyId(String userId) {

		User usr = wealthPlansDAO.getUserObjectbyId(userId);
		if (usr != null)
			return usr;
		else
			return null;
	}

	protected List<WealthPlans> getAllSharedDataUsingTimeStamp(String trstdid,
			Long timestamp) {

		List<WealthPlans> usr = wealthPlansDAO.fetchAllSharedWealthPlans(
				trstdid, timestamp);
		return usr;

	}

	protected ShareWealthPlans getSharedFull(String wpid, String user_id) {
		return wealthPlansDAO.getSharedFull(wpid, user_id);
	}

	protected User getWealthPlanObjectByAdvisorID(String id) {

		try {
			User usr = wealthPlansDAO.byAdvisorID(id);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;

	}

	protected boolean checkId(String id) {

		User usrs = wealthPlansDAO.checkId(id);
		if (usrs != null) {
			return true;
		}
		return false;
	}

	protected boolean getUpdate(WealthPlans wp, int flag) {

		if (flag == 1)
			wealthPlansDAO.update(wp);
		else
			wealthPlansDAO.save(wp);
		return true;
	}

	protected boolean getUpdate(AttachedDocuments ad, int flag) {

		if (flag == 1)
			wealthPlansDAO.update(ad);
		else
			wealthPlansDAO.save(ad);
		return true;
	}

	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = wealthPlansDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}

	protected WealthPlans checkWealthPlanID(String wpid) {

		WealthPlans wps = wealthPlansDAO.checkWPID(wpid);
		if (wps != null)
			return wps;
		else
			return null;
	}

	protected AttachedDocuments checkDocumentID(String docId, String wealth_plan_id,String planElement_id) {

		AttachedDocuments ad = wealthPlansDAO.checkDocumentID(docId, wealth_plan_id, planElement_id);
		if (ad != null)
			return ad;
		else
			return null;
	}
	
	protected boolean deleteAllWealthPlanElements(String wpId) {

		try {
			int result = wealthPlansDAO.deleteAllWealthPlanElements(wpId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected boolean unShareWP(String wpId) {

		try {
			int result = wealthPlansDAO.unShareWP(wpId);
			if (result > 0) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void saveOrUpdateWealthplans(JSONObject objects,
			JSONObject jsonObject) {

		Gson gson = new Gson();
		String wealth_plan_id = (String) objects.get("wealth_plan_id");

		if (wealth_plan_id != null) {
			WealthPlans wps1 = checkWealthPlanID(wealth_plan_id);
			if (wps1 != null) {

				if (objects.get("title") != null)
					wps1.setTitle((String) objects.get("title"));
				if (objects.get("description") != null)
					wps1.setDescription((String) objects.get("description"));
				if (objects.get("is_delete") != null)
					wps1.setIs_delete((Long) objects.get("is_delete"));
				if (objects.get("status_id") != null)
					wps1.setStatus_id((Long) objects.get("status_id"));

				wps1.setUpdated_date(String.valueOf(System.currentTimeMillis()));

				
				getUpdate(wps1, 1);
				if(((Long) objects.get("status_id") == 5l) || ((Long) objects.get("status_id") == 6l)){
					unShareWP((String) objects.get("wealth_plan_id"));
				}
				if((Long) objects.get("is_delete") == 1l){
					deleteAllWealthPlanElements((String) objects.get("wealth_plan_id"));
				}
			} else {

				if (objects.get("client_id") != null) {
					if (checkId((String) objects.get("client_id"))) {
						if (objects.get("is_delete") != null) {

							WealthPlans wp = gson.fromJson(
									objects.toJSONString(), WealthPlans.class);

							wp.setAdvisor_id((String) jsonObject
									.get("advisor_id"));
							wp.setStatus_id(4l);
							wp.setCreated_by((String) jsonObject
									.get("advisor_id"));
							if (wp.getCreated_date() == null) {
								wp.setCreated_date(String.valueOf(System
										.currentTimeMillis()));
							}
							wp.setUpdated_date(String.valueOf(System
									.currentTimeMillis()));

							getUpdate(wp, 0);
						}
					}
				}
			}
			if (objects.get("attachments") != null) {
				JSONArray getArray = (JSONArray) objects.get("attachments");

				if (getArray.size() > 0) {
					for (int i = 0; i < getArray.size(); i++) {
						JSONObject jsonObj = (JSONObject) getArray.get(i);
						saveOrUpdateAttachedDocument(jsonObj, (String) objects.get("wealth_plan_id"),
								(Long) objects.get("is_delete"));
					}
				}
			}
		}
	}

	protected void saveOrUpdateAttachedDocument(JSONObject objects,
			String wealth_plan_id, Long isDelete) {

		Gson gson = new Gson();
		String document_id = (String) objects.get("document_id");
		String planElement_id = (String) objects.get("plan_element_id");

		if (document_id != null) {
			if (planElement_id != null) {
				AttachedDocuments ad = checkDocumentID(document_id, wealth_plan_id, planElement_id);
				if (ad != null) {
					if (objects.get("document_name") != null)
						ad.setDocument_name((String) objects.get("document_name"));
					if (objects.get("document_size") != null)
						ad.setDocument_size((String) objects.get("document_size"));
					if (objects.get("status") != null)
						ad.setStatus((Long) objects.get("status"));
					if (isDelete != null)
						ad.setIs_delete(isDelete);
					if (objects.get("created_date") != null)
						ad.setCreated_date((String) objects.get("created_date"));

					ad.setUpdated_date(String.valueOf(System
							.currentTimeMillis()));

					getUpdate(ad, 1);
				} else {

					if (isDelete != null) {
						AttachedDocuments attachDoc = gson
								.fromJson(objects.toJSONString(),
										AttachedDocuments.class);
						attachDoc.setWealth_plan_id(wealth_plan_id);
						attachDoc.setIs_delete(isDelete);
						if (attachDoc.getCreated_date() == null) {
							attachDoc.setCreated_date(String.valueOf(System
									.currentTimeMillis()));
						}
						attachDoc.setUpdated_date(String.valueOf(System
								.currentTimeMillis()));

						getUpdate(attachDoc, 0);
					}
				}
			}
		}
	}
}
