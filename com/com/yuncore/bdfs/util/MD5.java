package com.yuncore.bdfs.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5 {

	/**
	 * md5加密
	 * 
	 * @param s
	 * @return
	 */
	public final static String md5(String s) {
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			final StringBuilder hexString = new StringBuilder();
			// 字节数组转换为 十六进制 数
			String shaHex = null;
			for (int i = 0; i < md.length; i++) {
				shaHex = Integer.toHexString(md[i] & 0xFF);
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算文件的md5
	 * 
	 * @param path
	 * @return
	 */
	public final static String md5File(String path) {
		final File file = new File(path);
		if (file.exists() && file.canRead()) {
			try {
				// 获得MD5摘要算法的 MessageDigest 对象
				final MessageDigest digest = MessageDigest.getInstance("MD5");
				final FileInputStream in = new FileInputStream(file);
				final byte[] buffer = new byte[1024 * 1024];
				int len = -1;
				while (-1 != (len = in.read(buffer))) {
					digest.update(buffer, 0, len);
				}
				in.close();

				// 获得密文
				final byte[] md = digest.digest();
				final StringBuilder hexString = new StringBuilder();
				// 字节数组转换为 十六进制 数
				String shaHex = null;
				for (int i = 0; i < md.length; i++) {
					shaHex = Integer.toHexString(md[i] & 0xFF);
					hexString.append(shaHex);
				}
				return hexString.toString();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 计算文件的md5,文件前多少字节
	 * 
	 * @param path
	 * @param count
	 * @return
	 */
	public final static String md5File(String path, int count) {
		final File file = new File(path);
		if (file.exists() && file.canRead() && file.length() >= count) {
			try {
				int unRead = count;
				// 获得MD5摘要算法的 MessageDigest 对象
				final MessageDigest digest = MessageDigest.getInstance("MD5");
				final FileInputStream in = new FileInputStream(file);
				final byte[] buffer = new byte[1024 * 1024];
				int len = -1;
				while (-1 != (len = in.read(buffer))) {
					if (unRead > len) {
						digest.update(buffer, 0, len);
					} else {
						digest.update(buffer, 0, unRead);
					}
					unRead -= len;
				}
				in.close();

				// 获得密文
				final byte[] md = digest.digest();
				final StringBuilder hexString = new StringBuilder();
				// 字节数组转换为 十六进制 数
				String shaHex = null;
				for (int i = 0; i < md.length; i++) {
					shaHex = Integer.toHexString(md[i] & 0xFF);
					hexString.append(shaHex);
				}
				return hexString.toString();
			} catch (Exception e) {
			}
		}
		return null;
	}

}
