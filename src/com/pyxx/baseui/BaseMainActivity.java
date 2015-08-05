package com.pyxx.baseui;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TabHost;

import com.example.palmtrends_utils.R;

public class BaseMainActivity extends ActivityGroup {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean flag = getResources().getBoolean(R.bool.hashome);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!getResources().getBoolean(R.bool.hashome)) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	public TabHost.TabSpec buildTabSpec(TabHost tab, String tag, Intent intent) {
		TabHost.TabSpec spec = tab.newTabSpec(tag);
		spec.setIndicator(tag);
		spec.setContent(intent);
		return spec;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = getResources().getBoolean(R.bool.hashome);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!flag) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.exit)
						.setMessage(
								getString(
										R.string.exit_message,
										new String[] { getString(R.string.app_name) }))
						.setPositiveButton(R.string.done,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();

									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
									}
								}).show();
			} else {
				this.finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
