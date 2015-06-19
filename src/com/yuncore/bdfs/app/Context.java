package com.yuncore.bdfs.app;

import com.yuncore.bdfs.exception.BDFsException;

public interface Context {

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	Object setProperty(String key, String value);

	boolean load() throws BDFsException;

	String getUserName();

	String getPassWord();

	boolean login() throws BDFsException;

}
