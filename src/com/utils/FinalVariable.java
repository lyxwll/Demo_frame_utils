package com.utils;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;

public class FinalVariable {
	public static String pid = "";
	static {
		pid = ShareApplication.share.getString(R.string.pid);
		PerfHelper.setInfo(PerfHelper.P_APP_ID, pid);
	}
	public static final int update = 1001;
	public static final int remove_footer = 1002;
	public static final int change = 1003;
	public static final int error = 1004;
	public static final int deletefoot = 1005;
	public static final int addfoot = 1006;
	public static final int nomore = 1007;
	public static final int first_load = 1008;
	public static final int load_image = 1009;
	public static final int other = 1010;
	public static final int first_update = 1011;
	public static int timer = 20;
	public static final int length = 15;
	public static final int home_length = 8;
	/**
	 * 
	 * 获取当前程序的版本号 　　
	 */

	public static final int vb_success = 10000;// 微博操作成功
	public static final int vb_bind = 10001;// 微博未绑定
	public final static int vb_error = 10002;// 异常
	public static final int vb_conten_null = 10003;// 评论为空
	public static final int vb_shortid = 10014;// 获取短ID
	public static final int vb_text_long = 10021;// 字数超过140
	public static final int vb_get_userinfor = 10022;// 获微博用户信息
}
