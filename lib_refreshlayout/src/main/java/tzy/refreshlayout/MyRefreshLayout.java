package tzy.refreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ProgressBar;

import tzy.refreshlayout.footer.MyRefreshFooterView2;
import tzy.refreshlayout.header.MyRefreshHeaderView2;
import tzy.refreshlayout.scrolltarget.RecyclerViewScrollTarget;
import tzy.refreshlayout.status.SimpleStatusView;

/**
 *
 */
public class MyRefreshLayout extends ViewGroup implements NestedScrollingParent2, NestedScrollingChild2, StatusView {
    public static final int REFRESH_TYPE_TOUCH_UP = 0;
    public static final int REFRESH_TYPE_SCROLLING = 1;

//    static final int TOUCH_DRAGGED_SCROLLING = 0;
//    static final int TOUCH_DRAGGED_REFRESHING = -1;
//    static final int TOUCH_DRAGGED_LOADING = 1;
//    int mDraggedStatus = TOUCH_DRAGGED_SCROLLING;

    //    static final int FLAG_FREE = 0;
    static final int FLAG_REFRESHING = 0x01;
    static final int FLAG_REFRESHING_DISABLE = 0x02;
    //    static final int FLAG_FINISHED_REFRESHING = FLAG_REFRESHING | FLAG_REFRESHING_DISABLE;
    static final int FLAG_FINISHED_REFRESHING = 0x04;
    static final int FLAG_LOADING = 0x08;
    static final int FLAG_LOADING_DISABLE = 0x10;
    //    static final int FLAG_FINISHED_LOADING = FLAG_LOADING | FLAG_LOADING_DISABLE;
    static final int FLAG_FINISHED_LOADING = 0x20;

    static final int FLAG_PROGRESS_REFRESHING = 0x40;

    static final int MASK_DISALLOW_REFRESHING = FLAG_REFRESHING | FLAG_REFRESHING_DISABLE | FLAG_LOADING | FLAG_FINISHED_REFRESHING | FLAG_PROGRESS_REFRESHING;
    static final int MASK_DISALLOW_LOADING = FLAG_LOADING | FLAG_LOADING_DISABLE | FLAG_REFRESHING | FLAG_FINISHED_LOADING | FLAG_PROGRESS_REFRESHING;

    private static final int MASK_STATUS = 0xfffff;
    int mFlags;
    private OnRefreshLoadListener mRefreshLoadListener;


    int mLastDraggedScrollY;
    static final int OVER_SCROLL_DISTANCE = 0;
    static final int OVER_FLING_DISTANCE = 6;
    private int mOverscrollDistance;
    private int mOverflingDistance;
    static final float SCROLL_CONSUMED_RATIO = 0.5f;
    private static final String TAG = "MyRefreshLayout";
    OnChildScrollUpCallback mChildScrollUpCallback;
    OnChildScrollDownCallback mChildScrollDownCallback;
    private final NestedScrollingParentHelper mParentHelper;
    private final NestedScrollingChildHelper mChildHelper;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
//    int mCurrentTargetOffsetTop;

    //    int mTotalUnconsumed;
    private View mTarget;
    private View mProgress;
    private MyRefreshFooter2 mFooterProgress;
    private MyRefreshHeader2 mHeaderProgress;
    StatusView mStatusView;


    private int[] mChildSortedIndexes;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private ViewScroller mScroller;
    private boolean mNestedTouchScrollInProgress;
    private boolean mNestedNonTouchScrollInProgress;

    int mScrollY;

    int mTouchScrollY;
    int mUnTouchScrollY;

    public static final long DELAY_CALLBACK_TIME = 500;

    public MyRefreshLayout(Context context) {
        this(context, null);
    }

    public MyRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScrollView();
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);
    }

    private void initScrollView() {
        mScroller = new ViewScroller();
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        final float density = getContext().getResources().getDisplayMetrics().density;
        mOverscrollDistance = (int) (density * OVER_SCROLL_DISTANCE);
        mOverflingDistance = (int) (density * OVER_FLING_DISTANCE);
        createHeaderProgress();
        createFooterProgress();
        createProgress();
        createStatusView();
        addView((View) mHeaderProgress);
        addView((View) mFooterProgress);
        addView((View) mStatusView);
        addView(mProgress);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        if (!mHeaderProgress.onMeasure(this, widthMeasureSpec, heightMeasureSpec)) {
            ((View) mHeaderProgress).measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
        }
        if (!mFooterProgress.onMeasure(this, widthMeasureSpec, heightMeasureSpec)) {
            ((View) mFooterProgress).measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
        }
        ((View) mStatusView).measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mProgress.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));


        sortChildIndex();

    }

    private void sortChildIndex() {
        int progressIndex = -1;
        int statusIndex = -1;
        int headerIndex = -1;
        int footerIndex = -1;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (progressIndex < 0 && mProgress == child) {
                progressIndex = i;
            } else if (statusIndex < 0 && mStatusView == child) {
                statusIndex = i;
            } else if (headerIndex < 0 && mHeaderProgress == child) {
                headerIndex = i;
            } else if (footerIndex < 0 && mFooterProgress == child) {
                footerIndex = i;
            }
        }


        if (mChildSortedIndexes == null || mChildSortedIndexes.length != childCount) {
            mChildSortedIndexes = new int[childCount];
        }


        int priority = childCount;
        if (progressIndex >= 0) {
            --priority;
            mChildSortedIndexes[priority] = progressIndex;
        }
        if (statusIndex >= 0) {
            --priority;
            mChildSortedIndexes[priority] = statusIndex;

        }
        if (headerIndex >= 0) {
            --priority;

            mChildSortedIndexes[priority] = headerIndex;
        }
        if (footerIndex >= 0) {
            --priority;
            mChildSortedIndexes[priority] = footerIndex;
        }

        int start = 0;

        for (int i = 0; i < priority; ++i) {

            while (start < childCount) {
                if (start != progressIndex && start != statusIndex && start != headerIndex && start != footerIndex) {
                    mChildSortedIndexes[i] = start;
                    ++start;
                    break;
                } else {
                    ++start;
                }

            }


        }


    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return mChildSortedIndexes[i];
