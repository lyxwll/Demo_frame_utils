package com.pyxx.baseui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.pyxx.app.ShareApplication;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.utils.FileUtils;

public class BaseArticleActivity extends BaseActivity {
	public static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	public static final int WEIXIN_ONE = 0; // 单个好友
	public static final int WEIXIN_QUAN = 1; // 朋友圈
	public Listitem mCurrentItem;
	public int weixin_type = WEIXIN_ONE;

	public static Map<String, ArrayList<Listitem>> o_items = new HashMap<String, ArrayList<Listitem>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		o_items.clear();
	}

	@Override
	public void things(View view) {

	}

	public String shorturl = "";
	public String picurl = "";

	public void shareEmail(String info, String picurl, String title) {
		// 附件文件地址
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, info); // 正文
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		if (picurl != null && !"".equals(picurl)) {
			File file = new File(FileUtils.sdPath + "image/"
					+ FileUtils.converPathToName(picurl));
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
			intent.setType("message/rfc822");
		}
		try {
			startActivity(intent); // 调用系统的mail客户端进行发送
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showToast("请安装邮件客户端");
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri) {
		if (ShareApplication.debug)
			System.out.println("下载分享图片:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private static final int THUMB_SIZE = 150;

	private Bitmap comp(Bitmap image) {
		if (ShareApplication.debug)
			System.out.println("开始压缩分享图片:");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 32) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		if (ShareApplication.debug)
			System.out.println("结束压缩分享图片:");
		return bitmap;
	}

	/**
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return Bitmap
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		Matrix matrix = new Matrix();
		// 宽高缩放率
		float scaleWidth = ((float) 120) / width;
		float scaleHeight = ((float) 120) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

}
