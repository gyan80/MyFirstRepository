package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONObject;

@Entity
@Table(name = "plan_elements")
public class PlanElement {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "plan_element_id", nullable = false, length = 255)
	String plan_element_id;

	@Column(name = "advisor_id", nullable = false, length = 255)
	String advisor_id;

	@Column(name = "title", nullable = true, length = 255)
	//@ColumnTransformer(read = "AES_DECRYPT(title, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String title;

	@Column(name = "status_id", nullable = false, length = 20)
	Long status_id;

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
	
	public String getPlan_element_id() {
		return plan_element_id;
	}

	public void setPlan_element_id(String plan_element_id) {
		this.plan_element_id = plan_element_id;
	}

	public String getAdvisor_id() {
		return advisor_id;
	}

	public void setAdvisor_id(String advisor_id) {
		this.advisor_id = advisor_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

		js.put("plan_element_id", plan_element_id);
		js.put("advisor_id", advisor_id);
		js.put("title", title);
		js.put("title", title);
		js.put("is_delete", is_delete);
		js.put("shared_status", swp.getShared_status());
		
		return js;
	}
	
	@Override
	public String toString() {
		return "{\"plan_element_id\":\"" + plan_element_id 
				+ "\",\"title\":\"" + title
				+ "\",\"advisor_id\":\"" + advisor_id 
				+ "\",\"is_delete\":" + is_delete
				+ "}";
	}

}
