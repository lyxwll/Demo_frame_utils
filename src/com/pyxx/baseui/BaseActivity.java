package com.pyxx.baseui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.pyxx.loadimage.ImageCache;
import com.pyxx.loadimage.ImageCache.ImageCacheParams;
import com.pyxx.loadimage.ImageFetcher;

public abstract class BaseActivity extends FragmentActivity {

	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		if (ShareApplication.mImageWorker == null) {
			ImageCacheParams cacheParams = new ImageCacheParams(
					ShareApplication.IMAGE_CACHE_DIR);
			ImageFetcher mImageWorker = new ImageFetcher(this, 800);
			// mImageWorker.setLoadingImage(R.drawable.empty_photo);
			mImageWorker.setImageCache(ImageCache.findOrCreateCache(this,
					cacheParams));
			ShareApplication.mImageWorker = mImageWorker;
		}
		setFinish();
	};

	public abstract void things(View view);

	public static String ACTIVITY_FINSH = "com.palmtrends.activity.finish";
	static {
		Resources res = ShareApplication.share.getResources();
		ACTIVITY_FINSH = res.getString(R.string.activity_all_finish);
	}
	FinishCastReceiver finishBroadCastReceiver;

	public void setFinish() {
		// 生成广播处理
		finishBroadCastReceiver = new FinishCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTIVITY_FINSH);
		// 注册广播
		registerReceiver(finishBroadCastReceiver, intentFilter);
	}

	public class FinishCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	}

	@Override
	public void finish() {
		if (finishBroadCastReceiver != null)
			try {
				unregisterReceiver(finishBroadCastReceiver);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		super.finish();
	}
}
