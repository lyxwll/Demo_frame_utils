package com.pyxx.basefragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.palmtrends_utils.R;
import com.pyxx.baseview.XListView;
import com.pyxx.baseview.XListView.IXListViewListener;
import com.pyxx.entity.Data;
import com.pyxx.entity.Entity;
import com.pyxx.loadimage.Utils;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

/**
 * Fragment布局的刷新加载list工具类
 * 
 * @author wll
 */
public class LoadMoreListFragment<T extends Entity> extends BaseFragment<T>
		implements IXListViewListener {
	public XListView mListview;
	public View mMain_layout;// 布局显示
	public LinearLayout mContainers;
	public View mLoading;
	public View mLoading_nodate;
	public View mLoading_hasdate;
	public TextView footer_text;// 加载更多文字提示
	public ProgressBar footer_pb;// 加载更多进度条
	public View mList_footer;// 加载更多
	public FrameLayout.LayoutParams mHead_Layout;// 头图ICON大上设置
	public LinearLayout.LayoutParams mIcon_Layout;// 列表ICON大小设置
	public int nodata_tip = R.string.no_data;
	public int layout_id = R.layout.list_loadmoresinglerlist;
	private static final String KEY_CONTENT_NODTA_TIP = "SinglerListFragment:nodata_tip";
	private static final String KEY_CONTENT_LAYOUT_ID = "SinglerListFragment:layout_id";

	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vgroup,
			Bundle savedInstanceState) {
		if ((mParttype == null || mParttype.equals(""))
				&& (savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT_NODTA_TIP
						+ this.getId())) {
			nodata_tip = savedInstanceState.getInt(KEY_CONTENT_NODTA_TIP
					+ this.getId());
			layout_id = savedInstanceState.getInt(KEY_CONTENT_LAYOUT_ID
					+ this.getId());
		}
		super.onCreateView(inflater, vgroup, savedInstanceState);
		if (mMain_layout == null) {
			mContainers = new LinearLayout(mContext);
			mMain_layout = inflater.inflate(layout_id, null);
			initListFragment(inflater);
			mContainers.addView(mMain_layout);
		} else {
			mContainers.removeAllViews();
			mContainers = new LinearLayout(getActivity());
			mContainers.addView(mMain_layout);
		}
		return mContainers;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_CONTENT_NODTA_TIP + this.getId(), nodata_tip);
		outState.putInt(KEY_CONTENT_LAYOUT_ID + this.getId(), layout_id);
		super.onSaveInstanceState(outState);
	}

	/**
	 * fragment初始化
	 * 
	 * @param inflater
	 */
	public void initListFragment(LayoutInflater inflater) {

		int w = (99 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		int h = (88 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		mIcon_Layout = new LinearLayout.LayoutParams(w, h);
		findView();
		addListener();
		initData();
	}

	/**
	 * fragment布局查找
	 */
	public void findView() {
		mListview = (XListView) mMain_layout.findViewById(R.id.list_id_list);
		mListview.setTag(mOldtype);
		mListview.setPullLoadEnable(true);
		mListview.setXListViewListener(this);
		mLoading = mMain_layout.findViewById(R.id.loading);
		mLoading_nodate = mMain_layout.findViewById(R.id.laoding_no_date);
		mLoading_hasdate = mMain_layout.findViewById(R.id.laoding_has_date);
		mList_footer = mListview.mFooterView;
		footer_pb = (ProgressBar) mList_footer
				.findViewById(R.id.xlistview_footer_progressbar);
		footer_text = (TextView) mList_footer
				.findViewById(R.id.xlistview_footer_hint_textview);
		// mListview.addFooterView(mList_footer);
	}

	/**
	 * 列表footer初始化
	 */
	public void initfooter() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				footer_text.setText(mContext.getResources().getString(
						R.string.xlistview_footer_hint_normal));
				footer_pb.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 点击事件重写
	 * 
	 * @param item
	 * @param position
	 * @return
	 */
	public boolean dealClick(T item, int position) {
		return false;
	}

	/**
	 * fragment交互事件添加
	 */
	public void addListener() {
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				T li = (T) parent.getItemAtPosition(position);
				if (dealClick(li, position)) {
					return;
				}
			}
		});
		// mLi
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mLoading.setVisibility(View.GONE);
				switch (msg.what) {
				case FinalVariable.addfoot:// 添加footer
					mLoading_nodate.setVisibility(View.GONE);
					if (mListview != null && mList_footer != null) {
						if (mListview.getFooterViewsCount() == 0) {
							mListview.addFooterView(mList_footer);
						}
					}
					break;
				case FinalVariable.nomore:
					mHandler.sendEmptyMessage(FinalVariable.remove_footer);
					mLoading.setVisibility(View.VISIBLE);
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						mLoading_nodate.setVisibility(View.VISIBLE);
						return;
					}
					Utils.showToast(nodata_tip);
					mLoading_nodate.setVisibility(View.VISIBLE);
					break;
				case FinalVariable.remove_footer:// footer删除
					mLoading_nodate.setVisibility(View.GONE);
					if (mListview != null && mList_footer != null
							&& mListview.getFooterViewsCount() > 0) {
						try {
							mListview.removeFooterView(mList_footer);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					initfooter();
					break;
				case FinalVariable.error:// 网络连接出错
					initfooter();
					if (mData != null && mData.list.size() > 0) {
					} else {
						mLoading_nodate.setVisibility(View.VISIBLE);
						mLoading_nodate
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										new Thread(new Runnable() {

											@Override
											public void run() {
												// stub
												reFlush();
											}
										}).start();
										mLoading_nodate
												.setVisibility(View.GONE);
									}
								});
						mLoading.setVisibility(View.VISIBLE);
					}

					if (!Utils.isNetworkAvailable(mContext)) {
						Utils.showToast(R.string.network_error);
						mLoading_nodate.setVisibility(View.VISIBLE);
						return;
					}
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						mLoading_nodate.setVisibility(View.VISIBLE);
						return;
					}
					Utils.showToast(nodata_tip);
					mLoading_nodate.setVisibility(View.VISIBLE);
					break;
				case FinalVariable.first_update:
					mLoading_nodate.setVisibility(View.GONE);
					if (mlistAdapter != null && mlistAdapter.datas != null) {
						mlistAdapter.datas.clear();
						mlistAdapter = null;
					}
					update();
					break;
				case FinalVariable.update:// 数据加载完成界面更新
					mLoading_nodate.setVisibility(View.GONE);
					update();
					break;
				case FinalVariable.length:
					initfooter();
					if (mData != null && mData.list.size() > 0) {
					} else {
						mLoading_nodate.setVisibility(View.VISIBLE);
						((TextView) mMain_layout
								.findViewById(R.id.but_refurbish_data))
								.setText("暂无符合条件的信息");
						mLoading_nodate
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										new Thread(new Runnable() {

											@Override
											public void run() {
												reFlush();
											}
										}).start();
										mLoading_nodate
												.setVisibility(View.GONE);
									}
								});
						mLoading.setVisibility(View.VISIBLE);
					}
					if (!Utils.isNetworkAvailable(mContext)) {
						Utils.showToast(R.string.network_error);
						return;
					}
					break;

				}
			}
		};
	};

	/**
	 * 当数据加载完成
	 */
	public void onupdate(Data data) {

	}

	/**
	 * 数据返回后列表刷新
	 */
	@Override
	public void update() {

		initfooter();
		mLoading.setVisibility(View.GONE);
		if (mData != null) {
			onupdate(mData);
			isloading = false;
			if (mlistAdapter == null) {
				mlistAdapter = new ListAdapter(mData.list, mParttype);// 初始化列表数据
				mListview.setAdapter(mlistAdapter);
				if (mData.list.size() < mFooter_limit) {// 控制footer是否显示出来
					mHandler.sendEmptyMessage(FinalVariable.remove_footer);
				} else {
					mHandler.sendEmptyMessage(FinalVariable.addfoot);
				}

			} else {
				mlistAdapter.addDatas(mData.list);// 控制数据
				if (mData.list.size() < mFooter_limit) {
					mHandler.sendEmptyMessage(FinalVariable.remove_footer);
				} else {
					mHandler.sendEmptyMessage(FinalVariable.addfoot);
				}
			}
		}

	}

	/**
	 * 
	 * 数据下拉刷新操作
	 * 
	 */
	public class GetDataTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			if (isloading) {
				return false;
			}
			if (params[0].startsWith("ref") || params == null) {
				reFlush();
			} else {
				try {
					// footer_text.setText(mContext.getResources().getString(
					// R.string.loading));
					// footer_pb.setVisibility(View.VISIBLE);
					isloading = true;
					loadMore();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result)
				onLoad();
			super.onPostExecute(result);
		}
	}

	@Override
	public View getListHeadview(T item) {
		return null;
	}

	@Override
	public View getListItemview(View view, T item, int position) {
		return null;
	}

	@Override
	public void onRefresh() {
		new GetDataTask().execute("refulsh");
	}

	@Override
	public void onLoadMore() {
		if (mListview.getFooterViewsCount() > 0) {
			new GetDataTask().execute("loadmore");
		}
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private void onLoad() {
		mListview.stopRefresh();
		mListview.stopLoadMore();
		String time = sdf.format(new Date());
		PerfHelper.setInfo(XListView.update_time + mOldtype, time);
		mListview.setRefreshTime(time);
	}
}
