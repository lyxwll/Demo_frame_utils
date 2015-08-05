package com.pyxx.baseview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.palmtrends_utils.R;
import com.pyxx.app.ShareApplication;
import com.utils.PerfHelper;

public class PullToRefreshListView extends ListView {
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 2;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	private TranslateAnimation translateAnimation;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	public static final String update_time = "pull_update_time";
	private int headContentWidth;
	private int headContentHeight;
	private int movelength;

	private int startY;
	// private int firstItemIndex;
	private int state = DONE;
	private boolean isBack;
	public OnRefreshListener refreshListener;

	private final static String TAG = "abc";

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	private void init(Context context) {
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.head, null);

		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(50);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		if (ShareApplication.debug) {
			Log.v("size", "width:" + headContentWidth + " height:"
					+ headContentHeight);
		}

		addHeaderView(headView);
		// setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (this.getFirstVisiblePosition() == 0 && !isRecored) {
				startY = (int) event.getY();
				isRecored = true;
			}
			break;
		case MotionEvent.ACTION_UP:

			if (state != REFRESHING) {
				if (state == DONE) {
					// 什么都不做
				}
				if (state == PULL_To_REFRESH) {
					state = DONE;
					changeHeaderViewByState();
					if (ShareApplication.debug) {
						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
				}
				if (state == RELEASE_To_REFRESH) {
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
					// lastUpdatedTextView.setText(getResources().getString(
					// R.string.pull_to_refresh_update)
					// + sdf.format(new Date()));
				}
			}

			isRecored = false;
			isBack = false;

			break;

		case MotionEvent.ACTION_MOVE:
			int tempY = (int) event.getY();
			if (!isRecored && this.getFirstVisiblePosition() == 0) {
				if (ShareApplication.debug) {
					Log.v(TAG, "在move时候记录下位置");
				}
				isRecored = true;
				startY = tempY;
			}
			if (state != REFRESHING && isRecored) {
				// 可以松手去刷新了
				if (state == RELEASE_To_REFRESH) {
					// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
					if (((tempY - startY) / RATIO < headContentHeight)
							&& (tempY - startY) / RATIO > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
						if (ShareApplication.debug) {
							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
					}
					// 一下子推到顶了
					else if ((tempY - startY) / RATIO <= 0) {
						state = DONE;
						changeHeaderViewByState();
						if (ShareApplication.debug) {
							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
					}
					// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
					else {
						// 不用进行特别的操作，只用更新paddingTop的值就行了
					}
				}
				// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
				if (state == PULL_To_REFRESH) {
					// 下拉到可以进入RELEASE_TO_REFRESH的状态
					if ((tempY - startY) / RATIO >= headContentHeight) {
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();
						if (ShareApplication.debug) {
							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
					}
					// 上推到顶了
					else if (tempY - startY <= 0) {
						state = DONE;
						changeHeaderViewByState();
						if (ShareApplication.debug) {
							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}
				}

				// done状态下
				if (state == DONE) {
					if ((tempY - startY) / RATIO > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				// 更新headView的size
				if (state == PULL_To_REFRESH) {
					headView.setPadding(0, -1 * headContentHeight
							+ (tempY - startY) / RATIO, 0, 0);
					headView.invalidate();
				}

				// 更新headView的paddingTop
				if (state == RELEASE_To_REFRESH) {
					headView.setPadding(0, (tempY - startY) / RATIO
							- headContentHeight, 0, 0);
					movelength = (tempY - startY) / RATIO - headContentHeight;
					headView.invalidate();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText(getResources().getString(
					R.string.pull_to_refresh_release_label));
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			if ("".equals(PerfHelper.getStringData(update_time + this.getTag()))) {
				lastUpdatedTextView.setText("尚未更新");
			} else {
				lastUpdatedTextView.setText(PerfHelper
						.getStringData(update_time + this.getTag()));
			}
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText(getResources().getString(
						R.string.pull_to_refresh_tap_label));
			} else {
				tipsTextview.setText(getResources().getString(
						R.string.pull_to_refresh_tap_label));
			}
			if (ShareApplication.debug) {
				Log.v(TAG, "当前状态，下拉刷新");
			}
			break;

		case REFRESHING:
			int headY = headView.getHeight();
			float i = (float) (headY - headContentHeight)
					/ (float) this.getHeight();
			headView.setPadding(0, 0, 0, 0);
			translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, i, Animation.RELATIVE_TO_SELF,
					0.0f);
			translateAnimation.setDuration(300);
			translateAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// headView.setPadding(0, 0, 0, 0);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {

				}
			});
			this.setAnimation(translateAnimation);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(getResources().getString(
					R.string.pull_to_refresh_refreshing_label));
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		case DONE:
			headY = headView.getHeight();
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			float z = 0;
			if (headY <= headContentHeight) {
				z = (float) headY / (float) this.getHeight();
			} else {
				z = (float) headContentHeight / (float) this.getHeight();
			}
			translateAnimation = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, z, Animation.RELATIVE_TO_SELF,
					0.0f);
			translateAnimation.setDuration(300);
			translateAnimation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// headView.setPadding(0, -1 * headContentHeight, 0, 0);
				}
			});
			this.setAnimation(translateAnimation);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			tipsTextview.setText(getResources().getString(
					R.string.pull_to_refresh_tap_label));
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

	Handler h = new Handler();

	public void deal(int current) {
		if (headContentHeight < current) {
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			return;
		}
		final int c = current + 45;
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				headView.setPadding(0, -1 * c, 0, 0);
				// headView.invalidate();
				deal(c);
			}
		}, 1);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete(String oldtype) {
		state = DONE;
		PerfHelper.setInfo(
				update_time + oldtype,
				getResources().getString(R.string.pull_to_refresh_update)
						+ sdf.format(new Date()));
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
}
