package com.pyxx.baseview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.palmtrends_utils.R;
import com.pyxx.entity.part;
import com.utils.PerfHelper;

/**
 * 左右滑动Layout
 * 
 * @author wll
 */
public class SecondScrollView extends RelativeLayout implements OnClickListener {
	public List<part> parts = new ArrayList<part>();
	public LinearLayout second_items;
	public LinearLayout second_move_items;
	public View content;// 用于Fragment切换布局
	public View secondscroll;
	public View second_left;
	public View second_right;
	public View second_pasue;
	public boolean second_canscroll = true;
	public RelativeLayout seccond_allitem;// 二级栏目所有item
	public HorizontalScrollView second_content;// 二级栏目滑动
	public int content_id = R.id.secondscroll;
	public boolean hasAnimation = true;

	public SecondScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		content = LayoutInflater.from(this.getContext()).inflate(
				R.layout.second_content, null);
		secondscroll = LayoutInflater.from(this.getContext()).inflate(
				R.layout.secondscroll, null);

	}

	public SecondScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public SecondScrollView(Context context) {
		super(context);
		content = LayoutInflater.from(this.getContext()).inflate(
				R.layout.second_content, null);
		secondscroll = LayoutInflater.from(this.getContext()).inflate(
				R.layout.secondscroll, null);
		seccond_allitem = (RelativeLayout) secondscroll
				.findViewById(R.id.seccond_allitem);
		second_left = secondscroll.findViewById(R.id.second_left);
		second_right = secondscroll.findViewById(R.id.second_rigth);
		second_content = (HorizontalScrollView) secondscroll
				.findViewById(R.id.second_content);

	}

	/**
	 * 设置是否二级栏目可以滑动及滑块的显隐
	 * 
	 * @param canornot
	 */
	public void setsecond_Canscroll(boolean canornot) {
		second_canscroll = canornot;
		if (!second_canscroll) {
			second_right.setVisibility(View.GONE);
			second_left.setVisibility(View.GONE);
			HorizontalScrollView.LayoutParams hl = new HorizontalScrollView.LayoutParams(
					HorizontalScrollView.LayoutParams.FILL_PARENT,
					HorizontalScrollView.LayoutParams.WRAP_CONTENT);
			hl.gravity = Gravity.CENTER;
			seccond_allitem.setLayoutParams(hl);
		}
	}

	/**
	 * 设置是否二级栏目可以滑动及滑块的显隐
	 * 
	 * @param canornot
	 */
	public void setsecond_HasAnimation(boolean canornot) {
		hasAnimation = canornot;
	}

	/**
	 * 代码方式设置布局位置
	 * 
	 * @param rlp
	 */
	public void setScrollContentParam(RelativeLayout.LayoutParams rlp) {
		content.setLayoutParams(rlp);
	}

	/**
	 * 
	 * @param index
	 *            当前返回二级栏目布局位置
	 * @param info
	 *            当前返回二级栏目布局信息设置
	 * @return 返回二级栏目每个ITEM布局
	 */
	public View getItem(int index, part info) {
		TextView vi = new TextView(this.getContext());
		vi.setText(info.part_name);
		vi.setTextSize(18);
		vi.setPadding(5, 0, 5, 0);
		vi.setTextColor(getResources().getColor(android.R.color.black));
		return vi;
	}

	/**
	 * 
	 * @param index
	 *            当前位置
	 * @param info
	 *            当前信息
	 * @return 返回二级栏目滑块布局
	 */
	public View getItem_move(int index, part info) {
		ImageView iv = new ImageView(this.getContext());
		iv.setBackgroundColor(getResources().getColor(
				android.R.color.darker_gray));
		return iv;
	}

	public int init_w = View.MeasureSpec.makeMeasureSpec(0,
			View.MeasureSpec.UNSPECIFIED);
	public int init_h = View.MeasureSpec.makeMeasureSpec(0,
			View.MeasureSpec.UNSPECIFIED);
	public LinearLayout.LayoutParams init_layoutparam;
	public View old_item;
	public View old_move_item;

	/**
	 * 初始化栏目信息
	 */
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		initSecondPart();
	}

	public void addLayout() {
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		rl.addRule(RelativeLayout.BELOW, R.id.second_layout);
		content.setLayoutParams(rl);
		addView(content);
		addView(secondscroll);
	}

	public void initSecondPart() {
		boolean isfirst = true;
		second_items = (LinearLayout) secondscroll
				.findViewById(R.id.second_items);
		second_move_items = (LinearLayout) secondscroll
				.findViewById(R.id.move_items);
		second_move_items.removeAllViews();
		second_items.removeAllViews();
		this.removeAllViews();
		parts = getParts();
		int count = parts.size();
		content_id = Math.abs(parts.get(0).part_type.hashCode());
		content.setId(content_id);
		addLayout();
		all_frag = new Fragment[count];
		for (int i = 0; i < count; i++) {
			View v = getItem(i, parts.get(i));
			v.setTag(parts.get(i).part_sa + "#" + i);
			if (isfirst) {
				changePart(v);
				second_pasue = v;
				isfirst = false;
			}
			second_items.addView(v);
			v.setOnClickListener(this);
			if (hasAnimation) {
				v.measure(init_w, init_h);
				int width = v.getMeasuredWidth();
				int height = v.getMeasuredHeight();
				init_layoutparam = getInit_Secondlayoutparam(width, height);
				View v_Move = getItem_move(i, parts.get(i));
				v_Move.setLayoutParams(init_layoutparam);
				v_Move.setTag(i + "");
				v_Move.setVisibility(View.INVISIBLE);
				second_move_items.addView(v_Move);
			}
		}
		old_move_item = second_move_items.findViewWithTag(0 + "");
		if (old_move_item != null) {
			old_move_item.setVisibility(View.VISIBLE);
		}

		// 添加滑动背景
		if (second_canscroll) {
			final GestureDetector mGestureDetector = new GestureDetector(// 手势控制事件
					new SimpleOnGestureListener() {
						@Override
						public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
							if (second_items.getWidth() == (second_content
									.getScrollX() + second_content.getWidth())) {
								second_right.setVisibility(View.INVISIBLE);
							} else {
								if (0 == second_content.getScrollX()) {
									second_left.setVisibility(View.INVISIBLE);
								} else {
									second_left.setVisibility(View.VISIBLE);
									second_right.setVisibility(View.VISIBLE);
								}
							}
							return super.onScroll(e1, e2, distanceX, distanceY);
						}
					});

			second_content.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					mGestureDetector.onTouchEvent(arg1);
					return false;
				}
			});
		}
	}

	public LinearLayout.LayoutParams getInit_Secondlayoutparam(int w, int h) {
		return new LinearLayout.LayoutParams(w, h);
	}

	public void getParts(String type) {

	}

	@Override
	public void onClick(View v) {
		second_pasue = v;
		if (v.getTag().equals(old_item.getTag())) {
			return;
		}
		startSlipAnimation(
				v,
				second_move_items.findViewWithTag(v.getTag().toString()
						.split("#")[1]));
	}

	// 二级栏目动画滑块控制
	public void startSlipAnimation(final View view, final View move) {
		if (!hasAnimation) {
			changePart(view);
			return;
		}
		Animation animation = new TranslateAnimation(
				0.0f,
				(move.getLeft() + (move.getWidth() / 2))
						- (old_move_item.getLeft() + (old_move_item.getWidth() / 2)),
				0.0f, 0.0f);
		animation.setDuration(200);
		animation.setFillAfter(false);
		animation.setFillBefore(false);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				changePart(view);
				updataCurView(move);
			}
		});
		old_move_item.startAnimation(animation);
		second_move_items.invalidate();
	}

	/**
	 * 栏目切换方法
	 * 
	 * @param current
	 *            当前点击布局
	 * @param old
	 *            旧的点击布局
	 */
	public void changePart(View current) {
		changeStyle(current);
		old_item = current;
		changeFragment(current);

	}

	/**
	 * 改变选择效果
	 * 
	 * @param current
	 */
	public void changeStyle(View current) {
		((TextView) current).setTextColor(getResources().getColor(
				android.R.color.white));
		if (old_item != null)
			((TextView) old_item).setTextColor(getResources().getColor(
					android.R.color.black));
	}

	public Fragment result;
	public static Fragment frag;
	public Fragment all_frag[];

	/**
	 * 栏目fragment初始化
	 * 
	 * @param current
	 */
	public void changeFragment(View current) {

		FragmentActivity fa = (FragmentActivity) this.getContext();
		FragmentManager fm = fa.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		int current_index = Integer.valueOf(current.getTag().toString()
				.split("#")[1]);
		result = fm.findFragmentByTag(current.getTag().toString()
				+ PerfHelper.getStringData("tag"));
		if (frag != null) {
			// ft.remove(frag);
			ft.detach(frag);
		}
		if (result != null) {
			all_frag[current_index] = result;
			ft.attach(result);
			frag = all_frag[current_index];
		} else {
			if (all_frag[current_index] == null) {
				all_frag[current_index] = initFragment(current_index, current
						.getTag().toString().split("#")[0]);
			}
			frag = all_frag[current_index];
			ft.add(content_id,
					frag,
					current.getTag().toString()
							+ PerfHelper.getStringData("tag"));
		}
		ft.commit();
		// fm.executePendingTransactions();
	}

	public Fragment initFragment(int index, String fragmentinfo) {
		return null;
	}

	/**
	 * 滑动布局状态改变
	 * 
	 * @param move
	 */
	public void updataCurView(View move) {
		old_move_item.setVisibility(View.INVISIBLE);
		move.setVisibility(View.VISIBLE);
		old_move_item = move;
	}

	public List<part> getParts() {
		List<part> parts = new ArrayList<part>();
		for (int i = 0; i < 10; i++) {
			part p = new part();
			p.part_name = "天下";
			p.part_sa = "tianxia";
			p.part_type = "news";
			parts.add(p);
		}
		return parts;
	}
}
