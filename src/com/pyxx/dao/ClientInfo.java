package com.pyxx.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.Handler;

import com.pyxx.app.ShareApplication;
import com.pyxx.entity.Entity;
import com.pyxx.entity.info;

/**
 * 客户端信息收集类
 * 
 * @author wll
 */
public class ClientInfo {

	// 提交从定时打开统计数据
	public static void submit() {
		final List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("action", "local_tip"));
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public static class Version extends Entity {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String available; // 当有更新时，值为1，无更新时，值为0
		public String update_url; // 最新版本下载地址。
		public String force; // 参数值为1时强制升级；为0时不强制。
		public String alert; // 更新提示信息。
		public String code;// 接口调用错误时，code为0。
	}

	/**
	 * 用户反馈
	 * 
	 * @param sid
	 * @param sname
	 * @param pid
	 * @param comment
	 * @param hand
	 */
	public static void senduser_feedback(String email, String comment,
			final Handler handler) {
		final List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("suggest", comment));
		param.add(new BasicNameValuePair("os_ver",
				android.os.Build.VERSION.RELEASE));
		param.add(new BasicNameValuePair("email", email));
		new Thread() {
			@Override
			public void run() {
				try {
					param.add(new BasicNameValuePair("client_ver",
							ShareApplication.getVersion()));
					String json = MySSLSocketFactory.getinfo(Urls.suggest_url,
							param);
					JSONObject obj = new JSONObject(json);
					info i = new info();
					i.code = obj.getString("code");
					i.msg = obj.getString("msg");
					// msg=i.msg;
					if ("0".equals(i.code)) {
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(2);
				}
			}
		}.start();
	}
}
