package com.yuncore.bdfs.entity;

import java.io.File;

import org.json.JSONObject;

import com.yuncore.bdfs.tools.Util;

public class BDFSFile implements EntityJSON {

	private String id;

	private String dir;

	private String name;

	private long length;

	private String fId;

	private long session;

	/**
	 * 0文件 1文件夹
	 */
	private int type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isFile() {
		return this.type == 0;
	}

	public boolean isDirectory() {
		return this.type == 1;
	}

	public long getSession() {
		return session;
	}

	public void setSession(long session) {
		this.session = session;
	}

	public String getAbsolutePath() {
		if (dir.endsWith(File.separator)) {
			return dir + name;
		}
		return dir + File.separator + name;

	}

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LocalFile) {
			final LocalFile task = (LocalFile) obj;
			if (task.getType() == task.getType()) {
				return task.getfId() == getfId();
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (dir + File.separator + name).hashCode();
	}

	@Override
	public String toString() {
		return "[dir=" + dir + ", name=" + name + ", length=" + length
				+ ", type=" + type + "]";
	}

	public String toFid() {
		return Util.md5(toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.entity.EntityJSONObject#formJOSN(java.lang.String)
	 */
	@Override
	public boolean formJOSN(String json) {
		return formJOSN(new JSONObject(json));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yuncore.bdfs.entity.EntityJSONObject#formJOSN(org.json.JSONObject)
	 */
	@Override
	public boolean formJOSN(JSONObject object) {
		if (null != object) {
			if (object.has("id")) {
				setId(object.getString("id"));
			}
			if (object.has("dir")) {
				setDir(object.getString("dir"));
			}
			if (object.has("name")) {
				setName(object.getString("name"));
			}
			if (object.has("length")) {
				setLength(object.getLong("length"));
			}
			if (object.has("type")) {
				setType(object.getInt("type"));
			}
			if (object.has("fId")) {
				setfId(object.getString("fId"));
			}
			if (object.has("session")) {
				setSession(object.getLong("session"));
			}
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.entity.EntityJSONObject#toJSON()
	 */
	@Override
	public String toJSON() {
		final JSONObject object = new JSONObject();
		toJSON(object);
		return object.toString();
	}

	@Override
	public void toJSON(JSONObject object) {
		object.put("id", id);
		object.put("dir", dir);
		object.put("name", name);
		object.put("length", length);
		object.put("type", type);
		object.put("fId", fId);
		object.put("session", session);
	}

}
