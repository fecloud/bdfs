package com.yuncore.bdfs.tools;

import org.json.JSONObject;

import com.yuncore.bdfs.entity.EntityJSONObject;

public class CloudObject {

	protected int errno;

	protected long request_id;

	public <T extends EntityJSONObject> boolean formJOSN(String json, T t) {
		final JSONObject object = new JSONObject(json);
		getBase(object);
		if(errno == 0 && null != t){
			return t.formJOSN(object);
		}
		return false;
	}

	protected void getBase(JSONObject object) {
		if (object.has("errno")) {
			errno = object.getInt("errno");
		}

		if (object.has("request_id")) {
			request_id = object.getLong("request_id");
		}
	}

	@Override
	public String toString() {
		return "PCSObject [errno=" + errno + ", request_id=" + request_id + "]";
	}

	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public long getRequest_id() {
		return request_id;
	}

	public void setRequest_id(long request_id) {
		this.request_id = request_id;
	}

}
