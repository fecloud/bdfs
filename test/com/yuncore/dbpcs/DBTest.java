package com.yuncore.dbpcs;

import com.yuncore.bdfs.db.CloudFileDeleteDao;
import com.yuncore.bdfs.db.DBHelper;
import com.yuncore.bdfs.db.DownloadDao;
import com.yuncore.bdfs.db.UploadDao;
import com.yuncore.bdfs.entity.LocalFile;

public class DBTest extends PCSTestCase {

	DBHelper helper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		helper = new DBHelper();
	}

	public void test() {

	}

	public void testclearAllTable() {
		helper.executeSQL("drop table cloudfile");
		helper.executeSQL("drop table clouddelete");
		helper.executeSQL("drop table clouddownload");
		helper.executeSQL("drop table localfile");
		helper.executeSQL("drop table localdelete");
		helper.executeSQL("drop table localupload");
		helper.executeSQL("drop table cloudhistory");
		helper.executeSQL("drop table localhistory");
		helper.executeSQL("drop table version");
	}

	public void testCloudDownload() {

		LocalFile query = new DownloadDao().query();
		assertNotNull(query);

		boolean delete = new DownloadDao().delete(query);

		assertEquals(delete, true);
	}

	public void testUpload() {

		LocalFile query = new UploadDao().query();
		assertNotNull(query);

		boolean delete = new UploadDao().delete(query);

		assertEquals(delete, true);
	}
	
	public void testCloudDelete() {

		LocalFile query = new CloudFileDeleteDao().query();
		assertNotNull(query);

		boolean delete = new CloudFileDeleteDao().delete(query);

		assertEquals(delete, true);
	}
}