//        return super.getChildDrawingOrder(childCount,i);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        ensureTarget();
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop() - mScrollY;
        final int childWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int childHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);


        if (!mHeaderProgress.onLayout(this, mScrollY, childLeft, childTop, childLeft + childWidth, childTop + childHeight, childWidth, childHeight)) {
            final View headerView = (View) mHeaderProgress;
            final int headerLeft = getPaddingLeft();
            final int headerTop = getPaddingTop() - headerView.getMeasuredHeight() - mScrollY;
//            final int headerTop = getPaddingTop() - mHeaderScrollY;
            headerView.layout(headerLeft, headerTop, headerLeft + headerView.getMeasuredWidth(), headerTop + headerView.getMeasuredHeight());
        }
        if (!mFooterProgress.onLayout(this, mScrollY, childLeft, childTop, childLeft + childWidth, childTop + childHeight, childWidth, childHeight)) {

            final View footerView = (View) mFooterProgress;
            final int footerLeft = getPaddingLeft();
            final int footerTop = getMeasuredHeight() - getPaddingBottom() - mScrollY;
//            final int footerTop = getMeasuredHeight() - getPaddingBottom() - footerView.getMeasuredHeight() - mFooterScrollY;
            footerView.layout(footerLeft, footerTop, footerLeft + footerView.getMeasuredWidth(), footerTop + footerView.getMeasuredHeight());
        }

        ((View) mStatusView).layout(childLeft, childTop, childLeft + ((View) mStatusView).getMeasuredWidth(), childTop + ((View) mStatusView).getMeasuredHeight());
        mProgress.layout((getMeasuredWidth() - mProgress.getMeasuredWidth()) / 2, (getMeasuredHeight() - mProgress.getMeasuredHeight()) / 2, (getMeasuredWidth() + mProgress.getMeasuredWidth()) / 2, (getMeasuredHeight() + mProgress.getMeasuredHeight()) / 2);


    }

    private static final int INVALID_POINTER = -1;
    private boolean mIsBeingDragged = false;
    private int mActivePointerId = INVALID_POINTER;
    private int mLastMotionY;
    private VelocityTracker mVelocityTracker;


    private float mInitialMotionY;
    private float mInitialDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mNestedTouchScrollInProgress) {
            return false;
        }
//        Log.i("@@", "@@@@@@@@@@@:onInterceptTouchEvent");

        final int action = ev.getActionMasked();
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
//                mScroller.mScroller.computeScrollOffset();
                mIsBeingDragged = mScroller.isBackingScrolling();
//                if (!ViewCompat.isNestedScrollingEnabled(mTarget)) {
//                    mScroller.stop();
//                }

//                if (mIsBeingDragged) {
//                    mLastDraggedScrollY = mScrollY;
//                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;

    }


//    public void fling(int velocityY) {
//        mScroller.fling(velocityY);
//    }
//
//    private void flingWithNestedDispatch(int velocityY) {
//        fling(velocityY);
//    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mNestedTouchScrollInProgress) {
            return false;
        }
//        Log.i("@@", "@@@@@@@@@@@:onTouchEvent");

        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = mScroller.isBackingScrolling();
                mScroller.stop();
                mInitialMotionY = mInitialDownY;
                mLastDraggedScrollY = mScrollY;

