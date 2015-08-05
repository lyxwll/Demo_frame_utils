package com.utils;


public class JniUtils {
	public static String getkey() {
		String key = null;
		key = stringFromJNI(PerfHelper.getStringData(PerfHelper.P_USERID));
		PerfHelper.setInfo("md5_key", key);
		return key;
	}

	static native String stringFromJNI(String uid);

	static {
		System.loadLibrary("md5");
	}
}