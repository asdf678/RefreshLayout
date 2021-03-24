package tzy.refreshlayout.header;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import tzy.refreshlayout.MyRefreshHeader2;
import tzy.refreshlayout.RefreshLayout;
import tzy.refreshlayout.R;
import tzy.refreshlayout.Scroller;

/**
 * Created by Administrator on 2018/3/21.
 */

public class MyRefreshHeaderView2 extends LinearLayout implements MyRefreshHeader2 {
    public MyRefreshHeaderView2(Context context) {
        super(context);
        init();
    }

    public MyRefreshHeaderView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRefreshHeaderView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyRefreshHeaderView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //    private TextView mTextView;
    ImageView mImageView;
    MyDrawable mDrawable;
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    private void init() {
        View.inflate(getContext(), R.layout.layout_refresh_header, this);

        mImageView = findViewById(R.id.refresh_header_image);
        mDrawable = new MyDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.refresh_icon));
        mImageView.setImageDrawable(mDrawable);
//        mImageView.setImageDrawable(mDrawable);

//        setOrientation(HORIZONTAL);
//        LayoutParams paramsText = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mTextView = new AppCompatTextView(getContext());
//        mTextView.setTextSize(20f);
//        addView(mTextView, paramsText);
//        LayoutParams paramsProgress = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        ProgressBar progressBar = new ProgressBar(getContext());
//        addView(mImageView, paramsProgress);

//        setPadding();
        setGravity(Gravity.CENTER);
    }


    @Override
    public boolean onMeasure(RefreshLayout parent, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {

        measure(MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), MeasureSpec.AT_MOST));

        return true;
    }

    @Override
    public boolean onLayout(RefreshLayout parent, int scrollY, int targetLeft, int targetTop, int targetRight, int targetBottom, int targetWidget, int targetHeight) {
        final View headerView = this;
        final int headerLeft = parent.getPaddingLeft();
        final int headerTop = parent.getPaddingTop() - headerView.getMeasuredHeight() - scrollY;
//            final int headerTop = getPaddingTop() - mHeaderScrollY;
        headerView.layout(headerLeft, headerTop, headerLeft + headerView.getMeasuredWidth(), headerTop + headerView.getMeasuredHeight());
        return true;
    }

    @Override
    public void onScrolling(RefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling) {
        final float f = (float) distance / getOverScrollDistance() * 360;
        parent.offsetChildren(this, -deltaY);

        if (!mDrawable.isRunning()) {
            mDrawable.setRotation(f);
            int alpha = (int) ((float) -distance / getOverScrollDistance() / 2f * 255f);
            alpha = Math.min(alpha, 255);
            mDrawable.setAlpha(alpha);
//            Log.i("@@", "@@@@@@:alpha" + alpha);

        } else {

        }
//        Log.i("@@", "@@@@@@:distance" + distance);


        if (backScrolling) {
            if (!parent.isRefreshing()) {
                onNotRefreshingBackScrolling();
            } else {
                onRefreshingBackScrolling();
            }

        }


    }

    @Override
    public boolean onStartRefreshing(Scroller scrollTarget, int distance, int overScrollRange, int type) {
        if (distance <= overScrollRange && type == RefreshLayout.REFRESH_TYPE_TOUCH_UP) {
            return true;
        }
        return false;
    }

    void onNotRefreshingBackScrolling() {

    }

    void onRefreshingBackScrolling() {

    }


    @Override
    public int getOverScrollDistance() {
        return getMeasuredHeight();
    }

    @Override
    public void onRefreshingAccepted(int distance, int overScrollRange, int type) {
//        mTextView.setText("正在刷新...");
        mDrawable.start();

    }

    @Override
    public void onRefreshingNotAccepted(int distance, int overScrollRange, int type) {
        if (distance > overScrollRange) {
//            mTextView.setText("下拉后刷新");
        } else if (type == RefreshLayout.REFRESH_TYPE_SCROLLING) {
//            mTextView.setText("松开后刷新");

        }


    }


    @Override
    public boolean onFinishRefreshing(Scroller scrollTarget, int distance) {
        //        mTextView.setText("刷新完成");
        mDrawable.stop();
        return false;

    }
}