//                if (mIsBeingDragged = mScroller.isBackingScrolling() || mScrollY != 0) {
//                    mInitialMotionY = ev.getY();
//                    mScroller.stop();
//                    mLastDraggedScrollY = mScrollY;
//                }

                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);
                int deltaY = (int) (mInitialMotionY - y);
                if (mIsBeingDragged) {
                    mInitialMotionY = y;
                    final int topRange;
                    final int bottomRange;
                    if (mLastDraggedScrollY < 0) {
                        topRange = Integer.MIN_VALUE;
                        bottomRange = 0;
                    } else if (mLastDraggedScrollY > 0) {
                        topRange = 0;
                        bottomRange = Integer.MAX_VALUE;
                    } else {
                        topRange = 0;
                        bottomRange = 0;
                    }
                    final int scrollY = mScrollY;
                    overScrollByCompat(deltaY, scrollY, topRange, bottomRange, 0, true);
                    if (scrollY != mScrollY) {
                        moveSpinner(mScrollY);
                    }
                    if (mLastDraggedScrollY == 0) {
                        mLastDraggedScrollY = mScrollY;
                    }

//                    final int newScrollY = mScrollY + deltaY;
//                    if (newScrollY < 0 && !canChildScrollUp()) {
//                        overScrollByCompat(deltaY, mScrollY, topRange, bottomRange, 0, true);
//                    } else if (newScrollY > 0 && !canChildScrollDown()) {
//                        overScrollByCompat(deltaY, mScrollY, topRange, bottomRange, 0, true);
//                    } else {
//                        return false;
//                    }
//                    scrollBy(0, deltaY);
//                    Log.i("@@", "@@@@@@@@@:delaty:" + deltaY + ",scrollY:" + mScrollY);
//                    if (newScrollY>) {
//                    }
//                    overScrollByCompat(deltaY, mScrollY, topRange, bottomRange, 0, true)
//
////                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//////                    Log.i("@@", "@@@@@@@overscrollTop:" + overscrollTop);
//                    if (overscrollTop > 0 && !mLoading && !canChildScrollUp()) {
//                        moveSpinner(overscrollTop);
//                    } else if (overscrollTop < 0 && !mRefreshing && !canChildScrollDown()) {
//                        finishSpinnerLoading(overscrollTop);
//                    } else {
//                        return false;
//                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {

                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    int deltaY = (int) (mInitialMotionY - y);
                    final int newScrollY = mScrollY + deltaY;
                    finishSpinner(newScrollY);
//                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
//                    mIsBeingDragged = false;
//                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (!mIsBeingDragged) {
            if (mScrollY != 0) {
                mInitialMotionY = mInitialDownY;
                mIsBeingDragged = true;
                mLastDraggedScrollY = mScrollY;
            } else if (yDiff > 0 && !canChildScrollUp()) {
                if (Math.abs(yDiff) > mTouchSlop) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mLastDraggedScrollY = -1;

                }
            } else if (yDiff < 0 && !canChildScrollDown()) {
                if (Math.abs(yDiff) > mTouchSlop) {
                    mInitialMotionY = mInitialDownY - mTouchSlop;
                    mIsBeingDragged = true;
                    mLastDraggedScrollY = 1;
                }
            } else {
                mInitialDownY = y;
            }
        }


//        if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
//
//        }

    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if (mOnRequestDisallowInterceptTouchEventListener != null) {
            if (mOnRequestDisallowInterceptTouchEventListener.requestDisallowInterceptTouchEvent(b)) {
                super.requestDisallowInterceptTouchEvent(b);
            }
        } else {
            if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                    || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
                // Nope.
            } else {
                super.requestDisallowInterceptTouchEvent(b);
            }
        }


    }

    OnRequestDisallowInterceptTouchEventListener mOnRequestDisallowInterceptTouchEventListener;

    public void setOnRequestDisallowInterceptTouchEventListener(OnRequestDisallowInterceptTouchEventListener requestDisallowInterceptTouchEventListener) {
        mOnRequestDisallowInterceptTouchEventListener = requestDisallowInterceptTouchEventListener;
    }

    public interface OnRequestDisallowInterceptTouchEventListener {
        public boolean requestDisallowInterceptTouchEvent(boolean b);
    }

    boolean overScrollByCompat(int deltaY,
                               int scrollY, int maxOverScrollY,
                               boolean isTouchEvent) {

        final int topRange = getHeaderTop();
        final int bottomRange = getFooterBottom();
        return overScrollByCompat(deltaY, scrollY, topRange, bottomRange, maxOverScrollY, isTouchEvent);
    }


    boolean overScrollByCompat(int deltaY,
                               int scrollY, int topRange, int bottomRange, int maxOverScrollY,
                               boolean isTouchEvent) {
        int newDeltaY = 0;
        if (isTouchEvent) {
            if (deltaY > 0) {
                newDeltaY = (int) ((deltaY + 1) * SCROLL_CONSUMED_RATIO);
            } else if (deltaY < 0) {
                newDeltaY = (int) ((deltaY - 1) * SCROLL_CONSUMED_RATIO);
            }


        } else {
            newDeltaY = deltaY;
        }

        int newScrollY = scrollY + newDeltaY;
        final int top = topRange - maxOverScrollY;
        final int bottom = bottomRange + maxOverScrollY;

        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        onOverScrolled(0, newScrollY, false, clampedY);
        mHeaderProgress.onScrolling(this, newScrollY, newScrollY - scrollY, ((View) mHeaderProgress).getVisibility() == View.VISIBLE, false);
        mFooterProgress.onScrolling(this, newScrollY, newScrollY - scrollY, ((View) mFooterProgress).getVisibility() == View.VISIBLE, false);


        return clampedY;
    }


    boolean backScroll(int dy) {
        final int old = mScrollY;
        int newScrollY = old + dy;
        final int topRange;
        final int bottomRange;
        final int maxOverScrollY;
        topRange = Integer.MIN_VALUE;
        bottomRange = Integer.MAX_VALUE;
        maxOverScrollY = 0;
        final int top = topRange - maxOverScrollY;
        final int bottom = bottomRange + maxOverScrollY;

        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        onOverScrolled(0, newScrollY, false, clampedY);
        mHeaderProgress.onScrolling(this, newScrollY, newScrollY - old, ((View) mHeaderProgress).getVisibility() == View.VISIBLE, true);
        mFooterProgress.onScrolling(this, newScrollY, newScrollY - old, ((View) mFooterProgress).getVisibility() == View.VISIBLE, true);

        return clampedY;
    }


    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        final int newDeltaY = scrollY - mScrollY;
