package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONObject;

@Entity
@Table(name = "wealth_plan_elements")
public class WealthPlanElements {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "wealth_plan_element_id", nullable = false, length = 255)
	String wealth_plan_element_id;

	@Column(name = "wealth_plan_id", nullable = false, length = 255)
	String wealth_plan_id;

	@Column(name = "plan_element_id", nullable = false, length = 255)
	String plan_element_id;

	//@ColumnTransformer(read = "AES_DECRYPT(goal, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	@Column(name = "goal", nullable = true, length = 255)
	String goal;

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
	
	public String getWealth_plan_element_id() {
		return wealth_plan_element_id;
	}

	public void setWealth_plan_element_id(String wealth_plan_element_id) {
		this.wealth_plan_element_id = wealth_plan_element_id;
	}

	public String getWealth_plan_id() {
		return wealth_plan_id;
	}

	public void setWealth_plan_id(String wealth_plan_id) {
		this.wealth_plan_id = wealth_plan_id;
	}

	public String getPlan_element_id() {
		return plan_element_id;
	}

	public void setPlan_element_id(String plan_element_id) {
		this.plan_element_id = plan_element_id;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
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

		js.put("wealth_plan_element_id", wealth_plan_element_id);
		js.put("wealth_plan_id", wealth_plan_id);
		js.put("plan_element_id", plan_element_id);
		js.put("goal", goal);
		js.put("is_delete", is_delete);
		if(swp != null)
			js.put("shared_status", swp.getShared_status());

		return js;
	}

	@Override
	public String toString() {
		return "{\"wealth_plan_element_id\":\"" + wealth_plan_element_id
				+ "\",\"wealth_plan_id\":\"" + wealth_plan_id
				+ "\",\"plan_element_id\":\"" + plan_element_id
				+ "\",\"goal\":\"" + goal 
				+ "\",\"is_delete\":" + is_delete
				+ "}";
	}

}
