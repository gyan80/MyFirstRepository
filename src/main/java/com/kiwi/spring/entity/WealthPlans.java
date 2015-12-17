package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Entity
@Table(name = "wealth_plans")
public class WealthPlans {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "wealth_plan_id", nullable = false, length = 255)
	String wealth_plan_id;

	@Column(name = "advisor_id", nullable = false, length = 255)
	String advisor_id;

	@Column(name = "client_id", nullable = false, length = 255)
	String client_id;

	@Column(name = "title", nullable = true, length = 255)
	// @ColumnTransformer(read =
	// "AES_DECRYPT(title, 'kEyLI1Fy648tzWXGuRcxrg==')", write =
	// "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String title;

	@Column(name = "description", nullable = true, length = 4000)
	// @ColumnTransformer(read =
	// "AES_DECRYPT(description, 'kEyLI1Fy648tzWXGuRcxrg==')", write =
	// "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String description;

	@Column(name = "status_id", nullable = false, length = 20)
	Long status_id;

	@Column(name = "is_delete", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long is_delete;

	@Column(name = "created_by", nullable = false, length = 255)
	String created_by;

	@Column(name = "created_date", nullable = true, length = 255)
	String created_date;

	@Column(name = "updated_date", nullable = true, length = 255)
	String updated_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWealth_plan_id() {
		return wealth_plan_id;
	}

	public void setWealth_plan_id(String wealth_plan_id) {
		this.wealth_plan_id = wealth_plan_id;
	}

	public String getAdvisor_id() {
		return advisor_id;
	}

	public void setAdvisor_id(String advisor_id) {
		this.advisor_id = advisor_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Long status_id) {
		this.status_id = status_id;
	}

	public Long getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Long is_delete) {
		this.is_delete = is_delete;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getJson(ShareWealthPlans swp, String pic) {

		Long val = swp.getFull_plan();
		if (val == null)
			val = 0l;

		JSONObject js = new JSONObject();

		js.put("wealth_plan_id", wealth_plan_id);
		js.put("advisor_id", advisor_id);
		js.put("client_id", client_id);
		js.put("title", title);
		js.put("description", description);
		js.put("is_delete", is_delete);
		js.put("status_id", status_id);
		js.put("full_plan", val);
		js.put("shared_status", swp.getShared_status());
		js.put("profile_picture", pic);

		return js;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJsonWithProfilePicture(String pic, JSONArray jsonArray, int shared_status) {

		JSONObject js = new JSONObject();

		js.put("wealth_plan_id", wealth_plan_id);
		js.put("advisor_id", advisor_id);
		js.put("client_id", client_id);
		js.put("title", title);
		js.put("description", description);
		js.put("is_delete", is_delete);
		js.put("status_id", status_id);
		js.put("profile_picture", pic); 
		js.put("attachments", jsonArray);
		js.put("shared_status", shared_status);
		
		return js;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJsonWithProfilePicture(String pic, JSONArray jsonArray) {

		JSONObject js = new JSONObject();

		js.put("wealth_plan_id", wealth_plan_id);
		js.put("advisor_id", advisor_id);
		js.put("client_id", client_id);
		js.put("title", title);
		js.put("description", description);
		js.put("is_delete", is_delete);
		js.put("status_id", status_id);
		js.put("profile_picture", pic); 
		js.put("attachments", jsonArray);

		return js;
	}

	@Override
	public String toString() {
		return "{\"wealth_plan_id\":\"" + wealth_plan_id
				+ "\",\"advisor_id\":\"" + advisor_id 
				+ "\",\"client_id\":\""	+ client_id 
				+ "\",\"title\":\"" + title	
				+ "\",\"description\":\"" + description 
				+ "\",\"is_delete\":" + is_delete 
				+ ",\"status_id\":" + status_id + "}";
	}
}