//        offsetChildrenTopAndBottom(-newDeltaY);
        offsetChildren(mTarget, -newDeltaY);
        offsetChildren((View) mStatusView, -newDeltaY);
//        offsetChildren(mProgress, -newDeltaY);
        mScrollY = -mTarget.getTop();

    }

    final void showProgressView() {
        final View p = mProgress;
        p.bringToFront();
        if (p.getVisibility() != View.VISIBLE) {
            p.setVisibility(VISIBLE);
        }
    }

    final void hideProgressView() {
        final View p = mProgress;
        if (p.getVisibility() == View.VISIBLE) {
            p.setVisibility(GONE);
        }
    }

    final void showRefreshingView() {
        final View h = (View) mHeaderProgress;
        h.bringToFront();
        if (h.getVisibility() != View.VISIBLE) {
            h.setVisibility(VISIBLE);
        }
    }

    final void hideRefreshingView() {
        final View h = (View) mHeaderProgress;
        if (h.getVisibility() == View.VISIBLE) {
            h.setVisibility(GONE);
        }
    }

    final void showLoadingView() {
        final View f = (View) mFooterProgress;
        f.bringToFront();
        if (f.getVisibility() != View.VISIBLE) {
            f.setVisibility(VISIBLE);
        }
    }

    final void hideLoadingView() {
        final View f = (View) mFooterProgress;
        if (f.getVisibility() == View.VISIBLE) {
            f.setVisibility(GONE);
        }
    }


    public void startProgressRefreshing() {
        startProgressRefreshing(false);
    }

    public void startProgressRefreshing(boolean notify) {


        if ((mFlags & MASK_DISALLOW_REFRESHING) == 0) {
            mFlags |= FLAG_PROGRESS_REFRESHING;
            hideLoadingView();
            hideRefreshingView();
            showProgressView();
            hideStatusView();

            if (notify) {
                performProgressRefreshingCallback();
            }

        }

    }


    public void startRefreshing() {
        startRefreshing(false);
    }

    public void startRefreshing(boolean notify) {
        if ((mFlags & MASK_DISALLOW_REFRESHING) == 0) {
            mFlags |= FLAG_REFRESHING;
            hideLoadingView();
            showRefreshingView();
            hideProgressView();

            if (notify) {
                performRefreshingCallback();
            }

        }

    }


    public void startLoading() {
        startLoading(false);
    }

    public void startLoading(boolean notify) {


        if ((mFlags & MASK_DISALLOW_LOADING) == 0) {
            mFlags |= FLAG_LOADING;
            showLoadingView();
            hideRefreshingView();
            hideProgressView();

            if (notify) {
                performLoadingCallback();
            }

        }

    }


    public void setRefreshEnabled(boolean enabled) {
        if (enabled) {
            mFlags &= ~FLAG_REFRESHING_DISABLE;
        } else {
            mFlags |= FLAG_REFRESHING_DISABLE;

        }
    }

    public void setLoadEnabled(boolean enabled) {
        if (enabled) {
            mFlags &= ~FLAG_LOADING_DISABLE;
        } else {
            mFlags |= FLAG_LOADING_DISABLE;
        }

    }

    public boolean isLoading() {
        return (mFlags & FLAG_LOADING) != 0;
    }

    public boolean isRefreshing() {
        return (mFlags & FLAG_REFRESHING) != 0;
    }

    public boolean isProgressRefreshing() {
        return (mFlags & FLAG_PROGRESS_REFRESHING) != 0;
    }


    @Override
    public void showEmptyView() {
        bringChildToFront((View) mStatusView);
        mStatusView.showEmptyView();
    }

    @Override
    public void showNetworkView() {
        bringChildToFront((View) mStatusView);
        mStatusView.showNetworkView();
    }

    @Override
    public void hideStatusView() {
        mStatusView.hideStatusView();
    }


    class ViewScroller implements Runnable {
        private final OverScroller mScroller;
        //        private boolean mTouchUpSpringBacking = false;//当前是否处于TouchUp的回弹状态
        //        private long mLastScroll;
        static final int ANIMATED_SCROLL_GAP = 250;

        ViewScroller() {
            mScroller = new OverScroller(getContext());
        }


        @Override
        public void run() {

            if (mScroller.computeScrollOffset()) {
                final int y = mScroller.getCurrY();
                final int oldY = mScrollY;

                int dy = y - oldY;
//                final int flingTopRange, flingBottomRange;
//                if (dy > 0) {
//                    flingTopRange = 0;
//                    flingBottomRange = getScrollBottomRange();
//                    overScrollByCompat(dy, oldY, flingTopRange, flingBottomRange, 0, false);
//                } else if (dy < 0) {
//                    flingTopRange = 0;
//                    flingBottomRange = 0;
//                    overScrollByCompat(dy, oldY, flingTopRange, flingBottomRange, 0, false);
//
//                }


                if (dy != 0) {
                    backScroll(dy);


                }
                ViewCompat.postOnAnimation(MyRefreshLayout.this, this);
            }
        }


        public void stop() {
            removeCallbacks(this);
            final boolean finished = mScroller.isFinished();
            if (!finished) {
                mScroller.abortAnimation();
            }

        }

        public boolean isBackingScrolling() {
            final boolean finished = mScroller.isFinished();
            return !finished;
        }


//        public void fling(int velocityY) {
//            removeCallbacks(this);
//            mScroller.fling(0, mScrollY, // start
//                    0, velocityY, // velocities
//                    0, 0, // x
//                    Integer.MIN_VALUE, Integer.MAX_VALUE, // y
//                    0, 0); // overscroll
//            mTouchUpSpringBacking = false;
//            post(this);
//        }

        public boolean springBackScrolling(int scrollY) {
            scrollTo(scrollY);
            return true;
        }


        void scrollBy(int dy) {
            removeCallbacks(this);
            mScroller.startScroll(0, mScrollY, 0, dy);
            ViewCompat.postOnAnimation(MyRefreshLayout.this, this);

        }

        void scrollTo(int scrollY) {
            scrollBy(scrollY - mScrollY);
        }
    }


    protected int getHeaderTop() {
        return -((View) mHeaderProgress).getHeight();
    }

    protected int getFooterBottom() {
        return ((View) mFooterProgress).getHeight();
    }


    public void offsetChildren(View view, int offset) {
        ViewCompat.offsetTopAndBottom(view, offset);
    }


    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;

        recycleVelocityTracker();


    }

