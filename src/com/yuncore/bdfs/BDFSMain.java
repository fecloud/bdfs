/**
 * @(#) BDFSMain.java Created on 2015-9-7
 *
 * 
 */
package com.yuncore.bdfs;

import com.yuncore.bdfs.cookie.LoginGetCookie;
import com.yuncore.bdfs.http.cookie.FileCookieContainer;

/**
 * The class <code>BDFSMain</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class BDFSMain {

	public static void main(String[] args) {
		if (args.length < 1) {
			printHelp();
		} else {
			final String action = args[0].trim();
			if ("sync".equalsIgnoreCase(action)) {
				if (args.length >= 2) {
					final Sync pcsSync = new Sync(args);
					pcsSync.start();
				} else {
					printHelp();
				}
			} else if ("cookie".equalsIgnoreCase(action)) {
				cookie(args);
			} else {
				printHelp();
			}
		}
	}

	private static final void cookie(String[] args) {
		if (args.length >= 3) {
			int index = 0;
			boolean write = false;
			for (String s : args) {
				if ("-w".equalsIgnoreCase(s.trim())) {
					write = true;
					index++;
					break;
				}
			}
			System.setProperty(Const.COOKIECONTAINER,
					FileCookieContainer.class.getName());
			new LoginGetCookie(write, args[index + 1], args[index + 1])
					.getCookie();
		} else {
			printHelp();
		}
	}

	private static final void printHelp() {
		System.err.println("Usage:bdsync [sync|cookie]");
		System.err.println("");
		System.err
				.println("sync <local_dir> <-p port> [-l exinclude dir] [-c exinclude dir]");
		System.err.println("");
		System.err.println("cookie [-w] <username> <password>");
	}

}
