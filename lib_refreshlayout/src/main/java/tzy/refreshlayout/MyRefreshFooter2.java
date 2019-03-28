package tzy.refreshlayout;

/**
 * Created by Administrator on 2018/3/5.
 */

public interface MyRefreshFooter2 {
    //自定义测量
    boolean onMeasure(RefreshLayout parent, int parentWidthMeasureSpec, int parentHeightMeasureSpec);

    //自定义布局
    boolean onLayout(RefreshLayout parent, int scrollY, int targetLeft, int targetTop, int targetRight, int targetBottom, int targetWidget, int targetHeight);

    /**
     * 滑动监听
     *
     * @param distance      滑动总距离
     * @param deltaY        滑动距离
     * @param visible       是否可见
     * @param backScrolling 是否处于回弹状态
     */
    void onScrolling(RefreshLayout parent, int distance, int deltaY, boolean visible, boolean backScrolling);

    //接受加载
    void onLoadingAccepted(int distance, int overScrollRange, int type);

    //不接收加载
    void onLoadingNotAccepted(int distance, int overScrollRange, int type);


    /**
     * 是否开始加载更多
     *
     * @param distance        滑动的总距离
     * @param overScrollRange 滑动
     * @param type            滑动或者手势抬起{@link RefreshLayout#REFRESH_TYPE_SCROLLING,RefreshLayout#REFRESH_TYPE_TOUCH_UP}
     */
    boolean onStartLoading(Scroller scrollTarget, int distance, int overScrollRange, int type);

    //接受触摸加载事件的距离
    int getOverScrollDistance();

    //完成加载
    boolean onFinishLoading(Scroller scrollTarget, int distance);
}
