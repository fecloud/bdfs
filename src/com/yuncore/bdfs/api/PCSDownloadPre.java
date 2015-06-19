//package com.yuncore.dbpcs.api;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import com.yuncore.dbpcs.tools.CloudObject;
//
//public class PCSDownloadPre extends CloudObject {
//
//	private List<PCSDownloadDLink> files;
//
//	public List<PCSDownloadDLink> getFiles() {
//		return files;
//	}
//
//	public void setFiles(List<PCSDownloadDLink> files) {
//		this.files = files;
//	}
//
//	@Override
//	public boolean formJOSN(JSONObject object) {
//		if (null != object) {
//			jsonBase(object);
//			if (object.has("dlink")) {
//				final JSONArray array = object.getJSONArray("dlink");
//				if (null != array && array.length() > 0) {
//					
//					files = new ArrayList<PCSDownloadPre.PCSDownloadDLink>();
//					PCSDownloadDLink link = null;
//					
//					for (int i = 0; i < array.length(); i++) {
//						link = new PCSDownloadDLink();
//						link.formJOSN(array.getJSONObject(i));
//						files.add(link);
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	public static class PCSDownloadDLink extends CloudObject {
//
//		private long fs_id;
//
//		private String dlink;
//
//		public long getFs_id() {
//			return fs_id;
//		}
//
//		public void setFs_id(long fs_id) {
//			this.fs_id = fs_id;
//		}
//
//		public String getDlink() {
//			return dlink;
//		}
//
//		public void setDlink(String dlink) {
//			this.dlink = dlink;
//		}
//
//		@Override
//		public boolean formJOSN(JSONObject object) {
//			if (null != object) {
//				if (object.has("fs_id")) {
//					fs_id = object.getLong("fs_id");
//				}
//
//				if (object.has("dlink")) {
//					dlink = object.getString("dlink");
//				}
//			}
//			return false;
//		}
//
//		@Override
//		public String toString() {
//			return "PCSDownloadDLink [fs_id=" + fs_id + ", dlink=" + dlink
//					+ "]";
//		}
//
//	}
//
//}
