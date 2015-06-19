package com.yuncore.bdfs.entity;

import org.json.JSONObject;

public class CloudQuota implements EntityJSONObject {

	private long total;

	private long free;

	private long used;

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	@Override
	public boolean formJOSN(JSONObject object) {
		if (null != object) {
			if (object.has("total")) {
				total = object.getLong("total");
			}

			if (object.has("free")) {
				free = object.getLong("free");
			}

			if (object.has("used")) {
				used = object.getLong("used");
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "PCSQuota [total=" + total + ", free=" + free + ", used=" + used
				+ "]";
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
