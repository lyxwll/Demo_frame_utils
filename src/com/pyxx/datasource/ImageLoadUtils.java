package com.pyxx.datasource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.pyxx.dao.Urls;
import com.pyxx.entity.DataTransport;
import com.utils.FileUtils;
import com.utils.FinalVariable;

/**
 * 图片加载并缓存
 * 
 * @author HeJian
 * 
 */
public class ImageLoadUtils {
	public static Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(8);

	public static void downloadFileForSave(final String imageUrl,
			final Handler handler) {
		executorService.submit(new Runnable() {
			public void run() {
				String imagename = FileUtils.converPathToName(imageUrl);
				if (handler != null) {
					Message m = new Message();
					DataTransport dt = new DataTransport();
					try {
						dt.bit = FileUtils.getImageSdFromSave(imagename);
						if (dt.bit != null) {
							dt.url = imageUrl;
							m.obj = dt;
							m.what = FinalVariable.load_image;
							handler.sendMessage(m);
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					String url = "";
					if (imageUrl.startsWith("http")) {
						url = imageUrl;
					} else {
						url = Urls.main
								+ imageUrl.replace(Urls.main, "").replace(
										"file://", "");
					}
					final Drawable drawable = Drawable.createFromStream(
							new URL(url).openStream(), "image.png");
					// imageCache.put(imageUrl, new SoftReference<Drawable>(
					// drawable));
					if (handler != null) {
						Message m = new Message();
						DataTransport dt = new DataTransport();
						dt.bit = drawable;
						dt.url = imageUrl;
						m.obj = dt;
						m.what = FinalVariable.load_image;
						handler.sendMessage(m);
					}
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					imagename = FileUtils.converPathToName(imageUrl);
					File file = new File(FileUtils.savePath);
					if (file.exists()) {
						file.mkdirs();
					}
					file = new File(file.getAbsoluteFile() + "/" + imagename);
					file.createNewFile();
					FileOutputStream output = new FileOutputStream(file);
					if (imagename.endsWith("png")) {
						bitmap.compress(CompressFormat.PNG, 80, output);
					} else {
						bitmap.compress(CompressFormat.JPEG, 80, output);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static void downloadFile(final String imageUrl, final Handler handler) {
		// if (imageCache.containsKey(imageUrl)) {
		// SoftReference<Drawable> softReference = imageCache.get(imageUrl);
		// if (softReference.get() != null) {
		// if (handler != null) {
		// Message m = new Message();
		// DataTransport dt = new DataTransport();
		// dt.bit = softReference.get();
		// dt.url = imageUrl;
		// m.obj = dt;
		// m.what = FinalVariable.load_image;
		// handler.sendMessage(m);
		// }
		// return;
		// }
		// }

		// System.out.println("-------------------------" + Urls.main
		// + imageUrl.replace(Urls.main, "").replace("file://", ""));
		executorService.submit(new Runnable() {
			public void run() {
				String imagename = FileUtils.converPathToName(imageUrl);
				if (handler != null) {
					Message m = new Message();
					DataTransport dt = new DataTransport();
					try {
						dt.bit = FileUtils.getImageSd(imagename);
						if (dt.bit != null) {
							dt.url = imageUrl;
							m.obj = dt;
							m.what = FinalVariable.load_image;
							handler.sendMessage(m);
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				try {
					String url = "";
					if (imageUrl.startsWith("http")) {
						url = imageUrl;
					} else {
						url = Urls.main
								+ imageUrl.replace(Urls.main, "").replace(
										"file://", "");
					}
					final Drawable drawable = Drawable.createFromStream(
							new URL(url).openStream(), "image.png");
					// imageCache.put(imageUrl, new SoftReference<Drawable>(
					// drawable));
					if (handler != null) {
						Message m = new Message();
						DataTransport dt = new DataTransport();
						dt.bit = drawable;
						dt.url = imageUrl;
						m.obj = dt;
						m.what = FinalVariable.load_image;
						handler.sendMessage(m);
					}
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					imagename = FileUtils.converPathToName(imageUrl);
					File file = FileUtils.createSdFile("image/" + imagename);
					FileOutputStream output = new FileOutputStream(file);
					if (imagename.endsWith("png")) {
						bitmap.compress(CompressFormat.PNG, 100, output);
					} else {
						bitmap.compress(CompressFormat.JPEG, 100, output);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static void downloadFile(final String imageUrl, final String type,
			final Handler handler) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {
				if (handler != null) {
					Message m = new Message();
					DataTransport dt = new DataTransport();
					dt.bit = softReference.get();
					dt.url = imageUrl;
					m.obj = dt;
					m.what = FinalVariable.load_image;
					handler.sendMessage(m);
				}
				return;
			}
		}
		final String imagename = FileUtils.converPathToName(imageUrl);
		if (FileUtils.isFileExist("image/" + type + imagename)) {
			if (handler != null) {
				try {
					Message m = new Message();
					DataTransport dt = new DataTransport();
					dt.bit = FileUtils.getImageSd(type + imagename);
					dt.url = imageUrl;
					m.obj = dt;
					m.what = FinalVariable.load_image;
					handler.sendMessage(m);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		executorService.submit(new Runnable() {
			public void run() {
				try {
					URL url = null;
					if (imageUrl.startsWith("http")) {
						url = new URL(imageUrl);
					} else {
						url = new URL(Urls.main
								+ imageUrl.replace(Urls.main, ""));
					}
					final Drawable drawable = Drawable.createFromStream(
							url.openStream(), "image.png");
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					if (handler != null) {
						Message m = new Message();
						DataTransport dt = new DataTransport();
						dt.bit = drawable;
						dt.url = imageUrl;
						m.obj = dt;
						m.what = FinalVariable.load_image;
						handler.sendMessage(m);
					}
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					File file = FileUtils.createSdFile("image/" + type
							+ imagename);
					FileOutputStream output = new FileOutputStream(file);
					if (imagename.endsWith("png")) {
						bitmap.compress(CompressFormat.PNG, 75, output);
					} else {
						bitmap.compress(CompressFormat.JPEG, 75, output);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}