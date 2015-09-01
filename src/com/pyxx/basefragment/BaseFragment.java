package com.pyxx.basefragment;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.example.palmtrends_utils.R;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.datasource.JsonParser;
import com.pyxx.entity.AdType;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.utils.FinalVariable;

/**
 * Fragment的基类
 * 
 * @author wll
 */
@SuppressLint({ "HandlerLeak", "NewApi" })
public abstract class BaseFragment<T> extends Fragment {
	public int mPage;
	public int mLength = 20;
	public int mFooter_limit = mLength;

	public String mOldtype = "";
	public String mParttype = "";
	public String mUrltype = "";
	public Data mData;
	public ListAdapter mlistAdapter;
	public boolean isloading = false;
	public Handler mHandler;
	public Context mContext;
	public int mHeadType = -1;
	public Object headobj;// 头图变量
	public boolean isShowAD = true;
	public int ad_type = 3;
	public int ad_pos = 1;

	// 变量保存
	private static final String KEY_CONTENT_PARTTYPE = "SinglerListFragment:parttype";
	private static final String KEY_CONTENT_OLDTYPE = "SinglerListFragment:oldtype";
	private static final String KEY_CONTENT_HEADTYPE = "SinglerListFragment:headtype";
	private static final String KEY_CONTENT_HEADOBJ = "SinglerListFragment:headobj";
	private static final String KEY_CONTENT_URLTYPE = "SinglerListFragment:urltype";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup containers,
			Bundle savedInstanceState) {
		if ((mParttype == null || mParttype.equals(""))
				&& (savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT_PARTTYPE
						+ this.getId())) {
			mParttype = savedInstanceState.getString(KEY_CONTENT_PARTTYPE
					+ this.getId());
			mOldtype = savedInstanceState.getString(KEY_CONTENT_OLDTYPE
					+ this.getId());
			mHeadType = savedInstanceState.getInt(KEY_CONTENT_HEADTYPE
					+ this.getId());
			headobj = savedInstanceState.getSerializable(KEY_CONTENT_HEADOBJ
					+ this.getId());
		}
		mContext = inflater.getContext();
		mLength = mContext.getResources().getInteger(R.integer.mleng);
		mFooter_limit = mLength;
		return super.onCreateView(inflater, containers, savedInstanceState);
	}

	/**
	 * 变量保存
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT_PARTTYPE + this.getId(), mParttype);
		outState.putString(KEY_CONTENT_OLDTYPE + this.getId(), mOldtype);
		outState.putInt(KEY_CONTENT_HEADTYPE + this.getId(), mHeadType);
		outState.putSerializable(KEY_CONTENT_HEADOBJ + this.getId(),
				(Serializable) headobj);
	}

	@Override
	public void onDestroy() {
		headobj = null;
		super.onDestroy();
	}

	public void initType(String type, String partType, String urltype) {
		this.mOldtype = type;
		this.mParttype = partType;
		this.mUrltype = urltype;
	}
	
	public void initType(String type, String partType) {
		this.mOldtype = type;
		this.mParttype = partType;
	}

	/**
	 * 返回head
	 * 
	 * @param item
	 * @return
	 */
	public abstract View getListHeadview(T item);

	/**
	 * 返回列表项
	 * 
	 * @param view
	 * @param item
	 * @param position
	 * @return
	 */
	public abstract View getListItemview(View view, T item, int position);

	/**
	 * 界面初始化
	 */
	public abstract void findView();

	/**
	 * 列表事件添加
	 */
	public abstract void addListener();

	/**
	 * 数据加载完成后界面更新
	 */
	public abstract void update();

	/**
	 * 多个头图时调用
	 * 
	 * @param obj
	 * @param type
	 * @return
	 */
	public View getListHeadview(Object obj, int type) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public void fillHead() {
		if (mData != null && mData.list != null && mData.headtype != -1) { // 初始化headview
			mHeadType = mData.headtype;
			if (mData.headtype == 0) {
				Listitem head = (Listitem) mData.obj;
				head.ishead = "true";
				mData.list.add(0, head);
			} else {
				headobj = mData.obj;
				Listitem head = new Listitem();
				head.ishead = "true";
				mData.list.add(0, head);
			}
		}
		// if (mData.headtype != -1) {// 初始化headview
		// mHeadType = mData.headtype;
		// if (mData.headtype == 0) {
		// Listitem head = (Listitem) mData.obj;
		// head.ishead = "true";
		// mData.list.add(0, head);
		// } else {
		// headobj = mData.obj;
		// Listitem head = new Listitem();
		// head.ishead = "true";
		// mData.list.add(0, head);
		// }
		// }

	}

	/**
	 * 数据初始化
	 */
	public void initData() {
		mPage = 1;
		new Thread() {
			String old;

			@Override
			public void run() {
				isloading = true;
				old = mOldtype;
				try {
					Data d = getDataFromDB(mOldtype, 1, mLength, mParttype);
					if (d != null && d.list != null && d.list.size() > 0) {
						mData = d;
						fillHead();
						mHandler.sendEmptyMessage(FinalVariable.update);
					}
					d = getDataFromNet(mUrltype, mOldtype, 1, mLength, true,
							mParttype);
					if (!mOldtype.equals(old)) {
						return;
					}
					if (d != null && d.list != null && d.list.size() > 0) {
						mData = d;
					} else {
						mHandler.sendEmptyMessage(FinalVariable.length);
						return;
					}
					mData = d;
					fillHead();
					onDataLoadComplete(d, true);
				} catch (Exception e) {
					onDataError(e);
				} finally {
					isloading = false;
				}
			}
		}.start();
	}

	/**
	 * 当数据加载错误
	 * 
	 * @param e
	 */
	public void onDataError(Exception e) {
		e.printStackTrace();
		Message msg = new Message();
		if (e instanceof JSONException) {
			msg.what = FinalVariable.nomore;
			if (e.getMessage() != null
					&& (e.getMessage().indexOf("cannot be converted") != -1 || e
							.getMessage().indexOf("End of input") != -1)) {
				msg.obj = mContext.getResources().getString(
						R.string.data_type_error);
			} else {
				msg.obj = e.getMessage();
			}
			mHandler.sendMessage(msg);
		} else {
			msg.what = FinalVariable.error;
			// msg.obj = e.getMessage();
			mHandler.sendMessage(msg);
		}
	}

	/**
	 * 当数据加载完成
	 * 
	 * @param data
	 */
	public void onDataLoadComplete(Data data, boolean isFirst) {
		if (data != null && data.list != null) {
			if (isFirst) {
				mHandler.sendEmptyMessage(FinalVariable.first_update);
			} else {
				mHandler.sendEmptyMessage(FinalVariable.update);
			}
			if (data.loadmorestate == 0) {
				mHandler.sendEmptyMessage(FinalVariable.remove_footer);
			}
		} else {
			mHandler.sendEmptyMessage(FinalVariable.remove_footer);
		}

	}

	/**
	 * 加载更多
	 */
	public void loadMore() {
		if (mPage == 1) {
			mPage++;
		}
		String old;// 不同栏目切换区分
		old = mOldtype;
		try {
			Data d = getDataFromDB(mOldtype, mPage, mLength, mParttype);
			if (d == null || d.list == null || d.list.size() < mLength) {
				if (Utils.isNetworkAvailable(mContext)) {
					d = getDataFromNet(mUrltype, mOldtype, mPage, mLength,
							false, mParttype);
				}
			} else {// 主要用于收藏情况下使用
				mData = d;
				mHandler.sendEmptyMessage(FinalVariable.update);
				mPage++;
				return;
			}
			if (d != null && d.list != null && d.list.size() > 0) {
				if (!mOldtype.equals(old)) {
					return;
				}
				mData = d;
				onDataLoadComplete(mData, false);
				mPage++;
			} else {
				mHandler.sendEmptyMessage(FinalVariable.remove_footer);
			}
		} catch (Exception e) {
			onDataError(e);
			e.printStackTrace();
		} finally {
			isloading = false;
		}
	}

	/**
	 * 刷新操作
	 */
	public void reFlush() {
		String old = mOldtype;
		try {
			mPage = 1;
			isloading = true;
			Data d = getDataFromNet(mUrltype, mOldtype, 1, mLength, true,
					mParttype);
			if (!mOldtype.equals(old)) {
				return;
			}
			if (d != null && d.list != null && d.list.size() > 0) {
				mData = d;
			} else {
				mHandler.sendEmptyMessage(FinalVariable.length);
				return;
			}
			fillHead();
			onDataLoadComplete(d, true);
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			if (e instanceof JSONException) {
				msg.what = FinalVariable.nomore;
				if (e.getMessage() != null
						&& (e.getMessage().indexOf("cannot be converted") != -1 || e
								.getMessage().indexOf("End of input") != -1)) {
					msg.obj = mContext.getResources().getString(
							R.string.data_type_error);
				} else {
					msg.obj = e.getMessage();
				}
				mHandler.sendMessage(msg);
			} else {
				msg.what = FinalVariable.error;
				// msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}
		} finally {
			isloading = false;
		}
	}

	public void resumeAction() {
		if (mlistAdapter != null) {
			if (mParttype.startsWith(DBHelper.FAV_FLAG)) {
				initData();
				// list_adapter.notifyDataSetChanged();
			} else {
				mlistAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onResume() {
		resumeAction();
		super.onResume();
	}

	/**
	 * 获取网络数据并插入数据库
	 * 
	 * @param url
	 * @param oldtype
	 * @param page
	 * @param count
	 * @param isfirst
	 * @param parttype
	 * @return
	 * @throws Exception
	 */
	public Data getDataFromNet(String url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		if (oldtype.startsWith(DBHelper.FAV_FLAG)) {
			return DNDataSource.list_Fav(
					oldtype.replace(DBHelper.FAV_FLAG, ""), page - 1, count);
		}
		String json = DNDataSource.list_FromNET(url, oldtype, page, count,
				parttype, isfirst);
		Data data = parseJson(json);
		if (data != null && data.list != null && data.list.size() > 0) {
			if (isfirst) {
				DBHelper.getDBHelper().delete("listinfo", "listtype=?",
						new String[] { oldtype });
			}
			DBHelper.getDBHelper().insert(oldtype + page, json, oldtype);
		} else {
			return null;
		}
		return data;
	}

	/**
	 * json数据解析
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public Data parseJson(String json) throws Exception {

		return JsonParser.ParserArticleList(json);
	}

	/**
	 * 从数据库获取加载数据
	 * 
	 * @param url
	 * @param oldtype
	 * @param page
	 * @param count
	 * @param parttype
	 * @return
	 * @throws Exception
	 */
	public Data getDataFromDB(String oldtype, int page, int count,
			String parttype) throws Exception {
		String json = DNDataSource.list_FromDB(oldtype, page, count, parttype);
		if (json == null || "".equals(json) || "null".equals(json)) {
			return null;
		}
		Data data = parseJson(json);
		return data;
	}

	public class ListAdapter extends BaseAdapter {
		public List datas;
		String type;// 栏目参数别名

		@SuppressWarnings("unchecked")
		public ListAdapter(List datas, String type) {
			super();
			this.datas = datas;
			this.type = type;

		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			final T li = (T) datas.get(position);
			AdType item = null;
			if (li instanceof AdType) {
				item = (AdType) li;
			}
			if (item != null && "true".equals(item.ishead) && position == 0
					&& !mParttype.startsWith(DBHelper.FAV_FLAG)) {
				if (mHeadType == 0) {
					return getListHeadview(li);
				}
				return getListHeadview(headobj, mHeadType);

			}
			if (v instanceof FrameLayout || v instanceof WebView) {
				v = null;
			}
			v = getListItemview(v, li, position);
			return v;
		}

		public void addDatas(List list) {// 数据追加
			if (datas != null)
				datas.addAll(list);
			this.notifyDataSetChanged();
		}

	}
}
