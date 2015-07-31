package com.yuncore.bdfs.client.entity;

import org.json.JSONObject;

import com.yuncore.bdfs.entity.EntityJSON;

public class CloudRmResult implements EntityJSON {

	private long taskid;

	public long getTaskid() {
		return taskid;
	}

	public void setTaskid(long taskid) {
		this.taskid = taskid;
	}

	@Override
	public boolean formJOSN(JSONObject object) {
		if (null != object) {
			if (object.has("taskid")) {
				taskid = object.getLong("taskid");
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "PCSRmResult [taskid=" + taskid + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.entity.EntityJSONObject#formJOSN(java.lang.String)
	 */
	@Override
	public boolean formJOSN(String json) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.entity.EntityJSONObject#toJSON()
	 */
	@Override
	public String toJSON() {
		return null;
	}

	@Override
	public void toJSON(JSONObject object) {
		
	}

}
