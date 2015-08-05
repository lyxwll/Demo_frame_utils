package com.pyxx.baseui;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.pyxx.baseview.ImageDetailViewPager.OnViewListener;
import com.pyxx.dao.Urls;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.ImageCache;
import com.pyxx.loadimage.ImageCache.ImageCacheParams;
import com.pyxx.loadimage.ImageFetcher;
import com.pyxx.loadimage.ImageResizer;
import com.pyxx.loadimage.Utils;
import com.pyxx.zoom.GestureImageView.OnPageChangeCallback;
import com.utils.FileUtils;

public class BasePicInfoActivity extends BaseActivity implements
		OnPageChangeCallback, OnViewListener {

	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	public static final int WEIXIN_ONE = 0; // 单个好友
	public static final int WEIXIN_QUAN = 1; // 朋友圈
	protected static final int DATA_APPEND_CURRENT = 0;
	protected static final int DATA_APPEND_LEFT = 1;
	protected static final int DATA_APPEND_RIGHT = 2;

	protected static final String IMAGE_CACHE_DIR = "bigimages";
	protected static final String EXTRA_IMAGE = "extra_image";

	public static ImageResizer mImageWorker;
	protected String shortID = "";
	public int weixin_type = WEIXIN_ONE;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mImageWorker == null) {
			final int height = getResources().getDisplayMetrics().heightPixels;
			final int width = getResources().getDisplayMetrics().widthPixels;
			final int longest = height > width ? height : width;
			mImageWorker = new ImageFetcher(this, longest);
			ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
			mImageWorker.setImageCache(ImageCache.findOrCreateCache(this,
					cacheParams));
			mImageWorker.setImageFadeIn(false);
		}
	}

	// 下载图片
	public void downloadImage(Listitem current_image) {
		if (current_image != null) {
			Bitmap bt = mImageWorker.mImageCache
					.getBitmapFromMemCache(Urls.main + current_image.icon);
			if (bt == null) {
				bt = mImageWorker.getImageCache().getBitmapFromDiskCache(
						Urls.main + current_image.icon);
			}
			if (bt == null) {
				Utils.showToast(R.string.draw_load_toast);
			} else {
				Utils.showProcessDialog(this, R.string.draw_load_down);
				FileUtils.writeToFile(bt, current_image.icon);
			}
		}
	}

	// 邮件分享
	public void shareEmail(String info, String picurl, String title) {
		// 附件文件地址
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra("body", info); // 正文
		intent.putExtra("subject", title);
		if (picurl != null) {
			File file = new File(FileUtils.sdPath + "/pic_cache/http/cache_"
					+ FileUtils.converPathToName(picurl));
			if (ShareApplication.debug) {
				System.out.println(file.getAbsolutePath() + "==");
			}
			if (file.exists()) { // 添加附件，附件为file对象

				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
				if (file.getName().endsWith(".gz")) {
					intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
				} else if (file.getName().endsWith(".txt")) {
					intent.setType("text/plain"); // 纯文本则用text/plain的mime
				} else {
					intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
				}
			} else {
				intent.setType("plain/text");
			}
		} else {
			intent.setType("plain/text");
		}

		try {
			startActivity(intent); // 调用系统的mail客户端进行发送
		} catch (Exception e) {
			Utils.showToast("请安装邮件客户端");
		}
	}

	public Handler wxHandler = new Handler() {
		public void handleMessage(Message message) {
			endToWeixin();
			if (message.what == 0) {
				Utils.showToast("资源获取失败");
			}
		};
	};

	public void endToWeixin() {

	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	// 图片压缩
	public static Bitmap zoomImage(Bitmap bgimage, float newWidth,
			float newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		float maxWidth = width > height ? height : width;
		// 图片剪切为最大的正方形(居中截取)
		Bitmap bit = Bitmap.createBitmap(bgimage,
				(int) ((width - maxWidth) / 2),
				(int) ((height - maxWidth) / 2), (int) maxWidth,
				(int) maxWidth, null, true);
		Bitmap bitmap = Bitmap.createScaledBitmap(bit, (int) newWidth,
				(int) newHeight, true);
		// 图片压缩到32K以下
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] by = baos.toByteArray();
		double mid = by.length / 1024;
		// 图片超过32K时，进行压缩处理
		Bitmap ret = bitmap;
		if (mid > 32) {
			double i = mid / 32 + 1;
			// 保持宽高比不变，压缩后不超过32K
			ret = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
					bitmap.getHeight() / Math.sqrt(i));
		}
		return ret;
	}

	// 图片缩放
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 宽高缩放
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	@Override
	public void onDoubleTap() {

	}

	@Override
	public void onSingleTapConfirmed() {

	}

	@Override
	public void onLongPress() {

	}

	@Override
	public void onLeftOption(boolean left) {

	}

	@Override
	public void onRightOption(boolean right) {

	}

	@Override
	public void changeIndex(int index) {

	}

	@Override
	public void things(View view) {

	}
}
