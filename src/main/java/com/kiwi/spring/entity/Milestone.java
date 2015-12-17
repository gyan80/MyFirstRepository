package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONObject;

@Entity
@Table(name = "milestones")
public class Milestone {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "milestone_id", nullable = false, length = 255)
	String milestone_id;

	@Column(name = "wealth_plan_element_id", nullable = false, length = 255)
	String wealth_plan_element_id;

	@Column(name = "title", nullable = true, length = 255)
//	@ColumnTransformer(read = "AES_DECRYPT(title, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String title;

	@Column(name = "is_delete", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long is_delete;

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
	
	public String getMilestone_id() {
		return milestone_id;
	}

	public void setMilestone_id(String milestone_id) {
		this.milestone_id = milestone_id;
	}

	public String getWealth_plan_element_id() {
		return wealth_plan_element_id;
	}

	public void setWealth_plan_element_id(String wealth_plan_element_id) {
		this.wealth_plan_element_id = wealth_plan_element_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Long is_delete) {
		this.is_delete = is_delete;
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
	public JSONObject getJson(ShareWealthPlans swp) {

		JSONObject js = new JSONObject();

		js.put("milestone_id", milestone_id);
		js.put("wealth_plan_element_id", wealth_plan_element_id);
		js.put("title", title);
		js.put("is_delete", is_delete);
		js.put("shared_status", swp.getShared_status());

		return js;
	}
	
	@Override
	public String toString() {
		return "{\"milestone_id\":\"" + milestone_id 
				+ "\",\"wealth_plan_element_id\":\"" + wealth_plan_element_id 
				+ "\",\"title\":\"" + title
				+ "\",\"is_delete\":" + is_delete 
				+ "}";
	}
}
