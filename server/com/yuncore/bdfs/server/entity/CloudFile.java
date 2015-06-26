package com.yuncore.bdfs.server.entity;

import org.json.JSONObject;

import com.yuncore.bdfs.entity.BDFSFile;

public class CloudFile extends BDFSFile {

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
				path = getUnixPath(path);
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

	/**
	 * 取文件的所在的路径
	 * 
	 * @param file
	 * @return
	 */
	public static String getUnixPath(String file) {
		if (null != file) {
			int lastIndexOf = file.lastIndexOf("/");
			if (lastIndexOf == 0) {
				return "/";
			} else {
				return file.substring(0, lastIndexOf);
			}
		}
		return null;
	}

	/**
	 * 取文件的所在的文件
	 * 
	 * @param file
	 * @return
	 */
	public static String getUnixFileName(String file) {
		if (null != file) {
			int lastIndexOf = file.lastIndexOf("/");
			return file.substring(lastIndexOf + 1);
		}
		return null;
	}

	public boolean formSuperJOSN(String json) {
		return super.formJOSN(new JSONObject(json));
	}

}
