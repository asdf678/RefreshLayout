package tzy.refreshlayout.header;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class MyDrawable extends BitmapDrawable implements Animatable {

    float mRotation;

    public MyDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        setupAnimators();
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        canvas.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        super.draw(canvas);
    }

    private static final int ANIMATION_DURATION = 1332;

    @Override
    public void start() {
        mAnimator.cancel();
        // Already showing some part of the ring
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.start();
    }

    @Override
    public void stop() {
        mAnimator.cancel();
        setRotation(0);
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    private Animator mAnimator;

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private float mRotationCount;
    private boolean mFinishing;

    private void setupAnimators() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                Log.i("@@", "@@@@onAnimationUpdate" + animation.getAnimatedValue());
                float interpolatedTime = (float) animation.getAnimatedValue();
                setRotation(interpolatedTime * 360);
//                    invalidateSelf();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(LINEAR_INTERPOLATOR);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {
                mRotationCount = 0;
//                Log.i("@@", "@@@@onAnimationStart");

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                Log.i("@@", "@@@@onAnimationEnd");

                // do nothing
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                Log.i("@@", "@@@@onAnimationCancel");

                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
//                Log.i("@@", "@@@@onAnimationRepeat");


                if (mFinishing) {

                    mFinishing = false;
                    animator.cancel();
                    animator.setDuration(ANIMATION_DURATION);
                    animator.start();
                } else {
                    mRotationCount = mRotationCount + 1;
                }
            }
        });
        mAnimator = animator;
    }

}
