package com.example.phonegetin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

public class CircularProgressBar extends ProgressBar {

	private static final String TAG = "CircularProgressBar";

	private static final int STROKE_WIDTH = 80;

	private String mTitle = "";
	private String mSubTitle = "";

	private int mStrokeWidth = STROKE_WIDTH;

	private RectF mCircleBounds;

	private Paint mProgressColorPaint;
	private Paint mBackgroundColorPaint;
	private Paint mTitlePaint;
	private Paint mSubtitlePaint;

	private boolean mHasShadow = true;
	private int mShadowColor = Color.BLACK;

	public interface ProgressAnimationListener {
		public void onAnimationStart();

		public void onAnimationFinish();

		public void onAnimationProgress(int progress);
	}

	public CircularProgressBar(Context context) {
		super(context);
		mCircleBounds = new RectF();
		mBackgroundColorPaint = new Paint();
		mProgressColorPaint = new Paint();
		mTitlePaint = new Paint();
		mSubtitlePaint = new Paint();
		init(null, 0);
	}

	public CircularProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCircleBounds = new RectF();
		mBackgroundColorPaint = new Paint();
		mProgressColorPaint = new Paint();
		mTitlePaint = new Paint();
		mSubtitlePaint = new Paint();
		init(attrs, 0);
	}

	public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCircleBounds = new RectF();
		mBackgroundColorPaint = new Paint();
		mProgressColorPaint = new Paint();
		mTitlePaint = new Paint();
		mSubtitlePaint = new Paint();
		init(attrs, defStyle);
	}

	public void init(AttributeSet attrs, int style) {
		// so that shadow shows up properly for lines and arcs
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CircularProgressBar, style, 0);
		String color;
		Resources res = getResources();

		this.mHasShadow = a.getBoolean(
				R.styleable.CircularProgressBar_hasShadow, true);

		color = a.getString(R.styleable.CircularProgressBar_progressColor);
		if (color == null)
			mProgressColorPaint.setColor(res
					.getColor(R.color.circular_progress_default_progress));
		else
			mProgressColorPaint.setColor(Color.parseColor(color));

		color = a.getString(R.styleable.CircularProgressBar_backgroundColor);
		if (color == null)
			mBackgroundColorPaint.setColor(res
					.getColor(R.color.circular_progress_default_background));
		else
			mBackgroundColorPaint.setColor(Color.parseColor(color));

		color = a.getString(R.styleable.CircularProgressBar_titleColor);
		if (color == null)
			mTitlePaint.setColor(res
					.getColor(R.color.circular_progress_default_title));
		else
			mTitlePaint.setColor(Color.parseColor(color));

		String t = a.getString(R.styleable.CircularProgressBar_mtitle);
		if (t != null)
			mTitle = t;

		mStrokeWidth = a.getInt(R.styleable.CircularProgressBar_strokeWidth,
				STROKE_WIDTH);

		a.recycle();

		mProgressColorPaint.setAntiAlias(true);
		mProgressColorPaint.setStyle(Paint.Style.STROKE);
		mProgressColorPaint.setStrokeWidth(mStrokeWidth);

		mBackgroundColorPaint.setAntiAlias(true);
		mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
		mBackgroundColorPaint.setStrokeWidth(mStrokeWidth);

		mTitlePaint.setTextSize(80);
		mTitlePaint.setStyle(Style.FILL);
		mTitlePaint.setAntiAlias(true);
		mTitlePaint.setTypeface(battery_fragment.cirprogress);
		mTitlePaint.setShadowLayer(0.1f, 0, 1, Color.GRAY);

		mSubtitlePaint.setTextSize(50);
		mSubtitlePaint.setStyle(Style.FILL);

		mSubtitlePaint.setAntiAlias(true);
		mSubtitlePaint.setTypeface(battery_fragment.cirprogress);
		// mSubtitlePaint.setShadowLayer(0.1f, 0, 1, Color.GRAY);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {

		canvas.drawArc(mCircleBounds, 0, 360, false, mBackgroundColorPaint);

		int prog = getProgress();
		float scale = getMax() > 0 ? (float) prog / getMax() * 360 : 0;

		if (mHasShadow)
			mProgressColorPaint.setShadowLayer(3, 0, 1, mShadowColor);
		canvas.drawArc(mCircleBounds, 270, scale, false, mProgressColorPaint);

		if (!TextUtils.isEmpty(mTitle)) {
			int xPos = (int) (getMeasuredWidth() / 2 - mTitlePaint
					.measureText(mTitle) / 2);
			int yPos = (int) (getMeasuredHeight() / 2);

			float titleHeight = Math.abs(mTitlePaint.descent()
					+ mTitlePaint.ascent());
			if (TextUtils.isEmpty(mSubTitle)) {
				yPos += titleHeight / 2;
			}
			canvas.drawText(mTitle, xPos, yPos, mTitlePaint);

			yPos += titleHeight;
			xPos = (int) (getMeasuredWidth() / 2 - mSubtitlePaint
					.measureText(mSubTitle) / 2);

			canvas.drawText(mSubTitle, xPos, yPos, mSubtitlePaint);
		}

		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec,
			final int heightMeasureSpec) {
		final int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int min = Math.min(width, height);
		setMeasuredDimension(min + 2 * STROKE_WIDTH, min + 2 * STROKE_WIDTH);

		mCircleBounds.set(STROKE_WIDTH, STROKE_WIDTH, min + STROKE_WIDTH, min
				+ STROKE_WIDTH);
	}

	@Override
	public synchronized void setProgress(int progress) {
		super.setProgress(progress);

		// the setProgress super will not change the details of the progress bar
		// anymore so we need to force an update to redraw the progress bar
		invalidate();
	}

	public void animateProgressTo(final int start, final int end,
			final ProgressAnimationListener listener) {
		if (start != 0)
			setProgress(start);

		final ObjectAnimator progressBarAnimator = ObjectAnimator.ofFloat(this,
				"animateProgress", start, end);
		progressBarAnimator.setDuration(1500);
		progressBarAnimator.setInterpolator(new LinearInterpolator());

		progressBarAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				CircularProgressBar.this.setProgress(end);
				if (listener != null)
					listener.onAnimationFinish();
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
				if (listener != null)
					listener.onAnimationStart();
			}
		});

		progressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int progress = ((Float) animation.getAnimatedValue())
						.intValue();
				if (progress != CircularProgressBar.this.getProgress()) {
					Log.d(TAG, progress + "");
					CircularProgressBar.this.setProgress(progress);
					if (listener != null)
						listener.onAnimationProgress(progress);
				}
			}
		});
		progressBarAnimator.start();
	}

	public synchronized void setTitle(String title) {
		this.mTitle = title;
		invalidate();
	}

	public synchronized void setSubTitle(String subtitle) {
		this.mSubTitle = subtitle;
		invalidate();
	}

	public synchronized void setSubTitleColor(int color) {
		mSubtitlePaint.setColor(color);
		invalidate();
	}

	public synchronized void setTitleColor(int color) {
		mTitlePaint.setColor(color);
		invalidate();
	}

	public synchronized void setHasShadow(boolean flag) {
		this.mHasShadow = flag;
		invalidate();
	}

	public synchronized void setShadow(int color) {
		this.mShadowColor = color;
		invalidate();
	}

	public String getTitle() {
		return mTitle;
	}

	public boolean getHasShadow() {
		return mHasShadow;
	}

}
