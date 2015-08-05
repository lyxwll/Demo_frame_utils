package com.pyxx.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.pyxx.entity.part;

public class JsonDao {

	/**
	 * 客户端更新信息解析
	 * 
	 * @param m_mainurl
	 * @param nameValuePairs
	 * @param c
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object getJsonObject(String url,
			List<NameValuePair> nameValuePairs, Class c) throws Exception {
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		HttpPost httppost = new HttpPost(url);
		String strs = null;
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
		HttpResponse response = httpclient.execute(httppost);
		strs = EntityUtils.toString(response.getEntity(), "utf-8");
		Object o = c.newInstance();
		Field[] fs = c.getFields();
		JSONObject obj = new JSONObject(strs);
		for (Field f : fs) {
			if (f.getType().getSimpleName().equals("Integer")) {
				f.set(o, obj.get(f.getName().toLowerCase()));
			} else if (f.getType().getSimpleName().equals("String")) {
				f.set(o, obj.getString(f.getName().toLowerCase()));
			}
		}
		httpclient.getConnectionManager().shutdown();
		return o;
	}

	//
	// public static String getNames(String url, String cid) throws Exception {
	// DBHelper db = DBHelper.getDBHelper();
	// String strs = getInfo(url);
	// PerfHelper.setInfo(PerfHelper.p_zlmenu, strs);
	// List<part> patrs = new ArrayList<part>();
	// JSONArray ajson = new JSONArray(strs);
	// int count = ajson.length();
	// if (count > 0) {
	// db.delete("part_list", "part_type=?", new String[] { cid });
	// }
	// for (int i = 0; i < count; i++) {
	// part o = new part();
	// JSONObject obj = ajson.getJSONObject(i);
	// o.part_type = cid;
	// o.part_index = i;
	// o.part_name = obj.getString("name");
	// o.part_sa = obj.getString("sa");
	// patrs.add(o);
	// }
	// db.insert(patrs, "part_list", part.class);
	// return strs;
	// }

	/**
	 * 获取二级菜单数据,并把数据存储到数据库中
	 * 
	 * @param m_mainurl
	 *            对应栏目请求地址
	 * @throws Exception
	 */
	public static void getPartList(String url, String[] parts) {
		try {
			String strs = MySSLSocketFactory.getinfo_Get(url);
			Log.i("liyuling", "二级菜单更新    json: " + strs);
			JSONObject json = new JSONObject(strs);
			DBHelper db = DBHelper.getDBHelper();
			for (String str : parts) {
				JSONArray ja = json.getJSONArray(str);
				int count = ja.length();
				if (count > 0) {
					db.delete("part_list", "part_type=?", new String[] { str });
				}
				List<part> partList = new ArrayList<part>();
				for (int i = 0; i < count; i++) {
					JSONObject obj = ja.getJSONObject(i);
					part o = new part();
					o.part_sa = obj.getString("sa");
					o.part_name = obj.getString("name");
					o.part_type = str;
					o.part_index = i;
					partList.add(o);
				}
				db.insert(partList, "part_list", part.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getInfo(String url) throws Exception {
		String strs;
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		strs = MySSLSocketFactory.getinfo(url, param);
		return strs;
	}
}