//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        if (disallowIntercept) {
//            recycleVelocityTracker();
//        }
//        super.requestDisallowInterceptTouchEvent(disallowIntercept);
//    }


    protected void createFooterProgress() {
        if (mFooterProgress == null) {
            mFooterProgress = new MyRefreshFooterView2(getContext());
            ((View) mFooterProgress).setVisibility(View.GONE);
        }
    }

    protected void createHeaderProgress() {
        if (mHeaderProgress == null) {
            mHeaderProgress = new MyRefreshHeaderView2(getContext());
            ((View) mHeaderProgress).setVisibility(View.GONE);
        }
    }

    protected void createProgress() {
        if (mProgress == null) {
            mProgress = new ProgressBar(getContext());
            mProgress.setVisibility(GONE);
        }
    }

    protected void createStatusView() {
        if (mStatusView == null) {
            mStatusView = new SimpleStatusView(getContext());
            mStatusView.hideStatusView();
        }
    }


    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mFooterProgress && child != mHeaderProgress && child != mProgress && child != mStatusView) {
                    mTarget = child;
//                    return;
                    break;
                }
            }
        }
        if (mTarget == null)
            throw new NullPointerException("target view is Null");

        if (mScrollTarget == null) {
            mScrollTarget = generateScrollTarget(mTarget);
        }
    }

    protected ScrollTarget generateScrollTarget(View target) {
        return new RecyclerViewScrollTarget(target);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }


    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }


    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mChildHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public void stopNestedScroll(int type) {
        mChildHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mChildHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow, int type) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow,
                                           int type) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // NestedScrollingParent


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        final boolean rlt = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && !mScroller.isBackingScrolling();
//        Log.i("MyRefreshLayout", "@@@@@@@@@@@@@" + rlt + ",type:" + type);

        return rlt;
    }


    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
        if (type == ViewCompat.TYPE_TOUCH) {
            mNestedTouchScrollInProgress = true;
            mScroller.stop();
        } else {
            mNestedNonTouchScrollInProgress = true;
        }
    }


    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
