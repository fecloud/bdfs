package com.yuncore.bdfs.util;

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
			for (int i = 0; i < md.length; i++) {
				String shaHex = Integer.toHexString(md[i] & 0xFF);
				// if (shaHex.length() < 2) {
				// hexString.append(0);
				// }
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
