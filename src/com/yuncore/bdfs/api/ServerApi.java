package com.yuncore.bdfs.api;

import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.entity.LocalFile;
import com.yuncore.bdfs.exception.ServerApiException;

public interface ServerApi {

	boolean uploadlocal(String file) throws ServerApiException;

	Account getAccount() throws ServerApiException;

	CloudFile getDownload() throws ServerApiException;

	boolean deldownload(String id) throws ServerApiException;

}