//        Log.i("##", "############onStopNestedScroll:" + type);

        mParentHelper.onStopNestedScroll(target, type);
        if (type == ViewCompat.TYPE_TOUCH) {
            mNestedTouchScrollInProgress = false;
            if (/*!mNestedNonTouchScrollInProgress &&*/ mScrollY != 0) {
                finishSpinner(mScrollY);
            }

        } else {
            mNestedNonTouchScrollInProgress = false;
        }


        stopNestedScroll(type);
    }

    private boolean finishSpinner(int scrollY) {
        final int topRange = Math.min(-mHeaderProgress.getOverScrollDistance(), 0);
        final int bottomRange = Math.max(mFooterProgress.getOverScrollDistance(), 0);

        if (scrollY < 0) {
            switch ((mFlags & MASK_DISALLOW_REFRESHING)) {
                case 0:
                    if (mHeaderProgress.onStartRefreshing(mScrollTarget, scrollY, topRange, REFRESH_TYPE_TOUCH_UP)) {
                        mFlags |= FLAG_REFRESHING;
                        mHeaderProgress.onRefreshingAccepted(scrollY, bottomRange, REFRESH_TYPE_TOUCH_UP);
                        performRefreshingCallback();
                        if (scrollY < topRange) {
                            mScroller.springBackScrolling(topRange);
                            return true;
                        }
                    } else {
                        mHeaderProgress.onRefreshingNotAccepted(scrollY, bottomRange, REFRESH_TYPE_TOUCH_UP);
                        mScroller.springBackScrolling(0);
                        return true;
                    }
                    showRefreshingView();
                    hideLoadingView();
                    break;
                case FLAG_REFRESHING:
                    if (scrollY < topRange) {
                        mScroller.springBackScrolling(topRange);
                        return true;
                    }
                    showRefreshingView();

                    break;
                default:
                    mScroller.springBackScrolling(0);
                    hideRefreshingView();
                    return true;

            }


        } else if (scrollY > 0) {
            switch (mFlags & MASK_DISALLOW_LOADING) {
                case 0:
                    if (mFooterProgress.onStartLoading(mScrollTarget, scrollY, bottomRange, REFRESH_TYPE_TOUCH_UP)) {
                        mFlags |= FLAG_LOADING;
                        mFooterProgress.onLoadingAccepted(scrollY, bottomRange, REFRESH_TYPE_TOUCH_UP);
                        performLoadingCallback();
                        if (scrollY > bottomRange) {
                            mScroller.springBackScrolling(bottomRange);
                            return true;
                        }
                    } else {
                        mHeaderProgress.onRefreshingNotAccepted(scrollY, bottomRange, REFRESH_TYPE_TOUCH_UP);
                        mScroller.springBackScrolling(0);
                        return true;
                    }
                    showLoadingView();
                    hideRefreshingView();
                    break;
                case FLAG_LOADING:
                    if (scrollY > bottomRange) {
                        mScroller.springBackScrolling(bottomRange);
                        return true;
                    }
                    showLoadingView();

                    break;
                default:
                    mScroller.springBackScrolling(0);
                    hideLoadingView();
                    return true;

            }


        }


        return false;
    }

    private void moveSpinner(int scrollY) {
        final int topRange = Math.min(-mHeaderProgress.getOverScrollDistance(), 0);
        final int bottomRange = Math.max(mFooterProgress.getOverScrollDistance(), 0);

        if (scrollY < 0) {
            switch (mFlags & MASK_DISALLOW_REFRESHING) {
                case 0:
                    if (mHeaderProgress.onStartRefreshing(mScrollTarget, scrollY, topRange, REFRESH_TYPE_SCROLLING)) {
                        mFlags |= FLAG_REFRESHING;
                        mHeaderProgress.onRefreshingAccepted(scrollY, topRange, REFRESH_TYPE_SCROLLING);
                        performRefreshingCallback();
                    } else {
                        mHeaderProgress.onRefreshingNotAccepted(scrollY, topRange, REFRESH_TYPE_SCROLLING);
                    }
                    showRefreshingView();
                    hideLoadingView();
                    break;
                case FLAG_REFRESHING:
                    showRefreshingView();
                    break;
                default:
                    hideRefreshingView();

                    break;
            }


        } else if (scrollY > 0) {
            switch (mFlags & MASK_DISALLOW_LOADING) {
                case 0:
                    if (mFooterProgress.onStartLoading(mScrollTarget, scrollY, bottomRange, REFRESH_TYPE_SCROLLING)) {
                        mFlags |= FLAG_LOADING;
                        mFooterProgress.onLoadingAccepted(scrollY, bottomRange, REFRESH_TYPE_SCROLLING);
                        performLoadingCallback();
                    } else {
                        mFooterProgress.onLoadingNotAccepted(scrollY, bottomRange, REFRESH_TYPE_SCROLLING);

                    }
                    showLoadingView();
                    hideRefreshingView();
                    break;
                case FLAG_LOADING:
                    showLoadingView();
                    break;
                default:
                    hideLoadingView();

                    break;
            }

        }

    }


    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mScrollOffset, type);
        final int dy = dyUnconsumed + mScrollOffset[1];
//        Log.i("##", "#######onNestedScroll:dyConsumed:" + dyConsumed + ",dyUnconsumed:" + dyUnconsumed);
//        overScrollByCompat(dy, mScrollY, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, true);
        final int scrollY = mScrollY;


        if (type == ViewCompat.TYPE_TOUCH) {
//            Log.i("##", "#######Nested:" + dy);
            final int topRange;
            final int bottomRange;
            if (scrollY <= 0 && dy < 0 && !canChildScrollUp()) {
                topRange = Integer.MIN_VALUE;
                bottomRange = 0;
//                Log.i("##", "#######Nested");

                overScrollByCompat(dy, scrollY, topRange, bottomRange, 0, true);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }


            } else if (scrollY >= 0 && dy > 0 && !canChildScrollDown()) {
                topRange = 0;
                bottomRange = Integer.MAX_VALUE;
//                Log.i("##", "#######Nested");
                overScrollByCompat(dy, scrollY, topRange, bottomRange, 0, true);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
            }


        } else if (!mScroller.isBackingScrolling()) {
//            Log.i("##", "#######Nested");


            if (scrollY <= 0 && dy < 0 && !canChildScrollUp()) {
                int topRange = Math.min(-mHeaderProgress.getOverScrollDistance(), 0);
                final int bottomRange = 0;

                switch ((mFlags & MASK_DISALLOW_REFRESHING)) {
                    case 0:
                        if (!mHeaderProgress.onStartRefreshing(mScrollTarget, dy, topRange, REFRESH_TYPE_SCROLLING)) {
                            topRange = 0;
                        }
                        break;
                    case FLAG_REFRESHING:


                        break;
                    default:
                        topRange = 0;
                        break;

                }


                overScrollByCompat(dy, scrollY, topRange, bottomRange, 0, false);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
            } else if (scrollY >= 0 && dy > 0 && !canChildScrollDown()) {
                final int topRange = 0;
                int bottomRange = Math.max(mFooterProgress.getOverScrollDistance(), 0);

                switch (mFlags & MASK_DISALLOW_LOADING) {
                    case 0:
                        if (!mFooterProgress.onStartLoading(mScrollTarget, dy, bottomRange, REFRESH_TYPE_SCROLLING)) {
                            bottomRange = 0;
                        }
                        break;
                    case FLAG_LOADING:

                        break;
                    default:
                        bottomRange = 0;
                        break;

                }


                overScrollByCompat(dy, scrollY, topRange, bottomRange, 0, false);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
            }


        }


