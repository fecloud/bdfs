package com.yuncore.bdfs.client;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Usage:bdpan <local_dir>  [exinclude dir]");
		} else {
			final Sync pcsSync = new Sync(args);
			pcsSync.start();
		}

	}

}
