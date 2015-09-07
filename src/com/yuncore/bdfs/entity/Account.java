package com.yuncore.bdfs.entity;

import org.json.JSONObject;

public class Account implements EntityJSON {

	private String username;

	private String password;

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

	@Override
	public boolean formJOSN(String json) {
		return formJOSN(new JSONObject(json));
	}

	@Override
	public boolean formJOSN(JSONObject object) {
		if (object.has("username")) {
			this.username = object.getString("username");
		}
		if (object.has("password")) {
			this.password = object.getString("password");
		}
		return true;
	}

	@Override
	public String toJSON() {
		final JSONObject object = new JSONObject();
		toJSON(object);
		return object.toString();
	}

	@Override
	public void toJSON(JSONObject object) {
		object.put("username", username);
		object.put("password", password);
	}

}
