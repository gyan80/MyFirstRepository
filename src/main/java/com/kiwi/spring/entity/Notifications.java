package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notifications {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "notification_id", nullable = false, length = 255)
	String notification_id;
	
	@Column(name = "user_id", nullable = false, length = 255)
	String user_id;

	@Column(name = "todo_id", nullable = false, length = 255)
	String todo_id;
	
	@Column(name = "wealth_plan_id", nullable = false, length = 255)
	String wealth_plan_id;

	@Column(name = "todo_status", nullable = true, length = 20)
	Long todo_status;
	
	@Column(name = "title", nullable = false, length = 255)
	String title;

	@Column(name = "frequency", nullable = false, length = 255)
	String frequency;

	@Column(name = "is_delete", nullable = true, length = 20)
	Long is_delete;
	
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
	
	public String getNotification_id() {
		return notification_id;
	}

	public void setNotification_id(String notification_id) {
		this.notification_id = notification_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getTodo_id() {
		return todo_id;
	}

	public void setTodo_id(String todo_id) {
		this.todo_id = todo_id;
	}

	public String getWealth_plan_id() {
		return wealth_plan_id;
	}

	public void setWealth_plan_id(String wealth_plan_id) {
		this.wealth_plan_id = wealth_plan_id;
	}

	public Long getTodo_status() {
		return todo_status;
	}

	public void setTodo_status(Long todo_status) {
		this.todo_status = todo_status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
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
	
	@Override
	public String toString() {
		return "{\"notification_id\":\"" + notification_id
				+ "\",\"user_id\":\"" + user_id
				+ "\",\"todo_id\":\"" + todo_id
				+ "\",\"wealth_plan_id\":\"" + wealth_plan_id
				+ "\",\"todo_status\":" + todo_status
				+ ",\"title\":\"" + title
				+ "\",\"frequency\":\"" + frequency
				+ "\",\"is_delete\":" + is_delete
				+ "}";
	}
}