////        Log.i("@@", "@@@@@@@onNestedScroll:mTotalUnconsumed:" + mTotalUnconsumed + ",dy:" + dy + ",canChildScrollUp:" + canChildScrollUp() + ",canChildScrollDown:" + canChildScrollDown());
//
//        if (type == ViewCompat.TYPE_TOUCH) {
//            if (dy < 0 && !canChildScrollUp()) {
//                mScrollY += dy;
//                moveSpinner(mScrollY, type == ViewCompat.TYPE_TOUCH);
//            } else if (dy > 0 && !canChildScrollDown()) {
//                mScrollY += dy;
//                moveSpinner(mScrollY, type == ViewCompat.TYPE_TOUCH);
//            }
//        } else {
//
//        }


    }


//    private boolean moveSpinner(float overscrollTop, boolean isTouchEvent) {
//        Log.i("@@", "##############moveSpinner:" + overscrollTop);
//        final int oldOffsetTop = mCurrentTargetOffsetTop;
//        final int newOffsetTop = (int) (overscrollTop * 0.5);
//        return overScrollByCompat(newOffsetTop - oldOffsetTop, oldOffsetTop, 0, isTouchEvent);
//    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
//        Log.i("##", "#######NestedPre:" + target + "," + type);

        final int scrollY = mScrollY;
        if (type == ViewCompat.TYPE_TOUCH) {
            final int unconsumedY = -scrollY;
            final int topRange;
            final int bottomRange;


            final int consumedY;
            if (dy > 0 && unconsumedY > 0) {
                topRange = Integer.MIN_VALUE;
                bottomRange = 0;
                if (dy > unconsumedY) {
                    consumedY = unconsumedY;
                } else {
                    consumedY = dy;
                }
//                Log.i("##", "#######NestedPre");


                overScrollByCompat(consumedY, scrollY, topRange, bottomRange, 0, true);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
                consumed[1] = (mScrollY - scrollY) != 0 ? consumedY : 0;

            } else if (dy < 0 && unconsumedY < 0) {
                topRange = 0;
                bottomRange = Integer.MAX_VALUE;
                if (dy < unconsumedY) {
                    consumedY = unconsumedY;
                } else {
                    consumedY = dy;
                }
//                Log.i("##", "#######NestedPre");
                overScrollByCompat(consumedY, scrollY, topRange, bottomRange, 0, true);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
                consumed[1] = (mScrollY - scrollY) != 0 ? consumedY : 0;

            }
        } else if (!mScroller.isBackingScrolling()) {
            final int unconsumedY = -scrollY;
            final int topRange;
            final int bottomRange;


            final int consumedY;
            if (dy > 0 && unconsumedY > 0) {
                topRange = Math.min(-mHeaderProgress.getOverScrollDistance(), 0);
                bottomRange = 0;
                if (dy > unconsumedY) {
                    consumedY = unconsumedY;
                } else {
                    consumedY = dy;
                }
//                Log.i("##", "#######NestedPre");
                overScrollByCompat(consumedY, scrollY, topRange, bottomRange, 0, false);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
                consumed[1] = mScrollY - scrollY;

            } else if (dy < 0 && unconsumedY < 0) {
                topRange = 0;
                bottomRange = Math.max(mFooterProgress.getOverScrollDistance(), 0);
                if (dy < unconsumedY) {
                    consumedY = unconsumedY;
                } else {
                    consumedY = dy;
                }
//                Log.i("##", "#######NestedPre");
                overScrollByCompat(consumedY, mScrollY, topRange, bottomRange, 0, false);
                if (scrollY != mScrollY) {
                    moveSpinner(mScrollY);
                }
                consumed[1] = mScrollY - scrollY;

            }
        }

        final int[] parentConsumed = mScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null, type)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
//        Log.i("##", "#######onNestedPreScroll:dy:" + dy + ",dyconsumed:" + consumed[1]);

    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
