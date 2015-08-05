package com.pyxx.baseview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.pyxx.entity.Listitem;

/**
 * 继承{@link ViewPager},重写{@link ViewPager#onInterceptTouchEvent(MotionEvent)}
 * 方法,用于拦截{@link OnTouchListener}事件.
 * 
 * 
 */
public class ImageDetailViewPager extends ViewPager {

	private boolean isLeft = true;
	private boolean isRight = true;
	private boolean isMove = true;

	public ImageDetailViewPager(Context context) {
		super(context);
	}

	public ImageDetailViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 1
	// true-->onTouchEvent
	// false --> interceptTouchEvent
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	//
	// if(isMove){
	// System.out.println("......true");
	// return super.dispatchTouchEvent(ev);
	// }
	// System.out.println("......fasle");
	// return false;
	// }

	// 2
	// true--> onTouchEvent
	// false --> 子view 的 dispatchTouchEvent
	/**
	 * 向左滑动isLeft = true,向右滑动isRight = true.反之则不能.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		if (isLeft || isRight) {
			return super.onInterceptTouchEvent(event);
		}
		// if (isMove) {
		// return super.onInterceptTouchEvent(event);
		// }
		return false;
	}

	public void isMove(boolean move) {
		isMove = move;
	}

	public void isLeft(boolean left) {
		isLeft = left;
		if (mListener != null) {
			mListener.onLeftOption(left);
		}
		// throw new RuntimeException();
	}

	public void isRight(boolean right) {
		isRight = right;
		if (mListener != null) {
			mListener.onRightOption(right);
		}
	}

	// 监听器
	private OnViewListener mListener;

	/**
	 * 获得监听事件
	 * 
	 * @return 返回监听器
	 */
	public OnViewListener getOnViewListener() {
		return mListener;
	}

	/**
	 * 设置监听事件
	 * 
	 * @param mListener
	 *            监听器
	 */
	public void setOnViewListener(OnViewListener mListener) {
		this.mListener = mListener;
	}

	/**
	 * 点击、双击、长按接口
	 */
	public interface OnViewListener {
		// 双击
		void onDoubleTap();

		// 单击
		void onSingleTapConfirmed();

		// 长按
		void onLongPress();

		// 左滑事件
		void onLeftOption(boolean left);

		// 右滑事件
		void onRightOption(boolean right);

	}

	public static interface OnArticleOptions {
		int FAV_CANCLE = 1;
		int FAV_ = 2;

		void onInitData(Listitem item);

		// 通过things定义自己和外部数据交互
		void onThings(int things);
	}
}
