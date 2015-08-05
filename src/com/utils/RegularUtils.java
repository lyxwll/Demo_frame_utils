package com.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtils {
	/**
	 * 邮箱验证工具
	 * 
	 * @param line
	 * @return
	 */
	public static boolean getEmail(String line) {
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher m = p.matcher(line);
		return m.find();
	}
}
