package com.pyxx.exceptions;

import org.json.JSONException;

/**
 * 数据异常
 * 
 * @author wll
 */
public class DataException extends JSONException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String code1 = "100011";// 请在布局中添加R.id.mainroot,布局必须为相对布局

	public DataException(String s) {
		super(s);
	}
}
