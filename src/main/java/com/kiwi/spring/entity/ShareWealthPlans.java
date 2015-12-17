package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "share_plans")
public class ShareWealthPlans {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "advisor_id", nullable = false, length = 255)
	String advisor_id;

	@Column(name = "client_id", nullable = false, length = 255)
	String client_id;

	@Column(name = "shared_to", nullable = false, length = 255)
	String shared_to;
	
	@Column(name = "shared_by", nullable = false, length = 255)
	String shared_by;

	@Column(name = "wealth_plan_id", nullable = false, length = 255)
	String wealth_plan_id;
	
	@Column(name = "plan_element_id", nullable = true, length = 255)
	String plan_element_id;

	@Column(name = "shared_status", nullable = false, length = 1)
	Long shared_status;
	
	@Column(name = "full_plan", nullable = false, length = 1)
	Long full_plan;

	@Column(name = "created_date", nullable = false, length = 255)
	String created_date;
	
	@Column(name = "updated_date", nullable = false, length = 255)
	String updated_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getShared_to() {
		return shared_to;
	}

	public void setShared_to(String shared_to) {
		this.shared_to = shared_to;
	}

	public String getShared_by() {
		return shared_by;
	}

	public void setShared_by(String shared_by) {
		this.shared_by = shared_by;
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

	public Long getShared_status() {
		return shared_status;
	}

	public void setShared_status(Long shared_status) {
		this.shared_status = shared_status;
	}

	public Long getFull_plan() {
		return full_plan;
	}

	public void setFull_plan(Long full_plan) {
		this.full_plan = full_plan;
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

	@Override
	public String toString() {
		return "{\"shared_by\":\"" + shared_by
				+ "\",\"advisor_id\":\"" + advisor_id
				+ "\",\"client_id\":\"" + client_id
				+ "\",\"shared_to\":\"" + shared_to 
				+ "\",\"wealth_plan_id\":\""	+ wealth_plan_id 
				+ "\",\"plan_element_id\":\"" + plan_element_id
				+ "\",\"shared_status\":" + shared_status 
				+ ",\"full_plan\":" + full_plan 
				+ "}";
	}
}
