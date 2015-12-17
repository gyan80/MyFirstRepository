package com.kiwi.spring.util;

public class CustomException {

	private String statusCode;
	private String message;
	private String currentDate;
	private String result;
	
	private static CustomException instance = null;
	
	private CustomException(){}

	public static CustomException getInstance() {
	      if(instance == null) {
	         instance = new CustomException();
	      }
	      return instance;
	}
 
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public void setFields(String sCode, String cDate, String msg, String rslt){
		this.setStatusCode(sCode);
		this.setCurrentDate(cDate);
		this.setMessage(msg);
		this.setResult(rslt);
	}

	@Override
	public String toString() {
	   return "{\"statusCode\":\"" + statusCode +  "\",\"currentDate\":\"" + currentDate +  "\",\"result\":\"" + result +  "\",\"message\":\"" + message + "\"}";
	}
}
