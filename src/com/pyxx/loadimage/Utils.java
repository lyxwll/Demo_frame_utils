/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyxx.loadimage;

import java.io.File;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.StatFs;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.utils.FileUtils;

/**
 * Class containing some static utility methods.
 */
@SuppressLint("NewApi")
public class Utils {
	public static final int IO_BUFFER_SIZE = 8 * 1024;

	private Utils() {
	};

	/**
	 * Workaround for bug pre-Froyo, see here for more info:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */

	public static int getBitmapSize(Bitmap bitmap) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
		// return bitmap.getByteCount();
		// }
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */

	@SuppressLint("NewApi")
	public static boolean isExternalStorageRemovable() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		// return Environment.isExternalStorageRemovable();
		// }
		return true;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */

	public static File getExternalCacheDir(Context context) {
		// if (hasExternalCacheDir()) {
		// return context.getExternalCacheDir();
		// }
		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = FileUtils.sdPath + "/pic_cache/";
		return new File(cacheDir);
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */

	public static long getUsableSpace(File path) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		// return path.getUsableSpace();
		// }
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * Get the memory class of this device (approx. per-app memory limit)
	 * 
	 * @param context
	 * @return
	 */
	public static int getMemoryClass(Context context) {
		return ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	/**
	 * Check if OS version has a http URLConnection bug. See here for more
	 * information:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 * 
	 * @return
	 */
	public static boolean hasHttpConnectionBug() {
		return Build.VERSION.SDK_INT < 8;
	}

	/**
	 * Check if OS version has built-in external cache dir method.
	 * 
	 * @return
	 */
	public static boolean hasExternalCacheDir() {
		return Build.VERSION.SDK_INT >= 8;
	}

	/**
	 * Check if ActionBar is available.
	 * 
	 * @return
	 */
	public static boolean hasActionBar() {
		return Build.VERSION.SDK_INT >= 11;
	}

	private static final int DATE_DIALOG = 0;
	public static Handler h;

	public static void showToast(final String info) {
		h.post(new Runnable() {

			@Override
			public void run() {
				if (mypDialog != null) {
					mypDialog.dismiss();
				}
				try {
					// Toast.makeText(ShareApplication.share, info,
					// 2000).show();
					showToast(info, Toast.LENGTH_SHORT, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void showToast(final int id) {
		h.post(new Runnable() {
			@Override
			public void run() {
				if (mypDialog != null) {
					mypDialog.dismiss();
				}
				try {
					Toast.makeText(
							ShareApplication.share,
							ShareApplication.share.getResources().getString(id),
							2000).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void showDialog(Context ac, String info, String title) {
		try {
			new AlertDialog.Builder(ac)
					.setPositiveButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).setMessage(info).setTitle(title).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showTimeDialog(final Activity ac, int type,
			final TextView tv) {
		Calendar calendar = Calendar.getInstance();
		if (type == DATE_DIALOG) {
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					tv.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
				}
			};
			DatePickerDialog dlg = new DatePickerDialog(ac, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			dlg.show();
		} else {
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker timePicker, int hourOfDay,
						int minute) {
					tv.setText(hourOfDay + ":" + minute + ":00");
				}
			};
			new TimePickerDialog(ac, timeListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true).show();
		}

	}

	public static ProgressDialog mypDialog;

	public static void dismissProcessDialog() {
		h.post(new Runnable() {
			@Override
			public void run() {
				if (mypDialog != null) {
					mypDialog.dismiss();
					mypDialog = null;
				}
			}
		});

	}

	public static ProgressDialog showProcessDialog(Context ac, String message) {
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(message);
		mypDialog.setCancelable(false);
		mypDialog.setIndeterminate(false);
		mypDialog.setCancelable(false);
		mypDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				h.post(new Runnable() {

					@Override
					public void run() {
						mypDialog.dismiss();
						// mypDialog = null;

					}
				});

				return false;
			}
		});
		try {
			h.post(new Runnable() {

				@Override
				public void run() {
					mypDialog.show();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mypDialog;
	}

	public static ProgressDialog showProcessDialog(Context ac, int id) {
		if (mypDialog != null) {
			try {
				mypDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mypDialog.setMessage(ac.getResources().getString(id));

			return mypDialog;
		}
		mypDialog = new ProgressDialog(ac);
		mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mypDialog.setMessage(ac.getResources().getString(id));
		mypDialog.setCancelable(false);
		mypDialog.setIndeterminate(false);
		mypDialog.setCancelable(false);
		mypDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				mypDialog.dismiss();
				mypDialog = null;
				return false;
			}
		});
		try {
			mypDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mypDialog;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static Toast toast;
	private static String lastToast = "";
	private static long lastToastTime;

	public static void showToast(String message, int duration, int icon) {
		if (message != null && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();
			if (!message.equalsIgnoreCase(lastToast)
					|| Math.abs(time - lastToastTime) > 2000) {

				View view = LayoutInflater.from(ShareApplication.share)
						.inflate(R.layout.v2_view_toast, null);
				((TextView) view.findViewById(R.id.title_tv)).setText(message);
				if (icon != 0) {
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					((ImageView) view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}

				if (null == toast) {
					toast = new Toast(ShareApplication.share);
				}

				toast.setView(view);
				toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0,
						dip2px(ShareApplication.share, 48));
				// getToastMarignBottom()
				// toast.setGravity(Gravity.TOP|Gravity.LEFT,0 ,0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();

				lastToast = message;
				lastToastTime = System.currentTimeMillis();
			}
		}
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
