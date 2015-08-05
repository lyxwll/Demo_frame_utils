package com.pyxx.baseui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.part;
import com.pyxx.loadimage.Utils;
import com.utils.FinalVariable;
import com.utils.GpsUtils;
import com.utils.PerfHelper;

/**
 * 欢迎界面，必需继承
 * 
 * @author HeJian
 * 
 */
public abstract class BaseInitAcitvity extends BaseActivity {
	/**
	 * 注：在这个方法必须被调用，并在这个方法里面初始化应用程序信息，如PID，缓存文件名等
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);
		PerfHelper.getPerferences(getApplicationContext());
		DBHelper.getDBHelper();
		PerfHelper.setInfo(PerfHelper.P_PHONE_W, getResources()
				.getDisplayMetrics().widthPixels);
		PerfHelper.setInfo(PerfHelper.P_PHONE_H, getResources()
				.getDisplayMetrics().heightPixels);
		/* 注册GPS定位广播并打开定位 */
		setGPSFinish();
		GpsUtils.getLocation();
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FinalVariable.error) {
				finish();
				Utils.showToast(R.string.network_error);
				return;
			}
			begin_StartActivity();
		}
	};

	/**
	 * 数据加载完成后跳转
	 */
	public abstract void begin_StartActivity();

	/**
	 * 栏目初始化操作
	 */
	public void initparts() throws Exception {
		DBHelper db = DBHelper.getDBHelper();
		int index;
		String names[] = this.getResources().getStringArray(
				R.array.second_names);
		int count = names.length;
		for (int i = 0; i < count; i++) {
			index = 0;

			String design_data[] = names[i].split(";");
			List<part> design_patrs = new ArrayList<part>();
			for (String partstr : design_data) {
				part p = new part();
				String part[] = partstr.split(",");
				p.part_name = part[0];
				p.part_index = index;
				index++;
				p.part_sa = part[2];
				p.part_type = part[1];
				design_patrs.add(p);
			}
			db.insert(design_patrs, "part_list", part.class);
		}
	}

	/**
	 * 二级栏目更新操作
	 */
	public void initNetPart() {

	}

	@Override
	public void things(View view) {

	}

	/**
	 * GPS定位确认
	 */
	FinishGPSReceiver finishBroadCastGPSReceiver;
	boolean isfrist = true;

	public void setGPSFinish() {
		// 生成广播处理
		finishBroadCastGPSReceiver = new FinishGPSReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(GpsUtils.GPS_FINSH);
		// 注册广播
		ShareApplication.share.registerReceiver(finishBroadCastGPSReceiver,
				intentFilter);
	}

	public class FinishGPSReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ShareApplication.debug)
				System.out.println("GPS定位完毕");
			if (isfrist) {
				isfrist = false;
				gpslocateend();
			}
		}
	}

	/**
	 * wsergsfafsaa GPS定位完成执行型
	 */
	public abstract void gpslocateend();

	private static boolean sdcardAvailable;
	private static boolean sdcardAvailabilityDetected;

	/**
	 * 
	 * @return SD is available ?
	 */
	public static synchronized boolean detectSDCardAvailability() {
		boolean result = false;
		try {
			Date now = new Date();
			String fileName = Environment.getExternalStorageDirectory()
					+ "/.test";
			File file = new File(fileName);
			result = file.createNewFile();
			file.delete();
			result = true;
		} catch (Exception e) {
			// Can't create file, SD Card is not available
			result = false;
			e.printStackTrace();
		} finally {
			sdcardAvailabilityDetected = true;
			sdcardAvailable = result;
		}
		return result;
	}

	/**
	 * 
	 * @return SD is available ?
	 */
	public static boolean isSDCardAvailable() {
		if (!sdcardAvailabilityDetected) {
			sdcardAvailable = detectSDCardAvailability();
			sdcardAvailabilityDetected = true;
		}
		return sdcardAvailable;
	}

	public void showOther() {

	}
}
