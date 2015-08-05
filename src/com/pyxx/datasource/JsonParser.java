package com.pyxx.datasource;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;

/**
 * json解析
 * 
 * @author wll
 */
public class JsonParser {

	public static Data ParserArticleList(String json) throws Exception {
		Data data = new Data();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("code")) {
			int code = jsonobj.getInt("code");
			if (code == 0) {
				throw new DataException(jsonobj.getString("msg"));
			}
		}
		if (jsonobj.has("def")) {
			data.loadmorestate = jsonobj.getInt("def");
		}
		JSONArray jsonay = jsonobj.getJSONArray("list");
		if (jsonobj.has("head")) {
			try {
				JSONArray ja = jsonobj.getJSONArray("head");
				if (ja.length() == 1) {
					JSONObject jsonhead = ja.getJSONObject(0);
					Listitem head1 = new Listitem();
					head1.nid = jsonhead.getString("id");
					head1.title = jsonhead.getString("title");
					head1.des = jsonhead.getString("des");
					head1.icon = jsonhead.getString("icon");
					head1.getMark();
					head1.ishead = "true";
					data.obj = head1;
					data.headtype = 0;
				} else {
					int count = ja.length();
					List<Listitem> li = new ArrayList<Listitem>();
					for (int i = 0; i < count; i++) {
						JSONObject jsonhead = ja.getJSONObject(i);
						Listitem head1 = new Listitem();
						head1.nid = jsonhead.getString("id");
						head1.title = jsonhead.getString("title");
						head1.des = jsonhead.getString("des");
						head1.icon = jsonhead.getString("icon");
						head1.ishead = "true";
						head1.getMark();
						li.add(head1);
					}
					data.obj = li;
					data.headtype = 2;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");
			o.title = obj.getString("title");
			try {
				if (obj.has("des")) {
					o.des = obj.getString("des");
				}
				if (obj.has("adddate")) {
					o.u_date = obj.getString("adddate");
				}
				o.icon = obj.getString("icon");
			} catch (Exception e) {
			}
			o.getMark();
			// o.list_type = obj.getString("type");
			data.list.add(o);
		}
		return data;
	}
}
