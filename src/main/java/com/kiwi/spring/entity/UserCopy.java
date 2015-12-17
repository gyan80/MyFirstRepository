package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "users")
public class UserCopy {

	@Id
	@GeneratedValue
	Long id;
	
	@Column(name = "user_id", nullable = false, length = 255)
	String user_id;

	@Column(name = "advisor_id", nullable = false, length = 255)
	String advisor_id;

	@Column(name = "status_id", nullable = true, length = 20)
	Long status_id;
	
	@Column(name = "is_delete", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long is_delete;
	
	@Column(name = "profile_picture", nullable = true, length = 255)
	String profile_picture;
	
	@Column(name = "email", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(email, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String email;

	@Column(name = "title", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(title, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String title;

	@Column(name = "first_name", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(first_name, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String first_name;

	@Column(name = "middle_name", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(middle_name, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String middle_name;

	@Column(name = "last_name", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(last_name, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String last_name;
	
	@Column(name = "dob", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(dob, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String dob;

	@Column(name = "relation_to_self", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(relation_to_self, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String relation_to_self;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAdvisor_id() {
		return advisor_id;
	}

	public void setAdvisor_id(String advisor_id) {
		this.advisor_id = advisor_id;
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

	public String getProfile_picture() {
		return profile_picture;
	}

	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getRelation_to_self() {
		return relation_to_self;
	}

	public void setRelation_to_self(String relation_to_self) {
		this.relation_to_self = relation_to_self;
	}

}
