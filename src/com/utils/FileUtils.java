package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.pyxx.loadimage.Utils;

/**
 * 文件保存到sd卡中
 * 
 * @author wll
 */
public class FileUtils {
	public static String sdPath; // 存放sd卡目录
	public static String savePath;
	static {
		savePath = Environment.getExternalStorageDirectory()
				+ "/download/image/";
		sdPath = Environment.getExternalStorageDirectory()
				+ "/.nomedia/pyxx/"
				+ ShareApplication.share.getResources().getString(
						R.string.cache_dir) + "/";
		createSdDir("");
	}

	public String getSdPath() {
		return sdPath;
	}

	public static String converPathToName(String path) {
		if (path.endsWith("png") | path.endsWith("jpg")) {
			path = path.substring(path.lastIndexOf("/") + 1, path.length());
		} else {
			try {
				path = URLEncoder.encode(path.replace("*", ""), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return path;
	}

	public FileUtils() {
		// 得到sd卡的目录
		// sdPath = Environment.getExternalStorageDirectory() + "/zhangyue/";
	}

	/**
	 * 获取文件大小
	 * 
	 * @param length
	 * @return
	 */
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	}

	/**
	 * 文件创建
	 * 
	 * @param fileName
	 * @return 被创建好的文件
	 * @throws IOException
	 */
	public static File createSdFile(String fileName) throws IOException {
		File file = new File(sdPath);
		if (!file.exists())
			createSdDir("");
		file = new File(sdPath + "image/");
		if (!file.exists())
			createSdDir("image/");
		file = new File(sdPath + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 文件保存
	 * 
	 * @param fileName
	 * @return 被创建好的文件
	 * @throws IOException
	 */
	public static File saveSdFile(String fileName) throws IOException {
		File file = new File(savePath);

		if (!file.exists())
			file.mkdirs();

		file = new File(savePath + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 目录创建
	 * 
	 * @param dirName
	 * @return 返回被创建的文件目录
	 */
	public static File createSdDir(String dirName) {
		File file = new File(sdPath + dirName);
		if (!file.exists()) {
			file.mkdirs();
		}

		return file;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(sdPath + fileName);
		return file.exists();
	}

	public static void deleteFiles() {
		File file = new File(sdPath);
		if (file.exists()) {
			File[] files = file.listFiles();
			int count = files.length;
			for (int i = 0; i < count; i++) {
				if (files[i].isDirectory()) {
					File[] fs = files[i].listFiles();
					int cou = fs.length;
					for (int j = 0; j < cou; j++) {
						fs[j].delete();
					}
				} else {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param path1
	 * @param path2
	 */
	public void copy(String path1, String path2) {
		try {
			FileInputStream fis = new FileInputStream(path1);
			int l = fis.available();
			byte[] c = new byte[l];
			fis.read(c);
			fis.close();
			int i = path2.lastIndexOf("/") + 1;
			String path = path2.substring(0, i);
			File f = new File(path);
			f.mkdirs();
			FileOutputStream fos = new FileOutputStream(path2);
			fos.write(c);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Drawable getImageSd(String fileName) throws IOException {
		File f = new File(sdPath + "image/" + fileName);
		if (f.exists()) {
			return Drawable.createFromPath(f.getAbsolutePath());
		}
		// if (PerfHelper.getBooleanData(PerfHelper.isnetstate) && type != null
		// && !"".equals(type)) {
		// f = new File(sdPath + "html/" + type + "/" + fileName);
		// if (type.endsWith("article")) {
		// String[] info = type.split("_");
		// System.out.println(sdPath + "html/" + info[1] + "/" + info[0]
		// + "/" + fileName);
		// f = new File(sdPath + "html/" + info[1] + "/" + info[0] + "/"
		// + fileName);
		// }
		// if (!f.exists()) {
		// return null;
		// }
		// return Drawable.createFromPath(f.getAbsolutePath());
		// }

		return null;
	}

	public static Drawable getImageSdFromSave(String fileName)
			throws IOException {
		File f = new File(savePath + fileName);
		if (f.exists()) {
			return Drawable.createFromPath(f.getAbsolutePath());
		}

		return null;
	}

	public static void writeToFile(final Bitmap bt, final String icon) {
		new Thread() {
			@Override
			public void run() {
				if (bt != null) {
					File file;
					FileOutputStream output = null;
					try {
						file = FileUtils.saveSdFile(converPathToName(icon));
						output = new FileOutputStream(file);
						bt.compress(CompressFormat.PNG, 100, output);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (output != null) {
								output.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				Utils.showToast("已保存到/download/image/下");
				Utils.dismissProcessDialog();
				super.run();
			}
		}.start();
	}

	public static void writeCacheToFile(InputStream is, String imagename)
			throws Exception {
		File file = FileUtils.createSdFile("image/" + imagename);
		FileOutputStream output = new FileOutputStream(file);
		int count = 0;
		byte[] bs = new byte[1024 * 2];
		while ((count = is.read(bs)) != -1) {
			output.write(bs, 0, count);
		}
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Map readCacheToMap() throws Exception {
		FileInputStream fis = new FileInputStream(sdPath + "t.tmp");
		ObjectInputStream ois = new ObjectInputStream(fis);
		int i = ois.readInt();
		ois.close();
		return null;
	}

	public static void writeImage(Bitmap bitmap, String destPath, int quality) {
		try {
			FileUtils.deleteFile(destPath);
			if (FileUtils.createFile(destPath)) {
				FileOutputStream out = new FileOutputStream(destPath);
				if (bitmap.compress(Bitmap.CompressFormat.PNG, quality, out)) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		File file = new File(path);
		// 先判断文件是否存在
		if (file.exists()) {
			// 文件可写，文件不是目录
			if (file.canWrite() && file.isFile()) {
				// 删掉文件
				// Logg.out("delete File     =" + file.getAbsolutePath());
				return file.delete();
			} else if (file.canWrite() && file.isDirectory()) {
				// 文件是目录时，拿到本目录下面的所有文件
				String[] files = file.list();
				for (String f : files) {
					deleteFile(file.getAbsolutePath() + File.separator + f);
					// 递归删除本目录下所有的文件
				}
				// 最后删除本目录
				// Logg.out("delete Directory=" + file.getAbsolutePath());
				return file.delete();// 删除当前目录
			}
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * 创建一个文件，创建成功返回true
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean createFile(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				return file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 将一个文件拷贝到另外一个地方
	 * 
	 * @param sourceFile
	 *            源文件地址
	 * @param destFile
	 *            目的地址
	 * @param shouldOverlay
	 *            是否覆盖
	 * @return
	 */
	public static boolean copyFiles(String sourceFile, String destFile,
			boolean shouldOverlay) {
		try {
			if (shouldOverlay) {
				deleteFile(destFile);
			}
			FileInputStream fi = new FileInputStream(sourceFile);
			writeFile(destFile, fi);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从一个输入流里写文件
	 * 
	 * @param destFilePath
	 *            要创建的文件的路径
	 * @param in
	 *            要读取的输入流
	 * @return 写入成功返回true,写入失败返回false
	 */
	public static boolean writeFile(String destFilePath, InputStream in) {
		try {
			if (!createFile(destFilePath)) {
				return false;
			}
			FileOutputStream fos = new FileOutputStream(destFilePath);
			int readCount = 0;
			int len = 1024;
			byte[] buffer = new byte[len];
			while ((readCount = in.read(buffer)) != -1) {
				fos.write(buffer, 0, readCount);
			}
			fos.flush();
			if (null != fos) {
				fos.close();
				fos = null;
			}
			if (null != in) {
				in.close();
				in = null;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

}
