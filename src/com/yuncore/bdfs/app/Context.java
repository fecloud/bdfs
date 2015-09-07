package com.yuncore.bdfs.app;

import com.yuncore.bdfs.exception.BDFSException;

public interface Context {

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	Object setProperty(String key, String value);

	boolean load() throws BDFSException;

}