//        Log.i("RefreshLayout", "@@@@onNestedPreFling:scrollY:" + mScrollY + ",velocityY:" + velocityY);
        final int scrollY = mScrollY;
        final int topRange;
        final int bottomRange;
        if ((mFlags & FLAG_REFRESHING) != 0) {
            topRange = Math.min(-mHeaderProgress.getOverScrollDistance(), 0);


        } else {
            topRange = 0;
        }
        if ((mFlags & FLAG_LOADING) != 0) {
            bottomRange = Math.max(mFooterProgress.getOverScrollDistance(), 0);
        } else {
            bottomRange = 0;
        }

        if ((scrollY < topRange && velocityY > 0) || (scrollY > bottomRange && velocityY < 0)) {
//            Log.i("@@", "@@@@@@@@@@@@:dispatch onNestedPreFling");
            return true;
        }

        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }


    void resetRefresh() {
        boolean smoothScroll = !mHeaderProgress.onFinishRefreshing(mScrollTarget, mScrollY);
        resetScrolling(FLAG_REFRESHING, smoothScroll);
    }

    void resetLoading() {
        boolean smoothScroll = !mFooterProgress.onFinishLoading(mScrollTarget, mScrollY);
        resetScrolling(FLAG_LOADING, smoothScroll);
    }

    void resetProgressRefresh() {
        hideProgressView();
    }

    private void resetScrolling(int status, boolean smoothScroll) {
        if (status == FLAG_REFRESHING && mScrollY < 0) {
            if (smoothScroll) {
                mScroller.springBackScrolling(0);
            } else {
//                offsetChildren(mTarget);
                backScroll(-mScrollY);
            }
        } else if (status == FLAG_LOADING && mScrollY > 0) {
            if (smoothScroll) {
                mScroller.springBackScrolling(0);
            } else {
                backScroll(-mScrollY);
            }
        }
    }

    public void stopRefreshing(boolean finished) {
        if (finished) {
            finishedRefreshing();
        } else {
            stopRefreshing();
        }
    }

    public void stopRefreshing() {
        if ((mFlags & FLAG_REFRESHING) != 0) {
            mFlags ^= FLAG_REFRESHING;
//            mFlags &= ~FLAG_REFRESHING;
            resetRefresh();
        } else if ((mFlags & FLAG_PROGRESS_REFRESHING) != 0) {
            mFlags ^= FLAG_PROGRESS_REFRESHING;
            resetProgressRefresh();
        }

    }

    public void finishedRefreshing() {
        mFlags |= FLAG_FINISHED_REFRESHING;
        stopRefreshing();
    }

    public void setLoadFinished(boolean finished) {
        if (finished) {
            mFlags |= FLAG_FINISHED_LOADING;
        } else {
            mFlags &= ~FLAG_FINISHED_LOADING;

        }
    }

    public void setRefreshFinished(boolean finished) {
        if (finished) {
            mFlags |= FLAG_FINISHED_REFRESHING;
        } else {
            mFlags &= ~FLAG_FINISHED_REFRESHING;

        }
    }

    public void stopLoading(boolean finished) {
        if (finished) {
            finishedLoading();
        } else {
            stopLoading();
        }
    }

    public void finishedLoading() {
        mFlags |= FLAG_FINISHED_LOADING;
        stopLoading();

    }

    public void stopLoading() {
        if ((mFlags & FLAG_LOADING) != 0) {
            mFlags ^= FLAG_LOADING;//mFlags &= ~FLAG_LOADING
//                    mFlags |= FLAG_FINISHED_LOADING;
            resetLoading();
        }
    }


    @Override
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }


//    @Override
//    public int computeVerticalScrollRange() {
//        return ((View) mHeaderProgress).getHeight() + mTarget.getHeight() + ((View) mFooterProgress).getHeight();
//    }
//
//    @Override
//    public int computeVerticalScrollOffset() {
//        return ((View) mHeaderProgress).getHeight() + mScrollY;
//
//    }
//
//    @Override
//    public int computeVerticalScrollExtent() {
//        return getHeight();
//    }
//
//    @Override
//    public boolean canScrollVertically(int direction) {
//        return super.canScrollVertically(direction);
//    }

    public StatusView getStatusView() {
        return mStatusView;
    }

    public interface OnChildScrollUpCallback {
        boolean canChildScrollUp(MyRefreshLayout parent, @Nullable View child);
    }

    public interface OnChildScrollDownCallback {
        boolean canChildScrollDown(MyRefreshLayout parent, @Nullable View child);
    }

    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }

    public boolean canChildScrollDown() {
        if (mChildScrollDownCallback != null) {
            return mChildScrollDownCallback.canChildScrollDown(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, 1);
        }
        return mTarget.canScrollVertically(1);
    }

    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    public void setOnChildScrollDownCallback(@Nullable OnChildScrollDownCallback callback) {
        mChildScrollDownCallback = callback;
    }

    private void performLoadingCallback() {
        if (mRefreshLoadListener != null) {
            ViewCompat.postOnAnimationDelayed(this, mLoadingCallbackRunnable, DELAY_CALLBACK_TIME);

        }
    }

    private final Runnable mLoadingCallbackRunnable = new Runnable() {
        @Override
        public void run() {
            mRefreshLoadListener.onLoading(MyRefreshLayout.this);
        }
    };


    private void performRefreshingCallback() {
        if (mRefreshLoadListener != null) {
            ViewCompat.postOnAnimationDelayed(this, mRefreshCallbackRunnable, DELAY_CALLBACK_TIME);

        }
    }

    private final Runnable mRefreshCallbackRunnable = new Runnable() {
        @Override
        public void run() {
            mRefreshLoadListener.onRefresh(MyRefreshLayout.this);

        }
    };

    private void performProgressRefreshingCallback() {
        if (mRefreshLoadListener != null) {
            ViewCompat.postOnAnimationDelayed(this, mProgressRefreshCallbackRunnable, DELAY_CALLBACK_TIME);
        }
    }

    private final Runnable mProgressRefreshCallbackRunnable = new Runnable() {
        @Override
        public void run() {
            mRefreshLoadListener.onProgressRefresh(MyRefreshLayout.this);
        }
    };


    public void setOnRefreshLoadListener(OnRefreshLoadListener listener) {
        mRefreshLoadListener = listener;
    }

    public interface OnRefreshLoadListener {
        void onProgressRefresh(MyRefreshLayout view);

        void onRefresh(MyRefreshLayout view);

        void onLoading(MyRefreshLayout view);
    }

    private ScrollTarget mScrollTarget;

    public void setScrollTarget(ScrollTarget scrollTarget) {
        mScrollTarget = scrollTarget;
    }
}
