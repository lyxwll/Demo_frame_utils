/**
 * 
 */
package com.pyxx.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 */
@SuppressWarnings("rawtypes")
public class Data extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	public int type;//
	public List list = new ArrayList();
	public String date;
	public Object obj;
	public Object obj1;
	public int headtype = -1;// -1表示没有头图 0表示列表滚动，1表示列表外，2表示多张图
	public int loadmorestate = 1;// 1 表示有数据、0表示没有更多数据
}
