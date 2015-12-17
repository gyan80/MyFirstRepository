package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class Messages {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "message_id", nullable = false, length = 255)
	String message_id;
	
	@Column(name = "wealth_plan_element_id", nullable = false, length = 255)
	String wealth_plan_element_id;
	
	@Column(name = "user_id", nullable = false, length = 255)
	String user_id;

	@Column(name = "message", nullable = true)
	String message;

	@Column(name = "created_date", nullable = false, length = 255)
	String created_date;
	
	@Column(name = "updated_date", nullable = true, length = 255)
	String updated_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getWealth_plan_element_id() {
		return wealth_plan_element_id;
	}

	public void setWealth_plan_element_id(String wealth_plan_element_id) {
		this.wealth_plan_element_id = wealth_plan_element_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
}
