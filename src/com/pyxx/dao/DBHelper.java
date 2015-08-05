package com.pyxx.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;

/**
 * 数据库操作封装
 * 
 * @author HeJian
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static String DATABASE_NAME;
	private final static int DATABASE_VERSION = 2;
	private static DBHelper mDBHelper;
	private SQLiteDatabase db;
	public final static String FAV_FLAG = "FAV_TAG";// 收藏标志位

	public static DBHelper getDBHelper() {
		DATABASE_NAME = ShareApplication.share.getPackageName() + "_db";
		if (mDBHelper == null) {
			mDBHelper = new DBHelper(ShareApplication.share);
		}
		return mDBHelper;
	}

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] upgrade = ShareApplication.share.getResources()
				.getStringArray(R.array.appconfig_sql);
		for (String sql : upgrade) {
			db.execSQL(sql);
		}
		this.db = db;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String[] upgrade = ShareApplication.share.getResources()
				.getStringArray(R.array.appconfig_sql_upgrade);
		for (String sql : upgrade) {
			db.execSQL(sql);
		}
		onCreate(db);
	}

	/**
	 * 列表数据插入，VALUE参数为JSON数据串
	 * 
	 * @param url
	 *            请求连接的URL地址
	 * @param value
	 *            请求返回的JSON数据
	 * @param listtype
	 *            列表类型
	 * @return
	 */
	public long insert(String url, String value, String listtype) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		ContentValues cv = new ContentValues();
		cv.put("url", url);
		cv.put("infos", value);
		cv.put("listtype", listtype);
		long row = db.insert("listinfo", null, cv);
		return row;
	}

	/**
	 * 数据插入
	 * 
	 * @param table
	 *            要插入的表名
	 * @param cv
	 *            插入数据
	 * @return
	 */
	public long insert(String table, ContentValues cv) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		long row = db.insert(table, null, cv);
		return row;
	}

	public long counts(String tableName, String where) {
		long i = 0;
		if (db == null) {
			db = this.getWritableDatabase();
		}
		Cursor cursor = db.query(tableName, new String[] { "count(c_id) t" },
				where, null, null, null, null);

		if (cursor.moveToFirst()) {
			i = cursor.getLong(cursor.getColumnIndex("t"));
		}
		cursor.close();
		return i;
	}

	/**
	 * 单字段更新操作
	 * 
	 * @param tablename
	 *            更新表名
	 * @param where_name
	 *            更新条件名
	 * @param whereValue
	 *            更新条件值
	 * @param columnName
	 *            更新字段
	 * @param value
	 *            更新字段的值
	 */
	public void update(String tablename, String where_name, String whereValue,
			String columnName, String value) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String where = where_name + "=?";
		String[] wherev = { whereValue };
		ContentValues cv = new ContentValues();
		cv.put(columnName, value);
		db.update(tablename, cv, where, wherev);
	}

	/**
	 * 
	 * @param tablename
	 *            更新表名
	 * @param where
	 *            更新条件
	 * @param whereValue
	 *            更新条件值
	 * @param cv
	 *            更新的所有字段
	 */
	public void update(String tablename, String where, String[] whereValue,
			ContentValues cv) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		db.update(tablename, cv, where, whereValue);
	}

	/**
	 * 
	 * @param o
	 * @param tablename
	 * @throws Exception
	 *             数据库插入数据 tablename是可以为空的，如果为空那么插入的表名为类名相同
	 */
	@Deprecated
	public void insert(List items, String tablename, Class c) throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		Field[] fs = c.getFields();
		StringBuffer sql;
		db.beginTransaction();
		for (Object o : items) {
			sql = new StringBuffer();
			if (tablename == null) {
				sql.append("insert into " + c.getSimpleName().toLowerCase());
			} else {
				sql.append("insert into " + tablename);
			}
			StringBuffer sql_name = new StringBuffer();
			StringBuffer sql_value = new StringBuffer();
			for (Field f : fs) {
				if ("c_id".equals(f.getName())) {
				} else {
					if (f.getType().getSimpleName().equals("String")) {
						sql_name.append(f.getName().toLowerCase() + ",");
						sql_value.append("\'" + f.get(o) + "\'" + ",");
					} else {
						sql_name.append(f.getName().toLowerCase() + ",");
						sql_value.append(f.get(o) + ",");
					}
				}
			}
			String names = sql_name.toString().substring(0,
					sql_name.length() - 1);
			String values = sql_value.toString().substring(0,
					sql_value.length() - 1);
			sql.append("(" + names + ")").append(" ").append("values(")
					.append(values).append(");");
			try {
				db.execSQL(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 清空表数据
	 * 
	 * @param 活动信息表
	 * 
	 */
	public void delete_table_huodongmsg() {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String sql = "DELETE FROM  huodongmsg";
		db.execSQL(sql);
		sql = "CREATE TABLE IF NOT EXISTS citymodel(c_id INTEGER primary key autoincrement,id CHAR,title CHAR,type CHAR);";
		db.execSQL(sql);
	}

	@Deprecated
	public Object insertObject(Object o, String tablename) throws Exception {
		// select("homereaderitem", Object.class);
		if (db == null) {
			db = this.getWritableDatabase();
		}
		Class c = o.getClass();
		Field[] fs = c.getFields();
		StringBuffer sql = new StringBuffer();
		if (tablename == null) {
			sql.append("insert into " + c.getSimpleName().toLowerCase());
		} else {
			sql.append("insert into " + tablename);
		}
		StringBuffer sql_name = new StringBuffer();
		StringBuffer sql_value = new StringBuffer();
		for (Field f : fs) {
			if ("c_id".equals(f.getName())) {
			} else {
				if (f.getType().getSimpleName().equals("String")) {
					sql_name.append(f.getName().toLowerCase() + ",");
					sql_value.append("\'" + f.get(o) + "\'" + ",");
				} else {
					sql_name.append(f.getName().toLowerCase() + ",");
					sql_value.append(f.get(o) + ",");
				}
			}
		}
		String names = sql_name.toString().substring(0, sql_name.length() - 1);
		String values = sql_value.toString().substring(0,
				sql_value.length() - 1);
		sql.append("(" + names + ")").append(" ").append("values(")
				.append(values).append(");");
		db.execSQL(sql.toString());
		return o;
	}

	/**
	 * 
	 * @param tablename
	 *            表名
	 * @param where
	 *            删除条件名
	 * @param values
	 *            删除条件值
	 */
	public void delete(String tablename, String where, String[] values) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		db.delete(tablename, where, values);
	}

	/**
	 * 单／多字段查找
	 * 
	 * @param tablename
	 * @param where
	 * @param values
	 * @return
	 */
	public String select(String tablename, String fieldName, String where,
			String[] values) {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String value = "";
		String sql = "select " + fieldName + " from " + tablename + " where "
				+ where;
		Cursor cursor = db.rawQuery(sql, values);
		if (fieldName.indexOf(",") != -1) {
			if (cursor.moveToFirst()) {
				int count = fieldName.split(",").length;
				for (int i = 0; i < count; i++) {
					value += cursor.getString(i) + ",";
				}
			}
		} else {
			if (cursor.moveToFirst() && !cursor.isNull(0)) {
				value = cursor.getString(0);
			}
		}
		cursor.close();
		return value;
	}

	public void deleteall(String[] array) {
		if (db == null)
			db = this.getReadableDatabase();
		for (String sql : array) {
			db.execSQL(sql);
		}
	}

	/**
	 * 数据查找返回对应类型的列表数据
	 * 
	 * @param tablename
	 *            查找表名
	 * @param c
	 *            返回类型
	 * @param where
	 *            查找条件
	 * @param page
	 *            页数
	 * @param length
	 *            每页长度
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList select(String tablename, Class c, String where, int page,
			int length) throws Exception {
		// if (db == null)
		if (db == null) {
			db = this.getWritableDatabase();
		}
		db = this.getReadableDatabase();
		Cursor cursor = db.query(tablename, null, where, null, null, null,
				"c_id asc limit " + page * length + ", " + length);
		Field[] fs = c.getFields();
		ArrayList al = new ArrayList();
		while (cursor.moveToNext()) {
			Object o = c.newInstance();
			for (Field f : fs) {
				if (f.getType().getSimpleName().equals("Integer")) {
					f.set(o, cursor.getInt(cursor.getColumnIndex(f.getName()
							.toLowerCase())));
				} else if (f.getType().getSimpleName().equals("String")) {
					f.set(o, cursor.getString(cursor.getColumnIndex(f.getName()
							.toLowerCase())));
				}
			}
			al.add(o);
		}
		cursor.close();
		return al;
	}

	// APP统计时间开始／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／

	/**
	 * 时间统计存储
	 * 
	 * @param mark
	 *            :1 已经上传 0 未上传或者上传失败 2 正在记录
	 */
	public void insert_apptime(String type, String s_time, String e_time,
			String mark, String aid, String open_mode) throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String insert = "INSERT INTO apptime (type,starttime,endtime,mark,aid,open_mode) values('"
				+ type
				+ "','"
				+ s_time
				+ "','"
				+ e_time
				+ "','"
				+ mark
				+ "','"
				+ aid + "','" + open_mode + "')";
		db.execSQL(insert);
	}

	/**
	 * 获取统计时间
	 */
	public JSONArray get_apptime() throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		JSONArray jsonArray = new JSONArray();
		Cursor cursor = db.query("apptime", new String[] { "type", "starttime",
				"endtime", "aid", "open_mode" }, "mark='0'", null, null, null,
				null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					JSONObject js = new JSONObject();
					int type = cursor.getColumnIndex("type");
					int starttime = cursor.getColumnIndex("starttime");
					int endtime = cursor.getColumnIndex("endtime");
					int aid = cursor.getColumnIndex("aid");
					int open_mode = cursor.getColumnIndex("open_mode");
					try {
						js.put("type", cursor.getString(type));
						js.put("s_time", cursor.getString(starttime));
						js.put("e_time", cursor.getString(endtime));
						js.put("aid", cursor.getString(aid));
						js.put("open_mode", cursor.getString(open_mode));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					jsonArray.put(js);
				} while (cursor.moveToNext());
			} else {
				cursor.close();
				return null;
			}
		} else {
			cursor.close();
			return null;
		}
		cursor.close();
		return jsonArray;
	}

	/**
	 * 时间统计数据清理(删除已经上传的时间数据)
	 * 
	 * @throws Exception
	 */
	public void delete_apptime() throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String delete = "delete from apptime where mark='0'";
		db.execSQL(delete);
	}

	/**
	 * 时间统计结束时间更新(APP)
	 * 
	 * @param mark
	 * */
	public void up_appendtime(String starttime, String endtime, String mark)
			throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String update = "update apptime set endtime='" + endtime + "',mark='"
				+ mark + "' where starttime='" + starttime + "'";
		db.execSQL(update);
	}

	/**
	 * 时间统计结束时间更新(文章)
	 * 
	 * @param starttime
	 * @param endtime
	 * @param aid
	 *            文章ID
	 * @param mark
	 * @throws Exception
	 */
	public void up_appendtime(String starttime, String endtime, String aid,
			String mark) throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String update = "update apptime set endtime='" + endtime + "',mark='"
				+ mark + "' where starttime='" + starttime + "'and aid='" + aid
				+ "'";
		db.execSQL(update);
	}

	/**
	 * 时间统计结状态更新(当有存在异常退出，状态为1本该更新为0，而没有正常更新，在下次App启动时将其状态改变为0)
	 * 
	 * @param mark
	 *            :1 已经上传 0 未上传或者上传失败
	 */
	public void up_app_mark() throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String update = "update apptime set mark='" + 0 + "' where mark='" + 1
				+ "'";
		db.execSQL(update);
	}

	/**
	 * 初始化栏目数据
	 * 
	 * @throws Exception
	 */
	public void initPart() throws Exception {
		if (db == null) {
			db = this.getWritableDatabase();
		}
		String update = "update part_list set part_updatetime='00'";
		db.execSQL(update);
	}
	// APP统计时间结束／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／／
}
