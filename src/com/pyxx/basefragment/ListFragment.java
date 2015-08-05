package com.pyxx.basefragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.palmtrends_utils.R;
import com.pyxx.baseview.PullToRefreshListView;
import com.pyxx.baseview.PullToRefreshListView.OnRefreshListener;
import com.pyxx.entity.Data;
import com.pyxx.entity.Entity;
import com.pyxx.loadimage.Utils;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

/**
 * Fragment布局的list工具类
 * 
 * @author wll
 */
public class ListFragment<T extends Entity> extends BaseFragment<T> {
	public ListView mListview;
	public View mMain_layout;// 布局显示
	public LinearLayout mContainers;
	public View mLoading;
	public TextView footer_text;// 加载更多文字提示
	public ProgressBar footer_pb;// 加载更多进度条
	public View mList_footer;// 加载更多
	public FrameLayout.LayoutParams mHead_Layout;// 头图ICON大上设置
	public LinearLayout.LayoutParams mIcon_Layout;// 列表ICON大小设置
	public int nodata_tip = R.string.no_data;
	public int layout_id = R.layout.list_singlerlist;
	private static final String KEY_CONTENT_NODTA_TIP = "SinglerListFragment:nodata_tip";
	private static final String KEY_CONTENT_LAYOUT_ID = "SinglerListFragment:layout_id";
	private static final String KEY_CONTENT_ADCONTROLL = "SinglerListFragment:layout_adcontroll";

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
				&& savedInstanceState.containsKey(KEY_CONTENT_NODTA_TIP)) {
			nodata_tip = savedInstanceState.getInt(KEY_CONTENT_NODTA_TIP);
			layout_id = savedInstanceState.getInt(KEY_CONTENT_LAYOUT_ID);
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
		outState.putInt(KEY_CONTENT_NODTA_TIP, nodata_tip);
		outState.putInt(KEY_CONTENT_LAYOUT_ID, layout_id);
		super.onSaveInstanceState(outState);
	}

	/**
	 * fragment初始化
	 * 
	 * @param inflater
	 */
	public void initListFragment(LayoutInflater inflater) {
		mList_footer = inflater.inflate(R.layout.footer, null);
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
		mListview = (ListView) mMain_layout.findViewById(R.id.list_id_list);
		mLoading = mMain_layout.findViewById(R.id.loading);
		footer_pb = (ProgressBar) mList_footer.findViewById(R.id.footer_pb);
		footer_text = (TextView) mList_footer.findViewById(R.id.footer_text);
		mListview.addFooterView(mList_footer);
	}

	/**
	 * 列表footer初始化
	 */
	public void initfooter() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				footer_text.setText(mContext.getResources().getString(
						R.string.loading_n));
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
		mList_footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isloading) {
					return;
				}
				try {
					footer_text.setText(mContext.getResources().getString(
							R.string.loading));
					footer_pb.setVisibility(View.VISIBLE);
					isloading = true;
					new LoadDataTask().execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			((PullToRefreshListView) mListview)
					.setOnRefreshListener(new OnRefreshListener() {
						public void onRefresh() {
							new GetDataTask().execute();
						}
					});
		} catch (Exception e) {
		}
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mLoading.setVisibility(View.GONE);
				switch (msg.what) {
				case FinalVariable.addfoot:// 添加footer
					if (mListview != null && mList_footer != null) {
						if (mListview.getFooterViewsCount() == 0) {
							mListview.addFooterView(mList_footer);
						}
					}
					break;
				case FinalVariable.nomore:
					mHandler.sendEmptyMessage(FinalVariable.remove_footer);
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						return;
					}
					Utils.showToast(nodata_tip);
					break;
				case FinalVariable.remove_footer:// footer删除
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
					if (!Utils.isNetworkAvailable(mContext)) {
						Utils.showToast(R.string.network_error);
						return;
					}
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						return;
					}
					Utils.showToast(R.string.network_error);

					break;
				case FinalVariable.first_update:
					if (mlistAdapter != null && mlistAdapter.datas != null) {
						mlistAdapter.datas.clear();
						mlistAdapter = null;
					}
					update();
					break;
				case FinalVariable.update:// 数据加载完成界面更新
					update();
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
			mListview.setTag(mOldtype);
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
	public class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			reFlush();
			return new String[] {};
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (mListview instanceof PullToRefreshListView)
				((PullToRefreshListView) mListview).onRefreshComplete(mOldtype);
			super.onPostExecute(result);
		}
	}

	/**
	 * 
	 * 数据下拉刷新操作
	 * 
	 */
	public class LoadDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected String[] doInBackground(Void... params) {
			loadMore();
			return new String[] {};
		}

		@Override
		protected void onPostExecute(String[] result) {
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

}
