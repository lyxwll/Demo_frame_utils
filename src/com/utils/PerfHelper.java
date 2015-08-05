/**
 * 
 */
package com.utils;

import com.pyxx.app.ShareApplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @description 配置信息读写工具类
 */

public class PerfHelper {

	private static final String P_NAME = "artanddesign";
	public static final String P_UPDATE = "p_update_setting";
	public static final String P_TEXT = "p_text_setting";
	public static final String P_USERID = "p_user_setting";
	public static final String P_APP_ID = "p_app_setting";
	public static final String P_CITY = "p_city";// 客户选择城市
	public static final String P_NOW_CITY = "p_now_city";// 定位坐标城市
	public static final String P_CITY_No = "p_city_no";// 所选城市对应ID

	public static final String P_GPS = "p_location_setting";
	public static final String P_GPS_LONG = "p_location_long";// 定位经度
	public static final String P_GPS_LATI = "p_location_la";// 定位纬度
	public static final String P_GPS_YES = "p_location_yes";// 是否定位成功
	public static final String P_PUSH = "p_push_setting";
	public static final String P_USER_LOGIN = "p_user_login";
	public static final String P_SELLER_LOGIN = "p_seller_login";//商家登录
	public static String P_PHONE_W = "p_phonew_setting";
	public static String P_PHONE_H = "p_phoneh_setting";
	public static String P_DATE_MODE = "p_isdate_setting";// 白天黑夜模式
															// night表示黑夜／day表示白天
	public static final String P_SHARE_AGE = "p_share_age_setting";
	public static final String P_SHARE_EMAIL = "p_share_email_setting";
	public static final String P_SHARE_SEX = "p_share_sex_setting";
	public static final String P_SHARE_STATE = "p_share_state_setting";// 微博绑定状态－－－配合微博sname一起使用
	public static final String P_SHARE_NAME = "p_share_name_setting";// 微博绑定名称－－－配合微博sname一起使用
	public static final String P_SHARE_USER_IMAGE = "p_share_user_image_setting";// 微博绑定用户头像－－－配合微博sname一起使用
	public static final String P_SHARE_USER_ID = "p_share_user_id_setting";// 微博绑定用户ID－－－配合微博sname一起使用
	public static final String P_ALARM_TIME = "p_alarm_time_setting";// 时间提醒
	public static final String P_SECOND_TIME = "p_second_time_setting";// 二级栏目更新时间

	public static final String P_APP_PUSH_SERVER_PORT = "p_push_server_port";// 推送服务端口
	public static final String P_APP_PUSH_SERVER_IP = "p_push_server_ip";// 推送服务IP
	public static final String P_APP_PUSH_SERVER_TIME = "p_push_server_time";// 推送最新时间

	private static SharedPreferences sp;
	private static PerfHelper ph;

	private PerfHelper() {

	}

	public static PerfHelper getPerferences(Context a) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		return ph;
	}

	public static PerfHelper getPerferences() {
		return ph;
	}

	public static void setInfo(String name, String data) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putString(name, data);
		e.commit();
	}

	public static void setInfo(String name, int data) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putInt(name, data);
		e.commit();
	}

	public static void setInfo(String name, boolean data) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putBoolean(name, data);
		e.commit();
	}

	public static int getIntData(String name) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		return sp.getInt(name, 0);
	}

	public static String getStringData(String name) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		return sp.getString(name, "");
	}

	public static boolean getBooleanData(String name) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		return sp.getBoolean(name, false);
	}

	public static void setInfo(String name, long data) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		SharedPreferences.Editor e = sp.edit().putLong(name, data);
		e.commit();
	}

	public static long getLongData(String name) {
		if (ph == null) {
			ph = new PerfHelper();
			sp = ShareApplication.share.getSharedPreferences(P_NAME, 0);
		}
		return sp.getLong(name, 0);
	}
}
