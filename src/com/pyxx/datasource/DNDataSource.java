package com.pyxx.datasource;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;

import com.pyxx.dao.DBHelper;
import com.pyxx.dao.MySSLSocketFactory;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.utils.PerfHelper;

public class DNDataSource {
	/**
	 * 从网络加载数据
	 * 
	 * @param url
	 *            连接URL
	 * @param type
	 *            SA参数类型
	 * @param page
	 *            当前页数（从0开始）
	 * @param count
	 *            每页长度
	 * @param partType
	 *            大栏目类型
	 * @param isfirst
	 *            是否为第一次加载
	 * @return
	 * @throws Exception
	 */
	public static String list_FromNET(String url, String type, int page,
			int count, String partType, boolean isfirst) throws Exception {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("sa", type));
		param.add(new BasicNameValuePair("pageNo", page+""));
		param.add(new BasicNameValuePair("pageNum", count + ""));
		param.add(new BasicNameValuePair("log", PerfHelper
				.getStringData(PerfHelper.P_GPS_LONG)));
		param.add(new BasicNameValuePair("dim", PerfHelper
				.getStringData(PerfHelper.P_GPS_LATI)));
		String json = MySSLSocketFactory.getinfo(url, param);
		json = json.replaceAll("'", "‘");
		return json;
	}

	public static String CityLift_list_FromNET(String url, String type,
			int page, int count, String partType, boolean isfirst)
			throws Exception {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("pageNo", page + ""));
		param.add(new BasicNameValuePair("pageNum", count + ""));
		param.add(new BasicNameValuePair("log", PerfHelper
				.getStringData(PerfHelper.P_GPS_LONG)));
		param.add(new BasicNameValuePair("dim", PerfHelper
				.getStringData(PerfHelper.P_GPS_LATI)));
		String json = MySSLSocketFactory.getinfo(url, param);
		json = json.replaceAll("'", "‘");
		return json;
	}

	/**
	 * 其它传参方式
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String list_FromNET(String url, List<NameValuePair> param)
			throws Exception {
		String json = MySSLSocketFactory.getinfo(url, param);
		json = json.replaceAll("'", "‘");
		return json;
	}

	/**
	 * 从数据库读取数据
	 * 
	 * @param url
	 *            url连接
	 * @param type
	 *            SA参数类型
	 * @param page
	 *            当前页数（从0开始）
	 * @param count
	 *            每页长度
	 * @param partType
	 *            大栏目类型
	 * @return
	 */
	public static String list_FromDB(String type, int page, int count,
			String partType) {
		String json = DBHelper.getDBHelper().select("listinfo", "infos",
				"url=?", new String[] { type + page });
		return json;
	}

	/**
	 * 从数据库读取数据
	 * 
	 * @param url
	 *            url连接
	 * @param type
	 *            SA参数类型
	 * @param page
	 *            当前页数（从0开始）
	 * @param count
	 *            每页长度
	 * @param partType
	 *            大栏目类型
	 * @return
	 * @throws Exception
	 */
	public static Data list_Fav(String type, int page, int count)
			throws Exception {
		ArrayList al = DBHelper.getDBHelper().select("listitemfa",
				Listitem.class, "show_type='" + type + "'", page, count);
		Data d = new Data();
		if (al != null) {
			d.list = al;
		}
		return d;
	}

	/**
	 * 已读设置
	 * 
	 * @param mark
	 */
	public static void insertRead(String mark, String html) {
		DBHelper db = DBHelper.getDBHelper();
		ContentValues cv = new ContentValues();
		cv.put("n_mark", mark);
		cv.put("htmltext", html);
		db.insert("readitem", cv);
	}

	/**
	 * 文章数据缓存
	 * 
	 * @param table
	 * @param n_mark
	 * @param column
	 * @param value
	 */
	public static void updateRead(String table, String n_mark, String column,
			String value) {
		DBHelper db = DBHelper.getDBHelper();
		db.update(table, "n_mark", n_mark, column, value);
	}
}
