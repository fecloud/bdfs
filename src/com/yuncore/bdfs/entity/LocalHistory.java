package com.yuncore.bdfs.entity;

import java.util.Date;

import org.json.JSONObject;

import com.yuncore.bdfs.tools.Util;

public class LocalHistory implements EntityJSONObject {

	private long id;

	private Date time;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setTime(long time) {
		this.time = new Date(time);
	}

	@Override
	public boolean formJOSN(String json) {
		return false;
	}

	@Override
	public boolean formJOSN(JSONObject object) {
		return false;
	}

	@Override
	public String toJSON() {
		return null;
	}

	@Override
	public void toJSON(JSONObject object) {
	}

	public JSONObject toJSONObject() {
		final JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("time", Util.formatTime(time.getTime()));
		return object;
	}

}
