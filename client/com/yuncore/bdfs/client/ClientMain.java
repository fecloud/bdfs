package com.yuncore.bdfs.client;

import com.yuncore.bdfs.client.http.cookie.FileCookieContainer;

public class ClientMain {

	public static void main(String[] args) {
		if (args.length < 1) {
			printHelp();
		} else {
			final String action = args[0].trim();
			if ("sync".equalsIgnoreCase(action)) {
				final Sync pcsSync = new Sync(args);
				pcsSync.start();
			} else if ("cookie".equalsIgnoreCase(action) && args.length == 3) {
				System.setProperty("java.io.logpriority", "VERBOSE");
				System.setProperty(Const.COOKIECONTAINER,
						FileCookieContainer.class.getName());
				new LoginGetCookie(false, args[1], args[2]).getCookie();
			} else if ("ucookie".equalsIgnoreCase(action) && args.length == 3) {
				System.setProperty("java.io.logpriority", "VERBOSE");
				System.setProperty(Const.COOKIECONTAINER,
						FileCookieContainer.class.getName());
				new LoginGetCookie(true, args[1], args[2]).getCookie();
			} else {
				printHelp();
			}
		}
	}

	public static final void printHelp() {
		System.err.println("Usage:bdsync [sync|cookie|ucookie]");
		System.err.println("");
		System.err.println("sync <local_dir> <-p port> [-l exinclude dir] [-c exinclude dir]");
		System.err.println("");
		System.err.println("sync [cookie|ucookie] <username> <password>");
	}

}
