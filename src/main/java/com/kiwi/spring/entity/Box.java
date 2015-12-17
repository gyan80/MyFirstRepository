package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "box_data")
public class Box {

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "access_token", nullable = false, length = 255)
	String access_token;

	@Column(name = "refresh_token", nullable = false, length = 255)
	String refresh_token;

	@Column(name = "updated_date", nullable = true, length = 255)
	String updated_date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}

/*	@Override
	public String toString() {
		return "{\"box_access_token\":\"" + access_token
				+ "\", \"timestamp\":" + updated_date 
				+ "}";
	}*/
}
