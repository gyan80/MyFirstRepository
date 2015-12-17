package com.kiwi.spring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnTransformer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Entity
@Table(name = "users")
public class User {

	transient String shared_id; 
	
	public String getShared_id() {
		return shared_id;
	}

	public void setShared_id(String shared_id) {
		this.shared_id = shared_id;
	}

	@Id
	@GeneratedValue
	Long id;

	@Column(name = "user_id", nullable = false, length = 255)
	String user_id;

	@Column(name = "advisor_id", nullable = false, length = 255)
	String advisor_id;

	@Column(name = "quick_box_id", nullable = true, length = 20)
	Long quick_box_id;

	@Column(name = "username", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(username, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String username;

	@Column(name = "password", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(password, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String password;

	@Column(name = "email", nullable = false, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(email, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String email;

	@Column(name = "forgot_token", nullable = true, length = 255)
	String forgot_token;

	@Column(name = "password_reset", nullable = true, length = 20)
	Long password_reset;

	@Column(name = "role_id", nullable = true, length = 20)
	Long role_id;

	@Column(name = "status_id", nullable = true, length = 20)
	Long status_id;

	@Column(name = "created_date", nullable = true, length = 255)
	String created_date;

	@Column(name = "updated_date", nullable = true, length = 255)
	String updated_date;

	@Column(name = "profile_picture", nullable = true, length = 255)
	String profile_picture;

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

	@Column(name = "address", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(address, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String address;

	@Column(name = "city", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(city, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String city;

	@Column(name = "state", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(state, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String state;

	@Column(name = "zip", nullable = true, length = 20)
	@ColumnTransformer(read = "AES_DECRYPT(zip, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	Long zip;

	@Column(name = "ssn", nullable = true, length = 20)
	@ColumnTransformer(read = "AES_DECRYPT(ssn, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	Long ssn;

	@Column(name = "dob", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(dob, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String dob;

	@Column(name = "telephone", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(telephone, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String telephone;

	@Column(name = "marital_status", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(marital_status, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String marital_status;

	@Column(name = "currently_employed", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(currently_employed, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String currently_employed;

	@Column(name = "employer_name", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(employer_name, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String employer_name;

	@Column(name = "type_of_business", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(type_of_business, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String type_of_business;

	@Column(name = "employer_address", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(employer_address, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String employer_address;

	@Column(name = "employer_city", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(employer_city, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String employer_city;

	@Column(name = "employer_state", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(employer_state, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String employer_state;

	@Column(name = "employer_zip", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(employer_zip, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String employer_zip;

	@Column(name = "occupation", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(occupation, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String occupation;

	@Column(name = "age_of_retirement", nullable = true, length = 255)
	@ColumnTransformer(read = "AES_DECRYPT(age_of_retirement, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String age_of_retirement;

	@Column(name = "spouse", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(spouse, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String spouse;

	@Column(name = "family", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(family, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String family;

	@Column(name = "relation_to_self", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(relation_to_self, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String relation_to_self;

	@Column(name = "income", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(income, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String income;

	@Column(name = "future_income", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(future_income, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String future_income;

	@Column(name = "projected_pension", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(projected_pension, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String projected_pension;

	@Column(name = "working_during_retirement", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(working_during_retirement, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String working_during_retirement;

	@Column(name = "expected_windfalls", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(expected_windfalls, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String expected_windfalls;

	@Column(name = "other", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(other, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String other;

	@Column(name = "expense", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(expense, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String expense;

	@Column(name = "education_expenses", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(education_expenses, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String education_expenses;

	@Column(name = "miscellaneous_expenses", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(miscellaneous_expenses, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String miscellaneous_expenses;

	@Column(name = "retirement_savings", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(retirement_savings, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String retirement_savings;

	@Column(name = "other_savings_investments", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(other_savings_investments, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String other_savings_investments;

	@Column(name = "real_estate", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(real_estate, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String real_estate;

	@Column(name = "other_real_estate", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(other_real_estate, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String other_real_estate;

	@Column(name = "business_interests", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(business_interests, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String business_interests;

	@Column(name = "additional_assets", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(additional_assets, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String additional_assets;

	@Column(name = "current_debt", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(current_debt, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String current_debt;

	@Column(name = "life_insurance", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(life_insurance, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String life_insurance;

	@Column(name = "medical_insurance", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(medical_insurance, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String medical_insurance;

	@Column(name = "property_insurance", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(property_insurance, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String property_insurance;

	@Column(name = "notes", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(notes, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String notes;

	@Column(name = "home_ext", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(home_ext, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String home_ext;

	@Column(name = "home_telephone", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(home_telephone, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String home_telephone;

	@Column(name = "work_ext", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(work_ext, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String work_ext;

	@Column(name = "work_telephone", nullable = true)
	@ColumnTransformer(read = "AES_DECRYPT(work_telephone, 'kEyLI1Fy648tzWXGuRcxrg==')", write = "AES_ENCRYPT(?, 'kEyLI1Fy648tzWXGuRcxrg==')")
	String work_telephone;

	@Column(name = "is_delete", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long is_delete;
	
	@Column(name = "add_to_network_again", nullable = false, length = 20, columnDefinition = "BIGINT default 0")
	Long add_to_network_again;
	
	@Column(name = "logo", nullable = true, length = 255)
	String logo;
	
	@Column(name = "device_wipe", nullable = false, length = 2, columnDefinition = "BIGINT default 0")
	Long device_wipe;

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

	public Long getQuick_box_id() {
		return quick_box_id;
	}

	public void setQuick_box_id(Long quick_box_id) {
		this.quick_box_id = quick_box_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getForgot_token() {
		return forgot_token;
	}

	public void setForgot_token(String forgot_token) {
		this.forgot_token = forgot_token;
	}

	public Long getPassword_reset() {
		return password_reset;
	}

	public void setPassword_reset(Long password_reset) {
		this.password_reset = password_reset;
	}

	public Long getRole_id() {
		return role_id;
	}

	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

	public Long getStatus_id() {
		return status_id;
	}

	public void setStatus_id(Long status_id) {
		this.status_id = status_id;
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

	public String getProfile_picture() {
		return profile_picture;
	}

	public void setProfile_picture(String profile_picture) {
		this.profile_picture = profile_picture;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Long getZip() {
		return zip;
	}

	public void setZip(Long zip) {
		this.zip = zip;
	}

	public Long getSsn() {
		return ssn;
	}

	public void setSsn(Long ssn) {
		this.ssn = ssn;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMarital_status() {
		return marital_status;
	}

	public void setMarital_status(String marital_status) {
		this.marital_status = marital_status;
	}

	public String getCurrently_employed() {
		return currently_employed;
	}

	public void setCurrently_employed(String currently_employed) {
		this.currently_employed = currently_employed;
	}

	public String getEmployer_name() {
		return employer_name;
	}

	public void setEmployer_name(String employer_name) {
		this.employer_name = employer_name;
	}

	public String getType_of_business() {
		return type_of_business;
	}

	public void setType_of_business(String type_of_business) {
		this.type_of_business = type_of_business;
	}

	public String getEmployer_address() {
		return employer_address;
	}

	public void setEmployer_address(String employer_address) {
		this.employer_address = employer_address;
	}

	public String getEmployer_city() {
		return employer_city;
	}

	public void setEmployer_city(String employer_city) {
		this.employer_city = employer_city;
	}

	public String getEmployer_state() {
		return employer_state;
	}

	public void setEmployer_state(String employer_state) {
		this.employer_state = employer_state;
	}

	public String getEmployer_zip() {
		return employer_zip;
	}

	public void setEmployer_zip(String employer_zip) {
		this.employer_zip = employer_zip;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getAge_of_retirement() {
		return age_of_retirement;
	}

	public void setAge_of_retirement(String age_of_retirement) {
		this.age_of_retirement = age_of_retirement;
	}

	public String getSpouse() {
		return spouse;
	}

	public void setSpouse(String spouse) {
		this.spouse = spouse;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getRelation_to_self() {
		return relation_to_self;
	}

	public void setRelation_to_self(String relation_to_self) {
		this.relation_to_self = relation_to_self;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getFuture_income() {
		return future_income;
	}

	public void setFuture_income(String future_income) {
		this.future_income = future_income;
	}

	public String getProjected_pension() {
		return projected_pension;
	}

	public void setProjected_pension(String projected_pension) {
		this.projected_pension = projected_pension;
	}

	public String getWorking_during_retirement() {
		return working_during_retirement;
	}

	public void setWorking_during_retirement(String working_during_retirement) {
		this.working_during_retirement = working_during_retirement;
	}

	public String getExpected_windfalls() {
		return expected_windfalls;
	}

	public void setExpected_windfalls(String expected_windfalls) {
		this.expected_windfalls = expected_windfalls;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getExpense() {
		return expense;
	}

	public void setExpense(String expense) {
		this.expense = expense;
	}

	public String getEducation_expenses() {
		return education_expenses;
	}

	public void setEducation_expenses(String education_expenses) {
		this.education_expenses = education_expenses;
	}

	public String getMiscellaneous_expenses() {
		return miscellaneous_expenses;
	}

	public void setMiscellaneous_expenses(String miscellaneous_expenses) {
		this.miscellaneous_expenses = miscellaneous_expenses;
	}

	public String getRetirement_savings() {
		return retirement_savings;
	}

	public void setRetirement_savings(String retirement_savings) {
		this.retirement_savings = retirement_savings;
	}

	public String getOther_savings_investments() {
		return other_savings_investments;
	}

	public void setOther_savings_investments(String other_savings_investments) {
		this.other_savings_investments = other_savings_investments;
	}

	public String getReal_estate() {
		return real_estate;
	}

	public void setReal_estate(String real_estate) {
		this.real_estate = real_estate;
	}

	public String getOther_real_estate() {
		return other_real_estate;
	}

	public void setOther_real_estate(String other_real_estate) {
		this.other_real_estate = other_real_estate;
	}

	public String getBusiness_interests() {
		return business_interests;
	}

	public void setBusiness_interests(String business_interests) {
		this.business_interests = business_interests;
	}

	public String getAdditional_assets() {
		return additional_assets;
	}

	public void setAdditional_assets(String additional_assets) {
		this.additional_assets = additional_assets;
	}

	public String getCurrent_debt() {
		return current_debt;
	}

	public void setCurrent_debt(String current_debt) {
		this.current_debt = current_debt;
	}

	public String getLife_insurance() {
		return life_insurance;
	}

	public void setLife_insurance(String life_insurance) {
		this.life_insurance = life_insurance;
	}

	public String getMedical_insurance() {
		return medical_insurance;
	}

	public void setMedical_insurance(String medical_insurance) {
		this.medical_insurance = medical_insurance;
	}

	public String getProperty_insurance() {
		return property_insurance;
	}

	public void setProperty_insurance(String property_insurance) {
		this.property_insurance = property_insurance;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getHome_ext() {
		return home_ext;
	}

	public void setHome_ext(String home_ext) {
		this.home_ext = home_ext;
	}

	public String getHome_telephone() {
		return home_telephone;
	}

	public void setHome_telephone(String home_telephone) {
		this.home_telephone = home_telephone;
	}

	public String getWork_ext() {
		return work_ext;
	}

	public void setWork_ext(String work_ext) {
		this.work_ext = work_ext;
	}

	public String getWork_telephone() {
		return work_telephone;
	}

	public void setWork_telephone(String work_telephone) {
		this.work_telephone = work_telephone;
	}

	public Long getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Long is_delete) {
		this.is_delete = is_delete;
	}

	public Long getAdd_to_network_again() {
		return add_to_network_again;
	}

	public void setAdd_to_network_again(Long add_to_network_again) {
		this.add_to_network_again = add_to_network_again;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Long getDevice_wipe() {
		return device_wipe;
	}

	public void setDevice_wipe(Long device_wipe) {
		this.device_wipe = device_wipe;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUserJson(String profilePic, String advisorProfilePic, String logo) throws ParseException{
		
		JSONObject js = new JSONObject();
		JSONParser parser = new JSONParser();
		
		js.put("user_id", user_id);
		js.put("advisor_id", advisor_id);
		js.put("is_delete", is_delete);
		js.put("quick_box_id", quick_box_id);
		js.put("username", username);
		js.put("created_date", created_date);
		js.put("status_id", status_id);
		js.put("email", email);
		js.put("profile_picture", profilePic);
		js.put("title", title);
		js.put("first_name", first_name);
		js.put("middle_name", middle_name);
		js.put("last_name", last_name);
		js.put("address", address);
		js.put("city", city);
		js.put("state", state);
		js.put("zip", zip);
		js.put("ssn", ssn);
		js.put("dob", dob);
		js.put("telephone", telephone);
		js.put("home_ext", home_ext);
		js.put("home_telephone", home_telephone);
		js.put("work_ext", work_ext);
		js.put("work_telephone", work_telephone);
		js.put("marital_status", marital_status);
		js.put("currently_employed", currently_employed);
		js.put("employer_name", employer_name);
		js.put("type_of_business", type_of_business);
		js.put("employer_address", employer_address);
		js.put("employer_city", employer_city);
		js.put("employer_state", employer_state);
		js.put("employer_zip", employer_zip);
		js.put("occupation", occupation);
		js.put("age_of_retirement", age_of_retirement);
		
		if(spouse == null)
			js.put("spouse", null);
		else
			js.put("spouse", (JSONObject) parser.parse(spouse));
		
		if(family == null)
			js.put("family", null);
		else
			js.put("family", (JSONArray) parser.parse(family));
		
		if(income == null)
			js.put("income", null);
		else
			js.put("income", (JSONArray) parser.parse(income));
		
		if(future_income == null)
			js.put("future_income", null);
		else
			js.put("future_income", (JSONObject) parser.parse(future_income));
		
		if(projected_pension == null)
			js.put("projected_pension", null);
		else
			js.put("projected_pension", (JSONObject) parser.parse(projected_pension));
		
		if(working_during_retirement == null)
			js.put("working_during_retirement", null);
		else
			js.put("working_during_retirement", (JSONObject) parser.parse(working_during_retirement));
		
		if(expected_windfalls == null)
			js.put("expected_windfalls", null);
		else
			js.put("expected_windfalls", (JSONArray) parser.parse(expected_windfalls));
		
		if(other == null)
			js.put("other", null);
		else
			js.put("other", (JSONArray) parser.parse(other));
		
		if(expense == null)
			js.put("expense", null);
		else
			js.put("expense", (JSONObject) parser.parse(expense));
		
		if(education_expenses == null)
			js.put("education_expenses", null);
		else
			js.put("education_expenses", (JSONArray) parser.parse(education_expenses));
		
		if(miscellaneous_expenses == null)
			js.put("miscellaneous_expenses", null);
		else
			js.put("miscellaneous_expenses", (JSONArray) parser.parse(miscellaneous_expenses));
		
		if(retirement_savings == null)
			js.put("retirement_savings", null);
		else
			js.put("retirement_savings", (JSONArray) parser.parse(retirement_savings));
		
		if(other_savings_investments == null)
			js.put("other_savings_investments", null);
		else
			js.put("other_savings_investments", (JSONArray) parser.parse(other_savings_investments));
		
		if(real_estate == null)
			js.put("real_estate", null);
		else
			js.put("real_estate", (JSONObject) parser.parse(real_estate));
		
		if(other_real_estate == null)
			js.put("other_real_estate", null);
		else
			js.put("other_real_estate", (JSONArray) parser.parse(other_real_estate));
		
		if(business_interests == null)
			js.put("business_interests", null);
		else
			js.put("business_interests", (JSONArray) parser.parse(business_interests));
		
		if(additional_assets == null)
			js.put("additional_assets", null);
		else
			js.put("additional_assets", (JSONArray) parser.parse(additional_assets));
		
		if(current_debt == null)
			js.put("current_debt", null);
		else
			js.put("current_debt", (JSONArray) parser.parse(current_debt));
		
		if(life_insurance == null)
			js.put("life_insurance", null);
		else
			js.put("life_insurance", (JSONArray) parser.parse(life_insurance));
		
		if(medical_insurance == null)
			js.put("medical_insurance", null);
		else
			js.put("medical_insurance", (JSONArray) parser.parse(medical_insurance));
			
		if(property_insurance == null)
			js.put("property_insurance", null);
		else
			js.put("property_insurance", (JSONArray) parser.parse(property_insurance));
		
		js.put("relation_to_self", relation_to_self);
		js.put("advisor_profile_pic", advisorProfilePic);
		js.put("logo", logo);
		js.put("notes", notes);
		
		return js;
	}
	
	@Override
	public String toString() {
		return "{\"user_id\":\"" + user_id + "\",\"advisor_id\":\""
				+ advisor_id + "\",\"is_delete\":" + is_delete
				+ ",\"role_id\":" + role_id
				+ ",\"quick_box_id\":" + quick_box_id + ",\"username\":\""
				+ username + "\",\"created_date\":\"" + created_date
				+ "\",\"status_id\":" + status_id + ",\"email\":\"" + email
				+ "\",\"profile_picture\":\"" + profile_picture
				+ "\",\"title\":\"" + title + "\",\"first_name\":\""
				+ first_name + "\",\"middle_name\":\"" + middle_name
				+ "\",\"last_name\":\"" + last_name + "\",\"address\":\""
				+ address + "\",\"city\":\"" + city + "\",\"state\":\"" + state
				+ "\",\"zip\":" + zip + ",\"ssn\":" + ssn + ",\"dob\":\"" + dob
				+ "\",\"telephone\":\"" + telephone + "\",\"home_ext\":\""
				+ home_ext + "\",\"home_telephone\":\"" + home_telephone
				+ "\",\"work_ext\":\"" + work_ext + "\",\"work_telephone\":\""
				+ work_telephone + "\",\"marital_status\":\"" + marital_status
				+ "\",\"currently_employed\":\"" + currently_employed
				+ "\",\"employer_name\":\"" + employer_name
				+ "\",\"type_of_business\":\"" + type_of_business
				+ "\",\"employer_address\":\"" + employer_address
				+ "\",\"employer_city\":\"" + employer_city
				+ "\",\"employer_state\":\"" + employer_state
				+ "\",\"employer_zip\":\"" + employer_zip
				+ "\",\"occupation\":\"" + occupation
				+ "\",\"age_of_retirement\":\"" + age_of_retirement
				+ "\",\"spouse\":" + spouse + ",\"family\":" + family
				+ ",\"income\":" + income + ",\"future_income\":"
				+ future_income + ",\"projected_pension\":" + projected_pension
				+ ",\"working_during_retirement\":" + working_during_retirement
				+ ",\"expected_windfalls\":" + expected_windfalls
				+ ",\"other\":" + other + ",\"expense\":" + expense
				+ ",\"education_expenses\":" + education_expenses
				+ ",\"miscellaneous_expenses\":" + miscellaneous_expenses
				+ ",\"retirement_savings\":" + retirement_savings
				+ ",\"other_savings_investments\":" + other_savings_investments
				+ ",\"real_estate\":" + real_estate + ",\"other_real_estate\":"
				+ other_real_estate + ",\"business_interests\":"
				+ business_interests + ",\"additional_assets\":"
				+ additional_assets + ",\"current_debt\":" + current_debt
				+ ",\"life_insurance\":" + life_insurance
				+ ",\"medical_insurance\":" + medical_insurance
				+ ",\"property_insurance\":" + property_insurance
				+ ",\"relation_to_self\":\"" + relation_to_self
				+ "\",\"shared_id\":\"" + shared_id
				+ "\",\"notes\":\"" + notes + "\"}";
	}

}
