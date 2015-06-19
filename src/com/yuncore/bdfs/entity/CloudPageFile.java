package com.yuncore.bdfs.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class CloudPageFile implements EntityJSONObject{

	private int errno;
	
	private List<CloudFile> list;

	@Override
	public boolean formJOSN(JSONObject object) {
		if(object.has("errno")){
			errno = object.getInt("errno");
		}
		if (object.has("list")) {
			list = new ArrayList<CloudFile>();
			final JSONArray array = object.getJSONArray("list");
			CloudFile file = null;
			for (int i = 0; i < array.length(); i++) {
				file = new CloudFile();
				file.formJOSN(array.getJSONObject(i));
				list.add(file);
			}
		}
		return true;
	}
	
	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public List<CloudFile> getList() {
		return list;
	}

	public void setList(List<CloudFile> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "PCSPageFile [list=" + list + "]";
	}

	/* (non-Javadoc)
	 * @see com.yuncore.dbpcs.entity.EntityJSONObject#formJOSN(java.lang.String)
	 */
	@Override
	public boolean formJOSN(String json) {
		return false;
	}

	/* (non-Javadoc)
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
