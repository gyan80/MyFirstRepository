package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONObject;

@Entity
@Table(name = "todos")
public class Todo {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "todo_id", nullable = false, length = 255)
	String todo_id;

	@Column(name = "milestone_id", nullable = false, length = 255)
	String milestone_id;

	@Column(name = "title", nullable = true, length = 255)
	//@ColumnTransformer(read = "AES_DECRYPT(title, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String title;

	@Column(name = "owner", nullable = true, length = 255)
	//@ColumnTransformer(read = "AES_DECRYPT(owner, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String owner;

	@Column(name = "due_date", nullable = true, length = 255)
	String due_date;

	@Column(name = "status", nullable = false, length = 20)
	Long status;
	
	@Column(name = "priority", nullable = false, length = 10)
	Long priority;

	@Column(name = "is_delete", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long is_delete;

	@Column(name = "notification_entry", nullable = false, length = 255)
	String notification_entry;
	
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
	
	public String getTodo_id() {
		return todo_id;
	}

	public void setTodo_id(String todo_id) {
		this.todo_id = todo_id;
	}

	public String getMilestone_id() {
		return milestone_id;
	}

	public void setMilestone_id(String milestone_id) {
		this.milestone_id = milestone_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public Long getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Long is_delete) {
		this.is_delete = is_delete;
	}

	public String getNotification_entry() {
		return notification_entry;
	}

	public void setNotification_entry(String notification_entry) {
		this.notification_entry = notification_entry;
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

		js.put("todo_id", todo_id);
		js.put("milestone_id", milestone_id);
		js.put("title", title);
		js.put("owner", owner);
		js.put("status", status);
		js.put("priority", priority);
		js.put("is_delete", is_delete);
		js.put("notification_entry", notification_entry);
		js.put("due_date", due_date);
		js.put("shared_status", swp.getShared_status());

		return js;
	}
	
	@Override
	public String toString() {
		return "{\"todo_id\":\"" + todo_id 
				+ "\",\"milestone_id\":\"" + milestone_id 
				+ "\",\"title\":\"" + title
				+ "\",\"owner\":\"" + owner 
				+ "\",\"status\":" + status 
				+ ",\"priority\":" + priority
				+ ",\"is_delete\":"	+ is_delete
				+ ",\"notification_entry\":\""	+ notification_entry
				+ "\",\"due_date\":\"" + due_date
				+ "\"}";
	}
}
