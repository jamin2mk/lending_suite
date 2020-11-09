package ru.bpmink.bpm.model.auth;

import com.google.gson.annotations.SerializedName;

import ru.bpmink.bpm.model.common.RestEntity;

public class Authentication extends RestEntity {

	public Authentication() {
		// TODO Auto-generated constructor stub
	}

	@SerializedName("csrf_token")
	private String csrf_token;

	@SerializedName("expiration")
	private String expiration;

	public String getCsrfToken() {
		return csrf_token;
	}

	public void setCsrfToken(String csrf_token) {
		this.csrf_token = csrf_token;
	}

	public String getExpiration() {
		return expiration;
	}

	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

}
