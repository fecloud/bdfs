package com.yuncore.bdfs.server.repeat;

import com.yuncore.bdfs.server.dao.UploadDao;

public class UpLoadRepeat extends DownLoadRepeat {

	public UpLoadRepeat() {
		downloadDao = new UploadDao();
	}

	@Override
	protected String selectTableName() {
		return "cloudfile";
	}

	@Override
	protected String getTAG() {
		return "UpLoadRepeat";
	}

}
