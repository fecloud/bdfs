package com.yuncore.bdfs.entity;

import org.json.JSONObject;

import com.yuncore.bdfs.tools.Util;

public class CloudFile extends LocalFile implements EntityJSONObject {

	// private int dirEmpty;
	//
	// public int getDirEmpty() {
	// return dirEmpty;
	// }

	// public void setDirEmpty(int dirEmpty) {
	// this.dirEmpty = dirEmpty;
	// }

	public String getAbsolutePath() {
		if (getDir().endsWith("/")) {
			return getDir() + getName();
		}
		return getDir() + "/" + getName();
	}

	@Override
	public boolean formJOSN(JSONObject object) {
		if (null != object) {
			if (object.has("server_filename")) {
				setName(object.getString("server_filename"));
			}

			if (object.has("size")) {
				setLength(object.getLong("size"));
			}

			if (object.has("isdir")) {
				setType(object.getInt("isdir"));
			}

			if (object.has("path")) {
				String path = object.getString("path");
				path = Util.getUnixPath(path);
				setDir(path);
			}

			// if (object.has("dir_empty")) {
			// dirEmpty = object.getInt("dir_empty");
			// }

			return true;
		}
		return false;
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

	public boolean formSuperJOSN(String json) {
		return super.formJOSN(new JSONObject(json));
	}

}
