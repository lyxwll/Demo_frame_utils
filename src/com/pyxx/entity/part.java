package com.pyxx.entity;

//db.execSQL("CREATE TABLE IF NOT EXISTS part(c_id INTEGER primary key autoincrement,
//part_name TEXT,part_sa TEXT,part_choise TEXT,part_index TEXT)");
/**
 * 栏目记录
 * 
 * 
 */
public class part extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer c_id;
	public String part_name;
	public String part_sa;
	public String part_choise;
	public Integer part_index;
	public String part_type;
	public String part_updatetime;
	public String part_rgb;
	public String part_touch;
	public String part_untouch;

}
