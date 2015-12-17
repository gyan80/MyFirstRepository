package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.simple.JSONObject;

@Entity
@Table(name = "box_folders")
public class BoxFolders {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "as_user", nullable = false, length = 255)
	String as_user;

	@Column(name = "shared_id", nullable = false)
	String shared_id;
	
	@Column(name = "private_id", nullable = false)
	String private_id;

	@Column(name = "user_id", nullable = false, length = 255)
	String user_id;

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

	public String getAs_user() {
		return as_user;
	}

	public void setAs_user(String as_user) {
		this.as_user = as_user;
	}

	public String getShared_id() {
		return shared_id;
	}

	public void setShared_id(String shared_id) {
		this.shared_id = shared_id;
	}

	public String getPrivate_id() {
		return private_id;
	}

	public void setPrivate_id(String private_id) {
		this.private_id = private_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
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

		js.put("shared_folder", shared_id);
		
		return js;
	}

	@Override
	public String toString() {
		return "{\"private_folder\":" + private_id
				+ ",\"shared_folder\":" + shared_id 
				+ "}";
	}
}
