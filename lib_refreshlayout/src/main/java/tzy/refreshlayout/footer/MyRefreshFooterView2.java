package tzy.refreshlayout.footer;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import tzy.refreshlayout.MyRefreshFooter2;
import tzy.refreshlayout.RefreshLayout;
import tzy.refreshlayout.Scroller;

/**
 * Created by Administrator on 2018/3/21.
 */

public class MyRefreshFooterView2 extends LinearLayout implements MyRefreshFooter2 {
    public MyRefreshFooterView2(Context context) {
        super(context);
        init();
    }

    public MyRefreshFooterView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyRefreshFooterView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyRefreshFooterView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private TextView mTextView;

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutParams paramsText = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextView = new AppCompatTextView(getContext());
        mTextView.setTextSize(20f);
        addView(mTextView, paramsText);
        LayoutParams paramsProgress = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ProgressBar progressBar = new ProgressBar(getContext());
        addView(progressBar, paramsProgress);
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
        final View footerView = this;
        final int footerLeft = parent.getPaddingLeft();
        final int footerTop = parent.getMeasuredHeight() - parent.getPaddingBottom() - scrollY;
//            final int footerTop = getMeasuredHeight() - getPaddingBottom() - footerView.getMeasuredHeight() - mFooterScrollY;
        footerView.layout(footerLeft, footerTop, footerLeft + footerView.getMeasuredWidth(), footerTop + footerView.getMeasuredHeight());
        return true;
    }

    @Override
    public void onScrolling(RefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling) {
        parent.offsetChildren(this, -deltaY);
    }

    @Override
    public void onLoadingAccepted(int distance, int overScrollRange, int type) {
        mTextView.setText("正在加载......");

    }

    @Override
    public void onLoadingNotAccepted(int distance, int overScrollRange, int type) {
        mTextView.setText("松开加载更多.....");
    }

    @Override
    public boolean onStartLoading(Scroller scrollTarget, int distance, int overScrollRange, int type) {

        if (scrollTarget.isScrollSupported()) {
            return distance > 0 && type == RefreshLayout.REFRESH_TYPE_SCROLLING;

        } else {
            return distance >= overScrollRange && type == RefreshLayout.REFRESH_TYPE_TOUCH_UP;
        }

    }


    @Override
    public int getOverScrollDistance() {
        return getMeasuredHeight();
    }

    @Override
    public boolean onFinishLoading(Scroller scrollTarget, int distance) {
        mTextView.setText("加载完成");
        if (scrollTarget.isScrollSupported()) {
            if (distance > 0) {
                scrollTarget.stopNestedScroll();
                scrollTarget.scrollBy(distance);
            }
            return true;

        } else {
            return false;
        }
    }


//    @Override
//    public boolean onStartLoading(int distance) {
//        return false;
//    }


}
