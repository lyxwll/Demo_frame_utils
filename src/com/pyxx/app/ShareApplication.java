package com.pyxx.app;

import java.util.List;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.ImageFetcher;
import com.pyxx.loadimage.Utils;

/**
 * 
 * @author Administrator
 *
 */
public class ShareApplication extends Application {
	public static ShareApplication share;
	public static List<Listitem> items;// 列表数据 注：些列表数据必须为对应adapter数据列表的指针
	public static ImageFetcher mImageWorker;
	public static final String IMAGE_CACHE_DIR = "images";
	public static Handler hander_other;
	public static boolean debug = true;

	@Override
	public void onCreate() {
		super.onCreate();
		share = this;
		hander_other = new Handler();
		Utils.h = new Handler();
		// 拦截错误日志
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		// crashHandler.sendPreviousReportsToServer();
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public static String getVersion() {
		PackageManager packageManager = share.getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					share.getPackageName(), 0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}
}
