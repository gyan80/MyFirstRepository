package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.simple.JSONObject;

@Entity
@Table(name = "attached_document")
public class AttachedDocuments {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "document_id", nullable = false, length = 255)
	String document_id;
	
	@Column(name = "wealth_plan_id", nullable = false, length = 255)
	String wealth_plan_id;
	
	@Column(name = "plan_element_id", nullable = true, length = 255)
	String plan_element_id;

	@Column(name = "document_name", nullable = false, length = 255)
	String document_name;

	@Column(name = "status", nullable = false, length = 20)
	Long status;
	
	@Column(name = "is_delete", nullable = false, length = 20)
	Long is_delete;
	
	@Column(name = "document_size", nullable = false, length = 255)
	String document_size;

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

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
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

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Long is_delete) {
		this.is_delete = is_delete;
	}

	public String getDocument_size() {
		return document_size;
	}

	public void setDocument_size(String document_size) {
		this.document_size = document_size;
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
	public JSONObject getJson() {

		JSONObject js = new JSONObject();

		js.put("document_id", document_id);
		js.put("document_name", document_name);
		js.put("document_size", document_size);
		js.put("plan_element_id", plan_element_id);
		js.put("status", status);
		js.put("created_date", created_date);
		js.put("is_delete", is_delete);
		
		return js;
	}

}
