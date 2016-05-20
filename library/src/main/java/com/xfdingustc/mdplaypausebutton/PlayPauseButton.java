package com.xfdingustc.mdplaypausebutton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class PlayPauseButton extends FrameLayout {
    private static final String TAG = PlayPauseButton.class.getSimpleName();
    private static final Property<PlayPauseButton, Integer> COLOR =
        new Property<PlayPauseButton, Integer>(Integer.class, "color") {
            @Override
            public Integer get(PlayPauseButton v) {
                return v.getColor();
            }

            @Override
            public void set(PlayPauseButton v, Integer value) {
                v.setColor(value);
            }
        };

    private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

    private PlayPauseDrawable mDrawable;
    private final Paint mPaint = new Paint();
    private int mPauseBackgroundColor;
    private int mPlayBackgroundColor;

    private AnimatorSet mAnimatorSet;
    private int mBackgroundColor;
    private int mWidth;
    private int mHeight;

    public PlayPauseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseButton);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mDrawable = new PlayPauseDrawable(getContext());
        mDrawable.setCallback(this);

        mBackgroundColor = a.getColor(R.styleable.PlayPauseButton_bgColor,
            getResources().getColor(android.R.color.transparent));

        mPauseBackgroundColor = a.getColor(R.styleable.PlayPauseButton_pauseBgColor,
            getResources().getColor(android.R.color.transparent));
        mPlayBackgroundColor = a.getColor(R.styleable.PlayPauseButton_playBgColor,
            getResources().getColor(android.R.color.transparent));
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(size, size);
        mDrawable.setDimension(size, size);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDrawable.setBounds(0, 0, w, h);
        Log.d(TAG, "width: " + w + " height: " + h);
        mWidth = w;
        mHeight = h;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            setClipToOutline(true);
        }
    }

    private void setColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    private int getColor() {
        return mBackgroundColor;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackgroundColor);
        final float radius = Math.min(mWidth, mHeight) / 2f;
//        Log.d(TAG, "radius: " + radius);
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, radius, mPaint);
        mDrawable.draw(canvas);
    }

    public void toggle() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();
        final boolean isPlay = mDrawable.isPlay();
        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(this, COLOR, isPlay ? mPauseBackgroundColor : mPlayBackgroundColor);
        colorAnim.setEvaluator(new ArgbEvaluator());
        final Animator pausePlayAnim = mDrawable.getPausePlayAnimator();
        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
        mAnimatorSet.playTogether(colorAnim, pausePlayAnim);
        mAnimatorSet.start();
    }
}
