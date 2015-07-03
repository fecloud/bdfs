package com.yuncore.bdfs.entity;

import org.json.JSONObject;

import com.yuncore.bdfs.util.MD5;

public class BDFSFile implements EntityJSON {

	protected String id;

	protected String path;

	protected long length;

	protected String fId;

	protected long session;

	/**
	 * 0文件 1文件夹
	 */
	protected boolean isdir;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public boolean isDir() {
		return isdir;
	}

	public void setDir(boolean isdir) {
		this.isdir = isdir;
	}

	public long getSession() {
		return session;
	}

	public void setSession(long session) {
		this.session = session;
	}

	public String getAbsolutePath() {
		return path;

	}

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDirectory() {
		return isdir;
	}

	public boolean isFile() {
		return !isdir;
	}

	public String toFid() {
		this.fId = MD5.md5(toString());
		return fId;
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
				this.id = object.getString("id");
			}
			if (object.has("path")) {
				this.path = object.getString("path");
			}
			if (object.has("length")) {
				this.length = object.getLong("length");
			}
			if (object.has("isdir")) {
				this.isdir = object.getBoolean("isdir");
			}
			if (object.has("fId")) {
				this.fId = object.getString("fId");
			}
			if (object.has("session")) {
				this.session = object.getLong("session");
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[path=" + path + ", length=" + length + ", isdir=" + isdir
				+ "]";
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
		object.put("path", path);
		object.put("length", length);
		object.put("isdir", isdir);
		object.put("fId", fId);
		object.put("session", session);
	}

}
