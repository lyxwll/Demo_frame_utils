package com.pyxx.dao;

import android.content.res.Resources;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;

/**
 * url类
 * 
 * @author wll
 */
public class Urls {
	public static String main;
	public static String app_api;
	public static String home = "?action=top";
	// 文章列表链接
	public static String list_url;
	// 图片列表链接
	public static String draw_list;
	public static String draw_content_url;
	// 文章评论列表接口
	public static String review_url;
	// 用户反馈链接
	public static String suggest_url;
	// 文章评论接口
	public static String pinglun_url;
	static {
		Resources res = ShareApplication.share.getResources();
		main = res.getString(R.string.main);
		String[] str = { main };
		app_api = main + "/api_v2.php";
	}

}
